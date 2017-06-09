package org.omscentral.gis.ui.panel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import java.awt.event.*;

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Cursor;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DisplayPane extends JScrollPane {

  private DisplayMode functionMode = null;
  private DisplayMode observerMode = DisplayMode.NO_OBSERVER;
  private DisplayMode controlMode = DisplayMode.CONTROL;
  private boolean coordsVisible = false;
  protected boolean popupEnabled = false;
  protected DisplayPaneContext context = null;
  private List<DisplayPaneListener> listeners = new ArrayList<DisplayPaneListener>(1);
  private Function activeFunction = null;
  private JPopupMenu popup = null;
  private Crosshair crosshair = new Crosshair(this);

  protected double zoomRatio = 2.0;
  protected static int INSET = 10;

  public boolean isFocusable() {
    return true;
  }

  public boolean isRequestFocusEnabled() {
    return true;
  }

  public DisplayPane() {
    super();
    DisplayMode.setPane(this);

    setViewportBorder(BorderFactory.createMatteBorder(
      INSET, INSET, INSET, INSET,java.awt.Color.white)
    );

    addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        processModeEvent(e);
      }
//      public void keyReleased(KeyEvent e) {
//        processModeEvent(e);
//      }
    });

    MouseAdapter a = new MouseAdapter();
    getViewport().addMouseListener(a);
    getViewport().addMouseMotionListener(a);

    getViewport().addComponentListener(new ResizeAdapter());

    crosshair.setVisible(true);
  }

  public void setCursor(Cursor cursor) {
    viewport.setCursor(cursor);
  }

  public Crosshair getCrosshair() {
    return crosshair;
  }

  protected DisplayMode getFunctionMode() {
    return functionMode;
  }

  protected DisplayMode getObserverMode() {
    return observerMode;
  }

  public void setFunctionMode(DisplayMode mode) {
    functionMode = mode;
    if (mode != null) {
      popupEnabled = true;
      setCursor(mode.getCursor());
    }
    else
      popupEnabled = true;
    repaint();
  }

  public void setObserverMode(DisplayMode mode) {
    observerMode = mode;
  }

  public void setPopup(JPopupMenu p) {
    popup = p;
  }

  public JPopupMenu getPopup() {
    return popup;
  }

  public DisplayPaneContext getContext() {
    return context;
  }

  public void setContext(DisplayPaneContext c) {
    if (c == null)
    throw new java.lang.IllegalArgumentException(
      "Cannot use a null context");
    context = c;
    setViewportView(c);
  }

  public void addDisplayPaneListener(DisplayPaneListener l) {
    listeners.add(l);
  }

  public void removeDisplayPaneListener(DisplayPaneListener l) {
    listeners.remove(l);
  }

  protected void fireSelection(Point p, boolean clear) {
    for (int i = 0; i < listeners.size(); i++)
      ((DisplayPaneListener)listeners.get(i)).mouseSelected(p,clear);
  }

  protected void fireSelection(Rectangle r,boolean clear) {
    for (int i = 0; i < listeners.size(); i++)
      ((DisplayPaneListener)listeners.get(i)).mouseSelected(r,clear);
  }

  protected void fireTracking(Point p) {
    for (int i = 0; i < listeners.size(); i++)
      ((DisplayPaneListener)listeners.get(i)).mouseTracked(p);
  }

  protected void fireScaling() {
    for (int i = 0; i < listeners.size(); i++)
      ((DisplayPaneListener)listeners.get(i)).scaleChanged();
  }

  protected void firePanMode() {
    for (int i = 0; i < listeners.size(); i++)
      ((DisplayPaneListener)listeners.get(i)).panMode();
  }

  public double getViewedScale() {
    double w = Math.min(
      context.getVirtualBounds().getWidth(),
      context.getSize().getWidth());
    double h = Math.min(
      context.getVirtualBounds().getHeight(),
      context.getSize().getHeight());
    return Math.max(
      w/viewport.getSize().width,
      h/viewport.getSize().height);
  }

  public void clearMode() {
    functionMode = null;
  }

  public void setCoordsVisible(boolean vis) {
    coordsVisible = vis;
  }

  public boolean areCoordsVisible() {
    return coordsVisible;
  }

  public synchronized void paint(Graphics g) {
    crosshair.setCoordinatesVisible(false);
    super.paint(g);
  }


  public Point getCenterCoordinates() {
    Dimension size = viewport.getSize();
    return new Point(size.width / 2, size.height / 2);
  }


  /**
   * Description of the Method
   *
   * @param P  Description of Parameter
   * @return   Description of the Returned Value
   */
  public Point toViewCoordinates(Point P) {
    return viewportToView(P);
  }

  public void zoomIn() {
    Point view = toViewCoordinates(getCenterCoordinates());
    zoomTo(view, zoomRatio,true);
  }

  public void zoomIn(Point center) {
    Point view = toViewCoordinates(center);
    zoomTo(view, zoomRatio,true);
  }

  public int getDecimalScale() {
    return 1 + Math.round
        ((float) Math.ceil(Math.log(context.getScale()) / Math.log(10)));
  }

  public void zoomOut() {
    Point view = toViewCoordinates(getCenterCoordinates());
    zoomTo(view, 1/zoomRatio,true);
  }

  public void zoomOut(Point center) {
    Point view = toViewCoordinates(center);
    zoomTo(view, 1/zoomRatio,true);
  }



  public void zoomTo(Rectangle r) {
    if (r.isEmpty()) return;
    double scale =
      (0 < r.width) ?
      (double) viewport.getSize().width / r.width :
      0;
    Point corner = this.toViewCoordinates(new Point((int)r.getCenterX(),(int)r.getCenterY()));
    zoomTo(corner,scale,true);
  }

  public void zoomTo(Point viewCenter,double scale,boolean relative) {
    Point2D realCenter = context.toRealPoint(viewCenter);
    context.scaleBy(
      scale,
      relative
    );
    fireScaling();
    adjustViewportToCenter(realCenter);
  }

  private void adjustViewportToCenter(Point2D real) {
    Point p = context.toVirtualPoint(real);
    Rectangle r = viewport.getBounds();
    p.translate(-r.width/2,-r.height/2);
    if (p.x < 0)
      p.x = 0;
    if (p.y < 0)
      p.y = 0;
    viewport.setViewPosition(p);
    viewport.doLayout();
  }

  public final Point viewportToView(Point P) {
    Point Q = viewport.getViewPosition();
    Q.translate(P.x, P.y);
    return Q;
  }

    public void panViewport(int xChange, int yChange) {
    Point p = viewport.getViewPosition();
    p.x += xChange;
    p.y += yChange;
    if (p.x < 0)
      p.x = 0;
    if (p.y < 0)
      p.y = 0;
    viewport.setViewPosition(p);
    viewport.doLayout();
  }

  protected void processModeEvent(InputEvent e) {
    if (context == null) return;

    if (e instanceof MouseEvent) {
      MouseEvent me = (MouseEvent)e;
      fireTracking(me.getPoint());
      if (me.isPopupTrigger() && popupEnabled) {
        popup = getPopup();
        popup.show(this,me.getX(),me.getY());
      }
    }
    invokeFunction(e);
  }

  private void invokeFunction(InputEvent e) {
    if (e instanceof MouseEvent) {
      if (observerMode != null)
        observerMode.invoke((MouseEvent)e);
      if (functionMode != null)
        functionMode.invoke((MouseEvent)e);
    }
    else
    if (e instanceof KeyEvent) {
      if (observerMode != null)
        observerMode.invoke((KeyEvent)e);
      if (functionMode != null)
        functionMode.invoke((KeyEvent)e);
      controlMode.invoke((KeyEvent)e);
    }
  }

  class MouseAdapter implements MouseListener, MouseMotionListener {
    public void mouseDragged(MouseEvent e) {
      processModeEvent(e);
    }
    public void mouseMoved(MouseEvent e) {
      processModeEvent(e);
    }
    public void mouseClicked(MouseEvent e) {
      requestFocus();
      processModeEvent(e);
    }
    public void mousePressed(MouseEvent e) {
      processModeEvent(e);
    }
    public void mouseReleased(MouseEvent e) {
      processModeEvent(e);
    }
    public void mouseEntered(MouseEvent e) {
      requestFocusInWindow();
      DisplayMode.setPane(DisplayPane.this);
      processModeEvent(e);
    }
    public void mouseExited(MouseEvent e) {
      processModeEvent(e);
    }
  }

  class ResizeAdapter extends ComponentAdapter {
    public void componentResized(ComponentEvent e) {
      Rectangle r = context.getVirtualBounds();
      if (r == null) return;
      Dimension d = r.getSize();
      context.setVirtualBoundsSize(d.width,d.height);
      fireScaling();
    }
  }


}
