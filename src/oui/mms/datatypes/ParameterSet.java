package oui.mms.datatypes;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.util.ArrayHashMap;

public class ParameterSet {
    private static final Logger logger = Logger.getLogger(ParameterSet.class.getName());
    private final ArrayHashMap<String,Dimension> dimensions = new ArrayHashMap();
    private final ArrayHashMap<String,Parameter> parameters = new ArrayHashMap();
//    private ArrayList<ParameterSetHistory> history = new ArrayList<ParameterSetHistory>();
    private String description, version;
    private final boolean writeHistory = false;
    private String encoding = null;
    private String eol_style = null;
    private File file;

    public ParameterSet() {
        this.version = "1.7";
    }
    
    public ArrayList getParamsFor2DDim(String dim_name) {
        ArrayList<Parameter> params = new ArrayList(20);
        
        String dim1_name = dim_name.substring(0,dim_name.indexOf(','));
        String dim2_name = dim_name.substring(dim_name.indexOf(',') + 1,dim_name.length());
        
        Dimension dim1 = dimensions.get(dim1_name);
        Dimension dim2 = dimensions.get(dim2_name);
        
        for (Iterator<Parameter> i = parameters.iterator(); i.hasNext();) {
            Parameter p = (i.next());

            if ((p.isDimensionedBy(dim1)) && (p.isDimensionedBy(dim2))) {
                params.add(p);
            }
        }
        return (params);
    }
    
    public TreeSet get2DCombos() {
        TreeSet<String> params = new TreeSet();
        
        for (Parameter p : parameters) {
            if (p.getNumDim()== 2) {
                String foo = p.getDimension(0).getName() + "," + p.getDimension(1).getName();
                params.add(foo);
            }
        }
        return (params);
    }
    
    /*
     *  Implement ParameterSet interface
     */
    
    public Parameter getParameter(String name) {
        return parameters.get(name);
    }

    public Dimension getDimension(String name) {
        return dimensions.get(name);
    }
//    public Map<String,Dimension> getDims() {return dimensions;}
//    public Map<String,Parameter> getParams() {return parameters;}
    public String getEncoding () {return encoding;}
    public void setEncoding (String encoding) {this.encoding = encoding;}
    public String getEOL() {return eol_style;}
    public void setEOL (String eol_style) {this.eol_style = eol_style;}
    
    public Set getParamsForDim(Dimension dim) {
        TreeSet<Parameter> params = new TreeSet();
        
        for (Parameter p : parameters) {
            if (p.isDimensionedBy(dim)) {
                params.add(p);
            }
        }
        return (params);
    }
       
    public Set get1DParamsForDim(Dimension dim) {
        TreeSet<Parameter> params = new TreeSet();
        
        for (Parameter p : parameters) {
            if (p.getNumDim() == 1 && p.isDimensionedBy(dim)) {
                params.add(p);
            }
        }
        return (params);
    }
        
    public void addDimension(Dimension dim) {
        dimensions.put(dim.getName(), dim);
    }
    
    public void setDimension(Dimension dim, int size) {
        if (dim.getSize() == size) return;
        
        dim.setSize(size);
        
        Set param_set = getParamsForDim(dim);
        for (Iterator<Parameter> i = param_set.iterator(); i.hasNext();) {
            Parameter param = i.next();
            param.resize();
        }
                
//        history.add (new ParameterSetHistory (dim, "changed size to " + size));
    }
     
    public void setParameterValues(Parameter param, Object vals) {
        param.setVals(vals);
//        history.add (new ParameterSetHistory (param, "changed array values"));
    }
    
    public void setParameterValue(Parameter param, Object val, int index) {
        param.setValueAt(val, index);
//        history.add (new ParameterSetHistory (param, "changed value at index " + index));
    }    
    
    public void addParameter (Parameter parameter) {
        /*
         *  Check to make sure that all of the dimensions that this parameter uses
         *  are in the dimension map.  Add them if they are not.
         */
        int num_dim = parameter.getNumDim();
        for (int i = 0; i < num_dim; i++) {
            if (!dimensions.containsKey(parameter.getDimension(i).getName())) {
                addDimension (parameter.getDimension(i));
            }
        }
        
        /*
         *  See if this parameter is already in the parameter set.  Remove it
         *  if it is.
         */
        
        Parameter param = getParameter (parameter.getName());
        if (param != null) {
            String foobar = "Parameter " + parameter.getName() +
                    " has already been read from " + file.getAbsolutePath();
            logger.log(Level.WARNING, foobar);
            parameters.remove(parameter.getName());
        }
        
        parameters.put(parameter.getName(), parameter);

//        Object[] o = parameters.values().toArray();
//        parameterArray = new Parameter[parameters.size()];
//        for (int i = 0; i < parameters.size(); i++) {
//            parameterArray[i] = (Parameter)(o[i]);
//        }
    }
    
