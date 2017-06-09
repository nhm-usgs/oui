package oui.mms.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

public class MmsParamsWriter {
    private static final Logger logger = Logger.getLogger(MmsParamsWriter.class.getName());

    public static void write (String out_file_name, ParameterSet mms_params) {
        PrintWriter out = null;
        Iterator<Dimension> dit;
        Iterator<Parameter> pit;
        
/*
 * Determine line separator -- this is needed so the format doesn't change
 * when the file is written back out
 *     PC   is CR + LF  "\r\n"
 *     Unix is LF       "\n"
 */

        String system_eol_style = System.getProperty("line.separator");

        if (mms_params.getEOL() != null) {
            if (mms_params.getEOL().equals("PC")) {
                System.setProperty("line.separator", "\r\n");
            } else {
                System.setProperty("line.separator", "\n");
            }
        } else {
            mms_params.setEOL(system_eol_style);
        }

        OutputStreamWriter osw;
        try {
            if (mms_params.getEncoding() == null) {
                osw = new OutputStreamWriter(new FileOutputStream(out_file_name));
            } else {
                osw = new OutputStreamWriter(new FileOutputStream(out_file_name), Charset.forName(mms_params.getEncoding()));
            }

            out = new PrintWriter(osw);

            out.println(mms_params.getDescription());
            out.println(mms_params.getVersion());

            /*
             *  Write out history
             */
//            if (mms_params.isWriteHistory()) {
//                ArrayList hist =  mms_params.getHistory();
//                it = hist.iterator();
//                while (it.hasNext()) {
//                    ParameterSetHistory mpsh = (ParameterSetHistory)(it.next());
//                    out.write(mpsh.toString() + "\n");
//                }
//            }
            
            out.println("** Dimensions **");

//            Set dim_names = mms_params.getDimensionNames();
            dit = mms_params.getDimenIterator();
            while (dit.hasNext()) {
                out.println("####");
                Dimension dim = dit.next();
                out.println(dim.getName());
                out.println(dim.getSize());
                String[] item_names = dim.getItemNames();
                String[] item_desc = dim.getItemDesc();
                if (item_names != null) {
                    for (int j = 0; j < item_names.length; j++) {
                        if (item_names[j] != null) {
                            out.println(item_names[j]);
                        }
                        if ((item_desc != null) && (item_desc[j] != null)) {
                            out.println(item_desc[j]);
                        }
                    }
                }
            }

            out.println("** Parameters **");

            pit = mms_params.getParamIterator();

            while (pit.hasNext()) {
                out.println("####");
                Parameter param = pit.next();
//                out.println(param.getName() + " " + param.getWidth());
                out.println(param.getName());
                out.println(param.getNumDim());
                out.println(param.getDimension(0).getName());
                if (param.getNumDim() == 2) {
                    out.println(param.getDimension(1).getName());
                }

//  Here's a real hack.  For some reason, if a dimension size is set to zero,
//  the parameter size must be set to one, so there is one value for the parameter.

                if (param.getSize() == 0) {
                    out.println("1");
                } else {
                    out.println(param.getSize());
                }

                if (param.getType() == Integer.class) {
                    out.println("1");

                    int[] val = (int[]) (param.getVals());
//  Here's a real hack.  For some reason, if a dimension size is set to zero,
//  the parameter size must be set to one, so there is one value for the parameter.

                    if (param.getSize() == 0) {
                        out.println("0");
                    } else {
                        for (int j = 0; j < param.getSize(); j++) {
                            out.println(val[j]);
                        }
                    }
                } else if (param.getType() == Float.class) {
                    out.println("2");

                    float[] val = (float[]) (param.getVals());
//  Here's a real hack.  For some reason, if a dimension size is set to zero,
//  the parameter size must be set to one, so there is one value for the parameter.

                    if (param.getSize() == 0) {
                        out.println("0.0");
                    } else {
                        for (int j = 0; j < param.getSize(); j++) {
                            out.println(val[j]);
                        }
                    }
                
                } else if (param.getType() == Double.class) {
                    out.println("3");

                    double[] val = (double[]) (param.getVals());
//  Here's a real hack.  For some reason, if a dimension size is set to zero,
//  the parameter size must be set to one, so there is one value for the parameter.

                    if (param.getSize() == 0) {
                        out.println("0.0");
                    } else {
                        for (int j = 0; j < param.getSize(); j++) {
                            out.println(val[j]);
                        }
                    }
                } else if (param.getType() == String.class) {
                    out.println("4");

                    String[] val = (String[]) (param.getVals());
//  Here's a real hack.  For some reason, if a dimension size is set to zero,
//  the parameter size must be set to one, so there is one value for the parameter.

                    if (param.getSize() == 0) {
                        out.println("????");
                    } else {
                        for (int j = 0; j < param.getSize(); j++) {
                            out.println(val[j]);
                        }
                    }
                } else {
                    logger.log(Level.SEVERE, "Can not determine the type of parameter {0} when writting file {1}", new Object[]{param.getName(), out_file_name});
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error = {0} when writting file {1}", new Object[]{e.getMessage(), out_file_name});

        } finally {
            if (out != null) {
                out.close();
            }
            System.setProperty("line.separator", system_eol_style);
        }
    }

    public static void main(String arg[]) {
        try {
            MmsParamsReader mp1 = new MmsParamsReader("/home/projects/oui/projects/rio_grande/riogr_mms_work/input/params/Abiquiu.xprms_xyz.params");
            ParameterSet ps = mp1.read();
//            System.out.println("Dimensions = " + mp1.getDims());
//            System.out.println("Parameters = " + mp1.getParams());

            MmsParamsWriter.write("/home/projects/oui/projects/rio_grande/riogr_mms_work/input/params/Abiquiu.test.params", ps);

//            MmsParamsReader mp2 = new MmsParamsReader(new java.io.FileReader("/home/projects/oui/projects/rio_grande/riogr_mms_work/input/params/Abiquiu.test.params"));
//            MmsParamsDifference.diff (mp1, mp2);
        } catch (IOException e) {
            System.out.println(arg[0] + " io exception");
        }
    }
}
