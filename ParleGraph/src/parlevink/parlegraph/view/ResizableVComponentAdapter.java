/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: ResizableVComponentAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:20  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:17  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2003/07/16 16:47:23  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.4  2002/09/30 20:16:46  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.3  2002/09/27 08:15:43  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.2  2002/09/23 07:44:43  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:49  dennisr
// first add

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import parlevink.xml.*;
import java.io.*;

/**
 * ResizableVComponentAdapter is the default implementation of the ResizableVComponent interface.
 * 
 */
public class ResizableVComponentAdapter extends VComponentAdapter implements ResizableVComponent {

/*++++++++++++++++++++*
 * Variables section. *
 *++++++++++++++++++++*/
 
    //for move & resize modes
    protected int moveMode, resizeMode, moveRestrictions;
    private static int defaultResizeMode = RM_ALLOW_RESIZE | RM_FOCUS_ENTAILS_RESIZING | RM_CORNER_HANDLES | RM_MIDDLE_HANDLES;
    private static int defaultMoveMode = MM_ALLOW_MOVE | MM_FOCUS_ENTAILS_MOVING;
    private static int defaultMoveRestrictions = MR_CONSTRAIN_CHILD;
    
    
    //for the handles
    protected RectangularShape  topLeft, topRight, bottomLeft, bottomRight;
    protected RectangularShape leftMiddle, topMiddle, rightMiddle, bottomMiddle;
    protected double x1, x2, x3, x4, x5;
    protected double y1, y2, y3, y4, y5;
	public static final double          HANDLESIZE = 5;
	protected double                    handleSize = HANDLESIZE;
    public static final double          MINSIZE = 3*HANDLESIZE;
    protected double                    minSize = MINSIZE;
    protected int focusHandle;

    //for the dragging events
	 private double xoff, yoff;
	 private double sx, sy;

/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
     
    public ResizableVComponentAdapter() {
        super();
    }

    /**
     *@deprecated
     */
    public ResizableVComponentAdapter(double x, double y , double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }
    /**
     * Initializes static variables if they have not yet been initialized.
     * If overridden by subclass: call super!
     */
    public void checkStaticInit() {
        super.checkStaticInit();
    }
 
    /**
     * Initializes simple variables for this VComponent, such as x, y, etc
     * (Not the objects which are dependent on these variables!).
     * in this case the move & resizemodes are initted.
     */
    public void preInit() {
        super.preInit();
        moveMode = getDefaultMoveMode();
        resizeMode = getDefaultResizeMode();
        moveRestrictions = getDefaultMoveRestrictions();
    }
 
    protected int getDefaultMoveMode() {
        return defaultMoveMode;
    }
    protected int getDefaultResizeMode() {
        return defaultResizeMode;
    }
    protected int getDefaultMoveRestrictions() {
        return defaultMoveRestrictions;
    }
    
    /**
     * Initializes objects for this VComponent that are dependent on the simple variables
     */
    public void init() {
        super.init();
    }
 
    /**
     * Any initialization that depends on or operates on the objects created in
     * init() is done here...
     */
    public void postInit() {
        super.postInit();
    }
 
    
    
/*++++++++++++++++++++*
 * Painting section.  *
 *++++++++++++++++++++*/
   
    public void paintVComponent(Graphics2D g2) {
       super.paintVComponent(g2);
       if (   ( (resizeMode & RM_ALLOW_RESIZE) > 0)
           && ( (resizeMode & RM_RESIZING) > 0) ) 
            paintResizeDecoration(g2);
    }

   public void paintResizeDecoration(Graphics2D g2) {
      if (   (topLeft != null)
          && ( (resizeMode & RM_CORNER_HANDLES) > 0) ) {
         g2.fill(topLeft);
         g2.fill(topRight);
         g2.fill(bottomLeft);
         g2.fill(bottomRight);
      }
      if (   (topMiddle != null)  //deze conditie mag eruit
          && ( (resizeMode & RM_MIDDLE_HANDLES) > 0) ) {
         g2.fill(topMiddle);
         g2.fill(leftMiddle);
         g2.fill(rightMiddle);
         g2.fill(bottomMiddle);
      }      
   }

/*++++++++++++++++++++++++++*
 * Transformation section.  *
 *++++++++++++++++++++++++++*/
    
