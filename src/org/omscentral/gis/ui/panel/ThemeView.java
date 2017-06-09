package org.omscentral.gis.ui.panel;

import java.util.List;
import java.util.ArrayList;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import org.omscentral.gis.model.EnumerableTheme;
import org.omscentral.gis.model.FPRasterTheme;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.ImageTheme;
import org.omscentral.gis.model.Theme;
import org.omscentral.gis.model.ThemeObserver;
import org.omscentral.gis.model.VectorTheme;

import org.omscentral.gis.ui.ColorBlend;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 11, 2001
 * @todo think about improvements regarding layer management.
 *  1. create adapter for node/tree management
 */
public class ThemeView extends DisplayPaneContext implements ThemeObserver, LayerObserver {
  /**
   * Description of the Field
   */
  protected 
      List<Layer>layers;
  /**
   * Description of the Field
   */
  protected Hashtable<Theme,Selection> selections;
  /**
   * Description of the Field
   */
  protected Font labelFont;
  /**
   * The integer font size, not including any zoom ratio
   */
  protected int labelFontSize = 9;
  /**
   * Description of the Field
   */
  protected FontMetrics metrics;
  /**
   * Description of the Field
   */
  protected List<ThemeViewObserver> observers = new ArrayList<ThemeViewObserver>(2);
  /**
   * Description of the Field
   */
  protected final static double BOX_SCALE = 1.1000000000000001D;
  /**
   * Description of the Field
   */
  protected final static double DEFAULT_SCALE = 1D;
  /** The scalar to multiply font size by when adjusting to zoom level */
  protected final static double NON_FIXED_FONT_SCALAR = 100d;

  String tipText = "";

  /**
   * Constructor for the ThemeView object
   */
  public ThemeView() {
    super();
    layers = new java.util.LinkedList<Layer>();
    selections = new Hashtable<Theme,Selection>();
    labelFont = new Font("DIALOG", 2, labelFontSize);
    metrics = getFontMetrics(labelFont);
    observers = null;
  }

//  public String getToolTipText() {
//    return tipText;
//  }

  public void layerChanged(Layer layer) {
    repaint();
  }

  public void themeUpdated(Theme theme) {
    repaint();
  }
  
  public int getLabelSize() {
      return labelFontSize;
  }  
  
  public int increaseLabelSize(double scale) {
      int size = getFontMetrics(labelFont).getFont().getSize();
      size += (size >= 99) ? 0 : ((size >= 24) ? 2 : 1);
      scale = (scale > 0) ? (scale * NON_FIXED_FONT_SCALAR) : 1d;
      labelFont = labelFont.deriveFont((float)size * (float)scale);
      setLabelFont(labelFont);
      System.out.println("Label size = " + size);
 
      return size;
  }
  
  public int decreaseLabelSize(double scale) {
      int size = getFontMetrics(labelFont).getFont().getSize();
      size -= (size <= 1) ? 0 : ((size >= 26) ? 2 : 1);
      scale = (scale > 0) ? (scale * NON_FIXED_FONT_SCALAR) : 1d;
      labelFont = labelFont.deriveFont((float)size * (float)scale);
      setLabelFont(labelFont);
      System.out.println("Label size = " + size);
 
      return size;
  }  
  
  public void setLabelSize(int size, double scale) {
      labelFontSize = size;
      scale = (scale > 0) ? (scale * NON_FIXED_FONT_SCALAR) : 1d;
      labelFont = labelFont.deriveFont((float)size * (float)scale);
      setLabelFont(labelFont);
  }  

  /**
   * Sets the LabelFont attribute of the ThemeView object
   *
   * @param font  The new LabelFont value
   */
  public final synchronized void setLabelFont(Font font) {
    labelFont = font;
    metrics = getFontMetrics(font);
    for (int i = 0; i < layers.size(); i++) {
      ((Layer) layers.get(i)).setLabelFont(font, metrics);
    }

    repaint();
  }


