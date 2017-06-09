/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.cawsc.apps;

import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.prefs.Preferences;

/**
 *
 * @author jmd
 */
public class ApplicationProgram {
    
    /** Stores preferences that can be used the next time the program runs. */
    public Preferences preferences = null;

    public ApplicationProgram() {
        initializePreferencesSystem();        
    }
      
    /**
     * Initialize the Java Preferences system. The first time it is accessed, it will display a
     * needlessly alarming error message. Hide this message from the user.
     */
    public void initializePreferencesSystem()
    {
        PrintStream errStream = System.err;
        System.setErr(new PrintStream(new PipedOutputStream()));
        Preferences.userNodeForPackage(ApplicationProgram.class);
        System.setErr(errStream);
        preferences = Preferences.userNodeForPackage(this.getClass());
    }
}
