package oui.tstool;

import gov.usgs.cawsc.gui.GuiUtilities;
import java.awt.RenderingHints;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import oui.mms.datatypes.TimeSeries;
import oui.util.Plotter;

public class PlotXY extends Plotter {
    JFreeChart chart = null;
    ChartPanel chartPanel = null;
    private float[][] data = new float[2][];
    
    public ChartPanel getPanel () {return chartPanel;} 
    public void clearAll () {}
    
    public PlotXY(TimeSeriesTool tst) {
        tst.setPlotType(TimeSeriesTool.XY_POINT_PLOT);
        
        Object[] selected = tst.getTraceListList().getSelectedValues();
        
        if (selected.length != 2) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(tst), "Two data sets must be selected for a XY plot.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        TimeSeries tsd0 = (TimeSeries)(selected[0]);
        double[] dates0 = tsd0.getDates();
        double[] vals0 = tsd0.getVals();
        
        TimeSeries tsd1 = (TimeSeries)(selected[1]);
        double[] dates1 = tsd1.getDates();
        double[] vals1 = tsd1.getVals();
        
        int count = 0;
        for (int i0 = 0; i0 < dates0.length; i0++) {
            for (int i1 = 0; i1 < dates1.length; i1++) {
                if ((int)dates0[i0] == (int)dates1[i1]) {
                    count++;
                    continue;
                }
            }
        }
        
        data[0] = new float[count];
        data[1] = new float[count];
        
        count = 0;
        for (int i0 = 0; i0 < dates0.length; i0++) {
            for (int i1 = 0; i1 < dates1.length; i1++) {
                if ((int)dates0[i0] == (int)dates1[i1]) {
                    data[0][count] = (float)vals0[i0];
                    data[1][count] = (float)vals1[i1];
                    count++;
                    continue;
                }
            }
        }
        
        NumberAxis domainAxis = new NumberAxis(tsd0.getName());
        domainAxis.setAutoRangeIncludesZero(true);
        NumberAxis rangeAxis = new NumberAxis(tsd1.getName());
        rangeAxis.setAutoRangeIncludesZero(true);
        FastScatterPlot plot = new FastScatterPlot(data, domainAxis, rangeAxis);
        chart = new JFreeChart(null, plot);
// TODO        chart.setOldLegend(null);

        // force aliasing of the rendered content..
        chart.getRenderingHints().put
            (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        chartPanel = new ChartPanel(chart, true);
        tst.setPlotter(this);
    }
}
