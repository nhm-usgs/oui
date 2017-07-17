/*
 * SingleRun.java
 *
 * Created on July 16, 2004, 10:08 AM
 */
package oui.mms.io;

import java.util.ArrayList;
import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.TimeSeries;

/**
 *
 * @author  markstro
 */
public class MmsOuiEspReader  {
    static double MISSING = -99.9;
    
    public static void readEsp(EnsembleData ed) {
        ArrayList outputFiles = ed.getOutput();
        TimeSeries outputTS;
        
/*
 *  Read the statvar files and put the data and dates into the output
 */
        for (int i = 0; i < outputFiles.size(); i++) {
            outputTS = (TimeSeries)(outputFiles.get(i));
            MmsStatvarReader msvr = new MmsStatvarReader(outputTS.getSource());
            TimeSeries ts = msvr.getTimeSeries(outputTS.getDescription());
            
            if (ts == null || ts.getVals() == null) {
                double[] dates = msvr.getDates();
                outputTS.setDates(dates);
                
                double[] trace_data = new double[dates.length];
                for (int j = 0; j < trace_data.length; j++) {
                    trace_data[j] = MISSING;
                }
                outputTS.setVals(trace_data);
            } else {
                outputTS.setVals(ts.getVals());
                outputTS.setDates(ts.getDates());
            }
        }
/*
 *  Read the init data
 */
        ArrayList forecasts = ed.getForecasts();
        TimeSeries initTS = ed.getInitialization();
        int initLength = (int)(initTS.getEnd().getJulian()) - (int)(initTS.getStart().getJulian()) + 1;
        
        outputTS = (TimeSeries)(outputFiles.get(0));
        initTS.setSource (outputTS.getSource());

        double[] trace_dates = outputTS.getDates();
        double[] trace_data = outputTS.getVals();
        
        double[] init_dates = new double[initLength];
        double[] init_data = new double[initLength];
        
        for (int i = 0; i < initLength; i++) {
            init_dates[i] = trace_dates[i];
            init_data[i] = trace_data[i];
        }
        
        initTS.setDates(init_dates);
        initTS.setVals(init_data);
        
/*
 * Read the traces
 */
        int forecastLength = (int)(outputTS.getEnd().getJulian()) - (int)(outputTS.getStart().getJulian()) - initLength + 1;
        
        for (int i = 0; i < outputFiles.size(); i++) {
            
            outputTS = (TimeSeries)(outputFiles.get(i));
//            System.out.println ("MmmOuiEspReader: outputTS = " + outputTS.getName());

            trace_dates = outputTS.getDates();
            trace_data = outputTS.getVals();
            
            double[] forecast_dates = new double[forecastLength];
            double[] forecast_data = new double[forecastLength];
            
            for (int j = 0; j < forecastLength; j++) {
                forecast_dates[j] = trace_dates[j + initLength];
                forecast_data[j] = trace_data[j + initLength];
            }
            
            TimeSeries forecastTS = (TimeSeries)(forecasts.get(i));
            forecastTS.setDates(forecast_dates);
            forecastTS.setVals(forecast_data);
            forecastTS.setSource(outputTS.getSource());
        }
    }
}
