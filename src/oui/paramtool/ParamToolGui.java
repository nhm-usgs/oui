/*
 * MmsParameterEditorGUI.java
 *
 * Created on August 27, 2004, 3:37 PM
 */
package oui.paramtool;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.MenuBarProvider;
import gov.usgs.cawsc.gui.PersistentSplitterTracker;
import gov.usgs.cawsc.gui.WindowFactory;
import java.awt.*;
import java.io.File;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.mms.gui.Mms;
import oui.mms.io.*;
import oui.util.ExcelAdapter;
import oui.util.OuiHelp;
import oui.util.ReportPanel;
import oui.util.TableSorter;

/**
 *
 * @author  markstro
 */
public class ParamToolGui extends JPanel implements MenuBarProvider {
    private ParamToolTreeModel model;
    private ParameterSet mms_params = null;
    private FontMetrics metrics;
    private Insets insets;
    private Font aFont;
    private Color aBackground;
    private Color aForeground;
    private Border border;
    private TableSorter sorter = null;
    private ParameterSet default_ps = null;

    protected PersistentSplitterTracker persistentSplitterTracker = null;

    /** Creates new form MmsParameterEditorGUI */
    public ParamToolGui(ParameterSet mms_params) {
        initComponents();
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setCellSelectionEnabled(true);
        ExcelAdapter myAd = new ExcelAdapter(jTable1);

        paramTree.setModel(null);
        loadParameterSet(mms_params);
        
        persistentSplitterTracker = new PersistentSplitterTracker(this, jSplitPane1);
    }

    public ParamToolGui(ParameterSet mms_params, ParameterSet default_ps) {
        this(mms_params);
        this.default_ps = default_ps;
    }
    
    public JMenuBar getMenuBar() {
            return jMenuBar1;
    }

    public final void loadParameterSet(ParameterSet mms_params) {
        if (mms_params == null) {
            return;
        }

        this.mms_params = mms_params;

        if (mms_params.getFileName() != null) {
            //this.setTitle(mms_params.getFileName().substring(Mms.fileNameIndex(mms_params.getFileName()) + 1));
            WindowFactory.instance().setWindowTitle(this, mms_params.getFileName().substring(Mms.fileNameIndex(mms_params.getFileName()) + 1));
        }

        model = new ParamToolTreeModel(mms_params);
        paramTree.setModel(model);

        initRowHeaders();
        generateRowHeaders();
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

    private void generateRowHeaders() {

        /*
         * Creating a panel to be used as the row header.
         *
         * Since I'm not using any LayoutManager,
         * a call to setPreferredSize().
         */
        JPanel pnl = new JPanel((LayoutManager) null);

        java.awt.Dimension dim;
        Object selection = null;
        if (paramTree.getSelectionCount() > 0) {
            selection = paramTree.getSelectionPath().getLastPathComponent();
        }

        int digitcount = 1;
        if (selection == ParamToolTreeModel.dimensionSizes) {
            digitcount = 12;
        } else {

            Iterator<Dimension> iterator = mms_params.getDimenIterator();
            while (iterator.hasNext()) {
                Dimension next = iterator.next();
                int size = next.getSize();
                if (size == 0) {
                    digitcount = 1;
                } else {
                    digitcount = (int) (Math.log10((double) size)) + 1;
                }
            }
        }

        // Add some padding to make sure that this will work
        digitcount = digitcount + 4;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < digitcount; i++) {
            buf.append('9');
        }
        String foo = new String(buf);
        dim = new java.awt.Dimension(metrics.stringWidth(foo) + insets.right + insets.left, jTable1.getRowHeight() * jTable1.getRowCount());

        pnl.setPreferredSize(dim);

        // Adding the row header labels
        dim.height = jTable1.getRowHeight();

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            int ii;
            if (sorter != null) {
                ii = sorter.modelIndex(i);
            } else {
                ii = i;
            }

            JButton lbl;
            if (selection == ParamToolTreeModel.dimensionSizes) {
                Dimension mms_dim = mms_params.getDimensionAt(i);
                lbl = new JButton(mms_dim.getName());
            } else {
                lbl = new JButton(Integer.toString(ii + 1));
            }

            lbl.setFont(aFont);
            lbl.setBackground(aBackground);
            lbl.setForeground(aForeground);
            lbl.setBorder(border);
            lbl.setBounds(0, i * dim.height, dim.width, dim.height);
            pnl.add(lbl);
        }

