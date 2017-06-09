package oui.mms.datatypes;

public class Dimension {
    private String name;
    private int size;
    private String desc = null;
    private boolean fixed = false;
    private String[] item_names = null;
    private String[] item_desc = null;
    
    public Dimension() {
    }

    public Dimension(String n, int s) {
        name = n;
        size = s;
    }
    
    public Dimension(String n, int s, String d) {
        name = n;
        size = s;
        desc = d;
    }
    
    public String getName() {return (name);}
    public int getSize() {return (size);}
    public String getDesc() {return (desc);}
    public boolean isFixed () {return fixed;}
    public void setFixed (boolean f) {fixed = f;}
    
    public void setSize(int new_size) {
        if (item_names != null) {
            String[] new_item_names = new String[new_size];
            for (int i = 0; i < new_size; i++) {
                if (i < size) {
                    new_item_names[i] = item_names[i];
                } else {
                    new_item_names[i] = item_names[size - 1];
                }
            }
            item_names = null;
            item_names = new_item_names;
        }
        
        if (item_desc != null) {
            String[] new_item_desc = new String[new_size];
            for (int i = 0; i < new_size; i++) {
                if (i < size) {
                    new_item_desc[i] = item_desc[i];
                } else {
                    new_item_desc[i] = item_desc[size - 1];
                }
            }
            item_desc = null;
            item_desc = new_item_desc;
        }
        size = new_size;
    }
    
    public String[] getItemNames () {
        return item_names;
    }
    
    public String[] getItemDesc () {
        return item_desc;
    }

    @Override
    public String toString () {
        return name;
    }
    
    public void addItemName(int i, String in, int size) {
        if (item_names == null) {
            item_names = new String[size];
        }
        item_names[i] = in;
    }
    
    public void addItemDesc(int i, String in, int size) {
        if (item_desc == null) {
            item_desc = new String[size];
        }
        item_desc[i] = in;
    }

    public void setName(String name) {
        this.name = name;
    }
}