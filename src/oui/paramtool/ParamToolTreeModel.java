/*
 * OuiTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 * Jython support added June 20, 2003
 */

package oui.paramtool;

import javax.swing.tree.TreeModel;
import oui.mms.datatypes.ParameterSet;

/** This implements the TreeTableModel for the JTreeTable in the OUI TreePanel.
 *
 * @author markstro
 * @version 2.0
 */
public class ParamToolTreeModel implements TreeModel {
    public static String dimensionSizes = "Dimension Sizes";
    public static String parameterValues = "Parameter Values by Dimension";
    private ParameterSet mms_params;
    
    public ParamToolTreeModel(ParameterSet mms_params) {
        this.mms_params = mms_params;
    }

    @Override
    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
    }
    
    @Override
    public Object getChild(Object node, int i) {

        if (node == mms_params) {    // root node
            if (i == 0) {
                return dimensionSizes;
            } else {
                return parameterValues;
            }
          
        } else if (node == dimensionSizes) {    // "Dimension Sizes" leaf
            return mms_params.getDimensionAt(i);
             
        } else if (node == parameterValues) {   // "Parameter Values by Dimension" node
            return mms_params.getEditableDimensionArray()[i];
            
        } else if (node.toString().indexOf(',') > 0) {   // Multiple dimension node
            return mms_params.getParamsFor2DDim(node.toString()).get(i);

        } else {   // Single dimension or multidimension parameter leaf
            return null;
        }
    }
    
    /** Returns the number of children of <code>node</code>.
     * @param node A tree node.
     * @return The number of children of <code>node</code>.
     */
    @Override
    public int getChildCount(Object node) {
        
        if (node == mms_params) {    // root node
            return 2;
            
        } else if (node == dimensionSizes) {    // "Dimension Sizes" leaf
            return mms_params.getNumberOfDimensions();
            
        } else if (node == parameterValues) {   // "Parameter Values by Dimension" node
            return mms_params.getEditableDimensionArray().length;
            
        } else if (node.toString().indexOf(',') > 0) {   // Multiple dimension node
            return mms_params.getParamsFor2DDim(node.toString()).size();

        } else {   // Single dimension or multidimension parameter leaf
            return 0;
        }
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        
        if (parent == mms_params) {
            if (child ==  dimensionSizes) {
                return 0;
            } else {
                return 1;
            }
            
        } else if (parent == dimensionSizes) {
            return 0;
            
        } else if (parent == parameterValues) {
            for (int i = 0; i < mms_params.getNumberOfParameters(); i++) {
                if (child == mms_params.getEditableDimensionArray()[i]) {
                    return i;
                }
            }            
        }
        
        return 0;
    }
    
    @Override
    public Object getRoot() {
        return mms_params;
    }
    
    @Override
    public boolean isLeaf(Object node) {
        if (node == mms_params) {    // root node
            return false;
            
        } else if (node == dimensionSizes) {    // "Dimension Sizes" leaf
            return true;
            
        } else if (node == parameterValues) {   // "Parameter Values by Dimension" node
            return false;
            
        } else if (node.toString().indexOf(',') > 0) {   // Multiple dimension node
            return false;
            
        } else {   // Single dimension or multidimension parameter leaf
            return true;
        }
        
    }
    
    @Override
    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
    }
    
    @Override
    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
    }
    
}
