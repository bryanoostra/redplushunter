/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MVertex.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.4  2003/06/24 10:40:28  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.2  2003/02/24 13:24:00  dennisr
// *** empty log message ***
//
// Revision 1.1  2002/09/16 14:05:42  dennisr
// first add
//
// Revision 1.4  2002/01/29 14:58:51  reidsma
// Full redocumentation
//

package parlevink.parlegraph.model;

import java.util.*;

/**
 * This interface is the base interface for all vertex classes in abstract graph models.
 * <br>
 * This interface defines a few extra methods: connectMEdge and disconnectMEdge. These methods are used 
 * to inform the MVertex of which edges it is incident with
 * in its enclosing model graph. These methods are invoked only by the enclosing MGraph. 
 * getIncidentMEdges gives access to this information, which can be used to speed up visualisation and marker passing
 * algorithms and so on.
 * <br>
 * A vertex can be part of only one MGraph.
 */
public interface MVertex extends MComponent {

    /**
     * Inform this vertex of new incident edge in the graph model.
     * <br>
     * Called only by enclosing graph and by MEdge
     */
    public void connectOutMEdge(MEdge e);
    
    /**
     * Inform this vertex of new incident edge in the graph model.
     * <br>
     * Called only by enclosing graph and by MEdge
     */
    public void connectInMEdge(MEdge e);
    
    /**
     * Inform this vertex of the fact that an edge is no longer incident with this vertex in the graph model.
     * <br>
     * Called only by enclosing graph and by MEdge
     */
    public void disconnectOutMEdge(MEdge e);
    
    /**
     * Inform this vertex of the fact that an edge is no longer incident with this vertex in the graph model.
     * <br>
     * Called only by enclosing graph and by MEdge
     */
    public void disconnectInMEdge(MEdge e);
    
    /**
     * Self evident
     */
    public Iterator getIncidentOutMEdges();
    /**
     * Self evident
     */
    public Iterator getIncidentInMEdges();
    /**
     * Self evident
     */
    public Iterator getIncidentMEdges();
}