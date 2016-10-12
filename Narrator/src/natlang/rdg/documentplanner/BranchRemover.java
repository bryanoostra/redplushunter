package natlang.rdg.documentplanner;

import java.util.*;

import parlevink.parlegraph.model.MLabeledEdge;
import natlang.rdg.model.*;

/**
 * This module removes long branches and splits them into smaller branches.
 * 
 * Needed to combine more dependency trees. In this version it simply breaks up branches
 * which are longer than three trees into branches of two or three trees.
 * 
 * @author Nanda Slabbers
 *
 */
public class BranchRemover
{
	RSGraph graph;
	
	RSVertex vert1;
	RSVertex vert2;
	
	int currlen;
	
	boolean changed;
	boolean stop;
	
	boolean nuc;
	
	/**
	 * Creates the Branch Remover
	 *
	 */
	public BranchRemover()
	{
		vert1 = null;
		vert2 = null;
		currlen = 0;
		changed = false;
		stop = false;
	}
	
	/**
	 * Sets the graph
	 * @param g the graph
	 */
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	/**
	 * Starts the transformation of the document plan
	 * @return graph
	 */
	public RSGraph transform()
	{		
		vert1 = null;
		vert2 = null;
		currlen = 0;
		changed = false;
		stop = false;	
		
		int cnt = 0;
		
		while (cnt < 10)
		{
			cnt++;
			vert1 = null;
			vert2 = null;
			currlen = 0;
			changed = false;
			stop = false;	
						
//			Iterator it = graph.getMVertices();
//			if (it.hasNext())
//				transformDepthFirst((RSVertex) it.next());
			if(graph.getRoot() != null)
				transformDepthFirst(graph.getRoot());
			
			if (!changed)
				break;
			if (!stop)
				break;
		}
						
		return graph;
	}
	
	private void p(String s){
		System.out.println(s);
	}
	
