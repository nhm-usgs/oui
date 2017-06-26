/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.paramtool;

import gov.usgs.cawsc.gui.GuiUtilities;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

/**
 *
 * @author markstro
 */
// This class "fixes" a Parameter File to get rid of all errors and warnings for
// a PRMS setup.
public class ParamToolFileFixer {

    ParamToolGui gui;

    public ParamToolFileFixer(ParamToolGui gui, ParameterSet default_ps, ParameterSet mms_params) {
        this.gui = gui;

        Object[] options = {"Continue", "Quit"};
        int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(gui), "Before you continue, make sure that you have the necessary backup of Parameter File\n" + mms_params.getFileName(), "Continue?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == 0) {
            fixIt(default_ps, mms_params);
        }
    }

    private void fixIt(ParameterSet default_ps, ParameterSet mms_params) {
        // list parameters that are in the parameter file, but not in the par_name file.
        // delete these from the parameter file
        Iterator<Parameter> it = mms_params.getParamIterator();
        while (it.hasNext()) {
            Parameter p = it.next();

            // skip Lumen parameters
            String foo = p.getName();
            if (!foo.equals("poi_gage_id") && !foo.equals("poi_gage_segment")
                    && !foo.equals("poi_type") && !foo.equals("hru_x")
                    && !foo.equals("hru_y") && !foo.equals("parent_poigages")
                    && !foo.equals("parent_segment") && !foo.equals("parent_gw")
                    && !foo.equals("parent_ssr") && !foo.equals("parent_hru")
                    && !foo.equals("hru_segment") && !foo.equals("tosegment")) {
                
                Parameter paramCheck = default_ps.getParameter(p.getName());

                if (paramCheck == null) {
                    ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}\" is in the parameter file, but is not used by this PRMS application. Deleting it.", new Object[]{p.getName()});
                    mms_params.remove(p);
                }
            }
        }

        // list parameters that are in the par_name file, but not in the parameter file.
        // add these to the parameter file.  
        System.out.println();
        Iterator<Parameter> paramIterator = default_ps.getParamIterator();
        while (paramIterator.hasNext()) {
            Parameter p = paramIterator.next();
            Parameter paramCheck = mms_params.getParameter(p.getName());

            if (paramCheck == null) {
                ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}\" is used by this PRMS application, but is not in the parameter file. Adding it.", new Object[]{p.getName()});
                mms_params.addParameter(p);
            }
        }

        // look for missing values  
        System.out.println();

        paramIterator = mms_params.getParamIterator();
        while (paramIterator.hasNext()) {
            Parameter p = paramIterator.next();
            p.getVals();

            if (p.getType() == Integer.class) {
                int[] val = (int[]) (p.getVals());

                for (int i = 0; i < val.length; i++) {
                    if (val[i] == -999) {
                        int defVal = Integer.parseInt(default_ps.getParameter(p.getName()).getDefaultVal());
                        val[i] = defVal;
                        ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is specified with the missing value flag. Setting to defalut value {2}", new Object[]{p.getName(), (i + 1), defVal});
                    }
                }

            } else if (p.getType() == Float.class) {
                float[] val = (float[]) (p.getVals());

                for (int i = 0; i < val.length; i++) {
                    if (val[i] < -998.0 && val[i] > -1000.0) {
                        float defVal = Float.parseFloat(default_ps.getParameter(p.getName()).getDefaultVal());
                        val[i] = defVal;
                        ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is specified with the missing value flag. Setting to defalut value {2}", new Object[]{p.getName(), (i + 1), defVal});
                    }
                }

            } else if (p.getType() == Double.class) {
                double[] val = (double[]) (p.getVals());

                for (int i = 0; i < val.length; i++) {
                    if (val[i] < -998.0 && val[i] > -1000.0) {
                        double defVal = Double.parseDouble(default_ps.getParameter(p.getName()).getDefaultVal());
                        val[i] = defVal;
                        ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is specified with the missing value flag. Setting to defalut value {2}", new Object[]{p.getName(), (i + 1), defVal});
                    }
                }

            } else if (p.getType() == String.class) {
                String[] val = (String[]) (p.getVals());

                for (int i = 0; i < val.length; i++) {
                    if (val[i].contains("-999")) {
                        String defVal = default_ps.getParameter(p.getName()).getDefaultVal();
                        val[i] = defVal;
                        ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is specified with the missing value flag. Setting to defalut value {2}", new Object[]{p.getName(), (i + 1), defVal});
                    }
                }
            }
        }

        // look for values out of bounds  
        System.out.println();

        paramIterator = mms_params.getParamIterator();
        while (paramIterator.hasNext()) {
            Parameter p = paramIterator.next();
            p.getVals();

            if (p.getType() == Integer.class) {
                try {
                    int[] val = (int[]) (p.getVals());
                    int lo = Integer.parseInt(default_ps.getParameter(p.getName()).getLowBound());
                    int hi = Integer.parseInt(default_ps.getParameter(p.getName()).getUpBound());

                    for (int i = 0; i < val.length; i++) {
                        if (val[i] < lo) {
                            int defVal = Integer.parseInt(default_ps.getParameter(p.getName()).getLowBound());
                            val[i] = defVal;
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is < lower bound. Setting to lower bound {2}",
                                    new Object[]{p.getName(), (i + 1), defVal});

                        } else if (val[i] > hi) {
                            int defVal = Integer.parseInt(default_ps.getParameter(p.getName()).getUpBound());
                            val[i] = defVal;
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is > upper bound. Setting to upper bound {2}",
                                    new Object[]{p.getName(), (i + 1), defVal});
                        }
                    }
                } catch (NumberFormatException e) {
                    ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "No range check available for parameter \"{0}\"", new Object[]{p.getName()});

                }

            } else if (p.getType() == Float.class) {
                try {
                    float[] val = (float[]) (p.getVals());
                    float lo = Float.parseFloat(default_ps.getParameter(p.getName()).getLowBound());
                    float hi = Float.parseFloat(default_ps.getParameter(p.getName()).getUpBound());
                    for (int i = 0; i < val.length; i++) {
                        if (val[i] < lo) {
                            float defVal = Float.parseFloat(default_ps.getParameter(p.getName()).getLowBound());
                            val[i] = defVal;
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is < lower bound. Setting to lower bound {2}",
                                    new Object[]{p.getName(), (i + 1), defVal});
                        } else if (val[i] > hi) {
                            float defVal = Float.parseFloat(default_ps.getParameter(p.getName()).getUpBound());
                            val[i] = defVal;
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is > upper bound. Setting to upper bound {2}",
                                    new Object[]{p.getName(), (i + 1), defVal});
                        }
                    }
                } catch (Exception e) {
                    ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "No range check available for parameter \"{0}\"", new Object[]{p.getName()});
                }

            } else if (p.getType() == Double.class) {
                try {
                    double[] val = (double[]) (p.getVals());
                    double lo = Double.parseDouble(default_ps.getParameter(p.getName()).getLowBound());
                    double hi = Double.parseDouble(default_ps.getParameter(p.getName()).getUpBound());
                    for (int i = 0; i < val.length; i++) {
                        if (val[i] < lo) {
                            double defVal = Double.parseDouble(default_ps.getParameter(p.getName()).getLowBound());
                            val[i] = defVal;
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is < lower bound. Setting to lower bound {2}",
                                    new Object[]{p.getName(), (i + 1), defVal});
                        } else if (val[i] > hi) {
                            double defVal = Double.parseDouble(default_ps.getParameter(p.getName()).getUpBound());
                            val[i] = defVal;
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}[{1}]\" is > upper bound. Setting to upper bound {2}",
                                    new Object[]{p.getName(), (i + 1), defVal});
                        }
                    }
                } catch (Exception e) {
                    ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "No range check available for parameter \"{0}\"", new Object[]{p.getName()});
                }

            } else if (p.getType() == String.class) {
                ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "No range check available for parameter \"{0}\"", new Object[]{p.getName()});
            }
        }

        // Check soil_rechr_max > soil_moist_max.
        System.out.println();

        Parameter smm = mms_params.getParameter("soil_moist_max");
        double[] smmVals = (double[]) (smm.getVals());

        Parameter srm = mms_params.getParameter("soil_rechr_max");
        double[] srmVals = (double[]) (srm.getVals());

        for (int i = 0; i < srmVals.length; i++) {
            if (srmVals[i] > smmVals[i]) {
                srmVals[i] = smmVals[i];
                ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "soil_rechr_max[{0}] is greter than soil_moist_max[{1}] resetting value to {2}", new Object[]{(i + 1), (i + 1), smmVals[i]});
            }
        }

        // Check soil_rechr_init > soil_rechr_max.
        System.out.println();

        Parameter sri = mms_params.getParameter("soil_rechr_init");
        double[] sriVals = (double[]) (sri.getVals());

        srm = mms_params.getParameter("soil_rechr_max");
        srmVals = (double[]) (srm.getVals());

        for (int i = 0; i < srmVals.length; i++) {
            if (sriVals[i] > srmVals[i]) {
                sriVals[i] = srmVals[i];
                ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "soil_rechr_init[{0}] is greter than soil_rechr_max[{1}] resetting value to {2}", new Object[]{(i + 1), (i + 1), srmVals[i]});
            }
        }

        // Check soil_moist_max > soil_moist_init.
        System.out.println();

        Parameter smi = mms_params.getParameter("soil_moist_init");
        double[] smiVals = (double[]) (smi.getVals());

        smm = mms_params.getParameter("soil_moist_max");
        smmVals = (double[]) (smm.getVals());

        for (int i = 0; i < smmVals.length; i++) {
            if (smiVals[i] > smmVals[i]) {
                smiVals[i] = smmVals[i];
                ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "soil_moist_init[{0}] is greter than soil_moist_max[{1}] resetting value to {2}", new Object[]{(i + 1), (i + 1), smmVals[i]});
            }
        }

        // Check muskingum routing coeficients.
        System.out.println();
        Parameter k = mms_params.getParameter("K_coef");
        double[] kVals = null;
        if (k != null) {
            kVals = (double[]) (k.getVals());
            for (int i = 0; i < kVals.length; i++) {
                if (kVals[i] < 1.0) {
                    kVals[i] = 1.0;
                    ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "K_coef[{0}] is less than 1.0, resetting value to 1.0", new Object[]{(i + 1)});
                }
            }
        }

        System.out.println();
        Parameter x = mms_params.getParameter("x_coef");
        double[] xVals;
        if (x != null) {
            xVals = (double[]) (x.getVals());
            for (int i = 0; i < xVals.length; i++) {
                if (xVals[i] > 0.5) {
                    xVals[i] = 0.5;
                    ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "x_coef[{0}] is greater than 0.5, resetting value to 0.5", new Object[]{(i + 1)});
                }
            }
        }

// check the values of k and x to make sure that Muskingum routing is stable
        System.out.println();
        double Ts;
        if (kVals != null) {
            for (int i = 0; i < kVals.length; i++) {
                if (kVals[i] < 2.0) {
                    Ts = 1.0;
                } else if (kVals[i] < 3.0) {
                    Ts = 2.0;
                } else if (kVals[i] < 4.0) {
                    Ts = 3.0;
                } else if (kVals[i] < 6.0) {
                    Ts = 4.0;
                } else if (kVals[i] < 8.0) {
                    Ts = 6.0;
                } else if (kVals[i] < 12.0) {
                    Ts = 8.0;
                } else if (kVals[i] < 24.0) {
                    Ts = 12.0;
                } else {
                    Ts = 24.0;
                }

                double x_max = Ts / (2.0 * kVals[i]);
//                if (kVals != null) {
//                    if (xVals[i] > x_max) {
//                        xVals[i] = x_max * 0.99;
//                        ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "x_coef[{0}] is too large for the internal time step, resetting value to {1}", new Object[]{(i + 1), xVals[i]});
//                    }
//                }
            }
        }
    }
}
