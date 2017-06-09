package org.omscentral.gis.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Hashtable;
import java.util.NoSuchElementException;

import java.awt.Dimension;

import java.awt.geom.Rectangle2D;

import org.omscentral.gis.model.FPRasterModel;
import org.omscentral.gis.model.DefaultFPRasterModel;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class ArcGridReader {

  /**
   * Description of the Field
   */
  public final static String[] KEYS = new String[]
      {
      "NCOLS", "NROWS",
      "XLLCENTER", "YLLCENTER",
      "XLLCORNER", "YLLCORNER",
      "CELLSIZE",
      "NODATA", "NODATA_VALUE"
      };

  String file;


  /**
   * Constructor for the ArcGridReader object
   *
   * @param fileName         Description of Parameter
   * @exception IOException  Description of Exception
   */
  public ArcGridReader(String fileName) throws IOException {
    if (!fileName.endsWith(".dat"))
      throw new java.lang.IllegalArgumentException("Reader must be created" +
      "with filename ending in .dat");
    file = fileName;
  }

  public FPRasterModel createModel() throws IOException {
    return createModel(file);
  }


  public static FPRasterModel createModel(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    FPRasterModel model;
    try {
      model = read(reader);
    }
    finally {
      reader.close();
    }
    return model;
  }

  public static FPRasterModel createModel(String filename) throws IOException {
    return createModel(new FileInputStream(filename));
  }




  /**
   * Description of the Method
   *
   * @param reader           Description of Parameter
   * @exception IOException  Description of Exception
   */
  protected static FPRasterModel read(BufferedReader reader) throws IOException {
    Hashtable<String,Object> header = new Hashtable<String,Object>();
    boolean readingHeader = true;
    int row = 0;
    int nbOfRows = 0,nbOfCols = 0;
    String line;
    double value, missing = 0;
    DefaultFPRasterModel model = null;
    double[][] body = null;


    while (((line = reader.readLine()) != null)
         && ((readingHeader) || (row < nbOfRows))) {
      if (readingHeader) {
        String[] tokens = tokenize(line, " \t");
        String key = (0 < tokens.length) ? tokens[0].toUpperCase() : null;
        if ((tokens.length == 2) && (validKey(key))) {
          try {
            if (key.equals("NCOLS") || key.equals("NROWS")) {
              header.put(key, Integer.valueOf(tokens[1]));
            }
            else {
              header.put(key, Double.valueOf(tokens[1]));
            }
          }
          catch (NumberFormatException E) {
            throw new IllegalArgumentException();
          }
        }
        else {
          readingHeader = false;

          if (((header.containsKey("NCOLS"))
               && (header.containsKey("NROWS"))
               && (header.containsKey("XLLCENTER")
               || header.containsKey("XLLCORNER"))
               && (header.containsKey("YLLCENTER")
               || header.containsKey("YLLCORNER"))
               && (header.containsKey("CELLSIZE"))
               && (header.containsKey("NODATA")
               || header.containsKey("NODATA_VALUE")))) {
            nbOfCols = ((Integer) header.get("NCOLS")).intValue();
            nbOfRows = ((Integer) header.get("NROWS")).intValue();

            double cellSize = ((Double) header.get("CELLSIZE")).doubleValue();
            missing =
                (header.containsKey("NODATA"))
                 ? ((Double) header.get("NODATA")).doubleValue()
                 : ((Double) header.get("NODATA_VALUE")).doubleValue();

            double xllCorner =
                (header.containsKey("XLLCORNER"))
                 ? ((Double) header.get("XLLCORNER")).doubleValue()
                 : ((Double) header.get("XLLCENTER")).doubleValue() - cellSize / 2.0;
            double yllCorner =
                (header.containsKey("YLLCORNER"))
                 ? ((Double) header.get("YLLCORNER")).doubleValue()
                 : ((Double) header.get("YLLCENTER")).doubleValue() - cellSize / 2.0;

            model =
              new DefaultFPRasterModel(
                nbOfCols,nbOfRows,xllCorner,yllCorner,cellSize,missing
              );
            body = new double[nbOfCols][nbOfRows];
          }
          else {
            throw new IllegalArgumentException("Bad File headers");
          }
        }
      }

      if ((!readingHeader) && (row < nbOfRows)) {
        java.util.StringTokenizer tokenizer =
            new java.util.StringTokenizer(line, " \t");
        for (int x = 0; x < nbOfCols; x++) {
          try {
            body[x][row] = value =
                Double.valueOf(tokenizer.nextToken()).doubleValue();
            if (value != missing) {
              model.setMin(Math.min(model.getMinimum().doubleValue(), value));
              model.setMax(Math.max(model.getMaximum().doubleValue(), value));
            }
          }
          catch (NumberFormatException E) {
            throw new IllegalArgumentException();
          }
          catch (NoSuchElementException E) {
            throw new IllegalArgumentException();
          }
        }

        row++;
      }
      // if ( ! readingHeader)

    }
    // while ((line = reader.readLine()) != null)

    if (row < nbOfRows) {
      throw new IllegalArgumentException();
    }

    model.setData(body);

    return model;
  }




  /**
   * Description of the Method
   *
   * @param string  Description of Parameter
   * @param delim   Description of Parameter
   * @return        Description of the Returned Value
   */
  private static String[] tokenize(String string, String delim) {
    if (string == null) {
      return null;
    }
    else if (string.length() == 0) {
      return new String[0];
    }
    else {
      java.util.StringTokenizer tokenizer =
          new java.util.StringTokenizer(string, delim);

      String[] tokens = new String[tokenizer.countTokens()];
      for (int i = 0; i < tokens.length; i++) {
        tokens[i] = tokenizer.nextToken();
      }

      return tokens;
    }
  }



  /**
   * Description of the Method
   *
   * @param key  Description of Parameter
   * @return     Description of the Returned Value
   */
  private static boolean validKey(String key) {
    for (int i = 0; i < KEYS.length; i++) {
      if (KEYS[i].equals(key)) {
        return true;
      }
    }
    return false;
  }

  /**
   *
   */
  static public void main(String[] args) throws IOException {
    ArcGridReader.createModel("/home/en/.scfigis/workspace/east-elv");
  }

}

