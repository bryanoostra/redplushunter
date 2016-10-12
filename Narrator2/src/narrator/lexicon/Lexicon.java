package narrator.lexicon;

import java.util.Map;
import java.util.Vector;

import narrator.reader.LexiconReader2;

/**
 * The Lexicon maps the concepts in the Fabula to
 * words, including linguistic information such as 
 * the determiner (for nouns) and verb category.
 * @author Marissa Hoek
 *
 */
public class Lexicon {
	private Map<String,Vector<Entry>> entries;
	
	/**
	 * Creates the lexicon by reading the words from
	 * an XML file. Note: actual file I/O is done in
	 * the class LexiconReader.
	 * @param filename
	 */
	public Lexicon(String fabula){
		entries = new LexiconReader2(fabula).getEntryMap();
	}
	
	/**
	 * Returns all lexicon entries that match this
	 * concept. The LexicalChooser can then pick one
	 * of these entries.
	 * @param Concept
	 * @return A list of entries that match this concept.
	 */
	public Vector<Entry> getAllEntries(String concept){
		return entries.get(concept);
	}
	
	public Map<String,Vector<Entry>> getMap(){
		return entries;
	}
	
	/**
	 * Returns the Entry for a given root and part
	 * of speech tag
	 * @param root of the word we want the entry of
	 * @param PoS tag, to make sure we differentiate between words with the same root but different PoS-tags
	 * @return An Entry with entry.getRoot()==root and entry.getPos()==pos, or null;
	 */
	public Entry getEntryFromWord(String root, String pos){
		Entry result = null;

		Map<String,Vector<Entry>> map = this.getMap();	
		for (Vector<Entry> v : map.values()){
			for (Entry e : v){
				if (e.getRoot()==root && e.getPos()==pos){
					result = e;
				}
			}
		}
		
		return result;
	}
	
}
