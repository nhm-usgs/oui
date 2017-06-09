package oui.mms.mmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.w3c.dom.Node;
import oui.gui.OuiGui;
import oui.mms.MmsProjectXml;
import oui.treetypes.OuiModelTreeNode;
import oui.util.concurrent.CommandRunner;

/**
 *
 * @author  markstro
 */
public class CommandTreeNode extends OuiModelTreeNode {
    private String arg = null;
    
    public CommandTreeNode (Node xml_node) {
        super(xml_node);
        _type = "Execute on the command line";  // overrides OuiTreeNode;
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        arg = pxml.getElementContent(xml_node, "@command", "none");
    }
    
    public void run() {
//        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
//        String arg = pxml.getElementContent(_xml_node, "@command", "none");
        
        try {
            new CommandRunner(arg).evaluate();
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Completed", "Run Status", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getTreeScrollPane()), "Run Unsuccessful\n" + e.getMessage(), "Run Status", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void declare () {}
    public void initialize () {}
    public void cleanup() {}
}
