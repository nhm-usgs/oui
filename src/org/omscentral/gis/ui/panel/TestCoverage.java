package org.omscentral.gis.ui.panel;

import org.omscentral.gis.model.*;
import java.awt.*;
import java.awt.geom.*;

public class TestCoverage extends VectorModel implements AttributeModel{
  static Feature[] f;
  static Rectangle2D bounds = new Rectangle2D.Double(-10000,10000,10000,10000);

  static {
      double[] points =
        {-10000D,10000D,0,10000D,0,20000D,-10000D,20000D};
      int[] parts = {0};
      f = new Feature[1];
      f[0] = new VectorModel.MultiPolygonFeature(bounds.getBounds2D(),4,points,parts);
  }

  public TestCoverage() {
  }


  public int getAttributeCount() { return 0; }


  public String[] getAttributeNames() { return new String[0];}


  public String getAttributeName(int column) { return null; }


  /**
   * Gets the RecordCount attribute of the AttributeModel object
   *
   * @return   The RecordCount value
   */
  public int getRecordCount() { return 0; }


  /**
   * Gets the Attributes attribute of the AttributeModel object
   *
   * @param recordIndex  Description of Parameter
   * @return             The Attributes value
   */
  public Object[] getAttributes(int recordIndex) { return new Object[0]; }


  public  Object clone() {
    return null;
  }
  /**
   * Gets the Attribute attribute of the AttributeModel object
   *
   * @param recordIndex     Description of Parameter
   * @param attributeIndex  Description of Parameter
   * @return                The Attribute value
   */
  public Object getAttribute(int recordIndex, int attributeIndex) { return null;}




    public Rectangle2D getBounds() {
      return bounds.getBounds2D();
    }


    public int getFeatureCount() {
      return 1;
    }

    public Feature getFeature(int index) {
      return f[0];
    }


    public Feature[] getFeatures() {
      return f;
    }

}
