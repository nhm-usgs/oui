package oui.util.dbf;

import java.util.Vector;
import org.omscentral.gis.model.AttributeModel;

public class OuiAttributeModel implements AttributeModel {
    byte dbfVersion;
    byte dbfLastUpdate[];
    int dbfNumberOfRecords;
    public Vector<OuiAttributeModel.DbfColumn> dbfColumns;
    
    public OuiAttributeModel(Vector<OuiAttributeModel.DbfColumn> cols) {
        dbfColumns = cols;
    }
    
    /**
     * Gets the DbfVersion attribute of the DbfFileParser object
     *
     * @return   The DbfVersion value
     */
    public int getDbfVersion() {
        return dbfVersion;
    }
    
    /**
     * Gets the DbfDate attribute of the DbfFileParser object
     *
     * @return   The DbfDate value
     */
    public String getDbfDate() {
        return dbfLastUpdate[2] + "/" + dbfLastUpdate[1] + "/" + dbfLastUpdate[0];
    }
    
    /**
     * Gets the index of the attribute AttributeName of the OuiAttributeMode object
     * (column index for a given column name)
     *
     * @param att_name  AttributeName to look up
     * @return          The attribute index (-1 if not found)
     */
    
    public int getAttributeIndex(String att_name) {
        int attIndex = -1;
        
        for (int i = 0; i < getAttributeCount(); i++) {
            if (att_name.equals(getAttributeName(i))) {
                attIndex = i;
                break;
            }
        }
        return attIndex;
    }
    
    /**
     * Gets the Attribute values of the OuiAttributeModel object
     * (returns a column of data)
     *
     * @param attrIndex  Description of Parameter
     * @return             The Attributes value
     */
    public Object[] getAttributeValues(int attIndex) {
        
        Object[] values = null;
        if (attIndex != -1) {
            values = new Object[getRecordCount()];
            for (int i = 0; i < getRecordCount(); i++) {
                values[i] = getAttribute(i, attIndex);
            }
        }
        
        return values;
    }
    
    /**
     * Gets the Attribute values of the OuiAttributeModel object
     * by attribute name (returns a column of data)
     *
     * @param att_name     Name of attribute
     * @return             The Attributes value
     */
    public Object[] getAttributeValues(String att_name) {
        return getAttributeValues(getAttributeIndex(att_name));
    }
    
    /**
     * Gets the unique Attribute values of the OuiAttributeModel object
     * by attribute name (returns unique values from a column of data)
     *
     * @param att_name     Name of attribute
     * @return             The unique Attributes values
     */
    
    public Object[] getUniqueAttributeValues(String att_name) {
        
        Object[] all_values =  getAttributeValues(att_name);
        Vector<Object> value_vec = new Vector<Object> ();
        
        for (int i = 0; i < all_values.length; i++) {
            if (value_vec.contains(all_values[i]) == false) {
                value_vec.add(all_values[i]);
            }
        }
        
        return value_vec.toArray();
    }
    
    /**
     * Gets the field lengths of the Attributes (column widths for
     * display in a table)
     *
     * @return             The Attribute lengths
     */
    
    public int[] getAttributeLengths() {
        int[] S = new int[getAttributeCount()];
        
        for(int i = 0; i < getAttributeCount(); i++) {
            S[i] = ((OuiAttributeModel.DbfColumn)(dbfColumns.elementAt(i))).fieldLength;
        }
        return S;
    }
    
    /**
     * Gets the decimal count of the Attributes (number of decimal places by
     * column)
     *
     * @return             The number of decimal places for each Attribute
     */
    public int[] getAttributeDecimalCount() {
        int[] S = new int[dbfColumns.size()];
        
        for(int i = 0; i < dbfColumns.size(); i++) {
            S[i] = ((OuiAttributeModel.DbfColumn)(dbfColumns.elementAt(i))).fieldDecimalCount;
        }
        return S;
    }
    
    /**
     * Gets the data type of the Attributes (data type by column)
     *
     * @return             The data type for each Attribute
     */
    public char[] getAttributeTypes() {
        char[] S = new char[dbfColumns.size()];
        
        for(int i = 0; i < dbfColumns.size(); i++) {
            S[i] = ((OuiAttributeModel.DbfColumn)(dbfColumns.elementAt(i))).fieldType;
        }
        return S;
    }
    
