/*
 * MMSDataTreeNode.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.mms.dmi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.omscentral.gis.model.Feature;
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
public class MMSDataTreeNode extends OuiDataTreeNode {
    private String[] variableNames = null;
    private String dataFileTag = null;
//    private MmsProjectXml pxml = null;
    
    /** Create an MMSDataTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public MMSDataTreeNode(Node xml_node) {
        super(xml_node);
        _type = "Shape w/MMS Data File";   // overrides OuiTreeNode/OuiShapeTreeNode
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        dataFileTag = MmsProjectXml.getElementContent(xml_node, "@datafile", "set your datafile!");
        variableNames = pxml.getDmiVariableNames(xml_node);
    }
    
    /** Call this when a feature is selected from the OUI map and timeseries data is
     * to be plotted. This method will read the data from an MMS input file and plot
     * it in the TimeSeriesTool.
     * @param feature The feature selected from the OUI map.
     */
    public void queryData(Feature feature) {
        String stationName = getStationNameForFeature(feature);
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        
        int variableIndex = -1;
        try {
            variableIndex = Integer.decode(pxml.getModelIdForThemeId(_theme_name, stationName)).intValue();
        } catch (Exception ex) {
            System.out.println("Bad value for modelId tag.   Theme name = " + _theme_name + " station name = " + stationName);
        }
        if(variableIndex == -1) return;  //  No data for selected item
        
        /**
         *  Make a popup menu with the list of variables for the selected feature.
         *  This is shown when the user selects a feature on the map.
         */
        JPopupMenu analysis_popup = new JPopupMenu();
        analysis_popup.add(new JLabel("Which variable for " + stationName + "?"));
        analysis_popup.addSeparator();
        
        if (variableNames != null) {
            for (int i = 0; i < variableNames.length; i++) {
                analysis_popup.add(new JMenuItem(variableNames[i])).addActionListener(new AnalysisActionListener(stationName, variableIndex));
            }
            analysis_popup.show(OuiGui.getOuiGisPanel(), 100, 100);
        }
    }
    
    private class AnalysisActionListener implements ActionListener {
        private String stationName;
        private int variableIndex;
        
        public AnalysisActionListener(String stationName, int variableIndex) {
            this.stationName = stationName;
            this.variableIndex = variableIndex;
        }
        
        public void actionPerformed(ActionEvent e) {
            String variableName = e.getActionCommand();
            MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
            
            MmsDataFileReader dataFileReader = new MmsDataFileReader(pxml.getFileFullPath("datafile", dataFileTag));
            TimeSeries tsc = dataFileReader.getTimeSeries(variableName + " " + variableIndex);
            
            TimeSeriesTool tst = TimeSeriesTool.createTimeSereiesTool();
            tsc.setName(variableName + " at " + stationName);
            tst.addTrace(tsc);
        }
    }
}
