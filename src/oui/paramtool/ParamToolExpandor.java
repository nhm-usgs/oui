/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.paramtool;

import gov.usgs.cawsc.gui.GuiUtilities;
import java.util.Iterator;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

/**
 *
 * @author markstro
 */
public class ParamToolExpandor {

    ParamToolGui gui;

    public ParamToolExpandor(ParamToolGui gui, ParameterSet default_ps, ParameterSet mms_params) {
        this.gui = gui;

        Object[] options = {"Continue", "Quit"};
        int n = JOptionPane.showOptionDialog(GuiUtilities.windowFor(gui),
                "You should run Fix It (if you're going to run Fix It) before you run The Expandor\n"
                + "Before you continue, make sure that you have the necessary backup of Parameter File\n"
                + mms_params.getFileName(), "Continue?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
        if (n == 0) {
            expand(default_ps, mms_params);
        }
    }

    private void expand(ParameterSet default_ps, ParameterSet mms_params) {

        // Look for parameters in the parameter file with dimensions that do
        // not match the dimensions in the par_name file.
        
        // If you make changes in here, change the c code in PRMS too: read_params.c
        // These codes should do the same thing.
        //
        System.out.println();
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

                boolean match = true;

                Parameter paramCheck = default_ps.getParameter(p.getName());

                // if the parameter has different number of dimension, then they
                // don't match
                if (p.getNumDim() != paramCheck.getNumDim()) {
                    match = false;
                }

                if (!p.getDimension(0).getName().equals(paramCheck.getDimension(0).getName())) {
                    match = false;
                }

                if (p.getNumDim() == 2 && paramCheck.getNumDim() == 2) {
                    if (!p.getDimension(1).getName().equals(paramCheck.getDimension(1).getName())) {
                        match = false;
                    }
                }

                if (match == false) {
                    String str = "Converting " + p.getDimension(0);
                    for (int i = 1; i < p.getNumDim(); i++) {
                        str = str + "," + p.getDimension(i);
                    }

                    str = str + " to " + paramCheck.getDimension(0);
                    for (int i = 1; i < paramCheck.getNumDim(); i++) {
                        str = str + "," + paramCheck.getDimension(i);
                    }

                    ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}\" needs to be expanded. {1}",
                            new Object[]{p.getName(), str});

                    // convert "one" to anything
                    if (p.getDimension(0).getName().equals("one")) {
                        Dimension[] dim = new Dimension[paramCheck.getNumDim()];
                        for (int i = 0; i < paramCheck.getNumDim(); i++) {
                            dim[i] = paramCheck.getDimension(i);
                        }
                        p.setDimension(dim);

                        Object val;
                        if (p.getType() == Integer.class) {
                            int[] oldVals = (int[]) p.getVals();
                            int[] newVal = new int[p.getSize()];

                            for (int i = 0; i < p.getSize(); i++) {
                                newVal[i] = oldVals[0];
                            }
                            val = newVal;

                        } else if (p.getType() == Float.class) {
                            float[] oldVals = (float[]) p.getVals();
                            float[] newVal = new float[p.getSize()];

                            for (int i = 0; i < p.getSize(); i++) {
                                newVal[i] = oldVals[0];
                            }
                            val = newVal;

                        } else if (p.getType() == Double.class) {
                            double[] oldVals = (double[]) p.getVals();
                            double[] newVal = new double[p.getSize()];

                            for (int i = 0; i < p.getSize(); i++) {
                                newVal[i] = oldVals[0];
                            }
                            val = newVal;

                        } else {
                            String[] oldVals = (String[]) p.getVals();
                            String[] newVal = new String[p.getSize()];

                            for (int i = 0; i < p.getSize(); i++) {
                                newVal[i] = oldVals[0];
                            }
                            val = newVal;
                        }

                        p.setVals(val);
                        ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}\"  expanded!",
                                new Object[]{p.getName()});

