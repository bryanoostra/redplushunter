/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MGraph.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.4  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/02/24 13:24:00  dennisr
// *** empty log message ***
//
// Revision 1.2  2002/09/30 20:16:45  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.1  2002/09/16 14:05:42  dennisr
// first add
//
// Revision 1.16  2002/05/17 14:54:39  reidsma
// Redocumentation
//

package parlevink.parlegraph.model;

import java.util.*;

/**
 * This interface is the base interface for all graph classes in abstract graph models.
 * A graph is an MVertex that can contain MEdges and MVertices.
 * This interface defines several emthods to maintain the structure of the graph.
 * It is possible to define nested graphs which contain other graphs; edges may have a graph as end point
 * and the end points of an edge need not be embedded in the same subgraph as the edge or 
 * even the other end point (see also MEdge documentation).
 * <br>
 * Furthermore it is possible to access the vertices and edges using unique 
 * string identifiers. Those identifiers are only unique within this MGraph though. 
 * Edges and vertices cannot have the same identifiers.
 * <br>
 * It is not allowed to add MCOmponents to an MGraph when they are already element in another MGraph. 
 * Trying to do this results in an exception.
 * <br> 
 * At the moment, it is not possible to MOVE vertices or edges to other (sub)graphs. This should be
 * done by first removeing the MComponent from its parent graph and then adding it to another
 * parent graph. When moving vertices, one should take care not to lose the corresponding edges:
 * removing a vertex from an MGraph means removing its edges as well. So first collect all edges of 
 * the vertex, then remove vertex, add vertex to another MGraph, and add all edges to an MGraph.
 * This moving behaviour may be defined better in future versions of the parlegraph API.
 */
public interface MGraph extends MVertex {


/*++++++++++++++++++++++++++++++++*
 * Basic graph functionality      *
 *++++++++++++++++++++++++++++++++*/
 
/*-------Adding components-------*/

    /**
     * This method adds an MEdge to the collection of edges in this graph.
     * If the edge was already present, nothing changes.
     * If the edge contains vertices that are not present in this graph or in one 
     * of the supergraphs of this graph (directly or indirectly through subgraphs)
     * then those vertices will be added to this graph.
     * The MGraph property of the edge will get updated to point at this MGraph.
     * <br>
     * This method returns the ID string that has been assigned to the edge, or null if e == null.
     * The ID is garantueed to be unique within this MGraph.
     * <br>
     * NB: The MEdge must NOT yet be part of another graph. If it is, a runtime exception is thrown.
     * <br>
     * post: isDirty() == true
     */
    public String addMEdge(MEdge e);

    /**
     * This method adds an MVertex to the collection of vertices in this graph.
     * If the vertex was already present, nothing changes.
     * The MGraph property of the vertex will get updated to point at this MGraph.
     * <br>
     * This method returns the ID string that has been assigned to the vertex, or null if v == null.
     * This ID is garantueed to be unique within this MGraph.
     * <br>
     * NB: The MVertex must NOT yet be part of another graph. If it is, a runtime exception is thrown.
     * <br>
     * post: isDirty() == true
     */
    public String addMVertex(MVertex v);

    /**
     * This method adds an MEdge or MVertex to this graph, using addMEdge or addMVertex
     * depending on the class of mc.
     * This method returns the ID string that has been assigned to the component.
     * <br> If mc is not a vertex or edge, a RuntimeException is thrown.
     * <br> post: isdirty == true
     */
    public String addMComponent(MComponent mc);

    /**
     * This method adds an MEdge to the collection of edges in this graph, using the given ID.
     * If the edge was already present, its ID will be set to the new ID.
     * If the ID was already in use, a DuplicateIDException will be thrown.
     * If the edge contains vertices that are not present in this graph or in one 
     * of the supergraphs of this graph (directly or indirectly through subgraphs)
     * then those vertices will be added to this graph with a new, free, unique ID. 
     * The MGraph property of the edge will get updated to point at this MGraph.
     * <br>
     * NB: The MEdge must NOT yet be part of another graph. If it is, a runtime exception is thrown.
     * <br>
     * post: isDirty() == true
     */
    public void addMEdge(MEdge e, String ID);
    
