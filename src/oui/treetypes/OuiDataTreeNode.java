/*
 * OuiDataTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.treetypes;

import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorTheme;
import org.w3c.dom.Node;
import oui.gui.OuiGui;
import oui.mms.OuiProjectXml;

/** This abstract class allows a data file and tools for viewing the data file
 * to be coupled with shape/dbf file in the Object User Interface (OUI).
 * The information describing the shape file and how to display it is read from the
 * XML control file tree.xml in the OUI project directory. Refer to <I>The Object
 * User Interface (OUI): User's Manual</I> for more information.
 *
 * @author markstro
 * @version 2.0
 */
public abstract class OuiDataTreeNode extends OuiShapeTreeNode {
    Boolean in_query_mode = false;
    String _id_attribute = null;
    String _label_attribute = null;
//    Node _dmiNode = null;
    String _dimNodeName = null;
    
    /** Create an OuiDataTreeNode object.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     */
    public OuiDataTreeNode(Node xml_node) {
        super(xml_node);
        
        OuiProjectXml pxml = OuiProjectXml.getOuiProjectXml();
        _has_data = true;   // overrides OuiTreeNode
        _id_attribute = pxml.getThemeMetaData (_theme_name, "@idAttribute", "");
        _dimNodeName = xml_node.getParentNode().getNodeName();
        _label_attribute = pxml.getThemeMetaData (_theme_name, "@nameAttribute", "");
//        _dmiNode = xml_node.getParentNode();
    }
    
    /** Set the query state for this node. When the query mode is on for the node,
     * its features can be selected from the OUI map and passed into a visualization tool.
     * @param query_mode Turn the query mode of the theme on or off.
     */
    public void setQueryMode(Boolean query_mode) {
        in_query_mode = query_mode;
        if (in_query_mode) {
            OuiGui.getLoadedPanel().setQueryNode(this);
        } else {
            OuiGui.getLoadedPanel().setQueryNode(null);
        }
    }
    
   /**
     * Getter for property dmiNode.
     * @return Value of property dmiNode.
     */
    public Node getDmiNode() {
        OuiProjectXml pxml = OuiProjectXml.getOuiProjectXml();
        return OuiProjectXml.selectSingleNode(pxml.getProjectXmlNode(), _dimNodeName);
    }
    
    public String getStationNameForFeature(Feature feature) {
        return (((VectorTheme)_theme).getAttribute(feature, _id_attribute)).toString();
    }
    
    public String getLabelForFeature(Feature feature) {
        return (((VectorTheme) _theme).getAttribute(feature, _label_attribute)).toString();
    }
    
    /** Return the query mode state for this node.
     * @return Is this node in query mode?
     */
    public Boolean isInQueryMode() {return in_query_mode;}
    
    /** Extending classes must implement what to do when a feature is selected on the OUI map.
     * @param feature The selected feature from the OUI map.
     */
    public abstract void queryData(Feature feature);

    // This method called when the "Query" check box in the the "Loaded Themes"
    // panel is checked for this theme.
    public void queryNotification() {
    }

    public String getIdAttribute () {
        return _id_attribute;
    }
    
}