                        // convert 1D to 1D
                    } else if (p.getNumDim() == 1 && paramCheck.getNumDim() == 1) {

                        // Convert "nsub" to anything else. No other 1 to 1 mappings done yet.
                        
                        if (p.getDimension(0).getName().equals("nsub")) {  // subbasin to one mappable dimension

                            String mapParamName = getMapParamName(paramCheck.getDimension(0).getName());

                            Parameter mapping_param = default_ps.getParameter(mapParamName);

                            if (mapping_param == null) {
                                System.out.println ("\n ERROR -- ParamToolExpandor:  mapping parameter " + mapParamName + " must be set in parameter file before parameter " + p.getName());
                                return;
                            }

                            subbasinTo1DArray(p, mapping_param, paramCheck);
                        }
                        
                        
                    } else if (p.getNumDim() == 1 && paramCheck.getNumDim() == 2) {

                        // convert "nmonths" to "nhru,nmonths"
                        if (p.getDimension(0).getName().equals("nmonths")
                                && paramCheck.getDimension(0).getName().equals("nhru")
                                && paramCheck.getDimension(1).getName().equals("nmonths")) {

                            Dimension[] dim = new Dimension[2];
                            dim[0] = mms_params.getDimension("nhru");
                            dim[1] = mms_params.getDimension("nmonths");
                            p.setDimension(dim);

                            Object val;
                            if (p.getType() == Integer.class) {
                                int[] oldVals = (int[]) p.getVals();
                                int[] newVal = new int[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[j];
                                    }
                                }
                                val = newVal;

                            } else if (p.getType() == Float.class) {
                                float[] oldVals = (float[]) p.getVals();
                                float[] newVal = new float[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[j];
                                    }
                                }
                                val = newVal;

                            } else if (p.getType() == Double.class) {
                                double[] oldVals = (double[]) p.getVals();
                                double[] newVal = new double[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[j];
                                    }
                                }
                                val = newVal;

                            } else {
                                String[] oldVals = (String[]) p.getVals();
                                String[] newVal = new String[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[j];
                                    }
                                }
                                val = newVal;
                            }

                            p.setVals(val);
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}\"  expanded!",
                                    new Object[]{p.getName()});

                            // convert "nhru" to "nhru,nmonths"
                        } else if (p.getDimension(0).getName().equals("nhru")
                                && paramCheck.getDimension(0).getName().equals("nhru")
                                && paramCheck.getDimension(1).getName().equals("nmonths")) {

                            Dimension[] dim = new Dimension[2];
                            dim[0] = mms_params.getDimension("nhru");
                            dim[1] = mms_params.getDimension("nmonths");
                            p.setDimension(dim);

                            Object val;
                            if (p.getType() == Integer.class) {
                                int[] oldVals = (int[]) p.getVals();
                                int[] newVal = new int[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[i];
                                    }
                                }
                                val = newVal;

                            } else if (p.getType() == Float.class) {
                                float[] oldVals = (float[]) p.getVals();
                                float[] newVal = new float[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[i];
                                    }
                                }
                                val = newVal;

                            } else if (p.getType() == Double.class) {
                                double[] oldVals = (double[]) p.getVals();
                                double[] newVal = new double[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[i];
                                    }
                                }
                                val = newVal;

                            } else {
                                String[] oldVals = (String[]) p.getVals();
                                String[] newVal = new String[p.getSize()];

                                int k = 0;
                                for (int i = 0; i < dim[0].getSize(); i++) {
                                    for (int j = 0; j < dim[1].getSize(); j++) {
                                        newVal[k++] = oldVals[i];
                                    }
                                }
                                val = newVal;
                            }

                            p.setVals(val);
                            ParamTool.PARAMTOOLLOGGER.log(Level.INFO, "Parameter \"{0}\"  expanded!",
                                    new Object[]{p.getName()});
                        }
                    }  // end of 1D to 2D conversion code

                } else {
//                    System.out.println("parameter " + p.getName() + " does not need to be fixed");
                }
            }
        }
    }

    // This is a hack, but will work as long as the mapping parameter names
    // do not change. Not sure what else to do. This method was converted from
    // the mmf c file "read_params.c".
    private final static String DIMNAMES[] = {"nhru", "nsegment",
        "nrain", "ntemp", "nobs", "ngw",
        "nssr"
    };
    
    private final static String MAPPARAMNAMES[] = {"hru_subbasin", "segment_subbasin",
        "rain_subbasin", "temp_subbasin", "obs_subbasin", "gw_subbasin",
        "ssr_subbasin"
    };

    private String getMapParamName(String name) {
        String mapParamName;
        int i;

        mapParamName = null;
        for (i = 0; i < DIMNAMES.length; i++) {
            if (name.equals(DIMNAMES[i])) {
                mapParamName = MAPPARAMNAMES[i];
            }
        }

        return mapParamName;
    }

// This method was converted from the mmf c file "read_params.c".
    private void subbasinTo1DArray(Parameter p, Parameter mapping_param, Parameter paramCheck) {        
        int[] map = (int[]) mapping_param.getVals();

        Object val;
        if (p.getType() == Double.class) {
//            for (i = 0; i < param - > size; i++) {
//                map = ((int *) (mapping_param - > value))[i];
//                ((double *) (param - > value))[i] = ((double *) pf_value)[map - 1];
//            }
            
            double[] oldVals = (double[]) p.getVals();
            double[] newVal = new double[paramCheck.getSize()];

            for (int i = 0; i < newVal.length; i++) {
                newVal[i] = oldVals[map[i]];
            }
            val = newVal;
            
        } else if (p.getType() == Float.class) {
//            for (i = 0; i < param - > size; i++) {
//                map = ((int *) (mapping_param - > value))[i];
//                ((double *) (param - > value))[i] = ((double *) pf_value)[map - 1];
//            }
            
            float[] oldVals = (float[]) p.getVals();
            float[] newVal = new float[paramCheck.getSize()];

            for (int i = 0; i < newVal.length; i++) {
                newVal[i] = oldVals[map[i]];
            }
            val = newVal;

        } else if (p.getType() == Integer.class) {
//            for (i = 0; i < param - > size; i++) {
//                map = ((int *) (mapping_param - > value))[i];
//                ((int *) (param - > value))[i] = ((int *) pf_value)[map - 1];
//            }

            int[] oldVals = (int[]) p.getVals();
            int[] newVal = new int[paramCheck.getSize()];

            for (int i = 0; i < newVal.length; i++) {
                newVal[i] = oldVals[map[i]];
            }
            val = newVal;

        } else {
//            for (i = 0; i < param - > size; i++) {
//                map = ((int *) (mapping_param - > value))[i];
////          *((char **)param->value + i) = strdup (*pf_value + map - 1);
//                 * ((char * *) param - > value + i) = strdup(pf_value + map - 1);
//            }

            String[] oldVals = (String[]) p.getVals();
            String[] newVal = new String[paramCheck.getSize()];

            for (int i = 0; i < newVal.length; i++) {
                newVal[i] = oldVals[map[i]];
            }
            val = newVal;
        }
        p.setVals(val);
    }
}
