/*
 * MmsDrawSchematic.java
 *
 * Created on July 18, 2005, 9:01 AM
 */

package oui.mms.gui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author  markstro
 */
public class MmsSchematicTree {
    private ArrayList<MmsModule> modules = new ArrayList<MmsModule>();
    private String schemFileName;
    private String modelName;
    
    /** Creates a new instance of MmsDrawSchematic */
    public MmsSchematicTree(String schemFileName) {
        this.schemFileName = schemFileName;
        readSchem();
    }

    public String getModelName() {
        return modelName;
    }
    
    private void readSchem() {
        String line;
        BufferedReader in = null;
        String[] split;
        
        try {
            in = new BufferedReader(new FileReader(schemFileName));
            line = in.readLine();
            modelName = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            
//            int count = 0;
            while (line != null) {
                // First line is module name and version in CSV format
                split = line.split(",");
                MmsModule mmsModule = new MmsModule(split[0], split[1]);
                modules.add(mmsModule);

                // Second line is a list of declared variables in CSV format
                line = in.readLine();
                split = line.split(",");
                mmsModule.addVariables (split);

                // Third line is a list of declared parameters in CSV format
                line = in.readLine();
                split = line.split(",");
                mmsModule.addParameters(split);

                line = in.readLine();
            }
            
        } catch (IOException ex) {
            System.out.println("Problem reading schematic file");
            ex.printStackTrace();

        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Problem reading schematic file. Expecting new version of control.mod_name");
        } finally {
            try {
                if (in!= null) {
                    in.close();
                    in = null;
                }
            }  catch (IOException E) {}
        }
    }
    
    public ArrayList<MmsModule> getModules() {
        return modules;
    }
    
    public class MmsModule {
        private String name;
        private String version;
        private ArrayList<String> variables = new ArrayList<String>(10);
        private ArrayList<String> parameters= new ArrayList<String>(10);
        
        public MmsModule(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName () {
            return name;
        }

        public String getVersion () {
            return version;
        }
        
        public ArrayList<String> getParameters () {
            return parameters;
        }

        public ArrayList<String> getVariables() {
            return variables;
        }
                
        private void addVariables(String[] split) {
            addValues(split, variables);
        }

        private void addParameters(String[] split) {
            addValues(split, parameters);
        }

        private void addValues(String[] split, ArrayList<String> arrayList) {
            for (int i = 0; i < split.length-1; i++) {
                arrayList.add(split[i]);
            }

            // Check to see if last one is a valid, non-blank value
            if ((split[(split.length)-1].length()>0) && (!(split[(split.length)-1].startsWith(" ")))) {
                arrayList.add(split[(split.length)-1]);
            }
        }
    }
}
