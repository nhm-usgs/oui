/*
 * HTMLMaker.java
 *
 * Created on January 17, 2003, 3:58 PM
 */

package org.omscentral.gis.util;

import org.omscentral.gis.model.VectorModel;
import org.omscentral.gis.model.VectorTheme;
import org.omscentral.gis.model.Feature;

import org.omscentral.gis.ui.panel.DisplayPaneContext;

import java.util.List;
import java.util.ArrayList;
import java.awt.geom.*;
/**
 *
 * @author  en
 */
public class HTMLMaker {
  
  public static String createHTMLImageMapTag(String mapname,VectorTheme theme,DisplayPaneContext context) {
    StringBuffer map = new StringBuffer();
    StringBuffer array = new StringBuffer();
    StringBuffer html = new StringBuffer();
    html.append("<html><head>");
    array.append("<script>\n");
    array.append("columns = Array();\n");
    String[] names = theme.getAttributeNames();
    for (int i = 0, ii = names.length; i < ii; i++) 
      array.append("columns[" + i + "] = '" + names[i] + "';");
    array.append("data = Array();\n");
    
    map.append("<image src='" + mapname + ".jpg' usemap='" + mapname + "'>\n");
    map.append("<MAP name='" + mapname + "'>\n");
    Feature[] features = theme.getFeatureArray();
    for (int i = 0; i < features.length; i++) {
      map.append("<AREA shape='poly' coords='");
      int cnt = 0;
      if (features[i] instanceof VectorModel.MultiPointFeature) {
        VectorModel.MultiPointFeature mpf = (VectorModel.MultiPointFeature) features[i];
        Point2D[] points = mpf.getPoints();
        java.awt.Point p = null;
        java.awt.Point last = null;
        StringBuffer pointBuf = new StringBuffer();
        for (int j = 0; j < points.length; j++) {
          p = context.toVirtualPoint(points[j]);
          if (last != null) {
            if (Math.abs(p.x - last.x ) < 2 || Math.abs( p.y - last.y) < 2 )
              continue;
          }
          last = p;
          pointBuf.append(p.x + "," + p.y);
          pointBuf.append(",");
        }
        map.append(pointBuf.substring(0,pointBuf.length()-1));
        // theme.getAttribute(mpf,1)
        array.append("data[" + i + "] = Array(");
        Object[] atts = theme.getAttributes(features[i]);
        for (int j = 0; j < atts.length; j++) {
          array.append("\"" + atts[j].toString() + "\"");
          if (j + 1 < atts.length)
            array.append(",");
        }
        array.append(");\n");
        map.append("' href='javascript:void(0)' onMouseOver=\"updateTable(" + i + "); return true;\">\n");
      }
    }
    map.append("</MAP>");
    array.append("\nfunction updateTable(id) {\n");
    array.append("\tvar d = data[id];\n");
    array.append("\tfor (var i=0; i < columns.length; i++) {\n");
    array.append("\t\tel = document.getElementById(columns[i]);\n");
    array.append("\t\tel.innerHTML = d[i];\n");
    array.append("\t}\n");
    array.append("\n}\n</script>\n");
    html.append(array);
    html.append("</head><body><h1>" + mapname + "</h1>");
    html.append(map);
    html.append("<table><tbody>\n");
    for (int i = 0, ii = names.length; i < ii; i++) {
      html.append("<tr><td>" + names[i] + "</td><td id='" + names[i] + "'>&nbsp</td></tr>\n");
    }
    html.append("</tbody></table>\n");
    html.append("</body></html>");
    return html.toString();
  }
  
}
