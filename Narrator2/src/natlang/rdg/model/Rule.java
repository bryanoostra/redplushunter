package natlang.rdg.model;

import natlang.rdg.libraries.*;
import parlevink.parlegraph.model.*;

import java.util.*;


/**	This class models a grammatical rule
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class Rule implements LibraryConstants
{
	private final String cat;				//parent category
	private final List daughters = new ArrayList();		//ordered list with child nodes
	private int numMod, modPositioned;
	private int numCnj, cnjPositioned;
	
	/** Creates a rule, takes a category and a variable number of daughter labels */
	public Rule(String c, String[] d)
	{
		cat = new String(c);
		numMod = 0;
		numCnj = 0;
		modPositioned = 0;
		cnjPositioned = 0;
		
		for (int i = 0; i < d.length; i++)
		{
			daughters.add(d[i]);
			if (d[i].equals(MOD))
				numMod++;
			else if (d[i].equals(CNJ))
				numCnj++;
		}
	}
	
	/** Checks whether this particular rule applies to the given list of child edges */
	public boolean applies(List children)
	{
		int modFound = 0;
		int cnjFound = 0;
		
		for (int i = 0; i < children.size(); i++)
		{
			MLabeledEdge e1 = (MLabeledEdge) children.get(i);

			if (e1.getClass().getName().equals("natlang.rdg.model.MOrderedEdge"))
			{
				MOrderedEdge edge = (MOrderedEdge) children.get(i);
				StringBuffer bf = new StringBuffer(edge.getLabel());
				int idx;
				if ((idx = bf.indexOf(IDENTICAL)) >= 0)
					bf.delete(idx, (idx + IDENTICAL.length()));
				if ((idx = bf.indexOf(BORROWED)) >= 0)
					bf.delete(idx, (idx + BORROWED.length()));
				String str = bf.toString();
				int modCntr = 0;
				int cnjCntr = 0;
				
				for (int j = 0; j < daughters.size(); j++)
				{
					String lb = (String) daughters.get(j);
					if (lb.equals(str))
					{	//there can be more than one modifier in a rule, so check whether they are the
						if (str.equals(MOD))	//right number
						{
							if (modFound >= numMod)
								return false;
							else if (modCntr < modFound)
								modCntr++;
							else
							{
								modFound++;
								break;
							}
						}
						else if (str.equals(CNJ))	//conjuncts also
						{
							if (cnjFound >= numCnj)
								return false;
							else if (cnjCntr < cnjFound)
								cnjCntr++;
							else
							{
								cnjFound++;
								break;
							}
						}
						else
							break;
					}
					else if (j == (daughters.size() - 1))	//if label not found, return false
						return false;
				}
			}
		}	//if less modifiers or conjuncts have been found than should be there, return false
		if ((modFound != numMod) || (cnjFound != numCnj))
			return false;
			
		if (daughters.size() != children.size())
			return false;
		
		return true;
	}
	
	/* Gives the position of the label according to this rule */
	public int getPosition(String lb) throws Exception
	{
		int idx;
		StringBuffer bf = new StringBuffer(lb);	//remove identical and borrowed from the label
		if ((idx = bf.indexOf(IDENTICAL)) >= 0)	//so the label can be compared
			bf.delete(idx, (idx + IDENTICAL.length()));
		if ((idx = bf.indexOf(BORROWED)) >= 0)
			bf.delete(idx, (idx + BORROWED.length()));
			
		String newlb = bf.toString();
		int modCntr = 0;
		int cnjCntr = 0;
		
		for (int i = 0; i < daughters.size(); i++)
		{
			if (daughters.get(i).equals(newlb))	//as there can be more than one modifier
			{	//or conjunct, count the occurances
				if (newlb.equals(MOD) && (numMod > 1))
					if (modPositioned > modCntr)
						modCntr++;
					else
					{
						modPositioned++;
						return i;
					}
				else if (newlb.equals(CNJ) && (numCnj > 1))
					if (cnjPositioned > cnjCntr)
						cnjCntr++;
					else
					{
						cnjPositioned++;
						return i;
					}
				else	
					return i;
			}
		}
			
		//return -1;
		throw new Exception("the given label is no part of this rule!");
	}
	
	/** Produce a String, listing the target category and the child nodes */
	public String toString()
	{
		StringBuffer result = new StringBuffer("");
		for (int i = 0; i < daughters.size(); i++)
			result.append((String) daughters.get(i) + " ");
		return result.toString();
	}
	
	/** Return the head category */
	public String getCategory()
	{
		return cat;
	}
	
	/** Return a List with child nodes */
	public List getDaughters()
	{
		return daughters;
	}
	
	/** Check whether this rule equals another rule, by checking its cat and its
	 *	child nodes
	 */
	public boolean equals(Rule r)
	{
		 if (!r.getCategory().equals(cat))
		 	return false;
		 
		 List daughters2 = r.getDaughters();
		 if (daughters.size() != daughters2.size())
		 	return false;
		 
		 for (int i = 0; i < daughters.size(); i++)
		 {
		 	String d1 = (String) daughters.get(i);
		 	String d2 = (String) daughters2.get(i);
		 	if (!d1.equals(d2))
		 		return false;
		 }
		 return true;
	}
}