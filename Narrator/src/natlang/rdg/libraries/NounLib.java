package natlang.rdg.libraries;

import parlevink.util.*;

import java.io.*;

/**	This class inflects nouns
 */
public class NounLib extends MorphLib implements LibraryConstants
{
	/** Inflects the given noun using the given morph-tag
	 *	@throws Exception when the node is not a noun 
	 */
	public String getInflectedForm(String root, String morph) throws Exception
	{
		Resources rs = new Resources(this);
		BufferedReader reader = rs.getReader("nouns.txt");
		
		while (true)
		{
			String str = reader.readLine();
			if (str == null)
				break;
			
			if (str.startsWith(root))
			{
				String result = inflectIrregular(morph, str);
				return result;
			}
		}
		return inflectRegular(root, morph);
	}
	
	/**	Reads the inflection of an irregular noun from a file and returns that
	 */
	private String inflectIrregular(String morph, String str) throws Exception
	{
		if (morph == null)
			throw new Exception("Missing morph tag");
		
		if (morph.indexOf(SINGULAR) >= 0)
			return getColumn(str, 0);
		else if (morph.indexOf(PLURAL) >= 0)
			return getColumn(str, 1);
		else
			throw new Exception("unfamiliar morph tag");
	}
	
	/**	Inflects a regular noun according to the Dutch morphology rules
	 */
	private String inflectRegular(String root, String morph) throws Exception
	{
		if (morph == null)
			throw new Exception("Missing morph tag");
		
		if (root == null)
			throw new Exception("Missing root tag");
		
		StringBuffer word = new StringBuffer(root);
		if (morph.indexOf(PLURAL) >= 0)
		{	
			char lastChar = root.charAt(root.length() - 1);
			char charBefore = root.charAt(root.length() - 2);
			char secondCharBefore = root.charAt(root.length() - 3);
			if (isVowel(lastChar))	
			{			//plural form is "'s" (auto's, menu's)
				if ((lastChar == 'a') || (lastChar == 'o') || (lastChar == 'u'))
					word.append("'s");
				else if (lastChar == 'i')
				{
					if (!isVowel(charBefore)) //(mini's)
						word.append("'s");
					else //long vowel, plural is "en" (contreien, uien, haaien, ooien)
						word.append("en");
				}
				else if (lastChar == 'e')
				{	//tenues, hindes, hindoes
					if (!isVowel(charBefore) || (charBefore == 'u') || (charBefore == 'o'))	//(hertjes, avenues, hindoes)
						word.append("s");
					if ((charBefore == 'e') || (charBefore == 'i'))		//(zeeën, knieën)
						word.append("ën");
					//there is another rule here which depends on stress - perhaps the words governed by this rule should be listed in the irregular-noun file
				}
			}
			//last letter not a vowel - this includes endings in 'ij' (pijen, dijen)
			else if (isVowel(charBefore) && isVowel(secondCharBefore))	//(tuin - tuinen)
			{
				if (lastChar == 's')	//(huis - huizen)
					word.setCharAt(word.length() - 1, 'z');
				else if (lastChar == 'f')	//(geloof - geloven)
					word.setCharAt(word.length() - 1, 'v');
			
				if (charBefore == secondCharBefore)		//(laag - lagen)
					word.deleteCharAt(root.length() - 2);
				
				word.append("en");
			}
			else if (isVowel(charBefore))
			{
				if (root.endsWith("er") || root.endsWith("en"))
					if ((root.length() > 4) || isVowel(root.charAt(0)))
						word.append("s");
					else
					{
						word.append(lastChar);
						word.append("en");
					}
				else
				{
					word.append(lastChar);		//poppen
					word.append("en");
				}
			}
			else
				word.append("en");	
		}
		//if number is singular, root does not need to be inflected
		return word.toString();
	}
}