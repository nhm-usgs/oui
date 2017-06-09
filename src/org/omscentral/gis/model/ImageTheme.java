package org.omscentral.gis.model;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;


/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class ImageTheme extends RasterTheme implements ImageObserver {


  /**
   * Description of the Field
   */
  protected Image image;

  /**
   * Description of the Field
   */
  protected int[] pixels = null;
  /**
   * Description of the Field
   */
  protected boolean failed = false;



  /**
   * Constructor for the ImageTheme object
   *
   * @param themeName  Description of Parameter
   * @param image      Description of Parameter
   * @param matrix     Description of Parameter
   * @param vector     Description of Parameter
   */
  public ImageTheme
      (String themeName, Image image, double[][] matrix, double[] vector) {
    super(themeName, matrix, vector);
    this.image = image;
  }



  /**
   * Gets the Image attribute of the ImageTheme object
   *
   * @return   The Image value
   */
  public final Image getImage() {
    return image;
  }


  /**
   * Gets the Size attribute of the ImageTheme object
   *
   * @return   The Size value
   */
  public Dimension getSize() {
    while ((super.getSize() == null) && (!failed)) {
      synchronized (this) {
        int width = image.getWidth(this);
        int height = image.getHeight(this);

        if ((width < 0) || (height < 0)) {
          try {
            wait();
          }
          catch (InterruptedException E) {
            failed = true;
          }
          ;
        }
        else {
          setSize(width, height);
        }
      }
    }

    return super.getSize();
  }


  /**
   * Gets the FeatureAt attribute of the ImageTheme object
   *
   * @param x  Description of Parameter
   * @param y  Description of Parameter
   * @return   The FeatureAt value
   */
  public Feature getFeatureAt(int x, int y) {
    return null;
  }


  /**
   * Gets the AttributeAt attribute of the ImageTheme object
   *
   * @param x  Description of Parameter
   * @param y  Description of Parameter
   * @return   The AttributeAt value
   */
  public Object getAttributeAt(int x, int y) {
    return null;
  }



  /**
   * Description of the Method
   *
   * @param image   Description of Parameter
   * @param flags   Description of Parameter
   * @param x       Description of Parameter
   * @param y       Description of Parameter
   * @param width   Description of Parameter
   * @param height  Description of Parameter
   * @return        Description of the Returned Value
   */
  public boolean imageUpdate
      (Image image, int flags, int x, int y, int width, int height) {
    if (this.image == image) {
      synchronized (this) {
        if ((flags & (ABORT | ERROR)) != 0) {
          System.err.println
              (((flags & ABORT) != 0)
               ? "Image loading aborted."
               : "Error while loading image.");
          failed = true;
          notify();
        }
        else if ((flags & (HEIGHT | WIDTH)) != 0) {
          notify();
        }

        return ((flags & (ABORT | ERROR | HEIGHT | WIDTH | ALLBITS)) == 0);
      }
    }
    else {
      return false;
    }
  }


  /**
   * Gets the Index attribute of the ImageTheme object
   *
   * @param x  Description of Parameter
   * @param y  Description of Parameter
   * @return   The Index value
   */
  protected final int getIndex(int x, int y) {
    return x + y * getSize().width;
  }


  /**
   * Gets the PixelAt attribute of the ImageTheme object
   *
   * @param x  Description of Parameter
   * @param y  Description of Parameter
   * @return   The PixelAt value
   */
  protected final int getPixelAt(int x, int y) {
    return pixels[getIndex(x, y)];
  }


  /**
   * Description of the Method
   *
   * @return   Description of the Returned Value
   */
  protected boolean ensureSize() {
    return (getSize() != null);
  }

  public RasterModel getRasterModel() {
    return null;
  }

  public void modelChanged() {

  }

  public VAT getVAT() {
    return null;
  }

  public boolean hasVAT() {
    return false;
  }


  /**
   * Description of the Method
   *
   * @return   Description of the Returned Value
   */
  protected boolean ensureData() {
    if ((pixels == null) && (!failed) && (ensureSize())) {
      synchronized (this) {
        Dimension D = getSize();
        pixels = new int[D.width * D.height];
        PixelGrabber pg = new PixelGrabber
            (image, 0, 0, D.width, D.height, pixels, 0, D.width);

        try {
          pg.grabPixels();
          if ((pg.getStatus() & (ABORT | ERROR)) != 0) {
            throw new InterruptedException();
          }
        }
        catch (InterruptedException E) {
          pg.abortGrabbing();
          pixels = null;
          failed = true;
        }
        ;
      }
    }
    return (pixels != null);
  }

}

