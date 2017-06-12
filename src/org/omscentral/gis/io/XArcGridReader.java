package org.omscentral.gis.io;


/* -------------------------------------------------------------------------
 * The Extended ArcGridReader
 *
 * @(#)XArcGridReader.java @#version[] - @#date[]
 *
 * Author:		Michael Gruebsch
 * changed by Ugo Taddei, 23.3.99
 * Organisation:	Friedrich-Schiller-Universitaet Jena, Germany
 *			Geoinformatik, Institut fuer Geographie
 *
 * -------------------------------------------------------------------------
 */
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;
import java.awt.Dimension;
import java.util.StringTokenizer;

/* -------------------------------------------------------------------------
 * Class definition - ArcGridReader
 *
 */
public class XArcGridReader {

    /* -------------------------------------------------------------------------
     * Class constants (public)
     *
     */
    public static final String[] KEYS = new String[]{
        "NCOLS", "NROWS",
        "XLLCENTER", "YLLCENTER",
        "XLLCORNER", "YLLCORNER",
        "CELLSIZE",
        "NODATA", "NODATA_VALUE"
    };

    /* -------------------------------------------------------------------------
     * Protected class variables
     *
     */
    protected String fileName;

    protected boolean failed = false;

    protected Hashtable<String, Object> header = new Hashtable<>();
    protected int headerLines = 0;
    protected float body[][] = null;

//  protected Rectangle2D bounds = null;
    protected double minimum = Double.POSITIVE_INFINITY;
    protected double maximum = Double.NEGATIVE_INFINITY;
    protected double cellSize;
    protected double xllCorner;
    protected double yllCorner;

    public int nbOfCols;
    public int nbOfRows;

    protected double missingValue;


    /* -------------------------------------------------------------------------
     * Constructors (public)
     *
     */
    public XArcGridReader(String fileName) {
        this.fileName = fileName;
    }


