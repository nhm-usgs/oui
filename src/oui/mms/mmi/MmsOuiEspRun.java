/*
 * SingleRun.java
 *
 * Created on July 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.WindowFactory;
import java.io.File;
import java.util.ArrayList;
import oui.esptool.EspTool;
import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;
import oui.mms.io.*;
import oui.util.concurrent.CommandRunner;

/**
 *
 * @author  markstro
 */
public class MmsOuiEspRun {
    public static void writeEspDataFiles(EnsembleData ed, int initLength,
            String inputDataFile, String destDir, OuiCalendar forecastStart,
            OuiCalendar forecastEnd) {
        ArrayList<TimeSeries> forecasts = new ArrayList();
        ArrayList<TimeSeries> historic = new ArrayList();
        ArrayList<TimeSeries> input = new ArrayList();
        
        MmsDataFileReader dataFileReader = new MmsDataFileReader(inputDataFile);
        OuiCalendar data_file_start = dataFileReader.getStart();
        OuiCalendar data_file_end = dataFileReader.getEnd();
/*
 *  Get all the data
 */
        double[][] all_data = dataFileReader.getAllData();
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

        ed.setInitialization(new TimeSeries ("init", null, null, init_start,
                init_end, "init", dataFileReader.getFileName(), "unknown"));

        //  this is the  offset from the start of the data to the start of the init period
        int init_offset = (int)(init_start.getJulian()) - (int)(data_file_start.getJulian());
        
        if (init_start.before(data_file_start)) {
            System.out.println ("MmsEspRun runESP: init_start is before the start of the data file.");
        }
        
/*
 *  new_data_start is the start date that will be associated with the new data file
 *  new_data_end is the end date that will be associated with the new data file
 */
        OuiCalendar new_data_start = OuiCalendar.getClone(init_start);
        OuiCalendar new_data_end = OuiCalendar.getClone(forecastEnd);
        int new_data_length = (int)(new_data_end.getJulian()
                - (int)(new_data_start.getJulian()) + 1);

        double[][] new_data = new double[num_of_columns][new_data_length];

/*
 *  Copy the data for the init period into the new_data array
 */
        for (int col = 0; col < num_of_columns; col++) {
            for (int i = 0; i < initLength; i++) {
                new_data[col][i] = all_data[col][i + init_offset];
            }
        }
        
/*
 *  historic_start is the start date of the historic period used to generate the trace
 *  historic_end is the end date of the historic period used to generate the trace
 */
        int forecast_length = (int) (forecastEnd.getJulian())
                - (int) (forecastStart.getJulian()) + 1;

        OuiCalendar historic_start = OuiCalendar.getInstance();
        // DANGER ESP debug for Kate!
//        historic_start.set(data_file_start.getYear(), forecastStart.getMonth(),
//                forecastStart.getDay());
        historic_start.set(data_file_start.getYear(), forecastStart.getMonth() - 1,
                forecastStart.getDay());

        OuiCalendar historic_end = OuiCalendar.getInstance();
//        historic_end.setJulian(historic_start.getJulian() + forecast_length);
        historic_end.setJulian(historic_start.getJulian() + forecast_length - 1);

        while (historic_start.before(data_file_start)) {
            historic_start.set(historic_start.getYear() + 1,
                    historic_start.getMonth() - 1, historic_start.getDay());
        }
        historic_end = OuiCalendar.getInstance();
//        historic_end.setJulian(historic_start.getJulian() + forecast_length);
        historic_end.setJulian(historic_start.getJulian() + forecast_length - 1);

/*
 *  Loop over trace periods.
 */
        while (data_file_end.after(historic_end)) {
/*
 *  Write the datafile
 */
//  this is the  offset from the start of the data to the start of the trace period
            int trace_offset = (int)(historic_start.getJulian())
                    - (int)(data_file_start.getJulian());
            
            MmsDataFileWriter mdfw = new MmsDataFileWriter(new_data_start,
                    new_data_end, header);
            
            for (int col = 0; col < num_of_columns; col++) {
                for (int i = 0; i < forecast_length; i++) {
                    new_data[col][i + initLength] = all_data[col][i + trace_offset];
                }
                mdfw.addTrace(new_data[col]);
            }
            
            String file_name = destDir + "/" + ed.getName() + "_ESP_"
                    + historic_start.getYear() + ".data";
            mdfw.write(file_name);
                        
            String espYear = "" + historic_start.getYear();
            input.add(new TimeSeries(espYear, null, null,
                    OuiCalendar.getClone(new_data_start),
                    OuiCalendar.getClone(new_data_end), "input", file_name, "unknown"));
            
            historic.add (new TimeSeries(espYear, null, null,
                    OuiCalendar.getClone(historic_start),
                    OuiCalendar.getClone(historic_end),
                    "historic", dataFileReader.getFileName(), "unknown"));
            
            forecasts.add (new TimeSeries(espYear, null, null, forecastStart,
                    forecastEnd, "forecast", null, "unknown"));
/*
 *  Step to the next trace
 */            
            historic_start.set(historic_start.getYear() + 1,
                    historic_start.getMonth() - 1, historic_start.getDay());
            historic_end = OuiCalendar.getInstance();
            historic_end.setJulian(historic_start.getJulian() + forecast_length);
        }
        ed.setForecasts(forecasts);
        ed.setHistoric(historic);
        ed.setInput(input);
    }
    
