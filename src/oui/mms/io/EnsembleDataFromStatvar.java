package oui.mms.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import oui.esptool.EspTool;

public class EnsembleDataFromStatvar {
    
    /**
     * Holds value of property traceCount.
     */
    private int traceCount;
    
//    double[] plot_date;
//    double[][] plot_data;
//    double[] trace_volumes;
//    double[] trace_peaks;
//    int[] time_to_peak;
//    int[] volume_order;
//    int[] peak_order;
//    int[] volume_rank;
//    int[] peak_rank;
//    int trace_count;
//    int date_count;
//    int key_order[];
//    int key_count;
//    int key_id;
//    static double missing_data_value = 99999.0;
//    String[] year_list;
//    String[] prob_labels;
//    String[] simple_prob_labels;
//    String[] tr_labels;
//    EspTool esp_t;
//    ModelDateTime init_start_date;
//    ModelDateTime init_end_date;
//    ModelDateTime fore_start_date;
//    public ModelDateTime fore_end_date;
//    int analysis_date_count;
//    ModelDateTime analysis_start_date, analysis_end_date;
    
/*
 ** This constructor reads data from an esp series file and an esp output
 ** file.
 */
    public EnsembleDataFromStatvar(String esp_out_name) {
        String line;
        traceCount = 0;
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(esp_out_name));
            while ((line = in.readLine()) != null) {
                traceCount++;
            }
            in.close();
            
        } catch (Exception ex) {
            System.out.println("Problem reading " + esp_out_name);
        }
        
//        year_list = new String[traceCount];
//        prob_labels = new String[traceCount];
//        simple_prob_labels = new String[traceCount];
//        tr_labels = new String[traceCount];
//        trace_volumes = new double[traceCount];
//        trace_peaks = new double[traceCount];
//        time_to_peak = new int[traceCount];
//        volume_order = new int[traceCount];
//        peak_order = new int[traceCount];
//        volume_rank = new int[traceCount];
//        peak_rank = new int[traceCount];
//        
//        Format f1 = new Format("%4.1f");
//        Format f2 = new Format("%2.0f");
        
/*
 **  Get the list of years from the esp output file.
 */
//        try {
//            StringTokenizer st;
//            
//            BufferedReader in = new BufferedReader(new FileReader(esp_out_name));
//            
//            for (int i = 0; i < traceCount; i++ ) {
//                line = in.readLine();
//                year_list[i] = line.substring(0, 4);                
//                float tr = (float)((traceCount + 1)) / (float) (i + 1);
//                float p = (float)1.0 / tr * (float)100.0;
//                float p_rnd = fivePercentRound(p);
//                prob_labels[i] = " (act P = " + f1.form(p) + "%;" + " dmi P = " + f2.form(p_rnd) + "%)";
//                simple_prob_labels[i] = f1.form(p);
//                tr_labels[i] = f1.form(tr);
//            }
//            in.close();
//        } catch (Exception ex) {
//            System.out.println("Problem reading " + esp_out_name);
//        }
        
/*
 **  Set up date array
 */
