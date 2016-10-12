package natlang.rdg.documentplanner;

import java.util.*;

import natlang.rdg.model.*;
import parlevink.parlegraph.model.*;

/**
 * The StateTransformer decides how an internal state can be told best.
 * It can combine a state with an action or convert the state into an adverb or a entirely different sentence
 * 
 * @author Nanda Slabbers
 *
 */
public class StateTransformer 
{
	private RSGraph graph;
	
	private static final Vector HAPPYADVERBS = new Vector();
	private static final Vector SCAREDADVERBS = new Vector();
	private static final Vector ANGRYADVERBS = new Vector();

	private static final Vector HAPPYMESS = new Vector();
	private static final Vector SCAREDMESS = new Vector();
	private static final Vector ANGRYMESS = new Vector();
	
	/**
	 * Constructor - Initializes the lists of adverbs and sentences
	 *
	 */
	public StateTransformer()
	{
		HAPPYADVERBS.addElement("sing");
		HAPPYADVERBS.addElement("hum");
		HAPPYADVERBS.addElement("hop");
		HAPPYADVERBS.addElement("happy");
		
		SCAREDADVERBS.addElement("scared");
		SCAREDADVERBS.addElement("poundingheart");
		SCAREDADVERBS.addElement("shakinglegs");
		
		ANGRYADVERBS.addElement("angry");
		
		HAPPYMESS.addElement(new CannedText("Zij zong zachtjes in zichzelf."));
		HAPPYMESS.addElement(new CannedText("Haar hart maakte een sprongetje van vreugde."));
				
		SCAREDMESS.addElement(new CannedText("Ze stond te trillen van angst."));
		SCAREDMESS.addElement(new CannedText("Haar hart bonsde in haar keel."));
		
		ANGRYMESS.addElement(new CannedText("Ze stampte met haar voeten op de grond."));		
	}
	
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	public RSGraph getGraph()
	{
		return graph;
	}
	
	/**
	 * Starts the transformation of the document plan
	 * @return graph
	 */
	public RSGraph transform()
	{
//		Iterator it = graph.getMVertices();
//		transformDepthFirst((RSVertex) it.next());		
		if(graph.getRoot() != null)
			transformDepthFirst(graph.getRoot());			
		return graph;
	}
	
	/** 
	 * Transforms the document plan depth first and looks for state / action pairs
	 * @param v
	 */
	public void transformDepthFirst(RSVertex v)
	{		
		if (v.getType().equals("mess"))
		{
		}
		else if (v.getType().equals("text"))
		{
		}
		else if (v.getType().equals("rhetrel"))
		{
			RSVertex sat = ((RhetRelation) v).getSatellite();
			RSVertex nuc = ((RhetRelation) v).getNucleus();
			
			if (sat != null && sat.getType().equals("mess")
					&& nuc != null && nuc.getType().equals("mess") 
					&& ((RhetRelation) v).getLabel().equals("temporal"))
			{
				RSVertex vnew = transformState((PlotElement) sat, (PlotElement) nuc);
				
				if (vnew != null)
				{
					RhetRelation par = (RhetRelation) v.getParent().next();
					Iterator edges = par.getIncidentMEdges();
					MLabeledEdge edge = null;
					String label = "";
					while (edges.hasNext())
					{
						MLabeledEdge tmpedge = (MLabeledEdge) edges.next();
						if (tmpedge.getSource().equals(v) || tmpedge.getTarget().equals(v))
						{
							edge = tmpedge;
							label = tmpedge.getLabel();
						}							
					}
					
					graph.removeMVertex(v);
					graph.removeMEdge(edge);
					
					if (label.equals("nucleus"))
					{
						RSEdge nedge = new RSEdge("nucleus");
						nedge.setSource(par);
						nedge.setTarget(vnew);
						graph.addMEdge(nedge);
					}
					else
					{
						RSEdge sedge = new RSEdge("satellite");
						sedge.setTarget(par);
						sedge.setSource(vnew);
						graph.addMEdge(sedge);
					}
					System.out.println("Ja, een state transformen");
				}
			}
			else
			{
				if (sat != null)
					transformDepthFirst(sat);
				if (nuc != null)
					transformDepthFirst(nuc);
			}			
		}
	}
	