    /**
     * This method adds a vertex to the graph, using the given ID.
     * If the vertex was already present, its ID will be set to the new ID.
     * If the ID was already in use, a DuplicateIDException will be thrown.
     * The MGraph property of the vertex will get updated to point at this MGraph.
     * <br>
     * NB: The MVertex must NOT yet be part of another graph. If it is, a runtime exception is thrown.
     * <br>
     * post: isDirty() == true
     */
    public void addMVertex(MVertex v, String ID);

    /**
     * This method adds an MEdge or MVertex with the given ID to this graph, using 
     * addMEdge or addMVertex depending on the class of mc.
     * If the component was already present, its ID will be set to the new ID.
     * If the ID was already in use, a DuplicateIDException will be thrown.
     * <br> If mc is not a vertex or edge, a RuntimeException is thrown.
     * <br> post: isdirty == true
     */
    public void addMComponent(MComponent mc, String ID);

    /**
     * This method adds an MEdge to the graph, WITHOUT ASSIGNING AN ID. Except for that,
     * this method behaves exactly like AddMEdge(MEdge e).
     * If one of the end vertices was not present, it will be added to the graph with a new,
     * free, unique ID. 
     * Use this method with care, because it may ruin the MGraph's ID administration.
     * If this method is used, take care to assign a correct unique ID afterwards!
     * <br> post: isdirty == true
     */
    public void addMEdgeNoID(MEdge e);
    
    /**
     * This method adds an MEdge to the graph, WITHOUT ASSIGNING AN ID. Besides that,
     * this method behaves exactly like AddMVertex(MVertex v).
     * Use this method with care, because it may ruin the MGraph's ID administration.
     * If this method is used, take care to assign a correct unique ID afterwards!
     * <br> post: isdirty == true
     */
    public void addMVertexNoID(MVertex v);
    
    /**
     * This method adds an MComponent to the graph, WITHOUT ASSIGNING AN ID. Besides that,
     * this method behaves exactly like AddMComponent(MComponent mc).
     * Use this method with care, because it may ruin the MGraph's ID administration.
     * If this method is used, take care to assign a correct unique ID afterwards!
     * <br> post: isdirty == true
     */
    public void addMComponentNoID(MComponent mc);
    
/*-------Removing components-------*/

    /**
     * This method removes an MEdge from the collection of edges in this graph.
     * If the edge was not present, nothing changes.
     * The graph property of the edge will be set to null. It's ID will remain unchanged 
     * but is more or less meaningless after the edge has been removed from the MGraph.
     * <br> post: isdirty == true
     */
    public void removeMEdge(MEdge e);
    
    /**
     * This method removes a MVertex from the collection of vertices in this graph.
     * If the vertex was not present, nothing changes.
     * All edges having this vertex as endpoint are removed from the graph as well.
     
     * [@@@ I am still not sure about the desirability of that removing-of-edges... 
     Since null end points are actually allowed, don't you think this is a job for 
     the controller? It's too much work, when the one modifying the graph does 
     probably not want such things to happen just like that... However,
     then those edges should still be set to end point null... And how can we 
     maintain correctness with subgraphs & edges pointing INTO the subgraph? @@@] *
     
     * The graph property of the vertex and all removed edges will be set to null.
     * The ID's will remain unchanged but are more or less meaningless after the 
     * components have been removed from the MGraph.
     * <br> post: isdirty == true
     */
    public void removeMVertex(MVertex v);

    /**
     * This method removes an MEdge with the given ID from the collection of edges in this graph.
     * If the edge was not present, nothing changes.
     * The graph property of the edge will be set to null. It's ID will remain unchanged 
     * but is more or less meaningless after the edge has been removed from the MGraph.
     * <br> post: isdirty == true
     */
    public void removeMEdge(String ID);

