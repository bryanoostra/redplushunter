package natlang.rdg.libraries;

import java.util.Map;
import java.util.Vector;

import narrator.lexicon.Entry;
import narrator.lexicon.LexicalChooser;

/**Inflects a verb. If information from the lexicon from a certain form is
 * available, it will always use that form. If it's not available, it will
 * try to inflect the verb using standard rules and available forms.
 * @author Marissa
 *
 */
public class VerbInflecter extends MorphLib implements LibraryConstants{
	public String getInflectedForm(Entry entry, String morph) throws Exception{
		//String root = entry.getRoot();
		Map<String,String> tenses = entry.getTenses();
		
		String result = "";
		
		//Check the lexicon for given entries, otherwise inflect the root
		//using standard rules.
		if (morph.indexOf(PRESENT)>=0){
			if (morph.indexOf(SINGULAR)>=0){
				if (morph.indexOf(FIRST)>=0){
					if (tenses.containsKey(FIRSTSINGULARPRESENT))
						result = tenses.get(FIRSTSINGULARPRESENT);
				}
				if (morph.indexOf(SECOND)>=0){
					if (tenses.containsKey(SECONDSINGULARPRESENT))
						result = tenses.get(SECONDSINGULARPRESENT);
					//If no second person is specified it's usually the same as the third person.
					else if (tenses.containsKey(THIRDSINGULARPRESENT))
						result = tenses.get(THIRDSINGULARPRESENT);
				}
				if (morph.indexOf(THIRD)>=0){
					if (tenses.containsKey(THIRDSINGULARPRESENT))
						result = tenses.get(THIRDSINGULARPRESENT);
				}
			}
			if (morph.indexOf(PLURAL)>=0){
				if (tenses.containsKey(PLURALPRESENT))
					result = tenses.get(PLURALPRESENT);
			}
		}
		
		if (morph.indexOf(PAST)>=0){
			if (morph.indexOf(SINGULAR)>=0){
				if (tenses.containsKey(SINGULARSIMPLEPAST))
					result = tenses.get(SINGULARSIMPLEPAST);
			}
			if (morph.indexOf(PLURAL)>=0){
				if (tenses.containsKey(PLURALSIMPLEPAST))
					result = tenses.get(PLURALSIMPLEPAST);
			}
		}
		if (morph.indexOf(PERFECT)>=0){
			if (tenses.containsKey(PARTICIPLE))
				result = tenses.get(PARTICIPLE);
		}
		
		if (result.equals("")){
			result = inflectWeakVerb(entry,morph);
		}
		
		return result;
	}
	
	@Override
	//This is empty for now because the Morphlib will otherwise whine.
	//TODO Later Morphlib should just include getInflectedForm(entry,morph).
	public String getInflectedForm(String root, String morph) throws Exception {
		return null;
	}
	
	
	/** This function returns the infinitive of a regular verb, given its root */
	private StringBuffer getInf(Entry entry)
	{
		String root = entry.getRoot();
		Map<String,String> tenses = entry.getTenses();
		
		//The infinitive is the same as the plural present.
		//If this was specified in the lexicon, use that one.
		if (tenses.containsKey(PLURALPRESENT))
			return new StringBuffer(tenses.get(PLURALPRESENT));
		
		//System.out.println(" ROOT: " + root);
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
	private String inflectWeakVerb(Entry entry, String morph)
	{
		String r = entry.getRoot();
		//System.out.println("Inflecting weak verb "+r);
		StringBuffer root = new StringBuffer(r);
		StringBuffer inf = getInf(entry);
		char lastChar = root.charAt(r.length() - 1);
		Map<String, String> tenses = entry.getTenses();
		
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
			
			if (morph.indexOf(PLURAL) >= 0){ //maakten
				//if an irregular form exists for the singular past
				//add "en"
				if (tenses.containsKey(SINGULARSIMPLEPAST))
					return tenses.get(SINGULARSIMPLEPAST)+"en";
				
				root.append("n");
			}
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
	
	public void test(LexicalChooser lc){
		System.out.println("Testing Verb inflecter...");
		try{
			for (Vector<Entry> sense : lc.getLexicon().getMap().values()){
				for (Entry entry: sense){
					if (entry.getPos().equals(LibraryConstants.VERB)){
						System.out.println(getInflectedForm(entry,"1.sing.pres"));
						System.out.println(getInflectedForm(entry,"2.sing.pres"));
						System.out.println(getInflectedForm(entry,"3.sing.pres"));
						System.out.println(getInflectedForm(entry,"3.plural.pres"));
						System.out.println(getInflectedForm(entry,"1.sing.past"));
						System.out.println(getInflectedForm(entry,"1.plural.past"));
						System.out.println(getInflectedForm(entry,"1.sing.perf"));
						System.out.println(getInflectedForm(entry,"prog"));
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
