package natlang.rdg.surfacerealizer;

import natlang.rdg.libraries.*;
import natlang.rdg.model.*;
import parlevink.parlegraph.model.*;

import java.util.*;

/** The Elliptor class performs Ellipsis. First it is checked whether there are one
 *	or more identical nodes in each conjunct. If so, they are marked 'identical' 
 *	and a suitable construction is selected, after which the tree or node is transformed
 *	@author Feikje Hielkema
 *	@version 1.0
 */

public class Elliptor implements RDGTransformer, LibraryConstants
{
	private DepTreeTransformer trans;	//transformer of the given tree
	private String relation;			//category of the relation
	public static final String CNJELLIPTED = "cnjellipted";
	public static final String CNJRAISEDRIGHTNODE = "cnjraised";
	
	/** Constructs the elliptor for a dependency tree, setting the root to be 
	 *	transformed and the relation it represents 
	 */
	public Elliptor(RSDepTreeModel tree, String rel)
	{
		trans = new DepTreeTransformer(tree);
		relation = rel;
	}
	
	/** Checks whether the tree has recurrent elements */
	public boolean check()
	{
		RSDepTreeNode root = trans.getTree().getRoot();		//check whether there is a coordinator
		MLabeledEdge edge = root.getOutgoingEdge(CRD);
		if (edge == null)					//must be paratactic structure
			return false;
			
		RSDepTreeNode t = (RSDepTreeNode) edge.getTarget();	//cannot get Ellipsis with "want" 
		if (t.getData().get(RSTreeNodeData.ROOT).equals("want"))
			return false;
					
		List conjuncts = getConjuncts();
		if (conjuncts.size() != 2)	//the relations should be nested, so that 
			return false;		//there are always exactly two conjuncts
		
		boolean result = false;
		RSDepTreeNode c1 = (RSDepTreeNode) conjuncts.get(0);
		RSDepTreeNode c2 = (RSDepTreeNode) conjuncts.get(1);
		String cat1 = c1.getData().get(RSTreeNodeData.CAT);
		String cat2 = c2.getData().get(RSTreeNodeData.CAT);
					
		if (cat1.equals(SMAIN) && cat2.equals(SMAIN))	//no nested conjunction
		{												//for every child node, check
			Iterator it = c1.getIncidentOutMEdges();	//if it has a twin in the other conjunct
			while (it.hasNext())
			{
				edge = (MLabeledEdge) it.next();
				RSDepTreeNode target = (RSDepTreeNode) edge.getTarget();
				if (edge.getLabel().indexOf(BORROWED) < 0)	//there may be a borrowed subject in a vc, that cannot be ellipted there
				{
					String catTarget1 = target.getData().get(RSTreeNodeData.CAT);
					if ((catTarget1 != null) && catTarget1.equals(CONJ))	//coordinated constituent
						target = (RSDepTreeNode) target.getChildNodes(CNJ).next();
						
					Iterator it2 = c2.getChildNodes(edge.getLabel());
					while (it2.hasNext())
					{
						RSDepTreeNode target2 = (RSDepTreeNode) it2.next();
						//if target2 is a conjunction, one if its conjuncts might be identical to target
						String catTarget = target2.getData().get(RSTreeNodeData.CAT);
						if ((catTarget != null) && catTarget.equals(CONJ))
							target2 = (RSDepTreeNode) target2.getChildNodes(CNJ).next();
						
						if (identical(target, target2))	//if the nodes are identical, mark them
						{								//so they can be easily retrieved
							result = true;
							mark(target, target2);
						}
					}
				}
			}
		}
		else	//nested conjunction
		{		//find out which is the conjunction, which the sentence
			RSDepTreeNode conjunction = null;	//(there can only be one conjunction)
			RSDepTreeNode smain = null;
			if (cat1.equals(CONJ))
			{
				conjunction = c1;
				smain = c2;
			}
			else
			{
				conjunction = c2;
				smain = c1;
			}
			//get the conjuncts of the nested conjunction
			List subConjuncts = getConjuncts(conjunction);	
			RSDepTreeNode subC1 = (RSDepTreeNode) subConjuncts.get(0);
			RSDepTreeNode subC2 = (RSDepTreeNode) subConjuncts.get(1);
			Iterator it = subC1.getIncidentOutMEdges();
			List borrowed = new ArrayList();
			
			while (it.hasNext())	//collect all borrowed nodes in the nested conj
			{						//because these have been involved in another ellipsis
				edge = (MLabeledEdge) it.next();
				if (edge.getLabel().indexOf(BORROWED) >= 0)
					borrowed.add(edge.getTarget());
			}
			it = subC2.getIncidentOutMEdges();
			while (it.hasNext())
			{
				edge = (MLabeledEdge) it.next();
				if (edge.getLabel().indexOf(BORROWED) >= 0)
					borrowed.add(edge.getTarget());
			}
			
			//for all borrowed nodes, check whether there is an identical one in smain
			for (int i = 0; i < borrowed.size(); i++)
			{
				RSDepTreeNode b = (RSDepTreeNode) borrowed.get(i);
				StringBuffer sb = new StringBuffer(b.getIncomingLabel());
				int idx = sb.indexOf(BORROWED);		//the identical node would not be labeled borrowed
				if (idx >= 0)						//so adapt the label
					sb.delete(idx, (idx + BORROWED.length()));
				
				it = smain.getChildNodes(sb.toString());
				while (it.hasNext())
				{
					RSDepTreeNode target = (RSDepTreeNode) it.next();
					if (identical(b, target))		//if a borrowed node has a twin, mark the twin
					{
						mark(b, target);
						result = true;
					}
				}
			}
		}
		
		return result;
	}
	