    public void setResizeMode(int resize) {
        resizeMode = resize;
        if ( ( resizeMode & RM_RESIZING) > 0) {
            if (topLeft == null) 
                initCornerHandles();
            if (topMiddle == null) 
                initMiddleHandles();
        }
        recalculateShape();
    }
    
    public int getResizeMode() {
        return resizeMode;
    }

    public void setMoveMode(int move) {
        moveMode = move;
    }
    
    public int getMoveMode() {
        return moveMode;
    }

    //one of...
    public void setMoveRestrictions(int mr) {
        moveRestrictions = mr;
    }
    
    public int getMoveRestrictions() {
        return moveRestrictions;
    }

    
/*****************************
 * Internal stuff
 *****************************/
    
   /**
    * Determines which handle, if any, contains point p = (x,y).
    * It is assumed that p lies within the bounding box, and that the 
    * values of x1, x2, x3, x4, y1, y2, y3, y4 have been set (by recalculateShape).
    * Also, it is assumed that at least corner handles are enabled, and, 
    * optionally, middleHandles might be enabled.
    * The code returned must be interpreted as follows:
    * code 0: not on the border, in particular, no handle contains p.
    * code 1 .. 8: as indicated in the following diagram:
    *
    *   1    5    2
    *
    *   6         7
    *
    *   3    8    4
    *   
    * code 9: not a handle, but still on the border, i.e.
    * p is somewehere between 1 and 5, or between 5 and 2, or between 1 and 6 etc.
    * The size of the handles, and the width of the border, is determined by
    * the variable "handleSize". 
    * When middle handles are disabled, codes 5, 6, 7, and 8 are converted into 9.
    */
   protected int classifyHandle(double xp, double yp) {
      if (yp <= y1) {
         // on upper part of border.
         if (( (resizeMode & RM_CORNER_HANDLES) > 0) && xp <= x1)             return 1;  // upper left.
         if (( (resizeMode & RM_CORNER_HANDLES) > 0) && xp >= x4)             return 2;  // upper right.
         if (( (resizeMode & RM_MIDDLE_HANDLES) > 0) && x2 <= xp && xp <= x3) return 5;  // upper middle.
         return 9;   // on border, not on handle.
      } else if (yp >= y4) {  
         // on lower part of border.
         if (( (resizeMode & RM_CORNER_HANDLES) > 0) && xp <= x1)             return 3;  // lower left.
         if (( (resizeMode & RM_CORNER_HANDLES) > 0) && xp >= x4)             return 4;  // lower right.
         if (( (resizeMode & RM_MIDDLE_HANDLES) > 0) && x2 <= xp && xp <= x3) return 8;  // lower middle.
         return 9;   // on border, not on handle.
     } else if (xp <= x1) {
         // on left part of border, not in upper or lower part. 
         if (( (resizeMode & RM_MIDDLE_HANDLES) > 0) && y2 <= yp && yp <= y3) return 6; // left middle.
         return 9;   // on border, not on handle.
      } else if (xp >= x4) {
         // on right part of border, not in upper or lower part.
         if (( (resizeMode & RM_MIDDLE_HANDLES) > 0) && y2 <= yp && yp <= y3) return 7; // right middle.
         return 9;   // on border, not on handle.        
      } else return 10; // not on the border: in the middle of the shape.
   }

