package narrator.documentplanner;

import java.util.Iterator;
import java.util.Vector;
import parlevink.parlegraph.model.MLabeledEdge;
import narrator.reader.FabulaNode;
import narrator.reader.FabulaReader;
import narrator.shared.GraphTransformer;
import narrator.shared.NarratorException;
import natlang.rdg.libraries.CauseLib;
import natlang.rdg.libraries.LibraryConstants;
import natlang.rdg.libraries.TemporalLib;
import natlang.rdg.model.PlotElement;
import natlang.rdg.model.RSEdge;
import natlang.rdg.model.RSGraph;
import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;

/**
 * <p>InitialDocumentPlanBuilder is the first component of the DocumentPlanner and
 * is responsible for the conversion of the fabula in OWL format into an initial
 * document plan.</p>
 * 
 * <p>A document plan is a binary tree consisting of rethorical relations (e.g.
 * "temporal", "nonvoluntary-cause", etc) as internal nodes. The leave nodes are
 * plot elements which are the fundamental elements of the story. These contain
 * the actions, perceptions, events, etc of the story.</p>
 * 
 * <p>Each internal node can have two children (hence it's a binary tree): a
 * nucleus and a satellite. The nucleus is the most important of the two,
 * and so each parent has at least a nucleus.</p> 
 * 
 * <p>The most important function is createTreeOwl(..) which implements the method 
 * described in Nanda Slabbers' thesis 'Narration for Virtual Storytelling' (2006)
 * (paragraph 6.2.2 Converting the fabula into an initial document plan, page 58-64).</p>
 * 
 * <p>The function transform() is the function that initiates all 
 * necessary functions in the correct order.</p>
 * 
 * 
 * @author Nanda Slabbers
 * @author René Zeeders
 * @author Marissa Hoek
 */
public class InitialDocPlanBuilder implements GraphTransformer{
	private RSGraph graph;
	private FabulaReader fabulaReader;
	private Vector<FabulaNode> toldMessages;
	private Vector<MLabeledEdge> edges;
		
	public InitialDocPlanBuilder(FabulaReader fabulaReader) throws NarratorException{
		this.fabulaReader = fabulaReader;
		
		toldMessages = new Vector<FabulaNode>();
		edges = new Vector<MLabeledEdge>();
	}
	
	@Override
	public RSGraph getGraph() {
		return graph;
	}

	@Override
	public RSGraph transform() {		
		//System.out.println(this.getStartingNode());
		
		FabulaNode n = getStartingNode();
		Vector<RSVertex> owltrees = new Vector<RSVertex>();
		while (n != null){
			RSVertex rr = createTree(n, null, null, true);
			owltrees.add(rr);
			n = getStartingNode();
		}
		
		Iterator<RSVertex> owltreeIt = owltrees.iterator();
		RSVertex tree = null;
		if(owltreeIt.hasNext())
			tree = owltreeIt.next();
			
		while(owltreeIt.hasNext()){
			
			RSVertex nuc = owltreeIt.next();
			if(nuc != null)
				tree = createRhetRel("temporal", tree, nuc);
			
		}
		
		tree = createRhetRel("temporal", tree);
	
		graph = new RSGraph((RhetRelation) tree);
		
		
		
		for (int i=0; i<edges.size(); i++)
		{			
			MLabeledEdge tmp = (MLabeledEdge) edges.elementAt(i);
			graph.addMEdge(tmp);
		}
		
		TreeBalancer tb = new TreeBalancer(graph);
		tb.balance(graph.getRoot());
		
		return graph;
	}

	/**
	 * Returns the starting node of the fabula, which is the node with the lowest
	 * time argument.
	 * @return the starting node
	 */
	private FabulaNode getStartingNode()
	{
		long time = Long.MAX_VALUE;
		
		// get all plot elements and select the one with the smallest time argument
		Vector<FabulaNode> allElements = fabulaReader.getAllElements();

		FabulaNode result = null;
		for (FabulaNode n : allElements){
			if (!alreadyTold(n) && n.getTime()<time){
				time = n.getTime();
				result = n;
			}
		}
	
		return result;
	}
	
