/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.mapper;

import gov.usgs.cawsc.gui.WindowFactory;
import java.io.FileNotFoundException;
import org.omscentral.gis.io.ShpFileParser;
import org.omscentral.gis.model.AttributeModel;
import org.omscentral.gis.model.VectorModel;
import org.omscentral.gis.model.VectorTheme;
import oui.gui.OuiGISPanel;
import oui.util.dbf.OuiDbfFileParser;

/**
 *
 * @author markstro
 */
public abstract class AbstractMapper {

    public MapPanel mapPanel;
    public OuiGISPanel ouiGisPanel;
    public VectorTheme vt;

    public AbstractMapper(String args[]) {

        // Get the map going
        String title = "OUI";
        mapPanel = new MapPanel();
        WindowFactory.displayInFrame(mapPanel, title);
        vt = loadTheme(args[0]);

        ouiGisPanel = mapPanel.getGisPanel();
        ouiGisPanel.addTheme(vt);
        ouiGisPanel.setActiveThemeDirectly(vt);
        ouiGisPanel.setActiveTheme(vt);
    }

    private VectorTheme loadTheme(String shape_file_name) {
        vt = null;

        try {
            ShpFileParser sfp = new ShpFileParser(shape_file_name + ".shp");
            VectorModel vectorModel = sfp.createModel();

            OuiDbfFileParser dfp = new OuiDbfFileParser(shape_file_name + ".dbf");
            AttributeModel attribute_model = dfp.createModel();
            vt = new VectorTheme(vectorModel, attribute_model);

        } catch (FileNotFoundException e1) {
            System.err.println(e1);
        } catch (Exception e2) {
            System.err.println(e2);
        }
        return vt;
    }

    public abstract void setMapControl(String args[]);
}
