package natlang.rdg.model;

import parlevink.parlegraph.model.MLabeledVertex;

import java.util.*;

/**
 * Represents a sentence stored as canned text (can be a leaf node)
 * 
 * @author Nanda Slabbers
 *
 */
public class CannedText extends MLabeledVertex implements RSVertex 
{
	private String text;
	
	/**
	 * Creates an empty canned text 
	 *
	 */
	public CannedText()
	{
		text = "";
	}
	
	/**
	 * Creates a canned text object with the text set to the string s
	 * @param s
	 */
	public CannedText(String s)
	{
		text = s;
	}
	
	/**
	 * Returns the text
	 */
	public String getText()
	{
		return text;
	}
	
	/**
	 * Returns the type
	 */
	public String getType()
	{
		return "text";
	}

	/**
	 * Returns an iterator with the parent node
	 */
	public Iterator getParent()
	{
		List result = new ArrayList();
		Iterator it = getIncidentInMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			result.add(edge.getSource());
		}
		
		it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			result.add(edge.getTarget());
		}
		
		return result.iterator();
	}

}
