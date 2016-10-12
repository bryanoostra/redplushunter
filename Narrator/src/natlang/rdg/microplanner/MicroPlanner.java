package natlang.rdg.microplanner;

import natlang.debug.LogFactory;
import natlang.rdg.*;
import natlang.rdg.lexicalchooser.*;
import natlang.rdg.model.*;

import java.util.*;
import java.util.logging.Logger;

import parlevink.parlegraph.model.*;

/**
 * The MicroPlanner is responsible for converting the document plan into a 
 * rhetorical dependency graph, while generating sentence plans and performing the
 * first part of the lexicalization
 * @author Nanda Slabbers
 */
public class MicroPlanner 
{
	private PlotElement pe;
	
	private RSGraph graph;
	private LexicalChooser lc;
	private RSDepTreeCreator dtc;
			
	private Vector edges;
	private String prep;
	private String svp;
	private String deplabel;
	
	private String kind;
	private String name;
	private String agens;
	private String patiens;
	private String target;
	private Instrument instrument;
			
	private PlotElement subpe;
	
	private CharacterInfo charinf;
	
	private Logger logger;
			
	/**
	 * Constructor - Initializes necessary variables
	 *
	 */
	public MicroPlanner()
	{
		edges = new Vector();
		prep = "";
		svp = "";
		deplabel = "";
		lc = new LexicalChooser();
		dtc = new RSDepTreeCreator(lc);
		charinf = CharacterInfo.getCharacterInfoObject();
		logger = LogFactory.getLogger(this);
	}
	
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	/**
	 * Starts the transformation of the document plan into a rhetorical dependency graph
	 */
	public RSGraph transform()
	{
//		Iterator it = graph.getMVertices();
//		if (it.hasNext())
//			transformDepthFirst((RSVertex) it.next());
		if (graph.getRoot()!= null)
			transformDepthFirst(graph.getRoot());						
//		System.out.println(" MICROPLANNER ");
//		Narrator.printGoed(graph.getRoot(), 0);
//		System.exit(0);
		return graph;
	}
	
	/**
	 * Transforms the document plan depth first
	 * and converts the leaf nodes into dependency trees
	 */
	public void transformDepthFirst(RSVertex v)
	{		
		if (v.getType().equals("mess"))
		{						
			pe = (PlotElement) v;			
			
			Iterator it = pe.getIncidentMEdges();
			MLabeledEdge edge = (MLabeledEdge) it.next();
			
			kind = pe.getKind();
			name = pe.getName();					
			agens = pe.getAgens();					
			patiens = pe.getPatiens();
			target = pe.getTarget();	
			instrument = pe.getInstrument();
			subpe = pe.getSubElement();
			
			logger.info(kind + " - " + name + " - " + agens + " - " + patiens + " - " + target + " - " + instrument + " - " + subpe);
			//FK2008-03-26: FIXME: Bah. Instantievariabelen worden hier gebruikt als parameters!
			//					    Aangezien het strings betreft is het niet veel minder efficient om dat te doen. 

			RSDepTreeModel tree = createTree();		
			System.out.println(edge);
			if (edge.getLabel().equals("nucleus"))
				edge.setTarget(tree);
			else if (edge.getLabel().equals("satellite"))
				edge.setSource(tree);
			
			graph.removeMVertex(v);
		}
		else if (v.getType().equals("text"))
		{
		}
		else if (v.getType().equals("rhetrel"))
		{
			RSVertex sat = ((RhetRelation) v).getSatellite();
			RSVertex nuc = ((RhetRelation) v).getNucleus();
			
			if (sat != null)
				transformDepthFirst(sat);
			if (nuc != null)
				transformDepthFirst(nuc);			
		}
	}
	
