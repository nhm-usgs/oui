/*
 * OuiGui.java
 *
 * Created on July 26, 2004, 1:44 PM
 */
package oui.gui;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.MenuBarProvider;
import gov.usgs.cawsc.gui.PersistentSplitterTracker;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import oui.util.OuiHelp;

/**
 *
 * @author markstro
 */
public class OuiGui extends JPanel implements PropertyChangeListener, MenuBarProvider {

    private static final Logger logger = Logger.getLogger(OuiGui.class.getName());
    private static LoadedPanel loadedPanel = null;
    private static OuiGISPanel ouiGisPanel = null;
    private static TreePanel treePanel = null;
    
    protected PersistentSplitterTracker persistentSplitterTracker1 = null;
    protected PersistentSplitterTracker persistentSplitterTracker2 = null;

    public OuiGui() {
        initComponents();

        loadedPanel = new LoadedPanel(loadedTable);
        ouiGisPanel = new OuiGISPanel(mapContentPanel, featureLabel, loadedPanel);
        ouiGisPanel.addPropertyChangeListener(this);
        fontSizeSpinner.setValue(ouiGisPanel.getThemeView().getLabelSize());

        loadedPanel.setGisPanel(ouiGisPanel);

        treeScrollPane.getViewport().setBackground(treeScrollPane.getBackground());
        treePanel = new TreePanel(treeScrollPane, loadedPanel);

        persistentSplitterTracker1 = new PersistentSplitterTracker(this, jSplitPane1, "1");
        persistentSplitterTracker2 = new PersistentSplitterTracker(this, jSplitPane2, "2");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("labelFontSize".equals(evt.getPropertyName())) {
            int fontSize = (int) evt.getNewValue();
            fontSizeSpinner.setValue(fontSize);
        }
        if ("panMode".equals(evt.getPropertyName())) {
            if (Boolean.TRUE == evt.getNewValue()) {
                map_pan_button.setSelected(true);
            }
        }
    }

    // This is needed by some of the TreeNode classes
    public static OuiGISPanel getOuiGisPanel() {
        return ouiGisPanel;
    }

    // This is needed by some of the TreeNode classes
    public static Component getTreeScrollPane() {
        return treePanel.tree;
    }

    // This is needed by some of the TreeNode classes
    public static LoadedPanel getLoadedPanel() {
        return loadedPanel;
    }

