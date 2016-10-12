package narrator.reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import narrator.lexicon.Entry;

@Deprecated
public class LexiconReader {
	private Map<String,Vector<Entry>> entries;
	private String worldFile;
	private String language;
	
	public LexiconReader(String filename, String language){
		entries = new HashMap<String,Vector<Entry>>();
		this.worldFile=filename;
		this.language=language;
		getXML();
	}
	
	public Map<String,Vector<Entry>> getEntryMap(){
		return entries;
	}
	
	private void getXML(){
		try{
			// obtain and configure a SAX based parser  
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();  
			  
			// obtain object for SAX parser  
			SAXParser saxParser = saxParserFactory.newSAXParser();  
			  
			// default handler for SAX handler class  
			// all three methods are written in handler's body  
			DefaultHandler defaultHandler = new DefaultHandler(){  
				
			boolean locationOpen,
				itemOpen,
				characterOpen,
				actionOpen, 
				emotionOpen,
				roleOpen,
				nameOpen = false;
			
			
			boolean presentationOpen, 
				narrationOpen, 
				descriptiveOpen, 
				entryOpen = false;
			
			boolean rootOpen,
				partofspeechOpen,
				prepositionOpen,
				participleOpen,
				deplabelOpen,
				determinerOpen,
				genderOpen = false;
				
			
			String currentID = "";
			Vector<Entry> currentList = null;
				
			// this method is called every time the parser gets an open tag '<'  
			// identifies which tag is being open at time by assigning an open flag  
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				//Only read descriptive data if any of the entities that allow
				//a lexicon is open.				
				if (qName.equalsIgnoreCase("LOCATION") && !characterOpen && !itemOpen){
					currentID = attributes.getValue("id");
					locationOpen = true;
				}
				
				if (qName.equalsIgnoreCase("ITEM")){
					currentID = attributes.getValue("id");
					itemOpen = true;
				}
				
				if (qName.equalsIgnoreCase("CHARACTER")){
					currentID = attributes.getValue("id");
					characterOpen = true;
				}
				
				if (qName.equalsIgnoreCase("ACTION")){
					currentID = attributes.getValue("type");
					actionOpen = true;
				}
				
				if (qName.equalsIgnoreCase("EMOTION")){
					currentID = attributes.getValue("type");
					emotionOpen = true;
				}
				
				if (qName.equalsIgnoreCase("ROLE")){
					currentID = attributes.getValue("id");
					roleOpen = true;
				}
				
				if (characterOpen && qName.equalsIgnoreCase("NAME")){
					currentID = attributes.getValue("id");
					nameOpen = true;
				}

				if ((locationOpen || itemOpen || characterOpen || actionOpen || emotionOpen)
						&& qName.equalsIgnoreCase("PRESENTATION")){
					presentationOpen = true;
				}
				
				if (presentationOpen && qName.equalsIgnoreCase("NARRATION")){
					narrationOpen = true;
				}
				
				if (narrationOpen && qName.equalsIgnoreCase("DESCRIPTIVE")){
					//Only open this tag if the language matches the one we want.
					if (attributes.getValue("lang").equalsIgnoreCase(language)){
						descriptiveOpen = true;
						currentList = new Vector<Entry>();
						entries.put(currentID, currentList);
					}
				}
				
				if (descriptiveOpen && qName.equalsIgnoreCase("ENTRY")){
					entryOpen = true;
					currentList.add(new Entry());
					//System.out.println("Found entry in: "+currentID);
				}
				
				if (entryOpen){
					if (qName.equalsIgnoreCase("ROOT")){
						rootOpen = true;
					}
					if (qName.equalsIgnoreCase("PARTOFSPEECH")){
						partofspeechOpen = true;
					}
					if (qName.equalsIgnoreCase("DETERMINER")){
						determinerOpen = true;
					}
					if (qName.equalsIgnoreCase("GENDER")){
						genderOpen = true;
					}
					if (qName.equalsIgnoreCase("PREPOSITION")){
						prepositionOpen = true;
					}
					if (qName.equalsIgnoreCase("PARTICIPLE")){
						participleOpen = true;
					}
					if (qName.equalsIgnoreCase("DEPENDENCYLABEL")){
						deplabelOpen = true;
					}
				}
			}  
			  
			// prints data stored in between '<' and '>' tags  
			public void characters(char ch[], int start, int length) throws SAXException {
				String s = new String(ch, start, length).trim();
				if (entryOpen){
					//System.out.println(s);
					Entry currentEntry = currentList.lastElement();
					currentEntry.setConcept(currentID);
					if (rootOpen){
						currentEntry.setRoot(s);
					}
					if (partofspeechOpen){
						currentEntry.setPos(s);
					}
					if (prepositionOpen){
						currentEntry.setPrep(s);
					}
					if (participleOpen){
						currentEntry.setSvp(s);
					}
					if (deplabelOpen){
						currentEntry.setDepLabel(s);
					}
					if (determinerOpen){
						currentEntry.setDet(s);
					}
					if (genderOpen){
						currentEntry.setGen(s);
					}
					
				}
			}  
			  
		    // calls by the parser whenever '>' end tag is found in xml   
		    // makes tags flag to 'close'  
		    public void endElement(String uri, String localName, String qName) throws SAXException {
				if (qName.equalsIgnoreCase("LOCATION")){
					locationOpen = false;
				}
				
				if (qName.equalsIgnoreCase("ITEM")){
					itemOpen = false;
				}
				
				if (qName.equalsIgnoreCase("CHARACTER")){
					characterOpen = false;
				}
				
				if (qName.equalsIgnoreCase("ACTION")){
					actionOpen = true;
				}
				
				if (qName.equalsIgnoreCase("EMOTION")){
					emotionOpen = true;
				}
				
				if (qName.equalsIgnoreCase("ROLE")){
					roleOpen = false;
				}
				
				if (qName.equalsIgnoreCase("PRESENTATION")){
					presentationOpen = false;
				}
				if (qName.equalsIgnoreCase("NARRATION")){
					narrationOpen = false;
				}
				if (qName.equalsIgnoreCase("DESCRIPTIVE")){
					descriptiveOpen = false;
				}
				if (qName.equalsIgnoreCase("ENTRY")){
					entryOpen = false;
				}
				
				if (entryOpen){
					if (qName.equalsIgnoreCase("ROOT")){
						rootOpen = false;
					}
					if (qName.equalsIgnoreCase("PARTOFSPEECH")){
						partofspeechOpen = false;
					}
					if (qName.equalsIgnoreCase("DETERMINER")){
						determinerOpen = false;
					}
					if (qName.equalsIgnoreCase("GENDER")){
						genderOpen = false;
					}
					if (qName.equalsIgnoreCase("PREPOSITION")){
						prepositionOpen = false;
					}
					if (qName.equalsIgnoreCase("PARTICIPLE")){
						participleOpen = false;
					}
					if (qName.equalsIgnoreCase("DEPENDENCYLABEL")){
						deplabelOpen = false;
					}
				}
		    }  
		    }; //This denotes the end of the defaultHandler inner class. 
		     
		   // parse the XML specified in the given path and uses supplied  
		   // handler to parse the document  
		   // this calls startElement(), endElement() and character() methods  
		   // accordingly  
		   saxParser.parse(worldFile, defaultHandler);  
		  } catch (Exception e) {  
		   e.printStackTrace();  
		  }  
	}

}
