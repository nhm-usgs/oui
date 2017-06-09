/*
 * MmsSchemPanel.java
 *
 * Created on July 18, 2005, 10:48 AM
 */

package oui.mms.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 *
 * @author  markstro
 */
public class MmsSchemPanel extends JPanel {
    private MmsSchematic ms;    
    
    /** Creates a new instance of MmsSchemPanel */
    public MmsSchemPanel(Mms mms) {
        String schemFileName = mms.getNameFileBase() + ".mod_name";
//        String schemFileName = mms.getControlMap().getFileName() + ".mod_name";
        ms = new MmsSchematic(schemFileName);
        
        draw();
    }
    
    public MmsSchemPanel(String fileName) {
         ms = new MmsSchematic(fileName);

        draw();
    }
    
    public final void draw () {
                

 
/*
 * draw buttons
 */
        ArrayList modules = ms.getModules();
        Iterator it = modules.iterator();
        while (it.hasNext()) {
            MmsSchematic.MmsModule mm = (MmsSchematic.MmsModule)(it.next());
//@todo layout manager problem with drawing schematic
            this.add (mm.getButton(), new org.netbeans.lib.awtextra.AbsoluteConstraints(mm.getX(), mm.getY(), -1, -1));
        }
    }

    @Override
    public void paint( Graphics g ) {
        int width = getSize().width;
        int height = getSize().height;
/*
 *  Color the background
 */
        g.setColor(new Color (112, 128, 144));		// darkSlateGray
        g.fillRect( 0, 0, width,  height );

/*
 * draw lines
 */
        g.setColor( Color.white);
        ArrayList modules = ms.getModules();
        Iterator it = modules.iterator();
        while (it.hasNext()) {
            MmsSchematic.MmsModule mm = (MmsSchematic.MmsModule)(it.next());
            
            for (int i = 0; i < mm.getNumConnections(); i++) {
                MmsSchematic.MmsModule dest = ms.getModuleAt(mm.getConnections()[i]);
                g.drawLine(mm.getCenterX(), mm.getCenterY(), dest.getCenterX(), dest.getCenterY());
            }
        }       
        
/*
 * repaint buttons
 */
        it = modules.iterator();
        while (it.hasNext()) {
            MmsSchematic.MmsModule mm = (MmsSchematic.MmsModule)(it.next());
            mm.getButton().repaint();
        }       
    }
}
