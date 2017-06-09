package oui.mms.gui.treetable;

/*
 * %W% %E%
 *
 * Copyright 1997, 1998 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer. 
 *   
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution. 
 *   
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.  
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,   
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

/**
 * FileSystemModel is a TreeTableModel representing a hierarchical file 
 * system. Nodes in the FileSystemModel are PrmsNode which, when they
 * are directory nodes, cache their children to avoid repeatedly querying 
 * the real file system. 
 * 
 * @version %I% %G%
 *
 * @author Philip Milne
 * @author Scott Violet
 */
public class PrmsModel extends AbstractTreeTableModel implements TreeTableModel {

    // Names of the columns.
    static protected String[] cNames = {"Name", "Description", "Type", "Size", "Value", "Units"};

    // Types of the columns.
    static protected Class[] cTypes = {TreeTableModel.class, String.class, String.class, String.class, String.class, String.class};

    public PrmsModel(PrmsNode prmsRoot) {
        super(prmsRoot);
    }

    //
    // The TreeModel interface
    //
    @Override
    public int getChildCount(Object node) {
        PrmsNode prmsNode = ((PrmsNode) node);
        return prmsNode.getChildCount();
    }

    @Override
    public Object getChild(Object node, int i) {
        PrmsNode prmsNode = ((PrmsNode) node);
        return prmsNode.getChild(i);
    }

    //
    //  The TreeTableNode interface. 
    //
    @Override
    public int getColumnCount() {
        return cNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return cNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return cTypes[column];
    }

    @Override
    public Object getValueAt(Object node, int column) {
        PrmsNode prmsNode = (PrmsNode) node;

        switch (column) {
            case 0:
                return prmsNode.getName();
            case 1:
                return prmsNode.getDesc();
            case 2:
                return prmsNode.getType();
            case 3:
                return prmsNode.getSize();
            case 4:
                return prmsNode.getValue();
            case 5:
                return prmsNode.getUnits();
        }

        return null;
    }
}


