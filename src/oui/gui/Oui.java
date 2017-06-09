/*
 * Oui.java
 *
 * Created on November 13, 2002, 2:11 PM
 */
package oui.gui;

import gov.usgs.cawsc.apps.GuiProgram;
import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.WindowFactory;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
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

        logger.log(Level.INFO, "Oui-JMD started with {0}", xml_file_name);
        OuiProjectXml.OuiProjectXmlFactory(xml_file_name);

        OuiGui gui = new OuiGui();

        // Read the preferred window size from the prefs.
//        Rectangle rect = new Rectangle(0, 0, 1000, 700);
//        rect.x = preferences.getInt("x", rect.x);
//        rect.y = preferences.getInt("y", rect.y);
//        rect.width = preferences.getInt("width", rect.width);
//        rect.height = preferences.getInt("height", rect.height);
//
//        GuiUtilities.keepWindowInBounds(rect);

        JFrame frame = WindowFactory.instance().getMainFrame();
        WindowFactory.instance().packWindowWith(null, gui, frame);
        WindowFactory.instance().displayInWindow(gui);
//        frame.add(gui);
//        frame.setJMenuBar(gui.getMenuBar());
//        frame.pack();
//        frame.setBounds(rect);
//        frame.setVisible(true);

        //gui.setSize(gui.getPreferredSize());
        //gui.setVisible(true);
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
