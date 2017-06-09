package org.omscentral.gis.ui.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;

import org.omscentral.gis.ui.ColoredShape;

import org.omscentral.gis.model.VectorModel;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorTheme;

import org.omscentral.gis.util.Iteration;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Area;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Polygon;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 11, 2001
 */
public class VectorLayer extends Layer {

  /**
   * Description of the Field
   */
  protected ViewFeature[] features;

  protected AbstractVectorThemeColorModel colorModel;

  /**
   * Description of the Field
   */
  protected Grid grid;

  public Feature getFeature (int index) {
      return features[index].getFeature();
  }
  
  public static Renderer createBoundingBoxRenderer(final Paint p) {
    return new Renderer() {
      public void render(Object o,Graphics2D g) {
        ViewFeature feature = (ViewFeature) o;
        g.setPaint(p);
        g.draw(feature.getVirtualBounds());
      }
    };
  }


  private final Drawing labelDrawing =
    new Drawing(this) {

      Font orgFont;


      /**
       * Description of the Method
       *
       * @param obj  Description of Parameter
       */
      public final void iterate(Object obj) {
        ViewFeature viewfeature = (ViewFeature) obj;
        Point point = viewfeature.getLabelPosition();

        String s = ((VectorTheme) theme).getAttribute(viewfeature.getIndex(), labelIdx).toString();
        int halfWidth = metrics.stringWidth(s) / 2;
        int halfHeight = metrics.getHeight() / 2;
        super.g.drawString(s, point.x - halfWidth, point.y - halfHeight);
        if (viewfeature.isSelected()) {
          super.g.drawLine(point.x - halfWidth - 1, point.y - halfHeight - 1, point.x + halfWidth + 1, point.y + halfHeight + 1);
          //super.g.drawLine(point.x - 5, point.y - 6, point.x + metrics.stringWidth(s), point.y + 6);
        }
      }


      /**
       * Description of the Method
       */
      void close() {
        super.g.setFont(orgFont);
        super.close();
      }


      /**
       * Description of the Method
       *
       * @param g  Description of Parameter
       */
      void open(Graphics g) {
        super.open(g);
        g.setColor(Color.black);
        orgFont = g.getFont();
        if (labelFont != null) {
          g.setFont(labelFont);
        }
      }

    };
  private final Drawing featureDrawing =
    new Drawing(this) {

      /**
       * Description of the Method
       *
       * @param obj  Description of Parameter
       */
      public final void iterate(Object obj) {
        ViewFeature viewfeature = (ViewFeature) obj;
        viewfeature.drawFeature(super.g);
      }

    };

  /**
   * Description of the Field
   */
  public static int pointRadius = 3;


  /**
   * Constructor for the VectorLayer object
   *
   * @param vectortheme     Description of Parameter
   * @param transformation  Description of Parameter
   */
  protected VectorLayer(VectorTheme vectortheme, AffineTransform transformation) {
    super(vectortheme, transformation);
    colorModel = AbstractVectorThemeColorModel.createDefault(vectortheme);
    features = new ViewFeature[vectortheme.getFeatureCount()];
    grid = new Grid(super.realBounds, features.length);
    for (int i = 0; i < features.length; i++) {
      features[i] = createViewFeature(i, vectortheme.getFeature(i));
      features[i].updateScale();
      grid.registerFeature(features[i]);
    }
    grid.close();
  }


  /**
   * Sets the Selection attribute of the VectorLayer object
   *
   * @param feature  The new Selection value
   * @param flag     The new Selection value
   */
  public final synchronized void setSelection(Feature feature, boolean flag) {
    int index = getIndexOfFeature(feature);
    features[index].setSelected(flag);
  }

  public final synchronized void setOutline(Feature feature, boolean flag) {
    int index = getIndexOfFeature(feature);
    features[index].setOutlined(flag);
  }

  public final synchronized void clearOutlined() {
    for (int i = 0; i < features.length; i++) {
      features[i].setOutlined(false);
    }
  }
  
  

  public AbstractVectorThemeColorModel getColorModel() {
    return colorModel;
  }


  /**
   * Sets the Selection attribute of the VectorLayer object
   *
   * @param flag  The new Selection value
   */
  public final synchronized void setSelection(boolean flag) {
    for (int i = 0; i < features.length; i++) {
      features[i].setSelected(flag);
    }
  }


