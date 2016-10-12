package natlang.rdg.documentplanner;

import java.util.*;
import parlevink.parlegraph.model.*;
import natlang.rdg.model.*;

/**
 * The MoodCreator can be used to add sentences to the document plan which are
 * only meant to create a mood and which do not have any important contents. The
 * sentences are added based on the location and internal state of the agens
 * of the plot element being currently carried out. 
 * 
 * The MoodModel can be extended with variables such as season, time and weather..
 * 
 * @author Nanda Slabbers
 *
 */
public class MoodCreator
{
	private RSGraph graph;
	
	private String location;
	private String state;
	/*private String season;
	private String time;
	private String weather;*/
	
	//locations
	public static final String FOREST = "forest";	
	public static final String CASTLE = "castle";
	public static final String MOUNTAIN = "mountain";
	public static final String DESERT = "desert";
	public static final String PATH = "path";
	
	//moods
	public static final String HAPPY = "happy";
	public static final String ANGRY = "angry";
	public static final String SCARED = "scared";
	
	public static Vector FORESTHAPPY;
	public static Vector FORESTSCARED;
	public static Vector PATHHAPPY;
	public static Vector PATHSCARED;
	
	//seasons
	/*public static final String SPRING = "spring";
	public static final String SUMMER = "summer";
	public static final String AUTUMN = "autumn";
	public static final String WINTER = "winter";
	
	//times
	public static final String MORNING = "morning";
	public static final String AFTERNOON = "afternoon";
	public static final String EVENING = "evening";
	public static final String NIGHT = "night";
	
	//weather
	public static final String SUN = "sun";
	public static final String RAIN = "rain";
	public static final String WIND = "wind";*/
	
	/**
	 * Constructor
	 * 
	 * Initializes the current location, current internal state and the lists of 
	 * sentences that can be added to the document plan based on these variables 
	 */
	public MoodCreator()
	{
		location = "";
		state = "";
		/*season = "";
		time = "";
		weather = "";	*/	
		
		FORESTHAPPY = new Vector();
		FORESTSCARED = new Vector();
		PATHHAPPY = new Vector();
		PATHSCARED = new Vector();
		
		FORESTHAPPY.add(new CannedText("De vogeltjes floten vrolijk op de takken van de bomen."));
		FORESTSCARED.add(new CannedText("De gure wind waaide door de bomen."));
		PATHHAPPY.add(new CannedText("De lammetjes huppelden vrolijk door de wei."));
		PATHSCARED.add(new CannedText("De maan scheen nauwelijks door de dichte bomen op het donkere pad."));
	}
	
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	public RSGraph getGraph()
	{
		return graph;
	}
		
	/**
	 * Starts the transformation
	 * @return graph
	 */
	public RSGraph transform()
	{
		location = "";
		state = "";
		
//		Iterator it = graph.getMVertices();
//		transformDepthFirst((RSVertex) it.next());		
		if(graph.getRoot() != null)
			transformDepthFirst(graph.getRoot());		
		
		return graph;
	}
	
	/**
	 * Transforms the document plan depth first and adds nodes if possible
	 * @param v
	 */
	public void transformDepthFirst(RSVertex v)
	{		
		if (v.getType().equals("mess"))
		{
			PlotElement pe = (PlotElement) v;
			if (pe.getType().equals("state"))
				state = pe.getName();
			location = FOREST;			
			CannedText ct = getPossibleMessage();
						
			if (ct != null)
			{
				RhetRelation par = (RhetRelation) v.getParent().next();
				MLabeledEdge edge = (MLabeledEdge) v.getIncidentMEdges().next();
				String label = edge.getLabel();
				
				graph.removeMVertex(v);
				graph.removeMEdge(edge);				
									
				RhetRelation rr = createRhetRel("temporal", pe, ct);
				
				if (label.equals("nucleus"))
				{
					RSEdge nuc = new RSEdge("nucleus");
					nuc.setSource(par);
					nuc.setTarget(rr);
					graph.addMEdge(nuc);
				}
				else
				{
					RSEdge sat = new RSEdge("satellite");
					sat.setTarget(par);
					sat.setSource(rr);
					graph.addMEdge(sat);
				}
			}
		}
		else if (v.getType().equals("text"))
		{
		}
		else if (v.getType().equals("rhetrel"))
		{
			RSVertex sat = ((RhetRelation) v).getSatellite();
			RSVertex nuc = ((RhetRelation) v).getNucleus();
			
			if (sat != null)
				transformDepthFirst(sat);
			if (nuc != null)
				transformDepthFirst(nuc);		
		}
	}
		
