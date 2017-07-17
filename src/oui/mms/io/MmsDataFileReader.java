package oui.mms.io;

import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;

public class MmsDataFileReader {
    double[] dates = null;
    int data_count = -1;
    OuiCalendar start_time = null;
    OuiCalendar end_time = null;
    private ArrayList<Integer> max_index = null;
    private ArrayList<String> var_names = null;
    private int numberOfColumns = -1;
    private String[] variableList = null;
    private double[][] allData = null;
    private String header = null;
    private String description = null;
    private String fileName = null;
    
    public MmsDataFileReader(String fn) {
        this.fileName = fn;
        readHeader();
    }
    
    public ArrayList<Integer> getMaxIndex() {
        if (max_index == null) readHeader();
        return this.max_index;
    }
    
    public ArrayList<String> getVarNames() {
        if (var_names == null) readHeader();
        return this.var_names;
    }

    public int getNumberOfColumns() {
        if (numberOfColumns == -1) readHeader();
        return numberOfColumns;
    }
    
    public double[][] getAllData() {
        if (allData == null) readAllData();
        return allData;
    }
    
    public String getHeader() {
        if (header == null) readHeader();
        return header;
    }
    
    public OuiCalendar getStart() {
        if (start_time == null) {
            readAllData();
        }
        return start_time;
    }
    
    public OuiCalendar getEnd() {
        if (end_time == null)
            readAllData();
        return end_time;
    }
    
    public double[] getDates() {
        if (dates == null) readAllData();
        return (dates);
    }
    
/*
 **  Pass in the column number.  This includes the date columns.
 **  Starts with zero. Column 0 is the year, Column 1 is the month, etc.
 **  Column 6 is the first data column.
 */
    public double[] getValues(int col_num) {
        if (allData == null) readAllData();
        return allData[col_num - 6];
    }
    
    public String[] getVariableList() {
        if (variableList == null) {
            variableList = new String[numberOfColumns];
            int count = 0;
            for (int i = 0; i < var_names.size(); i++) {
                String var_name = var_names.get(i);
                Integer foo = max_index.get(i);
                for (int j = 0; j < foo; j++) {
                    variableList[count++] = var_name + " " + (j + 1);
                }
            }
        }
        return this.variableList;
    }
    
    public TimeSeries getTimeSeries(String dataName) {
        StringTokenizer st = new StringTokenizer(dataName, " ");
        String var_name = st.nextToken();
        int index = Integer.parseInt(st.nextToken());
        return new TimeSeries(dataName, getDates(), getValues(var_name, index), getStart(), getEnd(), dataName, fileName, "unknown");
    }
    
/*
 **  Pass in the variable name and the index of this variable.  Indicies
 **  start with 1.  Using the data file header, figure out the column and
 **  call getValues (col_num).
 */
    public double[] getValues(String var_name, int index) {
        int col_num = 5;
        boolean found = false;
/*
 *  figure out the column number for this var name and index
 */
        for (int i = 0; i < var_names.size(); i++) {
            if (var_name.equals(var_names.get(i))) {
                found = true;
                col_num = col_num + index;
                break;
            } else {
                Integer foo = max_index.get(i);
                col_num = col_num + foo;
            }
        }
        
        if (found) {
            return (getValues(col_num));
        } else {
            System.out.println(" MmsDataFileReader getValue: var_name = " +
            var_name + " index = " + index + " not found");
            return (null);
        }
    }
    
    private OuiCalendar getDateFromLine(String line) {
        return getDateFromLine(line, OuiCalendar.getInstance());
    }
    
    private OuiCalendar getDateFromLine(String line, OuiCalendar mdt) {
        String[] split = line.split("  *");
        int year = Integer.valueOf(split[0]);
        int mon = Integer.valueOf(split[1]);
        int day = Integer.valueOf(split[2]);
        int hour = Integer.valueOf(split[3]);
        int min = Integer.valueOf(split[4]);
        int sec = Integer.valueOf(split[5]);
        mdt.set(year, mon-1, day, hour, min, sec);

        return mdt;
    }  
    
