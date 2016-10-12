package narrator.lexicon;

import java.util.*;
//import java.util.logging.Logger;
//import natlang.debug.LogFactory;

import narrator.shared.NarratorException;

/**
 * The LexicalChooser is called when the Microplanner needs 
 * to choose a word for a certain concept. Since some concepts 
 * can be expressed using multiple words, this choice is more 
 * difficult than just calling the lexicon. The LexicalChooser 
 * contains a word history to remember which words were already 
 * chosen, and tries to create variety in the story by picking 
 * words that were not used yet with a higher probability.
 * 
 * @author Nanda Slabbers
 * @author Marissa Hoek
 *
 */
public class LexicalChooser
{
	Lexicon lex;
	WordHistory wordhist;
	
	//private Logger logger;

	/**
	 * Creates the Lexical Chooser
	 *
	 */
	public LexicalChooser(Lexicon lexicon)
	{
		this.lex = lexicon;
		wordhist = new WordHistory();
		
		//logger = LogFactory.getLogger(this);
	}
	
	/**
	 * Returns an entry for the given concept, and if possible an entry that has not recently been used
	 * @param concept
	 * @param store
	 * @throws NarratorException 
	 */
	public Entry getEntry(String concept, boolean store) 		// eerste keer eerste entry
	{															// daarna 20% kans op andere entry en 100-#x20% kans op eerste
		System.out.println("Getting entry for "+concept);
		
		if (concept.equals("")) return null;
		
		Vector<Entry> entries = lex.getAllEntries(concept);
		
/*		if (entries==null){
			throw new NarratorException("Concept not found in the lexicon: "+concept);
		}*/
		
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
	
/*	*//**
	 * Returns all entries for the given concept
	 * @param concept
	 *//*	
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
			//logger.warning("No entries for concept " + concept);
		}
		return result;
	}*/			
	
	/**
	 * Checks if the concept is of the given part of speech
	 * @param part of speech tag
	 * @param concept
	 */
	public boolean isPos(String pos, String con)
	{
		Vector<Entry> entries = lex.getAllEntries(con);
		for (Entry e : entries)
			return (e.getPos().equals(pos));	
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
		Vector<Entry> nouns = lex.getAllEntries(concept);
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

	public Lexicon getLexicon() {
		return lex;
	}
}