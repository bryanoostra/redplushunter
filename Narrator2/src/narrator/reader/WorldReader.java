package narrator.reader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;  
import javax.xml.parsers.SAXParserFactory;  

import narrator.shared.NarratorException;

import org.xml.sax.Attributes;  
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;  

/**
 * Reads the world data from the Virtual StoryTeller.
 * This includes characters, objects, places and actions.
 * @author Marissa Hoek
 *
 */
public class WorldReader extends DefaultHandler{
	public static final String PREFIX_SETTINGSFILE = "<!--World Setting file: ";
	public static final String SUFFIX_SETTINGSFILE = "-->";
	
	private String worldFile;
	private Vector<CharacterModel> characters;
	private Map<String,Vector<String>> adjectives = new HashMap<String,Vector<String>>();
	
	/**
	 * Constructs a World Reader from a Fabula file. The World Reader will
	 * look at the Fabula file to find a setting file.
	 * @param fabula Path to a fabula file with a link to a World file.
	 */
	public WorldReader(String fabula){
		worldFile = getSettingFile(fabula);
		characters = new Vector<CharacterModel>();
	}
	
	public Vector<CharacterModel> getCharacters(){
		return characters;
	}
	
	public Map<String, CharacterModel> getCharacterMap(){
		Map <String, CharacterModel> result = new HashMap<String, CharacterModel>();
		
		for (CharacterModel c : characters){
			result.put(c.getEntity(), c);
		}
		
		return result;
	}
	
