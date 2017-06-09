/*
 * MmsSingleBasinRun.java
 *
 * Created on July 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.w3c.dom.Node;
import oui.gui.Oui;
import oui.gui.OuiGui;
import oui.mms.MmsProjectXml;
import oui.mms.io.MmsDataFileReader;
import oui.treetypes.OuiModelTreeNode;
import oui.mms.datatypes.OuiCalendar;
import oui.util.concurrent.CommandRunner;

/**
 *
 * @author  markstro
 */
public class MmfSingleBasinRunTreeNode extends OuiModelTreeNode implements MmsSingleModelRunner {
    private static final Logger logger = Logger.getLogger(MmfSingleBasinRunTreeNode.class.getName());
    private String dataFilePath = null;
    private String paramFilePath = null;
    private String controlFilePath = null;
    private String statvarFilePath = null;
    private String mmi = null;
    private String variableName = null;
    private String variableIndex = null;
    private String executable = null;
    
    public MmfSingleBasinRunTreeNode(Node xml_node) {
        super(xml_node);
        logger.setLevel(Oui.ouiLogLevel);

        _type = "Single MMS Model";  // overrides OuiTreeNode;

        mmi = MmsProjectXml.getElementContent(xml_node, "@mmi", "none");
        logger.info("loaded");
    }

    public void run() {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        Node mmiNode = pxml.getMmiNode(mmi);
        if (mmiNode == null) {
            logger.log(Level.SEVERE, "No mmi node found for {0}", mmi);
            return;
        }
                
        Node prmsNode = MmsProjectXml.selectSingleNode(mmiNode, "models[@name='prms']");
        if (prmsNode == null) {
            logger.log(Level.SEVERE, "Looking for attribute \"models[@name=\'prms\']\" in mmi node {0}", mmi);
        }
        
        String dataFile = MmsProjectXml.getElementContent(prmsNode, "@dataFileName", null);
        if (dataFile == null) {
            logger.log(Level.SEVERE, "Looking for attribute \"@dataFileName\" in mmi node {0}", mmi);
        }

        dataFilePath = pxml.getFileFullPath("datafile", dataFile);
        if (dataFilePath == null) {
            logger.log(Level.SEVERE, "Can't find full path for {0}", dataFile);
        }

        String paramFile = MmsProjectXml.getElementContent(prmsNode, "@paramFileName", null);
        if (paramFile == null) {
            logger.log(Level.SEVERE, "Looking for attribute \"@paramFileName\" in PRMS mmi node {0}", mmi);
        }

        paramFilePath = pxml.getFileFullPath("parameterfile", paramFile);
        if (paramFilePath == null) {
            logger.log(Level.SEVERE, "Can't find full path for {0}", paramFile);
        }
        
        String controlFile = MmsProjectXml.getElementContent(prmsNode, "@controlFileName", null);
        if (controlFile == null) {
            logger.log(Level.SEVERE, "Looking for attribute \"@controlFileName\" in PRMS mmi node {0}", mmi);
        }

        controlFilePath = pxml.getFileFullPath("controlFile", controlFile);
        if (controlFilePath == null) {
            logger.log(Level.SEVERE, "Can't find full path for {0}", controlFile);
        }
        
        String statvarFile = MmsProjectXml.getElementContent(prmsNode, "@statvarFileName", null);
        if (statvarFile == null) {
            logger.log(Level.SEVERE, "Looking for attribute \"@statvarFileName\" in PRMS mmi node {0}", mmi);
        }
        statvarFilePath = pxml.getFileFullPath("statvarfile", statvarFile);
        if (statvarFilePath == null) {
            logger.log(Level.SEVERE, "Can't find full path for {0}", statvarFile);
        }
        
        variableName = MmsProjectXml.getElementContent(prmsNode, "@variableName", null);
//        if (variableName == null) {
//            logger.log(Level.SEVERE, "Looking for attribute \"@variableName\" in PRMS mmi node {0}", mmi);
//        }

        variableIndex = MmsProjectXml.getElementContent(prmsNode, "@variableIndex", null);
//        if (variableIndex == null) {
//            logger.log(Level.SEVERE, "Looking for attribute \"@variableIndex\" in PRMS mmi node {0}", mmi);
//        }

        executable = MmsProjectXml.getElementContent(prmsNode, "@executable", null);
        if (executable == null) {
            logger.log(Level.SEVERE, "Looking for attribute \"@executable\" in PRMS mmi node {0}", mmi);
        }
        
        MmsDataFileReader mdfr = new MmsDataFileReader(dataFilePath);
        if (mdfr == null) {
            logger.log(Level.SEVERE, "Couldn't read Data File {0}", dataFilePath);
        }

        OuiCalendar data_file_start = mdfr.getStart();
        OuiCalendar data_file_end = mdfr.getEnd();

        MmsSingleBasinRunGui gui = new MmsSingleBasinRunGui(dataFilePath, data_file_start, data_file_end, this);
        WindowFactory.displayInFrame(gui, "Run MMS Model");
    }

    public void runModel(OuiCalendar queryStart, OuiCalendar queryEnd) {
        String datafile = " -set data_file " + dataFilePath;
        String paramfile = " -set param_file " + paramFilePath;

        String statvarfile = "";
        if (variableName != null) {
            statvarfile = " -set nstatVars 1 -set statVar_names " + variableName + " -set statVar_element " + variableIndex + " -set statsON_OFF 1 -set stat_var_file " + statvarFilePath;
        }

        String start = " -set start_time " + queryStart.getControlFileDateTime();
        String end = " -set end_time " + queryEnd.getControlFileDateTime();
        String control = " -C" + controlFilePath;
        String runoptions = " -set init_vars_from_file 0 -set save_vars_to_file 0 -set gisOutON_OFF 0";
        
        String arg = executable + control + datafile + paramfile + statvarfile + start + end + runoptions;

        logger.log(Level.INFO, "Running: {0}", arg);

        try {
            new CommandRunner(arg).evaluate();
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Completed", "Run Status", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Unsuccessful\n" + e.getMessage(), "Run Status", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void cleanup() {}
    public void declare() {}
    public void initialize() {}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        LocalProjectXml xml = (LocalProjectXml) (LocalProjectXml.OuiProjectXmlFactory(
//                "D:\\Jaquish_Documents\\Programming\\OUI\\projects\\rio_grande\\rio_grande.xml"));
//        String xql = "node[@name='Models']/node[@name='Update MMS Data File']";
//
//        Node node = xml.selectSingleNode(xml.getTreeXmlNode(), xql);
//
//        SingleRunMmi foo = new SingleRunMmi(node);
//        foo.declare();
//        foo.initialize();
//        foo.run();
//        foo.cleanup();
    }
}
