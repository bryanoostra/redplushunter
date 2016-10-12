package natlang.rdg.model;

import parlevink.parlegraph.model.*;

import java.util.*;
import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.net.*;

/** RSGraph is a MGraph which models a rhetorical dependency structure, 
 *	constructing it out of a xml-document.
 *	@author Feikje Hielkema
 *	@version 1.0
  */

public class RSGraph extends MGraphAdapter
{
	private Document doc;				//JDOM document of the parsed XML file
	private Element root;				//Root element of the parsed XML file
	
	/** Constructor reads and constructs a RSGraph from an xml-file.
	 *	@param f a File (must be xml)
	 *	@throws an Exception, could be IOException when the input is not well-
	 *	formed, or a JDOMException when there is an error building the file
	 */
	public RSGraph(File f) throws Exception
	{
		super();
		readRDG(f);
	}
	
	/** Constructor reads and constructs a RSGraph from an xml-file.
	 *	@param u an URL(must be xml)
	 *	@throws an Exception, could be IOException when the input is not well-
	 *	formed, or a JDOMException when there is an error building the file
	 */
	public RSGraph(URL u) throws Exception
	{
		super();
		try
		{
   			SAXBuilder saxbuild = new SAXBuilder();
      			doc = saxbuild.build(u);
      			root = doc.getRootElement();
      		}
      		catch (Exception ex)
      		{
      		   	throw(ex);
      		}  
		
		if (isRDG())
		{
			makeRDG();
			System.out.println("modeled RSGraph");
		}
	}
	
	public RSGraph(RSDepTreeModel deptree)
	{
		addMVertex(deptree);
	}
	
	public RSGraph(RhetRelation rhetrel)
	{
		addMVertex(rhetrel);
	}
	
	public RSGraph(CannedText ct)
	{
		addMVertex(ct);
	}
	
	public RSGraph(PlotElement m)
	{
		addMVertex(m);
	}
	
	/** Builds the document, checks whether the input is a rhetdepgraph, and 
	 *	constructs the RSGraph
	 *	@param f a File (must be xml)
	 *	@throws an Exception, could be IOException when the input is not well-
	 *	formed, or a JDOMException when there is an error building the file
	 */
	public void readRDG(File f) throws Exception
	{
		doc = makeDocument(f);
		if (isRDG())
			makeRDG();
	}

	/**	Builds the document using the SAXBuilder package
	 *	@param f a File (must be xml)
	 *	@throws a JDOMException (see SAXBuilder documentation)
	 *	@return jdom document
	 */
	private Document makeDocument(File f) throws Exception
	{
		try
		{
   			SAXBuilder saxbuild = new SAXBuilder();
      			doc = saxbuild.build(f);
      			root = doc.getRootElement();
      		}
      		catch (Exception ex)
      		{
      		   	throw(ex);
      		}   			
   		return doc;
	}
	
	/** Checks whether the input is an rhetorical dependency graph, by checking 
	 *	the top node tag 
	 *	@throws IOException, if the input is not a rhetdepgraph
	 */
	private boolean isRDG() throws IOException
	{
		if ((root == null) || !(root.getName().equals("rhetdepgraph")))    
    			throw new IOException("Wrong input: rhetdepgraph expected");
    		return true;
    	}
    
    	/**	Constructs the RSGraph 
    	 *	@throw IOException, when the input is not well-formed
    	 */	
    	private void makeRDG() throws IOException
    	{
    	  	makeRDG(root);
	}	

	/** Reads the input xml and constructs the RDGModel
	 *	@param element (see org.w3c.dom)
	 *	@throws IOException, when a child node is not a rhetrelation or a deptree
	*/
	private void makeRDG(Element element) throws IOException
	{
		List children = element.getChildren();
		String id;
		Element child;
		
		for (int i = 0; i < children.size(); i++)
		{	
			RSVertex target;
			child = (Element) children.get(i);
			if (child.getName().equals("dt"))
				target = makeDepTree(child);
			else if (child.getName().equals("rhetrelation"))
				target = makeRhetRelation(child);	
			else
				throw new IOException("Error reading input: rhetrelation or deptree expected");
		}
	}
	
	/** Constructs a dependency tree 
	 *	@param element
	 *	@throws IOException if the input for the deptree is not well-formed
	 *	@return RSDepTreeModel, a RSVertex and a MGraphAdapter which models 
	 *	a Dependency Tree
	 */
	private RSDepTreeModel makeDepTree(Element element) throws IOException
	{
		String index;
		if ((index = element.getAttributeValue("index")) != null)
		{
			if (containsID(index))
				return (RSDepTreeModel) getMVertex(index);
			else
			{
				RSDepTreeModel result = new RSDepTreeModel(element);
				addMVertex(result, index);
				return result;
			}
		}
		else
		{
			RSDepTreeModel result = new RSDepTreeModel(element);
			addMVertex(result);
			return result;
		}
	}
    
