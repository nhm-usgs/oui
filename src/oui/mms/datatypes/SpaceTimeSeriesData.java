package oui.mms.datatypes;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SpaceTimeSeriesData implements SpaceTimeSeriesInterface {
    
    String data_file_name;
    ArrayList<String> variable_names = new ArrayList<String>();
    double[] variable_min, variable_max, variable_range;
    int zoneCount, timeCount, variableCount;
    OuiCalendar start_time, end_time;
    OuiCalendar current_time = new OuiCalendar();
    int current_time_step;
    double[][][] vals = null;  // indexes are variable, time step, space index
    double[] dates = null;
    int var_index;
    
    public SpaceTimeSeriesData() {
        try {
            start_time = new OuiCalendar();
            start_time.setDT("1980-10-01");
            end_time = new OuiCalendar();
            end_time.setDT("1981-09-30");
        } catch (SetOuiCalendarException ex) {
            Logger.getLogger(SpaceTimeSeriesData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public SpaceTimeSeriesData(String data_file_name) {
        this.data_file_name = data_file_name;
        scanDataFile();
        readData();
    }
    
    public int getVariableCount() {return variableCount;}
    
    public ArrayList<String> getVariableNames() {
        return variable_names;
    }
    
    public double getVariableMinValue() {return variable_min[var_index];}
    
    public double getVariableMaxValue() {return variable_max[var_index];}
    
    public double getVariableRange() {return variable_range[var_index];}
    
    public OuiCalendar getStartTime() {return start_time;}
    
    public OuiCalendar getEndTime() {return end_time;}
    
    public OuiCalendar getCurrentTime() {return current_time;}
    
    public int getZoneCount() {return zoneCount;}
    
    public int getTimeCount() {
        return timeCount;
    }
    
    public int getTimeStep() {return current_time_step;}
    
    public void setVariableIndex(int vi) {this.var_index = vi;}
    
    public double[][][] getData() {return vals;}
    
    public void setTimeStep(int ts) {
        if (ts < 0) ts = 0;
        if (ts > timeCount - 1) {
            ts = timeCount - 1;
        }
        
        current_time_step = ts;
        current_time.setJulian(dates[ts]);
    }
    
    /**
     *
     */
    private void scanDataFile() {
        try {
            String line;
            StringTokenizer st;
            
            BufferedReader in = new BufferedReader( new FileReader(data_file_name) );
            
            //            in.readLine();	//
            //            in.readLine();	//	These lines read in the header and...well ignore it for now...
            //            in.readLine();	//
            //            in.readLine();	//
            //            in.readLine();	//
            //            in.readLine();	//
            //            in.readLine();	//
            //            in.readLine();	//
            //            in.readLine();	//
            
            // Skip header
            line = in.readLine();
            while (line.charAt(0) == '#') {
                line = in.readLine();
            }
            
            st = new StringTokenizer(line, " \t");
            st.nextToken();
            String zone_name = st.nextToken();
            
            while (st.hasMoreTokens()) variable_names.add( st.nextToken());
            variableCount = variable_names.size();
            
            variable_min = new double[variableCount];
            variable_max = new double[variableCount];
            variable_range = new double[variableCount];
            
            for (int i = 0; i < variableCount; i++) {
                variable_min[i] = 10000000.0;
                variable_max[i] = -10000000.0;
            }
            
            in.readLine();
            
            int count = 0;
            timeCount = 0;
            String date = null;
            int zone_id = 0, last_zone_id;
            
            while ((line = in.readLine()) != null) {
                st = new StringTokenizer(line, " \t");
                date = st.nextToken();
                
                if (count == 0) {
                    int yr = Integer.valueOf(date.substring(0, 4)).intValue();
                    //                    int mo = Integer.valueOf(date.substring(5, 7)).intValue() - 1;
                    int mo = Integer.valueOf(date.substring(5, 7)).intValue() -1;
                    int da = Integer.valueOf(date.substring(8, 10)).intValue();
                    
                    start_time = new OuiCalendar();
                    start_time.set(yr, mo, da, 0, 0, 0);
                }
                
                last_zone_id = zone_id;
                zone_id = Integer.valueOf(st.nextToken()).intValue();
                
                if (zone_id == 1) {
                    timeCount++;
                    zoneCount = last_zone_id;
                }
                
                for (int i = 0; i < variableCount; i++) {
                    double val = Double.valueOf(st.nextToken()).doubleValue();
                    variable_min[i] = Math.min(val, variable_min[i]);
                    variable_max[i] = Math.max(val, variable_max[i]);
                }
                count++;
            }
            
            
            int yr = Integer.valueOf(date.substring(0, 4)).intValue();
            //            int mo = Integer.valueOf(date.substring(5, 7)).intValue() - 1;
            int mo = Integer.valueOf(date.substring(5, 7)).intValue();
            int da = Integer.valueOf(date.substring(8, 10)).intValue();
            
            end_time = new OuiCalendar();
            end_time.set (yr, mo, da, 0, 0, 0);
            
            for (int i = 0; i < variableCount; i++) {
                variable_range[i] = variable_max[i] - variable_min[i];
                variable_range[i] = variable_range[i] + (variable_range[i] * 0.01);
            }
            
            in.close();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean nextTimeStep() {
        current_time_step++;
        
        if (current_time_step >= timeCount) {
            return false;
        }
        
        current_time.setJulian(dates[current_time_step]);
        return true;
    }
    
    public double[] getCurrentData() {
        if (vals == null) System.out.println("getCurrentData vals = null");
        return vals[this.var_index][current_time_step];
    }
    
    private void readData() {
        String line = null;
        String date;
        StringTokenizer st;
        BufferedReader in = null;
        OuiCalendar mdt = new OuiCalendar();
        
        vals = new double[variableCount][timeCount][zoneCount];
        dates = new double[timeCount];
        
        try {
            in = new BufferedReader(new FileReader(data_file_name));
            
            // Skip header
            line = in.readLine();
            while (line.charAt(0) == '#') line = in.readLine();
            in.readLine();
            //            in.readLine();
            
            for (int i = 0; i < timeCount; i++) {
                date = null;
                for (int j = 0; j < zoneCount; j++) {
                    line = in.readLine();
                    st = new StringTokenizer(line, " \t");
                    date = st.nextToken();  //  date
                    st.nextToken();  //   zone
                    
                    for (int k = 0; k < variableCount; k++) {
                        vals[k][i][j] = Double.valueOf(st.nextToken()).doubleValue();
                    }
                }
                int yr = Integer.valueOf(date.substring(0, 4)).intValue();
                int mo = Integer.valueOf(date.substring(5, 7)).intValue() - 1;
                int da = Integer.valueOf(date.substring(8, 10)).intValue();
                mdt.set(yr, mo, da, 0, 0, 0);
                dates[i] = mdt.getJulian();
            }
        } catch (FileNotFoundException e) {
            System.out.println("GIS data file " + data_file_name + " not found.");
            e.printStackTrace();
            
        } catch (IOException e) {
            System.out.println("GIS data file " + data_file_name + " IO exception.");
            e.printStackTrace();
            
        } catch (Exception e) {
            System.out.println("line = " + line);
            e.printStackTrace();
            
        } finally{
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}
        }
    }
    
    public void write(String fileName, double[] dates, double[][][] data,
            int timeCount, int variableCount, int zoneCount, 
            ArrayList<String> variableNames) {
        //        data[timeCount][variableCount][zoneCount]
        OuiCalendar mdt = new OuiCalendar();
//      Format sf = new Format ("%11.3e");  // doesn't work
//      Format sf = new Format ("%f");

        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(fileName));
            System.out.println("SpaceTimeSeriesData: writing file " + fileName);
            out.println("#");
            out.println("# Begin DBF");
            out.println("# timestamp,#FIELD_ISODATETIME,25,0");
            out.println("# nzone,#FIELD_DECIMAL,10,2");
            
            for (int i = 0; i < variableNames.size(); i++) {
                out.println("# " + variableNames.get(i) + ",#FIELD_DECIMAL,10,2");
            }
            out.println("# End DBF");
            out.println("#");
            
            out.print("timestamp\tnzone");
            for (int i = 0; i < variableNames.size(); i++) {
                out.print("   " + variableNames.get(i));
            }
            out.println("");
            
            out.print("25d\t10n");
            for (int i = 0; i < variableNames.size(); i++) {
                out.print("\t10n");
            }
            out.println("");
            
            for (int i = 0; i < timeCount; i++) {
                mdt.setJulian(dates[i]);
                for (int j = 0; j < zoneCount; j++) {
                    out.print(mdt.getGISDateTime() + "\t" + (j+1));
                    
                    for (int k = 0; k < variableCount; k++) {
//                        out.print("\t" + sf.form (data[k][i][j]));
                        out.print("\t" + data[k][i][j]);
                    }
                    out.println("");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            if (out!= null) out.close();
        }
    }
    
    /**
     * Getter for property dates.
     * @return Value of property dates.
     */
    public double[] getDates() {
        return this.dates;
    }
    
    /**
     * Setter for property dates.
     * @param dates New value of property dates.
     */
    public void setDates(double[] dates) {
        this.dates = dates;
    }

    public int getVariableIndex(String string) {
        int indexOf = variable_names.indexOf(string);
        if (indexOf < 0) {
            System.out.println ("SpaceTimeSeriesData variable = " + string + " not found in file = " + data_file_name);
        }
        return indexOf;
    }

    public String getVariableNameAt (int col) {
        String get = variable_names.get(col);
        if (get == null) {
            System.out.println ("SpaceTimeSeriesData variable at index = " + col + " not found in file = " + data_file_name);
        }
        return get;
    }

    public TimeSeries getTimeSeries(String varName, int spaceIndex) {
        return getTimeSeries(getVariableIndex (varName), spaceIndex);
    }
            
    public TimeSeries getTimeSeries(int variableIndex, int spaceIndex) {
        double[] dataSlice = new double[dates.length];
        for (int ts = 0; ts < dates.length; ts++) {
            dataSlice[ts] = vals[variableIndex][ts][spaceIndex];
        }

        TimeSeries timeSeries = new TimeSeries();
        timeSeries.setDates(dates);
        timeSeries.setVals(dataSlice);

        return timeSeries;
    }
}
