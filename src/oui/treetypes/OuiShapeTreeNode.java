/*
 * OuiShapeTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.treetypes;

import gov.usgs.cawsc.gui.WindowFactory;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import org.w3c.dom.Node;
import org.omscentral.gis.model.Theme;
import org.omscentral.gis.ui.panel.AbstractVectorThemeColorModel;
import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.io.ShpFileParser;
import org.omscentral.gis.model.VectorModel;
import java.io.FileNotFoundException;
import java.util.HashMap;
import oui.gui.OuiGui;
import oui.mms.OuiProjectXml;
import oui.util.dbf.OuiAttributeModel;
import oui.util.dbf.OuiDbfFileParser;
import oui.util.dbf.OuiDbfAttributeEditor;

/** This class contains the necessary information to register a shape and dbf
 * file with the Object User Interface (OUI). Other classes are available for
 * ascii grid files, shape files with various data sources, models, etc.
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
public class OuiShapeTreeNode extends OuiThemeTreeNode {
    protected String shape_file_name;
    protected String  dbf_file_name;
    protected String label_attribute, fill_color_name, border_color_name;
    protected int label_index;
    protected Boolean labels_displayed = false;
    protected OuiDbfAttributeEditor table_frame = null;
    protected OuiAttributeModel attribute_model;

    static HashMap<String,Color> fill_colors = null;
    static HashMap<String,Color> border_colors = null;

    /**
     * Holds value of property vectorModel.
     */
    private VectorModel vectorModel;
    
    /** Create an OuiShapeTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public OuiShapeTreeNode(Node xml_node) {
        super(xml_node);

        setUpColors();
        
        String color_name = OuiProjectXml.getElementContent(xml_node, "@color", "none");
        fill_color_name = OuiProjectXml.getElementContent(xml_node, "@fillColor", "none");
        border_color_name = OuiProjectXml.getElementContent(xml_node, "@borderColor", "none");
        //  Set the default fill color
        if (fill_color_name.equals("none")) {
            if (color_name.equals("none")) {
                fill_color_name = "clear";
            } else {
                fill_color_name = color_name;
            }
        }
        
        //  Set the default border color
        if (border_color_name.equals("none")) {
            if (color_name.equals("none")) {
                border_color_name = "black";
            } else {
                border_color_name = color_name;
            }
        }
        
        label_attribute = OuiProjectXml.getOuiProjectXml().getThemeMetaData (_theme_name, "@nameAttribute","");
        
        if (_theme_name.equals("no theme")) {
            shape_file_name = null;
            dbf_file_name = null;
            System.out.println("error with theme " + _name + ".  No theme specified.");
            
        } else {
            _type = "ESRI Shape File";   // overrides OuiTreeNode
            if (_file_name == null) {
                _has_map = false;
                _has_table = false;
                shape_file_name = null;
                dbf_file_name = null;
            } else {
                _has_table = true;           // overrides OuiTreeNode
                _has_map = true;             // overrides OuiTreeNode
                
                shape_file_name = _file_name + ".shp";	//	<-- May need an "Oui.rootPath + " here too...
                dbf_file_name = _file_name + ".dbf";		//	<--
            }
        }
    }
    
    /** Set the activated state for this node. When a node is activated, its features
     * can be selected on the OUI map.
     * @param is_activated Set to true activates the node. False, de-activates the node.
     */
    public void activate(Boolean is_activated) {
        if (is_activated.booleanValue()) {
            if (OuiGui.getLoadedPanel().getActivatedNode() != null) {
                OuiGui.getOuiGisPanel().setSelection(OuiGui.getLoadedPanel().getActivatedNode().getTheme(), false);
            }
            
            setDisplayed(true);
            OuiGui.getOuiGisPanel().setActiveTheme(_theme);
            OuiGui.getLoadedPanel().setActivatedNode(this);
            
        } else {
            OuiGui.getOuiGisPanel().setSelection(_theme, false);
            OuiGui.getOuiGisPanel().setActiveTheme(null);
            OuiGui.getLoadedPanel().setActivatedNode(null);
        }
    }

    /** Show the attribute (dbf) data for this node.
     * @param is_table_shown Turn the table on or off.
     */
    public void showTable(Boolean is_table_shown) {
        if (is_table_shown.booleanValue()) {
            if (table_frame == null) {
                table_frame = new OuiDbfAttributeEditor(this);
                String title = "GIS Attribute Tool: " + this.getName();
                WindowFactory.displayInFrame(table_frame, title);
            }
        } else {
            WindowFactory.instance().closeWindow(table_frame);
            
            table_frame = null;
        }
    }
    
    public void tableClosed() {
            table_frame = null;
    }
    
    /** Show the labels for this node on the OUI map.
     * @param labels_displayed Turn the labels on or off.
     */
    public void showLabel(Boolean labels_displayed) {
        if (_theme != null) {
            this.labels_displayed = labels_displayed;
            
            if (label_index < 0) {
               OuiGui.getOuiGisPanel().setLabelIndex(_theme, 0);
                
            } else {
                OuiGui.getOuiGisPanel().setLabelIndex(_theme, label_index);
            }

            OuiGui.getOuiGisPanel().setLabelsVisible(_theme, labels_displayed.booleanValue());
            OuiGui.getOuiGisPanel().tVrepaint();
        }
    }
    
    /** Return the node label state.
     * @return Are the labels currently displayed on the OUI map?
     */
    public Boolean isLabelShown() {return labels_displayed;}
    
    /** Return the attribute (dbf) table state for this node
     * @return Is the dbf attribute table for this node displayed?
     */
    public Boolean isTableShown() {
        if (table_frame == null) {
            return false;
        }
        return true;
    }
    
    /** Return the activation state for this node.
     * @return Is this node activated?
     */
    public Boolean isActivated() {
        if (OuiGui.getLoadedPanel().getActivatedNode() == this) {
            return true;
        } else {
            return false;
        }
    }
    
    /** Return true because this class always has a dbf attribute table.
     * @return Does this node have a dbf attribute table?  Always true.
     */
    public boolean hasTable() {return true;}
    
    public int getLabelIndex() {
        if (label_index < 0) {
            return 0;
        } else {
            return label_index;
        }
    }
    
    public void setTableFrame(OuiDbfAttributeEditor table_frame) {
        this.table_frame = table_frame;
    }
    
    public OuiDbfAttributeEditor getTableFrame() {return table_frame;}
    
    public OuiAttributeModel getAttributeModel() {
        if (attribute_model == null) loadTheme();
        return attribute_model;
    }
    
    /** Remove the theme for this node from the OUI map.
     */
    @Override
    public void removeTheme() {
        super.removeTheme();
        if (OuiGui.getLoadedPanel().getActivatedNode() == this) {
            OuiGui.getLoadedPanel().setActivatedNode(null);
        }
    }
    
    /** Set the displayed state for this node. When a node is displayed, its features
     * are visible on the OUI map.
     * @param d Turn the visibility of the theme on or off.
     */
    @Override
    public void setDisplayed(Boolean d) {
        super.setDisplayed(d);
        
        AbstractVectorThemeColorModel vcm = OuiGui.getOuiGisPanel().getColorModel((VectorTheme)_theme);
        
        for (int i = 0; i < ((VectorTheme)_theme).getFeatureCount(); i++) {
//System.out.println(" fill_color_name = " + fill_color_name + " foo = " + OuiColors.getFillColor(fill_color_name));
            vcm.setFillPaint(fill_colors.get(fill_color_name), i);
            vcm.setBorderPaint(border_colors.get(border_color_name), i);
        }
        
        OuiGui.getOuiGisPanel().tVrepaint();
    }
    
    /** Load the theme for this node for the OUI map.
     * @return The GIS theme for this node.
     */
    public Theme loadTheme() {
        VectorTheme vt = null;
        
        try {
            ShpFileParser sfp = new ShpFileParser(shape_file_name);
            vectorModel = sfp.createModel();
            
            OuiDbfFileParser dfp = new OuiDbfFileParser(dbf_file_name);
            attribute_model = dfp.createModel();
            vt = new VectorTheme(vectorModel, attribute_model);
            
/*
 **  Figure out the label_index.  The label_attribute comes from the tree file.
 **  Use this to look up the column index.
 */
            if (label_attribute.equals("")) {
                label_index = 0;
            } else {
                label_index = attribute_model.getAttributeIndex(label_attribute);
            }
            
        } catch (FileNotFoundException e1) {
            System.err.println(e1);
        } catch (Exception e2) {
            System.err.println(e2);
        }
        return ((Theme)vt);
    }
    
    public String getDbfFileName() {return dbf_file_name;}
        
    /**
     * Getter for property vectorModel.
     * @return Value of property vectorModel.
     */
    public VectorModel getVectorModel() {
        return this.vectorModel;
    }

    private void setUpColors() {
        if (fill_colors != null) {
            return;
        }
        
        fill_colors = new HashMap<String, Color>();
        border_colors = new HashMap<String, Color>();

        fill_colors.put("black", new Color(0, 0, 0, 50));
        fill_colors.put("blue", new Color(0, 0, 255, 50));
        fill_colors.put("cyan", new Color(0, 255, 255, 50));
        fill_colors.put("darkGray", new Color(100, 100, 100, 50));
        fill_colors.put("gray", new Color(200, 200, 200, 50));
        fill_colors.put("green", new Color(0, 255, 0, 50));
        fill_colors.put("lightGray", new Color(240, 240, 240, 50));
        fill_colors.put("magenta", new Color(255, 0, 255, 50));
        fill_colors.put("orange", new Color(255, 0, 0, 50));
        fill_colors.put("pink", new Color(255, 193, 193, 50));
        fill_colors.put("red", new Color(255, 0, 0, 50));
        fill_colors.put("white", new Color(255, 255, 255, 50));
        fill_colors.put("yellow", new Color(255, 255, 0, 50));
        fill_colors.put("clear", new Color(0, 0, 0, 0));

        border_colors.put("black", Color.black);
        border_colors.put("blue", Color.blue);
        border_colors.put("cyan", Color.cyan);
        border_colors.put("darkGray", Color.darkGray);
        border_colors.put("gray", Color.gray);
        border_colors.put("green", Color.green);
        border_colors.put("lightGray", Color.lightGray);
        border_colors.put("magenta", Color.magenta);
        border_colors.put("orange", Color.orange);
        border_colors.put("pink", Color.pink);
        border_colors.put("red", Color.red);
        border_colors.put("white", Color.white);
        border_colors.put("yellow", Color.yellow);
        border_colors.put("clear", new Color(0, 0, 0, 0));
    }
}
