/* @author Eelco Herder, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: SpanningTrees.java,v $
// Revision 1.1  2006/05/24 09:00:28  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:17  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2004/11/10 15:24:41  herder
// Added Tree Layout  and SpanningTrees, antialiasing in VGraphPanel turned on again
//
 
package parlevink.parlegraph.utils;

import parlevink.parlegraph.model.*;
import java.util.logging.*;
import java.util.*;

/**
This class provides static methods for creating different kinds of spanning trees.
Each method returns a set containing the edges that belong to the spanning tree.
*/

public class SpanningTrees
{
	
    /** Returns a set of edges that form a spanning tree of a graph,
        width distances to the root as short as possible.
    */
    public static Set findHierarchicalEdges(MGraph graph, MVertex root)
    {
        // Set that will contain the hierarchical edges
        Set hierarchicalEdges = new HashSet();
        // We want to perform breadth-first search
        // Therefore we make use of a queue for each iteration
        LinkedHashSet queue = new LinkedHashSet();
        queue.add(root); // Initial queue is the root
        LinkedHashSet newQueue; 
        // Sorted array of vertices that can be reached
        // via hierarchical edges (it needs to be sorted to ensure
        // that all vertices are as close to the root as possible
        // in the resulting spanning tree)
        LinkedHashSet verticesDone = new LinkedHashSet();
	    verticesDone.add(root);
        boolean forward = true;
        int numberIterations = 10;
        boolean firstTime = true; // To avoid breakdown of the loop
        
        /*
        In this loop, the graph is searched forwards and backwards
        (this changes with each iteration). In each iteration it
        adds all edges that can be reached from the queue in the
        specified direction to the spanning tree. 
        The loop ends when no vertices have been added.
        */
        while (numberIterations>1 || firstTime)
        {
            // If, after one iteration, no vertex has been found,
            // it still is possible that the root is a 'sink', with
            // incoming edges, but no outgoing edges.
            // In that case, the queue will be empty and should be filled with the root again
            if (!forward && firstTime)
            {
                firstTime = false;
                if (queue.isEmpty()) queue.add(root);
            }
            numberIterations = 0;
            while(!queue.isEmpty())
            {
                numberIterations++;
                newQueue = findHierarchicalEdgesIteration(graph, queue,verticesDone, hierarchicalEdges, forward);
                queue = newQueue;
            }
            // Put all vertices visited in the queue, so that
            // edges pointing to the other direction than the current
            // search direction can be found, and reverse search direction
            queue = (LinkedHashSet) verticesDone.clone();
            forward = !forward;
        }
        return hierarchicalEdges;

        
    }
        
        
	/**
	Looks at all outgoing or incoming edges from the queue (depends on search direction)
	and adds edges that lead to vertices that are not visited before to the set of hierarchical edges
	*/
	private static LinkedHashSet findHierarchicalEdgesIteration(MGraph graph, LinkedHashSet queue, LinkedHashSet verticesDone, Set hierarchicalEdges, boolean forward)
	{
	    LinkedHashSet newQueue = new LinkedHashSet();
	    Iterator edgeIt;
	    MLabeledEdge anEdge;
	    
	    // Loop through the queue (all vertices whose children need to be examined)
	    Iterator i=queue.iterator();
	    while (i.hasNext())
	    {
	        // Get outgoing edges of vertex queue(i) (forward loop) or incoming edges (backward loop)
	        if (forward)
	        {
	            edgeIt = ((MVertex) i.next()).getIncidentOutMEdges();
	        }
	        else
	        {
	            edgeIt = ((MVertex) i.next()).getIncidentInMEdges();
	        }
	        
	        // Iterate outgoing/incoming edges and add all that lead
	        // to vertices that are not in the set VerticesDone to
	        // the Set hierarchicaledges
	        while (edgeIt.hasNext())
	        {
	            anEdge = (MLabeledEdge) edgeIt.next();    
	            // forward search        
	            if (forward && !verticesDone.contains(anEdge.getTarget())) 
	            {
	               MVertex newVertex = anEdge.getTarget();
	               hierarchicalEdges.add(anEdge);
	               newQueue.add(newVertex); // we will have to examine the 
	               verticesDone.add(newVertex);
	            }
	            // backward search
	            else if (!forward &&!verticesDone.contains(anEdge.getSource()))
	            {
	               MVertex newVertex = anEdge.getSource();
	               hierarchicalEdges.add(anEdge);
	               newQueue.add(newVertex);
	               verticesDone.add(newVertex);
	            }
	        }
	    }
	    return newQueue;
	}
	
		/**
	Removes edges from the graph that do not belong to the
	hierarchical tree. The set of removed edges are returned
	*/
	public static Set createHierarchicalTree(MGraph graph, MVertex root, ArrayList userNames)
	{
		Set hierarchicaledges = findHierarchicalEdges(graph, root);
		Set removededges = new HashSet();
		Iterator edges = graph.getMEdges();
		MEdge anedge;
        	while (edges.hasNext())
		{

			anedge = (MEdge) edges.next();
			if (!hierarchicaledges.contains(anedge))
			{
				removededges.add(anedge);
				graph.removeMEdge(anedge);
				edges = graph.getMEdges();
			}

		}
		return removededges;
	}
	
	/**
	Add the removed edges, as returned by the method CreateHierarchicalTree
	to the graph. Obviously, the method can be used for adding other sets
	of edges as well, but we do not encourage such a type of use
	*/
	public static void addRemovedEdges(MGraph graph, Set removededges)
	{
		Iterator edges = removededges.iterator();
		MEdge anedge;
		while (edges.hasNext())
		{
			anedge = (MEdge) edges.next();
			graph.addMEdge(anedge);
		
		}
	}
		
	
}
