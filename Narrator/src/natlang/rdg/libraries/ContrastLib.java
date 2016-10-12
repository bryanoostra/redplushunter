package natlang.rdg.libraries;

import java.util.*;

/** ContrastLib implements the contrast part of the cue word taxonomy. The subcategories
 *	of the Contrast relation are contained in static final Lists. A suitable cue word
 *	is found for a relation using its category 
 *	@author Feikje Hielkema
 *	@version 1.0
 **/
 
public class ContrastLib extends CueWordLib implements LibraryConstants
{
	//primitive that distinguishes a subclass
	public static final String CAUSAL = "causal";

	//lists of words in subclasses
	private List CONTRASTLISTVG;
	private List CAUSALLISTBEP;
	
	private List CAUSALLISTVG;
	private List CONTRASTLISTBEP;
	
	/** fills the lists with the cue words */
	public ContrastLib()
	{		
		CONTRASTLISTVG = new ArrayList();
		CAUSALLISTBEP = new ArrayList();
		
		CAUSALLISTVG = new ArrayList();
		CONTRASTLISTBEP = new ArrayList();
		
		CONTRASTLISTVG.add(createNode("maar", VG, CRD));
		CONTRASTLISTBEP.add(createNode("echter", ADV, MOD));
		
		CAUSALLISTBEP.add(createNode("toch", ADV, MOD));
		CAUSALLISTVG.add(createNode("hoewel", COMP, CMP));
	}
	
	/**	Gives the cue words that could be used to realise the given relation
	 *	@param cat the category
	 *  @param pos the part of speech tag
	 *	@return Iterator with options
	 */
	public Iterator getOptions(String cat, String pos)
	{
		result = new ArrayList();
		
		if (cat == null)
			return null;
			
		if (!cat.startsWith(CONTRAST))
			return null;
		
		if (cat.indexOf(CAUSAL) >= 0)
		{
			if (pos.equals("vg"))
				addList(CAUSALLISTVG);
			else
				addList(CAUSALLISTBEP);
		}
		
		if (pos.equals("vg"))
			addList(CONTRASTLISTVG);
		else
			addList(CONTRASTLISTBEP);
		
		return getResult();
	}
}