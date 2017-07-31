package oui.mms.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

public class MmsDefaultParamsReader {
    private final FileReader file;
    private final String fileName;
    private ParameterSet mps;
    
    public MmsDefaultParamsReader(String fileName) throws IOException {
        if (fileName != null) {
            this.file = new FileReader(fileName);
        } else {
            this.file = null;
        }
        this.fileName = fileName;
    }
    
    public ParameterSet read() throws IOException {

        if (file == null) {
            mps = null;
            return null;
        }

        mps = new ParameterSet();
        mps.setFileName(fileName);
//        System.out.println ("MmsDefaultParamsReader reading " + fileName);
        
/*
 *  Read dimensions
 */
        
        String line;
        BufferedReader in = null;
        String name = null;

        try {
            in = new BufferedReader(file);
            
            line = in.readLine();  // Program MMS
            
            while (!line.contains("DIMENSIONS")) {
                line = in.readLine();
            }
            line = in.readLine();
            line = in.readLine();
            
/*
 *  Read Dimensions
 */
            while (line.startsWith("Name  :")) {
                name = line.substring(8);
//                System.out.println ("MmsDefaultParamsReader: dimension name = " + name);
                line = in.readLine();
                String value = line.substring(8);
                int size = Integer.parseInt(value);
                line = in.readLine();
                String desc = line.substring(8);
                
                Dimension dim = new Dimension(name, size, desc);
//                System.out.println ("MmsDefaultParamsReader adding dimension " + name);
                mps.addDimension(dim);
                
                line = in.readLine();
                if (line.startsWith("Fixed")) {
                    dim.setFixed (true);
                    line = in.readLine();
                }
                line = in.readLine();
            }
            
            line = in.readLine();
            line = in.readLine();
            
/*
 *  Read Parameters
 */
            while (line != null && line.startsWith("Name      :")) {
                name = line.substring(12);
//                System.out.println ("MmsDefaultParamsReader reading parameter " + name);
                line = in.readLine();
                String module = line.substring(12);
//                System.out.println ("MmsDefaultParamsReader reading parameter module " + module);
                line = in.readLine();
                String desc = line.substring(12);
//                System.out.println ("MmsDefaultParamsReader reading parameter desc " + desc);

                line = in.readLine();
                String help = line.substring(12);
//                System.out.println ("MmsDefaultParamsReader reading parameter help " + help);
                line = in.readLine();
                String ndim = line.substring(12);
                int num_dim = Integer.parseInt(ndim);
//                System.out.println ("MmsDefaultParamsReader reading parameter num_dim " + num_dim);
                line = in.readLine();
                String d = line.substring(12);
                line = in.readLine();
                String s = line.substring(12);
                int size = Integer.parseInt(s);
//                System.out.println ("MmsDefaultParamsReader reading parameter size " + size);
                line = in.readLine();
                String type = line.substring(12);
                line = in.readLine();
                String units = line.substring(12);
                line = in.readLine();
                String wid = line.substring(12);
                int width = Integer.parseInt(wid);

                String max = null;
                String min = null;
                String defaul = null;
                String bounded = null;
                if (!type.contentEquals("string")) {
                    line = in.readLine();
                    max = line.substring(12);
                    line = in.readLine();
                    min = line.substring(12);
                    line = in.readLine();
                    defaul = line.substring(12);
                    line = in.readLine();
                    bounded = null;
                    if (line != null && line.startsWith("Bounded")) {
                        bounded = line.substring(12);
                        line = in.readLine();
                    }
                } else {
//                    System.out.println (" this is a string");
                    line = in.readLine();
                }

                Class type_class = null;
                Object vals = null;
                switch (type) {
                    case "double":
                        {
                            type_class = Double.class;
                            double val = Double.parseDouble(defaul);
                            vals = new double[size];
                            for (int i = 0; i < size; i++) {
                                ((double[])vals)[i] = val;
                            }       break;
                        }
                    case "float":
                        {
                            type_class = Float.class;
                            float val = Float.parseFloat(defaul);
                            vals = new float[size];
                            for (int i = 0; i < size; i++) {
                                ((float[]) vals)[i] = val;
                            }       break;
                        }
                    case "long":
                        {
                            type_class = Integer.class;
                            int val = Integer.parseInt(defaul);
                            vals = new int[size];
                            for (int i = 0; i < size; i++) {
                                ((int[])vals)[i] = val;
                            }       break;
                        }
                    default:
                        type_class = String.class;
                        vals = new String[size];
                        for (int i = 0; i < size; i++) {
                            ((String[])vals)[i] = "default";
                        }   break;
                }
                
                Dimension[] dims = new Dimension[num_dim];
                String dim_name = d.substring(0, d.indexOf(" "));
                dims[0] = (Dimension)(mps.getDimension(dim_name));
                
                if (num_dim > 1) {
                    int starti = d.indexOf(",") + 1;
                    dim_name = d.substring(d.indexOf(",") + 2, d.lastIndexOf("-") - 1);
                    dims[1] = (Dimension)(mps.getDimension(dim_name));
                }  
                
                Parameter mmsp = new Parameter(name, width, dims, type_class, vals);
                mmsp.setDesc(desc);
                mmsp.setModule(module);
                mmsp.setUnits(units);
                mmsp.setLowBound(min);
                mmsp.setUpBound(max);
                mmsp.setDefaultVal(defaul);
                mmsp.setIsDefaultFile(true);

//                System.out.println ("MmsDefaultParamsReader adding parameter " + name);
                mps.addParameter(mmsp);
                line = in.readLine();
            }

        } catch (IOException | NumberFormatException ex) {
            System.out.println ("MmsDefaultParamsReader: Problem reading parameters: " + name);
            throw ex;
        } finally {
            try {
                if (in!= null) {
                    in.close();
                    in = null;
                }
            }  catch (IOException E) {}
        }
        
        if (mps.isDimsEmpty() && mps.isParamsEmpty()) {
            mps = null;
            throw (new IOException ("MmsDefaultParamsReader: Invalid MMS parameter_name file."));
        }
        return mps;
    }
    
    public static void main(String arg[]) {
        try {
            MmsDefaultParamsReader mp = new MmsDefaultParamsReader("d:/gsflow_projects/bin/gsflow.par_name");
            ParameterSet ps = mp.read();
            
//            System.out.println ("Dimensions = " + ps.getDims());
//            System.out.println ("Parameters = " + ps.getParams());
            
        } catch (java.io.FileNotFoundException e) {
            System.out.println(arg[0] + " not found");
        } catch (IOException e) {
            System.out.println(arg[0] + " io exception");
        }
    }
}