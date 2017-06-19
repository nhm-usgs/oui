/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.mscb.csvutils;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author markstro
 */
public class TableModelViewer {
    
    public TableModelViewer(TableModel tm) {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new JScrollPane(new JTable(tm)));
        f.setSize(200, 300);
        f.setVisible(true);
    }
}
