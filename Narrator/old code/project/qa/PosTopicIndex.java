package project.qa;

/*Last change: 28 Oktober 2004 PosTopicIndex.java
   By: Erik Dikkers
   Description: A class voor creating a posTopicIndex where each instance of this class
   represents a subject in the dependency tree that might be identified as the topic.
*/

import java.util.*;
import org.jdom.*;

public class PosTopicIndex {
	
	public Integer beginIndex;			//begin index of subject
	public Integer endIndex;			//end index of subject
	public int topicNr;				//identification of the topic
	public Element topElement;			//main element that contains this subject
	public int score;				//subject score or topic identification
	public ArrayList elements = new ArrayList();	//contains all the subject elements
		
	// Basic constructor for creating an index & word pair
	public PosTopicIndex(Integer i1, Integer i2) {		
		beginIndex = i1;
		endIndex = i2;
		
		if (i1.intValue() == -1 && i2.intValue() == -1) {	
			score = -1;
		} else { score = 0; };
	}
	
	// Retrieves the beginIndex of this pair
	public Integer getBeginIndex() {
		return beginIndex;	
	}
	
	// Retrieves the endIndex of this pair
	public Integer getEndIndex() {
		return endIndex;	
	}
	
	// Set topic identifier
	public void setTopicNumber(int nr) {
		topicNr = nr;	
	}
	
	// Retrieve topic identification number
	public int getTopicNr() {
		return topicNr;	
	}
	
	// Add element to the arraylist of topic elements
	public void add(Element elm, Integer begin) {
		IndexWordPair pair = new IndexWordPair(begin,elm);
		elements.add(pair);
	}
	
	// Retrieve the arraylist with all the topic elements
	public ArrayList getTopicElements() {
		return elements;	
	}
	
	// Increase the discourse topic score for this possible Topic
	public void increaseScore() {
		score++;	
	}
	
	// Retrieve the discourse topic score for this possible Topic
	public int getScore() {
		return score;	
	}
	
	// Set the main element that contains this subject
	public void setElement(Element elm) {
		topElement = elm;
	}
	
	// Retrieve the element that contains this subject
	public Element getElement() {
		return topElement;	
	}
}