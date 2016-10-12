package natlang.rdg.discourse;

import natlang.rdg.model.*;

import java.util.*;


/**
 * A history containing all plot elements mentioned sofar
 * Is used to detect repetition, add discourse markers and repeat information
 * if a certain entity has not been mentioned for a long time 
 * 
 * @author Nanda Slabbers
 */
public class DiscourseHistory 
{
	private Vector<PlotElement> history;
	
	/**
	 * Constructor, creates new history
	 *
	 */
	public DiscourseHistory()
	{
		history = new Vector<PlotElement>();
	}
	
	/**
	 * Adds plot element to history
	 * @param pe plot element
	 */
	public void addPlotElement(PlotElement pe)
	{
		history.add(pe);
	}
	
	/**
	 * Checks whether a discourse marker should be added to the current plot element 
	 * @param pe the plot element
	 * @return the discourse marker to be added to m
	 */
	public String addDiscourseMarker(PlotElement pe)
	{
		if (shortRepeatedMention(pe))
		{
			if (pe.getKind().equals("action") || pe.getKind().equals("event") || pe.getKind().equals("perception"))
				return "again";
			if (pe.getKind().equals("state"))
				return "still";
			if (pe.getKind().equals("goal") || pe.getKind().equals("setting"))
				return "remove";		// no real discourse marker, but indicates that the plot element has to be removed because it has already been told
		}
		if (addAlso(pe))
			return "also";
		
		return "";
	}
	
	/**
	 * Checks whether the plot element has just been told
	 * @param pe the plot element
	 */
	public boolean shortRepeatedMention(PlotElement pe)
	{		
		if ((history.size() > 0 && pe.equals((PlotElement) history.elementAt(history.size()-1)))
				|| (history.size() > 1 && pe.equals((PlotElement) history.elementAt(history.size()-2)))
				|| (history.size() > 2 && pe.equals((PlotElement) history.elementAt(history.size()-3)))
				|| (history.size() > 3 && pe.equals((PlotElement) history.elementAt(history.size()-4))))
			return true;		
		
		return false;
	}
	
	/**
	 * Compares the current plot element to the final two plot elements
	 * @param pe the current plot element
	 * @return whether the word 'also' should be added
	 */
	public boolean addAlso(PlotElement pe)
	{
		if ((history.size() > 0 && addAlso(pe, (PlotElement) history.elementAt(history.size()-1)))
				|| (history.size() > 1 && addAlso(pe, (PlotElement) history.elementAt(history.size()-2))))
			return true;
		
		return false;
	}
	
	/**
	 * Compares two plot elements and checks if they satisfy the requirements for adding 'also':
	 * - the plot elements differ in only one argument
	 * - the plot elements differ in two arguments and these arguments have been swapped
	 * Additional requirement:
	 * - if the plot elements differ in only one argument AND this argument is the name of the action:
	 * 	 only add the cue word if the plot elements happen at the same time!	 
	 * @param pe1 first plot element
	 * @param pe2 second plot element
	 * @return a boolean indicating whether the cue word 'also' should be added
	 */
	public boolean addAlso(PlotElement pe1, PlotElement pe2)
	{		
		String type1 = pe1.getKind();
		String name1 = pe1.getName();
		String agens1 = pe1.getAgens();
		String patiens1 = pe1.getPatiens();
		String target1 = pe1.getTarget();
		boolean instrument1 = false;
		if (pe1.getInstrument() != null)
			instrument1 = true;
		
		String type2 = pe2.getKind();
		String name2 = pe2.getName();
		String agens2 = pe2.getAgens();
		String patiens2 = pe2.getPatiens();
		String target2 = pe2.getTarget();
		boolean instrument2 = false;
		if (pe2.getInstrument() != null)
			instrument2 = true;
		
		if (type1.equals(type2))
		{
			int nrdiffs = 0;
			
			if (!name1.equals(name2))
				nrdiffs++;
			if (!agens1.equals(agens2))
				nrdiffs++;
			if (!patiens1.equals(patiens2))
				nrdiffs++;
			if (!target1.equals(target2))
				nrdiffs++;
			if (instrument1 != instrument2)
				nrdiffs++;
			
			if (nrdiffs == 1)
			{
				if (type1.equals("action"))
				{
					if (name1.equals(name2))
						return true;					
					else
					{
						if (pe1.getTime() == pe2.getTime())
							return true;
					}
				}
				else
					return true;
			}
			
			if (nrdiffs == 2)
			{
				if (name1.equals(name2) && (instrument1 == instrument2) &&
						((agens1.equals(patiens2) && patiens1.equals(agens2)) ||
								(agens1.equals(target2) && target1.equals(agens2)) ||
								(patiens1.equals(target2) && target1.equals(patiens2))))
					return true;
			}
		}		
		return false;
	}
	
	/**
	 * Checks whether some background information should be repeated in order to refresh the reader's mind
	 * @param pe the current plot element
	 * @return the background information to be added (null of no information should be added)
	 */
	public PlotElement repeatBgInfo(PlotElement pe)
	{		
		String ag = pe.getAgens();
		String pa = pe.getPatiens();
		String ta = pe.getTarget();
		
		boolean bag = false;
		boolean bpa = false;
		boolean bta = false;
		
		for (int i=history.size()-1; i>history.size()-10; i--)
		{
			PlotElement tmp = (PlotElement) history.elementAt(i);
			if (tmp.contains(ag))
				bag = true;
			if (tmp.contains(pa))
				bpa = true;
			if (tmp.contains(ta))
				bta = true;
			
			if (bag && bpa && bta)
				return null;
			
			if (i==0)
				return null;
		}
				
		if (!bag)
			return getFirstState(ag);
		if (!bpa)
			return getFirstState(pa);
		if (!bta)
			return getFirstState(ta);
		
		return null;
	}
	
	/**
	 * Retrieves the plot element used to introduce a certain entity (can be used 
	 * to repeat background information)
	 * @param ent the entity for which background information should be added
	 * @return the background information
	 */
	public PlotElement getFirstState(String ent)
	{
		for (int i=0; i<history.size(); i++)
		{
			PlotElement tmp = (PlotElement) history.elementAt(i);
			if (tmp.contains(ent) && tmp.getKind().equals("state"))
				return new PlotElement(tmp);
		}
		return null;
	}
		
	public DiscourseHistory getCopy()
	{
		Vector<PlotElement> tmp = new Vector<PlotElement>();
		for (int i=0; i<history.size(); i++)
			tmp.add(history.elementAt(i));
		
		DiscourseHistory result = new DiscourseHistory();
		result.setHist(tmp);
		
		return result;
	}
	
	public void setHist(Vector<PlotElement> v)
	{
		history = v;
	}
}
