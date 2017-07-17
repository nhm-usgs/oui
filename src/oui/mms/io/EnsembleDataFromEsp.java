package oui.mms.io;

import java.util.ArrayList;
import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;

public class EnsembleDataFromEsp extends EnsembleData {
    
    public EnsembleDataFromEsp(String name, TimeSeries initialization, ArrayList<TimeSeries> forecasts, ArrayList<TimeSeries> historic) {
        super(name, initialization, forecasts, historic);
    }
    
    public static EnsembleData load(String initStatvarFile, String espSeriesFile, String espInitVariable, String title) {
        return load (initStatvarFile, espSeriesFile, espInitVariable, title, 1);
    }
    
    public static EnsembleData load(String initStatvarFile, String espSeriesFile, String espInitVariable, String title, int colNum) {
        EnsembleData ed = null;
        
        try {
            ed = new EnsembleData(title, null, null, null);
            
/*
 * Read and plot the data for the init period
 */
            MmsStatvarReader rsvd = new MmsStatvarReader(initStatvarFile);
            OuiCalendar initStart = rsvd.getStart();
            OuiCalendar initEnd = rsvd.getEnd();
            OuiCalendar foreStart = OuiCalendar.getInstance();
            foreStart.setJulian(initEnd.getJulian() + 1.0);
            
            if (espInitVariable == null) {
                espInitVariable = "basin_cfs.strmflow 1";
            }
            
            TimeSeries initialization = rsvd.getTimeSeries(espInitVariable);
            initialization.setName("init");
            ed.setInitialization(initialization);
            double[] init_vals = initialization.getVals();
            
/*
 **  Get the list of years from the esp output file.
 */
            MmsEspSeriesReader mesr = new MmsEspSeriesReader(espSeriesFile, foreStart);
            String[] years = mesr.getTraceYears();
            int numYears = years.length;
            
            ArrayList<TimeSeries> historic = new ArrayList<>(numYears);
            ArrayList<TimeSeries> forecasts = new ArrayList<>(numYears);
            ArrayList<TimeSeries> output = new ArrayList<>(numYears);

            for (int j = 0; j < numYears; j++) {
                TimeSeries ts = new TimeSeries();
                ts.setName(years[j]);
                historic.add(ts);
                
/* read the esp traces
 */
//                    MmsEspSeriesReader mesr = new MmsEspSeriesReader(1, espSeriesFile, Integer.parseInt(yr), foreStart);
                TimeSeries tsc = (TimeSeries)mesr.getForecastSeries(colNum, j);
//                    tsc.dump();
//                    System.out.println("start = " + tsc.getDates()[0]);
                forecasts.add(tsc);
/*
 * put together the output traces
 */
                double[] fore_vals = tsc.getVals();
                int out_size = init_vals.length + fore_vals.length;
                
                double[] out_vals = new double[out_size];
                double[] out_dates = new double[out_size];
                double start_date = initialization.getStart().getJulian();
                
                for (int i = 0; i < init_vals.length; i++) {
                    out_vals[i] = init_vals[i];
                    out_dates[i] = start_date + i;
                }
                
                for (int i = 0; i < fore_vals.length; i++) {
                    out_vals[i + init_vals.length] = fore_vals[i];
                    out_dates[i+ init_vals.length] = start_date + i + init_vals.length;
                }
                output.add(new TimeSeries(years[j], out_dates, out_vals, initialization.getStart(), null, "output", "esp series file", "unknown"));
            }
            
            
            ed.setHistoric(historic);
            ed.setForecasts(forecasts);
            ed.setOutput(output);
            
        } catch (Exception E) {
            System.err.println(E.getMessage());
        }
        
        return ed;
    }
}
