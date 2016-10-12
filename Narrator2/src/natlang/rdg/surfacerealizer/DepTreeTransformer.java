package natlang.rdg.surfacerealizer;

import natlang.rdg.model.*;
import parlevink.parlegraph.model.*;

import java.util.*;

/**	DepTreeTransformator performs operations on a RSDepTreeModel, such as removing and adding nodes. It has a root 
  *	and a current, on which all operations take place.
  *	@author Feikje Hielkema
  *	@version 1.0, March 3d 2005
  */
  
public class DepTreeTransformer 
{
	RSDepTreeModel tree;		//tree to be transformed
	RSDepTreeNode root;		//root of the tree
	RSDepTreeNode current;		//current node, on which operations take place
	
	/** Constructs a DepTreeTransformer by setting the RSDepTreeModel */
	public DepTreeTransformer(RSDepTreeModel deptree)
	{
		setTree(deptree);
	}
	
	/** Constructs an empty DepTreeTransformer */
	public DepTreeTransformer()
	{
		
	}
	
	/** Sets the tree to be transformed */
	public void setTree(RSDepTreeModel deptree)
	{
		tree = deptree;
		root = tree.getRoot();
		current = root;
	}
	
	/** returns the tree */
	public RSDepTreeModel getTree()
	{
		return tree;
	}
	
