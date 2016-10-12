/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VMultiLineEdge.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.3  2005/11/25 15:28:21  swartjes
// Made a couple of graphical adjustments to clarify knowledge:
// - Vertices are drawn on top of edges in stead of below
// - Edges are light gray
// - Arrows are smaller
//
// Revision 1.2  2005/11/11 14:57:57  swartjes
// Removed a bug with ForceLayout and made vertices non-resizable (annoying when trying to drag)
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.8  2003/07/16 16:48:04  dennisr
// *** empty log message ***
//
// Revision 1.7  2003/06/24 10:43:24  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/04/22 11:17:48  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/01/23 16:16:45  dennisr
// probleem met pijltjes gefixt
//
// Revision 1.3  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.2  2002/11/04 09:40:40  dennisr
// *** empty log message ***
//
// Revision 1.1  2002/09/16 14:08:51  dennisr
// first add
//
// Revision 1.8  2002/06/11 09:10:01  reidsma
// waypoints in multiline edge are now XMLized
// save&load button in default GUIPanel for controller
//
// Revision 1.7  2002/06/10 08:22:55  reidsma
// Uitbreiding GUI
//
// Revision 1.6  2002/05/17 09:12:27  reidsma
// maintenance
//
// Revision 1.5  2002/05/16 15:13:43  reidsma
// improved visuals
//
// Revision 1.4  2002/05/16 14:17:01  reidsma
// improved waypoint support
//
// Revision 1.3  2002/05/16 14:11:41  reidsma
// no message
//
// Revision 1.2  2002/05/16 13:58:07  reidsma
// waypoint support improved
//
// Revision 1.1  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import parlevink.xml.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * VMultiLineEdge introduces <i>waypoints</i>: route points defining the segments that
 * form the edge. 
 * <p>
 * In a visualization, these waypoints are visible VComponents that can be moved around. 
 * As soon as a waypoints moves (interactively or through a call to a method such as 
 * setLocation2D) the segments of this edge, and therefore its visual appearance, change
 * as well.
 * <p>
 * If you want to subclass this edge to use the waypoints in a different way (for example
 * as the control points for a Bezier-curve), you only need to re-implement the methods
 * <code>recalculateShape</code> (which calculates the segments based on the waypoints)
 * and <code>contains</code> (which uses the segments to decide whether a points is 'inside'
 * this edge). The administration of the waypoints themselves, and invoking recalculateShape
 * when the waypoints change, is already taken care of in this class.
 * <p>
 * If this class changes its contains(p) implementation (see for example VEllipticVertex for hints)
 * you even don't need to override contains anymore.
 */
public class VMultiLineEdge extends VLineEdge {
    
    /**
     * A list of all the waypoints in this multilineedge
     */
    protected ArrayList waypoints;
    
    /**
     * A list of all the waypoints in this multilineedge
     */
    protected GeneralPath linePath;

//    protected static boolean WAYPOINTS_ALWAYS_VISIBLE = false;
    
    static protected Shape waypointShape = new Ellipse2D.Double(0,0,7,7);
    
    public void preInit() {
        super.preInit();
        //initialize empty array of waypoints.....
        waypoints = new ArrayList();
        linePath = new GeneralPath();
    }


    public void setSourceCoord(Point2D p) {
        super.setSourceCoord(p);
        placeArrowHeads();
        resetBounds();
        recalculateShape();
    }

    //copies values of coords & updates arrows
    public void setTargetCoord(Point2D p) {
        super.setTargetCoord(p);
        placeArrowHeads();
        resetBounds();
        recalculateShape();
    }

    /**
     * Returns the point that should be used to determine the atachment coordinates of the edge to the source vertex
     */
    protected Point2D getSourceAttachmentPoint() {
        if (waypoints.size() > 0) {
            return ((VComponent)waypoints.get(0)).getCenter2D();
        }
        return super.getSourceAttachmentPoint();
    }

    /**
     * Returns the point that should be used to determine the atachment coordinates of the edge to the target vertex
     */
    protected Point2D getTargetAttachmentPoint() {
        if (waypoints.size() > 0) {
            return ((VComponent)waypoints.get(waypoints.size() - 1)).getCenter2D();
        }
        return super.getTargetAttachmentPoint();
    }

