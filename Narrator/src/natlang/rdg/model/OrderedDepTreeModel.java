package natlang.rdg.model;


import natlang.rdg.discourse.*;
import natlang.rdg.libraries.*;
import natlang.rdg.surfacerealizer.*;
import parlevink.parlegraph.model.*;

import java.util.*;

/** This class models a dependency tree which branches are ordered, so that it 
 *	can be easily made into the surface form
 *	@author Feikje Hielkema
 *	@version 1.0
 */
 
public class OrderedDepTreeModel extends RSDepTreeModel implements LibraryConstants
{
	private boolean inapp;
	
	/** This constructs an OrderedDepTreeModel by copying the vertices from the tree, 
	 *	and substituting the MLabeledEdges by MOrderedEdges. It still needs to be ordered!
	 */
	public OrderedDepTreeModel(RSDepTreeModel tree)
	{			
		super(tree.getRoot());
		List components = tree.decompose();

		for (int i = 0; i < components.size(); i++)
		{			
			MComponentAdapter comp = (MComponentAdapter) components.get(i);
			if (comp.getClass().getName().equals("parlevink.parlegraph.model.MLabeledEdge"))
			{
				MOrderedEdge edge = new MOrderedEdge((MLabeledEdge)comp);
				this.addMEdge(edge);
			}
			else
			{
				this.addMVertex((MLabeledVertex) comp);
			}
		}
	}
	
	/** Orders a tree, using a discourse history */
	public void orderTree(RuleHistory history) throws Exception
	{
		orderTree(this.getRoot(), history);
	}
	
	/** Orders the children of a node in the tree, and recurses if needed 
	 *	@throws Exception
	 */
	private void orderTree(RSDepTreeNode source, RuleHistory history) throws Exception
	{
		if (source.getData().get(RSTreeNodeData.POS) != null)	//end recurse loop
			return;
		
		String cat;
		if ((cat = source.getData().get(RSTreeNodeData.CAT)) == null)
			throw new Exception("This node has neither pos- nor cat-tag: " + source.getData().get(RSTreeNodeData.ROOT));
		
		//System.out.println(cat);
		
		//get the appropriate library for this node's syntactic category
		ConstituentLib lib = new ConstituentLib();
		lib = lib.getAppropriateLib(cat);
		List l = new ArrayList();
		Iterator it = source.getIncidentOutMEdges();
		while (it.hasNext())
		{
			Object o = it.next();
			System.out.println(" o : " + o.getClass().getName());
			if (o.getClass().getName().equals("natlang.rdg.model.MOrderedEdge"))
				l.add(o);
		}
		it = lib.getRules(l);
		Rule r = null;
		
		//select a non-recent cue word using the discourse history
		while (it.hasNext())
		{
			Rule temp = (Rule) it.next();
			if (!history.isRecent(temp))
			{				
				r = temp;
				break;
			}
		}
		if (r == null)
			r = history.getLeastRecent(lib.getRules(l));
		history.setRecent(r);
		
		//order this node using the selected rule
		orderWithRule(source, r);
		
		System.out.println("regel van " + cat + ": " + r);
		
		//order the child nodes recursively
		for (int i = 0; i < l.size(); i++)
		{
			MLabeledEdge edge = (MLabeledEdge) l.get(i);
			RSDepTreeNode target = (RSDepTreeNode) edge.getTarget();
			orderTree(target, history);		//recurse
		}
	}
	
	/** Orders the children of a RSDepTreeNode, using a selected rule 
	 *	@throws Exception if the given rule does not apply to the given node
	 */
	private void orderWithRule(RSDepTreeNode source, Rule r) throws Exception
	{
		Iterator it = source.getIncidentOutMEdges();
		List edges = new ArrayList();
		while (it.hasNext())
			edges.add(it.next());
				
		// if the rule does not apply, the modifiers will be thrown away 
		if (!r.applies(edges))	//check whether the selected rule applies
		{
			for (int i=0; i<edges.size(); i++)
			{
				MOrderedEdge e = (MOrderedEdge) edges.get(i);
				if (e.getLabel().equals("mod"))
				{
					edges.remove(i);
					this.removeMEdge(e);
				}
			}
		}
		//	throw new Exception("wrong rule selected");
		
		//put the child nodes in the right order
		for (int i=0; i < edges.size(); i++)
		{
			MOrderedEdge edge = (MOrderedEdge) edges.get(i);
			//System.out.println("label: " + edge.getLabel());
			int num = r.getPosition(edge.getLabel());	//get the position of this edge
			edge.setPosition(num);					//and communicate it to the edge			
		}
	}
	
