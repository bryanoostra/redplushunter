package narrator.documentplanner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import edu.uci.ics.jung.graph.DirectedGraph;
import narrator.reader.FabulaEdge;
import narrator.reader.FabulaNode;
import narrator.reader.FabulaReader;
import narrator.reader.SaveEdge;
import narrator.reader.StringUtil;
import narrator.shared.Tuple;

public class Flashback {	
	private DirectedGraph<FabulaNode, FabulaEdge> graph;
	//Map a character and an emotion to a plot element.
	private Map<Tuple<String,String>,FabulaNode> emotionCauses;
	private Random rand;
	
	public Flashback(DirectedGraph<FabulaNode, FabulaEdge> graph, String history){
		this.graph = graph;
		this.emotionCauses = new HashMap<Tuple<String,String>,FabulaNode>();
		this.rand = new Random();
	
		FabulaReader fr = null;
		//read backstory file
		try {
			fr = new FabulaReader(history);
		} catch (Exception e){
			e.printStackTrace();
			
			//if an Exception is found, don't add flashbacks
			fr=null;
		}
		
		if (fr!=null){
			DirectedGraph<FabulaNode,FabulaEdge> fabulaGraph = fr.getGraph();
			for (FabulaNode n : fabulaGraph.getVertices()){
				if (n.getType().equals("state")){
					String emotion = n.getName();
					String agens = n.getAgens();
					for (FabulaEdge e : fabulaGraph.getInEdges(n)){
						if (e.getType().equals("psi-causes") || e.getType().equals("phi-causes")){
							FabulaNode causeNode = fabulaGraph.getSource(e);
							Tuple<String,String> tuple = new Tuple<String,String>(agens,emotion);
							//check if this is actually the latest cause for this emotion.
							//if the map contains an earlier one, replace it with this one
							FabulaNode previous = emotionCauses.get(tuple);
							if (previous==null){
								//if no other exists, add it without problems
								emotionCauses.put(tuple, causeNode);
							} else{
								//if another exists, replace only if the old one is from an earlier time
								if (previous.getTime()<=causeNode.getTime())
									emotionCauses.put(tuple, causeNode);
							}
						}
					}
				}
			}
		}
		
		System.out.println(emotionCauses);
		
		//create list of final emotion state and causes
		/*		
		kind = k;
		name = n;
		agens = a;
		patiens = p;
		target = t;
		this.instr = instr;*/
		//emotionCauses.put(new Tuple<String,String>("bystander1","angry"),new FabulaNode("action","insult","policeofficer","bystander1","",""));
	}
	
	public DirectedGraph<FabulaNode, FabulaEdge> transform(){
		System.out.println("Doing flashbacks");
		
        Vector<SaveEdge> addEdges = new Vector<SaveEdge>();
        Vector<FabulaNode> addNodes = new Vector<FabulaNode>();
        Vector<FabulaNode> alreadyAdded = new Vector<FabulaNode>();
		
		//loop over all nodes
		for (FabulaNode n : graph.getVertices()){
			//If the node is an emotion
			if (n.getType().equals("state")){
				System.out.println("State node found: "+n);
				//And does not have incoming edges that
				//directly cause it (psi-causes or phi-causes)
				boolean add = true;
				for (FabulaEdge e : graph.getInEdges(n)){
					if (e.getType().equals("psi-causes") || e.getType().equals("phi-causes"))
						add = false;
				}
				//if already added, don't add it again.
				//add a cause if it exists.
				if (add){
					System.out.println("Adding a flashback!");
					Tuple<String,String> test = new Tuple<String,String>(n.getAgens(),n.getName());
					System.out.println(test);
					FabulaNode p = emotionCauses.get(test);
					System.out.println(p);
					if (p!=null&&!alreadyAdded.contains(p)){
						alreadyAdded.add(p);
						p.setTime(n.getTime()-1);	//the cause should have the timestamp of the effect, minus 1
						p.setFlashback(true);
						addNodes.add(p);
						FabulaEdge addEdge = new FabulaEdge(rand.nextInt(9999));
						addEdge.setType("psi-causes");
						addEdges.add(new SaveEdge(addEdge,p,n));
					}
				}
			}
		}
		
       for (SaveEdge e : addEdges){
        	graph.addEdge(e.edge, e.node1, e.node2);
        } 
        for (FabulaNode n : addNodes){
        	graph.addVertex(n);
        }
		
		return graph;
	}
	
	public DirectedGraph<FabulaNode, FabulaEdge> getGraph(){
		return graph;
	}
}