	/**
	 * Traverses the graph depth first while recognizing long branching and possibly splitting
	 * them into smaller branches.
	 * 
	 * @param v the current node
	 */
	public void transformDepthFirst(RSVertex v)
	{			
		p(" -- transformdepthfirst; " +
				"\n\t - currlen: " + currlen +
				"\n\t - type: " + v.getType() + " : " + 
				(v instanceof RhetRelation ? ((RhetRelation)v).getLabel() : ((PlotElement)v).getName()));
		if (stop)
			return;
				
		if (v.getType().equals("mess"))
		{			
			p(((PlotElement)v).getName() + " --> mess");
			PlotElement pe = (PlotElement) v;
			RhetRelation par = (RhetRelation) pe.getParent().next();
									
			boolean split = false;		// indicates whether the branch should be splitted
			boolean curr = false;		// IF the branch has to be splitted, it indicates whether it should be splitted at the current node or two levels down 
			
			// first check if the previous nodes are 'at the same level'
			// this means: the parent's other child node's nucleus is NOT a dependency tree....			
			RSVertex otherchild = par.getSatellite();
			if (otherchild != null && otherchild.getType().equals("rhetrel"))
			{
				p("otherchild != null && otherchild.getType().equals(\"rhetrel\")");
				RSVertex check = ((RhetRelation) otherchild).getNucleus();
				if (!check.getType().equals("mess"))
				{
					p("!check.getType().equals(\"mess\")");
					if (currlen > 1 && ((RhetRelation) check).getNucleus().getType().equals("mess"))
					{
						p("-- SPLIT activated");
						split = true;
						curr = true;
					}
					currlen = 0;
				}
			}		
			
			currlen++;
			
			
			
			// split if the relation is 'temporal' (since this relation will never be expressed)
			if (currlen > 2 && ((RhetRelation) v.getParent().next()).getLabel().equals("temporal"))
			{				
				p("temporal");
				boolean really = true;			// waarvoor was dat ook weer?
				RhetRelation rrpar = (RhetRelation) v.getParent().next();
				Iterator tmp = rrpar.getIncidentInMEdges();
				while (tmp.hasNext())
				{
					String lb = ((MLabeledEdge) tmp.next()).getLabel();
					if (lb.equals("nucleus"))
						really = false;
				}
				if (really)
				{
					p("-- SPLIT activated");
					curr = true;
					split = true;
				}
			}
			
			// split if length is 4 AND ( the parent relation is a causal relation OR the branch is exactly 4 nodes long ) 
			if (currlen == 4)
			{		
				p(" currlen is equal to 4 ");
				if (par.getLabel().startsWith("cause")){
					p("-- SPLIT activated");
					split = true;
				}
				
				// also split if the branch is EXACTLY four nodes long:
				// check if label up from parent is 'nuc'				
				String lbup = getLabelUp(par);
				if (lbup.equals("nucleus")){
					p("-- SPLIT activated");
					split = true;
				}
			}
			
			// always split if length is 5
			if (currlen == 5){
				p("currlen is equal to 5");
				p("-- SPLIT activated");
				split = true;
			}
			
			if (split)
			{
				p("-- SPLIT in progress");
				String rel = "";		// store away the original relation label
						
				// if the reason for splitting is the temporal relation:
				// the branch should be splitted exactly at node 'v'
				if (curr)
				{
					vert1 = par.getSatellite();
					rel = ((RhetRelation) pe.getParent().next()).getLabel();
					moveMessage(pe);
				}
				// if the reason for splitting is that the current length is 4 or 5:
				// the branch should be splitted at the node 2 nodes under v
				else
				{
					RhetRelation rr = (RhetRelation) par.getSatellite();
					
					// never split if the relation is 'elaboration'
					if (rr.getLabel().equals("relative"))
					{
						stop = true;
						return;
					}
					else
					{
						vert1 = (RhetRelation) rr.getSatellite();
						rel = rr.getLabel();
						moveMessage((PlotElement) (rr.getNucleus()));
					}
				}
										
				// update tree:
				// neem parent, gooi weg en zet v op plaat van parent
				// vert2 is dan nucleus van store
				// vert2 van store losmaken
				// vert1 en vert2 samenvoegen en als nuc van store opslaan
								
				RhetRelation store = getStore((PlotElement) v);				
								
				if (nuc)
					vert2 = store.getNucleus();
				else
					vert2 = store.getSatellite();
							
				// create a rhetorical relation with the original label
				RhetRelation rr = createRhetRel(rel, vert1, vert2);	
					
				if (nuc)
				{
					Iterator edges = store.getIncidentOutMEdges();
					MLabeledEdge me = null;
					while (edges.hasNext())
					{
						me = (MLabeledEdge) edges.next();
						if (me.getLabel().equals("nucleus"))
							break;
					}
					me.setTarget(rr);
				}
				else
				{
					Iterator edges = store.getIncidentInMEdges();
					MLabeledEdge me = null;
					while (edges.hasNext())
					{
						me = (MLabeledEdge) edges.next();
						if (me.getLabel().equals("satellite"))
							break;
					}
					me.setSource(rr);
				}
								
				stop = true;
				changed = true;
			}
		}
		else if (v.getType().equals("text"))
			currlen = 0;
		else if (v.getType().equals("rhetrel"))
		{			
			currlen = 0;
			
			RSVertex sat = ((RhetRelation) v).getSatellite();
			RSVertex nuc = ((RhetRelation) v).getNucleus();
			
			if (sat != null)
				transformDepthFirst(sat);
			if (nuc != null)
				transformDepthFirst(nuc);
		}
	}
	
	/**
	 * This function gets the Store node (the node that the branch is connected to).
	 * The node can be retrieved by taking the parent's parent etc and checking which
	 * node has an edge labeled nucleus as edge to their parent, or has a nucleus that
	 * is not a plot element  
	 * @param pe
	 * @return relation
	 */
	public RhetRelation getStore(PlotElement pe)
	{
		RhetRelation par = (RhetRelation) pe.getParent().next();
		
		while (par != null)
		{						
			String lab = getLabelUp(par);
			
			if (par.getSatellite() == null)
				return par;
			
			if (!lab.equals("satellite"))
			{
				Iterator edges = par.getIncidentInMEdges();
				while (edges.hasNext())
				{
					MLabeledEdge me = (MLabeledEdge) edges.next();
					if (me.getLabel().equals("nucleus"))
					{
						nuc = true;
						return (RhetRelation) me.getSource();
					}
				}
			}
			
			if (!par.getNucleus().getType().equals("mess"))
			{
				nuc = false;
				return par;
			}
						
			Iterator edges = par.getIncidentOutMEdges();
			while (edges.hasNext())
			{
				MLabeledEdge me = (MLabeledEdge) edges.next();
				if (me.getLabel().equals("satellite"))
					par = (RhetRelation) me.getTarget();
			}			
		}
		nuc = true;
		return par;
	}
	
