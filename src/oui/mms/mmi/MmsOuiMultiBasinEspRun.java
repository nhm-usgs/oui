/*
 * SingleRun.java
 *
 * Created on July 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.WindowFactory;
import java.util.ArrayList;
import oui.esptool.EspTool;
import oui.mms.datatypes.EnsembleData;
import oui.mms.io.MmsDataFileReader;
import oui.mms.io.MmsDataFileWriter;
import oui.mms.io.MmsOuiEspReader;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;
import oui.util.concurrent.CommandRunner;


/**
 *
 * @author  markstro
 */
public class MmsOuiMultiBasinEspRun extends MmsOuiEspRun {
 
     public static ArrayList writeXrouteDataFiles(ArrayList localEnsembleData, String espDataDestDir, String xrouteHeaderDataFile, String[] basins) {
         
/*
 *  Loop over trace periods.
 */
         EnsembleData ed = (EnsembleData)(localEnsembleData.get(0));
         
         ArrayList<TimeSeries> output = ed.getOutput();
         TimeSeries outTS = (TimeSeries)(output.get(0));
         OuiCalendar start = outTS.getStart();
         OuiCalendar end = outTS.getEnd();
         
         OuiCalendar initStart = ed.getInitializationStart();
         OuiCalendar initEnd = ed.getInitializationEnd();
//         OuiCalendar forecastStart = ed.getForecastStart();
//         OuiCalendar forecastEnd = ed.getForecastEnd();
         
         
         MmsDataFileReader mdfr = new MmsDataFileReader(xrouteHeaderDataFile);
         String header = "ESP xroute Data File" + mdfr.getHeader();

 /*
  *  Set up routedEnsembleData
  *  Assuming that each basin is a forecast point
  */
         ArrayList<EnsembleData> routedEnsembleData = new ArrayList<> (localEnsembleData.size());

         for (int basinIndex = 0; basinIndex < localEnsembleData.size(); basinIndex++) {
             EnsembleData basinEd = (EnsembleData)(localEnsembleData.get(basinIndex));
             ed = new EnsembleData("xroute_" + basinEd.getName(), null, null, basinEd.getHistoric());
             ed.setInput(new ArrayList<> (basinEd.getHistoric().size()));
             ed.setOutput(new ArrayList<> (basinEd.getHistoric().size()));
             ed.setForecasts(new ArrayList<> (basinEd.getHistoric().size()));
             ed.setInitialization(new TimeSeries ("init", null, null, initStart, initEnd, "init",  null, "unknown"));
             routedEnsembleData.add(basinIndex, ed);
         }
         ed = (EnsembleData)(routedEnsembleData.get(0));
         ArrayList historic = ed.getHistoric();
         
/*
 *  Write the datafile, one for each forecast period
 */
         for (int yearIndex = 0; yearIndex < historic.size(); yearIndex++) {
             TimeSeries histTS = (TimeSeries)(historic.get(yearIndex));
             String year = histTS.getName();
             
             String fileName = espDataDestDir + "/xroute_ESP_" + year + ".data";
             System.out.println ("writing " + fileName);

            MmsDataFileWriter mdfw = new MmsDataFileWriter(start, end, header);
            
            for (int basinIndex = 0; basinIndex < localEnsembleData.size(); basinIndex++) {
                EnsembleData basinEd = (EnsembleData)(localEnsembleData.get(basinIndex));
                EnsembleData routedBasinEd = (EnsembleData)(routedEnsembleData.get(basinIndex));
                routedBasinEd.getInput().add(yearIndex, new TimeSeries ("input", null, null, start, end, "input", fileName, "unknown"));

                ArrayList outAL = (ArrayList)(basinEd.getOutput());
                TimeSeries outputTS = (TimeSeries)(outAL.get(yearIndex));
//                System.out.println ("   adding data year = " + year + " basin = " + basinEd.getName());
                mdfw.addTrace(outputTS.getVals());
            }
            
            mdfw.write(fileName);
        }
         return routedEnsembleData;
    }
     
