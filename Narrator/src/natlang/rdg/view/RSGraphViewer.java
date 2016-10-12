package natlang.rdg.view;

import natlang.rdg.libraries.*;
import natlang.rdg.model.*;
import parlevink.parlegraph.model.*;

import java.util.*;
import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.net.*;

public class RSGraphViewer extends MGraphAdapter implements LibraryConstants
{
	private Document doc;				//JDOM document of the parsed XML file
	private Element root;				//Root element of the parsed XML file
	private MLabeledVertex treeroot;
	
	/** Constructor reads and constructs a RSGraph from an xml-file.
	 *	@param f a File (must be xml)
	 *	@throws an Exception, could be IOException when the input is not well-
	 *	formed, or a JDOMException when there is an error building the file
	 */
	public RSGraphViewer(File f) throws Exception
	{
		super();
		readRDG(f);
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
		if ((root == null) || (!root.getName().equals("rhetdepgraph")))    
    			throw new IOException("Wrong input: rhetdepgraph expected");
    		return true;
    	}
    
    	/**	Constructs the RSGraph 
    	 *	@throws IOException, when the input is not well-formed
    	 */	
    	private void makeRDG() throws IOException
    	{
    		makeRDG(root);
	}	

	/** Reads the input xml and constructs the RDGModel
	 *	@param element (see org.w3c.dom)
	 *	@throws IOException, when a child node is not a rhetrelation or a deptree
	*/
	private MLabeledVertex makeRDG(Element element) throws IOException
	{
		String cat = element.getAttributeValue("cat");
		String pos = element.getAttributeValue("pos");
		MLabeledVertex source, target;
		MLabeledEdge edge;
		
		if (pos != null)
			return adaptLeaf(element);
		else
		{
			if (cat == null)
			{
				source = new MLabeledVertex(element.getName());
				treeroot = source;	//rdg-element
			}
			else
				source = new MLabeledVertex(cat);
			List children = element.getChildren();
		
			for (int i = 0; i < children.size(); i++)
			{	
				Element child = (Element) children.get(i);
				String childRel = child.getAttributeValue("rel");
				
				if (childRel != null)
					edge = new MLabeledEdge(childRel);
				else
				{
					edge = new MLabeledEdge(child.getName());
					while (child.getAttributeValue("cat") == null)
						child = (Element) child.getChildren().get(0);
				}
				
				edge.setSource(source);
				String id = child.getAttributeValue("index");
				if ((id != null) && containsID(id))
					target = (MLabeledVertex) getMVertex(id);
				else
				{
					target = makeRDG(child);
					if (id != null)
						setID(target, id);
				}
				edge.setSource(source);
				edge.setTarget(target);
				addMEdge(edge);
			}
			return source;
		}
	}	
	
	/** Adapts a leaf node so its morph- and root tag can be viewed, by attaching
	 *	another node beneath it 
	 */
	private MLabeledVertex adaptLeaf(Element leaf)
	{
		MLabeledVertex source = new MLabeledVertex(leaf.getAttributeValue("pos"));
		MLabeledVertex target = new MLabeledVertex(leaf.getAttributeValue("root"));
		MLabeledEdge edge = new MLabeledEdge();
		String morph;
		if ((morph = leaf.getAttributeValue("morph")) != null)
			edge.setLabel(morph);
		edge.setSource(source);
		edge.setTarget(target);
		this.addMEdge(edge);
		return source;
	}
	
	/** Returns the root of the tree */
	public MLabeledVertex getRoot()
	{
		return treeroot;
	}
}