package oui.mms.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;

public class MmsStatvarReader {
    private final String fileName;
    private double[] dates = null;
    private int variableCount = -1;
    OuiCalendar start = null;
    OuiCalendar end = null;
    String[] variableNames = null;
    String[] variableList = null;
    int[] variableIndexes = null;
    
    public MmsStatvarReader(String fn) {
        fileName = fn;
    }
    
    public String getFileName () {
        return fileName;
    }
    
    public OuiCalendar getStart() {
        if (this.start == null) readDates ();
        return this.start;
    }
    
    public OuiCalendar getEnd() {
        if (this.end == null) readDates ();
        return this.end;        
    }
    
    public double[] getDates() {
        if (this.dates == null) readDates ();
        return this.dates;
    }
    
    public int getVariableCount () {
        if (variableCount == -1) readHeader ();
        return this.variableCount;
    }

    public String[] getVariableNames () {
        if (variableNames == null) readHeader ();
        return this.variableNames;
    }
    
    public int[] getVariableIndexes() {
        if (variableIndexes == null) readHeader();
        return this.variableIndexes;
    }
    
    public String[] getVariableList() {
        if (variableList == null) {
            if ((variableNames == null) || (variableIndexes == null)) readHeader();

            variableList = new String[variableNames.length];
            for (int i = 0; i < variableList.length; i++) {
                variableList[i] = variableNames[i] + " " + variableIndexes[i];
            }
        }
        return this.variableList;
   }
    
    public TimeSeries getTimeSeries(String dataName) {
        double[] vals;
        try {
            StringTokenizer st = new StringTokenizer(dataName, " ");
            String var_name = st.nextToken();
            int index = Integer.valueOf(st.nextToken());
            vals = getValues(var_name, index);

            return new TimeSeries(dataName, getDates(), vals, getStart(), getEnd(), dataName, fileName, "unknown");
        } catch (NumberFormatException e) {
//            System.out.println ("MmsStatvarReader: problem reading " + fileName);
//            System.out.println ("   line = " + dataName);
        }
        return null;
    }
   
    private void readDates() {
        BufferedReader in = null;
        String line;
        OuiCalendar mdt = new OuiCalendar();

        try {
            in = new BufferedReader (new FileReader (fileName));
            
            int var_count = Integer.parseInt (in.readLine ());
            for (int i = 0; i < var_count; i++) {
                line = in.readLine();
            }
            
            int data_line_count = 0;
            while ((line = in.readLine()) != null) {
                data_line_count++;
            }

            dates  = new double[data_line_count];
            in.close ();
            in = null;
            
            in = new BufferedReader (new FileReader (fileName));
            
            var_count = Integer.parseInt (in.readLine ());
            for (int i = 0; i < var_count; i++) {
                line = in.readLine();
            }
                
            data_line_count = 0;
            while ((line = in.readLine()) != null) {                
                StringTokenizer st = new StringTokenizer(line, " ");
                st.nextToken();  // skip past line number;
                
                int year = Integer.valueOf(st.nextToken());
                int mon = Integer.valueOf(st.nextToken());
                int day = Integer.valueOf(st.nextToken());
                int hour = Integer.valueOf(st.nextToken());
                int min = Integer.valueOf(st.nextToken());
                int sec = Integer.valueOf(st.nextToken());
                mdt.set (year, mon-1, day, hour, min, sec);
                
                dates[data_line_count++] =  mdt.getJulian();
            }
            
            start = new OuiCalendar();
            start.setJulian(dates[0]);
            end = new OuiCalendar();
            end.setJulian(dates[data_line_count - 1]);

        } catch (IOException e) {
            start = null;
            end = null;
//            e.printStackTrace();
        } catch (NumberFormatException e) {
            start = null;
            end = null;

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException E) {
            }
        }
    }
    
    private void readHeader() {
        BufferedReader in = null;
        String line;

        try {
            in = new BufferedReader(new FileReader(fileName));
            variableCount = Integer.valueOf(in.readLine());
            
            variableNames = new String[variableCount];
            variableIndexes = new int[variableCount];

            for (int i = 0; i < variableCount; i++) {
                line = in.readLine();
                StringTokenizer st = new StringTokenizer (line, " ");
                variableNames[i] = st.nextToken ();
                variableIndexes[i] = Integer.valueOf (st.nextToken ());
            }
            
        } catch (IOException e) {
            //            e.printStackTrace();
            variableNames = null;
            variableIndexes = null;
        } catch (NumberFormatException e) {
//            e.printStackTrace();
            variableNames = null;
            variableIndexes = null;
            
        } finally {
            try {
                if (in!= null) in.close();
            } catch (IOException E) {}
        }
    }
    
    public double[] getValues(String name) {
        String foo = name.replace('[', ' ').replace(']', ' ');
        StringTokenizer st = new StringTokenizer(foo, " ");
        String var_name = st.nextToken();
        int index = Integer.valueOf (st.nextToken ());
        
        return getValues (var_name, index);
    }

    public double[] getValues(String var_name, int index) {
        String line;
        BufferedReader in = null;
        int data_line_count = 0;

        try {
            in = new BufferedReader (new FileReader (fileName));
            
            int var_count = Integer.valueOf(in.readLine());
            for (int i = 0; i < var_count; i++) {
                line = in.readLine();
            }
            
            data_line_count = 0;
            while ((line = in.readLine()) != null) {
                data_line_count++;
            }
            in.close();
            in = null;
            
         } catch (IOException e) {
//            e.printStackTrace();   
         } catch (NumberFormatException e) {
//            e.printStackTrace();             
            
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}   
        }
        
        double[] values = new double[data_line_count];
        
        try {
            String name = var_name + " " + index;
            int match_index = -1;
            float value;
            StringTokenizer st;
            
            in = new BufferedReader(new FileReader(fileName));
            
            int var_count = Integer.valueOf(in.readLine());
            for (int i = 0; i < var_count; i++) {
                line = in.readLine();
                if (line.equals(name)) {
                    match_index = i;
                }
            }
            
            System.out.print("reading " + fileName);
            
            if (match_index == -1) {
                System.out.println("   " + name + " not found!");
                return null;
            }
            
            int j = 0;
            int k = 0;
            while ((line = in.readLine()) != null) {
                if (j == 50) {
                    System.out.print(".");
                    j = 0;
                }
                j++;
                
                st = new StringTokenizer(line, " ");
                st.nextToken();  // skip past line number;
                st.nextToken();  // skip past year
                st.nextToken();  // skip past month
                st.nextToken();  // skip past day
                st.nextToken();  // skip past hour
                st.nextToken();  // skip past minute
                st.nextToken();  // skip past second

                for (int i = 0; i < match_index; i++) {
                    st.nextToken();                   // skip past other values
                }
                
                values[k] =  Double.valueOf(st.nextToken());
                k++;
            }
            System.out.println(" done");
            
         } catch (IOException e) {
//            e.printStackTrace();
             values = null;
         } catch (NumberFormatException e) {

//            e.printStackTrace();
             values = null;
            
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}   
        }
        
        return values;
    }
}


