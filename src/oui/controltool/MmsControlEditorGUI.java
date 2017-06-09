/*
 * MmsParameterEditorGUI.java
 *
 * Created on August 27, 2004, 3:37 PM
 */
package oui.controltool;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.MenuBarProvider;
import gov.usgs.cawsc.gui.PersistentSplitterTracker;
import gov.usgs.cawsc.gui.WindowFactory;
import oui.mms.datatypes.ControlSet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import oui.mms.gui.Mms;

/**
 *
 * @author markstro
 */
public class MmsControlEditorGUI extends JPanel implements MenuBarProvider {

    private MmsControlEditTreeModel model;
    private ControlSet mmsControlMap = null;
    private FontMetrics metrics;
    private Insets insets;
    private Font aFont;
    private Color aBackground;
    private Color aForeground;
    private Border border;

    protected PersistentSplitterTracker persistentSplitterTracker = null;

    /**
     * Creates new form MmsParameterEditorGUI
     */
    public MmsControlEditorGUI(ControlSet mmsControlMap) {
        this.mmsControlMap = mmsControlMap;
        initComponents();
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setCellSelectionEnabled(true);
        paramTree.setModel(null);
        loadControlMap(mmsControlMap);
        
        persistentSplitterTracker = new PersistentSplitterTracker(this, jSplitPane1);
    }

    public JMenuBar getMenuBar() {
            return jMenuBar1;
    }

    public void loadControlMap(ControlSet mmsControlMap) {
        if (mmsControlMap == null) {
            return;
        }

        this.mmsControlMap = mmsControlMap;

        if (mmsControlMap.getFileName() != null) {
            //this.setTitle(mmsControlMap.getFileName().substring(Mms.fileNameIndex(mmsControlMap.getFileName()) + 1));
            WindowFactory.instance().setWindowTitle(this, mmsControlMap.getFileName().substring(Mms.fileNameIndex(mmsControlMap.getFileName()) + 1));
        }

        model = new MmsControlEditTreeModel(mmsControlMap);
        paramTree.setModel(model);

        initRowHeaders();
        generateRowHeaders(null);
        paramTree.setSelectionRow(1);
    }

    private void initRowHeaders() {
        // Create a row-header to display row numbers.
        // This row-header is made of labels whose Borders,
        // Foregrounds, Backgrounds, and Fonts must be
        // the one used for the table column headers.
        // Also ensure that the row-header labels and the table
        // rows have the same height.
        TableColumn aColumn = jTable1.getColumnModel().getColumn(0);
        TableCellRenderer aRenderer = jTable1.getTableHeader().getDefaultRenderer();
        if (aRenderer == null) {
            System.out.println(" Aouch !");
            aColumn = jTable1.getColumnModel().getColumn(0);
            aRenderer = aColumn.getHeaderRenderer();
            if (aRenderer == null) {
                System.out.println(" Aouch Aouch !");
                //                System.exit(3);
                exitForm(null);
            }
        }
        Component aComponent = aRenderer.getTableCellRendererComponent(jTable1, aColumn.getHeaderValue(), false, false, -1, 0);
        aFont = aComponent.getFont();
        aBackground = aComponent.getBackground();
        aForeground = aComponent.getForeground();

        border = (Border) UIManager.getDefaults().get("TableHeader.cellBorder");
        insets = border.getBorderInsets(jTable1.getTableHeader());
        metrics = jTable1.getFontMetrics(aFont);
    }

