package natlang.structure;

/*Last change: 28 Oktober Analyzer.java
   By: Erik Dikkers
   Description: A sub class of Analyzer for analyzing a question XML file.
   
   Adapted by Feikje Hielkema to AnalyzerRDG, a subclass of Analyzer for analyzing a rhetdepgraph XML file
*/

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;

public class AnalyzerRDG extends Analyzer 
{	
	private StringBuffer sb = new StringBuffer(""); //stringbuffer for question topic	


	// Basic constructor for creating an empty Analyzer			
//    	public AnalyzerRDG() 
//    	{
//		super();
//    	} 
    	
	// Constructor makes use of the super constructor for creating a JDOM
	// document and points to the root of that document
    	public AnalyzerRDG (File f) 
    	{
    		super(f);
		
		// Actual analysis
		if ((root==null) || !root.getName().equals("rhetdepgraph"))
			// performing the necessary operations to retrieve the question details
    			// analyzeTypeQ();
       			System.out.println(root.getName() + " Invalid start of Alpino parsed file!, must be top");
    	}
       	    	
   	// Return the nodes that are available in the parsed XML file
    	public String showNodes(String str) 
    	{    	
    		sb.append("Overview of the document tree, with all the elements and attributes:\n\n");
    		if (root != null && root.getName().equals("rhetdepgraph")) {
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
}