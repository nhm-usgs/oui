package org.omscentral.gis.model;



import java.util.Iterator;


/* -------------------------------------------------------------------------
 * Interface definition - EnumerableTheme
 *
 */

/**
 * EnumerableTheme defines the concept that features of a theme might be
 * enumerable. Obvious this is the case if they are internally stored in
 * an array or vector.
 *
 * @author Michael Gr&uuml;bsch
 * @see Feature
 * @see Theme
 */
public interface EnumerableTheme extends Theme
{

  /**
   * Returns the number of features in this theme.
   *
   * @return  the number of features
   */
  public int getFeatureCount();

  /**
   * Returns the feature at a certain index.
   *
   * @param  index the feature index
   * @return the feature object
   */
  public Feature getFeature(int index);

  /**
   * Returns the index of a certain feature.
   *
   * @param feature the feature
   * @return the feature index
   */
  public int getIndex(Feature feature);

  /**
   * Enumerates all features of this theme.
   *
   * @return the enumeration of the features.
   */
  public Iterator iterator();

  /**
   * Returns all attributes of a feature at the specified feature index.
   *
   * @param  index the index of the feature
   * @return the attributes
   */
  public Object[] getAttributes(int index);

  /**
   * Returns a certain attribute of a feature.
   *
   * @param  featureIndex the index of the feature
   * @param  attributeIndex the index of the attribute
   * @return the attribute
   */
  public Object getAttribute(int featureIndex, int attributeIndex);


}


