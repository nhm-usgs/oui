/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.cawsc.apps;

import gov.usgs.cawsc.gui.WindowFactory;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * @author jmd
 */
public class GuiProgram extends ApplicationProgram implements ActionListener, WindowListener,
    ComponentListener {
    
    /**
     * The constructor for the application
     * 
     * @param programName The name of the program
     */
    public GuiProgram(String programName)
    {
        super();

        // This causes JOptionPane to treat the button with focus as the default button, so
        // users can select it using Tab and
        // Enter.
        UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);

        // Set up the program with all the bells and whistles...
        createFrame(programName, true);

        registerListeners();
        //setIcon();
        registerMessageObserver();
    }

    /**
     * Create a new frame and use it as the frame for the program.
     * 
     * @param name The name to put on the frame.
     * @param registerListeners Whether to register this object as a window listener of the
     *            frame, causing a program exit when it is closed.
     * @return The frame that was created.
     */
    public JFrame createFrame(String name, boolean registerListeners)
    {
        JFrame frame = new JFrame(name);
        WindowFactory.instance().setMainFrame(frame);
        frame.addComponentListener(this);

        return frame;
    }

    /**
     * Set the frame that will be used by the program.
     * 
     * @param frame The frame
     * @param registerListeners Whether to register this object as a window listener of the
     *            frame, causing a program exit when it is closed.
     */
    public void setFrame(JFrame frame, boolean registerListeners)
    {
            registerListeners();
    } 
    
    /**
     * Register this object as the window listener of the frame, so that the program will exit
     * upon closing.
     */
    public void registerListeners()
    {

        // add the window listener for closing the window
        WindowFactory.instance().getMainFrame().addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent aEvent)
            {
                System.exit(0);
            }
        });

    }

    /**
     * Set the icon in the window.
     */
    public void setIcon(String iconPath)
    {
        // Set the icon image
        WindowFactory.instance().getMainFrame().setIconImage(new ImageIcon(iconPath).getImage());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {

    }
    
    /**
     * Register this object as an observer of the ApplicationMessageHandler.
     */
    public void registerMessageObserver()
    {

        // add this class as the application observer class
    //    ApplicationMessageHandler.instance().setObserver(this);
    }

    @Override
    public void windowClosed(WindowEvent event)
    {
    }

    @Override
    public void windowOpened(WindowEvent event)
    {
    }

    @Override
    public void windowDeiconified(WindowEvent event)
    {
    }

    @Override
    public void windowIconified(WindowEvent event)
    {
    }

    @Override
    public void windowActivated(WindowEvent event)
    {
    }

    @Override
    public void windowDeactivated(WindowEvent event)
    {
    }

    @Override
    public void windowClosing(WindowEvent event)
    {
        System.exit(0);
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        // Save the new size to the preferences, so it can be read on the next start up.
        Component comp = (Component) e.getSource();
        this.preferences.putInt("width", comp.getWidth());
        this.preferences.putInt("height", comp.getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
        // Save the new size to the preferences, so it can be read on the next start up.
        Component comp = (Component) e.getSource();
        Rectangle rect = comp.getBounds();
        this.preferences.putInt("x", rect.x);
        this.preferences.putInt("y", rect.y);
    }

}
