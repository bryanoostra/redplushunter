package project.qa;

/*Last change: 28 Oktober Analyzer.java
   By: Erik Dikkers
   Description: A sub class of Analyzer for analyzing a question XML file.
*/

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;

public class AnalyzerQ extends Analyzer {
	
	// Properties necessary for the Formulator	
	public String Qtype = "ERROR";				//Question Type
	public String whword = "ERROR";				//Identified whword
	public String Qtopic = "ERROR";				//Question Topic
	public String Atype = "ERROR";				//Answer Type
	public ArrayList QtopicElements = new ArrayList();	//Contains the element that form the topic
	
	private String tmpTopic;			//contains one word of the topic	
	private StringBuffer sb = new StringBuffer(""); //stringbuffer for question topic	
	private final String space = " ";		//creating a " " string to add where necessary 
	public boolean isQuestion = true;		//when NO question file was selected
	private boolean foundpos = false;		//identifying the pos=noun, pos=name, pos=adv & pos=prep
	private boolean foundnp = false;		//identifying the cat=np relation
	private boolean foundsv1 = false;		//identifying the cat=sv1 relation
	private boolean foundppart = false;		//identifying cat=ppart relation
	private boolean foundti = false;		//identifying cat=ti relation

	// Basic constructor for creating an empty Analyzer			
    	public AnalyzerQ() {
		super();
    	} 
    	
	// Constructor makes use of the super constructor for creating a JDOM
	// document and points to the root of that document
    	public AnalyzerQ (File f) {
    		super(f);
		
		// Actual analysis
		if (root!=null && root.getName().equals("top")) {
			// performing the necessary operations to retrieve the question details
    			analyzeTypeQ();
    			if (isQuestion==true) {
	    			analyzeTopicQ(root);
    				for (int i=0;i<QtopicElements.size();i++) {
    					Element elm = (Element) QtopicElements.get(i);
    					sb.append(elm.getAttributeValue("word") + space);
    				}
    				Qtopic = sb.toString();
    			} else {
    				Qtopic = "NO question";	
    			}	
    		} else {
    			System.out.println(root.getName() + " Invalid start of Alpino parsed file!, must be top");
    		}
    	}
        	
    	// Analyzing the Question Type and accompanying Answer Type
    	private void analyzeTypeQ() {
    		
		Element child = root.getChild("node"); //first relation in the dependency tree
								
		// if first relation catergory is "whq", it is a WH-word question.
		if (child.getAttributeValue("cat").equals("whq")) {
			Qtype = "WH-word";	
			Element child1 = child.getChild("node"); //first word in the sentence within a WH-word question.
			
			if (child1.getAttributeValue("word")==null) { // if there exists another relation before the WH-word can be read
								      // further investigation is necessary							      
				Element child2 = child1.getChild("node");
				whword = child2.getAttributeValue("word").toLowerCase();				
				
				if (whword.equals("welke")) { Atype = "Alternative"; } // classification of NP for "Welke ..."
				else if (whword.equals("hoeveel")) { Atype = "Quantity/Amount";}
				else { Atype = "For future usage";}
			} else {
				whword = child1.getAttributeValue("word").toLowerCase();		
				//if wh-word welke/wiens/hoeveel/hoe lang/hoe vaak/ hoe ver/welke soort extra information is available.
				if (whword.equals("wie")) { Atype= "Person"; }
				else if (whword.equals("wanneer")) { Atype = "Time"; }
				else if (whword.equals("wat")) { Atype = "(Object/Idea/Action)"; }
				else if (whword.equals("wiens")) { Atype = "Possession"; }
				else if (whword.equals("waar")) { Atype = "Place"; } //attention, waar voor, waar in, waar door
				else if (whword.equals("waarvoor")) { Atype = "(Object/Idea/Action)";}
				else if (whword.equals("waarin")) { Atype = "Place";}
				else if (whword.equals("waardoor")) { Atype = "Manner";}
				else if (whword.equals("hoe")) { Atype = "Manner"; } //attention, hoe veel, hoe lang, hoe vaak
				else if (whword.equals("hoelang")) { Atype = "Duration";}
				else if (whword.equals("hoeveel")) { Atype = "Quantity/Amount";}
				else if (whword.equals("hoevaak")) { Atype = "Frequency";}
				// else wanneer/waar/wie/waarom/wat/hoe enough to know what is asked about.	
				else { Atype = "For future usage";}				
			}
								
		// if relation catergory is "sv1", it is a sentence with a verb on the first spot
		// this represents an yes/no question based on a corpus evaluation.
		} else if (child.getAttributeValue("cat").equals("sv1")) { 
			Qtype = "Yes/No";
			Atype = "Boolean";
			whword = "NONE";
		
		// if there exists a 'of' in the sentence, it is a 'choice' question.			
		} else if (false){
			// walking through all elements and checking for the word 'of'
			// skip for now and focus on the boolean and wh-word
			Qtype = "Choice";
			Atype = "Alternative";
			whword = "NONE";
		
		// if no question selected, the types display "ERROR".
		} else {
			isQuestion = false;
			//root = null;
		}
    	}
    	
