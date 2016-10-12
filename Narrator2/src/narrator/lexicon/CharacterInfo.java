package narrator.lexicon;

import java.util.Map;
import java.util.Vector;

import narrator.reader.CharacterModel;
import narrator.reader.WorldReader;

/**
 * This class reads information about characters, 
 * and also objects in the story world. The 
 * Microplanner can then query this class to find
 * out properties about characters. Looking at 
 * the example characters, this stores: The 
 * systematic name used in the Fabula, a class
 * this character belongs to (such as Pirate, 
 * Girl, or Ship), this characters gender (or 
 * Neutral) and their name.
 *  
 * @author Marissa Hoek
 *
 */
public class CharacterInfo {
	private Map<String,CharacterModel> characters;
	private Vector<CharacterModel> characterList;
	private WorldReader wReader;
	
	public CharacterInfo(String fabula){
		//add Characters
		wReader = new WorldReader(fabula);
		try{wReader.getXml();}
		catch (Exception e){
			e.printStackTrace();
		}
		characters = wReader.getCharacterMap();
		characterList = wReader.getCharacters();
		
		//TODO add Objects
	}
	
	public CharacterModel getChar(String entity){
		return characters.get(entity);
	}
	
	public Vector<CharacterModel> getChars(){
		return characterList;
	}
	
	public boolean isEntity(String entity){
		return characters.containsKey(entity);
	}
	
	public String getGender(String entity){
		String result = "";
		CharacterModel c = characters.get(entity);
		if (c!=null) result = c.getGender();
		return result;
	}
	
	public Map<String,Vector<String>> getAdjectives(){
		return wReader.getAdjectives();
	}
}