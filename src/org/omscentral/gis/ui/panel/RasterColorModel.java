package org.omscentral.gis.ui.panel;

import org.omscentral.gis.model.RasterTheme;
import org.omscentral.gis.model.RasterModel;
import org.omscentral.gis.model.VAT;
import java.awt.Color;
import java.awt.Image;
import org.omscentral.gis.ui.ColorBlend;

public abstract class RasterColorModel {
  protected ColorBlend blend = null;
  protected Color lowColor = Color.black;
  protected Color highColor = Color.white;
  protected float transparency = 1f;
  protected RasterLayer layer;
  protected RasterTheme theme;

  public abstract Image createImage(RasterModel fprastermodel);

  public ColorBlend getColorBlend() {
    return blend;
  }

  public void setColorBlend(ColorBlend b) {
    blend = b;
    notifyChange();
  }

  public void setLowColor(Color low) {
    lowColor = low;
    blend = createColorBlend(layer,lowColor,highColor);
    notifyChange();
  }

  public Color getLowColor() {
    return lowColor;
  }

  public Color getHighColor() {
    return highColor;
  }

  public void setHighColor(Color high) {
    highColor = high;
    blend = createColorBlend(layer,lowColor,highColor);
    notifyChange();
  }

  protected void notifyChange() {
    layer.colorModelChanged();
  }

  protected void update() {
    blend = createColorBlend(layer,lowColor,highColor);
  }

  public static RasterColorModel createDefault(RasterLayer layer,RasterTheme theme) {
    return new DefaultRasterColorModel(
      layer,
      theme
    );
  }

  public static ColorBlend createColorBlend(RasterLayer layer,Color low,Color high) {
    if (layer == null)
      return null;
    double d1 = layer.getMinimumValue().doubleValue();
    double d2 = layer.getMaximumValue().doubleValue();
    ColorBlend s = new ColorBlend(
      low,
      high,
      false,d1,d2
    );
    return s;
  }

  public static ColorBlend createDefaultColorBlend(RasterLayer layer) {
    return createColorBlend(layer,Color.black,Color.white);
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
    notifyChange();
  }
  
}

class DefaultRasterColorModel extends RasterColorModel {
  String dataItem = VAT.VALUES;

  public DefaultRasterColorModel(RasterLayer l,RasterTheme m) {
    layer = l;
    theme = m;
    blend = createDefaultColorBlend(layer);
  }

  /**
   * Description of the Method
   *
   * @param fprastermodel  Description of Parameter
   * @return               Description of the Returned Value
   */
  public Image createImage(RasterModel fprastermodel) {
    double d = fprastermodel.getMissingValue().doubleValue();
    double d1 = layer.getMinimumValue().doubleValue();
    double d2 = layer.getMaximumValue().doubleValue();
    if (blend == null)
      blend = createColorBlend(layer,lowColor,highColor);
    java.awt.Dimension dimension = fprastermodel.getRasterSize();
    int ai[] = new int[dimension.width * dimension.height];
    int i = 0;
    double d3;
    for (int j = 0; j < dimension.height; j++) {
      for (int k = 0; k < dimension.width; k++) {
        d3 = layer.getValueAt(k,j).doubleValue();
        ai[i++] =
            d3 != d ? blend.getARGB(Math.max(Math.min(d3, d2), d1)) : 0xffffff;
      }
    }

    return java.awt.Toolkit.getDefaultToolkit().createImage(
      new java.awt.image.MemoryImageSource(
        dimension.width, dimension.height, ai, 0, dimension.width)
    );
  }
}
