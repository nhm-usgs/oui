/*
 * Mms.java
 *
 * Created on May 10, 2005, 4:39 PM
 */

package oui.mms.gui;

import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.util.ArrayList;
import oui.mms.datatypes.*;
import oui.mms.io.*;

/**
 *
 * @author  markstro
 */
public final class Mms {
    private ParameterSet ps = null;
    private ParameterSet defaultPs = null;
    private VariableSet vs = null;
    private ControlSet cs = null;
    private MmsSchematic schematic = null;
    private MmsSchematicTree schematicTree = null;
    private MmsGui gui = null;
    private boolean topLevel = false;

    public boolean isTopLevel () {return topLevel;}
    
    /** Creates a new instance of Mms
     * @param controlFile
     * @param topLevel */
    public Mms(String controlFile, boolean topLevel) {
        this(controlFile);
        this.topLevel = topLevel;
    }
    
    public Mms(String controlFile) {
        this.topLevel = true;
        loadControlFile(controlFile);
        
        gui = new MmsGui(this);
        if (gui != null) gui.setControlFile(cs);
        if (gui != null && ps != null) gui.updateParamFileLabelGui(ps);
        if (gui != null) gui.setDataFile();
        
        String title = "Modular Modeling System";
        WindowFactory.displayInFrame(gui, title);
    }
    
    public String getNameFileBase () {
       boolean oldVersion = false;
       
       if (oldVersion) {
           return getSingleControlValue("executable_model");
       } else {
           return getControlMap().getFileName();
       }
    }
    
    public static int fileNameIndex (String fileName) {
        int path_index = fileName.lastIndexOf('/');
        if (path_index == -1) {
            path_index = fileName.lastIndexOf('\\');
        }
        return path_index;
    }
    
    public void loadControlFile(String controlFile) {
        try {
            cs = ControlSet.readMmsControl(controlFile);
//            if (gui != null) gui.setControlFile(cs.getFileName(), cs.getDescription());

        } catch (IOException e) {
            System.out.println("Can't read control file " + controlFile);
        }
        
        // Run the model with the -print flag to make sure that the mod_name, par_name, and var_name files are up to date
        generatePrintFiles(this);
        
        loadParamFile(getSingleControlValue("param_file"));
        loadVarInfoFile();
        
        //  Load the default parameters for the executable
        String default_param_file_name = null;
        try {
            default_param_file_name = getNameFileBase() + ".par_name";
            MmsDefaultParamsReader mdpr = new MmsDefaultParamsReader(default_param_file_name);
            defaultPs = (ParameterSet) mdpr.read();

                    // First time through the gui is null
            if (gui != null) {
                gui.setControlFile(cs);
            }

        } catch (java.io.IOException e) {
            System.out.println(default_param_file_name + " io exception");
        }

    }
    
    public void loadParamFile(String paramFileName) {
        try {
            MmsParamsReader mp = new MmsParamsReader(paramFileName);
            ps = (ParameterSet)(mp.read());
//            ps.writeHistory(false);
//            if (gui != null) gui.setParamFile(ps.getFileName(), ps.getDescription());
            
            setControlValues("param_file", paramFileName);

            // Update the GUI label and save the choice to the control file.
            // if gui == null, then this is the first time through and not being updated from the menubar
            if (gui != null) {
                gui.updateParamFileLabelGui(ps);
                saveControlFile();
            }

        } catch (IOException e) {
            System.out.println("Can't read param file: " + paramFileName);
        }
    }

    public void loadVariableFiles() {
        ArrayList<String> dataFiles = getControlValues("data_file");
        MmsDataFileReader[] mdfr = new MmsDataFileReader[dataFiles.size()];
        
        mdfr[0] = new MmsDataFileReader (dataFiles.get(0));
        OuiCalendar start = mdfr[0].getStart();
        OuiCalendar end = mdfr[0].getEnd();
        for (int i = 1; i < dataFiles.size(); i++) {
            mdfr[i] = new MmsDataFileReader(dataFiles.get(i));
            OuiCalendar temp_start = mdfr[i].getStart();
            OuiCalendar temp_end = mdfr[i].getEnd();
            
            if (start.after(temp_start)) start = temp_start;
            if (end.before(temp_end)) end = temp_end;
        }
        
        if (vs == null) vs = new VariableSet();
        vs.setStart (start);
        vs.setEnd (end);
                 
//        try {
//            MmsParamsReader mp = new MmsParamsReader(paramFileName);
//            ps = (ParameterSet)(mp.read());
//            ps.writeHistory(false);
//            if (gui != null) gui.setParamFile(ps.getShortFileName(), ps.getDescription());
//            
//            setControlValues ("param_file",  paramFileName);
//            
//        } catch (IOException e) {
//            System.out.println("Can't read param file");
//        }
//        if (gui != null) gui.setDataFile();
    }
        
