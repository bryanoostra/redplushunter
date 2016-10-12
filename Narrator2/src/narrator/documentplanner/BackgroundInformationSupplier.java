package narrator.documentplanner;

import java.util.Iterator;
import java.util.Vector;

import narrator.lexicon.CharacterInfo;
import narrator.reader.CharacterModel;
import narrator.shared.Settings;
import natlang.rdg.discourse.DiscourseHistory;
import natlang.rdg.discourse.History;
import natlang.rdg.lexicalchooser.StoryWorld;
import natlang.rdg.libraries.LibraryConstants;
import natlang.rdg.model.PlotElement;
import natlang.rdg.model.RSEdge;
import natlang.rdg.model.RSGraph;
import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;
import parlevink.parlegraph.model.MLabeledEdge;

/**
 * The BackgroundInformationSupplier is responsible for adding background information
 * to the document plan and adding necessary discourse markers. In onder to achieve this
 * it uses a discourse history and a history containing the entities already mentioned.
 * Furthermore it uses the character history to get an entity's name. 
 * 
 * @author Nanda Slabbers
 *
 */
public class BackgroundInformationSupplier 
{
	RSGraph graph;
	History hist;
	DiscourseHistory dischist;
	History oldhist;
	DiscourseHistory olddischist;
	CharacterInfo chars;
	StoryWorld sw;
	Vector<PlotElement> removemess;
		
	/**
	 * Constructor - initializes the characterinfo
	 * Currently modelled, but should be taken from input
	 */
	public BackgroundInformationSupplier(CharacterInfo characters)
	{
		chars = characters;
		sw = new StoryWorld();
		hist = new History();
		oldhist = new History();
		dischist = new DiscourseHistory();
		olddischist = new DiscourseHistory();
	}
	
	/**
	 * Sets the graph
	 * @param g the graph
	 */
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	/**
	 * Starts the transformation of the document plan
	 * @return graph
	 */
	public RSGraph transform()
	{				
		removemess = new Vector<PlotElement>();		
		
//		Iterator it = graph.getMVertices();
//		if (it.hasNext())
//			transformDepthFirst((RSVertex) it.next());
		if(graph.getRoot() != null)
			transformDepthFirst(graph.getRoot());
							
		for (int i=0; i<removemess.size(); i++)		
			removeMessage((PlotElement) removemess.elementAt(i));
		
		return graph;
	}
	