    public static void runEsp (EnsembleData ed, String workspace, String model, String envFile, String contFileExt, String outputDir, String espVariableName, String espVariableIndex) {
        ArrayList inputFiles = ed.getInput();
        ArrayList historic = ed.getHistoric();
        ArrayList<TimeSeries> outputFiles = new ArrayList (inputFiles.size());
        
        for (int i = 0; i < inputFiles.size(); i++) {
            TimeSeries inputTimeSeries = (TimeSeries)(inputFiles.get(i));
            TimeSeries historicTimeSeries = (TimeSeries)(historic.get(i));
            int espYear = historicTimeSeries.getStart().getYear();
            
            String datafile = " -set data_file " + inputTimeSeries.getSource();
            String statvarfilename = outputDir + "/" + ed.getName() + "_ESP_" + espYear + ".statvar";
            
            //  If espVariableName or espVariableIndex is null, then use the use
            //  the statvar specification in the control file.
            String statvarfile = " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            if (espVariableName != null && espVariableIndex != null) {
                statvarfile = " -set nstatVars 1 -set statVar_names " + espVariableName + " -set statVar_element " + espVariableIndex + " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            }
            
            String start = " -set start_time " + inputTimeSeries.getStart().getControlFileDateTime();
            String end = " -set end_time " + inputTimeSeries.getEnd().getControlFileDateTime();
            String env = " -batch -E" + workspace + "/" + envFile;
            String control = " -C" + ed.getName() + contFileExt;
            String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0 -set db_on_off 0";
            String executable = workspace + "/" + model;
            
            String arg = executable + env + control + datafile + statvarfile + start + end + runoptions;
            
            System.out.println("MmsEspRun.runEsp: " + arg);
            
            CommandRunner.runModel(arg,  null);
            outputFiles.add(new TimeSeries("" + espYear, null, null,
                    inputTimeSeries.getStart(), inputTimeSeries.getEnd(),
                    espVariableName + " " + espVariableIndex , workspace + "/output/" + statvarfilename, "unknown"));
        }
        ed.setOutput(outputFiles);
    }
 
