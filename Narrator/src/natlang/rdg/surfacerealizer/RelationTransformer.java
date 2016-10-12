package natlang.rdg.surfacerealizer;

import natlang.rdg.Narrator;
import natlang.rdg.discourse.*;
import natlang.rdg.libraries.*;
import natlang.rdg.model.*;
import natlang.rdg.view.RDGEditor;
import parlevink.parlegraph.model.*;

import java.util.*;

/**	This class transforms a rhetorical dependency graph into one or more dependency 
 *	trees, which may be connected by a cue word
 *	@author Feikje Hielkema (modified by Nanda Slabbers)
 *	@version 1.0
 */
public class RelationTransformer implements RDGTransformer, LibraryConstants
{
	RSGraph graph;					//the graph to be transformed
	CueWordHistory history;			//the Discourse History
	List result = new ArrayList();	//the results of the transformation
	boolean tempfirst = true;
	
	/** Constructs a RelationTransformer by setting the graph */
	public RelationTransformer(RSGraph g)
	{
		setGraph(g);
	}
	
	/** Sets the Discourse History */
	public void setDiscourseHistory(CueWordHistory h)
	{
		history = h;
	}
	
	/** Sets the graph to be transformed */
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	/** @see RDGTransformer */
	public boolean check()
	{
		Iterator it = graph.getRelations();
		if (history == null)
			return false;
		return it.hasNext();
	}
	
	/** @see RDGTransformer */
	public Iterator getResult() 
	{
		result = new ArrayList();
//		Iterator it = graph.getMVertices();
//		
//		if (it.hasNext())
//		{
//			RSVertex root = (RSVertex) it.next(); 					
//			recursiveDepthFirst(root);
//		}				
		
		if(graph.getRoot() != null)
			recursiveDepthFirst(graph.getRoot());
		
		return result.iterator();
	}
	
	/**
	 * Traverse the graph depth first and copy the dependency trees
	 * into the result array list
	 * @param node
	 */
	public void recursiveDepthFirst(RSVertex node)
	{
		if (node.getType().equals("deptree"))
			result.add(node);
		else if (node.getType().equals("text"))
			result.add(node);
		else if (node.getType().equals("rhetrel"))
		{
			RSVertex nuc = ((RhetRelation) node).getNucleus();
			RSVertex sat = ((RhetRelation) node).getSatellite();
			
			if (sat != null)
				recursiveDepthFirst(sat);
			if (nuc != null)
				recursiveDepthFirst(nuc);			
		}
	}
	
	/**	Transforms the Rhetorical Dependency Graph, by first transforming the 
	 *	relation(s) at the bottom and adding conjunctions. Then adjuncts are added
	 *  in order to show the relations BETWEEN sentences. Finally a cue word may be
	 *  added to the left most dependency tree in the graph to show the relation to
	 *  the previous paragraph.
	 *	@throws Exception if the relation cannot be transformed to one or more 
	 *	RSDepTreeModels
   	*/
	public boolean transform() throws Exception
	{
		RSVertex root = graph.getRoot();
		
		if (root != null) 
		{
			transformDepthFirst(root, root, "nucleus");		// eerste voegwoorden
		}
		
		
		if (root != null)
		{
			transformDepthFirst(root, root, "nucleus");		// nog een keer voegwoorden
		}
		
		if (root != null)
		{
			transformDepthFirst2(root, root, "nucleus");	// mogelijke bepalingen
		}
		
		if (root != null)
		{
			transformFinalRelation(root);					// eventueel bepaling aan eerste zin (cueword over alinegrens heen)
		}
		
		return true;						
	}
		
	/**
	 * Removes the relation from the tree
	 * @param rel
	 */
	private void removeRelation(RhetRelation rel)
	{
		RSVertex nucleus = rel.getNucleus();
		if (nucleus != null)
		{
			if (nucleus.getType().equals("deptree"))
				graph.removeMVertex(nucleus);
			else if (nucleus.getType().equals("rhetrel"))
				removeRelation((RhetRelation) nucleus);
		}
		
		RSVertex satellite = rel.getSatellite();
		if (satellite != null)
		{
			if (satellite.getType().equals("deptree"))
				graph.removeMVertex(satellite);
			else if (satellite.getType().equals("rhetrel"))
				removeRelation((RhetRelation) satellite);
		}
		
		graph.removeMVertex(rel);
	}
	
