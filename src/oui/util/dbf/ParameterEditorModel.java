package oui.util.dbf;

import javax.swing.table.AbstractTableModel;

public abstract class ParameterEditorModel extends AbstractTableModel {
   public abstract String getRowName (int row);
}