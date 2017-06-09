package org.omscentral.gis.model;

import java.awt.geom.Rectangle2D;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class VectorTheme extends BasicTheme {

  /**
   * Description of the Field
   */
  //protected Rectangle2D bounds;
  /**
   * Description of the Field
   */
  //protected Feature[] features;

  /**
   * Description of the Field
   */
  protected AttributeModel attributeModel;

  protected VectorModel vectorModel;

  /**
   * Constructor for the VectorTheme object
   *
   * @param vectorModel     Description of Parameter
   * @param attributeModel  Description of Parameter
   */
  public VectorTheme(VectorModel vectorModel, AttributeModel attributeModel) {
    super();

    this.vectorModel = vectorModel;
    this.attributeModel = attributeModel;
  }

  public AttributeModel getAttributeModel() {
    return attributeModel;
  }

  public VectorModel getVectorModel() {
    return vectorModel;
  }


  /**
   * get the bounding box for this theme
   *
   * @return   the bounding box
   */
  public Rectangle2D getBounds() {
    return vectorModel.getBounds();
  }



  /**
   * Get the number of features in this theme.
   *
   * @return   the number of features as int
   */
  public int getFeatureCount() {
    return vectorModel.getFeatures().length;
  }


  /**
   * Get a single feature.
   *
   * @param index  the feature index
   * @return       the feature object
   */
  public Feature getFeature(int index) {
    Feature[] features = vectorModel.getFeatures();
    return features[index];
  }


  /**
   * Returns the index of a given feature.
   *
   * @param feature  the feature whose index is to be located
   * @return         the index of the given feature or -1 if not found
   */
  public synchronized int getIndex(Feature feature) {
    Feature[] features = vectorModel.getFeatures();
    for (int i = 0; i < features.length; i++) {
      if (features[i] == feature) {
        return i;
      }
    }
    return -1;
  }


  /**
   * NOT YET IMPLEMENTED
   *
   * @param x  Description of Parameter
   * @param y  Description of Parameter
   * @return   The FeatureAt value
   */
  public Feature getFeatureAt(double x, double y) {
    return null;
  }


  /**
   * gets the number of attributes which are available for each feature
   *
   * @return   the number of attributes
   */
  public int getAttributeCount() {
    return (attributeModel != null) ? attributeModel.getAttributeCount() : 0;
  }


  /**
   * gets the names of the fields of the attributes
   *
   * @return   the names as string array
   */
  public String[] getAttributeNames() {
    return
        (attributeModel != null)
         ? attributeModel.getAttributeNames()
         : new String[0];
  }


  /**
   * gets the names of the field at the column i of the attributes
   *
   * @param index  Description of Parameter
   * @return       the names as string, null if there is no attribute in this
   *      colum
   */
  public String getAttributeName(int index) {
    return
        (attributeModel != null)
         ? attributeModel.getAttributeName(index)
         : null;
  }


  /**
   * Gets the Attributes attribute of the VectorTheme object
   *
   * @param feature  Description of Parameter
   * @return         The Attributes value
   */
  public Object[] getAttributes(Feature feature) {
    return getAttributes(getIndex(feature));
  }


  public Object getAttribute(Feature feature,String name) {
    Object[] o = getAttributes(feature);
    String[] n = getAttributeNames();
    int idx = 0;
    for (int i = 0; i < n.length; i++) {
      if (n[i].equals(name)) {
        idx = i;
        break;
      }
    }
    return o[idx];
  }


  /**
   * Gets the Attributes attribute of the VectorTheme object
   *
   * @param index  Description of Parameter
   * @return       The Attributes value
   */
  public Object[] getAttributes(int index) {
    if ((0 <= index) && (attributeModel != null)) {
      return attributeModel.getAttributes(index);
    }
    else {
      Object[] attributes = new Object[getAttributeCount()];
      for (int i = 0; i < attributes.length; i++) {
        attributes[i] = null;
      }
      return attributes;
    }
  }


  /**
   * gets the attribute of a feature at the specified index and the column as an
   * object. The type of the objects could be Double for numerical columns,
   * Boolean for logical and Strings for the rest of it (Character, Date and
   * Memo)
   *
   * @param featureIndex    the index of the feature
   * @param attributeIndex  Description of Parameter
   * @return                the attribute as object
   */
  public Object getAttribute(int featureIndex, int attributeIndex) {
    return
        (attributeModel != null)
         ? attributeModel.getAttribute(featureIndex, attributeIndex)
         : null;
  }


  /**
   * Gets the Attribute attribute of the VectorTheme object
   *
   * @param feature  Description of Parameter
   * @param index    Description of Parameter
   * @return         The Attribute value
   */
  public Object getAttribute(Feature feature, int index) {
    return getAttribute(getIndex(feature), index);
  }


  /**
   * Get all features of this theme as an array.
   *
   * @return   the features as array
   */
  public Feature[] getFeatureArray() {
    return vectorModel.getFeatures();
  }

  public String toString() {
    String type = "";
    if (getVectorModel().isPoint()) type = "Point Cover";
    else if (getVectorModel().isLine()) type = "Line Cover";
    else if (getVectorModel().isPoly()) type = "Poly Cover";
    String retval = "Vector " + type + "\n";
    retval += "Features : " + getFeatureCount();
    return retval;

  }

      public int getIdIndex(String idAttributeName) {
        AttributeModel mod = getAttributeModel();
        int idAttributeIndex = -1;
        for (int i = 0; i < mod.getAttributeCount(); i++) {
            if (mod.getAttributeName(i).equals(idAttributeName)) {
                idAttributeIndex = i;
                break;
            }
        }
        return idAttributeIndex;
    }
}

