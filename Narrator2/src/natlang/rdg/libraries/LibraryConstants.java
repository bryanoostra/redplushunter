package natlang.rdg.libraries;

/** This interface lists the constants that can be used in the dependency tree. This 
 *	list is derived from the CGN-corpus and the Alpino Treebank. Not all of them
 *	are used at the moment. The variables are simply the capitalized Strings they represent
 */

public interface LibraryConstants
{
	//dependency relations
	public static final String TOP = "top";	
	public static final String HD = "hd";
	public static final String SU = "su";
	public static final String OBJ1 = "obj1";
	public static final String OBJ2 = "obj2";
	public static final String OBJ = "obj";
	public static final String SUP = "sup";
	public static final String POBJ1 = "pobj1";
	public static final String SE = "se";
	public static final String PREDC = "predc";
	public static final String VC = "vc";
	public static final String PC = "pc";
	public static final String ME = "me";
	public static final String LD = "ld";
	public static final String SVP = "svp";	
	//modifiers
	public static final String MOD = "mod";
	public static final String MODB = "modb";
	public static final String MODA = "moda";
	public static final String PREDM = "predm";
	public static final String ADVP = "advp";
	public static final String PRT = "prt";
	//complementers	
	public static final String CMP = "cmp";
	public static final String BODY = "body";
	//comparations
	public static final String OBCOMP = "obcomp";
	//prepositional domain
	public static final String HDF = "hdf";
	//nominal domain
	public static final String DET = "det";
	public static final String PART = "part";
	//not-local dependencies
	public static final String WHD = "whd";
	public static final String RHD = "rhd";
	public static final String MWP = "mwp";
	//nevenschikking
	public static final String CRD = "crd";
	public static final String CNJ = "cnj";
	public static final String LIJST = "lijst";		//java already knows a list...
	public static final String LP = "lp";
	//discourse structures
	public static final String DU = "du";
	public static final String DP = "dp";
	
	//syntactic categories
	//verbal domain
	public static final String SMAIN = "smain";
	public static final String SSUB = "ssub";
	public static final String SREL = "srel";
	public static final String SV1 = "sv1";
	public static final String INF = "inf";
	public static final String PPART = "ppart";
	public static final String PPRES = "ppres";
	//complements of verbal domain
	public static final String CP = "cp";
	public static final String SVAN = "svan";
	public static final String TI = "ti";
	public static final String OTI = "oti";
	public static final String AHI = "ahi";
	public static final String UI = "ui";
	//comparations
	public static final String COMPP = "compp";
	//nominal
	public static final String NP = "np";
	public static final String DETP = "detp";
	//prepositional
	public static final String PP = "pp";
	//adjective 
	public static final String AP = "ap";
	//not-local dependencies
	public static final String REL = "rel";
	public static final String WHREl = "whrel";
	public static final String WHQ = "whq";
	public static final String WHSUB = "whsub";
	public static final String XP = "xp";
	public static final String MWU = "mwu";
	//nevenschikking
	public static final String CONJ = "conj";
	//discourse structures
	public static final String NUCL = "nucl";
	public static final String SAT = "sat";
	public static final String TAG = "tag";
	public static final String DLINK = "dlink";

	//pos-tags
	//nominal domain
	public static final String NOUN = "noun"; 
	
	public static final String NOM = "nom";
	public static final String NAME = "name";
	public static final String PRON = "pron";
	//verbal domain
	public static final String VERB = "verb";
	public static final String ADV = "adv";
	public static final String TEINF = "teinf";
	//prepositional domain & adjective domain
	public static final String PREP = "prep";
	public static final String ADJ = "adj";
	//nevenschikkende voegwoorden
	public static final String VG = "vg";
	//onderschikkende voegwoorden
	public static final String COMP = "comp";
	//betrekkelijk voornaamwoorden
	public static final String BETR = "betr";
	//diverse
	public static final String NUM = "num";
	public static final String FIXED = "fixed"; //???
	
	//for referring expression generator
	public static final String POSS = "poss";
	

	//morphological tags
	public static final String SINGULAR = "sing";	//number
	public static final String PLURAL = "plural";
	public static final String FIRST = "1";		//person
	public static final String SECOND = "2";
	public static final String THIRD = "3";
	public static final String PRESENT = "pres";	//tense
	public static final String PAST = "past";
	public static final String PERFECT = "perf";
	public static final String PROGRESSIVE = "prog";

	//rhetorical relations
	public static final String CAUSE = "cause";
	public static final String PURPOSE = "purpose";
	public static final String CONTRAST = "contrast";
	public static final String TEMPORAL = "temp";
	public static final String ADDITIVE = "additive";
	public static final String RELATIVE = "relative";
	
	public static final String NUCLEUS = "nucleus";
	public static final String SATELLITE = "satellite";
	public static final String CNJNUCLEUS = "cnjnucleus";
	public static final String CNJSATELLITE = "cnjsatellite";
	
	//tags used for ellipsis
	public static final String IDENTICAL = "identical";
	public static final String BORROWED = "borrowed";
	public static final String RAISEDRIGHTNODE = "raised";
	public static final String ELLIPTED = "ellipted";
	public static final String FOURTH = "4";
	public static final String FIFTH = "5";
	
	//tags used in the Discourse Histories
	public static final String MALE = "male";
	public static final String FEMALE = "female";
	public static final String NEUTRAL = "neutral";
	public static final String PLACE = "place";
	
	//verb tense tags used in the lexicon
	public static final String FIRSTSINGULARPRESENT = "sg1";
	public static final String SECONDSINGULARPRESENT = "sg2";
	public static final String THIRDSINGULARPRESENT = "sg3";
	public static final String PLURALPRESENT = "plpres";
	public static final String SINGULARSIMPLEPAST = "sgpast";
	public static final String PLURALSIMPLEPAST = "plpast";
	public static final String PARTICIPLE = "participle";
	
	public static final String ADJINFLECT = "adjinflect";
			
}
