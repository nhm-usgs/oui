/*
 * OuiTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 * Jython support added June 20, 2003
 */

package oui.gui;

import oui.mms.gui.treetable.AbstractTreeTableModel;
import oui.mms.gui.treetable.TreeTableModel;
import oui.treetypes.OuiThemeTreeNode;
import oui.treetypes.OuiTreeNode;

/** This implements the TreeTableModel for the JTreeTable in the OUI TreePanel.
 *
 * @author markstro
 * @version 2.0
 */
public class OuiTreeTableModel extends AbstractTreeTableModel implements TreeTableModel {
    static protected String[] col_names = {"Name", "Description", "Theme", "Type"};
    static protected Class[] col_types = {TreeTableModel.class, String.class, String.class, String.class};

    public OuiTreeTableModel() {
        super (TreeNodes.getRootNode());
    }

    // The TreeModel interface
    //
    
    /** Returns the number of children of <code>node</code>.
     * @param node A tree node.
     * @return The number of children of <code>node</code>.
     */
    public int getChildCount(Object node) {        
        OuiTreeNode tnd = (OuiTreeNode)node;
        return tnd.getChildCount();
    }
    
    /** Returns the child of <code>node</code> at index <code>i</code>.
     * @param node A tree node.
     * @param i Selected index.
     * @return The child of <code>node</code> at index <code>i</code>.
     */
    public Object getChild(Object node, int i) {
        OuiTreeNode tnd = (OuiTreeNode)node;
        return (tnd.getChildren()).elementAt(i);
    }
    
    //
    //  The TreeTableNode interface.
    //
    
    /** Returns the number of columns.
     * @return The number of columns.
     */
    public int getColumnCount() {
        return col_names.length;
    }
    
    /** Returns the name for a particular column.
     * @param column Selected column index.
     * @return The name for a particular column.
     */
    public String getColumnName(int column) {
        return col_names[column];
    }
    
    /** Returns the class for the particular column.
     * @return The class for the particular column.
     * @param column Selected column index. */
    @Override
    public Class getColumnClass(int column) {
        return col_types[column];
    }
    
    /** Returns the value of the particular column.
     * @param node A tree node.
     * @param column Selected column index.
     * @return The value of the particular column.
     */
    public Object getValueAt(Object node, int column) {
        OuiTreeNode tnd = (OuiTreeNode)node;
        
        switch (column) {
            case 0:      // Name
                return tnd;
                
            case 1:      // Description
                return tnd.getDesc();
                
            case 2:      // Theme name
                if (tnd.has_map()) {
                    OuiThemeTreeNode ttn = (OuiThemeTreeNode)node;
                    return ttn.getThemeName();
                } else {
                    return null;
                }
                
            case 3:      // Type
                return tnd.getType();
        }
        return null;
    }
}
