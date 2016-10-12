package natlang.rdg.libraries;

/**	This is an abstract class with functionality to inflect words
 */
public abstract class MorphLib implements LibraryConstants
{
	/** inflect the given word - every child class must implement this */
	public abstract String getInflectedForm(String root, String morph) throws Exception;
	
	/**	Function to help read irregular nouns or verbs from a file. The x'th column
	 *	of the given line (from a file) is returned, if there are that many columns.
	 *	Else, null is returned
	 *	@param line line from file
	 *	@param column column number
	 *	@return String
	 */
	protected String getColumn(String line, int column)
	{
		StringBuffer buffer = new StringBuffer(line);
		
		for (int i = 1; i < column; i++)
		{
			int idx = buffer.indexOf(" ");
			if (idx < 0)
				return null;
			
			buffer.delete(0, idx + 1);
		}
		
		int idx = buffer.indexOf(" ");
		if (idx > 0)
			return buffer.substring(0, idx);
		else
			return buffer.toString();
	}
	
	/** Checks whether the given character is a vowel or not */
	protected boolean isVowel(char c)
	{
		if ((c == 'a') || (c == 'e') || (c == 'i') || (c == 'u') || (c == 'o'))
			return true;
		return false;
	}
}