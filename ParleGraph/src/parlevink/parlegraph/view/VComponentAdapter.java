/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VComponentAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.2  2005/11/25 15:28:21  swartjes
// Made a couple of graphical adjustments to clarify knowledge:
// - Vertices are drawn on top of edges in stead of below
// - Edges are light gray
// - Arrows are smaller
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.14  2005/01/05 08:59:52  dennisr
// *** empty log message ***
//
// Revision 1.13  2003/07/16 16:47:31  dennisr
// *** empty log message ***
//
// Revision 1.12  2003/06/27 07:39:35  zwiers
// List container reset to : java.util.List container
//
// Revision 1.11  2003/06/25 12:52:47  dennisr
// *** empty log message ***
//
// Revision 1.10  2003/06/24 10:43:23  dennisr
// *** empty log message ***
//
// Revision 1.9  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.8  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.7  2002/10/21 07:58:34  dennisr
// *** empty log message ***
//
// Revision 1.6  2002/09/30 20:16:47  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.5  2002/09/27 08:15:44  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.4  2002/09/23 09:29:05  dennisr
// resize behaviour: the children do not move when parent is resized
//
// Revision 1.3  2002/09/23 07:44:44  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.2  2002/09/16 15:16:24  dennisr
// documentatie
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.21  2002/06/04 12:54:22  reidsma
// minor fixes
//
// Revision 1.20  2002/05/16 15:13:43  reidsma
// improved visuals
//
// Revision 1.19  2002/05/16 12:14:59  reidsma
// concurrentModificationException opgelost
//
// Revision 1.18  2002/05/16 10:41:01  zwiers
// conflict resolved
//
// Revision 1.17  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.16  2002/03/04 12:16:33  reidsma
// Delegates:
// naast de mousedelegates zijn er nu ook keydelegates. Hiervoor zijn een paar functies hernoemd (getMouseEventdelegate ipv geteventdelegate, etc)
// en een paar toegevoegd. De defaultController krijgt nu alle keyevents te horen.
//
// Revision 1.15  2002/02/12 09:36:17  reidsma
// sourceCoord & targetCoord are now always non-null;
// isRightMouse and co are now static
//
// Revision 1.14  2002/02/11 09:18:21  reidsma
// no message
//
// Revision 1.13  2002/02/05 14:35:58  reidsma
// layouting
//
// Revision 1.12  2002/02/04 16:19:35  reidsma
// debugging
//
// Revision 1.11  2002/02/01 13:22:28  reidsma
// setColor added
//
// Revision 1.10  2002/02/01 13:03:51  reidsma
// edit controller; general maintenance
//
// Revision 1.9  2002/01/29 14:58:07  reidsma
// debugging
//
// Revision 1.8  2002/01/28 14:00:45  reidsma
// Enumerations en Vectors vervangen door Iterator en ArrayList
//
// Revision 1.7  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.6  2002/01/22 16:05:07  reidsma
// no message
//
// Revision 1.5  2002/01/22 12:29:49  reidsma
// XML; multi-line textlabels; arrowheads; moving endpoints (first test)
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import parlevink.xml.*;
import java.util.logging.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

/**
 * VComponentAdapter is the default implementation of the VComponent interface. 
 * 
 * For documentation on the methods from the interface, see the interface documentation.
 * For documentation on initialisation of new VComponents, see the constructor.
 */
public class VComponentAdapter implements VComponent {

/*++++++++++++++++++++*
 * Variables section. *
 *++++++++++++++++++++*/
 
    /**
     * The VGraph to which this VComponent belongs. @@@ should be removed in refactoring!
     */
    VGraph currentVGraph;
    
    /* MComponent object administration */
    protected MComponent mcomponent;
  
    /* Administration fo VComponentListeners registered with this VComponent */
	EventListenerList movedListenerList; 
	GraphEvent graphEvent; //the graphevent used to warn the listeners about changes
    GraphEvent mcChangedEvent = new GraphEvent(this); //used to warn me about new MC....

    /* The event delegates... */
    ArrayList mouseEventDelegates;  //an ArrayList indexed with mouse-event ID's
    ArrayList keyEventDelegates;    //an ArrayList indexed with key-event ID's
    
    /* visualisation (positioning etc) */
    protected double         x, y;
    protected double         width, height;
    protected double         minimumwidth, minimumheight;
    protected double         centerx, centery;
    protected boolean        enabled, visible, hasfocus, allowfocus;
    protected Color          color;
    	 	 
    protected Stroke defaultStroke;
    protected RectangularShape focusBound;

    /**
     * The Stroke for drawing focus bounds around a VComponent is shared across
     * ALL subclasses of VComponentAdapter.
     */
    protected static Stroke focusStroke;
        
    /*container support*/
    protected VComponent     parent;
    protected java.util.List container;
    protected ListIterator   iter;     //used to iterate the children

    /* event support */
    
    protected GraphMouseEvent     mousePress, mouseRelease, mouseDrag; //three variables used to record the latest mouse events
    protected int            mouseX, mouseY;   // (x,y) coordinates of last mouse event
    protected int            dragDeltaX, dragDeltaY; // displacement between current and previous drag event

