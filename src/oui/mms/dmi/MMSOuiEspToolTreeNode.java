/*
 * MMSEspToolTreeNode.java
 *
 * Created on December 16, 2004, 2:11 PM
 */

package oui.mms.dmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import javax.swing.JOptionPane;
import org.omscentral.gis.model.Feature;
import org.w3c.dom.Node;
import oui.mms.datatypes.EnsembleData;
import oui.esptool.EspTool;
import oui.mms.MmsProjectXml;
import oui.treetypes.OuiDataTreeNode;

/** This class contains the necessary information to register a shape/dbf
 * file and an MMS input data file with the Object User Interface (OUI).
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
public class MMSOuiEspToolTreeNode extends OuiDataTreeNode {
    private String mmsWorkspace;
//    private MmsProjectXml pxml;
    private String espDir = null;
    private String espDmiClassName= null;
    private String espVariable= null;
//    private Node espDmiNode= null;
    private String espDmiNodeName = null;


    /** Create an MMSDataTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public MMSOuiEspToolTreeNode(Node xml_node) {
        super(xml_node);
        _type = "Shape w/MMS ESP XML File";   // overrides OuiTreeNode/OuiShapeTreeNode
        
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        mmsWorkspace = pxml.getPath(xml_node);
        espDir = MmsProjectXml.getElementContent (xml_node, "@espDir", null);
        espDmiClassName = MmsProjectXml.getElementContent (xml_node, "@espDmiClass", null);
        espVariable = MmsProjectXml.getElementContent (xml_node, "@espVariable", null);
        
        if (espDmiClassName != null) {
             espDmiNodeName = MmsProjectXml.getElementContent(xml_node, "@espDmi", null);
//            espDmiNode = pxml.getDmiNode(espDmiNodeName);
        }
    }
    
    /** Call this when a feature is selected from the OUI map and timeseries data is
     * to be plotted. This method will read the data from an MMS input file and plot
     * it in the TimeSeriesTool.
     * @param feature The feature selected from the OUI map.
     */
    public void queryData (Feature feature) {
        String stationName = getStationNameForFeature(feature);
        String subBasin = MmsProjectXml.getMmsProjectXml().getModelNameForThemeId(this._theme_name, stationName);
        String title = subBasin;

        if (subBasin == null) { // If subBasin is null, then there is only one ESP run and the file is called esp.xml
            subBasin = "esp";
            title = getLabelForFeature(feature);
        }
        String xml_file = mmsWorkspace + espDir + subBasin + ".xml";
        EnsembleData ed;
        if (espVariable != null) {
            ed = EnsembleData.load(xml_file, espVariable + " " + stationName);
        } else {
            ed = EnsembleData.load(xml_file);
        }

// Not sure what is going on, but will hack for now
//        System.out.println ("ed.name = " + ed.getName());
      if (ed.getName().contentEquals("esp")) {
          ed.setName (title + ": " + espVariable);
      }

        if (ed != null) {
            EspTool et = new EspTool(ed, title);
            et.setEspDmiClassName(espDmiClassName);
            et.setEspDmiNode(MmsProjectXml.getMmsProjectXml().getDmiNode(espDmiNodeName));
            WindowFactory.displayInFrame(et, title);
        } else {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(null), "The esp file " + xml_file + " could not be loaded.  Have you run ESP?", "ESP Tool File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
