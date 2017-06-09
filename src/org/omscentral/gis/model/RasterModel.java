package org.omscentral.gis.model;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

/**
 * RasterModel is an interface specification for raster GIS data. All raster
 * data have some things in common which are to make accessible by special
 * classes designed to read those data. <P>
 *
 * Raster data and the real world area which they represent are provided in
 * different coordinate systems with opposite orientation of its y-axis: <DL>
 * <DT>Raster data</DT> <DD>The coordinate space of the raster grid data is a
 * discrete coordinate system whose origin (0,0) is the coordinate of the in the
 * upper left cell. Therefore the lower right cell has the coordinate
 * (width-1,height-1).</DD> <DT>Real world area</DT> <DD>The real world area is
 * assumed to be a clip from a continuous geographic reference system where the
 * origin might be outsight of the area in question. Furthermore it is assumed
 * that the lower left corner has the lowest values for the x- and y-part of its
 * coordinate.</DD></DL> The real world area is subdivided into a grid whose
 * cells have a certain width and height which are equal for all cells. The
 * raster grid is defined with the same number of rows and columns. <P>
 *
 * The upper left cell in the real world grid is mapped onto the upper left cell
 * of the raster and vica versa. If A is the real world grid matrix, B the
 * raster grid matrix, and Z(x,y) is the element in column x and row y of a
 * matrix Z the general mapping between A and B is defined as: <BLOCKQUOTE>
 * A(x,y) = B(x,y)</BLOCKQUOTE> The following equations are used (all values in
 * real world units): <BLOCKQUOTE><DL><DT>width of one raster cell</DT> <DD>
 * getBounds().getWidth() / getRasterSize().width</DD> <DT>height of one raster
 * cell</DT> <DD>getBounds().getHeight() / getRasterSize().height</DD> <DT>x
 * part of the coordinate of the upper left corner of the most upper left raster
 * cell</DT> <DD>getBounds().getXMin()</DD> <DT>y part of the coordinate of the
 * upper left corner of the most upper left raster cell</DT> <DD>
 * getBounds().getYMin() - getBounds().getHeight()</DL></BLOCKQUOTE>
 *
 * @author    Michael Gr&uuml;bsch
 * @created   June 11, 2001
 */
public interface RasterModel extends Cloneable {

  /**
   * Returns the dimension of the raster. Raster data are assumed to be provided
   * in a rectangular grid with a certain number of rows and columns where the
   * number of rows is defined by the height part and the number of columns by
   * the width part of the returned dimension. The amount of data can by
   * calculated by multiplying width and height.
   *
   * @return   the dimension of the raster
   */
  public Dimension getRasterSize();

  /**
   * Returns the real world bounding box of the area represented by the raster
   * data. The origin, that is the lower left corner, of the real world area
   * might be accessed by getBounds().getMin(), the total hight by
   * getBounds().getHeight(), the total width by getBounds().getWidth().
   *
   * @return   the real world bounding box of the raster data
   * @see      R2D#getMin()
   * @see      R2D#getHeight()
   * @see      R2D#getWidth()
   */
  public Rectangle2D getBounds();


  /**
   * Returns the raster data in the specified column and row. Valid values for
   * column are between 0 and getRasterSize().width-1, for row between 0 and
   * getRasterSize().height-1. By getRasterDataAt(0,0) the data in the upper
   * left corner are retrieved.
   *
   * @param column  the column of the requested data
   * @param row     the row of the requested data
   * @return        the requested data
   */
  public Object getRasterDataAt(int column, int row);


  /**
   * Gets the Minimum attribute of the RasterModel object
   *
   * @return   The Minimum value
   */
  public Number getMinimum();


  /**
   * Gets the Maximum attribute of the RasterModel object
   *
   * @return   The Maximum value
   */
  public Number getMaximum();


  /**
   * Gets the MissingValue attribute of the RasterModel object
   *
   * @return   The MissingValue value
   */
  public Number getMissingValue();

  public Number getCellSize();

  public Object clone();

}

