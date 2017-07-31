/*
 * SingleRun.java
 *
 * Created on July 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.WindowFactory;
import java.util.ArrayList;
import org.w3c.dom.Node;
import oui.mms.datatypes.EnsembleData;
import oui.mms.MmsProjectXml;
import oui.mms.io.MmsDataFileReader;
import oui.mms.io.MmsDataFileWriter;
import oui.mms.io.MmsStatvarReader;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;
import oui.util.concurrent.CommandRunner;

/**
 *
 * @author  markstro
 */
public class MmsOuiEspRunTreeNode extends MmsModelTreeNode implements MmsEspModelRunner {
    private MmsEspRunSimpleGui gui = null;
    private String dataFilePath = null;
    private String mmi = null;
    private final String start_time = null;
    private final String end_time = null;
    private OuiCalendar data_file_start;
    
    /** Creates a new instance of SingleRunMmi
     * @param xml_node */
    public MmsOuiEspRunTreeNode(Node xml_node) {
        super(xml_node);
        mmi = MmsProjectXml.getElementContent(xml_node, "@mmi", "none");
    }

    @Override
    public void run() {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();

        
        String dataFile = pxml.getMmiFile(mmi, "@data");
        dataFilePath = pxml.getFileFullPath("datafile", dataFile);
        
        MmsDataFileReader mdfr = new MmsDataFileReader(dataFilePath);
        data_file_start = mdfr.getStart();
        OuiCalendar data_file_end = mdfr.getEnd();

        if (showRunnerGui) {
            gui = new MmsEspRunSimpleGui(dataFilePath, data_file_start, data_file_end, this);
            WindowFactory.displayInFrame(gui, "Run MMS Model in ESP Mode");
        }
    }

    @Override
    public void runModel(OuiCalendar forecastStart, OuiCalendar forecastEnd) {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        String envFile = pxml.getMmiFile(mmi, "@env");
        String env = " -E" + pxml.getFileFullPath("envfile", envFile);
        String control = " -C" + pxml.getMmiFile(mmi, "@control");
//        String statvar = " -setYMD stat_var_file " + pxml.getMmiFile(mmi, "@statvar");
        String statvar = " -set stat_var_file " + "ESP.init.statvar";
        String paramFile = pxml.getMmiFile(mmi, "@param");
        String param = " -set param_file " + pxml.getFileFullPath("parameterfile", paramFile);
        String data = " -set data_file " + dataFilePath;
        String executable = pxml.getMmiPath (mmi) + "/" + pxml.getMmiFile(mmi, "@executable");
//        String runoption = " " + pxml.getElementContent(_xml_node, "@options", "");
        
//        OuiCalendar runStart = new OuiCalendar ();
//        runStart.setJul2Greg(forecastStart.getJulian() - 730.0);
//        OuiCalendar runEnd = forecastEnd;
 /*
  *  Init run
  */
//        start_time = " -setYMD start_time " + initStart.getControlFileDateTime();
//        end_time = " -setYMD end_time " + initEnd.getControlFileDateTime();
//        String arg = executable + " -batch" + env + control + data + param + start_time + end_time + statvar + " -setYMD statsON_OFF 1 -setYMD var_save_file ESP.init -setYMD save_vars_to_file 1 -setYMD init_vars_from_file 0 " + runoption;
//        System.out.println("MmsRun: executing = " + arg);
//        
//        try {
//            new CommandRunner(arg).evaluate();
////            JOptionPane.showMessageDialog(OuiGui.getTreeScrollPane(), "Run Completed", "Run Status", JOptionPane.INFORMATION_MESSAGE);
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(OuiGui.getTreeScrollPane(), "Run Unsuccessful\n" + e.getMessage(), "Run Status", JOptionPane.ERROR_MESSAGE);
//        }
//        
// /*
//  *  ESP run
//  */ 
//        OuiCalendar esp_start = new OuiCalendar(data_file_start.getYear(), forecastStart.getMonth(), forecastStart.getDay(), 0, 0, 0.0);
//        if (esp_start.getJulian() < data_file_start.getJulian()) {
//            esp_start = new OuiCalendar(esp_start.getYear() + 1, esp_start.getMonth(), esp_start.getDay(), 0, 0, 0.0);
//        }
//
//        OuiCalendar esp_end = new OuiCalendar(esp_start.getJulian() + (forecastEnd.getJulian() - forecastStart.getJulian()));
//        
//        start_time = " -setYMD start_time " + esp_start.getControlFileDateTime();
//        end_time = " -setYMD end_time " + esp_end.getControlFileDateTime();
//        arg = executable + " -batch -esp " + env + control + data + param + start_time + end_time + " -setYMD statsON_OFF 0 -setYMD var_init_file ESP.init -setYMD init_vars_from_file 1 -setYMD save_vars_to_file 0 " + runoption;
//        System.out.println("MmsRun: executing = " + arg);
//        
//        try {
//            new CommandRunner(arg).evaluate();
//            JOptionPane.showMessageDialog(OuiGui.getTreeScrollPane(), "Run Completed", "Run Status", JOptionPane.INFORMATION_MESSAGE);
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(OuiGui.getTreeScrollPane(), "Run Unsuccessful\n" + e.getMessage(), "Run Status", JOptionPane.ERROR_MESSAGE);
//        }
    }
    