	/**
	 * Creates a dependency tree for the current plot element.
	 * Doing this it first chooses the correct template and calls the associated function. 
	 */
	public RSDepTreeModel createTree()		
	{
		edges = new Vector();

		// there are 20 different types of goals, and some of them use templates for other
		// types of plot elements, so first change the current plot element in order to select the correct template
		if (kind.endsWith("goal"))
			transformGoal();
		
		RSDepTreeNode topNode = dtc.createCatNode("top", "smain");		
		RSDepTreeModel result = new RSDepTreeModel(topNode);
		
		// add modifiers (details about the plot element's name)
		Vector mods = pe.getDetails(name);		
		for (int i=0; i<mods.size(); i++)
		{
			String mod = (String) mods.elementAt(i);
			Entry ent = lc.getEntry(mod, false);
			
			if (!kind.equals("state") || !ent.getPos().equals("adj"))
			{				
				RSDepTreeNode modNode = dtc.createAdjNode(mod, "adv", "mod", false);
				edges.addElement(createEdge("mod", topNode, modNode));
			}
		}
		
		// add discourse marker (decided by background information supplier)
		if (!pe.getDiscm().equals(""))
		{
			RSDepTreeNode modNode = dtc.createModNode(pe.getDiscm());
			edges.addElement(createEdge("mod", topNode, modNode));
		}		
						
		if (kind.equals("action"))	
		{
			if (pe.getDescr().equals("failed"))
				createFailedActionTree(topNode);
			else if (pe.getDescr().equals("passive"))
				createPassiveActionTree(topNode);
			else
				createActionTree(topNode);
		}		
		else if (kind.equals("event"))
			createActionTree(topNode);
		else if (kind.equals("goal"))
			createGoalTree(topNode);
		else if (kind.equals("perception"))
			createPerceptionTree(topNode);
		else if (kind.equals("setting"))
			createSettingTree(topNode);
		else if (kind.equals("belief"))
			createPerceptionTree(topNode);
		else if (kind.equals("state"))
		{
			if (pe.getDescr().equals("wat"))
				createWatStateTree(topNode);
			else if (pe.getDescr().equals("zo"))
				createZoStateTree(topNode);
			else
				createStateTree(topNode, false);
		}
								
		for (int i=0; i<edges.size(); i++)
		{
			MLabeledEdge tmp = (MLabeledEdge) edges.elementAt(i);
			result.addMEdge(tmp);
		}
				
		return result;
	}
	
