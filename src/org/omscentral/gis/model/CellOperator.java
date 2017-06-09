package org.omscentral.gis.model;

public abstract class CellOperator {
  protected RasterTheme theme;
  public double[][] data;
  protected double missing;
  protected double cellsize;

  public boolean ignoresNoData() {
    return true;
  }

  public void setTheme(RasterTheme t) {
    theme = t;
    FPRasterModel model = (FPRasterModel)t.getRasterModel();
    data = model.getData();
    missing = model.getMissingValue().doubleValue();
    cellsize = model.getCellSize().doubleValue();
  }

  public double[][] getData() {
    return data;
  }

  public String getName() {
    return "";
  }

  public boolean createsNewTheme() {
    return true;
  }

  public abstract double getValue(int x,int y);

}