    public static void runEsp (EnsembleData ed, String workspace, String model, String envFile, String contFileExt,
            String outputDir, String espVariableName, String espVariableIndex, String relativeMmsWorkspace, String paramFileExt) {
        ArrayList inputFiles = ed.getInput();
        ArrayList historic = ed.getHistoric();
        ArrayList<TimeSeries> outputFiles = new ArrayList (inputFiles.size());
        
        for (int i = 0; i < inputFiles.size(); i++) {
            TimeSeries inputTimeSeries = (TimeSeries)(inputFiles.get(i));
            TimeSeries historicTimeSeries = (TimeSeries)(historic.get(i));
            int espYear = historicTimeSeries.getStart().getYear();
            
            String datafile = " -set data_file " + inputTimeSeries.getSource();
            String paramfile = " -set param_file " + relativeMmsWorkspace + "/input/params/" + ed.getName() + paramFileExt;
            String statvarfilename = relativeMmsWorkspace + "/output/" +  outputDir + "/" + ed.getName() + "_ESP_" + espYear + ".statvar";
            
            //  If espVariableName or espVariableIndex is null, then use the use
            //  the statvar specification in the control file.
            String statvarfile = " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            if (espVariableName != null && espVariableIndex != null) {
                statvarfile = " -set nstatVars 1 -set statVar_names " + espVariableName + " -set statVar_element " + espVariableIndex + " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            }
            
            String start = " -set start_time " + inputTimeSeries.getStart().getControlFileDateTime();
            String end = " -set end_time " + inputTimeSeries.getEnd().getControlFileDateTime();
//            String env = " -batch -E" + workspace + "/" + envFile;
            String control = " -C" + relativeMmsWorkspace + "/control/" + ed.getName() + contFileExt;
            String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0";
//            String executable = model;
            String executable = relativeMmsWorkspace + "/" + model;
            
            String arg = executable + control + datafile + paramfile + statvarfile + start + end + runoptions;
            
            System.out.println("MmsEspRun.runEsp: " + arg);
            
            CommandRunner.runModel(arg,  null);
            outputFiles.add(new TimeSeries("" + espYear, null, null, inputTimeSeries.getStart(), inputTimeSeries.getEnd(), espVariableName + " " + espVariableIndex , workspace + "/output/" + outputDir + "/" + ed.getName() + "_ESP_" + espYear + ".statvar", "unknown"));
        }
        ed.setOutput(outputFiles);
    }

