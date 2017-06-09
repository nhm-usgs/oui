package oui.mms.datatypes;

import java.util.ArrayList;

public class Control implements Comparable {
    private String name;
//    private int size;
    private int type;  // 1 = long  2 = float  3 = double  4 = string
    private ArrayList<String> vals;
    private ArrayList<String> origVals;
    
    public Control(String name, int type, ArrayList<String> vals) {
        this.name = name;
        this.type = type;
        this.vals  = vals;
//        System.out.println ("Control: name = " + name + " type = " + type + " vals = " + vals);
    }

    
    @Override
    public String toString() {return name;}
        
    /*
     * Implements oui.util.Parameter
     */
    public String getName() {return (name);}
    public int getSize() {return (vals.size());}
    public int getType() {return (type);}
    public ArrayList<String> getVals() {return (vals);}
    public String getSingleVal() {return vals.get(0);}
    public void setVals(ArrayList<String> vals) {this.vals = vals;}

    public void  setValueAt (int index, String val) {
        if (index < vals.size()) {
            vals.set(index, val);
        } else {
            vals.add(index, val);
        }
    }
    
    /*
     *  Implements java.lang.Comparable
     */
    public int compareTo(Object o) {return name.compareTo((String)o);}
}