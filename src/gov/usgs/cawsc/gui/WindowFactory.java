/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cawsc.gui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

/**
 *
 * @author John M. Donovan
 */
public class WindowFactory {

    /**
     * The instance of this class or a subclass that is in use by the application.
     */
    protected volatile static WindowFactory windowFactoryInstance = null;

    protected JFrame mainFrame = null;

    protected HashMap<Component, Window> windowMap = new HashMap<>();

    protected LinkedList<Window> windowList = new LinkedList<>();

    protected LinkedList<Window> requestedToFrontList = new LinkedList<>();

    protected LinkedList<Window> requestedIconifiedList = new LinkedList<>();

    protected LinkedList<Window> untrackedList = new LinkedList<>();

    /**
     * Stores preferences that can be used the next time the program runs.
     */
    protected Preferences preferences = Preferences.userNodeForPackage(this.getClass());

    /**
     * The simple class name of this class.
     */
    protected String frameFactoryClassName = this.getClass().getSimpleName();

    public void setMainFrame(JFrame frame) {
        mainFrame = frame;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Display a panel inside a modal dialog box.
     *
     * @param associate The component that caused the dialog to be opened.
     */
    public Window createModalDialogFor(Component associate, Component content, String title) {
        return createDialogFor(associate, content, title, Dialog.ModalityType.DOCUMENT_MODAL);
    }

    /**
     * Display a panel inside a modeless dialog box.
     *
     * @param associate The component that caused the dialog to be opened.
     */
    public Window createModelessDialogFor(Component associate, Component content, String title) {
        return createDialogFor(associate, content, title, Dialog.ModalityType.MODELESS);
    }

    /**
     * Display a panel inside a dialog box.
     *
     * @param associate The component that caused the dialog to be opened.
     */
    public Window createDialogFor(Component associate, Component content, String title, Dialog.ModalityType modalityType) {
        // Create the dialog if this is the fist time it has been used
        JDialog dialog = null;

        Window ancestor = GuiUtilities.findAncestorFor(associate);
        dialog = new JDialog(ancestor, title, modalityType);

        // If checking needs to be done before closing, DO_NOTHING_ON_CLOSE would be better
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        return packWindowWith(associate, content, dialog);
    }

    /**
     * Display a panel inside a JFrame.
     *
     * @param content The component to be placed within the frame
     * @param title The title of the frame
     */
    public Window createFrameFor(Component content, String title) {
        // Create the frame if this is the fist time it has been used
        JFrame frame = null;

        frame = new JFrame(title);

        // If checking needs to be done before closing, DO_NOTHING_ON_CLOSE would be better
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        if (content instanceof CloseablePanel) {
            class ClosedPanelAdapter extends WindowAdapter {

                public CloseablePanel panel = null;

                @Override
                public void windowClosed(WindowEvent e) {
                    panel.closePanel();
                }
            }

            ClosedPanelAdapter cpAdapter = new ClosedPanelAdapter();
            cpAdapter.panel = (CloseablePanel) content;
            frame.addWindowListener(cpAdapter);
        }

        return packWindowWith(null, content, frame);
    }

    /**
     * Display a panel inside a Window.
     *
     * @param associate The component associated with the window to be opened, if a dialog,
     * or null if none
     * @param content The component to place within the window
     * @param window The window
     *
     */
    public Window packWindowWith(Component associate, Component content, Window window) {

        Window ancestor = GuiUtilities.findAncestorFor(associate);
        Rectangle windowRect = new Rectangle(300, 300, 500, 500);
        if (associate == null) {
            associate = ancestor;
        }

        // Center the window over the ancestor window
        windowRect = GuiUtilities.getCenterLocation(associate, windowRect.width,
            windowRect.height);

        window.add(content);
        window.pack();
        window.setMinimumSize(window.getSize());

        String simpleClassName = content.getClass().getSimpleName();

        windowRect.x = this.preferences.getInt(simpleClassName + ".windowX", windowRect.x);
        windowRect.y = this.preferences.getInt(simpleClassName + ".windowY", windowRect.y);
        windowRect.width = this.preferences.getInt(simpleClassName + ".windowWidth",
            windowRect.width);
        windowRect.height = this.preferences.getInt(simpleClassName + ".windowHeight",
            windowRect.height);

        windowMap.put(content, window);

        // Add listeners for resizing and moving the window
        class SizeAndShapeAdapter extends ComponentAdapter {

            public Component adapterContent = null;

            @Override
            public void componentMoved(java.awt.event.ComponentEvent evt) {

                Window window = (Window) evt.getComponent();
                Rectangle windowRect = window.getBounds();
                String simpleClassName = adapterContent.getClass().getSimpleName();
                WindowFactory.this.preferences.putInt(simpleClassName + ".windowX", windowRect.x);
                WindowFactory.this.preferences.putInt(simpleClassName + ".windowY", windowRect.y);
            }

            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {

                Window window = (Window) evt.getComponent();
                Rectangle windowRect = window.getBounds();
                String simpleClassName = adapterContent.getClass().getSimpleName();
                WindowFactory.this.preferences.putInt(simpleClassName + ".windowWidth", windowRect.width);
                WindowFactory.this.preferences.putInt(simpleClassName + ".windowHeight", windowRect.height);
            }
        };
        class ActivationAdapter extends WindowAdapter {

            @Override
            public void windowActivated(WindowEvent e) {
                Window window = (Window) e.getComponent();
                WindowFactory.instance().bringAllToFront(window, false);
                WindowFactory.instance().trackWindow(window);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                Window window = (Window) e.getComponent();
                WindowFactory.instance().finishIconifiedRequest(window);
                WindowFactory.instance().bringAllToFront(window, true);
                WindowFactory.instance().trackWindow(window);
            }

            @Override
            public void windowIconified(WindowEvent e) {
                Window window = (Window) e.getComponent();
// It was requested that the mainFrame be able to be iconified while leaving the other windows open
// JIRA OUI-7 and OUI-44
//                if (window == mainFrame) {
//                    WindowFactory.instance().iconifyAllExcept(window);
//                } else {
                WindowFactory.instance().stopTracking(window);
//                }
            }
        };

        SizeAndShapeAdapter ssAdapter = new SizeAndShapeAdapter();
        ssAdapter.adapterContent = content;
        window.addComponentListener(ssAdapter);
        ActivationAdapter activationAdapter = new ActivationAdapter();
        window.addWindowListener(activationAdapter);

        return window;
    }

    /**
     * Display this panel inside a window.
     *
     * @param component The component that caused the window to be opened.
     */
    public void displayInWindow(Component content) {
        // Create the window if this is the fist time it has been used
        Window window = windowMap.get(content);
        if (window == null) {
            window = createModelessDialogFor(null, content, "");
        }

        Rectangle windowRect = window.getBounds();
        String simpleClassName = content.getClass().getSimpleName();

        // Display the window.
        windowRect.x = this.preferences.getInt(simpleClassName + ".windowX", windowRect.x);
        windowRect.y = this.preferences.getInt(simpleClassName + ".windowY", windowRect.y);
        windowRect.width = this.preferences.getInt(simpleClassName + ".windowWidth",
            windowRect.width);
        windowRect.height = this.preferences.getInt(simpleClassName + ".windowHeight",
            windowRect.height);

        shiftIfOverlayingOtherWindow(windowRect);
        GuiUtilities.keepWindowInBounds(windowRect);
        window.setBounds(windowRect);
        window.setVisible(true);

        windowRect = window.getBounds();
        this.preferences.putInt(simpleClassName + ".windowX", windowRect.x);
        this.preferences.putInt(simpleClassName + ".windowY", windowRect.y);
        this.preferences.putInt(simpleClassName + ".windowWidth", windowRect.width);
        this.preferences.putInt(simpleClassName + ".windowHeight", windowRect.height);
    }

    public void shiftIfOverlayingOtherWindow(Rectangle rect) {
        for (Window window : windowList) {
            if (window != null && window.getBounds().equals(rect)) {
                rect.x += 10;
                rect.y += 20;
                shiftIfOverlayingOtherWindow(rect);
                return;
            }
        }
    }

    /**
     * Hide the window for the specified component.
     *
     * @param content The component associated with the window
     * @return The window that was found and hidden, or null if none
     */
    public Window closeWindow(Component content) {
        // Create the window if this is the fist time it has been used
        Window window = windowMap.get(content);
        if (window != null) {
            // setVisible(false) is another option that keeps the window resources in memory
            window.dispose();
        }

        return window;
    }

    /**
     * Dispose of the window for the specified component and remove it from the list of
     * windows.
     *
     * @param content The component associated with the window
     * @return The window that was found and removed, or null if none
     */
    public Window removeWindow(Component content) {
        // Create the window if this is the fist time it has been used
        Window window = windowMap.get(content);
        if (window != null) {
            window.dispose();
            windowMap.remove(content);
        }

        return window;
    }

    /**
     * Set the title of the window for the specified component.
     *
     * @param content The component associated with the window
     * @param title The new title of the window
     * @return The window that was found and hidden, or null if none
     */
    public Window setWindowTitle(Component content, String title) {
        // Create the window if this is the fist time it has been used
        Window window = windowMap.get(content);
        if (window != null) {
            if (window instanceof JDialog) {
                ((JDialog) window).setTitle(title);
            } else if (window instanceof JFrame) {
                ((JFrame) window).setTitle(title);
            }
        }

        return window;
    }

    public static void displayInModelessDialog(Component content, String title) {
        WindowFactory.instance().createModelessDialogFor(null, content, title);
        WindowFactory.instance().displayInWindow(content);
    }

    public static void displayInFrame(Component content, String title) {
        WindowFactory.instance().createFrameFor(content, title);
        WindowFactory.instance().displayInWindow(content);
    }

    public void bringAllToFront(Window topWindow, boolean includeIconified) {
        boolean topIconified = topWindow instanceof Frame && ((Frame) topWindow).getExtendedState() == Frame.ICONIFIED;
        if (!topIconified && !requestedToFrontList.contains(topWindow)) {

            if (windowList.isEmpty()) {
                windowList.add(mainFrame);
            }
            Window last = windowList.isEmpty() ? null : windowList.getLast();
            if (last != topWindow) {
                windowList.remove(topWindow);
                windowList.add(topWindow);
            }

            List<Window> nonShowingList = new LinkedList<>();

            for (Window window : windowList) {
                if (window != null && (window.isShowing() || includeIconified)) {
                    if (untrackedList.contains(window)
                        || (!includeIconified && window instanceof Frame && ((Frame) window).getExtendedState() == Frame.ICONIFIED)) {
                        continue;
                    } else {
                        if (window instanceof Frame && ((Frame) window).getExtendedState() == Frame.ICONIFIED) {
                            ((Frame) window).setState(Frame.NORMAL);
                        }
                        requestedToFrontList.add(window);
                        window.toFront();
                    }
                } else {
                    nonShowingList.add(window);
                }
            }
            windowList.removeAll(nonShowingList);
        } else {
            if (requestedToFrontList.contains(topWindow)) {
                requestedToFrontList.remove(topWindow);
            } else {
            }
        }
    }

    public void iconifyAllExcept(Window exceptedWindow) {
        if (!requestedIconifiedList.contains(exceptedWindow)) {
            for (Window window : windowList) {
                if (window != exceptedWindow && window instanceof Frame) {
                    ((Frame) window).setState(Frame.ICONIFIED);
                    requestedIconifiedList.add(window);
                }
            }
        }
    }

    public void finishIconifiedRequest(Window window) {
        requestedIconifiedList.remove(window);
    }

    public void trackWindow(Window window) {
        untrackedList.remove(window);
    }

    public void stopTracking(Window window) {
        if (!requestedIconifiedList.contains(window)) {
            untrackedList.add(window);
        }
    }

    /**
     * Return the instance of WindowFactory or a subclass that is in use by the application
     *
     * @return The instance of MessageLog
     */
    public static WindowFactory instance() {
        if (windowFactoryInstance == null) {
            synchronized (WindowFactory.class) {
                if (windowFactoryInstance == null) {
                    windowFactoryInstance = new WindowFactory();
                }
            }
        }

        return windowFactoryInstance;
    }

    /**
     * Set the instance of the class or a subclass that is in use by the application.
     *
     * @param newInstance The instance
     */
    public static void setInstance(WindowFactory newInstance) {
        windowFactoryInstance = newInstance;
    }

}