    public static void writeEspDataFiles(EnsembleData ed, int initLength, String inputDataFile, String destDir, OuiCalendar forecastStart, OuiCalendar forecastEnd) {
        ArrayList<TimeSeries> forecasts = new ArrayList<> ();
        ArrayList<TimeSeries> historic = new ArrayList<> ();
        ArrayList<TimeSeries> input = new ArrayList<> ();
        
        MmsDataFileReader dataFileReader = new MmsDataFileReader(inputDataFile);
        OuiCalendar data_file_start = dataFileReader.getStart();
        OuiCalendar data_file_end = dataFileReader.getEnd();
/*
 *  Get all the data
 */
        double[][] all_data = dataFileReader.getAllData();
//        double[] dates = dataFileReader.getDates();
        int num_of_columns = dataFileReader.getNumberOfColumns();
        String header = "ESP Data File" + dataFileReader.getHeader();
/*
 *  The init_start date is two years before the forecast start date.
 *  The init_end data is one day before the forecast start date.
 */        
        OuiCalendar init_start = OuiCalendar.getInstance();
        init_start.setJulian(forecastStart.getJulian() - initLength);

        OuiCalendar init_end = OuiCalendar.getInstance();
        init_end.setJulian(forecastStart.getJulian() - 1.0);

        ed.setInitialization(new TimeSeries ("init", null, null, init_start, init_end, "init", dataFileReader.getFileName(), "unknown"));

        int init_offset = (int)(init_start.getJulian()) - (int)(data_file_start.getJulian());  //  this is the  offset from the start of the data to the start of the init period
        
        if (init_start.before(data_file_start)) {
            System.out.println ("MmsEspRun runESP: init_start is before the start of the data file.");
        }
        
/*
 *  new_data_start is the start date that will be associated with the new data file
 *  new_data_end is the end date that will be associated with the new data file
 */
        init_start.getMillis();
        OuiCalendar new_data_start = (OuiCalendar)(init_start.clone());
        forecastEnd.getMillis();
        OuiCalendar new_data_end = (OuiCalendar)(forecastEnd.clone());
        int new_data_length = (int)(new_data_end.getJulian()
                - new_data_start.getJulian() + 1.0);

        double[][] new_data = new double[num_of_columns][new_data_length];

/*
 *  Copy the data for the init period into the new_data array
 */
        for (int col = 0; col < num_of_columns; col++) {
            for (int i = 0; i < initLength; i++) {
//                double data = all_data[col][i + init_offset];
                new_data[col][i] = all_data[col][i + init_offset];
            }
        }
        
/*
 *  historic_start is the start date of the historic period used to generate the trace
 *  historic_end is the end date of the historic period used to generate the trace
 */
        int forecast_length = (int)(forecastEnd.getJulian()) - (int)(forecastStart.getJulian()) + 1;
        
        OuiCalendar historic_start = OuiCalendar.getInstance();
        historic_start.set(data_file_start.getYear(), forecastStart.getMonth(), forecastStart.getDay());

        OuiCalendar historic_end = OuiCalendar.getInstance();
        historic_end.setJulian(historic_start.getJulian() + forecast_length);

        while (historic_start.before(data_file_start)) {
            historic_start.set(historic_start.getYear() + 1, historic_start.getMonth() - 1, historic_start.getDay());
        }
        historic_end = OuiCalendar.getInstance();
        historic_end.setJulian(historic_start.getJulian() + forecast_length);

/*
 *  Loop over trace periods.
 */
        while (data_file_end.after(historic_end)) {
/*
 *  Write the datafile
 */
            int trace_offset = (int)(historic_start.getJulian()) - (int)(data_file_start.getJulian());  //  this is the  offset from the start of the data to the start of the trace period
            MmsDataFileWriter mdfw = new MmsDataFileWriter(new_data_start, new_data_end, header);
            

            for (int col = 0; col < num_of_columns; col++) {
                for (int i = 0; i < forecast_length; i++) {
                    new_data[col][i + initLength] = all_data[col][i + trace_offset];
                }
                mdfw.addTrace(new_data[col]);
            }
            String file_name = destDir + "/" + ed.getName() + "_ESP_" + historic_start.getYear() + ".data";
            mdfw.write(file_name);
                        
            String espYear = "" + historic_start.getYear();
            
            new_data_start.getMillis();
            new_data_end.getMillis();
            input.add(new TimeSeries(espYear, null, null,
                    (OuiCalendar)(new_data_start.clone()),
                    (OuiCalendar)(new_data_end.clone()), "input", file_name, "unknown"));
            historic_start.getMillis();
            historic_end.getMillis();
            historic.add (new TimeSeries(espYear, null, null,
                    (OuiCalendar)(historic_start.clone()),
                    (OuiCalendar)(historic_end.clone()), "historic",
                    dataFileReader.getFileName(), "unknown"));
            forecastStart.getMillis();
            forecastEnd.getMillis();
            forecasts.add (new TimeSeries(espYear, null, null, forecastStart,
                    forecastEnd, "forecast", null, "unknown"));
/*
 *  Step to the next trace
 */            
            historic_start.set(historic_start.getYear() + 1,
                    historic_start.getMonth() - 1, historic_start.getDay());
            historic_start.getMillis();
                    
            historic_end = OuiCalendar.getInstance();
            historic_end.setJulian(historic_start.getJulian() + forecast_length);
            historic_end.getMillis();
        }
        ed.setForecasts(forecasts);
        ed.setHistoric(historic);
        ed.setInput(input);
    }
    