    	// Analyzing the Question Topic
      	private void analyzeTopicQ(Element current) {
    		List children = current.getChildren("node");
       		List attributes = current.getAttributes();
       		Iterator iteratorChild = children.iterator();
       		Iterator iteratorAttr = attributes.iterator();
       		String tmpword;
       		
       		// For each element in the tree, look for appropriate properties
       		// When elements of question topic are identified, add them to ArrayList topicElements.
       		
       		// Seperate instance for wat, welke and hoe, one class for the other whwords
       		// Different selection for yes/no and choice questions
       		while (iteratorAttr.hasNext()) {
       			Attribute attr = (Attribute) iteratorAttr.next();
       			
       			//*possibilities for whword: wat
       			if (whword.equals("wat")) {
       				// "Wat is er toe doen tegen RSI?" -> tegen RSI works not good enough.
       				if (attr.getValue().equals("sv1") && !foundsv1) {
	       				foundsv1 = true;	
       				} else if (attr.getValue().equals("np")) {
       					foundnp = true;
       				} else if ( (attr.getValue().equals("name") || attr.getValue().equals("name(ORG)") || attr.getValue().equals("noun") || attr.getValue().equals("prep")) && foundsv1) {    		
       					foundpos = true;
       				} else if ( (attr.getName().equals("word")) && (foundpos || foundnp)) { 
       					tmpTopic = attr.getValue();
       					QtopicElements.add(current);
       					foundpos = false;
       				}
       			} //*possibilities for whword: welke, hoeveel
       			else if (whword.equals("welke") || whword.equals("hoeveel")) {
       				// usually in the form "Welke *** verb [noun]/[name], so topic should
				// noun, name, adj & prep are likely to represent the question topic
				// not np, because "Welke" is placed inside np relation
       				if (attr.getValue().equals("noun") || attr.getValue().equals("name") || attr.getValue().equals("name(ORG)") || attr.getValue().equals("adj") || attr.getValue().equals("prep")) {
       					foundpos = true;       					
       				} else if (attr.getName().equals("word") && foundpos) {
 	      				tmpTopic = attr.getValue();
 	      				QtopicElements.add(current);
       					foundpos = false; 				
       				}
       			} //*possibilities for yes/no questions
       			// make use of sv1 relation in this type of question
       			else if (Qtype.equals("Yes/No")){
				       				       							
       				if (attr.getValue().equals("noun") || attr.getValue().equals("name") || attr.getValue().equals("name(ORG)")) {
       					foundpos = true;       				
       				} else if (attr.getName().equals("word") && foundpos) {
       					tmpTopic = attr.getValue();
       					QtopicElements.add(current);    					
       				} 
       			}//*other possibilities: hoe, waar, waardoor, wanneer
       			//not al possible words are catched, to indicate that there are wh-words that are not covered
       			//there is an extra else clause that indicated the topic as !!!!!!
       			else if (whword.equals("hoe") || whword.equals("waar") || whword.equals("waardoor") || whword.equals("wanneer") || whword.equals("waarvoor")) {       				
       				if (attr.getValue().equals("np")) {
       					foundnp = true;
       				} else if (attr.getValue().equals("ppart")) {
       					foundppart = true;
       				} else if (attr.getValue().equals("ti")) {
       					foundti = true;
   				} else if ( attr.getValue().equals("name") || attr.getValue().equals("name(ORG)") || attr.getValue().equals("noun") || attr.getValue().equals("adj") || attr.getValue().equals("prep")) {    		
	       				foundpos = true;   	
       				} else if ( !current.getAttributeValue("rel").equals("whd") && attr.getName().equals("word") && (foundpos || foundnp || foundppart || foundti)) {
 	      				tmpTopic = attr.getValue();
 	      				Element elm = current.getParentElement();
 	      				
 	      				//if special cases are found: ppart, ti, etc extra checking necessary
 	      				if (foundppart == true) {
						if (elm.getAttributeValue("cat").equals("ppart")) {
 	      						QtopicElements.add(current);
 	      					}
 	      				} else if (foundti == true) {
 	      					if (elm.getAttributeValue("cat").equals("ti")) {
 	      						QtopicElements.add(current);
 	      					}
 	      				} else {
 	      					QtopicElements.add(current);
 	      				}
 	      				foundpos = false; 	
       				}
       			}

// to catch questions that are not recognized.
       			else {
       				sb.append("!"); 
       			}
       		}
       		
       		// recursive walking through the JDOM tree
    		while (iteratorChild.hasNext()) {
      			Element child = (Element) iteratorChild.next();
      			analyzeTopicQ(child);
    		} 
    	}
}