	/** Returns the surface form of the tree, using Orthography
	 *	@throws Exception
	 */
	public String getSurfaceForm() throws Exception
	{
		inapp = false;
		StringBuffer result = getSurfaceForm(this.getRoot());
		Orthography ortho = new Orthography();
		return ortho.finish(result);
	}
	
	/** If the given node is a leaf, inflect its root. Else, produce the surface form
	 *	of its children (in their given order)
	 *	@throws Exception
	 */
	private StringBuffer getSurfaceForm(RSDepTreeNode node) throws Exception	
	{
		StringBuffer surfaceForm = new StringBuffer("");
		int i = 0;
	
		while (true)
		{
			RSDepTreeNode child = getNode(node, i);
						
			if (child == null)
			{				
				// comma at the end of a relative clause 
				if (node.getData().get(RSTreeNodeData.CAT).equals("rel"))
					surfaceForm.append(',');
				
				// exclamation mark at the end of a state marked with 'excl'
				if (node.getData().get(RSTreeNodeData.CAT).equals("smain") && node.getData().get(RSTreeNodeData.MORPH) != null &&
						node.getData().get(RSTreeNodeData.MORPH).equals("excl"))
					surfaceForm.append('!');
				
				// comma at the end of an apposition
				if (node.getData().get(RSTreeNodeData.REL).equals("modb") && inapp)
				{
					surfaceForm.append(',');
					inapp = false;
				}
				
				break;
			}
			
			// comma right before the relative clause (right before the relative pronoun)
			if (child.getData().get("rel").equals("rhd"))
				surfaceForm.append(',');
			
			// comma right before the apposition
			if (node.getData().get("cat").equals("np") && child.getData().get("rel").equals("modb")
					&& getNode(child, 0) != null && getNode(child, 0).getData().get("pos") != null && getNode(child, 0).getData().get("pos").equals("det"))
			{
				inapp = true;
				surfaceForm.append(',');
			}
			
			MOrderedEdge edge = getOrderedEdge(node, i);
			i++;
			if (edge.getLabel().indexOf(BORROWED) >= 0)	//more than one edge leads to that node (shared node)
				System.out.println("shared node found");	//do nothing, node is already elsewhere in surface form				
			else if (child.isLeaf())	//if child is a leaf, inflect its root
			{
				Morphology morph = new Morphology();
				morph.doMorphology(child);
				Orthography orth = new Orthography();
				orth.doOrthography(surfaceForm, child);
			}
			else	//else process its children
			{
				surfaceForm.append(getSurfaceForm(child));
				String cat = child.getData().get(RSTreeNodeData.CAT);
				if (cat.equals(CP) || cat.equals(OTI))
				{
					while ((surfaceForm.charAt(surfaceForm.length() - 1) == ' ') || 
						(surfaceForm.charAt(surfaceForm.length() - 1) == ','))
						surfaceForm.deleteCharAt(surfaceForm.length() - 1);		
					surfaceForm.append(",");
				}
			}
		}

		return surfaceForm;
	}
	
	/** Returns the x'th child of the specified RSDepTreeNode
	 */
	private RSDepTreeNode getNode(RSDepTreeNode source, int i)
	{
		Iterator it = source.getIncidentOutMEdges();
		while (it.hasNext())
		{
			MOrderedEdge edge = (MOrderedEdge) it.next();
			if (edge.getPosition() == i)
				return (RSDepTreeNode) edge.getTarget();
		}
		return null;
	}
	
	/** Returns the i'th edge from the node source
	 */
	public MOrderedEdge getOrderedEdge(RSDepTreeNode source, int i)
	{
		Iterator it = source.getIncidentOutMEdges();
		while (it.hasNext())
		{
			MOrderedEdge edge = (MOrderedEdge) it.next();
			if (edge.getPosition() == i)
			{
				return edge;
			}
		}
		return null;
	}
}