package org.omscentral.gis.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Theme specifies the concept of geometrical data which are provided with a
 * certain set of attributes. <P>
 *
 *
 *
 * @author    Michael Gr&uuml;bsch
 * @created   June 19, 2001
 * @see       EnumerableTheme
 */
public interface Theme {
  /**
   * Sets the name of the theme.
   *
   * @param themeName  the new name
   */
  public void setName(String themeName);


  /**
   * Returns the name of the theme.
   *
   * @return   the name
   */
  public String getName();


  /**
   * Returns the bounding box for this theme. The bounding box is defined to be
   * the smallest rectangle which contains the theme and whose sides are
   * parallel to the coordinate axis.
   *
   * @return   the bounding box
   */
  public Rectangle2D getBounds();


  /**
   * Returns the first feature at the given world coordinate. If no such feature
   * exists null is to be returned.
   *
   * @param x  the x part of the world coordinate
   * @param y  the y part of the world coordinate
   * @return   the feature
   */
  public Feature getFeatureAt(double x, double y);


  /**
   * Returns the first feature at the given world coordinate. If no such feature
   * exists null is to be returned.
   *
   * @param world  the world coordinate
   * @return       the feature
   */
  public Feature getFeatureAt(Point2D world);


  /**
   * Checks if there are attributes available for the features.
   *
   * @return   true iff attribute information is available
   */
  public boolean attributesAvailable();


  /**
   * Returns the number of attributes available for each feature. If no
   * attributes are available 0 is to be returned.
   *
   * @return   the number of available attributes
   */
  public int getAttributeCount();


  /**
   * Returns the names of the attributes. If there are no attributes available
   * an array of length 0 is to be returned.
   *
   * @return   the names of all attributes
   */
  public String[] getAttributeNames();


  /**
   * Returns the name of the i-th attribute.
   *
   * @param index  the index of the attribute
   * @return       the name of the attribute
   */
  public String getAttributeName(int index);


  /**
   * Returns all attributes of a feature. The type of an attribute object is to
   * be Double for floating points, Integer for integers, Boolean for logical
   * values and undefined otherwise. The parameter feature might be null, in
   * this case an object array of length getAttributeCount() should be returned
   * whose elements are null.
   *
   * @param feature  the feature in question
   * @return         the attribute array
   */
  public Object[] getAttributes(Feature feature);


  /**
   * Returns a certain attribute of a feature. The same conventions apply for
   * the returned type and for the case that the feature might be null as in <A
   * HREF="#getAttributes(gpsr.gis.model.Feature)">getAttributes(Feature)</A> .
   *
   * @param feature  the feature in question
   * @param index    the index of the attribute
   * @return         the attribute
   */
  public Object getAttribute(Feature feature, int index);


  /**
   * Returns the attributes of the first feature at the given world coordinate.
   * The same conventions apply for the returned types and for the case that the
   * feature at the given coordinate might be null as in <A
   * HREF="#getAttributes(gpsr.gis.model.Feature)">getAttributes(Feature)</A> .
   *
   * @param x  the x part of the world coordinate
   * @param y  the y part of the world coordinate
   * @return   the attribute array
   */
  public Object[] getAttributesAt(double x, double y);


  /**
   * Returns an attribute of the first feature at the given world coordinate.
   * The same conventions apply for the returned type and for the case that the
   * feature at the given coordinate might be null as in <A
   * HREF="#getAttributes(gpsr.gis.model.Feature)">getAttributes(Feature)</A> .
   *
   * @param x      the x part of the world coordinate
   * @param y      the y part of the world coordinate
   * @param index  the attribute index
   * @return       the attribute
   */
  public Object getAttributeAt(double x, double y, int index);


  /**
   * Returns all attributes of the first feature at the given world coordinate.
   * The same conventions apply for the returned types and for the case that the
   * feature at the given coordinate might be null as in <A
   * HREF="#getAttributes(gpsr.gis.model.Feature)">getAttributes(Feature)</A> .
   *
   * @param world  the world coordinate
   * @return       the attribute array
   */
  public Object[] getAttributesAt(Point2D world);


  /**
   * Returns an attribute of the first feature at the given world coordinate.
   * The same conventions apply for the returned type and for the case that the
   * feature at the given coordinate might be null as in <A
   * HREF="#getAttributes(gpsr.gis.model.Feature)">getAttributes(Feature)</A> .
   *
   * @param world  the world coordinate
   * @param index  the attribute index
   * @return       the attribute
   */
  public Object getAttributeAt(Point2D world, int index);


  /**
   * Description of the Method
   *
   * @return   Description of the Returned Value
   */
  public String toString();


  /**
   * Description of the Method
   */
  public void notifyChanges();


  /**
   * Sets the Observer attribute of the Theme object
   *
   * @param obs  The new Observer value
   */
  public void addObserver(ThemeObserver obs);

  public void removeObserver(ThemeObserver obs);

}

