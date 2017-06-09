/*
 * OuiTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.treetypes;

import java.util.Vector;
import org.w3c.dom.Node;
import oui.mms.OuiProjectXml;

/** This class contains the necessary information to register a node
 * with the Object User Interface (OUI). It is the top parent of all node tree
 * classes. Other classes are available for displaying
 * ascii grid files, shape files with various data sources, models, etc.
 * The information describing a node and how to display it is read from the
 * XML control file tree.xml in the OUI project directory. Refer to <I>The Object
 * User Interface (OUI): User's Manual</I> for more information.
 * <P>
 * The element in tree.xml must look like this:<BR>
 * <CODE>&lt;node name="Output" type="MMS Statvar w/Shape" desc="Historic local flows"/&gt;</CODE>
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
 * <LI><B>type</B> (optional) specifies the node type.  The default type is blank.<BR>
 * <LI><B>desc</B> (optional) is a description of the node. The default description is blank.<BR>
 *</UL>
 * <P>
 *
 * @author markstro
 * @version 2.0
 */
public class OuiTreeNode {
    protected String _name = null;
    protected String _type = null;
    protected String _desc = null;
    protected Vector<OuiTreeNode> _children = new Vector<OuiTreeNode>(10, 10);
//    protected Node _xml_node = null;
    protected boolean _has_model = false;
    protected boolean _has_map = false;
    protected boolean _has_table = false;
    protected boolean _has_data = false;

    /** Create an OuiTreeNode. An object of this class or sub-class can be displayed
     * in the OUI data tree.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
        
    public OuiTreeNode(Node xml_node) {
//        _xml_node = xml_node;
        OuiProjectXml pxml = OuiProjectXml.getOuiProjectXml();
        
        if (xml_node != null) {
            _name = pxml.getElementContent(xml_node, "@name", "no name");
            _type = pxml.getElementContent(xml_node, "@type", "");
            _desc = pxml.getElementContent(xml_node, "@desc", "");
        }
    }
    
    /** Return a printable version of this tree node.
     * @return A printable version of this tree node.
     */
    public String toString() {return _name;}

    /** Return the name of the tree node.
     * @return The name of the tree node.
     */
    public String getName() {return _name;}

    /** Return the type of the tree node.
     * @return The type of the tree node.
     */
    public String getType() {return _type;}

    /** Return the description of the tree node.
     * @return The description of the tree node.
     */
    public String getDesc() {return _desc;}

    /** Return the children of this tree node.
     * @return The children of this tree node.
     */
    public Vector getChildren() {return _children;}

    /** Return the number of children of this tree node.
     * @return The number of children of this tree node.
     */
    public int getChildCount() {return _children.size();}

    /** Add a child to this tree node.
     * @param child A child tree node to this tree node.
     */
    public void addChild(OuiTreeNode child) {_children.add(child);}

    /** Return the tree node model state.
     * @return Does the tree node have a model?
     */
    public boolean has_model() {return _has_model;}

    /** Return the tree node table state.
     * @return Does the tree node have a dbf or parameter table?
     */
    public boolean has_table() {return _has_table;}

    /** Return the tree node data state.
     * @return Does the tree node have a external model data?
     */
    public boolean has_data() {return _has_data;}

    /** Return the tree node map state.
     * @return Does the tree node have a map theme?
     */
    public boolean has_map() {return _has_map;}

    /** Return a child of this tree node.
     * @return The child of this tree node at index i.
     * @param i The child index.
     */
    public OuiTreeNode getChildAt(int i) {
        return (OuiTreeNode)(_children.elementAt(i));
    }
}
