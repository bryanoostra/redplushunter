package natlang.rdg.libraries;

import natlang.rdg.model.*;

import java.util.*;


/** TemporalLib implements the temporal part of the cue word taxonomy. The subcategories
 *	of the Temporal relation are contained in static final Lists. A suitable cue word
 *	is found for a relation using its category 
 *	@author Feikje Hielkema
 *	@version 1.0
 **/

public class TemporalLib extends CueWordLib implements LibraryConstants
{
	//primitives that distinguish subclasses of Temporal relations
	public static final String BEFORE = "before";
	public static final String AFTER = "after";
	public static final String GAP = "gap";
	public static final String SEQUENCE = "sequence";
	public static final String DURING = "during";
	public static final String ATLAST = "atlast";
	public static final String SOONAS = "soonas";
	public static final String SUDDENLY = "suddenly";
	public static final String FINALLY = "finally";
	public static final String WHEN = "when";
	public static final String FIRST = "first";
	public static final String LAST = "last";
	public static final String ONCE = "once";

	//Lists of words in subclasses
	private List BEFORESEQUENCELASTLISTVG;
	private List BEFORESEQUENCEFIRSTLISTBEP;
	private List BEFOREGAPLISTBEP;
	private List DURINGLISTVG;
	private List DURINGLISTBEP;
	private List AFTERSEQUENCELASTLISTVG;
	private List AFTERSEQUENCEFIRSTLISTBEP;
	private List AFTERGAPLISTBEP;
	private List SUDDENLYLISTBEP;
	private List FINALLYLISTBEP;
	private List ATLASTLISTBEP;
	private List WHENLISTVG;
	private List SOONASLISTVG;
	private List ONCELISTBEP;
	
