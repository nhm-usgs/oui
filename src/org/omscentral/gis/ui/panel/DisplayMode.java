package org.omscentral.gis.ui.panel;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Cursor;

import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.omscentral.gis.model.*;

public abstract class DisplayMode {

  private static DisplayPane pane = null;

  public static int MEASURE_LINE_MOUSE_BUTTON = MouseEvent.BUTTON1_MASK;
  public static int MEASURE_BOX_MOUSE_BUTTON = MouseEvent.BUTTON2_MASK;
  public static int SELECT_POINT_MOUSE_BUTTON = MouseEvent.BUTTON1_MASK;
  public static int SELECT_BOX_START_MOUSE_BUTTON = MouseEvent.BUTTON2_MASK;
  public static int SELECT_BOX_DRAG_MOUSE_BUTTON = MouseEvent.BUTTON2_MASK;
  public static int SELECT_BOX_END_MOUSE_BUTTON = MouseEvent.BUTTON2_MASK;
  public static int ZOOM_IN_POINT_MOUSE_BUTTON = MouseEvent.BUTTON1_MASK;
  public static int ZOOM_BOX_START_MOUSE_BUTTON = MouseEvent.BUTTON2_MASK;
  public static int ZOOM_BOX_DRAG_MOUSE_BUTTON = MouseEvent.BUTTON2_MASK;
  public static int ZOOM_BOX_END_MOUSE_BUTTON = MouseEvent.BUTTON2_MASK;
  public static int ZOOM_OUT_POINT_MOUSE_BUTTON = MouseEvent.BUTTON1_MASK;
  public static int ZOOM_IN_OUT_TOGGLE_KEY = MouseEvent.SHIFT_DOWN_MASK;