	/**
	 * Transforms the tree depth first and tries to combine the dependency trees at the bottom.
	 * Doing this it adds cue words which represent the relations WITHIN sentences, such
	 * as omdat, nadat 
	 * @param node
	 * @param par
	 * @param label
	 */
	public void transformDepthFirst(RSVertex node, RSVertex par, String label)
	{			
		if (node.getType().equals("rhetrel"))
		{			
			RSVertex nuc = ((RhetRelation) node).getNucleus();
			RSVertex sat = ((RhetRelation) node).getSatellite();
			System.out.println("nuc: " + nuc);
			System.out.println("sat: " + nuc);
			
			if (sat != null && sat.getType().equals("rhetrel"))
				transformDepthFirst(sat, node, "satellite");
			if (nuc != null && nuc.getType().equals("rhetrel"))
				transformDepthFirst(nuc, node, "nucleus");
								
			if ((nuc == null || nuc.getType().equals("deptree"))
				&& ((sat == null) || sat.getType().equals("deptree")))
			{
				try
				{
					RSDepTreeModel nuc2 = (RSDepTreeModel) nuc;
					RSDepTreeModel sat2 = (RSDepTreeModel) sat;
															
					if (	
							(
								(nuc2 == null)
								||
								!(nuc2.getRoot().getData().get("cat").equals("smain")) 
								|| 
								(nuc2.getRoot().getData().get("morph") == null)
								|| 
								!(nuc2.getRoot().getData().get("morph").equals("excl"))
							)
							&& 
							(
								(sat2 == null)
								||
								!(sat2.getRoot().getData().get("cat").equals("smain")) 
								|| 
								(sat2.getRoot().getData().get("morph") == null) 
								|| 
								!(sat2.getRoot().getData().get("morph").equals("excl"))
							)
						)
					{
						RSVertex vert = transformRhetRel((RhetRelation) node);
						if (vert.getType().equals("deptree"))
						{				
							removeRelation((RhetRelation) node);
							
							if(node.equals(par))
								graph = new RSGraph((RSDepTreeModel) vert);
							
							else if (label.equals("nucleus"))
								setNucleus((RhetRelation) par, (RSDepTreeModel) vert);
							else
								setSatellite((RhetRelation) par, (RSDepTreeModel) vert);
				
						}
					}
				}
				catch(Exception e)
				{
					System.out.println("1 surfacerealizer.RelationTranformer > error: " + e);
					e.printStackTrace();
				}
			}
		}
	}	
		
	/**
	 * Transforms the tree depth first and adds cue words to dependency trees in order
	 * to express rhetorical relations BETWEEN sentences (such as daarom, daarna)
	 * @param node
	 * @param par
	 * @param label
	 */
	public void transformDepthFirst2(RSVertex node, RSVertex par, String label)
	{				
		if (node.getType().equals("rhetrel"))
		{			
			RhetRelation rel = (RhetRelation) node;
			RSVertex nuc = rel.getNucleus();
			RSVertex sat = rel.getSatellite();
			
			if (sat != null && sat.getType().equals("rhetrel"))
				transformDepthFirst2(sat, node, "satellite");
			if (nuc != null && nuc.getType().equals("rhetrel"))
				transformDepthFirst2(nuc, node, "nucleus");
							
			if (nuc != null && nuc.getType().equals("deptree"))
			{
				if (!(((RSDepTreeModel) nuc).getRoot().getData().get("cat").equals("smain")) || 
						(((RSDepTreeModel) nuc).getRoot().getData().get("morph") == null) || 
						!(((RSDepTreeModel) nuc).getRoot().getData().get("morph").equals("excl")))
				{
					try
					{	
						transformRhetRel2((RhetRelation) node);
					}
					catch(Exception e)
					{
						System.out.println("2 surfacerealizer.RelationTranformer > error: " + e);
					}
				}
			}
		}
	}
	