	/**
	 * Converts the fabula structure into an initial document plan recursively. See 
	 * Nanda Slabbers' thesis 'Narration for Virtual Storytelling' (2006)
	 * (paragraph 6.2.2 Converting the fabula into an initial document plan, page 58-64)
	 * for a more in depth explanation. 
	 * Modified by Marissa Hoek to use the new Fabula format. 
	 * @param curr the name of the node currently to be added to the document plan
	 * @param prev the name of the last node added to the document plan
	 * @param v the document plan generated so far
	 * @param forward a boolean indicating whether the tree is to be created forwards or backwards
	 * @return the document plan
	 */
	private RSVertex createTree(FabulaNode curr, FabulaNode prev, RSVertex v, boolean forward)
	{	
		toldMessages.add(curr);
		System.out.println("Creating ID Tree for "+curr);
		
		//return null;
		
		Vector<FabulaNode> nextnodes = new Vector<FabulaNode>();
		Vector<FabulaNode> prevnodes = new Vector<FabulaNode>();
		RSVertex result = v;
		
		//fill nextnodes
		Vector<FabulaNode> initialNextnodes = new Vector<FabulaNode>();
		initialNextnodes.addAll(fabulaReader.getGraph().getSuccessors(curr));
		for (FabulaNode n : initialNextnodes){
			if (!alreadyTold(n)){
				nextnodes.add(n);
			}
		}
					
		//fill prevnodes
		Vector<FabulaNode> initialPrevnodes = new Vector<FabulaNode>();
		initialPrevnodes.addAll(fabulaReader.getGraph().getPredecessors(curr));
		for (FabulaNode n : initialPrevnodes){
			if (!alreadyTold(n)){
				//System.out.println(n);
				prevnodes.add(n);
			}
		}
		//if (prevnodes.size()>=1) System.out.println(prevnodes.size());
					
		
		if (forward) {
			if (prevnodes.size() == 0) {
				// do nothing at all
			}
			else if (prevnodes.size() == 1) {
				if (result != null) {
					// node is added to the document plan based on the time
					// arguments
					if (precedes(prevnodes.elementAt(0), result)) {
						RSVertex tree = createTree(prevnodes
								.elementAt(0), curr, null, false);
						if (tree != null)
							result = createRhetRel("additive", tree, result);
					}
					else {
						RSVertex tree = createTree(prevnodes
								.elementAt(0), curr, null, false);
						if (tree != null)
							result = createRhetRel("additive", result, tree);
					}
				}
				else
					result = createTree(prevnodes.elementAt(0), curr, null,
							false);
			}
			else if (prevnodes.size() > 1) {
				RSVertex tmpresult = null;
				for (int i = prevnodes.size() - 2; i >= 0; i--) {
					if (i == prevnodes.size() - 2) {
						RSVertex tree1 = createTree(prevnodes
								.elementAt(i), curr, null, false);
						RSVertex tree2 = createTree(prevnodes
								.elementAt(i + 1), curr, null, false);
						if (tree1 != null && tree2 != null)
							tmpresult = createRhetRel("additive", tree1,
									tree2);
						else if (tree1 == null)
							tmpresult = tree2;
						else if (tree2 == null)
							tmpresult = tree1;
					}
					else {
						RSVertex tree = createTree(prevnodes
								.elementAt(i), curr, null, false);
						if (tree != null)
							tmpresult = createRhetRel("additive", tree,
									tmpresult);
					}
				}
				if (result != null) {
					if (tmpresult != null)
						result = createRhetRel("additive", result,
								tmpresult);
				}
				else
					result = tmpresult;
			}

			if (nextnodes.size() == 0) {
				if (result != null) {
					//logger.warning("1. HIER!");
					String rel = getRelation(prev, curr);
					result = createRhetRel(rel, result,
							createPlotElement(curr));
				}
				else
					result = createPlotElement(curr);
			}
			else if (nextnodes.size() == 1) {
				if (result != null) {
					//logger.warning("2. HIER!");					
					String rel = getRelation(prev, curr);
					//System.out.println("Current: "+curr);
					//System.out.println("Previous: "+prev);
					//TODO I changed something here because otherwise the Narrator crashes on
					//nodes with two incoming edges ^MH
					result = createRhetRel(rel, result,
							createPlotElement(curr));
					//result = createTree(nextnodes.elementAt(0), curr, result,	true);
				}
				else
					result = createTree(
							nextnodes.elementAt(0), curr,
							createPlotElement(curr), true);
			}
			else if (nextnodes.size() > 1) {
				RSVertex tmpresult = null;
				for (int i = nextnodes.size() - 2; i >= 0; i--) {
					if (i == nextnodes.size() - 2) {
						RSVertex tree1 = createTree(nextnodes
								.elementAt(i), curr, null, true);
						RSVertex tree2 = createTree(nextnodes
								.elementAt(i + 1), curr, null, true);
						if (tree1 != null && tree2 != null)
							tmpresult = createRhetRel(
									"temp-after-sequence", tree1, tree2);
						else if (tree1 == null)
							tmpresult = tree2;
						else if (tree2 == null)
							tmpresult = tree1;
					}
					else {
						RSVertex tree = createTree(nextnodes
								.elementAt(i), curr, null, true);
						if (tree != null)
							tmpresult = createRhetRel(
									"temp-after-sequence", tree, tmpresult);
					}
				}

				if (result != null) {
					//logger.warning("3. HIER!");
					if (prev == null) {
					//	logger.warning("null");
					}
					System.out.println(prev + " - " + curr);
					String rel = getRelation(prev, curr);
					result = createRhetRel(rel, result,
							createPlotElement(curr));
					String rel2 = getRelation(curr, nextnodes
							.elementAt(0));
					result = createRhetRel(rel2, result, tmpresult);
				}
				else {
					//logger.warning("4. HIER!");
					String rel = getRelation(curr,nextnodes
							.elementAt(0));
					result = createRhetRel(rel, createPlotElement(curr),
							tmpresult);
				}
			}
		}
		else // if backwards
		{
			RSVertex prevs = null;
			RSVertex nexts = null;

			// the new previous nodes are used to create all incoming
			// branches
			if (prevnodes.size() == 1)
				prevs = createTree(prevnodes.elementAt(0),
						curr, null, false);
			else if (prevnodes.size() > 1) {
				for (int i = prevnodes.size() - 2; i >= 0; i--) {
					if (i == prevnodes.size() - 2) {
						RSVertex tree1 = createTree(prevnodes
								.elementAt(i), curr, null, false);
						RSVertex tree2 = createTree(prevnodes
								.elementAt(i + 1), curr, null, false);
						if (tree1 != null && tree2 != null)
							prevs = createRhetRel("additive", tree1, tree2);
						else if (tree1 == null)
							prevs = tree2;
						else if (tree2 == null)
							prevs = tree1;
					}
					else {
						RSVertex tree = createTree(prevnodes
								.elementAt(i), curr, null, false);
						if (tree != null)
							prevs = createRhetRel("additive", tree, prevs);
					}
				}
			}

			// the new next nodes are used to create all outgoing branches
			if (nextnodes.size() == 1)
				nexts = createTree(nextnodes.elementAt(0),
						curr, null, true);
			else if (nextnodes.size() > 1) {
				for (int i = nextnodes.size() - 2; i >= 0; i--) {
					if (i == nextnodes.size() - 2) {
						RSVertex tree1 = createTree(nextnodes
								.elementAt(i), curr, null, true);
						RSVertex tree2 = createTree(nextnodes
								.elementAt(i + 1), curr, null, true);
						if (tree1 != null && tree2 != null)
							nexts = createRhetRel("temp-after-sequence",
									tree1, tree2);
						else if (tree1 == null)
							nexts = tree2;
						else if (tree2 == null)
							nexts = tree1;
					}
					else {
						RSVertex tree = createTree( nextnodes
								.elementAt(i), curr, null, true);
						if (tree != null)
							nexts = createRhetRel("temp-after-sequence",
									tree, nexts);
					}
				}
			}

			// then the sofar generated trees are connected in the correct
			// order
			if (prevs != null) {
				//logger.warning("5. HIER!");
				String rel = getRelation(prevnodes
						.elementAt(prevnodes.size() - 1), curr);
				result = createRhetRel(rel, prevs, createPlotElement(curr));
			}
			if (nexts != null) {
				//logger.warning("6. HIER!");
				String rel = getRelation(curr, nextnodes
						.elementAt(0));
				if (result != null)
					result = createRhetRel(rel, result, nexts);
				else
					result = createRhetRel(rel, createPlotElement(curr),
							nexts);
			}
			if (result == null)
				result = createPlotElement(curr);
		}

		
		return result;
	}
	
