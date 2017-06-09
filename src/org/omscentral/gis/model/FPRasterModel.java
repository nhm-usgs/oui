package org.omscentral.gis.model;


import java.awt.Dimension;


/**
 * The FPRasterModel specifies raster data which consist of floating point
 * values.
 *
 * @author    Michael Gr&uuml;bsch
 * @created   June 11, 2001
 */
public interface FPRasterModel extends RasterModel {

  /**
   * Gets the DoubleDataAt attribute of the FPRasterModel object
   *
   * @param column  Description of Parameter
   * @param row     Description of Parameter
   * @return        The DoubleDataAt value
   */
  public double getDoubleDataAt(int column, int row);

//  public void setDoubleDataAt(int column, int row, double d);

  public void setData(double[][] data);

  public double[][] getData();

  public void resetToNoData();

  public String toString();

}

