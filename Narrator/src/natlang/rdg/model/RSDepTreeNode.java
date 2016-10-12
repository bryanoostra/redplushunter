package natlang.rdg.model;

import parlevink.parlegraph.model.*;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

import natlang.debug.LogFactory;

import org.jdom.*;

/** This class represents a node in a dependency tree of the type used in a 
 *	rhetorical dependency graph (based on the dependency trees used by Alpino). 
 *	It inherits the usual treenode-functionality from natlang.deptree.TreeNode, 
 *	such as a mother, daughters, a label. The data are contained in a RTreeNodeData.
 */

public class RSDepTreeNode extends MLabeledVertex
{
	private RSTreeNodeData data;	//data
	private Vector<String> adjs;
	private Logger logger;
		
	/** Constructs a RSDepTreeNode from a xml-file
	 *	@param element
	 *	@throws IOException, when Element has no pos- and no cat-tag
	 */
	public RSDepTreeNode(Element element) throws IOException
	{			
		super();
		logger = LogFactory.getLogger(this);
		String lb;
		if ((lb = element.getAttributeValue(RSTreeNodeData.POS)) != null);
		else if ((lb = element.getAttributeValue(RSTreeNodeData.CAT)) != null);
		else
			throw new IOException("Error reading input: pos or cat attribute expected");
		setLabel(lb);
		data = new RSTreeNodeData(element);
		logger.info("constructed data");
	}
	
	/**	Constructs a RSDepTreeNode given a dataset
	 *	@param d
	 */
	public RSDepTreeNode(RSTreeNodeData d)
	{
		super();
		data = d;
		adjs = new Vector<String>();
		setLabel(d.getNodeLabel());
	}

	/**	Returns the incoming edge specified by String lb
	 *	@param lb label
	 *	@return MLabeledEdge
	 */
	public MLabeledEdge getIncomingEdge(String lb)
	{
		Iterator<MEdge> it = getIncidentInMEdges();
		MLabeledEdge result;
		while (it.hasNext())
		{
			result = (MLabeledEdge) it.next();
			if (result.getLabel().equals(lb))
				return result;
		}
		return null;
	}
	
	/**	Returns the outgoing edge specified by String lb
	 *	@param lb
	 *	@return MLabeledEdge
	 */
	public MLabeledEdge getOutgoingEdge(String lb)
	{
		Iterator<MEdge> it = getIncidentOutMEdges();
		MLabeledEdge result;
		while (it.hasNext())
		{
			result = (MLabeledEdge) it.next();
			if (result.getLabel().equals(lb))
				return (MLabeledEdge) result;
		}
		return null;
	}
	