  /**
   * Sets the LabelIndex attribute of the ThemeView object
   *
   * @param i  The new LabelIndex value
   * @param j  The new LabelIndex value
   */
  public final void setLabelIndex(int i, int j) {
    getLayerAt(i).setLabelIndex(j);
    repaint();
  }


  /**
   * Sets the LabelsVisible attribute of the ThemeView object
   *
   * @param i     The new LabelsVisible value
   * @param flag  The new LabelsVisible value
   */
  public final void setLabelsVisible(int i, boolean flag) {
    getLayerAt(i).setLabelsVisible(flag);
    repaint();
  }


  /**
   * Sets the Selection attribute of the ThemeView object
   *
   * @param theme    The new Selection value
   * @param feature  The new Selection value
   * @param flag     The new Selection value
   */
  public synchronized void setSelection(Theme theme, Feature feature, boolean flag) {
    getSelection(theme).setSelection(feature, flag);
  }


  /**
   * Sets the Selection attribute of the ThemeView object
   *
   * @param theme  The new Selection value
   * @param flag   The new Selection value
   */
  public synchronized void setSelection(Theme theme, boolean flag) {
    getSelection(theme).setSelection(flag);
  }


  /**
   * Sets the ThemeVisible attribute of the ThemeView object
   *
   * @param i     The new ThemeVisible value
   * @param flag  The new ThemeVisible value
   */
  public final void setThemeVisible(int i, boolean flag) {
    getLayerAt(i).setVisible(flag);
    repaint();
  }

  public void setThemeVisible(Theme t,boolean vis) {
    int idx = getThemeIndex(t);
    setThemeVisible(idx,vis);
  }


  /**
   * Gets the LabelIndex attribute of the ThemeView object
   *
   * @param i  Description of Parameter
   * @return   The LabelIndex value
   */
  public final int getLabelIndex(int i) {
    return getLayerAt(i).getLabelIndex();
  }


  /**
   * Gets the LabelsVisible attribute of the ThemeView object
   *
   * @param i  Description of Parameter
   * @return   The LabelsVisible value
   */
  public final boolean getLabelsVisible(int i) {
    return getLayerAt(i).getLabelsVisible();
  }


  /**
   * Gets the MaximumSize attribute of the ThemeView object
   *
   * @return   The MaximumSize value
   */
  public Dimension getMaximumSize() {
    return isEmpty() ? super.getMaximumSize() : virtualBounds.getSize();
  }


  /**
   * Gets the MinimumSize attribute of the ThemeView object
   *
   * @return   The MinimumSize value
   */
  public Dimension getMinimumSize() {
    return isEmpty() ? super.getMinimumSize() : virtualBounds.getSize();
  }


