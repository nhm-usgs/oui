/*
 * MmsOuiMultiBasinEspRunTreeNode.java
 *
 * Created on Dec 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.WindowFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
public class MmsOuiMultiDataFileRunTreeNode extends MmsModelTreeNode implements MmsSingleModelRunner {
//    private MmsProjectXml pxml;
    private String dataFileExtension;
    private String[] subBasins;
    private String mmsWorkspace;
    private String executable;
    private String envFile;
    private String controlFileExt;
    private String variableName;
    private String variableIndex;
    private String xrouteHeaderDataFile;
    private String xrouteExecutable;
    private String xrouteControlFile;
    private String xrouteVariableName;
    private String[] xrouteVariableIndex;
    private  String mmi = null;
    
    /** Creates a new instance of SingleRunMmi
     * @param xml_node */
    public MmsOuiMultiDataFileRunTreeNode(Node xml_node) {
        super(xml_node);
        mmi = MmsProjectXml.getElementContent(xml_node, "@mmi", null);
    }
    
    @Override
    public void run() {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        
/*
 *  Get the MMI nodes
 */

        Node mmiNode = pxml.getMmiNode(mmi);
        Node prmsNode = MmsProjectXml.selectSingleNode(mmiNode, "models[@name='prms']");
        Node routeNode = MmsProjectXml.selectSingleNode(mmiNode, "models[@name='route']");
             
/*
 *  Read the stuff for PRMS from XML
 */        
        mmsWorkspace = pxml.getPath(prmsNode);
        dataFileExtension = MmsProjectXml.getElementContent(prmsNode, "@dataFileExt", null);
        variableName = MmsProjectXml.getElementContent(prmsNode, "@variableName", null);
        variableIndex = MmsProjectXml.getElementContent(prmsNode, "@variableIndex", null);
//        initLength = Integer.parseInt(pxml.getElementContent(prmsNode, "@initLength", null));
//        espIODir = pxml.getElementContent(prmsNode, "@espIODir", null);
        executable = MmsProjectXml.getElementContent(prmsNode, "@executable", null);
        envFile = MmsProjectXml.getElementContent(prmsNode, "@envFile", null);
        controlFileExt = MmsProjectXml.getElementContent(prmsNode, "@controlFileExt", null);

/*
 *  Get the sub basin names
 */
        String subBasinTheme = MmsProjectXml.getElementContent(prmsNode, "@theme", null);
        Node subBasinThemeMappingNode = MmsProjectXml.selectSingleNode(pxml.getProjectXmlNode(), "MetaData/gis/themes/theme[@theme='" + subBasinTheme + "']/mappings");
        
        // DANGER hack
        if (subBasinThemeMappingNode == null) {
            subBasinThemeMappingNode = MmsProjectXml.selectSingleNode(pxml.getProjectXmlNode(), "MetaData/themes/theme[@theme='" + subBasinTheme + "']/mappings");
        }
        
        NodeList mappingNodes = MmsProjectXml.selectNodes(subBasinThemeMappingNode, "mapping");
        subBasins = new String[mappingNodes.getLength()];
        for (int i = 0; i < mappingNodes.getLength(); i++) {
            subBasins[i] = MmsProjectXml.getElementContent(mappingNodes.item(i), "@modelName", null);
        }
        
        String data_file = mmsWorkspace + "/input/data/" + subBasins[0] + dataFileExtension;
//        espDataDestDir = mmsWorkspace + "/input/data/" + espIODir;
        
/*
 *  Read the stuff for xroute from XML
 */        
        xrouteExecutable = MmsProjectXml.getElementContent(routeNode, "@executable", null);
        xrouteControlFile = MmsProjectXml.getElementContent(routeNode, "@controlFile", null);
        xrouteHeaderDataFile = mmsWorkspace + "/input/data/" + MmsProjectXml.getElementContent(routeNode, "@dataFileHeader", null);
        xrouteVariableName = MmsProjectXml.getElementContent(routeNode, "@variableName", null);
/*
 *  Create the GUI
 */
        
        MmsDataFileReader mdfr = new MmsDataFileReader(data_file);
        if (showRunnerGui) {
            MmsRunSimpleGui gui = new MmsRunSimpleGui(data_file, mdfr.getStart(), mdfr.getEnd(), this);
            WindowFactory.displayInFrame(gui, "Run MMS Model");
        }
    }
    
    @Override
    public void runModel(OuiCalendar runStart, OuiCalendar runEnd) {
/*
         *  Run MMS for each subBasin
         */
        for (String subBasin : subBasins) {
            String datafile = " -set data_file " + mmsWorkspace + "/input/data/" + subBasin + dataFileExtension;
            String statvarfilename = subBasin + ".statvar";
            String statvarfile = " -set nstatVars 1 -set statVar_names " + variableName + " -set statVar_element " + variableIndex + " -set statsON_OFF 1 -set stat_var_file " + statvarfilename;
            String start = " -set start_time " + runStart.getControlFileDateTime();
            String end = " -set end_time " + runEnd.getControlFileDateTime();
            String env = " -batch -E" + mmsWorkspace + "/" + envFile;
            String control = " -C" + subBasin + controlFileExt;
            String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0 -set db_on_off 0";
            String exe = mmsWorkspace + "/" + executable;
            String arg = exe + env + control + datafile + statvarfile + start + end + runoptions;
            System.out.println("MmsOuiMultiDataFileRunTreeNode.runModel: " + arg);
            CommandRunner.runModel(arg, null);
        }

/*
 *  Write Xroute data file
 */
        MmsDataFileReader mdfr = new MmsDataFileReader(xrouteHeaderDataFile);
        String header = "Xroute data generated by OUI" + mdfr.getHeader();

        String xrouteDataFile = mmsWorkspace + "/input/data/" + "xroute.single.data";
        MmsDataFileWriter mdfw = new MmsDataFileWriter(runStart, runEnd, header);
        
        for (String subBasin : subBasins) {
            String statvarfilename = mmsWorkspace + "/output/" + subBasin + ".statvar";
            MmsStatvarReader msr = new MmsStatvarReader(statvarfilename);
            TimeSeries outputTS = msr.getTimeSeries(variableName + " " + variableIndex);
            mdfw.addTrace(outputTS.getVals());
        }
        
        mdfw.write(xrouteDataFile);
        
/*
 *  Run Xroute
 */
        
        String datafile = " -set data_file " + xrouteDataFile;
        String statvarfile = " -set stat_var_file xroute.single.statvar -set statsON_OFF 1";
        String env = " -batch -por -E" + mmsWorkspace + "/" + envFile;
        String control = " -C" + xrouteControlFile;
        String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0 -set db_on_off 0";
        String exe = mmsWorkspace + "/" + xrouteExecutable;
        String arg = exe + env + control + datafile + statvarfile + runoptions;
        
        System.out.println("MmsOuiMultiDataFileRunTreeNode.runModel: " + arg);
        if (showRunnerGui) {
            CommandRunner.runModel(arg, "MmsOuiMultiDataFileRun.runModel: run complete");
        } else {
            CommandRunner.runModel(arg, null);
        }
    }
}
