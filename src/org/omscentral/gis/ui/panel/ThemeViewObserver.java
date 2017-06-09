package org.omscentral.gis.ui.panel;

/**
 * A simple listener interface for ThemeView.
 *
 * @author    en
 * @created   June 19, 2001
 */
public interface ThemeViewObserver {

  /**
   * Called when the ThemeView has changed.
   * @param themeView  The ThemeView that changed.
   */
  public abstract void themeViewUpdated(ThemeView themeView);


}