//        date_count = 0;
//        
//        try {
//            StringTokenizer st;
//            BufferedReader in = new BufferedReader(new FileReader(series_name));
//            
///*
// **  Skip the variable count line in the esp series file.
// */
//            in.readLine();
//            
///*
// ** Get start time.
// */
//            line = in.readLine();
//            st = new StringTokenizer(line, " ");
//            start_yr = Integer.valueOf(st.nextToken()).intValue();
//            start_mon = Integer.valueOf(st.nextToken()).intValue();
//            start_day = Integer.valueOf(st.nextToken()).intValue();
//            start_hour = Integer.valueOf(st.nextToken()).intValue();
//            start_min = Integer.valueOf(st.nextToken()).intValue();
//            start_sec = Integer.valueOf(st.nextToken()).intValue();
//            
//            ModelDateTime start_date = new ModelDateTime(start_yr, start_mon, start_day, 0, 0, 0);
//            
///*
// ** get end time.
// */
//            line = in.readLine();
//            st = new StringTokenizer(line, " ");
//            end_yr = Integer.valueOf(st.nextToken()).intValue();
//            end_mon = Integer.valueOf(st.nextToken()).intValue();
//            end_day = Integer.valueOf(st.nextToken()).intValue();
//            end_hour = Integer.valueOf(st.nextToken()).intValue();
//            end_min = Integer.valueOf(st.nextToken()).intValue();
//            end_sec = Integer.valueOf(st.nextToken()).intValue();
//            
//            ModelDateTime end_date = new ModelDateTime(end_yr, end_mon, end_day,
//            0, 0, 0);
//            
//            date_count = (int)(end_date.getJulian() - start_date.getJulian())+1;
//            
///*
// **  This sets the plot_date array so that the time base
// **  of the x axis can be set to the start_date.  This is
// **  a hack to get away from the julian dates.  I couldn't
// **  get the time base to work for the very large julian
// **  day values.
// */
//            plot_date = new double[date_count];
//            for (int i = 0; i < date_count; i++) {
//                plot_date[i] = (double)i + id.last_plot_date + 1.0;
//            }
//            
//            in.close();
//            
//        } catch (Exception ex) {
//            System.out.println("Problem getting dates from file " + series_name);
//        }
//        
/*
 **  Figure out the start and end of the forecast.
 */
//        fore_start_date = new ModelDateTime(init_end_date.getJulian() + 1.0);
//        fore_end_date = new ModelDateTime(fore_start_date.getJulian() +
//        (double)(date_count - 1));
///*
// **  Put in the data
// */
//        plot_data = new double[traceCount][date_count];
//        
//        try {
//            StringTokenizer st;
//            
//            BufferedReader in = new BufferedReader(new FileReader(series_name));
//            
///*
// ** Skip the var count, start date and end date
// */
//            in.readLine();
//            in.readLine();
//            in.readLine();
//            
//            for (int i = 0; i < traceCount; i++) {
//                for (int j = 0; j < date_count; j++) {
//                    line = in.readLine();
//                    //System.out.println ("line = " + line);
//                    st = new StringTokenizer(line, " ");
//                    st.nextToken();
//                    st.nextToken();
//                    st.nextToken();
//                    st.nextToken();
//                    st.nextToken();
//                    st.nextToken();
//                    plot_data[i][j] = Double.valueOf(st.nextToken()).doubleValue();
//                    //System.out.println ("   plot_data[" + i + "][" + j + "] = " + plot_data[i][j]);
//                }
//                
///*
//            in.close ();
// */
//                
//                PROCESS_trace(null, null, plot_data, trace_volumes, trace_peaks,
//                time_to_peak, i);
//            }
//            
//            in.close();
//            
//            SORT_trace(trace_volumes, volume_order, volume_rank);
//            SORT_trace(trace_peaks, peak_order, peak_rank);
//            
///*
//         for (int j = 0; j < traceCount; j++) {
//            System.out.println ("volume = " + trace_volumes[j]);
//            System.out.println ("volume_order= " + volume_order[j]);
//            System.out.println ("");
//            System.out.println ("trace_peaks = " + trace_peaks[j]);
//            System.out.println ("time_to_peak = " + time_to_peak[j]);
//            System.out.println ("peak_order= " + peak_order[j]);
//            System.out.println ("");
//            System.out.println ("");
//         }
// */
//            
//        } catch (Exception ex) {
//            System.out.println("Problem getting value from file " + series_name);
//        }
//        
///*
// ** Legend stuff
// */
//        esp_t.plotter.jcChart.getLegend().setVisible(true);
        
/*
 ** Y Axis
 */