    /**
     * (Re-) calculates the shape that will be drawn by paintComponent/PaintBackground.    
     * Most especially, recalculate the locations of the resizing handles.
     */ 
    public void recalculateShape() {
  		super.recalculateShape();
        if (   (( resizeMode & RM_ALLOW_RESIZE) == 0) 
            && (( resizeMode & RM_RESIZING) == 0) )
            return;
      // @@@ERG inefficient. Zeker omdat dit vaak wordt aangeroepen, ook als je alleen maar met een 
      // VComponent sleept zonder te resizen.
      // x < x1 < x2 < x3 < x4 < x5 = x+width
      // y < y1 < y2 < y3 < y4 < y5 = y+height
      // the upper left handle consists of the points (xp, yp) such that
      // x <= xp <= x1 && y <= yp <= y1 etc.
      x1 = x + handleSize;
      x5 = x + width;
      x4 = x5 - handleSize;
      x2 = (x + x4)/2;
      x3 = x2 + handleSize;
      y1 = y + handleSize;
      y5 = y + height;
      y4 = y5 - handleSize;
      y2 = (y + y4)/2;
      y3 = y2 + handleSize;      

      if (topLeft != null) {
         topLeft.setFrameFromDiagonal     (x , y , x1, y1);
         topRight.setFrameFromDiagonal    (x4, y , x5, y1);
         bottomLeft.setFrameFromDiagonal  (x , y4, x1, y5);
         bottomRight.setFrameFromDiagonal (x4, y4, x5, y5);
      }
      if (topMiddle != null) {        
         topMiddle.setFrameFromDiagonal   (x2, y , x3, y1);
         leftMiddle.setFrameFromDiagonal  (x , y2, x1, y3);
         rightMiddle.setFrameFromDiagonal (x4, y2, x5, y3);
         bottomMiddle.setFrameFromDiagonal(x2, y4, x3, y5);
      }      
   }     

   private void initCornerHandles() {
      topLeft      = new Rectangle2D.Double();
      topRight     = new Rectangle2D.Double();
      bottomLeft   = new Rectangle2D.Double();
      bottomRight  = new Rectangle2D.Double();
   }
   
   private void initMiddleHandles() {
      topMiddle    = new Rectangle2D.Double();
      bottomMiddle = new Rectangle2D.Double();
      leftMiddle   = new Rectangle2D.Double();
      rightMiddle  = new Rectangle2D.Double();               
   }

/*************************
 * VComponent overrides: *
 * Event handling stuff  *
 *************************/
  
    /*  */
    public void setFocus(boolean b) {
        super.setFocus(b);
        if (   ( resizeMode & RM_FOCUS_ENTAILS_RESIZING) > 0 ){
            int newResize = resizeMode;
            if (hasFocus()) { //@@@XOR is easier?
                newResize = newResize | RM_RESIZING;
            } else {
                newResize = (newResize | RM_RESIZING) - RM_RESIZING;
            }
            setResizeMode(newResize);
        }
        if (   ( moveMode & MM_FOCUS_ENTAILS_MOVING) > 0 ){
            int newMove = moveMode;
            if (hasFocus()) { //@@@XOR is easier?
                newMove = newMove | MM_MOVING;
            } else {
                newMove = (newMove | MM_MOVING) - MM_MOVING;
            }
            setMoveMode(newMove);
        }
        recalculateShape();
    }
  
