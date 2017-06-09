/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package oui.paramtool;

import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsParamsReader;
import oui.mms.io.MmsParamsWriter;

/**
 *
 * @author markstro
 */
public class CommandLineFixit {
    private boolean changed;
    private boolean paramChanged;

    private CommandLineFixit(String paramFileName) {
        Double zero = 0.0;

        try {
            MmsParamsReader mp = new MmsParamsReader(paramFileName);
            ParameterSet ps = mp.read();

            changed = false;
            paramChanged = false;

            // dpsrt_area / hru_area < 0.001
            System.out.println("CommandLineFixit: dpsrt_area / hru_area < 0.001");
            Parameter param_dprst_area = ps.getParameter("dprst_area");
            Parameter param_hru_area = ps.getParameter("hru_area");
            double[] dprst_area = (double[]) param_dprst_area.getVals();
            double[] hru_area = (double[]) param_hru_area.getVals();

            for (int i = 0; i < param_dprst_area.getSize(); i++) {
                double dprst_frac_hru = dprst_area[i] / hru_area[i];
                if (dprst_frac_hru < 0.001 && dprst_frac_hru > 0.0) {
                    paramChanged = true;
                    changed = true;
                    param_dprst_area.setValueAt(zero, i);
                }
            }
            if (paramChanged) {
                System.out.println ("     changed!");
            }
            
            // hru_percent_imperv < 0.001
            System.out.println ("CommandLineFixit: hru_percent_imperv < 0.001");
            paramChanged = false;
            Parameter param_hru_percent_imperv = ps.getParameter("hru_percent_imperv");
            double[] hru_percent_imperv = (double[]) param_hru_percent_imperv.getVals();
            for (int i=0; i < param_hru_percent_imperv.getSize(); i++) {
                if (hru_percent_imperv[i] < 0.001 && hru_percent_imperv[i] > 0.0) {
                    paramChanged = true;
                    changed = true;
                    param_hru_percent_imperv.setValueAt(zero, i);
                }
            }
            if (paramChanged) {
                System.out.println ("     changed!");
            }
            
            // dprst_area > hru_area
            System.out.println ("CommandLineFixit: dprst_area > hru_area");
            paramChanged = false;
            dprst_area = (double[]) param_dprst_area.getVals();
            hru_area = (double[]) param_hru_area.getVals();
            for (int i=0; i < param_dprst_area.getSize(); i++) {
                double dprst_area_max = hru_area[i] * 0.995;
                if (dprst_area[i] > dprst_area_max) {
                    paramChanged = true;
                    changed = true;
                    dprst_area[i] = dprst_area_max;
                }
            }
            if (paramChanged) {
                System.out.println ("     changed!");
            }
            
            // dprst_area > 0.995 * hru_area
            System.out.println ("CommandLineFixit: dprst_area > 0.995 * hru_area");
            paramChanged = false;
            dprst_area = (double[]) param_dprst_area.getVals();
            hru_area = (double[]) param_hru_area.getVals();
            for (int i=0; i < param_dprst_area.getSize(); i++) {
                if (dprst_area[i] > hru_area[i] * 0.995) {
                    changed = true;
                    paramChanged = true;
                    dprst_area[i] = hru_area[i] * 0.995;
                }
            }
            if (paramChanged) {
                System.out.println ("     changed!");
            }
            
            // hru_imperv + dprst_area > hru_area
            System.out.println ("CommandLineFixit: hru_imperv + dprst_area > hru_area");
            paramChanged = false;
//            parameter = ps.getParameter("dprst_area");
//            p2 = ps.getParameter("hru_area");
//            Parameter param_hru_percent_imperv = ps.getParameter("hru_percent_imperv");
//            vals = (double[]) parameter.getVals();
//            vals2 = (double[]) p2.getVals();
//            double[] vals3 = (double[]) p3.getVals();
            hru_percent_imperv = (double[]) param_hru_percent_imperv.getVals();
            hru_area = (double[]) param_hru_area.getVals();
            dprst_area = (double[]) param_dprst_area.getVals();

            for (int i=0; i < param_dprst_area.getSize(); i++) {
                double hru_imperv_area = hru_percent_imperv[i] * hru_area[i];
                if (hru_imperv_area + dprst_area[i] > (hru_area[i] * 0.995)) {
                    changed = true;
                    paramChanged = true;
                    dprst_area[i] = (hru_area[i] * 0.995) - hru_imperv_area;
                }
            }
            if (paramChanged) {
                System.out.println ("     changed!");
            }
            
            if (changed) {
                MmsParamsWriter.write (paramFileName, ps);
            }


        } catch (java.io.IOException e) {
            System.out.println (e.toString());
        }
    }
    
    public static void main(String args[]) {
        CommandLineFixit clf;
        clf = new CommandLineFixit (args[0]);
    }


}
