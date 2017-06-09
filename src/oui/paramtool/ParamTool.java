package oui.paramtool;

import gov.usgs.cawsc.apps.GuiProgram;
import gov.usgs.cawsc.gui.WindowFactory;
import java.io.File;
import java.io.IOException;
import java.util.logging.*;
import oui.mms.datatypes.ParameterSet;
import oui.mms.gui.Mms;
import oui.mms.io.MmsDefaultParamsReader;
import oui.mms.io.MmsParamsReader;

public class ParamTool extends GuiProgram{

    private ParamToolGui mpeg;
    public static final Logger paramToolLogger = Logger.getLogger(ParamTool.class.getName());

    {
        try {
            Handler fh = new FileHandler("paramTool.log");
            fh.setFormatter(new SimpleFormatter());
            paramToolLogger.addHandler(fh);
        } catch (IOException ex) {
            paramToolLogger.log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            paramToolLogger.log(Level.SEVERE, null, ex);
        }
    }
    
    public ParamTool(ParameterSet mms_params) {
        super("ParamTool");       
        mpeg = new ParamToolGui(mms_params);
        String title = mms_params.getFileName().substring(Mms.fileNameIndex(mms_params.getFileName()) + 1);
        WindowFactory.displayInFrame(mpeg, title);
    }

    public ParamTool(ParameterSet mms_params, ParameterSet default_params) {
        super("ParamTool");       
        mpeg = new ParamToolGui(mms_params, default_params);
        String title = mms_params.getFileName().substring(Mms.fileNameIndex(mms_params.getFileName()) + 1);
        WindowFactory.displayInFrame(mpeg, title);
    }

    public static void main(String args[]) {
        ParamTool mpe = null;
        if (args.length == 1) {
            File test = new File(args[0]);
            if (test.exists()) {
                try {
                    MmsParamsReader mp = new MmsParamsReader(args[0]);
                    ParameterSet ps = (ParameterSet) (mp.read());
//                ps.writeHistory(false);
                    mpe = new ParamTool(ps);
                } catch (IOException ex) {
                    paramToolLogger.log(Level.SEVERE, null, ex);
                }
            } else {
                usage(args[0]);
            }

            } else if (args.length == 2) {
            try {
                File test = new File(args[0]);
                if (!test.exists()) {
                    usage(args[0]);
                    return;
                }

                test = new File(args[1]);
                if (!test.exists()) {
                    usage(args[1]);
                    return;
                }

                MmsParamsReader mp = new MmsParamsReader(args[0]);
                ParameterSet ps = mp.read();
//                ps.writeHistory(false);
                MmsDefaultParamsReader default_reader = new MmsDefaultParamsReader(args[1]);
                ParameterSet defaults = default_reader.read();
                mpe = new ParamTool(ps, defaults);
            } catch (IOException ex) {
                paramToolLogger.log(Level.SEVERE, null, ex);
            }

            } else {
                usage(null);
                return;
            }

            mpe.setTopLevel(true);
    }

    private static void usage(String fileName) {
        if (fileName != null) {
            System.out.println("File " + fileName + " does not exist!");
        }
        System.out.println("Usage:  oui.paramtool.ParamTool prms_param_file [parameter_name_file]");
    }

    public boolean isTopLevel() {
        return mpeg.isTopLevel();
    }

    public void setTopLevel(boolean topLevel) {
        mpeg.setTopLevel(topLevel);
    }
}