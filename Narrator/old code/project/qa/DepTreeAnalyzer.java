package project.qa;

/* 
 *  Last Change: 28 Oktober 2004 Analyzer.java
 *  By: Erik Dikkers
 *  Description: A class for parsing XML files for Dep Trees with JDOM that is extended by AnalyzerQ and AnalyzerF.
 
 * adapted by Rieks op den Akker 
 * added static method for making an MGRaph  (for the DEMO)
 */

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import natlang.deptree.model.*;  // DepTreeModel

import parlevink.parlegraph.model.*;

public class DepTreeAnalyzer {
	
	public Document doc;				//JDOM document of the parsed XML file
	public Element root;				//Root element of the parsed XML file
	
	private StringBuffer sb = new StringBuffer(); 	//stringbuffer for showing all nodes
	

/*
 * Properties for the Formulator defined in sub classes AnalyzerQ & AnalyzerF
	public String Qtype;			//Question Type
	public String whword;			//Identified whword
	public String Qtopic;			//Question Topic
	public String Atype;			//Answer Type
	public String Ftype;			//Fragment Type
	public String Ftopic;			//FRagment Topic
*/
   // SHOULD THROW EXCEPTION !!
	// Basic constructor for creating an empty Analyzer			
	public DepTreeAnalyzer() {
		doc = new Document();
		root = null;	
	}
			
	// Constructor creates a JDOM document and points to the root of that document
    	public DepTreeAnalyzer(File f) {

    		try {
    			SAXBuilder saxbuild = new SAXBuilder();
  	  	    	//SAXBuilder saxbuild = new SAXBuilder(true); //with property true, the builder will validate the xml document
  	  	    						      //by the internal dtd of the xml file. (No DTD available in Alpino
  	  	    						      //parsed file).
    	    		doc = saxbuild.build(f);
    	    		root = doc.getRootElement();
    	    		
    		} catch (Exception ex) {    			
    			ex.printStackTrace();
    			System.out.println("NO ALPINO parsed FILE!"); //in case the property true above is set or error with parsing
    		}
    	}
    	
    	/**
    	 * @param f a File that contains an XML spec of a Dependency Tree
    	 * @return org.jdom.Document build by a org.jdom.SAXBuilder
    	 */
    	public static Document makeDocument(File f) throws Exception {
    		SAXBuilder saxbuild = new SAXBuilder();
  		return saxbuild.build(f);
    	}
    	
    	
    	/**
    	 * @return the Document doc of this Analyzer
    	 * @returns null if something went wrong analysing 
    	 */
    	public Document getDocument(){
    		return doc;
    	}
    	

    	// Return the sentence that was parsed by Alpino and stored between <sentence></sentence> in the XML file	    
    	public String showSentence(String str) {
    		
   		String sentence = "";	
   		
   		if (root != null && root.getName().equals("top")) {   		
   			sentence = root.getChildText("sentence");
   		} else {
   			sentence = "No correct " + str + " file selected!";	
   		}
   		
    		return sentence;
    	}
    	
    	// Return the nodes that are available in the parsed XML file
    	public String showNodes(String str) {
    		
    		sb.append("Overview of the document tree, with all the elements and attributes:\n\n");
    		if (root !=null && root.getName().equals("top")) {
    			return viewElements(root);
    		} else {
    			sb.append("No correct " + str + " file selected!");
    			return sb.toString();
    		}
    	}
    	
    	// Retrieves for each node the relevant information
    	private String viewElements(Element current) {

		List attributes = current.getAttributes();
		Iterator attriterator = attributes.iterator();
		int i = 0;

		sb.append("Element *" + current.getName() + "* has " + attributes.size() + " attributes:\n");
		while(attriterator.hasNext()) {
			sb.append(" "+ attriterator.next() + "\n");
			i++;
		}
		sb.append("\n");
					
		if (!current.getTextTrim().equals("")) {			
			sb.append(" and text: **" + current.getText() + "**");
		}

    		List children = current.getChildren();
    		Iterator childiterator = children.iterator();
    	    	
   		while(childiterator.hasNext()) {
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
   	
   	private static MGraph makeMGraph(Element elem, HashMap indexToVMap){
   		DepTreeModel resultGraph = new DepTreeModel();
   		Element elem1 = (Element)(elem.getChildren().get(0));
   		MLabeledVertex vertex = new MLabeledVertex(elem1.getAttributeValue("cat"));
   		resultGraph.setRoot(vertex);
   		addNodes(resultGraph, elem1, vertex, indexToVMap);
   		return resultGraph;
   	}
   	
   	private static void addNodes(MGraph mg, Element elem, MLabeledVertex vertex, HashMap indexToVMap){
   		List nodes = elem.getChildren();
   		Element el;
   		MLabeledVertex subv, wordvertex;
   		MLabeledEdge edge, wordedge;
   		String indexVal;
   		for (int i=0; i<nodes.size();i++){
   			el = (Element)nodes.get(i);
   			if (el.getAttributeValue("rel")!=null){
   				if (el.getAttributeValue("cat")!=null){
   					subv = new MLabeledVertex(el.getAttributeValue("cat"));
   					if ((indexVal = el.getAttributeValue("index"))!=null ){
   						indexToVMap.put(indexVal,subv);	
   					}
   				}else if (el.getAttributeValue("pos")!=null){
   					subv = new MLabeledVertex(el.getAttributeValue("pos"));
   					indexVal = el.getAttributeValue("index");
   					if (indexVal!=null)
   						indexToVMap.put(indexVal,subv);
   					// add new vertex for the word as well
   					wordvertex = new MLabeledVertex(el.getAttributeValue("word"));
   					// and connect this to the previous vertex
   					wordedge = new MLabeledEdge(el.getAttributeValue("root"));
   					wordedge.setSource(subv);
        				wordedge.setTarget(wordvertex);
        				wordedge.setDirected(true);
        				mg.addMEdge(wordedge);
   				}else{
   				indexVal = el.getAttributeValue("index");
   				if (indexToVMap.get(indexVal)==null){
   					subv = new MLabeledVertex(el.getAttributeValue("index"));
   					indexToVMap.put(indexVal,subv);	
   				}else{
   					subv = (MLabeledVertex)indexToVMap.get(indexVal);
   				}
   				}
   				edge = new MLabeledEdge(el.getAttributeValue("rel"));
        			edge.setSource(vertex);
        			edge.setTarget(subv);
        			edge.setDirected(true);
        			mg.addMEdge(edge);  // new vertices are added to mg with the edge
        			addNodes(mg,el,subv,indexToVMap);
        		}
        			
   		}	
   	}
   	
   	
}