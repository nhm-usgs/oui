/*
 * Plotter.java
 *
 * Created on November 10, 2004, 8:42 AM
 */

package oui.util;

import org.jfree.chart.ChartPanel;
/**
 *
 * @author  markstro
 */
public class XYPlotter extends Plotter {
    ChartPanel chartPanel = null;
    
    public ChartPanel getPanel () {return chartPanel;}
    public void clearAll () {//dataset.removeAllSeries();
    }
    
    public XYPlotter(String title, String xAxisLabel, String yAxisLabel) {
    }
}
