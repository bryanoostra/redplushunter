package natlang.rdg.lexicalchooser;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import natlang.debug.LogFactory;
import natlang.rdg.datafilessetter.DataInstance;
import natlang.rdg.datafilessetter.DataInstanceSetter;
import natlang.rdg.ontmodels.OntModels;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * The lexicon, mapping concepts to dutch words and containing other linguistic information
 * @author Nanda
 *
 */
public class Lexicon
{
	public Vector entries;
	
	private OntModel m;
	private NamedGraphSet graphset;
	
	public static final String NARRATOR_KNOWLEDGE = "http://www.owl-ontologies.com/NarratorKnowledge.owl#";
	public static final String LEXICON_ENTRY = NARRATOR_KNOWLEDGE + "LexiconEntry";
	public static final String LEXICAL_OBJECT = NARRATOR_KNOWLEDGE + "LexicalObject";
	
	public static final String NK_ROOT = NARRATOR_KNOWLEDGE + "root";
	public static final String NK_POS = NARRATOR_KNOWLEDGE + "pos";
	public static final String NK_DET = NARRATOR_KNOWLEDGE + "determiner";
	public static final String NK_GEN = NARRATOR_KNOWLEDGE + "gender";
	public static final String NK_PREP = NARRATOR_KNOWLEDGE + "prep";
	public static final String NK_SVP = NARRATOR_KNOWLEDGE + "svp";
	public static final String NK_DEPLABEL = NARRATOR_KNOWLEDGE + "deplabel";
	public static final String NK_MASSNOUN = NARRATOR_KNOWLEDGE + "isMassNoun";
	
	public static final String NK_VERB = NARRATOR_KNOWLEDGE + "verb";
	public static final String NK_NOUN = NARRATOR_KNOWLEDGE + "noun";
	public static final String NK_NOM = NARRATOR_KNOWLEDGE + "nom";
	
	public static final String NK_NAME = NARRATOR_KNOWLEDGE + "name";
	
	private Logger logger;
	
	
	/**
	 * Creates the entire lexicon given the filename (in turtle format).
	 * @param filename
	 */
	public Lexicon(String filename) { // filename has extension .trig
		logger = LogFactory.getLogger(this);
		entries = new Vector();
		
		graphset = new NamedGraphSetImpl();
		
		graphset.read("NarratorKnowledge.owl", "TURTLE");
		graphset.read(filename, "TURTLE");
		
		
		m = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
//		m = ModelFactory.createOntologyModel(  );
		m.add(graphset.asJenaModel("http://example.org/defaultgraph"));
		
		ResIterator ri = m.listSubjects();
		
		
		while(ri.hasNext()){
			Resource r = ri.nextResource();
			OntResource or = m.getOntResource(r);
			if(or.hasRDFType(LEXICON_ENTRY)){
			
				String 	sConcept 	= or.getLocalName(), 
						sRoot   	= "",
						sPos   		= "",
						sDet   		= "",
						sGen     	= "",
						sPrep   	= "",
						sSvp   		= "",
						sDeplabel	= "";
				boolean isMassNoun = false;
				
				sRoot = getStringContent(or, NK_ROOT);
				sPos = getResourceContent(or, NK_POS);
				sDet = getResourceContent(or, NK_DET);
				sGen = getResourceContent(or, NK_GEN);
				sPrep = getStringContent(or, NK_PREP);
				sSvp = getStringContent(or, NK_SVP);
				sDeplabel = getResourceContent(or, NK_DEPLABEL);
				isMassNoun = getBooleanContent(or, NK_MASSNOUN);
				
				entries.addElement(new Entry(sConcept, sRoot, 	sPos, sDet, sGen, "", sPrep, sSvp, sDeplabel, isMassNoun));
				if(sPos.equals(NK_NOM)){
					
				}
			}
			
		}
		
		logger.info("Number of entries in lexicon: " + entries.size());
	}
	