	/**
	 * Adds a cue word to the very first dependency tree of the paragraph if the graph 
	 * contains a cue word that represents the relation to the previous paragraph. 
	 * This can be achieved by adding the cue word (such as daarom, daarna) to the
	 * leftmost dependency tree in the graph.  
	 * @param node
	 */
	public void transformFinalRelation(RSVertex node)
	{				
		if (node.getType().equals("rhetrel"))
		{			
			RhetRelation rel = (RhetRelation) node;
			String label = rel.getLabel();
			RSVertex nuc = rel.getNucleus();
			RSVertex sat = rel.getSatellite();
			RSVertex v = null;
			
			if (nuc != null && sat == null)
			{
				v = nuc;
				
				while (v.getType().equals("rhetrel"))
				{
					if (((RhetRelation) v).getSatellite() != null)
						v = ((RhetRelation) v).getSatellite();
					else
						v = ((RhetRelation) v).getNucleus();
				}
			}
					
			if (v != null && v.getType().equals("deptree"))
			{
				if (!(((RSDepTreeModel) v).getRoot().getData().get("cat").equals("smain")) || 
						(((RSDepTreeModel) v).getRoot().getData().get("morph") == null) || 
						!(((RSDepTreeModel) v).getRoot().getData().get("morph").equals("excl")))
				{
					try
					{						
						transformRhetRel3((RSDepTreeModel) v, label);
					}
					catch(Exception e)
					{
						System.out.println("3 surfacerealizer.RelationTranformer > error: " + e);
					}
				}
			}
		}
	}	
	
	/**
	 * Tries to combine the two children of the relation into one dependency tree
	 * by adding a conjunction (omdat, nadat)
	 * @param rel
	 * @return
	 * @throws Exception
	 */
	private RSVertex transformRhetRel(RhetRelation rel) throws Exception
	{		
		CueWordLib lib = new CueWordLib();	//Library holding information on possible cue words
		RSDepTreeModel nuc = (RSDepTreeModel) rel.getNucleus();
		RSDepTreeModel sat = (RSDepTreeModel) rel.getSatellite();
		
		Iterator it;
		if (rel.getSatellite() == null)
			it = lib.getOptions(rel.getLabel(), "bep");
		else
			it = lib.getOptions(rel.getLabel(), "vg");
		
		RSDepTreeNode cueWord = null;		
		RSDepTreeNode tmpnode = null;
						
		while (it.hasNext())
		{		
			RSDepTreeNode node = (RSDepTreeNode) it.next();
			System.out.println("Mogelijk cueword: " + node.getData().get("root") + " (van relatie " + rel.getLabel() + ")");
			
			String cue = node.getData().get("root");
			
			if (!cue.equals("maar") && !cue.equals("en") && !cue.equals("want") && !cue.equals("dus"))
			{
				if (lib.addToSatellite(node))
				{
					if (sat != null && (countAggregation(sat.getRoot()) > 0) && !rel.getLabel().equals("relative"))
						return rel;
					if (nuc != null && (countAggregation(nuc.getRoot()) > 0) && !rel.getLabel().equals("relative"))
						break;
				}
				else
				{
					if (sat != null && (countAggregation(nuc.getRoot()) > 0) && !rel.getLabel().equals("relative"))
						return rel;
					if (sat != null && (countAggregation(sat.getRoot()) > 0) && !rel.getLabel().equals("relative"))
						break;
				}
			}
					
			if (!tooLong(rel, node))
			{	
				cueWord = node;
				if (!history.isRecent(node))
					break;
			}
		}
				
		if (cueWord == null)
			return rel;
				
		// is wat lelijk, maar is om ervoor te zorgen dat een cueword nooit twee keer in dezelfde zin gebruikt wordt
		if(nuc != null){
			Iterator tmp = nuc.getMVertices();
			while (tmp.hasNext())
			{
				RSDepTreeNode m = (RSDepTreeNode) tmp.next();
				if (m.getData().get("root") != null && m.getData().get("root").equals(cueWord.getData().get("root")) && !rel.getLabel().equals("relative"))				
					return rel;
			}
		}
		if (sat != null)
		{
			Iterator tmp = sat.getMVertices();
			while (tmp.hasNext())
			{
				RSDepTreeNode m = (RSDepTreeNode) tmp.next();
				if (m.getData().get("root") != null && m.getData().get("root").equals(cueWord.getData().get("root")))
					return rel;
			}
		}
				
		// if cue word has been recently used: take a 'bepaling' and combine this with the cue word 'en' (unless the cue word is 'maar' or 'die')
		if (history.isRecent(cueWord) && !cueWord.getData().get("root").equals("maar") && !cueWord.getData().get("root").equals("die") && nuc != null && countAggregation(nuc.getRoot()) == 0 && sat != null && countAggregation(sat.getRoot()) == 0)
		{			
			it = lib.getOptions(rel.getLabel(), "bep");
			
			RSDepTreeNode tmpcueword = null;
			
			while (it.hasNext())
			{		
				RSDepTreeNode node = (RSDepTreeNode) it.next();
				
				if (!tooLong(rel, node))
				{
					tmpcueword = node;					
					
					if (!history.isRecent(node))
					{
						RSTreeNodeData data = new RSTreeNodeData(CRD);
						data.set(RSTreeNodeData.ROOT, "en");
						data.set(RSTreeNodeData.POS, VG);
						cueWord = new RSDepTreeNode(data);
						
						break;
					}
				}
			}	
			
			if (tmpcueword != null)
				tmpnode = tmpcueword;
		}
									
		if (tmpnode != null)
			history.setRecent(tmpnode);
		else
			history.setRecent(cueWord);	//set the selected cue word to recent		
		
		//print the selected cue word 
		System.out.println("!!!!!!!!! 1:" + cueWord.getData().get(RSTreeNodeData.ROOT) + " !!!!!!!!!!!");	
		
		// add tmpnode to nucleus
		if (tmpnode != null)
		{			
			RSDepTreeNode root = nuc.getRoot();
					
			MLabeledEdge edge = new MLabeledEdge("mod");
			edge.setSource(root);
			edge.setTarget(tmpnode);
			
			nuc.addMVertex(tmpnode);
			nuc.addMEdge(edge);
		}
		
		//call the conjunctor to process the relation and produce the dependency tree(s)
		Conjunctor c = new Conjunctor(rel, cueWord); 

		if (c.transform())
		{				
			it = c.getResult();
			RSDepTreeModel tree = (RSDepTreeModel) it.next();
			if (it.hasNext())
				return rel;
			else
			{	
				if (Narrator.ELLIPT)
				{
					Elliptor e = new Elliptor(tree, rel.getLabel());
					e.transform();
				}
				System.out.println("Elliption finished");				
				return tree;
			}
		}
		return rel;
	}
		
