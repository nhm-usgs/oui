/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.mscb.gdp;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author markstro
 */
public class GdbCsvReader {

    private int ndate;
    private int nhru;
    private String units;
    private String description;
    private String fileName;
    private int[] ids;
    private Date[] dates;
    private float[][] values;
    
    public int getNdate() {
        return ndate;
    }

    public int getNhru() {
        return nhru;
    }

    public String getUnits() {
        return units;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public int[] getIds() {
        return ids;
    }

    public Date[] getDates() {
        return dates;
    }

    public float[][] getValues() {
        return values;
    }

    public GdbCsvReader(File f) throws IOException, ParseException {
        this(f.getPath());
    }

    public GdbCsvReader(String path) throws IOException, ParseException,
            NumberFormatException {
        this.fileName = path;

        // get the sizes
        ndate = lineCount(path) - 3;
        columnCount(path);

        // allocate arrays
        values = new float[ndate][nhru];
        dates = new Date[ndate];

        String line = null;
        BufferedReader br = null;
        int c = -1;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "Cp1252"));


            line = br.readLine();
            line = br.readLine();
            line = br.readLine();

            String[] split;
//        1979-01-01T00:00:00Z
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            int r = 0;
            while ((line = br.readLine()) != null) {
                split = line.split(",");
                dates[r] = sdf.parse(split[0]);

                for (c = 0; c < nhru; c++) {
                    values[r][c] = Float.parseFloat(split[c + 1]);
                }
                r++;
            }
        } catch (IOException | NumberFormatException | ParseException e) {
            System.out.println(this.getClass().toString() + ": Problem parsing file " + path);
            System.out.println("line = " + line);
            System.out.println("token number " + (c+1));
            throw (e);
        } finally {
            if (br != null) {
                br.close();
            }
        }

    }

    private static int lineCount(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if (endsWithoutNewLine) {
                ++count;
            }
            return count;
        }
    }

    private void columnCount(String path) throws UnsupportedEncodingException,
            FileNotFoundException, IOException {

        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(path), "Cp1252"));
            line = br.readLine();
            description = line.substring(2);

            line = br.readLine();
            String[] split = line.split(",");
            nhru = split.length - 1;
            ids = new int[nhru];
            for (int i = 1; i < split.length; i++) {
                ids[i - 1] = Integer.parseInt(split[i]);
            }

            line = br.readLine();
            split = line.split(",");
            units = split[1];

        } catch (NumberFormatException e) {
            System.out.println ("gov.usgs.mows.gdp.GdbCsvReader.columnCount()");
            System.out.println ("Trying to parse integer IDs from file = " + fileName);
            System.out.println ("Choking on line = " + line);
            System.exit(1);
            
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static void main(String[] args) {
        File f = new File("C:/work/csvTests/solradTest.csv");
        try {
            GdbCsvReader gcr = new GdbCsvReader(f);
            
            System.out.println (gcr.getDescription() + " " + gcr.getUnits());
            System.out.println (gcr.getNdate() + " " + gcr.getNhru());
            System.out.println (gcr.getFileName());
        } catch (IOException | ParseException ex) {
            Logger.getLogger(GdbCsvReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
