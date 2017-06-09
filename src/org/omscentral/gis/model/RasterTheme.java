package org.omscentral.gis.model;

import java.util.Enumeration;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public abstract class RasterTheme extends BasicTheme {

  private Transformation transformation;
  private Dimension size;



  /**
   * Constructs a new RasterTheme. The parameters matrix and vector are to
   * relate the raster coordinates to real world coordinates. Raster coordinates
   * vary from 0 to the width respectivly height of the raster where the upper
   * left corner has the coordinates (0,0). In real world coordinates the lower
   * left corner of an orthogonal rectangle has the lowest coordinate values.
   * <P>
   *
   * The conversion is done as the following equation describes: <PRE>
   *
   * <BLOCKQUOTE><TT>| | | || | | | | Xr(0,0) | | A(0,0) B(0,1) || Xi(0,0) | |
   * C(0,0) | | | = | || | + | | | Yr(1,0) | | D(1,0) E(1,1) || Yi(1,0) | |
   * F(1,0) | | | | || | | |</TT></BLOCKQUOTE> <BLOCKQUOTE><TT>Xi - pixel column
   * number Yi - pixel row number</TT></BLOCKQUOTE> <BLOCKQUOTE><TT>Xr - x
   * map-coordinate of the center of the pixel Yr - y map-coordinate of the
   * center of the pixel</TT></BLOCKQUOTE> <BLOCKQUOTE><TT>A - dimension of a
   * pixel in map units in x direction (x-scale) B - rotation term C -
   * translation term, x map-coordinate of the center of the most upper-left
   * pixel D - rotation term E - negative dimension of a pixel in map units in y
   * direction (y-scale) F - translation term, y map-coordinate of the center of
   * the most upper-left pixel</TT></BLOCKQUOTE></PRE> Assuming the values:
   * <PRE>
   *
   * <BLOCKQUOTE><TT>A = 1 B = 0.5 C = 2 D = 0 E = -1 F = -3</TT></BLOCKQUOTE>
   * </PRE>one has to initialize a RasterTheme by: <PRE>
   *
   * <BLOCKQUOTE><TT>RasterTheme R = new RasterTheme ("The Theme's Name", new
   * double[][] {new double[]{1, 0.5}, new double[]{0, -1}}, new double[]{2,
   * -3});</TT></BLOCKQUOTE></PRE> If the parameter size is different from null
   * it is set by a call to setSize(int,int). <P>
   *
   * If the given matrix is not invertable, that is the transformation is not
   * affine, real world to raster coordinate translation is not defined.
   *
   * @param themeName  an arbitrary name for this theme
   * @param matrix     the matrix for scaling and rotating
   * @param vector     the vector for translating
   * @param size       the raster size
   * @see              Theme.Plain#Theme.Plain(java.lang.String)
   * @see              #setSize(int, int)
   */
  public RasterTheme
      (String themeName, double[][] matrix, double[] vector, Dimension size) {
    super(themeName);
    transformation = new Transformation(matrix, vector);
    if (size != null) {
      setSize(size.width, size.height);
    }
    else {
      this.size = null;
    }
  }



  /**
   * Constructs a new RasterTheme. It calls the other constructor by <PRE>
   *
   * <BLOCKQUOTE>RasterTheme(themeName, matrix, vector, null);</BLOCKQUOTE>
   * </PRE>
   *
   * @param themeName  an arbitrary name for this theme
   * @param matrix     the matrix for scaling and rotating
   * @param vector     the vector for translating
   * @see              #RasterTheme(java.lang.String, double[][], double[],
   *      java.awt.Dimension)
   */
  public RasterTheme(String themeName, double[][] matrix, double[] vector) {
    this(themeName, matrix, vector, null);
  }


  /*
   * -------------------------------------------------------------------------
   * Access methods (public) - Miscellaneous
   *
   */

  /**
   * Sets the size of the raster.
   *
   * @param width   the raster width
   * @param height  the raster height
   */
  public synchronized void setSize(int width, int height) {
    size = new Dimension(width, height);
  }


  /**
   * Returns the bounding box for this theme. This is done by a call <PRE>
   *
   * <BLOCKQUOTE>getBoundsR2D(0, 0, D.width, D.height);</BLOCKQUOTE></PRE> where
   * D is the Dimension returned by getSize()
   *
   * @return   the bounding box
   * @see      #getBoundsR2D(int, int, int, int)
   * @see      #getSize()
   */
  public Rectangle2D getBounds() {
    Dimension D = getSize();
    return getBounds(0, 0, D.width, D.height);
  }



  /**
   * Returns the number of features in this theme.
   *
   * @return   0
   */
  public int getFeatureCount() {
    return 0;
  }


  /**
   * Returns a single feature.
   *
   * @param index  the feature index
   * @return       null
   */
  public Feature getFeature(int index) {
    return null;
  }


  /**
   * Returns the feature at the given world coordinate. If this coordinate is
   * inside the raster it will be converted to a raster coordinate and the
   * appropriate getFeatureAt method will be called. Otherwise null is returned.
   *
   * @param x  the x part of the world coordinate
   * @param y  the y part of the world coordinate
   * @return   the feature
   * @see      #contains(double, double)
   * @see      #getRasterCoordinate(double, double)
   * @see      #getFeatureAt(java.awt.Point)
   */
  public final Feature getFeatureAt(double x, double y) {
    return (contains(x, y)) ? getFeatureAt(getRasterCoordinate(x, y)) : null;
  }


  /**
   * Returns the number of attributes available for each feature.
   *
   * @return   0
   */
  public int getAttributeCount() {
    return 0;
  }


  /**
   * Returns the names of the attributes. RasterTheme returns an empty array.
   *
   * @return   an empty array
   */
  public String[] getAttributeNames() {
    return new String[0];
  }


  /**
   * Returns all attributes of a feature. RasterTheme returns an empty array.
   *
   * @param feature  the feature in question
   * @return         an empty array
   */
  public Object[] getAttributes(Feature feature) {
    return new Object[0];
  }


  /**
   * Returns the size of the raster.
   *
   * @return   the raster size
   */
  public Dimension getSize() {
    return size;
  }


  /**
   * Calculates the real world coordinate of a raster coordinate. The
   * transformation is done by the means of the matrix and vector given at
   * construction time.
   *
   * @param x  the x part of the raster coordinate
   * @param y  the y part of the raster coordinate
   * @return   the real world coordinate
   * @see      #RasterTheme(java.lang.String, double[][], double[],
   *      java.awt.Dimension)
   */
  public Point2D getWorldCoordinate(int x, int y) {
    return new Point2D.Double
        (transformation.transformX(x, y), transformation.transformY(x, y));
  }


  /**
   * Calculates the real world coordinate of a raster coordinate. This is done
   * by a call to getWorldCoordinate(int,int)
   *
   * @param raster  the raster coordinate
   * @return        the real world coordinate
   * @see           #getWorldCoordinate(int, int)
   */
  public final Point2D getWorldCoordinate(Point raster) {
    return getWorldCoordinate(raster.x, raster.y);
  }


  /**
   * Calculates the raster coordinate of a real world coordinate. The
   * transformation is done by the inversion of the matrix and vector given at
   * construction time.If the given matrix is not invertable, that is the
   * transformation is not affine, real world to raster coordinate translation
   * is not defined and always results in the point at the origin.
   *
   * @param x  the x part of the world coordinate
   * @param y  the y part of the world coordinate
   * @return   the raster coordinate
   * @see      #RasterTheme(java.lang.String, double[][], double[],
   *      java.awt.Dimension)
   */
  public Point getRasterCoordinate(double x, double y) {
    return new Point
        ((int) Math.round(transformation.inverseX(x, y)),
        (int) Math.round(transformation.inverseY(x, y)));
  }


  /**
   * Calculates the raster coordinate of a real world coordinate. This is done
   * by a call to getWorldCoordinate(double,double)
   *
   * @param world  the world coordinate
   * @return       the raster coordinate
   * @see          #getWorldCoordinate(double, double)
   */
  public final Point getRasterCoordinate(Point2D world) {
    return getRasterCoordinate(world.getX(), world.getY());
  }


  /**
   * Calculates the real world bounding box of an area within the raster. It
   * should be noted that there might no direct correspondation between these
   * two geometrical rectangles because the result has underlied an affine
   * transformation.
   *
   * @param x       the x part of the coordinate of the upper left corner
   * @param y       the y part of the coordinate of the upper left corner
   * @param width   the width of the area
   * @param height  the height of the area
   * @return        the real world bounding box
   */
  public Rectangle2D getBounds(int x, int y, int width, int height) {
    double xMin = transformation.transformX(x, y);
    double yMin = transformation.transformY(x, y);
    double xMax = transformation.transformX(width, height);
    double yMax = transformation.transformY(width, height);
    double x1 = Math.min(xMin, xMax);
    double y1 = Math.min(yMin, yMax);
    double x2 = Math.max(xMin, xMax);
    double y2 = Math.max(yMin, yMax);
    return new Rectangle2D.Double(
        x1, y1,
        Math.abs(x2 - x1), Math.abs(y2 - y1)
        );
  }


  /**
   * Calculates the real world bounding box of an area within the raster. This
   * is done by a call to getBounds(int,int,int,int).
   *
   * @param R  the rectangular area within the raster
   * @return   the real world bounding box
   */
  public final Rectangle2D getBounds(Rectangle R) {
    return getBounds(R.x, R.y, R.width, R.height);
  }



  /**
   * Returns the feature at the given raster coordinate.
   *
   * @param x  the x part of the raster coordinate
   * @param y  the y part of the raster coordinate
   * @return   the feature
   */
  public abstract Feature getFeatureAt(int x, int y);


  /**
   * Returns the feature at the given raster coordinate. This is done by a call
   * to getFeatureAt(int,int)
   *
   * @param raster  the raster coordinate
   * @return        the feature
   * @see           #getFeatureAt(int, int)
   */
  public final Feature getFeatureAt(Point raster) {
    return getFeatureAt(raster.x, raster.y);
  }


  /*
   * -------------------------------------------------------------------------
   * Access methods (public) - Attributes
   *
   */

  /**
   * Returns the attribute of the feature at the given raster coordinate. The
   * type of the object returned is Double for floating points, Integer for
   * integers, Boolean for logical values and String otherwise.
   *
   * @param x  the x part of the raster coordinate
   * @param y  the y part of the raster coordinate
   * @return   the attribute
   */
  public abstract Object getAttributeAt(int x, int y);


  /**
   * Returns the attribute of the feature at the given raster coordinate. This
   * is done by a call to getAttributeAt(int,int)
   *
   * @param raster  the raster coordinate
   * @return        the attribute
   * @see           #getAttributeAt(int, int)
   */
  public final Object getAttributeAt(Point raster) {
    return getAttributeAt(raster.x, raster.y);
  }



  /**
   * Returns the index of a certain feature. <B>Left abstract.</B>
   *
   * @param feature  the feature
   * @return         the feature index
   */
  public int getIndex(Feature feature) {
    return -1;
  }



  /**
   * Checks if a given real world coordinate is inside the raster. This methods
   * converts the coordinate to a raster coordinate and checks if this point is
   * inside the rectangle defined by (0,0) as its origin and getSize() as its
   * dimension. It should be noted that it is not possible to replace this
   * approach by a call to getBounds().contains(x,y) because the raster might be
   * rotated in the real world.
   *
   * @param x  the x part of the world coordinate
   * @param y  the y part of the world coordinate
   * @return   true if the coordinate is inside the raster.
   * @see      #getSize()
   * @see      #getRasterCoordinate(double, double)
   */
  public boolean contains(double x, double y) {
    Dimension D = getSize();
    Point P = getRasterCoordinate(x, y);
    return ((0 <= P.x) && (P.x < D.width) && (0 <= P.y) && (P.y < D.height));
  }


  /**
   * Checks if a given real world coordinate is inside the raster. This is done
   * by a call to contains(double,double) with the appropriate values.
   *
   * @param world  the world coordinate
   * @return       true if the coordinate is inside the raster.
   * @see          #contains(double, double)
   */
  public final boolean contains(Point2D world) {
    return contains(world.getX(), world.getY());
  }

  protected abstract void modelChanged();

  public abstract RasterModel getRasterModel();

  public abstract VAT getVAT();

  public abstract boolean hasVAT();


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 19, 2001
   */
  private class Transformation {
    double A, B, C, D, E, F, iA, iB, iD, iE;


    /**
     * Constructor for the Transformation object
     *
     * @param matrix  Description of Parameter
     * @param vector  Description of Parameter
     */
    Transformation(double[][] matrix, double[] vector) {
      A = matrix[0][0];
      B = matrix[0][1];
      C = vector[0];
      D = matrix[1][0];
      E = matrix[1][1];
      F = vector[1];

      try {
        double iDet = 1.0 / checkMatrix(matrix);

        iA = iDet * E;
        iB = -iDet * B;
        iD = -iDet * D;
        iE = iDet * A;
      }
      catch (IllegalArgumentException E) {
        System.err.println(E);
        iA = iB = iD = iE = 0.0;
      }
    }


    /**
     * Description of the Method
     *
     * @param matrix  Description of Parameter
     * @return        Description of the Returned Value
     */
    final double checkMatrix(double[][] matrix) {
      if ((matrix.length == 2)
           && (matrix[0].length == 2)
           && (matrix[1].length == 2)) {
        double det =
            matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        if (det != 0) {
          return det;
        }
        else {
          throw new IllegalArgumentException("Matrix irregular");
        }
      }
      else {
        throw new IllegalArgumentException("2x2 matrix required");
      }
    }


    /**
     * Description of the Method
     *
     * @param x  Description of Parameter
     * @param y  Description of Parameter
     * @return   Description of the Returned Value
     */
    final double transformX(double x, double y) {
      return A * x + B * y + C;
    }


    /**
     * Description of the Method
     *
     * @param x  Description of Parameter
     * @param y  Description of Parameter
     * @return   Description of the Returned Value
     */
    final double transformY(double x, double y) {
      return D * x + E * y + F;
    }


    /**
     * Description of the Method
     *
     * @param x  Description of Parameter
     * @param y  Description of Parameter
     * @return   Description of the Returned Value
     */
    final double inverseX(double x, double y) {
      return iA * (x - C) + iB * (y - F);
    }


    /**
     * Description of the Method
     *
     * @param x  Description of Parameter
     * @param y  Description of Parameter
     * @return   Description of the Returned Value
     */
    final double inverseY(double x, double y) {
      return iD * (x - C) + iE * (y - F);
    }
  }

}

