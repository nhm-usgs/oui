package org.omscentral.gis.ui.panel;

import java.awt.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.geom.Rectangle2D;

import org.omscentral.gis.model.ImageTheme;
import org.omscentral.gis.model.Feature;


/**
 *  Description of the Class
 *
 *@author     en
 *@created    June 19, 2001
 */
public class ImageLayer extends RasterLayer {

	/**
	 *  Description of the Field
	 */
	protected Image image;
	/**
	 *  Description of the Field
	 */
	protected ImageObserver observer;

  protected float transparency = 1f;

	/**
	 *  Constructor for the ImageLayer object
	 *
	 *@param  theme     Description of Parameter
	 *@param  t         Description of Parameter
	 *@param  observer  Description of Parameter
	 */
	protected ImageLayer
			(ImageTheme theme, java.awt.geom.AffineTransform t, ImageObserver observer) {
		super(theme, t,null);
		image = theme.getImage();
		this.observer = observer;
	}



	/**
	 *  Description of the Method
	 *
	 *@param  G  Description of Parameter
	 */
	public void draw(Graphics G) {
		if (visible) {
			ImageTheme imgTheme = (ImageTheme) theme;

			Rectangle2D defaultBounds = imgTheme.getBounds();
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
				Point S1 = imgTheme.getRasterCoordinate(X.getMinX(), X.getMinY());
				Point S2 = imgTheme.getRasterCoordinate(X.getMaxX(), X.getMaxY());
        
        ((Graphics2D) G).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,transparency));

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
			}
		}
	}



	/**
	 *  Description of the Method
	 */
	public void flush() {
	}


	/**
	 *  Description of the Method
	 *
	 *@param  f  Description of Parameter
	 *@param  g  Description of Parameter
	 */
	public void drawFeature(Feature f, java.awt.Graphics g,Renderer r) {
	}

  /** Getter for property transparency.
   * @return Value of property transparency.
   *
   */
  public float getTransparency() {
    return transparency;
  }
  
  /** Setter for property transparency.
   * @param transparency New value of property transparency.
   *
   */
  public void setTransparency(float transparency) {
    this.transparency = transparency;
    notifyChange();
  }
  
}

