/*
 * Plotter.java
 *
 * Created on October 18, 2005, 12:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oui.util;

import org.jfree.chart.ChartPanel;

/**
 *
 * @author markstro
 */
public abstract class Plotter {
    public abstract ChartPanel getPanel ();
    public abstract void clearAll ();
}
