package org.omscentral.gis.ui.panel;

  import java.awt.Point;
  import java.awt.Rectangle;
  import java.awt.Graphics;
  import java.awt.Color;
  import java.awt.FontMetrics;
  import java.awt.geom.Rectangle2D;

  public class SelectFunction extends Rectangle implements Function{
    Point touchDown = null;
    Point lastPoint = null;

    /**
     * Description of the Field
     */
    protected static int X_PAD = 3;
    /**
     * Description of the Field
     */
    protected static int Y_PAD = 0;

    public Object getResult() {
      return ((Rectangle) this).getBounds();
    }

    DisplayPane pane;
    /**
     * Constructor for the ViewRectangle object
     *
     * @param P  Description of Parameter
     */
    SelectFunction(DisplayPane p) {
      super();
      pane = p;
    }


    /**
     * Description of the Method
     *
     * @param P  Description of Parameter
     */
    public synchronized void calculateBounds(Point P) {
      if (touchDown == null) touchDown = P;
      width = Math.abs(touchDown.x - P.x);
      height = Math.abs(touchDown.y - P.y);
      x = Math.min(touchDown.x, P.x);
      y = Math.min(touchDown.y, P.y);
      lastPoint = P;
    }



    /**
     * Description of the Method
     *
     * @param P  Description of Parameter
     */
    public synchronized void update(Point P) {
      redraw();
      calculateBounds(P);
      redraw();
    }

    public void end() {
      redraw();
    }


    /**
     * Description of the Method
     */
    final void redraw() {
      if (touchDown == null) return;
      draw(pane.getGraphics());
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
        String absoluteX = String.valueOf(GISPanel.round(box.getWidth(), decimal));
        String absoluteY = String.valueOf(GISPanel.round(box.getHeight(), decimal));

        GISPanel.drawString(G, absoluteX, Q.x + 3, true, Q.y - 3, false);
        GISPanel.drawString(G, absoluteY, Q.x + 3, true, Q.y + 3, true);
      }
    }


    /**
     * Description of the Method
     *
     * @param G  Description of Parameter
     */
    public synchronized void draw(Graphics G) {
      if (isEmpty()) return;

      int pad = 5;
      int rad = pad - 2;

      Rectangle clip = G.getClipBounds();
      Rectangle bounds = pane.getViewport().getBounds();

      G.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);

      G.setXORMode(Color.black);
      G.setColor(Color.white);

      Point P = new Point(x+pane.INSET,y+pane.INSET);
      Point Q = new Point(lastPoint.x+pane.INSET,lastPoint.y+pane.INSET);

      int right = P.x + width;
      int bottom = P.y + height;
      int golden = P.y + Math.round(height / 1.61803f);

      G.drawRect(P.x, P.y, width, height);
      G.drawOval(Q.x - rad, Q.y - rad, 2 * rad, 2 * rad);
      G.drawLine(Q.x, Q.y - pad, Q.x, Q.y + pad);
      G.drawLine(Q.x - pad, Q.y, Q.x + pad, Q.y);

      if ((Q.x < P.x) || (right < Q.x)) {
        G.drawLine(Q.x, Q.y, (Q.x < P.x) ? P.x : right, Q.y);
      }
      if ((Q.y < P.y) || (bottom < Q.y)) {
        G.drawLine(Q.x, Q.y, Q.x, (Q.y < P.y) ? P.y : bottom);
      }

      if (pane.areCoordsVisible()) {
        drawCoordinates(G, P, Q);
      }

      G.setPaintMode();
      G.setClip(clip);
    }



  }
