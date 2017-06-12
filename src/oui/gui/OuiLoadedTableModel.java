/*
 * OuiLoadedTableModel.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.gui;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import oui.treetypes.OuiDataTreeNode;
import oui.treetypes.OuiShapeTreeNode;
import oui.treetypes.OuiThemeTreeNode;

/** This implements the TableModel for the JTable in the OUI LoadedPanel.
 *
 * @author markstro
 * @version 2.0
 */
public class OuiLoadedTableModel extends AbstractTableModel {
    ArrayList<OuiThemeTreeNode> theme_vec = new ArrayList<>(10);
    
    static protected String[] col_names = {"Name", "Visible", "Labels", "Active", "Query", "Attributes"};
    private final LoadedPanel loadedPanel;
    private final OuiGISPanel gisPanel;
    
    /** Create an OuiLoadedTableModel object.
     * @param loadedPanel
     * @param gisPanel
     */
    public OuiLoadedTableModel(LoadedPanel loadedPanel, OuiGISPanel gisPanel) {
        this.loadedPanel = loadedPanel;
        this.gisPanel = gisPanel;
    }
    
    /** Add a tree node to the loaded table.
     * @param ottn The tree node to add.
     */
    public void addTheme(OuiThemeTreeNode ottn) {
        if (!theme_vec.contains(ottn)) {
            theme_vec.add(0, ottn);
        this.fireTableDataChanged();
        }
    }
    
    /** Move a node up in the list. This has the effect of brining a
     * theme one level closer to the top of the OUI map.
     * @param row The row index of the selected node.
     */
    public void moveUp(int row) {
        if (row == 0) return;
        
        theme_vec.add(row-1, theme_vec.remove(row));
        gisPanel.updateThemeOrder(theme_vec);
        this.fireTableDataChanged();
    }
    
    /** Move a node down in the list. This has the effect of brining a
     * theme one level closer to the bottom of the OUI map.
     * @param row The row index of the selected node.
     */
    public void moveDown(int row) {
        if (row == theme_vec.size()-1) return;
        
        theme_vec.add(row+1, theme_vec.remove(row));
        gisPanel.updateThemeOrder(theme_vec);
        this.fireTableDataChanged();
    }
    
    /** Move a node to the top of the list. This has the effect of moving a
     * theme to the top of the OUI map.
     * @param row The row index of the selected node.
     */
    public void moveTop(int row) {
        if (row == 0) return;
        
        theme_vec.add(0, theme_vec.remove(row));
        gisPanel.updateThemeOrder(theme_vec);
        this.fireTableDataChanged();
    }
    
    /** Move a node to the bottom of the list. This has the effect of moving a
     * theme to the bottom of the OUI map.
     * @param row The row index of the selected node.
     */
    public void moveBottom(int row) {
        if (row == theme_vec.size()-1) return;
        
        theme_vec.add(theme_vec.size() - 1, theme_vec.remove(row));
        gisPanel.updateThemeOrder(theme_vec);
        this.fireTableDataChanged();
    }
    
    /** Remove a node from the list. This deletes a
     * theme from the OUI map.
     * @param row The row index of the selected node.
     */
    public void remove(int row) {
        ((OuiThemeTreeNode)(theme_vec.get(row))).removeTheme();
        theme_vec.remove(row);
        this.fireTableDataChanged();
    }
    
    //  Implement the AbstractTable Model.
    @Override
    public String getColumnName(int col) {return col_names[col];}
    @Override
    public int getColumnCount() {return col_names.length;}
    @Override
    public int getRowCount() {return theme_vec.size();}
    @Override
    public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
    
    @Override
    public boolean isCellEditable(int row, int col) {
        if (col == 0) {
            return false;
        } else if (col == 1) {
            return true;
        } else if (col == 4) {
            return ((OuiThemeTreeNode)(theme_vec.get(row))).has_data();
        } else {
            return ((OuiThemeTreeNode)(theme_vec.get(row))).has_table();
        }
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        OuiThemeTreeNode ottn = (OuiThemeTreeNode)(theme_vec.get(row));
        
        switch (col) {
            case 0:  // name
                break;
            case 1:  // displayed
                ((OuiThemeTreeNode)(theme_vec.get(row))).setDisplayed((Boolean)value);
                break;
            case 2:  // labels
                ((OuiShapeTreeNode)(theme_vec.get(row))).showLabel((Boolean)value);
                break;
            case 3:  // active
//                for (int i = 0; i < theme_vec.size(); i++) {
//                    ((OuiShapeTreeNode)(theme_vec.elementAt(i))).activate(Boolean.FALSE);
//                }

                ((OuiShapeTreeNode)(theme_vec.get(row))).activate((Boolean)value);
                
                if (loadedPanel.getQueryNode() != null) {
                    loadedPanel.getQueryNode().setQueryMode(false);
                    loadedPanel.setQueryNode(null);
                }
                break;
                
            case 4:  // query
                if (((Boolean)value)) {
                    ((OuiShapeTreeNode)(theme_vec.get(row))).activate((Boolean)value);
                }
                
                if (loadedPanel.getQueryNode() != null) {
                    loadedPanel.getQueryNode().setQueryMode(false);
                    loadedPanel.setQueryNode(null);
                }
                
                ((OuiDataTreeNode)(theme_vec.get(row))).setQueryMode((Boolean)value);
                break;
                
            case 5:  // attributes
                ((OuiShapeTreeNode)(theme_vec.get(row))).showTable((Boolean)value);
                break;
        }
        this.fireTableDataChanged();
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        OuiThemeTreeNode ottn = (OuiThemeTreeNode)(theme_vec.get(row));
        
        switch (col) {
            case 0:  // name
                return ((OuiThemeTreeNode)(theme_vec.get(row))).getName();
                
            case 1:  // displayed
                return ((OuiThemeTreeNode)(theme_vec.get(row))).isDisplayed();
                
            case 2:  // labels
                if (((OuiThemeTreeNode)(theme_vec.get(row))).has_table()) {
                    return ((OuiShapeTreeNode)(theme_vec.get(row))).isLabelShown();
                } else {
                    return false;
                }
                
            case 3:  // active
                if (((OuiThemeTreeNode)(theme_vec.get(row))).has_table()) {
                    return ((OuiShapeTreeNode)(theme_vec.get(row))).isActivated();
                } else {
                    return false;
                }
                
            case 4:  // guery
                if (((OuiThemeTreeNode)(theme_vec.get(row))).has_data()) {
                    return ((OuiDataTreeNode)(theme_vec.get(row))).isInQueryMode();
                } else {
                    return false;
                }
                
            case 5:  // attributes
                if (((OuiThemeTreeNode)(theme_vec.get(row))).has_table()) {
                    return ((OuiShapeTreeNode)(theme_vec.get(row))).isTableShown();
                } else {
                    return false;
                }
        }
        return null;
    }
}