    private void readAllData () {
        if (allData != null) return;
        if (var_names == null) readHeader();
        
   
        String line = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(fileName));
/*
 *  Get start_time, end_time, and number of timesteps.
 */ 
            line = in.readLine();
            while (!(line.startsWith("########"))) {
                line = in.readLine();
            }

            line = in.readLine();
            start_time = getDateFromLine(line);
            data_count = 1;
            
            String last_line = line;
            while ((line = in.readLine()) != null) {
                last_line = line;
                data_count++;
            }
            
            in.close();       
            end_time = getDateFromLine(last_line);
            
/*
 *  Read data and dates
 */
            allData = new double[numberOfColumns][data_count];
            dates = new double[data_count];
            
            in = new BufferedReader(new FileReader(fileName));

            line = in.readLine();
            while (!(line.startsWith("########"))) {
                line = in.readLine();
            }
                 
            StringTokenizer st;
            OuiCalendar currentDate = OuiCalendar.getInstance();
            int i = 0, j, k = 0;
            while ((line = in.readLine()) != null) {
                dates[i] = getDateFromLine(line, currentDate).getJulian();
//                System.out.println ("read date = " + currentDate +  " " + currentDate.getJulian() + " offset date = " + (start_time.getJulian() + i)); 
                
                st = new StringTokenizer(line, " ");
                
                String tok;
                tok = st.nextToken();
                tok = st.nextToken();
                tok = st.nextToken();
                tok = st.nextToken();
                tok = st.nextToken();
                tok = st.nextToken();
                for (j = 0; j < numberOfColumns; j++) {
                    allData[j][i] = Double.valueOf(st.nextToken());
                }
                
                k++;
                if (k == 50) {
                    System.out.print(".");
                    k = 0;
                }
                i++;
            }
            System.out.println(" done reading " + fileName);

        } catch (IOException ex) {
            System.out.println("Problem reading file " + fileName);
            System.out.println("   line = " + line);
            Logger.getLogger(MmsDataFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in!= null) in.close();
            } catch (IOException E) {}
        }

    }
    
    private void readHeader() {
        
        StringTokenizer st;
        String line = null;
        
        var_names = new ArrayList<String>(10);
        max_index = new ArrayList<Integer>(10);
        numberOfColumns = 0;
        
        header = "";
        BufferedReader in = null;
        
        try {
            String name;
            int size;
            in = new BufferedReader(new FileReader(fileName));
            line = in.readLine();                      // description line
            description = line;
            line = in.readLine();
            
            while (!(line.startsWith("########"))) {
                header = header + "\n" + line;
                
                if (!(line.startsWith("//")) && (line.length() > 0) && !(line.startsWith("########"))) {
                    st = new StringTokenizer(line, " ");
                    name = st.nextToken();
                    var_names.add(name);
                    size = Integer.valueOf(st.nextToken()).intValue();
                    max_index.add(new Integer(size));
                    numberOfColumns = numberOfColumns + size;
                }
                line = in.readLine();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem reading file " + fileName);
            System.out.println("   line = " + line);
        } finally {
            try {
                if (in!= null) in.close();
            } catch (IOException E) {}
        }
    }
    
    public static void main(String[] args) {
        MmsDataFileReader mdfr = new MmsDataFileReader(
        "/home/mms_workspaces/mms_work/input/data/efcarson.data");
        
        OuiCalendar start = mdfr.getStart();
        OuiCalendar end = mdfr.getEnd();
        
        double[] q = mdfr.getValues("runoff", 1);
        
        System.out.println("q size = " + q.length);
        System.out.println("Start date = " + start.getSQLDate());
        System.out.println("End date = " + end.getSQLDate());
    }
    
    /**
     * Getter for property fileName.
     * @return Value of property fileName.
     */
    public String getFileName() {
        return this.fileName;
    }
    
    /**
     * Setter for property fileName.
     * @param fileName New value of property fileName.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
}
