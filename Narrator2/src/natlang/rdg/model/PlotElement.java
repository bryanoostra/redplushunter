package natlang.rdg.model;

import java.util.*;

import natlang.rdg.libraries.LibraryConstants;
import parlevink.parlegraph.model.*;

/**
 * This class represents a plot element taken from the input. It holds the kind
 * of plot element, the name, agens, patiens, target, instruments of the plot element,
 * a boolean indicating whether the plot element was success and the time of plot element.
 * 
 * Furthermore the plot element may be extended with a details Vector, specifying certain
 * details about elements of the plot element. Descr is the way the element should be 
 * described, discm shows whether a discourse marker should be included and subpe
 * is the subplotelement if the plot element has one.
 *  
 * @author Nanda Slabbers
 * @author Marissa Hoek
 *
 */
public class PlotElement extends MLabeledVertex implements RSVertex
{
	private String kind;
	
	private String name;
	private String agens;
	private String patiens;
	private String target;
	private Instrument instr;
	private long time;
	
	private Vector<Detail> details;
	private String descr;				// for actions: failed / passive
										// for states: wat / zo
	private PlotElement subpe;
	private String discm;
	private boolean isFlashback = false;
	
	/**
	 * Creates an empty plot element
	 *
	 */
	public PlotElement()
	{	
		super();
		
		setLabel("");
		
		details = new Vector<Detail>();
		descr = "";
		discm = "";
	}
	
	/**
	 * Creates a copy of a plot element
	 *
	 */
	public PlotElement (PlotElement pe)
	{
		super();
		
		setLabel(pe.getName());
		
		kind = pe.getKind();
		name = pe.getName();
		agens = pe.getAgens();
		patiens = pe.getPatiens();
		target = pe.getTarget();
		instr = pe.getInstrument();
		
		time = -1;
		
		details = new Vector<Detail>();
		descr = "";
		discm = "";
	}

	/**
	 * Creates a plot element
	 *
	 */
	public PlotElement(String k, String n, String a, String p, String t, Instrument instr)
	{			
		super();
		
		setLabel(n);
		
		kind = k;
		name = n;
		agens = a;
		patiens = p;
		target = t;
		this.instr = instr;
		
		time = -1;
		
		details = new Vector<Detail>();
		descr = "";
		discm = "";
	}
	
	/**
	 * Creates a plot element including the time argument
	 *
	 */
	public PlotElement(String k, String n, String a, String p, String t, Instrument instr, long tm)
	{			
		super();
		
		setLabel(n);
		
		kind = k;
		name = n;
		agens = a;
		patiens = p;
		target = t;
		this.instr = instr;
		
		time = tm;
		
		details = new Vector<Detail>();
		descr = "";
		discm = "";
	}
	
	/**
	 * Sets the sub plot element
	 * 
	 * */
	public void setSubElement(PlotElement dm)
	{
		subpe = dm;
	}
	
	/**
	 * Sets the details vector
	 * @param v
	 */
	public void setDetails(Vector<Detail> v)
	{
		details = v;
	}
	
	/**
	 * Sets the descriptor ("wat" or "zo" for states and "passive" or "failed" for actions)
	 * @param d
	 */
	public void setDescr(String d)
	{
		descr = d;
	}
	
	/**
	 * Sets the discourse marker
	 * @param d
	 */
	public void setDiscm(String d)
	{
		discm = d;
	}
	
	/**
	 * Adds a detail to the details vector
	 * @param ie
	 */
	public void addDetail(Detail ie)
	{
		details.addElement(ie);		
	}
	
	/**
	 * Sets the instrument
	 * @param i
	 */
	public void setInstrument(Instrument instr)
	{
		this.instr = instr;
	}
	
	/**
	 * Returns the vector with Details.
	 * In this case the Vector contains Detail objects.
	 */
	public Vector<Detail> getDetails()
	{
		return details;
	}

	/**
	 * Returns the sub plot element
	 */
	public PlotElement getSubElement()
	{
		return subpe;
	}
	
	/**
	 * Returns the descriptor
	 */
	public String getDescr()
	{
		return descr;
	}
	
	/**
	 * Returns the discourse marker
	 */
	public String getDiscm()
	{
		return discm;
	}
	
	/**
	 * Sets the agens
	 * @param a
	 */
	public void setAgens(String a)
	{
		agens = a;
	}
		
	/**
	 * Sets the name
	 * @param n
	 */
	public void setName(String n)
	{
		name = n;
	}
			
	/**
	 * Returns the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the agens
	 */
	public String getAgens()
	{
		return agens;
	}
	
