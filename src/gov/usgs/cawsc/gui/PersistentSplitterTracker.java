/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.cawsc.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.JSplitPane;

/**
 *
 * @author jmd
 */
public class PersistentSplitterTracker implements PropertyChangeListener {

    Object target = null;
    JSplitPane splitPane = null;

    /**
     * Stores preferences that can be used the next time the program runs.
     */
    protected Preferences targetPreferences = null;

    /**
     * The simple class name of this class.
     */
    protected String targetSimpleClassName = null;
    
    /** The code that is used to distinguish multiple split panes in the same target object prefs */
    protected String code = null;

    public PersistentSplitterTracker(Object target, JSplitPane splitPane) {
        this(target, splitPane, "");
    }
    public PersistentSplitterTracker(Object target, JSplitPane splitPane, String code) {
        this.target = target;
        this.splitPane = splitPane;
        if (code != null && code.length() > 0) {
            code = "." + code;
        }
        this.code = code;

        targetPreferences = Preferences.userNodeForPackage(target.getClass());

        targetSimpleClassName = target.getClass().getSimpleName();

        int splitDividerLocation = splitPane.getDividerLocation();
        splitDividerLocation = targetPreferences.getInt(targetSimpleClassName + code + ".dividerLocation",
            splitDividerLocation);

        if (splitDividerLocation < 0) {
            splitPane.setDividerLocation(0.3);
        } else {
            splitPane.setDividerLocation(splitDividerLocation);
        }

        splitPane.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == splitPane) {
            if (evt.getPropertyName().equals("dividerLocation")) {
                targetPreferences.putInt(targetSimpleClassName + code + ".dividerLocation",
                    (Integer) evt.getNewValue());
            }
        }
    }
}
