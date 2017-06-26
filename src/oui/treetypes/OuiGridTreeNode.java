/*
 * OuiGridTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.treetypes;

import org.w3c.dom.Node;
import org.omscentral.gis.model.Theme;
import org.omscentral.gis.model.FPRasterTheme;
import org.omscentral.gis.model.FPRasterModel;
import java.io.IOException;
import org.omscentral.gis.io.ArcGridReader;

/** This class contains the necessary information to register an ascii grid
 * file with the Object User Interface (OUI). Other classes are available for
 * shape files, shape files with various data sources, models, etc.
 * The information describing the grid file and how to display it is read from the
 * XML control file tree.xml in the OUI project directory. Refer to <I>The Object
 * User Interface (OUI): User's Manual</I> for more information.
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
 * "oui.gui.OuiGridTreeNode".<BR>
 * <LI><B>theme</B> (required) is the name of the grid file in the shapes/
 * directory in the project directory.<BR>
 * <LI><B>type</B> (optional) specifies the node type.  The default type is blank.<BR>
 * <LI><B>desc</B> (optional) is a description of the node. The default description is blank.<BR>
 * </UL>
 *
 * @author markstro
 * @version 2.0
 */
public class OuiGridTreeNode extends OuiThemeTreeNode {
    
    /** Create an OuiShapeGridNode.
     * @param n The xml node element which describes this shape/dbf file combo.
     */
    public OuiGridTreeNode(Node n) {
        super(n);
        
        if (_theme_name.equals("no theme")) {
            _type = null;
            _file_name = null;
            System.out.println("error with theme " + _name + ".  No theme specified.");
        } else {
            _type = "ESRI ASCII Grid";   // overrides OuiTreeNode
            _has_map = _file_name != null; // overrides OuiTreeNode
            // overrides OuiTreeNode
//            file_name = new String(Oui.project_dir + "shapes/"
//            + _theme_name + ".dat");
        }
    }
    /** Load the theme for this node for the OUI map.
     * @return The GIS theme for this node.
     */
    @Override
    public Theme loadTheme() {
        FPRasterTheme rt = null;
        
        try {
            ArcGridReader agr = new ArcGridReader(_file_name);
            FPRasterModel fprm = agr.createModel();
            rt = new FPRasterTheme(fprm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rt;
    }
}
