/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package oui.mms.gui.treetable;

import java.util.ArrayList;

/**
 *
 * @author markstro
 */
public class PrmsNode {
    ArrayList<PrmsNode> children = null;
    String name;
    String description;
    String type;
    String size;
    String value;
    String units;

    public PrmsNode(String name, String description, String type, String size, String value, String units) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.size = size;
        this.value = value;
        this.units = units;
    }

    /**
     * Returns the the string to be used to display this leaf in the JTree.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Loads the children, caching the results in the children ivar.
     */
//    protected Object[] getChildren() {
//        if (children != null) {
//            return children;
//        }
////        try {
////            String[] files = file.list();
////            if (files != null) {
////                fileMS.sort(files);
////                children = new PrmsNode[files.length];
////                String path = file.getPath();
////                for (int i = 0; i < files.length; i++) {
////                    File childFile = new File(path, files[i]);
////                    children[i] = new PrmsNode(childFile);
////                }
////            }
////        } catch (SecurityException se) {
////        }
//        return children;
//    }

    public void addChild (PrmsNode node) {
        if (children == null) {
            children = new ArrayList<PrmsNode>();
        }
        children.add(node);
    }

    PrmsNode getChild(int i) {
        if (children == null) {
            return null;
        } else {
            return children.get(i);
        }
    }

    int getChildCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    String getName() {
        return name;
    }

    String getDesc() {
        return description;
    }

    String getSize() {
        return size;
    }

    String getType() {
        return type;
    }

    String getUnits() {
        return units;
    }

    Object getValue() {
        return value;
    }
}