	/**
	 * Returns the patiens
	 */
	public String getPatiens()
	{
		return patiens;
	}
	
	/**
	 * Returns the target
	 */
	public String getTarget()
	{
		return target;
	}
	
	/**
	 * Returns the instruments vector
	 */
	public Instrument getInstrument()
	{
		return instr;
	}
		
	/**
	 * Returns the time argument
	 */
	public long getTime()
	{
		return time;
	}
	
	/**
	 * Sets the time
	 * @param t
	 */
	public void setTime(int t)
	{
		time = t;
	}

	/**
	 * Returns a vector with details about a certain concept.
	 * In this case the Vector contains Strings.
	 * @param concept
	 */
	public Vector<String> getDetails(String concept)
	{
		Vector<String> result = new Vector<String>();
		
		for (int i=0; i<details.size(); i++)
		{
			Detail d = (Detail) details.elementAt(i);
			if (d.getSubject().equals(concept))
				result.addElement(d.getDetail());
		}
		
		return result;
	}
	
	/**
	 * Returns the kind (such as action, state etc)
	 */
	public String getKind()
	{
		return kind;
	}
		
	/**
	 * Returns the type (simply the string "mess")
	 */
	public String getType()
	{
		return "mess";
	}

	/**
	 * Returns the parent node
	 */
	public Iterator getParent() 
	{
		List<MVertex> result = new ArrayList<MVertex>();
		Iterator it = getIncidentInMEdges();
		while (it.hasNext())
		{
			
			RSEdge edge = (RSEdge) it.next();
//			System.out.println(" *** (PE) IN *** " + edge.getLabel() + " --> "  + edge.getSource());
			if (edge.getLabel().equals(LibraryConstants.NUCLEUS)){
//				System.out.println(" FOUND IT ");
				result.add(edge.getSource());
			}
		}
		
		it = getIncidentOutMEdges();
		while (it.hasNext())
		{
			
			RSEdge edge = (RSEdge) it.next();
//			System.out.println(" *** (PE) OUT *** " + edge.getLabel() + " --> "  + edge.getTarget());
			if (edge.getLabel().equals(LibraryConstants.SATELLITE)){
//				System.out.println(" FOUND IT ");
				result.add(edge.getTarget());
			}
		}
		
		return result.iterator();
	}
	
	/**
	 * Checks whether the agens, patiens or target equals the given entity
	 * @param ent
	 */
	public boolean contains(String ent)
	{
		if (agens.equals(ent) || patiens.equals(ent) || target.equals(ent))
			return true;
		return false;
	}
	
	/**
	 * Checks if the plot elements are the same
	 * @param pe
	 */
	public boolean equals(PlotElement pe)
	{
		// moet instruments nog vergelijken, nu vindt die
		// 'de prinses sloeg de prins met een hamer' en
		// 'de prinses sloeg de prins met een knuppel' gelijk...
		boolean not1 = false;
		boolean not2 = false;
		
		// first check if they both include the modifier 'not' or 'nomore', or both
		// do NOT include such a modifier
		Vector<String> v1 = getDetails(name);
		for (int i=0; i<v1.size(); i++)
		{
			String det = (String) v1.elementAt(i);
			if (det.equals("not") || det.equals("nomore"))
				not1 = true;
		}	
		
		Vector<String> v2 = pe.getDetails(pe.getName());
		for (int i=0; i<v2.size(); i++)
		{
			String det = (String) v2.elementAt(i);
			if (det.equals("not") || det.equals("nomore"))
				not2 = true;
		}	
		
		if (not1 != not2)
			return false;
		
		// then check if the other elements are the same
		if (	(pe.getKind() == kind || pe.getKind().equals(kind))
				&& (pe.getName() == name || pe.getName().equals(name) )
				&& (pe.getAgens() == agens || pe.getAgens().equals(agens) )
				&& (pe.getPatiens() == patiens || pe.getPatiens().equals(patiens) )
				&& (pe.getTarget() == target || pe.getTarget().equals(target) )
				&& (pe.getInstrument() == instr || pe.getInstrument().equals(instr)))
			return true;
		return false;
	}
	
	public String toString()
	{
		return "Plot element:\t" + kind + "\t" + name + "\t" + agens + "\t" + patiens + "\t" + target + "\t" + instr + "\t" + subpe ;		
	}

	public boolean isFlashback() {
		return isFlashback;
	}

	public void setFlashback(boolean isFlashback) {
		this.isFlashback = isFlashback;
	}
}
