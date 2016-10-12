package natlang.rdg.libraries;

import java.util.*;

/** AdditiveLib implements the causal part of the cue word taxonomy. The subcategories
 *	of the Additive relation are contained in static final Lists. A suitable cue word
 *	is found for a relation using its category 
 *	@author Feikje Hielkema
 *	@version 1.0
 *
 *	en...ook zijn moeilijk te verwezenlijken hier, maar lukken in de Elliptor waarschijnlijk
 *	beter. Is het lelijk om dat zo te doen?
 **/

public class AdditiveLib extends CueWordLib implements LibraryConstants
{
	//primitives that distinguish subclasses of causal relations
	public static final String MOREOVER = "moreover";

	//lists of words in subclasses
	private List ADDITIVELISTVG;
	private List MOREOVERLISTBEP;
	
	/** fills the lists with the cue words */
	public AdditiveLib()
	{
		ADDITIVELISTVG = new ArrayList();
		MOREOVERLISTBEP = new ArrayList();
		
		ADDITIVELISTVG.add(createNode("en", VG, CRD));				
		MOREOVERLISTBEP.add(createNode("bovendien", ADV, MOD));
	}
		
	public Iterator getOptions(String cat, String pos)
	{
		result = new ArrayList();
		
		if (cat.indexOf(MOREOVER) >= 0)
			if (pos.equals("bep"))
				addList(MOREOVERLISTBEP);
		
		if (pos.equals("vg"))
			addList(ADDITIVELISTVG);
		
		return getResult();
	}
}