package natlang.rdg.lexicalchooser;

/**
 * Entry in the lexicon, containing the concept, root, part of speech, determiner,
 * gender, morphology, preposition, svp and dependency label
 * @author Nanda Slabbers
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
	
	/**
	 * Returns the root 
	 */	
	public String getRoot()
	{
		return root;
	}
	
	/**
	 * Returns the part of speech 
	 */	
	public String getPos()
	{
		return pos;
	}
	
	/**
	 * Returns the determiner 
	 */	
	public String getDet()
	{
		return det;
	}
	
	/**
	 * Returns the gender 
	 */	
	public String getGen()
	{
		return gen;
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
	
	/**
	 * Returns the verb particle 
	 */	
	public String getSvp()
	{
		return svp;
	}
	
	/**
	 * Returns the dependency label 
	 */	
	public String getDeplabel()
	{
		return deplabel;
	}
	
	public boolean isMassNoun(){
		return isMassNoun;
	}
	
	public String toString()
	{
		return "concept: " + concept + "\troot: " + root + "\tpos: " + pos + "\tprep: " + prep;
	}
}