	/**
	 * Transforms the document plan depth first and checks whether discoursemarkers
	 * should be added, messages should be removed, or names or old messages should
	 * be added
	 * @param v
	 */
	public void transformDepthFirst(RSVertex v)
	{		
		if (v.getType().equals("mess"))
		{
			PlotElement pe = (PlotElement) v;
									
			if (Settings.ADDDISCMARKERS)
			{
				String discmarker = dischist.addDiscourseMarker(pe);
				if (!discmarker.equals(""))
				{
					if (discmarker.equals("remove"))
						removemess.add(pe);
					else
						pe.setDiscm(discmarker);
				}
			}
									
			addNameOrMessage(pe.getAgens(), pe);
			addNameOrMessage(pe.getPatiens(), pe);
			addNameOrMessage(pe.getTarget(), pe);

			dischist.addPlotElement(pe);
			
			if (pe.getSubElement() != null)
			{
				PlotElement pe2 = pe.getSubElement();
				
				addNameOrMessage(pe2.getAgens(), (PlotElement) v);
				addNameOrMessage(pe2.getPatiens(), (PlotElement) v);
				addNameOrMessage(pe2.getTarget(), (PlotElement) v);
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
	 * Creates RhetRelation
	 * @param rel
	 * @param vert1
	 * @param vert2
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
	
	/**
	 * Adds the name or old message to the document plan
	 * @param ent the entity for which the name is to be added
	 * @param pe the message that should be added to the document plan
	 */
	public void addNameOrMessage(String ent, PlotElement pe)
	{
		if (ent == null || ent.equals(""))
			return;
		
		if (hist.newEntity(ent))
		{
			PlotElement tmp = getName(ent);			
			if (tmp != null)
			{
				addMessage(pe, tmp, true);
				dischist.addPlotElement(tmp);
			}
									
			PlotElement tmp2 = sw.getFact(ent);
			sw.addEntity(ent);
			if (tmp2 != null)
			{
				addMessage(pe, tmp2, false);
				dischist.addPlotElement(tmp2);
			}

			hist.addEntity(ent);
		}
		else
		{
			PlotElement tmp = dischist.repeatBgInfo(pe);

			if (tmp != null)
			{		
				addMessage(pe, tmp, true);
				dischist.addPlotElement(tmp);
			}
		}
	}
	
	/**
	 * Returns a plot element representing the name of the entity
	 * @param ent
	 * @return plot element
	 */
	public PlotElement getName(String ent)
	{
		System.out.println("Getting character "+ent);
		CharacterModel cm = chars.getChar(ent);
		
		/*
		 * &&
				!(Settings.FOCALIZATION&&
						(Settings.PERSPECTIVE.equals(LibraryConstants.FIRST) || Settings.PERSPECTIVE.equals(LibraryConstants.SECOND))
					&&cm.getName().equals(Settings.FOCALIZEDCHARACTER)))
		 */
		
		boolean focalization = (Settings.FOCALIZATION&&ent.equals(Settings.FOCALIZEDCHARACTER));
		boolean perspective = (Settings.PERSPECTIVE.equals(LibraryConstants.FIRST)||Settings.PERSPECTIVE.equals(LibraryConstants.SECOND));
		
		if (cm != null && cm.getName() != null && !cm.getName().equals("")&&!(focalization&&perspective))
		{
			return new PlotElement("action", "call", ent, cm.getName(), "", null);		
		}
		return null;
	}
	
	/**
	 * Adds a plot element representing background information to another plot element (by an elaboration relation) and adds this to the graph
	 * @param pe the original plot element
	 * @param pe2 the plot element representing the background information
	 * @param after a boolean indicating whether the background information should be added before or after the other plot element
	 */
	public void addMessage(PlotElement pe, PlotElement pe2, boolean after)
	{		
		RhetRelation par = (RhetRelation) pe.getParent().next();
		MLabeledEdge edge = (MLabeledEdge) pe.getIncidentMEdges().next();
		String label = edge.getLabel();
		
		graph.removeMVertex(pe);
		graph.removeMEdge(edge);
				
		RhetRelation rr = null;
		if (after)
			rr = createRhetRel("relative", pe, pe2);
		else
			rr = createRhetRel("cause-voluntary", pe2, pe);		// needs to be checked if this is correct in all cases
		
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
	
	/**
	 * Removes node from document plan (is somewhat strange, but is the same as the function in initialdocumentplanbuilder)
	 * 
	 * @param pe the node to be removed
	 */
	public void removeMessage(PlotElement pe)
	{
		// ok is wat vaag, maar variabelen zijn als volgt:
		// dm: de node (detailedmessage) die verwijderd moet worden
		// par: de parent (rhetrel) van dm
		// edge: de pijl tussen par en dm
		// othernode: het andere kind van par (detailedmessage of rhetrel), dat op de plaats van par moet komen
		// otheredge: de pijl tussen par en othernode
		// parpar: de parent van par (rhetrel), een van de kinderen moet othernode worden
		// paredge: de pijl tussen parpar en par
		
		RhetRelation par = (RhetRelation) pe.getParent().next();
		
		MLabeledEdge edge = (MLabeledEdge) pe.getIncidentMEdges().next();
		String label = edge.getLabel();
		
		RSVertex othernode;
		
		if (label.equals("nucleus"))
			othernode = par.getSatellite();
		else
			othernode = par.getNucleus();
		
		MLabeledEdge otheredge = null;
		MLabeledEdge paredge = null;
		Iterator<?> it = par.getIncidentMEdges();
		while (it.hasNext())
		{
			MLabeledEdge e = (MLabeledEdge) it.next();
			RSVertex src = (RSVertex) e.getSource();
			RSVertex tgt = (RSVertex) e.getTarget();
			if (src.getType().equals("rhetrel") && tgt.getType().equals("rhetrel"))				
				paredge = e;
			if ((src.getType().equals("mess") || tgt.getType().equals("mess")) && !e.equals(edge))
				otheredge = e;
		}
		
		if (paredge != null)
		{				
			String paredgelabel = paredge.getLabel();
			
			RhetRelation parpar = (RhetRelation) par.getParent().next();
			
			if (paredgelabel.equals("nucleus"))
				setNucleus(parpar, othernode);
			else
				setSatellite(parpar, othernode);
			
			graph.removeMVertex(pe);		
			graph.removeMVertex(par);
			graph.removeMEdge(edge);
			graph.removeMEdge(otheredge);
		}
	}
	
	/*public boolean passiveForm(Message m)
	{
		String currtype = m.getType();
		String currname = m.getName();
		String curragens = m.getAgens();
		String currpatiens = m.getPatiens();
		
		//System.out.println("Passive form:");
		//System.out.println(type + " " + name + " " + agens + " " + patiens);
		//System.out.println(currtype + " " + currname + " " + curragens + " " + currpatiens);
				
		if (currtype.equals("action") && (currname.equals("hit") || currname.equals("kick")))
		{
			//System.out.println("het zou kunnen");
			if ((currpatiens.equals(agens) || currpatiens.equals(patiens))
					&& !curragens.equals(agens) && !curragens.equals(patiens))
			{
				//System.out.println("Ja, het is zo!");
				return true;
			}
		}
		
		return false;
	}*/
				
	/**
	 * Sets the nucleus of the rhetrelation to a new rsvertex
	 */
	private void setNucleus(RhetRelation rel, RSVertex tree)
	{
		boolean result = false;
		Iterator<?> it = rel.getIncidentOutMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			if (edge.getLabel().equals("nucleus"))
			{
				edge.setTarget(tree);
				result = true;
				break;
			}
		}
		if (!result)
		{
			RSEdge edge = new RSEdge("nucleus");
			edge.setSource(rel);
			edge.setTarget(tree);
			graph.addMEdge(edge);
		}
	}
	
	/**
	 * Sets the satellite of the rhetrelation to a new rsvertex
	 * @param rel
	 * @param tree
	 */
	private void setSatellite(RhetRelation rel, RSVertex tree)
	{		
		boolean result = false;
		Iterator<?> it = rel.getIncidentInMEdges();
		while (it.hasNext())
		{
			RSEdge edge = (RSEdge) it.next();
			if (edge.getLabel().equals("satellite"))
			{
				edge.setSource(tree);
				result = true;
				break;
			}
		}
		if (!result)
		{
			RSEdge edge = new RSEdge("satellite");
			edge.setSource(tree);
			edge.setTarget(rel);
			graph.addMEdge(edge);
		}
	}
	
	/**
	 * This method is needed when the story has been split into paragraphs. If a document plan
	 * has to be shown, the graph has to be destroyed afterwards. Therefore the graph has to
	 * be reconstructed, so in order to use the correct discourse history and entity history
	 * these histories are reset to the ones used at the beginning of the paragraph.
	 *
	 */
	public void reset()
	{
		hist = oldhist.getCopy();
		dischist = olddischist.getCopy();
	}
	
	/**
	 * This method is needed when the story has been split into paragraphs. If a document plan
	 * has to be shown, the graph has to be destroyed afterwards. Therefore the graph has to
	 * be reconstructed, so in order to use the correct discourse history and entity history
	 * the temporary variables are set to the current variables.
	 */	
	public void endParagraph()
	{
		oldhist = hist.getCopy();
		olddischist = dischist.getCopy();
	}	
}	