  public static KeyStroke ZOOM_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_Z,KeyEvent.CTRL_MASK);
  public static KeyStroke SELECT_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK);
  public static KeyStroke MEASURE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_M,KeyEvent.CTRL_MASK);
  public static KeyStroke HAND_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_H,KeyEvent.CTRL_MASK);
  public static KeyStroke NO_MODE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_MASK);
  public static KeyStroke LT_OBS = KeyStroke.getKeyStroke(KeyEvent.VK_L,KeyEvent.CTRL_MASK);
  public static KeyStroke HVY_OBS = KeyStroke.getKeyStroke(KeyEvent.VK_V,KeyEvent.CTRL_MASK);
  public static KeyStroke NORMAL_OBS = KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_MASK);

  public static KeyStroke DIGITIZE_KEY = KeyStroke.getKeyStroke(KeyEvent.VK_D,KeyEvent.CTRL_MASK);

  public DisplayMode() {
    setup();
  }

  protected void checkContext() {
    if (pane == null)
      throw new java.lang.IllegalStateException("DisplayMode lacks a context" +
        ",use DisplayMode.setPane()");
  }

  public static void setPane(DisplayPane context) {
    pane = context;
  }

  public static DisplayMode NO_OBSERVER =
    new DisplayMode () {
      public void invoke(MouseEvent e) {
        checkContext();
      }
      public void invoke(KeyEvent e) {
        checkContext();
      }
    };

  public static DisplayMode NO_FUNCTION =
    new DisplayMode () {
      public void invoke(MouseEvent e) {
        checkContext();
      }
      public void invoke(KeyEvent e) {
        checkContext();
      }
    };


  public static DisplayMode DIGITIZE_MODE =
    new DisplayMode () {
      java.util.ArrayList<java.awt.Point> points = new java.util.ArrayList<java.awt.Point>();
      java.awt.Point current = null;
      java.awt.Point last = null;
      public synchronized void invoke(MouseEvent e) {
        checkContext();
        if (e.getID() == e.MOUSE_CLICKED && e.getClickCount() == 1) {
          points.add(e.getPoint());
          current = e.getPoint();
        }
        else if (e.getID() == e.MOUSE_MOVED && current != null) {
          java.awt.Graphics g = pane.getGraphics();
          if (last != null) {
          g.setXORMode(java.awt.Color.black);
          g.setColor(java.awt.Color.white);
          g.drawLine(current.x,current.y,last.x,last.y);
          }
          g.setPaintMode();
          g.setColor(java.awt.Color.black);
          g.drawLine(current.x,current.y,e.getPoint().x,e.getPoint().y);
          last = e.getPoint();
        }

      }
      public void invoke(KeyEvent e) {
        checkContext();
      }
    };

  public static DisplayMode LIGHT_TRACKING_OBSERVER =
    new DisplayMode () {

      public void invoke(MouseEvent e) {
        checkContext();
        if (e.getID() == e.MOUSE_MOVED) {
          pane.getCrosshair().setCoordinatesVisible(true);
          pane.getCrosshair().setLocation(e.getPoint());
        }
        else if (e.getID() == e.MOUSE_EXITED)
          pane.getCrosshair().setCoordinatesVisible(false);
      }
      public void invoke(KeyEvent e) {
        checkContext();
      }

  };


  public static DisplayMode HAND_FUNCTION_MODE =
    new DisplayMode () {
      Point last;

      public void invoke(MouseEvent e) {
        checkContext();
        int mod = e.getModifiers();
        int id = e.getID();
        if ( id == e.MOUSE_MOVED) {
          if (last == null)
            last = e.getPoint();
          int x =  last.x - e.getPoint().x;
          int y =  last.y - e.getPoint().y;
          x *= pane.getViewedScale();
          y *= pane.getViewedScale();
          Point c = pane.getViewport().getViewPosition();
          c.translate(x,y);
          if (c.y < 0) c.y = 0;
          if (c.x < 0) c.x = 0;
          if (c.y > pane.getContext().getHeight() - pane.getViewport().getHeight() )
            c.y = pane.getContext().getHeight() - pane.getViewport().getHeight();
          if (c.x > pane.getContext().getWidth()  - pane.getViewport().getWidth() )
            c.x = pane.getContext().getWidth()  - pane.getViewport().getWidth();
          pane.getViewport().setViewPosition(c);
          last = e.getPoint();
        }
      }
      public void invoke(KeyEvent e) {
        checkContext();
//        if (e.getID() == e.KEY_RELEASED && e.getKeyCode() == e.VK_CONTROL) {
//          System.out.println("setting old cursor " + oldCursor);
//          pane.setCursor(oldCursor);
//          pane.setObserverMode(oldMode);
//          pane.setFunctionMode(oldFunction);
//          last = null;
//        }
      }
      public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
      }
    };


  public static DisplayMode MEASURING_FUNCTION_MODE =
    new DisplayMode () {
      public void invoke(MouseEvent e) {
        checkContext();
      }
      public void invoke(KeyEvent e) {
        checkContext();
      }
    };


  public static DisplayMode SELECTION_FUNCTION_MODE =
    new DisplayMode () {
      Function selectBox;
      public void invoke(MouseEvent e) {
        checkContext();
        int mod = e.getModifiers();
        int id = e.getID();

        boolean clear = !(e.isControlDown() || e.isShiftDown());

        // wipe out the CTRL and SHIFT
        mod = mod &~ e.CTRL_MASK;

        if ( isTrigger2(e,e.MOUSE_CLICKED,SELECT_POINT_MOUSE_BUTTON))
          pane.fireSelection(e.getPoint(),clear);
        else if ((mod&SELECT_BOX_START_MOUSE_BUTTON) == SELECT_BOX_START_MOUSE_BUTTON &&
            id == e.MOUSE_PRESSED) {
          selectBox = new SelectFunction(pane);
          selectBox.update(e.getPoint());
        }
        else if ((mod&SELECT_BOX_DRAG_MOUSE_BUTTON) == SELECT_BOX_DRAG_MOUSE_BUTTON &&
            id == e.MOUSE_DRAGGED) {
          if (selectBox == null) return;
          selectBox.update(e.getPoint());
        }
        else if ((mod&SELECT_BOX_END_MOUSE_BUTTON) == SELECT_BOX_END_MOUSE_BUTTON &&
            id == e.MOUSE_RELEASED) {
          if (selectBox == null) return;
          selectBox.update(e.getPoint());
          pane.fireSelection((Rectangle) selectBox.getResult(),clear);
          selectBox.end();
          selectBox = null;
        }
      }
      public void invoke(KeyEvent e) {
        checkContext();
      }
    };