    public Object getValues(Dimension dim) {
        int[] i_a = new int[1];
        i_a[0] = dim.getSize();
        return i_a;
    }
    
    public Object getValues(Parameter param) {
        return param.getVals();
    }
        
    public Object getValues(String name) {
        Object ret;
        
        Parameter mp = getParameter (name);
        if (mp != null) {
            ret = getValues(mp);
            
        } else {
            Dimension md = getDimension (name);
            ret = getValues(md);
        }
        return ret;
    }
    
    public String getFileName () {
        return file.getAbsolutePath();
    }
    
    /**
     * Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public String getVersion() {
        return this.version;
    }
    
    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Iterator<String> getDimensionNames() {
        return dimensions.keyIterator();
    }
    
    public Iterator<String> getParameterNames() {
        return parameters.keyIterator();
    }
    
    @Override
    public String toString() {
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return "MMS Parameters";
        }
    }
    
//    public Dimension[] getDimensionArray () {
//        return dimensionArray;
//    }
    
//    public Parameter[] getParameterArray () {
//        return parameterArray;
//    }
    
    public String[] getEditableDimensionArray() {
        TreeSet<String> dim_set = new TreeSet();
        
        for (Parameter p : parameters) {
            if (p.getSize() > 0) {
                String foo = "";
                for (int j = 0; j < p.getNumDim(); j++) {
                    if (j != 0) foo = foo + ",";
                    foo = foo + p.getDimension(j).getName();
                }
                dim_set.add(foo);
            }
        }
        Object[] objs = dim_set.toArray();
        String[] ret = new String[objs.length];
        for (int i = 0; i < objs.length; i++) {
            ret[i] = (String)objs[i];
        }
        return ret;
    }  

    /**
     * Getter for property history.
     * @return Value of property history.
     */
//    public ArrayList getHistory() {
//        return this.history;
//    }
//
//    public void addHistory(String hist_line) {
//        if (writeHistory) this.history.add (new ParameterSetHistory (hist_line));
//    }
//
//    public void addHistory(Object what, String comment) {
//        if (writeHistory) this.history.add(new ParameterSetHistory(what, comment));
//    }
//
//    public void writeHistory(boolean write) {
//        if (writeHistory) this.writeHistory = write;
//    }
//
//    public boolean isWriteHistory() {
//        return this.writeHistory;
//    }
//
//    public void setWriteHistory(boolean writeHistory) {
//        this.writeHistory = writeHistory;
//    }
    
    public String getDirectory() {
        return file.getParent();
//        return fileName.substring(0, Mms.fileNameIndex(fileName));
    }
    
    public String getShortFileName() {
        return file.getName();
//        return fileName.substring(Mms.fileNameIndex(fileName)+1, fileName.length());
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileName(String filename) {
        setFile (new File (filename));
    }

    public void remove(Dimension dim) {

        // Remove the parameters which use this dimension
        Set paramsForDim = getParamsForDim(dim);
        Iterator iterator = paramsForDim.iterator();
        while (iterator.hasNext()) {
             parameters.remove(((Parameter)(iterator.next())).getName());
        }

        dimensions.remove(dim.getName());
    }

    public void remove(Parameter mms_param) {
        parameters.remove(mms_param.getName());
    }

    Dimension[] dimensionArray = null;
    public Dimension getDimensionAt(int rowIndex) {
        if (dimensionArray == null) {
            dimensionArray = new Dimension[dimensions.size()];
            dimensions.toArray(dimensionArray);
        }
        return dimensionArray[rowIndex];
    }

    public boolean isDimsEmpty() {
        return dimensions.isEmpty();
    }

    public boolean isParamsEmpty() {
        return parameters.isEmpty();
    }

    public Iterator<Dimension> getDimenIterator() {
        return dimensions.iterator();
    }

    public Iterator<Parameter> getParamIterator() {
        return parameters.iterator();
    }

    public int getNumberOfDimensions() {
        return dimensions.size();
    }

    public int getNumberOfParameters() {
        return parameters.size();
    }

//    public Parameter getParameterAt(int i) {
//        return parameters.getValue(i);
//    }

    public ArrayHashMap getParameters () {
        return parameters;
    }

//    public void addDimension(Dimension dim) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void setDimension(Dimension dim, int size) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void addParameter(Parameter parameter) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public Object getValues(Parameter param) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void setParameterValues(Parameter param, Object vals) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public void setParameterValue(Parameter param, Object val, int index) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    public ArrayHashMap<String,Dimension> getDimensions() {
        return dimensions;
    }
}

