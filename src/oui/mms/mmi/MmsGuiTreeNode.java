/*
 * MmsSingleBasinRun.java
 *
 * Created on July 16, 2004, 10:08 AM
 */
package oui.mms.mmi;

import org.w3c.dom.Node;
import oui.mms.MmsProjectXml;
import oui.mms.gui.Mms;
import oui.treetypes.OuiModelTreeNode;

/**
 *
 * @author  markstro
 */
public class MmsGuiTreeNode extends OuiModelTreeNode {
//    private MmsProjectXml pxml = null;
//    private String dataFilePath = null;
    private String mmi = null;
    private String file_name = null;

    public MmsGuiTreeNode(Node xml_node) {
        super(xml_node);
        _type = "MMS Model GUI";  // overrides OuiTreeNode;
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();

        mmi = MmsProjectXml.getElementContent(xml_node, "@mmi", "none");
        String name = MmsProjectXml.getElementContent(xml_node, "@controlFile", null);
        file_name = pxml.getFileFullPath("controlFile", name);
    }

    @Override
    public void run() {

        
//        String dataFile = pxml.getMmiFile(mmi, "@data");
//        dataFilePath = pxml.getFileFullPath("datafile", dataFile);
//        
//        MmsDataFileReader mdfr = new MmsDataFileReader(dataFilePath);
//        ModelDateTime data_file_start = mdfr.getStart();
//        ModelDateTime data_file_end = mdfr.getEnd();
        
//        String name = pxml.getElementContent(this._xml_node, "@controlFile", null);
//        String file_name = pxml.getFileFullPath("controlFile", name);
        new Mms(file_name, false);
    }

//    public void runModel(ModelDateTime queryStart, ModelDateTime queryEnd) {
//        String envFile = pxml.getMmiFile(mmi, "@env");
//        String env = " -E" + pxml.getFileFullPath("envfile", envFile);
//        String control = " -C" + pxml.getMmiFile(mmi, "@control");
//        String statvar = " -set stat_var_file " + pxml.getMmiFile(mmi, "@statvar");
//        String paramFile = pxml.getMmiFile(mmi, "@param");
//        String param = " -set param_file " + pxml.getFileFullPath("parameterfile", paramFile);
//        String data = " -set data_file " + dataFilePath;
//        String executable = pxml.getMmiPath (mmi) + "/" + pxml.getMmiFile(mmi, "@executable");
//        String runoption = " " + pxml.getElementContent(_xml_node, "@options", "");
//        String start_time = " -set start_time " + queryStart.getControlFileDateTime();
//        String end_time = " -set end_time " + queryEnd.getControlFileDateTime();        
//        String arg = executable + " -batch" + env + control + data + param + start_time + end_time + statvar + " -set statsON_OFF 1 " + runoption;        
//        System.out.println("MmsRun: executing = " + arg);
//        
//        try {
//            new CommandRunner(arg).evaluate();
//            JOptionPane.showMessageDialog(OuiGui.getTreeScrollPane(), "Run Completed", "Run Status", JOptionPane.INFORMATION_MESSAGE);
//        } catch (IOException e) {
//            JOptionPane.showMessageDialog(OuiGui.getTreeScrollPane(), "Run Unsuccessful\n" + e.getMessage(), "Run Status", JOptionPane.ERROR_MESSAGE);
//        }
//    }
    
    @Override
    public void cleanup() {}
    @Override
    public void declare() {}
    @Override
    public void initialize() {}
    
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
////        LocalProjectXml xml = (LocalProjectXml) (LocalProjectXml.OuiProjectXmlFactory(
////                "D:\\Jaquish_Documents\\Programming\\OUI\\projects\\rio_grande\\rio_grande.xml"));
////        String xql = "node[@name='Models']/node[@name='Update MMS Data File']";
////
////        Node node = xml.selectSingleNode(xml.getTreeXmlNode(), xql);
////
////        SingleRunMmi foo = new SingleRunMmi(node);
////        foo.declare();
////        foo.initialize();
////        foo.run();
////        foo.cleanup();
//    }
}
