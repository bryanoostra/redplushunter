package natlang.rdg.libraries;

import natlang.rdg.model.*;

import java.util.*;


/** 
 * Library class for relative clauses.
 * The cue word (actually relative pronoun) is initialized to 'die' and when 
 * generating the relative clause this may be changed to the correct pronoun
 * @author Nanda Slabbers
 **/

public class RelativeLib extends CueWordLib implements LibraryConstants
{	
	//lists of words in subclasses
	private List RELATIVELIST;
		
	/** fills the lists with the cue words */
	public RelativeLib()
	{
		RELATIVELIST = new ArrayList();
		RELATIVELIST.add(createNode("die", ADV, RHD));
	}
	
	public Iterator getOptions(String cat, String pos)
	{
		result = new ArrayList();
		addList(RELATIVELIST);
		return getResult();
	}
	
	public boolean addToNucleus(RSDepTreeNode cueWord) 
	{
		return true;
	}
}
