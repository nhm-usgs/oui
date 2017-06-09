package oui.mms.datatypes;

public class Parameter implements Comparable {
    private String name;
    private int width = 0;
    private Dimension[] dimensions;
    private int[] size;
    private Class type;
    private Object vals;
    private String desc = "No description available";
    private String module_name = "Not available";
    private String units = "Not available";
    private String low_bound = null;
    private String up_bound = null;
    private String def_val = null;
    private boolean isDefaultFile = false;
    
//    private Object origVals;
    private int total_size = 0;
    
    public Parameter(String name, int width, Dimension[] dimensions, Class type, Object vals) {
        this.name = name;
        this.width = width;
        this.dimensions = dimensions;
        this.type = type;
        this.vals = vals;
                
        size = new int[dimensions.length];
//        System.out.println ("Parameter name = " + name);
        for (int i = 0; i < dimensions.length; i++) {
            size[i] = dimensions[i].getSize();
            if (i == 0) {
                total_size = size[0];
            } else {
                total_size = total_size * size[i];
            }
        }
        
//        if (type == Integer.class) {
//            int[] temp = new int[total_size];
//            for (int i = 0; i < total_size; i++) {
//                temp[i] = ((int[])(vals))[i];
//            }
//            origVals = temp;
//        } else {
//            double[] temp = new double[total_size];
//            for (int i = 0; i < total_size; i++) {
//                temp[i] = ((double[])(vals))[i];
//            }
//            origVals = temp;
//        }
    }
    
    public boolean getIsDefaultFile () {return isDefaultFile;}
    public void setIsDefaultFile (boolean isDefaultFile) {this.isDefaultFile = isDefaultFile;}
    
    static void figureOutIndexes (int index, int[] sizes, int[] indexes) {
        int prev_div = 0;
        
        for (int i = (sizes.length - 1); i > 0; i--) {
            int div = sizes[0];
            for (int j = 1; j < i; j++) {
                div = div * sizes[j];
            }
            indexes[i] = (index - prev_div) / div;
            prev_div = (indexes[i] * div) + prev_div;
        }
        indexes[0] = index - prev_div;
    }
    
    static int figureOutIndex(Dimension[] dimensions, int[] indexes) {
        int index = 0;
        
        for (int i = (dimensions.length - 1); i > 0; i--) {
            int mult = dimensions[0].getSize();
            for (int j = 1; j < i; j++) {
                mult = mult * dimensions[j].getSize();
            }
            index = index + (mult * indexes[i]);
        }
        index = index + indexes[0];
        
        return index;
    }
    
    @Override
    public String toString() {
        return name;
    }
        
    /*
     * Implements oui.util.Parameter
     */
    public String getName() {return (name);}
    public int getWidth() {return (width);}
    public int getNumDim() {return (dimensions.length);}
    public Dimension getDimension(int index) {return (dimensions[index]);}
    public int getSize() {return (total_size);}
    public Class getType() {return (type);}
    public Object getVals() {
        return (vals);
    }
    public void setVals(Object vals) {
        this.vals = vals;
    }
    public void setDesc (String desc) {this.desc = desc;}
    public String getDesc () {return desc;} 
    public void setModule (String module_name) {this.module_name = module_name;}
    public String getModule () {return module_name;}
    public void setUnits (String units) {this.units = units;}
    public String getUnits() {return units;}
    public void setLowBound (String low_bound) {this.low_bound = low_bound;}
    public String getLowBound() {return low_bound;}
    public void setUpBound (String up_bound) {this.up_bound = up_bound;}
    public String getUpBound() {return up_bound;}
    public void setDefaultVal (String def_val) {this.def_val = def_val;}
    public String getDefaultVal() {return def_val;}

    public void  setValueAt (Object val, int index) {
        if (type == Integer.class) {
            int[] foo = (int[])vals;
            
            if (val.getClass() == Integer.class) {
                foo[index] = ((Integer) val).intValue();
            } else if (val.getClass() == String.class) {
                String crap = (String) val;
                foo[index] = Integer.parseInt(crap);
            } else {
                System.out.println("1 MmsParameter:setValueAt type " + val.getClass() + " being put into a Integer");
            }
        } else if (type == Float.class) {
            float[] foo = (float[]) vals;

            if (val.getClass() == Float.class) {
                foo[index] = ((Float) val).floatValue();
            } else if (val.getClass() == String.class) {
                String crap = (String) val;
                foo[index] = Float.parseFloat(crap);
            } else if (val.getClass() == Double.class) {
                foo[index] = (float)(((Double) val).doubleValue());

            } else {
                System.out.println("2 MmsParameter:setValueAt type " + val.getClass() + " being put into a Float");
            }
        } else if (type == Double.class) {
            double[] foo = (double[]) vals;

            if (val.getClass() == Double.class) {
                foo[index] = ((Double) val).doubleValue();
            } else if (val.getClass() == Float.class) {
                foo[index] = ((Float) val).floatValue();
            } else if (val.getClass() == String.class) {
                String crap = (String) val;
                foo[index] = Double.parseDouble(crap);
            } else {
                System.out.println ("3 MmsParameter:setValueAt type " + val.getClass() + " being put into a Double");
            }
        } else { // type == String
            String[] foo = (String[]) vals;
            foo[index] = (String) val;
        }      
    }
    