	/**
	 * Decides how the goal should be lexicalized which depends on the type of goal
	 * (attain, sustain, avoid, leave), the type of subplotelement (action, state, object)
	 * and whether the agens of the goal is the same as the agens of the subplotelement
	 *
	 */
	public void transformGoal()
	{
		logger.severe("this is a goal: " + kind + ", " + name);
		
		if (subpe == null)
		{
			// choose the verb depending on the type of goal					// examples!
//			if (kind.equals("attaingoal"))
//				name = "have";													// hij wilde de appel hebben
//			else if (kind.equals("sustaingoal"))
//				name = "keep";													// hij wilde de appel houden
//			else if (kind.equals("leavegoal"))
//			{
//				name = "have";
//				pe.addDetail(new Detail(name, "nomore"));						// hij wilde de appel niet meer hebben
//			}
//			else if (kind.equals("avoidgoal"))
//			{
//				name = "receive";
//				pe.addDetail(new Detail(name, "not"));							// hij wilde de appel niet krijgen
//			}			
			kind = "goal";
		}			
		else if (subpe.getKind().equals("action"))
		{
			if (subpe.getAgens().equals(agens))
			{
				name = subpe.getName();
				patiens = subpe.getPatiens();
				target = subpe.getTarget();
				instrument = subpe.getInstrument();
				subpe = subpe.getSubElement();
				
				if (kind.equals("attaingoal"))
					;															// hij wilde lachen
				else if (kind.equals("sustaingoal"))
					;															// hij wilde (blijven) lachen
				else if (kind.equals("leavegoal"))
					pe.addDetail(new Detail(name, "nomore"));					// hij wilde niet meer huilen
				else if (kind.equals("avoidgoal"))				 
					pe.addDetail(new Detail(name, "not"));						// hij wilde niet (gaan) huilen
				
				kind = "goal";				
			}
			else
			{
				name = "want";
				
				if (kind.equals("attaingoal"))
					;															// hij wilde dat zij huilde (zou huilen)
				else if (kind.equals("sustaingoal"))
					;															// hij wilde dat zij huilde (zou (gaan) huilen)
				else if (kind.equals("leavegoal"))
					subpe.addDetail(new Detail(subpe.getName(), "nomore"));		// hij wilde niet meer dat zij huilde (zou huilen) OF hij wilde dat zij niet meer huilde (zou huilen)
				else if (kind.equals("avoidgoal"))
					subpe.addDetail(new Detail(subpe.getName(), "not"));		// hij wilde niet dat zou huilde (zou huilen) OF hij wilde dat zij niet huilde (zou huilen)
				
				kind = "perception";
			}
		}
		else if (subpe.getKind().equals("state"))
		{
			if (subpe.getAgens().equals(agens))
			{			
				patiens = subpe.getName();				// tsja, bij 'gelukkig' zijn, is 'gelukkig' niet echt een obj1, maar een predc (nu toch zo!)
				target = subpe.getTarget();	
				instrument = subpe.getInstrument();
				subpe = subpe.getSubElement();
				
				if (kind.equals("attaingoal"))
					name = "become";											// hij wilde gelukkig worden
				else if (kind.equals("sustaingoal"))
					name = "stay";												// hij wilde gelukkig blijven
				else if (kind.equals("leavegoal"))
				{
					name = "be";												// hij wilde niet meer bang zijn
					pe.addDetail(new Detail(name, "nomore"));
				}
				else if (kind.equals("avoidgoal"))
				{
					name = "be";												// hij wilde niet bang zijn (worden)
					pe.addDetail(new Detail(name, "not"));
				}				
				kind = "goal";				
			}
			else
			{
				name = "want";
				
				if (kind.equals("attaingoal"))
					;															// hij wilde dat zij gelukkig was (zou zijn)
				else if (kind.equals("sustaingoal"))
					;															// hij wilde dat zij gelukkig was (zou zijn)
				else if (kind.equals("leavegoal"))
					subpe.addDetail(new Detail(subpe.getName(), "nomore"));		// hij wilde niet meer dat zij gelukkig was
				else if (kind.equals("avoidgoal"))
					subpe.addDetail(new Detail(subpe.getName(), "not"));		// hij wilde niet dat zij gelukkig was
				
				kind = "perception";
			}
		}
		
		logger.severe("this is the transformed goal: " + kind + ", " + name);
		
	}
		
	/**
	 * Template for successful actions
	 * @param topNode
	 */
	public void createActionTree(RSDepTreeNode topNode)
	{		
		if (!name.equals(""))
		{				
			RSDepTreeNode actionNode = createActionNode(name);
			edges.addElement(createEdge("hd", topNode, actionNode));
		}		
		if (!svp.equals(""))
		{
			RSDepTreeNode svpNode = dtc.createSvpNode(svp);
			edges.addElement(createEdge("svp", topNode, svpNode));
		}		
		if(!agens.equals(""))
		{		
			RSDepTreeNode agensNode = createEntNode(agens, "su");			
			edges.addElement(createEdge("su", topNode, agensNode));
		}		
		if(!patiens.equals(""))
		{
			RSDepTreeNode patiensNode = createEntNode(patiens, "obj1");
			edges.addElement(createEdge("obj1", topNode, patiensNode));
		}		
		if(!target.equals(""))
		{
			RSDepTreeNode targetNode = createPpNode(prep, target, deplabel);
			edges.addElement(createEdge(deplabel, topNode, targetNode));
		}
		
		RSDepTreeNode tmpinsNode = null;
		if (instrument != null)
			tmpinsNode = createEntNode(instrument.getInstr(), "obj1");

//		boolean first = true;
//		for (int i=0; i<instruments.size(); i++)
//		{
//			String tmp = (String) instruments.elementAt(i);
//			if (first)
//			{
//				tmpinsNode = createEntNode(tmp, "obj1");
//				first = false;
//			}
//			else if (i == (instruments.size()-1))
//			{
//				RSDepTreeNode tmpNode = createEntNode(tmp, "obj1");
//				tmpinsNode = createConjNode(tmpinsNode, tmpNode);
//			}
//			else
//			{
//				RSDepTreeNode tmpNode = createEntNode(tmp, "obj1");
//				tmpinsNode = createConj2Node(tmpinsNode, tmpNode);				
//			}
//		}
		if (tmpinsNode != null)
		{
			RSDepTreeNode insNode = dtc.createCatNode("mod", "pp");
			
			RSDepTreeNode prepNode = dtc.createPrepNode(instrument.getPrep()); // was "met"
			
			edges.addElement(createEdge("hd", insNode, prepNode));
			edges.addElement(createEdge("obj1", insNode, tmpinsNode));
			edges.addElement(createEdge("mod", topNode, insNode));
		}
	}
	
