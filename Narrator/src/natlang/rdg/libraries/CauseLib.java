package natlang.rdg.libraries;

import natlang.rdg.model.*;

import java.util.*;


/** CauseLib implements the causal part of the cue word taxonomy. The subcategories
 *	of the Causal relation are contained in static final Lists. A suitable cue word
 *	is found for a relation using its category 
 *	@author Feikje Hielkema
 *	@version 1.0
 **/

public class CauseLib extends CueWordLib implements LibraryConstants
{
	//primitives that distinguish subclasses of causal relations
	public static final String FIRST = "first";	//nucleus mentioned first, before satellite
	public static final String LAST = "last";	//other way around
	public static final String VOLUNTARY = "vol";	//voluntary cause (e.g. a plan)
	public static final String INVOLUNTARY = "invol"; //involuntary cause (e.g. an attitude)
	public static final String SUCHTHAT = "such";

	//lists of words in subclasses
	private List CAUSELISTVG;
	//private List VOLUNTARYFIRSTLISTVG;
	private List VOLUNTARYLASTLISTVG;
	private List INVOLUNTARYFIRSTLISTVG;
	private List INVOLUNTARYLASTLISTVG;
	private List SUCHTHATLISTVG;
	
	//private List CAUSELISTBEP;
	//private List VOLUNTARYFIRSTLISTBEP;
	private List VOLUNTARYLASTLISTBEP;
	//private List INVOLUNTARYFIRSTLISTBEP;
	private List INVOLUNTARYLASTLISTBEP;
	//private List SUCHTHATLISTBEP;
		
	/** fills the lists with the cue words */
	public CauseLib()
	{		
		CAUSELISTVG = new ArrayList();
		//VOLUNTARYFIRSTLISTVG = new ArrayList();
		VOLUNTARYLASTLISTVG = new ArrayList();
		INVOLUNTARYFIRSTLISTVG = new ArrayList();
		INVOLUNTARYLASTLISTVG = new ArrayList();
		SUCHTHATLISTVG = new ArrayList();
		
		//CAUSELISTBEP = new ArrayList();
		//VOLUNTARYFIRSTLISTBEP = new ArrayList();
		VOLUNTARYLASTLISTBEP = new ArrayList();
		//INVOLUNTARYFIRSTLISTBEP = new ArrayList();
		INVOLUNTARYLASTLISTBEP = new ArrayList();
		//SUCHTHATLISTBEP = new ArrayList();
		
		CAUSELISTVG.add(createNode("omdat", COMP, CMP));
		//CAUSELISTVG.add(createNode("en", VG, CRD));
		
		//VOLUNTARYFIRSTLISTVG.add(createNode("want", VG, CRD));
		
		VOLUNTARYLASTLISTBEP.add(createNode("daarom", ADV, MOD));
		VOLUNTARYLASTLISTBEP.add(createNode("dus", ADV, MOD));
		
		VOLUNTARYLASTLISTVG.add(createNode("dus", VG, CRD));

		INVOLUNTARYLASTLISTVG.add(createNode("zodat", COMP, CMP));
		INVOLUNTARYFIRSTLISTVG.add(createNode("doordat", COMP, CMP));
				
		//INVOLUNTARYLASTLISTVG.add(createNode("zodat", COMP, CMP));
		
		INVOLUNTARYLASTLISTBEP.add(createNode("daardoor", ADV, MOD));
		
		SUCHTHATLISTVG.add(createNode("dat", COMP, CMP));	
	}
		
	/** Returns the cue words suitable to the given relation category
	 *	@param cat the category
	 *	@param pos the part of speech tag
	 *  @return iterator with options
	 */
	public Iterator getOptions(String cat, String pos)
	{
		result = new ArrayList();
		
		if (cat == null)
			return null;
			
		if (!cat.startsWith(CAUSE))
			return null;
		
		if (cat.indexOf(SUCHTHAT) > 0)
		{
			if (pos.equals("vg"))
				addList(SUCHTHATLISTVG);
			//else
				//addList(SUCHTHATLISTBEP);
		}
		
		if (cat.indexOf(INVOLUNTARY) > 0)
		{
			if (cat.indexOf(LAST) > 0)
			{
				if (pos.equals("vg"))
					addList(INVOLUNTARYLASTLISTVG);
				else
					addList(INVOLUNTARYLASTLISTBEP);
			}
			else if (cat.indexOf(FIRST) > 0)
			{
				if (pos.equals("vg"))
					addList(INVOLUNTARYFIRSTLISTVG);
				//else
				//	addList(INVOLUNTARYFIRSTLISTBEP);
			}
			else
			{
				if (pos.equals("vg"))
				{
					addList(INVOLUNTARYLASTLISTVG);
					addList(INVOLUNTARYFIRSTLISTVG);					
				}
				else
				{
					addList(INVOLUNTARYLASTLISTBEP);
					//addList(INVOLUNTARYFIRSTLISTBEP);
				}
			}
		}
		else if (cat.indexOf(VOLUNTARY) > 0)
		{				
			if (cat.indexOf(LAST) > 0)
			{
				if (pos.equals("vg"))
					addList(VOLUNTARYLASTLISTVG);
				else
				//if (pos.equals("bep"))
					addList(VOLUNTARYLASTLISTBEP);
			}
			else if (cat.indexOf(FIRST) > 0)
			{
				//if (pos.equals("vg"))
				//	addList(VOLUNTARYFIRSTLISTVG);
				//else
				//	addList(VOLUNTARYFIRSTLISTBEP);
			}
			else
			{
				if (pos.equals("vg"))
				{
					addList(VOLUNTARYLASTLISTVG);
					//addList(VOLUNTARYFIRSTLISTVG);
				}
				else
				{
					addList(VOLUNTARYLASTLISTBEP);
					//addList(VOLUNTARYFIRSTLISTBEP);
				}
			}
		}
		else
		{
			if (pos.equals("vg"))
			{
				addList(VOLUNTARYLASTLISTVG);
				addList(INVOLUNTARYLASTLISTVG);
				//addList(VOLUNTARYFIRSTLISTVG);
				addList(INVOLUNTARYFIRSTLISTVG);
			}
			else
			{
				addList(VOLUNTARYLASTLISTBEP);
				addList(INVOLUNTARYLASTLISTBEP);
				//addList(VOLUNTARYFIRSTLISTBEP);
				//addList(INVOLUNTARYFIRSTLISTBEP);
			}
		}
		if (pos.equals("vg"))
			addList(CAUSELISTVG);
		//else
		//	addList(CAUSELISTBEP);
		
		return result.iterator();
	}
	
	/** Checks whether this cue word should be added to the nucleus
	 */
	public boolean addToNucleus(RSDepTreeNode cueWord) 
	{
		if (cueWord != null)
			if ((cueWord.getData().get(RSTreeNodeData.ROOT).equals("zodat"))
					|| (cueWord.getData().get(RSTreeNodeData.ROOT).equals("dat")))
				return true;
			else
				return super.addToNucleus(cueWord);
		else
			return super.addToNucleus(cueWord);
	}
	
	/** Checks whether this cue word should be added to the satellite
	 */
	public boolean addToSatellite(RSDepTreeNode cueWord) 
	{
		if (cueWord != null)
			if ((cueWord.getData().get(RSTreeNodeData.ROOT).equals("zodat"))
					|| (cueWord.getData().get(RSTreeNodeData.ROOT).equals("dat")))
				return false;
			else
				return super.addToNucleus(cueWord);
		else
			return super.addToNucleus(cueWord);
	}
}