  /**
   * Gets the PreferredScrollableViewportSize attribute of the ThemeView object
   *
   * @return   The PreferredScrollableViewportSize value
   */
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }


  /**
   * Gets the PreferredSize attribute of the ThemeView object
   *
   * @return   The PreferredSize value
   */
  public Dimension getPreferredSize() {
    return isEmpty() ? super.getPreferredSize() : virtualBounds.getSize();
  }


  /**
   * Gets the ScrollableBlockIncrement attribute of the ThemeView object
   *
   * @param rectangle  Description of Parameter
   * @param i          Description of Parameter
   * @param j          Description of Parameter
   * @return           The ScrollableBlockIncrement value
   */
  public int getScrollableBlockIncrement(Rectangle rectangle, int i, int j) {
    return i != 0 ? rectangle.height : rectangle.width;
  }


  /**
   * Gets the ScrollableTracksViewportHeight attribute of the ThemeView object
   *
   * @return   The ScrollableTracksViewportHeight value
   */
  public boolean getScrollableTracksViewportHeight() {
    return isEmpty();
  }


  /**
   * Gets the ScrollableTracksViewportWidth attribute of the ThemeView object
   *
   * @return   The ScrollableTracksViewportWidth value
   */
  public boolean getScrollableTracksViewportWidth() {
    return isEmpty();
  }


  /**
   * Gets the ScrollableUnitIncrement attribute of the ThemeView object
   *
   * @param rectangle  Description of Parameter
   * @param i          Description of Parameter
   * @param j          Description of Parameter
   * @return           The ScrollableUnitIncrement value
   */
  public int getScrollableUnitIncrement(Rectangle rectangle, int i, int j) {
    int k = 1;
    return j <= 0 ? -k : k;
  }


  /**
   * Gets the SelectionBounds attribute of the ThemeView object
   *
   * @param theme  Description of Parameter
   * @return       The SelectionBounds value
   */
  public final Rectangle2D getSelectionBounds(Theme theme) {
    return getSelection(theme).getBounds2D();
  }


  /**
   * Gets the ThemeAt attribute of the ThemeView object
   *
   * @param i  Description of Parameter
   * @return   The ThemeAt value
   */
  public final Theme getThemeAt(int i) {
    return getLayerAt(i).getTheme();
  }


  /**
   * Gets the ThemeCount attribute of the ThemeView object
   *
   * @return   The ThemeCount value
   */
  public final int getThemeCount() {
    return layers.size();
  }


  /**
   * Gets the ThemeIndex attribute of the ThemeView object
   *
   * @param theme  Description of Parameter
   * @return       The ThemeIndex value
   */
  public final int getThemeIndex(Theme theme) {
    for (int i = 0; i < getThemeCount(); i++) {
      if (theme == getThemeAt(i)) {
        return i;
      }
    }

    return -1;
  }


  /**
   * Gets the Empty attribute of the ThemeView object
   *
   * @return   The Empty value
   */
  public boolean isEmpty() {
    return getThemeCount() == 0;
  }


  /**
   * Gets the Selected attribute of the ThemeView object
   *
   * @param theme    Description of Parameter
   * @param feature  Description of Parameter
   * @return         The Selected value
   */
  public final boolean isSelected(Theme theme, Feature feature) {
    return getSelection(theme).isSelected(feature);
  }


  /**
   * Gets the ThemeVisible attribute of the ThemeView object
   *
   * @param i  Description of Parameter
   * @return   The ThemeVisible value
   */
  public final boolean isThemeVisible(int i) {
    return getLayerAt(i).isVisible();
  }


  /**
   * Adds a feature to the Theme attribute of the ThemeView object
   *
   * @param theme  The feature to be added to the Theme attribute
   */
  public final void addTheme(Theme theme) {
//    if (getLayerOf(theme) != null)
//      throw new RuntimeException("Cannot add theme to themeview twice");
    theme.addObserver(this);
    insertTheme(theme, getThemeCount());
  }


  /**
   * Adds a feature to the ThemeViewObserver attribute of the ThemeView object
   *
   * @param themeviewobserver  The feature to be added to the ThemeViewObserver
   *      attribute
   */
  public synchronized void addThemeViewObserver(ThemeViewObserver themeviewobserver) {
    if (observers == null)
      observers = new ArrayList<ThemeViewObserver>(2);
    if (observers.contains(themeviewobserver)) return;
    observers.add(themeviewobserver);
    themeviewobserver.themeViewUpdated(this);
  }


  /**
   * Description of the Method
   *
   * @param theme  Description of Parameter
   * @return       Description of the Returned Value
   */
  public final boolean contains(Theme theme) {
    return getThemeIndex(theme) >= 0;
  }


  /**
   * Description of the Method
   */
  public void flush() {
    for (int i = 0; i < layers.size(); i++) {
      ((Layer) layers.get(i)).flush();
    }
  }

  /**
   * Description of the Method
   *
   * @param t  Description of Parameter
   * @param f  Description of Parameter
   */
  public synchronized void drawFeature(Theme t, Feature f) {
//    Graphics2D g2 = (Graphics2D)getGraphics();
//    if (g2 == null) {
//      return;
//    }
//    Layer layerToPaint = getLayerOf(t);
//    int index = layers.indexOf(layerToPaint);
//    g.translate(-virtualBounds.x, -virtualBounds.y);
//    layerToPaint.drawFeature(f, g);
//    g.setClip(trans.createTransformedShape(f.getBounds2D()).getBounds());
//    for (int i = index + 1; i < layers.size(); i++) {
//      ((Layer) layers.get(i)).draw(g2);
//    }
//    g.translate(virtualBounds.x, virtualBounds.y);
  }


  /**
   * Description of the Method
   *
   * @param image  Description of Parameter
   * @param i      Description of Parameter
   * @param j      Description of Parameter
   * @param k      Description of Parameter
   * @param l      Description of Parameter
   * @param i1     Description of Parameter
   * @return       Description of the Returned Value
   */
  public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1) {
    if ((i & 0x38) != 0) {
      if ((i & 0x28) != 0) {
        repaint();
      }
      return (i & 0x20) == 0;
    }
    else {
      return super.imageUpdate(image, i, j, k, l, i1);
    }
  }


  /**
   * Description of the Method
   *
   * @param theme  Description of Parameter
   * @param i      Description of Parameter
   */
  public synchronized void insertTheme(Theme theme, int i) {
    insertTheme(theme,null,i);
  }

  public synchronized void insertTheme(Theme theme,Layer layer,int i) {
    if (layer == null)
      layer = createLayer(theme);
    layer.setLabelFont(labelFont, metrics);
    layers.add(i,layer);
    selections.put(theme, new Selection(theme, layer));
    updateBounds();
//    TimeSeries timeseries = getTimeSeries(theme);
//    if (timeseries != null) {
//      timeseries.addTimeSeriesListener(this);
//      timeseries.addTimeSeriesListener(layer);
//    }
    notifyChange();
  }


  /**
   * Description of the Method
   *
   * @param theme  Description of Parameter
   * @param point  Description of Parameter
   * @param i      Description of Parameter
   * @return       Description of the Returned Value
   */
  public final Feature locateFeature(Theme theme, Point viewCoords, int fuzzy) {
    return !contains(theme) ?
           null :
           getLayerOf(theme).locateFeature(viewCoords, fuzzy);
  }


  /**
   * Description of the Method
   *
   * @param theme  Description of Parameter
   * @param r2d    Description of Parameter
   * @param flag   Description of Parameter
   * @return       Description of the Returned Value
   */
  public final List locateFeatures(Theme theme, Rectangle2D r2d, boolean flag) {
    return !contains(theme) || !getLayerOf(theme).isVisible()
         ? new java.util.LinkedList()
         : getLayerOf(theme).locateFeatures(r2d, flag);
  }


  /**
   * Description of the Method
   *
   * @param i  Description of Parameter
   * @param j  Description of Parameter
   */
  public synchronized void moveTheme(int i, int j) {
//    Object tmp = layers.get(i);
    Layer tmp = layers.get(i);
    layers.set(i, layers.get(j));
    layers.set(j, tmp);
    repaint();
  }


  /**
   * Description of the Method
   *
   * @param g  Description of Parameter
   */
  public void paint(Graphics g) {
    super.paint(g);
    java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
    if (!isEmpty()) {
      for (int i = 0; i < layers.size(); i++) {
        ((Layer) layers.get(i)).draw(g2);
      }
    }
  }


  /**
   * Description of the Method
   *
   * @param theme  Description of Parameter
   */
  public synchronized void removeTheme(Theme theme) {
    if (contains(theme)) {
      theme.removeObserver(this);
      Layer layer = getLayerOf(theme);
//      TimeSeries timeseries = getTimeSeries(theme);
//      if (timeseries != null) {
//        timeseries.removeTimeSeriesListener(layer);
//        timeseries.removeTimeSeriesListener(this);
//      }
      selections.remove(theme);
      layers.remove(layer);
      updateBounds();
      notifyChange();
    }
  }


  /**
   * Description of the Method
   *
   * @param themeviewobserver  Description of Parameter
   */
  public synchronized void removeThemeViewObserver(ThemeViewObserver themeviewobserver) {
    observers.remove(themeviewobserver);
  }


  /**
   * Description of the Method
   *
   * @param theme       Description of Parameter
   */
