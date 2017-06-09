/*
 * MmsRunner.java
 *
 * Created on August 4, 2005, 11:43 AM
 */

package oui.mms.gui;

/**
 *
 * @author markstro
 */
public class MmsRunner {
    public static void main(String[] args) {
        //        System.out.println (System.getProperty("user.dir"));
        Mms mms = new Mms("./control/YUBA_PRMS4.control");
//        new Mms("gsflow_gui.control");
    }
}
