/*
 * MMSParameterTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.mms.dmi;

import java.io.IOException;
import oui.mms.io.MmsParamsReader;
import org.w3c.dom.Node;
import oui.gui.OuiGui;
import oui.treetypes.OuiShapeTreeNode;

/** This class contains the necessary information to register a shape/dbf
 * file and an MMS parameter file with the Object User Interface (OUI).
 * The information describing the shape file and how to display it is read from the
 * XML control file tree.xml in the OUI project directory. Refer to <I>The Object
 * User Interface (OUI): User's Manual</I> for more information.
 * <P>
 * The element in tree.xml must look like this:<BR>
 * <CODE>&lt;node name="Regions" class="oui.gui.OuiShapeTreeNode" theme="regions" nameAttribute="NAME" fillColor="green" borderColor="black"/&gt;</CODE>
 * <P>
 * The element tag must be <B>node</B>.
 * <P>
 * The element has the following attributes. Some are required and are specified as
 * such. If an optional attribute is not specified in the element, a default value
 * is supplied by this class.
 * <P>
 * Attributes:<BR>
 *<UL>
 * <LI><B>name</B> (required) is the name as it appeares in the OUI project tree.<BR>
 * <LI><B>class</B> (required) specifies this class.  It must be set to
 * "oui.gui.OuiShapeTreeNode".<BR>
 * <LI><B>theme</B> (required) is the name of the shape/dbf file in the shapes/
 * directory in the project directory.<BR>
 * <LI><B>nameAttribute</B> (optional) is the name of the column in the dbf file to
 * use for labels on the OUI map.<BR>
 * <LI><B>fillColor</B> (optional) is the color to use to fill in the shapes on the
 * OUI map. The default fillColor is clear (no fill).<BR>
 * <LI><B>borderColor</B> (optional) is the color to use to outline the shapes on
 * the OUI map. The default borderColor is black.<BR>
 * <LI><B>color</B> (optional) is the color to use for filling and outlining shapes,
 * or to draw lines and points. The default color is black.<BR>
 * <LI><B>type</B> (optional) specifies the node type.  The default type is blank.<BR>
 * <LI><B>desc</B> (optional) is a description of the node. The default description is blank.<BR>
 *</UL>
 * <P>
 * The following colors are valid: black, blue, cyan, darkGray, gray, green,
 * lightGray, magenta, orange, pink, red, white, yellow, and clear.
 *
 *
 * @author markstro
 * @version 2.0
 */
public class FooMMSParameterTreeNode extends OuiShapeTreeNode {
    MmsParamsReader mms_param_reader = null;
    
    /** Create an MMSParameterTree Node object.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public FooMMSParameterTreeNode(Node xml_node) {
        super(xml_node);
        _type = "ESRI Shape File with MMS Parameters";   // overrides OuiTreeNode
    }

    /** Show the MMS parameters for this node.
     * @param is_table_shown Turn the table on or off.
     */
    public void showTable(Boolean is_table_shown) {
        if (is_table_shown.booleanValue()) {
            if (mms_param_reader == null) {
                try {
                    mms_param_reader = new MmsParamsReader("/home/oui_projects/new_rio_grande/riogr_mms_work/input/params/DelNorte.xprms_xyz.params");
                } catch (IOException e) {
                    mms_param_reader = null;
                    System.out.println ("problem reading /home/oui_projects/new_rio_grande/riogr_mms_work/input/params/DelNorte.xprms_xyz.params");
                }
            }
            
        } else {
            mms_param_reader = null;
        }
    }
}
