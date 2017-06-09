/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oui.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsDefaultParamsReader;
import oui.mms.io.MmsParamsDifference;
import oui.mms.io.MmsParamsReader;
import gov.usgs.mscb.paramfile2csv.ParamFile2Csv;

/**
 *
 * @author markstro
 */
public class ParamChecker {

    public ParamChecker(File defaultFile, File paramFile) {
        try {
            MmsParamsReader mp = new MmsParamsReader(paramFile.getCanonicalPath());
            ParameterSet mms_params = mp.read();
            //                ps.writeHistory(false);
            MmsDefaultParamsReader default_reader = new MmsDefaultParamsReader(defaultFile.getCanonicalPath());
            ParameterSet default_ps = default_reader.read();

            String[] split = paramFile.getName().split("\\.");
            String path = paramFile.getParent();
            File newParamFile = new File(path, split[0] + "_report." + split[1]);

            FileWriter fileWriter = new FileWriter(newParamFile);
            fileWriter.write("\n\n======================  PART 1  =======================\n");
            fileWriter.write("Difference Report.\n\n");
            MmsParamsDifference.diff(fileWriter, mms_params, default_ps, false);

            fileWriter.write("\n\n======================  PART 2  =======================\n");
            fileWriter.write("List parameters that are in the parameter file, but not in the par_name file.\n\n");

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
                        fileWriter.write("Parameter " + p.getName() + " is in the parameter file, but is not used by this PRMS application.\n");
                    }
                }
            }

            fileWriter.write("\n\n======================  PART 3  =======================\n");
            fileWriter.write("List parameters that are in the par_name file, but not in the parameter file.\n\n");
            Iterator<Parameter> paramIterator = default_ps.getParamIterator();
            while (paramIterator.hasNext()) {
                Parameter p = paramIterator.next();
                Parameter paramCheck = mms_params.getParameter(p.getName());

                if (paramCheck == null) {
                    fileWriter.write("Parameter " + p.getName() + " is used by this PRMS application, but is not in the parameter file.\n");
                }
            }

            fileWriter.write("\n\n======================  PART 4  =======================\n");
            fileWriter.write("Missing values\n\n");

            // look for missing values  
            System.out.println();

            paramIterator = mms_params.getParamIterator();
            while (paramIterator.hasNext()) {
                Parameter p = paramIterator.next();

                if (p.getType() == Integer.class) {
                    int[] val = (int[]) (p.getVals());

                    for (int i = 0; i < val.length; i++) {
                        if (val[i] == -999) {
                            fileWriter.write("Parameter " + p.getName() + " is specified with the missing value flag.\n");
                        }
                    }

                } else if (p.getType() == Float.class) {
                    float[] val = (float[]) (p.getVals());

                    for (int i = 0; i < val.length; i++) {
                        if (val[i] < -998.0 && val[i] > -1000.0) {
                            fileWriter.write("Parameter " + p.getName() + " is specified with the missing value flag.\n");
                        }
                    }
                    
                } else if (p.getType() == Double.class) {
                    double[] val = (double[]) (p.getVals());

                    for (int i = 0; i < val.length; i++) {
                        if (val[i] < -998.0 && val[i] > -1000.0) {
                            fileWriter.write("Parameter " + p.getName() + " is specified with the missing value flag.\n");
                        }
                    }

                } else if (p.getType() == String.class) {
                    String[] val = (String[]) (p.getVals());

                    for (int i = 0; i < val.length; i++) {
                        if (val[i].contains("-999")) {
                            fileWriter.write("Parameter " + p.getName() + " is specified with the missing value flag.\n");
                        }
                    }
                }
            }

            fileWriter.write("\n\n======================  PART 5  =======================\n");
            fileWriter.write("Values out of bounds\n\n");

            paramIterator = mms_params.getParamIterator();
            while (paramIterator.hasNext()) {
                Parameter p = paramIterator.next();

                if (p.getType() == Integer.class) {
                    try {
                        int[] val = (int[]) (p.getVals());
                        int lo = Integer.parseInt(default_ps.getParameter(p.getName()).getLowBound());
                        int hi = Integer.parseInt(default_ps.getParameter(p.getName()).getUpBound());

                        for (int i = 0; i < val.length; i++) {
                            if (val[i] < lo) {
                                fileWriter.write("Parameter " + p.getName() + " is < lower bound.\n");

                            } else if (val[i] > hi) {
                                fileWriter.write("Parameter " + p.getName() + " is > upper bound.\n");
                            }
                        }
                    } catch (Exception e) {
                    }
                    
                } else if (p.getType() == Float.class) {
                    try {
                        int[] val = (int[]) (p.getVals());
                        float lo = Float.parseFloat(default_ps.getParameter(p.getName()).getLowBound());
                        float hi = Float.parseFloat(default_ps.getParameter(p.getName()).getUpBound());
                        for (int i = 0; i < val.length; i++) {
                            if (val[i] < lo) {
                                fileWriter.write("Parameter " + p.getName() + " is < lower bound.\n");

                            } else if (val[i] > hi) {
                                fileWriter.write("Parameter " + p.getName() + " is > upper bound.\n");

                            }
                        }
                    } catch (Exception e) {
                    }
                    
                } else if (p.getType() == Double.class) {
                    try {
                        double[] val = (double[]) (p.getVals());
                        double lo = Double.parseDouble(default_ps.getParameter(p.getName()).getLowBound());
                        double hi = Double.parseDouble(default_ps.getParameter(p.getName()).getUpBound());
                        for (int i = 0; i < val.length; i++) {
                            if (val[i] < lo) {
                                fileWriter.write("Parameter " + p.getName() + " is < lower bound.\n");

                            } else if (val[i] > hi) {
                                fileWriter.write("Parameter " + p.getName() + " is > upper bound.\n");

                            }
                        }
                    } catch (Exception e) {
                    }
                } else if (p.getType() == String.class) {
                }
            }

            fileWriter.write("\n\n======================  PART 6  =======================\n");
            fileWriter.write("Check soil_rechr_max > soil_moist_max\n\n");
            // Check soil_rechr_max > soil_moist_max.
            System.out.println();

            Parameter smm = mms_params.getParameter("soil_moist_max");
            double[] smmVals = (double[]) (smm.getVals());

            Parameter srm = mms_params.getParameter("soil_rechr_max");
            double[] srmVals = (double[]) (srm.getVals());

            for (int i = 0; i < srmVals.length; i++) {
                if (srmVals[i] > smmVals[i]) {
                    fileWriter.write("soil_rechr_max[" + (i + 1) + "] is greter than soil_moist_max[" + (i + 1) + "]\n");
                }
            }

            fileWriter.write("\n\n======================  PART 7  =======================\n");
            fileWriter.write("Check soil_rechr_init > soil_rechr_max\n\n");
            // Check soil_rechr_init > soil_rechr_max.
            System.out.println();

            Parameter sri = mms_params.getParameter("soil_rechr_init");
            double[] sriVals = (double[]) (sri.getVals());

            srm = mms_params.getParameter("soil_rechr_max");
            srmVals = (double[]) (srm.getVals());

            for (int i = 0; i < srmVals.length; i++) {
                if (sriVals[i] > srmVals[i]) {
                    fileWriter.write("soil_rechr_init[" + (i + 1) + "] is greter than soil_rechr_max[" + (i + 1) + "]\n");
                }
            }

            fileWriter.write("\n\n======================  PART 8  =======================\n");
            fileWriter.write("Check soil_moist_max > soil_moist_init\n\n");
            // Check soil_moist_max > soil_moist_init.

            Parameter smi = mms_params.getParameter("soil_moist_init");
            double[] smiVals = (double[]) (smi.getVals());

            smm = mms_params.getParameter("soil_moist_max");
            smmVals = (double[]) (smm.getVals());

            for (int i = 0; i < smmVals.length; i++) {
                if (smiVals[i] > smmVals[i]) {
                    fileWriter.write("soil_moist_init[" + (i + 1) + "] is greter than soil_moist_max[" + (i + 1) + "]\n");
                }
            }


            fileWriter.write("\n\n======================  PART 9  =======================\n");
            fileWriter.write("muskingum routing coeficients\n\n");
            // Check muskingum routing coeficients.
            System.out.println();
            Parameter k = mms_params.getParameter("K_coef");
            double[] kVals = null;
            if (k != null) {
                kVals = (double[]) (k.getVals());
                for (int i = 0; i < kVals.length; i++) {
                    if (kVals[i] < 1.0) {
                        fileWriter.write("K_coef[" + (i + 1) + "] is less than 1.0\n");
                    }
                }
            }

            System.out.println();
            Parameter x = mms_params.getParameter("x_coef");
            double[] xVals = null;
            if (x != null) {
                xVals = (double[]) (x.getVals());
                for (int i = 0; i < xVals.length; i++) {
                    if (xVals[i] > 0.5) {
                        fileWriter.write("x_coef[" + (i + 1) + "] is greater than 0.5\n");
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
                    if (kVals != null) {
                        if (xVals[i] > x_max) {
                            xVals[i] = x_max * 0.99;
                            fileWriter.write("x_coef[" + (i + 1)
                                    + "] s too large for the internal time step, reset value to " + xVals[i] + "\n");
                        }
                    }
                }
            }

            fileWriter.write("\n\n======================  PART 10  =======================\n");
            fileWriter.write("Check for all parameters values set to the same value\n\n");

            paramIterator = mms_params.getParamIterator();
            while (paramIterator.hasNext()) {
                Parameter p = paramIterator.next();

                if (p.getSize() > 1) {

                    if (p.getType() == Integer.class) {
                        try {
                            int[] val = (int[]) (p.getVals());
                            boolean allTheSame = true;
                            for (int i = 1; i < val.length; i++) {
                                if (val[i] != val[0]) {
                                    allTheSame = false;
                                    break;
                                }
                            }

                            if (allTheSame) {
                                fileWriter.write("All values of parameter " + p.getName() + " are set to " + val[0] + "\n");
                            }
                        } catch (Exception e) {
                        }

                    } else if (p.getType() == Float.class) {
                        try {
                            float[] val = (float[]) (p.getVals());
                            boolean allTheSame = true;
                            for (int i = 1; i < val.length; i++) {
                                if (val[i] != val[0]) {
                                    allTheSame = false;
                                    break;
                                }
                            }

                            if (allTheSame) {
                                fileWriter.write("All values of parameter " + p.getName() + " are set to " + val[0] + "\n");
                            }
                        } catch (Exception e) {
                        }
                    } else if (p.getType() == Double.class) {
                        try {
                            double[] val = (double[]) (p.getVals());
                            boolean allTheSame = true;
                            for (int i = 1; i < val.length; i++) {
                                if (val[i] != val[0]) {
                                    allTheSame = false;
                                    break;
                                }
                            }

                            if (allTheSame) {
                                fileWriter.write("All values of parameter " + p.getName() + " are set to " + val[0] + "\n");
                            }
                        } catch (Exception e) {
                        }
                    } else if (p.getType() == String.class) {
                        try {
                            String[] val = (String[]) (p.getVals());
                            boolean allTheSame = true;
                            for (int i = 1; i < val.length; i++) {
                                if (val[i].equals(val[0])) {
                                    allTheSame = false;
                                    break;
                                }
                            }

                            if (allTheSame) {
                                fileWriter.write("All values of parameter " + p.getName() + " are set to " + val[0] + "\n");
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(ParamChecker.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(ParamChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
   //        args[0] = "E:\applications\southPlatteV2\GIS\shapes\10Lnhru"; // shapefile with out the .shp extention
   //        args[1] = "hru_id";
   //        args[2] = "E:\applications\southPlatteV2\GIS\Region10Lwdefaults.wpar";
   //        args[3] = "nhru";
   //        args[4] = "E:\applications\southPlatteV2\southPlattePrms\control\10L.control.par_name";
    
    public static void main(String args[]) {
        File paramFile = new File(args[2]);
        File defaultFile = new File(args[4]);
        ParamChecker b = new ParamChecker(defaultFile, paramFile);
        
        
        // This is a good idea, but NHDplus regions are just too big to load into a OUI map
//        ParamMapper paramMapper = new ParamMapper (args);
        
        File destDir = new File (paramFile.getParent() + "_csv");
        destDir.mkdirs();
        ParamFile2Csv paramFile2Csv = new ParamFile2Csv(paramFile, destDir);
    }
}

