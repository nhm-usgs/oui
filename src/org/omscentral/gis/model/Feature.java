package org.omscentral.gis.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Description of the Interface
 *
 * @author    en
 * @created   June 8, 2001
 */
public interface Feature {

  /**
   * Returns the bounding box of the geometrical object. The bounding box is
   * defined to be the smallest rectangle which contains the object and whose
   * sides are parallel to the coordinate axises.
   *
   * @return   the bounding box of the object
   */
  public Rectangle2D getBounds2D();


  /**
   * Cecks if a given point is inside the geometrical object.
   *
   * @param x  the x part of the point coordinate
   * @param y  the y part of the point coordinate
   * @return   true iff the point (x,y) is inside the object
   */
  public boolean contains(double x, double y);


  /**
   * Cecks if a given point is inside the geometrical object.
   *
   * @param P  the point
   * @return   true iff the point P is inside the object
   */
  public boolean contains(Point2D p);

}
