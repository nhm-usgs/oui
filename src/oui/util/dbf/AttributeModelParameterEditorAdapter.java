/*
 * $Id: $
 * $Author: $
 * $Date: $
 */

package oui.util.dbf;

/**
 *
 *
 * @see
 * @version    $Revision: $ / $Date: $
 * @author     $Author: markstro $
 */
public class AttributeModelParameterEditorAdapter extends ParameterEditorModel {

   OuiAttributeModel am;

   public AttributeModelParameterEditorAdapter (OuiAttributeModel am) {
      this.am = am;
   }

   public String getRowName (int row) {
      return "" + (row + 1);
   }

    //////////////////////////////////////////////////////////////////////////
    //
    //             Implementation of the TableModel Interface
    //
    //////////////////////////////////////////////////////////////////////////

    @Override
   public String getColumnName(int col) {
      return am.getAttributeName (col);
   }

//  DANGER
    @Override
    public Class getColumnClass (int col) {
       return Object.class;
    }

    @Override
    public boolean isCellEditable (int row, int col) {
       return true;
    }

    public int getColumnCount() {
        if (am == null) {
            System.out.println("am = null");
        }
        return am.getAttributeCount();
    }

    // Data methods

   public int getRowCount() {
      return am.getRecordCount ();
   }

   public Object getValueAt (int row, int col) {
//       System.out.println ("AttributeModelParameterEditorAdapter row = " + row + " col = " + col);
      return am.getAttribute (row, col);
   }

    @Override
   public void setValueAt (Object value, int row, int column) {
//      am.setAttribute (row, column, value);
   }
}
