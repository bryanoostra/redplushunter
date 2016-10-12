package narrator.reader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import narrator.lexicon.Entry;
import narrator.shared.Settings;
import natlang.rdg.libraries.LibraryConstants;

// THIS IS THE ACTUAL LEXICONREADER!
public class LexiconReader2 implements LibraryConstants{
	private Map<String,Vector<Entry>> entries;
	private String lexiconFile;
	
	public static final String PREFIX_LEXICONFILE = "<!--Lexicon file";
	public static final String SUFFIX_LEXICONFILE = "-->";
	
	public LexiconReader2(String fabula){
		entries = new HashMap<String,Vector<Entry>>();
		File fabulaFile = new File(fabula);
		File parent = fabulaFile.getAbsoluteFile().getParentFile();
		String path = parent.getAbsolutePath();		
		this.lexiconFile=StringUtil.getSettingFile(fabula,PREFIX_LEXICONFILE+" lang=\""+Settings.LANGUAGE+"\": ",SUFFIX_LEXICONFILE);
		lexiconFile = path+File.separatorChar+lexiconFile;	
		//System.out.println(lexiconFile);
		getXML();
	}
	
	public Map<String,Vector<Entry>> getEntryMap(){
		return entries;
	}

	private void getXML() {
		try{
			// obtain and configure a SAX based parser  
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();  
			  
			// obtain object for SAX parser  
			SAXParser saxParser = saxParserFactory.newSAXParser();  
			  
			// default handler for SAX handler class  
			// all three methods are written in handler's body  
			DefaultHandler defaultHandler = new DefaultHandler(){
				private String currentString;
				
				boolean senseOpen,
						entryOpen;
				
				String currentID = "";
				Vector<Entry> currentList = null;
						
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					//Create a new word sense
					if (qName.equalsIgnoreCase("SENSE")){
						currentID = attributes.getValue("id");
						senseOpen = true;
						currentList = new Vector<Entry>();
						entries.put(currentID, currentList);
					}
					
					//Create a new entry
					if (senseOpen && qName.equalsIgnoreCase("ENTRY")){
						entryOpen = true;
						currentList.add(new Entry());
						System.out.println("Found entry in: "+currentID);
					}
					
					//Empty the string with contents of the tag
					currentString = "";
				}
				
				// prints data stored in between '<' and '>' tags  
				public void characters(char ch[], int start, int length) throws SAXException {
					String s = new String(ch, start, length).trim();
					//Store everything in the current tag into a temporary string.
					currentString += s;
				}
				
				public void endElement(String uri, String localName, String qName) throws SAXException {
					if (qName.equalsIgnoreCase("SENSE")){
						senseOpen = false;
					}
					
					if (qName.equalsIgnoreCase("ENTRY")){
						entryOpen = false;
					}
					
					//Only allow properties to be stored if we're currently in an entry.
					if (entryOpen){
						Entry currentEntry = currentList.lastElement();
						currentEntry.setConcept(currentID);
						
						if (qName.equalsIgnoreCase("ROOT")){
							currentEntry.setRoot(currentString);
						}
						if (qName.equalsIgnoreCase("PARTOFSPEECH")){
							currentEntry.setPos(currentString);
						}
						if (qName.equalsIgnoreCase("DETERMINER")){
							currentEntry.setDet(currentString);
						}
						if (qName.equalsIgnoreCase("GENDER")){
							currentEntry.setGen(currentString);
						}
						if (qName.equalsIgnoreCase("PREPOSITION")){
							currentEntry.setPrep(currentString);
						}
						if (qName.equalsIgnoreCase("PARTICLE")){
							currentEntry.setSvp(currentString);
						}
						if (qName.equalsIgnoreCase("DEPENDENCYLABEL")){
							currentEntry.setDepLabel(currentString);
						}
						if (qName.equalsIgnoreCase("PLURAL")){
							currentEntry.setPlural(currentString);
						}
						if (qName.equalsIgnoreCase(ADJINFLECT)){
							currentEntry.setInflectedAdjective(currentString);
						}					
						if (qName.equalsIgnoreCase("AUXVERB")){
							currentEntry.setAuxverb(currentString);
						}
						if (qName.equalsIgnoreCase("ATPREPOSITION")){
							currentEntry.setAtPreposition(currentString);
						}
						
						//Store the verb tenses
						Map<String,String> tenses = currentEntry.getTenses();
						if (qName.equalsIgnoreCase(FIRSTSINGULARPRESENT)){
							tenses.put(FIRSTSINGULARPRESENT,currentString);
						}
						if (qName.equalsIgnoreCase(SECONDSINGULARPRESENT)){
							tenses.put(SECONDSINGULARPRESENT,currentString);
						}
						if (qName.equalsIgnoreCase(THIRDSINGULARPRESENT)){
							tenses.put(THIRDSINGULARPRESENT,currentString);
						}
						if (qName.equalsIgnoreCase(PLURALPRESENT)){
							tenses.put(PLURALPRESENT,currentString);
						}
						if (qName.equalsIgnoreCase(SINGULARSIMPLEPAST)){
							tenses.put(SINGULARSIMPLEPAST, currentString);
						}
						if (qName.equalsIgnoreCase(PLURALSIMPLEPAST)){
							tenses.put(PLURALSIMPLEPAST, currentString);
						}
						if (qName.equalsIgnoreCase(PARTICIPLE)){
							tenses.put(PARTICIPLE, currentString);
						}
					}
				}
				
			}; //end of the inner class
			saxParser.parse(lexiconFile, defaultHandler);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		LexiconReader2 lexicon = new LexiconReader2("simple.graphml");
		
		//test the contents of the lexicon
		System.out.println("Lexicon: ");
		Map<String,Vector<Entry>> map = lexicon.getEntryMap();	
		for (Vector<Entry> v : map.values()){
			for (Entry e : v){
				System.out.println(e);
			}
		}
	}
}