/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VGraphLayout.java,v $
// Revision 1.1  2006/05/24 09:00:20  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:39  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2003/07/15 21:01:57  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/03/03 17:56:07  dennisr
// *** empty log message ***
//
// Revision 1.2  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.1  2002/09/16 14:08:51  dennisr
// first add
//
// Revision 1.2  2002/02/05 14:35:58  reidsma
// layouting
//
// Revision 1.1  2002/02/04 17:17:32  reidsma
// layout class added
//

package parlevink.parlegraph.layout;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import parlevink.xml.*;

import java.util.logging.*;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;

/**
 * VGraphLayout is a class that takes care of the layout of graph visualisations. One VGraphLayout
 * can layout only ONE GRAPH (Changed since previous version)
 * @@@documenteren!!!

NB: misschien is tussen 2 calls de inhoud vd graaf veranderd. Dan is touched natuurlijk ook niet correct meer.
 
 zie layout.txt in aantekeningen
 
 */
public class VGraphLayout {
	/* logging */
	protected static Logger logger;

    protected VGraph theVGraph;

    protected Set touchedVComponents;
    protected Set fixedVComponents;

    /**
     * Creates a default VGraphLayout.
     */
    public VGraphLayout(VGraph vg) {
    	//initialize logger for this class:
	    if (logger == null) {
	    	logger = Logger.getLogger(getClass().getName());	    
	    }
	    theVGraph = vg;
	    touchedVComponents = new HashSet();
	    fixedVComponents = new HashSet();
    }

    public void setFixed (VComponent vc, boolean fix) {
        if (fix) {
            fixedVComponents.add(vc);
        } else {
            fixedVComponents.remove(vc);
        }
    }
    
    public void setTouched (VComponent vc, boolean touch) {
        if (touch) {
            touchedVComponents.add(vc);
        } else {
            touchedVComponents.remove(vc);
        }
    }

    /**
     * Do layout VGraph.
     * Default implementation is VERY simple...
     * Anyone who can think of a better way to do this is very welcome to implement it...
     */
    public void doLayout() {
        Set todo = new HashSet();
        //take all untouched children vc's that are untouched and unfixed
        //only place VVertices & VTextBoxes
	    Iterator iter = theVGraph.getChildren();
        while (iter.hasNext()) {
            Object next = iter.next();
            if (   !(fixedVComponents.contains(next) || touchedVComponents.contains(next)) 
                && ((next instanceof VVertex) || (next instanceof VTextBox)) ) {
                todo.add(next);
            }
        }

        layoutVComponents(todo);
    }
    
    //in this case only gets iterator of components that have to be layed out (so no edges or whatever)
    protected void layoutVComponents (Set todo) {
        //iterate through them and place them somewhere until all are placed
        Iterator iter = todo.iterator();
        int RIGHT  = 0;
        int BOTTOM = 1;
        //de beschikking hebbend over fixed en touched als 'ankers'
        
        //place them in along right & lower outer edges of graph, as first test version. 
        //Is good for megaviewer anyway
        //dont forget to touch all of those vc's
        double xOffs = 20;
        double xMax = theVGraph.getWidth();
        double yMax = theVGraph.getHeight();
        double yOffs = 20;
        int side = RIGHT;
        if (xMax > yMax) {
            side = BOTTOM;
        }
            
	    while (iter.hasNext()) {
            VComponent next =  (VComponent)iter.next();
            if (side == RIGHT) {
                //place vc on new location
                next.setRelativeLocation2D(theVGraph.getWidth() + 20, yOffs);
                //determine new offset for y
                yOffs = yOffs + 20 + next.getHeight();
                //if below vg: set side to BOTTOM & determine new offsets for x&y & resetbounds
                if (yOffs > yMax) {
                    side = BOTTOM;
                    xOffs = 20;
                    yOffs = 20;
                    xMax = theVGraph.getWidth();
                    yMax = theVGraph.getHeight();
                    theVGraph.resetBounds();
                }
            } else {
                //place vc on new location
                next.setRelativeLocation2D(xOffs, theVGraph.getHeight() + 20);
                //determine new offset for x
                xOffs = xOffs + 20 + next.getWidth();
                //if beside vg: set side to RIGHT & determine new offsets for x&y & resetbounds
                if (xOffs > xMax) {
                    side = RIGHT;
                    xOffs = 20;
                    yOffs = 20;
                    xMax = theVGraph.getWidth();
                    yMax = theVGraph.getHeight();
                    theVGraph.resetBounds();
                }
            }
            touchedVComponents.add(next);
	    }

        //reset size of vgraph to some bounding box that is bounds to ALL components
        theVGraph.resetBoundsRecursive();
        
	    //the minimumsize of a graph should be set in the layout method
	    theVGraph.setMinimumSize(theVGraph.getWidth(), theVGraph.getHeight());
    }
    
    /**
     */
    public void fullLayout() {
        //default version is extremely simple: refer to doLayout, untouching all
        touchedVComponents.clear();
        if (fixedVComponents.size() == 0 )
            theVGraph.setBounds2D(0.0,0.0,0.0,0.0);
        doLayout();
    }
    
}