    public static void runEsp (EnsembleData ed, String workspace, String model, String envFile, String contFileExt, String outputDir) {
        ArrayList inputFiles = ed.getInput();
        ArrayList historic = ed.getHistoric();
        ArrayList<TimeSeries> outputFiles = new ArrayList<> (inputFiles.size());
        
        for (int i = 0; i < inputFiles.size(); i++) {
            TimeSeries inputTimeSeries = (TimeSeries)(inputFiles.get(i));
            TimeSeries historicTimeSeries = (TimeSeries)(historic.get(i));
            int espYear = historicTimeSeries.getStart().getYear();
            
            String datafile = " -set data_file " + inputTimeSeries.getSource();
            String statvarfilename = outputDir + "/" + ed.getName() + "_ESP_" + espYear + ".statvar";
            String statvarfile = " -set nstatVars 1 -set statVar_names basin_cfs.strmflow -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            String start = " -set start_time " + inputTimeSeries.getStart().getControlFileDateTime();
            String end = " -set end_time " + inputTimeSeries.getEnd().getControlFileDateTime();
            String env = " -batch -E" + workspace + "/" + envFile;
            String control = " -C" + ed.getName() + contFileExt;
            String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0 -set db_on_off 0";
            String executable = workspace + "/" + model;
            
            String arg = executable + env + control + datafile + statvarfile + start + end + runoptions;
            
            System.out.println("MmsEspRun.runEsp: " + arg);
            
            CommandRunner.runModel(arg,  null);
            outputFiles.add(new TimeSeries("" + espYear, null, null, inputTimeSeries.getStart(), inputTimeSeries.getEnd(), "output", workspace + "/output/" + statvarfilename, "unknown"));
        }
        ed.setOutput(outputFiles);
    }
    
    public static void readEsp (EnsembleData ed, String variableName) {
        ArrayList outputFiles = ed.getOutput();
        TimeSeries outputTS;
        
/*
 *  Read the statvar files and put the data and dates into the output
 */
        for (int i = 0; i < outputFiles.size(); i++) {
            outputTS = (TimeSeries)(outputFiles.get(i));
            MmsStatvarReader msvr = new MmsStatvarReader(outputTS.getSource());
            TimeSeries ts = msvr.getTimeSeries(variableName);
            outputTS.setDates(ts.getDates());
            outputTS.setVals(ts.getVals());
        }
/*
 *  Read the init data
 */
        ArrayList forecasts = ed.getForecasts();
        TimeSeries initTS = ed.getInitialization();
        int initLength = (int)(initTS.getEnd().getJulian()) - (int)(initTS.getStart().getJulian()) + 1;
        
        outputTS = (TimeSeries)(outputFiles.get(0));

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
        }
    }
}
