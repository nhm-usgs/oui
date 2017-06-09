/*
 * MMSAnimationTreeNode.java
 *
 * Created on Wed Sep  8 14:37:50 MDT 2004
 */

package oui.mms.dmi;

import org.omscentral.gis.model.Feature;
import org.w3c.dom.Node;
import oui.treetypes.OuiDataTreeNode;
import oui.mms.MmsProjectXml;

//import com.sun.j3d.utils.applet.MainFrame;
//import oui.an3d.An3dGui;

/** This class contains the necessary information to register a shape/dbf
 * file and an MMS output statvar file with the Object User Interface (OUI).
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
public class MMSAnimationTreeNode extends OuiDataTreeNode {
//    private String mmsWorkspace = null;
//    private MmsProjectXml pxml = null;
    private String dem_name = null;
    private String feature_name = null;
    private String gisout_name = null;
    
    /** Create an MMSStatvarTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public MMSAnimationTreeNode(Node xml_node) {
        super(xml_node);
        _type = "Animation w/MMS GIS Output File";   // overrides OuiTreeNode/OuiShapeTreeNode
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        dem_name = pxml.getElementContent(xml_node, "@demtheme", "no file");
        feature_name = pxml.getElementContent(xml_node, "@featuretheme", "no file");
        gisout_name = pxml.getElementContent(xml_node, "@gisoutfile", "no file");
//        String work_space = pxml.getElementContent(xml_node, "@mmsworkspace", "no workspace");
//        mmsWorkspace = pxml.getMmsWorkspacePath(work_space);
    }
    
    /** Call this when a feature is selected from the OUI map and timeseries data is
     * to be plotted. This method will read the data from an MMS statvar file and plot
     * it in the TimeSeriesTool.
     * @param feature The feature selected from the OUI map.
     */
    public void queryData(Feature feature) {
        String station_name = getStationNameForFeature(feature);
//        System.out.println ("station_name = " + station_name);
        
//        if (!station_name.equals("521400")) return;
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        
        String path = pxml.getPath(pxml.getThemeMetaData(dem_name, "@path", null));
        String dem_file = path + "/" + pxml.getThemeMetaData(dem_name, "@file_name", null);
        
        path = pxml.getPath(pxml.getThemeMetaData(feature_name, "@path", null));
        String feature_file = path + "/" + pxml.getThemeMetaData(feature_name, "@file_name", null);

        String gis_out_file = pxml.getFileFullPath("gisoutfile", gisout_name);
        
//        An3dGui test = new An3dGui(dem_file, feature_file, gis_out_file);
//        test.setVisible(true);
    }
    

}
