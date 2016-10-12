/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VComponent.java,v $
// Revision 1.1  2006/05/24 09:00:20  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.8  2003/07/16 16:47:31  dennisr
// *** empty log message ***
//
// Revision 1.7  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.5  2002/09/30 20:16:47  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.4  2002/09/27 08:15:43  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.3  2002/09/23 09:29:05  dennisr
// resize behaviour: the children do not move when parent is resized
//
// Revision 1.2  2002/09/23 07:44:44  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import parlevink.xml.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

/**
 * VComponent is the base interface for the visualisation classes in the parlegraph packages. 
 * It defines the communication to the model components, the basic drawing behaviour and several 
 * functions for moving, resizing, XML import/export, container structures, etc.
 * <br><i> For an extensive overview of the different parts of this interface,
 * see the developer's documentation of the parlegraph packages.</i>
 */
public interface VComponent extends MComponentListener,
				                    VComponentMovedListener,
				                    MouseDelegate,
				                    KeyDelegate,
				                    XMLizable {



/*++++++++++++++++++++++++++++++++++++*
 * Graph section.                     *
 * These methods support the          *
 * connection to the MComponent that *
 * is being visualised.               *
 *++++++++++++++++++++++++++++++++++++*/

    /**
     * This method sets the MComponent that this visualisation object should draw.
     */
    public void setMComponent(MComponent newObject);
    
    /**
     * This method returns the MComponent that this visualisation object draws.
     */
    public MComponent getMComponent();

/*++++++++++++++++++++++++++++++++++++*
 * Sizing and positioning section.    *
 * These methods set/get position     *
 * and size of the VComponent         *
 * itself, but not of sub components  *
 * or connected components.           *
 *++++++++++++++++++++++++++++++++++++*/

   /**
    * retrieves the x and y coordinates, in the form of a Point2D object,
    * of the top left corner of the bounding box, within the Swing
    * coordinate system.
    */
   public Point2D getLocation2D();
   
   /**
    * gets the x coordinate of the top left corner of the bounding box,
    * within the Swing coordinate system.
    */
   public double getX();

   /**
    * gets the y coordinate of the top left corner of the bounding box,
    * within the Swing coordinate system.
    */
   public double getY();

   /**
    * gets the width of the bounding box.
    */
   public double getWidth();

   /**
    * gets the height of the bounding box.
    */
   public double getHeight();

  /** 
   * Sets the values of the parameter
   * to the bounding box of the VComponent and returns this Rectangle2D
   * A new rectangle is allocated (only) if rv is null,
   * otherwise, the fields of rv are set, and rv is returned.
   * @param rv: A rectangle object to be set, or null.
   * @return Bounding box in the form of a Rectangle.
   */
   public Rectangle2D getBounds2D(Rectangle2D rv);
      
  /**
   * getCenter yields a point within the bounding box that is considered
   * the "center" of this VComponent. It is not necessarily the center
   * of the bounding box.
   * @return Point2D p, which denotes the center of this VComponent.
   */
   public Point2D getCenter2D();

   /**
    * Gets the position of the top-left corner of the bounding box,
    * relative to the VComponent that contains this VComponent.
    * That is, in terms of VComponent, this method uses
    * "relative" coordinates.
    * If this VComponent has no parent, the method boils down to
    * a call of getLocation2D. (This implies that the order of 
    * adding a VComponent as a child, and calling its setRelativeLocation method
    * is important: first add, then call setRelativeLocation.)
    */
   public Point2D getRelativeLocation2D();

	/**
	 * Returns the minimum width of this component.
	 */
	public double getMinimumWidth();
	
	/**
	 * Returns the minimum height of this component.
	 */
	public double getMinimumHeight();

   /**
    * Sets the position of the top-left corner of the bounding box,
    * relative to the Swing coordinate system.
    * <br>
    * The center of this VComponent moves exactly as much as 
    * the top left corner of the bounding box.
    * <p>
    * If calling this method would lead this VComponent to be displayed
    * outside of its parent's bounding box, the bounding box of the parent 
    * will be enlarged as well.
    */
   public void setLocation2D(double px, double py);

   /**
    * Sets the position of the top-left corner of the bounding box,
    * relative to the Swing coordinate system.
    * <br>
    * The center of this VComponent moves exactly as much as 
    * the top left corner of the bounding box.
    * <p>Will recalculate the shape of this VComponent.
    * <p>
    * If calling this method would lead this VComponent to be displayed
    * outside of its parent's bounding box, the bounding box of the parent 
    * will be enlarged as well.
    */
   public void setLocation2D(Point2D p);
   
   /**
    * Sets the size of the of the bounding box.
    * <br>If width or height are larger than the allowed minimum,
    * that parameter will be adjusted to the minimum. 
    * <br> The center will move relatively to the top left corner of the 
    * bounding box, to reflect the new size.
    * <p>Will recalculate the shape of this VComponent.
    * <p>
    * If calling this method would lead this VComponent to be displayed
    * outside of its parent's bounding box, the bounding box of the parent 
    * will be enlarged as well.
    */
   public void setSize2D(double width, double height);
     
   /**
    * Sets the bounding box, both changing size and location. 
    * The minimum size is observed, the center will be adjusted.
    * <p>Will recalculate the shape of this VComponent.
    * <p>
    * If calling this method would lead this VComponent to be displayed
    * outside of its parent's bounding box, the bounding box of the parent 
    * will be enlarged as well.
    */
   public void setBounds2D(Rectangle2D rv);

   /**
    * Sets the bounding box, both changing size and location. 
    * The minimum size is observed, the center will be adjusted.
    * <p>Will recalculate the shape of this VComponent.
    * <p>
    * If calling this method would lead this VComponent to be displayed
    * outside of its parent's bounding box, the bounding box of the parent 
    * will be enlarged as well.
    */
   public void setBounds2D(double x, double y, double width, double height);
   
   /**
    * Sets the center of the VComponent. If cx or cy out of bounding 
    * box, no change is made in that parameter.
    * No call to recalculateShape!
    */
   public void setCenter2D(double cx, double cy);

   /**
    * Sets the position of the top-left corner of the bounding box,
    * relative to the VComponent that contains this VComponent.
    * That is, in terms of VComponent, this method uses
    * "relative" coordinates.
    * If this VComponent has no parent, the method boils down to
    * a call of setLocation2D.
    * <p>Will recalculate the shape of this VComponent.
    * <p>
    * If calling this method would lead this VComponent to be displayed
    * outside of its parent's bounding box, the bounding box of the parent 
    * will be enlarged as well.
    */
   public void setRelativeLocation2D(double rx, double ry);

	/**
	 * Sets the minimum size of this component. 
	 * A VComponent cannot be made smaller than this minimum size.
	 */
	public void setMinimumSize(double minwidth, double minheight);

    /**
     * Determines whether point p is inside this VComponent or not.
     * The coordinates of p must be provided in the Swing coordinate system.
     * The result of this method is NOT affected by the settings enabled, allowfocus, hasfocus, isvisible, etc.
     */
    public boolean contains(Point2D p);
	
    /** 
     * Resets the bounding box of this VComponent to be slightly larger than the smallest enclosing rectangle of the 
     * bounding boxes of the children.
     * <br> If this VComponent does not have any children, the size will be set to the minumumsize.
     */
    public void resetBounds();

    /** 
     * Recursively resets the bounding box of all children, then calls resetBounds
     */
    public void resetBoundsRecursive();
    
/*++++++++++++++++++++++++++++++*
 * GUI interaction section.     *
 *++++++++++++++++++++++++++++++*

   /**
    * Enable or disable the VComponent.
    * <br> A disabled VComponent will ignore user events.
    * When a component is disabled its subcomponents will receive no events 
    * as well, whether they are enabled or not.
    */
   public void setEnabled(boolean b);
	
   /**
    * Returns true if this VComponent is enabled.
    */
   public boolean isEnabled();

   /**
    * Determines whether this VComponent is allowed to get focus. If false, setFocus cannot be set to true.
    * If false, and the VComponent currently ahs focus, hasFocus will be set false.
    */
   public void allowFocus(boolean b);

   /**
    * NOT YET STABLE. DONT TRUST THIS METHOD; CONTACT DENNIS
    */
   public boolean focusAllowed();

   /**
    * A component that has focus will receive 
    * user interaction events, like Key events.
    * setFocus is typically called by "low level" components;
    * it should not be called by applications that are build on top of the graph packages.
    * <br>
    * if focus not allowed, nothing happens.
    * <br>
    * In the painting section you will find a method drawFocusDecoration, which is responsible for 
    * embellishing the paint behaviour of a VComponent that has focus.
    */
   public void setFocus(boolean b);

   /**
    * Returns true if this VComponent currently has focus.
    */
   public boolean hasFocus();

   /**
    * Returns this and all subcomponents as far as they have focus.
    * A component that has focus is the component that is currently receiving 
    * user interaction events, like Key events.
    */
   public ArrayList getFocussedComponents();

   /**
    * Shows or hides this component depending on the value of parameter b.
    * Hiding a component automatically makes its sub components invisible.
    */
   public void setVisible(boolean b);
  
    /**
     * Returns true when this component should be visible when its parent is visible.
     */
    public boolean isVisible();

    /**
     * Returns the tooltip for this VComponent. If null, no tooltip.
     */
    public String getToolTipText();
        

/*++++++++++++++++++++++++++++++*
 * Event delegates              *
 ********************************/
 
    /**
     * It is possible to set mouse event delegates for a VComponent. When some Object implementing
     * MouseDelegate is registered as 
     * event delegate for a certain GraphMouseEvent, all GraphMouseEvents of that type will be redirected by
     * this VComponent to that delegate using the relevant GraphMouseEvent handling routines.
     */
    public void setMouseEventDelegate(MouseDelegate newDelegate, int mouseEventID);

    /**
     * Returns the delegate for a certain mouse event type, or null if no delegate was set
     * for that type.
     */
    public MouseDelegate getMouseEventDelegate(int mouseEventID);

    /**
     * It is possible to set key event delegates for a VComponent. When some Object implementing
     * KeyDelegate is registered as 
     * event delegate for a certain GraphKeyEvent, all GraphKeyEvents of that type will be redirected by
     * this VComponent to that delegate using the relevant GraphKeyEvent handling routines.
     */
    public void setKeyEventDelegate(KeyDelegate newDelegate, int keyEventID);

    /**
     * Returns the delegate for a certain key event type, or null if no delegate was set
     * for that type.
     */
    public KeyDelegate getKeyEventDelegate(int keyEventID);

/*++++++++++++++++++++*
 * Painting section.  *
 *++++++++++++++++++++*/
   
   /**
    * Paints the VComponent and calls the paintmethod of the
    * VComponents which are contained in the 
    * VComponent.
    * It is assumed that components at the end of the list 
    * should be drawn "on top" of components earlier in the list.
    * If this component has focus, a focus decoration will also be drawn.
    * <br>
    * If this VCmoponent is painted correctly, and all children as well, isDirty will be false afterwards.
    */
   public void paintVComponent(Graphics2D g2);

   /**
    * paints the background, on top of which all sub components are drawn.
    * This method will get overriden to create certain painting behaviour for new 
    * VComponents classes.
    */
   public void paintBackground2D(Graphics2D g2);

   /**
    * Paints a special decoration on a VComponent that has focus. 
    * This method will get overriden to create certain painting behaviour for new 
    * VComponents classes.
    */
   public void paintFocusDecoration(Graphics2D g2);

   /**
    * (Re-) calculates the shape that will be drawn by paintVComponent/PaintBackground2D.
    * Must be called after component's shape has been modified, or after the
    * the positioning/size parameters x, y, width, or height have been modified.
    * This method can be needed for example to recalculate bitmaps after resizing.
    * <br>
    * This method also notifies all VComponentListeners registered at this VComponent
    * that a change has occurred.
    */
   public void recalculateShape();

    /**
     * Set the color with which this VComponent should be drawn.
     * The effect of this method depends on the actual VComponent implementation,
     * for example VComponents drawing a bitmap might ignore this setting.
     */
    public void setColor(Color c);

    /**
     * Return the drawing color of this VComponent.
     */
    public Color getColor();

/*+++++++++++++++++++++*
 * Container section.  *
 *+++++++++++++++++++++*/

//maybe some methods to modify the Z-order...?

   /**
    * Yields the parent VComponent of this component, or null if
    * this VComponent is not contained in another VComponent.
    */
   public VComponent getParent();

   /**
    * Sets the parent VComponent of this component.
    * This method is only called by the parent VComponent when a child is added, 
    * to provide a backward link to the parent.
    *<b>maybe remove from interface, put in adapter?</b>
    */
   public void setParent(VComponent newParent);
   
   /**
    * Adds some other VComponent vc to this VComponent.
    * The position is "on top of" other components already present.
    */
   public void addVComponent(VComponent vc);
  
   /**
    * Removes a given VComponent vc from this VComponent.
    */
   public void removeVComponent(VComponent vc);

   /**
    * Removes all VComponents from this VComponent.
    */
   public void removeAllVComponents();

    /**
     * Returns an Iterator over all children of the VComponent.
     */
    public Iterator getChildren();
    
   /**
    * Yields the  (sub) component which is enabled, and contains point p,
    * possibly the component itself. 
    * The stacking order determines which component is returned, where
    * the component itself is at the bottom of the stack.
    * If no component can be found, null is returned.
    */
   public VComponent findVComponentAt(Point2D p);

   /**
    * Yields all(sub) components which are enabled, and contain point p,
    * possibly including the component itself. 
    * If no component can be found, an empty list is returned is returned.
    */
   public ArrayList findAllVComponentsAt(Point2D p);

    /**
     * returns the VGraph of which this component is an element, if any.
     * <BR>NB: This is NOT necessarily the parent!
    @@@ugly. should be removed in refactoring.
     */
    public VGraph getVGraph();

    /**
     * Sets the VGraph of which this component is an element. Should only be called by the VGraph
     * to provide a backward link to the containing VRgaph.
    @@@ugly. should be removed in refactoring.
     */
    public void setVGraph(VGraph graph);

    /**
     * returns the top VGraph of which this component is an element, if any.
     * If this VComponent is a top VGraph itself, it is returned. 
     * If there is no top VGraph, null is returned.
    @@@ugly. should be removed in refactoring.
     */
    public VGraph getTopVGraph();

    /**
     * If a VComponent visualizing the given MComponent is present as child in this container, it is
     * returned. Otherwise, null is returned.
     * This is used to track whether MComponents are visualized, and where.
    @@@ugly. refactoring: need better solution here.
     */
    public VComponent getViewerForMComponent(MComponent mc);
    
/*++++++++++++++++++++++++++*
 * Transformation section.  *
 *++++++++++++++++++++++++++*/

   public static final int COMPONENT_ONLY = 0;
   public static final int RECURSIVE = 1;

    /**
     * Resizes the VComponent, using the center as fixed point.
     * <br> The mode can be component_only or recursive (the last one also resizes the children).
     * <p> Will recalculate shape
     */
    public void resize2D(int mode, double newwidth, double newheight);

    /**
     * Resizes the VComponent, repositioning the VComponent in such a way
     * that fixedPoint (a Point2D within the Swing coordinate system that is also
     * within the bounding box of this VComponent) stays in the same location.
     * <br> The mode can be component_only or recursive (the last one also resizes the children).
     * <br> The children are moved in such a way that they stay in the same location w.r.t. the fixedpoint of the vcomponent.
     * <b>oi... that means they don't move at all :o)</b> 
     */
    public void resize2D(int mode, double newwidth, double newheight, Point2D fixedPoint);

    /**
     * Translates the VComponent over the given vector.
     * <br> The mode can be component_only or recursive (the last one also translates the children).
     */
    public void translate2D(int mode, double tx, double ty);


/*+++++++++++++++++++++++++++++++++++++++++++++++++++++*
 *+ Listeners section                                 +*
 *+ This section contains the communication           +* 
 *+ between the visualisation objects and the         +* 
 *+ VComponentMovedListeners                          +*
 *+++++++++++++++++++++++++++++++++++++++++++++++++++++*/

	/**
	 * Registers a VComponentMovedListener. VComponentMovedListeners are notified of changes in the 
	 * location of this VComponent through a call to notifyListeners().
	 */
	public void addVComponentMovedListener (VComponentMovedListener newListener);

	/**
	 * Removes a VComponentMovedListener. 
	 */
	public void removeVComponentMovedListener (VComponentMovedListener listener);

	/**
	 * This method notifies all registered VComponentMovedListener that the location of this 
	 * VComponent has been changed. Any Listeners that seem to have disappeared are
	 * removed from the list (emitting an error log text).
	 */
	public void notifyVComponentMovedListeners ();

/***********************
 * Graph Mouse Events *
 **********************/

    public void mouseClicked(GraphMouseEvent e);
    public void mouseEntered(GraphMouseEvent e);
    public void mouseExited(GraphMouseEvent e);
    public void mousePressed(GraphMouseEvent e);
    public void mouseReleased(GraphMouseEvent e);
    public void mouseDragged(GraphMouseEvent e);
    public void mouseMoved(GraphMouseEvent e); 

/***********************
 * Graph Key Events *
 **********************/

    public void keyPressed(GraphKeyEvent e);
    public void keyReleased(GraphKeyEvent e);
    public void keyTyped(GraphKeyEvent e);

}