	/**
	 * Decides how a certain state and action can best be told, possibly by combining
	 * them into one node, otherwise connecting them (currently randomly)
	 * @param pe1 the state
	 * @param pe2 the action
	 * @return the state and action combined into one or connected by a rhetorical relation 
	 */
	public RSVertex transformState(PlotElement pe1, PlotElement pe2)
	{	
		if (pe1.getKind().equals("state") && pe2.getKind().equals("action"))
		{		
			int i = (int) (4 * Math.random());	
			switch (i) 
			{
			case 0:		//2 zinnen: De prinses trilde van angst. Ze schreeuwde.
				RSVertex tmp = createActionState(pe1);
				return createRhetRel("temporal", tmp, pe2);
			case 1: 	//1 zin met adjective: De bange prinses schreeuwde.
				return createAdjState(pe1, pe2);
			case 2:		//1 zin met adverbial adjunct: Bang schreeuwde de prinses / De prinses schreeuwde van angst
				return createAdvState(pe1, pe2);
			case 3:		//gewoon 2 zinnen: De prinses was bang. Ze schreeuwde.
				return createRhetRel("temporal", pe1, pe2);
			default:
				break;
			}
		}
		return null;
	}
	
	/**
	 * Possibility 1: Add adjective as detail to the AGENS of the action
	 * @param pe1 the state
	 * @param pe2 the action
	 * @return plot element
	 */
	public PlotElement createAdjState(PlotElement pe1, PlotElement pe2)
	{
		PlotElement result = pe2;		
		Detail det = new Detail(pe1.getAgens(), pe1.getName());		
		result.addDetail(det);
		return result;
	}
	
	/**
	 * Possibility 2: Add an adverb to the entire action 
	 * @param pe1 the state
	 * @param pe2 the action
	 * @return plot element
	 */
	public PlotElement createAdvState(PlotElement pe1, PlotElement pe2)
	{
		PlotElement result = pe2;		
		String adv = null;
						
		if (pe1.getName().equals("happy"))
		{
			int i = (int) (HAPPYADVERBS.size() * Math.random());
			adv = (String) HAPPYADVERBS.elementAt(i);			
		}
		else if (pe1.getName().equals("scared"))
		{
			int i = (int) (SCAREDADVERBS.size() * Math.random());
			adv = (String) SCAREDADVERBS.elementAt(i);
		}
		else if (pe1.getName().equals("angry"))
		{
			int i = (int) (ANGRYADVERBS.size() * Math.random());
			adv = (String) ANGRYADVERBS.elementAt(i);
		}		
		else
			return result;

		result.addDetail(new Detail(pe2.getName(), adv));			
		return result;
	}
	
	/**
	 * Possibility 3: Convert the state into an action
	 * @param pe
	 * @return rsvertex
	 */
	public RSVertex createActionState(PlotElement pe)
	{
		RSVertex result = pe;
										
		if (pe.getName().equals("happy"))
		{
			int i = (int) (HAPPYMESS.size() * Math.random());
			result = (RSVertex) HAPPYMESS.elementAt(i);			
		}
		else if (pe.getName().equals("scared"))
		{
			int i = (int) (SCAREDMESS.size() * Math.random());
			result = (RSVertex) SCAREDMESS.elementAt(i);
		}
		else if (pe.getName().equals("angry"))
		{
			int i = (int) (ANGRYMESS.size() * Math.random());
			result = (RSVertex) ANGRYMESS.elementAt(i);
		}		
		return result;
	}
	
	/**
	 * Creates a RhetRelation
	 * @param rel
	 * @param vert1
	 * @param vert2
	 * @return relation
	 */
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