//        JCAxis yaxis = esp_t.plotter.jcChart.getChartArea().getYAxis(0);
//        yaxis.setGridVisible(true);
//        yaxis.setTitle(new JCAxisTitle("CFS"));
//        yaxis.getTitle().setPlacement(JCLegend.WEST);
//        yaxis.getTitle().setRotation(ChartText.DEG_270);
//        
///*
// ** X Axis
// */
//        JCAxis xaxis = esp_t.plotter.jcChart.getChartArea().getXAxis(0);
//        xaxis.setGridVisible(true);
//        xaxis.setTitle(new JCAxisTitle("Date"));
//        xaxis.setTimeUnit(JCAxis.DAYS);
//        
///*
// **  TimeBase is set to start_date because the x axis data values
// **  are reset to start at 1.0.  See comment above.
// */
///*
//      try {
//         BufferedReader in = new BufferedReader
//                    (new FileReader (output_dir + "/last_esp_run_dates"));
//         in.readLine ();
//         in.readLine ();
//         in.readLine ();
//         in.readLine ();
//         line = in.readLine ();
//         in.close ();
// 
//         line = line.replace (',', ' ');
//         ModelDateTime mdt = new ModelDateTime (line);
// 
////         xaxis.setTimeBase (mdt.getTime ());
// 
//      } catch (Exception ex) {
//         System.out.println ("Problem with "+output_dir+"/last_esp_run_dates");
//      }
// */
//        
//        xaxis.setTimeUnit(JCAxis.DAYS);
//        xaxis.setTimeFormat("yyyy-MM-dd");
//        xaxis.setAnnotationMethod(JCAxis.TIME_LABELS);
//        xaxis.setGridVisible(true);
//        
///*
// ** Misc.
// */
//        esp_t.plotter.arr.setChartType(JCChart.PLOT);
//        esp_t.plotter.arr.setHoleValue(-99999.0);
//        //      EspTool.plotter.arr.setDataSource (this);
//    }
    
