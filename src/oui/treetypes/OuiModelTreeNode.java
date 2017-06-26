/*
 * OuiModelTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.treetypes;

import org.w3c.dom.Node;

/**This abstract class is a template to register an executable model
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
 * <LI><B>type</B> (optional) specifies the node type.  The default type is blank.<BR>
 * <LI><B>desc</B> (optional) is a description of the node. The default description is blank.<BR>
 * </UL>
 * @author markstro
 * @version 2.0
 */
public abstract class OuiModelTreeNode extends OuiTreeNode {
    
    /** Create an OuiModelTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     */
    public OuiModelTreeNode(Node xml_node) {
        super(xml_node);
        _has_model = true;   // overrides OuiTreeNode
    }
    
    /** Extending classes must implement the code that performs the declare function for the model.
     */
    public abstract void declare();
    
    /** Extending classes must implement the code that performs the initialize function for the model.
     */
    public abstract void initialize();
    
    /** Extending classes must implement the code that performs the run function for the model.
     */
    public abstract void run();
    
    /** Extending classes must implement the code that performs the cleanup function for the model.
     */
    public abstract void cleanup();
}