    /**
     * This method removes the MVertex with the given ID from the collection of vertices in this graph.
     * If the vertex was not present, nothing changes.
     * All edges having this vertex as endpoint are removed from the graph as well.
     
     * [@@@ I am still not sure about the desirability of that removing-of-edges... 
     Since null end points are actually allowed, don't you think this is a job for 
     the controller? It's too much work, when the one modifying the graph does 
     probably not want such things to happen just like that... However,
     then those edges should still be set to end point null... And how can we 
     maintain correctness with subgraphs & edges pointing INTO the subgraph? @@@] *
     
     * The graph property of the vertex and all removed edges will be set to null.
     * The ID's will remain unchanged but are more or less meaningless after the 
     * components have been removed from the MGraph.
     * <br> post: isdirty == true
     */
    public void removeMVertex(String ID);
    
    /**
     * This method first clears all subgraphs. Then it removes all vertices and edges
     * from the graph.
     * <br>
     * This method is recursive. It more or less completely destroys the structure of the
     * graph model.
     * <br>
     * If the graph should be cleared but the components should stay structured and available,
     * use 'decompose'.
     * <br> post: isdirty == true
     */
    public void clearMGraph();
    
    /**
     * This method removes all components from this MGraph and returns those in an
     * ArrayList of MComponents. Subgraphs stay intact and are returned as one element
     * in the list, each vertice and edge is a separate element in the list.
     * <br>
     * Since all MComponents are connected through pointers, links between the 
     * MComponents are kept intact. However, the different components may no longer be 
     * accessible through 'up' and 'path' access. <B> nader toelichten </b>
     * <br>
     * NB: if not all edges are resolved, these edges may be invalid or unresolveable 
     * after a call to decompose().
     */
    public ArrayList decompose();

/*-------Accessing components-------*/

    /** 
     * Returns the number of vertices in the graph.
     */
    public int size();

    /**
     * Returns true if this MComponent is an ancestor of the parameter mc. 
     */
    public boolean ancestorOf(MComponent mc);
    
    /**
     * This method returns an Iterator of all edges in the graph.
     */
    public Iterator getMEdges();

    /**
     * This method returns an Iterator of all edges in the graph having the given
     * MVertex as end or begin point.
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getMEdges(MVertex v);

    /**
     * This method returns an Iterator of all edges in the graph having the given
     * MVertex as begin point. Loops are also present only once in the iterator.
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getOutMEdges(MVertex v);

    /**
     * This method returns an Iterator of all edges in the graph having the given
     * MVertex as end point.
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getInMEdges(MVertex v);
    
    /**
     * This method returns an Iterator of all vertices in the graph.
     */
    public Iterator getMVertices();

    /**
     * This method returns an Iterator of all edges and vertices in the graph.
     */
    public Iterator getMComponents();

    /**
     * Returns true if this MGraph directly (non-recursive) contains the given MEdge
     */
    public boolean containsMEdge(MEdge e);
    
    /**
     * Returns true if this MGraph directly (non-recursive) contains the given MVertex
     */
    public boolean containsMVertex(MVertex v);

    /**
     * Returns true if this MGraph directly (non-recursive) contains the given MComponent (MEdge or MVertex)
     */
    public boolean containsMComponent(MComponent mc);
    
/*+++++++++++++++++++++++++++++++++*
 * ID section                      *
 * This section contains all kinds *
 * of methods to manipulate IDs    *
 * in the MGraph.                  *
 *+++++++++++++++++++++++++++++++++*/

    /**
     * This method returns the ID for the given MVertex, which must be present in the graph.
     * If the vertex was not contained in the graph, an exception is thrown.
     */
    public String getID(MVertex v);

    /**
     * This method returns the ID for the given MEdge, which must be present in the graph.
     * If the edge was not contained in the graph, an exception is thrown.
     */
    public String getID(MEdge e);

