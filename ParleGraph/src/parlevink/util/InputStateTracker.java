/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $       
 */

// Last modification by: $Author: swartjes $
// $Log: InputStateTracker.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2005/07/10 14:08:07  zwiers
// listeners added
//
// Revision 1.5  2005/07/09 15:51:33  zwiers
// MOUSEINPUTLISTENER corrected
//
// Revision 1.4  2005/06/23 09:50:06  zwiers
// comment update
//
// Revision 1.1  2005/02/04 16:40:51  zwiers
// initial version
//

package parlevink.util;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


/**
 * InputStateTracker is a combined KeyListener, MouseListener, MouseMotionListener, MouseWheelListener.
 * (Actually, the listener mode determines which combination of those four Listeners are active)
 * An InputStateTracker can be attached or detached from any AWT Component, by means of the listenTo and
 * detach methods. 
 * It captures mouse-related events and key-related events, and simply records the current
 * state of keys, mouse buttons and the position of the mouse. So, it converts the Java event based
 * interfaces for keyboard and mouse into a state based interface. 
 * The current state of keys is available in the form of a public array "keyDown": keyDown[keycode]
 * is true as long as that key is down, i.e. pressed and not yet released. Note that several keys can
 * be down at the same time. Also, it records the state for "raw" key codes, i.e. a modifier key like
 * the SHIFT key means that keyDown[KeyEvent.VK_SHIFT] is true, but does not modify the key code for other keys. 
 * Only keycodes in the range 0 .. KEYCODES are recorded. In practice, this includes all keys on 
 * a standard keyboard, but it does not include all Unicode keys.
 * In addition to keyDown, there is an int array keyLocation, which records the keyboard location
 * of a key, while it is down. (Distinguishes between KEY_LOCATION_LEFT , KEY_LOCATION_RIGHT, KEY_LOCATION_NUMPAD etc).
 *
 * Mouse button states are recorded by means of public boolean variables button[123]Down.
 * for each mouse button i ([123]), int pairs (x[123]Pressed, y[123]Pressed) 
 * and (x[123]Released, y[123]Released)  denote the mouse position at the moment 
 * of the last button pressed, and button released actions  for that mouse button.
 * Also, clickCount[123] denotes whether the last button press/release were 
 * the second (or third...) within a "double click".   
 */
