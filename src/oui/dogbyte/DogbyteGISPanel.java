/*
 * OuiGUSPanel.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.dogbyte;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.Theme;
import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.ui.panel.DisplayMode;
import org.omscentral.gis.ui.panel.GISPanel;
import org.omscentral.gis.ui.panel.GISPanelObserver;
import org.omscentral.gis.ui.panel.ThemeView;
import org.omscentral.gis.ui.panel.AbstractVectorThemeColorModel;
import org.omscentral.gis.ui.panel.VectorLayer;
import oui.treetypes.OuiThemeTreeNode;

/** This is an OMS GISPanel with a few twists.
 *
 * @author markstro
 * @version 2.0
 */

public class DogbyteGISPanel extends GISPanel implements GISPanelObserver, GISPanel.CoordinatesListener {
    final String blankFeatureString = new String(" ");
    
    /**
     * Create the SpagtiGISPanel object.
     * 
     * 
     * @param oui The OUI object for this panel.
     */
    public DogbyteGISPanel() {
        setFunctionMode(DisplayMode.SELECTION_FUNCTION_MODE);
        setGISPanelObserver(this);
        addCoordListener(this);
//        OuiGui.getMapPanel().add(this, java.awt.BorderLayout.CENTER);
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
    public void normalMode() {setFunctionMode(SELECT_FUNCTION);}
    public void zoomInMode() {setFunctionMode(ZOOM_FUNCTION);}
    public void zoomOutMode() {zoomOut();}
    
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
    
    public void pointed(Point2D p, boolean isSelected) {
        System.out.println(p);
        System.out.println("pointed function");
    }
    public void customize(Theme theme, Feature feature, int index) {}
    public synchronized void tracked(Point2D location, boolean inside) {}
    public void tracked(Theme theme, Feature feature, Point2D location, boolean inside) {}
    public void selected(Theme theme, boolean isSelected) {}
    public void functionInvoked() {}
    public void functionFinished() {}
    
    public void selected(Theme theme, Feature feature, boolean isSelected) {
//        // Get the OuiTreeNode for this theme
//        OuiTreeNode otn = ReadTree.getNodeForTheme(theme);
//        
//        // If the parameter table is open, highlight the row.
//        if (otn.has_table() && (((OuiShapeTreeNode)otn).isTableShown().booleanValue())) {
//            ((OuiShapeTreeNode)otn).getTableFrame().setSelectedFeatureInTable(feature);
//        }
//        
//        if (feature != null) {
//            
//            // Check for query data
//            if (otn.has_data() && (((OuiDataTreeNode)otn).isInQueryMode()).booleanValue()) {
//                ((OuiDataTreeNode)otn).queryData(feature);
//            }
//        }
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
    public void update(Point2D realCoord, Point screenCoord, int decimalScale) {
//        Feature f;
//        mouseSelectedPoint = new Point(screenCoord);
//        x = realCoord.getX();
//        y = realCoord.getY();
//        String loc = "[x = " + format.form(realCoord.getX()) + ",    y = " + format.form(realCoord.getY()) + "]";
//        if ((OuiGui.getLoadedPanel().getActivatedNode() == null) || ((f = locateFeature(screenCoord)) == null)) {
//            OuiGui.getFeatureLabel().setText(loc);
//            
//        } else {
//            Object[] attributes = OuiGui.getLoadedPanel().getActivatedNode().getTheme().getAttributes(f);
//            OuiGui.getFeatureLabel().setText(attributes[OuiGui.getLoadedPanel().getActivatedNode().getLabelIndex()].toString() + "     " + loc);
//        }
    }
}