	/**
	 * Template for failed actions
	 * @param topNode
	 */
	public void createFailedActionTree(RSDepTreeNode topNode)
	{
		RSDepTreeNode hdNode = createActionNode("try");
		edges.addElement(createEdge("hd", topNode, hdNode));		
		
		if (!name.equals(""))
		{		
			RSDepTreeNode infNode = dtc.createVerbNode(name, "vc", "teinf");
			edges.addElement(createEdge("vc", topNode, infNode));
			
			// mag wat netter, maar is even om deplabel, svp en prep te setten
			createActionNode(name);
		}		
		if (!svp.equals(""))
		{
			RSDepTreeNode svpNode = dtc.createSvpNode(svp);
			edges.addElement(createEdge("svp", topNode, svpNode));
		}		
		if(!agens.equals(""))
		{
			RSDepTreeNode agensNode = createEntNode(agens, "su");
			edges.addElement(createEdge("su", topNode, agensNode));
		}		
		if(!patiens.equals(""))
		{
			RSDepTreeNode patiensNode = createEntNode(patiens, "obj1");
			edges.addElement(createEdge("obj1", topNode, patiensNode));
		}		
		if(!target.equals(""))
		{
			RSDepTreeNode targetNode = createPpNode(prep, target, deplabel);
			edges.addElement(createEdge(deplabel, topNode, targetNode));
		}
	}
	
	/**
	 * Template for actions in passive voice
	 * @param topNode
	 */
	public void createPassiveActionTree(RSDepTreeNode topNode)
	{		
		if (!name.equals(""))
		{		
			// 'wordt geslagen' (in engels 'is hit', maar to be wordt al vertaald naar
			// 'zijn', dus even 'become' gekozen..
			RSDepTreeNode hdNode = createActionNode("become");
			edges.addElement(createEdge("hd", topNode, hdNode));	
			
			RSDepTreeNode perfNode = dtc.createVerbNode(name, "vc", "perf");
			edges.addElement(createEdge("vc", topNode, perfNode));
			
			//zelfde: om deplabel, svp, en prep te setten
			createActionNode(name);
		}		
		if (!svp.equals(""))
		{
			RSDepTreeNode svpNode = dtc.createSvpNode(svp);
			edges.addElement(createEdge("svp", topNode, svpNode));			
		}	
		if(!agens.equals(""))
		{
			RSDepTreeNode agensNode = createPpNode("door", agens, "predc"); 
			edges.addElement(createEdge("predc", topNode, agensNode));
		}	
		if(!patiens.equals(""))
		{
			RSDepTreeNode patiensNode = createEntNode(patiens, "su");
			edges.addElement(createEdge("su", topNode, patiensNode));
		}		
		/*if(!target.equals(""))
		{
			RSDepTreeNode targetNode = createPpNode(prep, target, deplabel);
			edges.addElement(createEdge(deplabel, topNode, targetNode));
		}
		
		RSDepTreeNode tmpinsNode = null;
		boolean first = true;
		for (int i=0; i<instruments.size(); i++)
		{
			String tmp = (String) instruments.elementAt(i);
			if (first)
			{
				tmpinsNode = createEntNode(tmp, "obj1");
				first = false;
			}
			else if (i == (instruments.size()-1))
			{
				RSDepTreeNode tmpNode = createEntNode(tmp, "obj1");
				tmpinsNode = createConjNode(tmpinsNode, tmpNode);
			}
			else
			{
				RSDepTreeNode tmpNode = createEntNode(tmp, "obj1");
				tmpinsNode = createConj2Node(tmpinsNode, tmpNode);				
			}
		}
		if (tmpinsNode != null)
		{
			RSDepTreeNode insNode = dtc.createCatNode("predc", "pp");
			RSDepTreeNode prepNode = dtc.createPrepNode("met");
			
			edges.addElement(createEdge("hd", insNode, prepNode));
			edges.addElement(createEdge("obj1", insNode, tmpinsNode));
			edges.addElement(createEdge("predc", topNode, insNode));
		}*/
	}
	
