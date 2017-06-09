package org.omscentral.gis.ui.panel;


/**
 * A simple interface for those interested in a layer's changes.
 *
 * @author    en
 * @created   January 3, 2002
 */
public interface LayerObserver {

  public void layerChanged(Layer l);
}
