/*
 * MmsParamEditTableModel.java
 *
 */

package oui.paramtool;

import java.util.logging.Level;
import javax.swing.table.AbstractTableModel;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

/** This implements the MmsParamEditTableModel.
 *
 * @author markstro
 * @version 2.0
 */
public class ParamToolTableModel extends AbstractTableModel {
    private Object selection;
    private ParameterSet mms_params;
    
    public ParamToolTableModel(ParameterSet mms_params, Object selection) {
        this.mms_params = mms_params;
        this.selection = selection;
    }
    
    @Override
    public void addTableModelListener(javax.swing.event.TableModelListener l) {
    }
    
    @Override
    public Class getColumnClass(int columnIndex) {
        if (selection == ParamToolTreeModel.dimensionSizes) {  //  dimension size table
            return Integer.class;
            
        } else {
            Dimension dim = (Dimension)(mms_params.getDimension(selection.toString()));
            Parameter param;
            
            if (dim == null) {  // This is a 2D parameter
                param = (Parameter)(mms_params.getParameter(selection.toString()));
                
            } else {  // This is a dimension name; process 1D parameters for this dimension
                param = (Parameter)(mms_params.get1DParamsForDim(dim).toArray()[columnIndex]);
            }
            return param.getType();
        }
    }
    
    @Override
    public int getColumnCount() {
//        System.out.println("getColumnCount selection = " + selection.toString());
        
        if (selection == ParamToolTreeModel.dimensionSizes) {  // This is dimension sizes
            return 1;
        } else {
            Dimension dim = (Dimension)(mms_params.getDimension(selection.toString()));
            
            if (dim == null) {  // This is a 2D parameter
                Parameter param = (Parameter)(mms_params.getParameter(selection.toString()));
                return param.getDimension(1).getSize();
                
            } else {  // This is a dimension name; process 1D parameters for this dimension
                return mms_params.get1DParamsForDim(dim).size();
            }
        }
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        if (selection == ParamToolTreeModel.dimensionSizes) {  // This is dimension sizes
            return "Dimension Size";
            
        } else {
            Dimension dim = (Dimension)(mms_params.getDimension(selection.toString()));
            
            if (dim == null) {  // This is a 2D parameter
                return Integer.toString(columnIndex + 1);
            } else {  // This is a dimension name; process 1D parameters for this dimension
                return mms_params.get1DParamsForDim(dim).toArray()[columnIndex].toString();
            }
        }
    }
    
    @Override
    public int getRowCount() {
        if (selection == ParamToolTreeModel.dimensionSizes) {  // This is dimension sizes
//            return mms_params.getDimensionNames().size();
            return mms_params.getDimensions().size();
            
        } else {
            Dimension dim = (Dimension)(mms_params.getDimension(selection.toString()));
            
            if (dim == null) {   // This is a 2D parameter
                Parameter param = (Parameter)(mms_params.getParameter(selection.toString()));
                return param.getDimension(0).getSize();
                
            } else {    // This is a dimension name; process 1D parameters for this dimension
                return dim.getSize();
            }
        }
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (selection == ParamToolTreeModel.dimensionSizes) {  // This is dimension sizes
            Dimension dim = mms_params.getDimensionAt(rowIndex);
//            Dimension dim = (Dimension)(dims[rowIndex]);
            return new Integer(dim.getSize());
            
        } else {
            String toString = selection.toString();
            Dimension dim = (Dimension) (mms_params.getDimension(toString));
            Parameter param = null;
            
            try {
                if (dim == null) { // This is a 2D parameter
                    param = (Parameter) (mms_params.getParameter(selection.toString()));
                    int index = (columnIndex * param.getDimension(0).getSize()) + rowIndex;
                    if (param.getType() == Integer.class) {
                        return new Integer(((int[]) (param.getVals()))[index]);
                    } else if (param.getType() == Double.class) {
                        return new Double(((double[]) (param.getVals()))[index]);
                    } else if (param.getType() == Float.class) {
                        return new Float(((float[]) (param.getVals()))[index]);
                    } else { // DANGER not sure what to do here
                        return new Double(((double[]) (param.getVals()))[index]);
                    }

                } else {  // This is a dimension name; process 1D parameters for this dimension
                    param = (Parameter) (mms_params.get1DParamsForDim(dim).toArray()[columnIndex]);

                    if (param.getType() == Integer.class) {
                        int int_val = ((int[]) (param.getVals()))[rowIndex];
                        return new Integer(int_val);

                    } else if (param.getType() == Float.class) {
                        float flo_val = ((float[]) (param.getVals()))[rowIndex];
                        return new Float(flo_val);

                    } else if (param.getType() == Double.class) {
                        double dub_val = ((double[]) (param.getVals()))[rowIndex];
                        return new Double(dub_val);

                    } else if (param.getType() == String.class) {
                        String str_val = ((String[]) (param.getVals()))[rowIndex];
                        return str_val;

                    } else {
                        ParamTool.paramToolLogger.log(Level.SEVERE, "Unknown data type {0} for dimension {1}, parameter {2}", new Object[]{param.getType().toString(), dim.getName(), param.getName()});
                        return null;
                    }
                }
            } catch (NullPointerException e) {
                ParamTool.paramToolLogger.log(Level.SEVERE, "Null pointer for dimension {0}, parameter {1}", new Object[]{dim.getName(), param.getName()});
                return null;
            }
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    
    @Override
    public void removeTableModelListener(javax.swing.event.TableModelListener l) {
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
        if (selection == ParamToolTreeModel.dimensionSizes) {  // This is dimension sizes
            Dimension dim = mms_params.getDimensionAt(rowIndex);
            mms_params.setDimension(dim, ((Integer)aValue).intValue());
            
        } else {
            Dimension dim = (Dimension)(mms_params.getDimension(selection.toString()));
            
            if (dim == null) { // This is a 2D parameter
                Parameter param = (Parameter)(mms_params.getParameter(selection.toString()));
                int index = (columnIndex * param.getDimension(0).getSize()) + rowIndex;
                mms_params.setParameterValue(param, aValue, index);

            } else {  // This is a dimension name; process 1D parameter for this dimension
                Parameter param = (Parameter)(mms_params.get1DParamsForDim(dim).toArray()[columnIndex]);
                mms_params.setParameterValue(param, aValue, rowIndex);
            }
        }
//        this.fireTableCellUpdated(rowIndex, columnIndex);
    }
}