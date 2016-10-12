package natlang.rdg.documentplanner;

import natlang.rdg.model.*;
import parlevink.parlegraph.model.*;

import java.net.*;
import java.util.*;

import jtp.*;
import jtp.context.owl.*;
import jtp.disp.*;
import jtp.fol.kif.*;
import jtp.fol.*;
import jtp.func.*;

/** 
 * InitialDocumentPlanBuilder is the first component of the DocumentPlanner and is 
 * responsible for the conversion of the fabula in OWL format into an initial document plan
 * 
 * This class is created for testing purposes, in order to check if the algorithm
 * correctly builds larger structures than the Plop example.
 *  
 * @author Nanda Slabbers 
 */
public class InitialDocPlanBuilderTester
{	
	private RSGraph graph;
	private Vector edges;
	private OWLReasoningContext context;
	private int type = 3;
	String prevtype;			
	Vector toldMessages;
		
	PlotElement dm0;
	PlotElement dm1;
	PlotElement dm2;
	PlotElement dm3;
	PlotElement dm4;
	PlotElement dm5;
	PlotElement dm6;
	PlotElement dm7;
	PlotElement dm8;
	PlotElement dm9;
	PlotElement dm10;
	PlotElement dm11;
	PlotElement dm12;
	PlotElement dm13;
	PlotElement dm14;
	PlotElement dm15;
	PlotElement dm16;
	PlotElement dm17;
	PlotElement dm18;
	PlotElement dm19;
	PlotElement dm20;
	PlotElement dm21;
	PlotElement dm22;
	PlotElement dm23;
	PlotElement dm24;
	PlotElement dm25;
	PlotElement dm26;
		