    public void loadVarInfoFile() {
        String varInfoFileName = getNameFileBase() + ".var_name";
        
        try {
            MmsVarInfoReader mvir = new MmsVarInfoReader(varInfoFileName, ps);
            vs = (VariableSet)(mvir.read());
                        
        } catch (IOException e) {
            System.out.println("Can't read variable info file: " + varInfoFileName);
        }
    }
    
//    public static void saveControlFile() {
    public void saveControlFile() {
   
        try {
            ControlSet.writeMmsControl(cs, cs.getFileName());
        } catch (IOException e) {
            System.out.println("Can't write control file");
        }
    }
    
    public void saveControlFile(String controlFileName) {
        try {
            ControlSet.writeMmsControl(cs, controlFileName);
            loadControlFile(controlFileName);
        } catch (IOException e) {
            System.out.println("Can't write control file");
        }
    }
    
    public void saveParamFile (String paramFileName) {
        MmsParamsWriter.write(paramFileName, ps);
        loadParamFile(paramFileName);
    }
    
//    public static ParameterSet getParamSet() {return ps;}
//    public static ControlSet getControlMap() {return cs;}
//    public static VariableSet getVariableSet() {return vs;}
    public ParameterSet getParamSet() {return ps;}
    
    public ControlSet getControlMap() {
        return cs;
    }
    
    public VariableSet getVariableSet() {return vs;}
    public ParameterSet getDefaultParamSet () {return defaultPs;}
    public boolean getControlValueExists (String controlName) {return cs.controlVariableExixts(controlName);}
    
//    public static String getSingleControlValue(String controlName) {
    public String getSingleControlValue(String controlName) {
   
        Control mc = (Control)(cs.getControlVariableByName(controlName));
        
        if (mc == null) {
            System.out.println("Mms.getSingleControlValue: " + controlName + " not set in the control file.");
            
            ArrayList<String> vals = new ArrayList<String>(1);
// DANGER This stuff is a hack.  Do this until I can figure out something better to
// do when control names are not defined.
            if (controlName.equals("executable_model")) {
                System.out.println("Mms.getSingleControlValue: " + controlName + " added to the control file.  You might want to check the value.");

                int type = 4;  // string
                String foo = "./mms_work/models/xprms";
                vals.add(0, foo);
                cs.put(new Control(controlName, type, vals));
                return foo;

            } else if (controlName.equals("executable_desc")) {
                System.out.println("Mms.getSingleControlValue: " + controlName + " added to the control file.  You might want to check the value.");
                
                int type = 4;  // string
                String foo =  "MMF version of PRMS";
                vals.add(0, foo);
                cs.put(new Control(controlName, type, vals));
                return foo;
                
            } else if (controlName.equals("dispGraphsBuffSize")) {
                System.out.println("Mms.getSingleControlValue: " + controlName + " added to the control file.  You might want to check the value.");
                
                int type = 4;  // string
                String foo = "30";
                vals.add(0, foo);
                cs.put(new Control(controlName, type, vals));
                return foo;
                
            } else {
                return null;
            }
// end DANGER
            
        } else {
            return mc.getSingleVal();
        }
    }
    
