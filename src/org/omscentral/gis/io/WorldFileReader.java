package org.omscentral.gis.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.IOException;

/**
 * Description of the Class
 *
 * @author en
 * @created June 19, 2001
 */
public class WorldFileReader {

    /**
     * Gets the Matrix attribute of the WorldFileReader class
     *
     * @param stream Description of Parameter
     * @return The Matrix value
     */
    public static double[] getMatrix(InputStream stream) {

        int i = 0;
        double val[] = new double[6];
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            try (LineNumberReader lr = new LineNumberReader(in)) {
                String line = lr.readLine();
                while ((line != null) && i < 6) {
                    if (!line.equals("")) {
                        val[i++] = Double.valueOf(line.trim());
                    }
                    line = lr.readLine();
                }
            }
            if (i != 6) {
                System.err.println("Error: Wrong element count in world file format.");
                return null;
            }
        } catch (NumberFormatException nfe) {
            System.err.println("Error: Wrong world file format.");
            return null;
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            return null;
        }
        return val;
    }
}

