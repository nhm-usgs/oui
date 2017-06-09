package org.omscentral.gis.ui.panel;


import java.awt.Point;

import java.awt.geom.Point2D;

import org.omscentral.gis.model.Theme;
import org.omscentral.gis.model.Feature;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class Location {

  /**
   * Description of the Field
   */
  protected Theme theme;
  /**
   * Description of the Field
   */
  protected Feature feature;
  /**
   * Description of the Field
   */
  protected Point2D location;


  /**
   * Constructor for the Location object
   *
   * @param themeView      Description of Parameter
   * @param theme          Description of Parameter
   * @param location       Description of Parameter
   * @param fuzzyDistance  Description of Parameter
   */
  protected Location
      (ThemeView themeView, Theme theme, Point location, int fuzzyDistance) {
    if ((theme != null) && themeView.contains(theme)) {
      this.theme = theme;
      this.feature = themeView.locateFeature(theme, location, fuzzyDistance);
      this.location = themeView.toRealPoint(location);
    }
    else {
      this.theme = null;
      this.feature = null;
      this.location = null;
    }
  }

}
