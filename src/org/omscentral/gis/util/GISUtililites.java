package org.omscentral.gis.util;

import org.omscentral.gis.model.VectorModel;
import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.model.Feature;

import org.omscentral.gis.ui.panel.DisplayPaneContext;

import java.util.List;
import java.util.ArrayList;

import java.awt.Shape;
import java.awt.Shape;
import java.awt.geom.*;

public class GISUtililites {

  public static VectorModel.MultiPolygonFeature createPolyFeature(Shape s) {
    Point2D[] points = getPoints(s);
    int[] parts = {0};
    return new VectorModel.MultiPolygonFeature(
      s.getBounds2D(),points,parts
    );
  }

  public static Point2D[] getPoints(Shape s) {
    PathIterator path = new FlatteningPathIterator(
      s.getPathIterator(new AffineTransform()),10);
    List<Point2D.Double> points = new ArrayList<Point2D.Double>();
    while (!path.isDone()) {
      double[] ps = new double[6];
      int seg = path.currentSegment(ps);

      if (seg == path.SEG_CUBICTO){
          for (int i = 0; i < 3; i++)
            points.add(new Point2D.Double(ps[i*2],ps[i*2 + 1]));
      } else if (seg == path.SEG_LINETO || seg == path.SEG_MOVETO ) {
          points.add(new Point2D.Double(ps[0],ps[1]));
      } else if (seg == path.SEG_QUADTO) {
          for (int i = 0; i < 2; i++)
            points.add(new Point2D.Double(ps[i*2],ps[i*2 + 1]));
      }
      path.next();
    }
    Point2D[] ret = new Point2D[points.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = (Point2D) points.get(i);
    }
    return ret;

  }

  public static Object[] getMinAndMax(VectorTheme theme,int idx)
    throws Exception
  {
    Feature[] feat = theme.getFeatureArray();
    Object[] vals = new Object[theme.getFeatureCount()];
    for (int i = 0; i < feat.length; i++) {
      vals[i] = theme.getAttribute(feat[i],idx);
    }
    if (! (vals[0] instanceof Comparable))
      throw new Exception("Not comparable values");
    java.util.Arrays.sort(vals);
    return new Object[]{vals[0],vals[vals.length-1]};
  }

  public static String createHTMLImageMapTag(String mapname,VectorTheme theme,DisplayPaneContext context) {
    StringBuffer html = new StringBuffer();
    html.append("<MAP name='" + mapname + "'>");
    Feature[] features = theme.getFeatureArray();
    for (int i = 0; i < features.length; i++) {
      html.append("<AREA shape='poly' coords='");
      int cnt = 0;
      if (features[i] instanceof VectorModel.MultiPointFeature) {
        VectorModel.MultiPointFeature mpf = (VectorModel.MultiPointFeature) features[i];
        Point2D[] points = mpf.getPoints();
        java.awt.Point p = null;
        java.awt.Point last = null;
        StringBuffer pointBuf = new StringBuffer();
        for (int j = 0; j < points.length; j++) {
          p = context.toVirtualPoint(points[j]);
          if (last != null) {
            if (Math.abs(p.x - last.x )< 2 || Math.abs( p.y - last.y) < 2 )
              continue;
          }
          last = p;
          pointBuf.append(p.x + "," + p.y);
          pointBuf.append(",");
        }
        html.append(pointBuf.substring(0,pointBuf.length()-1));
        html.append("' href='" + theme.getAttribute(mpf,1) + "'>\n");
      }
    }
    html.append("</MAP>");
    return html.toString();
  }

  public static int getUTMZone(double lon) {
    return (int) Math.floor((lon + 180.0) / 6) +1;
  }

  public static double getUTMCentralMeridian(int zone) {
    double cmerid = Math.toRadians(-183.0 + (zone * 6.0));
    return cmerid;
  }

  public static double arcLengthOfMeridian(double phi) {
    double alpha, beta, gamma, delta, epsilon, n;
    double result;

    // ellipsoid constants for WGS84
    double sm_a = 6378137.0;
    double sm_b = 6356752.314;

    /* Precalculate n */
    n = (sm_a - sm_b) / (sm_a + sm_b);

    /* Precalculate alpha */
    alpha = ((sm_a + sm_b) / 2.0)
       * (1.0 + (Math.pow (n, 2.0) / 4.0) + (Math.pow (n, 4.0) / 64.0));

    /* Precalculate beta */
    beta = (-3.0 * n / 2.0) + (9.0 * Math.pow (n, 3.0) / 16.0)
       + (-3.0 * Math.pow (n, 5.0) / 32.0);

    /* Precalculate gamma */
    gamma = (15.0 * Math.pow (n, 2.0) / 16.0)
        + (-15.0 * Math.pow (n, 4.0) / 32.0);

    /* Precalculate delta */
    delta = (-35.0 * Math.pow (n, 3.0) / 48.0)
        + (105.0 * Math.pow (n, 5.0) / 256.0);

    /* Precalculate epsilon */
    epsilon = (315.0 * Math.pow (n, 4.0) / 512.0);

    /* Now calculate the sum of the series and return */
    result = alpha
        * (phi + (beta * Math.sin (2.0 * phi))
            + (gamma * Math.sin (4.0 * phi))
            + (delta * Math.sin (6.0 * phi))
            + (epsilon * Math.sin (8.0 * phi)));

    return result;
  }