    /* -------------------------------------------------------------------------
     * Methods (private)
     *
     */
    private boolean validKey(String key) {
        for (String KEYS1 : KEYS) {
            if (KEYS1.equals(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean ensureHeader() {
        if ((!failed) && (headerLines == 0)) {
            readHeader();
        }
        return (!failed);
    }

    private boolean ensureBody() {
        if ((!failed) && (body == null)) {
            readBody();
        }
        return (!failed);
    }


    /* -------------------------------------------------------------------------
     * Methods (protected)
     *
     */
    protected void readHeader() {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName));
            boolean readingHeader = true;
            String line;

            while (readingHeader) {
                if ((line = reader.readLine()) != null) {
                    String[] tokens = Tokenizer.tokenize(line, " \t");
                    if (tokens.length == 2) {
                        String key = tokens[0].toUpperCase();
                        if (validKey(key)) {
                            try {
                                if (key.equals("NCOLS") || key.equals("NROWS")) {
                                    header.put(key, Integer.valueOf(tokens[1]));
                                } else {
                                    header.put(key, Double.valueOf(tokens[1]));
                                }
                                headerLines++;
                            } catch (NumberFormatException E) {
                                throw new IllegalArgumentException();
                            }
                        } else {
                            readingHeader = false;
                        }
                    } else {
                        readingHeader = false;
                    }
                } else {
                    readingHeader = false;
                }
            }

            if (((header.containsKey("NCOLS"))
                    && (header.containsKey("NROWS"))
                    && (header.containsKey("XLLCENTER")
                    || header.containsKey("XLLCORNER"))
                    && (header.containsKey("YLLCENTER")
                    || header.containsKey("YLLCORNER"))
                    && (header.containsKey("CELLSIZE"))
                    && (header.containsKey("NODATA")
                    || header.containsKey("NODATA_VALUE")))) {
                nbOfCols = ((Integer) header.get("NCOLS"));
                nbOfRows = ((Integer) header.get("NROWS"));

                cellSize = ((Double) header.get("CELLSIZE"));
                missingValue
                        = (header.containsKey("NODATA"))
                        ? ((Double) header.get("NODATA"))
                        : ((Double) header.get("NODATA_VALUE"));

                xllCorner
                        = (header.containsKey("XLLCORNER"))
                        ? ((Double) header.get("XLLCORNER"))
                        : ((Double) header.get("XLLCENTER")) - cellSize / 2.0;
                yllCorner
                        = (header.containsKey("YLLCORNER"))
                        ? ((Double) header.get("YLLCORNER"))
                        : ((Double) header.get("YLLCENTER")) - cellSize / 2.0;

                /*        bounds = new Rectangle2D
                 (xllCorner,
                 yllCorner,
                 xllCorner + nbOfCols * cellSize,
                 yllCorner + nbOfRows * cellSize);
                 */
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IOException E) {
            failed = true;
            System.err.println("Error reading file " + fileName);
        } catch (IllegalArgumentException E) {
            failed = true;
            System.err.println("Invalid file format " + fileName);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException E) {
            }
        }
    }

    protected void readBody() {
        if (ensureHeader()) {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader(fileName));
                String line;

                for (int i = 0; i < headerLines; i++) {
                    line = reader.readLine();
                }

                body = new float[nbOfCols][nbOfRows];
                double value;

                for (int y = 0; y < nbOfRows; y++) {
                    line = reader.readLine();
                    if (line != null) {
                        String[] tokens = Tokenizer.tokenize(line, " \t");
                        System.err.print(".");
                        if (nbOfCols <= tokens.length) {
                            for (int x = 0; x < nbOfCols; x++) {
                                try {
                                    value = Float.valueOf(tokens[x]);
                                    body[x][y] = (float) value;
                                    if (value != missingValue) {
                                        minimum = Math.min(minimum, body[x][y]);
                                        maximum = Math.max(maximum, body[x][y]);
                                    }
                                } catch (NumberFormatException E) {
                                    throw new IllegalArgumentException();
                                }
                            }
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                System.err.println();
            } catch (IOException E) {
                failed = true;
                body = null;
                System.err.println("Error reading file " + fileName);
            } catch (IllegalArgumentException E) {
                failed = true;
                body = null;
                System.err.println("Invalid file format " + fileName);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException E) {
                }
            }
        }
    }

    public double getCellSize() {
        return (ensureBody()) ? cellSize : 0.0;
    }


    /* -------------------------------------------------------------------------
     * Interface implementation - RasterModel (extended by FPRasterModel)
     *
     */
    public Dimension getRasterSize() {
        return (ensureHeader()) ? new Dimension(nbOfCols, nbOfRows) : null;
    }
    /*
     public final Rectangle2D getBounds()
     {
     return (ensureHeader()) ? bounds : null;
     }
     */

    public final Object getRasterDataAt(int column, int row) {
        return (ensureBody()) ? new Double(body[column][row]) : null;
    }

    public double getXllCorner() {
        return (ensureBody()) ? xllCorner : 0.0;
    }

    public double getYllCorner() {
        return (ensureBody()) ? yllCorner : 0.0;
    }

    /* -------------------------------------------------------------------------
     * Interface implementation - FPRasterModel
     *
     */
    public double getFPDataAt(int column, int row) {
        return (ensureBody()) ? body[column][row] : 0.0;
    }

    public double getMinimum() {
        return (ensureBody()) ? minimum : 0.0;
    }

    public double getMaximum() {
        return (ensureBody()) ? maximum : 0.0;
    }

    public double getMissingValue() {
        return (ensureHeader()) ? missingValue : 0.0;
    }

    /**
     * Get samples with NaN in place of missing values
     *
     * @return
     */
    public float[] getSamples() {
        float[] samples = null;
        if (ensureHeader() && ensureBody()) {
            samples = new float[nbOfCols * nbOfRows];
            int index = nbOfRows * nbOfCols - 1;
            for (int ii = 0; ii < nbOfRows; ii++) {
                for (int jj = nbOfCols - 1; jj > -1; jj--) {

                    samples[index] = (body[jj][ii] == missingValue) ? Float.NaN : (float) body[jj][ii];

                    index--;
                }
            }
        }
        return samples;
    }

    /**
     * Get samples with min in place of missing values
     *
     * @return
     */
    public float[] getMinSamples() {
        float[] samples = null;
        if (ensureHeader() && ensureBody()) {
            samples = new float[nbOfCols * nbOfRows];
            int index = nbOfRows * nbOfCols - 1;
            for (int ii = 0; ii < nbOfRows; ii++) {
                for (int jj = nbOfCols - 1; jj > -1; jj--) {

                    samples[index] = (body[jj][ii] == (float) missingValue) ? (float) minimum : (float) body[jj][ii];

                    index--;
                }
            }
        }
        return samples;
    }

    /**
     * Get DEM as array[][] of float
     *
     * @return
     */
    public float[][] getDemArray() {
        float[][] dem = null;
        if (ensureHeader() && ensureBody()) {
            dem = new float[nbOfCols][nbOfRows];
            for (int y = 0; y < nbOfRows; y++) {
                for (int x = 0; x < nbOfCols; x++) {
                    dem[x][y] = (body[x][y] == (float) missingValue) ? Float.NaN : (float) body[x][y];
                }
            }
    //dem[x][y] = (float) body[x][y];

        }
        return dem;
    }

    /**
     * Get points at line j.
     *
     * @param j
     * @return
     */
    public float[] getMinLine(int j) {
        float[] line = null;
        if (ensureHeader() && ensureBody()) {
            line = new float[nbOfCols];
            for (int y = 0; y < nbOfCols; y++) {
                line[y] = (body[y][j] == (float) missingValue) ? (float) minimum : (float) body[y][j];
            }

        }
        return line;
    } // end of getLine

    /**
     * Get points at line j. Return Float.NaN for missing.
     *
     * @param j
     * @return
     */
    public float[] getLine(int j) {
        float[] line = null;
        if (ensureHeader() && ensureBody()) {
            line = new float[nbOfCols];
            for (int y = 0; y < nbOfCols; y++) {
                line[y] = (body[y][j] == (float) missingValue) ? Float.NaN : (float) body[y][j];
            }

        }
        return line;
    } // end of getLine

    /**
     * Get points at column i.
     *
     * @param i
     * @return
     */
    public float[] getMinColumn(int i) // *****  ???  Doesn't have to invert nOrows -> 0 ?
    {
        float[] col = null;
        if (ensureHeader() && ensureBody()) {
            col = new float[nbOfRows];
            for (int y = 0; y < nbOfRows; y++) {
                col[y] = (body[i][y] == (float) missingValue) ? (float) minimum : (float) body[i][y];
            }

        }
        return col;
    } // end of getColumn

    /**
     * Get points at column i. Return NaN for missing.
     *
     * @param i
     * @return
     */
    public float[] getColumn(int i) // *****  ???  Doesn't have to invert nOrows -> 0 ?
    {
        float[] col = null;
        if (ensureHeader() && ensureBody()) {
            col = new float[nbOfRows];
            for (int y = 0; y < nbOfRows; y++) {
                col[y] = (body[i][y] == (float) missingValue) ? Float.NaN : (float) body[i][y];
            }
        }
        return col;
    } // end of getColumn
}

class Tokenizer extends StringTokenizer {