    /**
     * Delegate behaviour is kept intact; furthermore this method 
     * determines which resize handle (if any) is pressed.
     * xoff & yoff are recorded, to know how far handles are dragged
     */
    public void mousePressed(GraphMouseEvent e)  { 
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_PRESSED);
        if (eventDelegate != null) {
            eventDelegate.mousePressed(this, e);
        } else {
            super.mousePressed(e); // sets mouseX and mouseY, and records e.
            if (   ( ( getResizeMode() & RM_ALLOW_RESIZE) > 0 ) 
                && ( ( getResizeMode() & RM_RESIZING) > 0 ) ) {
                focusHandle = classifyHandle(mouseX, mouseY);
                switch (focusHandle) {
                    case 0: break; // not on border or handle.
                    
                    case 1: // upper left
                            xoff = x - mouseX;
                            yoff = y - mouseY;                   
                            break;
                    case 2: // upper right
                            xoff = x + width - mouseX;
                            yoff = y - mouseY;                   
                            break;
                    case 3: // lower left
                            xoff = x - mouseX;
                            yoff = y + height - mouseY;                   
                            break;
                    case 4: // lower right
                            xoff = x + width - mouseX;
                            yoff = y + height - mouseY;                   
                            break;
                    
                    case 5: // upper middle
                            yoff = y - mouseY;  
                            break;
                    case 6: // left middle
                            xoff = x - mouseX;  
                            break;        
                    case 7: // right middle
                            xoff = x + width - mouseX;   
                            break;       
                    case 8: // bottom middle
                            yoff = y + height - mouseY;   
                            break;          
                    case 9: // border
                            break;
                    case 10: // middle
                            break;
                }
            }
        }
    }

    /**
     * Delegate behaviour is kept intact; furthermore this method 
     * implements the moving and resizing behaviour using mouse input.
     */
   public void mouseDragged(GraphMouseEvent e)  { 
     MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_DRAGGED);
     if (eventDelegate != null) {
         eventDelegate.mouseDragged(this, e);
     } else {
         super.mouseDragged(e); // records dragging info.
         if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
            if (   ( ( getResizeMode() & RM_ALLOW_RESIZE) > 0 ) 
                && ( ( getResizeMode() & RM_RESIZING) > 0 ) ) {
                double mx = mouseX + xoff; // difference between the picking point when first pressed, 
                double my = mouseY + yoff; // which is somewhere inside the handle,         
                                           // and "ideal" corner/border points. mx and my are the points
                                           // to which the mouse should be considered to be dragged.
                double fixedx = x;  //the coordinates of the VComponent that should not move... usually the coordinates of a
                                    //handle. (not the one that is dragged...) 
                double fixedy = y;
                boolean doResize = false;
                //sx and sy are the new width and height...
                sx = width;
                sy = height;
                //dont forget to check that x & y are not locked in resizing...
                switch (focusHandle) {
                   case 0: //System.out.println("not on border"); 
                           break;          
        
                   case 1: if ((resizeMode & RM_X_LOCK) == 0) {
                               sx = Math.max((x+width-mx), minSize);
                           }
                           if ((resizeMode & RM_Y_LOCK) == 0) {
                               sy = Math.max((y+height-my), minSize);
                           }
                           fixedx = x + width;
                           fixedy = y + height;
                           doResize = true;
                           break;
                   case 2: if ((resizeMode & RM_X_LOCK) == 0) {
                               sx = Math.max((mx-x), minSize) ;
                           } 
                           if ((resizeMode & RM_Y_LOCK) == 0) {
                               sy = Math.max((y-my+height), minSize);
                           } 
                           fixedy = y + height;
                           doResize = true;
                           break;
                   case 3: if ((resizeMode & RM_X_LOCK) == 0) {
                               sx = Math.max((x+width-mx), minSize);
                           }
                           if ((resizeMode & RM_Y_LOCK) == 0) {
                               sy = Math.max((my-y), minSize);
                           }
                           fixedx = x + width;
                           doResize = true;
                           break;
                   case 4: if ((resizeMode & RM_X_LOCK) == 0) {
                               sx = Math.max((mx-x), minSize);
                           }
                           if ((resizeMode & RM_Y_LOCK) == 0) {
                               sy = Math.max((my-y), minSize);
                           }
                           doResize = true;
                           break;
                   
                   case 5: if ((resizeMode & RM_Y_LOCK) == 0) {
                               sy = Math.max((y+height-my), minSize);
                           }
                           fixedy = y + height;
                           doResize = true;
                           break;         
                   case 6: if ((resizeMode & RM_X_LOCK) == 0) {
                               sx = Math.max((x+width-mx), minSize);
                           }
                           fixedx = x + width;
                           doResize = true;
                           break;           
                   case 7: if ((resizeMode & RM_X_LOCK) == 0) {
                               sx = Math.max((mx-x), minSize);
                           }
                           doResize = true;
                           break;           
                   case 8: if ((resizeMode & RM_Y_LOCK) == 0) {
                               sy = Math.max((my-y), minSize);
                           }
                           doResize = true;
                           break;           
         
                   case 9: 
                   case 10:
                           if (   ((moveMode & MM_ALLOW_MOVE) > 0) 
                               && ((moveMode & MM_MOVING) > 0)) {
                              if ((moveMode & MM_X_LOCK) > 0 ) 
                                    dragDeltaX = 0;
                              if ((moveMode & MM_Y_LOCK) > 0 ) 
                                    dragDeltaY = 0;
                               doMove(dragDeltaX, dragDeltaY);
                            }
                            break;
                   }
                   if (doResize) { 
                        if (   ((resizeMode & RM_LOCK_RATIO) > 0) 
                            && (focusHandle >= 1)
                            && (focusHandle <= 4)
                            ) { //adjust sx or sy to the other...
                            //always adjust the one with the smallest deviation...
                            if ((sx * Math.abs(height/width)) > sy) { 
                                sy = sx * Math.abs(height/width);
                            } else {
                                sx = sy * Math.abs(width/height);
                            }
                        }
                        doResize(sx, sy, fixedx, fixedy);
                   }
              } else if (   ((moveMode & MM_ALLOW_MOVE) > 0) 
                         && ((moveMode & MM_MOVING) > 0)) {
                  if ((moveMode & MM_X_LOCK) > 0 ) 
                        dragDeltaX = 0;
                  if ((moveMode & MM_Y_LOCK) > 0 ) 
                        dragDeltaY = 0;
                  doMove(dragDeltaX, dragDeltaY);
              }
          }
      }
   }

    private void doMove(double dx, double dy) {
        if (moveRestrictions == MR_RESIZE_PARENT) {
            translate2D(VComponent.RECURSIVE, dx, dy);
        }
        if (moveRestrictions == MR_CONSTRAIN_CHILD) {
            if (parent != null) {
                if ((x+dx + width) > (parent.getX() + parent.getWidth())) {
                    dx = parent.getX() + parent.getWidth() - x - width;
                } else if ((x+dx) < parent.getX()) {
                    dx = parent.getX() - x;
                }
                if ((dy + y + height) > (parent.getY() + parent.getHeight())) {
                    dy = parent.getY() + parent.getHeight() - y - height;
                } else if ((dy + y) < parent.getY()) {
                    dy = parent.getY() - y;
                }
            }
            translate2D(VComponent.RECURSIVE, dx, dy);
        }
    }
    
    private void doResize(double sx, double sy, double fx, double fy) {
        if (moveRestrictions == MR_RESIZE_PARENT) { //default in VComponent
            resize2D(VComponent.COMPONENT_ONLY, sx, sy, new Point2D.Double(fx, fy));
        }
        if (moveRestrictions == MR_CONSTRAIN_CHILD) {
            //@@@ some complicated calculation should ensure that you cannot resize out of parent.
            resize2D(VComponent.COMPONENT_ONLY, sx, sy, new Point2D.Double(fx, fy));
        }
    }


/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

/*----- toXMLString support -----*/

    /**
     * Adds the following attributes to the attributes of the super class:
     * <ul>
     *      <li> "movemode", true if this VComponent can be moved using mouse input
     *      <li> "resizemode", true if this VComponent can be resized using mouse input
     * </ul>
     */
    protected String getAttributes() {
        String result = super.getAttributes();
        result = result + " movemode=\"" + moveMode + "\"";
        result = result + " resizemode=\"" + resizeMode + "\"";
        return result;
    } 

/*----- toXMLString support -----*/

    /**
     * Reads the following attribute besides the attributes of the super class:
     * <ul>
     *      <li> "movemode", true if this VComponent can be moved using mouse input
     *      <li> "resizemode", true if this VComponent can be resized using mouse input
     * </ul>
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("movemode")) {
            int mm = Integer.parseInt((String)attributes.get("movemode"));
            setMoveMode(mm);
        }
        if (attributes.containsKey("resizemode")) {
            int rm = Integer.parseInt((String)attributes.get("resizemode"));
            setResizeMode(rm);
        }
    }        

}