package natlang.rdg.surfacerealizer;

import jtp.*;
import jtp.context.owl.*;
import jtp.disp.*;
import jtp.fol.*;
import jtp.fol.kif.*;
import jtp.func.*;

import java.util.*;
import java.io.BufferedReader;

import parlevink.util.Resources;

/**
 * This module is responsible for applying inference rules with the generation of referring
 * expressions. It uses a JTP reasoning context for the reasoning.
 * 
 * @author Nanda Slabbers
 *
 */
public class BackupOfInferenceModule 
{
	private OWLReasoningContext context;
	
	public static final int NOVANDEF = 0;		//de poort
	public static final int NOVANINDEF = 1;		//een pagina
	public static final int VANDEF = 2;			//de slaapkamer van de prinses
	public static final int VANINDEF = 3;		//een been van de prinses
		
	/**
	 * Constructor, initializes the reasoning context and reads the rules into the context
	 *
	 */
	public BackupOfInferenceModule()
	{
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
			kifParser.setCaseSensitiveSymbols(true);
					
			System.out.println("Voor inference rules laden");

			// reading the rules and adding them to the reasoner one by one
			Resources rs = new Resources(this);
			BufferedReader reader = rs.getReader("rules.txt");
			
			while (true)
			{
				String str = reader.readLine();
								
				if (str == null)
					break;
				
				context.tellKifString(str);
			}
			
			System.out.println("Na inference rules laden");
		}
		catch (Exception e) 
		{
			System.out.println("error initializing inference rules");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}	
	
	/**
	 * Checks whether a bridging description can be used and returns the kind of
	 * expression to be generated (using a definite or indefinite article and
	 * including the van-part or not)
	 * @param e1 the entity e2 is related to (e.g. castle01)
	 * @param e2 the entity that is related to e1 (e.g. gate01)
	 * @param c2 the concept of e2
	 * @param ents the vector with all entities mentioned sofar
	 */
	public int chooseKindOfExpression(String e1, String e2, String c2, Vector ents)
	{
		System.out.println("choose kind of expression: " + e1 + " " + e2 + " (" + c2 + ") ");
		// global algorithm:
		
		//if van-part specified and it is a necessary or probable part
			//if there is only 1 salient entity that can have a c1
				//drop van-part:
				//if a c2 has exactly one c1
					//de poort
				//else
					//een poort
			//else 
				//include van-part:
				//if a c2 has exactly one c1
					//de slaapkamer van de prinses
				//else
					//een slaapkamer van de prinses
		//else
			//include van-part:
			//een poort van de dierentuin
		
		// later the choice of determiner should be done based on the storyworld
		// module, but this module is still in progress
							
		if (storedRule(e1, c2))
		{
			if (onlyEntity(e1, c2, ents))		
			{
				if (exactlyOne(e1, c2))
					return NOVANDEF;
				else
					return NOVANINDEF;
			}
			else
			{
				if (exactlyOne(e1, c2))
					return VANDEF;
				else
					return VANINDEF;
			}
		}
		return VANINDEF;
	}
			
	/**
	 * Checks whether a rule has been stored that a certain object usually has another
	 * object (such as checking whether a castle usually has a gate)  
	 * @param ent
	 * @param con
	 */
	public boolean storedRule(String ent, String con)
	{	
		Vector tmps = new Vector();
		
		try
		{
			ReasoningStepIterator rsi = context.ask("(has " + ent + " ?x)");
			ReasoningStep rs = null;	
			while ((rs = rsi.next()) != null)
			{				
				Literal lit = SubstUtils.deReferenceLiteral((Literal) rs.getGoal());
				if (!lit.getArgs().get(1).getClass().getName().equals("jtp.fol.Symbol"))
					tmps.add(lit.getArgs().get(1).toString());						
			}
						
			for (int i=0; i<tmps.size(); i++)
			{
				String tmp = (String) tmps.elementAt(i);				
				rsi = context.ask("(type " + con + " " + tmp + ")");
				if (rsi.next() != null)
					return true;
			}			
		}
		catch (Exception e)
		{
			System.out.println("error querying knowledge base");
		}
							
		return false;
	}
	
	/**
	 * Checks whether this is the only entity that can have a certain object
	 * (for example whether it is the only object that can have a gate)
	 * @param ent
	 * @param con
	 * @param ents
	 */
	public boolean onlyEntity(String ent, String con, Vector ents)
	{
		// zoek alle entiteiten die een 'poort' (kunnen) hebben
		// als castle de enige is EN er is maar 1 castle, dan true
		
		// of makkelijker: kijk voor alle salient entiteiten of ze een gate kunnen hebben
		// als dit maar 1 entiteit is: return true
					
		Vector posents = new Vector();
															
		for (int i=0; i<ents.size(); i++)
		{	
			String tmpent = (String) ents.elementAt(i);
			if (storedRule(tmpent, con))
				posents.add(tmpent);
		}
																
		if (posents.size() == 1)
			return true;	
				
		return false;
	}
	
	/**
	 * Check if a certain object has exactly one other object (such as whether 
	 * a castle has exactly one gate, or a book having more than one page)
	 * @param ent1
	 * @param con2
	 */
	public boolean exactlyOne(String ent1, String con2)
	{
		try
		{
			ReasoningStepIterator rsi = context.ask("(one " + ent1 + " " + con2 + ")");
			if (rsi.next() != null)
				return true;
		}
		catch (Exception e)
		{
			System.out.println("error querying knowledge base");
		}
						
		return false;
	}	
	
	/**
	 * Checks whether an entity is unique, such as the sun
	 * @param con
	 */
	public boolean unique(String con)
	{
		try
		{
			ReasoningStepIterator rsi = context.ask("(one " + con + ")");			
			if (rsi.next() != null)
				return true;
		}
		catch (Exception e)
		{
			System.out.println("error querying knowledge base");
		}
					
		return false;
	}
	
	/**
	 * Gets all possible concepts, so starst by the most specific concept and
	 * uses the subsumption hierarchy in order to get the more general concepts as well.
	 * @param ent
	 */
	public Vector getPossibleConcepts(String ent)
	{
		Vector result = new Vector();
						
		try
		{
			ReasoningStepIterator rsi = context.ask("(type ?x " + ent + ")");		
			ReasoningStep rs = null;
			
			while ((rs = rsi.next()) != null)
			{
				Literal lit = SubstUtils.deReferenceLiteral((Literal) rs.getGoal());
				String tmp = lit.getArgs().get(0).toString();
				if (!contains(result, tmp))
					result.add(tmp);
			}			
		}
		catch (Exception e) 
		{
			System.out.println("error querying inference rules");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean contains(Vector v, String s)
	{
		for (int i=0; i<v.size(); i++)
			if (((String) v.elementAt(i)).equals(s))
				return true;
		
		return false;
	}
	
	/**
	 * Adds a new rule to the reasoning context 
	 * @param concept
	 * @param entity
	 */
	public void tell(String concept, String entity)
	{
		try
		{
			context.tellKifString("(type " + concept + " " + entity + ")");
		}
		catch (Exception e)
		{
			System.out.println("error querying knowledge base");
		}
	}	
}
