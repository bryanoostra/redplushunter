package natlang.rdg.lexicalchooser;

import natlang.rdg.model.PlotElement;

import java.util.Vector;


/**
 * Modelled Story World module
 * @author Nanda Slabbers
 */
public class StoryWorld 
{
	private Vector facts;
	private Vector ents;
	
	/**
	 * Creates the story world
	 *
	 */
	public StoryWorld()
	{
		ents = new Vector();
		facts = new Vector();
		addFacts();
	}
	
	/**
	 * Adds facts such as locations of objects and where characters live
	 *
	 */
	public void addFacts()
	{
		//facts.addElement(new PlotElement("action", "live", "princess01", "", "castle01", new Vector()));
		//facts.addElement(new PlotElement("action", "be", "prince01", "", "bridge01", new Vector()));
	}	
		
	/**
	 * Returns a plot element that can be told. A piece of background information can be told
	 * if all entities have already been mentioned apart from the one that is currently being
	 * introduced.	  
	 * An example: the background information that 'the princess lives in a castle' can be added
	 * if the princess has already been mentioned and the current plot element contains the
	 * castle. 
	 * 
	 * @param ent
	 */
	public PlotElement getFact(String ent)
	{
		for (int i=0; i<facts.size(); i++)
		{
			PlotElement pe = (PlotElement) facts.elementAt(i);
			if (pe.getAgens().equals(ent))
			{
				if (alreadyMentioned(pe.getPatiens()) && alreadyMentioned(pe.getTarget()))
					return pe;
			}
			else if (pe.getPatiens().equals(ent))
			{
				if (alreadyMentioned(pe.getAgens()) && alreadyMentioned(pe.getTarget()))
					return pe;
			}
			else if (pe.getTarget().equals(ent))
			{
				if (alreadyMentioned(pe.getAgens()) && alreadyMentioned(pe.getPatiens()))
					return pe;
			}		
		}
		
		return null;
	}
	
	/**
	 * Checks if the entity has already been mentioned
	 * @param ent
	 */
	public boolean alreadyMentioned(String ent)
	{
		if (ent.equals(""))
			return true;
		
		for (int i=0; i<ents.size(); i++)
		{
			String tmp = (String) ents.elementAt(i);
			if (tmp.equals(ent))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Adds an entity to the list of entities already mentioned
	 * @param ent
	 */
	public void addEntity(String ent)
	{
		ents.add(ent);
	}
}
