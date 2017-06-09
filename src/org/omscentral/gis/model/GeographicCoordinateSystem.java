package org.omscentral.gis.model;


import java.awt.geom.Point2D;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class GeographicCoordinateSystem implements SpatialReferenceSystem {

  /**
   * The flattening at the poles.
   */
  protected double flattening;

  /**
   * The radius at the equator.
   */
  protected double radius;

  private final static double PI180 = Math.PI / 180.0;



  /**
   * Initializes the geographic reference system.
   *
   * @param flattening  the flattening at the poles
   * @param radius      the radius at the equator
   */
  public GeographicCoordinateSystem(double flattening, double radius) {
    this.flattening = flattening;
    this.radius = radius;
  }


  /**
   * Initializes the geographic reference system with a flattening of
   * 1/298.257222101 and a radius at the equator of 6378.137&nbsp;km.
   */
  public GeographicCoordinateSystem() {
    this(1 / 298.257222101, 6378.137);
  }


  /*
   * -------------------------------------------------------------------------
   * Interface implementation - SpatialReferenceSystem
   *
   */

  /**
   * Returns the distance in kilometers between two points. The points are
   * assumed to be in the given spatial reference system. The algorithm is taken
   * from: <BLOCKQUOTE>Meeus,&nbsp;J.: Astronomische Algorithmen. Leipzig et al.
   * 1994. pg&nbsp;94f. German translation of: Astronomical Algorithms.
   * Richmond, Virgina. 1991.</BLOCKQUOTE>
   *
   * @param P  the first point
   * @param Q  the second point
   * @return   the distance of the two points
   */
  public double getDistance(Point2D P, Point2D Q) {
    double F = (P.getY() + Q.getY()) / 2;
    double G = (P.getY() - Q.getY()) / 2;
    double l = (P.getX() - Q.getX()) / 2;

    double sin2F = Math.sin(F * PI180);
    sin2F *= sin2F;
    double cos2F = Math.cos(F * PI180);
    cos2F *= cos2F;
    double sin2G = Math.sin(G * PI180);
    sin2G *= sin2G;
    double cos2G = Math.cos(G * PI180);
    cos2G *= cos2G;
    double sin2l = Math.sin(l * PI180);
    sin2l *= sin2l;
    double cos2l = Math.cos(l * PI180);
    cos2l *= cos2l;

    double S = sin2G * cos2l + cos2F * sin2l;
    double C = cos2G * cos2l + sin2F * sin2l;

    double w = Math.atan(Math.sqrt(S / C));
    double R = Math.sqrt(S * C) / w;

    double D = 2 * w * radius;
    double H1 = (3 * R - 1) / (2 * C);
    double H2 = (3 * R + 1) / (2 * S);

    return
        D * (1
         + flattening * H1 * sin2F * cos2G
         - flattening * H2 * cos2F * sin2G);
  }


}
