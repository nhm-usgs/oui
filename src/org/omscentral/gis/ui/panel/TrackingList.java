package org.omscentral.gis.ui.panel;

import java.awt.Component;

import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.Theme;

import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

public class TrackingList extends JScrollPane implements ThemeViewObserver {
  JList list = new JList();
  DefaultTableModel model = new DefaultTableModel();
  JTable table = new JTable(model);
  Feature[] selected = null;
  Theme tracked;
  GISPanel gis;

  public TrackingList() {
    super();
    table.setAutoResizeMode(table.AUTO_RESIZE_OFF);
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        Layer l = gis.getThemeView().getLayerOf(gis.activeTheme);
        gis.getThemeView().getLayerOf(gis.activeTheme).clearOutlined();
        int[] rows = table.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
          ((VectorLayer)l).setOutline(selected[rows[i]],true);
        }
        gis.repaint();
      }
    });
    //list.setCellRenderer(new ListRenderer());
    super.setViewportView(table);
  }

  public void setGISPanel(GISPanel p) {
    if (p == null) return;
    gis = p;
    gis.getThemeView().addThemeViewObserver(this);
    themeViewUpdated(gis.getThemeView());
  }

  public void clear() {
    while (model.getRowCount() > 0) 
      model.removeRow(model.getRowCount() - 1);
    table.repaint();
  }

  public void themeViewUpdated(ThemeView view) {
    clear();
    ThemeView.Selection selection = view.getSelection(gis.activeTheme);
    if (selection != null) {
      selected = (Feature[]) selection.features.toArray(new Feature[selection.features.size()]);
      for (int i = 0, ii = selected.length; i < ii; i++) {
        model.addRow( tracked.getAttributes(selected[i]) );
      }
    }
    if (tracked != gis.activeTheme && gis.activeTheme != null) {
      tracked = gis.activeTheme;
      model.setColumnIdentifiers(tracked.getAttributeNames());
      initColumnSizes();
    }
    
  }
  
  private void initColumnSizes() {
    TableColumn column = null;
    Component comp = null;
    int headerWidth = 0;
    int cellWidth = 0;
    
    TableColumnModel columns = table.getColumnModel();
    System.out.println("columns " + columns.getColumnCount());
    for (int i = 0; i < columns.getColumnCount(); i++) {
      column = columns.getColumn(i);
      
      try {
        TableCellRenderer renderer = column.getHeaderRenderer();
        if (renderer == null)
          renderer = table.getDefaultRenderer(column.getClass());
        comp = renderer.getTableCellRendererComponent(
          table, column.getHeaderValue(),
          false, false, 0, 0
        );
        headerWidth = comp.getPreferredSize().width;
        headerWidth *= 1.2;
      } catch (NullPointerException e) {
        System.err.println("Null pointer exception!");
        System.err.println("  getHeaderRenderer returns null in 1.3.");
        System.err.println("  The replacement is getDefaultRenderer.");
      }
      
      column.setPreferredWidth(headerWidth);
    }
  }

}