//  public void timeStepChanged(TimeSeries timeseries) {
//    repaint();
//  }


  /**
   * Description of the Method
   *
   * Description of the Method
   *
   * @param theme       Description of Parameter
   */
  public synchronized void toggleSelection(Theme theme) {
    getSelection(theme).toggleSelection();
  }


  /**
   * Description of the Method
   *
   * @param theme    Description of Parameter
   * @param feature  Description of Parameter
   */
  public synchronized void toggleSelection(Theme theme, Feature feature) {
    getSelection(theme).toggleSelection(feature);
  }


  /**
   * Notify all ThemeViewObservers of change.
   */
  protected void notifyChange() {
    for (int i = 0; i < observers.size(); i++) {
      ((ThemeViewObserver)observers.get(i)).themeViewUpdated(this);
    }
  }

  /**
   * Gets the LayerAt attribute of the ThemeView object
   *
   * @param i  Description of Parameter
   * @return   The LayerAt value
   */
  public final Layer getLayerAt(int i) {
    return (Layer) layers.get(i);
  }


  /**
   * Gets the LayerOf attribute of the ThemeView object
   *
   * @param theme  Description of Parameter
   * @return       The LayerOf value
   */
  public final Layer getLayerOf(Theme theme) {
    return getLayerAt(getThemeIndex(theme));
  }


  /**
   * Gets the Selection attribute of the ThemeView object
   *
   * @param theme  Description of Parameter
   * @return       The Selection value
   */
  protected final Selection getSelection(Theme theme) {
    if (theme == null) return null;
    return (Selection) selections.get(theme);
  }


  /**
   * Gets the TimeSeries attribute of the ThemeView object
   *
   * @param theme  Description of Parameter
   * @return       The TimeSeries value
   */
