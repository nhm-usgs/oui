package org.omscentral.gis.ui.panel;

import java.awt.Point;
import java.awt.Graphics;

public interface Function {

  public Object getResult();

  public void update(Point p);

  public void end();

}