    public static void runXrouteEsp(ArrayList routedEnsembleData, String mms_workspace, String xrouteExecutable, String envFile, String xrouteControlFile, String espOutputDestDir, String xrouteEspVariableName, String[] xrouteEspVariableIndex) {

        EnsembleData ed = (EnsembleData)(routedEnsembleData.get(0));
        ArrayList input = ed.getInput();
        ArrayList historic = ed.getHistoric();
        
        for (int i = 0; i < input.size(); i++) {
            TimeSeries inputTS = (TimeSeries)(input.get(i));
            TimeSeries historicTS = (TimeSeries)(historic.get(i));
            int espYear = historicTS.getStart().getYear();
            
            String datafile = " -set data_file " + inputTS.getSource();
            String statvarfilename = espOutputDestDir + "/xroute_ESP_" + espYear + ".statvar";
            String statvarfile = " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            String start = " -set start_time " + inputTS.getStart().getControlFileDateTime();
            String end = " -set end_time " + inputTS.getEnd().getControlFileDateTime();
            String env = " -batch -E" + mms_workspace + "/" + envFile;
            String control = " -C" + xrouteControlFile;
            String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0";
            String executable = mms_workspace + "/" + xrouteExecutable;
            
            String arg = executable + env + control + datafile + statvarfile + start + end + runoptions;
            
            System.out.println("MmsOuiMultiBasinEspRun.runXrouteEsp: " + arg);
            
            CommandRunner.runModel(arg,  null);
            
            for (int basinIndex = 0; basinIndex < routedEnsembleData.size(); basinIndex++) {
                ed = (EnsembleData)(routedEnsembleData.get(basinIndex));
                ArrayList<TimeSeries> output = ed.getOutput();
                output.add(new TimeSeries("" + espYear, null, null, inputTS.getStart(), inputTS.getEnd(), xrouteEspVariableName + " " + xrouteEspVariableIndex[basinIndex], mms_workspace + "/output/" + statvarfilename, "unknown"));

                ArrayList<TimeSeries> forecasts = ed.getForecasts();
                forecasts.add(new TimeSeries("" + espYear, null, null, null, null, xrouteEspVariableName + " " + xrouteEspVariableIndex[basinIndex], mms_workspace + "/output/" + statvarfilename, "unknown"));
            }
        }
    }
    
    
//  Run this one for MMF models
    public static void runXrouteEsp(ArrayList routedEnsembleData, String mms_workspace, String xrouteExecutable, String envFile, String xrouteControlFile, String espOutputDestDir, String xrouteEspVariableName, String[] xrouteEspVariableIndex, String relativeMmsWorkspace) {

        EnsembleData ed = (EnsembleData)(routedEnsembleData.get(0));
        ArrayList input = ed.getInput();
        ArrayList historic = ed.getHistoric();
        
        for (int i = 0; i < input.size(); i++) {
            TimeSeries inputTS = (TimeSeries)(input.get(i));
            TimeSeries historicTS = (TimeSeries)(historic.get(i));
            int espYear = historicTS.getStart().getYear();
            
            String datafile = " -set data_file " + inputTS.getSource();
            String paramfile = " -set param_file " + relativeMmsWorkspace + "/input/params/xroute.params";
            String statvarfilename = relativeMmsWorkspace + "/output/" + espOutputDestDir + "/xroute_ESP_" + espYear + ".statvar";
            String statvarfile = " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            String start = " -set start_time " + inputTS.getStart().getControlFileDateTime();
            String end = " -set end_time " + inputTS.getEnd().getControlFileDateTime();
//            String env = " -batch -E" + mms_workspace + "/" + envFile;
            String control = " -C" + relativeMmsWorkspace + "/control/" + xrouteControlFile;
            String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0";
            String executable = mms_workspace + "/" + xrouteExecutable;
            
//            String arg = executable + env + control + datafile + paramfile + statvarfile + start + end + runoptions;
            String arg = executable + control + datafile + paramfile + statvarfile + start + end + runoptions;
            
            System.out.println("MmsOuiMultiBasinEspRun.runXrouteEsp: " + arg);
            
            CommandRunner.runModel(arg,  null);
            
            for (int basinIndex = 0; basinIndex < routedEnsembleData.size(); basinIndex++) {
                ed = (EnsembleData)(routedEnsembleData.get(basinIndex));
                ArrayList<TimeSeries> output = ed.getOutput();
                output.add(new TimeSeries("" + espYear, null, null, inputTS.getStart(), inputTS.getEnd(), xrouteEspVariableName + " " + xrouteEspVariableIndex[basinIndex], mms_workspace + "/output/" + espOutputDestDir + "/xroute_ESP_" + espYear + ".statvar", "unknown"));

                ArrayList<TimeSeries> forecasts = ed.getForecasts();
                forecasts.add(new TimeSeries("" + espYear, null, null, null, null, xrouteEspVariableName + " " + xrouteEspVariableIndex[basinIndex], mms_workspace + "/output/" + espOutputDestDir + "/xroute_ESP_" + espYear + ".statvar", "unknown"));
            }
        }
    }
        
