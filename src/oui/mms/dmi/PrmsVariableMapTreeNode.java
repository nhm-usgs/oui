/*
 * MMSStatvarTreeNode.java
 *
 * Created on May 31, 2012, 7:39 AM
 */

package oui.mms.dmi;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorTheme;
import org.w3c.dom.Node;
import oui.gui.OuiGui;
import oui.mapper.variableMap.VariableMapGui;
import oui.mms.MmsProjectXml;
import oui.mms.datatypes.SpaceTimeSeriesData;
import oui.treetypes.OuiDataTreeNode;

/** This class contains the necessary information to register a shape/dbf
 * file and an MMS output animation file with the Object User Interface (OUI).
 *
 *
 * @author markstro
 * @version 2.0
 */
public class PrmsVariableMapTreeNode extends OuiDataTreeNode {
    private String file_name = null;
    private String displayVariable = null; // This is set in the XML file and is used to preselect a variable from the popup menu (popup menu is not used)
    private String animationFileName = null;

    public PrmsVariableMapTreeNode(Node xml_node) {
        super(xml_node);
        _type = "Shape w/MMF Animation File";   // overrides OuiTreeNode/OuiShapeTreeNode
        
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        file_name = MmsProjectXml.getElementContent(xml_node, "@gisoutfile", "no file");
        displayVariable = MmsProjectXml.getElementContent(xml_node, "@displayVariable", null);
        animationFileName = pxml.getFileFullPath("gisoutfile", file_name);
    }

    private SpaceTimeSeriesData tsd = null;

    @Override
    public void queryNotification() {
// This method called when the "Query" check box in the the "Loaded Themes"
// panel is checked for this theme.        
        if (tsd == null) {
            tsd = new SpaceTimeSeriesData(animationFileName);
        }
        
        tsd.getVariableNames();

        // If the @displayVariable tag is not set in the xml file, it will be null.
        // Read the header of the animation file to find out the variables in there
        // and display a popup menu to ask them which variable to use for visualization.
        if (displayVariable == null) {
            JPopupMenu analysis_popup = new JPopupMenu();
            analysis_popup.add(new JLabel("Which variable?"));
            analysis_popup.addSeparator();

            ArrayList<String> foo = tsd.getVariableNames();

            if (foo == null) {
                JOptionPane.showMessageDialog(GuiUtilities.windowFor(OuiGui.getOuiGisPanel()), "The MMF output file " + animationFileName + " could not be loaded.  Have you run the model?", "Single Run File Error", JOptionPane.ERROR_MESSAGE);

            } else {
                for (int i = 0; i < foo.size(); i++) {
                    analysis_popup.add(new JMenuItem(foo.get(i))).addActionListener(
                            new AnalysisActionListener(foo.get(i)));
                }
                analysis_popup.show(OuiGui.getOuiGisPanel(), 100, 100);
            }
        } else {
            createAnimationTool(displayVariable);
        }
    }

    @Override
    public void queryData(Feature feature) {}

    public void createAnimationTool(String variableName) {
        int numberOfBins = 10;
        VariableMapGui atg = new VariableMapGui(tsd, OuiGui.getOuiGisPanel(),
                (VectorTheme) _theme, getIdAttribute(), variableName, numberOfBins,
                tsd.getZoneCount());
        String title = "PRMS Variable Mapper";
        WindowFactory.displayInFrame(atg, title);
    }

    private class AnalysisActionListener implements ActionListener {
        private String variableName;

        public AnalysisActionListener(String variableName) {
            this.variableName = variableName;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            createAnimationTool(variableName);
        }
    }
}
