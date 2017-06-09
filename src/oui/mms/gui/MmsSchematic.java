/*
 * MmsDrawSchematic.java
 *
 * Created on July 18, 2005, 9:01 AM
 */

package oui.mms.gui;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.plaf.metal.MetalIconFactory;
import oui.util.OuiHelp;

/**
 *
 * @author  markstro
 */
public class MmsSchematic {
    private ArrayList<MmsModule> modules = new ArrayList<MmsModule>();
    private String schemFileName;
    
    public ArrayList getModules() {return modules;}
    
    /** Creates a new instance of MmsDrawSchematic */
    public MmsSchematic(String schemFileName) {
        this.schemFileName = schemFileName;
        readSchem();
    }
    
    private void readSchem() {
        String line = null;
        BufferedReader in = null;
        
        try {
            in = new BufferedReader(new FileReader(schemFileName));
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            
            int count = 0;
            while (line.length() != 0) {
                StringTokenizer st = new StringTokenizer(line, ", ");
                String name = st.nextToken();
                
                int X = Integer.parseInt(st.nextToken());
                int Y = Integer.parseInt(st.nextToken());
                int numConnections = Integer.parseInt(st.nextToken());
                
                int[] connections = new int[numConnections];

                modules.add(count++, new MmsModule(name, X, Y, connections));
                line = in.readLine();
            }
            
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            
            line = in.readLine();
            StringTokenizer st = new StringTokenizer(line, " ");
            
            int module_index = 0;
            int con_index = 0;
            while (st.hasMoreTokens()) {
                int con = Integer.parseInt(st.nextToken());
                
                while (true) {
                    MmsModule mm = (MmsModule)(modules.get(module_index));
                    if (mm.getConnections().length == con_index) {
                        module_index++;
                        con_index = 0;
                    } else {
                        mm.getConnections()[con_index++] = con;
                        break;
                    }
                }
            }

        } catch (NumberFormatException ex) {
            System.out.println("Problem reading schematic file line = " + line);
        } catch (IOException ex) {
            System.out.println("Problem reading schematic file");
//            ex.printStackTrace();
//            throw ex;
        } finally {
            try {
                if (in!= null) {
                    in.close();
                    in = null;
                }
            }  catch (IOException E) {}
        }
    }
    
    public MmsModule getModuleForName (String name) {
        Iterator it = modules.iterator();
        while (it.hasNext()) {
            MmsModule mm = (MmsModule)(it.next());
            if (mm.getName().equals(name)) return mm;
        }
        return null;
    }
    
    public MmsModule getModuleAt(int index) {return (MmsModule)(modules.get(index));}
    
    public class MmsModule {
        private String name;
        private String shortName;
        private int X, Y;
        private int[] connections;
        private JButton button;
        
        public MmsModule(String name, int X, int Y, int[] connections) {
            this.name = name;
            int path_index = name.lastIndexOf(File.separatorChar);
            shortName = name.substring(path_index+1, name.length());
            
            this.X = X;
            this.Y = Y;
            this.connections = connections;
            this.button = new JButton(shortName, MetalIconFactory.getTreeComputerIcon());
            this.button.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OuiHelp.OuiHelpFactory().showTopic ("");
                }
            });
        }
        
        public JButton getButton() {return button;}
        public int getX() {return X;}
        public int getY() {return Y;}
        public int getNumConnections() {return connections.length;}
        public String getShortName() {return shortName;}
        public String getName() {return name;}
        public int[] getConnections() {return connections;}
        public int getCenterX() {return X + button.getWidth() / 2;}
        public int getCenterY() {return Y + button.getHeight() / 2;}
    }
}
