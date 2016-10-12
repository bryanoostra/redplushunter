package project.qa;

/*28 Juli 2004 NodeLister.java
   A test class that list all the nodes available in an xml file.
*/

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import java.io.IOException;
import java.util.*;

public class NodeLister {

  	public static void main(String[] args) {
  
    		if (args.length == 0) {
      			System.out.println("Usage: java NodeLister URL"); 
      			return;
    		} 
      
    		SAXBuilder builder = new SAXBuilder();
     
    		try {
	      		Document doc = builder.build(args[0]);
      			listNodes(doc, 0);      
    		} catch (JDOMException e) { 
	      		System.out.println(args[0] + " is not well-formed.");
      			System.out.println(e.getMessage());
    		} catch (IOException e) { 
      			System.out.println(e);
    		}  
  	}
  
  	public static void listNodes(Object o, int depth) {
   
    		printSpaces(depth);
    
    		if (o instanceof Element) {
      			Element element = (Element) o;
      			System.out.println("Element: " + element.getName());
      			List children = element.getContent();
      			Iterator iterator = children.iterator();
      			
      			while (iterator.hasNext()) {
        			Object child = iterator.next();
        			listNodes(child, depth+1);
      			}
    		} else if (o instanceof Document) {
      			System.out.println("Document");
      			Document doc = (Document) o;
      			List children = doc.getContent();
      			Iterator iterator = children.iterator();
      			
      			while (iterator.hasNext()) {
        			Object child = iterator.next();
        			listNodes(child, depth+1);
      			}
    		} else if (o instanceof Comment) {
      			System.out.println("Comment");
    		} else if (o instanceof CDATA) {
      			System.out.println("CDATA section");
      			// CDATA is a subclass of Text so this test must come
      			// before the test for Text.
    		} else if (o instanceof Text) {
      			System.out.println("Text");
    		} else if (o instanceof EntityRef) {
      			System.out.println("Entity reference");
    		} else if (o instanceof ProcessingInstruction) {
      			System.out.println("Processing Instruction");
    		} else {  // This really shouldn't happen
      			System.out.println("Unexpected type: " + o.getClass());
    		}
  }
  
  	private static void printSpaces(int n) {
    
    		for (int i = 0; i < n; i++) {
      			System.out.print(' '); 
    		}    
  	}
}

