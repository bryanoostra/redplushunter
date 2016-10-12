package narrator.documentplanner;

import java.util.Vector;

import narrator.reader.FabulaEdge;
import narrator.reader.FabulaNode;
import narrator.shared.Settings;
import edu.uci.ics.jung.graph.DirectedGraph;


/** Transforms the graph into a focalized graph.
 * 
 * The rules are: if a node doesn't contain the focalizing
 * character, including perceptions, the node and connecting
 * vertices are removed.
 * @author Marissa
 *
 */
public class Focalizer {
	private DirectedGraph<FabulaNode, FabulaEdge> graph;
	
	public Focalizer(DirectedGraph<FabulaNode, FabulaEdge> graph){
		this.graph = graph;
	}
	
	public DirectedGraph<FabulaNode, FabulaEdge> transform(){
        Vector<FabulaEdge> removeEdges = new Vector<FabulaEdge>();
        Vector<FabulaNode> removeNodes = new Vector<FabulaNode>();
        
        for (FabulaNode n : graph.getVertices())
        {
        	if (!containsCharacter(n)){
        		removeNodes.add(n);
        		for (FabulaEdge e : graph.getIncidentEdges(n)){
        			removeEdges.add(e);
        		}
        	}
        }
        
        for (FabulaEdge e : removeEdges){
        	graph.removeEdge(e);
        }    
        for (FabulaNode n : removeNodes){
        	graph.removeVertex(n);
        }
		
		return graph;
	}
	
	public DirectedGraph<FabulaNode, FabulaEdge> getGraph(){
		return graph;
	}
	
	private boolean containsCharacter(FabulaNode node){
		String character = Settings.FOCALIZEDCHARACTER;
		return node.getAgens().equals(character) 
				| node.getPatiens().equals(character) 
				| node.getTarget().equals(character);
	}
}
