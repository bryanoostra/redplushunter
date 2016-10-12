/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */
 

package parlevink.parlegraph.control;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import parlevink.parlegraph.utils.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

/**
 * This abstract implementation of CGraphController takes care of keeping track
 * of visual components and their events.
 * <p> Subclasses of this abstract class will add actual controller behaviour 
 * (e.g. interactive editing of graph structure).
 * <p>
 * Possibly, some actions are defined at this level such as save or load.@@@ (if so,
 * will somebody please document them???)
 */
public abstract class CAbstractGraphController implements 
                                                CGraphController,
                                                MComponentListener, 
                                                MouseDelegate, 
                                                KeyDelegate {

//@@@@@@@@@@@@@@@
// * Zodra de viewer die door dit ding geedit wordt een NIEUWE mcomponent toegewezen krijgt, doet
// * de editor het niet meer. Dus: dan moet je over nieuw setVGraph() aanroepen voor de viewer..
// * maar er was toch al twijfel of we het vervangen van de mcomponent wel willen toestaan,
// * dus ik ga niks doen om dat probleem te fixen.

/**********************
 * Attributes section *
 **********************/

    /** 
     * The VGraph that is being edited by this controller
     */
    protected VGraph theVGraph; 
    
    /**
     * This attribute contains a list of all MGraphs and sub MGraphs in the VGraph
     * to which this controller listens to keep informed of structural changes.
     * This is needed to avoid registering twice at the same MGraph. (of moet je MC.containslistener maken?)
     */
    protected ArrayList knownMGraphs;

/**************************
 * Initialization section *
 * (constructors, setting *
 * new VGraphs, etc)      *
 **************************/

    /**
     * Default constructor.
     */
    public CAbstractGraphController() {
        theVGraph = null;
        knownMGraphs = new ArrayList();
    }

    /**
     * Initialises the CGraphEditController with a VGraph after constructing it.
     */
    public CAbstractGraphController(VGraph newVGraph) {
        this();
        setVGraph(newVGraph);
    }
    
    /**
     * Initialises the CGraphEditController with a new VGraph. If the controller was 
     * connected to another VGraph all connections to that old VGraph and its MComponents 
     * are broken.
     */
    public void setVGraph(VGraph newVGraph) {
        unregister(); 
        theVGraph = newVGraph;
        register();
    }

    /**
     */
    public VGraph getVGraph() {
        return theVGraph;
    }

    /**
     * Breaks all connections to the current VGraph and its MComponents,
     * essentially disabling all editing capacities.
     * Only the link to the VGraph itself is preserved, making it possible
     * to re-enable editing by calling register().
     */
    protected void unregister() {
        //only if theVGraph != null
        if (theVGraph == null)
            return;
        //for all VComponents: set all eventDelegates which point to this controller to null
        disconnectEventDelegateRecursive(theVGraph);
        //for all known MGraphs: remove this controller as mcomponentListener
        Iterator kmgI = knownMGraphs.iterator();
        while (kmgI.hasNext()) 
            ((MGraph)kmgI.next()).removeMComponentListener(this);        
        knownMGraphs = new ArrayList();
    }
    
    /**
     * Connects to the current VGraph and its MComponents,
     * enabling editing capacities. 
     * All relevant eventDelegate properties are set to this controller;
     * mcomponentListener connections are made to all (sub) MGraphs
     * to keep track of structural changes.
     */
    protected void register() {
        //Only if theVGraph != null
        if (theVGraph == null)
            return;
        //for all VComponents: set all relevant eventDelegates to this controller 
        connectEventDelegateRecursive(theVGraph);
        //for all MGraphs within the VGraph: add this controller as mcomponentListener
        connectMGraphsRecursive((MGraph)theVGraph.getMComponent());
    }
    
    
/*****************************
 * Event connecting section  *
 *****************************/
 
    /**
     * Disconnects all eventDelegate connections between this controller and 
     * the given VComponent.
     */
    protected void disconnectEventDelegate(VComponent vc) {
        if (vc == null) 
            return;
        for (int eventNr = GraphMouseEvent.MOUSE_FIRST; eventNr <= GraphMouseEvent.MOUSE_LAST; eventNr++)
            if (vc.getMouseEventDelegate(eventNr) == this) 
                vc.setMouseEventDelegate(null, eventNr);
        for (int eventNr = GraphKeyEvent.KEY_FIRST; eventNr <= GraphKeyEvent.KEY_LAST; eventNr++)
            if (vc.getKeyEventDelegate(eventNr) == this) 
                vc.setKeyEventDelegate(null, eventNr);
    }
 
    /**
     * Disconnects all eventDelegate connections between this controller and 
     * the given VComponent and all of its children.
     */
    protected void disconnectEventDelegateRecursive(VComponent vc) {
        if (vc == null) 
            return;
        Iterator childrenI = vc.getChildren();
        while (childrenI.hasNext()) 
            disconnectEventDelegateRecursive((VComponent)childrenI.next());
        disconnectEventDelegate(vc);
    }

    /**
     * Sets all relevent eventDelegate connections between this controller and 
     * the given VComponent.
     * The actual effect depends on the run-time type of vc.
     * default: connect for all events to all vc's, as long as that delegate is null. 
     * (so, when a delegate is already taken....)(example: will not set delegate for
     * the textbox in a labeled edge, where that delegate is taken by the edge itself.)
     */
    protected void connectEventDelegate(VComponent vc) {
        if (vc == null) 
            return;
        for (int eventNr = GraphMouseEvent.MOUSE_FIRST; eventNr <= GraphMouseEvent.MOUSE_LAST; eventNr++)
            if (vc.getMouseEventDelegate(eventNr) == null) 
                vc.setMouseEventDelegate(this, eventNr);
        for (int eventNr = GraphKeyEvent.KEY_FIRST; eventNr <= GraphKeyEvent.KEY_LAST; eventNr++)
            if (vc.getKeyEventDelegate(eventNr) == null) 
                vc.setKeyEventDelegate(this, eventNr);
    }
 
    /**
     * Sets all relevant eventDelegate connections between this controller and 
     * the given VComponent and all of its children.
     */
    protected void connectEventDelegateRecursive(VComponent vc) {
        if (vc == null)
            return;
        Iterator childrenI = vc.getChildren();
        while (childrenI.hasNext()) 
            connectEventDelegateRecursive((VComponent)childrenI.next());
        connectEventDelegate(vc);
    }

    /**
     * Sets this controller as MComponentListener to 
     * the given MGraph and all of sub MGraphs.
     */
    protected void connectMGraphsRecursive(MGraph mg) {
        if (mg == null)
            return;
        mg.addMComponentListener(this);
        knownMGraphs.add(mg);
        Iterator verticesI = mg.getMVertices();
        while (verticesI.hasNext()) {
            MComponent nextMC = (MComponent)verticesI.next();
            if (nextMC instanceof MGraph)
                connectMGraphsRecursive((MGraph)nextMC);
        }
    }
    
    /**
     * Will be called when one of the (sub)graphs had a structural change.
     * <p>
     * Makes sure that all control connections are still aligned with structure of
     * model and view.
     * <p>
     * simple implementation: unregister, reregister.
     * So then all connections are consistent again.
     * @@@VERRRRRRY expensive though. Should be done far more simple?
     */
    public void mcomponentChanged(GraphEvent e) {
        unregister();
        register();
    }        

/**************************
 * Event routing section  *
 **************************/

    /**
     * default bounce event
     */
    public void mouseClicked(VComponent ref, GraphMouseEvent e) {
        bounceEvent(ref,e);
    }

    /**
     * default bounce event
     */
    public void mouseReleased(VComponent ref, GraphMouseEvent e) {
        bounceEvent(ref,e);
    }

    /**
     * default bounce event
     */
    public void mouseDragged(VComponent ref, GraphMouseEvent e) {
        bounceEvent(ref, e);
    }

    /**
     * default bounce event
     */
    public void mouseEntered(VComponent ref, GraphMouseEvent e) {
        bounceEvent(ref, e);
    }

    /**
     * default bounce event
     */
    public void mouseExited(VComponent ref, GraphMouseEvent e) {
        bounceEvent(ref, e);
    }

    /**
     * default bounce event
     */
    public void mousePressed(VComponent ref, GraphMouseEvent e) {
        bounceEvent(ref, e);
    }

    /**
     * default bounce event
     */
    public void mouseMoved(VComponent ref, GraphMouseEvent e) {
        bounceEvent(ref, e);
    }
    
    /**
     * default bounce event
     */
    public void keyPressed(VComponent ref, GraphKeyEvent e) {
        bounceEvent(ref, e);
    }

    /**
     * default bounce event
     */
    public void keyReleased(VComponent ref, GraphKeyEvent e) {
        bounceEvent(ref, e);
    }

    /**
     * default bounce event
     */
    public void keyTyped(VComponent ref, GraphKeyEvent e) {
        bounceEvent(ref, e);
    }
    
    /**
     * bounce for mouse
     * unregister this controller as delegate at ref, bounce event to that VComponent, reregister this controller
     * as event delegate for that event.
     * <br>
     * This method is used to give unwanted or unprocessed events back to the VComponent
     * that sent them to this controller.
     */
    protected void bounceEvent(VComponent ref, GraphMouseEvent ge) {
        
        if (ref == null)
            return;

        int ID = ge.getID();
            
        ref.setMouseEventDelegate(null, ID);
        switch (ID) {
            case GraphMouseEvent.MOUSE_CLICKED:
                ref.mouseClicked(ge);
                break;
            case GraphMouseEvent.MOUSE_ENTERED:
                ref.mouseEntered(ge);
                break;
            case GraphMouseEvent.MOUSE_EXITED:
                ref.mouseExited(ge);
                break;
            case GraphMouseEvent.MOUSE_PRESSED:
                ref.mousePressed(ge);
                break;
            case GraphMouseEvent.MOUSE_RELEASED:
                ref.mouseReleased(ge);
                break;
            case GraphMouseEvent.MOUSE_DRAGGED:
                ref.mouseDragged(ge);
                break;
            case GraphMouseEvent.MOUSE_MOVED:
                ref.mouseMoved(ge);
                break;
        }
        ref.setMouseEventDelegate(this, ID);
        
    }

    /**
     * bounce for key
     * unregister this controller as delegate at ref, bounce event to that VComponent, reregister this controller
     * as event delegate for that event.
     * Not good? extremely ineffective, especially since mousemove etc is also recorded / processed
     * Find better solution here!
     * <br>
     * This method is used to give unwanted or unprocessed events back to the VComponent
     * that sent them to this controller.
     */
    protected void bounceEvent(VComponent ref, GraphKeyEvent ge) {
        if (ref == null)
            return;

        int ID = ge.getID();
            
        ref.setKeyEventDelegate(null, ID);
        switch (ID) {
            case GraphKeyEvent.KEY_PRESSED:
                ref.keyPressed(ge);
                break;
            case GraphKeyEvent.KEY_RELEASED:
                ref.keyReleased(ge);
                break;
            case GraphKeyEvent.KEY_TYPED:
                ref.keyTyped(ge);
                break;
        }
        ref.setKeyEventDelegate(this, ID);
        
    }

    /** 
     * default: empty (yet)
     */
    public ActionMapMap getGlobalActionMap() {
        ActionMapMap result = new ActionMapMap();
        //default: empty
        return result;
    }
    
}