	/**
	 * Adds an adjunct (daarom, daarna) to the nucleus of the relation
	 * @param rel
	 * @return
	 * @throws Exception
	 */
	private RSVertex transformRhetRel2(RhetRelation rel) throws Exception
	{	
		CueWordLib lib = new CueWordLib();	//Library holding information on possible cue words
		Iterator it = lib.getOptions(rel.getLabel(), "bep");
		RSDepTreeNode cueWord = null;
						
		/*if (rel.getSatellite() != null && rel.getSatellite().getType().equals("deptree") && rel.getNucleus().getType().equals("deptree"))
			if (rel.getLabel().startsWith("cause") && (countAggregation(((RSDepTreeModel) rel.getSatellite()).getRoot()) == 2 
					|| countAggregation(((RSDepTreeModel) rel.getNucleus()).getRoot()) == 2))
				return rel;*/
		
		if (it != null)
		{
			while (it.hasNext())		//op deze manier worden het wel cycli van cue words
			{
				RSDepTreeNode node = (RSDepTreeNode) it.next();
				System.out.println("Mogelijke bepaling: " + node.getData().get("root"));				
								
				if (lib.addToNucleus(node))
				{
					if (rel.getNucleus().getType().equals("deptree") && (countAggregation(((RSDepTreeModel) rel.getNucleus()).getRoot()) == 0))
					{				
						if (!history.isRecent(node)) 
						{
							cueWord = node;
							break;
						}
					}
				}
				else
				{
					if (rel.getSatellite() != null && rel.getSatellite().getType().equals("deptree") && (countAggregation(((RSDepTreeModel) rel.getSatellite()).getRoot()) == 0))
					{				
						if (!history.isRecent(node)) 
						{
							cueWord = node;
							break;
						}
					}
				}
			}
		}
		
		if (cueWord == null)
			return rel;
								
		history.setRecent(cueWord);	//set the selected cue word to recent		
		
		//print the selected cue word 
		System.out.println("!!!!!!!!! 2:" + cueWord.getData().get(RSTreeNodeData.ROOT) + " !!!!!!!!!!!");	
		
		//call the conjunctor to process the relation and produce the dependency tree(s)
		Conjunctor c = new Conjunctor(rel, cueWord);

		// cue word moet eigenlijk aan eerste clause van de nucleus toegevoegd worden (als de nucleus al een complexe dependency tree is)
		// de trees zijn echter nog niet geordend, dus dit is niet mogelijk -> tijdelijke oplossing: grammatica aangepast 
		// zodat het cue word altijd aan het eind van de eerste clause wordt toegevoegd, in plaats van aan het eind van de complete zin
		if (c.transform())
		{				
			it = c.getResult();
			RSDepTreeModel tree = (RSDepTreeModel) it.next();
			if (it.hasNext())
				return rel;
			else
				return tree;
		}
		return rel;
	}
	