    protected Point2D getSourceArrowAttachmentPoint() {
        if (waypoints.size() > 0) {
            return ((VComponent)waypoints.get(0)).getCenter2D();
        }
        return super.getSourceArrowAttachmentPoint();
    }

    protected Point2D getTargetArrowAttachmentPoint() {
        if (waypoints.size() > 0) {
            return ((VComponent)waypoints.get(waypoints.size() - 1)).getCenter2D();
        }
        return super.getTargetArrowAttachmentPoint();
    }

    /**
     * Paints the edge as a multi line.... 
     * NB: NO CALL TO SUPER-PAINT-BEHAVIOUR (obviously)
     */
    public void paintBackground2D(Graphics2D g2){
    	
        if (source == target) {
            g2.draw(linePath); //super.paintBackground2D(g2); paint loop :(
        } else {
            g2.draw(linePath);
        }
    }

    public void setFocus(boolean b) {
        super.setFocus(b);
        /* not really useful, need to have a more generic solution here.
        for (int i = 0; i < waypoints.size(); i++) {
            VComponent wp = (VComponent)waypoints.get(i);
            if (hasFocus()) {
                //waypoints visible
                wp.setVisible (true);
            } else {
                //waypoints invisible
                wp.setVisible (WAYPOINTS_ALWAYS_VISIBLE);
            }
        }*/
    }
    
    /**
     * recalculates the shape of the path when the end points or waypoints have changed
     * Ivo Swartjes: made it synchronized, because of java.awt.geom.IllegalPathStateException
     * 		which is possibly caused because linePath.reset() is called by another thread
     * 		after linePath.moveTo, making the linePath end up as empty when the lineTo() methods
     * 		are called.
     */
    public synchronized void recalculateShape() {
        super.recalculateShape();
        if (waypoints == null)
            return;
        //does not change the vc boundaries in any way, so can be done here...
        Iterator waypIt = waypoints.iterator();
        //calculate
        linePath.reset();
        linePath.moveTo((float)sourceCoord.getX(), (float)sourceCoord.getY());
        double sumX = sourceCoord.getX();
        double sumY = sourceCoord.getY();
        VComponent nextWP = null;
        Point2D wpCenter = null;
        while (waypIt.hasNext()) {
            nextWP = (VComponent)waypIt.next();
            wpCenter = nextWP.getCenter2D();
            linePath.lineTo((float)wpCenter.getX(), (float)wpCenter.getY());
            sumX = sumX + nextWP.getX();
            sumY = sumY + nextWP.getY();
        }
        
       	linePath.lineTo((float)targetCoord.getX(), (float)targetCoord.getY());
               
        sumX = sumX + targetCoord.getX();
        sumY = sumY + targetCoord.getY();
        //zet centerX en centerY op de avg x en y
        centerx = sumX / (2 + waypoints.size());
        centery = sumY / (2 + waypoints.size());
    }

   /**
    * Determines whether point p is "inside" this VEdge or not.
    */
   public boolean contains(Point2D p) {
        //iets met waypoints
        Point2D p1 = sourceCoord;
        Point2D p2;
        Iterator waypIt = waypoints.iterator();
        //draw all the multilinesegments.....
        while (waypIt.hasNext()) {
            p2 = ((VComponent)waypIt.next()).getCenter2D();
            if (Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p.getX(), p.getY()) < PICKLIMIT) 
                return true;
            p1 = p2;
        }
        p2 = targetCoord;
        return Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p.getX(), p.getY()) < PICKLIMIT;        
   }        

