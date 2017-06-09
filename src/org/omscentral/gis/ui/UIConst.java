package org.omscentral.gis.ui;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Component;
import java.util.Enumeration;
import java.util.Properties;


import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.BoxLayout;


public class  UIConst {

    // Dimensions
    public final static Dimension hpad5 = new Dimension(5,1);
    public final static Dimension hpad10 = new Dimension(10,1);
    public final static Dimension vpad5 = new Dimension(1,5);
    public final static Dimension vpad10 = new Dimension(1,10);
    public final static Dimension vpad20 = new Dimension(1,20);

    // Fonts
    public final static Font defaultFont = new Font("Dialog", Font.PLAIN, 12);
    public final static Font boldFont = new Font("Dialog", Font.BOLD, 12);
    public final static Font smallFont = new Font("Dialog", Font.PLAIN,10);

    // Borders
    public final static EmptyBorder border2 = new EmptyBorder(2,2,2,2);
    public final static EmptyBorder border5 = new EmptyBorder(5,5,5,5);
    public final static EmptyBorder border10 = new EmptyBorder(10,10,10,10);
    public final static EmptyBorder border15 = new EmptyBorder(15,15,15,15);
    public final static EmptyBorder border20 = new EmptyBorder(20,20,20,20);

    public final static Border loweredBorder = new SoftBevelBorder(BevelBorder.LOWERED);

    public static void showErrorBox(Component parent, String msg) {

        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarningBox(Component parent, String msg) {

        JOptionPane.showMessageDialog(parent, msg, "Warning", JOptionPane.INFORMATION_MESSAGE);
    }

     public static JPanel createVerticalPanel() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    public static JPanel createHorizontalPanel() {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }

    public static void centerDialog(JFrame parent, JDialog dialog) {

        Dimension fSize = parent.getSize();
        Dimension dSize = dialog.getSize();
        Point fPoint = parent.getLocation();
        dialog.setLocation (fPoint.x + (fSize.width - dSize.width) / 2,
                            fPoint.y + (fSize.height - dSize.height) / 2);
    }

    public static Object[][] createPropertyArray(Properties p) {

        int size = p.size();
        Object t[][] = new Object[size][];
        int i=0;
        for(Enumeration e = p.keys() ; e.hasMoreElements(); i++)  {
            t[i] = new Object[2];
            Object k = e.nextElement();
            t[i][0] = k;
            t[i][1] = p.get(k);
        }
        return t;
    }

}


