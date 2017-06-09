package org.omscentral.gis.ui.panel;

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Color;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import org.omscentral.gis.ui.ColoredShape;

import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.model.Feature;


public abstract class AbstractVectorThemeColorModel {
  VectorTheme theme;
  Classification classification;

  public AbstractVectorThemeColorModel(VectorTheme t) {
    theme = t;
  }

  public void setClassification(Classification newClass) throws ClassificationException {
    classification = newClass;
    classification.classify(this,theme);
    theme.notifyChanges();
  }

  /**
   * Gets the Stroke attribute of the ColorModel object
   *
   * @param feature  Description of Parameter
   * @return         The Stroke value
   */
  public abstract Stroke getStroke(Feature feature);

  /**
   * Gets the Paint attribute of the ColorModel object
   *
   * @param feature  Description of Parameter
   * @return         The Paint value
   */
  public abstract Paint getFillPaint(Feature feature);


  /**
   * Gets the BorderPaint attribute of the ColorModel object
   *
   * @param feature  Description of Parameter
   * @return         The BorderPaint value
   */
  public abstract Paint getBorderPaint(Feature feature);


  /**
   * Gets the SelectedPaint attribute of the ColorModel object
   *
   * @return   The SelectedPaint value
   */
  public abstract Paint getSelectedPaint();


  /**
   * Gets the OutlinedPaint attribute of the ColorModel object
   *
   * @return   The OutlinedPaint value
   */
  public abstract  Paint getOutlinedPaint();


  public abstract  Stroke getOutlinedStroke();


  /**
   * Gets the SelectedStroke attribute of the ColorModel object
   *
   * @return   The SelectedStroke value
   */
  public abstract Stroke getSelectedStroke();


  /**
   * Sets the Stroke attribute of the ColorModel object
   *
   * @param stroke   The new Stroke value
   * @param feature  The new Stroke value
   * @return         Description of the Returned Value
   */
  public abstract boolean setStroke(Stroke stroke, Feature feature);



  /**
   * Sets the Paint attribute of the ColorModel object
   *
   * @param paint  The new Paint value
   * @param idx    The new Paint value
   * @return       Description of the Returned Value
   */
  public abstract boolean setFillPaint(Paint paint, int idx);

  /**
   * Sets the FillPaint attribute of the ColorModel object
   *
   * @param f  The new FillPaint value
   * @param p  The new FillPaint value
   * @return   Description of the Returned Value
   */
  public abstract boolean setFillPaint(Feature f, Paint p);

  /**
   * Sets the BorderPaint attribute of the ColorModel object
   *
   * @param paint  The new BorderPaint value
   * @param idx    The new BorderPaint value
   * @return       Description of the Returned Value
   */
  public abstract boolean setBorderPaint(Paint paint, int idx);


  /**
   * Sets the BorderPaint attribute of the ColorModel object
   *
   * @param f  The new BorderPaint value
   * @param p  The new BorderPaint value
   * @return   Description of the Returned Value
   */
  public abstract boolean setBorderPaint(Feature f, Paint p);

  /**
   * Sets the SelectedPaint attribute of the ColorModel object
   *
   * @param paint  The new SelectedPaint value
   */
  public abstract void setSelectedPaint(Paint paint);


  /**
   * Sets the SelectedStroke attribute of the ColorModel object
   *
   * @param stroke  The new SelectedStroke value
   */
  public abstract void setSelectedStroke(Stroke stroke);
  
  public abstract float getTransparency();

  /** Setter for property transparency.
   * @param transparency New value of property transparency.
   *
   */
  public abstract void setTransparency(float transparency);
  
  public abstract java.awt.Paint getDefaultFillPaint();

  /** Setter for property defaultFillPaint.
   * @param defaultFillPaint New value of property defaultFillPaint.
   *
   */
  public abstract void setDefaultFillPaint(java.awt.Paint defaultFillPaint);


  public static AbstractVectorThemeColorModel createDefault(VectorTheme theme) {
    return new VectorThemeColorModel(theme);
  }

}

