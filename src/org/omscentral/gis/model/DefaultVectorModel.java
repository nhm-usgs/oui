package org.omscentral.gis.model;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.ArrayList;

import org.omscentral.gis.io.ByteOrderInputStream;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorModel;

public class DefaultVectorModel extends VectorModel  {
  private int shpFileCode = -1;
  private int shpFileLength = -1;
  private int shpVersion = -1;
  private int shpType = -1;

  private Rectangle2D bounds;
  private Feature[] features;

  public DefaultVectorModel(Feature[] features,Rectangle2D bounds) {
    this.features=features;
    this.bounds = bounds;
  }

  public void addFeature(Feature feat) {
    Feature[] f = new Feature[features.length + 1];
    System.arraycopy(features,0,f,0,features.length);
    f[features.length+1] = feat;
    features = f;
  }

  /**
   * Gets the Bounds attribute of the ShpFileParser object
   *
   * @return   The Bounds value
   */
  public final Rectangle2D getBounds() {
    return bounds;
  }

  public Object clone() {
    Feature[] copy = new Feature[features.length];
    System.arraycopy(features,0,copy,0,features.length);
    DefaultVectorModel clone = new DefaultVectorModel(copy,bounds.getBounds2D());
    clone.setFileVersion(getFileVersion());
    clone.setShapeFileCode(getShapeFileCode());
    clone.setShapeType(getShapeType());
    return clone;
  }


  /**
   * Gets the FeatureCount attribute of the ShpFileParser object
   *
   * @return   The FeatureCount value
   */
  public final int getFeatureCount() {
    return features.length;
  }


  /**
   * Gets the Feature attribute of the ShpFileParser object
   *
   * @param index  Description of Parameter
   * @return       The Feature value
   */
  public final Feature getFeature(int index) {
    return features[index];
  }


  /**
   * Gets the Features attribute of the ShpFileParser object
   *
   * @return   The Features value
   */
  public final Feature[] getFeatures() {
    return features;
  }

  public int getShapeFileCode() {
    return shpFileCode;
  }

  public void setShapeFileCode(int code) {
    shpFileCode =code;
  }


  /**
   * get the version of the shape file (currently: 1000)
   *
   * @return   the version as int
   */
  public int getFileVersion() {

    return shpVersion;
  }

  public void setFileVersion(int version) {
    shpVersion = version;
  }


  /**
   * get the type of the shape file which is stored in the header.
   *
   * @return   a constant integer which is one of the ????_SHAPE constants
   */
  public int getShapeType() {

    return shpType;
  }

  public void setShapeType(int type) {
    shpType = type;
  }


}
