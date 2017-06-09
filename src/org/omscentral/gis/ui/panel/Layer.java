package org.omscentral.gis.ui.panel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.*;

import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.Theme;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 8, 2001
 */
public abstract class Layer {

  /**
   * Description of the Field
   */
  protected Theme theme;
  /**
   * Description of the Field
   */
  protected AffineTransform trans = new AffineTransform();
  /**
   * Description of the Field
   */
  protected Rectangle2D realBounds;
  /**
   * Description of the Field
   */
  protected Rectangle virtualBounds;
  /**
   * Description of the Field
   */
  protected boolean visible;
  /**
   * Description of the Field
   */
  protected boolean labels;
  /**
   * Description of the Field
   */
  protected int labelIdx;
  /**
   * Description of the Field
   */
  protected Font labelFont;
  /**
   * Description of the Field
   */
  protected FontMetrics metrics;

  /**
   * Description of the Field
   */
  protected Renderer renderer;

  /**
   * Description of the Field
   */
  protected List<LayerObserver> listeners;


  /**
   * Constructor for the Layer object
   *
   * @param theme  Description of Parameter
   * @param trans  Description of Parameter
   */
  protected Layer(Theme theme, AffineTransform trans) {
    visible = true;
    labels = false;
    labelIdx = -1;
    labelFont = null;
    metrics = null;
    this.theme = theme;
    trans = trans;
    realBounds = theme.getBounds();
    if (realBounds.isEmpty()) {
      realBounds = blowUp(realBounds);
    }
    virtualBounds = trans.createTransformedShape(realBounds).getBounds();
    listeners = new ArrayList<LayerObserver>(2);
  }


  /**
   * Description of the Method
   *
   * @param font         The new LabelFont value
   * @param fontmetrics  The new LabelFont value
   */
//  public void timeStepChanged(TimeSeries timeseries) {
//  }



  /**
   * Description of the Method
   *
   * Sets the LabelFont attribute of the Layer object
   *
   * @param font         The new LabelFont value
   * @param fontmetrics  The new LabelFont value
   */
  public synchronized void setLabelFont(Font font, FontMetrics fontmetrics) {
    labelFont = font;
    metrics = fontmetrics;
  }


  /**
   * Sets the LabelIndex attribute of the Layer object
   *
   * @param i  The new LabelIndex value
   */
  public synchronized void setLabelIndex(int i) {
    labelIdx = i;
  }


  /**
   * Sets the LabelsVisible attribute of the Layer object
   *
   * @param flag  The new LabelsVisible value
   */
  public synchronized void setLabelsVisible(boolean flag) {
    labels = flag;
  }
  
  public abstract Object getColoringModel();


  /**
   * Sets the Selection attribute of the Layer object
   *
   * @param feature  The new Selection value
   * @param flag     The new Selection value
   */
  public abstract void setSelection(Feature feature, boolean flag);


  /**
   * Sets the Outline attribute of the Layer object
   *
   * @param f        The new Outline value
   * @param outline  The new Outline value
   */
  public void setOutline(Feature f, boolean outline) {
  }


  /**
   * Sets the Selection attribute of the Layer object
   *
   * @param flag  The new Selection value
   */
  public abstract void setSelection(boolean flag);


  /**
   * Sets the Visible attribute of the Layer object
   *
   * @param flag  The new Visible value
   */
  public synchronized void setVisible(boolean flag) {
    visible = flag;
  }



  /**
   * Gets the LabelIndex attribute of the Layer object
   *
   * @return   The LabelIndex value
   */
  public int getLabelIndex() {
    return labelIdx;
  }


  /**
   * Gets the LabelsVisible attribute of the Layer object
   *
   * @return   The LabelsVisible value
   */
  public final boolean getLabelsVisible() {
    return labels;
  }


  /**
   * Gets the RealBounds attribute of the Layer object
   *
   * @return   The RealBounds value
   */
  public final Rectangle2D getRealBounds() {
    return realBounds.getBounds2D();
  }


  /**
   * Gets the Theme attribute of the Layer object
   *
   * @return   The Theme value
   */
  public final Theme getTheme() {
    return theme;
  }


  /**
   * Gets the VirtualBounds attribute of the Layer object
   *
   * @return   The VirtualBounds value
   */
  public final Rectangle getVirtualBounds() {
    return virtualBounds.getBounds();
  }


