/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VVertex.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2002/11/08 15:05:03  dennisr
// varia
//
// Revision 1.1  2002/09/16 14:08:52  dennisr
// first add
//
// Revision 1.2  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.1.1.1  2001/12/21 14:11:26  reidsma
// added
//

package parlevink.parlegraph.view;

import java.awt.geom.*;

/**
 * VVertex is the interface for the visual counterparts
 * of MGVertex vertices. In addition to the VComponent methods,
 * a VVertex has specialised methods for vertex-functionality.
 * <br>
 * One of the main aspects of the VVertex functionality is 
 * the ability to provide information about the coordinates where 
 * an edge viewer should connect to the VVertex. This makes it possible to 
 * let vertices have an arbitrary outline.
 */
public interface VVertex extends ResizableVComponent {
    
   /**
    * connectorPositionFor(p) provides advice about the positioning of a connector 
    * on the boundary of the VVertex, assuming an VEdge will be drawn from this VVertex 
    * towards the direction determined by point p.
    * The Point2D that is returned is guaranteed to be on the
    * boundary shape of the VVertex. The exact positioning is left unspecified
    * and might depend for instance on the positions of other connections already 
    * attached to the VVertex.
    * @param Point2D p, denoting the direction of an VEdge or VEdge segment.
    * $return Returns a Point2D on the boundary of the VVertex.
    */
   public Point2D connectorPositionFor(Point2D p);
   
}