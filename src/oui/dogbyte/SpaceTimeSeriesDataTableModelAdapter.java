/*
 * SpaceTimeSeriesDataTableModelAdapter.java
 *
 * Created on October 31, 2006, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oui.dogbyte;

import javax.swing.table.AbstractTableModel;
import oui.mms.datatypes.SpaceTimeSeriesData;

/**
 *
 * @author markstro
 */
public class SpaceTimeSeriesDataTableModelAdapter extends AbstractTableModel {
    private SpaceTimeSeriesData stsd;
    
    /** Creates a new instance of SpaceTimeSeriesDataTableModelAdapter */
    public SpaceTimeSeriesDataTableModelAdapter(SpaceTimeSeriesData stsd) {
        this.stsd = stsd;
    }

    //////////////////////////////////////////////////////////////////////////
    //
    //             Implementation of the TableModel Interface
    //
    //////////////////////////////////////////////////////////////////////////

    @Override
    public String getColumnName(int col) {
        return (String) stsd.getVariableNameAt(col);
    }

    @Override
    public Class getColumnClass (int col) {return Double.class;}
    @Override
    public boolean isCellEditable (int row, int col) {return false;}
    public int getColumnCount() {return stsd.getVariableCount();}
    public int getRowCount() {return stsd.getZoneCount();}
    @Override
    public void setValueAt(Object value, int row, int column) {}
    
    //  DANGER this needs to be modified for changing dates
    public Object getValueAt(int row, int col) {
        stsd.setVariableIndex(col);
        double[] vals = stsd.getCurrentData();
        return new Double(vals[row]);
    }
}
