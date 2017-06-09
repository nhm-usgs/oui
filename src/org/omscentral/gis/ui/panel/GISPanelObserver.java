package org.omscentral.gis.ui.panel;

import java.awt.event.MouseEvent;

import java.awt.geom.Point2D;

import org.omscentral.gis.model.Theme;
import org.omscentral.gis.model.Feature;


/**
 * Description of the Interface
 *
 * @author    en
 * @created   June 19, 2001
 */
public interface GISPanelObserver {

  /**
   * Description of the Method
   *
   * @param theme     Description of Parameter
   * @param feature   Description of Parameter
   * @param location  Description of Parameter
   * @param inside    Description of Parameter
   */
  public void tracked
      (Theme theme, Feature feature, Point2D location, boolean inside);


  /**
   * Description of the Method
   *
   * @param location  Description of Parameter
   * @param inside    Description of Parameter
   */
  public void tracked(Point2D location, boolean inside);


  /**
   * Description of the Method
   *
   * @param location  Description of Parameter
   * @param inside    Description of Parameter
   */
  public void pointed(Point2D location, boolean inside);


  /**
   * Description of the Method
   *
   * @param theme       Description of Parameter
   * @param feature     Description of Parameter
   * @param isSelected  Description of Parameter
   */
  public void selected(Theme theme, Feature feature, boolean isSelected);


  /**
   * Description of the Method
   *
   * @param theme       Description of Parameter
   * @param isSelected  Description of Parameter
   */
  public void selected(Theme theme, boolean isSelected);


  /**
   * Description of the Method
   *
   * @param theme    Description of Parameter
   * @param feature  Description of Parameter
   * @param index    Description of Parameter
   */
  public void customize(Theme theme, Feature feature, int index);


  /**
   * Description of the Method
   */
  public void functionInvoked();


  /**
   * Description of the Method
   */
  public void functionFinished();


}