	/**
	 * Returns a message that can be added based on the location and state
	 * @return sentence stored as canned text node
	 */
	public CannedText getPossibleMessage()
	{		
		// op dit moment gewoon maar 1 message per location/mood combinatie..
		if (location.equals(FOREST))
		{
			if (state.equals(HAPPY))
				return getForestHappyMessage();
			else if (state.equals(SCARED))
				return getForestScaredMessage();
		}
		else if (location.equals(PATH))
		{
			if (state.equals(HAPPY))
				return getPathHappyMessage();
			else if (state.equals(SCARED))
				return getPathScaredMessage();
		}		
		return null;
	}
	
	/**
	 * Returns randomly one of the sentences with forest as location and happy as state
	 * and removes this sentence from the list in order to avoid adding the same sentence
	 * more than one
	 * @return sentence stored as canned text node
	 */
	public CannedText getForestHappyMessage()
	{
		int i = (int) (Math.random() * FORESTHAPPY.size());		
		if (FORESTHAPPY.size() > 0)
		{
			CannedText ct = (CannedText) FORESTHAPPY.elementAt(i);
			FORESTHAPPY.remove(ct);
			return ct;
		}
		return null;
	}
	
	/**
	 * Returns randomly one of the sentences with forest as location and scared as state
	 * and removes this sentence from the list in order to avoid adding the same sentence
	 * more than one
	 * @return sentence stored as canned text node
	 */
	public CannedText getForestScaredMessage()
	{
		int i = (int) (Math.random() * FORESTSCARED.size());
		System.out.println("nou, dis raar: " + i);
		if (FORESTSCARED.size() > 0)
		{
			CannedText ct = (CannedText) FORESTSCARED.elementAt(i);
			FORESTSCARED.remove(ct);
			return ct;
		}
		return null;
	}
	
	/**
	 * Returns randomly one of the sentences with path as location and happy as state
	 * and removes this sentence from the list in order to avoid adding the same sentence
	 * more than one
	 * @return sentence stored as canned text node
	 */
	public CannedText getPathHappyMessage()
	{
		int i = (int) (Math.random() * PATHHAPPY.size());
		if (PATHHAPPY.size() > 0)
		{
			CannedText ct = (CannedText) PATHHAPPY.elementAt(i);
			PATHHAPPY.remove(ct);
			return ct;
		}
		return null;
	}
	
	/**
	 * Returns randomly one of the sentences with path as location and scared as state
	 * and removes this sentence from the list in order to avoid adding the same sentence
	 * more than one
	 * @return sentence stored as canned text node
	 */
	public CannedText getPathScaredMessage()
	{
		int i = (int) (Math.random() * PATHSCARED.size());
		if (PATHSCARED.size() > 0)
		{
			CannedText ct = (CannedText) PATHSCARED.elementAt(i);
			PATHSCARED.remove(ct);
			return ct;
		}
		return null;
	}
		
	/**
	 * Creates a RhetRelation node with two children
	 * @param rel the relation
	 * @param vert1 the first node / tree
	 * @param vert2 the second node / tree
	 * @return relation
	 */
	public RhetRelation createRhetRel(String rel, RSVertex vert1, RSVertex vert2)
	{
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge sat = new RSEdge("satellite");
		sat.setTarget(rhetrel);
		sat.setSource(vert1);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert2);
					
		graph.addMEdge(sat);
		graph.addMEdge(nuc);
		
		return rhetrel;
	}
	
	//set and get methods
	/*
	public void setLocation(String loc)
	{
		location = loc;
	}
	
	public void setState(String st)
	{
		state = st;
	}	
	
	public void setSeason(String sn)
	{
		season = sn;
	}
	
	public void setTime(String tm)
	{
		time = tm;
	}
	
	public void setWeather(String wth)
	{
		weather = wth;
	}*/
	
	/*public String getLocation()
	{
		return location;
	}
	
	public String getState()
	{
		return state;
	}*/
	
	/*public String getSeason()
	{
		return season;
	}
	
	public String getTime()
	{
		return time;
	}
	
	public String getWeather()
	{
		return weather;
	}*/
}