	/**
	 * Template for internal states
	 */
	public void createStateTree(RSDepTreeNode topNode, boolean addZo)
	{	
		boolean isadj = true;			// normally use 'to be', but if the name of state is a noun: use 'to have' (in order to generate 'had honger' instead of 'was hongerig')
		
		Entry tmp = lc.getEntry(name, false);
		logger.info(tmp.toString());
		if (tmp.getPos().equals("noun"))
			isadj = false;
			
		// if the name of the state is an adjective: use 'to be' (is blij), otherwise use 'to have' (heeft honger)
		RSDepTreeNode hdNode;
		if (isadj)
			hdNode = dtc.createVerbNode("be", "hd", "3.sing.past");
		else
			hdNode = dtc.createVerbNode("have", "hd", "3.sing.past");
		edges.addElement(createEdge("hd", topNode, hdNode));
						
		if (!name.equals(""))
		{				
			if (isadj)
			{
				RSDepTreeNode stateNode = createStateNode(name, "predc");
				edges.addElement(createEdge("predc", topNode, stateNode));
			}
			else
			{
				RSDepTreeNode stateNode = createStateNode(name, "obj1");
				edges.addElement(createEdge("obj1", topNode, stateNode));
			}
		}
		if(!agens.equals(""))
		{
			RSDepTreeNode agensNode = createEntNode(agens, "su"); 
			edges.addElement(createEdge("su", topNode, agensNode));
		}	
		if(!patiens.equals(""))
		{
			RSDepTreeNode patiensNode = createEntNode(patiens, "obj1");
			edges.addElement(createEdge("obj1", topNode, patiensNode));
		}	
		if(!target.equals(""))
		{
			RSDepTreeNode targetNode = createPpNode(prep, target, deplabel);
			edges.addElement(createEdge(deplabel, topNode, targetNode));
		}
	}
		
