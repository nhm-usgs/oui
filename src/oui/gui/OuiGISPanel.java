/*
 * OuiGUSPanel.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.gui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.Theme;
import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.ui.panel.DisplayMode;
import org.omscentral.gis.ui.panel.GISPanel;
import org.omscentral.gis.ui.panel.GISPanelObserver;
import org.omscentral.gis.ui.panel.ThemeView;
import org.omscentral.gis.ui.panel.AbstractVectorThemeColorModel;
import org.omscentral.gis.ui.panel.VectorLayer;
import oui.treetypes.OuiDataTreeNode;
import oui.treetypes.OuiShapeTreeNode;
import oui.treetypes.OuiThemeTreeNode;
import oui.treetypes.OuiTreeNode;

/** This is an OMS GISPanel with a few twists.
 *
 * @author markstro
 * @version 2.0
 */

public class OuiGISPanel extends GISPanel implements GISPanelObserver, GISPanel.CoordinatesListener {
    final String blankFeatureString = " ";
    private final JLabel featureLabel;
    private final LoadedPanel loadedPanel;

    public static KeyStroke FONT_UP_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,KeyEvent.ALT_MASK);
    public static KeyStroke FONT_DOWN_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,KeyEvent.ALT_MASK);

    protected boolean useFixedFontSize = true;


    /** Create the OuiGISPanel object.
     * @param mapPanel
     * @param featureLabel
     * @param loadedPanel
     */
    public OuiGISPanel (JPanel mapPanel, JLabel featureLabel, LoadedPanel loadedPanel) {
        this.featureLabel = featureLabel;
        this.loadedPanel = loadedPanel;

        setFunctionMode(DisplayMode.SELECTION_FUNCTION_MODE);
        setGISPanelObserver(this);
        addCoordListener(this);
        mapPanel.add(this, java.awt.BorderLayout.CENTER);
    }

    /*
     *  Convience classes for accessing the themes on the ThemeView
     */

    public void addTheme (Theme theme) {getThemeView().addTheme(theme);}
    public void setThemeVisible (Theme theme, boolean is_visible) {getThemeView().setThemeVisible(theme, is_visible);}
    public int getThemeIndex(Theme theme) {return getThemeView().getThemeIndex(theme);}
    public void removeTheme (Theme theme) {getThemeView().removeTheme(theme);}
    public void moveTheme (Theme theme, int pos) {getThemeView().moveTheme(getThemeIndex(theme), pos);}
    public void setSelection (Theme theme, boolean is_selected) {getThemeView().setSelection(theme, is_selected);}
    public void setLabelIndex(Theme theme, int lable_index) {getThemeView().setLabelIndex(getThemeIndex(theme), lable_index);}
    public void setLabelsVisible (Theme theme, boolean labels_visible) {getThemeView().setLabelsVisible(getThemeIndex(theme), labels_visible);}
    public void tVrepaint() {getThemeView().repaint();}
    public AbstractVectorThemeColorModel getColorModel (VectorTheme theme) {return ((VectorLayer)(getThemeView().getLayerOf(theme))).getColorModel();}
    public void normalMode() {setFunctionMode(SELECT_FUNCTION);setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));}
    public void zoomInMode() {setFunctionMode(ZOOM_FUNCTION);setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));}
    public void zoomRectMode() {setFunctionMode(ZOOM_RECT_FUNCTION);setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));}
    public void zoomOutMode() {zoomOut();}
    @Override
    public void panMode() {
        setFunctionMode(PAN_FUNCTION);
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        this.firePropertyChange("panMode", false, true);
    }

public void setFixedFontSize(boolean fixed){
    useFixedFontSize = fixed;
    double scale = useFixedFontSize ? 0d : context.getScale();
    getThemeView().setLabelSize(getThemeView().getLabelSize(), scale);
}

public void setLabelSize(int size){
    double scale = useFixedFontSize ? 0d : context.getScale();
    getThemeView().setLabelSize(size, scale);
}

public void increaseLabelSize(){
    double scale = useFixedFontSize ? 0d : context.getScale();
    int newSize = getThemeView().increaseLabelSize(scale);
    this.firePropertyChange("labelFontSize", -1, newSize);
}

