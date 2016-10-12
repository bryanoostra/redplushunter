package natlang.rdg.lexicalchooser;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import narrator.lexicon.CharacterInfo;
import narrator.lexicon.Lexicon;

public class PossibleAdjectives {
	//links concepts to a list of adjectives.
	Map<String,Vector<String>> adjectiveMap;
	
	Lexicon lexicon;
	
	public PossibleAdjectives(CharacterInfo characters, Lexicon lexicon){
		adjectiveMap = characters.getAdjectives();
		this.lexicon = lexicon;
	}
	
	/**
	 * Returns a possible adjective for a concept
	 * @param c
	 */
	public String getPossibleAdjective(String concept)
	{
		//System.out.println("Getting possible adjective for "+concept);
		String result = "";
		
		Vector<String> allAdjectives = adjectiveMap.get(concept);

		if (allAdjectives!=null && allAdjectives.size()!=0){
			for(String s : allAdjectives){
				System.out.println(s);
			}
			int i = (int) (Math.random() * allAdjectives.size());
			result = allAdjectives.get(i);
		}
		
		return result;
	}
	
	/**
	 * Returns a possible adverb for an adjective
	 * @param adj
	 */
	public String getPossibleAdverb(String adj)
	{
		String result = "";
		return result;
	}
}
