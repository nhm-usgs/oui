package org.omscentral.gis.ui.panel;

import java.awt.Point;
import java.awt.Rectangle;

public interface DisplayPaneListener {
  public void mouseTracked(Point p);
  public void mouseSelected(Point p,boolean clear);
  public void mouseSelected(Rectangle r,boolean clear);
  public void scaleChanged();
  public void panMode();
}
