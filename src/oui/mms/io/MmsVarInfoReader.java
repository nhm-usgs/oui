package oui.mms.io;

import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.VariableSet;
import oui.mms.datatypes.Variable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
//import java.util.logging.Logger;
import oui.mms.datatypes.ParameterSet;

public class MmsVarInfoReader {
    private final FileReader file;
//    private Logger log;
    private final String fileName;
    private VariableSet mvs;
    private ParameterSet mps;
    
    public MmsVarInfoReader(String fileName, ParameterSet mps) throws IOException {
//        this(new FileReader(fileName), mps);
        this.fileName = fileName;
        this.mps = mps;
        this.file = new FileReader(fileName);
    }

//    public MmsVarInfoReader(FileReader file, ParameterSet mps) throws IOException {
//        this.mps = mps;
//    }
    
    private String readValue(String line) {
        StringTokenizer st = new StringTokenizer(line, " ");
        st.nextToken();
        if (st.hasMoreTokens()) return st.nextToken();
        else return "";
    }
    
    public VariableSet read() throws IOException {
        mvs = new VariableSet();
        mvs.setFileName(fileName);
        
/*
 *  Read dimensions
 */
        
        String line = null;
        BufferedReader in = null;
        String name = null;
        
        try {
            in = new BufferedReader(file);
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            
            mvs.setDescription(line);
            
            while (!line.startsWith("Name:")) {
                line = in.readLine();
            }
            
            while (line != null) {
                name = readValue(line);
                String module = readValue(in.readLine());
                int ndim = Integer.parseInt(readValue(in.readLine()));
                String dimName = readValue(in.readLine());
                int size = Integer.parseInt(readValue(in.readLine()));
                String type = readValue(in.readLine());
                
                line = in.readLine();
                int path_index = line.indexOf(' ');
                String desc = line.substring(path_index+1, line.length());
                
                String units = readValue(in.readLine());
                
//                Object vals = null;
                Class type_class = null;
                
                if (type.equals("float")) {
                    type_class = Float.class;
                    
//                    for (int i = 0; i < size; i++) {
//                        st = new StringTokenizer(in.readLine(), " ");
//                        ((int[])vals)[i] = Integer.parseInt(st.nextToken());
//                    }
                    
                } else if (type.equals("long")){
                    type_class = Long.class;
                    
//                    for (int i = 0; i < size; i++) {
//                        st = new StringTokenizer(in.readLine(), " ");
//                        ((double[])vals)[i] = Double.parseDouble(st.nextToken());
//                    }
                    
                }
                
                Dimension[] dims = new Dimension[ndim];
                dims[0] = (Dimension)(mps.getDimension(dimName));
                
                if (ndim > 1) {
                    dims[1] = (Dimension)(mps.getDimension(dimName));
                }

                mvs.addVariable(new Variable(name, module, dims, type_class, desc, units));

                line = in.readLine();
                line = in.readLine();
            }

        } catch (IOException ex) {
            System.out.println ("MmsVarInfoReader: Problem reading variable info file");
            throw ex;
        } catch (NumberFormatException ex) {
            System.out.println ("MmsVarInfoReader: NumberFormatException while reading variable info file");
            System.out.println ("  name = " + name);
            System.out.println ("  line = " + line);
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
            throw (new IOException ("Invalid MMS variable info file."));
        }
        return mvs;
    }

}