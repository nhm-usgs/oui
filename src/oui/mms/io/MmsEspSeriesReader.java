package oui.mms.io;

import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.SetOuiCalendarException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import oui.mms.datatypes.TimeSeries;

public class MmsEspSeriesReader {
    int fore_year;
    String file_name;
    double[] dates = null;
//    double[] values = null;
    int data_line_count;
    OuiCalendar start_mdt = null;
    OuiCalendar end_mdt = null;
    OuiCalendar fore_start_mdt = null;
    OuiCalendar fore_end_mdt = null;
    
    private String[] traceYears = null;
    private int steps = -1;
    
    public MmsEspSeriesReader(String fn, OuiCalendar foreStart) {
        fore_start_mdt = foreStart;
        file_name = fn;
        
        getSteps ();
        dates = new double[steps];
        
        OuiCalendar foo = new OuiCalendar ();
        foo.setJulian(fore_start_mdt.getJulian());
        for (int i = 0; i < steps; i++) {
            dates[i] = foo.getJulian() + i;
        }
        fore_end_mdt = new OuiCalendar ();
        fore_end_mdt.setJulian(dates[steps-1]);
    }
    
    private int getSteps () {
       if (steps >= 0) return steps;
        
        BufferedReader in = null;
        ArrayList foo = new ArrayList();
        
        try {
            String line;
            
            in = new BufferedReader(new FileReader(file_name));
            
            //  Read the variable count
            line = in.readLine();
            int var_count = Integer.valueOf(line).intValue();
            
            //  Read the start time of the first trace
            line = in.readLine();
            
            try {
                start_mdt = new OuiCalendar();
                start_mdt.setDT (line);
            } catch (SetOuiCalendarException e) {
                System.out.println("MmsEspSeriesReader:  read bad start time");
                System.out.println(line);
            }
            
            //  Read the end time of the first trace
            line = in.readLine();
            try {
                end_mdt = new OuiCalendar();
                end_mdt.setDT (line);
            } catch (SetOuiCalendarException e) {
                System.out.println("MmsEspSeriesReader:  read bad end time");
                System.out.println(line);
            }
            
            steps = (int)(end_mdt.getJulian()) - (int)(start_mdt.getJulian()) + 1;
            
        } catch (FileNotFoundException e) {
            System.out.println(file_name);
        } catch (IOException e) {
            System.out.println("Can't read the header of " + file_name);
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}
        }
        
        return steps;
    }
    
    public String[] getTraceYears() {
        
        if (traceYears != null) return traceYears;
        
        BufferedReader in = null;
        ArrayList<String> foo = new ArrayList<String>();
        
        try {
            String line;
            OuiCalendar mdt = new OuiCalendar();
            OuiCalendar trace_start = new OuiCalendar();
            OuiCalendar trace_end = new OuiCalendar();
            float value;
            int year, mon, day, hour, min, sec;
            StringTokenizer st;
            
            in = new BufferedReader(new FileReader(file_name));
            
            //  Read the variable count
            line = in.readLine();
            int var_count = Integer.valueOf(line).intValue();
            
            //  Read the start time of the first trace
            line = in.readLine();
            
            try {
                start_mdt = new OuiCalendar();
                start_mdt.setDT(line);
            } catch (SetOuiCalendarException e) {
                System.out.println("MmsEspSeriesReader:  read bad start time");
                System.out.println(line);
            }
            
            //  Read the end time of the first trace
            line = in.readLine();
            try {
                end_mdt = new OuiCalendar();
                end_mdt.setDT(line);
            } catch (SetOuiCalendarException e) {
                System.out.println("MmsEspSeriesReader:  read bad end time");
                System.out.println(line);
            }
            
            OuiCalendar endTraceMdt = new OuiCalendar();
            endTraceMdt.setJulian(end_mdt.getJulian());
            line = in.readLine();
            
            while (line != null) {
                st = new StringTokenizer(line, " ");
                year = Integer.valueOf(st.nextToken()).intValue();
                mon = Integer.valueOf(st.nextToken()).intValue();
                day = Integer.valueOf(st.nextToken()).intValue();
                
                if ((year == endTraceMdt.getYear()) && (mon == endTraceMdt.getMonth()) && (day == endTraceMdt.getDay())) {
                    endTraceMdt.set(year + 1, mon - 1, day);
                    foo.add("" + year);
                }
                line = in.readLine();
            }
            
            
        } catch (FileNotFoundException e) {
            System.out.println(file_name);
        } catch (IOException e) {
            System.out.println("Can't read the header of " + file_name);
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}
        }
        
        String[] ret = new String[foo.size()];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = (String)(foo.get(i));  
        }
        
        return ret;
    }
    