/*
 **  This constructor reads data from the routed stat var files.
   
//    private void PROCESS_trace(Date start, Date end,
//    double[][] plot_data, double[] trace_volumes,
//    double[] trace_peaks, int[] time_to_peak, int j) {
//        
//        int start_index;
//        
//        if (start == null && end == null) {
//            analysis_start_date = fore_start_date;
//            analysis_end_date = fore_end_date;
//            analysis_date_count = date_count;
//            
//        } else {
//            analysis_start_date = new ModelDateTime();
//            analysis_start_date.setTime(start);
//            analysis_end_date = new ModelDateTime();
//            analysis_end_date.setTime(end);
//            analysis_date_count = (int)(analysis_end_date.getJulian() -
//            analysis_start_date.getJulian()) + 1;
//        }
//        
//        trace_volumes[j] = 0.0;
//        trace_peaks[j] = 0.0;
//        
//        if (analysis_start_date.before(fore_start_date)) {
//            //System.out.println ("Analysis before forecast");
//            
//            // If the analysis starts before the forecast, look at the data
//            // from the initialization period.
//            
//            int init_count_in_analysis = (int)(id.init_end_date.getJulian() -
//            analysis_start_date.getJulian()) + 1;
//            start_index = id.date_count - init_count_in_analysis;
//            //System.out.println ("start_index = " + start_index + " id.date_count = " + id.date_count + " init_count_in_analysis = " + init_count_in_analysis);
//            
//            // start_index = 553 id.date_count = 554 init_count_in_analysis = 1
//            
//            for (int k = start_index; k < id.date_count; k++) {
//                //System.out.println ("k = " + k + " id.plot_data[0][k] = " + id.plot_data[0][k]);
//                trace_volumes[j] = id.plot_data[0][k] + trace_volumes[j];
//                if (trace_peaks[j] < id.plot_data[0][k]) {
//                    trace_peaks[j] = id.plot_data[0][k];
//                    time_to_peak[j] = k + 1 - init_count_in_analysis;
//                }
//            }
//            
//            //System.out.println ("year_list[j] = " + year_list[j]);
//            //System.out.println ("    trace_volumes[j] = " + trace_volumes[j]);
//            analysis_date_count = analysis_date_count - init_count_in_analysis;
//            start_index = 0;
//            
//        } else if (analysis_start_date.after(fore_start_date)) {
//            //System.out.println ("Analysis after forecast");
//            start_index = (int)(analysis_start_date.getJulian() -
//            fore_start_date.getJulian());
//            
//        } else {
//            //System.out.println ("Analysis same as forecast");
//            start_index = 0;
//        }
//        
//        // Now look at the part of the analysis in the forecast period.
//        
//        for (int k = start_index; k < analysis_date_count; k++) {
//            trace_volumes[j] = plot_data[j][k] + trace_volumes[j];
//            if (trace_peaks[j] < plot_data[j][k]) {
//                trace_peaks[j] = plot_data[j][k];
//                time_to_peak[j] = k + 1;
//            }
//        }
//        //System.out.println ("year_list[j] = " + year_list[j]);
//        //System.out.println ("    trace_volumes[j] = " + trace_volumes[j]);
//    }
//    
//    public static void SORT_trace(double[] data, int[] order, int[] rank) {
//        double sorted_data[] = (double[])(data.clone());
//        double hold;
//        int pass, i, order_hold;
//        
//        for (i = 0; i < sorted_data.length; i++) {
//            order[i] = i + 1;
//        }
//        
//        for (pass = 1; pass < sorted_data.length; pass++) {
//            for (i = 0; i < sorted_data.length - 1; i++) {
//                if (sorted_data[i] < sorted_data[i+1]) {
//                    hold = sorted_data[i];
//                    sorted_data[i] = sorted_data[i+1];
//                    sorted_data[i + 1] = hold;
//                    
//                    order_hold = order[i];
//                    order[i] = order[i+1];
//                    order[i + 1] = order_hold;
//                }
//            }
//        }
//        
//        for (i = 0; i < order.length; i++) {
//            int j = order[i] - 1;
//            rank[j] = i + 1;
//        }
//    }
//    
//    private float fivePercentRound(float p) {
//        
//        if (p > 92.5) {
//            return ((float)95.0);
//        } else if (p > 87.5) {
//            return ((float)90.0);
//        } else if (p > 82.5) {
//            return ((float)85.0);
//        } else if (p > 77.5) {
//            return ((float)80.0);
//        } else if (p > 72.5) {
//            return ((float)75.0);
//        } else if (p > 67.5) {
//            return ((float)70.0);
//        } else if (p > 62.5) {
//            return ((float)65.0);
//        } else if (p > 57.5) {
//            return ((float)60.0);
//        } else if (p > 52.5) {
//            return ((float)55.0);
//        } else if (p > 47.5) {
//            return ((float)50.0);
//        } else if (p > 42.5) {
//            return ((float)45.0);
//        } else if (p > 37.5) {
//            return ((float)40.0);
//        } else if (p > 32.5) {
//            return ((float)35.0);
//        } else if (p > 27.5) {
//            return ((float)30.0);
//        } else if (p > 22.5) {
//            return ((float)25.0);
//        } else if (p > 17.5) {
//            return ((float)20.0);
//        } else if (p > 12.5) {
//            return ((float)15.0);
//        } else if (p > 7.5) {
//            return ((float)10.0);
//        } else {
//            return ((float)5.0);
//        }
//    }
//    
//    public void newDates(Date start_date, Date end_date) {
//        for (int j = 0; j < traceCount; j++) {
//            PROCESS_trace(start_date, end_date, plot_data, trace_volumes,
//            trace_peaks, time_to_peak, j);
//        }
//        
//        SORT_trace(trace_volumes, volume_order, volume_rank);
//        SORT_trace(trace_peaks, peak_order, peak_rank);
//    }
    
//    /**
//     * Getter for property traceCount.
//     * @return Value of property traceCount.
//     */
//    public int getTraceCount() {
//        return this.traceCount;
//    }
//    
//    /**
//     * Setter for property traceCount.
//     * @param traceCount New value of property traceCount.
//     */
//    public void setTraceCount(int traceCount) {
//        this.traceCount = traceCount;
//    }
//    
    }
}