	/** This function returns the label of the edge by which the node is connected
	 * to its parent node. 
	 * 
	 * Each rhetorical relation with two children has 3 edges: a nucleus, a satellite
	 * and the edge which relates the node to its parent node. A rhetorical relation node
	 * with only one child has an edge labelled nucleus and an edge which relates the node
	 * to its parent node.
	 * 
	 * @param rr
	 * @return label
	 */
	public String getLabelUp(RhetRelation rr)
	{		
		// assume that the node has 3 edges, return the label of the edge that occurs twice
		Iterator it = rr.getIncidentMEdges();
		boolean nuc = false;
		boolean sat = false;
		
		while (it.hasNext())
		{
			MLabeledEdge me = (MLabeledEdge) it.next();
			String lb = me.getLabel();
			if (lb.equals("nucleus"))
			{
				if (nuc)
					return lb;
				else
					nuc = true;
			}
			else
			{
				if (sat)
					return lb;
				else
					sat = true;
			}
		}
		
		// if no label has been found twice, the node has only one child node and
		// the other node can simply be returned
		if (sat)
			return "satellite";
		return "nucleus";
	}
		
	/**
	 * Moves the plot element one level up in the tree by removing the rhetorical relation
	 * that originally appeared at that place in the tree.
	 * @param pe
	 */
	public void moveMessage(PlotElement pe)
	{
		// ok is wat vaag, maar variabelen zijn als volgt:
		// dm: de node (detailedmessage) die verwijderd moet worden
		// par: de parent (rhetrel) van dm
		// edge: de pijl tussen par en dm
		// othernode: het andere kind van par (detailedmessage of rhetrel), dat op de plaats van par moet komen
		// otheredge: de pijl tussen par en othernode
		// parpar: de parent van par (rhetrel), een van de kinderen moet othernode worden
		// paredge: de pijl tussen parpar en par
		
		boolean print = false;
		
		if (print)
			System.out.println("move message: " + pe.getName());
		
		RhetRelation par = (RhetRelation) pe.getParent().next();
			
		if (print)
			System.out.println("parent node: " + par.getLabel());
		
		MLabeledEdge edge = (MLabeledEdge) pe.getIncidentMEdges().next();
		String label = edge.getLabel(); // altijd nucleus
		
		if (print)
			System.out.println("edge label: " + label);
		
		RSVertex othernode;
		
		if (label.equals("nucleus"))
			othernode = par.getSatellite();
		else
			othernode = par.getNucleus();
		
		if (print)
		{
			if (othernode.getType().equals("rhetrel"))	// ja altijd rhetrel (en is vert1)
				System.out.println("Other node is rr: " + ((RhetRelation) othernode).getLabel());
			if (othernode.getType().equals("mess"))
				System.out.println("Other node is dm: " + ((PlotElement) othernode).getName());
		}
		
		MLabeledEdge otheredge = null;		//altijd satellite
		MLabeledEdge paredge = null;
		Iterator it = par.getIncidentMEdges();
		while (it.hasNext())
		{
			MLabeledEdge e = (MLabeledEdge) it.next();
			RSVertex src = (RSVertex) e.getSource();
			RSVertex tgt = (RSVertex) e.getTarget();
			
			if (print)
			{
				System.out.println("nieuwe edge: " + e.getLabel());
				System.out.println("types src en tgt: " + src.getType() + " " + tgt.getType());
			}
			
			if (src.getType().equals("rhetrel") && tgt.getType().equals("rhetrel"))				
				paredge = e;
			if ((src.getType().equals("mess") || tgt.getType().equals("mess")) && !e.equals(edge))
				otheredge = e;
		}
		
		if (print)
		{
			if (otheredge != null)
				System.out.println("Other edge label: " + otheredge.getLabel());
			else
				System.out.println("other edge is null");
		}
		
		if (paredge != null)
		{				
			String paredgelabel = paredge.getLabel();
			
			if (print)
				System.out.println("par edge label: " + paredgelabel);
			
			if (paredgelabel.equals("nucleus"))
				paredge.setTarget(pe);
			else
				paredge.setSource(pe);					
						
			//graph.removeMVertex(othernode);	
			graph.removeMVertex(par);
			graph.removeMEdge(edge);
			graph.removeMEdge(otheredge);
		}
	}
	