   	 /** Constructs a RhetRelation and adds it to the RSGraph
   	  *	@param element
   	  *	@throws IOException, when a child node is not a nucleus or a satellite, 
   	  *	and a grandchild node not a rhetrelation or a deptree
	  *	@return RhetRelation, a RSVertex and MLabeledVertex which models a 
      *	rhetorical relation
      */ 
	private RhetRelation makeRhetRelation(Element element) throws IOException
	{
		String id;
		if (((id = element.getAttributeValue("index")) != null) && (containsID(id)))
			return (RhetRelation) getMVertex(id);
	
		RhetRelation rhet = new RhetRelation(element);
		RSEdge in = new RSEdge("satellite");
		RSEdge out = new RSEdge("nucleus");
		in.setTarget(rhet);
		out.setSource(rhet);
		List children = element.getChildren();
		RSVertex nucleus = null;
		RSVertex satellite = null;
		RSVertex temp;
				
		for (int i = 0; i < children.size(); i++)
		{
			Element child = (Element) children.get(i);
			Element grandchild = (Element) child.getChildren().get(0);	//n or s has only one child
			
			if (grandchild.getName().equals("rhetrelation"))
				temp = (RSVertex) makeRhetRelation(grandchild);
			else if (grandchild.getName().equals("dt"))
				temp = (RSVertex) makeDepTree(grandchild);
			else
				throw new IOException("Error reading input: rhetrelation or deptree expected");
				
			if (child.getName().equals("nucleus"))
				nucleus = temp;
			else if (child.getName().equals("satellite"))
				satellite = temp;
		}
		
		if (nucleus == null )
			throw new IOException("Error reading input: nucleus expected");
		
		if (satellite != null)		//some temporal relations have only a satellite
		{ 
			in.setSource(satellite);
			addMEdge(in);
		}
		out.setTarget(nucleus);
		addMEdge(out);
		
		if (id != null)			
			setID(rhet, id);
		
		System.out.println("made rhetrelation");
		return rhet;
	}
	
	/** Returns an iterator containing all relations in the graph
	 */
	public Iterator getRelations()
	{
		Collection c = new ArrayList();
		Iterator it = getMVertices();
		while (it.hasNext())
		{
			RSVertex n = (RSVertex) it.next();
			if (n.getType().equals("rhetrel"))
				c.add(n);
		}
		return c.iterator();
	}
	
	public RSVertex getRoot(){
		Iterator it = getMVertices();
		if (it.hasNext())
		{
			RSVertex n = (RSVertex) it.next();
			return getRoot(n);
		}
		return null;
	}
	
	public RSVertex getRoot(RSVertex el){
		
		if(el.getParent().hasNext())
			return getRoot((RSVertex)el.getParent().next());
		else return el;
		
	}
	
	/**	Returns the x'th relation in the graph
	 *	@param idx
	 *	@return RhetRelation
	 */ 
	public RhetRelation getRelation(int idx)
	{
		Iterator it = getMVertices();
		int i = 0;
		while (it.hasNext())
		{
			RSVertex result = (RSVertex) it.next();
			if (result.getType().equals("rhetrel"))
			{
				if (i == idx)
					return (RhetRelation) result;
				i++;
			}
		}
		return null;
	}
	
	/**	Returns the first relation in the graph with the label value, or the relation with the index value, 
	 *	depending on attr
	 *	@param attr
	 *	@param value
	 *	@return RhetRelation
	 */
	public RhetRelation getRelation(String attr, String value)
	{
		if (attr.equals("cat"))
		{
			Iterator it = getMVertices();
			while (it.hasNext())
			{
				RSVertex result = (RSVertex) it.next();
				if (result.getType().equals("rhetrel"))
				{
					String lb = ((RhetRelation)result).getLabel();
					if (lb.equals(value))
						return (RhetRelation) result;
				}
			}
		}
		else if (attr == RSTreeNodeData.INDEX)
			if (containsID(value))
				return (RhetRelation) getMVertex(value);
		return null;
	}
	
	/** Returns the deptree indicated by the String
	 * @param index
	 * @return RSDepTreeModel
	 */
	public RSDepTreeModel getDepTree(String index)
	{
		if (containsID(index))
			return (RSDepTreeModel) getMVertex(index);
		return null;
	}
	
	/**	Returns the xth RSDepTreeModel that is not part of a RhetRelation (if it's there)
	 *	@param idx
	 *	@return RSDepTreeModel
	 */			
	public RSDepTreeModel getDepTree(int idx)
	{
		Iterator it = getMVertices();
		int i = 0;
		while (it.hasNext())
		{
			RSVertex result = (RSVertex) it.next();
			if (result.getType().equals("deptree"))
			{
				if (result.getIncidentInMEdges() == null)
				{
					if (i == idx)
						return (RSDepTreeModel) result;
					i++;
				}
			}
		}
		return null;
	}
	
	public void printGoed(RSVertex node)
	{
		if (node.getType().equals("rhetrel"))
		{			
			System.out.println("\nrel: " + ((RhetRelation) node).getLabel());
			
			RSVertex nuc = ((RhetRelation) node).getNucleus();
			RSVertex sat = ((RhetRelation) node).getSatellite();
						
			if (sat != null)// && sat.getType().equals("rhetrel"))
			{
				System.out.println("\nsat:");
				printGoed(sat);
			}
			
			if (nuc != null)// && nuc.getType().equals("rhetrel"))
			{
				System.out.println("\nnuc:");
				printGoed(nuc);
			}			
		}
		else if (node.getType().equals("deptree"))
		{
			System.out.println("node: " + node);
		}
	}
}