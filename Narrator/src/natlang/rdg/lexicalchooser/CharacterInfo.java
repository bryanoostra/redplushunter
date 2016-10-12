package natlang.rdg.lexicalchooser;

import java.util.Vector;
import java.util.logging.Logger;

import natlang.debug.LogFactory;
import natlang.rdg.documentplanner.InitialDocPlanBuilder;
import natlang.rdg.ontmodels.OntModels;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * Modelled CharacterInfo
 * @author Nanda Slabbers
 */
public abstract class CharacterInfo 
{
	private Vector chars;
	
	private OntModel m;
	
	private static CharacterInfo characterInfoObject;
	
	private Logger logger;
	
	public static final String NARRATOR_KNOWLEDGE = "http://www.owl-ontologies.com/NarratorKnowledge.owl#";
	public static final String LEXICAL_OBJECT = NARRATOR_KNOWLEDGE + "LexicalObject";
	public static final String NK_NAME = NARRATOR_KNOWLEDGE + "name";
	public static final String NK_GEN = NARRATOR_KNOWLEDGE + "gender";
	
	public static final String SWC 			= "http://www.owl-ontologies.com/StoryWorldCore.owl#";
	public static final String SWC_ROLE		= SWC + "Role";
	public static final String SWC_OBJECT	= SWC + "Object";
	
