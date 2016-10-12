/* @author Job Zwiers  
 * @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:17 $    
 * @since version 0       
 */
 
// Last modification by: $Author: swartjes $
// $Log: VArrowHead.java,v $
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
// Revision 1.6  2003/07/16 16:47:31  dennisr
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
// Revision 1.2  2002/09/23 07:44:44  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.8  2002/05/16 10:00:54  reidsma
// major update (see changes.txt)
//
// Revision 1.7  2002/02/11 09:18:21  reidsma
// no message
//
// Revision 1.6  2002/02/01 13:22:43  reidsma
// :o)
//
// Revision 1.5  2002/01/25 15:23:09  reidsma
// Redocumentation
//
// Revision 1.4  2002/01/25 09:43:11  reidsma
// beginpunten van lijnen kunnen nu ook verplaatst
//
// Revision 1.3  2002/01/22 12:29:49  reidsma
// XML; multi-line textlabels; arrowheads; moving endpoints (first test)
//

package parlevink.parlegraph.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * A VArrowHead can used to draw the end points of an VEdge.
 * <br>
 * Depending on the boolean property paintArrow it will be invisible or it will 
 * draw an arrow head in the specified direction.
 * When a VArrowHead is created by an VEdgeAdapter, the VEdge will set itself as the 
 * eventDelegate for the MouseDragged, MouseReleased and MouseClicked events. Those 
 * events will be used by the VEdge to modify the end vertices of the MEdge in the 
 * graph model.
 *@@@@@ de construction moet nog beter.
 */
public class VArrowHead extends VShapedNonGraphElement {
		
/************************
 * attributes section   *	
 ************************/

	//private Polygon polygon;
	private static final double BASEDISTANCE=10.0; // 10.0 
	private static final double BASESCALE = 0.5; // 0.5

/************************
 * Constructors section *	
 ************************/
	
	/**
	 * Creates a new VArrowHead with a default size & headpoint.
	 */
	public VArrowHead() {
	    this(0,0,10,10,new Point2D.Double(0,0), new Point2D.Double(10,10));
	}

	/**
	 * Creates a new VArrowHead with a given size, pointing to the upper left corner of the bounding box.
	 */
	public VArrowHead(double x, double y, double width, double height) {
	    this(x,y,width,height,new Point2D.Double(x,y), new Point2D.Double(x+width,y+height));
	}

	/**
	 * Creates a new VArrowHead with a default size and given headpoint and pointing to (directionpoint+180 degrees).
	 */
	public VArrowHead(Point2D headPoint, Point2D baseDirectionPoint) {
	    this(0, 0, 10, 10, headPoint, baseDirectionPoint);
	}

	/**
	 * Creates a new VArrowHead with a given size and headpoint and pointing to (directionpoint+180 degrees).
	 * With these parameters the corners of the filled triangle will be calculated
	 */
	public VArrowHead(double x, double y, double width, double height, Point2D headPoint, Point2D baseDirectionPoint) {
	    super();
        setBounds2D(x, y, width, height);
	    color = Color.red;
		setVArrowHead(headPoint, baseDirectionPoint);
	}

/********************
 * Painting section *
 ********************/
 
   /**
    * Sets the head of the arrow and the direction from which it is coming from.
    * This method gets called by the VEdge to change the ArrowHead when the edge moved
    */
   public void setVArrowHead(Point2D headPoint, Point2D baseDirectionPoint) {
        Polygon polygon = new Polygon(new int[3], new int[3], 3);

		double x1 = headPoint.getX();
		double y1 = headPoint.getY();
		double x2 = baseDirectionPoint.getX();
		double y2 = baseDirectionPoint.getY();
		double scalefactor = BASEDISTANCE/headPoint.distance(baseDirectionPoint);
		
		double xbase = x1 + scalefactor*(x2-x1);
		double ybase = y1 + scalefactor*(y2-y1);
		
		double dx = x1 - xbase;
		double dy = y1 - ybase;
		
        polygon.xpoints[0] = (int) x1;
		polygon.ypoints[0] = (int) y1;
		polygon.xpoints[1] = (int) (xbase - BASESCALE * dy);
		polygon.ypoints[1] = (int) (ybase + BASESCALE * dx);
		polygon.xpoints[2] = (int) (xbase + BASESCALE * dy);
		polygon.ypoints[2] = (int) (ybase - BASESCALE * dx);		
		
        setShape (polygon);
	}

}