package org.omscentral.gis.model;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.awt.Rectangle;
import java.util.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class VAT {
  public static final int FLOAT = 1;
  public static final int INT = 2;
  public static final String VALUES = "Values";
  public static final String COUNT = "Count";

  List<Column> columns = new ArrayList<Column>(2);
  AbstractTableModel tableModel = new VATModel();
  protected String title = "VAT";

  public VAT(int[] vals,int[] cnts,String title) {
    Column val = addColumn(VALUES,INT);
    val.setData(vals);
    Column cnt = addColumn(COUNT,INT);
    cnt.setData(cnts);
    this.title = title;
  }

  public VAT(double[] vals,int[] cnts,String title) {
    Column val = addColumn(VALUES,FLOAT);
    val.setData(vals);
    Column cnt = addColumn(COUNT,INT);
    cnt.setData(cnts);
    this.title = title;
  }

  public Number getMin(String name) {
    Column cl = getColumn(name);
    return cl.getMin();
  }

  public Number getMax(String name) {
    Column cl = getColumn(name);
    return cl.getMax();
  }

  public Number lookup(String name,double val) {
    Column c = (Column) columns.get(0);
    int idx = c.lookup(val);
    if (idx >= 0) {
      Column cl = getColumn(name);
      if (cl != null) {
        return cl.getValueAt(idx);
      }
    }
    return null;
  }

  public boolean hasColumn(String name) {
    for (int i = 0; i < columns.size(); i++) {
      if ( ((Column)columns.get(i)).title.equals(name) )
        return true;
    }
    return false;
  }

  public void join(VAT vat) {
    for (int i = 0; i < vat.columns.size(); i++) {
      try {
//        addColumn( (Column) vat.columns.get(i) );
        columns.add( (Column) vat.columns.get(i) );
      } catch (RuntimeException re) {
        re.printStackTrace();
      }
    }
    tableModel.fireTableStructureChanged();
  }

  public void addColumn(Column col) {
    Column c = getColumn(col.title);
    if (c != null)
      throw new RuntimeException("Column " + col.title + " exists");
    columns.add(col);
    tableModel.fireTableStructureChanged();
  }

  public Column addColumn(String title,int type) {
    Column c = getColumn(title);
    if (c != null)
      throw new RuntimeException("Column " + title + " exists");
    if (type == INT)
      c = new IntColumn(title,getRowCnt());
    else if (type == FLOAT)
      c = new FloatColumn(title,getRowCnt());
    else
      throw new RuntimeException("Column type must be FLOAT or INT");
    columns.add(c);
    tableModel.fireTableStructureChanged();
    return c;
  }

  protected int getRowCnt() {
    int rows = 0;
    if (columns.size() > 0) {
      Column c = (Column) columns.get(0);
      rows = c.size();
    }
    return rows;
  }

  public void dropColumn(String title) {
    if (title.equals(VALUES) || title.equals(COUNT))
      return;
    Column c = getColumn(title);
    columns.remove(c);
    tableModel.fireTableStructureChanged();
  }

  public Column getColumn(String title) {
    for (int i = 0; i < columns.size(); i++) {
      Column c = (Column) columns.get(i);
      if (c.title.equals(title))
        return c;
    }
    return null;
  }

  public String[] getColumnNames() {
    String[] names = new String[columns.size()];
    for (int i = 0; i < names.length; i++) {
      names[i] = ( (Column) columns.get(i) ).getTitle();
    }
    return names;
  }

  public abstract class Column {
    private String title;
    public Column(String title) {
      this.title = title;
    }
    public void setTitle(String title) {
      this.title = title;
      tableModel.fireTableStructureChanged();
    }
    public String getTitle() {
      return title;
    }
    public abstract int size();
    public abstract Number getMin();
    public abstract Number getMax();
    public abstract Number getValueAt(int row);
    public abstract void setValueAt(int row,Object val);
    public abstract void setValueAt(int row,int val);
    public abstract void setValueAt(int row,double val);
    public abstract void setData(Object o);
    public abstract Object getData();
    public abstract int lookup(double val);
    public boolean equals(Object o) {
      if (o == null || !(o instanceof Column)) return false;
      return title.equals(((Column)o).title);
    }
    public String toString() {
      StringBuffer buff = new StringBuffer();
      buff.append("VAT Column : " + title + "\n");
      for (int i = 0; i < size(); i++) {
        buff.append(" " + getValueAt(i));
      }
      return buff.toString();
    }
  }

  public javax.swing.table.TableModel getTableModel() {
    return tableModel;
  }

  public void showTable() {
    JFrame f = new JFrame(title);
    JScrollPane js = new JScrollPane();
    JTable jt = new JTable(tableModel);
    jt.setAutoCreateColumnsFromModel(true);
    js.setViewportView(jt);
    f.setContentPane(js);
    f.pack();
    
    JFrame mainFrame = WindowFactory.instance().getMainFrame();
    Rectangle rect = GuiUtilities.getCenterLocation(mainFrame, f.getWidth(), f.getHeight());
    f.setLocation(rect.x, rect.y);

    f.setLocationRelativeTo(WindowFactory.instance().getMainFrame());
    f.setVisible(true);
  }

  public class IntColumn extends Column {
    int[] data;

    public IntColumn (String title,int size) {
      super(title);
      data = new int[size];
      for (int i = 0; i < data.length; i++) {
        data[i] = 0;
      }
    }

    public IntColumn (String title,int[] data) {
      super(title);
      this.data = data;
    }

    public Number getMin() {
      int[] dc = new int[data.length];
      System.arraycopy(data,0,dc,0,data.length);
      Arrays.sort(dc);
      return new Integer(dc[0]);
    }

    public Number getMax() {
      int[] dc = new int[data.length];
      System.arraycopy(data,0,dc,0,data.length);
      Arrays.sort(dc);
      return new Integer(dc[dc.length - 1]);
    }

    public int size() {
      return data.length;
    }

    public int lookup(double val) {
      for (int i = 0; i < data.length; i++) {
        if ((double)data[i] == val)
          return i;
      }
      return -1;
    }

    public Number getValueAt(int row) {
      return new Integer(data[row]);
    }

    public void setValueAt(int row,Object val) {
      try {
        data[row] = Integer.parseInt(val.toString());
      } catch (NumberFormatException nfe) {}
      tableModel.fireTableDataChanged();
    }

    public void setValueAt(int row,int val) {
      data[row] = val;
      tableModel.fireTableDataChanged();
    }

    public void setValueAt(int row,double val) {
      data[row] = (int)val;
      tableModel.fireTableDataChanged();
    }

    public void setData(Object o) {
      if (o instanceof int[])
        data = (int[]) o;
      else
        throw new RuntimeException("IntColumn needs int[]");
      tableModel.fireTableDataChanged();
    }

    public Object getData() {
      return data;
    }

  }

  public class FloatColumn extends Column {
    double[] data;

    public FloatColumn (String title,int size) {
      super(title);
      data = new double[size];
      for (int i = 0; i < data.length; i++) {
        data[i] = 0;
      }
    }

    public FloatColumn (String title,double[] data) {
      super(title);
      this.data = data;
    }

    public Number getMin() {
      double[] dc = new double[data.length];
      System.arraycopy(data,0,dc,0,data.length);
      Arrays.sort(dc);
      return new Double(dc[0]);
    }

    public Number getMax() {
      double[] dc = new double[data.length];
      System.arraycopy(data,0,dc,0,data.length);
      Arrays.sort(dc);
      return new Double(dc[dc.length - 1]);
    }

    public int size() {
      return data.length;
    }

    public int lookup(double val) {
      for (int i = 0; i < data.length; i++) {
        if (data[i] == val)
          return i;
      }
      return -1;
    }

    public Number getValueAt(int row) {
      return new Double(data[row]);
    }

    public void setValueAt(int row,Object val) {
      try {
        data[row] = Double.parseDouble(val.toString());
      } catch (NumberFormatException nfe) {}
      tableModel.fireTableDataChanged();
    }

    public void setValueAt(int row,int val) {
      data[row] = (double) val;
      tableModel.fireTableDataChanged();
    }

    public void setValueAt(int row,double val) {
      data[row] = val;
      tableModel.fireTableDataChanged();
    }

    public void setData(Object o) {
      if (o instanceof double[])
        data = (double[]) o;
      else
        throw new RuntimeException("FloatColumn needs double[]");
      tableModel.fireTableDataChanged();
    }

    public Object getData() {
      return data;
    }
  }

  class VATModel extends AbstractTableModel {

    public int getRowCount() {
      return getRowCnt();
    }


    public int getColumnCount() {
      return columns.size();
    }


    public String getColumnName(int columnIndex)  {
      Column c = (Column) columns.get(columnIndex);
      return c.title;
    }


    public Class getColumnClass(int columnIndex)  {
      return String.class;
    }


    public boolean isCellEditable(int rowIndex, int columnIndex)  {
      if (columnIndex > 1)
        return true;
      return false;
    }


    public Object getValueAt(int rowIndex, int columnIndex)  {
      Column c = (Column) columns.get(columnIndex);
      return c.getValueAt(rowIndex);
    }


    public void setValueAt(Object value, int rowIndex, int columnIndex)  {
      Column c = (Column) columns.get(columnIndex);
      c.setValueAt(rowIndex,value);
    }
  }
}