	private String getResourceContent(OntResource or, String property){
		StmtIterator sit = m.listStatements(or, m.createProperty(property), (RDFNode)null);
		if(sit.hasNext()){
			return ((Resource)sit.nextStatement().getObject()).getLocalName();
		}
		return "";
	}
	
	private String getStringContent(OntResource or, String property){
		StmtIterator sit = m.listStatements(or, m.createProperty(property), (RDFNode)null);
		if(sit.hasNext()){
			return ((Literal)sit.nextStatement().getObject()).getString();
		}
		return "";
	}
	
	private Boolean getBooleanContent(OntResource or, String property){
		StmtIterator sit = m.listStatements(or, m.createProperty(property), (RDFNode)null);
		if(sit.hasNext()){
			return ((Literal)sit.nextStatement().getObject()).getBoolean();
		}
		return false;
	}


	
	/**
	 * Creates the entire lexicon
	 *
	 */
	public Lexicon()
	{		
		logger = LogFactory.getLogger(this);
		
		entries = new Vector();
		
		
		m = OntModels.ontModelNarrator;
		
		if(m == null){
			createOldLexicon();
			return;
		}
		
		deriveLexiconEntries();
		
		logger.info("Number of entries in lexicon: " + entries.size());
		
		
	}
	
	private void deriveLexiconEntries(){
		ResIterator ri = m.listSubjects();
		
		while(ri.hasNext()){
			Resource r = ri.nextResource();
			OntResource or = m.getOntResource(r);
			if(or.hasRDFType(LEXICON_ENTRY)){
			
				String 	sConcept 	= or.getLocalName(), 
						sRoot   	= "",
						sPos   		= "",
						sDet   		= "",
						sGen     	= "",
						sPrep   	= "",
						sSvp   		= "",
						sDeplabel	= "";
				boolean isMassNoun = false;
				
				sRoot = getStringContent(or, NK_ROOT);
				sPos = getResourceContent(or, NK_POS);
				sDet = getResourceContent(or, NK_DET);
				sGen = getResourceContent(or, NK_GEN);
				sPrep = getStringContent(or, NK_PREP);
				sSvp = getStringContent(or, NK_SVP);
				sDeplabel = getResourceContent(or, NK_DEPLABEL);
				isMassNoun = getBooleanContent(or, NK_MASSNOUN);
				
				Entry e = new Entry(sConcept, sRoot, 	sPos, sDet, sGen, "", sPrep, sSvp, sDeplabel, isMassNoun);
				logger.info("Adding new entry: " + e.toString());
				entries.addElement(e);
			}
			else if(or.hasRDFType(LEXICAL_OBJECT) && getStringContent(or, NK_NAME) != null){
				String 	sConcept 	= or.getLocalName(), 
						sRoot   	= "",
						sPos   		= "",
						sDet   		= "",
						sGen     	= "",
						sPrep   	= "",
						sSvp   		= "",
						sDeplabel	= "";
				boolean isMassNoun = false;
				
				sRoot = getStringContent(or, NK_NAME);
				sPos = "nom";
				sGen = getResourceContent(or, NK_GEN);
				
				logger.info("Character: " + sConcept + ", " + sRoot + ", " + sGen);
				Entry e = new Entry("_"+sConcept+"_", sRoot, 	sPos, sDet, sGen, "", sPrep, sSvp, sDeplabel, isMassNoun);
				logger.info("Adding new entry: " + e.toString());
				entries.addElement(e);
			}
			
		}
	}
	
