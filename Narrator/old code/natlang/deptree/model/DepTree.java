package natlang.deptree.model;

import java.util.*;
import parlevink.parlegraph.model.*;
import parlevink.xml.*;
import java.io.IOException;


public class DepTree extends MComponentAdapter  
{
	private int numNodes = 0;
	private Node nodes[];		
	
	//* deptree has at most one attribute, e.g. index */
	protected void readAttributes(XMLTokenizer tokenizer) throws IOException 
	{
        	//process any known attributes from hashtable
        	HashMap attributes = tokenizer.attributes;

        	if (attributes.containsKey("index")) 
        	{
            	if (getMGraph() != null)
                		getMGraph().setID(this, (String)attributes.get("index"));
            	else
                	setID((String)attributes.get("index"));
        	}
    }

	//* deptree has one kind of content, e.g. node */
	protected void readContent(XMLTokenizer tokenizer) throws IOException 
	{
        while (!tokenizer.atETag(getClass().getName())) 
        {
            if (tokenizer.atSTag("node"))
            {
				Node n = new Node(tokenizer);	//maak (en lees gelijk in) nieuwe rhetrelatie
				nodes[num] = n;
				numNodes++;
			}
			
			else 
        		throw new IllegalStateException("Wrong tag while trying to read " + getClass().getName());	
             	            
            tokenizer.nextToken();
        }
    }
}