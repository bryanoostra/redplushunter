package natlang.rdg.libraries;


import natlang.rdg.model.*;

import java.util.*;
//import java.util.logging.Logger;

/**	This class is the base-class of all constituent libraries. All grammatical categories
 *	in the CGN are enumerated below (not all are implemented)
 */
public class ConstituentLib implements LibraryConstants
{
	//a list of rules, which is something all child classes must have
	protected final List rules = new ArrayList();
	//protected Logger logger;
	
	public ConstituentLib() {
		//logger = LogFactory.getLogger(this);
	}
	
	/**	This function returns the appropriate library for the given syntactic category
	 *	@throws Exception when an unknown category is encountered
	 */
	public ConstituentLib getAppropriateLib(String cat) throws Exception
	{
		ConstituentLib lib = null;
		
		if (cat.equals(SMAIN))
			lib = new SmainLib();
		else if (cat.equals(CONJ))
			lib = new ConjLib();
		else if (cat.equals(SSUB))
			lib = new SsubLib();
		else if (cat.equals(SREL))
			lib = new SrelLib();
/*		else if (cat.equals(SV1))
			lib = new Sv1Lib();		*/
		else if (cat.equals(INF))
			lib = new InfLib();
		else if (cat.equals(PPART))
			lib = new PpartLib();
/*		else if (cat.equals(PPRES))
			lib = new PpresLib();	*/
		else if (cat.equals(CP))
			lib = new CpLib();
/*		else if (cat.equals(SVAN))
			lib = new SvanLib(); */
		else if (cat.equals(OTI))
			lib = new OtiLib();
		else if (cat.equals(TI))
			lib = new TiLib();
/*		else if (cat.equals(AHI))
			lib = new AhiLib();
		else if (cat.equals(UI))
			lib = new UiLib();	
		else if (cat.equals(COMPP))
			lib = new ComppLib();	
*/		else if (cat.equals(NP))
			lib = new NpLib();	
//		else if (cat.equals(DETP))
//			lib = new DetpLib();
		else if (cat.equals(PP))
			lib = new PpLib();
		else if (cat.equals(AP))
			lib = new ApLib();
		else if (cat.equals(REL))			
			lib = new RelLib();
		else if (cat.equals("excl"))
			lib = new ExclLib();
/*		else if (cat.equals(WHREL))
			lib = new WhrelLib();
		else if (cat.equals(WHQ))
			lib = new WhqLib();
		else if (cat.equals(WHSUB))
			lib = new WhsubLib();
		else if (cat.equals(XP))
			lib = new XpLib();
		else if (cat.equals(MWU))
			lib = new MwuLib();	
		else if (cat.equals(NUCL))
			lib = new NuclLib();
		else if (cat.equals(SAT))
			lib = new SatLib();
		else if (cat.equals(TAG))
			lib = new TagLib();
		else if (cat.equals(DLINK))
			lib = new DlinkLib();	*/
		else
			throw new Exception(cat + " is an unknown category");
			
		return (ConstituentLib) lib;
	}
	
	/** Returns an iterator with all the rules that have the given child edges
	 *	@param children a list with all children
	 *	@return Iterator all applicable rules
	 */
	public Iterator getRules(List children) throws Exception
	{
		//logger.info(" *** LIST SIZE: " + children.size());
		List result = new ArrayList();
		for (int i = 0; i < rules.size(); i++)
		{
			Rule r = (Rule) rules.get(i);
			if (r.applies(children))
				result.add(r);
		}
		
		if (result.size() == 0)
		{
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < children.size(); i++)
			{
				MOrderedEdge child = (MOrderedEdge) children.get(i);
				sb.append(child.getLabel());
				sb.append(" ");
			}
			
			System.out.println(children.size());
			
			System.out.println("libraries.ConstituenLib > Error: There is no rule that has the children " + sb + "for category " + this.getClass().getName());
			
			//throw new Exception("There is no rule that has the children " + sb + "for category " + this.getClass().getName());
			
			// if there is no rule with all children: remove the modifiers and check
			// if there is a rule that applies to this new list of edges
			for (int i=0; i<children.size(); i++)
				if (((MOrderedEdge) children.get(i)).getLabel().equals(MOD))
					children.remove(i);
			for (int i=0; i < rules.size(); i++)
			{
				Rule r = (Rule) rules.get(i);
				if (r.applies(children))
					result.add(r);
			}
		}
			
		return result.iterator();
	}
}