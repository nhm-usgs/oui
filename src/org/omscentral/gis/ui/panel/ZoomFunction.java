package org.omscentral.gis.ui.panel;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

public class ZoomFunction extends SelectFunction {

  /**
   * Constructor for the ZoomRectangle object
   *
   * @param P  Description of Parameter
   */
  ZoomFunction(DisplayPane d) {
    super(d);
  }

  double getScaleRelative() {
    return (0 < width) ? (double) pane.getViewport().getSize().width / width : 0;
  }

  /**
   * Description of the Method
   *
   * @param P  Description of Parameter
   */
  public synchronized void calculateBounds(Point P) {
    if (touchDown == null) touchDown = P;

    Dimension size = pane.getViewport().getSize();
    float alpha = (float) size.height / size.width;

    width = Math.abs(touchDown.x - P.x);
    height = Math.abs(touchDown.y - P.y);
    float beta = (float) height / width;

    if (alpha < beta) {
      height = Math.round(width * alpha);
    }
    else if (beta < alpha) {
      width = Math.round(height / alpha);
    }

    x = touchDown.x;
    y = touchDown.y;
    if (P.x < touchDown.x) {
      x -= width;
    }
    if (P.y < touchDown.y) {
      y -= height;
    }

    lastPoint = P;
  }


  /**
   * Description of the Method
   *
   * @param G  Description of Parameter
   * @param P  Description of Parameter
   * @param Q  Description of Parameter
   */
  void drawCoordinates(Graphics G, Point P, Point Q) {
    Rectangle2D box = pane.getContext().toRealRectangle(this);
    int decimal = pane.getDecimalScale();

    String ordinate = String.valueOf(GISPanel.round(box.getMinX(), decimal));
    String coordinate = String.valueOf(GISPanel.round(box.getMaxY(), decimal));

    GISPanel.drawString(G, ordinate, P.x - 3, false, P.y - 3, false);
    GISPanel.drawString(G, coordinate, P.x - 3, false, P.y + 3, true);

    if (!isEmpty()) {
      String relative =
          String.valueOf(Math.round(getScaleRelative() * 100.0f)) + "%";
      String absoluteX = String.valueOf(GISPanel.round(box.getWidth(), decimal));
      String absoluteY = String.valueOf(GISPanel.round(box.getHeight(), decimal));

      GISPanel.drawString(G, relative, Q.x - 3, false, Q.y - 3, false);
      GISPanel.drawString(G, absoluteX, Q.x + 3, true, Q.y - 3, false);
      GISPanel.drawString(G, absoluteY, Q.x + 3, true, Q.y + 3, true);
    }
  }
}