	/**
	 * This function is created by René Zeeders.
	 * There are 6 posibilities with three solutions:
	 * 1.	sat
	 * 			*nuc*
	 * 			*sat*
	 * 
	 * 2.	nuc
	 * 			*nuc*
	 * 			*sat*
	 * 
	 * solution: if either starred vertice is removed,
	 * content of other vertice becomes content of parent
	 * 
	 * 3.	sat
	 * 			*sat*
	 * 	
	 * 4.	sat
	 * 			*nuc*
	 * 
	 * solution: if starred vertice is removed, delete parent also
	 * 
	 * 5.	nuc
	 * 			*sat*
	 * 	
	 * 6.	nuc
	 * 			*nuc*
	 * 
	 * solution: rename the other child of the parent of the 
	 * parent of the starred vertice (the one to be removed) to 'nuc', and then
	 * remove parent along with the starred vertice
	 * 
	 * @param pe the node to be removed 
	 */	
	private void rz_moveMessage(PlotElement pe)
	{
		// pe is element to be removed
		// pe_edge is the edge leading to pe (either nuc or sat)
		// par is parent of pe
		// othernode is the other child of par
		// parpar is the parent of par
		// parothernode is the the parent's sibbling
		
		boolean print = false;
		
		if (print) System.out.println("getting par...");
		RhetRelation par =(RhetRelation) pe.getParent().next();
		
		// relation between par is either nucleus or satellite
		// if nucleus, then par is source of edge
		// if satellite, then par is target of edge
		
		MLabeledEdge pe_edge = getParent(pe, par);
		
		String label = pe_edge.getLabel();
		RSVertex othernode = label.equals("nucleus") ? par.getSatellite() : par.getNucleus();
		
		MLabeledEdge othernode_edge = null;
		if(othernode != null)
			othernode_edge = getParent(othernode, par);
		
		
		if (print) System.out.println("getting parpar...");
		RhetRelation parpar = (RhetRelation) par.getParent().next();
		MLabeledEdge par_edge = getParent(par, parpar);
		String parlabel = par_edge.getLabel();

		RSVertex parothernode = parlabel.equals("nucleus") ? parpar.getSatellite() : parpar.getNucleus();
		MLabeledEdge parothernode_edge = null;
		if(parothernode != null)
			parothernode_edge = getParent(parothernode, parpar);
		
		String parothernodelabel = null;
		if (parothernode != null && parothernode.getType().equals("rhetrel"))
			parothernodelabel = ((RhetRelation) parothernode).getLabel();
		else if (parothernode != null)
			parothernodelabel = ((PlotElement) parothernode).getName();
		
		// solution 1 & 2
		// par.content = othernode.content
		// delete pe
		if(othernode != null){
			if (print) {
				System.out.println("Solution 1 & 2");
				System.out.println("	" + ((RhetRelation) parpar).getLabel());
				System.out.println("		\\---"+par_edge.getLabel()+"---> " + ((RhetRelation) par).getLabel());
				System.out.println("					\\---"+pe_edge.getLabel()+"---> " + pe);
				if(othernode != null )
					System.out.println("					\\---"+othernode_edge.getLabel()+"--->" + othernode);
				if(parothernode != null )
					System.out.println("		\\---"+parothernode_edge.getLabel()+"---> " + parothernode);
			}			
			

			if (print) System.out.println(" --> " + othernode_edge.getLabel());
			if(par_edge.getLabel().equals("nucleus")){
				par_edge.setTarget(pe);

			}
			else {
				par_edge.setSource(pe);

			}
			
//			graph.removeMVertex(pe);
			graph.removeMVertex(par);
			graph.removeMEdge(pe_edge);
			graph.removeMEdge(othernode_edge);
			
		}
		
	}
	
	/**
	 * Gives the edge between the give element and it's parent.
	 * Relation between par and el is either nucleus or satellite. 
	 * If nucleus, then par is source of edge
	 * if satellite, then par is target of edge
	 * @param el
	 * @param par
	 * @return
	 */
	private MLabeledEdge getParent(RSVertex el, RSVertex par){
		MLabeledEdge edge = null;
		Iterator edges_it = el.getIncidentMEdges();
		while(edges_it.hasNext()){
			MLabeledEdge e = (MLabeledEdge) edges_it.next();
			if(e.getLabel().equals("satellite") && e.getTarget() == par)
				edge = e;
			else if (e.getLabel().equals("nucleus") && e.getSource() == par)
				edge = e;
		}
		return edge;
	}
	
	public RhetRelation createRhetRel(String rel, RSVertex vert1, RSVertex vert2)
	{
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge sat = new RSEdge("satellite");
		sat.setTarget(rhetrel);
		sat.setSource(vert1);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert2);
					
		graph.addMEdge(sat);
		graph.addMEdge(nuc);
		
		return rhetrel;
	}
}