public class InputStateTracker
implements KeyListener, MouseInputListener, MouseListener, MouseMotionListener, MouseWheelListener
{
   protected int listenerMode = 0;                              // determines what type of listener will be registrated 
                                                                // for the component to which we attach
   public final int KEYCODES = 256;                      // keycodes in the range 0..KEYCODES are handled.
   public boolean[] keyDown = new boolean[KEYCODES];     // records current state of all keys
   public int[] keyLocation = new int[KEYCODES];         // corrsponding location for all pressed keys. 
                                                                //(KEY_LOCATION_LEFT , KEY_LOCATION_RIGHT, KEY_LOCATION_NUMPAD etc).
   
   public  boolean button1Down = false;                         // current state of mouse buttons
   public  boolean button2Down = false;
   public  boolean button3Down = false;
   public  int clickCount1 = 0;                                 // current accumulated clickcount for all mouse buttons
   public  int clickCount2 = 0;
   public  int clickCount3 = 0;
   public  int x1Pressed, y1Pressed, x1Released, y1Released;    // screen positions of last press and release events
   public  int x2Pressed, y2Pressed, x2Released, y2Released;    // for all mouse buttons. 
   public  int x3Pressed, y3Pressed, x3Released, y3Released;
   public  int xDragged, yDragged, xMoved, yMoved;              // positions of last mouseDragged and mouseMoved events.
   public  int x, y;                                            // position of last mouse  press, release, drag, or move
   public  int xDelta, yDelta;                                  // delta's for last mouse drag or move

   public  int wheelRotation = 0;               // number of "clicks" that the mouse wheel was rotated in the last mouse wheel event.
   public  int accumulatedWheelRotation = 0;    // accumulated wheelrotation
   // either a List of listeners or a single listener will be used.
   private ArrayList<MouseDragListener> dragListeners = null;
   private MouseDragListener dragListener = null;
   private ArrayList<MouseMoveListener> moveListeners = null;   
   private MouseMoveListener moveListener = null;
   private ArrayList<MousePressListener> pressListeners = null;   
   private MousePressListener pressListener = null;
   

   /**
    * sets the single MouseDragListener, that will be informed aboud mouseDragged events.
    * (Any previously registered MouseDragListeners will be deregistered)
    */
   public void setMouseDragListener(MouseDragListener mdl) {
      dragListener = mdl;
      if (dragListeners != null) {
          dragListeners.clear();
          dragListeners = null;  
      }
   }

   /**
    * adds a MouseDragListener, that will be informed aboud mouseDragged events.
    * If a single MouseDragListener was set before, a list will be allocated, 
    * which includes this previously registered listener.
    */
   public void addMouseDragListener(MouseDragListener mdl) {
      if (dragListeners == null) {
         dragListeners = new ArrayList<MouseDragListener>(4);
         if (dragListener != null) {
            dragListeners.add(dragListener);
            dragListener = null;
         }
      }
      dragListeners.add(mdl);
   }

   /**
    * removes a MouseDragListener.
    */
   public void removeMouseDragListener(MouseDragListener mdl) {
      dragListeners.remove(mdl);
   }

   /**
    * sets the single MouseMoveListener, that will be informed aboud mouseMoved events.
    * (Any previously registered MouseMoveListeners will be deregistered)
    */
   public void setMouseMoveListener(MouseMoveListener mml) {
      moveListener = mml;
      if (moveListeners != null) {
          moveListeners.clear();
          moveListeners = null;  
      }
   }

   /**
    * adds a MouseMoveListener, that will be informed aboud mouseDragged events.
    * If a single MouseMoveListener was set before, a list will be allocated, 
    * which includes this previously registered listener.
    */
   public void addMouseMoveListener(MouseMoveListener mml) {
      if (moveListeners == null) {
         moveListeners = new ArrayList<MouseMoveListener>(4);
         if (moveListener != null) {
            moveListeners.add(moveListener);
            moveListener = null;
         }
      }
      moveListeners.add(mml);
   }

   /**
    * removes a MouseMoveListener.
    */
   public void removeMouseMoveListener(MouseMoveListener mml) {
      moveListeners.remove(mml);
   }

   /**
    * sets the single MousePressListener, that will be informed aboud mousePressed events.
    * (Any previously registered MousePressListeners will be deregistered)
    */
   public void setMousePressListener(MousePressListener mpl) {
      pressListener = mpl;
      if (pressListeners != null) {
          pressListeners.clear();
          pressListeners = null;  
      }
   }

/**
    * adds a MousePressListener, that will be informed aboud mousePressed events.
    * If a single MousePressListener was set before, a list will be allocated, 
    * which includes this previously registered listener.
    */
   public void addMousePressListener(MousePressListener mpl) {
      if (pressListeners == null) {
         pressListeners = new ArrayList<MousePressListener>(4);
         if (pressListener != null) {
            pressListeners.add(pressListener);
            pressListener = null;
         }
      }
      pressListeners.add(mpl);
   }

   /**
    * removes a MouseMoveListener.
    */
   public void removeMousePressListener(MousePressListener mpl) {
      pressListeners.remove(mpl);
   }


   /**
    * various listener modes; they can be combined by means of the | operator 
    */
   public static final int KEYLISTENER = 1;
   public static final int MOUSELISTENER = 2;
   public static final int MOUSEMOTIONLISTENER = 4;
   public static final int MOUSEINPUTLISTENER = MOUSELISTENER | MOUSEMOTIONLISTENER ; // combines MOUSELISTENER and MOUSEMOTIONLISTENER
   public static final int MOUSEWHEELLISTENER = 8;
   

   /** 
    * create a new InputStateTracker, for a null component, and zero listenerMode,
    * i.e. not listening to anything.
    */
   public InputStateTracker() {
     // this(null, 0);
   }

   
   /**
    * creates an InputStateTracker that records the state for various input modalities,
    * depending on the listenerMode. The listenerMode can combine various modes by means of
    * "|". The basic modes are: KEYLISTENER , MOUSELISTENER, MOUSEMOTIONLISTENER, MOUSEWHEELLISTENER 
    * For instance, InputStateTracker(myPanel, InputStateTracker.KEYLISTENER | InputStateTracker.MOUSELISTENER )
    * will record the state of keys as well as the state of the mouse buttons. 
    */ 
   public InputStateTracker(Component c, int listenerMode) {
      this.listenerMode = listenerMode;
      listenTo(c);

   }   


   /**
    * adds listeners to Component c for all enabled events, according to the listenerMode
    */
   public void listenTo(Component c)  {
      if ( (listenerMode & KEYLISTENER) != 0)         c.addKeyListener(this);
      if ( (listenerMode & MOUSELISTENER) != 0)       c.addMouseListener(this);
      if ( (listenerMode & MOUSEMOTIONLISTENER) != 0) c.addMouseMotionListener(this);
      if ( (listenerMode & MOUSEWHEELLISTENER) != 0)  c.addMouseWheelListener(this);
   }
  

   /**
    * removes the listeners from Component c
    */
   public void detachFrom(Component c)  {
      if ( (listenerMode & KEYLISTENER) != 0)         c.removeKeyListener(this);
      if ( (listenerMode & MOUSELISTENER) != 0)       c.removeMouseListener(this);
      if ( (listenerMode & MOUSEMOTIONLISTENER) != 0) c.removeMouseMotionListener(this);
      if ( (listenerMode & MOUSEWHEELLISTENER) != 0)  c.removeMouseWheelListener(this);
   }

   /**
    * returns the current value of keyDown[keyCode]
    */
   public boolean isKeyDown(int keyCode) {
      return keyDown[keyCode];
   }

   /**
    * produces a descriptive String for a key location:
    * Left, Right, Numpad, S(tandard), or U(nknown).
    */
   public String locationString(int location) {
      if (location == KeyEvent.KEY_LOCATION_LEFT) {
         return "Left";
      } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
         return "Right";
      } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
         return "Numpad";
      } else if (location == KeyEvent.KEY_LOCATION_STANDARD) {
         return "S";
      } else {
         return "U";  
      } 
   }

   /**
    * prints the current state of this tracker: state of keys, mouse buttons etc
    */
   public void showState() {
      Console.print("Keys down: ");
      for (int k=0; k<KEYCODES; k++)  {
         if (keyDown[k]) {
             Console.print(" " + k + "-" + locationString(keyLocation[k]));  
         }         
      }
      Console.print(" Mouse buttons: ");
      if (button1Down) Console.print(" button1");
      if (button2Down) Console.print(" button2");
      if (button3Down) Console.print(" button3");
      if (clickCount1 > 0) {
         Console.print(" clickCount1: " + clickCount1);
         Console.print("  x1,y1 pressed at (" + x1Pressed + "," + y1Pressed + ") released at (" + x1Released + "," + y1Released + ")"); 
      }
      if (clickCount2 > 0) {
         Console.print(" clickCount2: " + clickCount2);
         Console.print("  x2,y2 pressed at (" + x2Pressed + "," + y2Pressed + ") released at (" + x2Released + "," + y2Released + ")"); 
      }
      if (clickCount3 > 0) {
         Console.print(" clickCount3: " + clickCount3);
         Console.print("  x3,y3 pressed at (" + x3Pressed + "," + y3Pressed + ") released at (" + x3Released + "," + y3Released + ")"); 
      }     
      
      Console.println();
      if (wheelRotation != 0) {
          Console.println("mouse wheel rotation: " + wheelRotation + ", accumulated: " + accumulatedWheelRotation);  
      }
   }

   /**
    * shows "motion state" of this tracker
    */
   public void showMotion() {
      Console.println("x, y dragged: " + xDragged + "," + yDragged + "    x, y moved: " + xMoved + ", " + yMoved);
   }


   /**
    * keyPressed handler from the KeyListener interface for this tracker. 
    * updates the keyDown and keyLocation arrays
    */
   public final void keyPressed(KeyEvent evt) {
      int keyCode = evt.getKeyCode();
      //char keyChar = evt.getKeyChar();
      if (keyCode < KEYCODES) {
          keyDown[keyCode] = true;  
          keyLocation[keyCode] = evt.getKeyLocation();
      }
      //Console.println("key pressed: \'" + keyChar + "\' code = " + keyCode);
      //showState();
   }
    

   /**
    * keyReleased handler from the KeyListener interface for this tracker. 
    * updates the keyDown and keyLocation arrays
    */
   public final  void keyReleased(KeyEvent evt) {
      int keyCode = evt.getKeyCode();
      //char keyChar = evt.getKeyChar();
      if (keyCode < KEYCODES) {
          keyDown[keyCode] = false;  
      }      
      //Console.println("key released: \'" + keyChar + "\' code = " + keyCode);
      // showState();
   }

   /**
    * keyTyped handler from the KeyListener interface: ignored.
    */
   public final void keyTyped(KeyEvent evt) {
      //      int keyCode = evt.getKeyCode();
     // char keyChar = evt.getKeyChar();
     // Console.println("key typed: \'" + keyChar + "\' code = " + keyCode);
   }


   /**
    * mouseClicked handler from the MouseListener interface for this tracker: ignored
    */
   public final void mouseClicked(MouseEvent e) {
      //Console.println("mouse clicked");   
   }

   /**
    * mouseEntered handler from the MouseListener interface for this tracker: ignored
    */
   public final void mouseEntered(MouseEvent e) {
      //Console.println("mouse entered");  
   }

   /**
    * mouseExited handler from the MouseListener interface for this tracker: ignored
    */
   public final void mouseExited(MouseEvent e) {     
      //Console.println("mouse exited"); 
   }


   /**
    * mousePressed handler from the MouseListener interface for this tracker. 
    * updates the buttonDown[123] states, the [xy][123]Pressed positions,
    * and the clickCount[123] counters
    */
   public final void mousePressed(MouseEvent e) {  
      //Console.println("mouse pressed");
      x = e.getX();
      y = e.getY();
      int button = e.getButton();
      if (button == MouseEvent.BUTTON1) {
         button1Down = true;
         clickCount1 = e.getClickCount();
         x1Pressed = x;
         y1Pressed = y;
      } else if (button == MouseEvent.BUTTON2) {
         button2Down = true;
         clickCount2 = e.getClickCount();
         x2Pressed = x;
         y2Pressed = y;
      } else if (button == MouseEvent.BUTTON3) {
         button3Down = true;
         clickCount3 = e.getClickCount();
         x3Pressed = x;
         y3Pressed = y;
      } else {
          Console.println("Unknown mouse button number: " + button);  
      }      
      if (pressListener != null) {
         pressListener.mousePressed(x, y);  
      } else if (pressListeners != null) {
         for (MousePressListener mpl : pressListeners) {
            mpl.mousePressed(x, y); 
         }         
      }
      //showState();
   }

   /**
    * mouseReleased handler from the MouseListener interface for this tracker. 
    * updates the buttonDown[123] states, the [xy][123]Released positions,
    * and the clickCount[123] counters
    */
   public final void mouseReleased(MouseEvent e) {
      x = e.getX();
      y = e.getY();
      int button = e.getButton();
      if (button == MouseEvent.BUTTON1) {
         button1Down = false;
         x1Released = x;
         y1Released = y;
         clickCount1 = e.getClickCount();
      } else if (button == MouseEvent.BUTTON2) {
         button2Down = false;
         x2Released = x;
         y2Released = y;
         clickCount2 = e.getClickCount();
      } else if (button == MouseEvent.BUTTON3) {
         button3Down = false;
         x3Released = x;
         y3Released = y;
         clickCount2 = e.getClickCount();
      } else {
          Console.println("Unknown mouse button number: " + button);  
      }
      //showState();
   }

   /**
    * mouseDragged handler from the MouseMotionListener interface for this tracker. 
    * updates the xDragged and yDragged position.
    */
   public final void mouseDragged(MouseEvent e) {
      //Console.println("mouse dragged");
      xDragged = e.getX();
      yDragged = e.getY();
      xDelta = xDragged - x;
      yDelta = yDragged - y;      
      x = xDragged;
      y = yDragged;
      if (dragListener != null) {
         dragListener.mouseDragged(xDelta, yDelta);  
      } else if (dragListeners != null) {
         for (MouseDragListener mdl : dragListeners) {
            mdl.mouseDragged(xDelta, yDelta); 
         }         
      }
      //showMotion();    
   }

   /**
    * mouseMoved handler from the MouseMotionListener interface for this tracker. 
    * updates the xMoved and yMoved position.
    */
   public final void mouseMoved(MouseEvent e) {
      xMoved = e.getX();
      yMoved = e.getY();
      xDelta = xMoved - x;
      yDelta = yMoved - y;      
      x = xMoved;
      y = yMoved;
      if (moveListener != null) {
         moveListener.mouseMoved(xDelta, yDelta);  
      } else if (moveListeners != null) {
         for (MouseMoveListener mml : moveListeners) {
            mml.mouseMoved(xDelta, yDelta);  
         }         
      }
      //showMotion();    
   }

   /**
    * mouseDragged handler from the MouseWheelListener interface for this tracker. 
    * updates the wheelRotation and accumulatedWheelRotation counters.
    */
   public final void mouseWheelMoved(MouseWheelEvent e) {
      //Console.println("wheel");
      wheelRotation = e.getWheelRotation();
      accumulatedWheelRotation += wheelRotation;
      //showState();      
   }

}