	/* logging */
	protected static Logger logger;;

    /* a counter used to check some efficiency issues */
    int gvfmcCounter = 0;

/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/

    /**
     * Creates a new VComponentAdapter.
     *
     * <p>The construction is delegated to the methods checkStaticInit(), preInit(), 
     * init() and postInit(). Changing the construction behaviour is possible, if you 
     * adhere to the conventions of these methods. 
     * First of all: the constructor without parameters of any new VComponent should ONLY
     * call the empty constructor of its superclass.
     * Other constructors, introduced for convenience, should first call this() (their own 
     * emtpy constructor) and after that proceed with any extra code to process the extra 
     * arguments. Default initialisation is done overriding the other init methods:
     * checkStaticInit, preInit, init and postInit.
     * <p>For example, if you want to have 
     * a different initial size for your VComponent, it is enough to override preInit():
     * after calling the superclass version of it you set new values for width and height.
     * Since the actual bounds ond border objects are created in the init() method, which is 
     * executed after preInit(), it will automatically have the correct (new) initial size.
     * <ul>
     * <li>checkStaticInit() should make sure that all relevant static variables have been initialized
     * <li>preInit() should initialize all SIMPLE attributes, such as width, height, etc. or objects 
     *     that are completely independent of any previous initialization
     * <li>init() creates objects which are dependent on the simple attributes, such as borders
     * <li>postInit() does any initialization that depends on the existence of these objects.
     * </ul>
     * <p>
     * A note on constructors: if you define no constructors for a subclass of VComponentAdapter,
     * it will automatically inherit the basic empty constructor. HOWEVER, if you define extra constructors,
     * you should also implement the constructor with no parameters and have it call super() as its only 
     * statement.
     *
     * <p>Most methods should not be called on a VComponent that has not yet been fully initialized.
     * Methods that CAN be called before that, should state so in their source code documentation.
     *
     * <p>A note on using static variables for default values: if you want to override a default value
     * in a subclass, and you change the value of the static variable, it will also change for all objects
     * of the superclass or other subclasses. It is better to use factory methods such as createDefaultShape(),
     * which can safely be overridden.
     *
     */
    public VComponentAdapter () {
//     * <p> Another note on default values: maybe we want to use a "lnf-like" construction,
//     * making some separate class responsible for distributing default values:
//     * <br>DefaultValuesManager.getDefaultResizeMode(Class c); ??? Though it looks a bit overkill, I must admit.
        checkStaticInit();
        preInit();
        init();
        postInit();
    } 
  
 
    /**
     * Initializes static variables if they have not yet been initialized.
     * If overridden by subclass: call super!
     */
    public void checkStaticInit() {
        if (focusStroke == null) {
            float[] dasharray = new float[2];
            dasharray[0] = 2;
            dasharray[1] = 4;
            focusStroke = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dasharray, 0);
        }
    	//initialize logger for this class:
	    if (logger == null) {
	    	logger = Logger.getLogger(getClass().getName());	    
	    }
    }
 
    /**
     * Initializes simple variables for this VComponent, such as x, y, etc
     * (Not the objects which are dependent on these variables!)
     */
    public void preInit() {
        enabled = true;
        visible = true;
        mcomponent = null;
        hasfocus=false;
        allowfocus=true;
    
        movedListenerList = new EventListenerList();
	    graphEvent = null;
	

        x = 0;
        y = 0;
        width = 50;
        height = 25;
        minimumwidth = 2;
        minimumheight = 2;
        centerx = 5;
        centery = 5;
        
        color = Color.black;

        mouseEventDelegates = new ArrayList();
        for (int i = GraphMouseEvent.MOUSE_FIRST; i <= GraphMouseEvent.MOUSE_LAST + 1; i++)
            mouseEventDelegates.add(null);
        
        keyEventDelegates = new ArrayList();
        for (int i = GraphKeyEvent.KEY_FIRST; i <= GraphKeyEvent.KEY_LAST + 1; i++)
            keyEventDelegates.add(null);
    }
 
    /**
     * Initializes objects for this VComponent that are dependent on the simple variables
     */
    public void init() {
        focusBound = createDefaultFocusBound();
    }
    
    public RectangularShape createDefaultFocusBound() {
        return new Rectangle2D.Double (x, y, width, height);
    }
    
    /**
     * Any initialization that depends on or operates on the objects created in
     * init() is done here...
     */
    public void postInit() {
        //@@nothing yet
    }
 
    /**
     * Will create a VComponent with given bounds.
     */
    public VComponentAdapter (double x, double y, double width, double height) {
	    this();
	    setBounds2D(x, y, width, height);
    } 

/*++++++++++++++++++++++++++++++++++++*
 * Graph section.                     *
 * These methods support the          *
 * connection to the MComponent that *
 * is being visualised.               *
 *++++++++++++++++++++++++++++++++++++*/

    /**
     * This method sets the MComponent that this visualisation object should draw.
     * Also updates the MComponentListener connections accordingly.
     */
    public void setMComponent(MComponent newObject){
        if (newObject != mcomponent) {
            if (mcomponent != null) {//if was already registered to listen to someone else, unregister
                mcomponent.removeMComponentListener(this);
            }
            mcomponent = newObject;
            if (mcomponent != null) {
                mcomponent.addMComponentListener(this);
             }
             mcomponentChanged(mcChangedEvent);
        }
    }

    public MComponent getMComponent(){
        return mcomponent;
    }

    /**
     * Refreshes the view of this visualisation to reflect the changes in the MComponent.
     */
    public void mcomponentChanged(GraphEvent ge) {
        recalculateShape();
    }

