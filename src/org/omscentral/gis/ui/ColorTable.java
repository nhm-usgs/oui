package org.omscentral.gis.ui;

import java.awt.Color;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Description of the Class
 *
 * @author    en
 * @created   June 19, 2001
 */
public class ColorTable {

  /**
   * Description of the Field
   */
  protected Color[] color;

  /**
   * Description of the Field
   */
  protected String name;


  /**
   * Constructor for the ColorTable object
   *
   * @param fileName  Description of Parameter
   */
  public ColorTable(String fileName) {
    Properties p = new Properties();
    try {
      p.load(new FileInputStream(fileName));
    }
    catch (IOException ioe) {
      System.err.println("Error loading colortable");
    }
    color = new Color[p.size() - 1];
    for (int i = 0; i < p.size() - 1; i++) {
      String col = (String) p.get("Color" + i);
      color[i] = Color.decode("0x" + col);
    }
    name = (String) p.get("Name");
  }


  /**
   * Gets the Colors attribute of the ColorTable object
   *
   * @return   The Colors value
   */
  public Color[] getColors() {
    return color;
  }



  /**
   * Gets the Name attribute of the ColorTable object
   *
   * @return   The Name value
   */
  public String getName() {
    return name;
  }

}

