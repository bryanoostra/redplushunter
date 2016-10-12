package narrator.lexicon;

import java.util.HashMap;
import java.util.Map;

/**
 * Entry in the lexicon, containing the concept, root, part of speech, determiner,
 * gender, morphology, preposition, svp and dependency label
 * @author Nanda Slabbers
 * @author Marissa Hoek
 */
public class Entry 
{	
	private String concept;			
	private String root;			//stam
	private String pos;				//part of speech (verb, noun etc)
	private String det;				//for nouns: determiner
	private String gen;				//for nouns: +/- gender (female, male, neutral, place, time)
	private String morph;			//morphological information
	private String prep;			//for verbs: preposition needed in target (gaan-naar)
	private String svp;				//for verbs: ... (op-pakken)
	private String deplabel;		//for verbs with target: dependency label
	private boolean isMassNoun = false; // for mass nouns. If the PoS is noun, then this could be true
										// in case of water for example
	private String plural;			//for nouns: plural form
	private String atprep;			//for nouns: that specify a location: the preposition used when a character is at that location.
	private String auxverb;			//for verbs: the auxiliary verb used for the perfect form.
	private String adjInflect = "";		//for adjectives: the inflected (root+e) form
	
	//The grammatical tenses for verbs
	private Map<String,String> tenses;

	
	/**
	 * Creates an emtpy entry
	 *
	 */
	public Entry()
	{
		concept = "";
		root = "";
		pos = "";
		det = "";
		morph = "";
		prep = "";
		svp = "";
		deplabel = "";
		gen="";
		plural = "";
		atprep = "";
		auxverb = "";
		adjInflect = "";
		
		tenses = new HashMap<String,String>();
	}
	
	/**
	 * Creates an entry
	 * @param cn the concept
	 * @param rt the root
	 * @param po the part of speech
	 * @param dt the determiner
	 * @param ge the gender
	 * @param mo the morphology tag
	 * @param pr the preposition
	 * @param vp the verb particle
	 * @param dl the dependency label
	 */
	public Entry(String cn, String rt, String po, String dt, String ge, String mo, String pr, String vp, String dl)
	{
		concept = cn;
		root = rt;
		pos = po;
		det = dt;
		gen = ge;
		morph = mo;
		prep = pr;
		svp = vp;
		deplabel = dl;
	}
	
	/**
	 * Creates an entry
	 * @param cn the concept
	 * @param rt the root
	 * @param po the part of speech
	 * @param dt the determiner
	 * @param ge the gender
	 * @param mo the morphology tag
	 * @param pr the preposition
	 * @param vp the verb particle
	 * @param dl the dependency label
	 */
	public Entry(String cn, String rt, String po, String dt, String ge, String mo, String pr, String vp, String dl, boolean isMassNoun)
	{
		concept = cn;
		root = rt;
		pos = po;
		det = dt;
		gen = ge;
		morph = mo;
		prep = pr;
		svp = vp;
		deplabel = dl;
		this.isMassNoun = isMassNoun;
	}
	
	/**
	 * Returns the concept 
	 */	
	public String getConcept()
	{
		return concept;
	}
	
	public void setConcept(String concept){
		this.concept = concept;
	}
	
	/**
	 * Returns the root 
	 */	
	public String getRoot()
	{
		return root;
	}
	
	public void setRoot(String root){
		this.root = root;
	}
	
	/**
	 * Returns the part of speech 
	 */	
	public String getPos()
	{
		return pos;
	}
	
	public void setPos(String pos){
		this.pos = pos;
	}
	
	/**
	 * Returns the determiner 
	 */	
	public String getDet()
	{
		return det;
	}
	
	public void setDet(String det){
		this.det = det;
	}
	
	/**
	 * Returns the gender 
	 */	
	public String getGen()
	{
		return gen;
	}
	
	public void setGen(String gen){
		this.gen = gen;
	}
	
	/**
	 * Returns the morph tag 
	 */	
	public String getMorph()
	{
		return morph;
	}
	
	/**
	 * Returns the preposition 
	 */	
	public String getPrep()
	{
		return prep;
	}
	
	public void setPrep(String prep){
		this.prep = prep;
	}
	
	/**
	 * Returns the verb particle 
	 */	
	public String getSvp()
	{
		return svp;
	}
	
	public void setSvp(String svp){
		this.svp = svp;
	}
	
	/**
	 * Returns the dependency label 
	 */	
	public String getDeplabel()
	{
		return deplabel;
	}
	
	public void setDepLabel(String deplabel){
		this.deplabel = deplabel;
	}
	
	public boolean isMassNoun(){
		return isMassNoun;
	}
	
/*	public String toString()
	{
		return "concept: " + concept + "\troot: " + root + "\tpos: " + pos + "\tprep: " + prep;
	}*/

	public String toString(){
		String result = "";
		result+=concept+": ";
		result+= "("+pos+") "; 
		if (!det.equals("")) result+=det+" ";
		result+=root+" ";
		if (!gen.equals("")) result+="("+gen+") ";
		if (!deplabel.equals("")) result+="("+deplabel+") ";
		if (!prep.equals("")) result+= "with preposition: "+prep;
		return result;
	}

	public String getPlural() {
		return plural;
	}

	public void setPlural(String plural) {
		this.plural = plural;
	}
	
	public String getAtPreposition(){
		return atprep;
	}
	
	public void setAtPreposition(String atprep){
		this.atprep = atprep;
	}
	
	public Map<String,String> getTenses(){
		return tenses;
	}

	public String getAuxverb() {
		return auxverb;
	}

	public void setAuxverb(String auxverb) {
		this.auxverb = auxverb;
	}

	public String getInflectedAdjective(){
		return adjInflect;
	}
	
	public void setInflectedAdjective(String adjInflect) {
		this.adjInflect = adjInflect;		
	}
}