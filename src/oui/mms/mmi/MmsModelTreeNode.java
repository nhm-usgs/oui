/*
 * OuiModelTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.mms.mmi;

import javax.swing.JFrame;
import org.w3c.dom.Node;
import oui.treetypes.OuiModelTreeNode;

/**This class is a template to register an MMS model
 * with the Object User Interface (OUI). The information describing the model
 * and how to run it is read from the XML control file tree.xml in the OUI
 * project directory. Refer to <I>The Object User Interface (OUI): User's
 * Manual</I> for more information.
 * <P>
 * The element in tree.xml must look like this:<BR>
 * <CODE>&lt;node name="Elevation" class="oui.gui.OuiGridTreeNode" theme="evfi1000"/&gt;</CODE>
 * <P>
 * The element tag must be <B>node</B>.
 * <P>
 * The element has the following attributes. Some are required and are specified as
 * such. If an optional attribute is not specified in the element, a default value
 * is supplied by this class.
 * <P>
 * Attributes:<BR>
 * <UL>
 * <LI><B>name</B> (required) is the name as it appeares in the OUI project tree.<BR>
 * <LI><B>class</B> (required) specifies this class.  It must be set to
 * the implementing model class.<BR>
 * <LI><B>desc</B> (optional) is a description of the node. The default description is blank.<BR>
 * </UL>
 * @author markstro
 * @version 2.0
 */
public class MmsModelTreeNode extends OuiModelTreeNode {
    JFrame runnerGui;
    boolean showRunnerGui = true;
    
    /**
     * Create an MmsModelTreeNode.
     * 
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     */
    public MmsModelTreeNode(Node xml_node) {
        super(xml_node);
        
        _type = "MMS Model";  // overrides OuiTreeNode;
//        System.out.println("MmsModelTreeNode: constructor");
    }
    
    /** Extending classes must implement the code that performs the declare function for the model.
     */
    @Override
    public void declare() {
//        System.out.println("MmsModelTreeNode: declare");
    }
    
    /** Extending classes must implement the code that performs the initialize function for the model.
     */
        
    @Override
    public void initialize() {
//        System.out.println("MmsModelTreeNode: initialize");
    }
    
    /** Extending classes must implement the code that performs the run function for the model.
     */
    @Override
    public void run() {
//        System.out.println("running model");
    }
    
    /** Extending classes must implement the code that performs the cleanup function for the model.
     */
    @Override
    public void cleanup() {
//        System.out.println("MmsModelTreeNode: cleanup");
    }
    
    public void setShowRunnerGui (boolean showRunnerGui) {
        this.showRunnerGui = showRunnerGui;
    }
    
    public boolean getShowRunnerGui() {
        return this.showRunnerGui;
    }
}
