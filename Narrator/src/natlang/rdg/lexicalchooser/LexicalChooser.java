package natlang.rdg.lexicalchooser;

import java.util.*;
import java.util.logging.Logger;

import natlang.debug.LogFactory;

/**
 * The module that manages all files in this package
 * 
 * @author Nanda Slabbers
 *
 */
public class LexicalChooser
{
	Lexicon lex;
	WordHistory wordhist;
	
	private Logger logger;

	/**
	 * Creates the Lexical Chooser
	 *
	 */
	public LexicalChooser()
	{
		lex = new Lexicon();
		wordhist = new WordHistory();
		
		logger = LogFactory.getLogger(this);
	}
	
	/**
	 * Returns an entry for the given concept, and if possible an entry that has not recently been used
	 * @param concept
	 * @param store
	 */
	public Entry getEntry(String concept, boolean store)		// eerste keer eerste entry
	{															// daarna 20% kans op andere entry en 100-#x20% kans op eerste
		Vector entries = getAllEntries(concept);
		
		int cnt = entries.size();
		Entry result = null;
		
		if (cnt == 0)
			return null;
		if (cnt == 1 || wordhist.getCount(((Entry) entries.elementAt(0)).getRoot()) == 0)	// gewoon eerste
		{
			result = (Entry) entries.elementAt(0);
		}		
		else if (cnt > 4)		// same chance for each entry 
		{
			int i = (int) (Math.random() * cnt);
			result = (Entry) entries.elementAt(i);
		}		
		else if (cnt > 1)		// each entry has a chance of 20% to be chosen
		{						// and the first entry has a chance of (100 - #entries * 20)% to be chosen
			int res;
			
			double i = Math.random();
						
			if (i > 0.8)
				res = cnt-1;
			else if (i > 0.6)
				res = cnt-2;
			else if (i > 0.4)
				res = cnt-3;
			else if (i > 0.2)
				res = cnt-4;
			else
				res = 0;
						
			if (res < 0)
				res = 0;
				
			result = ((Entry) entries.elementAt(res));
		}
		
		if (result != null)
		{
			if (store)
				wordhist.addWord(result.getRoot());
			
			return result;
		}
		return null;
	}
	
	/**
	 * Returns all entries for the given concept
	 * @param concept
	 */	
	public Vector getAllEntries(String concept)
	{
		Vector result = new Vector();
						
		for (int i=0; i < lex.entries.size(); i++)
		{
			Entry tmp = (Entry) lex.entries.elementAt(i);
			
			if (tmp.getConcept().equals(concept))
				result.addElement(tmp);
			if (result.size() > 0 && !tmp.getConcept().equals(concept))
				return result;
		}
		if (result.size() == 0) {
			logger.warning("No entries for concept " + concept);
		}
		return result;
	}			
	
	/**
	 * Checks if the concept is of the given part of speech
	 * @param pos
	 * @param con
	 */
	public boolean isPos(String pos, String con)
	{
		for (int i=0; i<lex.entries.size(); i++)
		{
			Entry tmp = (Entry) lex.entries.elementAt(i);
			
			if (tmp.getConcept().equals(con))
				return (tmp.getPos().equals(pos));
		}
		
		return false;
	}
	
	/**
	 * Adds a word to the word history
	 * @param w
	 */
	public void addWord(String w)
	{
		wordhist.addWord(w);
	}	
	
	/**
	 * Returns the original determiner ('de' or 'het')
	 * @param concept
	 * @param noun
	 */
	public String getOriginalDeterminer(String concept, String noun)
	{
		Vector nouns = getAllEntries(concept);
		for (int i=0; i<nouns.size(); i++)
		{
			Entry tmp = (Entry) nouns.elementAt(i);
			if (tmp.getRoot().equals(noun))
				return tmp.getDet();
		}
		return "";
	}
	
	public void print()
	{
		wordhist.print();		
	}
}