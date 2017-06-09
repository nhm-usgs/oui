/*
 * StdErrPrinter.java
 *
 * Created on August 26, 2005, 9:48 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package oui.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author markstro
 */
public class StdErrPrinter extends Thread {
    private Process process;
    
    public StdErrPrinter (Process process) {
        this.process = process;
    }
    
    public void run() {
        try{
            BufferedReader err_in = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            String line;
            while(!isInterrupted() &&
                    ((line = err_in.readLine()) != null))
                System.err.println(line);
            process.getErrorStream().close();
        } catch(IOException e) { e.printStackTrace(); }
        System.out.println("StdErrPrinter: leaving run");
    }
}