  protected static void calculateLonLatToUTM(double lon, double lat,double[] xy,double cmerid) {
    double phi = Math.toRadians(lat);
    double lambda = Math.toRadians(lon);
    double lambda0 = cmerid;

    // ellipsoid constants for WGS84
    double sm_a = 6378137.0;
    double sm_b = 6356752.314;
    double sm_EccSquared = 6.69437999013e-03;

    double N, nu2, ep2, t, t2, l;
    double l3coef, l4coef, l5coef, l6coef, l7coef, l8coef;
    double tmp;

    /* Precalculate ep2 */
    ep2 = (Math.pow (sm_a, 2.0) - Math.pow (sm_b, 2.0)) / Math.pow(sm_b, 2.0);

    /* Precalculate nu2 */
    nu2 = ep2 * Math.pow (Math.cos (phi), 2.0);

    /* Precalculate N */
    N = Math.pow (sm_a, 2.0) / (sm_b * Math.sqrt (1 + nu2));

    /* Precalculate t */
    t = Math.tan (phi);
    t2 = t * t;
    tmp = (t2 * t2 * t2) - Math.pow (t, 6.0);

    /* Precalculate l */
    l = lambda - lambda0;

    /* Precalculate coefficients for l**n in the equations below
       so a normal human being can read the expressions for easting
       and northing
       -- l**1 and l**2 have coefficients of 1.0 */
    l3coef = 1.0 - t2 + nu2;

    l4coef = 5.0 - t2 + 9 * nu2 + 4.0 * (nu2 * nu2);

    l5coef = 5.0 - 18.0 * t2 + (t2 * t2) + 14.0 * nu2
        - 58.0 * t2 * nu2;

    l6coef = 61.0 - 58.0 * t2 + (t2 * t2) + 270.0 * nu2
        - 330.0 * t2 * nu2;

    l7coef = 61.0 - 479.0 * t2 + 179.0 * (t2 * t2) - (t2 * t2 * t2);

    l8coef = 1385.0 - 3111.0 * t2 + 543.0 * (t2 * t2) - (t2 * t2 * t2);

    /* Calculate easting (x) */
    xy[0] = N * Math.cos (phi) * l + (N / 6.0 * Math.pow(Math.cos (phi), 3.0) * l3coef * Math.pow(l, 3.0))
          + (N / 120.0 * Math.pow (Math.cos (phi), 5.0) * l5coef * Math.pow (l, 5.0))
          + (N / 5040.0 * Math.pow (Math.cos (phi), 7.0) * l7coef * Math.pow (l, 7.0));

    /* Calculate northing (y) */
    xy[1] = arcLengthOfMeridian (phi) + (t / 2.0 * N * Math.pow (Math.cos (phi), 2.0) * Math.pow (l, 2.0))
          + (t / 24.0 * N * Math.pow (Math.cos (phi), 4.0) * l4coef * Math.pow (l, 4.0))
          + (t / 720.0 * N * Math.pow (Math.cos (phi), 6.0) * l6coef * Math.pow (l, 6.0))
          + (t / 40320.0 * N * Math.pow (Math.cos (phi), 8.0) * l8coef * Math.pow (l, 8.0));
  }

  public static int lonLatToUTM(double[] lonlat) {
    double UTMScaleFactor = 0.9996;

    int zone = getUTMZone(lonlat[0]);

    calculateLonLatToUTM(lonlat[0],lonlat[1],lonlat,getUTMCentralMeridian(zone));
    lonlat[0] = (lonlat[0] * UTMScaleFactor) + 500000.0;
    lonlat[1] *= UTMScaleFactor;
    if (lonlat[1] < 0.0)
      lonlat[1] += 10000000.0;
    return zone;
  }

  /**
   *
   */
  static public void main(String[] args) {
    double xy[] = {-104,40.5};
    System.out.println("zone "+ lonLatToUTM(xy));
    System.out.println("utm northing : " + xy[1]);
    System.out.println("utm easting : " + xy[0]);
  }
}
