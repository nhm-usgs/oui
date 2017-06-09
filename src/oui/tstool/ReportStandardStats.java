package oui.tstool;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JOptionPane;
import oui.mms.datatypes.TimeSeries;
import oui.util.ReportPanel;

public class ReportStandardStats {
    
//    int missing_count[];
//    double mean, min, max, sd, skew;
//    
    
    public ReportStandardStats(TimeSeriesTool tst) {
        Object[] selected = tst.getTraceListList().getSelectedValues();
//
        
        if (selected.length < 1) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(tst), "At least one data set must be selected for a statistics report.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ReportPanel reportPanel = new ReportPanel();
        WindowFactory.displayInFrame(reportPanel, "Time Series Standard Statistics");
        Writer out = reportPanel.getWriter();
        
        try {
            out.write("    Name               Count  Missing   Start Time    End Time       Mean       Minimum      Maximum      Std. Dev.    Skewness\n");
            out.write("===================================================================================================================================\n");
            
            double sum, sum_dev, variance, dev;
            int missing_count;
            double mean;
            double min;
            double max;
            double sd;
            double skew;
            
            for (int i = 0; i < selected.length; i++) {
                TimeSeries tsd = (TimeSeries)(selected[i]);
                
                double[] values = tsd.getVals();
                
                sum = 0;
                missing_count = 0;
                min = 1000000;
                max = -999999999;
                for (int j = 0; j < values.length; j++) {
                    if (values[j] <= -99.9) {  //  missing value
                        missing_count++;
                    } else {
                        sum += values[j];
                        
                        if (min > values[j]) min = values[j];
                        if (max < values[j]) max = values[j];
                    }
                }
                
                mean = sum / (values.length - missing_count);
                sum_dev = 0.0;
                variance = 0.0;
                skew = 0.0;
                for (int j = 0; j < values.length; j++) {
                    dev = values[j] - mean;
                    sum_dev += Math.abs(dev);
                    variance += dev * dev;
                    skew += dev * dev * dev;
                }
                
                variance = variance / (values.length - missing_count - 1);
                sd = Math.sqrt(variance);

                skew = skew / ((values.length - missing_count) * variance * sd);
      
                String s = String.format(
                        "%1$20s%2$7d%3$7d    %4$12s %5$12s   %6$5.3e   %7$5.3e   %8$5.3e   %9$5.3e   %10$5.3e",
                        tsd.getName(), values.length, missing_count, tsd.getStart().toString(),
                        mean, min, max, sd, skew);
                out.write(s + "\n");
            }

            out.flush();
            out.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