    /**
     * This method returns the MVertex for the given ID, which must be present in the graph.
     * If the ID was not contained in the graph, an exception is thrown.
     */
    public MVertex getMVertex(String ID);

    /**
     * This method returns the MEdge for the given ID, which must be present in the graph.
     * If the ID was not contained in the graph, an exception is thrown.
     */
    public MEdge getMEdge(String ID);

    /**
     * This method returns an MEdge or MVertex with the given ID, which must be present in the graph.
     * If the component was not contained in the graph, an exception is thrown.
     */
    public MComponent getMComponent(String ID);

    /**
     * This method sets the ID for the given MVertex, which must be present in the graph (or an exception is thrown).
     * If the vertex was not contained in the graph, nothing happens.
     * If the given ID was already in use, a DuplicateIDException is thrown.
     * post: isdirty == true
     */
    public void setID(MVertex v, String ID);

    /**
     * This method sets the ID for the given MEdge, which must be present in the graph (or an exception is thrown).
     * If the edge was not contained in the graph, nothing happens.
     * If the given ID was already in use, a DuplicateIDException is thrown.
     * post: isdirty == true
     */
    public void setID(MEdge e, String ID);
    
    /**
     * This method sets the ID for the given MComponent, which must be present in the graph (or an exception is thrown).
     * If the MComponent was not contained in the graph, nothing happens.
     * If the given ID was already in use, a DuplicateIDException is thrown.
     * post: isdirty == true
     */
    public void setID(MComponent mc, String ID);

    /**
     * Returns true iff this graph contains an element with the given ID.
     */
    public boolean containsID(String ID);

    /**
     * This method returns an ID that is garantueed to be unique in this graph.
     */
    public String getFreeID();
    
    /**
     * This method returns an Iterator of all IDs of all elements in the graph 
     * (NOT including the ID of the graph itself).
     */
    public Iterator getAllIDs();

/*+++++++++++++++++++++++++++++++++*
 * Recursive section               *
 * This section contains methods   *
 * that search the subgraphs of    *
 * this MGraph recursively.        *
 * This section is not complete,   *
 * i.e. does not contain all       *
 * symmetrical function.           *
 *+++++++++++++++++++++++++++++++++*/
    
    /**
     * Returns true if this MGraph or any subgraph of this MGraph contains the given 
     * MComponent.
     */
    public boolean containsMComponentRecursive(MComponent mc);
    
    /**
     * Returns a path to the given component, if it is contained (recursively) in this 
     * MGraph. The path consists of a list of ID's of the sub-MGraphs in which the
     * mcomponent is embedded, starting with the ID of this MGraph. 
     * <br> If mc == null, null is returned
     * <br> If mc is not contained in the graph (recursively), an empty arraylist is returned.
     * <br> The find- and follow path methods are used in the xml of edges, among other things.
     */
    public ArrayList findPath(MComponent mc);

    /**
     * Returns the MGraph that can be reached following the subgraphs based on the IDs in 'path'.
     * Returns null if no path of subgraphs with the IDs in 'path' can be followed, or if path == null.
     * <br> The find- and follow path methods are used in the xml of edges, among other things.
     */
    public MGraph followPath(ArrayList path);

    /**
     * This method returns an Iterator of all edges in the graph and its subgraphs having the given
     * MVertex as end or begin point.
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getMEdgesRecursive(MVertex v);

    /**
     * This method returns an Iterator of all edges in the graph and its subgraphs having the given
     * MVertex as begin point.
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getOutMEdgesRecursive(MVertex v);

    /**
     * This method returns an Iterator of all edges in the graph and its subgraphs having the given
     * MVertex as end point.
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getInMEdgesRecursive(MVertex v);

/*xmlstuff*/
    /**
     * This method tries to resolve all edges, i.e. tries to find the end points as defined by the XML
     * and attach them to the edges.
     */
    public void resolveEdges();

}