    public static void readXrouteEsp(ArrayList routedEnsembleData){
         for (int basinIndex = 0; basinIndex < routedEnsembleData.size(); basinIndex++) {
             EnsembleData basinEd = (EnsembleData)(routedEnsembleData.get(basinIndex));
             MmsOuiEspReader.readEsp(basinEd);
         }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            EnsembleData ed = null;
            
//            String dataFileBasinName = "RioGrande";
//            String espVariableName = "basin_cfs.strmflow";
//            String espVariableIndex = "1";
//            String espVariableCombo = espVariableName + " " + espVariableIndex;
//            int initLength = 730;
            String mms_workspace = "/home/projects/oui_projects/rio_grande/riogr_mms_work";
//            String data_file = mms_workspace + "/input/data/" + dataFileBasinName + ".xprms_xyz.data";
//            String espDataDestDir = mms_workspace + "/input/data/esp";
//            String espOutputDestDir = "esp";         //  This has mms_work/output/ prepended to it by MMS
//            String executable = "models/xprms_xyz";
//            String xrouteExecutable = "models/xroute";
//            String envFile = "control/mms.env";
//            String controlFileExt = ".xprms_xyz.oui.control";
//            String xrouteControlFile = "RioGrande.xroute.oui.control";
            OuiCalendar forecastStart = OuiCalendar.getInstance();
            forecastStart.setDT("1998-10-01");

            OuiCalendar forecastEnd = OuiCalendar.getInstance();
            forecastEnd.setDT("1999-07-30");
            
            String xrouteHeaderDataFile = mms_workspace + "/input/data/" + "RioGrande.xroute.data.header";
            
            String[] basins = {"Abiquiu", "Blanco", "ChamaChamita", "Conejos", "DelNorte", "ElVado", "Embudo", "Heron",
                                "Jemez", "LittleNavajo", "LosPinos", "Navajo", "RedRiver", "RioPuebloDeTaos",
                                "SanAntonio", "SantaFe", "RGCerro", "RGEmbudo", "RGTaosJunction", "RGOtowi"};
            
//            String xrouteEspVariableName = "inflow.muskingum";
//            String[] xrouteEspVariableIndex = {"52",
//                                               "0",
//                                               "55",
//                                               "42",
//                                               "50",
//                                               "48",
//                                               "63",
//                                               "43",
//                                               "29",
//                                               "0",
//                                               "6",
//                                               "0",
//                                               "1",
//                                               "9",
//                                               "31",
//                                               "16",
//                                               "59",
//                                               "64",
//                                               "62",
//                                               "66"
//            };
            
//            { //  Use this block to generate the ESP run and save to XML file
//                ed = new EnsembleData(dataFileBasinName, null, null, null);
//                MmsOuiEspRun.writeEspDataFiles(ed, initLength, data_file, espDataDestDir, forecastStart, forecastEnd);
//                
//                for (int i = 0; i < basins.length; i++) {
//                    ed.setName(basins[i]);
//                    MmsOuiEspRun.runEsp(ed, mms_workspace, executable, envFile, controlFileExt, espOutputDestDir, espVariableName, espVariableIndex);
//                    MmsOuiEspReader.readEsp(ed);
//                    
//                    String xml_file = mms_workspace + "/output/esp/" + basins[i] + ".xml";
//                    ed.save(xml_file);
//                }
//            }
            
            ArrayList<EnsembleData> localEnsembleData = new ArrayList<> (basins.length);
            
            {  //  Use this block to load ESP run into EnsembleData
                for (int i = 0; i < basins.length; i++) {
                    String xml_file = mms_workspace + "/output/esp/" + basins[i] + ".xml";
                    ed = EnsembleData.load(xml_file);
                    localEnsembleData.add (i, ed);
                    
//                    oui.esptool.EspTool espTool = new oui.esptool.EspTool(ed);
//                    espTool.show();
                }
            }
/*
 *  Xroute stuff
 */            
//            ArrayList routedEnsembleData = MmsOuiMultiBasinEspRun.writeXrouteDataFiles(localEnsembleData, espDataDestDir, xrouteHeaderDataFile, basins);
//            MmsOuiMultiBasinEspRun.runXrouteEsp(routedEnsembleData, mms_workspace, xrouteExecutable, envFile, xrouteControlFile, espOutputDestDir, xrouteEspVariableName, xrouteEspVariableIndex);
//            MmsOuiMultiBasinEspRun.readXrouteEsp(routedEnsembleData);
            
//            Iterator it = routedEnsembleData.iterator();
//            while (it.hasNext()) {
//                ed = (EnsembleData)(it.next());
//                String xml_file = mms_workspace + "/output/esp/" + ed.getName() + ".xml";
//                ed.save(xml_file);
//            }
            
            ArrayList<EnsembleData> routedEnsembleData = new ArrayList<> (basins.length);
            for (int i = 0; i < basins.length; i++) {
                String xml_file = mms_workspace + "/output/esp/xroute_" + basins[i] + ".xml";
                ed = EnsembleData.load(xml_file);
                routedEnsembleData.add(i, ed);
            }         
        
        EspTool et = new EspTool(ed);
        WindowFactory.displayInFrame(et, "OUI ESP Tool");
    } catch (oui.mms.datatypes.SetOuiCalendarException e) {}
    
}
}
