package oui.mms.datatypes;

import csvutils.CsvTableModelAdaptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvSpaceTimeSeries implements SpaceTimeSeriesInterface {

    String csvFileName;
    ArrayList<String> variable_names = new ArrayList();
    double[] variable_min, variable_max, variable_range;
    int zoneCount, timeCount, variableCount;
    OuiCalendar start_time, end_time;
    OuiCalendar current_time = new OuiCalendar();
    int current_time_step;
    double[][][] vals = null;  // indexes are variable, time step, space index
    double[] dates = null;
    int var_index;

    public CsvSpaceTimeSeries() {
        try {
            start_time = new OuiCalendar();
            start_time.setDT("1980-10-01");
            end_time = new OuiCalendar();
            end_time.setDT("1981-09-30");
        } catch (SetOuiCalendarException ex) {
            Logger.getLogger(CsvSpaceTimeSeries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CsvSpaceTimeSeries(String csvFileName) {
        this.csvFileName = csvFileName;
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

    public double getVariableMinValue() {
        return variable_min[var_index];
    }

    public double getVariableMaxValue() {
        return variable_max[var_index];
    }

    public double getVariableRange() {
        return variable_range[var_index];
    }

    public OuiCalendar getStartTime() {
        return start_time;
    }

    public OuiCalendar getEndTime() {
        return end_time;
    }

    public OuiCalendar getCurrentTime() {
        return current_time;
    }

    public int getZoneCount() {
        return zoneCount;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public int getTimeStep() {
        return current_time_step;
    }

    public void setVariableIndex(int vi) {
        this.var_index = vi;
    }

    public double[][][] getData() {
        return vals;
    }

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

    /**
     *
     */
    CsvTableModelAdaptor in = null;

    private void scanDataFile() {
        try {
            if (in == null) {
                in = new CsvTableModelAdaptor(csvFileName);
            }

            // Figure out the variable names
            HashSet<String> foo = new HashSet<String>();
            for (int i = 1; i < in.getColumnCount(); i++) {  // Start with 1 because "0" is "timestamp"
                foo.add(in.getColumnName(i));
            }
            variable_names = new ArrayList<String>(foo);
            variableCount = variable_names.size();

            variable_min = new double[variableCount];
            variable_max = new double[variableCount];
            variable_range = new double[variableCount];

            for (int i = 0; i < variableCount; i++) {
                variable_min[i] = 10000000.0;
                variable_max[i] = -10000000.0;
            }

            start_time = new OuiCalendar();
            // first time stamp is in first column, fourth row. The first line
            // doesn't count because it is the header.
            String date = (String) in.getValueAt(2, 0); 
            start_time.setDashDate(date);

            // Set zone_count (ie number of hrus)
            zoneCount = (in.getColumnCount() - 1) / variableCount;

            // Scan through everything looking for global min and max for each variable
            for (int i = 0; i < variableCount; i++) {
                String varName = variable_names.get(i);
                for (int j = 1; j < in.getColumnCount(); j++) {
                    if (varName.equals(in.getColumnName(j))) {
                        for (int k = 2; k < in.getRowCount(); k++) {
                            String fool = (String) in.getValueAt(k, j);
                            double val = Double.parseDouble(fool);
                            variable_min[i] = Math.min(val, variable_min[i]);
                            variable_max[i] = Math.max(val, variable_max[i]);
                        }
                    }
                }
            }
            
            end_time = new OuiCalendar();
            date = (String) in.getValueAt(in.getRowCount() - 1, 0); // last time stamp is in first column, last row.
            end_time.setDashDate(date);
            
            timeCount = in.getRowCount() - 2;

            for (int i = 0; i < variableCount; i++) {
                variable_range[i] = variable_max[i] - variable_min[i];
                variable_range[i] = variable_range[i] + (variable_range[i] * 0.01);
            }

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
        if (vals == null) {
            System.out.println("getCurrentData vals = null");
        }
        return vals[this.var_index][current_time_step];
    }

    private void readData() {
        OuiCalendar mdt = new OuiCalendar();

        vals = new double[variableCount][timeCount][zoneCount];
        dates = new double[timeCount];

        try {
            if (in == null) {
                in = new CsvTableModelAdaptor(csvFileName);
            }

            for (int k = 2; k < in.getRowCount(); k++) {
                for (int i = 0; i < variableCount; i++) {
                    String varName = variable_names.get(i);
                    for (int j = 1; j < in.getColumnCount(); j++) {
                        if (varName.equals(in.getColumnName(j))) {
                            int featureIndex = Integer.valueOf((String) in.getValueAt(0, j)); // The feature index is in row 1 after the header

                            double val = Double.parseDouble((String) in.getValueAt(k, j));
                            vals[i][k-2][featureIndex-1] = val; // vals[varIndex][timeIndex][featureIndex]
                        }
                    }
                }

                String date = (String) in.getValueAt(k, 0);
                mdt.setDashDate(date);

                dates[k-2] = mdt.getJulian();
            }

        } catch (FileNotFoundException e) {
            System.out.println("GIS data file " + csvFileName + " not found.");
            e.printStackTrace();

        } catch (IOException e) {
            System.out.println("GIS data file " + csvFileName + " IO exception.");
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void write(String fileName, double[] dates, double[][][] data,
            int timeCount, int variableCount, int zoneCount,
            ArrayList<String> variableNames) {
        System.out.println("CsvSpaceTimeSeries: write not implemented yet!");
    }

    /**
     * Getter for property dates.
     *
     * @return Value of property dates.
     */
    public double[] getDates() {
        return this.dates;
    }

    /**
     * Setter for property dates.
     *
     * @param dates New value of property dates.
     */
    public void setDates(double[] dates) {
        this.dates = dates;
    }

    public int getVariableIndex(String string) {
        int indexOf = variable_names.indexOf(string);
        if (indexOf < 0) {
            System.out.println("CsvSpaceTimeSeries variable = " + string + " not found in file = " + csvFileName);
        }
        return indexOf;
    }

    public String getVariableNameAt(int col) {
        String get = variable_names.get(col);
        if (get == null) {
            System.out.println("SpaceTimeSeriesData variable at index = " + col + " not found in file = " + csvFileName);
        }
        return get;
    }

    public TimeSeries getTimeSeries(String varName, int spaceIndex) {
        return getTimeSeries(getVariableIndex(varName), spaceIndex);
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
