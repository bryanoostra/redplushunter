package natlang.rdg.discourse;

import java.util.*;

/**
 * Really simple history, only containing the names of the entities
 * Used in the Document Planner to decide whether background information
 * and names should be added
 * @author Nanda
 *
 */
public class History 
{
	private Vector<String> ents;
	
	/**
	 * Constructor
	 *
	 */
	public History()
	{
		ents = new Vector<String>();
	}
	
	/**
	 * Adds entity to the history
	 * @param e
	 */
	public void addEntity(String e)
	{
		ents.add(e);
	}
	
	/**
	 * Checks whether the entity has been mentioned before
	 * @param e the entity
	 */
	public boolean newEntity(String e)
	{
		if (!e.equals(""))
		{
			for (int i=0; i<ents.size(); i++)
			{
				if (e.equals((String) ents.elementAt(i)))
					return false;
			}
			return true;
		}
		return false;
	}
	
	/*public void print()
	{
		for (int i=0; i<ents.size(); i++)
			System.out.print((String) ents.elementAt(i) + "\t");
	}*/
	
	public History getCopy()
	{
		Vector<String> tmp = new Vector<String>();
		for (int i=0; i<ents.size(); i++)
			tmp.add(ents.elementAt(i));
		
		History result = new History();
		result.setHist(tmp);
		
		return result;
	}
	
	/*public String elementAt(int i)
	{
		return (String) ents.elementAt(i);
	}
	
	public int size()
	{
		return ents.size();
	}*/
	
	public void setHist(Vector<String> v)
	{
		ents = v;
	}
}