/*++++++++++++++++++++++++++++++++++++*
 * Sizing and positioning section.    *
 *++++++++++++++++++++++++++++++++++++*/

    public Point2D getLocation2D(){
        return new Point2D.Double(x, y);
    }
   
    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public Rectangle2D getBounds2D(Rectangle2D rv){
        if (rv == null) 
            rv = new Rectangle2D.Double();
        rv.setRect(x, y, width, height);
        return rv;
    }
      
    public Point2D getCenter2D(){
        return new Point2D.Double(centerx, centery);
    }

    public Point2D getRelativeLocation2D() {
        if (parent == null) 
            return getLocation2D();
        else return new Point2D.Double(x - parent.getX(), y - parent.getY());
    }

	public double getMinimumWidth() {
		return minimumwidth;
	}
	
	public double getMinimumHeight() {
		return minimumheight;
	}
	
    public void setLocation2D(double px, double py){
        centerx = centerx - x + px; //also move center....
        centery = centery - y + py;
        x = px;
        y = py;
        recalculateShape();    
    }

    public void setLocation2D(Point2D p){
        setLocation2D(p.getX(), p.getY());
    }
   
    /**
     * Sets the size of the of the bounding box.
     */
    public void setSize2D(double newwidth, double newheight){
        if (newwidth <= minimumwidth) {
             newwidth = minimumwidth;
        }
        if (newheight <= minimumheight) {
            newheight = minimumheight;
        }
        centerx = x + (centerx - x) * newwidth / this.width;
        centery = y + (centery - y) * newheight / this.height;
        this.width = newwidth;
        this.height = newheight; 
        recalculateShape();   
    }
     
    public void setBounds2D(Rectangle2D rv){
        //minimumsize check will be done in other method
        setBounds2D(rv.getX(), rv.getY(), rv.getWidth(), rv.getHeight());
    }

    public void setBounds2D(double x, double y, double width, double height){
        //minimumsize check will be done in resize method
        //checking the bounds of the parent will also be done in the resizemethod & in setlocation2D.
        setLocation2D(x, y); 
        setSize2D(width, height);
        recalculateShape();
    }
   
    public void setCenter2D(double cx, double cy){
        if ((x <= cx) && (cx <= x+ width))
            centerx = cx;
        if ((y <= cy) && (cy <= y+ height))
            centery = cy;
    }

    public void setRelativeLocation2D(double px, double py) {
        if (parent == null) {
            setLocation2D(px, py);
        } else {
            setLocation2D(parent.getX() + px, parent.getY() + py);  
        }
    }
    
	public void setMinimumSize(double minwidth, double minheight) {
		minimumwidth = Math.max(10, minwidth);
		minimumheight = Math.max(10, minheight);
	}

    /**
     * Determines whether point p is inside this VComponent or not.
     * The coordinates of p must be provided in the Swing coordinate system.
     * The default implementation determines whether p is inside the bounding box.
     * <BR>
     */
    public boolean contains(Point2D p) {
        double px = p.getX();
        double py = p.getY();
  	    return ( (x<=px) && (px<=x+width) && (y<=py) && (py<=y+height) );
    }
    
    /** 
     * Resets the bounding box of this VComponent to be slightly larger than the smallest enclosing rectangle of the 
     * bounding boxes of the children.
     */
    public void resetBounds() {
        double newX = x;
        double newY = y;
        double newX2 = x + minimumwidth;
        double newY2 = y + minimumheight;
        if (container != null) {
            Iterator childIt = container.iterator();
            //init with first child...
            if (childIt.hasNext()) {
                VComponent nextChild = (VComponent)childIt.next();
                newX = nextChild.getX();
                newY = nextChild.getY();
                newX2 = newX + nextChild.getWidth();
                newY2 = newY + nextChild.getHeight();
                //continue checking other children
                while (childIt.hasNext()) {
                    nextChild = (VComponent)childIt.next();
                    newX = Math.min(newX,nextChild.getX());
                    newY = Math.min(newY,nextChild.getY());
                    newX2 = Math.max(newX2,nextChild.getX() + nextChild.getWidth());
                    newY2 = Math.max(newY2,nextChild.getY() + nextChild.getHeight());
                }
                //to avoid overlapping borders, to keep some space for the resizehandles
                newX = newX  -3;
                newY = newY  -3;
                newX2 = newX2 + 3;
                newY2 = newY2 + 3;
            } 
        }
        setBounds2D(newX, newY, newX2 - newX, newY2 - newY);
    }
    
    /** 
     * Recursively resets the bounding box of all children, then calls resetBounds
     */
    public void resetBoundsRecursive() {
        if (container != null) {
            Iterator childI = container.iterator();
            while (childI.hasNext())
                ((VComponent)childI.next()).resetBoundsRecursive(); 
        }
        resetBounds();        
    }
    
