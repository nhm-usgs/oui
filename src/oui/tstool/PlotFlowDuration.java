package oui.tstool;

import gov.usgs.cawsc.gui.GuiUtilities;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import oui.mms.datatypes.TimeSeries;
import oui.util.Plotter;

public class PlotFlowDuration extends Plotter {
    JFreeChart chart = null;
    ChartPanel chartPanel = null;
    private float[][] data = new float[2][];
    
    public ChartPanel getPanel () {return chartPanel;} 
    public void clearAll () {}
    
    public PlotFlowDuration(TimeSeriesTool tst) {
        tst.setPlotType(TimeSeriesTool.XY_LINE_PLOT);
        
        Object[] selected = tst.getTraceListList().getSelectedValues();
        
        if (selected.length < 1) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(tst), "At least one data set must be selected for a flow duration plot.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
/*
 ** set up the x vector
 */
        double[] x_values = new double[100];
        
        for (int i = 0; i < 100; i++) {
            x_values[i] = (double)i + 1.0;
        }
        
/*
 **  Put in the data
 */
        double[][]plot_data = new double[selected.length][100];
        double[] raw_values;
        
        XYSeriesCollection dataset = new XYSeriesCollection();  
        
        for (int j = 0; j < selected.length; j++) {
            TimeSeries tsd = (TimeSeries)(selected[j]);
            XYSeries series = new XYSeries(tsd.getName());
            
            raw_values = tsd.getVals();
            
            int valid_count = 0;
            for (int k = 0; k < raw_values.length; k++ ) {
                if (raw_values[k] >= 0.0) { // Check for missing/bad values
                    valid_count++;
                }
            }
            
            double[] copy = new double[valid_count];
            
            int l = 0;
            for (int k = 0; k < raw_values.length; k++ ) {
                if (raw_values[k] >= 0.0) { // Check for missing/bad values
                    copy[l++] = raw_values[k];
                }
            }
            
            try {
                sort(copy);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(GuiUtilities.windowFor(tst), "Series " + tsd.getName() + " has bad data.", "Warning", JOptionPane.WARNING_MESSAGE);
                break;
            }
            
            double interval = (double)(copy.length) / 100.0;
            double[] x = new double[99];
            double[] y = new double[99];
            for (int i = 0; i < 99; i++) {
                int copy_index = (int)((i + 1) * interval);
                if (copy_index < copy.length) {
                    series.add(99 - i, copy[copy_index]);
                }
            }
            dataset.addSeries(series);
        }

        chart = ChartFactory.createXYLineChart(
            "Flow Duration",      // chart title
            "% of the time exceeded",                      // x axis label
            "Flow",                      // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, 
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // get a reference to the plot for further customisation...
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer 
            = (XYLineAndShapeRenderer) plot.getRenderer();
// TODO       renderer.setShapesVisible(true);
////        renderer.setDefaultShapesFilled(true);
        renderer.setDrawOutlines(true);
        
        // change the auto tick unit selection to integer units only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        chartPanel = new ChartPanel(chart);
        tst.setPlotter(this);
    }
    
    private void sort(double[] ra) {
        int l = ra.length / 2 + 1;
        int ir = ra.length;
        double rra;
        
        while (true) {
            if (l > 1) {
                l = l - 1;
                rra = ra[l - 1];
            } else {
                rra = ra[ir - 1];
                ra[ir - 1] = ra[0];
                ir = ir - 1;
                if (ir == 1) {
                    ra[0] = rra;
                    return;
                }
            }
            
            int i = l;
            int j = l + l;
            
            while (j <= ir) {
                if (j < ir) {
                    if (ra[j - 1] < ra[j]) j = j + 1;
                }
                
                if (rra < ra[j - 1]) {
                    ra[i - 1] = ra[j - 1];
                    i = j;
                    j = j + j;
                } else {
                    j = ir + 1;
                }
            }
            
            ra[i - 1] = rra;
        }
    }
}
