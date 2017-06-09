package oui.tstool;

import oui.mms.datatypes.OuiCalendar;

/**
  */
public class TimeSeriesData {

   String var_name;
   int index;
   String file_name;
   String list_name;
   double[] dates = null;
   double[] values = null;
   int data_line_count;

   public TimeSeriesData (String vn, String ln, double[] d, double[] v) {
      var_name = vn;
      index = -1;
      list_name = ln;

      setValues (v);
      setDates (d);

   }

   public double[] getValues () {
      return (values);
   }

   public void setValues (double[] v) {
      values = v;
      data_line_count = values.length;
   }

   public double[] getDates () {
      return (dates);
   }

   public void setDates (double[] d) {
      dates = d;
      data_line_count = dates.length;
   }

   public double getStartTime () {
      return (dates[0]);
   }

   public double getEndTime () {
      return (dates[data_line_count - 1]);
   }

   public String getVarName () {
      return (var_name);
   }

   public int getIndex () {
      return (index);
   }

   public String toString () {
      return (list_name);
   }
}


