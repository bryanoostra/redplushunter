package project.qa;

/*Last change: 28 Oktober Analyzer.java
   By: Erik Dikkers
   Description: A sub class of Analyzer for analyzing a fragment XML file.
*/

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;

public class AnalyzerF extends Analyzer {
		
	// Properties necessary for the Formulator
	public String Ftype = "ERROR";			//Fragment Type
	public String Ftopic = "ERROR";			//Fragment Topic
	public ArrayList posTopics = new ArrayList();	//Contains all possibe topics
	
	private Integer beginIndex;			//contains the begin index value for a subject
	private Integer endIndex;			//contains the end index value for a subject
	private StringBuffer sb = new StringBuffer(); 	//stringbuffer for question topic
	private final String space = " ";		//creating a " " string to add where necessary 
	
	// Basic constructor for creating an empty Analyzer			
     	public AnalyzerF() {
		super();
    	} 			
			
	// Constructor makes use of the super constructor for creating a JDOM
	// document and points to the root of that document
    	public AnalyzerF (File f) {
    		super(f);
    		
    		// Actual analysis
    		if (root!=null && root.getName().equals("top")) {
    			// performing the necessary operations to retrieve the fragment details
			analyzeTypeF();
			posSubjectsF(root);	
			
			for (int i=0;i<posTopics.size();i++){
				PosTopicIndex pti = (PosTopicIndex) posTopics.get(i);
				pti.setTopicNumber(i);			
				analyzeTopicF(root,pti);
			}
			
			retrieveOrder();
			retrieveSubjects();
			
		}   else {
    			System.out.println(root.getName() + " Invalid start of Alpino parsed file, must be top!");
    		}			
    	}

    	// Analyzing the Fragment Type
    	private void analyzeTypeF() {
    		
    		Ftype = "Sentence";
    	 	// case scenario of text snippet, sentence, or paragraph.
    	 	// in this research only the usage of sentences is explored.
    	}
    	
    	// Identifying the possible subjects of the analyzed fragment
    	private void posSubjectsF(Element current) {
    		List children = current.getChildren("node");
       		List attributes = current.getAttributes();
       		Iterator iteratorChild = children.iterator();
       		Iterator iteratorAttr = attributes.iterator();
       		
       		String tmpword;
       		
       		//for each element in the tree, look for appropriate details
       		//walk through tree and identify the possible subjects (rel=su, index=? and rel=obj1 with index=?).
		while (iteratorAttr.hasNext()) {
       			Attribute attr = (Attribute) iteratorAttr.next();
       			Element parent = (Element) current.getParentElement();

       			//if subject relation is inside the dependency, very likely to be the sentence topic
       			//not necessary the discourse topic.
       			if (attr.getValue().equals("su") && current.getAttributeValue("index") == null) {
       				System.out.println("found su without index");    
       				beginIndex = new Integer(current.getAttributeValue("begin"));
       				endIndex = new Integer(current.getAttributeValue("end"));  
       				PosTopicIndex pti = new PosTopicIndex(beginIndex, endIndex);
       				pti.setElement(current);
       				posTopics.add(pti);
       			}
       			else if ( attr.getValue().equals("su") && current.getAttributeValue("index") != null && (current.getAttributeValue("cat") != null || current.getAttributeValue("word") != null))  {
       				System.out.println("found su with index & word");       				
       				beginIndex = new Integer(current.getAttributeValue("begin"));
       				endIndex = new Integer(current.getAttributeValue("end"));       				
       				PosTopicIndex pti = new PosTopicIndex(beginIndex,endIndex);
       				pti.setElement(current);
       				posTopics.add(pti);
       			} 
       			else if ( attr.getValue().equals("obj1") && current.getAttributeValue("index") != null && (current.getAttributeValue("cat") != null || current.getAttributeValue("word") != null))  {
       				System.out.println("found obj1 with index & word");       				
       				beginIndex = new Integer(current.getAttributeValue("begin"));
       				endIndex = new Integer(current.getAttributeValue("end"));       				
       				PosTopicIndex pti = new PosTopicIndex(beginIndex,endIndex);
       				pti.setElement(current);
       				posTopics.add(pti);
       			}       			
       		} 				      		
       		       		
       		//recursive walking through the JDOM tree
    		while (iteratorChild.hasNext()) {
      			Element child = (Element) iteratorChild.next();
      			posSubjectsF(child);
    		} 
    	}	
    	
    	// Using the identified subjects to store the possible subject elements
    	private void analyzeTopicF(Element current, PosTopicIndex pti) {		
    		List children = current.getChildren("node");
       		List attributes = current.getAttributes();
       		Iterator iteratorChild = children.iterator();
       		Iterator iteratorAttr = attributes.iterator();
       		
       		String tmpword;
       		
		while (iteratorAttr.hasNext()) {
       			Attribute attr = (Attribute) iteratorAttr.next();
       			Element parent = (Element) current.getParentElement();
       			
			if (attr.getName().equals("begin") && current.getAttributeValue("word") != null) {
				Integer attrBegin = new Integer(current.getAttributeValue("begin"));
	      			Integer attrEnd = new Integer(current.getAttributeValue("end"));
       				
      				if (attrBegin.intValue() >= pti.getBeginIndex().intValue() && attrEnd.intValue() <= pti.getEndIndex().intValue()) {
      					//System.out.println("Retrieving subject from " + pti.getBeginIndex() + " till " + pti.getEndIndex() + " topic " + pti.getTopicNr());
      					pti.add(current, attrBegin);
     				}
			}
		}
				       		       		
       		//recursive walking through the JDOM tree
    		while (iteratorChild.hasNext()) {
      			Element child = (Element) iteratorChild.next();
      			analyzeTopicF(child, pti);
    		} 
    	}	
    	
    	// Retrieving the lineair order for the identified subjects
    	public void retrieveOrder() {
    		
    		for (int i=0;i<posTopics.size();i++) {
    			PosTopicIndex pti = (PosTopicIndex) posTopics.get(i);
    			final ArrayList arl = pti.getTopicElements();
    			
			// Lineair order of the subject has to be checked
			Collections.sort(arl, new Comparator()
			{
				public int compare(Object obj1, Object obj2) {
					int n1 = arl.indexOf(obj1);
					int n2 = arl.indexOf(obj2);
					IndexWordPair iwp1 = (IndexWordPair) arl.get(n1);
					IndexWordPair iwp2 = (IndexWordPair) arl.get(n2);
					return (iwp1.getIndex()).compareTo(iwp2.getIndex());
				}
			});
			
			//replace elements with the correct lineair order.
			pti.elements = arl;
		}
	}
	
	// Retrieving all the subjects, seperated by "<> " to show to the user
	public void retrieveSubjects() {
		
		for (int j=0;j<posTopics.size();j++) {
			PosTopicIndex pti = (PosTopicIndex) posTopics.get(j);
			ArrayList arlist = pti.getTopicElements();
			for (int k=0;k<arlist.size();k++) {
				IndexWordPair iwp = (IndexWordPair) arlist.get(k);
				sb.append(iwp.getWord());
			}
			sb.append("<> ");
		}
		
		Ftopic = sb.toString();
    	}
}