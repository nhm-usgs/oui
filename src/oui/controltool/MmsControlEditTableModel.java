/*
 * MmsParamEditTableModel.java
 *
 */

package oui.controltool;

import oui.mms.datatypes.ControlSet;
import oui.mms.datatypes.Control;
import javax.swing.table.AbstractTableModel;

/** This implements the MmsParamEditTableModel.
 *
 * @author markstro
 * @version 2.0
 */
public class MmsControlEditTableModel extends AbstractTableModel {
    private Object selection;
    private ControlSet mcm;
    
    public MmsControlEditTableModel(ControlSet mcm, Object selection) {
        this.mcm = mcm;
        this.selection = selection;
    }
    
    @Override
    public void addTableModelListener(javax.swing.event.TableModelListener l) {
    }
    @Override
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }
    public int getColumnCount() {
        return 1;
    }
    @Override
    public String getColumnName(int columnIndex) {
        return selection.toString();
    }
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    @Override
    public void removeTableModelListener(javax.swing.event.TableModelListener l) {
    }
    
    public int getRowCount() {
        if (selection == mcm) {
            return 0;
            
        } else {
            Control mc = (Control)(selection);
            return mc.getSize();
        }
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (selection == mcm) {
            return null;
            
        } else {
            Control mc = (Control)(selection);
            return mc.getVals().get(rowIndex);
        }
    }
    
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Control mc = (Control)(selection);
        mc.setValueAt(rowIndex, (String)aValue);
//        this.fireTableCellUpdated(rowIndex, columnIndex);
    }
}