package gov.usgs.cawsc.gui;

import gov.usgs.cawsc.gui.WindowFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

/**
 * Utility and convenience methods for use with graphical user interfaces
 * <p>
 * United States Department of Interior U.S. Geological Survey Water Resources Division National
 * Water Information System
 * 
 * @author John M. Donovan
 */
public class GuiUtilities
{
    /** USGS green */
    public static Color USGS_GREEN = new Color(0, 111, 65);

    /** Light USGS green (50% bleach) */
    public static Color LIGHT_USGS_GREEN_50 = new Color(0.5f, 0.7176f, 0.6275f);

    /** Light USGS green */
    public static Color LIGHT_USGS_GREEN_90 = new Color(0.9f, 0.9434f, 0.9254f);

    /** Very light USGS green */
    public static Color LIGHT_USGS_GREEN_95 = new Color(242, 248, 246);

    /** Very light USGS green */
    public static Color LIGHT_USGS_GREEN_93 = new Color(237, 245, 242);

    /** Light gray */
    public static Color LIGHT_GRAY = new Color(0.92293f, 0.92293f, 0.92293f);

    /** Dark gray */
    public static Color DARK_GRAY = new Color(0.33f, 0.33f, 0.33f);

    /** USGS red */
    public static Color USGS_RED = new Color(255, 0, 51);

    /** USGS blue gray */
    public static Color USGS_BLUE_GREY = new Color(145, 176, 189);

    /** USGS green gray */
    public static Color USGS_GREEN_GREY = new Color(102, 164, 139);

    /** USGS warm grey */
    public static Color USGS_WARM_GREY = new Color(167, 157, 149);

    /** USGS light grey */
    public static Color USGS_LIGHT_GREY = new Color(199, 181, 188);

    /** Brown color representing changes */
    public static Color CHANGED_BROWN = new Color(0.4648f, 0.3320f, 0.1992f);

    /**
     * Return a new Color that is a bleached version of the provided color.
     * 
     * @param startingColor A color to bleach
     * @param bleachFactor The amount of bleaching. 0 returns the same as the starting color. 1
     *            returns white. 0.5 returns a color half way between the starting color and
     *            white.
     * @return The new bleached color
     */
    public static Color getBleachedColor(Color startingColor, float bleachFactor)
    {
        bleachFactor = Math.min(Math.max(bleachFactor, 0), 1);
        float[] rgb = startingColor.getRGBColorComponents(null);
        float newRed = 1 - ((1 - rgb[0]) * (1f - bleachFactor));
        float newGreen = 1 - ((1 - rgb[1]) * (1f - bleachFactor));
        float newBlue = 1 - ((1 - rgb[2]) * (1f - bleachFactor));

        return new Color(newRed, newGreen, newBlue);
    }
    
    public static Window windowFor(Component component) {
        if (component == null) {
            return WindowFactory.instance().getMainFrame();
        }
        else {
            Window ancestor = GuiUtilities.findAncestorFor(component);
            if (ancestor == null || !ancestor.isShowing()) {
                return WindowFactory.instance().getMainFrame();
            }
            else {
                return ancestor;
            }
        }
    }
    
    /**
     * Find the window ancestor of the component. If none can be found, use the frame for the
     * instance of NwisGuiProgram. If the program is not an instance of NwisGuiProgram, return
     * null.
     * 
     * @param component The component from where to start searching upward for a window ancestor
     * @return The ancestor, the program frame, or null.
     */
    public static Window findAncestorFor(Component component)
    {
        Window ancestor = null;
        if (component != null)
            ancestor = SwingUtilities.getWindowAncestor(component);
        if (ancestor == null)
            ancestor = WindowFactory.instance().getMainFrame();

        return ancestor;
    }

    /**
     * Shows a file selection dialog and lets the user choose a file.
     * 
     * @param title The title of the dialog box.
     * @param file The default file to display when the dialog first opens.
     * @param multiple Whether multiple selection is on
     * @param fileFilter The file filter to use, or null if none
     * @param parent The component to which the dialog should be attached.
     * @return An array of File objects that the user chose.
     */
    public static File[] showFileSelectionDialog(String title, File file, boolean multiple,
        FileFilter fileFilter, Component parent)
    {

        // Set up the file chooser dialog.
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setCurrentDirectory(file);
        if (!file.isDirectory())
            fc.setSelectedFile(file);
        fc.setMultiSelectionEnabled(multiple);

        // Show the open-file dialog and let the user choose the file.
        int returnVal = 0;

        fc.setDialogType(JFileChooser.OPEN_DIALOG);

        // Let the user choose the file.
        returnVal = fc.showOpenDialog(GuiUtilities.windowFor(parent));

        File[] files = null;

        // If the user pressed OK...
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            // Get the list of files.
            if (multiple)
                files = fc.getSelectedFiles();
            else
            {
                files = new File[1];
                files[0] = fc.getSelectedFile();
            }
        }

