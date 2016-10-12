/* @author Eelco Herder, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VTreeLayout.java,v $
// Revision 1.1  2006/05/24 09:00:20  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:39  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2004/11/10 15:24:41  herder
// Added Tree Layout  and SpanningTrees, antialiasing in VGraphPanel turned on again
//
 

package parlevink.parlegraph.layout;

import parlevink.parlegraph.model.*;
import parlevink.xml.*;
import parlevink.parlegraph.view.*;
import parlevink.parlegraph.utils.*;

import java.util.logging.*;
import java.lang.Math;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;


/**
VTreeLayout takes care of layouting a graph according to its corresponding
hierarchical tree (imagine picking up the graph at the root and letting the rest dangle
will leave only a tree with the shortest paths taut)
 */
public class VTreeLayout extends VGraphLayout
{
	private MVertex mainRoot; //the first edge from which layouting will take place
	private Set hierarchicaledges;
	private MGraph graph;
	private double graphWidth;
	private ArrayList verticesDone;
	private boolean centerAboveChildren = true;
	private ArrayList userNames = null;
	

	/**
    Creates a default VTTreeLayout.
    */
    public VTreeLayout(VGraph vg)
    {
       	super(vg);
    	if (logger == null)
    	{
	   		logger = Logger.getLogger(getClass().getName());	    
	   	}
    }
    
    /**
    Creates a default VTreeLayout.
    Sets the main root.
    */
    public VTreeLayout(VGraph vg, MVertex root)
    {
        this(vg);
        setMainRoot(root);
    }
    

    
   
	
	/**
	Sets the main root, that is used first for building the spanning tree
	@param root A vertex that serves as main root (without incoming edges)
	*/
	public void setMainRoot(MVertex root)
	{
		this.mainRoot = root;

	}
	
  	/**
  	Sets preference for centering a vertex above its children; if this is 
  	set to false, a left-to-right tree will be drawn; default is true
  	*/
  	public void setCenterAboveChildren(boolean center)
  	{
  	    centerAboveChildren = center;
  	    //fullLayout();
  	}
  		
        
    /**
    Places a vertex at position (x,y) and recursively calls this
    method for its children and parents.
    Returns the width of the widest subtree
    */
    private double placeVertex(MVertex avertex, MEdge sourceEdge, double x, double y, boolean down, boolean isFirst)
    {
    	// get the visualization component and its width
    	VVertex viewVertex = (VVertex) theVGraph.getViewerForMComponent(avertex);
    	double returnWidth = 0;
	if (viewVertex!=null)
	{
	    	double selfwidth = viewVertex.getWidth();
	
	    	// remove the edge that was followed from hierarchicaledges
	    	// so that it will not be examined twice (which would result
	    	// in an infinite recursion)
	
	    	// Iterate outgoing edges
	    	double outwidth = 0; // keeps track of total width of subtree
	    	double inwidth = 0; // keeps track of total width of subtree
	
	    	if (sourceEdge!=null)
	    	{
	    		if (hierarchicaledges!=null) hierarchicaledges.remove(sourceEdge);
	    	    if (down)
	    	    {
	    	        inwidth = inwidth + ((VVertex) theVGraph.getViewerForMComponent(sourceEdge.getTarget())).getWidth() + 20;
	    	    }
	    	    else
	    	    {
	    	        outwidth = outwidth + ((VVertex) theVGraph.getViewerForMComponent(sourceEdge.getTarget())).getWidth() + 20;
	    	    }	    
	    	}
	    	    
	    	double pluswidth; // temporary integerer
	    	MEdge anInEdge = null;
	    	MEdge anOutEdge = null;
	    	MVertex childvertex;
	    	Iterator edgeI = avertex.getIncidentOutMEdges();
	    	int outEdgeCounter = 0;
	    	// Iterates all edges with the same parent
	    	while (edgeI.hasNext())
	    	{
	    	    outEdgeCounter++;
	    		anOutEdge = (MEdge) edgeI.next();
	    		// if an edge is part of the spanning tree, then the
	    		// target vertex should be placed at position (x+outwidth,y+50)
	    		// note that outwidth is updated
	    		if (hierarchicaledges.contains(anOutEdge))
	    		{
	    			childvertex = anOutEdge.getTarget();
	    			pluswidth = placeVertex(childvertex, anOutEdge, x+outwidth, y+viewVertex.getHeight()+20, true, false);
	    			outwidth = outwidth+pluswidth;
	    		}
	    	}
	    	//viewVertex.translate2D(VComponent.COMPONENT_ONLY,Math.max(0,outwidth/2 - selfwidth/2),0);
	    	//translateVertexChildren(avertex, -outwidth/2 + selfwidth/2, 0);
	    	
	    	// Iterate incoming edges
	
	    	edgeI = avertex.getIncidentInMEdges();
	    	int inEdgeCounter=0;
	    	// Iterates all edges with the same child
	    	while (edgeI.hasNext())
	    	{
	    	    inEdgeCounter++;
	    		anInEdge = (MEdge) edgeI.next();
	    		// if an edge is part of the spanning tree, then the
	    		// source vertex should be placed at position (x+outwidth,y-50)
	    		// note that inwidth is updated
	    		if (hierarchicaledges.contains(anInEdge))
	    		{
	    			childvertex = anInEdge.getSource();
	    			pluswidth = placeVertex(childvertex, anInEdge, x+inwidth, y-theVGraph.getViewerForMComponent(childvertex).getHeight()-20, false, false);
	    			inwidth = inwidth+pluswidth;
	    		}
	    	}
	    	
	    	double bottomline;
	    	double topline;
	    	if (outEdgeCounter==0) outwidth = 0;
	    	if (inEdgeCounter==0) inwidth = 0;
	    	double transX = 0;
	    	// if the vertex should be centered above its children, compute
	    	// the translation (this will be done by default)
	    	if (centerAboveChildren)
	    	{
	    	    // The original transX formula avoids a tendency to the left,
	    	    // but at the cost of two expensive getViewerForMComponent calls,
	    	    // therefore replaced 
	    	    //if (outEdgeCounter==1 && ((VVertex) theVGraph.getViewerForMComponent(anOutEdge.getTarget())).getWidth()==selfwidth) bottomline = selfwidth; else bottomline = outwidth;
	    	    //if (inEdgeCounter==1 && ((VVertex) theVGraph.getViewerForMComponent(anInEdge.getSource())).getWidth()==selfwidth) topline = selfwidth; else topline = inwidth;
	    	    //transX = Math.max(0, Math.max(bottomline/2.0 - selfwidth/2.0, topline/2.0 - selfwidth/2.0));
	    	    transX = Math.max(0, Math.max((outwidth-10)/2.0 - selfwidth/2.0, (inwidth-10)/2.0 - selfwidth/2.0));
	    	}
	    	viewVertex.setLocation2D(x+transX,y);
	    	if (!isFirst) returnWidth = 10 + Math.max(selfwidth, Math.max(outwidth,inwidth));
	    	else returnWidth = 10 + Math.max(selfwidth, Math.max(outwidth/2.0,inwidth/2.0));
	    	//if (graphWidth < x + (returnWidth/2.0)) graphWidth = x + (returnWidth/2.0);
	   	    
	    	setTouched(viewVertex, true);
	    	hierarchicaledges.add(sourceEdge);
	    	// return the maximum width (vertex itself or width of one of the subtrees)
	    	// Due to the recursion the next vertex in the same layer (y position)
	    	// will be placed at position x + maximum width + 10
	}
    	return returnWidth;
    }
    	
