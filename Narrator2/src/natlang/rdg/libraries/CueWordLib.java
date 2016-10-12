package natlang.rdg.libraries;

import natlang.rdg.model.*;

import java.util.*;


/**	This class is the parent class for all classes holding information on cue words
 */
public class CueWordLib implements LibraryConstants
{
	//list of appropriate cue words for the last request
	List result = new ArrayList();
	
	/** Returns an Iterator with the possible cue words for this category
	 */
	public Iterator getOptions(String cat, String pos) throws Exception
	{
		if (cat == null)
			return null;
		
		CueWordLib lib = null;
		if (cat.startsWith(CAUSE))
		{
			lib = new CauseLib();
		}
		else if (cat.startsWith(PURPOSE))
			lib = new PurposeLib();
		else if (cat.startsWith(CONTRAST))
			lib = new ContrastLib();
		else if (cat.startsWith(TEMPORAL))
			lib = new TemporalLib();
		else if (cat.startsWith(ADDITIVE))
			lib = new AdditiveLib();
		else if (cat.startsWith(RELATIVE))
			lib = new RelativeLib();
		else
			throw new Exception("That's not a relation!: " + cat);
				
		return lib.getOptions(cat, pos);
	}
	
	/*public Iterator getOptions(String cat) throws Exception
	{		
		if (cat == null)
			return null;
		
		CueWordLib lib = null;
		if (cat.startsWith(CAUSE))
			lib = new CauseLib();
		else if (cat.startsWith(PURPOSE))
			lib = new PurposeLib();
		else if (cat.startsWith(CONTRAST))
			lib = new ContrastLib();
		else if (cat.startsWith(TEMPORAL))
			lib = new TemporalLib();
		else if (cat.startsWith(ADDITIVE))
			lib = new AdditiveLib();
		else if (cat.startsWith(RELATIVE))
			lib = new RelativeLib();
		else
			throw new Exception("That's not a relation!: " + cat);
			
		return lib.getOptions(cat);
	}*/
	
	/**	Returns the appropriate cue word library for this category
	 */
	public CueWordLib getAppropriateLib(String cat) throws Exception
	{
		CueWordLib lib = null;
		if (cat.startsWith(CAUSE))
			lib = new CauseLib();
		else if (cat.startsWith(PURPOSE))
			lib = new PurposeLib();
		else if (cat.startsWith(CONTRAST))
			lib = new ContrastLib();
		else if (cat.startsWith(TEMPORAL))
			lib = new TemporalLib();
		else if (cat.startsWith(ADDITIVE))
			lib = new AdditiveLib();
		else if (cat.startsWith(RELATIVE))
			lib = new RelativeLib();
		else
			throw new Exception("That's not a relation!: " + cat);
			
			
		return (CueWordLib)lib;
	}
	
	/**	Creates a RSDepTreeNode with a root, pos and rel-tag
	 */	
	protected static RSDepTreeNode createNode(String root, String pos, String rel)
	{
		RSTreeNodeData data = new RSTreeNodeData(rel);
		data.set(RSTreeNodeData.ROOT, root);
		data.set(RSTreeNodeData.POS, pos);
		return new RSDepTreeNode(data);
	}
	
	/** Checks whether this cue word should be added to the nucleus
	 */
	public boolean addToNucleus(RSDepTreeNode cueWord) 
	{
		if (cueWord != null)
			if (cueWord.getData().get(REL).equals(MOD))
				return true;
		return false;
	}
	
	/** Checks whether this cue word should be added to the satellite
	 */
	public boolean addToSatellite(RSDepTreeNode cueWord) 
	{
		if (cueWord != null)
			if (cueWord.getData().get(REL).equals(CMP))
				return true;
		return false;
	}
	
	/** Adds a list to result */
	protected void addList(List l)
	{
		for (int i = 0; i < l.size(); i++)
			result.add(l.get(i));
	}
	
	/** Returns the result */
	protected Iterator getResult()
	{
		return result.iterator();
	}
}