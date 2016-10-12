package narrator.lexicon;

/**
 * The number of times a certain word has already been used
 * 
 * @author Nanda Slabbers
 *
 */
public class WordCount 
{
	private String word;		//Dutch word!
	private int count;
	
	/**
	 * Creates an empty word count
	 *
	 */
	public WordCount()
	{
		word = "";
		count = 0;
	}
	
	/**
	 * Creates a word count for a noun
	 * @param n
	 */
	public WordCount(String n)
	{
		word = n;
		count = 1;
	}
	
	/**
	 * Increments the count for a noun
	 *
	 */
	public void incCount()
	{
		count++;
	}
	
	/**
	 * Returns the word
	 */
	public String getWord()
	{
		return word;
	}
	
	/**
	 * Returns the count
	 */
	public int getCount()
	{
		return count;
	}
}