    /**
    Simple tree layout on the given VGraph.
    The root of the tree is the first node encountered, unless the root
    has been specified by the user
    */
    public void doLayout()
    {
    	//get MGraph
        graph = (MGraph)theVGraph.getMComponent();
        //test whether graph is empty
       	Iterator vertI = graph.getMVertices();  
        if (!vertI.hasNext())
        {
            	return; //empty graph...
        }
        else // not empty
        {
        	// if mainRoot hasn't been set, pick a random one (the first one found)
        	if (mainRoot==null)
        	{
        		setMainRoot((MVertex) vertI.next());
        	}
        }
		// obtain the edges that form the graph's hierarchical tree
		hierarchicaledges = SpanningTrees.findHierarchicalEdges(graph,mainRoot);
        // place the root at spot (0,0)
        // all its children (reachable in the hierarchical tree) will be
        // placed recursively
        //graphWidth = 0;
        graphWidth = placeVertex(mainRoot,null,0,0,true, true);	
        // Garbage collection; place remaining vertices to the right of the graph
        // Suggestion for future work: layout for graphs with more than one component
        VVertex aVertex;
        Iterator allVertices = ((MGraph) theVGraph.getMComponent()).getMVertices();
        int i=0;
        while (allVertices.hasNext())
        {
            aVertex = (VVertex) theVGraph.getViewerForMComponent((MVertex) allVertices.next());
            if (!touchedVComponents.contains(aVertex))
            {
                aVertex.setLocation2D(graphWidth*2,50*i);
                i++;
            }
        }
        
        //reset size of vgraph to some bounding box that is bounds to ALL vertices
        theVGraph.resetBounds();
        
	    //the minimumsize of a graph should be set in the layout method. 
	    theVGraph.setMinimumSize(theVGraph.getWidth(), theVGraph.getHeight());
    }
    
    

}
