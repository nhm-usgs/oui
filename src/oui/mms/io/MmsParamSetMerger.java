/*
 * MmsParamSetMerger.java
 *
 * Created on August 18, 2005, 8:46 AM
 *
 */

package oui.mms.io;

import java.util.Iterator;
import oui.mms.datatypes.Dimension;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;

/**
 *
 * @author markstro
 */
public class MmsParamSetMerger {
    public static ParameterSet merge(ParameterSet defualt, ParameterSet update, String fileName) {
        ParameterSet newPs = new ParameterSet();
        newPs.setFileName(fileName);
        newPs.setDescription(defualt.getDescription());
        newPs.setVersion(defualt.getVersion());
//        newPs.addHistory("Updated by ParamSetMerger; source files are: " + defualt.getFileName() + " and " + update.getFileName());
        
        /*
         *  Add dimensions from defualt
         */
//        HashMap<String,Dimension> ps1_dimensions = (HashMap<String,Dimension>)(defualt.getDims());
        Iterator<Dimension> it = defualt.getDimenIterator();
        while (it.hasNext()) {
            Dimension dim = it.next();
            newPs.addDimension(new Dimension(dim.getName(), dim.getSize()));
        }
        
        /*
         * Add parameters from defualt
         */
        Iterator<Parameter> it1 = defualt.getParamIterator();
        while (it1.hasNext()) {
            Parameter par = it1.next();
            Dimension[] dims = new Dimension[par.getNumDim()];
            for (int i = 0; i < par.getNumDim(); i++) dims[i] = newPs.getDimension(par.getDimension(i).getName());
            newPs.addParameter(new Parameter( par.getName(), par.getWidth(), dims, par.getType(), makeVals(par.getVals(), par.getType())));
        }
        
        /*
         * Add or resize dimensions from update
         */
        Iterator<Dimension> it2 = update.getDimenIterator();
        while (it2.hasNext()) {
            Dimension dim2 = it2.next();
            Dimension dim0 = newPs.getDimension(dim2.getName());
            if (dim0 == null) newPs.addDimension(new Dimension( dim2.getName(), dim2.getSize()));
            else if (dim0.getSize() != dim2.getSize()) newPs.setDimension (dim0, dim2.getSize());
        }
        
        /*
         * Add or reset parameters from update
         */
        Iterator<Parameter> it3 = update.getParamIterator();
        while (it3.hasNext()) {
            Parameter param2 = it3.next();
            Parameter param0 = newPs.getParameter(param2.getName());
            
            if (param0 == null) {
                Dimension[] dims = new Dimension[param2.getNumDim()];
                for (int i = 0; i < param2.getNumDim(); i++) dims[i] = newPs.getDimension(param2.getDimension(i).getName());
                newPs.addParameter(new Parameter( param2.getName(), param2.getWidth(), dims, param2.getType(), makeVals (param2.getVals(), param2.getType())));
                
            } else {
                param0.setVals (makeVals (param2.getVals(), param2.getType()));
            }
        }
        
        return newPs;
    }
    
    private static Object makeVals (Object oldVals, Class type) {
        Object vals = null;
        
        if (type == Integer.class) {
            int[] old_vals = (int[])oldVals;
            vals = new int[old_vals.length];
            System.arraycopy(old_vals, 0, (int[]) vals, 0, old_vals.length);
            
        } else if (type == Double.class) {
            double[] old_vals = (double[])oldVals;
            vals = new double[old_vals.length];
            System.arraycopy(old_vals, 0, (double[]) vals, 0, old_vals.length);
            
        } else if (type == Float.class) {
            float[] old_vals = (float[])oldVals;
            vals = new float[old_vals.length];
            System.arraycopy(old_vals, 0, (float[]) vals, 0, old_vals.length);
        }
        
        return vals;
    }
}
