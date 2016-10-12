package natlang.rdg.model;

import natlang.rdg.libraries.*;
import parlevink.parlegraph.model.*;

import java.util.*;
import java.io.*;
import org.jdom.*;

/** RhetRelation is a MLabeledVertex; it has in- and outgoing edges which connect
 *	it to RSDepTreeModels and other RhetRelations. Its label gives the type of
 *	rhetorical relation. 
 *	@author Feikje Hielkema
 *	@version 1.0
 */

public class RhetRelation extends MLabeledVertex implements RSVertex
{
	/** Constructs a Vertex with the label category
	 *	@param element
	 *	@throws IOException when the element has no cat-attribute
	 */
	public RhetRelation(Element element) throws IOException
	{	
		super();
		String cat;
		if ((cat = element.getAttributeValue("cat")) != null)
			setLabel(cat);
		else
			throw new IOException("Error reading input: rhetrelation must have a category");
	}
	
	public RhetRelation(String rel)
	{
		super();
		setLabel(rel);
	}
	
	/** @see RSVertex */
	/*public boolean isDepTree()
	{
		return false;
	}*/
	
	public String getType()
	{
		return "rhetrel";
	}
	
	/** This function finds the relation that this relation is a part of (if any)
	 *	The relation should not be part of more than one other relation
	 */
	public Iterator getParent()
	{
		Iterator it = getIncidentInMEdges();
		List result = new ArrayList();
		while (it.hasNext())
		{
			
			RSEdge edge = (RSEdge) it.next();
//			System.out.println(" *** (RR) IN *** " + edge.getLabel() + " --> "  + edge.getSource());
			if (edge.getLabel().equals(LibraryConstants.NUCLEUS)){
//				System.out.println(" FOUND IT ");
				result.add(edge.getSource());
			}
		}
		
		it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			
			RSEdge edge = (RSEdge) it.next();
//			System.out.println(" *** (RR) OUT *** " + edge.getLabel() + " --> "  + edge.getTarget());
			if (edge.getLabel().equals(LibraryConstants.SATELLITE)){
//				System.out.println(" FOUND IT ");
				result.add(edge.getTarget());
			}
		}
		return result.iterator();
	}
		
	/**	Returns the nucleus of the relation
	 *	@return RSVertex
	 */
	public RSVertex getNucleus()
	{
		Iterator it = getIncidentOutMEdges();
		RSEdge edge;
		while (it.hasNext())
		{
			edge = (RSEdge) it.next();
			if (edge.getLabel() == LibraryConstants.NUCLEUS)
				return (RSVertex) edge.getTarget();
		}
		return null;
	}
	
	/**	Returns the satellite of the relation
	 *	@return RSVertex
	 */
	public RSVertex getSatellite()
	{
		Iterator it = getIncidentInMEdges();
		RSEdge edge;
		while (it.hasNext())
		{
			edge = (RSEdge) it.next();
			if (edge.getLabel() == LibraryConstants.SATELLITE)
				return (RSVertex) edge.getSource();
		}
		return null;
	}
	
	public String toString(){
//		return label + ":\nNUCLEUS:\t" + getNucleus() + "\nSATELLITE:\t" + getSatellite();
		return stringalize(this,0);
	}
	
	private String stringalize(RSVertex r, int spacing){
		
		String spaces = "";
		for(int i=0; i<spacing; i++){
			spaces += " ";
		}
		
		if(r instanceof RhetRelation){
			RhetRelation rr = (RhetRelation)r;
			return rr.getLabel() + 
					"\n" + spaces + "NUC: " + stringalize(rr.getNucleus(), spacing + 1) +
					"\n" + spaces + "SAT: " + stringalize(rr.getSatellite(), spacing + 1)
					;
		}
		else if (r==null)
			return null;
		else return r.toString();
	}
}
				
			