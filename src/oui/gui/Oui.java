/*
 * Oui.java
 *
 * Created on November 13, 2002, 2:11 PM
 */
package oui.gui;

import gov.usgs.cawsc.apps.GuiProgram;
import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.util.logging.*;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import oui.mms.OuiProjectXml;

/**
 * The top class for the Object User Interface. Refer to <I>The Object User Interface (OUI):
 * User's Manual</I> for more information.
 *
 * @author markstro
 * @author John M. Donovan
 * @version 2.0
 */
public class Oui extends GuiProgram {

    private static final Logger logger = Logger.getLogger(Oui.class.getName());
    public static Level ouiLogLevel = Level.OFF;

    public Oui(String xml_file_name) {
        super("OUI");

        preferences = Preferences.userNodeForPackage(Oui.class);

        String[] vals = {xml_file_name, VersionInfo.getValForTok("SHA"),
            VersionInfo.getValForTok("ORIGIN"), VersionInfo.getValForTok("TAG"),
            VersionInfo.getValForTok("COMMITDATE"), VersionInfo.getValForTok("COMPILER"),
            VersionInfo.getValForTok("OS_Version"), VersionInfo.getValForTok("BUILDER"),
            VersionInfo.getValForTok("BUILDDATE")
        };

        logger.log(Level.INFO, "Oui started with {0}\n  SHA = {1}\n  ORIGIN = {2}\n  "
                + "TAG = {3}\n  COMMITDATE = {4}\n  COMPILER = {5}\n  OS_Version = {6}\n  "
                + "BUILDER = {7}\n  BUILDDATE = {8}", vals);

        OuiProjectXml.OuiProjectXmlFactory(xml_file_name);

        OuiGui gui = new OuiGui();
        JFrame frame = WindowFactory.instance().getMainFrame();
        WindowFactory.instance().packWindowWith(null, gui, frame);
        WindowFactory.instance().displayInWindow(gui);
    }

    /**
     * Main function for OUI
     *
     * @param args args[0] is the project XML file<br>
     */
    public static void main(String[] args) {
        try {
            Handler fh = new FileHandler("oui.log");
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);

            if (args.length == 1) {
                Oui oui = new Oui(args[0]);
            } else {
                System.out.println("Usage: java -jar oui.jar <project.xml>");
            }
        } catch (IOException ex) {
            Logger.getLogger(Oui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Oui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