        JViewport vp = new JViewport();
        dim.height = jTable1.getRowHeight() * jTable1.getRowCount();
        vp.setViewSize(dim);
        vp.setView(pnl);
        tableScrollPane.setRowHeader(vp);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
        jMenu2 = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        copyB = new javax.swing.JButton();
        addB = new javax.swing.JButton();
        subB1 = new javax.swing.JButton();
        multB = new javax.swing.JButton();
        divB = new javax.swing.JButton();
        selAll = new javax.swing.JButton();
        selCol = new javax.swing.JButton();
        selRow = new javax.swing.JButton();
        fixParamFileButton = new javax.swing.JButton();
        expandorButton = new javax.swing.JButton();
        report = new javax.swing.JButton();
        diff = new javax.swing.JButton();
        parameterizer = new javax.swing.JButton();
        history = new javax.swing.JButton();
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

        jMenu2.setText("Help");

        helpMenuItem.setText("OUI Users Manual");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuSelected(evt);
            }
        });
        jMenu2.add(helpMenuItem);

        jMenuBar1.add(jMenu2);

        copyB.setText("Copy");
        copyB.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        copyB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBActionPerformed(evt);
            }
        });
        jToolBar1.add(copyB);

        addB.setText("+");
        addB.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBActionPerformed(evt);
            }
        });
        jToolBar1.add(addB);

        subB1.setText("-");
        subB1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        subB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                subB1ActionPerformed(evt);
            }
        });
        jToolBar1.add(subB1);

        multB.setText("X");
        multB.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        multB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multBActionPerformed(evt);
            }
        });
        jToolBar1.add(multB);

        divB.setText("/");
        divB.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        divB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                divBActionPerformed(evt);
            }
        });
        jToolBar1.add(divB);

        selAll.setText("All");
        selAll.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        selAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selAllActionPerformed(evt);
            }
        });
        jToolBar1.add(selAll);

        selCol.setText("Columns");
        selCol.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        selCol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selColActionPerformed(evt);
            }
        });
        jToolBar1.add(selCol);

        selRow.setText("Rows");
        selRow.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        selRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selRowActionPerformed(evt);
            }
        });
        jToolBar1.add(selRow);

        fixParamFileButton.setText("Fix It");
        fixParamFileButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fixParamFileButton.setFocusable(false);
        fixParamFileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fixParamFileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fixParamFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixParamFileButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(fixParamFileButton);

        expandorButton.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        expandorButton.setText("The Expandor");
        expandorButton.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        expandorButton.setFocusable(false);
        expandorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        expandorButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        expandorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandorButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(expandorButton);

        report.setText("Report");
        report.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportActionPerformed(evt);
            }
        });
        jToolBar1.add(report);

        diff.setText("Difference");
        diff.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        diff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                diffActionPerformed(evt);
            }
        });
        jToolBar1.add(diff);

        parameterizer.setText("Compare to Defaults");
        parameterizer.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        parameterizer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parameterizerActionPerformed(evt);
            }
        });
        jToolBar1.add(parameterizer);

        history.setText("Describe");
        history.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        history.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                describeActionPerformed(evt);
            }
        });
        jToolBar1.add(history);

        jSplitPane1.setResizeWeight(0.33);

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
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableScrollPane.setViewportView(jTable1);

        jSplitPane1.setRightComponent(tableScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMenuBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jMenuBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void helpMenuSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuSelected
        OuiHelp.OuiHelpFactory().showTopic("ParamToolGuich");
    }//GEN-LAST:event_helpMenuSelected

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Load Parameter File");

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if (retval == 0) {
            try {
                MmsParamsReader mp = new MmsParamsReader(chooser.getSelectedFile().getAbsolutePath());
                ParameterSet ps = mp.read();
                loadParameterSet(ps);

            } catch (java.io.IOException e) {
                Object[] options = {"OK"};
                int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Invalid parameter file.", "Warning", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            }
        }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void subB1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subB1ActionPerformed
        if (mms_params == null) {
            return;
        }

        double val = getUserValue();

        if (!Double.isNaN(val)) {
            int[] sel_rows = jTable1.getSelectedRows();
            int[] sel_cols = jTable1.getSelectedColumns();

            for (int i = 0; i < sel_rows.length; i++) {
                for (int j = 0; j < sel_cols.length; j++) {
                    setValueAt(getValueAt(sel_rows[i], sel_cols[j]) - val, sel_rows[i], sel_cols[j]);
                }
            }
            jTable1.clearSelection();
        }
    }//GEN-LAST:event_subB1ActionPerformed

    private void addBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBActionPerformed
        if (mms_params == null) {
            return;
        }

        double val = getUserValue();

        if (!Double.isNaN(val)) {
            int[] sel_rows = jTable1.getSelectedRows();
            int[] sel_cols = jTable1.getSelectedColumns();

            for (int i = 0; i < sel_rows.length; i++) {
                for (int j = 0; j < sel_cols.length; j++) {
                    setValueAt(getValueAt(sel_rows[i], sel_cols[j]) + val, sel_rows[i], sel_cols[j]);
                }
            }
            jTable1.clearSelection();
        }
    }//GEN-LAST:event_addBActionPerformed

    private void copyBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyBActionPerformed
        if (mms_params == null) {
            return;
        }

        double val = getUserValue();
        if (!Double.isNaN(val)) {
            int[] sel_rows = jTable1.getSelectedRows();
            int[] sel_cols = jTable1.getSelectedColumns();

            for (int i = 0; i < sel_rows.length; i++) {
                for (int j = 0; j < sel_cols.length; j++) {
                    setValueAt(val, sel_rows[i], sel_cols[j]);
                }
            }
            jTable1.clearSelection();
        }
    }//GEN-LAST:event_copyBActionPerformed

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

    private void describeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_describeActionPerformed
        String foo;

        if (mms_params == null) {
            return;
        }

        int[] sel = jTable1.getSelectedColumns();
        if (sel == null || sel.length == 0) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(this), "Nothing selected!", "Parameter Description", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (default_ps == null) {
            loadDefaultParamFile();
        }
        if (default_ps == null) {
            return;
        }

        for (int i = 0; i < sel.length; i++) {
            String colName = jTable1.getModel().getColumnName(jTable1.convertColumnIndexToModel(sel[i]));

            if (colName.contentEquals("Dimension Size")) {
                int[] rows = jTable1.getSelectedRows();
                if (rows.length > 0) {
                    Dimension mms_dim = default_ps.getDimensionAt(0);
                    mms_dim.getName();

                    if (mms_dim == null) {
                        foo = "No description available for " + default_ps.getDimensionAt(0);
                    } else {
                        foo = "Dimension: " + mms_dim.getName() + "\n"
                                + "Description: " + mms_dim.getDesc() + "\n";
                    }

                    JOptionPane.showMessageDialog(GuiUtilities.windowFor(this), foo, "Dimension Description", JOptionPane.INFORMATION_MESSAGE);
                }

            } else {
                Parameter param = default_ps.getParameter(colName);

                if (param == null) {
                    foo = "No description available for " + colName;
                } else {
                    foo = "Name: " + param.getName() + "\n"
                            + "Module: " + param.getModule() + "\n"
                            + "Description: " + param.getDesc() + "\n"
                            + "Dimension(s): " + param.getDimension(0) + "\n"
                            + "Default: " + param.getDefaultVal() + "\n"
                            + "Range: " + param.getLowBound() + " - " + param.getUpBound() + "\n"
                            + "Units: " + param.getUnits() + "\n"
                            + "Type: " + param.getType();
                }
                JOptionPane.showMessageDialog(GuiUtilities.windowFor(this), foo, "Parameter Description", JOptionPane.INFORMATION_MESSAGE);
            }
        }
}//GEN-LAST:event_describeActionPerformed

    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
        if (mms_params == null) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Save Parameter File");

        String path = mms_params.getFileName().substring(0, Mms.fileNameIndex(mms_params.getFileName()));
        chooser.setCurrentDirectory(new File(path));

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if (retval == 0) {
            MmsParamsWriter.write(chooser.getSelectedFile().getAbsolutePath(), mms_params);
        }
    }//GEN-LAST:event_saveAsButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (mms_params == null) {
            return;
        }

        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Overwrite the Parameter file?\n" + mms_params.getFileName(), "Save Parameter File", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == 0) {
            MmsParamsWriter.write(mms_params.getFileName(), mms_params);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void parameterizerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parameterizerActionPerformed
        if (mms_params == null) {
            return;
        }
        if (default_ps == null) {
            loadDefaultParamFile();
        }
        if (default_ps == null) {
            return;
        }

        ReportPanel reportPanel = new ReportPanel();
        WindowFactory.displayInFrame(reportPanel, "OUI Parameter Difference Report");

        MmsParamsDifference.diff(reportPanel.getWriter(), mms_params, default_ps);
    }//GEN-LAST:event_parameterizerActionPerformed

    private void loadDefaultParamFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Select MMF default parameter file");

        int path_index = Mms.fileNameIndex(mms_params.getFileName());

        if (path_index != -1) {
            String path = mms_params.getFileName().substring(0, path_index);
            chooser.setCurrentDirectory(new File(path));
        }

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if (retval == 0) {

            try {
                MmsDefaultParamsReader mdpr = new MmsDefaultParamsReader(chooser.getSelectedFile().getAbsolutePath());
                default_ps = (ParameterSet) mdpr.read();

            } catch (java.io.IOException e) {
                System.out.println(mms_params.getFileName() + " io exception");
            }
        }
    }

    private void diffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_diffActionPerformed
        if (mms_params == null) {
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Select a Parameter File");

        int path_index = Mms.fileNameIndex(mms_params.getFileName());

        if (path_index != -1) {
            String path = mms_params.getFileName().substring(0, path_index);
            chooser.setCurrentDirectory(new File(path));
        }

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if (retval == 0) {
            ReportPanel reportPanel = new ReportPanel();
            WindowFactory.displayInFrame(reportPanel, "OUI Parameter Difference Report");

            try {
                MmsParamsReader mp2 = new MmsParamsReader(chooser.getSelectedFile().getAbsolutePath());
                ParameterSet ps2 = mp2.read();
                MmsParamsDifference.diff(reportPanel.getWriter(), mms_params, ps2);

            } catch (java.io.IOException e) {
                System.out.println(mms_params.getFileName() + " io exception");
            }
        }
    }//GEN-LAST:event_diffActionPerformed

    private void selRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selRowActionPerformed
        jTable1.setColumnSelectionInterval(0, jTable1.getColumnCount() - 1);
    }//GEN-LAST:event_selRowActionPerformed

    private void selColActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selColActionPerformed
        jTable1.setRowSelectionInterval(0, jTable1.getRowCount() - 1);
    }//GEN-LAST:event_selColActionPerformed

    private void selAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selAllActionPerformed
        jTable1.selectAll();
    }//GEN-LAST:event_selAllActionPerformed

    private void divBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_divBActionPerformed
        if (mms_params == null) {
            return;
        }

        double val = getUserValue();

        if (val == 0.0) {
            Object[] options = {"OK"};
            int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), "Invalid value.", "Warning", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            val = Double.NaN;
        }

        if (!Double.isNaN(val)) {
            int[] sel_rows = jTable1.getSelectedRows();
            int[] sel_cols = jTable1.getSelectedColumns();

            for (int i = 0; i < sel_rows.length; i++) {
                for (int j = 0; j < sel_cols.length; j++) {
                    setValueAt(getValueAt(sel_rows[i], sel_cols[j]) / val, sel_rows[i], sel_cols[j]);
                }
            }
            jTable1.clearSelection();
        }
    }//GEN-LAST:event_divBActionPerformed

    private void multBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multBActionPerformed
        if (mms_params == null) {
            return;
        }

        double val = getUserValue();

        if (!Double.isNaN(val)) {
            int[] sel_rows = jTable1.getSelectedRows();
            int[] sel_cols = jTable1.getSelectedColumns();

            for (int i = 0; i < sel_rows.length; i++) {
                for (int j = 0; j < sel_cols.length; j++) {
                    setValueAt(getValueAt(sel_rows[i], sel_cols[j]) * val, sel_rows[i], sel_cols[j]);
                }
            }
            jTable1.clearSelection();
        }
    }//GEN-LAST:event_multBActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        exitForm(null);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void resetAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetAllButtonActionPerformed
        if (mms_params == null) {
//            return;
        }
    }//GEN-LAST:event_resetAllButtonActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void paramTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_paramTreeValueChanged
        if (mms_params == null) {
            return;
        }

        if (paramTree.getModel().isLeaf(evt.getPath().getLastPathComponent())) {
            sorter = new TableSorter(new ParamToolTableModel(mms_params, evt.getPath().getLastPathComponent()));

            sorter.addTableModelListener(new javax.swing.event.TableModelListener() {

                @Override
                public void tableChanged(javax.swing.event.TableModelEvent evt) {
                    generateRowHeaders();
                }
            });

            jTable1.setModel(sorter);

            sorter.setTableHeader(jTable1.getTableHeader()); //ADDED THIS

            generateRowHeaders();

            //  Resize the columns
            TableColumn column;
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

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        if (topLevel) {
            System.exit(0);
        } else {
            WindowFactory.instance().closeWindow(this);
        }
    }//GEN-LAST:event_exitForm

    private void fixParamFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixParamFileButtonActionPerformed
        ParamToolFileFixer paramToolFileFixer = new ParamToolFileFixer(this, default_ps, mms_params);
    }//GEN-LAST:event_fixParamFileButtonActionPerformed

    private void reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportActionPerformed
        if (mms_params == null) {
            return;
        }

        ReportPanel reportPanel = new ReportPanel();
        WindowFactory.displayInFrame(reportPanel, "OUI Parameter Report");
        MmsParamsReport.write(reportPanel.getWriter(), mms_params);
    }//GEN-LAST:event_reportActionPerformed

    private void expandorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandorButtonActionPerformed
        ParamToolExpandor paramToolExpandor = new ParamToolExpandor(this, default_ps, mms_params);
    }//GEN-LAST:event_expandorButtonActionPerformed

    /**
     * Getter for property topLevel.
     * @return Value of property topLevel.
     */
    public boolean isTopLevel() {
        return this.topLevel;
    }

    /**
     * Setter for property topLevel.
     * @param topLevel New value of property topLevel.
     */
    public void setTopLevel(boolean topLevel) {
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addB;
    private javax.swing.JButton copyB;
    private javax.swing.JButton diff;
    private javax.swing.JButton divB;
    private javax.swing.JMenuItem exitButton;
    private javax.swing.JButton expandorButton;
    private javax.swing.JButton fixParamFileButton;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton history;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem loadButton;
    private javax.swing.JButton multB;
    private javax.swing.JTree paramTree;
    private javax.swing.JButton parameterizer;
    private javax.swing.JButton report;
    private javax.swing.JMenuItem resetAllButton;
    private javax.swing.JMenuItem resetThisButton;
    private javax.swing.JMenuItem saveAsButton;
    private javax.swing.JMenuItem saveButton;
    private javax.swing.JButton selAll;
    private javax.swing.JButton selCol;
    private javax.swing.JButton selRow;
    private javax.swing.JButton subB1;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables
    /**
     * Holds value of property topLevel.
     */
    private boolean topLevel = false;
}
