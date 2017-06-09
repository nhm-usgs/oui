/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.util.pf2snt;

import csvutils.CsvWriter;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsParamsReader;

/**
 *
 * @author markstro
 */
public class ParamFile2Csv {

    public ParamFile2Csv(File parameterFile, File destDir) {
        
        try {
            // See if the destination dir exists
            if (!destDir.canWrite()) {
                System.out.println("ParamFile2Csv: not able to write to the destination directory: " + destDir.toString());
                System.exit(1);
            }
            
            // Write a README file in the destination directory
            writeReadmeFile (parameterFile, destDir, "README.txt");
            
            // Copy the original parameter file into the destination directory
            File copyFile = new File (destDir, parameterFile.getName());
            copyFileUsingFileStreams(parameterFile, copyFile);

            // Read the PRMS parameter file
            MmsParamsReader mpr = new MmsParamsReader(parameterFile.toString());
            ParameterSet ps = mpr.read();
            
            // Write the cvs files for each dimension
            Iterator<String> dimensionNames = ps.getDimensionNames();
            while (dimensionNames.hasNext()) {
                writeParameterValuesForDimension(dimensionNames.next(), ps, destDir);
            }
            
            // Write the multidimensional csv files.
            TreeSet combos = ps.get2DCombos();
            Object[] comboNames = combos.toArray();
            for (Object comboName : comboNames) {
//                System.out.println ("Combo = " + comboNames[i].toString());
                ArrayList paramsFor2DDim = ps.getParamsFor2DDim(comboName.toString());
                Iterator it = paramsFor2DDim.iterator();
                while (it.hasNext()) {
                    Parameter par = (Parameter) it.next();
//                    System.out.println("   param = " + par.getName());
                    writeValuesFor2Dparams(par, destDir);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(ParamFile2Csv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File parameterFile = new File (args[0]);
        File destDir = new File (args[1]);
        ParamFile2Csv paramFile2Csv = new ParamFile2Csv(parameterFile, destDir);
        SntempParametersFromPrms sntempParametersFromPrms = new SntempParametersFromPrms(destDir);
    }

    private void writeReadmeFile(File parameterFile, File destDir, String readmEtxt) {
        PrintWriter out = null;
        try {
            File readmeFile = new File(destDir, readmEtxt);
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(readmeFile));
            out = new PrintWriter(osw);
            out.println("The csv files in this direcotry have been written by ParamFile2Csv");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            out.println("on " + dateFormat.format(date));
            out.println("from PRMS Parameter File " + parameterFile);
            out.println("The file " + parameterFile.getName());
            out.println("in this directory is a copy of that file at the time this program was run.");

        } catch (FileNotFoundException e) {
            Logger.getLogger(ParamFile2Csv.class.getName()).log(Level.SEVERE, null, e);

        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void writeParameterValuesForDimension(String dimName, ParameterSet ps,
            File destDir) throws IOException {
        
        Dimension dim = ps.getDimension(dimName);
        Set paramsForDim = ps.getParamsForDim(dim);
        Object[] paramArray = paramsForDim.toArray();

        String fileName = dimName + ".csv";
//                System.out.println ("writing " + fileName);
        File outFile = new File(destDir, fileName);
        CsvWriter writer = new CsvWriter(outFile.toString(), ',', Charset.forName("ISO-8859-1"));

        // These are the parameter names for this dimension
        String[] headers = new String[paramArray.length + 1];
        headers[0] = dimName + "_ID";
        for (int j = 1; j < headers.length; j++) {
            Parameter param = (Parameter) paramArray[j - 1];
            headers[j] = param.getName();
        }
        writer.writeRecord(headers);

        int rowCount = dim.getSize();  // ie number of HRUs
        int colCount = headers.length;  // is number of parameters for this dimension + 1 (for the ID)
        String[] vals = new String[colCount];
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                if (c == 0) {
                    vals[c] = "" + (r + 1);

                } else {
                    Parameter param = (Parameter) paramArray[c - 1];
//                            System.out.println ("   parameter " + param.getName());

                    if (param.getType() == Integer.class) {
                        int[] foo = (int[]) param.getVals();
                        vals[c] = Integer.toString(foo[r]);

                    } else if (param.getType() == Float.class) {
                        float[] foo = (float[]) param.getVals();
                        vals[c] = Float.toString(foo[r]);

                    } else if (param.getType() == Double.class) {
                        double[] foo = (double[]) param.getVals();
                        vals[c] = Double.toString(foo[r]);

                    } else { // type == String
                        String[] foo = (String[]) param.getVals();
                        vals[c] = foo[r];
                    }
                }
            }
            // write column by column
            writer.writeRecord(vals);
        }
        writer.close();
    }

    private void writeValuesFor2Dparams(Parameter param, File destDir) throws IOException {
        Dimension dim1 = param.getDimension(0);
        Dimension dim2 = param.getDimension(1);

        String fileName = param.getName() + "_" + dim1.getName() + "_" + dim2.getName() + ".csv";
//                System.out.println ("writing " + fileName);
        File outFile = new File(destDir, fileName);
        CsvWriter writer = new CsvWriter(outFile.toString(), ',', Charset.forName("ISO-8859-1"));

        // These are the parameter names for this dimension
        String[] headers = new String[dim2.getSize()];
        for (int j = 0; j < headers.length; j++) {
            headers[j] = "" + (j + 1);
        }
        
        writer.writeRecord(headers);

        int rowCount = dim1.getSize();
        int colCount = dim2.getSize();
        String[] vals = new String[colCount];
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                    int index = (c * rowCount) + r;
                    
                    if (param.getType() == Integer.class) {
                        int[] foo = (int[]) param.getVals();
                        vals[c] = Integer.toString(foo[index]);

                    } else if (param.getType() == Float.class) {
                        float[] foo = (float[]) param.getVals();
                        vals[c] = Float.toString(foo[index]);

                    } else if (param.getType() == Double.class) {
                        double[] foo = (double[]) param.getVals();
                        vals[c] = Double.toString(foo[index]);

                    } else { // type == String
                        String[] foo = (String[]) param.getVals();
                        vals[c] = foo[index];
                    }
            }
            // write column by column
            writer.writeRecord(vals);
        }
        writer.close();       
    }
    
    private static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }
}
