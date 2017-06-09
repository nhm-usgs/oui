package org.omscentral.gis.model;

import java.util.Vector;
/**
 * Description of the Interface
 *
 * @author    en
 * @created   June 8, 2001
 */
public interface AttributeModel {

  public class DbfColumn {

    public String fieldName;
    public char fieldType;
    public int fieldLength;
    public int fieldDecimalCount;
    public Vector<Object> rowData;

    /**
     * Description of the Field
     */
    public final static char FIELD_CHAR = 'C';
    /**
     * Description of the Field
     */
    public final static char FIELD_NUMERICAL = 'N';
    /**
     * Description of the Field
     */
    public final static char FIELD_LOGICAL = 'L';
    /**
     * Description of the Field
     */
    public final static char FIELD_DATE = 'D';
    /**
     * Description of the Field
     */
    public final static char FIELD_MEMO = 'M';


    /**
     * Constructor for the DbfColumn object
     *
     * @param fName          Description of Parameter
     * @param fType          Description of Parameter
     * @param fLength        Description of Parameter
     * @param fDecimalCount  Description of Parameter
     */
    public DbfColumn(String fName, char fType, int fLength, int fDecimalCount) {
      fieldName = fName;
      fieldType = fType;
      fieldLength = fLength;
      fieldDecimalCount = fDecimalCount;
      rowData = new Vector<Object>();
    }


    /**
     * Adds a feature to the RowData attribute of the DbfColumn object
     *
     * @param o  The feature to be added to the RowData attribute
     */
    public void addRowData(Object o) {
      rowData.addElement(o);
    }
  }

  /**
   * Gets the AttributeCount attribute of the AttributeModel object
   *
   * @return   The AttributeCount value
   */
  public int getAttributeCount();


  /**
   * Gets the AttributeNames attribute of the AttributeModel object
   *
   * @return   The AttributeNames value
   */
  public String[] getAttributeNames();


  /**
   * Gets the AttributeName attribute of the AttributeModel object
   *
   * @param column  Description of Parameter
   * @return        The AttributeName value
   */
  public String getAttributeName(int column);


  /**
   * Gets the RecordCount attribute of the AttributeModel object
   *
   * @return   The RecordCount value
   */
  public int getRecordCount();


  /**
   * Gets the Attributes attribute of the AttributeModel object
   *
   * @param recordIndex  Description of Parameter
   * @return             The Attributes value
   */
  public Object[] getAttributes(int recordIndex);


  /**
   * Gets the Attribute attribute of the AttributeModel object
   *
   * @param recordIndex     Description of Parameter
   * @param attributeIndex  Description of Parameter
   * @return                The Attribute value
   */
  public Object getAttribute(int recordIndex, int attributeIndex);

}

