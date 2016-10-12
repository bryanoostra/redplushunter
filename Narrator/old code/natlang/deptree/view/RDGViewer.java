package natlang.deptree.view;

/* 
 *  Last Change: 28 Oktober 2004 Analyzer.java
 *  By: Erik Dikkers
 *  Description: A class for parsing XML files for Dep Trees with JDOM that is extended by AnalyzerQ and AnalyzerF.
 
 * adapted by Rieks op den Akker 
 * added static method for making an MGRaph  (for the DEMO)
 *
 * adapted by Feikje Hielkema RDGViewer.java
 * adapted to view rhetdepgraphs
 */

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import natlang.deptree.model.*;  // DepTreeModel

import parlevink.parlegraph.model.*;

/**
 * @deprecated - not used in current Narrator. Use DependencyViewer in stead?
 * @author swartjes
 *
 */
public class RDGViewer 
{
	public Document doc;				//JDOM document of the parsed XML file
	public Element root;				//Root element of the parsed XML file
	
	private StringBuffer sb = new StringBuffer(); 	//stringbuffer for showing all nodes
		
	// Constructor creates a JDOM document and points to the root of that document
    	public RDGViewer(File f) {

    		try 
    		{
    			SAXBuilder saxbuild = new SAXBuilder();
  	  	    	//SAXBuilder saxbuild = new SAXBuilder(true); //with property true, the builder will validate the xml document
  	  	    						      //by the internal dtd of the xml file. (No DTD available in Alpino
  	  	    						      //parsed file).
    	    		doc = saxbuild.build(f);
    	    		root = doc.getRootElement();
    	    		
    		} 
    		catch (Exception ex) 
    		{    			
    			ex.printStackTrace();
    			System.out.println("NO RhetDepGraph parsed FILE!"); //in case the property true above is set or error with parsing
    		}
    	}
    	
    	/**
    	 * @param f a File that contains an XML spec of a Rhetorical Dependency Graph
    	 * @return org.jdom.Document build by a org.jdom.SAXBuilder
    	 */
    	public static Document makeDocument(File f) throws Exception 
    	{
    		SAXBuilder saxbuild = new SAXBuilder();
  		return saxbuild.build(f);
    	}
    	
    	
    	/**
    	 * @return the Document doc of this Viewer
    	 * @returns null if something went wrong analysing 
    	 */
    	public Document getDocument()
    	{
    		return doc;
    	}
    		
    	// Return the nodes that are available in the parsed XML file
    	public String showNodes(String str) {
    		
    		sb.append("Overview of the document tree, with all the elements and attributes:\n\n");
    		if (root !=null && root.getName().equals("rhetdepgraph")) {
    			return viewElements(root);
    		} else {
    			sb.append("No correct " + str + " file selected!");
    			return sb.toString();
    		}
    	}
    	
    	// Retrieves for each node the relevant information
    	private String viewElements(Element current) 
    	{
		List attributes = current.getAttributes();
		Iterator attriterator = attributes.iterator();
		int i = 0;

		sb.append("Element *" + current.getName() + "* has " + attributes.size() + " attributes:\n");
		while(attriterator.hasNext()) 
		{
			sb.append(" "+ attriterator.next() + "\n");
			i++;
		}
		sb.append("\n");
					
		if (!current.getTextTrim().equals("")) 			
			sb.append(" and text: **" + current.getText() + "**");

    		List children = current.getChildren();
    		Iterator childiterator = children.iterator();
    	    	
   		while(childiterator.hasNext()) 
   		{
     			Element child = (Element) childiterator.next();
     			viewElements(child);
     		}
     		
     		return sb.toString();
   	}
   	
   	//
   	public static MGraph readMGraph(File f) throws Exception {
   		BufferedReader in = new BufferedReader(new FileReader(f));	
   		DepTreeModel  resultGraph = new DepTreeModel();
   		resultGraph.readXML(in);
   		return resultGraph;
   	}
   	
   	
   	public static MGraph makeMGraph(Document doc){
   		HashMap indexToVertexMap = new HashMap();
   		return makeMGraph(doc.getRootElement(),indexToVertexMap);
   		
   	}
   	
