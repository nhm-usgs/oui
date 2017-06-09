/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.mapper.variableMap;

import gov.usgs.cawsc.gui.WindowFactory;
import oui.mapper.AbstractMapper;
import oui.mms.datatypes.SpaceTimeSeriesData;

/**
 *
 * @author markstro
 */
public class VariableMapper extends AbstractMapper {
    public VariableMapper(String args[]) {
        super (args);
        setMapControl (args);        
    }
            
    public static void main(String args[]) {
        
        // This is for testing
//        String args[] = new String[4];
//        args[0] = "C:/markstro/people/koczot3/mapperDemos/maps/MERCED_HRUS_FINAL_05072012";
//        args[1] = "HRU";
//        args[2] = "C:/markstro/people/koczot3/mapperDemos/output/MERCED_prms3_singleRun.ani.nhru";
//        args[3] = "pkwater_equiv";
    
        new VariableMapper (args);
    }

    @Override
    public final void setMapControl(String args[]) {
        int numberOfBins = 10;
        SpaceTimeSeriesData tsd = new SpaceTimeSeriesData(args[2]);
        WindowFactory.instance().setWindowTitle(mapPanel, "PRMS Variable Mapper: " + args[3]);
        VariableMapGui vmg = new VariableMapGui(tsd, ouiGisPanel, vt,
                args[1], args[3], numberOfBins, tsd.getZoneCount());
        ouiGisPanel.setLabelIndexDirectly(vt.getIdIndex(args[1]));
        
        String title = "PRMS Variable Mapper";
        WindowFactory.displayInFrame(vmg, title);
    }
}
