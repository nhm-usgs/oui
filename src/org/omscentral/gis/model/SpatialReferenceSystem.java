package org.omscentral.gis.model;


import java.awt.geom.Point2D;


/**
 * The abstract interface of spatial reference systems
 *
 * @see GeographicCoordinateSystem
 * @author  Michael Gruebsch
 * @version 1.0
 */
public interface SpatialReferenceSystem
{

  /**
   * Returns the distance in kilometers between two points. The points are
   * assumed to be in the given spatial reference system.
   *
   * @param P the first point
   * @param Q the second point
   * @return the distance of the two points
   */
  public double getDistance(Point2D P, Point2D Q);
}

