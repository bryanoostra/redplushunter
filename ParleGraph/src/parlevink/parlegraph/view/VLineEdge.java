/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VLineEdge.java,v $
// Revision 1.1  2006/05/24 09:00:20  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2003/07/16 16:48:04  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/04/22 11:17:48  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/01/23 16:16:45  dennisr
// probleem met pijltjes gefixt
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
// Revision 1.19  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.18  2002/03/12 12:16:35  reidsma
// all events on arrowheads are delegated to edge
//
// Revision 1.17  2002/03/04 12:16:33  reidsma
// Delegates:
// naast de mousedelegates zijn er nu ook keydelegates. Hiervoor zijn een paar functies hernoemd (getMouseEventdelegate ipv geteventdelegate, etc)
// en een paar toegevoegd. De defaultController krijgt nu alle keyevents te horen.
//
// Revision 1.16  2002/03/01 13:21:34  reidsma
// setTargetCoord & setSourceCoord added. Calling these methods causes the line to be drawn between those locations, regardless of coordinates of endpoints. Any change in endpoints resets those coordinates.
// Any arrowheads are moved along with the edge.
//
// Revision 1.15  2002/02/19 15:54:07  reidsma
// null source/target problem fixed
//
// Revision 1.14  2002/02/18 15:36:25  reidsma
// bugfix: now also edges with one or more null endpoints can be viewed
//
// Revision 1.13  2002/02/12 09:36:17  reidsma
// sourceCoord & targetCoord are now always non-null;
// isRightMouse and co are now static
//
// Revision 1.12  2002/02/05 14:35:58  reidsma
// layouting
//
// Revision 1.11  2002/02/01 13:03:50  reidsma
// edit controller; general maintenance
//
// Revision 1.10  2002/01/29 14:58:07  reidsma
// debugging
//
// Revision 1.9  2002/01/28 14:00:45  reidsma
// Enumerations en Vectors vervangen door Iterator en ArrayList
//
// Revision 1.8  2002/01/28 10:36:01  reidsma
// no message
//
// Revision 1.7  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.6  2002/01/25 09:43:11  reidsma
// beginpunten van lijnen kunnen nu ook verplaatst
//
// Revision 1.5  2002/01/22 15:00:47  reidsma
// better connector positions for rectangular vertices
//
// Revision 1.4  2002/01/22 12:29:48  reidsma
// XML; multi-line textlabels; arrowheads; moving endpoints (first test)
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * The most primitive non-abstract edge viewer implementation. 
 * Just a straight line between two points, possibly ending in an arrow.
 * The endpoints of the edge are represented using VArrowHeads. If the MEdge of this VEdge is directed
 * the target VArrowHead is shown as an arrow.
 * This class also implements behaviour to change which MVertex objects are the end points
 * of the MEdge for this viewer. This is done by setting this VEdge as event delegate for the VArrowHeads,
 * catching all dragging and dropping events at the end points of the VEdge.
 * Those events are processed to modify the model graph edges: whenever a end point is dragged onto
 * a VVertex, the MVertex of that VVertex is set as the new end point for the MEdge of this VEdge.
 */
public class VLineEdge extends VEdgeAdapter {
    
    protected VArrowHead targetArrowHead;
    protected VArrowHead sourceArrowHead;
    static protected double PICKLIMIT = 25;
    
    public void init() {
        super.init();
        targetArrowHead = new VArrowHead(50,50,50,50);
        for (int eventNr = GraphMouseEvent.MOUSE_FIRST; eventNr <= GraphMouseEvent.MOUSE_LAST; eventNr++)
            targetArrowHead.setMouseEventDelegate(this, eventNr);
        for (int eventNr = GraphKeyEvent.KEY_FIRST; eventNr <= GraphKeyEvent.KEY_LAST; eventNr++)
            targetArrowHead.setKeyEventDelegate(this, eventNr);
        addVComponent(targetArrowHead);
        sourceArrowHead = new VArrowHead(50,50,50,50);
        for (int eventNr = GraphMouseEvent.MOUSE_FIRST; eventNr <= GraphMouseEvent.MOUSE_LAST; eventNr++)
            sourceArrowHead.setMouseEventDelegate(this, eventNr);
        for (int eventNr = GraphKeyEvent.KEY_FIRST; eventNr <= GraphKeyEvent.KEY_LAST; eventNr++)
            sourceArrowHead.setKeyEventDelegate(this, eventNr);
        addVComponent(sourceArrowHead);
    }
    
    public VArrowHead getSourceArrowHead() {
        return sourceArrowHead;
    }

    public VArrowHead getTargetArrowHead() {
        return targetArrowHead;
    }

    public void setSourceCoord(Point2D p) {
        super.setSourceCoord(p);
        placeArrowHeads();
    }

    //copies values of coords & updates arrows
    public void setTargetCoord(Point2D p) {
        super.setTargetCoord(p);
        placeArrowHeads();
    }

    /**
     * Paints the edge as a line....
     */
    public void paintBackground2D(Graphics2D g2){
        super.paintBackground2D(g2);
        if (source == target) { //draw loop
        } else {
            g2.drawLine((int)sourceCoord.getX(),(int)sourceCoord.getY(),(int)targetCoord.getX(),(int)targetCoord.getY());
        }
    }

    /**
     */
    public void paintFocusDecoration(Graphics2D g2) {
        //@@@to be changed...
        super.paintFocusDecoration (g2);
/*        Shape s1 = new Rectangle2D.Double(getSourceCoord().getX(), getSourceCoord().getY(), 5, 5);
        Shape s2 = new Rectangle2D.Double(getTargetCoord().getX() - 5, getTargetCoord().getY(), 5, 5);
        g2.setColor(Color.black);
        g2.fill(s1);
        g2.fill(s2);
        g2.setColor(color);*/
    }
    /**
     * places the arrowheads on the right location & direction
     */
    protected void placeArrowHeads() {
        //check location & direction of arrowheads
        if (sourceCoord == targetCoord) { //draw loop arrow
            //pijltje bovenop?@@@
        } else {
            targetArrowHead.setVArrowHead(targetCoord, getTargetArrowAttachmentPoint());
            sourceArrowHead.setVArrowHead(sourceCoord, getSourceArrowAttachmentPoint());
        }
    }
    
    protected Point2D getSourceArrowAttachmentPoint() {
        return targetCoord;
    }

    protected Point2D getTargetArrowAttachmentPoint() {
        return sourceCoord;
    }
    
    
    public void mcomponentChanged(GraphEvent ge) {
        super.mcomponentChanged(ge);
        if (mcomponent != null)
            targetArrowHead.setVisible(((MEdge)mcomponent).isDirected());
        sourceArrowHead.setVisible(false);
    }        

    /**
     * Called when an end vertex moved. Moved the arrowHeads.
     */
    public void vcomponentMoved(GraphEvent ge) {
        super.vcomponentMoved(ge);
        placeArrowHeads();
        resetBounds(); //why@@@?
    }
    
   /**
    * Determines whether point p is "inside" this VEdge or not.
    */
   public boolean contains(Point2D p) {
       return Line2D.ptSegDistSq(sourceCoord.getX(), sourceCoord.getY(), targetCoord.getX(), targetCoord.getY(), p.getX(), p.getY()) < PICKLIMIT;        
   }        


}