  /**
   * Gets the VirtualBounds attribute of the Layer object
   *
   * @param feature  Description of Parameter
   * @return         The VirtualBounds value
   */
  public abstract Rectangle getVirtualBounds(Feature feature);


  /**
   * Gets the Selected attribute of the Layer object
   *
   * @param feature  Description of Parameter
   * @return         The Selected value
   */
  public abstract boolean isSelected(Feature feature);


  /**
   * Gets the Visible attribute of the Layer object
   *
   * @return   The Visible value
   */
  public final boolean isVisible() {
    return visible;
  }


  /**
   * Description of the Method
   */
  public void clearOutlined() {
  }


  /**
   * Description of the Method
   *
   * @param g  Description of Parameter
   */
  public abstract void draw(Graphics g);


  /**
   * Description of the Method
   */
  public abstract void flush();



  /**
   * Description of the Method
   *
   * @param i           Description of Parameter
   * @param viewCoords  Description of Parameter
   * @return            Description of the Returned Value
   */
  public abstract Feature locateFeature(Point viewCoords, int i);


  /**
   * Description of the Method
   *
   * @param r2d   Description of Parameter
   * @param flag  Description of Parameter
   * @return      Description of the Returned Value
   */
  public abstract java.util.List locateFeatures(Rectangle2D r2d, boolean flag);


  /**
   * Description of the Method
   *
   * @param trans  Description of Parameter
   */
  public synchronized void updateScale(AffineTransform trans) {
    this.trans = trans;
    virtualBounds = trans.createTransformedShape(realBounds).getBounds();
  }


  /**
   * Adds a feature to the Listener attribute of the Layer object
   *
   * @param obs  The feature to be added to the Listener attribute
   */
  public void addListener(LayerObserver obs) {
    if (!listeners.contains(obs)) {
      listeners.add(obs);
    }
  }


  /**
   * Description of the Method
   *
   * @param obs  Description of Parameter
   */
  public void removeListener(LayerObserver obs) {
    listeners.remove(obs);
  }


  /**
   * Description of the Method
   */
  protected void notifyChange() {
    for (int i = 0; i < listeners.size(); i++) {
      ((LayerObserver) listeners.get(i)).layerChanged(this);
    }
  }


  /**
   * Description of the Method
   *
   * @param r2d  Description of Parameter
   * @return     Description of the Returned Value
   */
  protected final Rectangle2D blowUp(Rectangle2D r2d) {
    Rectangle2D r2d1 = (Rectangle2D) r2d.clone();
//    double d = r2d.getMinX();
//    double d1 = r2d.getMinY();
//    if (r2d.getWidth() <= 0D) {
//      int i = getAccuracy(d);
//      double d2 = Math.pow(10D, i >= 0 ? i : i + 1);
//      r2d1.add(new Point2D.Double(d - d2, d1));
//      r2d1.add(new Point2D.Double(d + d2, d1));
//    }
//    if (r2d.getHeight() <= 0D) {
//      int j = getAccuracy(d1);
//      double d3 = Math.pow(10D, j >= 0 ? j : j + 1);
//      r2d1.add(new Point2D.Double(d, d1 - d3));
//      r2d1.add(new Point2D.Double(d, d1 + d3));
//    }
    r2d1.setFrame(r2d1.getCenterX() - 10,
        r2d1.getCenterY() - 10,
        r2d1.getWidth() + 20,
        r2d1.getHeight() + 20);
    return r2d1;
  }


  /**
   * Gets the Accuracy attribute of the Layer object
   *
   * @param d  Description of Parameter
   * @return   The Accuracy value
   */
  private final int getAccuracy(double d) {
    if (d == 0.0D) {
      return 0;
    }
    d = Math.abs(d);
    boolean flag = d != Math.floor(d);
    double d1 = flag ? 10D : 0.10000000000000001D;
    byte byte0 = ((byte) (flag ? -1 : 1));
    int i = 0;
    do {
      d *= d1;
      i += byte0;
    } while (flag ^ (d == Math.floor(d)));
    return i;
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   January 3, 2002
   */
  public interface Renderer {
    /**
     * Description of the Method
     *
     * @param o  Description of Parameter
     * @param g  Description of Parameter
     */
    public void render(Object o, Graphics2D g);
  }

}