/*++++++++++++++++++++++++++++++*
 * GUI interaction section.     *
 *++++++++++++++++++++++++++++++*/

    public void setEnabled(boolean b){
        enabled = b;
    }
	
    public boolean isEnabled(){
        return enabled;
    }

   public void allowFocus(boolean b) {
        allowfocus = b;
        if (!b && hasfocus)
            setFocus(false);
   }

    public boolean focusAllowed() {
        return allowfocus;
    }

    public void setFocus(boolean b){
        if (allowfocus || !b)
            hasfocus = b;
    }
   
    public boolean hasFocus(){
        return hasfocus;
    }

    public ArrayList getFocussedComponents() {
        ArrayList result = new ArrayList();
        if (hasfocus)
            result.add(this);
        if (container != null) {
            iter = container.listIterator(container.size());
            VComponent vc = null;
            while (iter.hasPrevious()) {
               vc = ((VComponent) iter.previous());
               result.addAll(vc.getFocussedComponents());
            }
        }
        return result;
    }        

    public void setVisible(boolean b){
        visible = b;
    }
  
    public boolean isVisible(){
        return visible;
    }

    public String getToolTipText() {
        return null;
    }

/*++++++++++++++++++++++++++++++*
 * Event delegates              *
 ********************************/
 
    public void setMouseEventDelegate(MouseDelegate newDelegate, int mouseEventID) {
        if ((GraphMouseEvent.MOUSE_FIRST <= mouseEventID) && (mouseEventID <= GraphMouseEvent.MOUSE_LAST)) {
            mouseEventDelegates.set(mouseEventID - GraphMouseEvent.MOUSE_FIRST, newDelegate);
        }
    }

    public MouseDelegate getMouseEventDelegate(int mouseEventID) {
        if ((GraphMouseEvent.MOUSE_FIRST <= mouseEventID) && (mouseEventID <= GraphMouseEvent.MOUSE_LAST)) {
            return (MouseDelegate)mouseEventDelegates.get(mouseEventID - GraphMouseEvent.MOUSE_FIRST);
        }
        return null;
    }

    public void setKeyEventDelegate(KeyDelegate newDelegate, int keyEventID) {
        if ((GraphKeyEvent.KEY_FIRST <= keyEventID) && (keyEventID <= GraphKeyEvent.KEY_LAST)) {
            keyEventDelegates.set(keyEventID - GraphKeyEvent.KEY_FIRST, newDelegate);
        }
    }

    public KeyDelegate getKeyEventDelegate(int keyEventID) {
        if ((GraphKeyEvent.KEY_FIRST <= keyEventID) && (keyEventID <= GraphKeyEvent.KEY_LAST)) {
            return (KeyDelegate)keyEventDelegates.get(keyEventID - GraphKeyEvent.KEY_FIRST);
        }
        return null;
    }

