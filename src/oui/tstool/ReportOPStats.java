package oui.tstool;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.awt.GridLayout;
import java.io.Writer;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.border.*;
import oui.mms.datatypes.OuiCalendar;
import oui.util.ReportPanel;

public class ReportOPStats {
    TimeSeriesTool tst;
    String out_file_name = "op_stats.out";
    int ibyr = 1980;        // begin water year for stats
    int ieyr = 1996;        // last water year for stats
    int mfs = 4;           // begin month for stats
    int mfn = 9;            // end month for stats
    float da = 123456;      // draingae area
    
    public ReportOPStats(TimeSeriesTool tst) {
        this.tst = tst;
        Object[] selected = tst.getTraceListList().getSelectedValues();
        
        if (selected.length != 2) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(tst), "Two data sets must be selected for observed/predicted statistics.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
/*
 ** Get Dates
 */
        Object[]      message = new Object[1];
        
        JPanel jp = new JPanel(new GridLayout(5,2));
        JTextField start_year = new JTextField(5);
        JTextField end_year = new JTextField(5);
        JTextField start_month = new JTextField(5);
        JTextField end_month = new JTextField(5);
        JTextField area = new JTextField(5);
        jp.add(new JLabel("Start Year:"));
        jp.add(start_year);
        jp.add(new JLabel("End Year:"));
        jp.add(end_year);
        jp.add(new JLabel("Start Month:"));
        jp.add(start_month);
        jp.add(new JLabel("End Month:"));
        jp.add(end_month);
        jp.add(new JLabel("Dranage Area:"));
        jp.add(area);
        
        jp.setBorder(new CompoundBorder(new TitledBorder(null, "Stat Report Info", TitledBorder.LEFT, TitledBorder.TOP, tst.getFont()), new EmptyBorder(2, 10, 1, 0)));
        message[0] = jp;
        
        String[]      options = { "OK", "Cancel"};
        
        int result = JOptionPane.showOptionDialog(GuiUtilities.windowFor(tst), message, "Stat Report Info", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        
        if(result == 0) {
            ibyr = Integer.valueOf(start_year.getText()).intValue();
            ieyr = Integer.valueOf(end_year.getText()).intValue();
            mfs = Integer.valueOf(start_month.getText()).intValue();
            mfn = Integer.valueOf(end_month.getText()).intValue();
            da = Integer.valueOf(area.getText()).intValue();
            
        } else {
            return;
        }
        
/*
 ** Output file name
 */
        //        JFileChooser chooser = new JFileChooser();
        //        chooser.setDialogTitle("Output Statistic File");
        //    chooser.setCurrentDirectory (new File (parent.output_dir));
        //
        //        int retval = chooser.showOpenDialog(tst);
        //        if(retval == JFileChooser.APPROVE_OPTION) {
        //            out_file_name = new String(chooser.getSelectedFile().getAbsolutePath());
        //        }
        compute();
    }
    
    private void compute() {
        double sum, sum_dev, variance, dev;
//        Format df8_1 = new Format("%8.1f");
//        Format df9_1 = new Format("%9.1f");
//        Format df9_2 = new Format("%9.2f");
//        Format df7_2 = new Format("%7.2f");
        
        String units = "e";     // english or metric units (e or m)
        
        Object[] selected = tst.getTraceListList().getSelectedValues();
        
        //        for (int i = 0; i < 2; i++) {
        try {
//            PrintWriter out = new PrintWriter(new FileWriter(out_file_name));
            ReportPanel reportPanel = new ReportPanel();
            WindowFactory.displayInFrame(reportPanel, "Time Series Standard Statistics");
            Writer out = reportPanel.getWriter();
        
            out.write("statistics for months " + mfs + " through " + mfn + "\n");
            
            if (units.equals("m")) {
                out.write("units are cubic meters/second\n");
            } else {
                out.write("units are cubic feet/second\n");
            }
            
            
            OuiCalendar end_report_date = new OuiCalendar();
            end_report_date.set(ieyr,(mfn + 1),1);
            end_report_date.setJulian(end_report_date.getJulian() - 1.0);
            
            int start_year;
            if ((mfs >= 10) && (mfs <= 12)) {
                start_year = ibyr - 1;
            } else {
                start_year = ibyr;
            }
            
            OuiCalendar period_start = new OuiCalendar();
            period_start.set(start_year, mfs, 1);
            
            OuiCalendar period_end;
            if (mfs > mfn) {
                period_end = new OuiCalendar();
                period_end.set((start_year + 1), (mfn + 1), 1);
            } else {
                period_end = new OuiCalendar();
                period_end.set(start_year, (mfn + 1), 1);
            }
            period_end.setJulian(period_end.getJulian() - 1.0);
            
            ArrayList<String> wy_labels = new ArrayList<String>(10);
            ArrayList<Double> obs_mean = new ArrayList<Double>(10);
            ArrayList<Double> pred_mean = new ArrayList<Double>(10);
            ArrayList<Double> obs_sd = new ArrayList<Double>(10);
            ArrayList<Double> pred_sd = new ArrayList<Double>(10);
            ArrayList<Double> obs_total = new ArrayList<Double>(10);
            ArrayList<Double> pred_total = new ArrayList<Double>(10);
            ArrayList<Double> obs_max = new ArrayList<Double>(10);
            ArrayList<Double> pred_max = new ArrayList<Double>(10);
            ArrayList<String> obs_date = new ArrayList<String>(10);
            ArrayList<String> pred_date = new ArrayList<String>(10);
            ArrayList<Double> standard_err = new ArrayList<Double>(10);
            ArrayList<Double> cv = new ArrayList<Double>(10);
            ArrayList<Double> ntd = new ArrayList<Double>(10);
            ArrayList<Double> r = new ArrayList<Double>(10);
            double ototal = 0.0;
            double ptotal = 0.0;
            double var_ototal = 0.0;
            double var_ptotal = 0.0;
            double ss_errtotal = 0.0;
            double total_obs_total = 0.0;
            double total_pred_total = 0.0;
            int tot_day_count = 0;
            
            while (period_end.getJulian() <= end_report_date.getJulian()) {
                int day_count = (int)(period_end.getJulian() - period_start.getJulian()) + 1;
                tot_day_count += day_count;
                
                if ((mfs >= 10) && (mfs <= 12)) {
                    wy_labels.add("" + (start_year + 1));
                } else {
                    wy_labels.add("" + start_year);
                }
                
                double julian_start = period_start.getJulian();
                
//                int start_index;
                
//                int ju, jm, jl;
//                
//                jl = -1;
//                ju = getTraceValueCount(i);
//                while (ju - jl > 1) {
//                    jm = (ju + jl) >> 1;
//                    if (julian_start > (getTraceDates(i))[jm]) {
//                        jl = jm;
//                    } else {
//                        ju = jm;
//                    }
//                }
//                start_index = jl + 1;
                
                double sum_obs = 0.0;
                double sum_pred = 0.0;
                double max_obs = 0.0;
                double max_pred = 0.0;
                String pdate = null;
                String odate = null;
                for (int j = 0; j < day_count; j++) {
//                    sum_obs += (getTraceValues(0))[start_index + j];
//                    sum_pred += (getTraceValues(1))[start_index + j];
                    
//                    if ((getTraceValues(0))[start_index + j] > max_obs) {
//                        max_obs = (getTraceValues(0))[start_index + j];
//                        odate = (new OuiCalendar(julian_start + j)).getMonthDay();
//                    }
//                    
//                    if ((getTraceValues(1))[start_index + j] > max_pred) {
//                        max_pred = (getTraceValues(1))[start_index + j];
//                        pdate = (new OuiCalendar(julian_start + j)).getMonthDay();
//                    }
                }
                
                obs_max.add(new Double(max_obs));
                pred_max.add(new Double(max_pred));
                obs_date.add(odate);
                pred_date.add(pdate);
                
                ototal += sum_obs;
                ptotal += sum_pred;
                
                double om = sum_obs / (double)day_count;
                double pm = sum_pred / (double)day_count;
                
                obs_mean.add(new Double(om));
                pred_mean.add(new Double(pm));
                
                obs_total.add(new Double(sum_obs * 23.8017 / da));
                pred_total.add(new Double(sum_pred * 23.8017 / da));
                
                double var_obs = 0.0;
                double var_pred = 0.0;
                double ss_err = 0.0;
                double yc1 = 0.0;
                for (int j = 0; j < day_count; j++) {
//                    double dev_obs = (getTraceValues(0))[start_index + j] - om;
//                    double dev_pred = (getTraceValues(1))[start_index + j] - pm;
//                    double err = (getTraceValues(0))[start_index + j] - (getTraceValues(1))[start_index + j];
//                    var_obs += dev_obs * dev_obs;
//                    var_pred += dev_pred * dev_pred;
//                    ss_err += err * err;
//                    yc1 += dev_obs * dev_pred;
                }
                
                var_ototal += var_obs;
                var_ptotal += var_pred;
                ss_errtotal += ss_err;
                
                double var_obs1 = var_obs / (day_count - 1);
                double var_pred1 = var_pred / (day_count - 1);
                double ss_err1 = ss_err / (day_count - 1);
                obs_sd.add(new Double(Math.sqrt(var_obs1)));
                pred_sd.add(new Double(Math.sqrt(var_pred1)));
                ss_err1 = Math.sqrt(ss_err1);
                standard_err.add(new Double(ss_err1));
                cv.add(new Double(ss_err1 / om));
                double ntd_1 = 1.0 - (ss_err / var_obs);
                ntd.add(new Double(ntd_1));
                double r1 = yc1 / Math.sqrt(var_pred * var_obs);
                r.add(new Double(r1));
                
                start_year++;
                period_start = new OuiCalendar();
                period_start.set(start_year, mfs, 1);
                if (mfs > mfn) {
                    period_end = new OuiCalendar();
                    period_end.set((start_year + 1), (mfn + 1), 1);
                } else {
                    period_end = new OuiCalendar();
                    period_end.set(start_year, (mfn + 1), 1);
                }
                period_end.setJulian(period_end.getJulian() - 1.0);
            }
            
/*
 ** Write header for first report
 */
            
//            out.println("                daily streamflow");
//            out.println("         -------------------------------");
//            out.println("             measured        predicted        total streamflow (inches)");
//            out.println("         --------------- ---------------  -------------------------------");
//            out.println(" water        standard         standard");
//            out.println(" year   mean  deviation  mean  deviation  measured predicted error pct err");
//            out.println();
//            
//            for (int j = 0; j < wy_labels.size(); j++) {
//                double ot = (((Double)(obs_total.elementAt(j))).doubleValue());
//                double pt = (((Double)(pred_total.elementAt(j))).doubleValue());
//                double err = (pt - ot);
//                double pe = (err / ot) * 100.0;
//                out.println(" " + wy_labels.elementAt(j)
//                + df8_1.form(((Double)(obs_mean.elementAt(j))).doubleValue())
//                + df9_1.form(((Double)(obs_sd.elementAt(j))).doubleValue())
//                + df8_1.form(((Double)(pred_mean.elementAt(j))).doubleValue())
//                + df9_1.form(((Double)(pred_sd.elementAt(j))).doubleValue())
//                + df9_2.form(ot)
//                + df9_2.form(pt)
//                + df9_2.form(err)
//                + df9_2.form(pe));
//            }
            
            var_ototal = var_ototal / (tot_day_count - 1);
            var_ptotal = var_ptotal / (tot_day_count - 1);
            
            double ot = ototal * 23.8017 / da;
            double pt = ptotal * 23.8017 / da;
            double err = (pt - ot);
            double pe = (err / ot) * 100.0;
//            out.println("\n tot "
//            + df8_1.form(ototal / tot_day_count)
//            + df9_1.form(Math.sqrt(var_ototal))
//            + df8_1.form(ptotal / tot_day_count)
//            + df9_1.form(Math.sqrt(var_ptotal))
//            + df9_2.form(ot)
//            + df9_2.form(pt)
//            + df9_2.form(err)
//            + df9_2.form(pe));
//            
//            out.println("\n\n\n");
//            out.println("                 maximum daily");
//            out.println("                  streamflow");
//            out.println("        -------------------------------");
//            out.println("           measured         predicted");
//            out.println("        -------------     -------------");
//            out.println(" water                                     standard");
//            out.println(" year   flow     date     flow     date      error    cv     ntd     r");
//            out.println();
            
            for (int j = 0; j < wy_labels.size(); j++) {
                double om2 = obs_max.get(j).doubleValue();
                double pm2 = pred_max.get(j).doubleValue();
                String od = obs_date.get(j);
                String pd = pred_date.get(j);
                double se = standard_err.get(j).doubleValue();
                double c_v = cv.get(j).doubleValue();
                double n_d_t = ntd.get(j).doubleValue();
                double r1 = r.get(j).doubleValue();
//                out.println(" " + wy_labels.elementAt(j)
//                + df8_1.form(om2)
//                + "   " + od + " "
//                + df8_1.form(pm2)
//                + "   " + pd + "  "
//                + df8_1.form(se)
//                + df7_2.form(c_v)
//                + df7_2.form(n_d_t)
//                + df7_2.form(r1));
            }
            out.close();
        } catch (IOException e) {
            System.out.println("IOException: filename = " + out_file_name);
        }
        //        }
    }
}