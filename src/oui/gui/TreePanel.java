/*
 * LoadedPanel.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.gui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import oui.mms.gui.treetable.JTreeTable;
import oui.treetypes.OuiModelTreeNode;
import oui.treetypes.OuiThemeTreeNode;
import oui.treetypes.OuiTreeNode;

/** The panel in the upper left of the OUI window that shows the tree nodes in
 * the OUI project tree.xml file.
 * @author markstro
 * @version 2.0
 */
public class TreePanel {

    JTreeTable tree;
    JPopupMenu popup;
    JMenuItem display_menu_item, run_menu_item;
    private final LoadedPanel loadedPanel;

    /** Create a TreePanel object.
     * @param treeScrollPane
     * @param loadedPanel
     */
    public TreePanel(JScrollPane treeScrollPane, LoadedPanel loadedPanel) {
        this.loadedPanel = loadedPanel;

        popup = _createPopup();

        tree = new JTreeTable(new OuiTreeTableModel());
        tree.addMouseListener(new PopupListener());
        treeScrollPane.setViewportView(tree);
    }

    /*
     ** Popup menu stuff
     */
    private JPopupMenu _createPopup() {
        JPopupMenu p = new JPopupMenu();

        display_menu_item = new JMenuItem("Load");
        p.add(display_menu_item);
        display_menu_item.addActionListener((ActionEvent e) -> {
            OuiThemeTreeNode ottn = (OuiThemeTreeNode) getSelectionFromTree();
            ottn.setDisplayed(true);
            loadedPanel.addTheme(ottn);
        });

        run_menu_item = new JMenuItem("Run");
        p.add(run_menu_item);
        run_menu_item.addActionListener((ActionEvent e) -> {
            OuiModelTreeNode omtn = (OuiModelTreeNode) getSelectionFromTree();
            omtn.run();
        });

        return p;
    }

    /** Returns the tree node which is currently selected in the tree.
     * @return The currently selected tree node.
     */
    private OuiTreeNode getSelectionFromTree() {
        return ((OuiTreeNode) (tree.getValueAt(tree.getSelectedRow(), 0)));
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

            int sel_row = tree.rowAtPoint(new Point(e.getX(), e.getY()));
            tree.setRowSelectionInterval(sel_row, sel_row);

            OuiTreeNode sel = getSelectionFromTree();
            display_menu_item.setEnabled(sel.has_map());
            run_menu_item.setEnabled(sel.has_model());

            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
