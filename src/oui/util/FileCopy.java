/*
 * FileCopy.java
 *
 * Created on July 14, 2004, 1:23 PM
 */

package oui.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

//import java.nio.channels.*;
//import java.io.*;

/**
 *
 * @author  markstro
 */
public class FileCopy {
    
    public void copyFile (String in_name, String out_name) throws Exception {
        copyFile (new File (in_name), new File (out_name));
    }

    public void copyFile (File inf, File outf) throws Exception {
        
        /*
         *  THis stuff stopped working!
         **/
//        FileChannel sourceChannel = new FileInputStream (in).getChannel ();
//        FileChannel destinationChannel = new FileOutputStream (out).getChannel ();
//        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
//// or
////  destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
//        sourceChannel.close ();
//        destinationChannel.close ();
         
        
        FileReader in = new FileReader(inf);
        FileWriter out = new FileWriter(outf);
        int c;

        while ((c = in.read()) != -1)
           out.write(c);

        in.close();
        out.close();
    }
    
    public static void main(String args[]){
        try {
            FileCopy j = new FileCopy();
            j.copyFile("/home/projects/oui/rio_grande/riogr_mms_work/input/data/RioGrande.xprms_xyz.data", "/home/projects/oui/rio_grande/riogr_mms_work/input/data/foo.data");
//            j.copyFile("/home/projects/oui/rio_grande/riogr_mms_work/input/data/crap.data", "/home/projects/oui/rio_grande/riogr_mms_work/input/data/foo.data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
