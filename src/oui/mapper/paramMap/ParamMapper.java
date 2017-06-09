/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.mapper.paramMap;

import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mapper.AbstractMapper;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsParamsReader;

/**
 *
 * @author markstro
 */
public class ParamMapper extends AbstractMapper {
    
    public ParamMapper(String args[]) {
        super (args);
        setMapControl (args);        
    }
    
    public static void main(String args[]) {
        // This is for testing
        //        String args[] = new String[4];
        //        args[0] = "C:/markstro/people/emily/mapperDemos(SP)/maps/platt_hru0 polygon";
        //        args[1] = "hru_int";
        //        args[2] = "C:/markstro/people/emily/mapperDemos(SP)/input/PAR1.wpar";
        //        args[3] = "nhru";
        ParamMapper paramMapper = new ParamMapper (args);
    }

    @Override
    public final void setMapControl(String[] args) {
        int numberOfBins = 10;

        try {
            // read the parameter file
            MmsParamsReader mp = new MmsParamsReader(args[2]);
            ParameterSet ps = (ParameterSet) (mp.read());

            // Create the controler
            ParamMapGui pmg = new ParamMapGui(ps, args[3], ouiGisPanel,
                    vt, args[1], numberOfBins, ps.getDimension(args[3]).getSize());
            ouiGisPanel.setLabelIndexDirectly(vt.getIdIndex(args[1]));

            String title = "PRMS Parameter Mapper";
            WindowFactory.displayInFrame(pmg, title);

        } catch (IOException ex) {
            Logger.getLogger(ParamMapGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
