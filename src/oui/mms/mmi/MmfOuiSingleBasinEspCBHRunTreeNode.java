/*
 * MmsOuiMultiBasinEspRunTreeNode.java
 *
 * Created on Dec 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.w3c.dom.Node;
import oui.mms.datatypes.EnsembleData;
import oui.mms.MmsProjectXml;
import oui.mms.io.MmsDataFileReader;
import oui.mms.io.MmsOuiEspReader;
import oui.mms.datatypes.OuiCalendar;
import oui.gui.OuiGui;
import oui.mms.datatypes.TimeSeries;
import oui.mms.io.CbhDateException;

/**
 *
 * @author  markstro
 */
public class MmfOuiSingleBasinEspCBHRunTreeNode extends MmsModelTreeNode implements MmsEspModelRunner {
//    private MmsProjectXml pxml;
    private String dataFileName;
    private int initLength;
    private String data_file;
    private String espDataDestDir;
//    private String[] subBasins;
    private String mmsWorkspace;
    private String executable;
//    private String envFile;
    private String controlFileName;
    private String espIODir;
    private String espVariableName;
    private String espVariableIndex;
//    private String xrouteHeaderDataFile;
//    private String xrouteExecutable;
//    private String xrouteControlFile;
//    private String xrouteEspVariableName;
//    private String[] xrouteEspVariableIndex;
//    private String relativeMmsWorkspace;
    private String paramFileName;
    private String precipFileName;
    private String tmaxFileName;
    private String tminFileName;
    private String mmi = null;
    private OuiCalendar data_start;
    private OuiCalendar data_end;
    private int data_num_dates;
    
    /** Creates a new instance of SingleRunMmi
     * @param xml_node */
    public MmfOuiSingleBasinEspCBHRunTreeNode(Node xml_node) {
        super(xml_node);
        mmi = MmsProjectXml.getElementContent(xml_node, "@mmi", "none");
    }

    @Override
    public void run() {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml(); 
/*
 *  Get the MMI nodes
 */

        Node mmiNode = pxml.getMmiNode(mmi);
        if (mmiNode == null) {
            System.out.println("MmfOuiSingleBasinEspCBHRunTreeNode.run: No mmi node found for " + mmi);
            return;
        }
                
        Node prmsNode = MmsProjectXml.selectSingleNode(mmiNode, "models[@name='prms']");
             
/*
 *  Read the stuff for PRMS from XML
 */        
        dataFileName = MmsProjectXml.getElementContent(prmsNode, "@dataFileName", null);
        espVariableName = MmsProjectXml.getElementContent(prmsNode, "@espVariableName", null);
        espVariableIndex = MmsProjectXml.getElementContent(prmsNode, "@espVariableIndex", null);
        initLength = Integer.parseInt(MmsProjectXml.getElementContent(prmsNode, "@initLength", null));
        espIODir = pxml.getEspIODir(prmsNode, "@espIODir");
        executable = MmsProjectXml.getElementContent(prmsNode, "@executable", null);
        controlFileName = pxml.getFileFullPath("controlFile", MmsProjectXml.getElementContent(prmsNode, "@controlFileName", null));
        paramFileName = pxml.getFileFullPath("parameterfile", MmsProjectXml.getElementContent(prmsNode, "@paramFileName", null));
        precipFileName = pxml.getFileFullPath("precipfile", MmsProjectXml.getElementContent(prmsNode, "@precipFileName", null));
        tmaxFileName = pxml.getFileFullPath("tmaxfile", MmsProjectXml.getElementContent(prmsNode, "@tmaxFileName", null));
        tminFileName = pxml.getFileFullPath("tminfile", MmsProjectXml.getElementContent(prmsNode, "@tminFileName", null));

        mmsWorkspace = pxml.getFilePath("datafile", dataFileName);
        data_file = pxml.getFileFullPath("datafile", dataFileName);
        espDataDestDir = mmsWorkspace + "/input" + espIODir;
        
/*
 *  Create the GUI
 */
        
        MmsDataFileReader mdfr = new MmsDataFileReader(data_file);
        data_start = mdfr.getStart();
        data_end = mdfr.getEnd();
        data_num_dates = mdfr.getDates().length;
        if (showRunnerGui) {
            MmsEspRunSimpleGui gui = new MmsEspRunSimpleGui(data_file, mdfr.getStart(), mdfr.getEnd(), this);
            WindowFactory.displayInFrame(gui, "Run MMS Model in ESP Mode");
        }
    }
    
    @Override
    public void runModel(OuiCalendar forecastStart, OuiCalendar forecastEnd) {
/*
 *  Generate the ESP run and save to XML file
 */
        EnsembleData ed = new EnsembleData("esp", null, null, null);
        MmsOuiEspRun.writeEspDataFiles(ed, initLength, data_file, espDataDestDir, forecastStart, forecastEnd);

        if (precipFileName != null) {
            ArrayList<TimeSeries> cbhPrecip = new ArrayList<>();
            try {
                MmsOuiEspRun.writeEspCBHFiles(ed, cbhPrecip, initLength,
                        precipFileName, espDataDestDir, forecastStart, forecastEnd,
                        data_start, data_end, data_num_dates);
            } catch (CbhDateException e) {
                JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()),
                        "ESP Run Unsuccessful\nDates in " + precipFileName + "\nDon't match dates in " + data_file,
                        "Run Status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ed.setCbhPrecip(cbhPrecip);
        }

        if (tmaxFileName != null) {
            ArrayList<TimeSeries> cbhTmax = new ArrayList<>();
            try {
                MmsOuiEspRun.writeEspCBHFiles(ed, cbhTmax, initLength, tmaxFileName,
                        espDataDestDir, forecastStart, forecastEnd,
                        data_start, data_end, data_num_dates);
            } catch (CbhDateException e) {
                JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()),
                        "ESP Run Unsuccessful\nDates in " + tmaxFileName + "\nDon't match dates in " + data_file,
                        "Run Status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ed.setCbhTmax(cbhTmax);
        }

        if (tminFileName != null) {
            ArrayList<TimeSeries> cbhTmin = new ArrayList<>();
            try {
                MmsOuiEspRun.writeEspCBHFiles(ed, cbhTmin, initLength, tminFileName,
                        espDataDestDir, forecastStart, forecastEnd,
                        data_start, data_end, data_num_dates);
            } catch (CbhDateException e) {
                JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()),
                        "ESP Run Unsuccessful\nDates in " + tminFileName + "\nDon't match dates in " + data_file,
                        "Run Status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ed.setCbhTmin(cbhTmin);
        }

        
        ed.setName("esp");
        
        MmsOuiEspRun.runEsp(ed, mmsWorkspace, executable, controlFileName,
                espIODir, espVariableName, espVariableIndex, mmsWorkspace, paramFileName);

        JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Completed", "Run Status", JOptionPane.INFORMATION_MESSAGE);

        MmsOuiEspReader.readEsp(ed);

        String xml_file = mmsWorkspace + "/output/" + espIODir + "/esp.xml";
        ed.save(xml_file);
    }
}