	private void createOldLexicon(){
//      concept  root   pos   det   gen     morph           prep   svp   deplabel
		entries.addElement(new Entry("SailToLand", "vaar", 	"verb", "", "", "", "naar", "", "ld"));
		entries.addElement(new Entry("GetOffBoat", "ga", 	"verb", "", "", "", "bij", "aan land", "ld"));
		
		entries.addElement(new Entry("FillBoatWithWater", "vul", 	"verb", "", "", "", "bij", "", ""));
		entries.addElement(new Entry("le_Chuck", "le Chuck", "nom", "", "male", "", "", "", ""));
		
		
		entries.addElement(new Entry("mooredAt", "land", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("hasOpenCloseProperty", "ben", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("type", "ben", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("hasWaterSupply", "heb", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("owns", "bezit", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("hasFullEmptyProperty", "voorraad", 	"noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("health", "gezondheid", 	"noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("supportedBy", "steun", 	"verb", "", "", "", "op", "", "ld"));
		entries.addElement(new Entry("containedBy", "zit", 	"verb", "", "", "", "in", "", "ld"));
		entries.addElement(new Entry("GetTreasure", "heb", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("Run", "ren", "verb", "", "", "", "naar", "", "ld"));
		entries.addElement(new Entry("OpenContainer", "open", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("Treasure", "schatkist", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("Hold", "onderruim", "noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("Hatch", "luik", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("Ladder", "ladder", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("Pond", "vijver", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("Water", "water", "noun", "het", "neutral", "", "", "", "", true));
		entries.addElement(new Entry("WaterSupply", "watervoorraad", "noun", "de", "neutral", "", "", "", ""));
		
		entries.addElement(new Entry("WoundEnemy", "verwond", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("Stab", "steek", 	"verb", "", "", "", "", "neer", ""));
		entries.addElement(new Entry("wornBy", "draag", 	"verb", "", "", "", "door", "", ""));
		entries.addElement(new Entry("belt", "riem", 	"noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("rapier", "zwaard", 	"noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("heldBy", "houd", 	"verb", "", "", "", "", "vast", "ld"));
		
		entries.addElement(new Entry("AnnounceRum", "verkondig", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("SayLetsGetSomeRum", "stel", 	"verb", "", "", "", "", "voor", "ld"));
		entries.addElement(new Entry("OutOfWater", "ben", 	"verb", "", "", "", "", "leeg", ""));
		entries.addElement(new Entry("RefillWaterSupply", "vul", 	"verb", "", "", "", "bij", "", ""));
		entries.addElement(new Entry("OpenDoor", "open", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("WalkFromToDoor", "loop", 	"verb", "", "", "", "", "naar", "ld"));
		
		
		entries.addElement(new Entry("Deck", "dek", 	"noun", "het", "place", "", "", "", ""));
		
		entries.addElement(new Entry("TreasureIsland", "schateiland", 	"noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("Ship", "schip", 	"noun", "het", "place", "", "", "", ""));
		
		entries.addElement(new Entry("lotagent", "LoTaGent", 	"noun", "de", "neutral", "", "", "", ""));
		
		entries.addElement(new Entry("GetBottle", "heb", 	"verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("TakeOut", "pak", 	"verb", "", "", "", "uit", "", "ld"));
		entries.addElement(new Entry("RumBottle", "rumfles", 	"noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("Crate", "krat", 	"noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("Pirate", "piraat", 	"noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("Captain", "kapitein", 	"noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("Lord", "heerschap", 	"noun", "het", "male", "", "", "", ""));
		
		// lolli probeersel:
		entries.addElement(new Entry("ToddleOffTo", "huppel", 	"verb", "", "", "", "naar", "", "ld"));
		entries.addElement(new Entry("Kick", "schop", "verb", "", "", "", "", "", "ld"));
		entries.addElement(new Entry("HaveIce", "ijs", 	"noun", "het", "neutral", "", "", "", ""));
//		entries.addElement(new Entry("goto", 		"ga", 		"verb", "", "", "", "naar", "", "ld"));
//		entries.addElement(new Entry("goto", 		"loop", 	"verb", "", "", "", "naar", "", "ld"));
//		entries.addElement(new Entry("goto", 		"wandel", 	"verb", "", "", "", "naar", "", "ld"));
		
		entries.addElement(new Entry("Perception", "zie", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("linda", "Linda", "nom", "", "female", "", "", "", ""));
		entries.addElement(new Entry("otto", "Otto", "nom", "", "male", "", "", "", ""));
		entries.addElement(new Entry("IceCreamTruck", "ijsverkooptent", "noun", "de", "place", "", "", "", ""));
		entries.addElement(new Entry("park", "park", "noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("ice", "ijsje", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("vanilla_ice", "ijsje", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("Buy", "koop", "verb", "", "", "", "", "van", "predc"));
//		entries.addElement(new Entry("iAction_linda_4", "koop", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("money", "geld", "noun", "het", "", "", "", "", ""));
		
		// plop voorbeeldje....
		entries.addElement(new Entry("hunger", "honger", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("think", "denk", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("takefrom", "pak", "verb", "", "", "", "", "op", ""));
		entries.addElement(new Entry("islocated", "lig", "verb", "", "", "", "in", "", "ld"));
		entries.addElement(new Entry("located", "ben", "verb", "", "", "", "in", "", "ld"));
				
		
		//                            concept  root   pos   det   gen     morph           prep   svp   deplabel
		
		//verbs
		entries.addElement(new Entry("appear", "verschijn", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("arrive", "kom", "verb", "", "", "", "bij", "aan", "ld"));
		entries.addElement(new Entry("ask", "vraag", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("be", "ben", "verb", "", "", "", "bij", "", "ld"));
		entries.addElement(new Entry("become", "word", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("blow", "waai", "verb", "", "", "", "door", "", "ld"));
		entries.addElement(new Entry("break", "breek", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("bring", "breng", "verb", "", "", "", "naar", "", "ld"));		
		entries.addElement(new Entry("call", "heet", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("capture", "sluit", "verb", "", "", "", "in", "op", "ld"));
		entries.addElement(new Entry("catch", "vang", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("climb", "klim", "verb", "", "", "", "in", "", "ld"));
		entries.addElement(new Entry("collapse", "stort", "verb", "", "", "", "", "in", ""));
		entries.addElement(new Entry("comment", "merk", "verb", "", "", "", "op", "aan", "predc"));
		entries.addElement(new Entry("cry", "huil", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("eat", "eet", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("enter", "ga", "verb", "", "", "", "", "binnen", ""));
		entries.addElement(new Entry("escape", "ontsnap", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("exist", "lig", "verb", "", "", "", "in", "", "ld"));
		entries.addElement(new Entry("fall", "val", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("find", "vind", "verb", "", "", "", "", "terug", ""));
		entries.addElement(new Entry("flee", "vlucht", "verb", "", "", "", "", "", ""));		
		entries.addElement(new Entry("follow", "volg", "verb", "", "", "", "naar", "", "ld"));
		entries.addElement(new Entry("give", "geef", "verb", "", "", "", "aan", "", "predc"));
		entries.addElement(new Entry("goto", "ga", "verb", "", "", "", "naar", "", "ld"));
		entries.addElement(new Entry("goto", "loop", "verb", "", "", "", "naar", "", "ld"));
		entries.addElement(new Entry("goto", "wandel", "verb", "", "", "", "naar", "", "ld"));
		entries.addElement(new Entry("have", "heb", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("hit", "sla", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("hold", "houd", "verb", "", "", "", "", "vast", ""));
		entries.addElement(new Entry("hop", "huppel", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("hum", "neurie", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("jump", "spring", "verb", "", "", "", "", "binnen", ""));
		entries.addElement(new Entry("keep", "houd", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("kick", "schop", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("kidnap", "ontvoer", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("kill", "vermoord", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("laugh", "lach", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("laughat", "lach", "verb", "", "", "", "", "uit", ""));
		entries.addElement(new Entry("live", "woon", "verb", "", "", "", "in", "", "ld"));
		entries.addElement(new Entry("look", "kijk", "verb", "", "", "", "", "rond", ""));
		entries.addElement(new Entry("love", "houd", "verb", "", "", "", "van", "", "predc"));
		entries.addElement(new Entry("make", "maak", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("open", "open", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("pickup", "pak", "verb", "", "", "", "", "op", ""));
		entries.addElement(new Entry("pick", "kies", "verb", "", "", "", "voor", "uit", "predc"));
		entries.addElement(new Entry("pound", "bons", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("puton", "zet", "verb", "", "", "", "op", "", "ld"));
		entries.addElement(new Entry("receive", "krijg", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("run", "ren", "verb", "", "", "", "", "weg", ""));
		entries.addElement(new Entry("scream", "schreeuw", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("search", "zoek", "verb", "", "", "", "naar", "", "predc"));
		entries.addElement(new Entry("shake", "tril", "verb", "", "", "", "van", "", "predc"));
		entries.addElement(new Entry("shine", "schijn", "verb", "", "", "", "door", "", "predc"));
		entries.addElement(new Entry("sing", "zing", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("sit", "zit", "verb", "", "", "", "op", "", "predc"));
		entries.addElement(new Entry("slaughter", "slacht", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("stamp", "stamp", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("stand", "sta", "verb", "", "", "", "bij", "", "ld"));
		entries.addElement(new Entry("stay", "blijf", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("struggle", "tegenstribbel", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("try", "probeer", "verb", "", "", "", "", "", ""));		
		entries.addElement(new Entry("visit", "bezoek", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("wail", "jammer", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("walk", "loop", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("want", "wil", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("whistle", "fluit", "verb", "", "", "", "", "", ""));
				
		//perceptions (also verbs)
		entries.addElement(new Entry("find", "vind", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("hear", "hoor", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("see", "zie", "verb", "", "", "", "", "", ""));
						
		//names
		entries.addElement(new Entry("amalia", "Amalia", "nom", "", "female", "", "", "", ""));
		entries.addElement(new Entry("brutus", "Brutus", "nom", "", "male", "", "", "", ""));
		entries.addElement(new Entry("charles", "Charles", "nom", "", "male", "", "", "", ""));
		entries.addElement(new Entry("diana", "Diana", "nom", "", "female", "", "", "", ""));
		entries.addElement(new Entry("plop", "Plop", "nom", "", "male", "", "", "", ""));
		//entries.addElement(new Entry("alexander", "Alexander", "nom", "", "male", "", "", "", ""));
		
		//nouns
		entries.addElement(new Entry("afternoon", "middag", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("animal", "dier", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("apple", "appel", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("autumn", "herfst", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("bedroom", "slaapkamer", "noun", "de", "place", "", "", "", ""));
		entries.addElement(new Entry("behaviour", "gedrag", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("bird", "vogel", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("branch", "tak", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("bridge", "brug", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("building", "gebouw", "noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("castle", "kasteel", "noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("cat", "poes", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("child", "kind", "noun", "het", "male", "", "", "", ""));
		entries.addElement(new Entry("cottage", "hut", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("country", "land", "noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("day", "dag", "noun", "de", "time", "", "", "", ""));		
		entries.addElement(new Entry("desert", "woestijn", "noun", "de", "place", "", "", "", ""));
		entries.addElement(new Entry("dwarf", "kabouter", "noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("edge", "rand", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("evening", "avond", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("father", "vader", "noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("fear", "angst", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("foot", "voet", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("forest", "bos", "noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("fork", "vork", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("gate", "poort", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("gift", "cadeau", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("girl", "meisje", "noun", "het", "female", "", "", "", ""));
		entries.addElement(new Entry("grandmother", "oma", "noun", "de", "female", "", "", "", ""));
		entries.addElement(new Entry("ground", "grond", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("hand", "hand", "noun", "de", "neutral", "", "", "", ""));		
		entries.addElement(new Entry("heart", "hart", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("horse", "paard", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("house", "huis", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("husband", "echtgenoot", "noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("joy", "vreugde", "noun", "de", "neutral", "", "", "", ""));		
		entries.addElement(new Entry("jumpn", "sprongetje", "noun", "het", "neutral", "", "", "", ""));		
		entries.addElement(new Entry("king", "koning", "noun", "de", "male", "", "", "", ""));		
		entries.addElement(new Entry("king", "vorst", "noun", "de", "male", "", "", "", ""));		
		//entries.addElement(new Entry("king", "king", "noun", "de", "", "", "", ""));		
		entries.addElement(new Entry("kiss", "zoen", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("knife", "mes", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("knight", "ridder", "noun", "de", "male", "", "", "", ""));		
		entries.addElement(new Entry("lamb", "lam", "noun", "het", "neutral", "", "", "", ""));		
		entries.addElement(new Entry("leg", "been", "noun", "het", "neutral", "", "", "", ""));		
		entries.addElement(new Entry("man", "man", "noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("moon", "maan", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("morning", "ochtend", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("mountain", "berg", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("mouth", "mond", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("night", "nacht", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("object", "ding", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("path", "pad", "noun", "het", "place", "", "", "", ""));
		entries.addElement(new Entry("person", "persoon", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("prince", "prins", "noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("princess", "prinses", "noun", "de", "female", "", "", "", ""));
		//entries.addElement(new Entry("princess", "meisje", "noun", "het", "female", "", "", "", ""));
		entries.addElement(new Entry("rain", "regen", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("sound", "geluid", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("spoon", "lepel", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("spring", "lente", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("summer", "zomer", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("sun", "zon", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("swamp", "moeras", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("sword", "zwaard", "noun", "het", "neutral", "", "", "", ""));
		entries.addElement(new Entry("throat", "keel", "noun", "de", "neutral", "", "", "", ""));				
		entries.addElement(new Entry("tree", "boom", "noun", "de", "neutral", "", "", "", ""));				
		entries.addElement(new Entry("villain", "schurk", "noun", "de", "male", "", "", "", ""));
		entries.addElement(new Entry("wind", "wind", "noun", "de", "neutral", "", "", "", ""));		
		entries.addElement(new Entry("winter", "winter", "noun", "de", "time", "", "", "", ""));
		entries.addElement(new Entry("woman", "vrouw", "noun", "de", "female", "", "", "", ""));
		
		//adjectives
		entries.addElement(new Entry("afraid", "bang", "adj", "", "", "", "voor", "", ""));
		entries.addElement(new Entry("angry", "boos", "adj", "", "", "", "op", "", ""));
		entries.addElement(new Entry("annoying", "vervelend", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("beautiful", "mooi", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("big", "groot", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("careful", "voorzichtig", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("conceited", "verwaand", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("cruel", "wreed", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("dark", "donker", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("dead", "dood", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("dense", "dicht", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("extraordinary", "buitengewoon", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("extremely", "hevig", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("faraway", "ver", "adj", "", "", "", "",  "",""));
		entries.addElement(new Entry("frightened", "geschrokken", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("happy", "blij", "adj", "", "", "", "", "", ""));		
		entries.addElement(new Entry("happy", "vrolijk", "adj", "", "", "", "", "", ""));		
		entries.addElement(new Entry("happy", "gelukkig", "adj", "", "", "", "", "", ""));		
		entries.addElement(new Entry("heavy", "zwaar", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("high", "hoog", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("hopeful", "hoopvol", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("hungry", "hongerig", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("inlove", "verliefd", "adj", "", "", "", "op", "", ""));
		entries.addElement(new Entry("jealous", "jaloers", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("jolly", "vrolijk", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("kind", "aardig", "adj", "", "", "", "", "", ""));
		//entries.addElement(new Entry("located", "gelegen", "adj", "", "", "", "in", "", ""));
		entries.addElement(new Entry("locked", "op slot", "adj", "", "", "", "", "", ""));		// kan ook anders, staat mbv svp in backup 21 september
		entries.addElement(new Entry("loud", "hard", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("lucky", "gelukkig", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("mean", "gemeen", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("missing", "kwijt", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("new", "nieuw", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("old", "oud", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("poor", "armoedig", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("quick", "snel", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("quiet", "zacht", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("raw", "guur", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("relieved", "opgelucht", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("rich", "rijk", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("rough", "hardhandig", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("sad", "verdrietig", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("scared", "bang", "adj", "", "", "", "voor", "", ""));
		entries.addElement(new Entry("sick", "ziek", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("small", "klein", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("strange", "vreemd", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("sweet", "lief", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("terrifying", "angstaanjagend", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("ugly", "lelijk", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("very", "heel", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("weak", "zwak", "adj", "", "", "", "", "", ""));
		entries.addElement(new Entry("young", "jong", "adj", "", "", "", "", "", ""));
		
		//time
		entries.addElement(new Entry("always", "altijd", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("finally", "uiteindelijk", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("suddenly", "plotseling", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("yesterday", "gisteren", "adv", "", "", "", "", "", ""));
		
		//place
		entries.addElement(new Entry("atcourt", "aan het hof", "noun", "het", "", "", "", "", ""));
		entries.addElement(new Entry("atbranch", "op een tak", "noun", "de", "", "", "", "", ""));
		
		//place and time expressions (whole phrases are stored instead of generating prep-nodes)
		entries.addElement(new Entry("atotherside", "aan de overkant", "expr", "", "", "", "", "", ""));
		entries.addElement(new Entry("everyday", "elke dag", "expr", "", "", "", "", "", ""));
		entries.addElement(new Entry("oneday", "op een dag", "expr", "", "", "", "", "", ""));
		entries.addElement(new Entry("onenight", "op een nacht", "expr", "", "", "", "", "", ""));
		entries.addElement(new Entry("onemorning", "op een ochtend", "expr", "", "", "", "", "", ""));
		entries.addElement(new Entry("poundingheart", "met een bonzend hart", "expr", "", "", "", "", "", ""));
		entries.addElement(new Entry("shakinglegs", "op trillende benen", "expr", "", "", "", "", "", ""));
				
		//others
		entries.addElement(new Entry("all", "alle", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("down", "beneden", "adv", "", "", "", "naar", "", ""));
		entries.addElement(new Entry("everybody", "iedereen", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("hardly", "nauwelijks", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("nobody", "niemand", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("something", "iets", "adv", "", "", "", "", "", ""));
				
		//prepositions
		entries.addElement(new Entry("at", "op", "prep", "", "", "", "", "", ""));
		
		//even nederlands
		entries.addElement(new Entry("verkennen", "verken", "verb", "", "", "", "", "", ""));
		entries.addElement(new Entry("stok", "stok", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("knuppel", "knuppel", "noun", "de", "neutral", "", "", "", ""));
		entries.addElement(new Entry("omgeving", "omgeving", "noun", "de", "place", "", "", "", ""));
		
		entries.addElement(new Entry("also", "ook", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("again", "weer", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("again", "nog een keer", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("still", "nog steeds", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("not", "niet", "adv", "", "", "", "", "", ""));
		entries.addElement(new Entry("nomore", "niet meer", "adv", "", "", "", "", "", ""));
	}
	
//	public static void main(String[] args){
//		Lexicon l = new Lexicon();
//		Iterator<Entry> it = (Iterator<Entry>)l.entries.iterator();
//		
//		OntModel model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
//		
//		final String NS_NARRATOR = "http://www.owl-ontologies.com/Narrator.owl#";
//		final String NS_OWL = "http://www.w3.org/2002/07/owl#";
//		
//		
////		model.setNsPrefix("narrator", NS_NARRATOR);
//		model.setNsPrefix("owl", NS_OWL);
//		model.setNsPrefix("narrator", NS_NARRATOR);
//		
//		OntResource lexiconentry = model.createOntResource(NS_NARRATOR+"lexicon_entry");
//
//		lexiconentry.addRDFType(com.hp.hpl.jena.rdf.model.ResourceFactory.createResource(NS_OWL+"Thing"));
//		
//		com.hp.hpl.jena.rdf.model.Property pconcept = model.createProperty(NS_NARRATOR, "concept");
//		com.hp.hpl.jena.rdf.model.Property proot = model.createProperty(NS_NARRATOR, "root");
//		com.hp.hpl.jena.rdf.model.Property ppos = model.createProperty(NS_NARRATOR, "PoS");
//		com.hp.hpl.jena.rdf.model.Property pdet = model.createProperty(NS_NARRATOR, "det");
//		com.hp.hpl.jena.rdf.model.Property pgen = model.createProperty(NS_NARRATOR, "gen");
//		com.hp.hpl.jena.rdf.model.Property pmorph = model.createProperty(NS_NARRATOR, "morph");
//		com.hp.hpl.jena.rdf.model.Property pprep = model.createProperty(NS_NARRATOR, "prep");
//		com.hp.hpl.jena.rdf.model.Property psvp = model.createProperty(NS_NARRATOR, "svp");
//		com.hp.hpl.jena.rdf.model.Property pdeplabel = model.createProperty(NS_NARRATOR, "deplabel");
//		
////      concept  root   pos   det   gen     morph           prep   svp   deplabel
//		while(it.hasNext()){
//			Entry e = it.next();
//			OntResource or = model.createOntResource("element_" + e.getConcept());
//
//			or.setRDFType(lexiconentry);
//
//			if(!e.getConcept().equals(""))
//				or.addProperty(pconcept, e.getConcept());
//			if(!e.getRoot().equals(""))
//				or.addProperty(proot, e.getRoot());
//			if(!e.getPos().equals(""))
//				or.addProperty(ppos, e.getPos());
//			if(!e.getDet().equals(""))
//				or.addProperty(pdet, e.getDet());
//			if(!e.getGen().equals(""))
//				or.addProperty(pgen, e.getGen());
//			if(!e.getMorph().equals(""))
//				or.addProperty(pmorph, e.getMorph());
//			if(!e.getPrep().equals(""))
//				or.addProperty(pprep, e.getPrep());
//			if(!e.getSvp().equals(""))
//				or.addProperty(psvp, e.getSvp());
//			if(!e.getDeplabel().equals(""))
//				or.addProperty(pdeplabel, e.getDeplabel());
//			
//		}
//		
//		NamedGraphSet graphset = new NamedGraphSetImpl();
//		graphset.read("Narrator.owl", "RDF/XML");
//		graphset.addGraph(new NamedGraphImpl("#defaultGraph", model.getGraph()));
//		graphset.write(System.out, "TRIG", null);
//		
//	}
	
//	public static void main(String[] args){
//		Lexicon l = new Lexicon();
//		Iterator<Entry> it = (Iterator<Entry>)l.entries.iterator();
//		
//		TreeSet<String> tset = new TreeSet<String>();
////      concept  root   pos   det   gen     morph           prep   svp   deplabel
//		while(it.hasNext()){
//			Entry e = it.next();
//			
//			
//			
//			if(!e.getMorph().equals(""))
//				tset.add(e.getMorph());
//			
//			
//			
//		}
//		
//		Iterator<String> itSet = tset.iterator();
//		while(itSet.hasNext()){
//			System.out.println("- " + itSet.next());
//		}
//		
//	}
	
	public static void main(String[] args){
		Lexicon l = new Lexicon("PiratesIvoLexicon.trig");
		
		Iterator<Entry> it = (Iterator<Entry>)l.entries.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
}

