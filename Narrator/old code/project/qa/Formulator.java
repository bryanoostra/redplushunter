package project.qa;

/*Last change: 28 Oktober 2004 Analyzer.java
   By: Erik Dikkers
   Description: A class for working with the properties provided by the Analyzer module.
   Capable of retrieving the information and generating the final answer.
*/

import java.io.*;
import org.jdom.*;
import org.jdom.output.*; 
import java.util.*;
import java.util.regex.*;

public class Formulator {
	
	//properties that will be shown in the end
	public String Tplacement = "ERROR";		//Placement of topic in the answer < 10, in front segment.
	public String algorithmCase = "ERROR";		//what case is selected through the algorithm
	public String generatedAnswer = "ERROR";	//in the end generated Answer
	
	private AnalyzerQ anaQ;				//Analyzed Questions
	private AnalyzerF anaF;				//Analyzed Fragment
	private Document answerDoc;			//JDOM model will be revised (forms the answer in the end)
	private Element answerRoot;			//Root of answer document
	private ArrayList QtopicElements;		//List of all the question topic elements (words)
	private ArrayList posSubjects;			//list of all found subjects (PosTopicIndex instances)
	
	private Element topic = new Element("node");		//for new Qtopic Element for insertion
	private boolean foundMatch = false;			//indicates if a question (possible topic) match is found
	private boolean addedTopic = false;			//indicates if a topic has already been inserted
	private boolean frontingNeeded = false;			//indicates if fronting is necessary for an identified topic
	private ArrayList transformElements = new ArrayList();	//element that needs a transformation
	private PosTopicIndex identifiedTopic = new PosTopicIndex(new Integer("-1"),new Integer("-1"));
	private PosTopicIndex identifiedComment = new PosTopicIndex(new Integer("-1"),new Integer("-1"));
	
