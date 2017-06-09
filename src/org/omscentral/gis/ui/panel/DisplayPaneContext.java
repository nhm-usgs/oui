package org.omscentral.gis.ui.panel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Dimension;


import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;


import javax.swing.JComponent;

public abstract class DisplayPaneContext extends JComponent{
  protected AffineTransform trans = new AffineTransform();
  protected Rectangle2D realBounds = null;
  protected Rectangle virtualBounds = null;

  public DisplayPaneContext() {
    setBackground(java.awt.Color.white);
  }

  protected AffineTransform getInverseTransform() {
    try {
      return trans.createInverse();
    }
    catch (java.awt.geom.NoninvertibleTransformException e) {}
    throw new java.lang.IllegalStateException(
      "Transform has somehow become univertable\n" + trans
    );
  }

  protected final boolean isInteger(long value) {
    return
        (((long) Integer.MIN_VALUE <= value)
         && (value <= (long) Integer.MAX_VALUE));
  }

  protected final boolean isScaleValid(double scale, Rectangle2D realBounds) {
    if (realBounds == null) return false;
    long xdim = Math.round(scale * realBounds.getWidth());
    long ydim = Math.round(scale * realBounds.getHeight());
    return
        (isInteger(Math.round(scale * realBounds.getMinX()))
         && isInteger(Math.round(scale * realBounds.getMinY()))
         && isInteger(Math.round(scale * realBounds.getMaxX()))
         && isInteger(Math.round(scale * realBounds.getMaxY()))
         && (0 < xdim) && isInteger(xdim)
         && (0 < ydim) && isInteger(ydim));
  }

  public Rectangle2D toRealRectangle(Rectangle r) {
    return getInverseTransform().createTransformedShape(r).getBounds2D();
  }
  public Rectangle getVirtualBounds() {
    return virtualBounds != null ? virtualBounds : getBounds();
  }
  public Rectangle2D getRealBounds() {
    return realBounds != null ? realBounds : getBounds();
  }
  public double getScaleX() {
    return trans.getScaleX();
  }
  public double getScaleY() {
    return trans.getScaleY();
  }
  public double getScale() {
    return Math.max(getScaleX(),getScaleY());
  }
  public Point toVirtualPoint(Point2D p) {
    trans.transform(p,p);
    return new Point((int)p.getX(),(int)p.getY());
  }
  public Rectangle toVirtualRectangle(Rectangle2D r) {
    return trans.createTransformedShape(r).getBounds();
  }
  public Point2D toRealPoint(Point p) {
    Point2D p2d = new Point2D.Double();
    return getInverseTransform().transform(
      new Point2D.Double(p.getX(),p.getY()),
      p2d
    );
  }


  /**
   * Description of the Method
   */
  protected final synchronized void updateScale(Dimension newSize) {
    if (realBounds == null) return;

    double scale = Math.min(
      (double) (newSize.width - 1) / realBounds.getWidth(),
      (double) (newSize.height - 1)/ realBounds.getHeight()
    );

    double xc;
    double yc;
    double xt;
    double yt;

    if (isBiggerThanParent(newSize.width,newSize.height)) {
      xc = (newSize.getWidth() / 2) / scale - (realBounds.getWidth() / 2);
      yc = (-newSize.getHeight() / 2) / scale - (realBounds.getHeight() / 2);
    }
    else {
      xc = (getWidth() / 2) / scale - (realBounds.getWidth() / 2);
      yc = (-getHeight() / 2) / scale - (realBounds.getHeight() / 2);
    }
    xt = xc - realBounds.getX();
    yt = yc - realBounds.getY();

    if (scale == getScale() &&
        xt == trans.getTranslateX() &&
        yt == trans.getTranslateY())
      return;

    trans.setToScale(scale, -scale);
    trans.translate(xt,yt);

    virtualBounds = virtualBox(realBounds);

    updateContents();

    repaint();
  }



  public void scaleBy(double scalar,boolean relative) {
    if (relative)
      scalar = scalar * getScale();

    if (isScaleValid(scalar, realBounds)) {
      setVirtualBoundsSize(
        (int) Math.round(scalar * realBounds.getWidth()),
        (int) Math.round(scalar * realBounds.getHeight())
      );
    }
  }

  protected void setVirtualBoundsSize(int width, int height) {
    Dimension newSize = new Dimension(width,height);
    Dimension adjustedSize;
    if (isBiggerThanParent(width,height))
      setSize(newSize = constrainSize(newSize));
    else
      setSize(getParentSize());
    updateScale(newSize);
  }

  /**
   * Description of the Method
   *
   * @param r2d  Description of Parameter
   * @return     Description of the Returned Value
   */
  protected final Rectangle virtualBox(Rectangle2D r2d) {
    return trans.createTransformedShape(r2d).getBounds();
  }

  private Dimension getParentSize() {
    java.awt.Container parent = getParent();
    if (parent == null) return super.getMinimumSize();
    return parent.getSize();
  }

  private Dimension constrainSize(Dimension d) {
    Dimension p = getParentSize();
    if (d.width < p.width)
      d.width = p.width;
    if (d.height < p.height)
      d.height = p.height;
    return d;
  }

  private boolean isBiggerThanParent(int w, int h) {
    Dimension d = getParentSize();
    return (d.width < w || d.height < h);
  }


  public void paint(Graphics g) {
    super.paint(g);
    java.awt.Color tmp = g.getColor();
    g.setColor(getBackground());
    g.fillRect(0,0,getWidth(),getHeight());
    g.setColor(tmp);
  }

  public abstract void updateContents();

}
