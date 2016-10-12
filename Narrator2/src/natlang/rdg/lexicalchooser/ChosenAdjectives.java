package natlang.rdg.lexicalchooser;

import java.util.*;

/**
 * Adjectives used in the story sofar
 * @author Nanda Slabbers
 */
public class ChosenAdjectives 
{
	private Vector eaps;		// vector containing entity-adjective pairs
	private Vector adjs;		// vector containing adjectives
	
	/**
	 * Creates new vectors for used adjectives
	 *
	 */
	public ChosenAdjectives()
	{
		eaps = new Vector();
		adjs = new Vector();
	}
	
	/**
	 * Adds an entity-adjective pair
	 * @param eap
	 */
	public void addEAP(EntityAdjectivePair eap)
	{
		eaps.add(eap);
	}
	
	/**
	 * Adds an adjective
	 * @param adj
	 */
	public void addAdjective(String adj)
	{
		adjs.add(adj);
	}
	
	/**
	 * Returns a used adjective for a certain entity
	 * @param entity
	 */
	public String getAdjective(String entity)
	{
		for (int i=0; i<eaps.size(); i++)
		{
			EntityAdjectivePair eap = (EntityAdjectivePair) eaps.elementAt(i);
			if (eap.getEntity().equals(entity))
				return eap.getAdjective();
		}
		return "";
	}
	
	/**
	 * Checks whether the adjective has already been used
	 * @param adj
	 */
	public boolean alreadyUsed(String adj)
	{
		for (int i=0; i<adjs.size(); i++)
		{
			String tmp = (String) adjs.elementAt(i);
			if (tmp.equals(adj))
				return true;
		}
		return false;
	}
}