	/**
	 * Creates the character info
	 *
	 */
	public CharacterInfo() 
	{
		logger = LogFactory.getLogger(this);
		
		chars = new Vector();
		m = OntModels.ontModelCombined;
		if(m == null){
			old_addCharacters();
			return;
		}
		try{
			addCharacters();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		addObjects();
	}
	
	public static CharacterInfo getCharacterInfoObject(){
		if(characterInfoObject == null)
			characterInfoObject = new CharacterInfo(){ };
		
		return characterInfoObject;
	}
	
	/**
	 * Adds a character to this character information object.
	 * @param ent the entity with which the character is represented
	 * @param con the concept of the character (the type or class)
	 * @param gender the gender of the character (male, female or neutral)
	 * @param nom the name of the character (can be null)
	 */
	public void addCharacter(String ent, String con, String gender, String nom){
		if(nom != null)
			chars.addElement(new CharacterModel(ent, con, gender, nom));
		else
			chars.addElement(new CharacterModel(ent, con, gender));
	}
	
	private String getStringContent(OntResource or, String property){
		StmtIterator sit = m.listStatements(or, m.createProperty(property), (RDFNode)null);
		if(sit.hasNext()){
			return ((Literal)sit.nextStatement().getObject()).getString();
		}
		return "";
	}
	
	private String getResourceContent(OntResource or, String property){
		StmtIterator sit = m.listStatements(or, m.createProperty(property), (RDFNode)null);
		if(sit.hasNext()){
			return ((Resource)sit.nextStatement().getObject()).getLocalName();
		}
		return "";
	}
	
	public void addCharacters()	throws Exception {
		if(chars.size() > 0) throw new Exception(); 
		ResIterator ri = m.listSubjects();
		logger.info("Add characters");
		while(ri.hasNext()){
			Resource r = ri.nextResource();
			OntResource or = m.getOntResource(r);
			if(or.hasRDFType(LEXICAL_OBJECT)){
			logger.info("lexical object: " + or.getLocalName());
				String 	sConcept 	= or.getLocalName(), 
						sName   	= "",
						sGen     	= "";

				sName = getStringContent(or, NK_NAME);
				sGen = getResourceContent(or, NK_GEN);
				
				ExtendedIterator ei = m.getOntResource(r.toString()).listRDFTypes(true);
				while(ei.hasNext()){
					RDFNode obj = (RDFNode)ei.next();
					
					// if a superclass of this class is SWC_ROLE, than add that class 
					// as the concept of the to be added character
					if(((OntClass)obj.as(OntClass.class)).hasSuperClass(ResourceFactory.createResource(SWC_ROLE))){
						Resource supertype = (Resource)obj;
												
						logger.info("add character: " + sConcept + ", " + supertype.getLocalName() + ", " + sGen + ", " + sName);
						
						addCharacter(sConcept, supertype.getLocalName(), sGen, "_"+sConcept+"_");
						
					}
				}
			}
		}
	}
	
	public void addObjects(){
		ResIterator ri = m.listSubjects();
		logger.info("Add Objects");
		while(ri.hasNext()){
			Resource r = ri.nextResource();
			OntResource or = m.getOntResource(r);
			if(or.hasRDFType(SWC_OBJECT) && !or.hasRDFType(LEXICAL_OBJECT)){
				logger.info(or.toString());
				String 	sConcept 	= or.getLocalName(), 
						sGen     	= "";

				sGen = getResourceContent(or, NK_GEN);
				
				if(sGen.equals(""))
					sGen = "neutral";
				
				Resource supertype = m.getOntResource(r.toString()).getRDFType(true);
				logger.info("add object: " + sConcept + ", " + supertype.getLocalName() + ", " + sGen);
				
				addCharacter(sConcept, supertype.getLocalName(), sGen, null);
			}
		}
//		System.exit(0);
	}
	
	/**
	 * Fills the character info with information about possible characters
	 *
	 */
	public void old_addCharacters()
	{
		chars.addElement(new CharacterModel("leChuck", "Captain", "male", "le_Chuck"));
		chars.addElement(new CharacterModel("oRumBottle_1", "RumBottle", "neutral"));
		chars.addElement(new CharacterModel("oHold_1", "Hold", "neutral"));
		chars.addElement(new CharacterModel("oHatch_1", "Hatch", "neutral"));
		chars.addElement(new CharacterModel("oLadder_up", "Ladder", "neutral"));
		chars.addElement(new CharacterModel("oPond_1", "Pond", "neutral"));
		chars.addElement(new CharacterModel("oCrate_1", "Crate", "neutral"));
		chars.addElement(new CharacterModel("oDeck_1", "Deck", "neutral"));
		chars.addElement(new CharacterModel("oTreasureIsland_1", "TreasureIsland", "neutral"));
		chars.addElement(new CharacterModel("oShip_1", "Ship", "place"));
		chars.addElement(new CharacterModel("oWater_1", "Water", "neutral"));
		chars.addElement(new CharacterModel("oWaterSupply_1", "WaterSupply", "neutral"));
		chars.addElement(new CharacterModel("money_1", "money", "neutral"));
		chars.addElement(new CharacterModel("treasure1", "Treasure", "neutral"));
		
		chars.addElement(new CharacterModel("anne_bonney", "Pirate", "female", "Anne Bonney"));
		chars.addElement(new CharacterModel("rapier_roy", "Lord", "male", "Lord Bradford"));
		chars.addElement(new CharacterModel("annesBelt", "belt", "neutral"));
		chars.addElement(new CharacterModel("annesRapier", "rapier", "neutral"));
		
		CharacterModel linda = new CharacterModel("linda", "girl", "female", "Linda");
		linda.addStatprop(new Property("size", "small"));
		chars.addElement(linda);
		
		CharacterModel otto = new CharacterModel("otto", "man", "male", "Otto");
		otto.addStatprop(new Property("age", "old"));
		chars.addElement(otto);
		
		CharacterModel icecreamtruck = new CharacterModel("oIceCreamTruck", "IceCreamTruck", "neutral");
		icecreamtruck.addStatprop(new Property("appearance", "beautiful"));
		chars.addElement(icecreamtruck);
		
		CharacterModel ice = new CharacterModel("vanilla_ice_1", "vanilla_ice", "neutral");
		ice.addStatprop(new Property("size", "big"));
		chars.addElement(ice);
		
//		chars.addElement(new CharacterModel("otto", "man", "male", "Otto"));
//		chars.addElement(new CharacterModel("linda", "girl", "female", "Linda"));
//		chars.addElement(new CharacterModel("vanilla_ice_1", "vanilla_ice", "neutral"));
//		chars.addElement(new CharacterModel("oIceCreamTruck", "IceCreamTruck", "neutral"));
		chars.addElement(new CharacterModel("money_1", "money", "neutral"));
		chars.addElement(new CharacterModel("park", "park", "neutral"));
		
		
		chars.addElement(new CharacterModel("humanoid.1", "dwarf", "male", "plop"));
		chars.addElement(new CharacterModel("fruitorvegetable.4", "apple", "neutral"));
		chars.addElement(new CharacterModel("geographicarea.5", "house", "neutral"));
		chars.addElement(new CharacterModel("dwarf01", "dwarf", "male", "plop"));
		chars.addElement(new CharacterModel("apple01", "apple", "neutral"));
		chars.addElement(new CharacterModel("knight01", "knight", "male")); //, "brutus"));
		chars.addElement(new CharacterModel("castle01", "castle", "neutral"));
		chars.addElement(new CharacterModel("gate01", "gate", "neutral"));
		chars.addElement(new CharacterModel("bedroom01", "bedroom", "neutral"));		
		chars.addElement(new CharacterModel("tree01", "tree", "neutral"));		
		chars.addElement(new CharacterModel("horse01", "horse", "neutral"));		
		chars.addElement(new CharacterModel("hand01", "hand", "neutral"));	
		chars.addElement(new CharacterModel("king01", "king", "male"));
		chars.addElement(new CharacterModel("husband01", "husband", "male"));
		chars.addElement(new CharacterModel("cottage01", "cottage", "neutral"));
		chars.addElement(new CharacterModel("behaviour01", "behaviour", "neutral"));
		chars.addElement(new CharacterModel("girl01", "girl", "female"));
		chars.addElement(new CharacterModel("swamp01", "swamp", "neutral"));
		chars.addElement(new CharacterModel("villain01", "villain", "male", "brutus"));
		chars.addElement(new CharacterModel("desert01", "desert", "neutral"));
		chars.addElement(new CharacterModel("woman01", "woman", "female"));
		chars.addElement(new CharacterModel("sound01", "sound", "neutral"));
		chars.addElement(new CharacterModel("edge01", "edge", "neutral"));
		chars.addElement(new CharacterModel("forest02", "forest", "neutral"));
		
		CharacterModel co01 = new CharacterModel("country01", "country", "neutral");
		co01.addStatprop(new Property("distance", "faraway"));
		chars.addElement(co01);
		
		CharacterModel fo01 = new CharacterModel("forest01", "forest", "neutral");
		fo01.addStatprop(new Property("size", "small"));
		chars.addElement(fo01);
				
		CharacterModel sw01 = new CharacterModel("sword01", "sword", "neutral");
		sw01.addStatprop(new Property("size", "big"));
		chars.addElement(sw01);
		
		CharacterModel br1 = new CharacterModel("bridge01", "bridge", "neutral");
		br1.addStatprop(new Property("age", "old"));
		br1.addStatprop(new Property("strength", "weak"));
		chars.addElement(br1);
		
		CharacterModel pr1 = new CharacterModel("princess01", "princess", "female", "amalia");
		pr1.addStatprop(new Property("appearance", "beautiful"));
		chars.addElement(pr1);
				
		CharacterModel pr = new CharacterModel("prince01", "prince", "male");
		pr.addStatprop(new Property("age", "young"));
		chars.addElement(pr);
		
		/*CharacterModel pr2 = new CharacterModel("princess02", "princess", "female");
		pr2.addStatprop(new Property("size", "big"));
		chars.addElement(pr2);		
		
		CharacterModel pr3 = new CharacterModel("princess03", "princess", "female");
		pr3.addStatprop(new Property("age", "old"));
		chars.addElement(pr3);		
		
		CharacterModel pr4 = new CharacterModel("princess04", "princess", "female");
		pr4.addStatprop(new Property("appearance", "ugly"));
		chars.addElement(pr4);*/	
	}
	
	/**
	 * Returns the character with entity name ent
	 * @param ent
	 */
	public CharacterModel getChar(String ent)
	{
		for (int i=0; i<chars.size(); i++)
		{
			CharacterModel tmp = (CharacterModel) chars.elementAt(i);
			
			if (tmp.getEntity().equals(ent))
				return tmp;
		}
		return null;
	}
	
	/**
	 * Returns all characters
	 */
	public Vector getChars()
	{
		return chars;
	}
	
	/**
	 * Checks whether the entity is an entity
	 * @param entity
	 */
	public boolean isEntity(String entity)
	{
		for (int i=0; i<chars.size(); i++)
		{
			CharacterModel ch = (CharacterModel) chars.elementAt(i);
			if (ch.getEntity().equals(entity))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the gender of the entity
	 * @param entity
	 */
	public String getGender(String entity)
	{
		for (int i=0; i<chars.size(); i++)
		{
			CharacterModel ch = (CharacterModel) chars.elementAt(i);
			if (ch.getEntity().equals(entity))
				return ch.getGender();
		}
		return "";
	}
}