	/** 
	 * Template for goals
	 * @param topNode
	 */
	public void createGoalTree(RSDepTreeNode topNode)
	{
		logger.info("Creating a goal tree:" + name + ", " + agens + ", " + patiens + ", " + target);
		
		RSDepTreeNode hdNode = dtc.createVerbNode("want", "hd", "3.sing.past");
		edges.addElement(createEdge("hd", topNode, hdNode));
						
		if (!name.equals(""))
		{	
			RSDepTreeNode goalNode = dtc.createVerbNode(name, "vc", "inf");
			edges.addElement(createEdge("vc", topNode, goalNode));						
		}
		if(!agens.equals(""))
		{
			RSDepTreeNode agensNode = createEntNode(agens, "su");
			edges.addElement(createEdge("su", topNode, agensNode));
		}
		if(!patiens.equals(""))
		{
			RSDepTreeNode patiensNode = createEntNode(patiens, "obj1");
			edges.addElement(createEdge("obj1", topNode, patiensNode));
		}
		if(!target.equals(""))
		{
			RSDepTreeNode targetNode = createPpNode(prep, target, deplabel);
			edges.addElement(createEdge(deplabel, topNode, targetNode));
		}
	}
	
//	/** 
//	 * Template for settings
//	 * @param topNode
//	 */
//	public void createSettingTree(RSDepTreeNode topNode)
//	{
//		RSDepTreeNode erNode = dtc.createErNode();
//		edges.addElement(createEdge("sup", topNode, erNode));
//		
//		RSDepTreeNode hdNode = dtc.createVerbNode(name, "hd", "3.sing.past");
//		edges.addElement(createEdge("hd", topNode, hdNode));
//				
//		if (!agens.equals(""))
//		{
//			RSDepTreeNode agensNode = createEntNode(agens, "su");
//			edges.addElement(createEdge("su", topNode, agensNode));
//		}
//		if(!target.equals(""))
//		{
//			//mag wat netter, maar is even om deplabel, svp en prep te setten
//			createActionNode(name);
//			
//			RSDepTreeNode targetNode = createPpNode(prep, target, deplabel);
//			edges.addElement(createEdge(deplabel, topNode, targetNode));
//		}
//	}
	/** 
	 * Template for settings
	 * Aangepast door RZ - oude is boven
	 * @param topNode
	 */
	public void createSettingTree(RSDepTreeNode topNode)
	{
//		RSDepTreeNode erNode = dtc.createErNode();
//		edges.addElement(createEdge("sup", topNode, erNode));
		
		RSDepTreeNode hdNode = dtc.createVerbNode(name, "hd", "3.sing.past");
		edges.addElement(createEdge("hd", topNode, hdNode));
				
		if (!agens.equals(""))
		{
			RSDepTreeNode agensNode = createEntNode(agens, "su");
			edges.addElement(createEdge("su", topNode, agensNode));
		}
		if(!patiens.equals(""))
		{
			//mag wat netter, maar is even om deplabel, svp en prep te setten
			createActionNode(name);
			RSDepTreeNode targetNode;
			if(!prep.equals("")){
				targetNode = createPpNode(prep, patiens, deplabel);
				edges.addElement(createEdge(deplabel, topNode, targetNode));
			}
			else {
				targetNode = createEntNode(patiens, "obj1");
				edges.addElement(createEdge("obj1", topNode, targetNode));
			}
			
		}
		
	}
	
	/**
	 * Template for perceptions (and beliefs)
	 * @param topNode
	 */
	public void createPerceptionTree(RSDepTreeNode topNode)
	{				
//		String agens = subpe.getAgens(), target = subpe.getTarget(), name = subpe.getName();
		
		if(!name.equals(""))
		{
			RSDepTreeNode hdNode = dtc.createVerbNode(name, "hd", "3.sing.past");
			edges.addElement(createEdge("hd", topNode, hdNode));			
		}		
		if(!agens.equals(""))
		{
			RSDepTreeNode agensNode = createEntNode(agens, "su");
			edges.addElement(createEdge("su", topNode, agensNode));
		}		
		if (subpe == null)
		{
			if (!patiens.equals(""))
			{
				RSDepTreeNode patiensNode = createEntNode(patiens, "obj1");
				edges.addElement(createEdge("obj1", topNode, patiensNode));
			}	
		}
		else
		{
			RSDepTreeNode cpNode = dtc.createCatNode("modb", "cp");						
			RSDepTreeNode cmpNode = dtc.createCmpNode();
			RSDepTreeNode bodyNode = dtc.createCatNode("body", "ssub");
			
			edges.addElement(createEdge("modb", topNode, cpNode));
			edges.addElement(createEdge("cmp", cpNode, cmpNode));
			edges.addElement(createEdge("body", cpNode, bodyNode));
			
			kind = subpe.getKind();
			name = subpe.getName();
			agens = subpe.getAgens();			
			patiens = subpe.getPatiens();
			target = subpe.getTarget();
			pe = subpe;
			subpe = subpe.getSubElement();	
			
			// add modifiers (details about the plot element's name)
			Vector mods = pe.getDetails(name);		
			logger.fine("add modifiers sub plot element: " + name + " - " + mods);
			for (int i=0; i<mods.size(); i++)
			{
				String mod = (String) mods.elementAt(i);
				Entry ent = lc.getEntry(mod, false);
				
				if (!kind.equals("state") || !ent.getPos().equals("adj"))
				{				
					RSDepTreeNode modNode = dtc.createAdjNode(mod, "adv", "mod", false);
					edges.addElement(createEdge("mod", bodyNode, modNode));
				}
			}
			
			// add discourse marker (decided by background information supplier)
			if (!pe.getDiscm().equals(""))
			{
				RSDepTreeNode modNode = dtc.createModNode(pe.getDiscm());
				edges.addElement(createEdge("mod", bodyNode, modNode));
			}	
							
			if (kind.equals("action"))			
			{
				if (pe.getDescr().equals("failed"))
					createFailedActionTree(bodyNode);
				else if (pe.getDescr().equals("passive"))
					createPassiveActionTree(bodyNode);
				else
					createActionTree(bodyNode);
			}
			else if (kind.equals("event"))
				createActionTree(bodyNode);
			else if (kind.equals("goal"))
				createGoalTree(bodyNode);
			else if (kind.equals("perception"))
				createPerceptionTree(bodyNode);
			else if (kind.equals("belief"))
				createPerceptionTree(bodyNode);
			else if (kind.equals("reason"))
				createActionTree(bodyNode);
			else if (kind.equals("setting"))
				createSettingTree(bodyNode);
			else if (kind.equals("state"))
			{
				if (pe.getDescr().equals("wat"))
					createWatStateTree(bodyNode);
				else if (pe.getDescr().equals("zo"))
					createZoStateTree(bodyNode);
				else
					createStateTree(bodyNode, false);
			}
		}
	}	
		
