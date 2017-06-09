package org.omscentral.gis.ui.panel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;

/**
 * Description of the Interface
 *
 * @author    en
 * @created   June 19, 2001
 */
public interface Transformation {

  /**
   * Sets the TransformToSize attribute of the Transformation object
   *
   * @param size  The new TransformToSize value
   * @param real  The new TransformToSize value
   */
  void setTransformToSize(Dimension size, Rectangle2D real);


  /**
   * Gets the RealPoint attribute of the Transformation object
   *
   * @param p  Description of Parameter
   * @return   The RealPoint value
   */
  public Point2D getRealPoint(Point p);


  /**
   * Gets the VirtualPoint attribute of the Transformation object
   *
   * @param p  Description of Parameter
   * @return   The VirtualPoint value
   */
  public Point getVirtualPoint(Point2D p);


  /**
   * Gets the RealBox attribute of the Transformation object
   *
   * @param box  Description of Parameter
   * @return     The RealBox value
   */
  public Rectangle2D getRealBox(Rectangle box);


  /**
   * Gets the VirtualBox attribute of the Transformation object
   *
   * @param box  Description of Parameter
   * @return     The VirtualBox value
   */
  public Rectangle getVirtualBox(Rectangle2D box);

}
