package org.omscentral.gis.io;


import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.ArrayList;

import org.omscentral.gis.io.ByteOrderInputStream;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorModel;
import org.omscentral.gis.model.DefaultVectorModel;
/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class ShpFileParser {

  private static int recordNumber;

  /**
   * Description of the Field
   */
  public final static int NULL_SHAPE = 0;
  /**
   * Description of the Field
   */
  public final static int POINT_SHAPE = 1;
  /**
   * Description of the Field
   */
  public final static int ARC_SHAPE = 3;
  /**
   * Description of the Field
   */
  public final static int POLYGON_SHAPE = 5;
  /**
   * Description of the Field
   */
  public final static int MULTIPOINT_SHAPE = 8;


  String file;
  /**
   * inits a fileset of shapefiles for parsing
   *
   * @param shapeFileBase  the basename for the shape files without the
   *      extensions ".shx". For example if the parameter is "streams", the
   *      constructor tries loading streams.shp. The constructor opens the file
   *      and reads the header of the .shp file.
   * @exception Exception  An exception is thrown when the file could not be
   *      loaded.
   */
  public ShpFileParser(String shapeFileBase) throws Exception {
    file = shapeFileBase;
  }

  public static VectorModel createModel(String file) throws Exception {
    return createModel(new FileInputStream(file));
  }

  public static VectorModel createModel(InputStream file) throws Exception {
    return readContents(new ByteOrderInputStream(file));
  }

  public VectorModel createModel() throws Exception {
    return createModel(file);
  }




  /**
   * gets the enumeration of the features
   *
   * @return   the Enumeration
   */
  public static Enumeration elements(final ByteOrderInputStream shpStream) {
    return
      new Enumeration() {
        int contentLength;
        /**
         * Description of the Method
         *
         * @return   Description of the Returned Value
         */
        public Object nextElement() {
          try {
            int stype = shpStream.readLittleEndInt();

            if (stype == NULL_SHAPE) {
              return null;
            }

            if (stype == POINT_SHAPE) {
              return new VectorModel.PointFeature
                  (shpStream.readLittleEndDouble(),
                  shpStream.readLittleEndDouble());
            }

            double x = shpStream.readLittleEndDouble();
            double y = shpStream.readLittleEndDouble();
            Rectangle2D box = new Rectangle2D.Double(
                x,
                y,
                Math.abs(x - shpStream.readLittleEndDouble()),
                Math.abs(y - shpStream.readLittleEndDouble())
                );

            Point2D[] points;
            int count;

            switch (stype) {
              case MULTIPOINT_SHAPE:
                count = shpStream.readLittleEndInt();
                return new VectorModel.MultiPointFeature
                    (box, count, shpStream.readLittleEndDouble(count * 2));

              case ARC_SHAPE:
              case POLYGON_SHAPE:
                int partsCount = shpStream.readLittleEndInt();
                count = shpStream.readLittleEndInt();

                int[] parts = shpStream.readLittleEndInt(partsCount);
                double[] coordinates = shpStream.readLittleEndDouble(count * 2);

                return
                    (stype == ARC_SHAPE)
                     ? new VectorModel.MultiLineFeature(box, count, coordinates, parts)
                     : new VectorModel.MultiPolygonFeature(box, count, coordinates, parts);
              default:
                throw new IllegalArgumentException
                    ("Unknow feature type: " + stype);
            }
          }
          catch (Exception e) {
            e.printStackTrace();
            return null;
          }
        }


        /**
         * Description of the Method
         *
         * @return   Description of the Returned Value
         */
        public boolean hasMoreElements() {
          try {
            recordNumber = shpStream.readBigEndInt();
            contentLength = shpStream.readBigEndInt();
            return true;
          }
          catch (IOException ioe) {
            try {
              shpStream.close();
            }
            catch (IOException io) {
            }
            return false;
          }
        }
        // hasMoreElements
      };
    // Enumeration
  }


  /**
   * Description of the Method
   *
   * @exception Exception  Description of Exception
   */
  private static VectorModel readContents(ByteOrderInputStream shpStream) throws Exception {
    int shpFileCode;
    int shpFileLength;
    int shpVersion;
    int shpType;
    Rectangle2D bounds;
    Feature[] features;

    // HEADER
    shpFileCode = shpStream.readBigEndInt();
    if (shpFileCode != 9994) {
      throw new Exception("Invalid filecode: " + shpFileCode);
    }
    shpStream.skip(20);
    shpFileLength = shpStream.readBigEndInt();
    shpVersion = shpStream.readLittleEndInt();
    shpType = shpStream.readLittleEndInt();

    double x = shpStream.readLittleEndDouble();
    double y = shpStream.readLittleEndDouble();
    bounds = new Rectangle2D.Double(
        x,
        y,
        Math.abs(x - shpStream.readLittleEndDouble()),
        Math.abs(y - shpStream.readLittleEndDouble())
        );

    shpStream.skip(32);

    // CONTENTS
    ArrayList<Object> temp = new ArrayList<Object>();
    for (Enumeration E = elements(shpStream); E.hasMoreElements(); ) {
      temp.add(E.nextElement());
    }

    features = new Feature[temp.size()];
    temp.toArray(features);

    DefaultVectorModel model = new DefaultVectorModel(features,bounds);
    model.setFileVersion(shpVersion);
    model.setShapeType(shpType);
    model.setShapeFileCode(shpFileCode);
    return model;
  }

  public static int getCurrentRecordNumber() {
    return recordNumber;
  }


}

