package oui.util.dbf;

import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.table.*;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.event.*;

public class ParameterEditor extends JPanel {
   ParameterEditorModel pe_model;
   JTable table;
   JPopupMenu popup = new DefaultParameterEditorPopupMenu ();

   public ParameterEditor (ParameterEditorModel pe_model) {
      this.pe_model = pe_model;
      this.setLayout (new BorderLayout());

      this.table = new JTable (pe_model);
      table.setAutoResizeMode(table.AUTO_RESIZE_OFF);
      table.setSelectionForeground(Color.red);
      table.setRowSelectionAllowed(true);
      table.setColumnSelectionAllowed(true);

      table.addMouseListener(new MouseAdapter () {
         public void mousePressed (MouseEvent e) {maybeShowPopup(e);}
         public void mouseReleased(MouseEvent e) {maybeShowPopup(e);}
         private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(),
                           e.getX(), e.getY());
            }
        }
      });

      DefaultListModel row_list_model = new DefaultListModel ();
      JList row_header = new JList (row_list_model);
//
//
// DANGER Should figure out the width based on name or value size
//      row_header.setFixedCellWidth(50);

      row_header.setFixedCellHeight(table.getRowHeight());
      row_header.setCellRenderer (new RowHeaderRenderer (table));
      for (int i = 0; i < pe_model.getRowCount (); i++) {
         row_list_model.addElement (pe_model.getRowName(i));
      }

      row_header.addListSelectionListener(new EditParamRowSelectionListener (this, table));

      JTableHeader col_header = table.getTableHeader();
      col_header.addMouseListener (new EditParamColumnSelectionListener (this, table));

      JScrollPane scrollpane = new JScrollPane (table);
      row_header.setBackground(scrollpane.getBackground());
      scrollpane.setRowHeaderView (row_header);
      this.add (scrollpane, BorderLayout.CENTER);
   }

   public ParameterEditorModel getModel () {return pe_model;}
   public JTable getTable () {return table;}
   public JPopupMenu getPopupMenu () {return popup;}
   public void setPopupMenu (JPopupMenu popup) {this.popup = popup;}

