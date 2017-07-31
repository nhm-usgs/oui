/*
 * MmsSingleBasinRun.java
 *
 * Created on July 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.w3c.dom.Node;
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
public class MmsSingleBasinRun extends OuiModelTreeNode implements MmsSingleModelRunner {
//    private MmsProjectXml pxml = null;
    private String dataFilePath = null;
    private String mmi = null;
    private String runoption = null;

    public MmsSingleBasinRun(Node xml_node) {
        super(xml_node);
        _type = "Single MMS Model";  // overrides OuiTreeNode;
        mmi = MmsProjectXml.getElementContent(xml_node, "@mmi", "none");
        runoption = " " + MmsProjectXml.getElementContent(xml_node, "@options", "");
    }

    @Override
    public void run() {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        
        String dataFile = pxml.getMmiFile(mmi, "@dataFileName");
        dataFilePath = pxml.getFileFullPath("datafile", dataFile);
        
        MmsDataFileReader mdfr = new MmsDataFileReader(dataFilePath);
        OuiCalendar data_file_start = mdfr.getStart();
        OuiCalendar data_file_end = mdfr.getEnd();

        MmsSingleBasinRunGui gui = new MmsSingleBasinRunGui(dataFilePath, data_file_start, data_file_end, this);
        WindowFactory.displayInFrame(gui, "Run MMS Model");
    }

    @Override
    public void runModel(OuiCalendar queryStart, OuiCalendar queryEnd) {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        
        String envFile = pxml.getMmiFile(mmi, "@env");
        String env = "-E" + pxml.getFileFullPath("envfile", envFile);
        String control = " -C" + pxml.getMmiFile(mmi, "@control");
        String statvar = " -set stat_var_file " + pxml.getMmiFile(mmi, "@statvar");
        String paramFile = pxml.getMmiFile(mmi, "@param");
        String param = " -set param_file " + pxml.getFileFullPath("parameterfile", paramFile);
        String data = " -set data_file " + dataFilePath;
        String executable = pxml.getMmiPath (mmi) + "/" + pxml.getMmiFile(mmi, "@executable");
//        String runoption = " " + pxml.getElementContent(_xml_node, "@options", "");
        String start_time = " -set start_time " + queryStart.getControlFileDateTime();
        String end_time = " -set end_time " + queryEnd.getControlFileDateTime();        
        String arg = executable + " -batch" + env + control + data + param + start_time + end_time + statvar + " -set statsON_OFF 1 " + runoption;        
        System.out.println("MmsRun: executing = " + arg);
        
        try {
            new CommandRunner(arg).evaluate();
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Completed", "Run Status", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Unsuccessful\n" + e.getMessage(), "Run Status", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void cleanup() {}
    @Override
    public void declare() {}
    @Override
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
