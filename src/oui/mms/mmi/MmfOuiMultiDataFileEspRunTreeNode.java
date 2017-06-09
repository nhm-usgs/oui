/*
 * MmfOuiMultiBasinEspRunTreeNode.java
 *
 * Created on Dec 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import oui.mms.datatypes.EnsembleData;
import oui.gui.OuiGui;
import oui.mms.MmsProjectXml;
import oui.mms.gui.Mms;
import oui.mms.io.MmsDataFileReader;
import oui.mms.io.MmsOuiEspReader;
import oui.mms.datatypes.OuiCalendar;

/**
 *
 * @author  markstro
 */
public class MmfOuiMultiDataFileEspRunTreeNode extends MmsModelTreeNode implements MmsEspModelRunner {
//    private MmsProjectXml pxml;
    private String dataFileExtension;
    private int initLength;
//    private String data_file;
    private String espDataDestDir;
    private String[] subBasins;
    private String mmsWorkspace;
    private String executable;
    private String envFile;
    private String controlFileExt;
    private String espIODir;
    private String espVariableName;
    private String espVariableIndex;
    private String xrouteHeaderDataFile;
    private String xrouteExecutable;
    private String xrouteControlFile;
    private String xrouteEspVariableName;
    private String[] xrouteEspVariableIndex;
    private String relativeMmsWorkspace;
    private String paramFileExt;
    private String mmi = null;
    
    /** Creates a new instance of SingleRunMmi */
    public MmfOuiMultiDataFileEspRunTreeNode(Node xml_node) {
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
        relativeMmsWorkspace = "./" + mmsWorkspace.substring(Mms.fileNameIndex(mmsWorkspace) + 1, mmsWorkspace.length());
        dataFileExtension = MmsProjectXml.getElementContent(prmsNode, "@dataFileExt", null);
        espVariableName = MmsProjectXml.getElementContent(prmsNode, "@espVariableName", null);
        espVariableIndex = MmsProjectXml.getElementContent(prmsNode, "@espVariableIndex", null);
        initLength = Integer.parseInt(MmsProjectXml.getElementContent(prmsNode, "@initLength", null));
        espIODir = pxml.getEspIODir(prmsNode, "@espIODir");
        executable = MmsProjectXml.getElementContent(prmsNode, "@executable", null);
        envFile = MmsProjectXml.getElementContent(prmsNode, "@envFile", null);
        controlFileExt = MmsProjectXml.getElementContent(prmsNode, "@controlFileExt", null);
        paramFileExt = MmsProjectXml.getElementContent(prmsNode, "@paramFileExt", null);

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
        espDataDestDir = mmsWorkspace + "/input/data/" + espIODir;
        
/*
 *  Read the stuff for xroute from XML
 */        
        xrouteExecutable = MmsProjectXml.getElementContent(routeNode, "@executable", null);
        xrouteControlFile = MmsProjectXml.getElementContent(routeNode, "@controlFile", null);
        xrouteHeaderDataFile = mmsWorkspace + "/input/data/" + MmsProjectXml.getElementContent(routeNode, "@dataFileHeader", null);
        xrouteEspVariableName = MmsProjectXml.getElementContent(routeNode, "@espVariableName", null);
/*
 *  Get the esp variable indicies
 */
        String forecastNodeTheme = MmsProjectXml.getElementContent(routeNode, "@theme", null);
        Node forecastThemeMappingNode = MmsProjectXml.selectSingleNode(pxml.getProjectXmlNode(), "MetaData/gis/themes/theme[@theme='" + forecastNodeTheme + "']/mappings");
        
        // DANGER hack
        if (forecastThemeMappingNode == null) {
            forecastThemeMappingNode = MmsProjectXml.selectSingleNode(pxml.getProjectXmlNode(), "MetaData/themes/theme[@theme='" + forecastNodeTheme + "']/mappings");
        } 
        
        mappingNodes = MmsProjectXml.selectNodes(forecastThemeMappingNode, "mapping");
        xrouteEspVariableIndex = new String[mappingNodes.getLength()];
        for (int i = 0; i < mappingNodes.getLength(); i++) {
            xrouteEspVariableIndex[i] = MmsProjectXml.getElementContent(mappingNodes.item(i), "@modelId", null);
        }
/*
 *  Create the GUI
 */
        
        MmsDataFileReader mdfr = new MmsDataFileReader(data_file);
        if (showRunnerGui) {
            MmsEspRunSimpleGui gui = new MmsEspRunSimpleGui(data_file, mdfr.getStart(), mdfr.getEnd(), this);
            WindowFactory.displayInFrame(gui, "Run MMS Model in ESP Mode");
        }
    }
    
    public void runModel(OuiCalendar forecastStart, OuiCalendar forecastEnd) {
/*
 *  Generate the ESP run and save to XML file
 */
        for (int i = 0; i < subBasins.length; i++) {
            EnsembleData ed = new EnsembleData(subBasins[i], null, null, null);
            String data_file = mmsWorkspace + "/input/data/" + subBasins[i] + dataFileExtension;

            MmsOuiEspRun.writeEspDataFiles(ed, initLength, data_file, espDataDestDir, forecastStart, forecastEnd);
            
            ed.setName(subBasins[i]);
            
            MmsOuiEspRun.runEsp(ed, mmsWorkspace, executable, envFile, controlFileExt,
                    espIODir, espVariableName, espVariableIndex, relativeMmsWorkspace, paramFileExt);
            
            MmsOuiEspReader.readEsp(ed);
            
            String xml_file = mmsWorkspace + "/output/" + espIODir + "/" + subBasins[i] + ".xml";
            ed.save(xml_file);
        }
/*
 *  Load ESP run into EnsembleData
 */
        
        ArrayList<EnsembleData> localEnsembleData = new ArrayList<EnsembleData>(subBasins.length);
        
        for (int i = 0; i < subBasins.length; i++) {
            String xml_file = mmsWorkspace + "/output/" + espIODir + "/" + subBasins[i] + ".xml";;
            EnsembleData ed = EnsembleData.load(xml_file);
            localEnsembleData.add(i, ed);
        }
/*
 *  Xroute stuff
 */
        ArrayList routedEnsembleData = MmsOuiMultiBasinEspRun.writeXrouteDataFiles(localEnsembleData, espDataDestDir, xrouteHeaderDataFile, subBasins);
        
        MmsOuiMultiBasinEspRun.runXrouteEsp(routedEnsembleData, mmsWorkspace, xrouteExecutable, envFile, xrouteControlFile, espIODir, xrouteEspVariableName, xrouteEspVariableIndex, relativeMmsWorkspace);
        
        MmsOuiMultiBasinEspRun.readXrouteEsp(routedEnsembleData);
        
        Iterator it = routedEnsembleData.iterator();
        while (it.hasNext()) {
            EnsembleData ed = (EnsembleData)(it.next());
            String xml_file = mmsWorkspace + "/output/" + espIODir + "/" + ed.getName() + ".xml";
            ed.save(xml_file);
        }
        
        if (showRunnerGui) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "MmfOuiMultiDataFileEspRunTreeNode: run complete", "Run Status", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