    /**
     * Sets the Attribute attribute of the DbfFileParser object
     *
     * @param recordIndex     Description of Parameter
     * @param attributeIndex  Description of Parameter
     * @param value           The new value object
     */
    public void setAttribute(int recordIndex, int attributeIndex, Object value) {
        ((OuiAttributeModel.DbfColumn)(dbfColumns.elementAt(attributeIndex))).rowData.setElementAt(value, recordIndex);
    }
    
    /**
     * Delete the attribute of the DbfFileParser object
     *
     * @param attributeIndex  Description of Parameter
     */
    public void deleteAttribute(int attributeIndex) {
        dbfColumns.remove(attributeIndex);
    }
    
    /**
     * Add an attribute to the attribute model
     *
     * @param name           Name of the attribute
     * @param type           Data type of the attribute
     * @param length         Field length of attribute
     * @param decimal_count  Number of decimal places of attribute
     * @param def            Description of attribute
     */
    public void addAttribute(String name, String type, int length, int dc,
            String def) {
        
        char t;
        
        if (type.equals("Character")) {
            t = OuiAttributeModel.DbfColumn.FIELD_CHAR;
        } else if (type.equals("Numerical")) {
            t = OuiAttributeModel.DbfColumn.FIELD_NUMERICAL;
        } else if (type.equals("Logical")) {
            t = OuiAttributeModel.DbfColumn.FIELD_LOGICAL;
        } else if (type.equals("Date")) {
            t = OuiAttributeModel.DbfColumn.FIELD_DATE;
        } else if (type.equals("Memo")) {
            t = OuiAttributeModel.DbfColumn.FIELD_MEMO;
        } else {
            t = OuiAttributeModel.DbfColumn.FIELD_CHAR;
        }
        
        OuiAttributeModel.DbfColumn col = new OuiAttributeModel.DbfColumn(name, t, length, dc);
        
        dbfColumns.addElement(col);
        
        
        for (int i = 0; i < getRecordCount(); i++) {
            col.addRowData(new String(def));
        }
    }
    
  /*
   * -------------------------------------------------------------------------
   * Interface implementation - AttributeModel
   *
   */
    
    /**
     * Gets the AttributeCount attribute of the DbfFileParser object
     *
     * @return   The AttributeCount value
     */
    public int getAttributeCount() {
        return dbfColumns.size();
    }
    
    
    /**
     * Gets the AttributeNames attribute of the DbfFileParser object
     *
     * @return   The AttributeNames value
     */
    public String[] getAttributeNames() {
        String[] S = new String[dbfColumns.size()];
        for (int i = 0; i < dbfColumns.size(); i++) {
            S[i] = ((AttributeModel.DbfColumn) dbfColumns.elementAt(i)).fieldName;
        }
        return S;
    }
    
    
    /**
     * Gets the AttributeName attribute of the DbfFileParser object
     *
     * @param column  Description of Parameter
     * @return        The AttributeName value
     */
    public String getAttributeName(int column) {
        return ((AttributeModel.DbfColumn) dbfColumns.elementAt(column)).fieldName;
    }
    
    /**
     * Gets the RecordCount attribute of the DbfFileParser object
     *
     * @return   The RecordCount value
     */
    public int getRecordCount() {
// markstro
        if (dbfNumberOfRecords <= 0) {
            AttributeModel.DbfColumn c = (AttributeModel.DbfColumn) dbfColumns.elementAt(0);
            dbfNumberOfRecords = c.rowData.size();
        }
        return dbfNumberOfRecords;
    }
    
    /**
     * Gets the Attributes attribute of the DbfFileParser object
     *
     * @param recordIndex  Description of Parameter
     * @return             The Attributes value
     */
    public Object[] getAttributes(int recordIndex) {
        Object[] values = new Object[getAttributeCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = getAttribute(recordIndex, i);
        }
        return values;
    }
    
    
    /**
     * Gets the Attribute attribute of the DbfFileParser object
     *
     * @param recordIndex     Description of Parameter
     * @param attributeIndex  Description of Parameter
     * @return                The Attribute value
     */
    public Object getAttribute(int recordIndex, int attributeIndex) {
        AttributeModel.DbfColumn c = (AttributeModel.DbfColumn) dbfColumns.elementAt(attributeIndex);
        return c.rowData.elementAt(recordIndex);
    }
}
