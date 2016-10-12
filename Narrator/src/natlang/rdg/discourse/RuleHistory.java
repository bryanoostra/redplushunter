package natlang.rdg.discourse;

import natlang.rdg.libraries.*;
import natlang.rdg.model.*;

import java.util.*;


/**	RuleHistory keeps track of the last few grammatical rules that were used to 
 *	order a dependency tree, to prevent monotone sentences. It only tracks the
 *	syntactic categories which contain several rules with the same daughters. In
 *	other categories, no variety is possible (a NP is always ordered DET HD)
 */
public class RuleHistory implements LibraryConstants
{
	private Rule[] smain;	//lists with rules for those categories in which it can vary
	private Rule[] ssub;
	private Rule[] ti;
	
	/** Initializes the lists with recently used rules. Only the last three rules
	 *	are remembered, as the number of different rules possible is not very great
	 */
	public RuleHistory()
	{
		smain = new Rule[3];
		ssub = new Rule[3];
		ti = new Rule[3];
	}
	
	/** Helper function - Returns the desired rule array */
	private Rule[] getArray(Rule r)
	{
		String cat = r.getCategory();
		if (cat.equals(SMAIN))
			return smain;
		else if (cat.equals(SSUB))
			return ssub;
		else if (cat.equals(TI))
			return ti;
		return null;
	}
	
	/** Puts the given rule at first position in recent, and moves the other rules
	 *	to the back
	 */
	public void setRecent(Object o)
	{
		try
		{
			Rule[] a = getArray((Rule) o);
			if (a == null)
				return;
			for (int i = (a.length - 1); i > 0; i--)
				a[i] = a[i - 1];
			a[0] = (Rule) o;
		}
		catch (ClassCastException e)
		{}
	}
	
	/** Checks whether the given rule was recently used */
	public boolean isRecent(Object o)
	{
		try
		{
			Rule[] a = getArray((Rule) o);
			if (a == null)
				return false;
			for (int i = 0; i < a.length; i++)
			{
				if (a[i] == null)
					break;
					
				if (a[i].equals((Rule) o))
					return true;
			}
			return false;
		}
		catch (ClassCastException e)
		{
			return false;
		}
	}
	
	/** If there is no rule that is not recent, get the least recent of the given list
	 */
	public Rule getLeastRecent(Iterator<Rule> it)
	{
		List<Rule> l = new ArrayList<Rule>();
		while (it.hasNext())
			l.add(it.next());
				
		Rule[] a = getArray((Rule) l.get(0));
		if (a == null)
			return (Rule) it.next();
			
		for (int i = (a.length - 1); i > 0; i--)	//go backwards, to start with least recent
		{
			if (a[i] == null)
				break;
				
			for (int j = 0; j < l.size(); j++)
			{
				Rule r = (Rule) l.get(j);
				if (a[i].equals(r))	//rules[j] least recent
					return r;
			}
		}
		return (Rule) l.get(0);	//none are at all recent, so just return one
	}
	
	/** Resets all arrays, for instance at the beginning of a new narrative */
	public void reset()
	{
		smain = new Rule[3];
		ssub = new Rule[3];
		ti = new Rule[3];
	}
}