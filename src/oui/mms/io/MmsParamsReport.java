package oui.mms.io;

import gov.usgs.cawsc.gui.WindowFactory;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.util.ReportPanel;

public class MmsParamsReport {
    public static void write(Writer out, ParameterSet mp1) {
        try {
            out.write("\n");
            out.write("File = " + mp1.getFileName() + "\n");
            out.write("\n");
            /*
             *  Check dimensions
             */
            //            Set dim_names1 = mp1.getDimensionNames();
            Iterator<String> foo = mp1.getDimensionNames();
            TreeSet<String> dim_names1 = new TreeSet<>();
            while (foo.hasNext()) {
                dim_names1.add(foo.next());
            }

            
            out.write("\n");
            
            out.write("Dimension\n");
            out.write("  Name            Size\n");
            out.write("---------------------------\n");
            Iterator it = dim_names1.iterator();
            while (it.hasNext()) {
                String dim_name = (String)(it.next());
                Dimension dim1 = mp1.getDimension(dim_name);
                if (dim1 != null) {
                    String s = String.format("%1$-15s  %2$4d", dim_name, dim1.getSize());
                    out.write (s + "\n");
                }
            }
            
/*
 *  Check parameters
 */
            out.write("\n\n\n");
            
//            Set param_names1 = mp1.getParameterNames();
            foo = mp1.getParameterNames();
            TreeSet<String> param_names1 = new TreeSet<>();
            while (foo.hasNext()) {
                param_names1.add(foo.next());
            }
            
            
            out.write("Parameter\n");
            out.write("  Name                Size          Mean             Min            Max\n");
            out.write("---------------------------------------------------------------------------\n");
            it = param_names1.iterator();
            while (it.hasNext()) {
                String param_name = (String) (it.next());
                Parameter param1 = mp1.getParameter(param_name);
                String s = String.format("%1$-20s  %2$4d %3$15.5f %4$15.5f %5$15.5f", param_name, param1.getSize(), param1.getMean(), param1.getMin(), param1.getMax());
                out.write(s + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(MmsParamsReport.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
            }
        }
    }

    public static void writeHistory(Writer out, ParameterSet mp1) {
        try {
            out.write("\n");
            out.write("File = " + mp1.getFileName() + "\n");
            out.write("\n");

        } catch (IOException ex) {
            Logger.getLogger(MmsParamsReport.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
            }
        }
    }
    
    public static void main(String arg[]) {
        try {
            MmsParamsReader mp1 = new MmsParamsReader(arg[0]);
            ParameterSet ps1 = mp1.read();
            ReportPanel rf = new ReportPanel();
            WindowFactory.displayInFrame(rf, "Report");
            
            MmsParamsReport.write (rf.getWriter(), ps1);

        } catch (java.io.IOException e) {
            System.out.println(arg[0] + " io exception");
        }

    }
    
}