    public boolean  isDimensionedBy(Dimension dim) {
        for (int i = 0; i < dimensions.length; i++) {
            if (dimensions[i] == dim) return true;
        }
        return false;
    }
    
    public double getMean() {
        if (total_size == 0) {
            return Double.NaN;
        }

        double mean = 0.0;
        if (type == Integer.class) {
            for (int i = 0; i < total_size; i++) {
                mean += ((int[]) vals)[i];
            }
            mean = mean / (double) total_size;
        } else if (type == Double.class) {
            for (int i = 0; i < total_size; i++) {
                mean += ((double[]) vals)[i];
            }
            mean = mean / (double) total_size;

        } else if (type == Float.class) {
            for (int i = 0; i < total_size; i++) {
                mean += ((float[]) vals)[i];
            }
            mean = mean / (float) total_size;

        } else { // (type == String) 
            mean = Double.NaN;
        }
        return mean;
    }
    
    public double getMin() {
        if (isDefaultFile) {
            return Double.parseDouble(getLowBound());
        }

        if (total_size == 0) {
            return Double.NaN;
        }

        double min = Double.POSITIVE_INFINITY;
        if (type == Integer.class) {
            for (int i = 0; i < total_size; i++) {
                if (min > ((int[]) vals)[i]) {
                    min = ((int[]) vals)[i];
                }
            }

        } else if (type == Double.class) {
            for (int i = 0; i < total_size; i++) {
                if (min > ((double[]) vals)[i]) {
                    min = ((double[]) vals)[i];
                }
            }
            
        } else if (type == Float.class) {
            for (int i = 0; i < total_size; i++) {
                if (min > ((float[]) vals)[i]) {
                    min = ((float[]) vals)[i];
                }
            }

        } else { // (type == String) 
            min = Double.NaN;
        }
        return min;
    }
    
    public double getMax() {
        if (isDefaultFile) return Double.parseDouble(getUpBound());
        
        if (total_size == 0) return Double.NaN;

        double max = Double.NEGATIVE_INFINITY;
        if (type == Integer.class) {
            for (int i = 0; i < total_size; i++) {
                if (max < ((int[])vals)[i]) {
                    max = ((int[])vals)[i];
                }
            }
            
        } else if (type == Double.class) {
            for (int i = 0; i < total_size; i++) {
                if (max < ((double[]) vals)[i]) {
                    max = ((double[]) vals)[i];
                }
            }
            
        } else if (type == Float.class) {
            for (int i = 0; i < total_size; i++) {
                if (max < ((float[]) vals)[i]) {
                    max = ((float[]) vals)[i];
                }
            }
        } else { // (type == String) 
            max = Double.NaN;
        }
        return max;
    }
    
