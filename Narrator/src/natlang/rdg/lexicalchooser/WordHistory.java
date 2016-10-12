package natlang.rdg.lexicalchooser;

import java.util.*;

/**
 * A list containing all words already used in the story together with the number
 * of times each word has been used
 * 
 * @author Nanda Slabbers
 *
 */
public class WordHistory
{
	private Vector words;
	
	/**
	 * Constructs an empty list for the words
	 *
	 */
	public WordHistory()
	{
		words = new Vector();
	}
	
	/**
	 * Adds a new word count to the list if the word has not been used before,
	 * and increments the count for the word if the word has been used before.
	 * @param w
	 */
	public void addWord(String w)
	{
		for (int i=0; i<words.size(); i++)
		{
			WordCount wc = (WordCount) words.elementAt(i);
			if (wc.getWord().equals(w))
			{
				wc.incCount();
				return;
			}
		}
		words.addElement(new WordCount(w));
	}
	
	/**
	 * Returns the number of times the word has been used
	 * @param w
	 */
	public int getCount(String w)
	{
		for (int i=0; i<words.size(); i++)
		{
			WordCount wc = (WordCount) words.elementAt(i);
			if (wc.getWord().equals(w))
				return wc.getCount();
		}
		return 0;
	}
	
	/**
	 * Returns an entire word count object
	 * @param w
	 */
	public WordCount getWordCount(String w)
	{
		for (int i=0; i<words.size(); i++)
		{
			WordCount wc = (WordCount) words.elementAt(i);
			if (wc.getWord().equals(w))
				return wc;
		}
		return null;
	}
	
	public void print()
	{
		System.out.println("\n\nAlle gebruikte woorden:\n\n");
		for (int i=0; i<words.size(); i++)
		{
			WordCount wc = (WordCount) words.elementAt(i);
			System.out.println(wc.getWord() + "\t" + wc.getCount());
		}
	}
}
