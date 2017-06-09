/*
 * OuiThemeTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.treetypes;

import org.omscentral.gis.model.Theme;
import org.w3c.dom.Node;
import oui.gui.OuiGISPanel;
import oui.gui.Oui;
import oui.gui.OuiGui;
import oui.mms.OuiProjectXml;

/**This abstract class contains the necessary information to register a GIS theme
 * with the Object User Interface (OUI). The information describing the theme
 * and how to display it is read from the XML control file tree.xml in the OUI
 * project directory. Refer to <I>The Object User Interface (OUI): User's
 * Manual</I> for more information.
 *
 * @author markstro
 * @version 2.0
 */
public abstract class OuiThemeTreeNode extends OuiTreeNode {
    
    protected Boolean _displayed = new Boolean(false);
    protected Theme _theme;
    protected String _theme_name;
    protected String _file_name;
    
    /** Create an OuiThemeTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    
    public OuiThemeTreeNode(Node xml_node) {
        super(xml_node);
        OuiProjectXml pxml = OuiProjectXml.getOuiProjectXml();
        _theme_name = pxml.getElementContent(xml_node, "@theme", "no theme");
        String path = pxml.getPath (pxml.getThemeMetaData(_theme_name, "@path", null));
        _file_name = path + "/" + pxml.getThemeMetaData(_theme_name, "@file_name", null);
    }
    
    /** Show the theme for this node on the OUI map.
     * @param d Turn the visiability of the theme on or off.
     */
    public void setDisplayed(Boolean d) {
        if (_displayed.booleanValue() == d.booleanValue()) {
            
        } else if (!_displayed.booleanValue() && d.booleanValue()) {
            if (_theme == null) {
                _theme = loadTheme();
                OuiGui.getOuiGisPanel().addTheme(_theme);
            }
            
            if (_theme != null) {
                OuiGui.getOuiGisPanel().setThemeVisible(_theme, true);
                _displayed = new Boolean(true);
            }
            
        } else {
            OuiGui.getOuiGisPanel().setThemeVisible(_theme, false);
            _displayed = new Boolean(false);
        }
    }
    
    /** Remove the theme for this node from the OUI map.
     */
    public void removeTheme() {
        _displayed = new Boolean(false);
        OuiGui.getOuiGisPanel().removeTheme(_theme);
        _theme = null;
    }
    
    /** Return the state state for this node.
     * @return Is this node visiable?
     */
    public Boolean isDisplayed() {return _displayed;}
    
    /** Return the theme for this node.
     * @return The theme for this node.
     */
    public Theme getTheme() {return _theme;}
    
    /** Return the theme name for this node.
     * @return The theme name for this node.
     */
    public String getThemeName() {return _theme_name;}
    
    public String getFileName() {return _file_name;}
    
    /** Extending classes must implement the code that reads the native format of the theme.
     * @return The theme for this node.
     */
    public abstract Theme loadTheme();
}
