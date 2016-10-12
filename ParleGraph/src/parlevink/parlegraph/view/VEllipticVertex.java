/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:17 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VEllipticVertex.java,v $
// Revision 1.1  2006/05/24 09:00:17  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2003/07/16 16:47:47  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.3  2002/11/08 15:05:03  dennisr
// varia
//
// Revision 1.2  2002/11/05 12:22:33  dennisr
// more generic in border and connection points
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.4  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.3  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.2  2002/01/15 15:06:57  reidsma
// no message
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;

/**
 * EllipticVertex is the most primitive non-abstract implementation of a VVertex. 
 * It visualises a vertex using an elliptic Arc2D.
 */
public class VEllipticVertex extends VVertexAdapter
{
    protected RectangularShape border;
    

    public VEllipticVertex() {
        super();
    }

    /**
     */
    public VEllipticVertex(double x, double y, double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }

    public void init() {
        super.init();
        border = createDefaultShape();
    }
    
    public RectangularShape createDefaultShape() {
        return new Arc2D.Double(x,y,width,height, 0.0d, 360.0, Arc2D.OPEN);
    }
    
    /**
     * Returns a point on the elliptic outline of this VVertex.
     * still to do: in simpele gevallen (rectangle2D, ellipse2D), gewoon snel berekenen met hulpmethod.
     */
    public Point2D connectorPositionFor(Point2D p) {
        if (border instanceof Arc2D) {
            return ellipticConnectorPositionFor(p);
        }
        //make sure p is not null
        if (p == null) p = new Point2D.Double();
        //closestP is the point on the border 'closest' to p
        Point2D closestP = null; //possibly make 'slightly global', because it is created too many times...
        //nextP is the next point on the border to be considered for closestP
        Point2D nextP = new Point2D.Double(); //possibly make 'slightly global', because it is created too many times...
        //iterate through the border as a series of connected lines
        PathIterator pi = border.getPathIterator(null, 0.5); //possibly make 'slightly global', because it is created too many times...
        double cds[] = new double[6]; //possibly make 'slightly global', because it is created too many times...
        double previousCds[] = new double[6]; //possibly make 'slightly global', because it is created too many times...
        boolean isFirst = true;
        while (!pi.isDone()) {
            pi.currentSegment(cds);
            if (isFirst)  {
                closestP = new Point2D.Double(cds[0],cds[1]);
                isFirst = false;
            } else {
                nextP.setLocation((cds[0] + previousCds[0]) / 2,(cds[1] + previousCds[1]) / 2); //check for next is not too detailed, to spare calculations
                if (p.distance(nextP) < p.distance(closestP)) {
                    closestP.setLocation(nextP);
                    //@@@possibly improve to actually calculate best point on this line... takes time! better do not :o)
                }
            }
            previousCds[0] = cds[0]; //no array copy but element copy! otherwise they're pointer equal and will therefore share their values
            previousCds[1] = cds[1];
            pi.next();
        }
        if (closestP == null) {
            closestP = new Point2D.Double(centerx, centerx);
        }
        return closestP;
    }
    
    /**
     * Returns a point on the outline of this VVertex if it is an ellipse
     */
    private Point2D ellipticConnectorPositionFor(Point2D p) {
        //make sure p is not null
        if (p == null) p = new Point2D.Double();
        //closestP is the point on the border 'closest' to p
        Point2D closestP = null; //possibly make 'slightly global', because it is created too many times...
        //nextP is the next point on the border to be considered for closestP
        Point2D nextP = new Point2D.Double(); //possibly make 'slightly global', because it is created too many times...
        //iterate through the border as a series of connected lines
        PathIterator pi = border.getPathIterator(null, 0.5); //possibly make 'slightly global', because it is created too many times...
        double cds[] = new double[6]; //possibly make 'slightly global', because it is created too many times...
        double previousCds[] = new double[6]; //possibly make 'slightly global', because it is created too many times...
        boolean isFirst = true;
        while (!pi.isDone()) {
            pi.currentSegment(cds);
            if (isFirst)  {
                closestP = new Point2D.Double(cds[0],cds[1]);
                isFirst = false;
            } else {
                nextP.setLocation((cds[0] + previousCds[0]) / 2,(cds[1] + previousCds[1]) / 2); //check for next is not too detailed, to spare calculations
                if (p.distance(nextP) < p.distance(closestP)) {
                    closestP.setLocation(nextP);
                    //@@@possibly improve to actually calculate best point on this line... takes time!
                }
            }
            previousCds[0] = cds[0]; //no array copy but element copy! otherwise they're pointer equal and will therefore share their values
            previousCds[1] = cds[1];
            pi.next();
        }
        if (closestP == null) {
            closestP = new Point2D.Double(centerx, centerx);
        }
        return closestP;
    }
   
    /**
     * (Re-) calculates the elliptic shape that will be drawn by paintVComponent/PaintBackground.    
     */ 
    public void recalculateShape() {
        super.recalculateShape();
        border.setFrame(x, y, width, height);
    }     
    
    /**
     * Paints the background of the VEllipticVertex, in the form of an eliptic border.
     */
    public void paintBackground2D(Graphics2D g2) {
        super.paintBackground2D(g2);
        g2.draw(border);
    }
    
    /**
     * Determines whether point p is inside this VComponent or not.
     */
    public boolean contains(Point2D p) {
        if (   ((getResizeMode() & RM_ALLOW_RESIZE) > 0 )
            && ((getResizeMode() & RM_RESIZING) > 0 )) {
            return super.contains(p);
        } else {
            return border.contains(p);   
        }
    }
}