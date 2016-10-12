package natlang.rdg.lexicalchooser;

import java.util.*;

/**
 * Pair of concept and a vector of adjectives
 * @author Nanda Slabbers
 */
public class ConceptAdjectivesPair 
{
	private String conc;
	private Vector adjs;
		
	/**
	 * Creates a pair of a concept and a vector of possible adjectives
	 * @param c
	 * @param a
	 */
	public ConceptAdjectivesPair(String c, Vector a)
	{
		conc = c;
		adjs = a;
	}
	
	/**
	 * Returns the concept
	 */
	public String getConcept()
	{
		return conc;
	}
	
	/**
	 * Returns the Vector with adjectives
	 */
	public Vector getAdjs()
	{
		return adjs;
	}
}