public void decreaseLabelSize() {
    double scale = useFixedFontSize ? 0d : context.getScale();
    int newSize = getThemeView().decreaseLabelSize(scale);
    this.firePropertyChange("labelFontSize", -1, newSize);
}

    @Override
    public void scaleChanged() {
        double scale = useFixedFontSize ? 0d : context.getScale();
        getThemeView().setLabelSize(getThemeView().getLabelSize(), scale);
        super.scaleChanged();
    }

   /*
    * Stack the themes on the OUI map in the same order that they are in on the loaded list.
    * @param theme_vec A vector containing the tree nodes in the correct order.
    */
    public void updateThemeOrder(ArrayList theme_vec) {
        for (int i = 0; i < theme_vec.size(); i++) {
            OuiThemeTreeNode ottn = ((OuiThemeTreeNode)(theme_vec.get(i)));
            getThemeView().moveTheme(getThemeIndex(ottn.getTheme()), theme_vec.size() - i - 1);
        }
    }

    /*
     * Remove all of the themes on the GIS panel.
     */
    public void clean() {
        ThemeView theme_view = getThemeView();

        for (int i = (theme_view.getThemeCount() - 1); i >= 0; i--) {
            Theme thm = theme_view.getThemeAt(i);
            theme_view.removeTheme(thm);
        }
    }



 /* -------------------------------------------------------------------------
  * Interface implementation - GISPanelObserver
  *
  */

    @Override
    public void pointed(Point2D p, boolean isSelected) {
        System.out.println(p);
        System.out.println("pointed function");
    }
    @Override
    public void customize(Theme theme, Feature feature, int index) {}
    @Override
    public synchronized void tracked(Point2D location, boolean inside) {}
    @Override
    public void tracked(Theme theme, Feature feature, Point2D location, boolean inside) {}
    @Override
    public void selected(Theme theme, boolean isSelected) {}
    @Override
    public void functionInvoked() {}
    @Override
    public void functionFinished() {}

    @Override
    public void selected(Theme theme, Feature feature, boolean isSelected) {
        // Get the OuiTreeNode for this theme
        OuiTreeNode otn = TreeNodes.getNodeForTheme(theme);

        if (otn == null) {
            return;
        }

        // If the parameter table is open, highlight the row.
        if (otn.has_table() && (((OuiShapeTreeNode)otn).isTableShown())) {
            ((OuiShapeTreeNode)otn).getTableFrame().setSelectedFeatureInTable(feature);
        }

        if (feature != null) {

            // Check for query data
            if (otn.has_data() && (((OuiDataTreeNode)otn).isInQueryMode())) {
                ((OuiDataTreeNode)otn).queryData(feature);
            }
        }
    }



 /* -------------------------------------------------------------------------
  * Interface implementation - GISPanel.CoordinatesListener
  *
  *  This does tracking.
  */
    protected Point mouseSelectedPoint = null;
    protected double x, y;
    public Point getMouseSelectedPoint() {
        return mouseSelectedPoint;
    }
    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    VectorTheme directlySetVectorTheme = null;
    int directlySetLabelIndex = 0;

    @Override
    public void update(Point2D realCoord, Point screenCoord, int decimalScale) {
        Feature f;
        mouseSelectedPoint = new Point(screenCoord);
        x = realCoord.getX();
        y = realCoord.getY();
        //    private Format format = new Format ("%.1f");
        String loc = String.format ("[x = %1$.1f,    y = %2$.1f]", realCoord.getX(),  realCoord.getY());
        f = locateFeature(screenCoord);
//        String loc = "[x = " + format.form(realCoord.getX()) + ",    y = " + format.form(realCoord.getY()) + "]";

        Theme activatedTheme = null;
        int labelIndex = 0;
        if (loadedPanel != null) {
            OuiShapeTreeNode activatedNode = loadedPanel.getActivatedNode();
            if (activatedNode != null) {
                activatedTheme = loadedPanel.getActivatedNode().getTheme();
                labelIndex = loadedPanel.getActivatedNode().getLabelIndex();
            }
        } else {
            activatedTheme = directlySetVectorTheme;
            labelIndex = directlySetLabelIndex;
        }

        if (f == null) {
            featureLabel.setText(loc);
        } else {
            if (activatedTheme == null) {
                featureLabel.setText(loc);

            } else {
                Object[] attributes = activatedTheme.getAttributes(f);
                featureLabel.setText(attributes[labelIndex].toString() + "     " + loc);
            }
        }
    }

    public void setActiveThemeDirectly(VectorTheme vt) {
        directlySetVectorTheme = vt;
    }

    public void setLabelIndexDirectly(int newLabelIndex) {
        directlySetLabelIndex = newLabelIndex;
    }

    @Override
    protected void processModeEvent(InputEvent e) {

    if (e instanceof KeyEvent) {
        // TODO increase or decrease the label font size
        if (KeyStroke.getKeyStrokeForEvent((KeyEvent)e).equals(FONT_UP_KEY)) {
            increaseLabelSize();
        }
        else if (KeyStroke.getKeyStrokeForEvent((KeyEvent)e).equals(FONT_DOWN_KEY)) {
            decreaseLabelSize();
        }
        else {
            super.processModeEvent(e);
        }
    }
    else {
        super.processModeEvent(e);
    }
  }
}
