package oui.mms.io;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import oui.mms.datatypes.OuiCalendar;
import java.util.Calendar;

public class MmsDataFileWriter {
    double[] dates = null;
    int data_count = -1;
    OuiCalendar start_time = null;
    OuiCalendar end_time = null;
    String header;
    ArrayList<double []> data = new ArrayList<>(10);
    
    public MmsDataFileWriter(OuiCalendar st, OuiCalendar et, String header) {
        start_time = st;
        start_time.getMillis();
        end_time = et;
        end_time.getMillis();
        this.header = header;
    }

    public void addTrace(double[] trace) {
        data.add (trace);
    }


    public void write(String fn) {
        String file_name = fn;
        start_time.getMillis();
        OuiCalendar curr = (OuiCalendar)(start_time.clone());
        
        int j = 0;
        try (PrintWriter out = new PrintWriter(new FileWriter(file_name))) {
            System.out.println ("MmsDataFileWriter: writing file " + file_name);
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
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
