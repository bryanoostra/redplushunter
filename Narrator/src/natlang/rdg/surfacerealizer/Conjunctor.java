package natlang.rdg.surfacerealizer;

import natlang.rdg.lexicalchooser.*;
import natlang.rdg.libraries.*;
import natlang.rdg.model.*;
import parlevink.parlegraph.model.*;

import java.util.*;


/** At the moment this class creates a grammatical Dependency Tree out of a relation 
 *	and a appropriate cue word. 
 *	@author Feikje Hielkema (modified by Nanda Slabbers)
 *	@version 1.0
 */

public class Conjunctor implements RDGTransformer, LibraryConstants
{
	RSDepTreeNode cueWord;		//the cue word
	RhetRelation relation;		//the relation to be processed
	List result = new ArrayList();	//resulting Dependency Trees
	CharacterInfo chars;
	BeVerbs bv;
	
	/** Constructs a Conjunctor, which will try to construe one or two grammatical 
	 *	Dependency Trees out of the given RhetRelation, with the given cue word
	 *	@param rel the relation
	 *	@param node the cue word
	 */
	public Conjunctor(RhetRelation rel, RSDepTreeNode node)
	{
		cueWord = node;
		relation = rel;
		chars = CharacterInfo.getCharacterInfoObject();
		bv = new BeVerbs();
	}
	
	/** Checks whether the given cue word node is a coordinator, complementer or
	 *	modifier
	 */
	public boolean check()
	{
		if (isACoordinator())
			return true;
		if (isAComplementer())
			return true;
		if (isAModifier())
			return true;
		if (isARelative())
			return true;
		
		return false;
	}
	
	/**	@see RDGTransformer
	 */
	public Iterator<RSDepTreeModel> getResult()
	{
		return result.iterator();
	}
		
	/** Checks whether the given cue word node is a coordinator, and whether its 
	 *	parent node has two children which are conjuncts and have the category SMAIN or CONJ
	 */
	private boolean isACoordinator()
	{
		if (cueWord.getLabel() != VG)		//cue word is a coordinator
			return false;
		
		RSDepTreeModel nucleus = (RSDepTreeModel) relation.getNucleus();
		String cat1 = nucleus.getRoot().getLabel();	//two equal categories
		RSDepTreeModel satellite = (RSDepTreeModel) relation.getSatellite();
		if (satellite == null)		//in that case it can only be modified
			return false;
		String cat2 = satellite.getRoot().getLabel();
		
		if (!(cat1.equals(SMAIN) || cat1.equals(CONJ)))
			return false;
		if (!(cat2.equals(SMAIN) || cat2.equals(CONJ)))
			return false;
		
		System.out.println("should be coordinated");
		return true;
	}

	/** Checks whether the cue word node is a complementer and whether the relation
	 *	consists of two smain's
	 */
	private boolean isAComplementer()
	{
		if (!cueWord.getLabel().equals(COMP))
			return false;

		RSDepTreeModel nucleus = (RSDepTreeModel) relation.getNucleus();
		RSDepTreeModel satellite = (RSDepTreeModel) relation.getSatellite();
		if ((nucleus == null) || (satellite == null))
			return false;

		String nLb = nucleus.getRoot().getLabel();
		String sLb = satellite.getRoot().getLabel();
		
		if (!(nLb.equals(SMAIN) || nLb.equals(CONJ)))
			return false;
		
		if (!(sLb.equals(SMAIN) || sLb.equals(CONJ)))
			return false;
		
		System.out.println("should be complemented");
		return true;
	}
	
