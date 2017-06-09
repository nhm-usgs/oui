/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author markstro
 */
public class VersionInfo {
    final static String props = "version.properties";

    public static final String getValForTok(String propToken) {
        String msg = "";
        Properties prop = new Properties();
        InputStream input;

        try {
            input = Oui.class.getClassLoader().getResourceAsStream(props);
            prop.load(input);
            msg = prop.getProperty(propToken);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VersionInfo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VersionInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msg;
    }
    
    public static void main(String[] args) {
        System.out.println ("BUILD token is " + getValForTok("BUILD"));
    }
}