	/**	Helper function; removes the children of a branche that needs to be removed
	 *	@param node  
	 *	
	 */
	public void removeChildren(RSDepTreeNode node)
	{
		Iterator it = node.getIncidentOutMEdges();
		List labels = new ArrayList();
		
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			RSDepTreeNode target = (RSDepTreeNode) edge.getTarget();
			Iterator subIt = target.getIncidentInMEdges();
			subIt.next();
				if (!subIt.hasNext())		
					labels.add(edge.getLabel());
		}
		for (int i = 0; i < labels.size(); i++)
		{
			RSDepTreeNode child = (RSDepTreeNode) node.getChildNodes((String) labels.get(i)).next();
			removeChildren(child);
			tree.removeMVertex(child);
		}
	}

	/** Remove the current node (and all its children), which is returned.
	 *	Current will be reset to the parent-node
	 */
	public boolean removeNode() 
	{
		RSDepTreeNode temp = current;
		if (moveUp(1))		//set current to the parent node
		{
			removeChildren(temp);
			tree.removeMVertex(temp);	
			return true;
		}
		else
		{
			removeChildren(current);
			tree.removeMVertex(temp);
			return true;
		}
	}
	
	/** Removes the given node from the tree */
	public boolean removeNode(RSDepTreeNode node)
	{
		if (setCurrent(node) && removeNode())
			return true;
		return false;
	}
	
	/**	Remove the node you reach when you go down the tree from current, taking the path specified by the List
	 *	@param lb
	 *	@return boolean
	*/
	public boolean removeChildFromCurrent(List lb)
	{
		if (moveDownFromCurrent(lb))
			return removeNode();
		return false;
	}	
	
	/**	Remove the node you reach when you go down the tree from current, following the relation specified by the String
	 *	@param lb
	 *	@return boolean
	*/
	public boolean removeChildFromCurrent(String lb)
	{
		List l = new ArrayList();
		l.add(lb);
		return removeChildFromCurrent(l);
	}
	
	/**	Remove the node you reach when you go down the tree from current, taking the path specified by the List
	 *	@param lb
	 *	@return boolean
	*/
	public boolean removeChildFromRoot(List lb)
	{
		RSDepTreeNode temp = current;
		current = root;
		boolean result = removeChildFromCurrent(lb);
		current = temp;
		return result;
	}
	
	/**	Remove the node you reach when you go down the tree from root, following the relation specified by the String
	 *	@param lb
	 *	@return boolean
	*/
	public boolean removeChildFromRoot(String lb)
	{
		List l = new ArrayList();
		l.add(lb);
		return removeChildFromRoot(l);
	}
	
	/** Add the given node to the model, attaching it to the current node
	 *	@param node
	 *	@return boolean, indicating whether operation has succeeded
	 */
	public boolean addNodeAtCurrent(RSDepTreeNode node)
	{
		//if (current.getOutgoingEdge(node.getIncomingLabel()) != null)
		//	return false;	//wil je dit wel?
			
		MLabeledEdge edge = new MLabeledEdge(node.getIncomingLabel());
		edge.setSource(current);
		edge.setTarget(node);
		tree.addMEdge(edge);
		addChildren(node);
		return true;
	}
	
	/** Adds the children of the given node to the tree */
	private void addChildren(RSDepTreeNode node)
	{
		Iterator it = node.getIncidentOutMEdges();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			tree.addMEdge(edge);
			addChildren((RSDepTreeNode) edge.getTarget());
		}
	}
		
	
	/**	Add the node at the place you reach when you move down from current following the List
	 *	@param node
	 *	@param lb
	 *	@return boolean
	 */
	public boolean addNodeAtCurrent(RSDepTreeNode node, List lb)
	{
		if (moveDownFromCurrent(lb))
			return addNodeAtCurrent(node);
		return false;
	}
	
	/**	Add the node at the place you reach when you move down from root following the List
	 *	@param node
	 *	@param lb
	 *	@return boolean
	 */
	public boolean addNodeAtRoot(RSDepTreeNode node, List lb)
	{
		if (moveDownFromRoot(lb))
			return addNodeAtCurrent(node);
		return false;
	}
	
	/**	Add the node at the root
	 *	@param node
	 *	@return boolean
	 */
	public boolean addNodeAtRoot(RSDepTreeNode node)
	{
		RSDepTreeNode temp = current;
		current = root;
		boolean result = addNodeAtCurrent(node);
		current = temp;
		return result;
	}
	
	/*public boolean addNodeToLeftClause(RSDepTreeNode node)
	{
		RSDepTreeNode temp = current;
		current = null;
		setCurrToRootLeftClause(root, false);
		boolean result = addNodeAtCurrent(node);
		current = temp;
		return result;
	}
	
	public void setCurrToRootLeftClause(RSDepTreeNode n, boolean found)
	{
		System.out.println("node: " + n.getData().get("cat"));
		if (n.getData().get("cat") != null && 
				(n.getData().get("cat").equals("smain") || n.getData().get("cat").equals("ssub"))
				&& current == null)
		{
			System.out.println("set to current");
			current = n;
		}
		else
		{
			System.out.println("doe kindjes");
			Iterator it = n.getChildNodes();
			while (it.hasNext())
				setCurrToRootLeftClause((RSDepTreeNode) it.next(), false);
		}
	}*/
	
	/** Sets the given node to current */
	public boolean setCurrent(RSDepTreeNode node)
	{
		if (tree.containsMComponent(node))
		{
			current = node;
			return true;
		}
		return false;
	}
	
	/** Move down from current node following the route given by the List and set 
	 *	current to the resulting node. 
	 *	@param lb
	 *	@return boolean, indicating whether operation has succeeded
	 */
	public boolean moveDownFromCurrent(List lb)
	{
		for (int i = 0; i < lb.size(); i++)
		{
			MLabeledEdge edge = current.getOutgoingEdge((String) lb.get(i));
			if (edge != null)
				current = (RSDepTreeNode) edge.getTarget();
			else
				return false;
		}
		return true;
	}
	
	/** Same, but now starting at the top node */
	public boolean moveDownFromRoot(List lb)
	{
		current = root;
		return moveDownFromCurrent(lb);
	}
	
	/** Move i steps up in the tree (starting from current). This only works if 
	 *	each node has only one parent. Otherwise false is returned
	 *	@param steps
	 *	@return boolean
	 */
	public boolean moveUp(int steps)
	{
		for (int i = 0; i < steps; i++)
		{
			MLabeledEdge edge;
			if ((edge = current.getIncomingEdge()) != null)
				current = (RSDepTreeNode) edge.getSource();
			else
				return false;
	   	}
	   	return true;
  	}
  	
  	/**	Move current one step up in the tree
  	 *	@return boolean 
  	 */
  	public boolean moveUp()
  	{
  		return moveUp(1);
  	}
  	
  	/** Move i steps up in the tree (starting from current) following the labels  
	 *	given by the List 
	 *	@param lb
	 *	@return boolean
	 */
  	public boolean moveUp(List lb)
  	{
  		for (int i = 0; i < lb.size(); i++)
		{
			MLabeledEdge edge = current.getIncomingEdge((String) lb.get(i));
			if (edge != null)
				current = (RSDepTreeNode) edge.getSource();
			else
				return false;
		}
		return true;
  	}
  	
  	/**	Change the value of a specified attribute of current
  	 *	@param attr
  	 *  @param value
  	 *	@return boolean
  	 */
  	public boolean changeAttributeValue(String attr, String value)
  	{
  		return current.getData().set(attr, value);
  	}
  	
  	public void setCurrentNode(RSDepTreeNode node)
  	{
  		removeNode();
  		addNodeAtCurrent(node);
  	}
}