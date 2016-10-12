package natlang.rdg.model;

import natlang.rdg.libraries.*;
import parlevink.parlegraph.model.*;

import java.util.*;
import java.io.*;
import org.jdom.*;

/** RDepTreeModel models a Dependency Tree as used in a rhetorical dependency 
 *	structure (based on Alpino dependency trees). 
 *	@author Feikje Hielkema
 *	@version 1.0
*/

public class RSDepTreeModel extends MGraphAdapter implements RSVertex
{
	private RSDepTreeNode root;			//root vertex
	
	/**	Constructs a RSDepTreeModel, reading from a xml-file
	 *	@param element
	 *	@throws IOException when the input is not well-formed
	 */
	public RSDepTreeModel(Element element) throws IOException
	{
		super();
		Element child = (Element) element.getChildren().get(0);
		root = new RSDepTreeNode(child);
		makeNode(child, root);
	}
	
	/** Constructs a RSDepTreeModel with only a root */
	public RSDepTreeModel(RSDepTreeNode top)
	{
		super();
		root = top;
	}
	
	/** Sets the root */
	public void setRoot(RSDepTreeNode r)
	{
		root = r;
	}
	
	/** @see RSVertex */
	public boolean isDepTree()
	{
		return true;
	}
	
	public String getType()
	{
		return "deptree";
	}
	
	/**	This function constructs the nodes of the RSDepTreeModel 
	 *	@param Element, RSDepTreeNode
	 *	@throws IOException when the input is not well-formed
	 */
	private void makeNode(Element element, RSDepTreeNode source) throws IOException
	{
		List children = element.getChildren();
		
		//construct node, connect and add to the graph, then recurse
		for (int i = 0; i < children.size(); i++)
		{
			Element child = (Element) children.get(i);
			RSDepTreeNode target;
			String index = child.getAttributeValue("index");
			MLabeledEdge edge = new MLabeledEdge();
			if ((index != null) && (containsID(index)))
			{
				//if the node is already in the graph, create a new edge to it, specifying
				target = (RSDepTreeNode) getMVertex(index);	//in its label that the node is borrowed
				StringBuffer sb = new StringBuffer(target.getIncomingLabel());
				sb.append(LibraryConstants.BORROWED);
				edge.setLabel(sb.toString());
			}
			else
			{
				//recurse if this node is not a leaf
				target = new RSDepTreeNode(child);
				edge.setLabel(target.getIncomingLabel());
				if (index != null)
					addMVertex(target, index);
				if (!target.isLeaf())
					makeNode(child, target);
			}
			
			edge.setSource(source);
			edge.setTarget(target);
			addMEdge(edge);
		}
	}
	
	/** returns the root node */
	public RSDepTreeNode getRoot()
	{
		return root;
	}
	
	/** Returns the relation that this dependency tree is a part of. 
	 */
	public Iterator getParent()
	{
		List result = new ArrayList();
		Iterator it = getIncidentInMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			result.add(edge.getSource());
		}
		
		it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			result.add(edge.getTarget());
		}
		
		return result.iterator();
	}
	
	public String toString()
	{
		String result = "";
		
		Iterator children = root.getChildNodes();
						
		while (children.hasNext())
		{
			MVertex tmp = (MVertex) children.next();
			if (tmp != null)
				result += "\n" + tmp.toString();
		}
		
		return result;
	}
}

