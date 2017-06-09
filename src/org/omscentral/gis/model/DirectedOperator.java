package org.omscentral.gis.model;

public abstract class DirectedOperator {
  protected RasterTheme theme;
  protected double[][] data;
  protected double missing;
  protected double cellsize;
  protected int[][] seedPts = new int[][]{{0,0}};
  protected int currentSeed = 0;

  public void setTheme(RasterTheme t) {
    theme = t;
    FPRasterModel model = (FPRasterModel)t.getRasterModel();
    data = model.getData();
    missing = model.getMissingValue().doubleValue();
    cellsize = model.getCellSize().doubleValue();
  }

  public String getName() {
    return "";
  }

  public int[][] getSeedPoints() {
    return seedPts;
  }

  public int getCurrentSeed() {
    return currentSeed;
  }

  public void setCurrentSeed(int i) {
    currentSeed = i;
  }

  public void setSeedPoints(int[][] p) {
    seedPts = p;
  }

  public boolean ignoresNoData() {
    return true;
  }

  public boolean needsClearModel() {
    return true;
  }

  public boolean createsNewTheme() {
    return true;
  }

  public abstract double getValue(int x,int y,double cell,int[] nextCoords);


}
