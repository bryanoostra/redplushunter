/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:17 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VEdgeAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:17  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.2  2005/11/25 15:28:22  swartjes
// Made a couple of graphical adjustments to clarify knowledge:
// - Vertices are drawn on top of edges in stead of below
// - Edges are light gray
// - Arrows are smaller
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.5  2003/07/16 16:47:31  dennisr
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
// Revision 1.3  2002/09/30 20:16:47  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.2  2002/09/27 08:15:44  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.15  2002/06/04 12:54:22  reidsma
// minor fixes
//
// Revision 1.14  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.13  2002/03/01 13:21:34  reidsma
// setTargetCoord & setSourceCoord added. Calling these methods causes the line to be drawn between those locations, regardless of coordinates of endpoints. Any change in endpoints resets those coordinates.
// Any arrowheads are moved along with the edge.
//
// Revision 1.12  2002/02/25 08:55:36  reidsma
// It is not allowed to add MCOmponents to an MGraph when they are already element in another MGraph. Since this version, trying to do this results in an exception.
// VLineEdge/VLineTextEdge/VEdgeAdapter: de sourceCoord en targetCoord zijn verhuisd naar de VEdgeAdapter; sourceCoord en targetCoord zijn een read-only property geworden (altijd geinitialiseerd).
//
// Revision 1.11  2002/02/19 12:59:00  reidsma
// no message
//
// Revision 1.10  2002/02/18 15:36:20  reidsma
// bugfix: now also edges with one or more null endpoints can be viewed
//
// Revision 1.9  2002/02/12 09:36:17  reidsma
// sourceCoord & targetCoord are now always non-null;
// isRightMouse and co are now static
//
// Revision 1.8  2002/02/05 14:35:58  reidsma
// layouting
//
// Revision 1.7  2002/02/04 16:19:35  reidsma
// debugging
//
// Revision 1.6  2002/01/29 14:58:07  reidsma
// debugging
//
// Revision 1.5  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.4  2002/01/22 12:29:49  reidsma
// XML; multi-line textlabels; arrowheads; moving endpoints (first test)
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.io.*;
import parlevink.xml.*;

/**
 * VEdgeAdapter is the default implementation of the VEdge interface. It is an abstract class.
 * A VEdgeAdapter is NOT allowed to resize or move in reaction to mouse events.... 
 */
public abstract class VEdgeAdapter extends ResizableVComponentAdapter implements VEdge
{
    protected Point2D sourceCoord;
    protected Point2D targetCoord;
    protected VVertex source;
    protected VVertex target;

    /**
     * Initializes simple variables for this VComponent, such as x, y, etc
     * (Not the objects which are dependent on these variables!)
     * this class: different move&resize modes
     */
    public void preInit() {
        super.preInit();
	    color = Color.lightGray;
        sourceCoord = new Point2D.Double(0,0);
        targetCoord = new Point2D.Double(0,0);
    }

    protected int getDefaultMoveMode() {
        return super.getDefaultMoveMode() & ~MM_ALLOW_MOVE;
    }
    protected int getDefaultResizeMode() {
        return super.getDefaultResizeMode() & ~RM_ALLOW_RESIZE;
    }
    protected int getDefaultMoveRestrictions() {
        return MR_CONSTRAIN_CHILD;
    }
 
 
//@@@

    public Point2D getSourceCoord() {
        return sourceCoord;
    }

    public Point2D getTargetCoord() {
        return targetCoord;
    }

    public void setSourceCoord(Point2D p) {
        sourceCoord.setLocation(p);
    }

    //copies values of coords & updates arrows
    public void setTargetCoord(Point2D p) {
        targetCoord.setLocation(p);
    }


    public void recalculateShape() {
        super.recalculateShape();
    }

