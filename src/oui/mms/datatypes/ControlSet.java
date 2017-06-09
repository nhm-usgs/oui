package oui.mms.datatypes;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import oui.mms.gui.Mms;

public class ControlSet {
    private String fileName;
    private String description;
    private ArrayList<Control> controlArray = new ArrayList<Control> ();
    
    
    public static ControlSet readMmsControl(String fileName) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        ControlSet mcm = readMmsControl(in);
        mcm.setFileName(fileName);
        return mcm;
    }
    
    public static ControlSet readMmsControl(BufferedReader in) throws IOException {
        ControlSet mcm = new ControlSet();
        
        String line;
        try {
            mcm.description = in.readLine();
            line = in.readLine();
            int line_count = 1;
            while (line != null) {
                String name = null;
                if (line.equals("####")) {
                    name = in.readLine();
                    int size = Integer.parseInt(in.readLine());
                    int type = Integer.parseInt(in.readLine());
                    ArrayList<String> vals = new ArrayList<String>(size);
                    for (int i = 0; i < size; i++) {
                        line = in.readLine();
                        
                        if (!line.equals("####")) { 
                            vals.add(i, line);
                        } else {
                            System.out.println ("MmsControlDescriptor.readMmsControl: reading file ");
                            System.out.println ("  control variable name is " + name);
                            System.out.println ("  too few values don't match size on line " +  line_count);                            
                        }
                        line_count++;
                    }
                    mcm.controlArray.add (new Control(name, type, vals));
                } else {
                    System.out.println ("MmsControlDescriptor.readMmsControl: reading file ");
                    System.out.println ("  control variable name is " + name);
                    System.out.println ("  too many values don't match size on line " +  line_count);
                }
                line = in.readLine();
                line_count++;
            }
            
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            
        } finally {
            try {
                if (in!= null) {
                    in.close();
                    in = null;
                }
            }  catch (IOException E) {}
        }
        
        return mcm;
    }

    public static void writeMmsControl(ControlSet mcm, String outputFileName) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(outputFileName));
        System.out.println("MmsControlFile.write: writing file " + outputFileName);
        writeMmsControl(mcm, out);
    }

    public static void writeMmsControl(ControlSet mcm, PrintWriter out) throws IOException {
        out.println(mcm.description);

        Iterator it = mcm.controlArray.iterator();
        while (it.hasNext()) {
            out.println("####");

            Control mc = (Control) (it.next());
            out.println(mc.getName());
            out.println(mc.getSize());
            out.println(mc.getType());
            ArrayList<String> vals = mc.getVals();
            for (int i = 0; i < mc.getSize(); i++) {
                out.println(vals.get(i));
            }
        }

        if (out != null) {
            out.close();
        }
    }
    
    /**
     * Getter for property fileName.
     * @return Value of property fileName.
     */
    public String getFileName() {
        return this.fileName;
    }
    
    public String getDescription() {
        return description;
    } 
    
    public void setDescription (String desc) {this.description = desc;}
    
    /**
     * Setter for property fileName.
     * @param fileName New value of property fileName.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getDirectory() {
        int path_index = fileName.lastIndexOf(File.separatorChar);
        if (path_index == -1) return "." + File.separatorChar;
        else return fileName.substring(0, path_index);
    }
    
    public String getShortFileName() {
        return fileName.substring(Mms.fileNameIndex(fileName)+1, fileName.length());
    }
    
    public String toString () {return this.fileName;}
    
    public static void main(String[] args) {
        try {
            ControlSet foo = ControlSet.readMmsControl("/home/projects/oui_projects/rio_grande/riogr_mms_work/control/Abiquiu.xprms_xyz.oui.control");
            Control mc = (Control)(foo.getControlVariableByName("statVar_names"));
            System.out.println ("test = " + mc.getVals());
            for (int i = 0; i < mc.getSize(); i++) {
                System.out.println ("val = " + mc.getVals().get(i));

            }
            ControlSet.writeMmsControl(foo, "/home/projects/oui_projects/rio_grande/riogr_mms_work/control/foo.control");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Control getControlVariableByName (String name) {
        Iterator<Control> it = controlArray.iterator();
        while (it.hasNext()) {
            Control mc = it.next();
            if (mc.getName().equals(name)) return mc;
        }
        return null;
    }
    
    public Control getControlVariableByIndex(int index) {
        return controlArray.get(index);
    }
    
    public boolean controlVariableExixts(String name) {
        if (getControlVariableByName(name) == null) return false;
        else return true;
    }
    
    public void put (Control mc) {
        controlArray.add(mc);
    }
    
    public int getControlVariableCount () {
        return controlArray.size();
    }
}

