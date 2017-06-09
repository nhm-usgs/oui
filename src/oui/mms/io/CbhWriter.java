package oui.mms.io;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import oui.mms.datatypes.OuiCalendar;

public class CbhWriter {
    double[] dates = null;
    int data_count = -1;
    OuiCalendar start_time = null;
    OuiCalendar end_time = null;
    String header;
    ArrayList<double []> data = new ArrayList(10);
    
    public CbhWriter(OuiCalendar st, OuiCalendar et, String header) {
        start_time = st;
        end_time = et;
        this.header = header;
    }

    public void addTrace(double[] trace) {
        data.add (trace);
    }


    public void write(String fn) {
        String file_name = fn;
        OuiCalendar curr = (OuiCalendar)(start_time.clone());
        
        int j = 0;
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(file_name));
            System.out.println ("CbhWriter: writing file " + file_name);
            out.println(header);
            out.println("################################################################################");
            
            int i = 0;
            while ((int)(curr.getJulian()) <= (int)(end_time.getJulian())) {
                out.print(curr.getMmsDateTime());
                
                for (j = 0; j < data.size(); j++) {
                    double[] vals = (double[])(data.get(j));
                    String s = String.format(" %1$.2f", vals[i]);
                    out.print(s);
                }
                out.println();
                
                i++;
                curr.add(Calendar.DATE, 1);
            }
            

        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println ("Bombed on " + curr.getMmsDateTime());
            System.out.println ("data j = " + j);
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            if (out!= null) out.close();
        }
    }
    
    public static void write(File file, int numcol, double[] dates,
            double[][] data, String header, String format) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(file.getPath()));
            System.out.println ("CbhWriter: writing file " + file.getPath());
            out.println(header);
            out.println("################################################################################");
            
            OuiCalendar curr = new OuiCalendar();

            for (int j = 0; j < dates.length; j++) {   // this is the loop over the time steps.
                curr.setJulian(dates[j]);
                out.print(curr.getMmsDateTime());
                for (int i = 0; i < numcol; i++) {     // this is the loop over the columns 
                    String s = String.format(format, data[i][j]);
                    out.print(s);
                }
                out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }
}
