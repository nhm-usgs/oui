/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omscentral.gis.ui.panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.ui.ColoredShape;


class VectorThemeColorModel extends AbstractVectorThemeColorModel {

  static int fillPaintDefaultCnt = 0;
  Paint[] fillPaintDefaults = {
    new Color(253,245,230),
    new Color(224,255,250),
    new Color(240,240,240),
    new Color(255,228,220),
    new Color(230,230,250),
    new Color(187,255,250),
    new Color(255,187,255),
    new Color(185,211,238),
    new Color(255,236,139),
  };

  Shape[] shapeList;
  Paint[] borderPaint;
  static Paint defaultBorderPaint = Color.black;
  Paint[] fillPaint;
  Paint defaultFillPaint;
  Stroke[] borderStroke;
  ColoredShape[] shapes;
  float transparency = 1f;
  static ColoredShape defaultShape = ColoredShape.whiteShape;
  static Stroke defaultStroke = new BasicStroke(1f);
  static Paint selectedPaint = Color.yellow;
  static Paint outlinedPaint = Color.blue;
  static Stroke selectedStroke = new BasicStroke(1f);
  static Stroke outlinedStroke = new BasicStroke(1f);

  public VectorThemeColorModel(VectorTheme theme) {
    super(theme);
    selectDefaultPaint();
  }

  private void selectDefaultPaint() {
    defaultFillPaint = fillPaintDefaults[fillPaintDefaultCnt++];
    if (fillPaintDefaultCnt >= fillPaintDefaults.length)
      fillPaintDefaultCnt = 0;
    defaultShape.setFillColor((Color)defaultFillPaint);
  }

    /**
   * Gets the Stroke attribute of the ColorModel object
   *
   * @param feature  Description of Parameter
   * @return         The Stroke value
   */
  public  Stroke getStroke(Feature feature) {
    if (borderStroke != null) {
      int idx = theme.getIndex(feature);
      if (borderStroke[idx] != null)
        return borderStroke[idx];
    }
    return defaultStroke;
  }

  /**
   * Gets the Paint attribute of the ColorModel object
   *
   * @param feature  Description of Parameter
   * @return         The Paint value
   */
  public  Paint getFillPaint(Feature feature) {
    if (fillPaint != null) {
      int idx = theme.getIndex(feature);
      if (fillPaint[idx] != null)
        return fillPaint[idx];
    }
    return defaultFillPaint;
  }


  /**
   * Gets the BorderPaint attribute of the ColorModel object
   *
   * @param feature  Description of Parameter
   * @return         The BorderPaint value
   */
  public  Paint getBorderPaint(Feature feature) {
    if (borderPaint != null) {
      int idx = theme.getIndex(feature);
      if (borderPaint[idx] != null)
        return borderPaint[idx];
    }
    return defaultBorderPaint;
  }


  /**
   * Gets the SelectedPaint attribute of the ColorModel object
   *
   * @return   The SelectedPaint value
   */
  public  Paint getSelectedPaint() {
    return selectedPaint;
  }


  /**
   * Gets the OutlinedPaint attribute of the ColorModel object
   *
   * @return   The OutlinedPaint value
   */
  public   Paint getOutlinedPaint() {
    return outlinedPaint;
  }


  public   Stroke getOutlinedStroke() {
    return outlinedStroke;
  }
  /**
   * Gets the SelectedStroke attribute of the ColorModel object
   *
   * @return   The SelectedStroke value
   */
  public  Stroke getSelectedStroke() {
    return selectedStroke;
  }


  /**
   * Sets the Stroke attribute of the ColorModel object
   *
   * @param stroke   The new Stroke value
   * @param feature  The new Stroke value
   * @return         Description of the Returned Value
   */
  public  boolean setStroke(Stroke stroke, Feature feature) {
    if (stroke == null || feature == null) return false;
    if (borderStroke == null)
      borderStroke = new Stroke[theme.getFeatureCount()];
    borderStroke[theme.getIndex(feature)] = stroke;
    return true;
  }

  /**
   * Sets the Paint attribute of the ColorModel object
   *
   * @param paint  The new Paint value
   * @param idx    The new Paint value
   * @return       Description of the Returned Value
   */
  public  boolean setFillPaint(Paint paint, int idx) {
      if (fillPaint == null)
          fillPaint = new Paint[theme.getFeatureCount()];
      
      if (paint == null || idx >= fillPaint.length) return false;
      
      fillPaint[idx] = paint;
      return true;
  }

  /**
   * Sets the BorderPaint attribute of the ColorModel object
   *
   * @param paint  The new BorderPaint value
   * @param idx    The new BorderPaint value
   * @return       Description of the Returned Value
   */
  public  boolean setBorderPaint(Paint paint, int idx) {
      if (borderPaint == null)
          borderPaint = new Paint[theme.getFeatureCount()];
      
      if (paint == null || idx >= borderPaint.length) return false;
      
      borderPaint[idx] = paint;
      return true;
  }
  

  /**
   * Sets the BorderPaint attribute of the ColorModel object
   *
   * @param f  The new BorderPaint value
   * @param p  The new BorderPaint value
   * @return   Description of the Returned Value
   */
  public  boolean setBorderPaint(Feature f, Paint p) {
    if (p == null || f == null) return false;
    if (borderPaint == null)
      borderPaint = new Paint[theme.getFeatureCount()];
    borderPaint[theme.getIndex(f)] = p;
    return true;
  }


  /**
   * Sets the FillPaint attribute of the ColorModel object
   *
   * @param f  The new FillPaint value
   * @param p  The new FillPaint value
   * @return   Description of the Returned Value
   */
  public  boolean setFillPaint(Feature f, Paint p) {
    if (p == null || f == null) return false;
    if (fillPaint == null)
      fillPaint = new Paint[theme.getFeatureCount()];
    fillPaint[theme.getIndex(f)] = p;
    return true;
  }

  /**
   * Sets the SelectedPaint attribute of the ColorModel object
   *
   * @param paint  The new SelectedPaint value
   */
  public void setSelectedPaint(Paint paint) {
    selectedPaint = paint;
  }


  /**
   * Sets the SelectedStroke attribute of the ColorModel object
   *
   * @param stroke  The new SelectedStroke value
   */
  public void setSelectedStroke(Stroke stroke) {
    selectedStroke = stroke;
  }

  /** Getter for property defaultFillPaint.
   * @return Value of property defaultFillPaint.
   *
   */
  public java.awt.Paint getDefaultFillPaint() {
    return defaultFillPaint;
  }  

  /** Setter for property defaultFillPaint.
   * @param defaultFillPaint New value of property defaultFillPaint.
   *
   */
  public void setDefaultFillPaint(java.awt.Paint defaultFillPaint) {
    this.defaultFillPaint = defaultFillPaint;
     theme.notifyChanges();
  }  

  /** Getter for property transparency.
   * @return Value of property transparency.
   *
   */
  public float getTransparency() {
    return transparency;
  }  

  /** Setter for property transparency.
   * @param transparency New value of property transparency.
   *
   */
  public void setTransparency(float transparency) {
    if (transparency < 0f || transparency > 1f)
      throw new IllegalArgumentException("Transparency must be between 0.0 and 1.0");
    this.transparency = transparency;
    theme.notifyChanges();
  }
  
}