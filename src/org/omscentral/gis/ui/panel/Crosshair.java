package org.omscentral.gis.ui.panel;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.geom.Point2D;

  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 20, 2001
   */
  public class Crosshair extends Point {

    boolean visible;
    boolean coordinates;

    protected int fuzzyDistance = GISPanel.fuzzyDistance;

    Cursor cursor = null;

    DisplayPane pane;

    /**
     * Constructor for the Crosshair object
     */
    Crosshair(DisplayPane p) {
      visible = false;
      coordinates = false;
      pane = p;
    }


    /**
     * Sets the Location attribute of the Crosshair object
     *
     * @param newX  The new Location value
     * @param newY  The new Location value
     */
    public synchronized void setLocation(int newX, int newY) {
      if (visible) {
        redraw();
      }
      super.setLocation(newX, newY);
      if (visible) {
        redraw();
      }
    }


    /**
     * Sets the Visible attribute of the Crosshair object
     *
     * @param newVisible  The new Visible value
     */
    synchronized void setVisible(boolean newVisible) {
      if (visible != newVisible) {
        redraw();
        visible = newVisible;
        if (visible) {
          cursor = pane.getCursor();
          pane.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        else if (cursor != null) {
          pane.setCursor(cursor);
          cursor = null;
        }
      }
    }


    /**
     * Sets the CoordinatesVisible attribute of the Crosshair object
     *
     * @param coordinates  The new CoordinatesVisible value
     */
    synchronized void setCoordinatesVisible(boolean coordinates) {
      if (this.coordinates != coordinates) {
        if (visible) {
          redraw();
        }
        this.coordinates = coordinates;
        if (visible) {
          redraw();
        }
      }
    }


    /**
     * Gets the Visible attribute of the Crosshair object
     *
     * @return   The Visible value
     */
    final boolean isVisible() {
      return visible;
    }





    /**
     * Description of the Method
     */
    final void redraw() {
      draw(pane.getGraphics());
    }


    /**
     * Description of the Method
     *
     * @param G  Description of Parameter
     */
    synchronized void draw(Graphics G) {
      if (G == null) return;

      Rectangle clip = G.getClipBounds();

      Rectangle bounds = pane.getViewport().getBounds();

      G.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);

      int right = bounds.x + bounds.width;
      int bottom = bounds.y + bounds.height;

      Point C = new Point(x+DisplayPane.INSET +1,y+DisplayPane.INSET+1);

      G.setXORMode(Color.black);
      G.setColor(Color.white);

      if (coordinates) {
        G.drawLine(bounds.x, C.y, right, C.y);
        G.drawLine(C.x, bounds.y, C.x, bottom);

        Point2D Q = pane.getContext().toRealPoint(new Point(x, y));
        String ordinate = String.valueOf(GISPanel.round(Q.getX(), pane.getDecimalScale()));
        String coordinate = String.valueOf(GISPanel.round(Q.getY(), pane.getDecimalScale()));

        GISPanel.drawString(G, ordinate, C.x + 3, true, bottom - 3, false);
        GISPanel.drawString(G, coordinate, right - 3, false, C.y + 3, true);
      }

      G.setPaintMode();
      G.setClip(clip);
    }



  }
