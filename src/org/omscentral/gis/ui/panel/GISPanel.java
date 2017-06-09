package org.omscentral.gis.ui.panel;

import java.util.List;
import java.util.ArrayList;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingConstants;
import javax.swing.AbstractAction;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JTextField;
import javax.swing.JPopupMenu;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.omscentral.gis.model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import org.omscentral.gis.io.*;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 20, 2001
 */
public class GISPanel
  extends DisplayPane
  implements ThemeViewObserver, DisplayPaneListener{

  private final static int FCTOFFS = 4;
  private final static int CLRFLAG = 1 << 1;
  private final static int SETFLAG = 1 << 0;
  private final static int BOXFLAG = 1 << 2;

  public static int NO_FUNCTION = 5;
  public static int SELECT_FUNCTION = 6;
  public static int MEASURE_FUNCTION = 7;
  public static int ZOOM_FUNCTION = 8;
  public static int ZOOM_RECT_FUNCTION = 9;
  public static int PAN_FUNCTION = 10;
  public static int NO_OBSERVER = 0;
  public static int LIGHT_TRACKING_OBSERVER = 1;
  public static int HEAVY_TRACKING_OBSERVER = 2;
  private static DisplayMode[] modes = {
    DisplayMode.NO_OBSERVER,
    DisplayMode.LIGHT_TRACKING_OBSERVER,
    DisplayMode.LIGHT_TRACKING_OBSERVER,
    null,
    null,
    DisplayMode.NO_FUNCTION,
    DisplayMode.SELECTION_FUNCTION_MODE,
    DisplayMode.MEASURING_FUNCTION_MODE,
    DisplayMode.ZOOM_FUNCTION_MODE,
    DisplayMode.ZOOM_RECT_FUNCTION_MODE,
    DisplayMode.PAN_FUNCTION_MODE
  };

  /**
   * Description of the Field
   */
  protected static int fuzzyDistance = 2;

  /**
   * Description of the Field
   */
  protected ThemeView themeView;

  /**
   * Description of the Field
   */
  protected GISPanelObserver observer = null;

	/**
   * Description of the Field
   */
  protected List<CoordinatesListener> coordListeners = new ArrayList<CoordinatesListener>();

  protected ScaleField scalefield = null;

  protected Yardstock yardstock = null;

  protected HvyTracking tracker = null;

	/**
   * Description of the Field
   */
  protected JPopupMenu popupMenu;

	/**
   * Description of the Field
   */
  protected boolean customizing = false;

  /**
   * Description of the Field
   */
  protected Theme activeTheme = null;


  protected String tipText;

  /**
   * Description of the Field
   */
  protected final static int RULE_WIDTH = 16;
  /**
   * Description of the Field
   */
  protected final static int BLOCK_WIDTH = 4;
  /**
   * Description of the Field
   */
  protected final static int BLOCK_PAD = 2;

  /**
   * Description of the Field
   */
  protected final static int MINOR_TICKS = 5;
  /**
   * Description of the Field
   */
  protected final static int MAJOR_TICKS = 5;

  /**
   * Description of the Field
   */
  protected final static int MINOR_WIDTH = 4;
  /**
   * Description of the Field
   */
  protected final static int MAJOR_WIDTH = 7;

  /**
   * Description of the Field
   */
  protected final static int INSET = 2;

  /**
   * Description of the Field
   */
  protected final static int X_PAD = 3;
  /**
   * Description of the Field
   */
  protected final static int Y_PAD = 0;



  private final static int BLOCK_OFFS = RULE_WIDTH - BLOCK_WIDTH - BLOCK_PAD;
  private final static int BLOCK_LEN = MINOR_TICKS * MAJOR_TICKS;


  /**
   * Constructor for the GISPanel object
   */
  public GISPanel() {
    super();
    super.setHorizontalScrollBarPolicy(
      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    super.setVerticalScrollBarPolicy(
      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    setContext(themeView = new ThemeView());

    themeView.addThemeViewObserver(this);

    super.addDisplayPaneListener(this);

    setCoordinatesVisible(false);

    setPopup( new JPopupMenu() );
    popupEnabled = true;
  }

  protected void finalize() throws Throwable {
    System.out.println("GISPanel is finalized");
    super.finalize();
  }


  /**
   * Sets the ActiveTheme attribute of the GISPanel object
   *
   * @param theme  The new ActiveTheme value
   */
  public synchronized void setActiveTheme(Theme theme) {
    activeTheme = theme;
  }


  /**
   * Sets the GISPanelObserver attribute of the GISPanel object
   *
   * @param newObserver  The new GISPanelObserver value
   */
  public synchronized void setGISPanelObserver(GISPanelObserver newObserver) {
    observer = newObserver;
  }


  /**
   * Sets the CoordinatesVisible attribute of the GISPanel object
   *
   * @param visible  The new CoordinatesVisible value
   */
  public synchronized void setCoordinatesVisible(boolean visible) {
    super.setCoordsVisible(visible);
  }


  /**
   * Sets the CoordListener attribute of the GISPanel object
   *
   * @param l  The new CoordListener value
   */
  public void addCoordListener(CoordinatesListener l) {
    if (!coordListeners.contains(l))
      coordListeners.add(l);
  }

  public void removeCoordListener(CoordinatesListener l) {
    coordListeners.remove(l);
  }

    public void panMode() {
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

  /**
   * Sets the FunctionMode attribute of the GISPanel object
   *
   * @param mask  The new FunctionMode value
   */
  public final synchronized void setFunctionMode(int m) {
    DisplayMode mode = modes[m];
    if (mode != null)
      setFunctionMode(mode);
  }

  public final synchronized void setObserverMode(int m) {
    DisplayMode mode = modes[m];
    if (mode != null)
      setObserverMode(mode);
  }

  public boolean isCurrentFunctionMode(int m) {
    DisplayMode mode = modes[m];
    if (getFunctionMode() == mode)
      return true;
    return false;
  }

  public boolean isCurrentObserverMode(int m) {
    DisplayMode mode = modes[m];
    if (getObserverMode() == mode)
      return true;
    return false;
  }



  /**
   * Gets the ThemeView attribute of the GISPanel object
   *
   * @return   The ThemeView value
   */
  public ThemeView getThemeView() {
    return themeView;
  }


  /**
   * Gets the CoordinatesVisible attribute of the GISPanel object
   *
   * @return   The CoordinatesVisible value
   */
  public boolean getCoordinatesVisible() {
    return super.areCoordsVisible();
  }










  /**
   * Description of the Method
   */
  public final void zoomIn() {
    if (!themeView.isEmpty()) {
      super.zoomIn();
    }
  }


  /**
   * Description of the Method
   */
  public final void zoomOut() {
    if (!themeView.isEmpty()) {
      super.zoomOut();
    }
  }


  /**
   * Description of the Method
   *
   * @param box  Description of Parameter
   */
  public final synchronized void zoomTo(Rectangle2D box) {
    if (!themeView.isEmpty()) {
      Dimension D = getViewport().getSize();
      themeView.scaleBy
          (
          (box.isEmpty())
           ? 1.0
           : Math.min(D.width / box.getWidth(), D.height / box.getHeight())
           / themeView.getScale(),
          true);
    }
  }


  /**
   * Description of the Method
   */
  public final void fit() {
    if (themeView.isEmpty()) {
      themeView.setSize(getViewport().getSize());
    }
    else {
      zoomTo(themeView.getRealBounds());
    }
  }


  /**
   * Description of the Method
   *
   * @return   Description of the Returned Value
   */
  public Yardstock getYardstockInstance() {
    if (yardstock == null)
      yardstock = new Yardstock();
    return yardstock;
  }


  /**
   * Description of the Method
   *
   * @return   Description of the Returned Value
   */
  public ScaleField getScaleFieldInstance() {
    if(scalefield == null)
      scalefield = new ScaleField();
    return scalefield;
  }

  public HvyTracking getTracker() {
    if (tracker == null) {
      tracker = new HvyTracking(this);
      addCoordListener(tracker);
    }
    return tracker;
  }


  /**
   * Description of the Method
   */
  public final void clearFunctionMode() {
    super.clearMode();
  }


  /**
   * Description of the Method
   *
   * @param G  Description of Parameter
   */
  public void paint(Graphics G) {
    super.paint(G);
  }


  /**
   * Description of the Method
   *
   * @param themeView  Description of Parameter
   */
  public void themeViewUpdated(ThemeView themeView) {
    if (themeView.isEmpty()) {
      fit();
    }
  }

  public JPopupMenu getPopup() {
    JPopupMenu p = new JPopupMenu();

    for (int i = 0; i < themeView.getThemeCount(); i++) {
      Theme t = themeView.getThemeAt(i);
      p.add(createThemeMenuItem(t));
    }
    return p;
  }

  private JMenuItem createThemeMenuItem(final Theme t) {
    JMenuItem retval = null;
    Layer layer = themeView.getLayerOf(t);
    if (t instanceof VectorTheme) {
      VectorTheme vt = (VectorTheme) t;
      retval = new JMenu(t.getName());
      String[] attnames = vt.getAttributeNames();
      addCheckItems(retval,attnames,layer);
    } else if (t instanceof RasterTheme) {
      RasterTheme rt = (RasterTheme) t;
      if (rt.hasVAT()) {
        retval = new JMenu(t.getName());
        VAT vat = rt.getVAT();
        String[] cols = vat.getColumnNames();
        addCheckItems(retval,cols,layer);
      } else {
        retval = new JCheckBoxMenuItem(t.getName());
      }
    }

    return retval;
  }

  private void addCheckItems(JMenuItem menu,final String[] names,final Layer select) {
    JCheckBoxMenuItem tmp;
    for (int i = 0; i < names.length; i++) {
      final String name = names[i];
      AbstractAction a = new AbstractAction(names[i]) {
        public void actionPerformed(ActionEvent e) {
          setActiveTheme(select.getTheme());
          if (tracker != null)
            tracker.setTrackingItem(select,name);
        }
      };
      menu.add(tmp = new JCheckBoxMenuItem(a));
      if (activeTheme == select.getTheme()) {
        if (tracker != null && tracker.getTrackingItem(select).equals(name))
          tmp.setSelected(true);
      }
    }
  }



  /**
   * Gets the Integer attribute of the GISPanel object
   *
   * @param value  Description of Parameter
   * @return       The Integer value
   */









  public synchronized void scaleTo(Point2D p,double relativeScale) {
    themeView.scaleBy(relativeScale,true);
  }


  /**
   * Description of the Method
   *
   * @param v      Description of Parameter
   * @param scale  Description of Parameter
   * @return       Description of the Returned Value
   */
  public static final double round(double v, int scale) {
    double power = Math.pow(10, scale);
    return Math.round(v * power) / power;
  }


  /**
   * Description of the Method
   *
   * @param G     Description of Parameter
   * @param S     Description of Parameter
   * @param x     Description of Parameter
   * @param left  Description of Parameter
   * @param y     Description of Parameter
   * @param top   Description of Parameter
   */
  public static void drawString(Graphics G, String S, int x,
      boolean left, int y, boolean top) {
    FontMetrics M = G.getFontMetrics(G.getFont());

    if (!(left)) {
      x -= M.stringWidth(S) + X_PAD * 2;
    }
    if (!(top)) {
      y -= M.getHeight() + Y_PAD * 2;
    }

    G.drawString(S, x + X_PAD, y + Y_PAD + M.getMaxAscent());
  }


  /**
   * Description of the Method
   *
   * @param G               Description of Parameter
   * @param x               Description of Parameter
   * @param y               Description of Parameter
   * @param length          Description of Parameter
   * @param horizontalLine  Description of Parameter
   */
  public static void drawLine(Graphics G, int x, int y,
      int length, boolean horizontalLine) {
    G.drawLine(x, y, (horizontalLine) ? x + length : x,
        (horizontalLine) ? y : y + length);
  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 20, 2001
   */
  public class Yardstock extends JComponent
       implements ThemeViewObserver {

    /**
     * Description of the Field
     */
    protected final int PAD = 25;
    /**
     * Description of the Field
     */
    protected String unit;


    /**
     * Constructor for the Yardstock object
     */
    public Yardstock() {
      unit = "";

      setAlignmentY(0.5f);
      setAlignmentX(0.5f);

      themeView.addThemeViewObserver(this);
    }


    /**
     * Gets the AlignmentY attribute of the Yardstock object
     *
     * @return   The AlignmentY value
     */
    public float getAlignmentY() {
      return 0.5f;
    }


    /**
     * Gets the AlignmentX attribute of the Yardstock object
     *
     * @return   The AlignmentX value
     */
    public float getAlignmentX() {
      return 0.5f;
    }


    /**
     * Gets the PreferredSize attribute of the Yardstock object
     *
     * @return   The PreferredSize value
     */
    public Dimension getPreferredSize() {
      FontMetrics M = getFontMetrics(getFont());
      return new Dimension
          (BLOCK_LEN + PAD + M.stringWidth(unit), M.getHeight());
    }


    /**
     * Gets the MinimumSize attribute of the Yardstock object
     *
     * @return   The MinimumSize value
     */
    public Dimension getMinimumSize() {
      return getPreferredSize();
    }


    /**
     * Gets the MaximumSize attribute of the Yardstock object
     *
     * @return   The MaximumSize value
     */
    public Dimension getMaximumSize() {
      Dimension size = super.getMaximumSize();
      size.height = getFontMetrics(getFont()).getHeight();
      return size;
    }


    /**
     * Description of the Method
     *
     * @param G  Description of Parameter
     */
    public void paint(Graphics G) {
      FontMetrics M = getFontMetrics(getFont());
      int baseLine = getBaseline(M);

      int x = 20;
      int y = baseLine - BLOCK_WIDTH;

      G.setColor(Color.white);
      G.fillRect(x, y, BLOCK_LEN + 1, BLOCK_WIDTH);

      G.setColor(Color.black);
      G.drawRect(x, y, BLOCK_LEN, BLOCK_WIDTH - 1);

      drawLine(G, x, y - MAJOR_WIDTH, MAJOR_WIDTH, false);
      for (int i = 1; i < MAJOR_TICKS; i++) {
        x += MINOR_TICKS;
        drawLine(G, x, y - MINOR_WIDTH, MINOR_WIDTH, false);
      }
      drawLine(G, x + MINOR_TICKS, y - MAJOR_WIDTH, MAJOR_WIDTH, false);

      G.drawString(unit, BLOCK_LEN + PAD, baseLine);
    }


    /**
     * Description of the Method
     *
     * @param themeView  Description of Parameter
     */
    public void themeViewUpdated(ThemeView themeView) {
      setVisible(!themeView.isEmpty());
    }

    protected void updateScale() {
      unit = String.valueOf
          (round(BLOCK_LEN / themeView.getScale(), getDecimalScale()));
      repaint();
    }

    /**
     * Description of the Method
     *
     * @param E  Description of Parameter
     */
    public synchronized void componentResized(ComponentEvent E) {
      unit = String.valueOf
          (round(BLOCK_LEN / themeView.getScale(), getDecimalScale()));
      repaint();
    }


    /**
     * Gets the Baseline attribute of the Yardstock object
     *
     * @param M  Description of Parameter
     * @return   The Baseline value
     */
    private final int getBaseline(FontMetrics M) {
      return M.getAscent();
    }

  }


  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 20, 2001
   */
  public class ScaleField extends JTextField
       implements ThemeViewObserver, ActionListener {

    /**
     * Constructor for the ScaleField object
     */
    public ScaleField() {
      super(6);
      setHorizontalAlignment(SwingConstants.RIGHT);
      themeView.addThemeViewObserver(this);
      addActionListener(this);
    }


    /**
     * Gets the MaximumSize attribute of the ScaleField object
     *
     * @return   The MaximumSize value
     */
    public Dimension getMaximumSize() {
      return getPreferredSize();
    }


    /**
     * Description of the Method
     *
     * @param E  Description of Parameter
     */
    public void actionPerformed(ActionEvent E) {
      if (!themeView.isEmpty()) {
        try {
          double scale = Double.valueOf(getText()).doubleValue() / 100;
          if (0 < scale) {
            Dimension tvSize = themeView.getSize();
            Dimension vpSize = viewport.getSize();
            double current = Math.max
                ((float) tvSize.width / vpSize.width,
                (float) tvSize.height / vpSize.height);
            themeView.scaleBy( scale / current, true);
          }
        }
        catch (NumberFormatException X) {
        }
      }
    }


    /**
     * Description of the Method
     *
     * @param themeView  Description of Parameter
     */
    public void themeViewUpdated(ThemeView themeView) {
      setEnabled(!themeView.isEmpty());
      repaint();
    }


    protected void updateScale() {
      double s = getViewedScale();
      setText(
        String.valueOf(round(s * 100,2))
      );
    }

  }



    /**
     * Description of the Method
     *
     * @param Q  Description of Parameter
     */
    private void popup(Point Q) {
      final Location L = new Location(themeView, activeTheme,
          viewportToView(Q), fuzzyDistance);
      if ((L.theme != null) && (L.feature != null)) {
        String[] attributes = L.theme.getAttributeNames();
        popupMenu.removeAll();
        popupMenu.add(new JLabel("Feature #"));
        // + L.theme.getIndexOfFeature(L.feature)));
        popupMenu.addSeparator();
        for (int i = 0; i < attributes.length; i++) {
          final int index = i;
          popupMenu.add(new JMenuItem(attributes[i])).addActionListener
              (
            new ActionListener() {
              /**
               * Description of the Method
               *
               * @param E  Description of Parameter
               */
              public void actionPerformed(ActionEvent E) {
                if (observer != null) {
                  observer.customize(L.theme, L.feature, index);
                }
              }
            });
        }
//        crosshair.setVisible(false);
        popupMenu.show(GISPanel.this, Q.x, Q.y);
      }
    }














  /**
   * Description of the Class
   *
   * @author    en
   * @created   June 20, 2001
   */
  public interface CoordinatesListener {
    /**
     * Description of the Method
     *
     * @param realCoord     Description of Parameter
     * @param screenCoord   Description of Parameter
     * @param decimalScale  Description of Parameter
     */
    public void update(Point2D realCoord, Point screenCoord, int decimalScale);
  }

  public void mouseTracked(Point p) {
    updateCoordListeners(p);
  }

  private void updateCoordListeners(Point p) {
    for (int i = 0; i < coordListeners.size(); i++) {
      ((CoordinatesListener)coordListeners.get(i)).update(
        themeView.toRealPoint(p),p,this.getDecimalScale()
      );
    }
  }

  public Feature locateFeature(Point viewportCoords) {
    return themeView.locateFeature(activeTheme,toViewCoordinates(viewportCoords),fuzzyDistance);
  }

  public void mouseSelected(Point p, boolean clear) {
		//System.out.print("\nmouseSelected(Point p, boolean clear)\n");
    if (activeTheme == null) return;
    clearSelection(clear);
    Feature f = locateFeature(p);
    handleSelection(activeTheme,f,clear);
  }

  private void clearSelection(boolean clear) {
    if (!clear)
      return;

    themeView.setSelection(activeTheme,false);
    if (observer != null)
			observer.selected(activeTheme,false);
  }

  private void handleSelection(Theme t, Feature f, boolean clear) {
    if (t == null) return;
    if (clear) {
      themeView.setSelection(t,f,true);
    }
    else {
      themeView.toggleSelection(t,f);
    }
    if (observer != null) {
      observer.selected(t,f,true);
    }
  }

  public void mouseSelected(Rectangle r, boolean clear) {
		//System.out.print("\nmouseSelected(Rectangle r, boolean clear)\n");
    if (activeTheme == null) return;
    clearSelection(clear);
    Point offset = toViewCoordinates(r.getLocation());
    r.setLocation(offset);
    List features = themeView.locateFeatures(
      activeTheme,themeView.toRealRectangle(r),true);
    for (int i = 0; i < features.size(); i++ )
		{
			handleSelection(activeTheme,(Feature)features.get(i),clear);
    }
  }

  public void scaleChanged() {
    if (yardstock != null)
      yardstock.updateScale();
    if (scalefield != null)
      scalefield.updateScale();
  }

  public void createJPEG(java.io.File file) throws java.io.IOException {
    BufferedImage image = new BufferedImage(
      getContext().getWidth(),getContext().getHeight(),
      BufferedImage.TYPE_INT_RGB
    );
    Graphics g = image.getGraphics();
    g.setClip(0,0,themeView.getWidth(),themeView.getHeight());
    themeView.paint(g);
    ImageIO.write(image,"JPEG",file);
  }

  public void createImageMap(int layer,String file) throws java.io.IOException {
    org.omscentral.gis.io.ThemeFactory factory = new org.omscentral.gis.io.ThemeFactory();
    String map = org.omscentral.gis.util.HTMLMaker.createHTMLImageMapTag(
      "map",(org.omscentral.gis.model.VectorTheme) getThemeView().getThemeAt(layer),getContext());
    java.io.File htmlFile = new java.io.File(file);
    java.io.FileOutputStream fout = new java.io.FileOutputStream(htmlFile);

    fout.write(map.getBytes());
    createJPEG(new java.io.File(htmlFile.getParent(),"map.jpg"));
  }

  /**
   *
   */
  static public void main(String[] args) throws java.io.IOException {
//    GISPanel gis = new GISPanel();
//    gis.getThemeView().setSize(400,400);
//    org.omscentral.gis.io.ThemeFactory factory = new org.omscentral.gis.io.ThemeFactory();
//    String dir = "/home/en/.scfigis/workspace";
//    gis.getThemeView().addTheme(factory.createTheme(dir,"east_hru"));
//    String html = org.omscentral.gis.util.GISUtililites.createHTMLImageMapTag(
//      "junk",(org.omscentral.gis.model.VectorTheme)gis.getThemeView().getThemeAt(0),gis.getContext());
//    System.out.println(html);
//    gis.createJPEG(new java.io.File("/home/en/east_hru.jpg"));
    final GISPanel gis = new GISPanel();

    JFrame f = new JFrame();
		f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
    f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(gis,BorderLayout.CENTER);
    JButton mm = new JButton("make map");
    mm.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent ae) {
        try {
          gis.createImageMap(0, "map.html");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    f.getContentPane().add(mm,BorderLayout.SOUTH);


    TrackingList list = new TrackingList();
    JFrame f2 = new JFrame();
    f2.getContentPane().add(list);
    list.setGISPanel(gis);

    f2.setSize(400,400);
    f2.setVisible(true);

		f.setSize(400,400);
		f.setLocationRelativeTo(null);
		f.setVisible(true);

    String dir = "/home/en/.oms/projects/gis";
    gis.getThemeView().addTheme(ThemeFactory.createTheme(dir,"east_hru"));
  }

}