	/**
	 * Creates node for verb of an action and retrieves possible preps, svps 
	 * and deplabels from lexicon
	 */
	public RSDepTreeNode createActionNode(String action)
	{
		Entry ent = lc.getEntry(action, false);
			
		if (ent != null)
		{
			prep = ent.getPrep();
			svp = ent.getSvp();
			deplabel = ent.getDeplabel();
			
			return dtc.createVerbNode(action, "hd", "3.sing.past");			
		}
			
		prep = "";
		svp = "";
		deplabel = "";
		
		return dtc.createNullNode("hd");
	}
		
	/**
	 * Creates pp node with two children: prep-node and entity-node
	 */
	public RSDepTreeNode createPpNode(String prep, String entity, String label)
	{		
		if (!prep.equals(""))
		{				
			RSDepTreeNode ppNode = dtc.createCatNode(label, "pp");	
			RSDepTreeNode prepNode = dtc.createPrepNode(prep);														
			RSDepTreeNode entNode = createEntNode(entity, "obj1");
			
			edges.addElement(createEdge("hd", ppNode, prepNode));
			edges.addElement(createEdge("obj1", ppNode, entNode));
			
			return ppNode;
		}			
		else
		{
			RSDepTreeNode entNode = createEntNode(entity, label);			
			return entNode;
		}						
	}
	
	/**
	 * Creates adj-node and retrieve prep and deplabel from lexicon
	 */
	public RSDepTreeNode createStateNode(String state, String label)
	{
		Entry ent = lc.getEntry(state, false);
		
		if (ent != null)
		{			
			prep = ent.getPrep();
			deplabel = "pc";				
			
			return dtc.createAdjNode(state, "adj", label, false);	
		}
		
		prep = "";
		deplabel = "";
		
		return dtc.createNullNode(label);
	}
	
	/**
	 * Creates conjnode with three children: node1, node2 and 'en'-node
	 */
	public RSDepTreeNode createConjNode(RSDepTreeNode node1, RSDepTreeNode node2)
	{
		RSDepTreeNode result = dtc.createCatNode("mod", "conj");		
		RSDepTreeNode crdNode = dtc.createCrdNode();
		
		edges.addElement(createEdge("crd", result, crdNode));
		edges.addElement(createEdge("cnj", result, node1));
		edges.addElement(createEdge("cnj", result, node2));
		
		return result;
	}	
	
