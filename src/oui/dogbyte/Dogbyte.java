
package oui.dogbyte;

public class Dogbyte {
    private DogbyteFrame frame;
    private DogbytePanel gui;
    private DogbyteData data;
    
    public Dogbyte() {
        this ("./dogbyte.xml");  //  Default xml project file
    }
    
    public Dogbyte(String file_name) {
        
        data = new DogbyteData (file_name);
        gui = new DogbytePanel(data);
        
        //  Don't need this if gui is a panel in a wizard
        frame = new DogbyteFrame();
        frame.setContentPane(gui);
        frame.setSize(1000, 800);
        frame.setVisible(true);
        setTopLevel(true);
    }
    
    public static void main(String args[]) {
        Dogbyte st = new Dogbyte("f:/dogbyte_projects/sagehen/dogbyte.xml");
    }
    
    public boolean isTopLevel() {return frame.isTopLevel();}
    public final void setTopLevel(boolean topLevel) {frame.setTopLevel(topLevel);}
    
}