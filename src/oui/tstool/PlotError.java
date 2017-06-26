package oui.tstool;

import gov.usgs.cawsc.gui.GuiUtilities;
import javax.swing.JOptionPane;
import oui.mms.datatypes.TimeSeries;
import oui.util.TimeSeriesPlotter;

public class PlotError {

    public PlotError(TimeSeriesTool tst) {
        tst.setPlotType(TimeSeriesTool.TIMESERIES_PLOT);
        
        Object[] selected = tst.getTraceListList().getSelectedValues();
        
        if (selected.length != 2) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(tst), "Two data sets must be selected for an error plot.", "Warning", JOptionPane.WARNING_MESSAGE);
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
                }
            }
        }
        
        double[] error = new double[count];
        double[] dates = new double[count];
        
        count = 0;
        for (int i0 = 0; i0 < dates0.length; i0++) {
            for (int i1 = 0; i1 < dates1.length; i1++) {
                if ((int)dates0[i0] == (int)dates1[i1]) {
                    error[count] = vals0[i0] - vals1[i1];
                    dates[count] = dates0[i0];
                    count++;
                }
            }
        }
//        tst.getPlotter().addTrace(dates, error, "Error", Plotter.SOLID);
        ((TimeSeriesPlotter)(tst.getPlotter())).addTrace(new oui.mms.datatypes.TimeSeries ("Error", dates, error));
    }
}