    /**
    **@@@deze doet in principe vooral de interne refs naar de VVertices bijwerken. 
      @@@Daarna worden de coordinaten rechtgezet, dmv vcomponentmoved

     * Recalculates the endpoints for the edge, if vertices have been replaced.
     */
    public void mcomponentChanged(GraphEvent ge) {
        if (mcomponent !=  null) { //well; should it be possible to set the MComponent to nul? No. Not guaranteed yet!
            //if changeCoordinates is true a new end VVertx was found, 
            //meaning that a new connectorposition has to be calculated
            boolean changeCoordinates = false;
            MVertex msource = ((MEdge)mcomponent).getSource();
            if (msource != null) {
                //get the viewer for the new source
                VVertex newsource = (VVertex)getTopVGraph().getViewerForMComponent(msource);
                if (newsource != source) {
                    if (source != null) {
                        source.removeVComponentMovedListener(this); //disconnect from old source
                    }
                    source = newsource;
                    changeCoordinates = true;
                    //connect to new source: this VEdge must follow the new VVertex if it moves around
                    if (source != null) {
                        source.addVComponentMovedListener(this);
                    }
                }
            }
            MVertex mtarget = ((MEdge)mcomponent).getTarget();
            if (mtarget != null) {
                //get the viewer for the new target
                VVertex newtarget = (VVertex)getTopVGraph().getViewerForMComponent(mtarget);
                if (newtarget != target) {
                    if (target != null) {
                        target.removeVComponentMovedListener(this);
                    }
                    target = newtarget;
                    changeCoordinates = true;
                    //connect to new target: this VEdge must follow the new VVertex if it moves around
                    if (target != null) {
                        target.addVComponentMovedListener(this);
    //                    logger.warning("targetlisten "+ getMComponent().getID() + target.getMComponent().getID());
                    }
                }
            }
            if (changeCoordinates) {
                vcomponentMoved(new GraphEvent(this));
            }
        }
    }
    
    /**
     * Berekent de nieuwe cordinaten voor de eindpunten en zet die ook op de relevante plaatsen. 
     */
    protected void calculateEndpoints() {
        if (source != null)
            sourceCoord.setLocation(source.connectorPositionFor(getSourceAttachmentPoint()));
        if (target != null)
            targetCoord.setLocation(target.connectorPositionFor(getTargetAttachmentPoint()));
        resetBounds();
    }
    
    /**
     * Called when an end vertex moved. Rearranges the coordinates. 
     */
    public void vcomponentMoved(GraphEvent ge) {
        calculateEndpoints(); //changes the endpoints & the boundingbox
        recalculateShape();   
    }
    
    /**
     * Returns the point that should be used to determine the atachment coordinates of the edge to the source vertex
     */
    protected Point2D getSourceAttachmentPoint() {
        if (target == null)
            return targetCoord;
        return target.getCenter2D();
    }

    /**
     * Returns the point that should be used to determine the atachment coordinates of the edge to the target vertex
     */
    protected Point2D getTargetAttachmentPoint() {
        if (source == null)
            return sourceCoord;
        return source.getCenter2D();
    }


/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

/*----- toXMLString support -----*/

    /**
     * Adds the following attributes to the attributes of the super class:
     * the coordinates of the end points (sourcex, sourcey, targetx, targety).
     */
    protected String getAttributes() {
        String result = super.getAttributes() + 
                        " sourcex=\"" +
                        sourceCoord.getX() +
                        "\" sourcey=\"" +
                        sourceCoord.getY() +
                        "\" targetx=\"" +
                        targetCoord.getX() +
                        "\" targety=\"" +
                        targetCoord.getY() +
                        "\"";
        return result;
    } 

/*----- readXML support -----*/

    /**
     * Reads the following attributes in extension to the attributes of the super class:
     * the coordinates of the end points (sourcex, sourcey, targetx, targety).
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        double sx = 0;
        double sy = 0;
        double tx = 0;
        double ty = 0;
        
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("sourcex")) {
            try {
                sx = Double.parseDouble((String)attributes.get("sourcex"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("sourcex"));
            }
        }
        if (attributes.containsKey("sourcey")) {
            try {
                sy = Double.parseDouble((String)attributes.get("sourcey"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("sourcey"));
            }
        }
        if (attributes.containsKey("targetx")) {
            try {
                tx = Double.parseDouble((String)attributes.get("targetx"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("targetx"));
            }
        }
        if (attributes.containsKey("targety")) {
            try {
                ty = Double.parseDouble((String)attributes.get("targety"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("targety"));
            }
        }
        sourceCoord.setLocation(sx, sy);
        targetCoord.setLocation(tx, ty);
    }        
    
}