	public void getXml() throws ParserConfigurationException, SAXException, IOException{  
		   // obtain and configure a SAX based parser  
		   SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();  
		  
		   // obtain object for SAX parser  
		   SAXParser saxParser = saxParserFactory.newSAXParser();  
		  
		   // default handler for SAX handler class  
		   // all three methods are written in handler's body  
		   DefaultHandler defaultHandler = new DefaultHandler(){  
			   
			String currentID = "";
			Vector<String> lastAdjectives;
			String currentString = "";
			
			boolean charOpen = false;
			boolean charactersOpen = false;
			boolean itemsOpen, itemOpen = false;
			boolean locationsOpen, locationOpen, innerLocationOpen, actionsOpen, innerCharacterOpen,actionInCharacterOpen;
		      
		    // this method is called every time the parser gets an open tag '<'  
		    // identifies which tag is being open at time by assigning an open flag  
		    public void startElement(String uri, String localName, String qName,  
		      Attributes attributes) throws SAXException {
				//Empty the string with contents of the tag
				currentString = "";
		    	
		    	if (qName.equalsIgnoreCase("CHARACTERS")) {  
		    		charactersOpen = true;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("ACTIONS")) {  
		    		actionsOpen = true;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("ACTION")&&charOpen) {  
		    		actionInCharacterOpen = true;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("ITEMS")){
		    		itemsOpen = true;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("LOCATIONS")){	//ignore the list of locations within a location
		    		locationsOpen = true;
		    	}
		    		    	
		    	if (qName.equalsIgnoreCase("ITEM")&&itemsOpen&&!actionsOpen) {  
		    		itemOpen = true;
		    		characters.add(new CharacterModel());
		    		currentID = attributes.getValue("id");
		    		characters.lastElement().setEntity(currentID);
		    		characters.lastElement().setGender("NULL");
		    		
		    		lastAdjectives = new Vector<String>();
		    		adjectives.put(currentID, lastAdjectives);
		    		//characters.lastElement().setConcept(attributes.getValue("id"));
		    	}  
		    	
		    	if (qName.equalsIgnoreCase("LOCATION")&&locationsOpen&&!locationOpen){
		    		locationOpen = true;
		    		
		    		characters.add(new CharacterModel());
		    		currentID = attributes.getValue("id");
		    		characters.lastElement().setEntity(currentID);
		    		characters.lastElement().setGender("NULL");
		    		
		    		lastAdjectives = new Vector<String>();
		    		adjectives.put(currentID, lastAdjectives);
		    	}
		    	
		    	//This is an inner location that is possible too,
		    	//a location tag inside a location tag.
		    	if (qName.equalsIgnoreCase("LOCATIONS")&&locationOpen){
		    		innerLocationOpen = true;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("LOCATION")&&charOpen&&!actionInCharacterOpen){
		    		characters.lastElement().setLocation(attributes.getValue("id"));
		    	}
		    	
		    	if (qName.equalsIgnoreCase("CHARACTER")&&charactersOpen&&!charOpen&&!actionsOpen) {  
		    		charOpen = true;
		    		characters.add(new CharacterModel());
		    		currentID = attributes.getValue("id");
		    		characters.lastElement().setEntity(currentID);
		    		
		    		lastAdjectives = new Vector<String>();
		    		adjectives.put(currentID, lastAdjectives);
		    	}  
		    	if (qName.equalsIgnoreCase("CHARACTER")&&charOpen){
		    		innerCharacterOpen = true;
		    	}
	}  
		  
		    // prints data stored in between '<' and '>' tags  
		    public void characters(char ch[], int start, int length)  
		      throws SAXException {
		    	String s = new String(ch, start, length);
				//Store everything in the current tag into a temporary string.
				currentString += s;
		    }  
		  
		    // calls by the parser whenever '>' end tag is found in xml   
		    // makes tags flag to 'close'  
		    public void endElement(String uri, String localName, String qName)  
		      throws SAXException {    	
		    	if (qName.equalsIgnoreCase("ACTION")&&actionInCharacterOpen) {  
		    		actionInCharacterOpen = false;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("CHARACTER")){
		    		if (innerCharacterOpen)
		    			innerCharacterOpen = false;
		    		else charOpen = false;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("ITEMS")){
		    		itemsOpen = false;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("ITEM")){
		    		itemOpen = false;
		    	}
		    	
		    	if (qName.equalsIgnoreCase("ACTIONS")) {  
		    		actionsOpen = false;
		    	}
		    			    	
		    	//Store the properties of a character: name, gender and role
		    	if (charOpen){
				     if (qName.equalsIgnoreCase("NAME")) {  
				    	 characters.lastElement().setName(currentString.trim());  
					 }
				     if (qName.equalsIgnoreCase("GENDER")) {
				    	 characters.lastElement().setGender(currentString.trim());
				     }
				     if (qName.equalsIgnoreCase("ROLE")) {
				    	 characters.lastElement().setConcept(currentString.trim());
				     }
		    	}
		    	
		    	//Store the property of items and locations: the word sense that connects
		    	//it to the lexicon
		    	if (itemOpen || locationOpen){
				     if (qName.equalsIgnoreCase("SENSE")) {
				    	 characters.lastElement().setConcept(currentString.trim());
				     }
		    	}
		    	
		    	//Store the adjective that can be used to describe an item, location or character
		    	if (itemOpen || charOpen || locationOpen){
				     if (qName.equalsIgnoreCase("ADJECTIVE")) {
				    	 lastAdjectives.add(currentString);
				     }
		    	}
		    	
			    if (qName.equalsIgnoreCase("LOCATION")&&!innerLocationOpen){
			    	locationOpen = false;
			    }
			    
		    	if (qName.equalsIgnoreCase("LOCATIONS")){
		    		if (innerLocationOpen)
		    			innerLocationOpen = false;
		    		else
		    			locationsOpen = false;
		    	}		     
		    }  
		   };  
		     
		   // parse the XML specified in the given path and uses supplied  
		   // handler to parse the document  
		   // this calls startElement(), endElement() and character() methods  
		   // accordingly  
		   saxParser.parse(worldFile, defaultHandler);  
		 } 
	
	//This code is actually not even THAT horrible.
	public static String getSettingFile(String fabula){
		File fabulaFile = new File(fabula);
		File parent = fabulaFile.getAbsoluteFile().getParentFile();
		String path = parent.getAbsolutePath();
		String localFilename = StringUtil.getSettingFile(fabula,PREFIX_SETTINGSFILE,SUFFIX_SETTINGSFILE);
		return path+File.separatorChar+localFilename;
	}
	
	public Map<String,Vector<String>> getAdjectives(){
		return adjectives;
	}
}