//    public MmsEspSeriesReader(int i, String fn, int y, OuiCalendar foreStart) {
//        this(i, fn, y);
//        fore_start_mdt = foreStart;
//    }
//    
//    public MmsEspSeriesReader(int i, String fn, int y) {
//        match_index = i;
//        file_name = fn;
//        fore_year = y;
//    }
//  
//    public TimeSeriesCookie getTimeSeries() {
//        return new TimeSeries("" + fore_year, getDates(), getValues(), fore_start_mdt, fore_end_mdt, null, file_name, "unknown");
//    }
    
    public TimeSeries getForecastSeries(int column, int traceNum) {
        
//        double fore_start = fore_start_mdt.getJulian();
        traceYears = getTraceYears();
        TimeSeries ts =  new TimeSeries(traceYears[traceNum], dates, getValues(column, traceNum), fore_start_mdt, fore_end_mdt, null, file_name, "unknown");
        return ts;
    }
    
//    public double[] getDates() {
//        if (dates == null) {
//            read();
//        }
//        
//        return (dates);
//    }
    
//    public int getStartYear() {
//        if (start_mdt == null) read();
//        return (start_mdt.getYear());
//    }
//    
//    public String getStartTime() {
//        if (start_mdt == null) read();
//        return (start_mdt.getControlFileDateTime());
//    }
//    
//    public String getEndTime() {
//        if (end_mdt == null) read();
//        return (end_mdt.getControlFileDateTime());
//    }
//    
//    public String getStartFore() {
//        if (fore_start_mdt == null) read();
//        return (fore_start_mdt.getControlFileDateTime());
//    }
//    
//    public String getEndFore() {
//        if (fore_end_mdt == null) read();
//        return (fore_end_mdt.getControlFileDateTime());
//    }
    
    public double[] getValues(int match_index, int traceNum) {
        BufferedReader in = null;
        double[] values = null;
        try {
            String line;
            //            int match_index = -1;
            OuiCalendar mdt = new OuiCalendar();
            OuiCalendar trace_start = new OuiCalendar();
            OuiCalendar trace_end = new OuiCalendar();
            float value;
            int i, year, mon, day, hour, min, sec;
            StringTokenizer st;
            
            in = new BufferedReader(new FileReader(file_name));
            
            //  Read the variable count
            line = in.readLine();
            int var_count = Integer.valueOf(line).intValue();
            
            //  Read the start time of the first trace
            in.readLine();
            in.readLine();
            
/*
 *  Skip to the requested trace
 */
            OuiCalendar prevEndDate = new OuiCalendar();
            prevEndDate.set(Integer.parseInt(traceYears[traceNum]), start_mdt.getMonth(), start_mdt.getDay());
            while ((line = in.readLine()) != null) {
                st = new StringTokenizer(line, " ");
                year = Integer.valueOf(st.nextToken()).intValue();
                mon = Integer.valueOf(st.nextToken()).intValue();
                day = Integer.valueOf(st.nextToken()).intValue();
                hour = Integer.valueOf(st.nextToken()).intValue();
                min = Integer.valueOf(st.nextToken()).intValue();
                sec = Integer.valueOf(st.nextToken()).intValue();
                mdt.set(year, mon, day, hour, min, sec);
                
                if (mdt.equals(prevEndDate)) break;
            }
            
            values = new double[steps];
            
            System.out.println("reading " + file_name);
            int j = 0;
            int k = 0;
            boolean started = false;
            while ((line = in.readLine()) != null) {
                
                if (j == 50) {
                    System.out.print(".");
                    j = 0;
                }
                j++;
                
                st = new StringTokenizer(line, " ");
                year = Integer.valueOf(st.nextToken()).intValue();
                mon = Integer.valueOf(st.nextToken()).intValue();
                day = Integer.valueOf(st.nextToken()).intValue();
                hour = Integer.valueOf(st.nextToken()).intValue();
                min = Integer.valueOf(st.nextToken()).intValue();
                sec = Integer.valueOf(st.nextToken()).intValue();
                mdt.set(year, mon, day, hour, min, sec);
                    
                    if (k == steps) break;
                    
                    for (i = 0; i < match_index - 1; i++) {
                        st.nextToken();                   // skip past other values
                    }
                    
                    values[k] =  Double.valueOf(st.nextToken()).doubleValue();
                    
//                    System.out.println("value = " + values[k] + " date = " + mdt.getMmsDateTime() + " steps = " + steps + " k = " + k);
                    
                    k++;
                
            }
            System.out.println(" done");
            
        } catch (FileNotFoundException e) {
            System.out.println(file_name);
        } catch (IOException e) {
            System.out.println("Can't read the header of " + file_name);
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}
        }
        return values;
        
    }
}


