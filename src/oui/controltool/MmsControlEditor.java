package oui.controltool;

import gov.usgs.cawsc.gui.WindowFactory;
import oui.mms.datatypes.ControlSet;
import oui.mms.gui.Mms;

public class MmsControlEditor {
    private MmsControlEditorGUI mceg;
    
    public MmsControlEditor(ControlSet mcm) {
        mceg = new MmsControlEditorGUI(mcm);

        String title = mcm.getFileName().substring(Mms.fileNameIndex(mcm.getFileName()) + 1);
        WindowFactory.displayInFrame(mceg, title);
    }
    
    public static void main(String args[]) {
        try {
            ControlSet mcs = ControlSet.readMmsControl("/home/projects/oui_projects/rio_grande/riogr_mms_work/control/Abiquiu.xprms_xyz.oui.control");
            MmsControlEditor mce = new MmsControlEditor(mcs);
            mce.setTopLevel(true);
            
        } catch (java.io.IOException e) {
            System.out.println(args[0] + " io exception");
        }
    }
    
    public boolean isTopLevel() {return mceg.isTopLevel();}
    public void setTopLevel(boolean topLevel) {mceg.setTopLevel(topLevel);}
}