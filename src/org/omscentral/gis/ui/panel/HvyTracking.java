package org.omscentral.gis.ui.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.omscentral.gis.model.*;
import java.awt.geom.*;

public class HvyTracking implements GISPanel.CoordinatesListener {
  private Map<Layer,String> trackingItems = new HashMap<Layer,String>(5);
  private JLabel label = new JLabel("");
  private GISPanel gis;

  public HvyTracking(GISPanel gis) {
    this.gis = gis;
  }

  public void update(Point2D realCoord, Point screenCoord, int decimalScale) {

  }

  public JLabel getTrackingLabel() {
    return label;
  }

  public void setTrackingItem(Layer layer,String name) {
    trackingItems.put(layer,name);
  }

  public String getTrackingItem(Layer layer) {
    return trackingItems.get(layer).toString();
  }

  public void update(Layer layer,Point p) {
    Theme theme = layer.getTheme();
    String item = trackingItems.get(layer).toString();
    Object val = null;
    if (item == null) return;
    if (theme instanceof VectorTheme) {
      Feature f = layer.locateFeature(p,GISPanel.fuzzyDistance);
      val = ( (VectorTheme) theme).getAttribute(f,item);
    } else if (theme instanceof RasterTheme) {
      RasterTheme rt = (RasterTheme) theme;

    }
  }
}
