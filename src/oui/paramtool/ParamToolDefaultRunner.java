
package oui.paramtool;

import gov.usgs.cawsc.gui.WindowFactory;
import oui.mms.datatypes.ParameterSet;
import oui.mms.gui.Mms;

public class ParamToolDefaultRunner {
    private ParamToolGui mpeg;
    
    public ParamToolDefaultRunner(ParameterSet mms_params) {
        mpeg = new ParamToolGui(mms_params);
        String title = mms_params.getFileName().substring(Mms.fileNameIndex(mms_params.getFileName()) + 1);
        WindowFactory.displayInFrame(mpeg, title);
    }
    
    public static void main(String args[]) {
//        try {
//            MmsDefaultParamsReader mp = new MmsDefaultParamsReader("C:/markstro/deployment/oui_distribution/oui_updated_model/feather/mms_work/models/xprms.par_name");
//            ParameterSet ps = (ParameterSet)(mp.read());
//            ps.writeHistory(false);
//            ParamTool mpe = new ParamTool(ps);
//            mpe.setTopLevel(true);
//
//        } catch (java.io.IOException e) {
//            System.out.println(args[0] + " io exception");
//        }
    }
    
    public boolean isTopLevel() {return mpeg.isTopLevel();}
    public void setTopLevel(boolean topLevel) {mpeg.setTopLevel(topLevel);}
    
}