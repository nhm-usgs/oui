package org.omscentral.gis.ui.panel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics2D;

import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.omscentral.gis.model.*;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 11, 2001
 */
public abstract class RasterLayer extends Layer {

  /**
   * Description of the Field
   */
  protected RasterColorModel color;
  protected boolean dirty = true;
  protected String dataItem = VAT.VALUES;


  /**
   * Constructor for the RasterLayer object
   *
   * @param theme      Description of Parameter
   * @param transform  Description of Parameter
   */
  protected RasterLayer(RasterTheme theme, AffineTransform transform) {
    super(theme, transform);
    color = RasterColorModel.createDefault(this, theme);
  }

  protected RasterLayer(RasterTheme theme, AffineTransform transform,RasterColorModel colorModel) {
    super(theme, transform);
    color = colorModel;
  }

  public Number getValueAt(int x,int y) {
    RasterTheme theme = (RasterTheme) super.theme;
    RasterModel model = theme.getRasterModel();
    if (dataItem == null || dataItem.equals(VAT.VALUES)) {
      return (Number) model.getRasterDataAt(x, y);
    } else {
      VAT vat = theme.getVAT();
      Number val = (Number) model.getRasterDataAt(x, y);
      Number missing = model.getMissingValue();
      if (val.doubleValue() == missing.doubleValue()) return val;
      return vat.lookup(dataItem,val.doubleValue());
    }
  }

  public Number getMinimumValue() {
    RasterTheme theme = (RasterTheme) super.theme;
    RasterModel model = theme.getRasterModel();
    if (dataItem == null || dataItem.equals(VAT.VALUES))
      return model.getMinimum();
    else {
      VAT vat = theme.getVAT();
      return vat.getMin(dataItem);
    }
  }

  public Number getMaximumValue() {
    RasterTheme theme = (RasterTheme) super.theme;
    RasterModel model = theme.getRasterModel();
    if (dataItem == null || dataItem.equals(VAT.VALUES))
      return model.getMaximum();
    else {
      VAT vat = theme.getVAT();
      return vat.getMax(dataItem);
    }
  }

  public void setDataItem(String item) {
    RasterTheme theme = (RasterTheme) super.theme;
    VAT vat = theme.getVAT();
    if (!vat.hasColumn(item)) return;
    dataItem = item;
    color.update();
    dirty = true;
    notifyChange();
  }


  /**
   * Sets the ColorModel attribute of the RasterLayer object
   *
   * @param c  The new ColorModel value
   */
  public void setColorModel(RasterColorModel c) {
    color = c;
    dirty = true;
    notifyChange();
  }


  /**
   * Sets the Selection attribute of the RasterLayer object
   *
   * @param feature    The new Selection value
   * @param selection  The new Selection value
   */
  public void setSelection(Feature feature, boolean selection) {
  }


  /**
   * Sets the Selection attribute of the RasterLayer object
   *
   * @param selection  The new Selection value
   */
  public void setSelection(boolean selection) {
  }


  /**
   * Gets the ColorModel attribute of the RasterLayer object
   *
   * @return   The ColorModel value
   */
  public RasterColorModel getColorModel() {
    return color;
  }
  
  public Object getColoringModel() {
    return color;
  }


  /**
   * Gets the VirtualBounds attribute of the RasterLayer object
   *
   * @param feature  Description of Parameter
   * @return         The VirtualBounds value
   */
  public Rectangle getVirtualBounds(Feature feature) {
    return new Rectangle(0, 0, 0, 0);
  }


  /**
   * Gets the Selected attribute of the RasterLayer object
   *
   * @param feature  Description of Parameter
   * @return         The Selected value
   */
  public boolean isSelected(Feature feature) {
    return false;
  }


  /**
   * Description of the Method
   *
   * @param P      Description of Parameter
   * @param fuzzy  Description of Parameter
   * @return       Description of the Returned Value
   */
  public Feature locateFeature(Point P, int fuzzy) {
    return ((RasterTheme) theme).getFeatureAt(P);
  }



  /**
   * Description of the Method
   *
   * @param B          Description of Parameter
   * @param contained  Description of Parameter
   * @return           Description of the Returned Value
   */
  public java.util.List locateFeatures(Rectangle2D B, boolean contained) {
    return new java.util.LinkedList();
  }


  /**
   * Description of the Method
   */
  protected void colorModelChanged() {
    dirty = true;
    notifyChange();
  }


}

