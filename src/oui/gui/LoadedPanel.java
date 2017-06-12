/*
 * LoadedPanel.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import oui.treetypes.OuiDataTreeNode;
import oui.treetypes.OuiShapeTreeNode;
import oui.treetypes.OuiThemeTreeNode;

/**
 *	The panel in the lower left of the OUI window that showes the loaded themes.
 *	
 *	@author markstro
 *	@version 2.0
 */

public class LoadedPanel {
    private OuiLoadedTableModel model = null;
    private final JPopupMenu popup;
    private OuiShapeTreeNode activated_node = null;
    private OuiDataTreeNode query_node = null;
    private final JTable loadedTable;
    
    /** Create a LoadedPanel object.
     * @param loadedTable
     */
    public LoadedPanel(JTable loadedTable) {
        this.loadedTable = loadedTable;

        popup = _createPopup();
        loadedTable.addMouseListener(new PopupListener());
    }

    public void setGisPanel(OuiGISPanel gisPanel) {
        model = new OuiLoadedTableModel(this, gisPanel);
        loadedTable.setModel(model);
    }
    
    /** Add a theme tree node to the loaded list.
     * @param ottn The theme tree node to add.
     */
    public void addTheme(OuiThemeTreeNode ottn) {
        model.addTheme(ottn);
    }
    
    /** Return the shape tree node with the currently active theme.
     * @return The shape tree node with the currently active theme.
     */
    public OuiShapeTreeNode getActivatedNode() {
        return activated_node;
    }
    
    public void setActivatedNode(OuiShapeTreeNode activated_node) {
        this.activated_node = activated_node;
    }
    
    public OuiDataTreeNode getQueryNode() {
        return query_node;
    }
    
    public void setQueryNode(OuiDataTreeNode query_node) {
        this.query_node = query_node;
        if (query_node != null) {
            query_node.queryNotification();
        }
    }
       
/*
 ** Popup menu stuff
 */
    private JPopupMenu _createPopup() {
        JPopupMenu p = new JPopupMenu();
        
        JMenuItem menu_item = new JMenuItem("Remove");
        p.add(menu_item);
        menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = loadedTable.getSelectedRows();
                for (int i = (selectedRows.length - 1); i >= 0; i--){
                    model.remove(selectedRows[i]);
                }
            }
        });
        
        menu_item = new JMenuItem("Move Up");
        p.add(menu_item);
        menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = loadedTable.getSelectedRows();
                Arrays.sort(selectedRows);
                for (int i = 0; i < selectedRows.length; i++){
                    model.moveUp(selectedRows[i]);
                }
                loadedTable.clearSelection();
            }
        });
        
        menu_item = new JMenuItem("Move Down");
        p.add(menu_item);
        menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = loadedTable.getSelectedRows();
                Arrays.sort(selectedRows);
                for (int i = (selectedRows.length - 1); i >= 0; i--){
                    model.moveDown(selectedRows[i]);
                }
            }
        });
        
        menu_item = new JMenuItem("Move to Top");
        p.add(menu_item);
        menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = loadedTable.getSelectedRows();
                Arrays.sort(selectedRows);
                int numMoved = 0;
                for (int i = (selectedRows.length - 1); i >= 0; i--){
                    model.moveTop(selectedRows[i] + numMoved);
                    numMoved++;
                }
            }
        });
        
        menu_item = new JMenuItem("Move to Bottom");
        p.add(menu_item);
        menu_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = loadedTable.getSelectedRows();
                Arrays.sort(selectedRows);
                int numMoved = 0;
                for (int i = 0; i < selectedRows.length; i++){
                    model.moveBottom(selectedRows[i] - numMoved);
                    numMoved++;
                }
            }
        });
        
        return p;
    }
    
    class PopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu(e);
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu(e);
            }
        }
        private void popupMenu(MouseEvent e) {
            if (loadedTable.getSelectedRowCount() < 2) {
                int sel_row = loadedTable.rowAtPoint(new Point(e.getX(), e.getY()));
                loadedTable.setRowSelectionInterval(sel_row, sel_row);
            }
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}