        return files;
    }

    /**
     * Calculate the X and Y coordinates of the top left corner of a window with the specified
     * width and height for the window to be centered over the ancestor component. The position
     * is adjusted to keep the window from going off the screen.
     * 
     * @param component The component whose ancestor over which to center the window. If the
     *            ancestor can't be found, center over the component. If the component is null,
     *            center on the screen.
     * @param width The width of the window
     * @param height The height of the window
     * @return The rectangle with the X and Y of the top left corner of the window
     */
    public static Rectangle getCenterLocation(Component component, int width, int height)
    {
        // Declare the corner point and rectangle that will be centered over
        Rectangle newRect = new Rectangle(0, 0, width, height);
        Rectangle rect = new Rectangle();

        // Retrieve the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle graphicsBounds = new Rectangle(0, 0, screenSize.width, screenSize.height);

        // If the component is null, center on the screen overall
        Window ancestor = null;
        if (component == null)
        {
            rect.width = screenSize.width;
            rect.height = screenSize.height;
        }
        else
        {
            ancestor = GuiUtilities.findAncestorFor(component);

            // If the ancestor is null, center on the component
            if (ancestor == null)
            {
                rect = new Rectangle(component.getX(), component.getY(), component.getWidth(),
                    component.getHeight());
            }
            else
            {
                // Center on the ancestor
                rect = new Rectangle(ancestor.getX(), ancestor.getY(), ancestor.getWidth(),
                    ancestor.getHeight());
                graphicsBounds = ancestor.getGraphicsConfiguration().getBounds();
            }
        }

        // Determine the corner for centering
        newRect.x = rect.x + ((rect.width - width) / 2);
        newRect.y = rect.y + ((rect.height - height) / 2);

        GuiUtilities.keepWindowInBounds(newRect);

        return newRect;
    }
    
    public static void keepWindowInBounds(Rectangle rect) {
        // Adjust the window rectangle's x-y position to keep it on the screen
        Rectangle graphicsBounds = new Rectangle(0, 0, 0, 0);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();
        for (GraphicsDevice screen : screens) {
            GraphicsConfiguration[] graphicsConfigurations = screen.getConfigurations();

            for (GraphicsConfiguration config : graphicsConfigurations) {
                Rectangle bounds = config.getBounds();
                if ((bounds.x + bounds.width) > graphicsBounds.width) {
                    graphicsBounds.width = bounds.x + bounds.width;
                }
                if ((bounds.y + bounds.height) > graphicsBounds.height) {
                    graphicsBounds.height = bounds.y + bounds.height;
                }
            }
        }

        if ((rect.x + rect.width) > (graphicsBounds.x + graphicsBounds.width)) {
            rect.x = graphicsBounds.x + graphicsBounds.width - rect.width;
        }
        if ((rect.y + rect.height) > (graphicsBounds.y + graphicsBounds.height)) {
            rect.y = graphicsBounds.y + graphicsBounds.height - rect.height;
        }
        if (rect.x < 0) {
            rect.x = 0;
        }
        if (rect.y < 0) {
            rect.y = 0;
        }
    }

    /**
     * Create a beveled border with an optional title
     * 
     * @param title The title string, or null if none
     * @return The compound border consisting of a beveled border within a title border
     */
    public static Border getCompoundBorder(String title, Color primaryColor)
    {
        if (primaryColor == null)
            primaryColor = GuiUtilities.USGS_GREEN;

        Border raisedbevel = BorderFactory.createRaisedBevelBorder();
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();
        Border compoundBorder;
        if (title == null)
        {
            compoundBorder = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
        }
        else
        {
            TitledBorder titledBorder = BorderFactory.createTitledBorder(raisedbevel, title);
            titledBorder.setTitlePosition(TitledBorder.ABOVE_TOP);
            compoundBorder = BorderFactory.createCompoundBorder(titledBorder, loweredbevel);
        }
        Border line1 = BorderFactory.createLineBorder(Color.white, 5);
        Border line2 = BorderFactory.createLineBorder(primaryColor, 2);
        Border line3 = BorderFactory.createLineBorder(Color.white, 5);

        Border compoundBorder2 = BorderFactory.createCompoundBorder(line1, compoundBorder);
        Border compoundBorder3 = BorderFactory.createCompoundBorder(line2, compoundBorder2);
        Border compoundBorder4 = BorderFactory.createCompoundBorder(line3, compoundBorder3);

        return compoundBorder4;
    }

    /**
     * Set the look and feel to Nimbus
     * 
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void useNimbusLAF() throws UnsupportedLookAndFeelException,
        IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    }
}