	/**
	 * Creates conjnode with three children: node1, node2 and ','-node
	 */
	public RSDepTreeNode createConj2Node(RSDepTreeNode node1, RSDepTreeNode node2)
	{
		RSDepTreeNode result = dtc.createCatNode("mod", "conj");		
		RSDepTreeNode crdNode = dtc.createCommaNode();
		
		edges.addElement(createEdge("crd", result, crdNode));
		edges.addElement(createEdge("cnj", result, node1));
		edges.addElement(createEdge("cnj", result, node2));
		
		return result;
	}	
	
	/**
	 * Creates an edge with label and source and target nodes
	 */
	public MLabeledEdge createEdge(String label, RSDepTreeNode src, RSDepTreeNode tgt) 
	{
		MLabeledEdge edge = new MLabeledEdge(label);
		edge.setSource(src);
		edge.setTarget(tgt);
		
		return edge;
	}	

	/**
	 * Creates a state tree for a state described as 'ze was nog nooit zo blij geweest'
	 * @param topNode
	 */
	public void createZoStateTree(RSDepTreeNode topNode)
	{
		topNode.getData().set("cat", "excl");
		
		RSTreeNodeData d = new RSTreeNodeData("top");
		d.set("cat", "smain");
		RSDepTreeNode node = new RSDepTreeNode(d);
		edges.addElement(createEdge("top", topNode, node));
		
		d = new RSTreeNodeData("mod");
		d.set("cat", "ap");
		RSDepTreeNode node1 = new RSDepTreeNode(d);
		edges.addElement(createEdge("mod", node, node1));
		
		d = new RSTreeNodeData("mod");
		d.set("pos", "adv");
		d.set("root", "nog");		
		RSDepTreeNode node2 = new RSDepTreeNode(d);
		edges.addElement(createEdge("mod", node1, node2));
		
		d = new RSTreeNodeData("hd");
		d.set("pos", "adv");
		d.set("root", "nooit");		
		RSDepTreeNode node3 = new RSDepTreeNode(d);
		edges.addElement(createEdge("hd", node1, node3));
		
		d = new RSTreeNodeData("vc");
		d.set("pos", "verb");
		d.set("root", "ben");
		d.set("morph", "3.sing.perfect");
		RSDepTreeNode node4 = new RSDepTreeNode(d);
		edges.addElement(createEdge("vc", node, node4));
				
		createStateTree(node, true);
	}
	
	/**
	 * Creates a state tree for a state described as 'wat was ze blij'
	 * @param topNode
	 */
	public void createWatStateTree(RSDepTreeNode topNode)
	{
		topNode.getData().set("morph", "excl");
		
		RSTreeNodeData d = new RSTreeNodeData("mod");
		d.set("pos", "adv");
		d.set("root", "wat");		
		RSDepTreeNode watNode = new RSDepTreeNode(d);
		edges.addElement(createEdge("mod", topNode, watNode));
				
		createStateTree(topNode, false);
	}
	
	public RSDepTreeNode createEntNode(String ent, String lb)
	{
		RSDepTreeNode entNode;
		
		// checks if an apposition should be added, which is the case if the details
		// Vector contains a detail for the entity that is an object instead of an adjective
		
		String app = "";

		if (pe.getDetails(ent).size() > 0)
		{
			String detail = (String) pe.getDetails(ent).elementAt(0);
			if (charinf.isEntity(detail))
				app = detail;
		}
		
		if (!app.equals(""))
		{
			entNode = dtc.createCatNode(lb, "np");
			Vector tmpdets = pe.getDetails(ent);				
			tmpdets.remove(app);

			RSDepTreeNode hdNode = dtc.createEntNode(ent, "hd", tmpdets);
			RSDepTreeNode appNode = dtc.createEntNode(app, "modb", pe.getDetails(app));
			
			edges.addElement(createEdge("hd", entNode, hdNode));
			edges.addElement(createEdge("modb", entNode, appNode));
		}
		else
			entNode = dtc.createEntNode(ent, lb, pe.getDetails(ent));
		
		return entNode;
	}
	
	/**
	 * Returns the lexical chooser used in the microplanner
	 */
	public LexicalChooser getLexicalChooser()
	{
		return lc;
	}
}