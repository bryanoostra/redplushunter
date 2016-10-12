package natlang.rdg.lexicalchooser;

import java.util.*;
import java.math.*;

/**
 * Lists with concept adjective pairs and adjective adverb pairs 
 * that can be used to add adjectives and adverbs
 * @author Nanda Slabbers
 *
 */
public class PossibleAdjectivesOld 
{
	private Vector concepts;
	private Vector adjectives;
	
	/**
	 * Creates the lists with concept adjective pairs and adjective adverb pairs 
	 *
	 */
	public PossibleAdjectivesOld()
	{
		concepts = new Vector();
		
		Vector adjs = new Vector();
		adjs.add("heavy");
		ConceptAdjectivesPair cap = new ConceptAdjectivesPair("gate", adjs);
		concepts.add(cap);
		
		adjs = new Vector();
		adjs.add("big");
		cap = new ConceptAdjectivesPair("castle", adjs);
		concepts.add(cap);
		
		adjs = new Vector();
		adjs.add("high");
		cap = new ConceptAdjectivesPair("tree", adjs);
		concepts.add(cap);
		
		adjectives = new Vector();		
		adjectives.add(new AdjectiveAdverbPair("beautiful", "extraordinary"));		
		adjectives.add(new AdjectiveAdverbPair("loud", "very"));
	}
	
	/**
	 * Returns a possible adjective for a concept
	 * @param c
	 */
	public String getPossibleAdjective(String c)
	{
		for (int i=0; i<concepts.size(); i++)
		{
			ConceptAdjectivesPair cap = (ConceptAdjectivesPair) concepts.elementAt(i);
			
			if (cap.getConcept().equals(c))
			{
				int n = (int) (Math.random() * cap.getAdjs().size());
				return (String) cap.getAdjs().elementAt(n);
			}
		}
		
		return "";
	}
	
	/**
	 * Returns a possible adverb for an adjective
	 * @param adj
	 */
	public String getPossibleAdverb(String adj)
	{
		for (int i=0; i<adjectives.size(); i++)
		{
			AdjectiveAdverbPair aap = (AdjectiveAdverbPair) adjectives.elementAt(i);
			if (aap.getAdjective().equals(adj))
			{
				adjectives.removeElementAt(i);
				return aap.getAdverb();
			}
		}
		return "";
	}
}
