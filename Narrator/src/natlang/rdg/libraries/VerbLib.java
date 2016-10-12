package natlang.rdg.libraries;

import parlevink.util.*;

import java.io.*;

/**	This class inflects verbs. The root-tag should contain the root (stam) of the
 *	verb, not the infinitive! (like Alpino trees do)
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class VerbLib extends MorphLib implements LibraryConstants
{
	/** Inflects the given verb, using the given morph-tag
	 *	@throws Exception when the node is not a verb
	 */
	public String getInflectedForm(String root, String morph) throws Exception
	{
		Resources rs = new Resources(this);
		BufferedReader reader = rs.getReader("verbs.txt");
		
		while (true)
		{
			String str = reader.readLine();
			if (str == null)
				break;
			
			if (str.startsWith(root))
			{
				String result = inflectStrongVerb(morph, str);
				return result;
			}
		}
		String result = inflectWeakVerb(root, morph);
		return result;	//als de verb niet in de lijst staat, is hij zwak
	}
	
	/**	Inflects a strong verb by looking up the right inflection in a strong verb file
	 */
	private String inflectStrongVerb(String morph, String line) throws Exception
	{
		if (morph == null)
			throw new Exception("Missing morph tag");
		
		if (morph.indexOf(PROGRESSIVE) >= 0)
			return getColumn(line, 8);
		
		if (morph.indexOf(PERFECT) >= 0)
			return getColumn(line, 7);
		
		if (morph.indexOf(PRESENT) >= 0)
		{
			if (morph.indexOf(SINGULAR) >= 0)
			{
				if (morph.indexOf(FIRST) >= 0)
					return getColumn(line, 1);
				if (morph.indexOf(SECOND) >= 0)
					return getColumn(line, 2);
				if (morph.indexOf(THIRD) >= 0)
					return getColumn(line, 3);
			}
			else if (morph.indexOf(PLURAL) >= 0)
				return getColumn(line, 4);
		}
		
		if (morph.indexOf(PAST) >= 0)
		{
			if (morph.indexOf(SINGULAR) >= 0)
				return getColumn(line, 5);
			if (morph.indexOf(PLURAL) >= 0)
				return getColumn(line, 6);
		}
		
		if (morph.indexOf(INF) >= 0)
		{
			StringBuffer result = new StringBuffer(getColumn(line, 9));
				if (morph.indexOf(TEINF) >= 0)
					result.insert(0, "te ");
			return result.toString();
		}
		
		throw new Exception("unfamiliar morph tag");
	}
	
	/** This function returns the infinitive of a regular verb, given its root */
	private StringBuffer getInf(String root)
	{
		System.out.println(" ROOT: " + root);
		StringBuffer inf = new StringBuffer(root);
		char lastChar = root.charAt(root.length() - 1);
		char lastButOne = root.charAt(root.length() - 2);
		char lastButTwo = root.charAt(root.length() - 3);
		boolean addCons = true;	//to check whether a consonent needs to be added 
							//to keep the vowel short (wil - willen)
		if (isVowel(lastChar))
			addCons = false;
		//The infinitive is usually (root + 'en'), but there are exceptions:
		else if (isVowel(lastButOne) && isVowel(lastButTwo))	//maak - maken, duik - duiken
		{
			addCons = false;				//hoeven, duiken, maken
			if (lastChar == 'f')			//graaf - graven, hoef - hoeven
				inf.replace(inf.length() - 1, inf.length(), "v");
			else if (lastChar == 's')		//loos - lozen
				inf.replace(inf.length() - 1, inf.length(), "z");
				
			if (!isVowel(lastChar) && (lastButOne == lastButTwo))	//maak - maken, niet: paai, paien
				inf.delete(inf.length() - 2, inf.length() - 1);
		}
		else if (!isVowel(lastButOne))		//will stay short vowel anyway (werk)
			addCons = false;
		else if (root.endsWith("el") || root.endsWith("er"))
			addCons = false;
		else if (lastChar == 'w')
			addCons = false;
		
		if (addCons)			//gil - gillen, las - lassen
			inf.append(lastChar);
			
		inf.append("en");		//werk - werken
		return inf;
	}
	
	/** Inflect regular verbs according to Dutch morphology rules. Not strictly 
	 *	necessary to implement when adapting the grammar to a different language,
	 *	but convenient as otherwise you'd have to specify all inflections of every 
	 *	verb you want to use. The same goes for inflectWeakVerb in NounLib
	 */
	private String inflectWeakVerb(String r, String morph)
	{
		StringBuffer root = new StringBuffer(r);
		StringBuffer inf = getInf(r);
		char lastChar = root.charAt(r.length() - 1);
				
		if (morph.indexOf(PROGRESSIVE) >= 0)		//werkend, lopend, makend, gillend
			return inf.append("d").toString();
						
		else if (morph.indexOf(PERFECT) >= 0)	
		{	
			if (kofschip(inf))			//gewerkt, gemaakt
			{
				if (lastChar != 't')		//gehaat
					root.append("t");
			}						
			else if (lastChar != 'd')
				root.append("d");		//gegild, geleefd
			
			String pre = root.toString();
			if ((root.length() > 5) && (pre.startsWith("ge") || pre.startsWith("ver") ||
                      			pre.startsWith("her") || pre.startsWith("ont") || 
                      			pre.startsWith("be") || pre.startsWith("mis")))
            			pre = ""; 
    			else
        		    	pre = "ge"; 
			root.insert(0, pre);		//geland, gevlucht, verwend, ontwend
		}
		
		else if (morph.indexOf(PRESENT) >= 0)
		{
			if (morph.indexOf(SINGULAR) >= 0)
			{
				if ((morph.indexOf(SECOND) >= 0) || (morph.indexOf(THIRD) >= 0))
					if (lastChar != 't') //second or third person. t must not be added when already there
						root.append("t");	//present singular based on root
			}
			else	//plural form is equal to infinitive (werken, vluchten)
				return inf.toString();
		}
		
		else if (morph.indexOf(PAST) >= 0)
		{
			if (kofschip(inf))
				root.append("te");	//maakte, haatte
			else
				root.append("de");	//gilde, doodde, leefde
			
			if (morph.indexOf(PLURAL) >= 0) //maakten
				root.append("n");
		}
		
		else if (morph.indexOf(TEINF) >= 0)
			return inf.insert(0, "te ").toString();	//te maken
			
		else if (morph.indexOf(INF) >= 0)	//maken
			return inf.toString();
		
		return root.toString();
	}
	
	/**	Famous Dutch verb rule - determines whether the past and perfect tense should
	 *	be inflected with a 'd' or a 't'. If true, a 't' should be used; else, a 'd'
	 *	It uses the infinitive, because an infinitive with 'v' or 'z' receives a 'd',
	 *	while its root ends in a 'f' or 's', and so would receive a 't'. 
	 */
	private boolean kofschip(StringBuffer inf)
	{
		char lastChar = inf.charAt(inf.length() - 3);		//minus 'en'
		if ((lastChar == 't') || (lastChar == 'k') || (lastChar == 'f') || (lastChar == 's') || 
			(lastChar == 'p'))
			return true;
		
		if (lastChar == 'h')
		{
			char lastButOne = inf.charAt(inf.length() - 4);
			if (lastButOne == 'c')
				return true;
		}
		
		return false;
	}
}