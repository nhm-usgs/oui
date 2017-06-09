package oui.mms.datatypes;

import java.util.ArrayList;

public interface SpaceTimeSeriesInterface {
    public int getVariableCount();
    public ArrayList<String> getVariableNames();
    public double getVariableMinValue();
    public double getVariableMaxValue();
    public double getVariableRange();
    public OuiCalendar getStartTime();
    public OuiCalendar getEndTime();
    public OuiCalendar getCurrentTime();
    public int getZoneCount();
    public int getTimeCount();
    public int getTimeStep();
    public void setVariableIndex(int vi);
    public double[][][] getData();
    public void setTimeStep(int ts);
//    public boolean nextTimeStep();
    public double[] getCurrentData();
    public void write(String fileName, double[] dates, double[][][] data, int timeCount, int variableCount, int zoneCount, ArrayList<String> variableNames);
    public double[] getDates();
    public void setDates(double[] dates);
    public int getVariableIndex(String string);
    public String getVariableNameAt (int col);
    public TimeSeries getTimeSeries(String varName, int spaceIndex);
    public TimeSeries getTimeSeries(int variableIndex, int spaceIndex);
}