    /**
     * This one called for single basin mmf
     * @param ed
     * @param workspace
     * @param model
     * @param contFile
     * @param outputDir
     * @param espVariableName
     * @param espVariableIndex
     * @param relativeMmsWorkspace
     * @param paramFile
     */
    public static void runEsp (EnsembleData ed, String workspace, String model, String contFile,
        String outputDir, String espVariableName, String espVariableIndex, String relativeMmsWorkspace, String paramFile) {
        ArrayList inputFiles = ed.getInput();
        ArrayList historic = ed.getHistoric();
        ArrayList<TimeSeries> outputFiles = new ArrayList (inputFiles.size());
        ArrayList<TimeSeries> cbhPrecipFiles = ed.getCbhPrecip();
        ArrayList<TimeSeries> cbhTmaxFiles = ed.getCbhTmax();
        ArrayList<TimeSeries> cbhTminFiles = ed.getCbhTmin();
        
        for (int i = 0; i < inputFiles.size(); i++) {
            TimeSeries inputTimeSeries = (TimeSeries)(inputFiles.get(i));
            TimeSeries historicTimeSeries = (TimeSeries)(historic.get(i));
            int espYear = historicTimeSeries.getStart().getYear();
            
            String datafile = " -set data_file " + inputTimeSeries.getSource();
            String paramfile = " -set param_file " + paramFile;
            String statvarfilename = relativeMmsWorkspace + "/output/" + outputDir + "/" + ed.getName() + "_ESP_" + espYear + ".statvar";

            String statvarfile = "  -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            if (espVariableName != null) {
                statvarfile = " -set nstatVars 1 -set statVar_names " + espVariableName + " -set statVar_element " + espVariableIndex + " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            }

            String cbhFiles = "";
            if (cbhPrecipFiles != null) {
                cbhFiles = " -set precip_day " + cbhPrecipFiles.get(i).getSource();
            }

            String cbhTmax = "";
            if (cbhTmaxFiles != null) {
                cbhTmax = " -set tmax_day " + cbhTmaxFiles.get(i).getSource();
            }

            String cbhTmin = "";
            if (cbhTminFiles != null) {
                cbhTmin = " -set tmin_day " + cbhTminFiles.get(i).getSource();
            }

            String start = " -set start_time " + inputTimeSeries.getStart().getControlFileDateTime();
            String end = " -set end_time " + inputTimeSeries.getEnd().getControlFileDateTime();
//            String env = " -batch -E" + workspace + "/" + envFile;
            String control = " -C" + contFile;
            String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set aniOutON_OFF 0";
//            String executable = workspace + "/" + model;
            String executable = model;
            
            String arg = executable + control + datafile + paramfile + statvarfile + cbhFiles + cbhTmax + cbhTmin + start + end + runoptions;
            
            System.out.println("MmsEspRun.runEsp: " + arg);
            
            CommandRunner.runModel(arg,  null);
            outputFiles.add(new TimeSeries("" + espYear, null, null,
                    inputTimeSeries.getStart(),
                    inputTimeSeries.getEnd(),
                    espVariableName + " " + espVariableIndex,
                    workspace + "/output/" + outputDir + "/" + ed.getName() + "_ESP_" + espYear + ".statvar",
                    "unknown"));
        }
        ed.setOutput(outputFiles);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            EnsembleData ed;
            
            String dataFileBasinName = "RioGrande";
            String subBasinName = "Abiquiu";
            String espVariableName = "basin_cfs.strmflow";
            String espVariableIndex = "1";
//            String espVariableCombo = espVariableName + " " + espVariableIndex;
            int initLength = 730;
            String mms_workspace = "/home/projects/oui_projects/rio_grande/riogr_mms_work";
            String xml_file = mms_workspace + "/output/esp/Abiquiu.xml";
            String data_file = mms_workspace + "/input/data/" + dataFileBasinName + ".xprms_xyz.data";
            String espDataDestDir = mms_workspace + "/input/data/esp";
            String espOutputDestDir = "esp";         //  This has mms_work/output/ prepended to it by MMS
            String executable = "models/xprms_xyz";
            String envFile = "control/mms.env";
            String controlFileExt = ".xprms_xyz.oui.control";
            OuiCalendar forecastStart = OuiCalendar.getInstance();
            forecastStart.setDT("1998-10-01");
            OuiCalendar forecastEnd = OuiCalendar.getInstance();
            forecastEnd.setDT("1999-07-30");
            
            { //  Use this block to generate the ESP run and save to XML file
                ed = new EnsembleData(dataFileBasinName, null, null, null);
                MmsOuiEspRun.writeEspDataFiles(ed, initLength, data_file, espDataDestDir, forecastStart, forecastEnd);
                
                ed.setName(subBasinName);
                MmsOuiEspRun.runEsp(ed, mms_workspace, executable, envFile,
                        controlFileExt, espOutputDestDir, espVariableName, espVariableIndex);
                MmsOuiEspReader.readEsp(ed);
                
                ed.save(xml_file);
            }
            
            {  //  Use this block to load ESP run into EnsembleData
                ed = EnsembleData.load(xml_file);
            }
            
            EspTool et = new EspTool(ed);
            WindowFactory.displayInFrame(et, "OUI ESP Tool");
        } catch (oui.mms.datatypes.SetOuiCalendarException e) {
        }
    }

    static void writeEspCBHFiles(EnsembleData ed, ArrayList<TimeSeries> cbhArray,
            int initLength, String cbh_file, String espDataDestDir,
            OuiCalendar forecastStart, OuiCalendar forecastEnd, OuiCalendar check_start,
            OuiCalendar check_end, int check_num_dates) throws CbhDateException {

        CbhReader cbhReader = new CbhReader(cbh_file);
        OuiCalendar data_file_start = cbhReader.getStart();
        OuiCalendar data_file_end = cbhReader.getEnd();
        
        // Check that the CBH file has the same dates as the PRMS Data File.
        if (!OuiCalendar.isSameDay(data_file_start, check_start)) {
            throw (new CbhDateException(data_file_start.getMmsDateTime(),
                    data_file_end.getMmsDateTime(), cbhReader.getDates().length));
        }
/*
 *  Get all the data
 */
        double[][] all_data = cbhReader.getAllData();
        int num_of_columns = cbhReader.getNumberOfColumns();
        String header = "ESP Data File" + cbhReader.getHeader();
/*
 *  The init_start date is two years before the forecast start date.
 *  The init_end data is one day before the forecast start date.
 */
        OuiCalendar init_start = OuiCalendar.getInstance();
        init_start.setJulian(forecastStart.getJulian() - initLength);

        OuiCalendar init_end = OuiCalendar.getInstance();
        init_end.setJulian(forecastStart.getJulian() - 1.0);

        ed.setInitialization(new TimeSeries ("init", null, null, init_start,
                init_end, "init", cbhReader.getFileName(), "unknown"));

        //  this is the  offset from the start of the data to the start of the init period
        int init_offset = (int)(init_start.getJulian()) - (int)(data_file_start.getJulian());

        if (init_start.before(data_file_start)) {
            System.out.println ("MmsOuiEspRun runESP: init_start is before the start of the data file.");
        }

/*
 *  new_data_start is the start date that will be associated with the new data file
 *  new_data_end is the end date that will be associated with the new data file
 */
        OuiCalendar new_data_start = OuiCalendar.getClone(init_start);
        OuiCalendar new_data_end = OuiCalendar.getClone(forecastEnd);
        int new_data_length = (int)(new_data_end.getJulian()
                - (int)(new_data_start.getJulian()) + 1);

/*
 *  historic_start is the start date of the historic period used to generate the trace
 *  historic_end is the end date of the historic period used to generate the trace
 */
        int forecast_length = (int)(forecastEnd.getJulian())
                - (int)(forecastStart.getJulian()) + 1;

        OuiCalendar historic_start = OuiCalendar.getInstance();
// DANGER ESP debug for Kate!
//        historic_start.set(data_file_start.getYear(), forecastStart.getMonth(), forecastStart.getDay());
//        historic_start.set(data_file_start.getYear(), forecastStart.getMonth() - 1, forecastStart.getDay());
        historic_start.set(data_file_start.getYear(), forecastStart.getMonth()-1,
                forecastStart.getDay());

        OuiCalendar historic_end = OuiCalendar.getInstance();
        historic_end.setJulian(historic_start.getJulian() + forecast_length);

        while (historic_start.before(data_file_start)) {
// DANGER ESP debug for Kate!
//            historic_start.set(historic_start.getYear() + 1, historic_start.getMonth() - 1, historic_start.getDay());
            historic_start.set(historic_start.getYear() + 1,
                    historic_start.getMonth() - 1, historic_start.getDay());
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
// DANGER ESP debug for Kate!
//            int trace_offset = (int)(historic_start.getJulian()) - (int)(data_file_start.getJulian());  //  this is the  offset from the start of the data to the start of the trace period
            int trace_offset = (int)(historic_start.getJulian())
                    - (int)(data_file_start.getJulian());  //  this is the  offset from the start of the data to the start of the trace period
// 4/1/2013 trace_offset is 23193
            CbhWriter mdfw = new CbhWriter(new_data_start, new_data_end, header);

            for (int col = 0; col < num_of_columns; col++) {
                // Allocate a new array for each HRU. Write the trace data into this array.
                double[] new_data = new double[new_data_length];
                
                // Write in the initialization period values
                for (int i = 0; i < initLength; i++) {
                    new_data[i] = all_data[col][i + init_offset];
                }

                // Write in the forecast period values
                for (int i = 0; i < forecast_length; i++) {
                    // DANGER ESP debug for Kate!
                    new_data[i + initLength] = all_data[col][i + trace_offset];
                }
                // Send the trace for the HRU off to the CBH writer.
                mdfw.addTrace(new_data);
            }

            File foo = new File (cbh_file);
            String file_name = espDataDestDir + "/" + historic_start.getYear()
                    + "_ESP_" + foo.getName();
            mdfw.write(file_name);

            String espYear = "" + historic_start.getYear();       
            cbhArray.add(new TimeSeries(espYear, null, null,
                    OuiCalendar.getClone(new_data_start),
                    OuiCalendar.getClone(new_data_end), "input", file_name, "unknown"));
/*
 *  Step to the next trace
 */
            historic_start.set(historic_start.getYear() + 1,
                    historic_start.getMonth() - 1, historic_start.getDay());
            historic_end = OuiCalendar.getInstance();
            historic_end.setJulian(historic_start.getJulian() + forecast_length);
        }
    }
}