    private void generateRowHeaders(Object selection) {

        /*
         * Creating a panel to be used as the row header.
         *
         * Since I'm not using any LayoutManager,
         * a call to setPreferredSize().
         */
        JPanel pnl = new JPanel((LayoutManager) null);

        Dimension dim = new Dimension(metrics.stringWidth("999") + insets.right + insets.left, jTable1.getRowHeight() * jTable1.getRowCount());

        pnl.setPreferredSize(dim);

        // Adding the row header labels
        dim.height = jTable1.getRowHeight();
        for (int ii = 0; ii < jTable1.getRowCount(); ii++) {
            //            JLabel lbl;
            JButton lbl = new JButton(Integer.toString(ii + 1));
            lbl.setFont(aFont);
            lbl.setBackground(aBackground);
            lbl.setForeground(aForeground);
            lbl.setBorder(border);
            lbl.setBounds(0, ii * dim.height, dim.width, dim.height);
            java.awt.event.ActionListener foo;
            //            lbl.addActionListener(new rowActionListener(ii));
            pnl.add(lbl);
        }

        JViewport vp = new JViewport();
        dim.height = jTable1.getRowHeight() * jTable1.getRowCount();
        vp.setViewSize(dim);
        vp.setView(pnl);
        tableScrollPane.setRowHeader(vp);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do
     * NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        loadButton = new javax.swing.JMenuItem();
        saveButton = new javax.swing.JMenuItem();
        saveAsButton = new javax.swing.JMenuItem();
        exitButton = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        resetThisButton = new javax.swing.JMenuItem();
        resetAllButton = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        treeScrollPane = new javax.swing.JScrollPane();
        paramTree = new javax.swing.JTree();
        tableScrollPane = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        jMenu1.setText("File");

        loadButton.setText("Load");
        loadButton.setToolTipText("Load a new parameter file");
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        jMenu1.add(loadButton);

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jMenu1.add(saveButton);

        saveAsButton.setText("Save As");
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsButtonActionPerformed(evt);
            }
        });
        jMenu1.add(saveAsButton);

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jMenu1.add(exitButton);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Reset");
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });

        resetThisButton.setText("Reset This Table to Original Values");
        jMenu3.add(resetThisButton);

        resetAllButton.setText("Reset All to Original Values");
        resetAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetAllButtonActionPerformed(evt);
            }
        });
        jMenu3.add(resetAllButton);

        jMenuBar1.add(jMenu3);

        paramTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                paramTreeValueChanged(evt);
            }
        });
        treeScrollPane.setViewportView(paramTree);

        jSplitPane1.setLeftComponent(treeScrollPane);

        tableScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tableScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableScrollPane.setViewportView(jTable1);

        jSplitPane1.setRightComponent(tableScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMenuBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jMenuBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Load Control File");

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if (retval == 0) {
            try {
                ControlSet mcm = ControlSet.readMmsControl(chooser.getSelectedFile().getAbsolutePath());
                loadControlMap(mcm);

            } catch (java.io.IOException e) {
                Object[] options = {"OK"};
                int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Invalid control file.", "Warning", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            }
        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void setValueAt(double val, int row, int col) {
        Class type = jTable1.getModel().getColumnClass(col);
        if (type == Integer.class) {
            jTable1.getModel().setValueAt(new Integer((int) val), row, col);
        } else {
            jTable1.getModel().setValueAt(new Double(val), row, col);
        }
    }

    private double getValueAt(int row, int col) {
        Number num_obj;

        Class type = jTable1.getModel().getColumnClass(col);
        if (type == Integer.class) {
            num_obj = (Integer) (jTable1.getValueAt(row, col));
        } else {
            num_obj = (Double) (jTable1.getValueAt(row, col));
        }
        return num_obj.doubleValue();
    }

    private double getUserValue() {
        double val = Double.NaN;
        String inputValue = JOptionPane.showInputDialog(GuiUtilities.windowFor(this), "Please input a value");
        if (inputValue != null) {
            try {
                val = Double.parseDouble(inputValue);
            } catch (NumberFormatException e) {
                Object[] options = {"OK"};
                int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Invalid value.", "Warning", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                val = Double.NaN;
            }
        }
        return val;
    }

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        if (mmsControlMap == null) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Save Control File");

        String path = mmsControlMap.getFileName().substring(0, Mms.fileNameIndex(mmsControlMap.getFileName()));
        chooser.setCurrentDirectory(new File(path));

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if (retval == 0) {
            try {
                ControlSet.writeMmsControl(mmsControlMap, chooser.getSelectedFile().getAbsolutePath());
            } catch (java.io.IOException e) {
                Object[] options = {"OK"};
                int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Invalid control file.", "Warning", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            }
        }
    }//GEN-LAST:event_saveAsButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (mmsControlMap == null) {
            return;
        }

        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Overwrite the control file?\n" + mmsControlMap.getFileName(), "Save Control File", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == 0) {
            try {
                ControlSet.writeMmsControl(mmsControlMap, mmsControlMap.getFileName());
            } catch (java.io.IOException e) {
                Object[] foo = {"OK"};
                n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Invalid control file.", "Warning", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, foo, foo[0]);
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        exitForm(null);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void resetAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAllButtonActionPerformed
        if (mmsControlMap == null) {
            return;
        }
    }//GEN-LAST:event_resetAllButtonActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void paramTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_paramTreeValueChanged
        if (mmsControlMap == null) {
            return;
        }

        if (paramTree.getModel().isLeaf(evt.getPath().getLastPathComponent())) {
            jTable1.setModel(new MmsControlEditTableModel(mmsControlMap, evt.getPath().getLastPathComponent()));
            generateRowHeaders(evt.getPath().getLastPathComponent());

            //  Resize the columns
            TableColumn column = null;
            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                column = jTable1.getColumnModel().getColumn(i);
                column.setPreferredWidth(150);
            }

        } else {
            if (evt.getOldLeadSelectionPath() != evt.getPath()) {
                paramTree.setSelectionPath(evt.getOldLeadSelectionPath());
            }
        }
    }//GEN-LAST:event_paramTreeValueChanged

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        if (topLevel) {
            System.exit(0);
        } else {
            WindowFactory.instance().closeWindow(this);
        }
    }//GEN-LAST:event_exitForm

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //        new MmsParameterEditorGUI().show();
    }

    /**
     * Getter for property topLevel.
     *
     * @return Value of property topLevel.
     */
    public boolean isTopLevel() {
        return this.topLevel;
    }

    /**
     * Setter for property topLevel.
     *
     * @param topLevel New value of property topLevel.
     */
    public void setTopLevel(boolean topLevel) {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitButton;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuItem loadButton;
    private javax.swing.JTree paramTree;
    private javax.swing.JMenuItem resetAllButton;
    private javax.swing.JMenuItem resetThisButton;
    private javax.swing.JMenuItem saveAsButton;
    private javax.swing.JMenuItem saveButton;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables

    /**
     * Holds value of property topLevel.
     */
    private boolean topLevel = false;
}
