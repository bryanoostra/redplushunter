package project.qa;

/*Last change: 28 Oktober 2004 IndexWordPair.java
   By: Erik Dikkers
   Description: A class voor creating a IndexWordPair to be able to reconstruct
   the correct lineair order for the words of the dependency tree. The lineair order
   can be retrieved by comparing the begin index of every node in the tree.
*/

import org.jdom.*;

public class IndexWordPair {
	
	public Integer index;		//index of the element in the lineair order
	public Element element;		//the element itself
		
	// Basic constructor for creating an index & word pair
	//public IndexWordPair(Integer i, String s) {
	public IndexWordPair(Integer i, Element elm) {
		index = i;
		element = elm;
	}
	
	// Retrieves the index of this pair
	public Integer getIndex() {
		return index;	
	}
	
	// Retrieves the word of this pair
	public String getWord() {
		String addition;
		String returnStr;
		
		//to add an extra space between words		
		if (element.getAttributeValue("rel").equals("cnj") && !element.getAttributeValue("word").equals("")) {
			addition = ", ";
		} else {
			addition = " ";	
		}
		
		returnStr = element.getAttributeValue("word") + addition;
		return returnStr;
	}
	
	public Element getElement() {
		return element;	
	}
}