  /**
   * Gets the VirtualBounds attribute of the VectorLayer object
   *
   * @param feature  Description of Parameter
   * @return         The VirtualBounds value
   */
  public Rectangle getVirtualBounds(Feature feature) {
    ViewFeature viewfeature = grid.findFirst(feature.getBounds2D(), false, new FeatureFinder(feature));
    if (viewfeature != null) {
      Rectangle rectangle = viewfeature.getVirtualBounds().getBounds();
      if (super.labels && super.labelIdx >= 0) {
        Point point = viewfeature.getLabelPosition();
        String s = ((VectorTheme) super.theme).getAttribute(viewfeature.getIndex(), super.labelIdx).toString();
        rectangle.add(new Rectangle(point.x + 5, (point.y + 5) - super.metrics.getMaxAscent(), super.metrics.stringWidth(s), super.metrics.getHeight()));
        if (viewfeature.isSelected()) {
          rectangle.add(new Point(point.x + 5, point.y + 6));
        }
      }
      rectangle.grow(1, 1);
      return rectangle;
    }
    else {
      return null;
    }
  }


  /**
   * Gets the Selected attribute of the VectorLayer object
   *
   * @param feature  Description of Parameter
   * @return         The Selected value
   */
  public final boolean isSelected(Feature feature) {
    return features[getIndexOfFeature(feature)].isSelected();
  }


  /**
   * Description of the Method
   *
   * @param g2  Description of Parameter
   */
  public synchronized void draw(Graphics g) {
    if (super.visible ) {
      Graphics2D g2 = (Graphics2D) g;
      Rectangle rectangle = g2.getClipBounds();
      featureDrawing.open(g2);
      
      Composite comp = g2.getComposite();
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,colorModel.getTransparency()));
      grid.iterate(rectangle, false, featureDrawing);
      g2.setComposite(comp);
      featureDrawing.close();
      if (super.labels && super.labelIdx >= 0) {
        labelDrawing.open(g2);
        grid.iterate(rectangle, false, labelDrawing);
        labelDrawing.close();
      }
    }
  }







  /**
   * Description of the Method
   */
  public void flush() {

  }



  /**
   * Description of the Method
   *
   * @param point  Description of Parameter
   * @param i      Description of Parameter
   * @return       Description of the Returned Value
   */
  public synchronized Feature locateFeature(Point point, int i) {

    ViewFeature viewfeature;
    if ((viewfeature = grid.findLast(point)) != null) {
      return viewfeature.getFeature();
    }
    if (i > 0) {
      int j = point.x + i;
      int k = point.y + i;
      for (int l = point.x - i; l <= j; l++) {
        for (int i1 = point.y - i; i1 <= k; i1++) {
          Point point1 = new Point(l, i1);
          ViewFeature viewfeature1;
          if ((viewfeature1 = grid.findLast(point1)) != null) {
            return viewfeature1.getFeature();
          }
        }

      }

    }
    return null;
  }


  /**
   * Description of the Method
   *
   * @param r2d   Description of Parameter
   * @param flag  Description of Parameter
   * @return      Description of the Returned Value
   */
  public List locateFeatures(Rectangle2D r2d, boolean flag) {
    return grid.findAll(r2d, flag);
  }


  /**
   * Description of the Method
   *
   * @param t  Description of Parameter
   */
  public synchronized void updateScale(AffineTransform t) {
    super.updateScale(t);

    for (int i = 0; i < features.length; i++) {
      features[i].updateScale();
    }

  }


  /**
   * Gets the Paint attribute of the VectorLayer object
   *
   * @param viewfeature  Description of Parameter
   * @return             The Paint value
   */
  protected Paint getFillPaint(ViewFeature viewfeature) {
    Feature feature = viewfeature.getFeature();
    if (viewfeature.outlined) return colorModel.getOutlinedPaint();
    return viewfeature.isSelected()
         ? colorModel.getSelectedPaint()
         : colorModel.getFillPaint(feature);
  }

  protected Paint getBorderPaint(ViewFeature viewfeature) {
    Feature feature = viewfeature.getFeature();
    return colorModel.getBorderPaint(feature);
  }