    public void resize() {
        
        int new_size = 0;
        for (int i = 0; i < dimensions.length; i++) {
            if (i == 0) {
                new_size = dimensions[0].getSize();
            } else {
                new_size = new_size * dimensions[i].getSize();
            }
        }
        
        if (type == Integer.class) {
            int[] temp = new int[new_size];
            int[] old_index = new int[dimensions.length];
            
            for (int i = 0; i < new_size; i++) {
                temp[i] = Integer.MIN_VALUE;
            }
                
            for (int i = 0; i < total_size; i++) {
//  Figure out old array indexes
                figureOutIndexes (i, size, old_index);
                boolean within_bounds = true;
                for (int j = 0; j < dimensions.length; j++) {
                    if (old_index[j] >= dimensions[j].getSize()) {
                        within_bounds = false;
//                        break;
                    }
                    
                    if (within_bounds) {
                        temp[figureOutIndex(dimensions, old_index)] = ((int[])vals)[i];
                    }
                }
            }
            vals = temp;
            
        } else if (type == Double.class) {
            double[] temp = new double[new_size];
            int[] old_index = new int[dimensions.length];
            
            for (int i = 0; i < new_size; i++) {
                temp[i] = Double.MIN_VALUE;
            }
                
            for (int i = 0; i < total_size; i++) {
//  Figure out old array indexes
                figureOutIndexes (i, size, old_index);
                boolean within_bounds = true;
                for (int j = 0; j < dimensions.length; j++) {
                    if (old_index[j] >= dimensions[j].getSize()) {
                        within_bounds = false;
//                        break;
                    }
                    
                    if (within_bounds) {
                        temp[figureOutIndex(dimensions, old_index)] = ((double[])vals)[i];
                    }
                }
            }
            vals = temp; 
            
            
        } else if (type == Float.class) {
            float[] temp = new float[new_size];
            int[] old_index = new int[dimensions.length];

            for (int i = 0; i < new_size; i++) {
                temp[i] = Float.MIN_VALUE;
            }

            for (int i = 0; i < total_size; i++) {
//  Figure out old array indexes
                figureOutIndexes(i, size, old_index);
                boolean within_bounds = true;
                for (int j = 0; j < dimensions.length; j++) {
                    if (old_index[j] >= dimensions[j].getSize()) {
                        within_bounds = false;
//                        break;
                    }

                    if (within_bounds) {
                        temp[figureOutIndex(dimensions, old_index)] = ((float[]) vals)[i];
                    }
                }
            }
            vals = temp;
            
        } else  { // type == String.class
            String[] temp = new String[new_size];
            int[] old_index = new int[dimensions.length];
            
            for (int i = 0; i < new_size; i++) {
                temp[i] = "";
            }
                
            for (int i = 0; i < total_size; i++) {
//  Figure out old array indexes
                figureOutIndexes (i, size, old_index);
                boolean within_bounds = true;
                for (int j = 0; j < dimensions.length; j++) {
                    if (old_index[j] >= dimensions[j].getSize()) {
                        break;
                    }
                    
                    if (within_bounds) {
                        temp[figureOutIndex(dimensions, old_index)] = ((String[])vals)[i];
                    }
                }
            }
            vals = temp;            
        }
        
        for (int i = 0; i < dimensions.length; i++) {
            size[i] = dimensions[i].getSize();
            if (i == 0) {
                total_size = size[0];
            } else {
                total_size = total_size * size[i];
            }
        }        
    }
    
    /*
     *  Implements java.lang.Comparable
     */
    @Override
    public int compareTo(Object o) {
        if (o.getClass() == Parameter.class) {
            return name.compareTo(((Parameter)o).getName());
        }
        return name.compareTo((String)o);
    }
    
    public static void main(String arg[]) {
        
//        int[] sizes = {4};
//        int index = 4;
//        int[] indexes = new int[1];
//        
//        
//        for (int foo = 0; foo < 4; foo++) {
//            System.out.print(foo);
//             Parameter.figureOutIndexes(foo, sizes, indexes);
//
//            for (int i = 0; i < indexes.length; i++) {
//                System.out.print("  " + indexes[i]);
//            }
//            System.out.println("");
//        }
        
        int[] indexes = new int[3];
        Dimension[] dims = new Dimension[3];
        dims[0] = new Dimension("nlapse", 4);
        dims[1] = new Dimension("nmonth", 3);
        dims[2] = new Dimension ("foo", 2);
        
        for (int ifoo = 0; ifoo < 2; ifoo++) {
            for (int imonth = 0; imonth < 3; imonth++) {
                for (int ilapse = 0; ilapse < 4; ilapse++) {
                    indexes[0] = ilapse;
                    indexes[1] = imonth;
                    indexes[2] = ifoo;
                    System.out.println (Parameter.figureOutIndex(dims, indexes) + " " + indexes[0] + " " + indexes[1] + " " + indexes[2]);
                }
            }
        }
    }

    public void setAllVals(String valString) {
        if (type == Integer.class) {
            int[] foo = (int[]) vals;
            int val =  Integer.parseInt(valString);
            for (int i = 0; i < foo.length; i++) {
                foo[i] = val;
            }

        } else if (type == Double.class) {
            double[] foo = (double[]) vals;
            double val = Double.parseDouble(valString);
            for (int i = 0; i < foo.length; i++) {
                foo[i] = val;
            }
            
        } else if (type == Float.class) {
            float[] foo = (float[]) vals;
            float val = Float.parseFloat(valString);
            for (int i = 0; i < foo.length; i++) {
                foo[i] = val;
            }
            
        } else { // type == String
            String[] foo = (String[]) vals;
            for (int i = 0; i < foo.length; i++) {
                foo[i] = valString;
            }
        }
    }

    public void setDimension(Dimension[] dim) {
        this.dimensions = dim;
        
        size = new int[dimensions.length];
//        System.out.println ("Parameter name = " + name);
        for (int i = 0; i < dimensions.length; i++) {
            size[i] = dimensions[i].getSize();
            if (i == 0) {
                total_size = size[0];
            } else {
                total_size = total_size * size[i];
            }
        }
    }

    public void setType(Class<Double> aClass) {
        this.type = aClass;
    }
}

