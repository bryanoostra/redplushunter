package natlang.rdg.libraries;

import narrator.lexicon.Entry;
import narrator.lexicon.LexicalChooser;

/**
 * Inflects nouns. Reads the correct plural from the
 * Lexicon if needed, otherwise uses 
 * @author Marissa Hoek, Feikje Hielkema
 *
 */
public class NounInflecter extends MorphLib implements LibraryConstants {
	
	/** Inflects the given noun using the given morph-tag
	 *	@throws Exception when the node is not a noun 
	 */
	public String getInflectedForm(Entry entry, String morph) throws Exception {
		String result = "";
		
		if (morph.indexOf(SINGULAR) >= 0){
			result = entry.getRoot();
		}
		if (morph.indexOf(PLURAL) >= 0){
			if (!entry.getPlural().equals("")){
				result = entry.getPlural();
			} else{
				result = inflectRegular(entry.getRoot());
			}
		}
		
		return result;
	}
		
		/**	Returns the plural of a regular noun according to the Dutch morphology rules
		 */
		private String inflectRegular(String root) throws Exception
		{
			StringBuffer word = new StringBuffer(root);
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
			return word.toString();
		}

		@Override
		public String getInflectedForm(String root, String morph)
				throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
		
		public void test(LexicalChooser lc){
			System.out.println("Testing Noun inflecter...");
			try{
				Entry entry = lc.getEntry("main_street", false);
				System.out.println(getInflectedForm(entry,SINGULAR));
				System.out.println(getInflectedForm(entry,PLURAL));
				entry = lc.getEntry("square", false);
				System.out.println(getInflectedForm(entry,SINGULAR));
				System.out.println(getInflectedForm(entry,PLURAL));
			} catch (Exception e){
				e.printStackTrace();
			}
		}
}