    public ArrayList<String> getControlValues(String controlName) {
//    public static String[] getControlValues(String controlName) {

        Control mc = (Control)(cs.getControlVariableByName(controlName));
        
         if (mc == null) {
            System.out.println("Mms.getControlValues: " + controlName + " not set in the control file.");
            
            ArrayList<String> vals = new ArrayList<String>(9);
// DANGER This stuff is a hack.  Do this until I can figure out something better to
// do when control names are not defined.
            if (controlName.equals("dispVar_element")) {
                System.out.println("Mms.getControlValues: " + controlName + " added to the control file.  You might want to check the value.");

                int type = 4;  // string
                vals.add(0, "1");
                vals.add(1, "1");
                vals.add(2, "1");
                vals.add(3, "1");
                vals.add(4, "1");
                vals.add(5, "1");
                vals.add(6, "1");
                vals.add(7, "1");
                vals.add(8, "1");
                
                cs.put(new Control(controlName, type, vals));
                return vals;

            } else if (controlName.equals("dispVar_names")) {
                System.out.println("Mms.getControlValues: " + controlName + " added to the control file.  You might want to check the value.");
                
                int type = 4;  // string
                vals.add(0, "basin_cfs");
                vals.add(1, "runoff");
                vals.add(2, "basin_gwflow");
                vals.add(3, "basin_sroff");
                vals.add(4, "basin_ssflow");
                vals.add(5, "hru_rain");
                vals.add(6, "hru_snow");
                vals.add(7, "basin_tmax");
                vals.add(8, "basin_tmin");

                cs.put(new Control(controlName, type, vals));
                return vals;
                
            } else if (controlName.equals("dispVar_plot")) {
                System.out.println("Mms.getControlValues: " + controlName + " added to the control file.  You might want to check the value.");
                
                int type = 4;  // string
                vals.add(0, "1");
                vals.add(1, "1");
                vals.add(2, "2");
                vals.add(3, "2");
                vals.add(4, "2");
                vals.add(5, "3");
                vals.add(6, "3");
                vals.add(7, "4");
                vals.add(8, "4");

                cs.put(new Control(controlName, type, vals));
                return vals;
                
            } else {
                return null;
            }
// end DANGER
                        
        } else {
            return mc.getVals();
        }
    } 
    
//    public static void setControlValues(String controlName, String value) {
    public void setControlValues(String controlName, String value) {

        setControlValues (controlName, 0, value);
    }
    
//    public static void setControlValues(String controlName, int index, String value) {
     public void setControlValues(String controlName, int index, String value) {
        Control mc = cs.getControlVariableByName(controlName);
        
        if (mc == null) {
            // 1 = long  2 = float  3 = double  4 = string
            ArrayList<String> foo = new ArrayList<String>(1);
            foo.add(0, value);
            mc = new Control(controlName, 4, foo);
            cs.put(mc);
            
        } else {
            mc.setValueAt(index, value);
        }
    }  
     
    public static void generatePrintFiles (Mms mms) {
        String control = " -C" + mms.getControlMap().getFileName();
        String executable = mms.getSingleControlValue("executable_model");
        String arg = executable + control + " -print";
        System.out.println("Mms.generatePrintFiles: executing = " + arg);
            
        try {
            Process process = Runtime.getRuntime().exec(arg);

        } catch(IOException e) {
        }
    }
        
//    public static void setControlValues(String controlName, String[] values) {
    public void setControlValues(String controlName, ArrayList<String> values) {
        Control mc = cs.getControlVariableByName(controlName);
        
        if (mc == null) {
            // 1 = long  2 = float  3 = double  4 = string
            mc = new Control(controlName, 4, values);
            cs.put(mc);
            
        } else {
            mc.setVals(values);
        }
    }
    
     public static void usage() {
        System.out.println("Mms usage:");
        System.out.println("  java -cp <classpath> oui.mms.Mms <ccontrol file>");
    }   
     
    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) {
         if (args.length != 1) {
             usage();
             return;
         }
         
         String controlFile = args[0];
        Mms mms = new Mms(controlFile);
        
        //// Temporary test
                MmsSingleRunGui msrg = new MmsSingleRunGui(mms);        
        String title = "MMS Run Control - Single Run";
        WindowFactory.displayInFrame(msrg, title);
     }

    void updateDataFileListGui() {
        gui.updateDataFileListGui();
    }

    MmsSchematic getSchematic() {
        return schematic;
    }

    MmsSchematicTree getSchematicTree() {
        return schematicTree;
    }

    void setSchematicTree(MmsSchematicTree mmsSchematicTree) {
       schematicTree = mmsSchematicTree;
    }
}
