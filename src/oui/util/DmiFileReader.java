package oui.util;

import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
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
      String line = "foo";

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
          start_time = new OuiCalendar();
          int year = Integer.valueOf(st.nextToken()).intValue();
          int mon = Integer.valueOf(st.nextToken()).intValue();
          int day = Integer.valueOf(st.nextToken()).intValue();
          int hour = Integer.valueOf(st.nextToken()).intValue();
          int min = Integer.valueOf(st.nextToken()).intValue();
          int sec = Integer.valueOf(st.nextToken()).intValue();
          start_time.set(year, mon - 1, day, hour, min, sec);


      } catch (Exception ex) {
         System.out.println ("Problem getting start date from file " +
                              file_name);
      }
      return (start_time);
   }

   public OuiCalendar getEnd () {
      StringTokenizer st;
      String line = "foo", last_line = null;

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

         end_time = new OuiCalendar();
          int year = Integer.valueOf(st.nextToken()).intValue();
          int mon = Integer.valueOf(st.nextToken()).intValue();
          int day = Integer.valueOf(st.nextToken()).intValue();
          int hour = Integer.valueOf(st.nextToken()).intValue();
          int min = Integer.valueOf(st.nextToken()).intValue();
          int sec = Integer.valueOf(st.nextToken()).intValue();
          end_time.set(year, mon - 1, day, hour, min, sec);

      } catch (Exception ex) {
         System.out.println ("Problem getting end date from file " +
                              file_name);
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
               data[count++] = Double.valueOf (st.nextToken ()).doubleValue ();
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
