package org.omscentral.gis.model;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.util.TreeSet;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class FPRasterTheme extends RasterTheme {
  
//  static java.util.ArrayList all = new java.util.ArrayList();
//  public static void all() {
//    for (int i = 0; i < all.size(); i++) {
//      java.lang.ref.SoftReference sr = (java.lang.ref.SoftReference) all.get(i);
//      System.out.println(sr.get());
//    }
//  }

  /**
   * Description of the Field
   */
  protected FPRasterModel model;


  protected VAT vat = null;

  /**
   * Constructor for the FPRasterTheme object
   *
   * @param themeName  Description of Parameter
   * @param model      Description of Parameter
   */
  public FPRasterTheme(String themeName, FPRasterModel model) {
    super
        (themeName,
        new double[][]
        {
        new double[]
        {
        getCellWidth(model),
        0.0
        },
        new double[]
        {
        0.0,
        -getCellHeight(model)
        }
        },
        new double[]
        {
        getXUL(model),
        getYUL(model)
        });

    this.model = model;

    Dimension D = model.getRasterSize();
    setSize(D.width, D.height);
    
    //all.add(new java.lang.ref.SoftReference(this));
  }
  
  protected void finalize() throws Throwable {
    System.out.println("theme : " + getName() + " finalized");
    super.finalize();
  }
  
  


  /**
   * Constructor for the FPRasterTheme object
   *
   * @param model  Description of Parameter
   */
  public FPRasterTheme(FPRasterModel model) {
    this(null, model);
  }


  /**
   * Gets the FeatureAt attribute of the FPRasterTheme object
   *
   * @param x  Description of Parameter
   * @param y  Description of Parameter
   * @return   The FeatureAt value
   */
  public Feature getFeatureAt(int x, int y) {
    return null;
  }



  /**
   * Gets the AttributeAt attribute of the FPRasterTheme object
   *
   * @param x  Description of Parameter
   * @param y  Description of Parameter
   * @return   The AttributeAt value
   */
  public Object getAttributeAt(int x, int y) {
    return null;
  }



  /**
   * Gets the RasterModel attribute of the FPRasterTheme object
   *
   * @return   The RasterModel value
   */
  public RasterModel getRasterModel() {
    return (RasterModel)model;
  }



  /**
   * Gets the XUL attribute of the FPRasterTheme class
   *
   * @param model  Description of Parameter
   * @return       The XUL value
   */
  private final static double getXUL(RasterModel model) {
    return model.getBounds().getMinX();
  }


  /**
   * Gets the YUL attribute of the FPRasterTheme class
   *
   * @param model  Description of Parameter
   * @return       The YUL value
   */
  private final static double getYUL(RasterModel model) {
    return model.getBounds().getMinY() + model.getBounds().getHeight();
  }


  /**
   * Gets the CellWidth attribute of the FPRasterTheme class
   *
   * @param model  Description of Parameter
   * @return       The CellWidth value
   */
  private final static double getCellWidth(RasterModel model) {
    return model.getBounds().getWidth() / model.getRasterSize().width;
  }


  /**
   * Gets the CellHeight attribute of the FPRasterTheme class
   *
   * @param model  Description of Parameter
   * @return       The CellHeight value
   */
  private final static double getCellHeight(RasterModel model) {
    return model.getBounds().getHeight() / model.getRasterSize().height;
  }

  protected void modelChanged() {
    updateVAT();
  }

  public VAT getVAT() {
    if (vat == null)
      createVAT();
    return vat;
  }

  public boolean hasVAT() {
    return vat != null;
  }

  protected void createVAT() {
    TreeSet<Stat> stats = new TreeSet<Stat>();
    double[][] body = model.getData();
    double missing = model.getMissingValue().doubleValue();
    for (int i = 0; i < body.length; i++) {
      for (int j = 0; j < body[i].length; j++) {
        if (body[i][j] == missing) continue;
        Stat stat = new Stat(body[i][j]);
        if (!stats.contains(stat))
          stats.add(stat);
        else {
          java.util.Iterator it = stats.iterator();
          Stat target = null;
          while (it.hasNext()) {
            target = (Stat)it.next();
            if (target.equals(stat))
              break;
          }
          target.increment();
        }
      }
    }
    java.util.Iterator it = stats.iterator();
    double[] vals = new double[stats.size()];
    int[] cnts = new int[stats.size()];
    int idx = 0;
    while (it.hasNext()) {
      Stat n = (Stat)it.next();
      vals[idx] = n.id;
      cnts[idx++] = n.cnt;
    }
    vat = new VAT(vals,cnts,"VAT : " + getName());
  }

  static class Stat implements Comparable {
    double id;
    int cnt=0;

    public Stat(double id) {
      this.id = id;
    }

    public void increment() {
      cnt++;
    }

    public int compareTo(Object o) {
      return (int)(this.id - ((Stat)o).id);
    }

    public boolean equals(Object o) {
      return ((Stat)o).id == this.id;
    }

    public String toString() {
      return "stat " + id + " cnt " + cnt;
    }
  }

  protected void updateVAT() {
    if (vat == null) return;
  }

  public String toString() {
    return "Floating Point, Double Precision Raster Coverage\n" +
           model.toString();
  }

}