	/**
	 * Adds an adjunct to the leftmost tree in the graph
	 * @param tree
	 * @param label
	 * @return
	 * @throws Exception
	 */
	private RSDepTreeModel transformRhetRel3(RSDepTreeModel tree, String label) throws Exception
	{		
		CueWordLib lib = new CueWordLib();
		Iterator it = lib.getOptions(label, "bep");
		RSDepTreeNode cueWord = null;
				
		if (it != null)
		{
			while (it.hasNext())
			{		
				cueWord = (RSDepTreeNode) it.next();
				if (!history.isRecent(cueWord)) 
					break;
			}
		}
				
		if (cueWord == null)
			return tree;
		
		System.out.println("!!!!!!!!! 3:" + cueWord.getData().get(RSTreeNodeData.ROOT) + " !!!!!!!!!!!");	
		
		RSDepTreeNode root = null;
		it = tree.getMVertices();
		if (it.hasNext())
			root = (RSDepTreeNode)graph.getRoot(); //(RSDepTreeNode) it.next();
				
		MLabeledEdge edge = new MLabeledEdge("mod");
		edge.setSource(root);
		edge.setTarget(cueWord);
		
		tree.addMVertex(cueWord);
		tree.addMEdge(edge);
		
		return tree;
	}
	
	/** Sets the nucleus of the given relation to the given tree */
	private void setNucleus(RhetRelation rel, RSDepTreeModel tree)
	{
		boolean result = false;
		Iterator it = rel.getIncidentOutMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			if (edge.getLabel().equals(NUCLEUS))
			{
				edge.setTarget(tree);
				result = true;
				break;
			}
		}
		if (!result)
		{
			RSEdge edge = new RSEdge(NUCLEUS);
			edge.setSource(rel);
			edge.setTarget(tree);
			graph.addMEdge(edge);
		}
	}
	
	/** Sets the satellite of the given relation to the given tree */
	private void setSatellite(RhetRelation rel, RSDepTreeModel tree)
	{		
		boolean result = false;
		Iterator it = rel.getIncidentInMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			if (edge.getLabel().equals(SATELLITE))
			{
				edge.setSource(tree);
				result = true;
				break;
			}
		}
		if (!result)
		{
			RSEdge edge = new RSEdge(SATELLITE);
			edge.setSource(tree);
			edge.setTarget(rel);
			graph.addMEdge(edge);
		}
	}
	
	/** This function is a check on the length of the generated sentences: when 
	 *	the relation to be transformed consists two aggregated trees, or one 
	 *	nested aggregation, it will disapprove all cue words trying to conjunct or complement the
	 *	relation
	 */
	private boolean tooLong(RhetRelation rel, RSDepTreeNode cueWord)
	{	//a modifier does not combine the two sentences, so is never too long
		if (cueWord.getIncomingLabel().equals(MOD))
			return false;
		
		//RZ:
		if(rel.getNucleus() == null || rel.getSatellite() == null)
			return false;
		
		// ff beter controleren!
		if (rel.getNucleus().getClass().getName().equals("natlang.rdg.model.RhetRelation") ||
				rel.getSatellite().getClass().getName().equals("natlang.rdg.model.RhetRelation"))
		{
			//System.out.println("nieuwe requirement");
			return true;
		}
			
		
		RSDepTreeNode root1 = ((RSDepTreeModel) rel.getNucleus()).getRoot();
		RSDepTreeNode root2 = ((RSDepTreeModel) rel.getSatellite()).getRoot();
	
		int aggr = countAggregation(root1) + countAggregation(root2);
		if (aggr > 1)
			return true;
		return false;
	}
	
	/** This function counts the occurrances of conjunctions, complementations etc, to determine
	 *  how often aggregation has taken place
	 */
	private int countAggregation(RSDepTreeNode source)
	{
		int result = 0;
		String cat = source.getData().get(RSTreeNodeData.CAT);
		if (cat.equals(CONJ) || cat.equals(CP) || cat.equals(OTI))
			result++;
		
		Iterator it = source.getIncidentOutMEdges();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			RSDepTreeNode target = (RSDepTreeNode) edge.getTarget();
			String c = target.getData().get(RSTreeNodeData.CAT);
			if (c != null)
				if (c.equals(CONJ) || c.equals(CP) || c.equals(OTI) || c.equals(SSUB) || c.equals(SMAIN))
					result += countAggregation(target);
		}
		return result;
	}		
}