/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */
 
// Last modification by: $Author: swartjes $
// $Log: MEdge.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:51  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2003/06/23 09:46:09  dennisr
// *** empty log message ***
//
// Revision 1.2  2002/09/23 07:44:43  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:05:41  dennisr
// first add
//
// Revision 1.4  2002/05/17 14:54:38  reidsma
// Full redocumentation
//

package parlevink.parlegraph.model;

/**
 * This interface is the base interface for all MEdge classes.
 * An MEdge is a diadic relation: it has one source and one target MVertex. (If the
 * MEdge is not directed the two nodes are still called 'source' and 'target'..)
 * An MEdge can be part of only one MGraph.
 * <br>
 * <hr>
 * <B>Multi level edges</B><br>
 * It is possible in a graph model to define edges for which one or both end points are not embedded 
 * in the same subgraph as the edge itself. It is also possible to define edges where the two endpoints 
 * are embedded in different subgraphs.
 * <br>
 * In principle, there is no problem here. The package does not actually rely on the end points being
 * embedded in the same subgraph. However, there are some cases where keeping track of this phenomenon 
 * is required. Therefore a few extra methods have been designed.
 * The special cases are:
 * <ol>
 *    <li> When adding an edge to a graph, the system should check whether the end points of the edge 
 *         are already present somewhere in the graph model. With multi level edges, the system should 
 *         then be able to detect the presence of an end point in the same graph even if it is embedded in
 *         a different subgraph.
 *         <br>
 *         The methods which have been introduced specifically for this case are listed below; for a detailed 
 *         description see the documentation of the relevant classes.
 *         <ul>
 *             <li> MGraph.containsMComponentRecursive(MComponent mc) 
 *             <li> MComponent.getTopMGraph()
 *             <li> MComponent.getMGraph(int up)
 *         </ul>
 *    <li> IDs are only unique <i>within one subgraph</i>. The XML reading procedures rely on the ID of end vertices
 *         to find the pointer to the right vertices. This means that the xml for a multi level edge should be extended 
 *         so it contains not only the ID of the end points but also information on the subgraph where that endpoint 
 *         should be found. <br>
 *         The methods & properties which have been introduced specifically for this case are:
 *         <ul>
 *             <li> edge-XML property multilevel: true if it is a multilevel edge.
 *             <li> edge-XML property up (for both end points): if it is a multilevel edge, the number of 
 *                  parent links you should follow to find the end node.
 *             <li> edge-XML property path (for both end points): if it is a multilevel edge, the path you should follow 
 *                  down through the subgraphs to find the end node, starting at the ancestor found through the 'up'
 *                  property. This property is a semicolon separated list of ID's
 *             <li> MGraph.findPath(MComponent mc) 
 *             <li> MGraph.followPath(ArrayList path) 
 *         </ul>
 * </ol>
 * <hr>
 * One last thing worth mentioning separately is the presence of methods for 'resolving'.
 * When an MGraph is being loaded from XML, it is possible that a new MEdge is loaded before
 * the XML of its end vertices has been processed. In that case it is already known that the MEdge has 
 * end vertices, but an actual pointer to those vertices cannot be obtained yet (hasSource 
 * returns true, though getSource() returns null).
 * When at a later time the vertices are loaded from XML, the pointers to those vertices 
 * should be resolved in this MEdge. This is done using the 'resolve()' method.
 */

public interface MEdge extends MComponent {

/*+++++++++++++++++++++++++*
 * Graph functionality.    *
 *+++++++++++++++++++++++++*/
 
    /**
     * Sets the source MVertex for this MEdge
     * If this MEdge is part of a MGraph, and the source MVertex is not present in the MGraph, 
     * or reachable from this graph through traversing sub and super graphs,
     * the source MVertex is added to the MGraph.
     * <br>
     * @@@Also notifies MVertices of change in incident edges....
     * <br>
     * afterwards, flagDirty == true
     */
    public void setSource(MVertex newSource);

    /**
     * Sets the target MVertex for this MEdge
     * If this MEdge is part of a MGraph, and the target MVertex is not present in the MGraph, 
     * or reachable from this graph through traversing sub and super graphs,
     * the target MVertex is added to the MGraph.
     * <br>
     * @@@Also notifies MVertices of change in incident edges....
     * <br>
     * afterwards, flagDirty == true
     */
    public void setTarget(MVertex newTarget);

    /**
     * Returns the source MVertex of the MEdge.
     * Returns null if this MEdge has no source vertex, or if the source vertex should exist 
     * but has not been read from XML yet.
     */
    public MVertex getSource();

    /**
     * Returns true if this MEdge has a source vertex, even if the source vertex 
     * has not been read from XML yet.
     */
    public boolean hasSource();
    
    /**
     * Returns the target MVertex of the MEdge
     * Returns null if this MEdge has no target vertex, or if the target vertex should exist 
     * but has not been read from XML yet.
     */
    public MVertex getTarget();

    /**
     * Returns true if this MEdge has a target vertex, even if the target vertex 
     * has not been read from XML yet.
     */
    public boolean hasTarget();
    
    /**
     * If this MEdge is incident with v, this method returns the other vertex 
     * (or returns v itself if this edge is a loop). Otherwise an IllegalArgumentException is thrown.
     */
    public MVertex getOtherVertex(MVertex v);
    
    /**
     * Makes the MEdge directed or undirected, depending on the argument.
     * <br>
     * afterwards, flagDirty == true
     */
    public void setDirected (boolean dir);
    
    /**
     * Returns true if the MEdge is directed.
     */
    public boolean isDirected ();
    
    /**
     * Reverses the direction of the edge, i.e. swaps source and target vertex. If the edge is not directed, 
     * the operation is performed but has no visible effect.
     * <br>
     * afterwards, flagDirty == true
     */
    public void  reverseDirection ();

    /**
     * Returns true if the specified MVertex is the source or target MVertex for this MEdge.
     * v != null; 
     * NB: If resolving of one of the end vertices fails, this method returns false,
     * even if it should have been true!
     */
    public boolean incidentWithMVertex(MVertex v);

/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/
 
/*----- readXML support -----*/

    /**     
     * A call to this method results in an attempt to resolve the end vertices of this MEdge. This is
     * needed when, while reading XML, the ID's of the end vertices were already known when this 
     * MEdge was created but those vertices were not yet processed. In that case the pointers to those
     * vertices have to be resolved.
     * <br>
     * afterwards, flagDirty == true. 
     * isResolved() may still be false however.
     */
    public void resolve();

    /**     
     * Returns true if the pointers to both the source and target of this MEdge are resolved 
     * (or if they are null, if the MEdge has no source or target).
     */
    public boolean isResolved();
}
