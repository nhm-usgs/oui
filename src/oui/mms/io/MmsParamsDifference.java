package oui.mms.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.TreeSet;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

public class MmsParamsDifference {
    private static boolean closeWhenDone = true;
    
    public static void diff(Writer out, ParameterSet mp1, ParameterSet mp2, boolean close) {
        closeWhenDone = close;
        diff(out, mp1, mp2);
    }
    
    public static void diff(Writer out, ParameterSet mp1, ParameterSet mp2) {        
        try {
            out.write("\n");
            out.write("File 1 = " + mp1.getFileName() + "\n");
            out.write("File 2 = " + mp2.getFileName() + "\n");
            out.write("\n");
            
/*
 *  Check dimensions
 */
            Iterator<String> foo = mp1.getDimensionNames();
            TreeSet<String> sorted_dim = new TreeSet<String> ();
            while (foo.hasNext()) {
                sorted_dim.add(foo.next());
            }
            
            foo = mp2.getDimensionNames();
            TreeSet<String>  dim_names2 = new TreeSet<String> ();
            while (foo.hasNext()) {
                dim_names2.add(foo.next());
            }
            
            if (sorted_dim.containsAll(dim_names2) && dim_names2.containsAll(sorted_dim)) {
                out.write("All dimension names match\n");
            } else {
                if (!dim_names2.containsAll(sorted_dim)) {
                    out.write("The following dimensions are in File 1 but not in File 2:\n");
                    out.write("Dimension            File 1\n");
                    out.write("  Name                Size\n");
                    out.write("---------------------------\n");
                    Iterator it = sorted_dim.iterator();
                    while (it.hasNext()) {
                        String dim_name = (String)(it.next());
                        if (!dim_names2.contains(dim_name)) {
                            Dimension dim1 = mp1.getDimension(dim_name);
                            String s = String.format("  %1$-15s  %2$4d\n",
                                    dim_name, dim1.getSize());
                            out.write(s);
                        }
                    }
                }
                
                if (!dim_names2.containsAll(sorted_dim)) {
                    out.write("The following dimensions are in File 2 but not in File 1:\n");
                    out.write("Dimension            File 2\n");
                    out.write("  Name                Size\n");
                    out.write("---------------------------\n");
                    Iterator it = dim_names2.iterator();
                    while (it.hasNext()) {
                        String dim_name = (String)(it.next());
                        if (!sorted_dim.contains(dim_name)) {
                            Dimension dim2 = mp2.getDimension(dim_name);
                            String s = String.format("  %1$-15s  %2$4d\n",
                                    dim_name, dim2.getSize());
                            out.write(s);
                        }
                    }
                }
            }
            
            out.write("\n");
            
            out.write("Dimension        File 1        File 2\n");
            out.write("  Name            Size          Size        Status\n");
            out.write("-----------------------------------------------------\n");
            Iterator it = sorted_dim.iterator();
            while (it.hasNext()) {
                String dim_name = (String) (it.next());
                Dimension dim1 = mp1.getDimension(dim_name);
                Dimension dim2 = mp2.getDimension(dim_name);
                if ((dim1 != null) && (dim2 != null)) {
                    String s = String.format("  %1$-15s  %2$4d          %3$4d\n",
                            dim_name, dim1.getSize(), dim2.getSize());
                    out.write(s);
                    if (dim1.getSize() == dim2.getSize()) {
                        out.write("same\n");
                    } else {
                        out.write("different\n");
                    }
                }
            }
            
/*
 *  Check parameters
 */
            out.write("\n");
            
//            Set<String> param_foo = mp1.getParameterNames();
//            TreeSet<String> sorted_param1 = new TreeSet<String> (param_foo);
            
            foo = mp1.getParameterNames();
            TreeSet<String> sorted_param1 = new TreeSet<String> ();
            while (foo.hasNext()) {
                sorted_param1.add(foo.next());
            }
            
//            Set<String> param_foo2 = mp2.getParameterNames();
//            TreeSet<String> sorted_param2 = new TreeSet<String> (param_foo2);
            
            foo = mp1.getParameterNames();
            TreeSet<String> sorted_param2 = new TreeSet<String>();
            while (foo.hasNext()) {
                sorted_param2.add(foo.next());
            }
            
            if (sorted_param1.containsAll(sorted_param2) && sorted_param2.containsAll(sorted_param1)) {
                out.write("parameter names match\n");
            } else {
                if (!sorted_param2.containsAll(sorted_param1)) {
                    out.write("The following parameters are in File 1 but not in File 2:\n");
                    out.write("Parameter                                   File 1\n");
                    out.write("  Name                Size          Mean             Min            Max\n");
                    out.write("---------------------------------------------------------------------------\n");
                    it = sorted_param1.iterator();
                    while (it.hasNext()) {
                        String param_name = (String)(it.next());
                        if (!sorted_param2.contains(param_name)) {
                            Parameter param1 = mp1.getParameter(param_name);
                            String s = String.format("  %1$-20s  %2$4d %3$15.5f %4$15.5f %5$15.5f\n",
                                    param_name, param1.getSize(), param1.getMean(), param1.getMin(), param1.getMax());
                            out.write(s);
                        }
                    }
                }
                
                if (!sorted_param1.containsAll(sorted_param2)) {
                    out.write("\nThe following parameters are in File 2 but not in File 1:\n");
                    
                    out.write("Parameter                                   File 2\n");
                    out.write("  Name                Size          Mean             Min            Max\n");
                    out.write("---------------------------------------------------------------------------\n");
                    it = sorted_param2.iterator();
                    while (it.hasNext()) {
                        String param_name = (String)(it.next());
                        if (!sorted_param1.contains(param_name)) {
                            Parameter param2 = mp2.getParameter(param_name);
                            String s = String.format("  %1$-20s  %2$4d %3$15.5f %4$15.5f %5$15.5f\n",
                                    param_name, param2.getSize(), param2.getMean(), param2.getMin(), param2.getMax());
                            out.write(s);
                        }
                    }
                }
            }
            
            out.write("\n");
            
            out.write("Parameter                                   File 1                                                          File 2\n");
            out.write("  Name                Size          Mean             Min            Max                 Size          Mean             Min            Max      Status\n");
            out.write("---------------------------------------------------------------------------------------------------------------------------------------------------------\n");
            it = sorted_param1.iterator();
            while (it.hasNext()) {
                String param_name = (String)(it.next());
//                System.out.println("MmsParamsDifference " + param_name);
                Parameter param1 = mp1.getParameter(param_name);
                Parameter param2 = mp2.getParameter(param_name);
                if ((param1 != null) && (param2 != null)) {
                    try {
                        String s = String.format(
                                "%1$-20s  %2$4d %3$15.5f %4$15.5f %5$15.5f              %6$4d %7$15.5f %8$15.5f %9$15.5f    ",
                                param_name, param1.getSize(),
                                param1.getMean(), param1.getMin(), param1.getMax(),
                                param2.getSize(), param2.getMean(), param2.getMin(), param2.getMax());
                        out.write(s);
                    } catch (NullPointerException e) {
                        System.out.println(param_name + " does not have mean, min, or max values.");
                    }

                    if ((param1.getSize() == 0) && (param2.getSize() == 0)) {
                        out.write("same\n");
                    } else if (isSame(param1.getMean(), param2.getMean()) && isSame(param1.getMin(), param2.getMin()) && isSame(param1.getMax(), param2.getMax())) {
                        out.write("same\n");
                    } else {
                        out.write("different\n");
                    }
                }
            }
        
        } catch (IOException e) {

        } finally {
            try {
                out.flush();
                if (closeWhenDone) {
                    out.close();
                }
            } catch (IOException e) {}
        }
    }
    
    private static boolean isSame (double v1, double v2) {
        double tolerence = 0.001;
        
        double diff = v1 - v2;
        
        if (v1 == 0.0) {
            if (v2 == 0.0) return true;
            else return false;
        }
        
        if (Math.abs(diff / v1) < tolerence) return true;
        else return false;

    }
}