/*++++++++++++++++++++++++++++++++*
 * Event handling methods         *
 *++++++++++++++++++++++++++++++++*/
 
    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise ignore it.
     */
    public void mouseClicked(GraphMouseEvent e) {
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_CLICKED);
        if (eventDelegate != null) {
            eventDelegate.mouseClicked(this, e);
        } else {
            
        }
    }
    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise ignore it.
     */
    public void mouseEntered(GraphMouseEvent e) {
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_ENTERED);
        if (eventDelegate != null) {
            eventDelegate.mouseEntered(this, e);
        } else {
            
        }
    }
    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise ignore it.
     */
    public void mouseExited(GraphMouseEvent e) {
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_EXITED);
        if (eventDelegate != null) {
            eventDelegate.mouseExited(this, e);
        } else {
            
        }
    }

   /**
    * Default implementation: If there is a delegate registered for this event,
    * pass the event on. Otherwise:
    * Records the current mouse event in mousePress, 
    * and determines the mouseX and mouseY coordinates.
    */
    public void mousePressed(GraphMouseEvent e) {
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_PRESSED);
        if (eventDelegate != null) {
            eventDelegate.mousePressed(this, e);
        } else {
            mousePress = e;
            mouseX = e.getX();
            mouseY = e.getY();
            Point2D p = new Point2D.Double();
            p.setLocation(mouseX,mouseY);
        }
    }

    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise: record the current mouse event in mouseRelease.
     */
    public void mouseReleased(GraphMouseEvent e) {
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_RELEASED);
        if (eventDelegate != null) {
            eventDelegate.mouseReleased(this, e);
        } else {
            mouseRelease = e;
        }
    }

    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise: record the current mouse event in mouseDrag, and its
     * coordinates and offset in mouseX, mouseY, dragDeltaX, dragDeltaY.
     */
    public void mouseDragged(GraphMouseEvent e) {
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_DRAGGED);
        if (eventDelegate != null) {
            eventDelegate.mouseDragged(this, e);
        } else {
            mouseDrag = e;
            dragDeltaX = mouseDrag.getX() - mouseX;  
            dragDeltaY = mouseDrag.getY() - mouseY;
            mouseX = mouseDrag.getX();
            mouseY = mouseDrag.getY();
        }
    }
    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise ignore it.
     */
    public void mouseMoved(GraphMouseEvent e) {
        MouseDelegate eventDelegate = getMouseEventDelegate(GraphMouseEvent.MOUSE_MOVED);
        if (eventDelegate != null) {
            eventDelegate.mouseMoved(this, e);
        } else {
            
        }
    }

    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void mouseClicked(VComponent ref, GraphMouseEvent e) {
        mouseClicked(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void mouseEntered(VComponent ref, GraphMouseEvent e) {
        mouseEntered(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void mouseExited(VComponent ref, GraphMouseEvent e) {
        mouseExited(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void mousePressed(VComponent ref, GraphMouseEvent e) {
        mousePressed(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void mouseReleased(VComponent ref, GraphMouseEvent e) {
        mouseReleased(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void mouseDragged(VComponent ref, GraphMouseEvent e) {
        mouseDragged(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void mouseMoved(VComponent ref, GraphMouseEvent e) {
        mouseMoved(e);
    } 


    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise ignore it.
     */
   public void keyPressed(GraphKeyEvent ke)     { 
        KeyDelegate eventDelegate = getKeyEventDelegate(GraphKeyEvent.KEY_PRESSED);
        if (eventDelegate != null) {
            eventDelegate.keyPressed(this, ke);
        } else {

        }
   }
   
    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise ignore it.
     */
   public void keyReleased(GraphKeyEvent ke)    { 
        KeyDelegate eventDelegate = getKeyEventDelegate(GraphKeyEvent.KEY_RELEASED);
        if (eventDelegate != null) {
            eventDelegate.keyReleased(this, ke);
        } else {
            
        }
   }
   
    /**
     * Default implementation: If there is a delegate registered for this event,
     * pass the event on. Otherwise ignore it.
     */
   public void keyTyped(GraphKeyEvent ke)       { 
        KeyDelegate eventDelegate = getKeyEventDelegate(GraphKeyEvent.KEY_TYPED);
        if (eventDelegate != null) {
            eventDelegate.keyTyped(this, ke);
        } else {
            
        }
   }

    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void keyPressed(VComponent ref, GraphKeyEvent e) {
        keyPressed(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void keyReleased(VComponent ref, GraphKeyEvent e) {
        keyReleased(e);
    }
    /**
     * If this VComponent is a delegate for a type of event, the default
     * action is to handle it as if the event was on this VComponent itself.
     */
    public void keyTyped(VComponent ref, GraphKeyEvent e) {
        keyTyped(e);
    }

/*++++++++++++++++++++*
 * Painting section.  *
 *++++++++++++++++++++*/
   
    public void paintVComponent(Graphics2D g2){
        if ( !isVisible() ) 
            return;
        g2.setColor(color); 
        paintBackground2D(g2);
        if (container != null) {
            VComponent nextVC;

            for (int i = 0; i < container.size(); i++) {
                nextVC = (VComponent)container.get(i);
               	nextVC.paintVComponent (g2);
            }
            
        }
        if (hasFocus()) {
            paintFocusDecoration(g2);
        }
    }

    /**
     * Should paint the background, on top of which all sub components are drawn. 
     * This is one of the methods that will usually be overridden by new visualisation classes.
     * The default implementation paints nothing.
     */
    public void paintBackground2D(Graphics2D g2){
    }

    /**
     * Paints a special decoration on a VComponent that has focus. 
     * Default: a blue dotted line around the bounding box. 
     */
    public void paintFocusDecoration(Graphics2D g2) {
        //set line to dotted
        defaultStroke = g2.getStroke();
        g2.setColor(Color.blue);
        g2.setStroke(focusStroke);
        //paint a beautiful dotted line....
        g2.draw(focusBound);
        //set line to what it was....
        g2.setStroke(defaultStroke);
        g2.setColor(color);
    }

    /**
     * Re-calculates the shape that will be drawn by paintVComponent/PaintBackground.
     * Must be called after the component's shape has been modified, or after the
     * the positioning/size parameters x, y, width, or height have been modified.
     * This method is called automatically by the implementation of
     * methods like setLocation2D, setSize  etc.
     * An example of operations that could be performed here,
     * is recalculating some buffered image after the VComponent has been resized.
     * Since this recalculation might involve a resizing of the image, 
     * VComponentMovedListeners is also called at the end of this method.
     */
    public void recalculateShape() {
        focusBound.setFrame(x, y, width, height);
        notifyVComponentMovedListeners();
    }

/*    het idee was goed maar dit kost VEEL teveel tijd. Zeker met de overvloed aan 'recalculateshape' calls.
protected void checkParentBounds(double x, double y, double width, double height) {
        //if (true) return;//@@@ tijdelijk uitgezet.
        if (parent == null)
            return;
        double px = parent.getX();
        double py = parent.getY();
        double pw = parent.getWidth();
        double ph = parent.getHeight();
        if (   (x < px)
            || (y < pw)
            || ((x + width) > (px + pw))
            || ((y + height) > (py + ph)) ) {
            px = Math.min(x, px);
            py = Math.min(y, py);
            pw = Math.max(x+width, px + pw) - px;
            ph = Math.max(y+height, py + ph) - py;
            parent.setBounds2D(px, py, pw, ph);
        }
    }*/
 
    public void setColor(Color c) {
        color = c;
    }

    public Color getColor() {
        return color;
    }

/*+++++++++++++++++++++*
 * Container section.  *
 *+++++++++++++++++++++*/

    public VComponent getParent(){
        return parent;
    }

    public void setParent(VComponent newParent){
        parent = newParent;
    }
   
    public void addVComponent(VComponent vc){
        if (container == null) 
            container = new ArrayList();
        if (!(container.contains(vc))) {
            container.add(vc);
            vc.setParent(this);
        }
    }
  
    public void removeVComponent(VComponent vc){
        if (container != null) 
            container.remove(vc);  
        if (vc != null) 
            vc.setParent(null);
    }

    public void removeAllVComponents(){
        if (container == null) 
            return;
        while (container.size() > 0) {
            ((VComponent)container.get(0)).setParent(null);
            container.remove(0);  
        }
    }

    public Iterator getChildren() {
        if (container == null)
            container = new ArrayList();
        return container.iterator();
    }

   public VComponent findVComponentAt(Point2D p){
        if (!focusBound.contains(p)) {
//            return null;
        }
        if (! isEnabled()) {
            return null;
        }
        VComponent vc;
        if (container != null) {
            iter = container.listIterator(container.size());
            while (iter.hasPrevious()) {
               vc = ((VComponent) iter.previous()).findVComponentAt(p);
               if (vc != null) {
                   return vc;
               }
            }
        }
        if (contains(p))
            return this;
        return null;
    }

   public ArrayList findAllVComponentsAt(Point2D p) {
        ArrayList result = new ArrayList();
        if (!focusBound.contains(p)) {
//            return result; edges vallen soms buiten hun parent :(
        }
        if (! isEnabled()) {
            return result;
        }
        if (container != null) {
            iter = container.listIterator(container.size());
            while (iter.hasPrevious()) {
                ArrayList al = ((VComponent) iter.previous()).findAllVComponentsAt(p);
                result.addAll(al);
            }
        }
        if (contains(p))
            result.add(this);
        return result;
    }
    
/**
*@@@ should be removed in refactoring!
*/
    public VGraph getVGraph() {
        return currentVGraph;
    }

    public void setVGraph(VGraph graph) {
        //only called by parent VGraphs!!!!!!!!!!!!!!!!!!!!!!
        if (graph == currentVGraph)
            return;
        currentVGraph = graph;
        recalculateShape();
    }
    
    /**
    @@@ugly. should be removed in refactoring.
    */
    public VGraph getTopVGraph() {
        VGraph parent = getVGraph();
        if (parent == null) 
            if (this instanceof VGraph) {
                return (VGraph)this;
            } else {
                return null;
            }
        while (parent.getVGraph() != null)
            parent = parent.getVGraph();
        return parent;
    }

     public VComponent getViewerForMComponent(MComponent mc) {
        gvfmcCounter++;
        if (gvfmcCounter == 100) {
            System.out.println("Another 100 gvfmc's");
        }
        if (mc == mcomponent)
            return this;
        //check all children
        if (container != null) {
            iter = container.listIterator();
            while (iter.hasNext()) {
                VComponent next = (VComponent)iter.next();
                VComponent inChild = next.getViewerForMComponent(mc);
                if (inChild != null) {
                    return inChild;
                }
            }
        }
        return null;
    }

      
/*++++++++++++++++++++++++++*
 * Transformation section.  *
 *++++++++++++++++++++++++++*/

    /**
     * Resizes the VComponent, using the center as fixed point. Explicitly uses 
     * the other resize2D method, so subclasses only need to override the other method.
     */
    public void resize2D(int mode, double newwidth, double newheight) {
        resize2D(mode, newwidth, newheight, getCenter2D());
    }

    public void resize2D(int mode, double newwidth, double newheight, Point2D fixedPoint) {
        if (newwidth <= minimumwidth) {
            newwidth = minimumwidth;
        }
        if (newheight <= minimumheight) {
            newheight = minimumheight;
        }
        double yfact = newheight/height;
        double xfact = newwidth/width;
        Point2D oldc = getCenter2D();
		setSize2D(newwidth, newheight); //may change center....
        //move x,y & center to keep fixedPoint
        setLocation2D(fixedPoint.getX() - xfact * (fixedPoint.getX() - x), fixedPoint.getY() - yfact * (fixedPoint.getY() - y));
        Point2D newc = getCenter2D();
/*        //always update location of children, even if they don't need to be resized. Child locations are fixed wrt fixcedpoint
  
  children don't move .......
  
        if (container != null) {
            iter = container.listIterator();
            while (iter.hasNext()) {
                VComponent next = (VComponent)iter.next();
                Point2D subLoc = next.getLocation2D();
                Point2D locationVector = new Point2D.Double(subLoc.getX() - oldc.getX(),subLoc.getY() - oldc.getY());
                double subnewx = newc.getX()+locationVector.getX();
                double subnewy = newc.getY()+locationVector.getY();
                next.translate2D(VComponent.RECURSIVE, subnewx-subLoc.getX(), subnewy-subLoc.getY());
            }        
        }*/
        if ( (mode & VComponent.RECURSIVE) != 0) {
            logger.severe("recursive resizing not yet supported");
        }
        recalculateShape();
    }

    public void translate2D(int mode, double tx, double ty) {
		setLocation2D(x + tx, y + ty);
        if ( (mode & VComponent.RECURSIVE) != 0) {
           if (container != null) {
              iter = container.listIterator();
              while (iter.hasNext()) {
                ((VComponent)iter.next()).translate2D(VComponent.RECURSIVE, tx, ty);
              }        
           }
        }
        recalculateShape();
    }

/*+++++++++++++++++++++++++++++++++++++++++++++++++++++*
 *+ Listeners section                                 +*
 *+ This section contains the communication           +* 
 *+ between the viewer objects and the movedlisteners +*
 *+++++++++++++++++++++++++++++++++++++++++++++++++++++*/

	public void addVComponentMovedListener (VComponentMovedListener newListener) {
		movedListenerList.add(VComponentMovedListener.class, newListener); 
		if (graphEvent == null) 
			graphEvent = new GraphEvent(this);
		newListener.vcomponentMoved(graphEvent); //to make sure that the componentlistener is up to date....
	}

	public void removeVComponentMovedListener (VComponentMovedListener listener){
		movedListenerList.remove(VComponentMovedListener.class, listener);
	}

	public void notifyVComponentMovedListeners (){
		Object[] listeners = movedListenerList.getListenerList(); 
		// Process the listeners last to first, notifying 
		// those that are interested in this event 
		for (int i = listeners.length-2; i>=0; i-=2) { 
			if (graphEvent == null) 
				graphEvent = new GraphEvent(this);
			((VComponentMovedListener)listeners[i+1]).vcomponentMoved(graphEvent);
		}
	}

    public void vcomponentMoved(GraphEvent ge) {
    }
    

/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/
 
/*----- rerouting -----*/
 
    /**
     * This method is rerouted to readXML(XMLTokenizer tokenizer).
     */
    public void readXML(Reader in) throws IOException {
        readXML(new XMLTokenizer(in));
    }
   
    /**
     * This method is rerouted to readXML(XMLTokenizer tokenizer).
     */
    public void readXML(String s) {
        try {
            readXML(new XMLTokenizer(s));
        } catch (IOException ex) {
            logger.severe("IOException caught & ignored in mComponentAdapter.readXML(String s): " +ex);
        }
    }
    
    /**
     * Uses toXMLString() to write XML to the PrintWriter.
     */
    public void writeXML(PrintWriter out) {
        out.print(toXMLString());
    }

/*----- toXMLString support -----*/

    /**
     * This method is as generic as possible for VComponents and subclasses.
     * The default operation uses the following protected methods:
     * <ul>
     *      <li> getOpenStart()
     *      <li> getAttributes()
     *      <li> getContent()
     *      <li> getEnd()
     * </ul>
     * The methods getOpenStart() and getEnd() provide begin and end tags using the full class name of
     * the VComponent as tags.
     * <br>
     * The method getAttributes() should return a list of (attribute,value) pairs. To add attributes for 
     * a new subclass of VComponent simply override this method, call the super class implementation
     * and add your own attributes at the beginning or end of the attribute string.
     * <br>
     * The method getContent() returns the content that should go between the start and end tags.
     * The default implementation returns an empty string.
     */ 
    public String toXMLString() {
        return getOpenStart() + getAttributes() + ">" + getContent() + getEnd();
    }

    /**
     * Default: returns the start tag without closing bracket, using the full class name as tag.
     */
    protected String getOpenStart() {
        return "<" + getClass().getName() + " ";
    } 

    /**
     * Default: returns an empty string.
     */
    protected String getContent() {
        return "";
    } 

    /**
     * Default: returns the (attribute,value) pairs for the following attributes:
     * x, y, w, h, cx, cy, minimumwidth, minimumheight, 
     * enabled, visible, hasfocus, mcomponent.getID().
     */
    protected String getAttributes() {
        String result = 
               "x=\"" +
                 getX() + 
               "\" y=\"" +
                 getY() +
               "\" color=\"" +
                 color.getRGB() +
               "\" width=\"" +
                 getWidth() +
               "\" height=\"" +
                 getHeight() +
               "\" cx=\"" +
                 getCenter2D().getX() +
               "\" cy=\"" +
                 getCenter2D().getY() +
               "\" minwidth=\"" +
                 minimumwidth +
               "\" minheight=\"" +
                 minimumheight +
               "\"";
        if (mcomponent != null) {
            result = result + " mcomponent=\"" + mcomponent.getID() + "\"";
        }
        if (!enabled) {
            result = result + " enabled=\"false\"";
        }
        if (!visible) {
            result = result + " visible=\"false\"";
        }
        if (!allowfocus) {
            result = result + " allowfocus=\"false\"";
        }
        return result;
    } 

    /**
     * Default: returns the complete end tag using the full class name as tag.
     */
    protected String getEnd() {
        return "</" + getClass().getName() + ">";
    } 
 
/*----- readXML support -----*/

    /**
     * This method is as generic as possible for VComponents and subclasses.
     * By default, the XMLTokenizer is set to skipping doctype, processing instructions and comments
     * and to parsing complete STags.
     * The default operation uses the following protected methods:
     * <ul>
     *      <li> readStart()
     *      <li> readAttributes()
     *      <li> readContent()
     *      <li> readEnd()
     * </ul>
     * The method readStart() only checks whether the start tag is the correct class name of this VComponent.
     * <br>
     * The method readAttributes() reads the attributes that this component should be able to recognize, using 
     * the HashMap attributes from XMLTokenizer. The start tag and attributes are NOT removed from the tokenizer, 
     * to make it possible for subclasses to read their own attributes as well.
     * <br>
     * The method readContent() should read all content between the start and end tags. The default implementation
     * skips all content until the corresponding end tag, taking care to skip nested pairs of begin and end tags
     * with the same tag name as this VComponent.
     * <br>
     * The method readEnd() reads and removes the end tag from the tokenizer, checking whether the end tag 
     * is the correct class name of this VComponent.
     * <br>
     * A very important requirement is that a VComponent should already be connected to an MComponent before reading XML.
     */ 
   public void readXML(XMLTokenizer tokenizer) throws IOException {
        boolean b;
        b = tokenizer.setSkipPI(true);
        b = tokenizer.setSkipComment(true);
        b = tokenizer.setCompleteSTags(true);
        b = tokenizer.setSkipDoctype(true);
        readStart(tokenizer);
        readAttributes(tokenizer);
        tokenizer.takeSTag();
        readContent(tokenizer);
        readEnd(tokenizer);
    }

    /**
     * Default: test whether the start tag is the full class name of this MComponent. Does not change the
     * state of the tokenizer.
     */
    protected void readStart(XMLTokenizer tokenizer) throws IOException {
        //only test for right tagname;
        if (!tokenizer.atSTag(getClass().getName())) {
            logger.severe("At wrong tag when trying to read " + getClass().getName());
            throw new IllegalStateException("At wrong tag when trying to read " + getClass().getName());
        }
    }

    /**
     * Default: Read and process the following attributes, if they are present:
     * color, x, y, w, h, cx, cy, minimumwidth, minimumheight, 
     * enabled, visible, hasfocus, mcomponent.getID().
     * Does not change the state of the tokenizer. allowfocus
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;

        if (attributes.containsKey("color")) {
            try {
                color = Color.decode((String)attributes.get("color"));
            } catch (NumberFormatException e) {
                logger.severe("colors in XML should be 24-bit integers. Wrong value: " + attributes.get("color"));
            }
        }
        if (attributes.containsKey("x")) {
            try {
                x = Double.parseDouble((String)attributes.get("x"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("x"));
            }
        }
        if (attributes.containsKey("y")) {
            try {
                y = Double.parseDouble((String)attributes.get("y"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("y"));
            }
        }
        if (attributes.containsKey("width")) {
            try {
                width = Double.parseDouble((String)attributes.get("width"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("width"));
            }
        }
        if (attributes.containsKey("height")) {
            try {
                height = Double.parseDouble((String)attributes.get("height"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("height"));
            }
        }
        if (attributes.containsKey("minwidth")) {
            try {
                minimumwidth = Double.parseDouble((String)attributes.get("minwidth"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("minwidth"));
            }
        }
        if (attributes.containsKey("minheight")) {
            try {
                minimumheight = Double.parseDouble((String)attributes.get("minheight"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("minheight"));
            }
        }
        if (attributes.containsKey("cx")) {
            try {
                centerx = Double.parseDouble((String)attributes.get("cx"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("cx"));
            }
        }
        if (attributes.containsKey("cy")) {
            try {
                centery = Double.parseDouble((String)attributes.get("cy"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("cy"));
            }
        }
        if (attributes.containsKey("enabled")) {
            boolean en = ((String)attributes.get("enabled")).equals("true");
            setEnabled(en);
        }
        if (attributes.containsKey("visible")) {
            boolean v = ((String)attributes.get("visible")).equals("true");
            setVisible(v);
        }
        if (attributes.containsKey("allowfocus")) {
            boolean v = ((String)attributes.get("allowfocus")).equals("true");
            setVisible(v);
        }
    }

    /**
     * Default: skip all content untill end tag, processing nested tags correctly.
     */
    protected void readContent(XMLTokenizer tokenizer) throws IOException {
        //skip content until (nested correctly) close tag
        int nestCount = 1;
        while (true) {//zolang altijd
            if (tokenizer.atSTag(getClass().getName())) {
                nestCount++;
            } else if (tokenizer.atETag(getClass().getName())) {
                nestCount--;
            }
            if (nestCount == 0) {
                break;
            }
            tokenizer.nextToken();
        }
    }

    /**
     * Default: test whether the end tag is the full class name of this VComponent, then removes the end tag.
     */
    protected void readEnd(XMLTokenizer tokenizer) throws IOException {
        tokenizer.takeETag(getClass().getName());
    }

}