	/** Checks whether the cue word node is a modifier and whether both dependency
	 *	trees in the relation are null
	 */
	private boolean isAModifier()
	{
		if (cueWord.getData().get(REL).equals(MOD))
		{
			if ((relation.getNucleus() != null) || (relation.getSatellite() != null))
			{
				System.out.println("should be modified");
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isARelative()
	{
		if (relation.getLabel().startsWith("rel"))
		{
			if ((relation.getNucleus() != null) || (relation.getSatellite() != null))
			{
				System.out.println("should be made into a relative clause");
				return true;
			}
		}
		
		return false;
	}
	
	/**	Transforms the RSDepTreeModel, by coordinating or complementing it, 
	 *	depending on the cue word that was given
	 */
	public boolean transform()
	{
		try
		{
			if (isACoordinator())
			{
				coordinate();
				changeTense();
				return true;
			}
			else if (isAComplementer())
			{
				complement();
				changeTense();
				return true;
			}
			else if (isAModifier())
			{
				modify();
				changeTense();
				return true;
			}
			else if (isARelative())
			{
				relate();
				return true;
			}
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/** Coordinates two equal categories. Later on perhaps check whether 
	 *	coordination could also take place at a lower level or something?
	 *	@throws Exception (see CueWordLib.addToNucleus())
	 */
	private void coordinate()
	{
		//create a new Dependency Tree
		RSTreeNodeData data = new RSTreeNodeData(TOP);
		data.set(RSTreeNodeData.CAT, CONJ);
		RSDepTreeNode top = new RSDepTreeNode(data);
		RSDepTreeModel tree = new RSDepTreeModel(top);
		
		//add the cue word
		MLabeledEdge edge = new MLabeledEdge(cueWord.getData().get(REL));
		edge.setSource(top);
		edge.setTarget(cueWord);
		
		//add an edge from the top to the nucleus
		MLabeledEdge edgeN = new MLabeledEdge(CNJNUCLEUS);
		edgeN.setSource(top);
		RSDepTreeModel nucleus;
		if (cueWord.getData().get(RSTreeNodeData.ROOT).equals("want"))
			nucleus = (RSDepTreeModel) relation.getSatellite();
		else
			nucleus = (RSDepTreeModel) relation.getNucleus();
		edgeN.setTarget(nucleus.getRoot());	//nog niet toevoegen aan tree, want target zit nog in nucleus
				
		//add an edge from the top to the satellite
		MLabeledEdge edgeS = new MLabeledEdge(CNJSATELLITE);
		edgeS.setSource(top);
		RSDepTreeModel satellite;
		if (cueWord.getData().get(RSTreeNodeData.ROOT).equals("want"))
			satellite = (RSDepTreeModel) relation.getNucleus();
		else
			satellite = (RSDepTreeModel) relation.getSatellite();
		edgeS.setTarget(satellite.getRoot());	
				
		//decompose the nucleus and satellite, and copy all its elements to tree
		List<MComponent> nodes = nucleus.decompose();
		for (int i = 0; i < nodes.size(); i++)
			tree.addMComponent((MComponent)nodes.get(i));
			
		nodes = satellite.decompose();
		for (int i = 0; i < nodes.size(); i++)
			tree.addMComponent((MComponent)nodes.get(i));
			
		//nu alles toevoegen aan tree
		tree.addMEdge(edgeN);
		tree.addMEdge(edgeS);
		RSDepTreeNode target = (RSDepTreeNode) edgeN.getTarget();
		target.getData().set(RSTreeNodeData.REL, CNJNUCLEUS);
		target = (RSDepTreeNode) edgeS.getTarget();
		target.getData().set(RSTreeNodeData.REL, CNJSATELLITE);
		tree.addMEdge(edge);
		
		result.add(tree);
		System.out.println("coordinated");
	}
				
	
	/**	Transforms the RSDepTreeModel by putting the complementing cue word in 
	 *	its right place, and adjusting the rest of the tree as well
	 *	@throws Exception (see CueWordLib.addToNucleus())
	 */
	private void complement() throws Exception
	{
		CueWordLib lib = new CueWordLib();
		CueWordLib library = lib.getAppropriateLib(relation.getLabel());
		RSDepTreeModel head, antecedent;
		boolean startsub = false;
		
		//check whether cue word needs to be added to nucleus or satellite
		if (library.addToNucleus(cueWord))
		{
			head = (RSDepTreeModel) relation.getSatellite();
			antecedent = (RSDepTreeModel) relation.getNucleus();
		}
		else
		{
			head = (RSDepTreeModel) relation.getNucleus();
			antecedent = (RSDepTreeModel) relation.getSatellite();
			startsub = true;
		}
						
		//create a cp-node and connect it to the root of head
		RSDepTreeNode root = head.getRoot();
		RSTreeNodeData cpData;
		if (startsub)
			cpData = new RSTreeNodeData(MODA);
		else
			cpData = new RSTreeNodeData(MODB);
		
		cpData.set(RSTreeNodeData.CAT, CP);
		RSDepTreeNode cp = new RSDepTreeNode(cpData);
	
		MLabeledEdge edge;		
		if (startsub)
			edge = new MLabeledEdge(MODA);
		else 
			edge = new MLabeledEdge(MODB);
		edge.setSource(root);
		edge.setTarget(cp);
		head.addMEdge(edge);		
				
		if (relation.getLabel().startsWith("cause-such") && cueWord.getData().get("root").equals("dat"))
		{
			RSDepTreeNode zoNode = createZoNode();
			RSDepTreeNode apNode = createApNode();
			RSDepTreeNode adjNode = null;
			
			Iterator<MComponent> nodes = head.getMComponents();

			while (nodes.hasNext())
			{
				MComponent comp = (MComponent) nodes.next();
	
				if (comp.getClass().getName().equals("natlang.rdg.model.RSDepTreeNode"))
				{
					if (((RSDepTreeNode) comp).getData().get("rel").equals("predc"))
						adjNode = (RSDepTreeNode) comp;
				}
			}
			
			nodes = head.getMComponents();
			
			while (nodes.hasNext())
			{
				MComponent comp = (MComponent) nodes.next();
				
				if (comp.getClass().getName().equals("parlevink.parlegraph.model.MLabeledEdge"))
				{
					MLabeledEdge e = (MLabeledEdge) comp;
					String label = e.getLabel();
					if ((label != null) && label.equals(PREDC))
					{
						head.addMEdge(createEdge("predc", adjNode.getParentNode(), apNode));
						head.removeMVertex(adjNode);
						head.addMEdge(createEdge("mod", apNode, zoNode));
						head.addMEdge(createEdge("hd", apNode, adjNode));
						break;						
					}		
				}
			}
		}

		String lb = cueWord.getData().get(RSTreeNodeData.REL);
		edge = new MLabeledEdge(lb);
		edge.setSource(cp);
		edge.setTarget(cueWord);
		head.addMEdge(edge);
		
		root = antecedent.getRoot();
		String antCat = root.getData().get(RSTreeNodeData.CAT);
		edge = new MLabeledEdge(BODY);
		edge.setSource(cp);
		edge.setTarget(root);
		
		if (!antCat.equals(CONJ))
			root.getData().set(RSTreeNodeData.CAT, SSUB);
							
		List<MComponent> nodes = antecedent.decompose();
		for (int i = 0; i < nodes.size(); i++)
		{
			MComponent comp = (MComponent) nodes.get(i);
			
			if (antCat.equals(CONJ) && comp.getClass().getName().equals("natlang.rdg.model.RSDepTreeNode"))
			{
				String cat = ((RSDepTreeNode) comp).getData().get(RSTreeNodeData.CAT);
				if ((cat != null) && cat.equals(SMAIN))
					((RSDepTreeNode) comp).getData().set(RSTreeNodeData.CAT, SSUB);
			}
			head.addMComponent(comp);
		}
		head.addMEdge(edge);
		
		//if cue-word is 'om', the tree needs to be adapted some more
		if (cueWord.getData().get(RSTreeNodeData.ROOT) == "om")	
			adaptRelwithOm(head);

		result.add(head);

		System.out.println("complemented");
	}
		
	/**	If the given cue word is a modifier, this function adds it to the appropriate
	 *	RSDepTreeModel from the given RhetRelation, and returns both RSDepTreeModels 
	 *	from the Relation
	 *	@throws Exception when the cue word is unknown
	 */
	private void modify() throws Exception
	{
		CueWordLib lib = new CueWordLib();
		CueWordLib library = lib.getAppropriateLib(relation.getLabel());
		
		if (library.addToNucleus(cueWord))
		{
			System.out.println("add to nucleus: " + cueWord.getData().get("root"));
			DepTreeTransformer trans = new DepTreeTransformer((RSDepTreeModel) relation.getNucleus());
			if (trans.addNodeAtRoot(cueWord))
				result.add(trans.getTree());
		}
		
		else if (library.addToSatellite(cueWord))
		{
			System.out.println("add to satellite: " + cueWord.getData().get("root"));
			DepTreeTransformer trans = new DepTreeTransformer((RSDepTreeModel) relation.getSatellite());
			if (trans.addNodeAtRoot(cueWord))
				result.add(trans.getTree());
		}
		
		else
			throw new Exception("unfamiliar cue word");
		
		System.out.println("modified");
	}
	
	private List<RSDepTreeNode> getNodes(RSDepTreeModel m)
	{
		List<RSDepTreeNode> result = new ArrayList<RSDepTreeNode>();
		
		Iterator<MComponent> it = m.getMComponents();
		while (it.hasNext())
		{
			MComponent mc = (MComponent) it.next();
			if (mc.getClass().getName().equals("natlang.rdg.model.RSDepTreeNode"))
				result.add((RSDepTreeNode) mc);
		}
		
		return result;
	}
	
	/**
	 * If the dependency trees are connected by an elaboration relation, the trees
	 * are combined into one by searching for equal entity nodes and copying the
	 * second tree into the first one as a relative clause
	 * @throws Exception
	 */
	private void relate() throws Exception
	{			
		CueWordLib lib = new CueWordLib();
		CueWordLib library = lib.getAppropriateLib(relation.getLabel());
		RSDepTreeModel head;
		RSDepTreeModel antecedent;
		
		if (library.addToNucleus(cueWord))
		{
			head = (RSDepTreeModel) relation.getSatellite();
			antecedent = (RSDepTreeModel) relation.getNucleus();
		}
		else
		{
			head = (RSDepTreeModel) relation.getNucleus();
			antecedent = (RSDepTreeModel) relation.getSatellite();
		}
			
		String entity = "";
		String mainid = "";
		String subid = "";
		List<RSDepTreeNode> main = getNodes(head);
		List<RSDepTreeNode> sub = getNodes(antecedent);
				
		// check which nodes are the same
		for (int i=0; i<main.size(); i++)
		{
			RSDepTreeNode n = (RSDepTreeNode) main.get(i);
			RSTreeNodeData data = n.getData();
			
			if (data.get("pos") != null && data.get("pos").equals("ent"))
			{
				String root = data.get("root");
				String id = n.getID();				
														
				for (int j=0; j<sub.size(); j++)
				{
					RSDepTreeNode n2 = (RSDepTreeNode) sub.get(j);
					RSTreeNodeData datasv = n2.getData();

					if (datasv.get("pos") != null && datasv.get("pos").equals("ent"))
					{						
						String sroot = datasv.get("root");
						String sid = n2.getID();
															
						if (sroot.equals(root))
						{
							mainid = id;
							subid = sid;
							entity = root;
						}
					}
				}
			}					
		}		
		
		RSDepTreeNode mnode = (RSDepTreeNode) head.getMVertex(mainid);
		RSDepTreeNode mparnode = mnode.getParentNode();
		RSDepTreeNode snode = (RSDepTreeNode) antecedent.getMVertex(subid);
		
		String mainrel = mnode.getData().get("rel");
		String prep = "";		
		String topid = snode.getID();
				
		while (snode != null && !snode.getData().get("rel").equals("top"))
		{			
			if (snode.getData().get("cat") != null && snode.getData().get("cat").equals("pp"))
				prep = snode.getChildNode("hd").getData().get("root");
			topid = snode.getID();
			snode = snode.getParentNode();
		}
		
		// create a node for the relative pronoun (possibly combined with a preposition)
		RSDepTreeNode betr = cueWord;
		String gender = chars.getGender(entity);
								
		if (!prep.equals(""))
		{
			if (gender.equals("female") || gender.equals("male"))
			{
				RSTreeNodeData data = new RSTreeNodeData("rhd");
				data.set("cat", "pp");
				
				betr = new RSDepTreeNode(data);
				
				data = new RSTreeNodeData("hd");
				data.set("pos", "prep");
				data.set("root", prep);
				
				RSDepTreeNode hdNode = new RSDepTreeNode(data);
				
				data = new RSTreeNodeData("obj1");
				data.set("pos", "adv");
				data.set("root", "wie");
				
				RSDepTreeNode advNode = new RSDepTreeNode(data);
	
				head.addMEdge(createEdge("hd", betr, hdNode));
				head.addMEdge(createEdge("obj1", betr, advNode));
			}
			else if (gender.equals("neutral"))
			{
				String relpr = "waar" + prep;
				betr.getData().set("root", relpr);
			}
			else if (gender.equals("place"))
			{
				betr.getData().set("root", "waar");
			}
		}
		
		System.out.println("relative pronoun: " + betr.getData().get("root"));
		
		List ids = getAllIds(topid, antecedent, new ArrayList());
												
		RSDepTreeNode srelNode = createSrelNode();				
		RSDepTreeNode relNode = createNpNode(mainrel);
		
		head.removeMVertex(mnode);
		head.removeMEdge(mparnode.getOutgoingEdge(mainrel));
		
		head.addMEdge(createEdge(mainrel, mparnode, relNode));
		head.addMEdge(createEdge("hd", relNode, mnode));
		head.addMEdge(createEdge("modb", relNode, srelNode));		
		head.addMEdge(createEdge("rhd", srelNode, betr));
	
		// copy the nodes from the second tree that are not related to the entity being turned into a relative pronoun, into a relative clause node
		List<MComponent> cmps = antecedent.decompose();		
		for (int i=0; i<cmps.size(); i++)
		{
			MComponent comp = (MComponent) cmps.get(i);
						
			if (comp.getClass().getName().equals("parlevink.parlegraph.model.MLabeledEdge"))
			{				
				RSDepTreeNode src = (RSDepTreeNode) ((MLabeledEdge) comp).getSource();
				RSDepTreeNode tgt = (RSDepTreeNode) ((MLabeledEdge) comp).getTarget();				
										
				String rel = ((MLabeledEdge) comp).getLabel();
				if (src.getData().get("rel").equals("top"))
				{
					if ((rel != null) && !tgt.getID().equals(topid))
						head.addMEdge(createEdge(rel, srelNode, tgt));
				}
				else
				{
					if (!appearsIn(ids, tgt.getID()))
						head.addMEdge(createEdge(rel, src, tgt));
				}
			}
		}
						
		result.add(head);
		System.out.println("related");
	}

	/** This function is called when the selected cue word was 'om'. In this case,
	 *	the satellite needs to be adapted and the complementary structure is a oti,
	 *	not a cp. Therefore it needs a separate function. This function can only be
	 *	called from complementer!
	 */
	private void adaptRelwithOm(RSDepTreeModel tree)	
	{
		if (cueWord.getData().get(RSTreeNodeData.ROOT) != "om")
			return;
		
		DepTreeTransformer trans = new DepTreeTransformer(tree);
		
		System.out.println("adaptRelWithOm");
		//change the syntactic categories of the CP and BODY nodes to OTI and TI
		RSDepTreeNode node = trans.getTree().getRoot().getChildNode(MODA, CP);
		//System.out.println("Node: " + node);
		node.getData().set(RSTreeNodeData.CAT, OTI);
		RSDepTreeNode body = node.getChildNode(BODY);
		RSDepTreeNode source = node;
		
		List<RSDepTreeNode> adapt = new ArrayList<RSDepTreeNode>();
		if (body.getData().get(RSTreeNodeData.CAT).equals(CONJ))
		{
			Iterator<MEdge> it = body.getIncidentOutMEdges();
			while (it.hasNext())
			{
				MLabeledEdge edge = (MLabeledEdge) it.next();
				if (edge.getLabel().indexOf(CNJ) >= 0)
				{
					RSDepTreeNode target = (RSDepTreeNode) edge.getTarget();
					adapt.add(target.getChildNode(VC));
				}
			}
			source = body;
		}
		else
			adapt.add(body.getChildNode(VC));
		
		for (int i = 0; i < adapt.size(); i++)
		{
			RSDepTreeNode newBody = (RSDepTreeNode) adapt.get(i);
			newBody.getData().set(RSTreeNodeData.REL, BODY);
			trans.setCurrent(source);
			trans.addNodeAtCurrent(newBody);
									
			RSDepTreeNode temp = source.getChildNode(BODY);
			MLabeledEdge edge = temp.getOutgoingEdge(VC);
			trans.getTree().removeMEdge(edge);
			trans.removeNode(temp);
			
			while (newBody.getChildNode(VC) != null)
				newBody = newBody.getChildNode(VC);
			
			newBody.getData().set(RSTreeNodeData.CAT, TI);
			RSDepTreeNode child = newBody.getChildNode(HD);
			child.getData().set(RSTreeNodeData.POS, VERB);
			child.getData().set(RSTreeNodeData.MORPH, TEINF);
			
			Iterator<MEdge> it = newBody.getIncidentOutMEdges();
			while (it.hasNext())
			{
				edge = (MLabeledEdge) it.next();
				String lb = edge.getLabel();
				if ((lb.indexOf(BORROWED) >= 0) && (lb.indexOf(SU) < 0) && (lb.indexOf(HD) < 0))
					edge.setLabel(lb.replaceAll(BORROWED, ""));
			}
		}
		tree = trans.getTree();
	}
	
	/** This function changes the tense of the main verb or adds a helper verb, 
	 *	if necessary
	 *	@throws Exception */
	private void changeTense() throws Exception
	{
		String cat = relation.getLabel();	//only needs to be done for temporal and cause relations
		if (cat.indexOf(TEMPORAL) < 0)
			return;
				
		boolean perfect = false;
		boolean toen = false;
		if (cat.indexOf(TemporalLib.SEQUENCE) >= 0)	//sequence gets told in same time
		{
			if (cueWord.getData().get(RSTreeNodeData.ROOT).equals("nadat"))
				perfect = true;
			else if (cueWord.getData().get(RSTreeNodeData.ROOT).equals("vervolgens"))
				toen = true;
			else
				return;
		}
		
		RSDepTreeNode verb = null;
		RSDepTreeNode verb2 = null;
		RSDepTreeNode source = null;
		int cntr = -1;
		
		for (int i = 0; i < result.size(); i++)
		{
			RSDepTreeModel tree = (RSDepTreeModel) result.get(i);
			if (tree.containsMComponent(cueWord))
			{
				source = tree.getRoot();
				if (cueWord.getData().get(RSTreeNodeData.POS).equals(COMP))	
				{
					if (tree.getRoot().getChildNode(MODB, CP) != null)
						source = tree.getRoot().getChildNode(MODB, CP).getChildNode(BODY);
					else					
						source = tree.getRoot().getChildNode(MODA, CP).getChildNode(BODY);
				}
				verb = source.getChildNode(HD);
				verb2 = source.getChildNode(VC);
				cntr = i;
				break;
			}
		}
		
		if (cntr < 0) 
			return;
		
		String oldMorph = verb.getData().get(RSTreeNodeData.MORPH);
		StringBuffer newMorph = new StringBuffer(oldMorph);
		if (toen)
		{
			return;
		}	
		else if ((cat.indexOf(TemporalLib.BEFORE) >= 0) && (source.getChildNode(VC) == null))
		{
			int idx = oldMorph.indexOf(PRESENT);
			if (idx >= 0)
			{
				newMorph.replace(idx, (idx + PRESENT.length()), PAST);
				verb.getData().set(RSTreeNodeData.MORPH, newMorph.toString());
			}
			return;
		}
		
		else if ((cat.indexOf(TemporalLib.AFTER) >= 0) || perfect)
		{	// if the sentence is in passive voice there already is a vc node, so in that
			// case the verb node should remain a hd-node, but the root should be set to 'be'
			if (verb2 != null)
			{
				verb.getData().set(RSTreeNodeData.ROOT, "ben");
			}
			//future and perfect tense need an auxiliary verb (de prins zal ooit gruwelijk wraak nemen)
			else
			{
				verb.getData().set(RSTreeNodeData.REL, VC);
				verb.getIncomingEdge().setLabel(VC);
				
				RSTreeNodeData data = new RSTreeNodeData(HD);
				data.set(RSTreeNodeData.POS, VERB);
				if (perfect)
				{
					if (bv.useBe(verb.getData().get("root")))
						data.set(RSTreeNodeData.ROOT, "ben");
					else
						data.set(RSTreeNodeData.ROOT, "heb");
					verb.getData().set(RSTreeNodeData.MORPH, PERFECT);
				}
				else
				{
					data.set(RSTreeNodeData.ROOT, "zal");
					verb.getData().set(RSTreeNodeData.MORPH, INF);
				}
				data.set(RSTreeNodeData.MORPH, oldMorph);
				RSDepTreeNode head = new RSDepTreeNode(data);
				
				MLabeledEdge edge = new MLabeledEdge(HD);
				edge.setSource(source);
				edge.setTarget(head);
				verb.getMGraph().addMEdge(edge);
			}
		}
		
		result.set(cntr, verb.getMGraph());	
		System.out.println("changed verb tense");	
	}
	
	public MLabeledEdge createEdge(String label, RSDepTreeNode src, RSDepTreeNode tgt) 
	{
		MLabeledEdge edge = new MLabeledEdge(label);
		edge.setSource(src);
		edge.setTarget(tgt);
		
		return edge;
	}	
	
	public RSDepTreeNode createSrelNode()
	{
		RSTreeNodeData data = new RSTreeNodeData("modb");
		data.set("cat", "rel");
				
		return new RSDepTreeNode(data);
	}

	public RSDepTreeNode createZoNode()
	{
		RSTreeNodeData data = new RSTreeNodeData("mod");
		data.set(RSTreeNodeData.ROOT, "zo");
		data.set(RSTreeNodeData.POS, "adv");
		return new RSDepTreeNode(data);
	}
	
	public RSDepTreeNode createApNode()
	{
		RSTreeNodeData apData = new RSTreeNodeData("mod");
		apData.set("cat", "ap");
		
		return new RSDepTreeNode(apData);
	}
	
	public RSDepTreeNode createHdNode(String action)
	{				
		RSTreeNodeData data = new RSTreeNodeData("hd");
		data.set("pos", "verb");
		data.set("root", action);
		data.set("morph", "3.singular.past");
		
		return new RSDepTreeNode(data);			
	}
	
	public List<String> getAllIds(String id, RSDepTreeModel m, List<String> ids)
	{
		ids.add(id);
		RSDepTreeNode mv = (RSDepTreeNode) m.getMVertex(id);
		Iterator<MVertex> it = mv.getChildNodes();
		while (it.hasNext())
		{
			RSDepTreeNode tmp = (RSDepTreeNode) it.next();
			ids = getAllIds(tmp.getID(), m, ids);
		}
		return ids;
	}
	
	public boolean appearsIn(List<String> ids, String id)
	{
		for (int i=0; i<ids.size(); i++)
			if (((String) ids.get(i)).equals(id))
				return true;
		
		return false;
	}
	
	public RSDepTreeNode createVzBetrNode(String prep)
	{
		RSTreeNodeData data = new RSTreeNodeData("rhd");
		data.set("cat", "pp");
		
		RSDepTreeNode ppNode = new RSDepTreeNode(data);
		
		data = new RSTreeNodeData("hd");
		data.set("pos", "prep");
		data.set("root", prep);
		
		RSDepTreeNode hdNode = new RSDepTreeNode(data);
		
		data = new RSTreeNodeData("obj1");
		data.set("pos", "adv");
		data.set("root", "wie");
		
		RSDepTreeNode advNode = new RSDepTreeNode(data);

		createEdge("hd", ppNode, hdNode);
		createEdge("obj1", ppNode, advNode);
		
		return ppNode;
	}
	
	public RSDepTreeNode createNpNode(String rel)
	{
		RSTreeNodeData npData = new RSTreeNodeData(rel);
		npData.set("cat", "np");
		
		return new RSDepTreeNode(npData);
	}
}