/*****************************
 *  The waypoints stufff.... *
 * Registered on moving of   *
 * waypoints, because of     *
 * arrowheads.               *
 * Some functions to create  *
 * waypoints "closest to some*
 * point"                    *
 *****************************/
    
    public VShapedNonGraphElement createNewWaypoint() {
        VShapedNonGraphElement result = new VShapedNonGraphElement();
        result.setShape(waypointShape);
        return result;
    }
    /**
     * adds a waypoint... given a location and the insertionnumber
     */ 
    public void addWaypoint(int wpindex, Point2D p) {
        VShapedNonGraphElement waypoint = createNewWaypoint();
        waypoint.setMoveRestrictions(ResizableVComponent.MR_RESIZE_PARENT);
        addVComponent(waypoint);
        for (int eventNr = GraphKeyEvent.KEY_FIRST; eventNr <= GraphKeyEvent.KEY_LAST; eventNr++)
            waypoint.setKeyEventDelegate(this, eventNr);
        waypoint.translate2D (VComponent.COMPONENT_ONLY, p.getX()- waypoint.getCenter2D().getX(), p.getY() - waypoint.getCenter2D().getY());
        waypoints.add(wpindex, waypoint);
        recalculateShape();
        waypoint.addVComponentMovedListener(this);
        //waypoint.setVisible(WAYPOINTS_ALWAYS_VISIBLE);
    }
 
    /**
     * adds a waypoint... given a location. Insert index will be such that the wp will appear on the linepart closest to p
     */ 
    public void addWaypoint(Point2D p) {
        int wpindex = 0;
        //calculate index
        double minimumDistance = -1;
        double nextDistance;
        Point2D p1 = sourceCoord;
        Point2D p2;
        Iterator waypIt = waypoints.iterator();
        int nextIndex = 0;
        //check multilinesegments.....
        while (waypIt.hasNext()) {
            p2 = ((VComponent)waypIt.next()).getCenter2D();
            nextDistance = Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p.getX(), p.getY());
            if (  (minimumDistance < 0)
                ||(nextDistance < minimumDistance)) {
                minimumDistance = nextDistance;
                wpindex = nextIndex;
            }
            nextIndex++;
            p1 = p2;
        }
        p2 = targetCoord;
        nextDistance = Line2D.ptSegDistSq(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p.getX(), p.getY());
        if (  (minimumDistance < 0)
            || (nextDistance < minimumDistance)) {
            minimumDistance = nextDistance;
            wpindex = nextIndex;
        }
        addWaypoint(wpindex, p);
    }
 
    /**
     * removess a waypoint... given the waypointindex
     */ 
    public void removeWaypoint(int wpindex) {
        VShapedNonGraphElement waypoint = (VShapedNonGraphElement)waypoints.get(wpindex);
        waypoint.removeVComponentMovedListener(this);
        removeVComponent(waypoint);
        waypoints.remove(wpindex);
        vcomponentMoved(new GraphEvent(this));
        recalculateShape();
    }

    /**
     * adds a waypoint by splitting the waypoint at the given index
     */ 
    public void splitWaypoint(int wpindex) {
        VShapedNonGraphElement waypoint = (VShapedNonGraphElement)waypoints.get(wpindex);
        addWaypoint(wpindex, waypoint.getLocation2D());
    }

    public ArrayList getWaypoints() {
        return new ArrayList(waypoints);
    }

/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

    /**
     * read waypoints
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("waypoints")) {
            String wpCoordinates = (String)attributes.get("waypoints");
            StringTokenizer st = new StringTokenizer(wpCoordinates, ",");
            double x = 0;
            double y = 0;
            while (st.hasMoreTokens()) {
                try {
                    x = Double.parseDouble(st.nextToken());
                    y = Double.parseDouble(st.nextToken());
                    addWaypoint(waypoints.size(), new Point2D.Double(x, y));
                } catch (NumberFormatException e) {
                    logger.severe("Waypoint coordinates should be double values!");
                }
            }
        }
        
    }        

    /**
     * save waypoints
     */
    protected String getAttributes() {
        String wpCoordinates = "";
        if (waypoints.size() > 0) {
            VComponent wp = (VComponent)waypoints.get(0);
            wpCoordinates = wpCoordinates + wp.getX() + "," + wp.getY(); 
        }
        for (int i = 1; i < waypoints.size(); i++) {
            VComponent wp = (VComponent)waypoints.get(i);
            wpCoordinates = wpCoordinates + "," + wp.getX() + "," + wp.getY(); 
        }
        String result = super.getAttributes() + 
                        " waypoints=\"" +
                        wpCoordinates +
                        "\"";
        return result;
    } 


}