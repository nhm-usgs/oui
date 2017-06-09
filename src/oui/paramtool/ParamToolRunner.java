
package oui.paramtool;

import gov.usgs.cawsc.gui.WindowFactory;
import oui.mms.io.MmsParamsReader;
import oui.mms.datatypes.ParameterSet;
import oui.mms.gui.Mms;
import oui.mms.io.MmsDefaultParamsReader;

public class ParamToolRunner {
    private ParamToolGui mpeg;
    
    public ParamToolRunner(ParameterSet mms_params) {
        mpeg = new ParamToolGui(mms_params);
        String title = mms_params.getFileName().substring(Mms.fileNameIndex(mms_params.getFileName()) + 1);
        WindowFactory.displayInFrame(mpeg, title);
    }
    
    public static void main(String args[]) {
        try {
//            MmsParamsReader mp = new MmsParamsReader("f:/gsflow/data/sagehen/input/prms/sagehen_prms.param");
//            MmsParamsReader mp = new MmsParamsReader("C:/markstro/people/koczot_ld/feather_LD_FL/mms_work/input/params/AC_feather.params");
            MmsParamsReader mp = new MmsParamsReader("D:\\backedUp\\data\\nhmMapPullerV2\\test2\\redRiver.params");
            ParameterSet ps = mp.read();
//            ps.writeHistory(false);

            MmsDefaultParamsReader mdpr = new MmsDefaultParamsReader("D:/backedUp/applications/southPlatteV2/southPlattePrms/control/southPlatte.control.par_name");
            ParameterSet default_ps = (ParameterSet) mdpr.read();

            ParamTool mpe = new ParamTool(ps, default_ps);
            mpe.setTopLevel(true);
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isTopLevel() {return mpeg.isTopLevel();}
    public void setTopLevel(boolean topLevel) {mpeg.setTopLevel(topLevel);}
    
}