//  protected ColoredShape getShape(ViewFeature viewfeature) {
//    return colorModel.getShape(viewfeature.getFeature());
//  }


  /**
   * Gets the Stroke attribute of the VectorLayer object
   *
   * @param viewfeature  Description of Parameter
   * @return             The Stroke value
   */
  protected Stroke getStroke(ViewFeature viewfeature) {
    Feature feature = viewfeature.getFeature();
    return viewfeature.isSelected()
         ? colorModel.getSelectedStroke()
         : colorModel.getStroke(feature);
  }


  /**
   * Gets the IndexOfFeature attribute of the VectorLayer object
   *
   * @param feature  Description of Parameter
   * @return         The IndexOfFeature value
   */
  protected int getIndexOfFeature(Feature feature) {
    ViewFeature viewfeature = grid.findFirst(feature.getBounds2D(), false, new FeatureFinder(feature));
    return viewfeature == null ? -1 : viewfeature.getIndex();
  }



  /**
   * Description of the Method
   *
   * @param i        Description of Parameter
   * @param feature  Description of Parameter
   * @return         Description of the Returned Value
   */
  private ViewFeature createViewFeature(int i, Feature feature) {
    if (feature == null) {
      return new NullView(i);
    }
    if (feature.getClass() == (org.omscentral.gis.model.VectorModel.PointFeature.class)) {
      return new ViewPoint(i, (org.omscentral.gis.model.VectorModel.PointFeature) feature);
    }
    if (feature.getClass() == (org.omscentral.gis.model.VectorModel.MultiPointFeature.class)) {
      return new ViewMultiPoint(i, (org.omscentral.gis.model.VectorModel.MultiPointFeature) feature);
    }
    if (feature.getClass() == (org.omscentral.gis.model.VectorModel.MultiLineFeature.class)) {
      return new ViewArc(i, (org.omscentral.gis.model.VectorModel.MultiLineFeature) feature);
    }
    if (feature.getClass() == (org.omscentral.gis.model.VectorModel.MultiPolygonFeature.class)) {
      if (((org.omscentral.gis.model.VectorModel.MultiPolygonFeature) feature).getPartCount() <= 1) {
        return new ViewPolygon(i, (org.omscentral.gis.model.VectorModel.MultiPolygonFeature) feature);
      }
      else {
        return new ViewMultiPolygon(i, (org.omscentral.gis.model.VectorModel.MultiPolygonFeature) feature);
      }
    }
    else {
      return null;
    }
  }

  public Object getColoringModel() {
    return colorModel;
  }  

  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected abstract class ViewFeature {

    boolean selected;
    boolean outlined;
    int index;
    Rectangle virtualBounds;

    /**
     * Constructor for the ViewFeature object
     *
     * @param i  Description of Parameter
     */
    ViewFeature(int i) {
      selected = false;
      index = i;
      virtualBounds = new Rectangle();
    }


    /**
     * Sets the Selected attribute of the ViewFeature object
     *
     * @param flag  The new Selected value
     */
    final void setSelected(boolean flag) {
      selected = flag;
    }

    final void setOutlined(boolean flag) {
      outlined = flag;
    }


    /**
     * Gets the Bounds2D attribute of the ViewFeature object
     *
     * @return   The Bounds2D value
     */
    Rectangle2D getBounds2D() {
      return getFeature().getBounds2D();
    }


    /**
     * Gets the Feature attribute of the ViewFeature object
     *
     * @return   The Feature value
     */
    abstract Feature getFeature();


    /**
     * Gets the Index attribute of the ViewFeature object
     *
     * @return   The Index value
     */
    final int getIndex() {
      return index;
    }


    /**
     * Gets the LabelPosition attribute of the ViewFeature object
     *
     * @return   The LabelPosition value
     */
    abstract Point getLabelPosition();


    /**
     * Gets the VirtualBounds attribute of the ViewFeature object
     *
     * @return   The VirtualBounds value
     */
    public Rectangle getVirtualBounds() {
      return this.virtualBounds;
    }


    /**
     * Gets the Selected attribute of the ViewFeature object
     *
     * @return   The Selected value
     */
    final boolean isSelected() {
      return selected;
    }


    /**
     * Description of the Method
     *
     * @param point  Description of Parameter
     * @return       Description of the Returned Value
     */
    abstract boolean contains(Point point);


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void drawFeature(Graphics2D g,Renderer r) {
      r.render(this,g);
    }

    void drawFeature(Graphics2D g) {
      g.setPaint(getFillPaint(this));
      fillFeature(g);
      g.setStroke(getStroke(this));
      g.setPaint(getBorderPaint(this));
      drawOutline(g);
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    abstract void drawOutline(Graphics2D g);


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    abstract void fillFeature(Graphics2D g);


    /**
     * Description of the Method
     */
    synchronized void updateScale() {
      initShapes();
    }


    /**
     * Description of the Method
     */
    abstract void initShapes();

  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected final class NullView extends ViewFeature {

    Feature feature;


    /**
     * Constructor for the NullView object
     *
     * @param i  Description of Parameter
     */
    NullView(int i) {
      super(i);
      feature =
        new Feature() {

          /**
           * Gets the Bounds2D attribute of the NullView object
           *
           * @return   The Bounds2D value
           */
          public Rectangle2D getBounds2D() {
            return realBounds;
          }


          /**
           * Description of the Method
           *
           * @param d   Description of Parameter
           * @param d1  Description of Parameter
           * @return    Description of the Returned Value
           */
          public boolean contains(double d, double d1) {
            return false;
          }


          /**
           * Description of the Method
           *
           * @param p2d  Description of Parameter
           * @return     Description of the Returned Value
           */
          public final boolean contains(Point2D p2d) {
            return false;
          }

        };
    }


    /**
     * Gets the Feature attribute of the NullView object
     *
     * @return   The Feature value
     */
    Feature getFeature() {
      return feature;
    }


    /**
     * Gets the LabelPosition attribute of the NullView object
     *
     * @return   The LabelPosition value
     */
    Point getLabelPosition() {
      return virtualBounds.getLocation();
    }


    /**
     * Description of the Method
     *
     * @param point  Description of Parameter
     * @return       Description of the Returned Value
     */
    boolean contains(Point point) {
      return false;
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void drawFeature(Graphics2D g) {
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void drawOutline(Graphics2D g) {
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void fillFeature(Graphics2D g) {
    }


    /**
     * Description of the Method
     */
    void scaleComplete() {
    }


    /**
     * Description of the Method
     */
    void initShapes() {
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected class ViewPoint extends ViewFeature {

    org.omscentral.gis.model.VectorModel.PointFeature point;
    ///ColoredShape shape;
    Shape shapeView;


    /**
     * Constructor for the ViewPoint object
     *
     * @param i             Description of Parameter
     * @param pointfeature  Description of Parameter
     */
    ViewPoint(int i, org.omscentral.gis.model.VectorModel.PointFeature pointfeature) {
      super(i);
      point = pointfeature;
      int d = VectorLayer.pointRadius;
      ///shape = getShape(this);
    }


    /**
     * Gets the Feature attribute of the ViewPoint object
     *
     * @return   The Feature value
     */
    final Feature getFeature() {
      return point;
    }

    Rectangle2D getBounds2D() {
      return new Rectangle2D.Double(point.x,point.y,1,1);
    }


    /**
     * Gets the LabelPosition attribute of the ViewPoint object
     *
     * @return   The LabelPosition value
     */
    Point getLabelPosition() {
// DANGER -- markstro
        Point2D p = new Point2D.Double(point.getX(),point.getY());
        Point2D q = new Point2D.Double();
        trans.transform(p,q);
        
        return new Point((int) q.getX(), (int) q.getY());
    }


    /**
     * Description of the Method
     */
    void initShapes() {
      Shape s = new Rectangle(0, 0, 10, 10);///shape.getShape();
      Point2D p = new Point2D.Double(point.getX(),point.getY());
      Point2D q = new Point2D.Double();
      trans.transform(p,q);
      AffineTransform at = new AffineTransform();
      at.setToTranslation(q.getX(),q.getY());
      shapeView = at.createTransformedShape(s);
      virtualBounds = shapeView.getBounds();
    }


    /**
     * Description of the Method
     *
     * @param point1  Description of Parameter
     * @return        Description of the Returned Value
     */
    boolean contains(Point point1) {
//    System.out.println("point bounds " + shapeView.getBounds());
 //   System.out.println("click point " + point1);
      return shapeView.contains(point1);
    }

    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void drawOutline(Graphics2D g) {
      g.draw(shapeView);
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void fillFeature(Graphics2D g) {
      if (!this.isSelected())
///        g.setPaint(shape.getFillColor());
        g.setPaint(Color.WHITE);
      else
        g.setPaint(colorModel.getSelectedPaint());
      g.fill(shapeView);
    }

  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected class ViewMultiPoint extends ViewFeature {

    Rectangle bounds;
    org.omscentral.gis.model.VectorModel.MultiPointFeature points;
    int partCount;
    Shape shapes[];


    /**
     * Constructor for the ViewMultiPoint object
     *
     * @param i                  Description of Parameter
     * @param multipointfeature  Description of Parameter
     */
    ViewMultiPoint(int i, org.omscentral.gis.model.VectorModel.MultiPointFeature multipointfeature) {
      super(i);
      bounds = null;
      points = multipointfeature;
      //initShapes();
    }


    /**
     * Description of the Method
     */
    public void flush() {

    }


    /**
     * Gets the Feature attribute of the ViewMultiPoint object
     *
     * @return   The Feature value
     */
    final Feature getFeature() {
      return points;
    }


    /**
     * Gets the LabelPosition attribute of the ViewMultiPoint object
     *
     * @return   The LabelPosition value
     */
    Point getLabelPosition() {
      Rectangle b = shapes[0].getBounds();
      return new Point((int) b.getCenterX(), (int) b.getCenterY());
    }


    /**
     * Gets the PartLength attribute of the ViewMultiPoint object
     *
     * @param i  Description of Parameter
     * @return   The PartLength value
     */
    int getPartLength(int i) {
      return points.getPointCount();
    }


    /**
     * Description of the Method
     *
     * @param point  Description of Parameter
     * @return       Description of the Returned Value
     */
    final boolean boundsContain(Point point) {
      return virtualBounds.contains(point);
    }


    /**
     * Description of the Method
     *
     * @param point  Description of Parameter
     * @return       Description of the Returned Value
     */
    boolean contains(Point point) {
      if (boundsContain(point)) {
        for (int i = shapes.length - 1; i >= 0; i--) {
          if (this instanceof ViewPolygon) {
            if (shapes[i].contains(point))
              return true;
          }
          else
          if (shapes[i].getBounds().contains(point)) {
            return true;
          }
        }
      }
      return false;
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void drawOutline(Graphics2D g) {
      for (int i = 0; i < shapes.length; i++) {
        g.draw(shapes[i]);
      }
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void fillFeature(Graphics2D g) {
      for (int i = 0; i < shapes.length; i++) {
        g.fill(shapes[i]);
      }
    }


    /**
     * Description of the Method
     */
    void initShapes() {
      partCount = 0;
      shapes = new Shape[0];
    }

  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected class ViewArc extends ViewMultiPoint {

    /**
     * Constructor for the ViewArc object
     *
     * @param i                 Description of Parameter
     * @param multilinefeature  Description of Parameter
     */
    ViewArc(int i, org.omscentral.gis.model.VectorModel.MultiLineFeature multilinefeature) {
      super(i, multilinefeature);
    }


    /**
     * Gets the PartLength attribute of the ViewArc object
     *
     * @param i  Description of Parameter
     * @return   The PartLength value
     */
    int getPartLength(int i) {
      return ((org.omscentral.gis.model.VectorModel.MultiLineFeature) super.points).getPartLength(i);
    }


    /**
     * Description of the Method
     */
    void initShapes() {
      VectorModel.MultiLineFeature mlf =
          (org.omscentral.gis.model.VectorModel.MultiLineFeature) points;
      shapes = new Shape[mlf.getPartCount()];
      this.virtualBounds.setBounds(0,0,0,0);
      for (int i = 0; i < shapes.length; i++) {
        GeneralPath path = new GeneralPath();
        Point2D[] points = mlf.getPointsOfPart(i);
        for (int j = 0; j < points.length; j++) {
          if (j + 1 < points.length) {
            path.append(new Line2D.Double(points[j], points[j + 1]), false);
          }
        }
        shapes[i] = trans.createTransformedShape(path);
        if (virtualBounds.isEmpty())
          virtualBounds = shapes[i].getBounds();
        this.virtualBounds.createUnion(shapes[i].getBounds2D());
      }
    }

    void drawOutline(Graphics2D g) {
      if (!this.isSelected())
        super.drawOutline(g);
    }

    void fillFeature(Graphics2D g) {
      if (this.isSelected())
        super.drawOutline(g);
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected class ViewPolygon extends ViewMultiPoint {

    /**
     * Constructor for the ViewPolygon object
     *
     * @param i                    Description of Parameter
     * @param multipolygonfeature  Description of Parameter
     */
    ViewPolygon(int i, org.omscentral.gis.model.VectorModel.MultiPolygonFeature multipolygonfeature) {
      super(i, multipolygonfeature);
      initShapes();
    }


    /**
     * Description of the Method Description of the Method
     */
    void initShapes() {
      VectorModel.MultiPolygonFeature mpf =
          (VectorModel.MultiPolygonFeature) points;
      shapes = new Shape[mpf.getPartCount()];
      this.virtualBounds.setBounds(0,0,0,0);
      for (int i = 0; i < shapes.length; i++) {
        Polygon poly = new Polygon();
        Point2D[] points = mpf.getPointsOfPart(i);
        for (int j = 0; j < points.length; j++) {
          poly.addPoint(
              (int) points[j].getX(),
              (int) points[j].getY()
              );
        }
        shapes[i] = trans.createTransformedShape(poly);
        if (virtualBounds.isEmpty())
          virtualBounds = shapes[i].getBounds();
        this.virtualBounds.createUnion(shapes[i].getBounds2D());
      }
      Area outerRing = new Area(shapes[0]);
      for (int i = 1; i < shapes.length; i++) {
        outerRing.subtract(new Area(shapes[i]));
      }
      shapes = new Shape[] {outerRing};
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected class ViewMultiPolygon extends ViewPolygon {

    /**
     * Constructor for the ViewMultiPolygon object
     *
     * @param i                    Description of Parameter
     * @param multipolygonfeature  Description of Parameter
     */
    ViewMultiPolygon(int i, org.omscentral.gis.model.VectorModel.MultiPolygonFeature multipolygonfeature) {
      super(i, multipolygonfeature);
    }


    /**
     * Description of the Method
     *
     * @param point  Description of Parameter
     * @return       Description of the Returned Value
     */
    boolean contains(Point point) {
      return boundsContain(point);
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected class GridEntry {

    List<ViewFeature> elements;
    ViewFeature features[];


    /**
     * Constructor for the GridEntry object
     *
     * @param i  Description of Parameter
     */
    GridEntry(int i) {
      elements = new java.util.ArrayList<ViewFeature>(i);
      features = null;
    }


    /**
     * Description of the Method
     *
     * @param viewfeature  Description of Parameter
     */
    void add(ViewFeature viewfeature) {
      elements.add(viewfeature);
    }


    /**
     * Description of the Method
     */
    synchronized void close() {
      features = new ViewFeature[elements.size()];
      elements.toArray(features);
      elements = null;
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  protected class Grid {

    Rectangle2D box;
    int elements;
    GridEntry grid[][];
    int xGrids;
    int yGrids;
    double gridWidth;
    double gridHeight;
    double featuresPerCell;
    double cellsPerFeature;
    double ratioFactor;
    int maxCol;
    int maxRow;


    /**
     * Constructor for the Grid object
     *
     * @param r2d  Description of Parameter
     * @param i    Description of Parameter
     */
    Grid(Rectangle2D r2d, int i) {
      box = r2d;
      elements = i;
      if (box.isEmpty()) {
        xGrids = yGrids = 1;
        gridWidth = gridHeight = 1.7976931348623157E+308D;
        ratioFactor = 1.0D;
      }
      else {
        double d = Math.max(1.0D, calculateRasterCount(elements));
        double d1 = Math.sqrt(d * Math.max(1.0D / d, Math.min(d, box.getWidth() / box.getHeight())));
        xGrids = Math.max(1, (int) Math.round(Math.ceil(d1)));
        yGrids = Math.max(1, (int) Math.round(d / d1));
        gridWidth = box.getWidth() / (double) xGrids;
        gridHeight = box.getHeight() / (double) yGrids;
        double area = (box.getMaxX() - box.getMinX()) *
            (box.getMaxY() - box.getMinY());
        ratioFactor = ((double) elements * 1.2D) / area;
      }
      maxCol = xGrids - 1;
      maxRow = yGrids - 1;
      cellsPerFeature = -1D;
      featuresPerCell = (double) elements / (double) (xGrids * yGrids);
      int j = (int) Math.round(featuresPerCell);
      grid = new GridEntry[xGrids][yGrids];
      for (int k = 0; k < xGrids; k++) {
        for (int l = 0; l < yGrids; l++) {
          grid[k][l] = new GridEntry(j);
        }

      }

    }


    /**
     * Description of the Method
     */
    synchronized void close() {
      int i = 0;
      for (int j = 0; j < xGrids; j++) {
        for (int k = 0; k < yGrids; k++) {
          grid[j][k].close();
          i += grid[j][k].features.length;
        }

      }

      cellsPerFeature = (double) i / (double) features.length;
    }


    /**
     * Description of the Method
     *
     * @param rectangle  Description of Parameter
     * @param flag       Description of Parameter
     * @return           Description of the Returned Value
     */
    final List findAll(Rectangle rectangle, boolean flag) {
      Rectangle real = rectangle;
      try {
        real = trans.createInverse().createTransformedShape(rectangle).getBounds();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      return findAll(real, flag);
    }


    /**
     * Description of the Method
     *
     * @param r2d   Description of Parameter
     * @param flag  Description of Parameter
     * @return      Description of the Returned Value
     */
    final List findAll(Rectangle2D r2d, boolean flag) {


      System.out.println("r2d " + r2d);
      System.out.println("box " + box);

      final ArrayList<Feature> l = new ArrayList<Feature>();
//          getVectorSize(box.createIntersection(r2d))
//          );
      iterate(r2d, flag,
        new Iteration() {

          /**
           * Description of the Method
           *
           * @param obj  Description of Parameter
           */
          public final void iterate(Object obj) {
            l.add(((ViewFeature) obj).getFeature());
          }

        });
      return l;
    }


    /**
     * Description of the Method
     *
     * @param r2d         Description of Parameter
     * @param flag        Description of Parameter
     * @param constraint  Description of Parameter
     * @return            Description of the Returned Value
     */
    ViewFeature findFirst(Rectangle2D r2d, boolean flag, Constraint constraint) {
      Rectangle2D r2d1 = box.createIntersection(r2d);
      if (r2d1 != null) {
        int i = getColumn(r2d1.getMinX());
        int j = getRow(r2d1.getMinY());
        int k = getColumn(r2d1.getMaxX());
        int l = getRow(r2d1.getMaxY());
        for (int i1 = i; i1 <= k; i1++) {
          for (int j1 = j; j1 <= l; j1++) {
            ViewFeature aviewfeature[] = grid[i1][j1].features;
            int k1 = getCellNumber(i1, j1);
            for (int l1 = 0; l1 < aviewfeature.length; l1++) {
              Rectangle2D r2d2;
              Rectangle2D r2d3 = r2d1.createIntersection(r2d2 = aviewfeature[l1].getBounds2D());
              if (r2d3 != null && k1 == getCellNumber(r2d3.getMinX(), r2d3.getMinY()) && (!flag || r2d1.contains(r2d2)) && constraint.isSatisfied(aviewfeature[l1])) {
                return aviewfeature[l1];
              }
            }

          }

        }

        return null;
      }
      else {
        return null;
      }
    }


    /**
     * Description of the Method
     *
     * @param point  Description of Parameter
     * @return       Description of the Returned Value
     */
    synchronized ViewFeature findLast(Point point) {
      Point2D p2d = new Point2D.Double();
      AffineTransform at;
      try {
        at = trans.createInverse();
      } catch (Exception e) {
        throw new java.lang.IllegalStateException("Transform univertable");
      }

      p2d = at.transform(
          new Point2D.Double(point.getX(), point.getY()),
          p2d
      );


//
//      if (box.contains(p2d)) {
        ViewFeature aviewfeature[] = grid[getColumn(p2d.getX())][getRow(p2d.getY())].features;
        for (int i = aviewfeature.length - 1; i >= 0; i--) {
            if (aviewfeature[i].contains(point)) {
              return aviewfeature[i];
          }
        }

        return null;
//      }
//      else {
//        return null;
//      }
    }


    /**
     * Description of the Method
     *
     * @param rectangle  Description of Parameter
     * @param flag       Description of Parameter
     * @param iteration  Description of Parameter
     */
    final void iterate(Rectangle rectangle, boolean flag, Iteration iteration) {
      Rectangle2D real = null;
      try {
        real = trans.createInverse().createTransformedShape(rectangle).getBounds2D();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      iterate(real, flag, iteration);
    }


    /**
     * Description of the Method
     *
     * @param r2d        Description of Parameter
     * @param flag       Description of Parameter
     * @param iteration  Description of Parameter
     */
    void iterate(Rectangle2D r2d, boolean flag, Iteration iteration) {
      Rectangle2D r2d1 = box.createIntersection(r2d);
      if (box.equals(r2d1)) {
        for (int i = 0; i < features.length; i++) {
          iteration.iterate(features[i]);
        }

      }
      else
          if (r2d1 != null) {
        int j = getColumn(r2d1.getMinX());
        int k = getRow(r2d1.getMinY());
        int l = getColumn(r2d1.getMaxX());
        int i1 = getRow(r2d1.getMaxY());
        for (int j1 = j; j1 <= l; j1++) {
          for (int k1 = k; k1 <= i1; k1++) {
            ViewFeature aviewfeature[] = grid[j1][k1].features;
            int l1 = getCellNumber(j1, k1);
            for (int i2 = 0; i2 < aviewfeature.length; i2++) {
              Rectangle2D r2d2;
              Rectangle2D r2d3 = r2d1.createIntersection(r2d2 = aviewfeature[i2].getBounds2D());
              if (r2d3 != null && l1 == getCellNumber(r2d3.getMinX(), r2d3.getMinY()) && (!flag || r2d1.contains(r2d2))) {
                iteration.iterate(aviewfeature[i2]);
              }
            }

          }

        }

      }
    }


    /**
     * Description of the Method
     *
     * @param viewfeature  Description of Parameter
     */
    synchronized void registerFeature(ViewFeature viewfeature) {
      Rectangle2D r2d = viewfeature.getBounds2D();
      int i = getColumn(r2d.getMinX());
      int j = getRow(r2d.getMinY());
      int k = getColumn(r2d.getMaxX());
      int l = getRow(r2d.getMaxY());
      for (int i1 = i; i1 <= k; i1++) {
        for (int j1 = j; j1 <= l; j1++) {
          grid[i1][j1].add(viewfeature);
        }

      }

    }


    /**
     * Gets the CellNumber attribute of the Grid object
     *
     * @param d   Description of Parameter
     * @param d1  Description of Parameter
     * @return    The CellNumber value
     */
    private final int getCellNumber(double d, double d1) {
      return getCellNumber(getColumn(d), getRow(d1));
    }


    /**
     * Gets the CellNumber attribute of the Grid object
     *
     * @param i  Description of Parameter
     * @param j  Description of Parameter
     * @return   The CellNumber value
     */
    private final int getCellNumber(int i, int j) {
      return i + j * xGrids;
    }


    /**
     * Gets the Column attribute of the Grid object
     *
     * @param d  Description of Parameter
     * @return   The Column value
     */
    private final int getColumn(double d) {
      return Math.min(maxCol, getIndex(d, box.getMinX(), gridWidth));
    }


    /**
     * Gets the Index attribute of the Grid object
     *
     * @param d   Description of Parameter
     * @param d1  Description of Parameter
     * @param d2  Description of Parameter
     * @return    The Index value
     */
    private final int getIndex(double d, double d1, double d2) {
      return Math.max(1, (int) Math.round(Math.ceil((d - d1) / d2))) - 1;
    }


    /**
     * Gets the Row attribute of the Grid object
     *
     * @param d  Description of Parameter
     * @return   The Row value
     */
    private final int getRow(double d) {
      return Math.min(maxRow, getIndex(d, box.getMinY(), gridHeight));
    }


    /**
     * Gets the VectorSize attribute of the Grid object
     *
     * @param r2d  Description of Parameter
     * @return     The VectorSize value
     */
    private final int getVectorSize(Rectangle2D r2d) {
      double area = (r2d.getMaxX() - r2d.getMinX()) *
          (r2d.getMaxY() - r2d.getMinY());
      return r2d == null ? 0 : (int) Math.round(ratioFactor * area) + 1;
    }


    /**
     * Description of the Method
     *
     * @param i  Description of Parameter
     * @return   Description of the Returned Value
     */
    private final double calculateRasterCount(int i) {
      return Math.pow(i, 0.59999999999999998D);
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  private class FeatureFinder implements Constraint {

    Feature feature;


    /**
     * Constructor for the FeatureFinder object
     *
     * @param feature1  Description of Parameter
     */
    FeatureFinder(Feature feature1) {
      feature = feature1;
    }


    /**
     * Gets the Satisfied attribute of the FeatureFinder object
     *
     * @param obj  Description of Parameter
     * @return     The Satisfied value
     */
    public final boolean isSatisfied(Object obj) {
      return ((ViewFeature) obj).getFeature() == feature;
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  private abstract class Drawing
       implements Iteration {

    Graphics2D g;


    /**
     * Constructor for the Drawing object
     *
     * @param l  Description of Parameter
     */
    Drawing(Layer l) {
      g = null;
    }


    /**
     * Description of the Method
     *
     * @param obj  Description of Parameter
     */
    public abstract void iterate(Object obj);


    /**
     * Description of the Method
     */
    void close() {
      g = null;
    }


    /**
     * Description of the Method
     *
     * @param g  Description of Parameter
     */
    void open(Graphics g) {
      this.g = (Graphics2D) g;
    }
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 11, 2001
   */
  private interface Constraint {
    /**
     * Gets the Satisfied attribute of the Constraint object
     *
     * @param o  Description of Parameter
     * @return   The Satisfied value
     */
    public boolean isSatisfied(Object o);
  }

}