	/**
	 * Constructor - creates the OWL Reasoning context and reads the fabula 
	 */
	public InitialDocPlanBuilderTester()
	{
		if (type == 0)
		{
			dm0 = new PlotElement("setting", "be", "dwarf01", "", "", null);
			dm0.setTime(0);
			dm1 = new PlotElement("state", "hunger", "dwarf01", "", "", null);
			dm1.setTime(1);
			dm2 = new PlotElement("perception", "see", "dwarf01", "apple01", "", null);
			dm2.setTime(2);
			dm3 = new PlotElement("goal", "eat", "dwarf01", "apple01", "", null);
			dm3.setTime(3);
			dm4 = new PlotElement("action", "takefrom", "dwarf01", "apple01", "", null);
			dm4.setTime(4);
			dm5 = new PlotElement("action", "eat", "dwarf01", "apple01", "", null);
			dm5.setTime(5);
			dm6 = new PlotElement("state", "happy", "dwarf01", "", "", null);
			dm6.setTime(6);
		}
		else if (type == 1)
		{
			dm0 = new PlotElement("action", "eat0", "dwarf01", "apple01", "", null);			
			dm0.setTime(0);
			dm1 = new PlotElement("action", "eat1", "dwarf01", "apple01", "", null);			
			dm1.setTime(1);
			dm2 = new PlotElement("action", "eat2", "dwarf01", "apple01", "", null);			
			dm2.setTime(2);
			dm3 = new PlotElement("action", "eat3", "dwarf01", "apple01", "", null);			
			dm3.setTime(3);
			dm4 = new PlotElement("action", "eat4", "dwarf01", "apple01", "", null);			
			dm4.setTime(4);
			dm5 = new PlotElement("action", "eat5", "dwarf01", "apple01", "", null);			
			dm5.setTime(5);
			dm6 = new PlotElement("action", "eat6", "dwarf01", "apple01", "", null);			
			dm6.setTime(6);
			dm7 = new PlotElement("action", "eat7", "dwarf01", "apple01", "", null);			
			dm7.setTime(7);
			dm8 = new PlotElement("action", "eat8", "dwarf01", "apple01", "", null);			
			dm8.setTime(8);
			dm9 = new PlotElement("action", "eat9", "dwarf01", "apple01", "", null);			
			dm9.setTime(9);
			dm10 = new PlotElement("action", "eat10", "dwarf01", "apple01", "", null);			
			dm10.setTime(10);
			dm11 = new PlotElement("action", "eat11", "dwarf01", "apple01", "", null);			
			dm11.setTime(11);
			dm12 = new PlotElement("action", "eat12", "dwarf01", "apple01", "", null);			
			dm12.setTime(12);
			dm13 = new PlotElement("action", "eat13", "dwarf01", "apple01", "", null);			
			dm13.setTime(13);
			dm14 = new PlotElement("action", "eat14", "dwarf01", "apple01", "", null);			
			dm14.setTime(14);
			dm15 = new PlotElement("action", "eat15", "dwarf01", "apple01", "", null);			
			dm15.setTime(15);
			dm16 = new PlotElement("action", "eat16", "dwarf01", "apple01", "", null);			
			dm16.setTime(16);
			dm17 = new PlotElement("action", "eat17", "dwarf01", "apple01", "", null);			
			dm17.setTime(23);
			dm18 = new PlotElement("action", "eat18", "dwarf01", "apple01", "", null);			
			dm18.setTime(18);
			dm19 = new PlotElement("action", "eat19", "dwarf01", "apple01", "", null);			
			dm19.setTime(19);
			dm20 = new PlotElement("action", "eat20", "dwarf01", "apple01", "", null);			
			dm20.setTime(20);
			dm21 = new PlotElement("action", "eat21", "dwarf01", "apple01", "", null);			
			dm21.setTime(21);
			dm22 = new PlotElement("action", "eat22", "dwarf01", "apple01", "", null);			
			dm22.setTime(22);
			dm23 = new PlotElement("action", "eat23", "dwarf01", "apple01", "", null);			
			dm23.setTime(17);
			dm24 = new PlotElement("action", "eat24", "dwarf01", "apple01", "", null);			
			dm24.setTime(24);
			dm25 = new PlotElement("action", "eat25", "dwarf01", "apple01", "", null);			
			dm25.setTime(25);
			dm26 = new PlotElement("action", "eat26", "dwarf01", "apple01", "", null);			
			dm26.setTime(26);
		}
		else if (type == 2)
		{
			dm0 = new PlotElement("action", "eat0", "dwarf01", "apple01", "", null);			
			dm0.setTime(0);
			dm1 = new PlotElement("action", "eat1", "dwarf01", "apple01", "", null);			
			dm1.setTime(1);
			dm2 = new PlotElement("action", "eat2", "dwarf01", "apple01", "", null);			
			dm2.setTime(2);
			dm3 = new PlotElement("action", "eat3", "dwarf01", "apple01", "", null);			
			dm3.setTime(3);
			dm4 = new PlotElement("action", "eat4", "dwarf01", "apple01", "", null);			
			dm4.setTime(4);
			dm5 = new PlotElement("action", "eat5", "dwarf01", "apple01", "", null);			
			dm5.setTime(5);
			dm6 = new PlotElement("action", "eat6", "dwarf01", "apple01", "", null);			
			dm6.setTime(6);
			dm7 = new PlotElement("action", "eat7", "dwarf01", "apple01", "", null);			
			dm7.setTime(7);
			dm8 = new PlotElement("action", "eat8", "dwarf01", "apple01", "", null);			
			dm8.setTime(8);
			dm9 = new PlotElement("action", "eat9", "dwarf01", "apple01", "", null);			
			dm9.setTime(9);
			dm10 = new PlotElement("action", "eat10", "dwarf01", "apple01", "", null);			
			dm10.setTime(10);			
		}
		else if (type == 3)
		{
			dm0 = new PlotElement("setting", "be", "princess01", "", "", null, 0);	
			dm1 = new PlotElement("state", "inlove", "knight01", "", "princess01", null, 1);
			dm2 = new PlotElement("state", "inlove", "princess01", "", "prince01", null, 2);	
			dm3 = new PlotElement("state", "jealous", "knight01", "", "", null, 3);		
			dm4 = new PlotElement("goal", "kidnap", "knight01", "princess01", "", null, 4);	
			dm5 = new PlotElement("action", "goto", "knight01", "", "castle01", null, 5);
			dm6 = new PlotElement("action", "open", "knight01", "gate01", "", null, 6);
			dm6.setDescr("failed");
			dm7 = new PlotElement("action", "climb", "knight01", "", "tree01", null, 7);
			dm8 = new PlotElement("action", "jump", "knight01", "bedroom01", "", null, 8);
			dm9 = new PlotElement("state", "frightened", "princess01", "", "", null, 9);
			dm10 = new PlotElement("action", "scream", "princess01", "", "", null, 10);			
			dm11 = new PlotElement("event", "hear", "nobody", "princess01", "", null, 11);			
			dm12 = new PlotElement("action", "pickup", "knight01", "princess01", "", null, 12);		
			dm13 = new PlotElement("action", "puton", "knight01", "princess01", "horse01", null, 13);	
			dm14 = new PlotElement("action", "bring", "knight01", "princess01", "bridge01", null, 14);	
			dm15 = new PlotElement("perception", "see", "princess01", "prince01", "", null, 15);	
			dm16 = new PlotElement("state", "relieved", "princess01", "", "", null, 16);			
		}
		else
		{
			dm0 = new PlotElement("setting", "be", "dwarf01", "", "", null);
			dm0.setTime(0);
			dm1 = new PlotElement("state", "hungry", "dwarf01", "", "", null);
			dm1.setTime(1);			
			dm2 = new PlotElement("goal", "eat", "dwarf01", "apple01", "", null);
			dm2.setTime(2);
			dm3 = new PlotElement("action", "takefrom", "dwarf01", "apple01", "", null);
			dm3.setTime(3);
			dm4 = new PlotElement("action", "eat", "dwarf01", "apple01", "", null);
			dm4.setTime(4);
			dm5 = new PlotElement("state", "happy", "dwarf01", "", "", null);
			dm5.setTime(5);
			dm6 = new PlotElement("action", "laugh", "dwarf01", "", "", null);
			dm6.setTime(6);
		}

		toldMessages = new Vector();
		prevtype = "";
					
		try
		{
			// Create OWL Reasoning context
			context = new OWLReasoningContext();
			context.setUp();
			
			// Add unprovable reasoner
			Unprovable unpReasoner = new Unprovable();
			unpReasoner.setAskingReasoner(context.getAskingReasoner());
			DispatcherUtils.addToDispatcher(unpReasoner, context.getAskingDispatcher());
			
			// Add equals reasoner
			DispatcherUtils.addToDispatcher(new Equals(), context.getAskingDispatcher());
			DispatcherUtils.addToDispatcher(new InEqual(), context.getAskingDispatcher());
			
			// Add Get-setof reasoner
			GetSetof getsetofReasoner = new GetSetof();
			getsetofReasoner.setAskingReasoner(context.getAskingReasoner());
			DispatcherUtils.addToDispatcher(getsetofReasoner, context.getAskingDispatcher());
			
			// Add ForIn reasoner
			DispatcherUtils.addToDispatcher(new ForIn(), context.getAskingDispatcher());
				
			// retrieve the parser used for parsing KIF sentences
			KIF2CNF kifParser = context.getKifParser();
				
			// create namespace mappings for OWL, RDF and RDFS
			kifParser.addNamespaceMapping("owl", "http://www.w3.org/2002/07/owl#");
			kifParser.addNamespaceMapping("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			kifParser.addNamespaceMapping("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			kifParser.addNamespaceMapping("fabula", "http://www.owl-ontologies.com/FabulaKnowledge.owl#");
			kifParser.addNamespaceMapping("swc", "http://www.owl-ontologies.com/StoryWorldCore.owl#");
			
			kifParser.setCaseSensitiveSymbols(true);
					
			System.out.println("Voor fabula laden");
	
			if (type == 0)
			{
				context.tellKifString("(fabula:causes m1 m3)");
				context.tellKifString("(fabula:causes m2 m3)");
				context.tellKifString("(fabula:causes m3 m4)");
				context.tellKifString("(fabula:causes m3 m5)");
				context.tellKifString("(fabula:causes m5 m6)");
			}
			else if (type == 1)
			{
				context.tellKifString("(fabula:causes m1 m5)");
				context.tellKifString("(fabula:causes m2 m3)");
				context.tellKifString("(fabula:causes m3 m5)");
				context.tellKifString("(fabula:causes m4 m5)");
				context.tellKifString("(fabula:causes m5 m6)");
				context.tellKifString("(fabula:causes m6 m14)");
				context.tellKifString("(fabula:causes m7 m8)");
				context.tellKifString("(fabula:causes m8 m10)");
				context.tellKifString("(fabula:causes m9 m10)");
				context.tellKifString("(fabula:causes m10 m12)");
				context.tellKifString("(fabula:causes m11 m13)");
				context.tellKifString("(fabula:causes m12 m13)");
				context.tellKifString("(fabula:causes m13 m14)");
				context.tellKifString("(fabula:causes m14 m15)");
				context.tellKifString("(fabula:causes m15 m16)");
				context.tellKifString("(fabula:causes m16 m18)");
				context.tellKifString("(fabula:causes m16 m19)");
				context.tellKifString("(fabula:causes m15 m17)");
				context.tellKifString("(fabula:causes m17 m20)");
				context.tellKifString("(fabula:causes m20 m21)");
				context.tellKifString("(fabula:causes m17 m24)");
				//context.tellKifString("(fabula:causes m22 m23)");
				context.tellKifString("(fabula:causes m23 m24)");
				context.tellKifString("(fabula:causes m24 m25)");
				context.tellKifString("(fabula:causes m24 m26)");
			}
			else if (type == 2)
			{
				context.tellKifString("(fabula:causes m1 m2)");
				context.tellKifString("(fabula:causes m2 m3)");
				context.tellKifString("(fabula:causes m3 m4)");
				context.tellKifString("(fabula:causes m4 m5)");
				context.tellKifString("(fabula:causes m5 m6)");
				context.tellKifString("(fabula:causes m6 m7)");
				context.tellKifString("(fabula:causes m7 m8)");
				context.tellKifString("(fabula:causes m8 m9)");
				context.tellKifString("(fabula:causes m9 m10)");
			}
			else if (type == 3)
			{
				context.tellKifString("(fabula:causes m1 m3)");
				context.tellKifString("(fabula:causes m2 m3)");
				context.tellKifString("(fabula:causes m3 m4)");
				context.tellKifString("(fabula:causes m4 m5)");
				context.tellKifString("(fabula:causes m4 m6)");
				context.tellKifString("(fabula:causes m6 m7)");
				context.tellKifString("(fabula:causes m6 m8)");
				context.tellKifString("(fabula:causes m8 m9)");
				context.tellKifString("(fabula:causes m9 m10)");
				context.tellKifString("(fabula:causes m10 m11)");
				context.tellKifString("(fabula:causes m6 m12)");
				context.tellKifString("(fabula:causes m6 m13)");
				context.tellKifString("(fabula:causes m6 m14)");
				context.tellKifString("(fabula:causes m14 m15)");
				context.tellKifString("(fabula:causes m15 m16)");
			}
			else 
			{
				context.tellKifString("(fabula:causes m1 m2)");
				context.tellKifString("(fabula:causes m2 m3)");
				context.tellKifString("(fabula:causes m2 m4)");
				context.tellKifString("(fabula:causes m3 m5)");
				context.tellKifString("(fabula:causes m4 m5)");
				context.tellKifString("(fabula:causes m5 m6)");
			}
			
			System.out.println("Na fabula laden");
		}
		catch (Exception e) 
		{
			System.out.println("error initializing fabula");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
		
	/**
	 * Starts the process of transforming the fabula into an initial document plan
	 * @return whether the transformation has ended successfully 
	 */
	public boolean transform()
	{		
		toldMessages = new Vector();
		edges = new Vector();	
			
		RSVertex rr = createRhetRel("temporal", createRhetRel("temp-once", dm0), createTreeOwl("m1", "", null, true));
				
		graph = new RSGraph((RhetRelation) rr);
		
		for (int i=0; i<edges.size(); i++)
		{			
			MLabeledEdge tmp = (MLabeledEdge) edges.elementAt(i);
			graph.addMEdge(tmp);
		}
		
		return true;
	}	
			
	/**
	 * Creates RhetRelation node with two child nodes
	 * @param rel kind of relation
	 * @param vert1 satellite
	 * @param vert2 nucleus
	 * @return the RhetRelation node of type rel and with children vert1 and vert2
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
					
		edges.addElement(sat);
		edges.addElement(nuc);
		
		return rhetrel;
	}
	
	/**
	 * Creates RhetRelation node with only one child node
	 * @param rel kind of relation
	 * @param vert1 the nucleus
	 * @return the RhetRelation node of type rel and with child vert1
	 */
	public RhetRelation createRhetRel(String rel, RSVertex vert1)
	{
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert1);
									
		edges.addElement(nuc);
		
		return rhetrel;
	}
	
	/**
	 * Returns the document plan
	 * @return document plan
	 */
	public RSGraph getGraph()
	{
		return graph;
	}
	
	/**
	 * Sets the graph (document plan)
	 * @param g 
	 */
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	/**
	 * Resets the document plan 
	 *
	 */
	public void setGraph()
	{
		graph = null;
	}

	public RSVertex createTreeOwl(String curr, String prev, RSVertex v, boolean forward)
	{		
		System.out.println("CreateTree: " + curr);
		
		if (alreadyTold(curr))
		{
			System.out.println("ALREADY TOLD!");
			return null;
		}
		
		toldMessages.add(curr);
		
		Vector nextnodes = new Vector();
		Vector prevnodes = new Vector();
		
		try
		{
			// find next nodes
			ReasoningStepIterator rsi = context.ask("(fabula:causes " + curr + " ?x)");
			ReasoningStep rs = null;	
			while ((rs = rsi.next()) != null)
			{
				Literal lit = SubstUtils.deReferenceLiteral((Literal) rs.getGoal());
				String n = lit.getArgs().get(1).toString();
				Symbol s = (Symbol) lit.getArgs().get(1);
				n = s.getName();
				if (!alreadyTold(n))
					insert(nextnodes, n);		
			}
			
			// find previous nodes
			rsi = context.ask("(fabula:causes ?x " + curr + ")");
			rs = null;	
			while ((rs = rsi.next()) != null)
			{
				Literal lit = SubstUtils.deReferenceLiteral((Literal) rs.getGoal());
				String n = lit.getArgs().get(0).toString();
				if (!alreadyTold(n))
					insert(prevnodes, n);						
			}
			
			System.out.println("nieuw aantal next nodes in " + curr + ": " + nextnodes.size());
			System.out.println("nieuw aantal prev nodes in " + curr + ": " + prevnodes.size());
						
			RSVertex result = v;
			
			if (forward)
			{
				if (prevnodes.size() == 0)
				{
					// do nothing at all
					System.out.println("prevnodes 0 van: " + curr);
				}
				else if (prevnodes.size() == 1)
				{
					System.out.println("prevnodes 1 van: " + curr);
					if (result != null)
					{
						System.out.println("result niet null, dus recursief aanroepen");
						// node is added to the document plan based on the time arguments 
						if (precedes((String) prevnodes.elementAt(0), result))
						{
							System.out.println("precedes");
							RSVertex tree = createTreeOwl((String) prevnodes.elementAt(0), curr, null, false);
							if (tree != null)
								result = createRhetRel("additive", tree, result);
						}
						else
						{
							System.out.println("niet precedes");
							RSVertex tree = createTreeOwl((String) prevnodes.elementAt(0), curr, null, false);
							if (tree != null)
								result = createRhetRel("additive", result, tree);
						}
					}
					else
						result = createTreeOwl((String) prevnodes.elementAt(0), curr, null, false);
				}
				else if (prevnodes.size() > 1)
				{
					System.out.println("prevnodes > 1 van: " + curr);
					RSVertex tmpresult = null;
					for (int i=prevnodes.size()-2; i>=0; i--)
					{
						if (i==prevnodes.size()-2)
							tmpresult = createRhetRel("additive", createTreeOwl((String) prevnodes.elementAt(i), curr, null, false), createTreeOwl((String) prevnodes.elementAt(i+1), curr, null, false));
						else
							tmpresult = createRhetRel("additive", createTreeOwl((String) prevnodes.elementAt(i), curr, null, false), tmpresult);
					}
					if (result != null)
						result = createRhetRel("additive", result, tmpresult);
					else
						result = tmpresult;
				}
							
				if (nextnodes.size() == 0)
				{					
					System.out.println("nextnodes 0 van: " + curr);
					if (result != null)
					{
						String rel = getRelation(prev, curr);
						result = createRhetRel(rel, result, createPlotElement(curr));
					}						
					else
						result = createPlotElement(curr);
				}				
				else if (nextnodes.size() == 1)
				{			
					System.out.println("nextnodes 1 van: " + curr);
					if (result != null)
					{
						String rel = getRelation(prev, curr);
						result = createRhetRel(rel, result, createPlotElement(curr));
						result = createTreeOwl((String) nextnodes.elementAt(0), curr, result, true);
					}
					else
						result = createTreeOwl((String) nextnodes.elementAt(0), curr, createPlotElement(curr), true);
				}				
				else if (nextnodes.size() > 1)
				{
					System.out.println("nextnodes >1 van: " + curr);
					RSVertex tmpresult = null;
					for (int i=nextnodes.size()-2; i>=0; i--)
					{
						if (i==nextnodes.size()-2)
						{
							RSVertex tree1 = createTreeOwl((String) nextnodes.elementAt(i), curr, null, true);
							RSVertex tree2 = createTreeOwl((String) nextnodes.elementAt(i+1), curr, null, true);
							if (tree1 != null && tree2 != null)
								tmpresult = createRhetRel("temp-after-sequence", tree1, tree2);
							else if (tree1 == null)
								tmpresult = tree2;
							else if (tree2 == null)
								tmpresult = tree1;
						}
						else
						{
							RSVertex tree = createTreeOwl((String) nextnodes.elementAt(i), curr, null, true);
							if (tree != null)
								tmpresult = createRhetRel("temp-after-sequence", tree, tmpresult);
						}
					}
	
					if (result != null)
					{
						String rel = getRelation(prev, curr);
						result = createRhetRel(rel, result, createPlotElement(curr));
						String rel2 = getRelation(curr, (String) nextnodes.elementAt(0)); 
						result = createRhetRel(rel2, result, tmpresult);
					}
					else
					{
						String rel = getRelation(curr, (String) nextnodes.elementAt(0)); 
						result = createRhetRel(rel, createPlotElement(curr), tmpresult);
					}
				}			
			}
			else		// if backwards
			{			// misschien hier ook extra checks zoals bij het genereren van een complete boom, dat die niet null is...

				System.out.println("backwards: " + curr);
				RSVertex prevs = null;
				RSVertex nexts = null;
							
				// the new previous nodes are used to create all incoming branches
				if (prevnodes.size() == 1)
					prevs = createTreeOwl((String) prevnodes.elementAt(0), curr, null, false);
				else if (prevnodes.size() > 1)
				{
					for (int i=prevnodes.size()-2; i>=0; i--)
					{
						if (i==prevnodes.size()-2)
							prevs = createRhetRel("additive", createTreeOwl((String) prevnodes.elementAt(i), curr, null, false), createTreeOwl((String) prevnodes.elementAt(i+1), curr, null, false));
						else
							prevs = createRhetRel("additive", createTreeOwl((String) prevnodes.elementAt(i), curr, null, false), prevs);
					}
				}
				
				// the new next nodes are used to create all outgoing branches
				if (nextnodes.size() == 1)
					nexts = createTreeOwl((String) nextnodes.elementAt(0), curr, null, true);
				else if (nextnodes.size() > 1)
				{
					for (int i=nextnodes.size()-2; i>=0; i--)
					{
						if (i==nextnodes.size()-2)
							nexts = createRhetRel("temp-after-sequence", createTreeOwl((String) nextnodes.elementAt(i), curr, null, true), createTreeOwl((String) nextnodes.elementAt(i+1), curr, null, true));
						else
							nexts = createRhetRel("temp-after-sequence", createTreeOwl((String) nextnodes.elementAt(i), curr, null, true), nexts);
					}
				}
				
				// then the sofar generated trees are connected in the correct order 
				if (prevs != null)
				{
					String rel = getRelation((String) prevnodes.elementAt(prevnodes.size()-1), curr); 
					result = createRhetRel(rel, prevs, createPlotElement(curr));
				}
				if (nexts != null)
				{
					String rel = getRelation(curr, (String) nextnodes.elementAt(0));
					if (result != null)
						result = createRhetRel(rel, result, nexts);
					else
						result = createRhetRel(rel, createPlotElement(curr), nexts);
				}				
				if (result == null)
					result = createPlotElement(curr);
			}
				
			return result;				
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage() + " - " + e.getClass());
			System.out.println(curr);
			System.out.println("error querying knowledge base");
		}
									
		return null;
	}
	
	
	public PlotElement createPlotElement(String plotel)
	{
		PlotElement result = getMessage(plotel);
		//result.setName("eat");
		return result;
	}
	
	public void insert(Vector v, String n)
	{
		int newtime = Integer.parseInt(n.substring(1));
		
		int idx = 0;
		while (idx < v.size())
		{
			int oldtime = Integer.parseInt(((String) v.elementAt(idx)).substring(1));
			if (newtime < oldtime)
				break;
			else if (newtime > oldtime)
				idx++;
		}
		v.add(idx, n);
		return;
	}
	
	//for testing purposes (if fromfile is set to false)
	public PlotElement getMessage(String nm)
	{
		if (nm.equals("m0"))
			return dm0;
		if (nm.equals("m1"))
			return dm1;
		if (nm.equals("m2"))
			return dm2;
		if (nm.equals("m3"))
			return dm3;
		if (nm.equals("m4"))
			return dm4;
		if (nm.equals("m5"))
			return dm5;
		if (nm.equals("m6"))
			return dm6;
		if (nm.equals("m7"))
			return dm7;
		if (nm.equals("m8"))
			return dm8;
		if (nm.equals("m9"))
			return dm9;
		if (nm.equals("m10"))
			return dm10;
		if (nm.equals("m11"))
			return dm11;
		if (nm.equals("m12"))
			return dm12;
		if (nm.equals("m13"))
			return dm13;
		if (nm.equals("m14"))
			return dm14;
		if (nm.equals("m15"))
			return dm15;
		if (nm.equals("m16"))
			return dm16;
		if (nm.equals("m17"))
			return dm17;
		if (nm.equals("m18"))
			return dm18;
		if (nm.equals("m19"))
			return dm19;
		if (nm.equals("m20"))
			return dm20;
		if (nm.equals("m21"))
			return dm21;
		if (nm.equals("m22"))
			return dm22;
		if (nm.equals("m23"))
			return dm23;
		if (nm.equals("m24"))
			return dm24;
		if (nm.equals("m25"))
			return dm25;
		if (nm.equals("m26"))
			return dm26;
		return null;
	}
	
	public String getRelation(String m1, String m2)
	{
		try
		{
			ReasoningStepIterator rsi = context.ask("(holds ?rel " + m1 + " " + m2 + ")");
			ReasoningStep rs = null;	
			
			while ((rs = rsi.next()) != null)
			{
				Literal lit = SubstUtils.deReferenceLiteral((Literal) rs.getGoal());				
				int tmp = lit.getArgs().get(0).toString().lastIndexOf(':') + 1;

				if ((lit.getArgs().get(0).toString().substring(tmp).equals("causes")))					
					return "cause";
			}
		}
		catch (Exception e)
		{
			System.out.println("error querying knowledge base");
		}		
		
		// if no relation is specified: return temporal
		return "temporal";
	}
	
	public boolean precedes(String n, RSVertex v)
	{
		System.out.println("precedes: " + n);
		int t = getMessage(n).getTime();
		
		System.out.println("t: " + t);
		
		int t1 = getRightmostTime(v);
		int t2 = getLeftmostTime(v);
		
		System.out.println("t1: " + t1);
		System.out.println("t2: " + t2);
		
		if (t < t1 && t < t2)
			return true;
		
		return false;
	}
	
	/**
	 * Gets the time of the rightmost plot element in v
	 * @param v
	 * @return the time
	 */
	public int getRightmostTime(RSVertex v)
	{
		if (v == null)
			return -1;
		if (v.getType().equals("mess"))
			return ((PlotElement) v).getTime();
		else if (v.getType().equals(("rhetrel")))
			getRightmostTime(((RhetRelation) v).getNucleus());
		return -1;
	}
	
	/**
	 * Gets the time of the leftmost plot element in v
	 * @param v
	 * @return the time
	 */
	public int getLeftmostTime(RSVertex v)
	{
		if (v == null)
			return -1;
		if (v.getType().equals("mess"))
			return ((PlotElement) v).getTime(); 
		else if (v.getType().equals(("rhetrel")))
			getLeftmostTime(((RhetRelation) v).getNucleus());
		return -1;
	}
	
	public boolean alreadyTold(String m)
	{
		for (int i=0; i<toldMessages.size(); i++)
		{
			if (m.equals((String) toldMessages.elementAt(i)))
				return true;
		}
		return false;
	}
}