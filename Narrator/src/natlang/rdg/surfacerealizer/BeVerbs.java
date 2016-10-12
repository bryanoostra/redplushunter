package natlang.rdg.surfacerealizer;

import java.util.*;

/**
 * Class containing the verbs that require 'to be' as auxiliary verb when using past perfect
 * May be better to use a .txt file
 * 
 * @author Nanda Slabbers
 *
 */
public class BeVerbs 
{
	private Vector<String> verbs;
	
	public BeVerbs()
	{
		verbs = new Vector<String>();
		
		verbs.add("ben");
		verbs.add("ga");
		verbs.add("klim");	
		verbs.add("kom");
		verbs.add("stijg");	
		verbs.add("loop");
	}
	
	public boolean useBe(String verb)
	{
		for (int i=0; i<verbs.size(); i++)
		{
			String tmp = (String) verbs.elementAt(i);
			
			if (tmp.equals(verb))
				return true;
		}
		return false;
	}
}
