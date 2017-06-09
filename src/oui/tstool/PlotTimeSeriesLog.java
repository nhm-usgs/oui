/*
 * PlotTimeSeries.java
 *
 * Created on November 12, 2004, 11:52 AM
 */

package oui.tstool;

import gov.usgs.cawsc.gui.GuiUtilities;
import javax.swing.JOptionPane;
import oui.mms.datatypes.TimeSeries;
import oui.util.TimeSeriesPlotter;

/**
 *
 * @author  markstro
 */
public class PlotTimeSeriesLog {
    
    /** Creates a new instance of PlotTimeSeries */
    public PlotTimeSeriesLog(TimeSeriesTool tst) {
        tst.setPlotType(TimeSeriesTool.TIMESERIES_LOG_PLOT);
        
        Object[] selected = tst.getTraceListList().getSelectedValues();
        
        if (selected.length < 1) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(tst), "At least one data set must be selected for a time sereis plot.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        for (int i = 0; i < selected.length; i++) {
            TimeSeries tsd = (TimeSeries)(selected[i]);
            ((TimeSeriesPlotter)(tst.getPlotter())).addTrace((TimeSeries)(selected[i]));
        }
    }
    
}