	/** Checks whether the two given nodes (and their children) are identical */
	private boolean identical(RSDepTreeNode node1, RSDepTreeNode node2)
	{
		if (!node1.getData().equals(node2.getData()))	//nodes must have the same data
			return false;								//(at least rel, pos/cat and root)
			
		if (node1.isLeaf())		//node2 must be leaf as well, because their data are identical
			return true;
		
		Iterator it1 = node1.getChildNodes();
		Iterator it2 = node2.getChildNodes();
				
		while (it1.hasNext())
		{
			if (!it2.hasNext())		//iterators must have same amount of elements
				return false;
			it2.next();
				
			boolean twin = false;
			RSDepTreeNode child1 = (RSDepTreeNode) it1.next();
			Iterator subIt = node2.getOutgoingEdges(child1.getIncomingLabel());
			while (subIt.hasNext())		//there may be two children with the same relation
			{				//(e.g. modifiers), they should not be confused in checking
				MLabeledEdge edge = (MLabeledEdge) subIt.next();
				if (identical(child1, (RSDepTreeNode) edge.getTarget()))
				{	//check whether their children are identical
					twin = true;
					break;
				}
			}
			if (!twin)
				return false;
		}
		if (it2.hasNext())			//iterators must have same amount of elements
			return false;
		return true;
	}
	
	/**	This function marks the edges to two nodes that have been found to be identical 
	 *	(it may only be called from check()!)
	 */
	private void mark(RSDepTreeNode node1, RSDepTreeNode node2)
	{
		Iterator it = node1.getIncidentInMEdges();
		StringBuffer sb = new StringBuffer(node1.getIncomingLabel());
		sb.append(IDENTICAL);
		
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			String lb = edge.getLabel();	//only mark it if it is not already marked borrowed or identical
			if ((lb.indexOf(BORROWED) < 0) && (lb.indexOf(IDENTICAL) < 0))
				edge.setLabel(sb.toString());
		}
				
