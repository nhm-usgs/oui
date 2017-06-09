/*
 * OuiTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 * Jython support added June 20, 2003
 */

package oui.controltool;

import oui.mms.datatypes.ControlSet;
import javax.swing.tree.TreeModel;

/** This implements the TreeTableModel for the JTreeTable in the OUI TreePanel.
 *
 * @author markstro
 * @version 2.0
 */
public class MmsControlEditTreeModel implements TreeModel {
    private ControlSet mmsControlMap;
    
    public MmsControlEditTreeModel(ControlSet mmsControlMap) {
        this.mmsControlMap = mmsControlMap;
    }

    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
    }
    
    public Object getChild(Object node, int i) {
        return mmsControlMap.getControlVariableByIndex(i);
    }
    
    /** Returns the number of children of <code>node</code>.
     * @param node A tree node.
     * @return The number of children of <code>node</code>.
     */
    public int getChildCount(Object node) {
        if (node == mmsControlMap) {    // root node
            return mmsControlMap.getControlVariableCount();
        } else {
            return 0;
        }
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == mmsControlMap) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public Object getRoot() {
        return mmsControlMap;
    }
    
    public boolean isLeaf(Object node) {
        if (node == mmsControlMap) {    // root node
            return false;
        } else {
            return true;
        }
    }
    
    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
    }
    
    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
    }
    
}
