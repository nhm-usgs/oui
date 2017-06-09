/*
 * OuiDbfAttributeEditor.java
 *
 * Created on January 12, 2007, 9:04 AM
 */

package oui.util.dbf;

import gov.usgs.cawsc.gui.CloseablePanel;
import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.MenuBarProvider;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Enumeration;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import org.omscentral.gis.model.Feature;
import oui.mms.gui.Mms;
import oui.treetypes.OuiShapeTreeNode;
import oui.util.OuiHelp;

/**
 *
 * @author  markstro
 */
public class OuiDbfAttributeEditor extends JPanel implements MenuBarProvider, CloseablePanel {
    OuiShapeTreeNode _ostn;
    OuiAttributeModel attribute_model;
    private ParameterEditor editor;

    /**
     * Creates new form OuiDbfAttributeEditor
     */
    public OuiDbfAttributeEditor(OuiShapeTreeNode ostn) {
        initComponents();

        _ostn = ostn;
        attribute_model = ostn.getAttributeModel();
        if (attribute_model == null) System.out.println("OuiDbfParameterEditor attribute_model = null");

        setLayout(new BorderLayout());

        editor = new ParameterEditor( new AttributeModelParameterEditorAdapter(attribute_model));
        add("Center", editor);
        setSize(getPreferredSize());
    }

    public JMenuBar getMenuBar() {
        return jMenuBar1;
    }
    
    public void closePanel() {
        _ostn.tableClosed();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveDbfMenuItem = new javax.swing.JMenuItem();
        writeReportMenuItem = new javax.swing.JMenuItem();
        writeXmlMenuItem = new javax.swing.JMenuItem();
        quitMenuItem = new javax.swing.JMenuItem();
        attributeMenu = new javax.swing.JMenu();
        addAttributeMenuItem = new javax.swing.JMenuItem();
        deleteAttributeMenuItem = new javax.swing.JMenuItem();
        labelAttributeMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();

        fileMenu.setText("File");
        saveDbfMenuItem.setText("Save to dbf File");
        saveDbfMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDbfMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveDbfMenuItem);

        writeReportMenuItem.setText("Write Report File");
        writeReportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeReportMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(writeReportMenuItem);

        writeXmlMenuItem.setText("Write XML File");
        writeXmlMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeXmlMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(writeXmlMenuItem);

        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(quitMenuItem);

        jMenuBar1.add(fileMenu);

        attributeMenu.setText("Attribute");
        addAttributeMenuItem.setText("Add Attribute");
        addAttributeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAttributeMenuItemActionPerformed(evt);
            }
        });

        attributeMenu.add(addAttributeMenuItem);

        deleteAttributeMenuItem.setText("Delete Attribute");
        deleteAttributeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAttributeMenuItemActionPerformed(evt);
            }
        });

        attributeMenu.add(deleteAttributeMenuItem);

        labelAttributeMenuItem.setText("Set Label Attribute");
        labelAttributeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelAttributeMenuItemActionPerformed(evt);
            }
        });

        attributeMenu.add(labelAttributeMenuItem);

        jMenuBar1.add(attributeMenu);

        helpMenu.setText("Help");
        helpMenuItem.setText("OUI Users Manual");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(helpMenuItem);

        jMenuBar1.add(helpMenu);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 279, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
        done();
    }//GEN-LAST:event_quitMenuItemActionPerformed

    private void done() {
        _ostn.showTable(new Boolean(false));
    }

    private void writeXmlMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeXmlMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Write XML File");

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if(retval == 0) {
            new DbfFileWriter(attribute_model).writeXML(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_writeXmlMenuItemActionPerformed

    private void writeReportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeReportMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Write Report File");

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if(retval == 0) {
            new DbfFileWriter(attribute_model).writeReport(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_writeReportMenuItemActionPerformed

    private void saveDbfMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDbfMenuItemActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Write DBF File");
        String path = _ostn.getDbfFileName().substring(0, Mms.fileNameIndex(_ostn.getDbfFileName()));
        chooser.setCurrentDirectory(new File(path));
        chooser.setSelectedFile(new File(_ostn.getDbfFileName()));

        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(this));
        if(retval == 0) {
            new DbfFileWriter(attribute_model).writeDBF(chooser.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_saveDbfMenuItemActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuItemActionPerformed
        OuiHelp.OuiHelpFactory().showTopic("OuidbfAttributeEditor");
    }//GEN-LAST:event_helpMenuItemActionPerformed

    private void labelAttributeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelAttributeMenuItemActionPerformed
        JOptionPane.showMessageDialog(GuiUtilities.windowFor(this), "Sorry, not implemented yet!", "Set Label Attribute", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_labelAttributeMenuItemActionPerformed

    private void deleteAttributeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAttributeMenuItemActionPerformed
        int[] sel = editor.getTable().getSelectedColumns();

        if (sel.length == 0) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(this), "Nothing selected!", "Delete Attributes", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Object[] options = {"Yes", "No"};
            String question = "Really delete attribute(s):";
            for (int i = 0; i < sel.length; i++) {
                question = question + " " + editor.getTable().getColumnName(sel[i]);
            }
            question = question + "?";

            int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(this), question, "Delete Attributes", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {
                for (int i = sel.length - 1; i > -1; i--) {  //  Loop runs backwards so that higher number columns are removed first
//                    System.out.println ("sel = " + sel[i]);
                    editor.getTable().removeColumn(editor.getTable().getColumnModel().getColumn(sel[i]));
                    attribute_model.deleteAttribute(sel[i]);

                    // Correct the model indicies int the Table Column objects
                    // by decrementing those indicies that folos the deleted column
                    //
                    JTable table = editor.getTable();
                    Enumeration en = table.getColumnModel().getColumns();
                    for (; en.hasMoreElements();) {
                        TableColumn c = (TableColumn)en.nextElement();
                        if (c.getModelIndex() >= sel[i]) {
                            c.setModelIndex(c.getModelIndex() - 1);
                        }
                    }
                }
            }

//            System.out.println ("col count = " + editor.getTable().getColumnModel().getColumnCount());
        }
    }//GEN-LAST:event_deleteAttributeMenuItemActionPerformed

    private void addAttributeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAttributeMenuItemActionPerformed
        new OuiAttributeAdderFrame (attribute_model, editor.getTable()).setVisible(true);
    }//GEN-LAST:event_addAttributeMenuItemActionPerformed

    public void setSelectedFeatureInTable(Feature feature) {
        if (feature == null) {
            editor.getTable().clearSelection();

        } else {

/*
 * DANGER this seems like a bug in the JTable.  I don't know why I must do a selectAll
 * to get the setRowSelectionInterval call to work.
 */
            editor.getTable().selectAll();
//            editor.getTable().clearSelection();

            for (int i = 0; i < _ostn.getVectorModel().getFeatureCount(); i++) {
                if (feature == _ostn.getVectorModel().getFeature(i)) {
                    editor.getTable().setRowSelectionInterval(i, i);
                    return;
                }
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addAttributeMenuItem;
    private javax.swing.JMenu attributeMenu;
    private javax.swing.JMenuItem deleteAttributeMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem labelAttributeMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JMenuItem saveDbfMenuItem;
    private javax.swing.JMenuItem writeReportMenuItem;
    private javax.swing.JMenuItem writeXmlMenuItem;
    // End of variables declaration//GEN-END:variables

}
