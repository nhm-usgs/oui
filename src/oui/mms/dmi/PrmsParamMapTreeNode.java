/*
 * MMSStatvarTreeNode.java
 *
 * Created on May 31, 2012, 7:39 AM
 */

package oui.mms.dmi;

import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.omscentral.gis.model.Feature;
import org.omscentral.gis.model.VectorTheme;
import org.w3c.dom.Node;
import oui.gui.OuiGui;
import oui.mapper.paramMap.ParamMapGui;
import oui.mms.MmsProjectXml;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsParamsReader;
import oui.treetypes.OuiDataTreeNode;

/** This class contains the necessary information to register a shape/dbf
 * file and an MMS output animation file with the Object User Interface (OUI).
 *
 *
 * @author markstro
 * @version 2.0
 */
public class PrmsParamMapTreeNode extends OuiDataTreeNode {
    private String fileTag = null;
    private String parameterFileName = null;
    private String dimension = null;

    public PrmsParamMapTreeNode(Node xml_node) {
        super(xml_node);
        _type = "Shape w/PRMS Parameter File";   // overrides OuiTreeNode/OuiShapeTreeNode
        
        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        fileTag = MmsProjectXml.getElementContent(xml_node, "@parameterfile", "no file");
        dimension = MmsProjectXml.getElementContent(xml_node, "@dimension", null);
        parameterFileName = pxml.getFileFullPath("parameterfile", fileTag);

        if (dimension == null) {
            System.out.println ("PrmsParamMapTreeNode: dimension attribute not set in .xml file for node " + xml_node.getNodeName());
            dimension = "nhru";
        }
    }

    private ParameterSet ps = null;

    @Override
    public void queryNotification() {
// This method called when the "Query" check box in the the "Loaded Themes"
// panel is checked for this theme.        
        if (ps == null) {
            try {
                MmsParamsReader mp = new MmsParamsReader(parameterFileName);
                ps = (ParameterSet) (mp.read());
            } catch (IOException ex) {
                Logger.getLogger(PrmsParamMapTreeNode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // If the @displayVariable tag is not set in the xml file, it will be null.
        // Read the header of the animation file to find out the variables in there
        // and display a popup menu to ask them which variable to use for visualization.
            createAnimationTool();
    }

    @Override
    public void queryData(Feature feature) {}

    public void createAnimationTool() {
        int numberOfBins = 10;
        ParamMapGui pmg = new ParamMapGui(ps, dimension, OuiGui.getOuiGisPanel(),
                (VectorTheme) _theme, getIdAttribute(), numberOfBins, -1);
        WindowFactory.displayInFrame(pmg, "Animation Tool");
    }
}
