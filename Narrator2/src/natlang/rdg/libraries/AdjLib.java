package natlang.rdg.libraries;

import narrator.lexicon.Entry;

/**	This class inflects adjectives
 */
public class AdjLib extends MorphLib implements LibraryConstants
{
	/** Inflects the given adjective using the given morph-tag
	 *	@throws Exception when the node is not a adjective 
	 */
	public String getInflectedForm(String root, String morph) throws Exception
	{		
		return inflectRegular(root, morph);
	}
	
	public String getInflectedForm(Entry entry, String morph) throws Exception{
		String result = "";
		if (entry.getInflectedAdjective()!="") result = entry.getInflectedAdjective();
		else result = inflectRegular(entry.getRoot(),morph);
		return result;
	}
			
	/**	Inflects a regular noun according to the Dutch morphology rules
	 */
	private String inflectRegular(String root, String morph) throws Exception
	{		
		if (morph == null){
			throw new Exception("Missing morph tag");			
		}
			
		if (root == null)
			throw new Exception("Missing root tag");
		
		StringBuffer word = new StringBuffer(root);

		System.out.println(word);
		
		char lastChar = root.charAt(root.length() - 1);
		char charBefore = root.charAt(root.length() - 2);
		char secondCharBefore = root.charAt(root.length() - 3);
		
		if (!root.endsWith("en") || isVowel(secondCharBefore))	//verlegen-verlegen, maar gemeen/gemene
		{
			if (!isVowel(lastChar))
			{
				if ((isVowel(charBefore) && isVowel(secondCharBefore)) || (secondCharBefore == 'i' && charBefore == 'j'))
				{
					if (lastChar == 's')    //zinloos-zinloze en wijs-wijze 
						word.setCharAt(word.length() - 1, 'z');
					if (lastChar == 'f')	//lief-lieve en stijf-stijve
						word.setCharAt(word.length() - 1, 'v');
				
					if (charBefore == secondCharBefore)		//groot-grote maar mooi-mooie
						word.deleteCharAt(root.length() - 2);
				}
				else if (isVowel(charBefore) && lastChar != 'g' && lastChar != 'j')	//snel-snelle, maar aardig-aardige (nb: gammel gaat fout)
					word.append(lastChar);
			}
			word.append("e");
		}		
		
		System.out.println(word);
		
		return word.toString();
	}
}