		it = node2.getIncidentInMEdges();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			String lb = edge.getLabel();
			if ((lb.indexOf(BORROWED) < 0) && (lb.indexOf(IDENTICAL) < 0))
				edge.setLabel(sb.toString());		//as the two nodes are identical, their edges must have the same label
		}
	}
	
	/** Returns all the nodes that were marked as identical by check() */
	private List getMarkedNodes()
	{
		List twins = new ArrayList();
		List l = getConjuncts();
		RSDepTreeNode conjunct = (RSDepTreeNode) l.get(0);
		if (!conjunct.getData().get(RSTreeNodeData.CAT).equals(SMAIN))	//if there is a nested conjucntion,
			conjunct = (RSDepTreeNode) l.get(1);						//find the smain, because all those nodes
		Iterator it = conjunct.getIncidentOutMEdges();					//are marked identical, the others may be borrowed
		
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			if (edge.getLabel().indexOf(IDENTICAL) >= 0)
				twins.add((RSDepTreeNode) edge.getTarget());
		}
		return twins;
	}
	
	/** Returns the conjuncts below the root */
	private List getConjuncts()
	{
		return getConjuncts(trans.getTree().getRoot());
	}
	
	/** Returns the conjuncts below node
	 */
	private List getConjuncts(RSDepTreeNode node)
	{	//node must be a conjunction
		if (!node.getData().get(RSTreeNodeData.CAT).equals(CONJ))
			return null;
		
		Iterator it = node.getIncidentOutMEdges();
		List conjuncts = new ArrayList();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			if (edge.getLabel().indexOf(CNJ) >= 0)
				conjuncts.add(edge.getTarget());
		}
		return conjuncts;
	}
		
	/** Picks a suitable construction and transforms the tree */
	public boolean transform()
	{
		try
		{
			if (!check())	//check for identical nodes
				return false;
		
			getAppropriateConstruction();	//perform the ellipsis
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/** Selects one or more suitable ellipsis structures and performs them.
	 *	The selection is made based on the number of marked nodes, and their relations
	 *	@throws Exception if there are more conjuncts than identical nodes
	 */
	private void getAppropriateConstruction() throws Exception
	{
		List nodes = getMarkedNodes();
		for (int i = 0; i < nodes.size(); i++)
		{	//print out marked nodes, so we can check if the marking went ok
			RSDepTreeNode n = (RSDepTreeNode) nodes.get(i);
			System.out.println(((MLabeledEdge) n.getIncidentInMEdges().next()).getLabel());
		}
		
		List conjuncts = getConjuncts();
		int numIdNodes = nodes.size() * conjuncts.size();
		if (numIdNodes < conjuncts.size())		//should not happen, check() would return false
			throw new Exception("More conjuncts than identical parts - have no way to handle this");
			
		RSDepTreeNode parent1 = (RSDepTreeNode) conjuncts.get(0);	//get the conjuncts
		RSDepTreeNode parent2 = (RSDepTreeNode) conjuncts.get(1);
		Iterator it1 = parent1.getIncidentOutMEdges();				//and their child nodes
		Iterator it2 = parent2.getIncidentOutMEdges();
		int cntr = 0;
		RSDepTreeNode strip = null;
		
		while (it1.hasNext() || it2.hasNext())
		{	//check whether there is a node with root 'ook' or 'niet', to indicate we should strip
			if (it1.hasNext())
			{
				MLabeledEdge edge = (MLabeledEdge) it1.next();
				RSDepTreeNode temp = (RSDepTreeNode) edge.getTarget();
				String s = temp.getData().get(RSTreeNodeData.ROOT);
				if ((s != null) && (s.equals("niet") || s.equals("ook")))
					strip = temp;
			}
			if (it2.hasNext())
			{
				MLabeledEdge edge = (MLabeledEdge) it2.next();
				RSDepTreeNode temp = (RSDepTreeNode) edge.getTarget();
				String s = temp.getData().get(RSTreeNodeData.ROOT);
				if ((s != null) && (s.equals("niet") || s.equals("ook")))
					strip = temp;
			}
			cntr++;	//cntr is the maximum number of children
		}
		
		int numDif = cntr - nodes.size();	//difference between number of children and marked nodes
		
		if (numDif == 1)	//only one node unmarked, so we can coordinate it or strip
		{	//the same restrictions go for Stripping and coordinating of single constituent
			Random rand = new Random();	//so choose using a random operator
			if (rand.nextInt(2) == 0)
				Strip(strip);
			else
				CoordConstituent();
			return;
		}
		else if ((numDif == 2) && (strip != null))	//two nodes different, but one of them is 'ook' or 'niet'
		{						//so strip: Amalia vlucht maar Brutus niet
			Strip(strip);
			return;
		}
		
		//else, perform other structures such as gapping, depending on the relation
		for (int i = 0; i < nodes.size(); i++)
		{	//these structures can be performed on the same tree
			RSDepTreeNode node1 = (RSDepTreeNode) nodes.get(i);	//identical nodes, so identical relations
			String lb = ((MLabeledEdge) node1.getIncidentInMEdges().next()).getLabel();
			RSDepTreeNode parent = node1.getParentNode();
						
			if (lb.indexOf(SU) >= 0)		//if identical subjects, 
			{
				ReduceConjunction(lb);		//perform Conjunction-Reduction
			}
				
			if (lb.indexOf(LD) >= 0)		//if identical locatives,
			{
				RaiseRightNode(lb);			//raise the right node
			}
				
			if (lb.indexOf(MOD) >= 0)		//if identical modifiers, raise right node if
			{								//there is no other mod or ld, else reduce conjunction
				if ((parent.getChildNode(MOD) != null) || (parent.getChildNode(LD) != null))
					RaiseRightNode(lb);
				else
					ReduceConjunction(lb);
			}
			
			if ((lb.indexOf(OBJ1) >= 0) && 	//if identical obj1, only raise right node if there
				(!parent.getChildNodes(MOD).hasNext()) && 	//are no mod or ld 's
				(!parent.getChildNodes(LD).hasNext()))
			{
				RaiseRightNode(lb);
			}
			
			if (lb.indexOf(HD) >= 0)		//if identical verbs, gap
			{
				Gap(lb);
			}
		}
	}
	
	/** Creates an elliptic structure called "conjunction reduction" by ellipting 
	 *	the subject. Is feasible with all relations and all paratactic cue words
	 */
	private void ReduceConjunction(String lb) throws Exception
	{
		RSDepTreeNode intact = null;
		RSDepTreeNode adapt = null;
		//the labels of the conjuncts determine which should be ellipted
		RSDepTreeNode sat = trans.getTree().getRoot().getChildNode(CNJSATELLITE);
		RSDepTreeNode raised = trans.getTree().getRoot().getChildNode(CNJRAISEDRIGHTNODE);
		RSDepTreeNode ell = trans.getTree().getRoot().getChildNode(CNJELLIPTED);
		if (sat != null)	//if they are still marked nucleus and satellite,
		{					//adapt the nucleus and keep the satellite intact
			intact = sat;
			adapt = trans.getTree().getRoot().getChildNode(CNJNUCLEUS);
		}
		else if (ell != null)	//if one conjunct has already been ellipted, ellipt that
		{						//conjunct further, keep the other intact
			adapt = ell;
			intact = trans.getTree().getRoot().getChildNode(CNJ);
		}
		else if (raised != null)	//if one conjunct has been raised, keep it intact
		{							//and ellipt the other conjunct
			intact = raised;
			adapt = trans.getTree().getRoot().getChildNode(CNJ);
		}
		else	//should not be possible
			throw new Exception("No satellite, raised or ellipted conjuncts");
		
		adapt.getData().set(RSTreeNodeData.REL, CNJELLIPTED);
		adapt.getIncomingEdge().setLabel(CNJELLIPTED);
		if (intact.getIncomingEdge().getLabel().indexOf(RAISEDRIGHTNODE) < 0)
		{	//set the label of the intact conjunct to CNJ, the adapted to CNJELLIPTED
			intact.getData().set(RSTreeNodeData.REL, CNJ);
			intact.getIncomingEdge().setLabel(CNJ);
		}
		
		if (intact.getData().get(RSTreeNodeData.CAT).equals(CONJ))
		{	//if intact is a nested conjunction, get either the raised or the not-ellipted node
			if (intact.getChildNode(CNJRAISEDRIGHTNODE) != null)	//(which was the intact node in the nested ellipsis)
				intact = intact.getChildNode(CNJRAISEDRIGHTNODE);
			else if (intact.getChildNode(CNJELLIPTED) != null)
				intact = intact.getChildNode(CNJ);
			else	//else, no ellipsis should take place
				throw new Exception("No raised or ellipted node in embedded conjunction");
		}
		else if (adapt.getData().get(RSTreeNodeData.CAT).equals(CONJ))
		{	//if adapt is a nested conj, get the raised or not-ellipted node, and change it this time
			if (adapt.getChildNode(CNJRAISEDRIGHTNODE) != null)
				adapt = adapt.getChildNode(CNJRAISEDRIGHTNODE);
			else if (adapt.getChildNode(CNJELLIPTED) != null)
				adapt = adapt.getChildNode(CNJ);
			else	//else, no ellipsis should take place
				throw new Exception("No raised or ellipted node in embedded conjunction");
		}
		
		System.out.println("Reducing conjunction");
		Ellipt(lb, intact, adapt);	//perform the ellipsis
	}
	
	/** Creates a elliptic structure called "conjunction reduction" by ellipting 
	 *	the object, locative or modifier. Is only feasible with additive and contrast relations
	 */
	private void RaiseRightNode(String lb) throws Exception
	{
		//raising right node may only take place with additive or contrast relations, not temporal or cause
		if ((relation.indexOf(ADDITIVE) < 0) && (relation.indexOf(CONTRAST) < 0))
			return;
		
		RSDepTreeNode intact = null;
		RSDepTreeNode adapt = null;
			
		RSDepTreeNode sat = trans.getTree().getRoot().getChildNode(CNJSATELLITE);
		RSDepTreeNode raised = trans.getTree().getRoot().getChildNode(CNJRAISEDRIGHTNODE);
		RSDepTreeNode ell = trans.getTree().getRoot().getChildNode(CNJELLIPTED);
		if (sat != null)	//if they are still marked nucleus and satellite,
		{					//adapt the nucleus and keep the satellite intact
			adapt = sat;
			intact = trans.getTree().getRoot().getChildNode(CNJNUCLEUS);
		}
		else if (ell != null)	//if one conjunct has already been ellipted, ellipt that
		{						//conjunct further, keep the other intact
			intact = ell;
			adapt = trans.getTree().getRoot().getChildNode(CNJ);
		}
		else if (raised != null)	//if one conjunct has been raised, keep it intact
		{							//and ellipt the other conjunct
			adapt = raised;
			intact = trans.getTree().getRoot().getChildNode(CNJ);
		}
		else
			throw new Exception("No satellite, raised or ellipted conjuncts");
		
		//mark the changed conjunct 'raised' and the intact node 'cnj', it it is not already marked 'ellipted'
		adapt.getData().set(RSTreeNodeData.REL, CNJRAISEDRIGHTNODE);
		adapt.getIncomingEdge().setLabel(CNJRAISEDRIGHTNODE);
		if (intact.getIncomingEdge().getLabel().indexOf(ELLIPTED) < 0)
		{
			intact.getData().set(RSTreeNodeData.REL, CNJ);
			intact.getIncomingEdge().setLabel(CNJ);
		}
		
		//if intact is a embedded conjunction, find the ellipted or not-raised conjunct to keep intact
		String catIntact = intact.getData().get(RSTreeNodeData.CAT);
		String catAdapt = adapt.getData().get(RSTreeNodeData.CAT);
		if ((catIntact != null) && catIntact.equals(CONJ))
		{
			if (intact.getChildNode(CNJELLIPTED) != null)
				intact = intact.getChildNode(CNJELLIPTED);
			else if (intact.getChildNode(CNJRAISEDRIGHTNODE) != null)
				intact = intact.getChildNode(CNJ);
			else
				throw new Exception("No raised or ellipted node in embedded conjunction");
		}
		//if adapt is nested, find the ellipted or not-raised conjunct to raise this time
		else if ((catAdapt != null) && catAdapt.equals(CONJ))
		{
			if (adapt.getChildNode(CNJELLIPTED) != null)
				adapt = intact.getChildNode(CNJELLIPTED);
			else if (adapt.getChildNode(CNJRAISEDRIGHTNODE) != null)
				adapt = adapt.getChildNode(CNJ);
			else
				throw new Exception("No raised or ellipted node in embedded conjunction");
		}
		
		System.out.println("Raising right node");
		Ellipt(lb, intact, adapt);	//perform ellipsis
	}
	
	/** Creates an elliptic structure called "gapping" by ellipting the verb. 
	 *	Feasible only with additive pr contrast relations
	 */
	private void Gap(String lb) throws Exception
	{
		//gapping may only take place with additive relations, not temporal, contrast or cause
		if ((relation.indexOf(ADDITIVE) < 0) && (relation.indexOf(CONTRAST) < 0))
			return;
		
		RSDepTreeNode intact = null;
		RSDepTreeNode adapt = null;
			
		RSDepTreeNode sat = trans.getTree().getRoot().getChildNode(CNJSATELLITE);
		RSDepTreeNode raised = trans.getTree().getRoot().getChildNode(CNJRAISEDRIGHTNODE);
		RSDepTreeNode ell = trans.getTree().getRoot().getChildNode(CNJELLIPTED);
		if (sat != null)	//if they are still marked nucleus and satellite,
		{					//adapt the nucleus and keep the satellite intact
			intact = sat;
			adapt = trans.getTree().getRoot().getChildNode(CNJNUCLEUS);
		}
		else if (ell != null)	//if one conjunct has already been ellipted, ellipt that
		{						//conjunct further, keep the other intact
			adapt = ell;
			intact = trans.getTree().getRoot().getChildNode(CNJ);
		}
		else if (raised != null)	//if one conjunct has been raised, keep it intact
		{							//and ellipt the other conjunct
			intact = raised;
			adapt = trans.getTree().getRoot().getChildNode(CNJ);
		}
		else
			throw new Exception("No satellite, raised or ellipted conjuncts");
		
		//mark the ellipted conjunct ellipted, the intact one cnj (if it's not already raised)
		adapt.getData().set(RSTreeNodeData.REL, CNJELLIPTED);
		adapt.getIncomingEdge().setLabel(CNJELLIPTED);
		if (intact.getIncomingEdge().getLabel().indexOf(RAISEDRIGHTNODE) < 0)
		{
			intact.getData().set(RSTreeNodeData.REL, CNJ);
			intact.getIncomingEdge().setLabel(CNJ);
		}
		
		//if intact is nested, find the raised or not-ellipted child node to keep intact
		if (intact.getData().get(RSTreeNodeData.CAT).equals(CONJ))
		{
			if (intact.getChildNode(CNJRAISEDRIGHTNODE) != null)
				intact = intact.getChildNode(CNJRAISEDRIGHTNODE);
			else if (intact.getChildNode(CNJ) != null)
				intact = intact.getChildNode(CNJ);
			else
				throw new Exception("No raised or ellipted node in embedded conjunction");
		}
		//if adapt is nested, find the raised or not-ellipted child node to adapt
		else if (adapt.getData().get(RSTreeNodeData.CAT).equals(CONJ))
		{
			if (adapt.getChildNode(CNJRAISEDRIGHTNODE) != null)
				adapt = adapt.getChildNode(CNJRAISEDRIGHTNODE);
			else if (adapt.getChildNode(CNJ) != null)
				adapt = adapt.getChildNode(CNJ);
			else
				throw new Exception("No raised or ellipted node in embedded conjunction");
		}
		
		System.out.println("Gapping");
		Ellipt(lb, intact, adapt);	//perform ellipsis
	}
	
	/** This function ellipts the node denoted by lb from the conjunct 'adapt. 
	 *	The first conjunct is left unchanged. The parent nodes of the ellipted 
	 *	node get a connection to the twin in the intact conjunct
	 */
	private void Ellipt(String lb, RSDepTreeNode intact, RSDepTreeNode adapt)
	{		
		RSDepTreeNode shared = intact.getChildNode(lb);	//create new label for shared node
		StringBuffer sb = new StringBuffer(lb);			//replace identical by borrowed
		int idx = sb.indexOf(IDENTICAL);
		if (idx >= 0)
			sb.delete(idx, (idx + IDENTICAL.length()));
		sb.append(BORROWED);
		String newlb = sb.toString();
		
		if (shared == null)	//if intact is part of a conjunction, the node will be labeled
			shared = intact.getChildNode(newlb);	//borrowed instead of identical
		
		//remove the redundant node, and create a connection to the shared node in the first conjunct
		RSDepTreeNode elliptedNode = adapt.getChildNode(lb);

		if (elliptedNode != null)
		{
			elliptedNode.getData().set("root", "");
			elliptedNode.getData().set("pos", "");
			elliptedNode.getData().set("index", "");

			System.out.println("ellipted node");
		}
	}
	
	/** Creates an elliptic structure called "Stripping" by ellipting all elements but one, 
	 *	and the given word 'ook' (too) or 'niet' (not). Only feasible 
	 *	with additive or contrast relations
	 */
	private void Strip(RSDepTreeNode ook)
	{
		if ((relation.indexOf(CONTRAST) < 0) && (relation.indexOf(ADDITIVE) < 0))
			return;
		
		System.out.println("Stripping");
		List conjuncts = getConjuncts();
		List labels = new ArrayList();
		List nodes = getMarkedNodes();
		
		RSDepTreeNode adapt = null;
		RSDepTreeNode intact = null;	//first and only transformation, so conjuncts are still labeled nucleus and satellite
		RSDepTreeNode nucleus = trans.getTree().getRoot().getChildNode(CNJNUCLEUS);
		RSDepTreeNode satellite = trans.getTree().getRoot().getChildNode(CNJSATELLITE);
		
		//if there is a nested conjunction
		if (nucleus.getData().get(RSTreeNodeData.CAT).equals(CONJ)
			|| satellite.getData().get(RSTreeNodeData.CAT).equals(CONJ))
		{	//the conjunction should stay intact, while the smain should be adapted
			if (nucleus.getData().get(RSTreeNodeData.CAT).equals(CONJ))
			{
				intact = nucleus.getChildNode(CNJELLIPTED);
				adapt = satellite;
			}
			else
			{
				intact = satellite.getChildNode(CNJELLIPTED);
				adapt = nucleus;
			}
			
			if (ook == null)	//if ook is null, the nested relation was not stripped, 
				return;			//so this should not be stripped either 

			//create a new edge from adapt to ook
			ook.getIncomingEdge().setLabel(MOD.concat(BORROWED));
			MLabeledEdge edge = new MLabeledEdge(MOD);
			edge.setSource(adapt);
			edge.setTarget(ook);
			trans.getTree().addMEdge(edge);
		}
		else	//if there is no nested conjunction
		{
			if (ook == null)	//no 'ook' or 'niet' present, so must be created
			{
				adapt = nucleus;
				intact = satellite;
				RSTreeNodeData data = new RSTreeNodeData(MOD);
				data.set(RSTreeNodeData.POS, ADV);
				data.set(RSTreeNodeData.ROOT, "ook");
				ook = new RSDepTreeNode(data);
				MLabeledEdge edge = new MLabeledEdge(MOD);
				edge.setSource(adapt);
				edge.setTarget(ook);
				trans.getTree().addMEdge(edge);
			}
			else	//one conjunct contains 'niet'. No node or edge has to be added
			{
				if (ook.getParentNode() == satellite)
				{
					adapt = satellite;
					intact = nucleus;
				}
				else if (ook.getParentNode() == nucleus)
				{
					adapt = nucleus;
					intact = satellite;
				}
			}
		}
		//set the label of intact to CNJ, and the label of adapt to 'ellipted'
		intact.getData().set(RSTreeNodeData.REL, CNJ);		
		intact.getIncomingEdge().setLabel(CNJ);			
		adapt.getIncomingEdge().setLabel(CNJELLIPTED);
		adapt.getData().set(RSTreeNodeData.REL, CNJELLIPTED);
		
		//remove all the redundant nodes (all nodes but one) from adapt, and 
		Iterator it = adapt.getChildNodes();	//create edges to their twins in intact
		while (it.hasNext())
		{
			RSDepTreeNode node = (RSDepTreeNode) it.next();
			String lb = ((MLabeledEdge) node.getIncidentInMEdges().next()).getLabel();
			int idx = lb.indexOf(IDENTICAL);
			//all marked nodes must be removed								
			if (idx >= 0)
			{	//create the new label, pointing to the shared node
				StringBuffer sb = new StringBuffer(lb);
				sb.delete(idx, (idx + IDENTICAL.length()));
				sb.append(BORROWED);
				String newLb = sb.toString();
				//connect to the shared node - not perse identical, but with same relation
				trans.removeNode(node);
				MLabeledEdge newEdge = new MLabeledEdge(newLb);
				newEdge.setSource(adapt);
				Iterator it2 = intact.getChildNodes(lb);
				
				if (it2.hasNext())
					newEdge.setTarget((RSDepTreeNode) it2.next());
				else	//if it is a nested relation, the node is already borrowed
					newEdge.setTarget((RSDepTreeNode) intact.getChildNodes(newLb).next());
					
				trans.getTree().addMEdge(newEdge);
			}						
		}
	}
	
	/** Coordinates a single constituent when all the other nodes are identical. 
	 *	Creates a new conj-node with the same label as the unmarked node, and takes the
	 *	unmarked node and its equivalent as conjuncts.
	 *	Only feasible with additive relations
	 */
	private void CoordConstituent() throws Exception
	{
		if (relation.indexOf(ADDITIVE) < 0)
			return;
		
		System.out.println("Coordinating constituent");
		RSDepTreeNode nucleus = trans.getTree().getRoot().getChildNode(CNJNUCLEUS);
		RSDepTreeNode satellite = trans.getTree().getRoot().getChildNode(CNJSATELLITE);
		RSDepTreeNode root = null;
		RSDepTreeNode ellipt = null;
		MLabeledEdge edge = null;
		Iterator it = nucleus.getIncidentOutMEdges();
		String lb = null;
		
		while (it.hasNext())	//find the unmarked node
		{
			edge = (MLabeledEdge) it.next();
			if (edge.getLabel().indexOf(IDENTICAL) < 0)
			{
				lb = edge.getLabel();
				break;
			}
		}
		
		String cat = nucleus.getChildNode(lb).getData().get(RSTreeNodeData.CAT);
		if ((cat != null) && cat.equals(CONJ))	//if there is a nested conjunction,
		{										//the unmarked node must be added there
			root = nucleus;
			ellipt = satellite;
		}
		else
		{
			root = satellite;
			ellipt = nucleus;
		}
		if (root.getChildNode(lb) == null)		//if it's not the same label, return
		{
			System.out.println("can't coordinate constituent: must be same relation");
			return;
		}
		cat = root.getChildNode(lb).getData().get(RSTreeNodeData.CAT);
		
		trans.getTree().removeMEdge(root.getIncomingEdge());		//disconnect root from the old root
		RSDepTreeNode oldRoot = trans.getTree().getRoot();
		RSDepTreeNode crd = oldRoot.getChildNode(CRD);			//get the coordinator
		trans.getTree().removeMEdge(crd.getIncomingEdge());		//disconnect it from the old root
		trans.getTree().setRoot(root);							//set new root
		
		if (lb.equals(SU))	//if the subject is coordinated, the verb must be in plural tense
		{
			RSDepTreeNode verb = root.getChildNode(HD.concat(IDENTICAL));
			String verbLb =	verb.getData().get(RSTreeNodeData.MORPH);
			verb.getData().set(RSTreeNodeData.MORPH, verbLb.replaceAll(SINGULAR, PLURAL));
		}
		
		RSDepTreeNode conjunction = null;
		if ((cat == null) || (!cat.equals(CONJ)))	//if not nested conjunction,
		{	//create a conjunction node and connect it to root
			RSTreeNodeData data = new RSTreeNodeData(edge.getLabel());
			data.set(RSTreeNodeData.CAT, CONJ);
			conjunction = new RSDepTreeNode(data);
			edge = new MLabeledEdge(lb);
			edge.setSource(root);
			edge.setTarget(conjunction);
			trans.getTree().addMEdge(edge);
			//no coordinator present yet, so at it to the new conjunction
			MLabeledEdge newEdge = new MLabeledEdge(CRD);
			newEdge.setSource(conjunction);
			newEdge.setTarget(crd);
			trans.getTree().addMEdge(newEdge);
		}
		else //if nested conjunction, just add the node there
			conjunction = root.getChildNode(lb);
		
		MLabeledEdge newEdge1 = new MLabeledEdge(CNJ);	//add node in root to the conjunction
		newEdge1.setSource(conjunction);
		newEdge1.setTarget(root.getChildNode(lb));
		trans.getTree().addMEdge(newEdge1);
		
		MLabeledEdge remove = root.getOutgoingEdge(lb);	//remove the old edge
		trans.getTree().removeMEdge(remove);
		
		MLabeledEdge newEdge2 = new MLabeledEdge(CNJ);	//add node in ellipt to the conjunction
		newEdge2.setSource(conjunction);
		RSDepTreeNode cnj2 = ellipt.getChildNode(lb);
		trans.getTree().removeMEdge(cnj2.getIncomingEdge());
		newEdge2.setTarget(cnj2);
		trans.getTree().addMEdge(newEdge2);
		trans.removeNode(oldRoot);	//remove old root + all the children that are still attached
	}
	
	/** see RDGTransformer */
	public Iterator getResult()
	{
		List result = new ArrayList();
		result.add(trans.getTree());
		return result.iterator();
	}
}