	/** fills the lists with the cue words */		
	public TemporalLib()
	{		
		BEFORESEQUENCELASTLISTVG = new ArrayList();
		BEFORESEQUENCEFIRSTLISTBEP = new ArrayList();
		BEFOREGAPLISTBEP = new ArrayList();
		DURINGLISTVG = new ArrayList();
		DURINGLISTBEP = new ArrayList();
		AFTERSEQUENCELASTLISTVG = new ArrayList();
		AFTERSEQUENCEFIRSTLISTBEP = new ArrayList();
		AFTERGAPLISTBEP = new ArrayList();
		SUDDENLYLISTBEP = new ArrayList();
		FINALLYLISTBEP = new ArrayList();
		ATLASTLISTBEP = new ArrayList();
		WHENLISTVG = new ArrayList();
		SOONASLISTVG = new ArrayList();
		ONCELISTBEP = new ArrayList();
		
		BEFORESEQUENCELASTLISTVG.add(createNode("voordat", COMP, CMP));
		BEFORESEQUENCEFIRSTLISTBEP.add(createNode("daarvoor", ADV, MOD));
		
		BEFOREGAPLISTBEP.add(createNode("ooit", ADV, MOD));
		BEFOREGAPLISTBEP.add(createNode("vroeger", ADV, MOD));
		
		DURINGLISTVG.add(createNode("terwijl", COMP, CMP));
		DURINGLISTBEP.add(createNode("tegelijkertijd", ADV, MOD));
		
		AFTERSEQUENCELASTLISTVG.add(createNode("nadat", COMP, CMP));
		AFTERSEQUENCEFIRSTLISTBEP.add(createNode("vervolgens", ADV, MOD));
		AFTERSEQUENCEFIRSTLISTBEP.add(createNode("daarna", ADV, MOD));
		
		AFTERGAPLISTBEP.add(createNode("ooit", ADV, MOD));
		AFTERGAPLISTBEP.add(createNode("later", ADV, MOD));
		
		SUDDENLYLISTBEP.add(createNode("plotseling", ADV, MOD));
		
		FINALLYLISTBEP.add(createNode("uiteindelijk", ADV, MOD));
		
		ATLASTLISTBEP.add(createNode("eindelijk", ADV, MOD));
		ATLASTLISTBEP.add(createNode("tenslotte", ADV, MOD));
		ATLASTLISTBEP.add(createNode("uiteindelijk", ADV, MOD));
		
		WHENLISTVG.add(createNode("als", COMP, CMP));
		
		SOONASLISTVG.add(createNode("zodra", COMP, CMP));
		
		ONCELISTBEP.add(createNode("eens", ADV, MOD));
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
		
		if (!cat.startsWith(TEMPORAL))
			return null;
			
		if (cat.indexOf(WHEN) >= 0)
		{
			if (cat.indexOf(SOONAS) >= 0)
			{
				if (pos.equals("vg"))
					addList(SOONASLISTVG);
				//else
				//	addList(SOONASLISTBEP);
			}
			if (pos.equals("vg"))
				addList(WHENLISTVG);
			//else 
				//addList(WHENLISTBEP);
		}
		else if (cat.indexOf(ATLAST) >= 0)
		{
			//if (pos.equals("vg"))
			//	addList(ATLASTLISTVG);
			//else
			if (pos.equals("bep"))
				addList(ATLASTLISTBEP);
		}
		else if (cat.indexOf(SUDDENLY) >= 0)
		{
			//if (pos.equals("vg"))
			//	addList(SUDDENLYLISTVG);
			//else
			if (pos.equals("bep"))
				addList(SUDDENLYLISTBEP);
		}
		else if (cat.indexOf(FINALLY) >= 0)
		{
			//if (pos.equals("vg"))
			//	addList(FINALLYLISTVG);
			//else
			if (pos.equals("bep"))
				addList(FINALLYLISTBEP);
		}
		else if (cat.indexOf(BEFORE) >= 0)
		{
			if (cat.indexOf(GAP) >= 0)
			{
				//if (pos.equals("vg"))
				//	addList(BEFOREGAPLISTVG);
				//else
				if (pos.equals("bep"))
					addList(BEFOREGAPLISTBEP);
			}
			else if (cat.indexOf(SEQUENCE) >= 0)
			{				
				if (cat.indexOf(FIRST) < 0)
				{
					if (pos.equals("vg"))
						addList(BEFORESEQUENCELASTLISTVG);
					//else
					//	addList(BEFORESEQUENCELASTLISTBEP);
				}
				if (cat.indexOf(LAST) < 0)
				{
					//if (pos.equals("vg"))
					//	addList(BEFORESEQUENCEFIRSTLISTVG);
					//else
					if (pos.equals("bep"))
						addList(BEFORESEQUENCEFIRSTLISTBEP);
				}
			}
		}
		else if (cat.indexOf(AFTER) >= 0)
		{
			if (cat.indexOf(SEQUENCE) >= 0)
			{
				if (cat.indexOf(FIRST) < 0)
				{
					if (pos.equals("vg"))
						addList(AFTERSEQUENCELASTLISTVG);
					//else
					//	addList(AFTERSEQUENCELASTLISTBEP);
				}
				if (cat.indexOf(LAST) < 0)
				{
					//if (pos.equals("vg"))
					//	addList(AFTERSEQUENCEFIRSTLISTVG);
					//else
					if (pos.equals("bep"))
						addList(AFTERSEQUENCEFIRSTLISTBEP);
				}
			}
			else if (cat.indexOf(GAP) >= 0)
			{
				//if (pos.equals("vg"))
				//	addList(AFTERGAPLISTVG);
				//else
				if (pos.equals("bep"))
					addList(AFTERGAPLISTBEP);
			}
		}
		else if (cat.indexOf(DURING) >= 0)
		{
			if (pos.equals("vg"))
				addList(DURINGLISTVG);
			else
				addList(DURINGLISTBEP);
		}
		else if (cat.indexOf(ONCE) >= 0)
		{
			//if (pos.equals("vg"))
			//	addList(ONCELISTVG);
			//else
			if (pos.equals("bep"))
				addList(ONCELISTBEP);
		}
		
		return getResult();
	}	
	
	public boolean addToNucleus(RSDepTreeNode cueWord) 
	{
		if (cueWord != null)
			if ((cueWord.getData().get(RSTreeNodeData.ROOT).equals("voordat"))
					|| (cueWord.getData().get(RSTreeNodeData.ROOT).equals("ooit")))
				return true;
		return super.addToNucleus(cueWord);
	}
}