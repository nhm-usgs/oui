package org.omscentral.gis.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

import java.util.ArrayList;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 8, 2001
 */
public abstract class BasicTheme implements EnumerableTheme {

  /**
   * An arbitrary name for this theme.
   */
  private String themeName;
  private ArrayList<ThemeObserver> observers = new ArrayList<ThemeObserver>(3);


  /**
   * Constructor for the BasicTheme object
   *
   * @param themeName  Description of Parameter
   */
  public BasicTheme(String themeName) {
    this.themeName = themeName;
  }


  /**
   * Constructs a new theme. Sets the theme name to null by calling this(null).
   */
  public BasicTheme() {
    this(null);
  }


  /**
   * Sets the name of the theme.
   *
   * @param themeName  the new name
   * @see              #themeName
   */
  public void setName(String themeName) {
    this.themeName = themeName;
  }


  /**
   * Returns the name of the theme.
   *
   * @return   the name
   * @see      #themeName
   */
  public String getName() {
    return themeName;
  }


  /**
   * Returns the bounding box for this theme. <B>Left abstract.</B>
   *
   * @return   the bounding box
   */
  public abstract Rectangle2D getBounds();


  /**
   * Returns the first feature at the given world coordinate. <B>Left abstract.
   * </B>
   *
   * @param x  the x part of the world coordinate
   * @param y  the y part of the world coordinate
   * @return   the feature
   */
  public abstract Feature getFeatureAt(double x, double y);


  /**
   * Returns the first feature at the given world coordinate. This is done by a
   * call to getFeatureAt(double,double)
   *
   * @param world  the world coordinate
   * @return       the feature
   * @see          #getFeatureAt(double, double)
   */
  public final Feature getFeatureAt(Point2D world) {
    return getFeatureAt(world.getX(), world.getY());
  }


  /**
   * Returns the number of attributes available for each feature. <B>Left
   * abstract.</B>
   *
   * @return   the number of attributes
   */
  public abstract int getAttributeCount();


  /**
   * Returns the names of the attributes. <B>Left abstract.</B>
   *
   * @return   the names of all attributes
   */
  public abstract String[] getAttributeNames();


  /**
   * Returns the name of the i-th attribute. This is done by relying on
   * getAttributeNames(). If index is less than zero or not less than
   * getAttributeCount() null is returned.
   *
   * @param index  the index of the attribute
   * @return       the name of the attribute
   * @see          #getAttributeNames()
   * @see          #getAttributeCount()
   */
  public String getAttributeName(int index) {
    return
        ((0 <= index) && (index < getAttributeCount()))
         ? getAttributeNames()[index]
         : null;
  }


  /**
   * Returns all attributes of a feature. <B>Left abstract.</B>
   *
   * @param feature  the feature in question
   * @return         the attribute array
   */
  public abstract Object[] getAttributes(Feature feature);


  /**
   * Returns an attribute of a feature. This is done by relying on
   * getAttributes(Feature). If index is less than zero or not less than
   * getAttributeCount() null is returned.
   *
   * @param feature  the feature in question
   * @param index    the index of the attribute
   * @return         the attribute
   * @see            #getAttributes(gpsr.gis.model.Feature)
   * @see            #getAttributeCount()
   */
  public Object getAttribute(Feature feature, int index) {
    return
        ((0 <= index) && (index < getAttributeCount()))
         ? getAttributes(feature)[index]
         : null;
  }


  /**
   * Returns the attributes of the first feature at the given world coordinate.
   * This is done by locating the feature via getFeatureAt(double,double) and a
   * call to getAttributes(Feature).
   *
   * @param x  the x part of the world coordinate
   * @param y  the y part of the world coordinate
   * @return   the attribute array
   * @see      #getFeatureAt(double, double)
   * @see      #getAttributes(gpsr.gis.model.Feature)
   */
  public Object[] getAttributesAt(double x, double y) {
    return getAttributes(getFeatureAt(x, y));
  }


  /**
   * Returns an attribute of the first feature at the given world coordinate.
   * This is done by relying on getAttributesAt(double,double). If index is less
   * than zero or not less than getAttributeCount() null is returned.
   *
   * @param x      the x part of the world coordinate
   * @param y      the y part of the world coordinate
   * @param index  the attribute index
   * @return       the attribute
   * @see          #getAttributesAt(double, double)
   */
  public Object getAttributeAt(double x, double y, int index) {
    return
        ((0 <= index) && (index < getAttributeCount()))
         ? getAttributesAt(x, y)[index]
         : null;
  }


