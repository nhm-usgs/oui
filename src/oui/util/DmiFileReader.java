package oui.util;

import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.OuiCalendar;

import oui.mms.io.MmsDataFileReader;

public class DmiFileReader {
   String file_name;
   double[] dates = null;
   int data_count = -1;
   OuiCalendar start_time = null;
   OuiCalendar end_time = null;
   public ArrayList max_index = null;
   public ArrayList var_names = null;

   public DmiFileReader (String fn) {
      file_name = fn;
   }

   public OuiCalendar getStart () {
      StringTokenizer st;
      String line;

      if (start_time != null) {
         return (start_time);
      }

      try {
         BufferedReader in = new BufferedReader (new FileReader (file_name));
/*
** Skip over the header -- 3 lines;
*/
         line = in.readLine ();
         line = in.readLine ();
         line = in.readLine ();

/*
** Read line with start time and return a OuiCalendar.
           */
          line = in.readLine();
          in.close();

          st = new StringTokenizer(line, "\t");
          st.nextToken();  // var name
          st.nextToken();  // site id
          st.nextToken();  // datatype id
          start_time = OuiCalendar.getInstance();
          int year = Integer.parseInt(st.nextToken());
          int mon = Integer.parseInt(st.nextToken());
          int day = Integer.parseInt(st.nextToken());
          int hour = Integer.parseInt(st.nextToken());
          int min = Integer.parseInt(st.nextToken());
          int sec = Integer.parseInt(st.nextToken());
          start_time.set(year, mon - 1, day, hour, min, sec);

      } catch (FileNotFoundException ex) {
           Logger.getLogger(DmiFileReader.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(DmiFileReader.class.getName()).log(Level.SEVERE, null, ex);
       }
      return (start_time);
   }

   public OuiCalendar getEnd () {
      StringTokenizer st;
      String line, last_line = null;

      if (end_time != null) {
         return (end_time);
      }

      try {
         BufferedReader in = new BufferedReader (new FileReader (file_name));

/*
**  Skip lines to end
*/
         while ((line = in.readLine ()) != null) {
            last_line = line;
         }

         in.close ();
/*
** Read line with end time and return a OuiCalendar.
*/
System.out.println ("DmiFileReader:: last_line = " + last_line);
         st = new StringTokenizer (last_line, "\t");
         st.nextToken ();  // var name
         st.nextToken ();  // site id
         st.nextToken ();  // datatype id

         end_time = OuiCalendar.getInstance();
          int year = Integer.parseInt(st.nextToken());
          int mon = Integer.parseInt(st.nextToken());
          int day = Integer.parseInt(st.nextToken());
          int hour = Integer.parseInt(st.nextToken());
          int min = Integer.parseInt(st.nextToken());
          int sec = Integer.parseInt(st.nextToken());
          end_time.set(year, mon - 1, day, hour, min, sec);
          
      } catch (FileNotFoundException ex) {
           Logger.getLogger(DmiFileReader.class.getName()).log(Level.SEVERE, null, ex);
       } catch (IOException ex) {
           Logger.getLogger(DmiFileReader.class.getName()).log(Level.SEVERE, null, ex);
       }
      return (end_time);
   }

   public double[] getDates () {
      if (dates == null) {
         readDates ();
      }

      return (dates);
   }

   public void readDates () {

      if (dates != null)
         return;

      OuiCalendar start = getStart ();
      OuiCalendar end = getEnd ();

      data_count = (int)(end.getJulian () - start.getJulian ()) + 1;

      dates = new double[data_count];

      double sj = start.getJulian ();
      for (int i = 0; i < data_count; i++) {
         dates[i] = sj + i;
      }
   }

   public void getVariableNames () {
      System.out.println ("DmiFileReader:getVariableNames not implemented");
   }

   public double[] getValues (String var_name, int index) {

      StringTokenizer st;
      String line = null;

      if (data_count == -1) {
         readDates ();
      }

      double[] data = new double[data_count];
      boolean found = false;

      try {
         String name;
         String last_name = "qwerty";
         int var_count = 0;
         int size;
         BufferedReader in = new BufferedReader (new FileReader (file_name));
         line = in.readLine ();                      // header
         line = in.readLine ();                      // header
         line = in.readLine ();                      // header
         
         int count = 0;
         while ((line = in.readLine ()) != null) {
            st = new StringTokenizer (line, "\t");
            name = st.nextToken ();
//            System.out.println ("dmi name = " + name + " last_name =  " + last_name);
            if (name.equals (last_name)) {
               var_count++;
            } else {
               last_name = name;
               var_count = 1;
            }
               
//            System.out.println ("dmi comparing" + name + " to " + var_name);
//            System.out.println ("dmi comparing" + var_count + " to " + index);
            if ((name.equals (var_name)) && (var_count == index)) {
               found = true;

               st.nextToken ();            // site id
               st.nextToken ();            // datatype id
               st.nextToken ();            // year
               st.nextToken ();            // month
               st.nextToken ();            // day
               st.nextToken ();            // hour
               st.nextToken ();            // minute
               st.nextToken ();            // second
               data[count++] = Double.valueOf (st.nextToken ());
            }
         }          
         in.close ();
      } catch (Exception ex) {
         System.out.println ("Problem getting values from file " + file_name);
         System.out.println ("line = " + line);
      }

      if (found) {
         return (data);
      } else {
         return (null);
      }
   }

   public static void main (String[] args) {
      MmsDataFileReader mdfr = new MmsDataFileReader (
                         "/home/mms_work/input/data/Gunnison.data");

//      OuiCalendar start = mdfr.getStart ();
//      OuiCalendar end = mdfr.getEnd ();

//      System.out.println ("Start date = " + start.getSQLDate ());
//      System.out.println ("End date = " + end.getSQLDate ());
   }
}
