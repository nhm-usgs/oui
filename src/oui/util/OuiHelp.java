/*
 * OuiHelp.java
 *
 * Created on January 11, 2007, 9:50 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oui.util;

import java.awt.Color;
import java.net.URL;
import javax.help.DefaultHelpModel;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.help.TextHelpModel;
import javax.swing.JFrame;

/**
 * This class displays JavaHelp HelpSets for OUI.
 */

public class OuiHelp {
    
    private static JFrame frame;
    
    // The initial width and height of the frame
    public static int WIDTH = 645;
    public static int HEIGHT = 495;
    private static JHelp jh = null;
    private HelpSet hs;
    private static OuiHelp help = null;
    
    public static OuiHelp OuiHelpFactory () {
        if (help == null) help = new OuiHelp ();
        return help;
    }
    
    private OuiHelp() {
        // Find the HelpSet file and create the HelpSet object:
        String helpHS = "HolidayHistory.hs";
        ClassLoader cl = this.getClass().getClassLoader();
        try {
            URL hsURL = HelpSet.findHelpSet(cl, helpHS);
            hs = new HelpSet(null, hsURL);
        } catch (Exception ee) {
            // Say what the exception really is
            System.out.println( "HelpSet " + ee.getMessage());
            System.out.println("HelpSet "+ helpHS +" not found");
        }
    }
    
    public void showTopic (String topic) {
        if (jh == null) {
            jh = new JHelp(hs);
        } else {
            jh.setHelpSetPresentation(hs.getDefaultPresentation());
            DefaultHelpModel m = new DefaultHelpModel(hs);
            jh.setModel(m);
        }
        createFrame(hs.getTitle());
        launch();
    }
            
    private String title = "";
    public void setTitle(String s) {title = s;}
    public String getTitle() {return title;}
    
    protected JFrame createFrame(String title) {
        if (jh == null) return null;
        if (title == null || title.equals("")) {
            TextHelpModel m = jh.getModel();
            HelpSet hs = m.getHelpSet();
            String hsTitle = hs.getTitle();
            if (hsTitle == null || hsTitle.equals("")) {
                setTitle("Unnamed HelpSet"); // maybe based on HS?
            } else {
                setTitle(hsTitle);
            }
        } else {
            setTitle(title);
        }
        if (frame == null) {
            frame = new JFrame(getTitle());
            frame.setSize(WIDTH, HEIGHT);
            frame.setForeground(Color.black);
            frame.setBackground(Color.lightGray);
            frame.getContentPane().add(jh);	// the JH panel

        } else {
            frame.setTitle(getTitle());
        }
        frame.pack();
        return frame;
    }
    
    protected void launch() {
        if (frame == null) return;
        frame.setVisible(true);
    }
}