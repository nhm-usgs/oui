/*
 * EspToolRunner.java
 *
 * Created on October 18, 2005, 3:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package oui.esptool;

import gov.usgs.cawsc.gui.WindowFactory;

/**
 *
 * @author markstro
 */
public class EspToolRunner {

    public static void main(String args[]) {
        EspTool et = new EspTool("d:/oui_projects/rio_grande/riogr_mms_work/output/esp/LittleNavajo.xml", "basin_cfs.strmflow 1");
        WindowFactory.displayInFrame(et, "OUI ESP Tool");

//        EspTool et1 = new EspTool("/home/mms_workspaces/heihe_work/output/ESP.init.statvar", "/home/mms_workspaces/heihe_work/output/esp.series", "basin_cms.strmflow 1", "Heihe");
//        et1.setVisible(true);
    }
}
