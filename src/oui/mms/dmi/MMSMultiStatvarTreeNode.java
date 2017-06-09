/*
 * MMSStatvarTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.mms.dmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import oui.mms.io.MmsStatvarReader;
import oui.tstool.TimeSeriesTool;
import org.w3c.dom.Node;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorTheme;
import javax.swing.JPopupMenu;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import org.omscentral.gis.model.EnumerableTheme;
import oui.gui.OuiGui;
import oui.treetypes.OuiDataTreeNode;
import oui.mms.MmsProjectXml;

/** This class contains the necessary information to register a shape/dbf
 * file and an MMS output statvar file with the Object User Interface (OUI).
 * The information describing the shape file and how to display it is read from the
 * XML control file tree.xml in the OUI project directory. Refer to <I>The Object
 * User Interface (OUI): User's Manual</I> for more information.
 * <P>
 * The element in tree.xml must look like this:<BR>
 * <CODE>&lt;node name="Regions" class="oui.gui.OuiShapeTreeNode" theme="regions" nameAttribute="NAME" fillColor="green" borderColor="black"/&gt;</CODE>
 * <P>
 * The element tag must be <B>node</B>.
 * <P>
 * The element has the following attributes. Some are required and are specified as
 * such. If an optional attribute is not specified in the element, a default value
 * is supplied by this class.
 * <P>
 * Attributes:<BR>
 *<UL>
 * <LI><B>name</B> (required) is the name as it appeares in the OUI project tree.<BR>
 * <LI><B>class</B> (required) specifies this class.  It must be set to
 * "oui.gui.OuiShapeTreeNode".<BR>
 * <LI><B>theme</B> (required) is the name of the shape/dbf file in the shapes/
 * directory in the project directory.<BR>
 * <LI><B>nameAttribute</B> (optional) is the name of the column in the dbf file to
 * use for labels on the OUI map.<BR>
 * <LI><B>fillColor</B> (optional) is the color to use to fill in the shapes on the
 * OUI map. The default fillColor is clear (no fill).<BR>
 * <LI><B>borderColor</B> (optional) is the color to use to outline the shapes on
 * the OUI map. The default borderColor is black.<BR>
 * <LI><B>color</B> (optional) is the color to use for filling and outlining shapes,
 * or to draw lines and points. The default color is black.<BR>
 * <LI><B>desc</B> (optional) is a description of the node. The default description is blank.<BR>
 *</UL>
 * <P>
 * The following colors are valid: black, blue, cyan, darkGray, gray, green,
 * lightGray, magenta, orange, pink, red, white, yellow, and clear.
 *
 *
 * @author markstro
 * @version 2.0
 */
public class MMSMultiStatvarTreeNode extends OuiDataTreeNode {
    private MmsStatvarReader msr = null;
    private String statvarFileExt = null;
//    private MmsProjectXml pxml = null;
    
    /** Create an MMSStatvarTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public MMSMultiStatvarTreeNode(Node xml_node) {
        super(xml_node);
        _type = "Shape w/Multiple MMS Statvar Files";   // overrides OuiTreeNode/OuiShapeTreeNode
        
//        pxml = MmsProjectXml.getMmsProjectXml();
        statvarFileExt = MmsProjectXml.getElementContent(xml_node, "@statvarExt", "");
    }
    
    /** Call this when a feature is selected from the OUI map and timeseries data is
     * to be plotted. This method will read the data from an MMS statvar file and plot
     * it in the TimeSeriesTool.
     * @param feature The feature selected from the OUI map.
     */
    public void queryData(Feature feature) {
        JPopupMenu analysis_popup = new JPopupMenu();
        String station_name = getStationNameForFeature(feature);
        analysis_popup.add (new JLabel("Which variable for " + station_name + "?"));
        analysis_popup.addSeparator();
        
        msr = getMsr(station_name);
        String[] var_names = msr.getVariableNames();
        
        if (var_names == null) {
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getOuiGisPanel()), "The MMS output file " + msr.getFileName() + " could not be loaded.  Have you run the model?", "Single Run File Error", JOptionPane.ERROR_MESSAGE);
        
        } else {
            int[] var_indexes = msr.getVariableIndexes();
            
            for (int i = 0; i < var_names.length; i++) {
                String variable_name = var_names[i] + " " + var_indexes[i];
                analysis_popup.add(new JMenuItem(variable_name)).addActionListener(new AnalysisActionListener(feature, this));
            }
            analysis_popup.show(OuiGui.getOuiGisPanel(), 100, 100);
        }
    }
    
    /**
     * Getter for property msr.
     * @return Value of property msr.
     */
    public MmsStatvarReader getMsr(String station_name) {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        String basin_name = pxml.getModelNameForThemeId(_theme_name, station_name);
        String file = pxml.getFileFullPath("statvarfile", basin_name + statvarFileExt);
            
        this.msr = new MmsStatvarReader (file);

//        if (this.msr == null) {
//            this.msr = new MmsStatvarReader (file);
//        } else if (!this.msr.getFileName().equals(file)) {
//            this.msr = new MmsStatvarReader (file);
//        }
        
        return this.msr;
    }
    
    private class AnalysisActionListener implements ActionListener {
        private Feature feature;
        private MMSMultiStatvarTreeNode mstn;
        
        public AnalysisActionListener(Feature feature, MMSMultiStatvarTreeNode mstn) {
            this.feature = feature;
            this.mstn = mstn;
        }
        
        public void actionPerformed(ActionEvent e) {
            for (int j = 0 ; j < ((VectorTheme)(_theme)).getFeatureCount(); j++) {
                Feature f = ((EnumerableTheme)(_theme)).getFeature(j);
                
                if (f == feature) {
                    String station_name = mstn.getStationNameForFeature (f);
                    MmsStatvarReader msr = mstn.getMsr(station_name);
                    String variable_name = e.getActionCommand();
                    
                    String list_label = variable_name + " at " + station_name;
                    
                    oui.mms.datatypes.TimeSeries tsc = msr.getTimeSeries(variable_name);
                    tsc.setName(list_label);
                    
                    TimeSeriesTool tst = TimeSeriesTool.createTimeSereiesTool ();
//                    tst.addTrace (msr.getDates (), msr.getValues (variable_name), list_label, list_label);
                    tst.addTrace (tsc);
                    break;
                }
            }
        }
    }
}
