/*
 * MMSMultiDataTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.mms.dmi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.omscentral.gis.model.EnumerableTheme;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorTheme;
import org.w3c.dom.Node;
import oui.mms.datatypes.TimeSeries;
import oui.gui.OuiGui;
import oui.mms.MmsProjectXml;
import oui.mms.io.MmsDataFileReader;
import oui.treetypes.OuiDataTreeNode;
import oui.tstool.TimeSeriesTool;

/** This class contains the necessary information to register a shape/dbf
 * file and an MMS input data file with the Object User Interface (OUI).
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
public class MMSMultiDataTreeNode extends OuiDataTreeNode {
//    private MmsProjectXml pxml;
    private String[] variableNames = null;
    private String dataFileExtension = null;

    /** Create an MMSMultiDataTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public MMSMultiDataTreeNode(Node xml_node) {
        super(xml_node);
        _type = "Shape w/MMS Data File";   // overrides OuiTreeNode/OuiShapeTreeNode
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
//        dmiName = pxml.getDmiName(this.getDmiNode());
        dataFileExtension = MmsProjectXml.getElementContent(xml_node, "@datafileExtension", "set your datafile!");
        variableNames = pxml.getDmiVariableNames(xml_node);
    }
    
    /** Call this when a feature is selected from the OUI map and timeseries data is
     * to be plotted. This method will read the data from an MMS input file and plot
     * it in the TimeSeriesTool.
     * @param feature The feature selected from the OUI map.
     */
    public void queryData(Feature feature) {
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        
        String stationName =  getStationNameForFeature(feature);
        String modelName = pxml.getModelNameForThemeId(this._theme_name, stationName);
        String modelID = pxml.getModelIdForThemeId(this._theme_name, stationName);
        String dataFileTag = modelName + dataFileExtension;
        String dataFile = pxml.getFileFullPath("datafile", dataFileTag);
        
        JPopupMenu analysis_popup = new JPopupMenu();
        analysis_popup.add (new JLabel("Which variable for " + getStationNameForFeature(feature) + "?"));
        analysis_popup.addSeparator();
        
        String[] variable_names = getVariableNames ();
        for (int i = 0; i < variable_names.length; i++) {
            analysis_popup.add(new JMenuItem(variable_names[i])).addActionListener (new AnalysisActionListener (feature, dataFile, modelID, stationName));
        }
        analysis_popup.show(OuiGui.getOuiGisPanel(), 100, 100);
    }
    
    /**
     * Getter for property variableNames.
     * @return Value of property variableNames.
     */
    public String[] getVariableNames() {
//        if (this.variableNames == null) {
//            this.variableNames = pxml.getDmiVariableNames(_xml_node);
//        }
        return this.variableNames;
    }
    
    private class AnalysisActionListener implements ActionListener {
        private Feature feature;
        private String dataFile;
        private String modelID;
        private String stationName;
        
        public AnalysisActionListener(Feature feature, String dataFile, String modelID, String stationName) {
            this.feature = feature;
            this.dataFile = dataFile;
            this.modelID = modelID;
            this.stationName = stationName;
        }
        
        public void actionPerformed(ActionEvent e) {
            for (int j = 0 ; j < ((VectorTheme)(_theme)).getFeatureCount(); j++) {
                Feature f = ((EnumerableTheme)(_theme)).getFeature(j);
                
                if (f == feature) {
                    MmsDataFileReader mdfr = new MmsDataFileReader (dataFile);
                    
                    String variable_name = e.getActionCommand();
                    
                    int var_index = -1;
                    
                    try {
                        var_index = Integer.decode(modelID).intValue();
                    } catch (NumberFormatException ex) {
                        System.out.println ("Bad ModelID Mapping \"" + modelID + "\" in project file for " + stationName);
                    }
                    
                    if (var_index == -1) return;  //  No data for selected item
                   
                    String list_label = variable_name + " at " + stationName;

                    TimeSeriesTool tst = TimeSeriesTool.createTimeSereiesTool ();
                    
                    TimeSeries tsc = mdfr.getTimeSeries(variable_name + " " + var_index);
                    tsc.setName(list_label);
                    tst.addTrace (tsc);
                    break;
                }
            }
        }
    }
}
