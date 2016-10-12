package narrator.reader;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import narrator.Main;

//import org.apache.commons.collections15.BidiMap;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLReader;

public class FabulaReader {
	private DirectedGraph<FabulaNode, FabulaEdge> graph;
	//private String fabula;
	public static FabulaReader instance;
	
	public FabulaReader(String fabula) throws ParserConfigurationException, SAXException, IOException{
		//this.fabula = fabula;
		this.read(fabula);
	}

	public void read(String fabula) throws ParserConfigurationException, SAXException, IOException {
        //Step 1 we make a new GraphML Reader. We want an Directed Graph of type node and edge.
        GraphMLReader<DirectedGraph<FabulaNode, FabulaEdge>, FabulaNode, FabulaEdge> gmlr = new GraphMLReader<DirectedGraph<FabulaNode, FabulaEdge>, FabulaNode, FabulaEdge>(new VertexFactory(), new EdgeFactory());

        //Next we need a Graph to store the data that we are reading in from GraphML. This is also a directed Graph
        // because it needs to match to the type of graph we are reading in.
        graph = new DirectedSparseMultigraph<FabulaNode, FabulaEdge>();

		gmlr.load(fabula, graph);
		 //Here we read in our graph. filename is our .graphml file, and graph is where we
        // will store our graph.

       //BidiMap<FabulaNode, String> vertex_ids = gmlr.getVertexIDs();  //The vertexIDs are stored in a BidiMap.
       Map<String, GraphMLMetadata<FabulaNode>> vertex_meta = gmlr.getVertexMetadata(); //Our vertex Metadata is stored in a map.
       Map<String, GraphMLMetadata<FabulaEdge>> edge_meta = gmlr.getEdgeMetadata(); // Our edge Metadata is stored in a map.

        for (FabulaNode n : graph.getVertices())
        {
            n.setType(vertex_meta.get("EventType").transformer.transform(n)); 
            n.setAgens(vertex_meta.get("Agens").transformer.transform(n));
            n.setPatiens(vertex_meta.get("Patiens").transformer.transform(n));
            n.setTarget(vertex_meta.get("Target").transformer.transform(n));
            n.setInstrument(vertex_meta.get("Instrument").transformer.transform(n));
            n.setName(vertex_meta.get("Type").transformer.transform(n));
            n.setSuccesful(Boolean.parseBoolean(vertex_meta.get("Successful").transformer.transform(n)));
            try{
            	n.setTime(Long.parseLong(vertex_meta.get("Time").transformer.transform(n)));
            } catch (NumberFormatException e){
            	Main.error("Incorrectly formed time value in "+n.getName());
            }
            //System.out.println(n);
        }

        // Just as we added the vertices to the graph, we add the edges as well.
        Vector<FabulaEdge> removeEdges = new Vector<FabulaEdge>();
        Vector<FabulaNode> removeNodes = new Vector<FabulaNode>();
        for (FabulaEdge e : graph.getEdges())
        {
        	String type = edge_meta.get("RelType").transformer.transform(e);
        	e.setType(type); //Set the edge's value
        	
        	//adds PlotElements to the Node and remove them from the graph
        	if (type.equals("subplotelement")){
        		FabulaNode source = graph.getSource(e);
        		FabulaNode destination = graph.getDest(e);
        		destination.setSubPlotElement(source.toPlotElement());
        		
        		removeEdges.add(e);
        		removeNodes.add(source);
        	}
            //System.out.println("Edge ID: "+e.getId()+", Type: "+e.getType());
        }
        
        for (FabulaEdge e : removeEdges){
        	graph.removeEdge(e);
        }
        
        for (FabulaNode n : removeNodes){
        	graph.removeVertex(n);
        }

		
	}

	public Vector<FabulaNode> getAllElements() {
		Vector<FabulaNode> result = new Vector<FabulaNode>();
		result.addAll(graph.getVertices());
		return result;
	}
	
	public DirectedGraph<FabulaNode, FabulaEdge> getGraph(){
		return graph;
	}
	
	public void setGraph(DirectedGraph<FabulaNode, FabulaEdge> graph){
		this.graph = graph;
	}
}