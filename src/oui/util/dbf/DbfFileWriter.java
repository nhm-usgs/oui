/*
 * $Id: $
 * $Author: $
 * $Date: $
 */
package oui.util.dbf;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.omscentral.gis.model.AttributeModel;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 *
 * @see
 * @version    $Revision: $ / $Date: $
 * @author     $Author: markstro $
 */
public class DbfFileWriter {

    private OuiAttributeModel oam;
    private static final Logger log = Logger.getLogger(DbfFileWriter.class.getName());

    public DbfFileWriter(OuiAttributeModel oam) {
        this.oam = oam;
    }

    public void writeReport(String file_name) {

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file_name));
            writer.println("Version : " + oam.getDbfVersion());
            writer.println("Date    : " + oam.getDbfDate());
            writer.println("Records : " + oam.getRecordCount());

            String[] attributes = oam.getAttributeNames();
            for (int k = 0; k < oam.getRecordCount(); k++) {
                for (int l = 0; l < attributes.length; l++) {
                    writer.println(attributes[l] + ": " + oam.getAttribute(k, l));
                }
                writer.println();
            }

            writer.close();

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void writeXML(String file_name) {

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file_name));

            writer.println("<?xml version=\"1.0\" ?>");
//         writer.println (
//                    "<!DOCTYPE DbfData SYSTEM \"/home/oui/src/XmlDbf/dbf.dtd\">");

            writer.println("<DbfData version=\"" + oam.getDbfVersion()
                    + "\" date=\"" + oam.getDbfDate()
                    + "\">");


            /*
             **  Write the attributes (columns).
             */
            String[] attributes = oam.getAttributeNames();
            char[] types = oam.getAttributeTypes();
            int[] len = oam.getAttributeLengths();
            int[] dec_cnt = oam.getAttributeDecimalCount();

            writer.println("   <Attributes>");
            for (int i = 0; i < attributes.length; i++) {
                writer.println("      <Attribute name=\"" + attributes[i]
                        + "\" type=\"" + types[i]
                        + "\" length=\"" + len[i]
                        + "\" decimal_count=\"" + dec_cnt[i]
                        + "\"/>");
            }
            writer.println("   </Attributes>");
            /*
             **  Write the records (rows).
             */
            writer.println("\n   <Records>");
            for (int k = 0; k < oam.getRecordCount(); k++) {
                writer.println("      <Record index=\"" + k + "\">");

                for (int l = 0; l < attributes.length; l++) {
                    writer.println("         <Field attribute=\"" + attributes[l]
                            + "\">" + oam.getAttribute(k, l) + "</Field>");
                }
                writer.println("      </Record>");
            }

            writer.println("   </Records>");
            writer.println("</DbfData>");
            writer.close();

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void writeDBF(String file_name) {

        try {
            ByteOrderOutputStream dbfOutStream =
                    new ByteOrderOutputStream(new FileOutputStream(file_name));

            writeDbfHeader(dbfOutStream);
            writeDbfData(dbfOutStream);
            dbfOutStream.close();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
            log.log(Level.WARNING, "writeDBF IOException: {0}", e.toString());
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            log.log(Level.WARNING, "writeDBF Exception: {0}", e.toString());
        }
    }

    void writeDbfHeader(ByteOrderOutputStream dbfOutStream) throws Exception {
        Integer version = ((Integer) (oam.getDbfVersion()));
        dbfOutStream.writeByte(version.intValue());
        String date = (String) (oam.getDbfDate());
        byte d2 = (byte) date.charAt(0);
        byte d1 = (byte) date.charAt(2);
        byte d0 = (byte) date.charAt(4);
        dbfOutStream.writeByte(d0);
        dbfOutStream.writeByte(d1);
        dbfOutStream.writeByte(d2);
        dbfOutStream.writeLittleEndInt(oam.getRecordCount());

        int dbfHeaderLength = ((oam.getAttributeCount() + 1) * 32) + 1;
        dbfOutStream.writeLittleEndUnsignedShort(dbfHeaderLength);

        int dbfRecordLength = 1;
        /*
        DANGER      for (int i = 0; i < oam.getRecordCount(); i++) {
         */
        int[] lengths = oam.getAttributeLengths();
        for (int i = 0; i < lengths.length; i++) {
            dbfRecordLength += lengths[i];
        }

        dbfOutStream.writeLittleEndUnsignedShort(dbfRecordLength);
        dbfOutStream.skip(20);
        /*
        int nFields = (( dbfHeaderLength - 1 ) / 32 ) - 1;
         */

        String[] names = oam.getAttributeNames();
        char[] types = oam.getAttributeTypes();
        int[] len = oam.getAttributeLengths();
        int[] dc = oam.getAttributeDecimalCount();
        for (int i = 0; i < oam.getAttributeCount(); i++) {

            StringBuilder sb = new StringBuilder(names[i]);
            sb.setLength(11);
            dbfOutStream.writeBigEndUnsignedByteArray(
                    (sb.toString()).getBytes());

            dbfOutStream.writeByte(types[i]);
            dbfOutStream.skip(4);
            dbfOutStream.writeByte(len[i]);
            dbfOutStream.writeByte(dc[i]);
            dbfOutStream.skip(14);
        }
        dbfOutStream.writeByte(0x0D);
    }

    void writeDbfData(ByteOrderOutputStream dbfOutStream) throws Exception {

        Object data;
        StringBuffer sb = null;
        byte[] b = null;
        String s = null;

        int[] len = oam.getAttributeLengths();
        char[] types = oam.getAttributeTypes();
        int[] dc = oam.getAttributeDecimalCount();

        for (int j = 0; j < oam.getRecordCount(); j++) {

            /*
             ** write the leading valid marker
             */
            dbfOutStream.writeByte(0x20);

            for (int i = 0; i < oam.getAttributeCount(); i++) {

                data = oam.getAttribute(j, i);

                try {
                    switch (types[i]) {
                        case AttributeModel.DbfColumn.FIELD_CHAR:
                        case AttributeModel.DbfColumn.FIELD_DATE:
                            String f = " 1$-" + (len[i] - 1) + "s";
                            s = String.format(f, (String) data);
                            b = (s).getBytes();
                            dbfOutStream.write(b, 0, len[i]);
                            break;

                        case AttributeModel.DbfColumn.FIELD_NUMERICAL:
                        case 'F': // This hack is here because AttributeModel does not have 'F' (I assume for float).
                            if (dc[i] == 0) {
                                f = "1$" + len[i] + "d";

                                if (data == null) {
                                    System.out.println("Problem with data at row = "
                                            + (j + 1) + " column = " + (i + 1));
                                } else if (((data.getClass()).getName()).equals(
                                        "java.lang.String")) {
                                    s = String.format(f, Integer.valueOf((String) data).intValue());
                                    b = s.getBytes();
                                } else {
                                    s = String.format(f, ((Integer) (data)).intValue());
                                    b = s.getBytes();
                                }
                                dbfOutStream.write(b, 0, len[i]);

                            } else {
                                f = "1$" + len[i] + "." + dc[i] + "f";
                                if (((data.getClass()).getName()).equals(
                                        "java.lang.String")) {
                                    s = String.format(f, Double.valueOf((String) data).doubleValue());
                                } else {
                                    s = String.format(f, ((Double) data).doubleValue());
                                }
                                b = s.getBytes();
                                dbfOutStream.write(b, 0, len[i]);

                            }
                            break;

                        case AttributeModel.DbfColumn.FIELD_LOGICAL:
                            if (((Boolean) data).booleanValue()) {
                                dbfOutStream.writeByte('t');
                            } else {
                                dbfOutStream.writeByte('f');
                            }
                            break;

                        default:
                            System.out.println("DbfDataModel::writeDbfData  Attempting to write Unknown data type.");
                    }

                } catch (NumberFormatException nfe) {
                    data = null;
                }
            }
            System.out.print(".");
        }
        dbfOutStream.close();
        System.out.println(" done writing dbf.");
    }
}