    public Tokenizer(String str) {
        super(str);
    }

    public Tokenizer(String str, String delimiter) {
        super(str, delimiter);
    }

    public Tokenizer(String str, String delimiter, boolean returnTokens) {
        super(str, delimiter, returnTokens);
    }

    protected static int countRealTokens(String[] tokens, String delimiter) {
        int cnt = (0 < tokens.length) ? 1 : 0;
        for (String token : tokens) {
            if ((1 == token.length()) && (0 <= delimiter.indexOf(token.charAt(0)))) {
                cnt++;
            }
        }
        return cnt;
    }

    protected static String getRealToken(String[] tokens, String delim, int idx) {
        int cnt = 0;
        for (int i = 0; i < tokens.length; i++) {
            boolean isDelimiter
                    = ((1 == tokens[i].length())
                    && (0 <= delim.indexOf(tokens[i].charAt(0))));

            if (cnt == idx) {
                return (isDelimiter) ? "" : tokens[i];
            } else if (isDelimiter) {
                cnt++;
                if ((cnt == idx) && (i == tokens.length - 1)) {
                    return "";
                }
            }
        }
        return null;
    }

    protected static String[] tokenizeStr(String string, String delim, boolean complete) {
        if (string == null) {
            return null;
        } else if (string.length() == 0) {
            return new String[0];
        } else {
            Tokenizer tokenizer = new Tokenizer(string, delim, complete);

            String[] tokens = new String[tokenizer.countTokens()];
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = tokenizer.nextToken();
            }

            if (complete) {
                String[] tokenizedString = new String[countRealTokens(tokens, delim)];
                for (int i = 0; i < tokenizedString.length; i++) {
                    tokenizedString[i] = getRealToken(tokens, delim, i);
                }

                return tokenizedString;
            } else {
                return tokens;
            }
        }
    }

    public static final String[] split(String string, String delimiter) {
        return tokenizeStr(string, delimiter, true);
    }

    public static final String[] split(String string) {
        return split(string, " \t\n\r");
    }

    public static final String[] tokenize(String string, String delimiter) {
        return tokenizeStr(string, delimiter, false);
    }

    public static final String[] tokenize(String string) {
        return tokenize(string, " \t\n\r");
    }

    public static final String join(String[] tokens, String junction) {
        if (tokens == null) {
            return null;
        } else {
            String result = "";
            for (int i = 0; i < tokens.length - 1; i++) {
                result += tokens[i] + junction;
            }
            if (0 < tokens.length) {
                result += tokens[tokens.length - 1];
            }

            return result;
        }
    }

    public static final String join(String[] tokens) {
        return join(tokens, "");
    }
}
