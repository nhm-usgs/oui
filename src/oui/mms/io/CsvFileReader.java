package oui.mms.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.SetOuiCalendarException;
import oui.mms.datatypes.TimeSeries;

public class CsvFileReader {
    private final String fileName;
    private double[] dates = null;
    private int variableCount = -1;
    OuiCalendar start = null;
    OuiCalendar end = null;
    String[] variableNames = null;
    String[] variableList = null;
    int[] variableIndexes = null;
    
    public CsvFileReader(String fn) {
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
        String var_name = dataName.split(" ")[0];
//        int index = Integer.valueOf(st.nextToken()).intValue();
        double[] vals = getValues(var_name, 1);
        if (vals == null) return null;
        return new TimeSeries(dataName, getDates(), vals, getStart(), getEnd(), dataName, fileName, "unknown");
    }
   
    private void readDates() {
        BufferedReader in = null;
        String line;
        OuiCalendar mdt = OuiCalendar.getInstance();

        try {
            in = new BufferedReader (new FileReader (fileName));
            in.readLine();
            
            int data_line_count = 0;
            while ((line = in.readLine()) != null) {
                data_line_count++;
            }

            dates  = new double[data_line_count];
            in.close ();
            in = null;
            
            in = new BufferedReader (new FileReader (fileName));
            in.readLine();
            data_line_count = 0;
            while ((line = in.readLine()) != null) {
                String date_str = line.substring(0, line.indexOf(','));
                mdt.setCsvDate (date_str);
                dates[data_line_count++] =  mdt.getJulian();
            }
            
            start = OuiCalendar.getInstance();
            start.setJulian(dates[0]);
            end = OuiCalendar.getInstance();
            end.setJulian(dates[data_line_count - 1]);
 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | SetOuiCalendarException ex) {
            Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            start = null;
            end = null;
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}   
        }
    }
    
    private void readHeader() {
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(fileName));
            variableNames = in.readLine().split(",");
            variableCount = variableNames.length;
            variableIndexes = new int[variableCount];
            for (int i = 0; i < variableCount; i++) variableIndexes[i] = 1;
            
        } catch (IOException e) {
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
        int index = Integer.parseInt (st.nextToken ());
        
        return getValues (var_name, index);
    }

    public double[] getValues(String var_name, int index) {
        String line;
        BufferedReader in = null;
        int data_line_count = 0;

        try {
            in = new BufferedReader(new FileReader(fileName));
            in.readLine();
            
            data_line_count = 0;
            while ((line = in.readLine()) != null) {
                data_line_count++;
            }
            in.close();
            in = null;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}
        }

        double[] values = new double[data_line_count];
        
        int match_index = -1;
        for (int i = 0; i < variableNames.length; i++) {
            if (var_name.equals(variableNames[i])) {
                match_index = i;
                break;
            }
        }
        
        if (match_index == -1) {
            System.out.println("CsvFileReader: variable " + var_name + " not found!");
            return null;
        }
        
        try {
            in = new BufferedReader(new FileReader(fileName));
            in.readLine();
            
            System.out.print("reading " + fileName);
            
            int j = 0;
            int k = 0;
            while ((line = in.readLine()) != null) {
                if (j == 50) {
                    System.out.print(".");
                    j = 0;
                }
                j++;
                
                String[] val_str = line.split(",");
                values[k++] =  Double.valueOf(val_str[match_index]);
            }
            System.out.println(" done");
             
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CsvFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            values = null;
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}   
        }
        
        return values;
    }
}


