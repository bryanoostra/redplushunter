/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VVertexAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2003/07/16 16:48:38  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/06/24 10:43:26  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/06/23 09:46:11  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/01/03 16:04:13  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.2  2002/09/16 15:16:24  dennisr
// documentatie
//
// Revision 1.1  2002/09/16 14:08:52  dennisr
// first add
//
// Revision 1.6  2002/06/04 12:54:22  reidsma
// minor fixes
//
// Revision 1.5  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.4  2002/01/29 14:58:07  reidsma
// debugging
//
// Revision 1.3  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.2  2002/01/22 15:00:47  reidsma
// better connector positions for rectangular vertices
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * VVertexAdapter is the default implementation for the VVertex interface.
 */

public class VVertexAdapter extends ResizableVComponentAdapter implements VVertex
{

    public VVertexAdapter() {
        super();
    }

    /**
     *@deprecated
     */
    public VVertexAdapter(double x, double y , double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }

    /**
     * Initializes simple variables for this VComponent, such as x, y, etc
     * (Not the objects which are dependent on these variables!)
     * In this case: different width/height
     */
    public void preInit() {
        super.preInit();
        width = 50;
        height = 20;
    }
 
    /**
     * see also: VVertex.connectorPositionFor(Point2D p)
     * Default: try to find a point on the bounding box.
     */
    public Point2D connectorPositionFor(Point2D p) {
        if (p == null) p = new Point2D.Double();
        Point2D resultP = new Point2D.Double();
        Rectangle2D rv = new Rectangle2D.Double(x, y, width, height);
        int oc = rv.outcode(p);
        double resx = x;
        double resy = y;
        if ((oc & Rectangle2D.OUT_RIGHT) != 0) {
            resx = x + width;
        }
        if ((oc & Rectangle2D.OUT_BOTTOM) != 0) {
            resy = y + height;
        }
        resultP.setLocation(resx, resy);
        return resultP;
    }

/*++++++++++++++++++++++++++++++++++++*
 * Graph section.                     *
 * These methods support the          *
 * connection to the MVertex that     *
 * is being visualised.               *
 *++++++++++++++++++++++++++++++++++++*/

    /**
     * This method also updates all edge viewers for edges having the MVertex as one of the end points...
     */
    public void setMComponent(MComponent newObject){
        super.setMComponent(newObject);
        if (mcomponent != null) {
            MVertex v = (MVertex)mcomponent;
            Iterator it = v.getIncidentMEdges();
            while (it.hasNext()) {
                MEdge me = (MEdge)it.next();
                me.flagDirty(true);
                me.notifyMComponentListeners();
            }
        }
        recalculateShape();
    }

}