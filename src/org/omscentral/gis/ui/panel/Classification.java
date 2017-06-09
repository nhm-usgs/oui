package org.omscentral.gis.ui.panel;

import java.awt.Color;
import java.awt.Paint;

import org.omscentral.gis.util.GISUtililites;

import org.omscentral.gis.ui.ColorBlend;
import org.omscentral.gis.ui.panel.AbstractVectorThemeColorModel;

import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.model.Feature;


public abstract class Classification {
  int featureIdx;
  Color low = null;
  Color high = null;

  public Classification(int featureIdx) {
    this.featureIdx = featureIdx;
  }

  public abstract void classify(AbstractVectorThemeColorModel model,VectorTheme t)
    throws ClassificationException;

  public ColorBlend createBlend(VectorTheme theme)
    throws ClassificationException {
    Feature[] feat = theme.getFeatureArray();
    Object[] vals = new Object[theme.getFeatureCount()];
    for (int i = 0; i < feat.length; i++) {
      vals[i] = theme.getAttribute(feat[i],featureIdx);
    }
    if (! (vals[0] instanceof Comparable))
      throw new ClassificationException("Not comparable values");
    java.util.Arrays.sort(vals);
    double min,max;
    try {
      min = Double.parseDouble(vals[0].toString());
      max = Double.parseDouble(vals[vals.length-1].toString());
    } catch (Exception e) {
      throw new ClassificationException("Not number values");
    }
    return new ColorBlend(low,high,true,min,max);
  }

  public static Classification createUnique(Color l,Color h,int index) {
    Classification c = new UniqueClasses(index);
    c.low = l;
    c.high = h;
    return c;
  }

  public static Classification createRange(
    Color l,Color h,int index, int num) {
    Classification c = new RangeClasses(index,num);
    c.low = l;
    c.high = h;
    return c;
  }

}

class UniqueClasses extends Classification {
  public UniqueClasses(int i) {
    super(i);
  }
  public void classify(AbstractVectorThemeColorModel model,VectorTheme theme)
    throws ClassificationException {
    Feature[] feat = theme.getFeatureArray();
    Paint paint;
    double val;
    ColorBlend blend = createBlend(theme);
    for (int i = 0; i < feat.length; i++) {
      val = Double.parseDouble(theme.getAttribute(feat[i],featureIdx).toString());
      paint = new Color(blend.getARGB(val));
      model.setFillPaint(feat[i],paint);
    }
  }
}

class RangeClasses extends Classification {
  int numClasses;
  public RangeClasses(int idx,int numClasses) {
    super(idx);
    this.numClasses = numClasses;
  }

  public void classify(AbstractVectorThemeColorModel model,VectorTheme theme)
    throws ClassificationException  {
    Object minVal = null,maxVal = null;
    Object o[] = null;
    try {
      o = GISUtililites.getMinAndMax(theme,featureIdx);
    } catch (Exception e) {throw new ClassificationException(
      e.getLocalizedMessage()
      );
    }
    double min;
    double max;
    try {
      min = Double.parseDouble(o[0].toString());
      max = Double.parseDouble(o[1].toString());
    } catch (Exception e) {
      throw new ClassificationException(
      "Could not classify, attributes are not numbers"
    );}
    double classWidth = Math.ceil((max + 1 - min) / numClasses);
    Feature[] feat = theme.getFeatureArray();
    Paint paint;
    double val;
    ColorBlend blend = createBlend(theme);
    for (int i = 0; i < feat.length; i++) {
      val = Double.parseDouble(theme.getAttribute(feat[i],featureIdx).toString());
      int classIdx = (int)Math.floor((val - min)/classWidth);
      paint = new Color(blend.getARGB(min + classIdx*classWidth));
      model.setFillPaint(feat[i],paint);
    }
  }
}
