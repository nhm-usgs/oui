package org.omscentral.gis.io;


import java.util.Vector;
import java.io.FileInputStream;
import java.io.InputStream;

import org.omscentral.gis.io.ByteOrderInputStream;

import org.omscentral.gis.model.AttributeModel;
import org.omscentral.gis.model.DefaultAttributeModel;

/**
 * @author    $Author: en $
 * @created   June 19, 2001
 * @see
 * @version   $Revision: 1.1.1.1 $ / $Date: 2002/07/12 22:54:59 $
 */


/**
 * @author    $Author: en $
 * @created   June 19, 2001
 * @see
 * @version   $Revision: 1.1.1.1 $ / $Date: 2002/07/12 22:54:59 $
 */
public class DbfFileParser {

  String file;



  /**
   * Constructor for the DbfFileParser object
   *
   * @param shapeFileBase  Description of Parameter
   * @exception Exception  Description of Exception
   */
  public DbfFileParser(String shapeFileBase) {
    file = shapeFileBase;
  }

  public static AttributeModel createModel(String file) throws Exception {
    return createModel(new FileInputStream(file));
  }

  public static AttributeModel createModel(InputStream file)throws Exception  {
    return readDbfData(new ByteOrderInputStream(file));
  }

  public AttributeModel createModel() throws Exception {
    return createModel(file);
  }

  /**
   * Description of the Method
   *
   * @exception Exception  Description of Exception
   */
  private static AttributeModel readDbfData(ByteOrderInputStream dbfStream) throws Exception {
    byte dbfVersion;
    byte dbfLastUpdate[];
    int dbfNumberOfRecords;
    int dbfHeaderLength;
    int dbfRecordLength;
    Vector<AttributeModel.DbfColumn> dbfColumns = new Vector<AttributeModel.DbfColumn>();

    dbfLastUpdate = new byte[3];

    //HEADER
    dbfVersion = (byte) dbfStream.readUnsignedByte();
    dbfLastUpdate[0] = (byte) dbfStream.readUnsignedByte();
    dbfLastUpdate[1] = (byte) dbfStream.readUnsignedByte();
    dbfLastUpdate[2] = (byte) dbfStream.readUnsignedByte();
    dbfNumberOfRecords = dbfStream.readLittleEndInt();
    dbfHeaderLength = dbfStream.readLittleEndUnsignedShort();
    dbfRecordLength = dbfStream.readLittleEndUnsignedShort();
    dbfStream.skip(20);
    int nFields = ((dbfHeaderLength - 1) / 32) - 1;

    for (int i = 0; i < nFields; i++) {
      String fName = (new String(dbfStream.readBigEndUnsignedByteArray(11))).trim();
      char fType = (char) dbfStream.readUnsignedByte();
      dbfStream.skip(4);
      int fLength = dbfStream.readUnsignedByte();
      int fDecimal = dbfStream.readUnsignedByte();
      dbfStream.skip(14);
      dbfColumns.addElement(new AttributeModel.DbfColumn(fName, fType, fLength, fDecimal));
    }
    if (dbfStream.readUnsignedByte() != 0x0D) {
      System.err.println("Header format error");
    }

    //CONTENTS
    Object data;
    for (int j = 0; j < dbfNumberOfRecords; j++) {
      // read the leading valid marker
      boolean valid = (dbfStream.readUnsignedByte() == 0x20);
      for (int i = 0; i < dbfColumns.size(); i++) {
        AttributeModel.DbfColumn field = (AttributeModel.DbfColumn) dbfColumns.elementAt(i);
        int dataSize = field.fieldLength;
        String dataStr = (new String(dbfStream.readBigEndUnsignedByteArray(dataSize))).trim();
        try {
          switch (field.fieldType) {
            case AttributeModel.DbfColumn.FIELD_CHAR:
            case AttributeModel.DbfColumn.FIELD_DATE:
              data = dataStr;
              break;
            case AttributeModel.DbfColumn.FIELD_NUMERICAL:
              data = (field.fieldDecimalCount == 0) ? (Object) new Integer(dataStr)
                   : (Object) new Double(dataStr);
              break;
            case AttributeModel.DbfColumn.FIELD_LOGICAL:
              if ("yYtT".indexOf(dataStr) != -1) {
                data = new Boolean(true);
              }
              else if ("nNfF".indexOf(dataStr) != -1) {
                data = new Boolean(false);
              }
              else {
                data = new Object();
              }
              break;
            default:
              data = dataStr;
          }
        }
        catch (NumberFormatException nfe) {
          data = null;
        }
        field.addRowData(data);
      }
    }
    dbfStream.close();

    DefaultAttributeModel model = new DefaultAttributeModel(dbfColumns);

    return model;
  }



}