    public JMenuBar getMenuBar() {
        return jMenuBar;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do
     * NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        map_button_group = new javax.swing.ButtonGroup();
        jMenuBar = new javax.swing.JMenuBar();
        file_menu = new javax.swing.JMenu();
        quit_menu_item = new javax.swing.JMenuItem();
        help_menu = new javax.swing.JMenu();
        system_help_menu_item = new javax.swing.JMenuItem();
        about_menu_item = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        treeScrollPane = new javax.swing.JScrollPane();
        loaded_scroll_pane = new javax.swing.JScrollPane();
        loadedTable = new javax.swing.JTable();
        mapPanel = new javax.swing.JPanel();
        map_button_panel = new javax.swing.JPanel();
        mouseClickActionPanel = new javax.swing.JPanel();
        map_select_button = new javax.swing.JRadioButton();
        map_pan_button = new javax.swing.JRadioButton();
        map_zoom_rect_button = new javax.swing.JRadioButton();
        fontSizePanel = new javax.swing.JPanel();
        fontSizeSpinner = new javax.swing.JSpinner();
        fixedCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        map_zoom_out_button = new javax.swing.JButton();
        map_zoom_in_button = new javax.swing.JButton();
        tracking_panel = new javax.swing.JPanel();
        featureLabel = new javax.swing.JLabel();
        mapContentPanel = new javax.swing.JPanel();

        file_menu.setText("File");

        quit_menu_item.setText("Quit");
        quit_menu_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quit_menu_itemActionPerformed(evt);
            }
        });
        file_menu.add(quit_menu_item);

        jMenuBar.add(file_menu);

        help_menu.setText("Help");

        system_help_menu_item.setText("OUI Users Manual");
        system_help_menu_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                system_help_pressed(evt);
            }
        });
        help_menu.add(system_help_menu_item);

        about_menu_item.setText("About");
        about_menu_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                about_menu_itemActionPerformed(evt);
            }
        });
        help_menu.add(about_menu_item);

        jMenuBar.add(help_menu);

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setResizeWeight(0.33);
        jSplitPane1.setOneTouchExpandable(true);

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setResizeWeight(0.75);
        jSplitPane2.setOneTouchExpandable(true);

        treeScrollPane.setBackground(new java.awt.Color(255, 255, 255));
        jSplitPane2.setLeftComponent(treeScrollPane);

        loaded_scroll_pane.setBorder(javax.swing.BorderFactory.createTitledBorder("Loaded Themes"));

        loadedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        loaded_scroll_pane.setViewportView(loadedTable);

        jSplitPane2.setRightComponent(loaded_scroll_pane);

        jSplitPane1.setLeftComponent(jSplitPane2);

        mouseClickActionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Mouse Action"));

        map_button_group.add(map_select_button);
        map_select_button.setSelected(true);
        map_select_button.setText("Select");
        map_select_button.setToolTipText("Select with each mouse click (Ctrl-Click for multiple)");
        map_select_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                map_select_buttonActionPerformed(evt);
            }
        });
        mouseClickActionPanel.add(map_select_button);

        map_button_group.add(map_pan_button);
        map_pan_button.setText("Pan");
        map_pan_button.setToolTipText("Zoom in with each mouse click");
        map_pan_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInSelected(evt);
            }
        });
        mouseClickActionPanel.add(map_pan_button);

        map_button_group.add(map_zoom_rect_button);
        map_zoom_rect_button.setText("Zoom Rect");
        map_zoom_rect_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                map_zoom_rect_buttonActionPerformed(evt);
            }
        });
        mouseClickActionPanel.add(map_zoom_rect_button);

        fontSizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Font Size"));

        fontSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(12, 1, 99, 1));
        fontSizeSpinner.setToolTipText("Set the font size of labels");
        fontSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fontSizeSpinnerStateChanged(evt);
            }
        });

        fixedCheckBox.setSelected(true);
        fixedCheckBox.setText("Fixed");
        fixedCheckBox.setToolTipText("Keep the font at a fixed screen size regardless of zoom level");
        fixedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout fontSizePanelLayout = new javax.swing.GroupLayout(fontSizePanel);
        fontSizePanel.setLayout(fontSizePanelLayout);
        fontSizePanelLayout.setHorizontalGroup(
            fontSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fontSizePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fontSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fixedCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        fontSizePanelLayout.setVerticalGroup(
            fontSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fontSizePanelLayout.createSequentialGroup()
                .addGroup(fontSizePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fixedCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Zoom"));

        map_zoom_out_button.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        map_zoom_out_button.setText("<html>&#8212");
        map_zoom_out_button.setToolTipText("Zoom Out");
        map_zoom_out_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom_out_pushed(evt);
            }
        });

        map_zoom_in_button.setFont(map_zoom_in_button.getFont().deriveFont(map_zoom_in_button.getFont().getStyle() | java.awt.Font.BOLD, map_zoom_in_button.getFont().getSize()+4));
        map_zoom_in_button.setText("+");
        map_zoom_in_button.setToolTipText("Zoom In");
        map_zoom_in_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                map_zoom_in_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(map_zoom_in_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(map_zoom_out_button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {map_zoom_in_button, map_zoom_out_button});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(map_zoom_in_button)
                    .addComponent(map_zoom_out_button, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout map_button_panelLayout = new javax.swing.GroupLayout(map_button_panel);
        map_button_panel.setLayout(map_button_panelLayout);
        map_button_panelLayout.setHorizontalGroup(
            map_button_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(map_button_panelLayout.createSequentialGroup()
                .addComponent(mouseClickActionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fontSizePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        map_button_panelLayout.setVerticalGroup(
            map_button_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mouseClickActionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(fontSizePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        tracking_panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Tracking"));
        tracking_panel.setLayout(new java.awt.BorderLayout());

        featureLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        featureLabel.setText(" ");
        featureLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        tracking_panel.add(featureLabel, java.awt.BorderLayout.WEST);

        mapContentPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tracking_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(map_button_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mapContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mapPanelLayout.createSequentialGroup()
                .addComponent(map_button_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mapContentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tracking_panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(mapPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMenuBar, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jMenuBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void path_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_path_menuActionPerformed

    }//GEN-LAST:event_path_menuActionPerformed

private void about_menu_itemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_about_menu_itemActionPerformed
{//GEN-HEADEREND:event_about_menu_itemActionPerformed

    String foo = "Object User Interface (OUI)" + "\n"
        + "U.S. Geological Survey" + "\n\n"
        + "OUI Version: 4.0.1dev3 " + "\n"
        + "Date: 2016-03-09" + "\n"
        + "Built with Java version: 1.7.0_79" + "\n"
        + "url: http://wwwbrr.cr.usgs.gov/oui" + "\n";

    JOptionPane.showMessageDialog(GuiUtilities.windowFor(this), foo, "About", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_about_menu_itemActionPerformed

private void quit_menu_itemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_quit_menu_itemActionPerformed
{//GEN-HEADEREND:event_quit_menu_itemActionPerformed
    quit();
}//GEN-LAST:event_quit_menu_itemActionPerformed

private void zoomInSelected(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoomInSelected
{//GEN-HEADEREND:event_zoomInSelected
//    ouiGisPanel.zoomInMode();
// This is actually the Pan radio button now
    ouiGisPanel.panMode();
}//GEN-LAST:event_zoomInSelected

private void exitForm(java.awt.event.WindowEvent evt)//GEN-FIRST:event_exitForm
{//GEN-HEADEREND:event_exitForm
    quit();
}//GEN-LAST:event_exitForm

private void system_help_pressed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_system_help_pressed
{//GEN-HEADEREND:event_system_help_pressed
    OuiHelp.OuiHelpFactory().showTopic("");
}//GEN-LAST:event_system_help_pressed

private void map_select_buttonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_map_select_buttonActionPerformed
{//GEN-HEADEREND:event_map_select_buttonActionPerformed
    ouiGisPanel.normalMode();
}//GEN-LAST:event_map_select_buttonActionPerformed

private void zoom_out_pushed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_zoom_out_pushed
{//GEN-HEADEREND:event_zoom_out_pushed
    ouiGisPanel.zoomOut();
}//GEN-LAST:event_zoom_out_pushed

    private void fontSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fontSizeSpinnerStateChanged
        int fontSize = (Integer) fontSizeSpinner.getValue();
        ouiGisPanel.setLabelSize(fontSize);
    }//GEN-LAST:event_fontSizeSpinnerStateChanged

    private void fixedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedCheckBoxActionPerformed
        ouiGisPanel.setFixedFontSize(fixedCheckBox.isSelected());
    }//GEN-LAST:event_fixedCheckBoxActionPerformed

    private void map_zoom_in_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_map_zoom_in_buttonActionPerformed
        ouiGisPanel.zoomIn();
    }//GEN-LAST:event_map_zoom_in_buttonActionPerformed

    private void map_zoom_rect_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_map_zoom_rect_buttonActionPerformed
        // TODO add your handling code here:
        // Test JMD:
        // Set the ZOOM_FUNCTION_MODE?
        ouiGisPanel.zoomRectMode();
    }//GEN-LAST:event_map_zoom_rect_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem about_menu_item;
    private javax.swing.JLabel featureLabel;
    private javax.swing.JMenu file_menu;
    private javax.swing.JCheckBox fixedCheckBox;
    private javax.swing.JPanel fontSizePanel;
    private javax.swing.JSpinner fontSizeSpinner;
    private javax.swing.JMenu help_menu;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTable loadedTable;
    private javax.swing.JScrollPane loaded_scroll_pane;
    private javax.swing.JPanel mapContentPanel;
    private javax.swing.JPanel mapPanel;
    private javax.swing.ButtonGroup map_button_group;
    private javax.swing.JPanel map_button_panel;
    private javax.swing.JRadioButton map_pan_button;
    private javax.swing.JRadioButton map_select_button;
    private javax.swing.JButton map_zoom_in_button;
    private javax.swing.JButton map_zoom_out_button;
    private javax.swing.JRadioButton map_zoom_rect_button;
    private javax.swing.JPanel mouseClickActionPanel;
    private javax.swing.JMenuItem quit_menu_item;
    private javax.swing.JMenuItem system_help_menu_item;
    private javax.swing.JPanel tracking_panel;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables

    private void quit() {
        logger.info("OUI exited successfully");
        System.exit(0);
    }
}