class DefaultParameterEditorPopupMenu extends JPopupMenu {
   public DefaultParameterEditorPopupMenu () {
      JMenuItem menuItem = new JMenuItem("Functions");
      add(menuItem);
      add (new JSeparator ());

      menuItem = new JMenuItem ("Fill Selected Cells");
      add (menuItem);
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CustomDialog cd = new CustomDialog ("Enter fill cell value", table);
            java.awt.Dimension size = cd.getPreferredSize();
            size.setSize(size.getWidth(), size.getHeight() + 50);
            cd.setSize(size);
            cd.setTitle("Fill Cells");
            cd.setVisible(true);

            Object new_val = cd.getValidatedInput();
            if (new_val == null) return;

            for (int row = 0; row < table.getRowCount(); row++) {
               for (int col = 0; col < table.getColumnCount(); col++) {
                  if (table.isCellSelected(row, col)) {
                     table.setValueAt(new_val, row, col);
                  }
               }
            }
      }});

      menuItem = new JMenuItem ("Multiply Selected Cells");
      add (menuItem);
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CustomDialog cd = new CustomDialog ("Multiply Cells", table);
            java.awt.Dimension size = cd.getPreferredSize();
            size.setSize(size.getWidth(), size.getHeight() + 50);
            cd.setSize(size);
            cd.setTitle("Enter multiplier value");
            cd.setVisible(true);

            Object new_val = cd.getValidatedInput();
            if (new_val == null) return;

            for (int row = 0; row < table.getRowCount(); row++) {
               for (int col = 0; col < table.getColumnCount(); col++) {
                  if (table.isCellSelected(row, col)) {
                     if (pe_model.getColumnClass(col) == Float.class) {
                        float val = ((Float)(table.getValueAt(row, col))).floatValue();
                        float mult = ((Float)new_val).floatValue();
                        table.setValueAt (new Float (val * mult), row, col);
                     } else {
                        int val = ((Integer)(table.getValueAt(row, col))).intValue();
                        int mult = ((Integer)new_val).intValue();
                        table.setValueAt (new Integer (val * mult), row, col);
                     }
                  }
               }
            }
      }});

      menuItem = new JMenuItem ("Divide Selected Cells");
      add (menuItem);
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CustomDialog cd = new CustomDialog ("Divide Cells", table);
            java.awt.Dimension size = cd.getPreferredSize();
            size.setSize(size.getWidth(), size.getHeight() + 50);
            cd.setSize(size);
            cd.setTitle("Enter divisor value");
            cd.setVisible(true);

            Object new_val = cd.getValidatedInput();
            if (new_val == null) return;

            for (int row = 0; row < table.getRowCount(); row++) {
               for (int col = 0; col < table.getColumnCount(); col++) {
                  if (table.isCellSelected(row, col)) {
                     if (pe_model.getColumnClass(col) == Float.class) {
                        float val = ((Float)(table.getValueAt(row, col))).floatValue();
                        float mult = ((Float)new_val).floatValue();
                        table.setValueAt (new Float (val / mult), row, col);
                     } else {
                        int val = ((Integer)(table.getValueAt(row, col))).intValue();
                        int mult = ((Integer)new_val).intValue();
                        table.setValueAt (new Integer (val / mult), row, col);
                     }
                  }
               }
            }
      }});

      menuItem = new JMenuItem ("Add Selected Cells");
      add (menuItem);
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CustomDialog cd = new CustomDialog ("Add Cells", table);
            java.awt.Dimension size = cd.getPreferredSize();
            size.setSize(size.getWidth(), size.getHeight() + 50);
            cd.setSize(size);
            cd.setTitle("Enter additive value");
            cd.setVisible(true);

            Object new_val = cd.getValidatedInput();
            if (new_val == null) return;

            for (int row = 0; row < table.getRowCount(); row++) {
               for (int col = 0; col < table.getColumnCount(); col++) {
                  if (table.isCellSelected(row, col)) {
                     if (pe_model.getColumnClass(col) == Float.class) {
                        float val = ((Float)(table.getValueAt(row, col))).floatValue();
                        float mult = ((Float)new_val).floatValue();
                        table.setValueAt (new Float (val + mult), row, col);
                     } else {
                        int val = ((Integer)(table.getValueAt(row, col))).intValue();
                        int mult = ((Integer)new_val).intValue();
                        table.setValueAt (new Integer (val + mult), row, col);
                     }
                  }
               }
            }
      }});

      menuItem = new JMenuItem ("Subtract Selected Cells");
      add (menuItem);
      menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            CustomDialog cd = new CustomDialog ("Subtract Cells", table);
            java.awt.Dimension size = cd.getPreferredSize();
            size.setSize(size.getWidth(), size.getHeight() + 50);
            cd.setSize(size);
            cd.setTitle("Enter subtractor value");
            cd.setVisible(true);

            Object new_val = cd.getValidatedInput();
            if (new_val == null) return;

            for (int row = 0; row < table.getRowCount(); row++) {
               for (int col = 0; col < table.getColumnCount(); col++) {
                  if (table.isCellSelected(row, col)) {
                     if (pe_model.getColumnClass(col) == Float.class) {
                        float val = ((Float)(table.getValueAt(row, col))).floatValue();
                        float mult = ((Float)new_val).floatValue();
                        table.setValueAt (new Float (val - mult), row, col);
                     } else {
                        int val = ((Integer)(table.getValueAt(row, col))).intValue();
                        int mult = ((Integer)new_val).intValue();
                        table.setValueAt (new Integer (val - mult), row, col);
                     }
                  }
               }
            }
      }});
   }
}

class EditParamRowSelectionListener implements ListSelectionListener {
   ParameterEditor mms_params;
   JTable table;
   int last_value = -1;

   public EditParamRowSelectionListener (ParameterEditor mms_params, JTable table) {
      this.mms_params = mms_params;
      this.table = table;
   }

   public void valueChanged (ListSelectionEvent e) {

      if (e.getValueIsAdjusting ()) return;
      if ((e.getLastIndex () == e.getFirstIndex ()) && (last_value != -1)) return;

      int new_value;
      if (last_value == -1) {
         new_value = e.getFirstIndex ();
      } else {
         if (last_value == e.getFirstIndex ()) {
            new_value = e.getLastIndex ();
         } else {
            new_value = e.getFirstIndex ();
         }
      }

      table.setRowSelectionInterval(new_value, new_value);
      table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
      last_value = new_value;
   }
}

class EditParamColumnSelectionListener extends MouseAdapter {
   ParameterEditor mms_params;
   JTable table;

   public  EditParamColumnSelectionListener (ParameterEditor mms_params, JTable table) {
      this.mms_params = mms_params;
      this.table = table;
   }

   public void mouseClicked (MouseEvent e) {
      TableColumnModel columnModel = table.getColumnModel();
      int viewColumn = columnModel.getColumnIndexAtX(e.getX());
      int column = viewColumn;
      if (e.getClickCount() == 1 && column != -1) {
         table.setColumnSelectionInterval(column,column);
         table.setRowSelectionInterval(0,table.getRowCount()-1);
      }
   }
}

}