//  private final TimeSeries getTimeSeries(Theme theme) {
//    TimeSeriesProvider timeseriesprovider = null;
//    if (theme instanceof TimeSeriesProvider) {
//      timeseriesprovider = (TimeSeriesProvider) theme;
//    }
//    else
//        if (theme instanceof FPRasterTheme) {
//      org.omscentral.gis.model.FPRasterModel fprastermodel = ((FPRasterTheme) theme).getRasterModel();
//      if (fprastermodel instanceof TimeSeriesProvider) {
//        timeseriesprovider = (TimeSeriesProvider) fprastermodel;
//      }
//    }
//    return timeseriesprovider == null ? null : timeseriesprovider.getTimeSeries();
//  }


  /**
   * Gets the TimeSeries attribute of the ThemeView object
   *
   * Description of the Method
   *
   * @param theme  Description of Parameter
   * @return       The TimeSeries value
   * @return       Description of the Returned Value
   */
  private final Layer createLayer(Theme theme) {
    Layer l = null;
    if (theme instanceof VectorTheme) {
      l = new VectorLayer((VectorTheme) theme, trans);
    }
    else if (theme instanceof ImageTheme) {
      l = new ImageLayer((ImageTheme) theme, trans, this);
    }
    else if (theme instanceof FPRasterTheme) {
      l = new FPRasterLayer((FPRasterTheme) theme, trans, this);
    }

    l.addListener(this);

    return l;
  }


  /**
   * Description of the Method
   *
   * @param rectangle  Description of Parameter
   */
  private final void repaintVirtualArea(Rectangle rectangle) {
    if (rectangle != null) {
      repaint(rectangle);
    }
  }


  /**
   * Description of the Method
   */
  private final synchronized void updateBounds() {
    realBounds = null;
    if (isEmpty()) {
      realBounds = null;
      virtualBounds = null;
      trans.setToScale(1.0D, 1.0D);
      repaint();
    }
    else {
      realBounds = ((Layer) layers.get(0)).getRealBounds();
      for (int i = 1; i < layers.size(); i++) {
        realBounds.add(((Layer) layers.get(i)).getRealBounds());
      }
      super.setVirtualBoundsSize(getWidth(),getHeight());
    }
  }


  public void updateContents() {
    for (int i = 0; i < layers.size(); i++) {
      ((Layer) layers.get(i)).updateScale(trans);
    }
  }





  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   * @todo make this more flexible for Swing use.
   *  1. Should this implement javax.swing.ListModel?
   *  2. Should this also be a list listener for external modifications?
   *     in case of using a "features" table which supports "reverse" selection
   */
  protected class Selection {

    Theme theme;
    Layer layer;
    List<Feature> features;

    /**
     * Constructor for the Selection object
     *
     * @param theme1  Description of Parameter
     * @param layer1  Description of Parameter
     */
    Selection(Theme theme1, Layer layer1) {
      theme = theme1;
      layer = layer1;
      features = new java.util.LinkedList<Feature>();
    }


    /**
     * Sets the Selection attribute of the Selection object
     *
     * @param feature  The new Selection value
     * @param flag     The new Selection value
     */
    synchronized void setSelection(Feature feature, boolean flag) {
      if (feature != null && isSelected(feature) ^ flag) {
        if (flag) {
          features.add(feature);
        }
        else {
          features.remove(feature);
        }
        layer.setSelection(feature, flag);
        repaintVirtualArea(layer.getVirtualBounds(feature));

        notifyChange();
      }
    }


    /**
     * Sets the Selection attribute of the Selection object
     *
     * @param flag  The new Selection value
     */
    synchronized void setSelection(boolean flag) {
      if (theme instanceof EnumerableTheme) {
        EnumerableTheme enumerabletheme = (EnumerableTheme) theme;
        for (java.util.Iterator i = enumerabletheme.iterator();
            i.hasNext();
            setSelection((Feature) i.next(), flag)) {
          ;
        }

        notifyChange();
      }
    }


    /**
     * Gets the BoundsR2D attribute of the Selection object
     *
     * @return   The BoundsR2D value
     */
    synchronized Rectangle2D getBounds2D() {
      Rectangle2D r2d = null;
      for (int i = 0; i < features.size(); i++) {
        Feature feature = getSelected(i);
        if (r2d == null) {
          r2d = feature.getBounds2D().getBounds2D();
        }
        else {
          r2d.add(feature.getBounds2D());
        }
      }

      return r2d;
    }


    /**
     * Gets the Selected attribute of the Selection object
     *
     * @return   The Selected value
     */
    final List getSelected() {
      return features;
    }


    /**
     * Gets the Selected attribute of the Selection object
     *
     * @param i  Description of Parameter
     * @return   The Selected value
     */
    final Feature getSelected(int i) {
      return (Feature) features.get(i);
    }


    /**
     * Gets the SelectionCount attribute of the Selection object
     *
     * @return   The SelectionCount value
     */
    final int getSelectionCount() {
      return features.size();
    }


    /**
     * Gets the Selected attribute of the Selection object
     *
     * @param feature  Description of Parameter
     * @return         The Selected value
     */
    final boolean isSelected(Feature feature) {
      return features.contains(feature);
    }


    /**
     * Description of the Method
     */
    final synchronized void toggleSelection() {
      if (theme instanceof EnumerableTheme) {
        EnumerableTheme enumerabletheme = (EnumerableTheme) theme;
        for (java.util.Iterator i = enumerabletheme.iterator();
            i.hasNext();
            toggleSelection((Feature) i.next())) {
          ;
        }
        notifyChange();
      }
    }


    /**
     * Description of the Method
     *
     * @param feature  Description of Parameter
     */
    final synchronized void toggleSelection(Feature feature) {
      if (feature != null) {
        setSelection(feature, isSelected(feature) ^ true);
      }
      notifyChange();
    }
  }





}
