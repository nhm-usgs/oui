package org.omscentral.gis.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * Description of the Interface
 *
 * @author    en
 * @created   June 19, 2001
 */
public abstract class VectorModel implements Cloneable {

  /**
   * Gets the Bounds attribute of the VectorModel object
   *
   * @return   The Bounds value
   */
  public abstract Rectangle2D getBounds();


  /**
   * Gets the FeatureCount attribute of the VectorModel object
   *
   * @return   The FeatureCount value
   */
  public abstract int getFeatureCount();


  /**
   * Gets the Feature attribute of the VectorModel object
   *
   * @param index  Description of Parameter
   * @return       The Feature value
   */
  public abstract Feature getFeature(int index);

  public abstract Object clone();


  /**
   * Gets the Features attribute of the VectorModel object
   *
   * @return   The Features value
   */
  public abstract Feature[] getFeatures();

  public boolean isPoint() {
    return (getFeatureCount()) < 0 ? false :
      (getFeature(0) instanceof VectorModel.PointFeature) ? true : false;
  }

  public boolean isLine() {
    return (getFeatureCount()) < 0 ? false :
      (getFeature(0) instanceof VectorModel.MultiLineFeature) ? true : false;
  }

  public boolean isPoly() {
    return (getFeatureCount()) < 0 ? false :
      (getFeature(0) instanceof VectorModel.MultiPolygonFeature) ? true : false;
  }

  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 19, 2001
   */
  public static class PointFeature extends Point2D.Double implements Feature {
    /**
     * Constructor for the PointFeature object
     *
     * @param x  Description of Parameter
     * @param y  Description of Parameter
     */
    public PointFeature(double x, double y) {
      super(x, y);
    }


    /**
     * Gets the Bounds2D attribute of the PointFeature object
     *
     * @return   The Bounds2D value
     */
    public Rectangle2D getBounds2D() {
      return new Rectangle2D.Double(getX(), getY(), 0, 0);
    }



    /**
     * Description of the Method
     *
     * @param x  Description of Parameter
     * @param y  Description of Parameter
     * @return   Description of the Returned Value
     */
    public boolean contains(double x, double y) {
      return ((this.getX() == x) && (this.getY() == y));
    }


