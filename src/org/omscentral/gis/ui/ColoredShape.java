package org.omscentral.gis.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.swing.Icon;

public class ColoredShape implements Icon {

    public static final ColoredShape whiteShape = new ColoredShape(Color.white);

    Color fill = Color.white;
    Color border = Color.black;
    //Shape shape = new java.awt.Rectangle(0,0,10,10);
    Shape shape = new Ellipse2D.Float(0f, 0f, 10f, 10f);
    int type = 0;

    public ColoredShape(Color fill) {
      this.fill = fill;
    }

    public ColoredShape(Shape s,Color fill,Color border) {
      shape = s;
      this.fill = fill;
      this.border = border;
    }

    public ColoredShape(Color fill,Color border) {
      this.fill = fill;
      this.border = border;
    }

    public void setShape(Shape s) {
      shape = s;
    }

    public Shape getShape() {
      return shape;
    }

    public int getIconWidth() {
      return (int) shape.getBounds().getWidth();
    }

    public int getIconHeight() {
      return (int) shape.getBounds().getHeight();
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D) g;
      AffineTransform at = new AffineTransform();
      at.translate(x,y);
      Shape icon = at.createTransformedShape(shape);

      java.awt.Paint tmp = g2.getPaint();
      g2.setPaint(fill);
      g2.fill(icon);
      g2.setPaint(border);
      g2.draw(icon);
    }


    public Color getFillColor() {
        return fill;
    }

    public void setFillColor(Color c) {
        fill = c;
    }

    public Color getBorderColor() {
      return border;
    }

    public void setBorderColor(Color c) {
      border = c;
    }

    public String toString() {
      return shape.toString() + " " + fill.toString();
    }
}