/// TODO CHECK THIS CODE
  public static DisplayMode PAN_FUNCTION_MODE =
    new DisplayMode () {
      Point lastPoint = null;
      public synchronized void invoke(MouseEvent e) {
        checkContext();

        if (isTrigger2(e,e.MOUSE_PRESSED, 0)) {
            lastPoint = e.getPoint();
        }
        else if (isTrigger2(e,e.MOUSE_DRAGGED, 0)) {
            Point newPoint = e.getPoint();
            pane.panViewport(lastPoint.x - newPoint.x, lastPoint.y - newPoint.y);
            lastPoint = newPoint;
        }
        else if (isTrigger2(e,e.MOUSE_RELEASED, 0)) {
          lastPoint = null;
        }
      }
      public void invoke(KeyEvent e) {
        checkContext();

        if (e.getKeyCode() == e. VK_EQUALS) {
          pane.zoomIn();
        }
        if (e.getKeyCode() == e.VK_MINUS) {
          pane.zoomOut();
        }
      }
    };

    public static DisplayMode ZOOM_RECT_FUNCTION_MODE =
    new DisplayMode () {
      Function zoomBox;
      public synchronized void invoke(MouseEvent e) {
        checkContext();

        if (isTrigger2(e,e.MOUSE_PRESSED, 0)) {
          if (zoomBox == null)
            zoomBox = new ZoomFunction(pane);
          zoomBox.update(e.getPoint());
        }
        else if (isTrigger2(e,e.MOUSE_DRAGGED, 0)) {
          if (zoomBox == null) return;
          zoomBox.update(e.getPoint());
        }
        else if (isTrigger2(e,e.MOUSE_RELEASED, 0)) {
          if (zoomBox == null) return;
          zoomBox.update(e.getPoint());
          pane.zoomTo((Rectangle) zoomBox.getResult());
          zoomBox.end();
          zoomBox = null;
          pane.firePanMode();
        }
      }
      public void invoke(KeyEvent e) {
        checkContext();
        if (e.getKeyCode() == e. VK_EQUALS) {
          pane.zoomIn();
        }
        if (e.getKeyCode() == e.VK_MINUS) {
          pane.zoomOut();
        }
      }
    };

  public static DisplayMode ZOOM_FUNCTION_MODE =
    new DisplayMode () {
      Function zoomBox;
      Cursor inCursor;
      Cursor outCursor;
      public synchronized void invoke(MouseEvent e) {
        checkContext();
        int mod = e.getModifiers();
        int id = e.getID();

        if (isTrigger3(e,e.MOUSE_CLICKED, ZOOM_IN_POINT_MOUSE_BUTTON,ZOOM_IN_OUT_TOGGLE_KEY))
          pane.zoomOut(e.getPoint());
        else if (isTrigger2(e,e.MOUSE_CLICKED,ZOOM_IN_POINT_MOUSE_BUTTON) )
          pane.zoomIn(e.getPoint());
        else if (isTrigger2(e,e.MOUSE_PRESSED,ZOOM_BOX_START_MOUSE_BUTTON)) {
          if (zoomBox == null)
            zoomBox = new ZoomFunction(pane);
          zoomBox.update(e.getPoint());
        }
        else if (isTrigger2(e,e.MOUSE_DRAGGED,ZOOM_BOX_DRAG_MOUSE_BUTTON)) {
          if (zoomBox == null) return;
          zoomBox.update(e.getPoint());
        }
        else if (isTrigger2(e,e.MOUSE_RELEASED,ZOOM_BOX_END_MOUSE_BUTTON)) {
          if (zoomBox == null) return;
          zoomBox.update(e.getPoint());
          pane.zoomTo((Rectangle) zoomBox.getResult());
          zoomBox.end();
          zoomBox = null;
        }
      }
      public void invoke(KeyEvent e) {
        checkContext();
        if (e.isShiftDown() && e.getID() == e.KEY_PRESSED) {
          if (outCursor != null)
            pane.setCursor(outCursor);
        }
        if (e.getKeyCode() == e.VK_SHIFT && e.getID() == e.KEY_RELEASED) {
          pane.setCursor(inCursor);
        }
        if (e.getKeyCode() == e. VK_EQUALS) {
          pane.zoomIn();
        }
        if (e.getKeyCode() == e.VK_MINUS) {
          pane.zoomOut();
        }
      }
      public void setInactiveCursor(Cursor c) {inCursor = c;}
      public void setActiveCursor(Cursor c) {outCursor = c;}
      public Cursor getCursor() {
        if (inCursor != null)
          return inCursor;
        return super.getCursor();
      }
    };

  public static DisplayMode CONTROL = new DisplayMode() {
    DisplayMode savedMode = null;
    DisplayMode savedFunction = null;
    public void invoke(MouseEvent e) {
        checkContext();
      }
      public void invoke(KeyEvent e) {
        if (isKeyStroke(e,ZOOM_KEY))
          pane.setFunctionMode(ZOOM_FUNCTION_MODE);
        else if (isKeyStroke(e,SELECT_KEY))
          pane.setFunctionMode(SELECTION_FUNCTION_MODE);
        else if (isKeyStroke(e,MEASURE_KEY))
          pane.setFunctionMode(MEASURING_FUNCTION_MODE);
        else if (isKeyStroke(e,HAND_KEY)) {
          savedMode = pane.getObserverMode();
          savedFunction = pane.getFunctionMode();
          pane.setFunctionMode(HAND_FUNCTION_MODE);
        }
        else if (isKeyStrokeRelease(e,HAND_KEY)) {
          if (savedMode != null)
            pane.setObserverMode(savedMode);
          if (savedFunction != null)
            pane.setFunctionMode(savedFunction);
          savedMode = null;
          savedFunction = null;
        }
        else if (isKeyStroke(e,NO_MODE_KEY))
          pane.setFunctionMode(NO_FUNCTION);
        else if (isKeyStroke(e,HVY_OBS)) {

        }
        else if (isKeyStroke(e,LT_OBS))
          pane.setObserverMode(DisplayMode.LIGHT_TRACKING_OBSERVER);
        else if (isKeyStroke(e,NORMAL_OBS))
          pane.setObserverMode(DisplayMode.NO_OBSERVER);
        else if (isKeyStroke(e,DIGITIZE_KEY))
          pane.setFunctionMode(DisplayMode.DIGITIZE_MODE);
      }
  };

  public abstract void invoke(KeyEvent e);

  public abstract void invoke(MouseEvent e);

  public void setup() {
  }

  public Cursor getCursor() {
    return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
  }

  public void setInactiveCursor(Cursor c) {
  }
  public void setActiveCursor(Cursor c) {
  }


  private static boolean isKeyStroke(KeyEvent e,KeyStroke s) {
    return KeyStroke.getKeyStrokeForEvent(e).equals(s);
  }

  private static boolean isKeyStrokeRelease(KeyEvent e,KeyStroke s) {
    return KeyStroke.getKeyStroke(s.getKeyCode(),s.getModifiers(),true).equals(
      KeyStroke.getKeyStrokeForEvent(e));
  }

  private static boolean isTrigger2(MouseEvent e,int mouseMask,int buttonMask) {
    int id = e.getID();
    int mod = e.getModifiers() &~e.CTRL_MASK;
    return (id == mouseMask && (mod&buttonMask) == buttonMask);
  }

  private static boolean isTrigger3(MouseEvent e,int mouseMask,int buttonMask,int keyMask) {
    int id = e.getID();
    int mod = e.getModifiers();
//        int mod = e.getModifiers() &~ keyMask;
    int keymod = e.getModifiersEx();
    return (
      id == mouseMask &&
      (mod&buttonMask) == buttonMask &&
      keymod == keyMask);
  }

}
