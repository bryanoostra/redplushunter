package project.qa;

/*28 Juli 2004 NodeLister.java
   A test class that list all the elements available in an xml file.
*/

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import java.io.IOException;
import java.util.*;


public class ElementLister {

  	public static void main(String[] args) {
  
    		if (args.length == 0) {
      			System.out.println("Usage: java ElementLister URL"); 
      			return;
    		} 
      
    		SAXBuilder builder = new SAXBuilder();
     
    		try {
      			Document doc = builder.build(args[0]);
      			Element root = doc.getRootElement();
      			listChildren(root, 0);      
    		} catch (JDOMException e) { 
      			System.out.println(args[0] + " is not well-formed.");
      			System.out.println(e.getMessage());
    		} catch (IOException e) { 
      			System.out.println(e);
    		}  
	}
  
  
  	public static void listChildren(Element current, int depth) {
   
    		printSpaces(depth);
    		System.out.println(current.getName());
    		List children = current.getChildren();
    		Iterator iterator = children.iterator();
    		while (iterator.hasNext()) {
      			Element child = (Element) iterator.next();
      			listChildren(child, depth+1);
    		}    
  	}
  
  	private static void printSpaces(int n) {
    
    		for (int i = 0; i < n; i++) {
      			System.out.print(' '); 
    		}
    	}
}