	/**
	 * Checks whether plot element n happens before all plot elements used in v
	 * @param n the name of the current plot element
	 * @param v the branch that should appear before or after n
	 * @return whether n precedes all plot elements in v
	 */		
	private boolean precedes(FabulaNode n, RSVertex v)
	{
		
		long tn = n.getTime();
		
		long t1 = getRightmostTime(v);
		long t2 = getLeftmostTime(v);
		
		if (tn < t1 && tn < t2)
			return true;
		
		return false;
	}
	
	/**
	 * Creates RhetRelation node with only one child node
	 * @param rel kind of relation
	 * @param vert1 the nucleus
	 * @return the RhetRelation node of type rel and with child vert1
	 */
	private RhetRelation createRhetRel(String rel, RSVertex vert1)
	{
		//logger.info(" CREATERHETREL: " + rel + "\n ver1: " + vert1);
		
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert1);
									
		edges.addElement(nuc);
		
		return rhetrel;
	}
	
	/**
	 * Creates RhetRelation node with two child nodes
	 * @param rel kind of relation
	 * @param vert1 satellite
	 * @param vert2 nucleus
	 * @return the RhetRelation node of type rel and with children vert1 and vert2
	 */
	private RhetRelation createRhetRel(String rel, RSVertex vert1, RSVertex vert2)
	{
		System.out.println(" CREATERHETREL: " + rel + '\n' + " ver1: " + vert1 + '\n' + " ver2: " + vert2 + '\n');
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge sat = new RSEdge("satellite");
		sat.setTarget(rhetrel);
		sat.setSource(vert1);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert2);
					
		edges.addElement(sat);
		edges.addElement(nuc);
		
		return rhetrel;
	}
	
	/**
	 * Checks whether the plot element has already been added to the document plan.
	 * @param n the name of the plot element
	 * @return true if m has been added to the document plan, false otherwise
	 */	
	private boolean alreadyTold(FabulaNode n)
	{
		return toldMessages.contains(n);
	}
	
	private PlotElement createPlotElement(FabulaNode n){
		return n.toPlotElement();
	}
	
	/**
	 * Gets the time of the rightmost plot element in v
	 * @param v
	 * @return the time
	 */
	private long getRightmostTime(RSVertex v)
	{
		System.out.println(v);		
		if (v.getType().equals("mess"))
			return ((PlotElement) v).getTime();
		else if (v.getType().equals(("rhetrel")))
			return getRightmostTime(((RhetRelation) v).getNucleus());
		return -1;
	}
	
	/**
	 * Gets the time of the leftmost plot element in v
	 * @param v
	 * @return the time
	 */
	private long getLeftmostTime(RSVertex v)
	{
		if (v.getType().equals("mess"))
			return ((PlotElement) v).getTime(); 
		else if (v.getType().equals(("rhetrel")))
			getLeftmostTime(((RhetRelation) v).getNucleus());
		return -1;
	}
	
	/**
	 * Retrieves the relation between two plot elements from the fabula structure.
	 * Momentarily these fabula relations are translated to these rethorical relations:
	 * <ul>
	 *  <li>psi_causes, motivates --> cause-voluntary</li>
	 *  <li>phi_causes, enables --> cause-nonvoluntary</li>
	 * </ul>
	 * 
	 * If the relation is not one of the above, the rethorical relation 'temporal'
	 * is returned.
	 * 
	 * @param m1 name of the first plot element
	 * @param m2 name of the second plot element
	 * @return the relation between m1 and m2
	 */
	
	
	/* Possible relations are:
	 * 
	 * 	CAUSE = "cause";
	 *	PURPOSE = "purpose";
	 *	CONTRAST = "contrast";
	 * 	TEMPORAL = "temp";
	 * 	ADDITIVE = "add";
	 *  RELATIVE = "relative";
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	//TODO why doesn't this actually do what it says????
	private String getRelation(FabulaNode n1, FabulaNode n2)
	{
		if (fabulaReader.getGraph().findEdge(n1,n2)==null){
			//Try it the other way around
			if (fabulaReader.getGraph().findEdge(n2,n1)==null){
				System.err.println("No relation found for "+n1+", "+n2);
				return null;
			} else{
				//Switch them around if the other way around exists
				FabulaNode temp = n1;
				n1 = n2;
				n2 = temp;
			}
		}
		String relation = fabulaReader.getGraph().findEdge(n1,n2).getType();
		String result = relation;
		System.out.println(result);
		
		if (relation.equals("psi-causes")|| relation.equals("motivates"))
			result = LibraryConstants.CAUSE+"."+CauseLib.VOLUNTARY;
		if (relation.equals("phi-causes"))
			result = LibraryConstants.CAUSE+"."+CauseLib.INVOLUNTARY;
		if (relation.equals("perception") )
			//result = LibraryConstants.ADDITIVE;
			result = "additive";
		if (relation.equals("enables"))
			result = LibraryConstants.TEMPORAL+"."+TemporalLib.AFTER+"."+TemporalLib.SEQUENCE;
		if (relation.equals("goal"))
			result = LibraryConstants.PURPOSE;
		
		if (result.equals("")||result.equals("null")){
			System.err.println("No relation found!");
		}
		
		return result;
	}

	@Override
	public void setGraph(RSGraph graph) {
		this.graph = graph;		
	}

}
