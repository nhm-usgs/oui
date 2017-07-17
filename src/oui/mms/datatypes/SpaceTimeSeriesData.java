package oui.mms.datatypes;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpaceTimeSeriesData implements SpaceTimeSeriesInterface {
    String data_file_name;
    ArrayList<String> variable_names = new ArrayList<>();
    double[] variable_min, variable_max, variable_range;
    int zoneCount, timeCount, variableCount;
    OuiCalendar start_time, end_time;
    OuiCalendar current_time;
    int current_time_step;
    double[][][] vals = null;  // indexes are variable, time step, space index
    double[] dates = null;
    int var_index;

    public SpaceTimeSeriesData() {
        start_time = OuiCalendar.getInstance();
        end_time = OuiCalendar.getInstance();
        current_time = OuiCalendar.getInstance();
    }

    public SpaceTimeSeriesData(String data_file_name) {
        this();
        this.data_file_name = data_file_name;
        scanDataFile();
        readData();
    }

    @Override
    public int getVariableCount() {
        return variableCount;
    }

    @Override
    public ArrayList<String> getVariableNames() {
        return variable_names;
    }

    @Override
    public double getVariableMinValue() {
        return variable_min[var_index];
    }

    @Override
    public double getVariableMaxValue() {
        return variable_max[var_index];
    }

    @Override
    public double getVariableRange() {
        return variable_range[var_index];
    }

    @Override
    public OuiCalendar getStartTime() {
        return start_time;
    }

    @Override
    public OuiCalendar getEndTime() {
        return end_time;
    }

    @Override
    public OuiCalendar getCurrentTime() {
        return current_time;
    }

    @Override
    public int getZoneCount() {
        return zoneCount;
    }

    @Override
    public int getTimeCount() {
        return timeCount;
    }

    @Override
    public int getTimeStep() {
        return current_time_step;
    }

    @Override
    public void setVariableIndex(int vi) {
        this.var_index = vi;
    }

    @Override
    public double[][][] getData() {
        return vals;
    }

    @Override
    public void setTimeStep(int ts) {
        if (ts < 0) {
            ts = 0;
        }
        if (ts > timeCount - 1) {
            ts = timeCount - 1;
        }

        current_time_step = ts;
        current_time.setJulian(dates[ts]);
    }

    private void scanDataFile() {
        BufferedReader in = null;
        try {
            String line;
            StringTokenizer st;
            in = new BufferedReader(new FileReader(data_file_name));
            // Skip header
            line = in.readLine();
            while (line.charAt(0) == '#') {
                line = in.readLine();
            }
            st = new StringTokenizer(line, " \t");
            st.nextToken();
            String zone_name = st.nextToken();
            while (st.hasMoreTokens()) {
                variable_names.add(st.nextToken());
            }
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
                    int yr = Integer.parseInt(date.substring(0, 4));
                    int mo = Integer.parseInt(date.substring(5, 7)) - 1;
                    int da = Integer.parseInt(date.substring(8, 10));
                    start_time.set(yr, mo, da, 0, 0, 0);
                }
                
                last_zone_id = zone_id;
                zone_id = Integer.parseInt(st.nextToken());
                
                if (zone_id == 1) {
                    timeCount++;
                    zoneCount = last_zone_id;
                }
                
                for (int i = 0; i < variableCount; i++) {
                    double val = Double.parseDouble(st.nextToken());
                    variable_min[i] = Math.min(val, variable_min[i]);
                    variable_max[i] = Math.max(val, variable_max[i]);
                }
                count++;
            }
            if (date != null) {
                int yr = Integer.parseInt(date.substring(0, 4));
                int mo = Integer.parseInt(date.substring(5, 7));
                int da = Integer.parseInt(date.substring(8, 10));
                end_time.set(yr, mo, da, 0, 0, 0);
            }
            for (int i = 0; i < variableCount; i++) {
                variable_range[i] = variable_max[i] - variable_min[i];
                variable_range[i] = variable_range[i] + (variable_range[i] * 0.01);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SpaceTimeSeriesData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SpaceTimeSeriesData.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(SpaceTimeSeriesData.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    @Override
    public double[] getCurrentData() {
        if (vals == null) {
            System.out.println("getCurrentData vals = null");
        }
        return vals[this.var_index][current_time_step];
    }

    private void readData() {
        String line = null;
        String date;
        StringTokenizer st;
        BufferedReader in = null;
        OuiCalendar mdt = OuiCalendar.getInstance();

        vals = new double[variableCount][timeCount][zoneCount];
        dates = new double[timeCount];

        try {
            in = new BufferedReader(new FileReader(data_file_name));

            // Skip header
            line = in.readLine();
            while (line.charAt(0) == '#') {
                line = in.readLine();
            }
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
                        vals[k][i][j] = Double.valueOf(st.nextToken());
                    }
                }
                if (date != null) {
                    int yr = Integer.parseInt(date.substring(0, 4));
                    int mo = Integer.parseInt(date.substring(5, 7)) - 1;
                    int da = Integer.parseInt(date.substring(8, 10));
                    mdt.set(yr, mo, da, 0, 0, 0);
                    dates[i] = mdt.getJulian();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("SpaceTimeSeriesData.readData: GIS data file "
                    + data_file_name + " not found.");

        } catch (IOException e) {
            System.out.println("SpaceTimeSeriesData.readData: GIS data file "
                    + data_file_name + " IO exception.");

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException E) {
            }
        }
    }

    @Override
    public void write(String fileName, double[] dates, double[][][] data,
            int timeCount, int variableCount, int zoneCount,
            ArrayList<String> variableNames) {

        OuiCalendar mdt = OuiCalendar.getInstance();
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
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
            for (String variableName : variableNames) {
                out.print("\t10n");
            }
            out.println("");

            for (int i = 0; i < timeCount; i++) {
                mdt.setJulian(dates[i]);
                for (int j = 0; j < zoneCount; j++) {
                    out.print(mdt.getGISDateTime() + "\t" + (j + 1));

                    for (int k = 0; k < variableCount; k++) {
//                        out.print("\t" + sf.form (data[k][i][j]));
                        out.print("\t" + data[k][i][j]);
                    }
                    out.println("");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SpaceTimeSeriesData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Getter for property dates.
     *
     * @return Value of property dates.
     */
    @Override
    public double[] getDates() {
        return this.dates;
    }

    /**
     * Setter for property dates.
     *
     * @param dates New value of property dates.
     */
    @Override
    public void setDates(double[] dates) {
        this.dates = dates;
    }

    @Override
    public int getVariableIndex(String string) {
        int indexOf = variable_names.indexOf(string);
        if (indexOf < 0) {
            System.out.println("SpaceTimeSeriesData variable = " + string
                    + " not found in file = " + data_file_name);
        }
        return indexOf;
    }

    @Override
    public String getVariableNameAt(int col) {
        String get = variable_names.get(col);
        if (get == null) {
            System.out.println("SpaceTimeSeriesData variable at index = " + col
                    + " not found in file = " + data_file_name);
        }
        return get;
    }

    @Override
    public TimeSeries getTimeSeries(String varName, int spaceIndex) {
        return getTimeSeries(getVariableIndex(varName), spaceIndex);
    }

    @Override
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