	/** Returns an iterator containing all edges with the given label, leaving from
	 *	this node
	 */
	public Iterator<MEdge> getOutgoingEdges(String lb)
	{
		Iterator<MEdge> it = getIncidentOutMEdges();
		List<MEdge> result = new ArrayList<MEdge>();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			if (edge.getLabel().equals(lb))
				result.add(edge);
		}
		return result.iterator();
	}
	
	/**	If the node has only one outgoing Edge, return that edge, otherwise null
	 *	@return MLabeledEdge
	 */
	public MLabeledEdge getOutgoingEdge()
	{
		Iterator<MEdge> it = getIncidentOutMEdges();
		if (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			if (it.hasNext())
				return null;	//more than one incoming edge, so you need a label
			else				//to determine which you want
				return edge;	//exactly one incoming edge
		}
		return null;			//no incoming edges
	}
	
	/**	If the node has only one incomingEdge, returns that edge, otherwise null
	 *	@return MLabeledEdge
	 */
	public MLabeledEdge getIncomingEdge()
	{
		Iterator<MEdge> it = getIncidentInMEdges();
		if (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			if (it.hasNext())
				return null;	//more than one incoming edge, so you need a label
			else				//to determine which you want
				return edge;	//exactly one incoming edge
		}
		return null;			//no incoming edges
	}
	
	/**	If the node has only one parent node, return that node, otherwise return null
	 *	@return RSDepTreeNode
	 */
	public RSDepTreeNode getParentNode()
	{
		MLabeledEdge edge;
		if ((edge = getIncomingEdge()) != null)
			return (RSDepTreeNode) edge.getSource();
		return null;
	}
	
	/**	Returns the parent node connected by the edge with label lb, if it does 
	 *	not exist return null
	 *	@param lb
	 *	@return RSDepTreeNode
	 */
	public RSDepTreeNode getParentNode(String lb)
	{
		MLabeledEdge edge;
		if ((edge = getIncomingEdge(lb)) != null)
			return (RSDepTreeNode) edge.getSource();
		return null;
	}
	
	/**	If the node has only one child node, return that node, otherwise return null
	 *	@return RSDepTreeNode
	 */
	public RSDepTreeNode getChildNode()
	{
		MLabeledEdge edge;
		if ((edge = getOutgoingEdge()) != null)
			return (RSDepTreeNode) edge.getTarget();
		return null;
	}
	
	/**	Returns the child node connected by the edge with label lb, if it does 
	 *	not exist return null
	 *	@param lb
	 *	@return RSDepTreeNode
	 */
	public RSDepTreeNode getChildNode(String lb)
	{
		MLabeledEdge edge;
		if ((edge = getOutgoingEdge(lb)) != null)
			return (RSDepTreeNode) edge.getTarget();
		return null;
	}
	
	/** can be useful when there is more than one edge with label lb ... */
	public RSDepTreeNode getChildNode(String lb, String cat)
	{
		Iterator<MEdge> it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			if (edge.getLabel().equals(lb))
			{
				logger.info("!!!!!!!!!!!!!!!!!!!! Found " + lb + " edge");
				RSDepTreeNode node = (RSDepTreeNode) edge.getTarget();
				if (node.getData().get(RSTreeNodeData.CAT).equals(cat))
					return node;
			}
		}
		return null;
	}
	
	/** Returns an iterator holding all child nodes */
	public Iterator<MVertex> getChildNodes()
	{
		List<MVertex> result = new ArrayList<MVertex>();
		Iterator<MEdge> it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			result.add(edge.getTarget());
		}
		return result.iterator();
	}
	
	/** Returns an Iterator with all the children of this node with dependency label lb
	 */
	public Iterator<MVertex> getChildNodes(String lb)
	{
		List<MVertex> result = new ArrayList<MVertex>();
		Iterator<MEdge> it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			MLabeledEdge edge = (MLabeledEdge) it.next();
			if (edge.getLabel().equals(lb))
				result.add(edge.getTarget());
		}
		return result.iterator();
	}
	
	/** Returns the number of children of this node */
	public int getNumChildNodes()
	{
		int i = 0;
		Iterator<MEdge> it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			it.next();
			i++;
		}
		return i;
	}
		
	/**	Returns the relation with its parent node
	 *	@return String
	 */
	public String getIncomingLabel()
	{
		return data.get(RSTreeNodeData.REL);
	}
	
	/**	Returns the data set
	 *	@return RSTreeNodeData
	 */
	public RSTreeNodeData getData()
	{
		return data;
	}
	
	/**	Returns whether the node is a leaf 
	 */
	public boolean isLeaf()
	{
		return data.isLeaf();
	}
	
	public Vector<String> getAdjs()
	{
		return adjs;
	}
	
	public void resetAdjs()
	{
		adjs = new Vector<String>();
	}
	
	public void addAdj(String adj)
	{
		adjs.add(adj);
	}
	
	/*public String toString()
	{		
		String result = "";
		result += "  node:\t" + data.toString();
		
		Iterator it = getChildNodes();
		while (it.hasNext())
		{
			Object o = it.next();
			System.out.println("type: " + o.getClass());
			if (o.getClass().getName().equals("natlang.rdg.model.RSDepTreeNode"))
			{
				RSDepTreeNode tmp = (RSDepTreeNode) o;
				result += "\n   " + tmp.toString();
			}
		}
		
		return result;
	}*/
}