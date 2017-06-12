package org.omscentral.gis.io;

import java.io.*;
import org.omscentral.gis.model.*;
import java.awt.*;
import java.awt.geom.*;

public class ArcGridWriter {
    public final static String[] KEYS = new String[]{
        "NCOLS", "NROWS",
        "XLLCORNER", "YLLCORNER",
        "CELLSIZE",
        "NODATA"
    };

    public static void write(File f, RasterModel m) throws IOException {
        try (PrintWriter writer = new PrintWriter(
                new BufferedOutputStream(new FileOutputStream(f))
        )) {
            writeHeader(writer, m);
            writeBody(writer, m);

            writer.flush();
        }
    }

    private static void writeHeader(Writer writer, RasterModel m) throws IOException {
        Dimension size = m.getRasterSize();
        Rectangle2D bounds = m.getBounds();
        writeHeaderLine(writer, KEYS[0], size.width + "");
        writeHeaderLine(writer, KEYS[1], size.height + "");
        writeHeaderLine(writer, KEYS[2], bounds.getX() + "");
        writeHeaderLine(writer, KEYS[3], bounds.getY() + "");
        writeHeaderLine(writer, KEYS[4], m.getCellSize().toString());
        writeHeaderLine(writer, KEYS[5], m.getMissingValue().toString());
    }

    private static void writeHeaderLine(Writer writer, String entry, String value) throws IOException {
        writer.write(entry + "\t" + value + "\n");
    }

    private static void writeBody(Writer writer, RasterModel m) throws IOException {
        Dimension size = m.getRasterSize();
        for (int y = 0; y < size.getHeight(); y++) {
            for (int x = 0; x < size.getWidth(); x++) {
                writer.write(m.getRasterDataAt(x, y).toString());
                writer.write(" ");
            }
            writer.write("\n");
        }
    }
}
