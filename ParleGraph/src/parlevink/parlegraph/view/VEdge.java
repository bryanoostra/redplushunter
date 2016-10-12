/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VEdge.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.5  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.4  2002/03/01 13:21:34  reidsma
// setTargetCoord & setSourceCoord added. Calling these methods causes the line to be drawn between those locations, regardless of coordinates of endpoints. Any change in endpoints resets those coordinates.
// Any arrowheads are moved along with the edge.
//
// Revision 1.3  2002/02/18 15:36:20  reidsma
// bugfix: now also edges with one or more null endpoints can be viewed
//
// Revision 1.2  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.1.1.1  2001/12/21 14:11:25  reidsma
// added
//

package parlevink.parlegraph.view;

import java.awt.geom.*;
/**
 * VEdge is the basic edge-visualisation class. 
 * A VEdge always has a reference to the VGraph of which it is an element.
 * <br>NB: At the moment this is only a tagging interface, but the default adapter 
 * VEdgeAdapter contains more functionality.
 */
public interface VEdge extends ResizableVComponent {

    public Point2D getSourceCoord();

    public Point2D getTargetCoord();

    //copies values of coords & updates arrows
    public void setSourceCoord(Point2D p);

    //copies values of coords & updates arrows
    public void setTargetCoord(Point2D p);

//coordinates of endpoints accessible...@@@ weak; can be cahnged but any event snaps them back....       
}