package org.omscentral.gis.model;

import java.util.Vector;

import org.omscentral.gis.model.AttributeModel;

public class DefaultAttributeModel implements AttributeModel {

  byte dbfVersion;
  byte dbfLastUpdate[];
  int dbfNumberOfRecords;
  Vector dbfColumns;

  public DefaultAttributeModel(Vector cols) {
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
