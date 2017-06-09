package org.omscentral.gis.ui.panel;

import java.awt.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

import org.omscentral.gis.model.FPRasterTheme;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.Theme;
import org.omscentral.gis.model.ThemeObserver;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 11, 2001
 */
class FPRasterLayer extends RasterLayer implements ThemeObserver {

  /**
   * Description of the Field
   */
  protected Object imageLock = new Object();

  /**
   * Description of the Field
   */
  protected Image image = null;
  /**
   * Description of the Field
   */
  protected ImageObserver observer;


  public void themeUpdated(Theme t) {
    dirty = true;
  }

  public Renderer createDefaultRenderer() {
    return new Renderer() {
      public void render(Object o,Graphics2D G) {
        FPRasterTheme rasterTheme = (FPRasterTheme) theme;

        Rectangle defaultBounds = rasterTheme.getBounds().getBounds();
        Rectangle2D X = null;
        try {
          if (G.getClipBounds() == null) {
            X = defaultBounds;
          }
          else {
            X = defaultBounds.createIntersection
                (trans.createInverse().createTransformedShape(G.getClipBounds()).getBounds2D());
          }
        }
        catch (Exception e) {
          e.printStackTrace();
        }

        if (X != null) {
          Rectangle D = trans.createTransformedShape(X).getBounds();
          Point S1 = rasterTheme.getRasterCoordinate(X.getMinX(), X.getMinY());
          Point S2 = rasterTheme.getRasterCoordinate(X.getMaxX(), X.getMaxY());

          synchronized (imageLock) {
            if ((dirty || image == null) && (color != null)) {
              image = color.createImage(rasterTheme.getRasterModel());
            }
            
            Composite composite = G.getComposite();
            G.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,color.getTransparency()));
            G.drawImage
                (image,
                D.x,
                D.y,
                D.x + D.width,
                D.y + D.height,
                S1.x,
                S2.y,
                S2.x,
                S1.y,
                observer);
            G.setComposite(composite);
          }
        }
      }
    };
  }


  /**
   * Constructor for the FPRasterLayer object
   *
   * @param theme      Description of Parameter
   * @param transform  Description of Parameter
   * @param observer   Description of Parameter
   */
  protected FPRasterLayer
      (FPRasterTheme theme, AffineTransform transform, ImageObserver observer) {
    super(theme, transform);
    this.observer = observer;
    theme.addObserver(this);
    renderer = createDefaultRenderer();
  }


  /**
   * Description of the Method
   *
   * @param f  Description of Parameter
   * @param g  Description of Parameter
   */
  public void drawFeature(Feature f, java.awt.Graphics g,Renderer r) {
  }



  /**
   * Description of the Method
   */
  public void flush() {

  }


  /**
   * Description of the Method
   *
   * @param G  Description of Parameter
   */
  public void draw(java.awt.Graphics g) {
    if (visible) {
      renderer.render(this,(Graphics2D)g);
      dirty = false;
    }
  }


//  public void timeStepChanged(TimeSeries timeSeries)
//  {
//    flush();
//  }


}

