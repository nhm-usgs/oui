/*
 * TimeSeriesPlotter.java
 *
 * Created on November 10, 2004, 8:42 AM
 */

package oui.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;

/**
 *
 * @author  markstro
 */
public class TimeSeriesPlotter extends Plotter {
    TimeSeriesCollection dataset = null;
    JFreeChart chart = null;
    ChartPanel chartPanel = null;
    private boolean isLog = false;
    
    public ChartPanel getPanel () {return chartPanel;}
    
    public TimeSeriesPlotter(String title, String xAxisLabel, String yAxisLabel, boolean isLog) {
        this.isLog = isLog;
        dataset = new TimeSeriesCollection();
        chart = createChart(dataset, title, xAxisLabel, yAxisLabel, isLog);
        chartPanel = new ChartPanel(chart);
    }
    
    public TimeSeriesPlotter(String title, String xAxisLabel, String yAxisLabel) {
        this.isLog = false;
        dataset = new TimeSeriesCollection();
        chart = createChart(dataset, title, xAxisLabel, yAxisLabel, false);
        chartPanel = new ChartPanel(chart);
    } 
    
//    private static JFreeChart createChart(XYDataset dataset, String title, String xAxisLabel, String yAxisLabel) {
//        return createChart (dataset, title, xAxisLabel, yAxisLabel, false);
//    }

    private static JFreeChart createChart(XYDataset dataset, String title, String xAxisLabel, String yAxisLabel, boolean isLog) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xAxisLabel, yAxisLabel,
            dataset, true, true, false);
        XYPlot plot = (XYPlot) chart.getPlot();
        if (isLog) {
            LogarithmicAxis yAxis = new LogarithmicAxis("Log Values");
            plot.setRangeAxis(yAxis);
        }
        plot.addRangeMarker(new ValueMarker(550.0));
        return chart;        
    }
    
    public void clearAll () {dataset.removeAllSeries();}
    
    public void clearTrace(String traceLabel) {
        org.jfree.data.time.TimeSeries series = dataset.getSeries(traceLabel);
        if (series != null) dataset.removeSeries(series);
    }
    
    public void addTrace(TimeSeries tsc) {
        org.jfree.data.time.TimeSeries series = new org.jfree.data.time.TimeSeries(tsc.getName(), org.jfree.data.time.Day.class);
        
        double[] vals = tsc.getVals();
        double[] dates = tsc.getDates();
        
        OuiCalendar mdt = new OuiCalendar ();
        for (int i = 0; i < vals.length; i++) {
            mdt.setJulian(dates[i]);
            if (isLog) {
                if (vals[i] <= 0.0) {
                    series.add(new Day(mdt.getDay(), mdt.getMonth(), mdt.getYear()), Double.NaN);
                } else {
                    series.add(new Day(mdt.getDay(), mdt.getMonth(), mdt.getYear()), vals[i]);
                }
            } else {
                series.add(new Day(mdt.getDay(), mdt.getMonth(), mdt.getYear()), vals[i]);
            }
        }

        dataset.addSeries(series);
    }
    
    public static void main(String[] args) {
        javax.swing.JFrame frame = new javax.swing.JFrame("Time Series Demo");
        
        TimeSeriesPlotter plotter = new TimeSeriesPlotter("Time Series Plot", "Date", "Value");        
        
        frame.getContentPane().add(plotter.getPanel());
        frame.setSize(600, 500);
        frame.setVisible(true);
        
        double[] x = {2453325, 2453326, 2453327};
        double[] y1 = {2.0, 4.0, 8.0};
        plotter.addTrace(new oui.mms.datatypes.TimeSeries ("data 1", x, y1, null, null, null, null, null));
        
        double[] y2 = {8.0, 4.0, 2.0};
        plotter.addTrace(new oui.mms.datatypes.TimeSeries ("data 2", x, y2, null, null, null, null, null));
    }
}