   	private static MGraph makeMGraph(Element el, HashMap indexToVMap)
   	{
   		List children = el.getChildren();			//collect list of children (e.g., rhetrelations)
    		Iterator childiterator = children.iterator();
    	    	DepTreeModel resultGraph = new DepTreeModel();
    	    	MLabeledVertex vertex = new MLabeledVertex("RhetDepGraph");
    	    	resultGraph.setRoot(vertex);				//set rhetdepgraph as root
    	    	
   		while(childiterator.hasNext()) 
   		{
     			Element elem1 = (Element) childiterator.next();
 	   		MLabeledVertex vertex1 = new MLabeledVertex(elem1.getAttributeValue("cat"));
   			MLabeledEdge edge = new MLabeledEdge("relation");		//add vertex and edge
   			edge.setSource(vertex);
   			edge.setTarget(vertex1);
        		edge.setDirected(true);
        		resultGraph.addMEdge(edge);
   			addNodes(resultGraph, elem1, vertex1, indexToVMap);
   		}
   		return resultGraph;
   	}
   	
   	private static void addNodes(MGraph mg, Element elem, MLabeledVertex vertex, HashMap indexToVMap)
   	{
   		List nodes = elem.getChildren();
   		Element el;
   		MLabeledVertex subv, wordvertex;
   		MLabeledEdge edge, wordedge;
   		String indexVal;
   		for (int i = 0; i < nodes.size(); i++)
   		{
   			el = (Element)nodes.get(i);
   			if (el.getAttributeValue("rel")!=null)			//Alpino node
   			{
   				if (el.getAttributeValue("cat")!=null)
   				{
   					subv = new MLabeledVertex(el.getAttributeValue("cat"));
   					if ((indexVal = el.getAttributeValue("index"))!=null )
   						indexToVMap.put(indexVal,subv);	
   				}
   				else if (el.getAttributeValue("pos")!=null)
   				{
   					subv = new MLabeledVertex(el.getAttributeValue("pos"));
   					indexVal = el.getAttributeValue("index");
   					if (indexVal!=null)
   						indexToVMap.put(indexVal,subv);
   					// add new vertex for the root as well
   					wordvertex = new MLabeledVertex(el.getAttributeValue("root"));
   					// and connect this to the previous vertex using if possible morph
    					if (el.getAttributeValue("morph") != null)
 						wordedge = new MLabeledEdge(el.getAttributeValue("morph"));
 					else
 						wordedge = new MLabeledEdge("-");
   					wordedge.setSource(subv);
        				wordedge.setTarget(wordvertex);
        				wordedge.setDirected(true);
        				mg.addMEdge(wordedge);
   				}
   				else
   				{
   					indexVal = el.getAttributeValue("index");
   					if (indexToVMap.get(indexVal)==null)
   					{
   						subv = new MLabeledVertex(el.getAttributeValue("index"));
   						indexToVMap.put(indexVal,subv);	
   					}
   					else
   						subv = (MLabeledVertex)indexToVMap.get(indexVal);
   				}
   				edge = new MLabeledEdge(el.getAttributeValue("rel"));       			    		
        		}
        		else if (el.getAttributeValue("cat") != null)	//rhetrelation
        		{
        			subv = new MLabeledVertex(el.getAttributeValue("cat"));
        			edge = new MLabeledEdge("relation");
        		}
        		else 						 //nucleus, satellite or dt
        		{
        			indexVal = el.getAttributeValue("index");
        			if (indexToVMap.get(indexVal)==null)
   				{
   					subv = new MLabeledVertex(el.getAttributeValue("index"));
   					indexToVMap.put(indexVal, subv);	
   				}
   				else
   					subv = (MLabeledVertex)indexToVMap.get(indexVal);
   				edge = new MLabeledEdge(el.getName());
   			}
   			
		      	edge.setSource(vertex);
        		edge.setTarget(subv);
        		edge.setDirected(true);		
        		mg.addMEdge(edge);  // new vertices are added to mg with the edge
        		addNodes(mg,el,subv,indexToVMap);		
   		}	
   	}  	   	
}