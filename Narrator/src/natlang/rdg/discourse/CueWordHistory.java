package natlang.rdg.discourse;

import natlang.rdg.libraries.*;
import natlang.rdg.model.*;

/** CueWordHistory keeps track of the last five cue words that were used, to prevent
 *	monotone text
 */
public class CueWordHistory implements LibraryConstants
{
	RSDepTreeNode[] recent;	//list of recently used cue words
	
	/** Creates a cuewordhistory by creating an empty list */
	public CueWordHistory()
	{
		recent = new RSDepTreeNode[3];
	}
	
	/** Checks whether a cue word is recent */
	public boolean isRecent(Object o)
	{
		try
		{
			RSDepTreeNode cueWord = (RSDepTreeNode) o;
			for (int i = 0; i < recent.length; i++)
			{
				if (recent[i] == null)
					return false;
					
				if (recent[i].getData().equals(cueWord.getData()))
					return true;
			}
			return false;
		}
		catch (ClassCastException e)
		{
			return false;
		}
	}
	
	/** Puts the given cue word on the first position of recent, and moves the other
	 *	elements to the back of the list
	 */
	public void setRecent(Object o)
	{
		try
		{
			RSDepTreeNode cueWord = (RSDepTreeNode) o;
			for (int i = (recent.length - 1); i > 0; i--)
				recent[i] = recent[i - 1];
			recent[0] = cueWord;
		}
		catch (ClassCastException e)
		{}
	}
	
	/** Resets recent */
	public void reset()
	{
		recent = new RSDepTreeNode[3];
	}
	
	public void printRecent()
	{
		for (int i=0; i<3; i++)
		{
			RSDepTreeNode tmp = recent[i];
			if (tmp != null)
				System.out.println(tmp.getData().get("root"));	
		}
	}
}