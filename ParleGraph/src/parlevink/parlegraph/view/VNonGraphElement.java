/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VNonGraphElement.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:17  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.4  2003/07/16 16:48:19  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.2  2002/09/23 07:44:44  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:52  dennisr
// first add

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;

/**
 * A VNonGraphElement is a VComponent which is not connected to any MComponent. 
 * It is a VComponent that can be viewed like any other VComponent, 
 * but the MComponent properties and methods do not work.
 * One of the uses of these elements is to make it possible to divide the functionality of a viewer
 * over several subcomponents. For this the event delegate functionality of 
 * th VComponent class is usually be used. For an example, see the implementation of the VLineEdge class 
 * (ArrowHead is a VNonGraphElement) or VLabeledVertex (the label is a VNonGraphElement).
 */
public class VNonGraphElement extends ResizableVComponentAdapter
{
    
    public VNonGraphElement() {
        super();
    }

    /**
     * Default move & resize modes: a VNongraphElement can be moved but not resized using mouse events.
     */
    public VNonGraphElement(double x, double y, double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }
    protected int getDefaultResizeMode() {
        return super.getDefaultResizeMode() & ~RM_ALLOW_RESIZE;
    }
    
    /**
     * This method is not supposed to be called on these objects, 
     * so an Exception is thrown
     */
    public void setMComponent(MComponent newObject) {
        throw new RuntimeException("VNonGraphElements cannot be connected to mcomponents");
    }
    
	/**
     * This method is not supposed to be called on these objects, 
     * so an exception is thrown
	 */
	public void mcomponentChanged(GraphEvent ge) {
        throw new RuntimeException("VNonGraphElements cannot be connected to mcomponents");
    }

}