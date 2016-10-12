package natlang.rdg.libraries;

import natlang.rdg.model.*;

import java.util.*;


/** PurposeLib implements the purpose part of the cue word taxonomy. The subcategories
 *	of the Purpose relation are contained in static final Lists. A suitable cue word
 *	is found for a relation using its category. The cue words suitable for Voluntary Cause
 *	are also suitable for Purpose, so Purpose extends CauseLib
 *	@author Feikje Hielkema
 *	@version 1.0
 **/
 
public class PurposeLib extends CauseLib implements LibraryConstants
{
	//list of words in purpose relation
	private List PURPOSELIST;
	
	/** fills the list with the cue word */
	public PurposeLib()
	{		
		super();
		
		PURPOSELIST = new ArrayList();
		
		PURPOSELIST.add(createNode("om", COMP, CMP));
	}
	
	/** Returns the cue words suitable to the given relation category
	 *	@param cat the category
	 *	@param pos the part of speech tag
	 *  @return iterator with options
	 */
	public Iterator getOptions(String cat, String pos)
	{
		if (cat == null)
			return null;
		
		if (!cat.startsWith(PURPOSE))
			return null;
		
		StringBuffer c = new StringBuffer(cat);
		c.replace(0, 7, "cause-voluntary");
		
		List result = new ArrayList();
		for (int i = 0; i < PURPOSELIST.size(); i++)
			result.add(PURPOSELIST.get(i));
			
		try
		{
			Iterator it = super.getOptions(c.toString(), pos);
			while (it.hasNext())
				result.add((RSDepTreeNode) it.next());
		}
		catch (Exception e)
		{
			System.out.println("exceptie");
		}
			
		return result.iterator();
	}
	
	/** Checks whether the cue word has to be added to the nucleus. "om" is a special
	 *	case that does not follow the general rule implemented in CueWordLib
	 */
	public boolean addToNucleus(RSDepTreeNode cueWord)
	{
		if (cueWord.getData().get(RSTreeNodeData.ROOT) == "om")
			return false;
		return super.addToNucleus(cueWord);
	}
	
	/** Checks whether the cue word has to be added to the satellite. "om" is a special
	 *	case that does not follow the general rule implemented in CueWordLib
	 */
	public boolean addToSatellite(RSDepTreeNode cueWord)
	{
		if (cueWord.getData().get(RSTreeNodeData.ROOT) == "om")
			return true;
		return super.addToSatellite(cueWord);
	}
}