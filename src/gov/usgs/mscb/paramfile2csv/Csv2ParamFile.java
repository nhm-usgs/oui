/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.mscb.paramfile2csv;

import gov.usgs.mscb.csvutils.CsvTableModelAdaptor;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsDefaultParamsReader;
import oui.mms.io.MmsParamsWriter;

/**
 *
 * @author markstro
 */
public class Csv2ParamFile {
    
    public Csv2ParamFile(File srcDir, File parameterFile, File parNameFile) {
        try {        
            MmsDefaultParamsReader dpr = new MmsDefaultParamsReader(parNameFile.getAbsolutePath());
            ParameterSet dps = dpr.read();

            // Get a list of the csv files
            ArrayList<File> fileList = getFileList(srcDir);

            ParameterSet ps = new ParameterSet();
            ps.setFile(parameterFile);
             
            // Set the header
            ps.setDescription("Written by paramfile2csv.Csv2ParamFile");
            ps.setVersion("1.7");
            
            Iterator<File> it = fileList.iterator();
            while (it.hasNext()) {
                File fileArray1 = (File) it.next();

                // 1D or 2D parameter?
                if (fileArray1.getName().contains("_")) {
                    CsvTableModelAdaptor ctma = new CsvTableModelAdaptor(fileArray1.getAbsolutePath());
                    
                    System.out.println("2D: " + fileArray1.getName());

                    String foo = (fileArray1.getName().split("\\."))[0];
                    String[] names = foo.split("_");
                    String dim1 = names[names.length - 2];
                    String dim2 = names[names.length - 1];

                    System.out.println("   dim1 = " + dim1 + " dim2 = " + dim2);
                    
                    Dimension[] dims = new Dimension[2];
                    dims[0] = ps.getDimension(dim1);
                    if (dims[0] == null) {
                        dims[0] = new Dimension(dim1, ctma.getRowCount());
                        ps.addDimension(dims[0]);
                    }
                    
                    dims[1] = ps.getDimension(dim2);
                    if (dims[1] == null) {
                        dims[1] = new Dimension(dim2, ctma.getColumnCount());
                        ps.addDimension(dims[1]);
                    }
                    
                    // Add the 2D parameter
                    String parName = names[0];
                    for (int i = 1; i < names.length - 2; i++) {
                        parName = parName + "_" + names[i];
                    }
                        
                    Object vals;
                    Class type;
                    Parameter dp = dps.getParameter(parName);
                    if (dp == null) {
                        System.out.println("Csv2ParamFile: parameter " + parName
                                + " not found in " + dps.getFileName()
                                + " adding this parameter to " + ps.getFileName()
                                + " as type String (4)");

                        type = String.class;

                        vals = new String[ctma.getRowCount() * ctma.getColumnCount()];
                        int k = 0;
                        for (int r = 0; r < ctma.getRowCount(); r++) {
                            for (int c = 0; c < ctma.getColumnCount(); c++) {
                                ((String[]) (vals))[k++] = ctma.getValueAt(r, c).toString();
                            }
                        }

                    } else {
                        if (dp.getType() == Integer.class) {
                            vals = new int[ctma.getRowCount() * ctma.getColumnCount()];
                            int k = 0;
                            for (int r = 0; r < ctma.getRowCount(); r++) {
                                for (int c = 0; c < ctma.getColumnCount(); c++) {
                                    ((int[]) (vals))[k++] = Integer.parseInt(ctma.getValueAt(r, c).toString());
                                }
                            }
                            type = Integer.class;

                        } else if (dp.getType() == Float.class) {
                            vals = new float[ctma.getRowCount() * ctma.getColumnCount()];
                            int k = 0;
                            for (int r = 0; r < ctma.getRowCount(); r++) {
                                for (int c = 0; c < ctma.getColumnCount(); c++) {
                                    ((float[]) (vals))[k++] = Float.parseFloat(ctma.getValueAt(r, c).toString());
                                }
                            }
                            type = Float.class;

                        } else if (dp.getType() == Double.class) {
                            vals = new double[ctma.getRowCount() * ctma.getColumnCount()];
                            int k = 0;
                            for (int r = 0; r < ctma.getRowCount(); r++) {
                                for (int c = 0; c < ctma.getColumnCount(); c++) {
                                    ((double[]) (vals))[k++] = Double.parseDouble(ctma.getValueAt(r, c).toString());
                                }
                            }
                            type = Double.class;

                        } else {
                            vals = new String[ctma.getRowCount() * ctma.getColumnCount()];
                            int k = 0;
                            for (int r = 0; r < ctma.getRowCount(); r++) {
                                for (int c = 0; c < ctma.getColumnCount(); c++) {
                                    ((String[]) (vals))[k++] = ctma.getValueAt(r, c).toString();
                                }
                            }
                            type = String.class;
                        }
                    }

                    ps.addParameter(new Parameter(parName, 0, dims, type, vals));

                } else {

                    // 1D parameters
                    CsvTableModelAdaptor ctma = new CsvTableModelAdaptor(fileArray1.getAbsolutePath());
                    String dimName = (fileArray1.getName().split("\\."))[0];

                    Dimension dim = ps.getDimension(dimName);
                    if (dim == null) {
                        dim = new Dimension(dimName, ctma.getRowCount());
                        ps.addDimension(dim);
                    }
                    
                    Dimension[] dims = new Dimension[1];
                    dims[0] = dim;
                    for (int c = 1; c < ctma.getColumnCount(); c++) {
                        String parName = ctma.getColumnName(c);
                        
                        Object vals;
                        Class type;
                        Parameter dp = dps.getParameter(parName);
                        if (dp == null) {
                            System.out.println("Csv2ParamFile: parameter " + parName
                                    + " not found in " + dps.getFileName()
                                    + " adding this parameter to " + ps.getFileName()
                                    + " as type String (4)");
                            
                            type = String.class;

                            vals = new String[ctma.getRowCount()];
                            for (int r = 0; r < ctma.getRowCount(); r++) {
                                ((String[]) (vals))[r] = ctma.getValueAt(r, c).toString();
                            }

                        } else {
                            if (dp.getType() == Integer.class) {
                                vals = new int[ctma.getRowCount()];
                                for (int r = 0; r < ctma.getRowCount(); r++) {
                                    ((int[]) (vals))[r] = Integer.parseInt(ctma.getValueAt(r, c).toString());
                                }
                                type = Integer.class;

                            } else if (dp.getType() == Float.class) {
                                vals = new float[ctma.getRowCount()];
                                for (int r = 0; r < ctma.getRowCount(); r++) {
                                    ((float[]) (vals))[r] = Float.parseFloat(ctma.getValueAt(r, c).toString());
                                }
                                type = Float.class;
                         
                            } else if (dp.getType() == Double.class) {
                                vals = new double[ctma.getRowCount()];
                                for (int r = 0; r < ctma.getRowCount(); r++) {
                                    ((double[]) (vals))[r] = Double.parseDouble(ctma.getValueAt(r, c).toString());
                                }
                                type = Double.class;
                                
                            } else {
                                vals = new String[ctma.getRowCount()];
                                for (int r = 0; r < ctma.getRowCount(); r++) {
                                    ((String[]) (vals))[r] = ctma.getValueAt(r, c).toString();
                                }
                                type = String.class;
                            }
                        }
                        
                        ps.addParameter(new Parameter(parName, 0, dims, type, vals));
                    }
                }
            }
            
            MmsParamsWriter.write(parameterFile.getAbsolutePath(), ps);
            
        } catch (IOException ex) {
            Logger.getLogger(Csv2ParamFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File srcDir = new File(args[0]);
        File parameterFile = new File(args[1]);
        File parNameFile = new File(args[2]);
        
        Csv2ParamFile csv2ParamFile = new Csv2ParamFile(srcDir, parameterFile,
                parNameFile);
    }
    
    private ArrayList<File> getFileList(File folder) {
        String files;
        File[] listOfFiles = folder.listFiles();
        ArrayList<File> list = new ArrayList();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                files = listOfFile.getName();
                if (files.endsWith(".csv") || files.endsWith(".CSV")) {
                    list.add(listOfFile);
                }
            }
        }
        return list;
    }
}