	private StringBuffer answerSentenceBuffer = new StringBuffer(); 		//stringbuffer for the generated answer
	private List position = new ArrayList();   					//list that contains all the IndexWordPairs for lineair sorting	
	private StringBuffer showNodes = new StringBuffer(); 				//stringbuffer for showing all nodes
	private XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat()); 	//for writing the answer XML file
	private DataOutputStream output;						//for writing to an output file

	//private ArrayList topicElements = new ArrayList();
	//private ArrayList commentElements = new ArrayList();
	//private StringBuffer commentBuffer = new StringBuffer();
			
	// Constructor creates a JDOM document and points to the root of that document
    	public Formulator(AnalyzerQ Q, AnalyzerF F) {
    		anaQ = Q;
    		anaF = F;
    		
    		// Create a copy of the JDOM model of the selected fragment to do revision
    		// this will be the document that contains the newly revised answer in the end and
    		// will be written to the XML output file
    		answerDoc = F.doc;
    		answerRoot = answerDoc.getRootElement();
    		
    		QtopicElements = Q.QtopicElements;
    		posSubjects = anaF.posTopics;
    		
    		/*// checking all stored subjects
    		for (int j=0;j<posSubjects.size();j++) {
    			PosTopicIndex pti = (PosTopicIndex) posSubjects.get(j);
    			ArrayList arlist = pti.getTopicElements();
			//ArrayList arlist = (ArrayList) posSubjects.get(j);
			for (int k=0;k<arlist.size();k++) {
				IndexWordPair iwp = (IndexWordPair) arlist.get(k);
				System.out.println(iwp.getWord());
				//showNodes.append(iwp.getWord());
			}
		}/*
		
		/*
		checking all the question elements
		for (int j=0;j<QtopicElements.size();j++) {
			Element elm = (Element) QtopicElements.get(j);
			showNodes.append(elm.getAttributeValue("word"));
		}*/
		
		// Perform the formulator actions
		QtopicMatcher(answerRoot);
		reviseQTopic();
		identifyTC(answerRoot, posSubjects);
    		performTransformations(answerRoot);
    		topicFronting();
    		// other necessary operations
    		retrieveLineairOrder();
    		writeToFile();
    	}
    	
    	// When identified and changed topic needs fronting, the topic element is placed
    	// before everything else through the "-1" beginIndex option.
    	private void topicFronting() {
    		if (frontingNeeded == true) {
    			Element frontedTopic = (anaF.root).getChild("node");
    			topic.setAttribute("begin","-1");
    			topic.setAttribute("end","0");
			frontedTopic.addContent(0,topic);
			
			String begin = topic.getAttributeValue("begin");
			if (begin !=null && topic.getAttributeValue("word")!=null) {
				Integer i = new Integer(begin);
				IndexWordPair pair = new IndexWordPair(i,topic);
				position.add(pair);
			}
    		}	
    	}
    	
    	// Change the topic according the some metrics !!!!! not clear how !!!!
    	// at the moment just creating the topic element with Qtopic
    	private void reviseQTopic() {
    		
    		// create topic element for insertion into for topic - comment structure
    		// further revision of Qtopic might be necessary based on order.
		topic.setAttribute("rel","Qtopic");	//rel
		topic.setAttribute("pos","Qtopic");	//pos
		topic.setAttribute("begin","0");	//begin
		topic.setAttribute("end","0");		//end
		topic.setAttribute("root","Qtopic");	//root
		topic.setAttribute("word",anaQ.Qtopic);	//topic string	
    	}

	// Compare the Qtopic with the found possible subjects in the fragment
	// if elements of Qtopic exist in a subject, it is identified for topic replacement.
	// else if no element of Qtopic exist, it is identifed as comment structure.
	private void QtopicMatcher(Element current) {
		List children = current.getChildren("node");
		int score = 0;
		
		if (current.getAttributeValue("word") != null) {
			String curWord = current.getAttributeValue("word");
			String posWord = current.getAttributeValue("pos");
			
			boolean posCheck = Pattern.matches("name.*", posWord);
			
			// only matching with noun, name words
			if (posWord.equals("noun") || posCheck == true) {
		
				for (int j=0;j<QtopicElements.size();j++) {
					Element elm = (Element) QtopicElements.get(j);
					String pos = elm.getAttributeValue("pos");
				
					// checking for occurence of name, name(ORG), name(LOC), etc
					// excluding preps like, 'van' 'door', etc
					boolean name = Pattern.matches("name.*", pos);
					
					if (pos.equals("noun") || name == true) {
						String elmWord = elm.getAttributeValue("word");
					
						//create pattern [Any character]*elmWord[Any character]*
						Pattern p = Pattern.compile(".*" + elmWord.toLowerCase() + ".*");
 						Matcher m = p.matcher(curWord.toLowerCase());
 						boolean b = m.matches();

 						//create pattern [Any character]*curWord[Any character]*
 						Pattern p1 = Pattern.compile(".*" + curWord.toLowerCase() + ".*");
 						Matcher m1 = p1.matcher(elmWord.toLowerCase());
 						boolean b1 = m1.matches();

						if(b == true || b1 == true) {
							transformElements.add(current);
							foundMatch = true;
							System.out.println("Found a match with Qelement, " + curWord.toLowerCase() + " matches with " + elmWord.toLowerCase());
							System.out.println("                         or, " + elmWord.toLowerCase() + " matches with " + curWord.toLowerCase());
						} 
					}
				}
			}
		}
		
		Iterator iterator = children.iterator();
    		while (iterator.hasNext()) {
	   		
	   		Element child = (Element) iterator.next();
      			QtopicMatcher(child);
    		}           	
	}
	
	//Revision of Qtopic for Topic role in the answer text (topic - comment pattern)
	private void identifyTC(Element current, ArrayList arl) {

		// if Q <-> A match is found, further investigation and revision is necessary
		if (foundMatch == true) {
			for (int i=0;i<transformElements.size();i++) {
				Element elm = (Element) transformElements.get(i);
				Integer elmBegin = new Integer(elm.getAttributeValue("begin"));
				Integer elmEnd = new Integer(elm.getAttributeValue("end"));
				
				for (int j=0;j<arl.size();j++) {
					PosTopicIndex pti = (PosTopicIndex) arl.get(j);
					Integer begin = pti.getBeginIndex();
					Integer end = pti.getEndIndex();		
					
					// if match is found inside a already identified possible topic, increase the possible topics score
					if (elmBegin.intValue() >= begin.intValue() && elmEnd.intValue() <= end.intValue()) {
						pti.increaseScore();	
					}	
					arl.set(j,pti);
				}
			}

			// set the subject with highest score to be the identified discourse topic
			// otherwise set the subject to have the function as comment
			if (arl.size() != 0) {
				for (int j=0;j<arl.size();j++) {
					PosTopicIndex pti = (PosTopicIndex) arl.get(j);
					int score = pti.getScore();
				
					if (score > identifiedTopic.getScore() && score != 0) {
						identifiedTopic = pti;			
						algorithmCase =	"*** Match found and one subject very likely to be topic ***";		
						System.out.println("*** Match found and one subject very likely to be topic ***");		
					} else if (pti.getTopicElements().size() >= 2) {
						identifiedComment = pti;	
						algorithmCase =	"*** Match found and one subject very likely to be comment ***";	
						System.out.println("*** Match found and one subject very likely to be comment ***");		
					} else { //create a new posTopicIndex for the found match, that was not originally indicated as subject
						Element nonIdentifiedMatch = (Element) transformElements.get(0);
						Integer elmB = new Integer(nonIdentifiedMatch.getAttributeValue("begin"));
						Integer elmE = new Integer(nonIdentifiedMatch.getAttributeValue("end"));
						PosTopicIndex ptinew = new PosTopicIndex(elmB,elmE);
						ptinew.setElement(nonIdentifiedMatch);
						ptinew.add(nonIdentifiedMatch,elmB);
						identifiedTopic = ptinew;
						algorithmCase = "*** Match found but not inside an identified subject ***";
						System.out.println("*** Match found but not inside an identified subject ***");
					}
				}
			} 
			else { //create a new posTopicIndex for the found match, that was not originally indicated as subject
				Element nonIdentifiedMatch = (Element) transformElements.get(0);
				Integer elmB = new Integer(nonIdentifiedMatch.getAttributeValue("begin"));
				Integer elmE = new Integer(nonIdentifiedMatch.getAttributeValue("end"));
				PosTopicIndex ptinew = new PosTopicIndex(elmB,elmE);
				ptinew.setElement(nonIdentifiedMatch);
				ptinew.add(nonIdentifiedMatch,elmB);
				identifiedTopic = ptinew;
				algorithmCase = "*** Match found but no subject identified ***";
				System.out.println("*** Match found but no subject identified ***");
			}
		} 
		// no match found, but still making use of found subject(s) in fragment
		else if (foundMatch == false && posSubjects.size() != 0){
			if (arl.size() == 1) {		
				PosTopicIndex pti = (PosTopicIndex) arl.get(0);
				int size = pti.getTopicElements().size();
				
				// short subject identified as subject, long subject as comment. 2 or 3, that's the question.
				if (size <= 3) {
					identifiedTopic = pti;	
					algorithmCase = "*** No match is found, 1 short subject found -> topic ***";
					System.out.println("*** No match is found, 1 short subject found -> topic ***");	
				} else {
					identifiedComment = pti;	
					algorithmCase = "*** No match is found, 1 long subject found -> comment ***";
					System.out.println("*** No match is found, 1 long subject found -> comment ***");	
				}
			} else if (arl.size() != 0 && arl.size() > 1) {
				identifiedTopic = (PosTopicIndex) arl.get(0);	
				
				for (int j=0;j<arl.size();j++) {
					PosTopicIndex pti = (PosTopicIndex) arl.get(j);
					int size = pti.getTopicElements().size();
		
					if (size < identifiedTopic.getTopicElements().size() && size <= 2) {	
						identifiedTopic = pti;
					} 					
				}
				// a better solution would be the use of semantic tags, to see what subject is the best topic
				algorithmCase = "*** No match is found, subjects were found -> shortest identified as topic ***";
				System.out.println("*** No match is found, subjects were found -> shortest identified as topic ***");	
			}
		}
		// total fragment is identified as comment, insert topic at start and fragment forms the topic. 
		else {
			System.out.println("*** No subject and no match is found, total fragment identified as comment ***");
			Element totolCoverElm = (anaF.root).getChild("node");			
			Integer elmB = new Integer(totolCoverElm.getAttributeValue("begin"));
			Integer elmE = new Integer(totolCoverElm.getAttributeValue("end"));
			PosTopicIndex ptinew = new PosTopicIndex(elmB,elmE);
			ptinew.setElement(totolCoverElm);
			ptinew.add(totolCoverElm,elmB);
			identifiedComment = ptinew;
			
			//commentElements = 
			//Element topChild = current.getChild("node");
			//if (topChild.getAttributeValue("rel") != null) {
			//	current.addContent(0, topic);
			//}
		}
	}

	// Providing the answer by building the answer sentence from the answerDoc object
   	private void performTransformations(Element current) {
   		List children = current.getChildren("node");
   		Iterator iterator = children.iterator();
   		String addition;
   		   		
		// replace the element that contains the topic with the Qtopic (topic - comment structure)   		   		
       		if (current == identifiedTopic.getElement()) {
       			if (current.getAttributeValue("word") != null) {
       				//current.setAttribute("word",topic.getAttributeValue("word"));
       				if (current.getAttributeValue("begin") != null && current.getAttributeValue("end") != null) {
       					String beginindex = current.getAttributeValue("begin");
       					Integer beginIn = new Integer(beginindex);
   					topic.setAttribute("begin", beginindex);
					topic.setAttribute("end",current.getAttributeValue("end"));
					
					if (beginIn.intValue() < 5) {
						Tplacement = "Front segment";
						current.setAttribute("word",topic.getAttributeValue("word"));
					} else {
						Tplacement = "Needs fronting";
						current.setAttribute("word","***");
						frontingNeeded = true;
					}
				}
			} else {       				
       				//current.setContent(topic);
       				if (current.getAttributeValue("begin") != null && current.getAttributeValue("end") != null) {
   					String beginindex = current.getAttributeValue("begin");
       					Integer beginIn = new Integer(beginindex);
   					topic.setAttribute("begin",beginindex);
					topic.setAttribute("end",current.getAttributeValue("end"));
					
					if (beginIn.intValue() < 5) {
						Tplacement = "In front segment";
						current.setContent(topic);  	 					
					} else {
						Tplacement = "Needs fronting";
						topic.setAttribute("word", "***");
						frontingNeeded = true;
						//current.setContent(topic);
					}
				}
       				children = current.getChildren("node");
       				iterator = children.iterator();
       			}
       		}
       		
       		// insert Qtopic at start of element that contains the comment (topic - comment structure)
       		if (current == identifiedComment.getElement() && addedTopic == false) {
       			if (current.getAttributeValue("begin") != null && current.getAttributeValue("end") != null) {
       				String beginindex = current.getAttributeValue("begin");
       				Integer beginIn = new Integer(beginindex);
       				topic.setAttribute("begin",beginindex);
				topic.setAttribute("end",current.getAttributeValue("end"));
				
				if (beginIn.intValue() < 5) {
					Tplacement = "In Front segment";
  	 				System.out.println("Topic in front segment");	
				} else {
					Tplacement = "Needs fronting";
   					System.out.println("Topic needs fronting");	
				}
			}			
	       		current.addContent(0,topic);
	       		addedTopic = true;
       			children = current.getChildren("node");
       			iterator = children.iterator();
       		}
       		
       		if (current.getAttributeValue("begin") != null) {
       			Integer beginElm = new Integer(current.getAttributeValue("begin"));
       			
       			if (identifiedComment.getBeginIndex().intValue() != -1) {
       			
       				if ( beginElm.intValue() < identifiedComment.getBeginIndex().intValue() || beginElm.intValue() >= identifiedComment.getEndIndex().intValue()) {
       					//System.out.println(current.getAttributeValue("word") + " is set to '_'");
       					current.setAttribute("word", "");
       				}
       			}
       		}

       		// elements that are not between the indexes should be deleted
       		// best option to realize after the topic insertion -> replacement
       		
       		
       		      		
       		//indicaters for what is identified as topic and what as comment
    		/*for (int i=0;i<topicElements.size();i++) {
    			IndexWordPair iwp = (IndexWordPair) topicElements.get(i);
    			Element elm = (Element) iwp.getElement();
	       		
	       		if (elm == current) {
	       			commentBuffer.append(current.getAttributeValue("word") + " ");
       				current.setAttribute("word", "topic");	
       			}
       		}
       		
       		for (int i=0;i<commentElements.size();i++) {
			IndexWordPair iwp = (IndexWordPair) commentElements.get(i);
    			Element elm = (Element) iwp.getElement();
	       		
	       		if (elm == current) {
	       			commentBuffer.append(current.getAttributeValue("word") + " ");
       				current.setAttribute("word", "comment");	
       			}
       		}*/
       		       		   		
	   	//adding all the index&word pairs to a list for sorting in the end
	   	//this since the liniear order of the xml is not that straightforward as Alpino claims.
		String begin = current.getAttributeValue("begin");
		if (begin !=null && current.getAttributeValue("word")!=null) {
			Integer i = new Integer(begin);
			IndexWordPair pair = new IndexWordPair(i,current);
			position.add(pair);
		}
			
       		//recursive walking through the JDOM tree
    		while (iterator.hasNext()) {
    			Element child = (Element) iterator.next();
      			performTransformations(child);
    		}     
   	}
	
	// Sorting the correct order of the words (based on begin index) in the dependency tree   	
	private void retrieveLineairOrder() {

    		Collections.sort(position,new Comparator()
 		{
			public int compare(Object obj1, Object obj2) {
	    			int n1= position.indexOf(obj1);
	    			int n2= position.indexOf(obj2);
	    			IndexWordPair iwp1 = (IndexWordPair) position.get(n1);
	    			IndexWordPair iwp2 = (IndexWordPair) position.get(n2);
	    			return (iwp1.getIndex()).compareTo(iwp2.getIndex());
			}
    		});

		// Storing the correct order of the sentence between the <sentence></sentence> tags
		for (int i=0;i<position.size();i++){
			IndexWordPair iwp = (IndexWordPair) position.get(i);
			answerSentenceBuffer.append(iwp.getWord());
	    	}	
	    	
	    	answerSentenceBuffer.append("."); //closing the sentence with a nice "."
	    	answerRoot.getChild("sentence").setText(answerSentenceBuffer.toString());
	    	
	    	// Answer that will be presented in the end
	    	generatedAnswer = answerRoot.getChild("sentence").getText();
    	}
   
   	// Writing the generated answer to an answer.XML file
   	private void writeToFile() {	
    		try {
    			output = new DataOutputStream( new FileOutputStream("answer.xml"));
      			outputter.output(answerDoc, output);     
    		} catch (IOException e) {
      			System.err.println(e);
    		}
    	}
    	
    	// Return the nodes that are available in the parsed XML file
    	public String showNodesF() {
    		
    		showNodes.append("Overview of the document tree, with all the elements and attributes:\n\n");
    		return viewElements(anaF.root);
    	}

    	// Retrieves for each node the relevant information
    	private String viewElements(Element current) {

		List attributes = current.getAttributes();
		Iterator attriterator = attributes.iterator();
		int i = 0;

		showNodes.append("Element *" + current.getName() + "* has " + attributes.size() + " attributes:\n");
		while(attriterator.hasNext()) {
			showNodes.append(" "+ attriterator.next() + "\n");
			i++;
		}
		showNodes.append("\n");
					
		if (!current.getTextTrim().equals("")) {			
			showNodes.append(" and text: **" + current.getText() + "**");
		}

    		List children = current.getChildren();
    		Iterator childiterator = children.iterator();
    	    	
   		while(childiterator.hasNext()) {
     			Element child = (Element) childiterator.next();
     			viewElements(child);
     		}
     		
     		return showNodes.toString();
   	}
}