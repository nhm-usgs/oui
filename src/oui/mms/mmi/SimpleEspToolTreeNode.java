/*
 * SimpleiEspToolTreeNode.java
 *
 */

package oui.mms.mmi;

import gov.usgs.cawsc.gui.WindowFactory;
import org.w3c.dom.Node;
import oui.mms.datatypes.EnsembleData;
import oui.esptool.EspTool;
import oui.mms.MmsProjectXml;
import oui.treetypes.OuiModelTreeNode;

/** 
 * @author markstro
 * @version 2.0
 */
public class SimpleEspToolTreeNode extends OuiModelTreeNode {
    private String espXmlFile;


    /** Create an MMSDataTreeNode.
     * @param xml_node The xml node element which describes this shape/dbf file combo.
     * @param parent The OUI tree node parent of this OUI tree node.
     */
    public SimpleEspToolTreeNode(Node xml_node) {
        super(xml_node);
        _type = "ESP Tool";   // overrides OuiTreeNode/OuiShapeTreeNode

        MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        this.espXmlFile = MmsProjectXml.getElementContent (xml_node, "@espXmlFile", null);
    }

    @Override
    public void declare() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        EnsembleData ed = EnsembleData.load(espXmlFile);
        EspTool et = new EspTool(ed);
        WindowFactory.displayInFrame(et, "OUI ESP Tool");
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
