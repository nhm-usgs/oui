package org.omscentral.gis.model;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.TreeSet;

public class DefaultFPRasterModel implements FPRasterModel {

  /**
   * Description of the Field
   */
  protected double body[][];

  /**
   * Description of the Field
   */
  protected Rectangle2D bounds;

  /**
   * Description of the Field
   */
  protected double minimum = Double.POSITIVE_INFINITY;
  /**
   * Description of the Field
   */
  protected double maximum = Double.NEGATIVE_INFINITY;

  protected double xll;
  protected double yll;
  protected double cellsize;

  /**
   * Description of the Field
   */
  protected double missing;

  protected int nbOfCols;

  protected int nbOfRows;

  protected RasterTheme theme;

  public DefaultFPRasterModel(
    int cols,int rows,double xll,double yll,double cellsize,double missing) {

    nbOfRows = rows;
    nbOfCols = cols;
    this.xll = xll;
    this.yll = yll;
    this.cellsize = cellsize;
    this.missing = missing;
    bounds = new Rectangle2D.Double(
      xll,
      yll,
      nbOfCols * cellsize,
      nbOfRows * cellsize
    );
  }

  protected void setTheme(RasterTheme theme) {
    this.theme = theme;
  }


  public void resetToNoData() {
    for (int i = 0; i < body.length; i++)
		{
      java.util.Arrays.fill(body[i],missing);
    }
    minimum = missing;
    maximum = missing;
  }

  public Object clone() {
    DefaultFPRasterModel model =
    new DefaultFPRasterModel(
      nbOfCols,nbOfRows,
      getBounds().getX(),getBounds().getY(),
      getCellSize().doubleValue(),getMissingValue().doubleValue());

    double[][] data = new double[nbOfCols][nbOfRows];
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[i].length; j++) {
        data[i][j] = getDoubleDataAt(i,j);
      }
    }
    model.setData(data);
    return model;
  }

  public FPRasterModel getClone() {
    return (FPRasterModel) clone();
  }


  public double[][] getData() {
    return body;
  }

  public void setData(double[][] d) {
    minimum = Double.MAX_VALUE;
    maximum = Double.MIN_VALUE;
    for (int i = 0; i < d.length; i++) {
      for (int j = 0; j < d[i].length; j++) {
        if (d[i][j] != missing) {
          if (d[i][j] < minimum)
            minimum = d[i][j];
          if (d[i][j] > maximum)
            maximum = d[i][j];
        }
      }
    }
    body = d;
    if (theme != null)
      theme.modelChanged();
  }

  public void setMin(double d) {
    this.minimum = d;
  }

  public void setMax(double d) {
    this.maximum = d;
  }

  /**
   * Gets the RasterSize attribute of the ArcGridReader object
   *
   * @return   The RasterSize value
   */
  public Dimension getRasterSize() {
    return new Dimension(nbOfCols, nbOfRows);
  }


  /**
   * Gets the Bounds attribute of the ArcGridReader object
   *
   * @return   The Bounds value
   */
  public final Rectangle2D getBounds() {
    return bounds.getBounds2D();
  }


  /**
   * Gets the RasterDataAt attribute of the ArcGridReader object
   *
   * @param column  Description of Parameter
   * @param row     Description of Parameter
   * @return        The RasterDataAt value
   */
  public final Object getRasterDataAt(int column, int row) {
    return new Double(body[column][row]);
  }



  /**
   * Gets the DoubleDataAt attribute of the ArcGridReader object
   *
   * @param column  Description of Parameter
   * @param row     Description of Parameter
   * @return        The DoubleDataAt value
   */
  public final double getDoubleDataAt(int column, int row) {
    return body[column][row];
  }

  /**
   * Gets the Minimum attribute of the ArcGridReader object
   *
   * @return   The Minimum value
   */
  public final Number getMinimum() {
    return new Double(minimum);
  }


  /**
   * Gets the Maximum attribute of the ArcGridReader object
   *
   * @return   The Maximum value
   */
  public final Number getMaximum() {
    return new Double(maximum);
  }


  /**
   * Gets the MissingValue attribute of the ArcGridReader object
   *
   * @return   The MissingValue value
   */
  public final Number getMissingValue() {
    return new Double(missing);
  }

  public final Number getCellSize() {
    return new Double(cellsize);
  }

  public String toString() {
    return "MIN : " + getMinimum() + "\n" +
           "MAX : " + getMaximum() + "\n" +
           "MISSING : "+ getMissingValue() + "\n" +
           "SIZE : " + getBounds().getWidth() + " X " + getBounds().getHeight() + "\n" +
           "NW CORNER : " + getBounds().getX() + " , " + getBounds().getY();
  }



}