    /**
     * Description of the Method
     *
     * @param p  Description of Parameter
     * @return   Description of the Returned Value
     */
    public boolean contains(Point2D p) {
      return contains(p.getX(), p.getY());
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 19, 2001
   */
  public static class MultiPointFeature implements Feature {
    Rectangle2D bounds;
    Point2D[] points;


    /**
     * Constructor for the MultiPointFeature object
     *
     * @param bounds  Description of Parameter
     * @param points  Description of Parameter
     */
    public MultiPointFeature(Rectangle2D bounds, Point2D[] points) {
      this.bounds = bounds;
      this.points = points;
    }


    /**
     * Constructor for the MultiPointFeature object
     *
     * @param bounds       Description of Parameter
     * @param count        Description of Parameter
     * @param coordinates  Description of Parameter
     */
    public MultiPointFeature
        (Rectangle2D bounds, int count, double[] coordinates) {
      this.bounds = bounds;
      this.points = new Point2D[count];

      int k = 0;
      for (int i = 0; i < count; i++, k += 2) {
        points[i] = new Point2D.Double(coordinates[k], coordinates[k + 1]);
      }
    }


    /**
     * Gets the Bounds2D attribute of the MultiPointFeature object
     *
     * @return   The Bounds2D value
     */
    public Rectangle2D getBounds2D() {

      return bounds;
    }


    /**
     * Gets the Points attribute of the MultiPointFeature object
     *
     * @return   The Points value
     */
    public final Point2D[] getPoints() {
      return points;
    }


    /**
     * Gets the Point attribute of the MultiPointFeature object
     *
     * @param index  Description of Parameter
     * @return       The Point value
     */
    public final Point2D getPoint(int index) {
      return points[index];
    }


    /**
     * Gets the PointCount attribute of the MultiPointFeature object
     *
     * @return   The PointCount value
     */
    public final int getPointCount() {
      return points.length;
    }


    /**
     * Description of the Method
     *
     * @param x  Description of Parameter
     * @param y  Description of Parameter
     * @return   Description of the Returned Value
     */
    public boolean contains(double x, double y) {
      return false;
    }


    /**
     * Description of the Method
     *
     * @param P  Description of Parameter
     * @return   Description of the Returned Value
     */
    public final boolean contains(Point2D P) {
      return contains(P.getX(), P.getY());
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 19, 2001
   */
  public static class MultiLineFeature extends MultiPointFeature {
    /**
     * Description of the Field
     */
    protected int[] parts;


    /**
     * Constructor for the MultiLineFeature object
     *
     * @param bounds  Description of Parameter
     * @param points  Description of Parameter
     * @param parts   Description of Parameter
     */
    public MultiLineFeature(Rectangle2D bounds, Point2D[] points, int[] parts) {
      super(bounds, points);
      this.parts = parts;
    }


    /**
     * Constructor for the MultiLineFeature object
     *
     * @param bounds       Description of Parameter
     * @param count        Description of Parameter
     * @param coordinates  Description of Parameter
     * @param parts        Description of Parameter
     */
    public MultiLineFeature
        (Rectangle2D bounds, int count, double[] coordinates, int[] parts) {
      super(bounds, count, coordinates);
      this.parts = parts;
    }


    /**
     * Gets the PointsOfPart attribute of the MultiLineFeature object
     *
     * @param partIndex  Description of Parameter
     * @return           The PointsOfPart value
     */
    public final Point2D[] getPointsOfPart(int partIndex) {
      Point2D[] p = new Point2D[getPartLength(partIndex)];
      System.arraycopy(points, parts[partIndex], p, 0, p.length);
      return p;
    }


    /**
     * Gets the PartOffset attribute of the MultiLineFeature object
     *
     * @param partIndex  Description of Parameter
     * @return           The PartOffset value
     */
    public final int getPartOffset(int partIndex) {
      return parts[partIndex];
    }


    /**
     * Gets the PartLength attribute of the MultiLineFeature object
     *
     * @param partIndex  Description of Parameter
     * @return           The PartLength value
     */
    public final int getPartLength(int partIndex) {
      return -parts[partIndex] +
          ((partIndex == parts.length - 1)
           ? points.length
           : parts[partIndex + 1]);
    }


    /**
     * Gets the PartCount attribute of the MultiLineFeature object
     *
     * @return   The PartCount value
     */
    public final int getPartCount() {
      return parts.length;
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 19, 2001
   */
  public static class MultiPolygonFeature extends MultiLineFeature {
    /**
     * Constructor for the MultiPolygonFeature object
     *
     * @param bounds  Description of Parameter
     * @param points  Description of Parameter
     * @param parts   Description of Parameter
     */
    public MultiPolygonFeature
        (Rectangle2D bounds, Point2D[] points, int[] parts) {
      super(bounds, points, parts);
    }


    /**
     * Constructor for the MultiPolygonFeature object
     *
     * @param bounds       Description of Parameter
     * @param count        Description of Parameter
     * @param coordinates  Description of Parameter
     * @param parts        Description of Parameter
     */
    public MultiPolygonFeature
        (Rectangle2D bounds, int count, double[] coordinates, int[] parts) {
      super(bounds, count, coordinates, parts);
    }


    /**
     * Description of the Method
     *
     * @return   Description of the Returned Value
     */
    public final MultiPolygonFeature normalizeDirty() {
      int polygonSize = points.length + (parts.length - 1);
      Point2D[] polygon = new Point2D[polygonSize];
      int offset = 0;

      for (int i = 0; i < parts.length; i++) {
        int partLen = getPartLength(i);
        System.arraycopy(points, parts[i], polygon, offset, partLen);
        offset += partLen;
        if (0 < i) {
          polygon[offset++] = points[0];
        }
      }

      return new MultiPolygonFeature
          (bounds.getBounds2D(), polygon, new int[]{0});
    }
  }



}