  /**
   * Returns all attributes of the first feature at the given world coordinate.
   * This is done by a call to getAttributesAt(double,double)
   *
   * @param world  the world coordinate
   * @return       the attribute array
   * @see          #getAttributesAt(double, double)
   */
  public final Object[] getAttributesAt(Point2D world) {
    return getAttributesAt(world.getX(), world.getY());
  }


  /**
   * Returns an attribute of the first feature at the given world coordinate.
   * This is done by a call to getAttributeAt(double,double,int)
   *
   * @param world  the world coordinate
   * @param index  the attribute index
   * @return       the attribute
   * @see          #getAttributeAt(double, double, int)
   */
  public final Object getAttributeAt(Point2D world, int index) {
    return getAttributeAt(world.getX(), world.getY(), index);
  }



  /**
   * Returns the number of features in this theme. <B>Left abstract.</B>
   *
   * @return   the number of features
   */
  public abstract int getFeatureCount();


  /**
   * Returns the feature at a certain index. <B>Left abstract.</B>
   *
   * @param index  the feature index
   * @return       the feature object
   */
  public abstract Feature getFeature(int index);


  /**
   * Returns the index of a certain feature. <B>Left abstract.</B>
   *
   * @param feature  the feature
   * @return         the feature index
   */
  public abstract int getIndex(Feature feature);


  /**
   * Returns all attributes of a feature at the specified feature index. This is
   * done by relying on getFeature(int) and getAttributes(Feature)
   *
   * @param index  the index of the feature
   * @return       the attributes
   * @see          #getFeature(int)
   * @see          Theme.Plain#getAttributes(gpsr.gis.model.Feature)
   */
  public Object[] getAttributes(int index) {
    return this.getAttributes(getFeature(index));
  }


  /**
   * Returns a certain attribute of a feature. This is done by relying on
   * getAttributes(int). If index is less than zero or not less than
   * getAttributeCount() null is returned.
   *
   * @param featureIndex    the index of the feature
   * @param attributeIndex  the index of the attribute
   * @return                the attribute
   * @see                   #getAttributes(int)
   * @see                   Theme.Plain#getAttributeCount()
   */
  public Object getAttribute(int featureIndex, int attributeIndex) {
    return
        ((0 <= attributeIndex) && (attributeIndex < getAttributeCount()))
         ? getAttributes(featureIndex)[attributeIndex]
         : null;
  }


  /**
   * Description of the Method
   */
  public void notifyChanges() {
    for (int i = 0; i < observers.size(); i++) {
      ((ThemeObserver) observers.get(i)).themeUpdated(this);
    }
  }


  /**
   * Description of the Method
   *
   * @param obs  Description of Parameter
   */
  public void removeObserver(ThemeObserver obs) {
    observers.remove(obs);
  }


  /**
   * Adds a feature to the Observer attribute of the BasicTheme object
   *
   * @param obs  The feature to be added to the Observer attribute
   */
  public void addObserver(ThemeObserver obs) {
    observers.add(obs);
  }


  /**
   * Checks if there are attributes available for the features. This is done by
   * a call to getAttributeCount().
   *
   * @return   true iff attribute information is available, that is
   *      getAttributeCount() returns a value greater than 0
   * @see      #getAttributeCount()
   */
  public boolean attributesAvailable() {
    return (0 < getAttributeCount());
  }


  /**
   * Enumerates all features of this theme. This is done by relying on
   * getFeatureCount() and getFeature(int).
   *
   * @return   the enumeration of the features.
   * @see      #getFeatureCount()
   * @see      #getFeature(int)
   */
  public java.util.Iterator iterator() {
    return
      new java.util.Iterator() {
        int count = 0;
        int size = getFeatureCount();


        /**
         * Description of the Method
         *
         * @return   Description of the Returned Value
         */
        public final boolean hasNext() {
          return (count < size);
        }


        /**
         * Description of the Method
         *
         * @return   Description of the Returned Value
         */
        public final Object next() {
          return getFeature(count++);
        }


        /**
         * Description of the Method
         */
        public final void remove() {
          throw new java.lang.UnsupportedOperationException();
        }
      };
  }

}

