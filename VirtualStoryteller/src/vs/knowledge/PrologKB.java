/* Copyright (C) 2008 Human Media Interaction - University of Twente
 * 
 * This file is part of The Virtual Storyteller.
 * 
 * The Virtual Storyteller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Virtual Storyteller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with The Virtual Storyteller. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package vs.knowledge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jpl.Atom;
import jpl.Compound;
import jpl.JPLException;
import jpl.Query;
import jpl.Term;
import jpl.Util;
import jpl.Variable;
import vs.characteragent.JustifiableGoalSchema;
import vs.communication.GoalSchema;
import vs.communication.Operator;
import vs.communication.RDFtriple;
import vs.communication.Schema;
import vs.communication.StoryEvent;
import vs.debug.LogFactory;
import vs.fabula.FabulaFactory;
import vs.poplanner.PlanLink;
import vs.poplanner.PlanOrdering;
import vs.poplanner.PlanStep;
import vs.rationalagent.RationalAgent;

/**
 * Contains all methods that have to call Prolog for their functionality. Do _NOT_ use any call to Prolog
 * anywhere else than here. The idea is that this class provides _the_ interface to Prolog. 
 * 
 * @author swartjes 
 */
public class PrologKB {

	// Modules
	protected static final String rdf_db 				= "rdf_db:";
	protected static final String schema_management 	= ""; // "schema_management:";
	protected static final String thread_management 	= ""; // "thread_management:";
	protected static final String goal_management 		= ""; // "goal_manager:";
	protected static final String fabula		 		= ""; // "fabula:";
	protected static final String reactive_layer 		= ""; // "reactive_layer:";
	protected static final String planner 				= "planner:";
	protected static final String knowledgebase 		= ""; // "knowledgebase:";
	protected static final String narrator 				= ""; // "narrator:";
	protected static final String narrative_inspiration = ""; // "narrative_inspiration:";
	protected static final String characterAgent 		= ""; // "basicCharacterAgent:";
	protected static final String worldAgent 			= ""; // "basicWorldAgent:";
	protected static final String plotAgent 			= ""; // "basicPlotAgent:";
	
	// TODO: Below predicates should all be made protected. Other classes should not be able to reference them.
	
	// -------------------------
	// General prolog predicates
	// -------------------------
	protected static final String consult 	= "consult";
	public static final String member 		= "member";
	protected static final String rdf_load 	= PrologKB.rdf_db + "rdf_load";
	public static final String length 		= "length";
	
	// -----------------------	
	// Character agent module 
	// -----------------------
	protected static final String setAgentID 			= characterAgent + "setAgentID";
	protected static final String hasAction 			= characterAgent + "hasAction";
	protected static final String canDo 				= characterAgent + "canDo";
	protected static final String goal_intention		= characterAgent + "goal_intention";
	//JB
	protected static final String getMotivations		= characterAgent + "character_motivations";
	protected static final String getMotivationStrength	= characterAgent + "motivation_strength";
	protected static final String getInterestDesire		= characterAgent + "interest_desire";
	protected static final String setDesire				= characterAgent + "set_interest_desire";
	
	//predicates needed for HumanCharacterAgent interface 
	protected static final String possible_action  		= characterAgent + "possible_action";
	protected static final String possible_transitmove_action = characterAgent + "possible_transitmove_action";
	
	// -----------------------	
	// World agent module 
	// -----------------------
	protected static final String check_schema_facts 	= worldAgent + "check_schema_facts";
	protected static final String resource_conflict		= worldAgent + "resource_conflict";
	
	// -----------------------	
	// Plot agent module 
	// -----------------------
	
	
	// ------------------------
	// Schema management module
	// ------------------------
	protected static final String applyOperatorEffects 		= schema_management + "apply_operator_effects";
	protected static final String getSchemaPreconditions    = schema_management + "schema_preconditions";
	protected static final String getSchemaKind				= schema_management + "schema_kind";
	protected static final String getOperatorType 			= schema_management + "schema_type";
	protected static final String getOperatorAgens 			= schema_management + "schema_agens";
	protected static final String getOperatorPatiens 		= schema_management + "schema_patiens";
	protected static final String getOperatorTarget 		= schema_management + "schema_target";
	protected static final String getOperatorOpponent		= schema_management + "schema_opponent";
	protected static final String getOperatorInstrument 	= schema_management + "schema_instrument";
	protected static final String getOperatorDuration		= schema_management + "operator_duration";
	protected static final String operator_effect 			= schema_management + "operator_effect";
	protected static final String getGoalUrgency 			= schema_management + "goal_urgency";

	//JB extract associated motivations from goal schema 
	protected static final String getGoalPosMotivationList	= schema_management + "goal_pos_motivation_list";
	protected static final String getGoalNegMotivationList	= schema_management + "goal_neg_motivation_list";
	protected static final String getGoalDramaticChoice		= schema_management + "goal_dramatic_choice";

	protected static final String getFramingScopeAll		= schema_management + "framing_scope_all";
	protected static final String getFramingScopePersonal	= schema_management + "framing_scope_personal";
	protected static final String getFramingScopeHidden		= schema_management + "framing_scope_hidden";
	protected static final String validateSchema 			= schema_management + "validate_schema";
	protected static final String validateGoalFailure		= schema_management + "validate_goal_failure_conditions";
	protected static final String select_action_from_rule	= schema_management + "action_selection";
	protected static final String select_goal_from_rule		= schema_management + "goal_selection";
	
	// ---------------------
	// Knowledge base module
	// ---------------------
	protected static final String rdfAssert 	= knowledgebase + "rdfAssert";
	protected static final String rdfRetract 	= knowledgebase + "rdfRetract";
	protected static final String query			= knowledgebase + "query";
	protected static final String first 		= knowledgebase + "first";
	protected static final String second 		= knowledgebase + "second";
	protected static final String getSubject 	= knowledgebase + "getSubject";
	protected static final String getPredicate 	= knowledgebase + "getPredicate";
	protected static final String getObject 	= knowledgebase + "getObject";

	// ----------------------------
	// Fabula module
	// ----------------------------
	protected static final String schema_enablement			= fabula + "schema_enablement";
	protected static final String schema_motivation			= fabula + "schema_motivation";
	protected static final String goal_success_cause		= fabula + "goal_success_cause";
	protected static final String goal_failure_cause		= fabula + "goal_failure_cause";
	protected static final String setting_enables_event 	= fabula + "setting_enables_event";
	protected static final String action_rule_cause 		= fabula + "action_rule_cause";
	protected static final String goal_rule_cause 			= fabula + "goal_rule_cause";
	protected static final String internal_element_cause	= fabula + "internal_element_cause";
	
	// ----------------------------
	// Narrative inspiration module
	// ----------------------------
	public static final String getSuggestion 			= narrative_inspiration + "getSuggestion";
	public static final String getSuggestionName 		= narrative_inspiration + "getSuggestionName";
	public static final String getSuggestionIndividual	= narrative_inspiration + "getSuggestionIndividual";
	public static final String getSuggestionType 		= narrative_inspiration + "getSuggestionType";
	public static final String getSuggestionCausers 	= narrative_inspiration + "getSuggestionCausers";
	public static final String getSuggestionBody 		= narrative_inspiration + "getSuggestionBody";
	public static final String nodeClass 				= narrative_inspiration + "nodeClass";
	public static final String causalityClass 			= narrative_inspiration + "causalityClass";
	public static final String fabulaNode 				= narrative_inspiration + "fabulaNode";
	public static final String fabulaCause 				= narrative_inspiration + "fabulaCause";
	public static final String getFabulaCharacter 		= narrative_inspiration + "getFabulaCharacter";
	public static final String getFabulaContents 		= narrative_inspiration + "getFabulaContents";
	public static final String getFabulaContentTruth 	= narrative_inspiration + "getFabulaContentTruth";
	public static final String createValidatedEvent 	= narrative_inspiration + "createValidatedEvent";
	public static final String createValidatedAction 	= narrative_inspiration + "createValidatedAction";

	// -----------------------------
	// Partial order planning module
	// -----------------------------
	protected static final String plan							= planner + "plan";
	protected static final String adaptPlan						= planner + "adapt_plan";
	protected static final String invalidatesPlan				= planner + "invalidates_plan";
	protected static final String invalidPlan					= planner + "invalid_plan";
	protected static final String executableOperator			= planner + "executableOperator";
	protected static final String executableFramingOperator		= planner + "executableImprovisation";
	protected static final String executableInferenceOperator	= planner + "executableInference";
	protected static final String executableInternalElementOperator	= planner + "executableInternalElement";	
	protected static final String executableEvent				= planner + "executableEvent";
	protected static final String finishedPlan					= planner + "finished_plan";
	protected static final String planStep 						= planner + "planStep";
	protected static final String planOrdering 					= planner + "planOrdering";
	protected static final String planLink 						= planner + "planLink";
	protected static final String planLinkFrom 					= planner + "planLinkFrom";
	protected static final String planLinkTo 					= planner + "planLinkTo";
	protected static final String planLinkCond 					= planner + "planLinkCond";
	
	// -------------------------
	// Episode management module
	// -------------------------
	protected static String possibleThread 			= thread_management + "possible_thread";
//	protected static String startEpisode 			= episode_management + "start_episode";
	protected static String necessaryCharacter	 	= thread_management + "necessary_character";
//	protected static String castedCharacter 		= episode_management + "casted_character";
	protected static String threadGoal 				= thread_management + "thread_goal";
	protected static String threadResolveGoal 		= thread_management + "thread_resolve_goal";	
	protected static String threadSetting 			= thread_management + "thread_setting";
	protected static String condition_to_triples	= thread_management + "condition_to_triples";
	
	// ----------------------
	// Goal management module
	// ----------------------
	protected static final String goal_rules_exist			= goal_management + "goal_rules_defined";
	protected static final String select_goal_rule			= goal_management + "select_goal_rule";
	protected static final String possible_goal				= goal_management + "possible_goal";
	protected static final String adopt_goal				= goal_management + "adopt_goal";
	protected static final String drop_goal					= goal_management + "drop_goal";
	protected static final String adopt_justifiable_goal	= goal_management + "adopt_justifiable_goal";
	protected static final String adopted_goal				= goal_management + "adopted_goal";
	protected static final String adopted_justifiable_goal	= goal_management + "adopted_justifiable_goal";
	protected static final String suggest_goal				= goal_management + "suggest_goal";
	protected static final String suggested_goal			= goal_management + "suggested_goal";
	protected static final String assert_causal_anchor		= goal_management + "assert_causal_anchor";
	protected static final String possible_goal_after_plan  = goal_management + "possible_justifiable_goal";
	
	// ---------------------
	// Reactive layer module
	// ---------------------
	protected static final String reactive_action_rule	= reactive_layer + "select_action_rule";
	
	// -------------------------
	// Narrator module
	// -------------------------
	protected static String narrate 			= narrator + "narrate";
	protected static String narrate_imperative 	= narrator + "narrate_imperative";
	protected static String narrate_category 	= narrator + "narrate_category";

	protected static String narrate_object  	= narrator + "object_template";
	
	protected static PrologKB M_KNOWLEDGEMANAGER = null;
	
	protected final Logger logger;	
	
	/**
	 * Singleton pattern's private constructor
	 */
	private PrologKB() {

		// Initialize logger
		logger = LogFactory.getLogger(this);

	}

	public static String addQuotes(String input) {
		StringBuilder sb = new StringBuilder().append('\'').append(input)
				.append('\'');
		return sb.toString(); //"'" + input + "'";
	}
	
	// Returns the answer that Prolog returns without quotes and if the answer
	// is "none" or "" the result is set to null
	public String getQNPF(String command, String action) {
		String output = removeQuotes(
				getPrologSingleVariable(command, action));
		if (output != null) {
			if (output.contentEquals("none") || output.contentEquals("")) {
				output = null;
			}
		}
		return output;
	}
	
	/**
	 * Substring from '#'
	 * @param input a String containing a '#'
	 * @return the string after the first '#'
	 */
	public static String fromNrSign(String input) {
		return input.substring(input.indexOf("#") + 1);
	}

	public static PrologKB getInstance() {
		if (M_KNOWLEDGEMANAGER == null) {
			M_KNOWLEDGEMANAGER = new PrologKB();
		}
		return M_KNOWLEDGEMANAGER;
	}
	
	public static String listToProlog(List<RDFtriple> l) {
		boolean notFirst = false;
		if (l == null) return "[]";
		
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (RDFtriple t: l) {
			if (notFirst) {
				sb.append(',');
			}
			sb.append(tripleToProlog(t));
			notFirst = true;
		}
		sb.append(']');
		return sb.toString();
	}
	
	/**
	 * Remove the single quotes from a Prolog value 
	 * @param input the string with potential quotes
	 * @return a string with quotes removed
	 */
	public static String removeQuotes(String input) {
		return input.replace('\'', ' ').trim();
	}

	/**
	 * Turns an RDF triple Java object back into a Prolog fact
	 * @param t the RDF triple in Java

	 * @return a Prolog string representing the triple
	 */	
	public static String tripleToProlog(RDFtriple t) {
		StringBuilder sb = new StringBuilder();
		if (t == null) return "";
		Atom subj = new Atom(t.getSubject());
		Atom pred = new Atom(t.getPredicate());
		Atom obj = new Atom(t.getObject());
		sb.append("(").append(subj).append(",").append(
				pred).append(",").append(obj).append(
				")");
		return sb.toString();
	}
	
	/**
	 * Tell Prolog to adopt a goal. This makes it no longer eligible for goal selection
	 * 
	 * @param goal the Prolog string representing the goal to adopt
	 */
	public boolean adoptGoal(String goal) {
		return call(PrologKB.adopt_goal, goal);
	}
	
	/**
	 * Tell Prolog to drop a goal. 
	 * 
	 * @param goal the Prolog string representing the goal to drop
	 */
	public boolean dropGoal(String goal) {
		return call(PrologKB.drop_goal, goal);
	}	
	
	public boolean isAdoptedGoal(String goal) {
		return call(PrologKB.adopted_goal, goal);
	}
	
	public boolean isAdoptedJustifiableGoal(String goal) {
		return call(PrologKB.adopted_justifiable_goal, goal);
	}	
	
	public boolean suggestGoal(String goal) {
		return call(PrologKB.suggest_goal, goal);
	}
	
	public boolean isSuggestedGoal(String goal) {
		return call(PrologKB.suggested_goal, goal);
	}
	
	/**
	 * Asserts given individual as a "causal anchor", i.e., tell KB this individual is certainly part of the
	 * causal chain
	 * 
	 * @param individual the Individual to assert as causal anchor
	 * @return whether successful
	 */
	public boolean assertCausalAnchor(String individual) {
		Atom ind = new Atom(individual);
		logger.fine("Asserting " + ind + " as causal anchor.");
		return call(PrologKB.assert_causal_anchor, ind.toString());
	}	

	/**
	 * Tell Prolog to adopt a justifiable goal. This makes it no longer eligible for goal selection
	 * 
	 * @param goal the Prolog string representing the goal to justify
	 */
	public boolean adoptJustifiableGoal(String goal) {
		return call(PrologKB.adopt_justifiable_goal, goal);
	}

	/**
	 * Applies operator effects
	 * 
	 * @param schema the operator schema to apply the effects of
	 *  
	 * @return wether successful
	 */
	public boolean applyOperatorEffects(String schema) {
		if (call(PrologKB.applyOperatorEffects, schema) ) {
			return true;
		} else {
			if (logger.isLoggable(Level.WARNING)) { 
				logger.warning("Applying operator effects failed.\nSchema: " + schema);
			}
			return false;
		}
	}

	/* See interface */
	public boolean ask(String query) {
		Query q = new Query(query);
		return q.hasSolution();
	}
	
	/* See interface */
	public boolean call(String prologCommand, String input) {
		return ask(prologCommand + "(" + input + ").");
	}	
	

	/**
	 * Determine which actions given character can pursue
	 * @param character the URI of the character
	 * @return a vector of actions
	 */
	public Vector<String> canDo(String character, String operator) {
		return getPrologSingleVariableList(
				PrologKB.canDo, PrologKB.addQuotes(character) + "," + operator);				
	}

	
	/**
	 * Checks whether given schema's preconditions hold, and ignores fabula
	 * 
	 * @param schema the schema to validate, as prolog string
	 * @return whether the schemas preconditions (that are not fabula preconditions) are true 
	 */
	public boolean checkSchemaFacts(String schema) {
		
		StringBuilder queryString = new StringBuilder();
		queryString.append(PrologKB.check_schema_facts).append('(').append(schema).append(").");
		
		return ask(queryString.toString()); 

	}
	
	/**
	 * Checks whether there is a resource conflict between two operator schemas. This is the case for instance when 
	 * two actions both use the same arguments somewhere (e.g. "john greets pete"  and "pete walks to the tree"). 
	 * 
	 * @param schema1 prolog description of operator schema
	 * @param schema2 prolog description of operator schema
	 * @return whether there is a resource conflict between given operator schemas
	 */
	public boolean isResourceConflict(String schema1, String schema2) {
		StringBuilder queryString = new StringBuilder();
		queryString.append(PrologKB.resource_conflict).append('(').append(schema1).append(',').append(schema2).append(").");
		
		return ask(queryString.toString()); 
	}
	
	public Vector<RDFtriple> conditionToTripleList(String condition) {
		
		Vector<RDFtriple> triples = new Vector<RDFtriple>();
		
		// Build query
		StringBuilder sb = new StringBuilder();
		sb.append(condition_to_triples).append("(");
		sb.append(condition);
		sb.append(",T,S,P,O)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Vector<RDFtriple> returnList = new Vector<RDFtriple>();
		for (Hashtable binding: q.allSolutions()) {
			String t = binding.get("T").toString();
			String s = binding.get("S").toString();
			String p = binding.get("P").toString();
			String o = binding.get("O").toString();
			
			RDFtriple trip = new RDFtriple();
			trip.setSubject(removeQuotes(s));
			trip.setPredicate(removeQuotes(p));
			trip.setObject(removeQuotes(o));
			trip.setTruth(Boolean.parseBoolean(t));
			triples.add(trip);
		}

		q.close();
		return returnList;				

	}	

	/**
	 *  consult expects foreward slashes (/) in file names
	 **/
	public boolean consult(String filename) {
		return ask(PrologKB.consult + "('" + filename + "').");
	}
	
	/**
	 * Returns the events in the plan that are executable, i.e. do not depend on the execution of other steps (in terms of causal links)
	 * 
	 * @param plan a prolog string representation of a plan
	 * @return a collection of prolog strings representing the schemas of steps in the plan
	 */
	public Vector<String> executableEvents(String plan) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Getting executable events for plan.");
		}
		return PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.executableEvent, plan);
	}	

	/**
	 * Returns the improvisations in the plan that are executable, i.e. do not depend on the execution of other steps (in terms of causal links)
	 * 
	 * @param plan a prolog string representation of a plan
	 * @return a collection of prolog strings representing the schemas of steps in the plan
	 */
	public Vector<String> executableFramingOperators(String plan) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Getting executable framing operators for plan.");
		}
		return PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.executableFramingOperator, plan);
	}
	
	/**
	 * Returns the inferences in the plan that are executable, i.e. do not depend on the execution of other steps (in terms of causal links)
	 * 
	 * @param plan a prolog string representation of a plan
	 * @return a collection of prolog strings representing the schemas of steps in the plan
	 */
	public Vector<String> executableInferenceOperators(String plan) {
		logger.fine("Getting executable inference operators for plan.");
		return PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.executableInferenceOperator, plan);
	}
	
	/**
	 * Returns the internal elements in the plan that are executable, i.e. do not depend on the execution of other steps (in terms of causal links)
	 * 
	 * @param plan a prolog string representation of a plan
	 * @return a collection of prolog strings representing the schemas of steps in the plan
	 */
	public Vector<String> executableInternalElementOperators(String plan) {
		logger.fine("Getting executable internal element operators for plan.");
		return PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.executableInternalElementOperator, plan);
	}	
	
	/**
	 * Returns the operators in the plan that are executable, i.e. do not depend on the execution of other steps (in terms of causal links)
	 * 
	 * @param plan a prolog string representation of a plan
	 * @return a collection of prolog strings representing the schemas of steps in the plan
	 */
	public Vector<String> executableOperators(String plan) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Getting executable operators for plan.");
		}
		return PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.executableOperator, plan);
	}

	
	/**
	 * Determines whether given plan is "finished", i.e., there are no more steps that can be executed
	 * @param plan Prolog string representing the plan
	 * @return whether plan is finished
	 */
	public boolean finishedPlan(String plan) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Seeing if plan is finished");
		}
		if (plan == null) {
			return false;
		}
		return call(finishedPlan, plan);
	}

	/**
	 * Determines whether plan is still valid in given context, i.e., replanning is not needed. This is a speedup for the planner.
	 * @param plan a Prolog string representing the plan

	 * @return whether the plan is "invalid"
	 */
	public boolean invalidPlan(String plan) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Seeing if current plan is invalid");
		}
		if (plan == null) {
			return true;
		}
		return call(invalidPlan, plan);
	}
	
	/**
	 * Get first element of tuple
	 * @param tuple a string representing the tuple
	 * @return first element of the tuple
	 */
	public String first(String tuple) {
		return getPrologSingleVariable(PrologKB.first, tuple);
	}
	
	
	/**
	 * Given a query and a variable occurring in this query, get all resulting binding from the answers
	 */
	public Vector<String> getAllResults(Query q, Variable v) {
		Vector<String> returnList = new Vector<String>();
		for (Hashtable binding: q.allSolutions()) {
			Term t = (Term) binding.get(v.toString());
			if (t != null) {
				returnList.add(t.toString());
			}			
		}
		q.close();
		return returnList;
	}
	
/*---------------------------------
 * METHODS
 * --------------------------------
 */	
	
	/**
	 * This method returns a number of Strings that represent the URIs of Individuals of fabula elements, that 
	 * enable the given schema. For instance, if #belief_23 and #belief_25 make the preconditions of #goal_22 true,
	 * this method returns <#belief_23, #belief_25>. 
	 * @param gs the goal schema under investigation
	 * @return a set of individuals
	 */
	public Set<String> getEnablingFabulaElements(GoalSchema gs) {	return getEnablingFabulaElements(gs.getPrologDescription());	}	
	public Set<String> getEnablingFabulaElements(Operator op) {		return getEnablingFabulaElements(op.getPrologDescription());	}		
	protected Set<String> getEnablingFabulaElements(String schema) {
		StringBuilder sb = new StringBuilder();
		sb.append(schema_enablement).append("(");
		sb.append(schema);
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;		
	}
	
	/**
	 * This method returns a number of Strings that represent the URIs of Individuals of setting fabula elements, that 
	 * enable the given event schema. For instance, if #setting_23 and #setting_25 make the preconditions of #event_22 true,
	 * this method returns <#setting_23, #setting_25>. 
	 * @param es the event schema under investigation
	 * @return a set of individuals
	 */
	public Set<String> getSettingsEnablingEvents(StoryEvent es) {
		StringBuilder sb = new StringBuilder();
		sb.append(setting_enables_event).append("(");
		sb.append(es.getPrologDescription());
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;		
	}
	
	/**
	 * This method returns a number of Strings that represent the URIs of Individuals of setting fabula elements, that 
	 * cause the given action rule schema. For instance, if #setting_23 and #setting_25 make the preconditions of action rule true,
	 * this method returns <#setting_23, #setting_25>. 
	 * @param actionRuleSchema the action rule schema under investigation
	 * @return a set of individuals
	 */
	public Set<String> getActionRuleCauses(String actionRuleSchema) {
		StringBuilder sb = new StringBuilder();
		sb.append(action_rule_cause).append("(");
		sb.append(actionRuleSchema);
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;		

	}	
	
	/**
	 * This method returns a number of Strings that represent the URIs of Individuals of setting fabula elements, that 
	 * cause the given goal rule schema. For instance, if #setting_23 and #setting_25 make the preconditions of goal rule true,
	 * this method returns <#setting_23, #setting_25>. 
	 * @param goalRuleSchema the goal rule schema under investigation
	 * @return a set of individuals
	 */
	public Set<String> getGoalRuleCauses(String goalRuleSchema) {
		StringBuilder sb = new StringBuilder();
		sb.append(goal_rule_cause).append("(");
		sb.append(goalRuleSchema);
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;		
	}
	
	public Set<String> getInternalElementCauses(String internalElementSchema) {
		StringBuilder sb = new StringBuilder();
		sb.append(internal_element_cause).append("(");
		sb.append(internalElementSchema);
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;	
	}
	
	public String getGoalPossibleAfterPlan(String character, String goal) {
		Atom charc = new Atom(character);
		return getPrologSingleVariable(PrologKB.possible_goal_after_plan, charc + "," + goal + ", _");
	}
	
	public Set<JustifiableGoalSchema> getGoalsPossibleAfterPlan(String character) {
		Set<JustifiableGoalSchema> just_goals = new HashSet<JustifiableGoalSchema>();
		
		Variable G = new Variable("Goal");
		Variable M = new Variable("MetaGoal");
		Variable P = new Variable("Plan");
		Atom charc = new Atom(character);
		// TODO use new Atom and new Term and new Query things
		StringBuilder sb = new StringBuilder();
		sb.append(PrologKB.possible_goal_after_plan).append('(').append(charc).append(", ").append(
				G.toString()).append(", ").append(M.toString()).append(", ").append(P.toString()).append(").");
		
		Query q = new Query(sb.toString()); //prologCommand + "(" + input + ", " + X.toString() + ").");
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Querying prolog with: " + q.toString());
		}
		//Hashtable[] answers = q.allSolutions();

		while (q.hasMoreElements()) {
			Hashtable binding = (Hashtable) q.nextElement();
			Term goal = (Term) binding.get(G.toString());
			Term metagoal = (Term) binding.get(M.toString());
			Term plan = (Term) binding.get(P.toString());
			if (goal != null && metagoal != null && plan != null) {
				GoalSchema gs = FabulaFactory.createGoalSchema(goal.toString(), character);
				JustifiableGoalSchema fgs = new JustifiableGoalSchema(gs, character, metagoal.toString(), plan.toString());
				just_goals.add(fgs);
			}
		}
		q.close();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Prolog returned: " + just_goals);
		}
		return just_goals;					
	}	
	
//------------------------- 
//	(Human) Character agent module
//-------------------------
	
	//get all possible actions for given character
	public Vector<String> getAllPossibleActions(String characterURI) {
		if (characterURI == null || characterURI.equals("")) {
			return new Vector<String>();
		}		
		Variable X = new Variable("Action");
		Query q = new Query(PrologKB.possible_action, new Term[] {new Atom(characterURI), X});
		logger.fine("Prolog query: " + q.toString());
		return getAllResults(q, X);
	}
	
	//get all possible TransitMove actions for given character
	public Hashtable<String, Vector<String>> getAllPossibleActionsGroupedByType(String characterURI) {
		if (characterURI == null || characterURI.equals("")) {
			return new Hashtable<String, Vector<String>>();
		}
		
		String action = "Action";
		String type = "Type";
		Query q = new Query(PrologKB.possible_action, new Term[] {new Atom(characterURI), new Variable(action), new Variable(type)});
		Hashtable<String, Vector<String>> type_action = new Hashtable<String, Vector<String>>();
		for (Hashtable binding: q.allSolutions()) {
			//String typeDescription = ((Term) binding.get(type)).toString();
			
					
			String actionDescription = ((Term) binding.get(action)).toString();
			
			String typeDescription = narrate_category(actionDescription);
			//IS: manual types 
			/*String typeDescription = narrate_category(actionDescription);	
			if (typeDescription.equals("")) {
				typeDescription = "no type";
			}*/
			
			Vector<String> actions = type_action.get(typeDescription);
			if(actions==null) {
				Vector<String> newGroup = new Vector<String>();
				newGroup.add(actionDescription);
				type_action.put(typeDescription, newGroup);
			} else {
				actions.add(actionDescription);
			}
		}
		q.close();
		return type_action;
	}
	
	//get all possible TransitMove actions for given character
	public Hashtable<String, String> getAllPossibleTransitMoveActions(String characterURI) {
		String target = "Target";
		String action = "Action";
		Query q = new Query(PrologKB.possible_transitmove_action, 
				new Term[] {new Atom(characterURI), new Variable(target), new Variable(action)});
		Hashtable<String, String> location_action = new Hashtable<String, String>();
		for (Hashtable binding: q.allSolutions()) {
			String destination = ((Term) binding.get(target)).toString();
			String transitMove = ((Term) binding.get(action)).toString();
			location_action.put(destination, transitMove);
		}
		q.close();
		return location_action;
	}
	
	//get all possible TransitMove actions for given character (inverse map)
	public Hashtable<String, String> getAllPossibleTransitMoveActions2(String characterURI) {
		String target = "Target";
		String action = "Action";
		Query q = new Query(PrologKB.possible_transitmove_action, 
				new Term[] {new Atom(characterURI), new Variable(target), new Variable(action)});
		Hashtable<String, String> location_action = new Hashtable<String, String>();
		for (Hashtable binding: q.allSolutions()) {
			String destination = ((Term) binding.get(target)).toString();
			String transitMove = ((Term) binding.get(action)).toString();
			location_action.put(transitMove, destination);
		}
		q.close();
		return location_action;
	}	
	
	//get the entities that inhabit each location
	public Hashtable<String,Vector<String>> getAllLocationsInhabitants() {
		/*
		 * TODO Jeroen: Hardcoded ontology predicate...
		 * Either conform to this standard (SWC ontology) or make it more general---but how? Maybe
		 * we can get the current domain name and use it as "<domainname>#at", so that only "at" 
		 * needs to be the same across ontologies.
		 * For now: stupid hardcode workaround.
		 * 
		 * Apparently, the query just returned all the instances of classes that are *at* some
		 * location. Yet we only want *entities*, i.e., characters, so let's try to combine two
		 * queries. Prolog should be excellent for this.
		 * 
		 * Yep, combining queries works!
		 * 
		 * Original query:
		 * Query q = new Query("query(Entity, 'http://www.owl-ontologies.com/StoryWorldCore.owl#at', Location)");
		 */
		
		// Check whether the entity is present in the storyworld *and* has a Role, i.e., is an
		// agent.
		
		Query q;
		
		// DOMAINTOGGLE
		// Commit:
		if( RationalAgent.readStoryDomain().getOntology().equals("http://www.ontology.com/commit.owl") )
		{
			q = new Query("query(Entity, 'http://www.ontology.com/commit.owl#at', Location)," +
								"query(Entity, 'http://www.ontology.com/commit.owl#hasRole', Role)");
		}
		else // Red and other domains:
		{
			q = new Query("query(Entity, 'http://www.owl-ontologies.com/StoryWorldCore.owl#at', Location)");
		}
				
		Hashtable<String,Vector<String>> location_inhabitants = new Hashtable<String,Vector<String>>();
		
		for (Hashtable binding: q.allSolutions()) {
			String agent = ((Term) binding.get("Entity")).toString();
			String location = ((Term) binding.get("Location")).toString();
			Vector<String> inhabitants = location_inhabitants.get(location);
			
			if(inhabitants==null) {
				inhabitants = new Vector<String>();
				location_inhabitants.put(location, inhabitants);
			}
			inhabitants.add(agent);
		}
		q.close();
		return location_inhabitants;
	}
	
	//get all entities that are at any location
	//looks at everybody(everything) at every location 
	//(used to check if for each an image is available)
	public Vector<String> getAllEntitiesAtAnyLocation() {
		/* TODO Jeroen: Same as above!
		 * Check for entities that are present in the world *and* have a role, i.e., are agents.
		 * Let's try to combine two queries in one.
		 * 
		 * Original: Query q = new Query("query(Entity, 'http://www.owl-ontologies.com/StoryWorldCore.owl#at', Location)");
		 */
		
		Query q;
		
		// DOMAINTOGGLE
		// Commit:
		if( RationalAgent.readStoryDomain().getOntology().equals("http://www.ontology.com/commit.owl") )
		{
			q = new Query("query(Entity, 'http://www.ontology.com/commit.owl#at', Location)," +
								"query(Entity, 'http://www.ontology.com/commit.owl#hasRole', Role)");
		}
		else // Red and other domains:
		{
			q = new Query("query(Entity, 'http://www.owl-ontologies.com/StoryWorldCore.owl#at', Location)");
		}
		
		return getAllResults(q, new Variable("Entity"));
		
	}
	
	//get all possible improvisations
	//XXX: no need to specify the character? 
//	public Vector<String> getAllPossibleImprovisations() {
//		Variable X = new Variable("Improvisation");
//		Query q = new Query("possible_improvisation", new Term[] {X});
//		return getAllResults(q, X);
//	}
	
	//get all characters attempt 1 (uses predicate in BasicCharacterAgent.pl)
	//XXX: does not work correctly?
//	public Vector<String> getAllCharacters1() {
//		Variable X = new Variable("Character");
//		Query q = new Query("charact", new Term[] {X});
//		return getAllResults(q, X);
//	}
	
	//get all characters attempt 3
	//XXX: looks at everybody/everything at all locations 
//	public Vector<String> getAllCharacters3() {
//		//Query q = new Query("query(rule(Agens, owlr:'typeOrSubType', swc:'Character'))");
//		//combine with:
//		Query q = new Query("query(Character, 'http://www.owl-ontologies.com/StoryWorldCore.owl#at', Location)");
//		return getAllResults(q, new Variable("Character"));
//	}
	
	
//	//NOT OPERATIONAL
//	//get all locations in the world
//	public Vector<String> getAllLocations() {
//		//Query q = new Query("query(rule(Location, owlr:'classOrSubClassOf', swc:'GeographicArea'))");
//		//Query q = new Query("query(rule(Location, owlr:'typeOrSubType', swc:'Location'))");
//		//return getAllResults(q, new Variable("Location"));
//		Variable X = new Variable("Location");
//		Query q = new Query("locat", new Term[] {X});
//		return getAllResults(q, X);
//	}
		
//	//get all actions to specified destination for given character
//	public Vector<String> getAllActionsToDestination(String characterURI, String destination) {
//		Variable X = new Variable("Action");
//		Query q = new Query(PrologKB.action_to_destination, new Term[] {new Atom(characterURI), new Atom(destination), X});
//		return getAllResults(q, X);
//	}
//	
//	//get all possible destinations for given character
//	public Vector<String> getAllPossibleDestinations(String characterURI) {
//		Variable X = new Variable("Target");
//		Query q = new Query(PrologKB.possible_destination, new Term[] {new Atom(characterURI), X});
//		return getAllResults(q, X);
//	}
//	
//	//check if location is a possible destination for given character
//	public boolean isPossibleDestination(String characterURI, String location) {
//		return ask(PrologKB.possible_destination + "(" + characterURI + "," + location + ").");
//	}
	

	
	public float getGoalUrgency(String schema) {
		String urg = getQNPF(PrologKB.getGoalUrgency, schema);
		return Float.parseFloat(urg);
	}
	
	/**
	 * JB
	 */
	public String getGoalDramaticChoice(String schema){
		return getQNPF(PrologKB.getGoalDramaticChoice, schema);
	}
	
	/**
	 * JB
	 * @return the positive motivations associated with a given goal schema
	 */
	public Vector<String> getGoalPosMotivationList(String schema) {
		return getPrologSingleVariableList(PrologKB.getGoalPosMotivationList, schema);
	}
	
	/**
	 * JB
	 * @return the positive motivations associated with a given goal schema
	 */
	public Vector<String> getGoalNegMotivationList(String schema) {
		return getPrologSingleVariableList(PrologKB.getGoalNegMotivationList, schema);
	}
	
	
	/**
	 * JB
	 * 
	 * Determine which motivations given character has
	 * @param character the URI of the character
	 * @return a vector of motivations of the given character
	 */
	public Vector<String> getMotivations(String character) {
		return getPrologSingleVariableList(
				PrologKB.getMotivations,PrologKB.addQuotes(character));				
	}
	
	/**
	 * JB
	 * 
	 * TESTQUERY FOR UPDATING MOTIVATION VALUES
	 *
	 */
	public void setDesire(String interest, float value) {
		String desire = "literal(type('http://www.w3.org/2001/XMLSchema#integer', '" + value + "'))";
		StringBuilder sb = new StringBuilder();
		sb.append(PrologKB.setDesire).append('(').append(interest).append(", ").append(
				desire).append(").");
		Query q = new Query(sb.toString());//prologCommand + "(" + input + ", " + X.toString() + ").");
		q.hasSolution();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("AapQuerying prolog with: " + q.toString());
		}
		//q.hasSolution();
	}

	/**
	 * JB
	 * 
	 * @return the strength associated with a given motivation
	 * 
	*/  
	public int getMotivationStrength(String motivation) {
		String strength = getQNPF(PrologKB.getMotivationStrength, motivation);
		if (strength == null) {
			strength = "0";
		}
		return Integer.parseInt(strength);
	}
	
	/**
	 * JB
	 * 
	 * @return the desire associated with a given interest
	 * 
	*/  
	public float getInterestDesire(String interest) {
		String desire = getQNPF(PrologKB.getInterestDesire, interest);
		if (desire == null) {
			desire = "1";
		}
		return Float.parseFloat(desire);
	}
	
	
	/**
	 * This method returns a number of Strings that represent the URIs of Individuals of fabula elements, that 
	 * motivate the given schema (goal schema or action selection schema). For instance, if #goal_23 and #goal_25 make the preconditions of #goal_22 true,
	 * this method returns <#goal_23, #goal_25>. 
	 * @param gs the Goal schema under investigation
	 * @return the set of Individuals as described
	 */
	public Set<String> getMotivatingFabulaElements(GoalSchema gs) {
		return getMotivatingFabulaElements(gs.getPrologDescription());
	}
	
	public Set<String> getMotivatingFabulaElements(String schemaDescr) {
		StringBuilder sb = new StringBuilder();
		sb.append(schema_motivation).append("(");
		sb.append(schemaDescr);
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;		
	}
	
	/**
	 * This method returns a number of Strings that represent the URIs of Individuals of fabula elements, that 
	 * cause the given goal to succeed. For instance, if #ie_34 and #ie_25 make the success conditions of #goal_22 true,
	 * this method returns <#ie_34, #ie_25>. 
	 * @param gs the Goal schema under investigation
	 * @return the set of Individuals as described
	 */
	public Set<String> getGoalSuccessCausingFabulaElements(GoalSchema gs) {
		StringBuilder sb = new StringBuilder();
		sb.append(goal_success_cause).append("(");
		sb.append(gs.getPrologDescription());
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;				
	}
	
	/**
	 * This method returns a number of Strings that represent the URIs of Individuals of fabula elements, that 
	 * cause the given goal to fail. For instance, if #ie_34 and #ie_25 make the failure conditions of #goal_22 true,
	 * this method returns <#ie_34, #ie_25>. 
	 * @param gs the Goal schema under investigation
	 * @return the set of Individuals as described
	 */
	public Set<String> getGoalFailureCausingFabulaElements(GoalSchema gs) {
		StringBuilder sb = new StringBuilder();
		sb.append(goal_failure_cause).append("(");
		sb.append(gs.getPrologDescription());
		sb.append(",Individual)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Set<String> returnList = new HashSet<String>();
		for (Hashtable binding: q.allSolutions()) {
			String ind = binding.get("Individual").toString();
			
			returnList.add(removeQuotes(ind));
		}

		q.close();
		return returnList;				
	}	
	
	/**
	 * Given a query and a variable occurring in this query, get one resulting binding from the answers
	 */
	public String getOneResult(Query q, Variable v) {
		Hashtable binding = q.oneSolution();
		q.close();
		if (binding != null) {
			Term t = (Term) binding.get(v.toString());
			if (t != null) {
				return t.toString();
			}			
		}
		return null;
	}	
	
	/**
	 * Retrieve effects of operator schema
	 * assumption is that effects can be applied 
	 * 
	 * @param schema the schema to retrieve the effects of, in prolog string
	 * @return a vector of the retrieved triples, or null if applying failed
	 */
	public Vector<RDFtriple> getOperatorEffects(String schema) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Successfully applied operator effects");
		}
		Vector<RDFtriple> effects = new Vector<RDFtriple>();
		
		// Build query
		StringBuilder sb = new StringBuilder();
		sb.append(operator_effect).append("(");
		sb.append(schema);
		sb.append(",T,S,P,O)");
		Query q = new Query(sb.toString());
		
		// Retrieve results
		Vector<RDFtriple> returnList = new Vector<RDFtriple>();
		for (Hashtable binding: q.allSolutions()) {
			String t = binding.get("T").toString();
			String s = binding.get("S").toString();
			String p = binding.get("P").toString();
			String o = binding.get("O").toString();
			
			RDFtriple trip = new RDFtriple();
			trip.setSubject(removeQuotes(s));
			trip.setPredicate(removeQuotes(p));
			trip.setObject(removeQuotes(o));
			trip.setTruth(Boolean.parseBoolean(t));
			effects.add(trip);
		}

		q.close();
		return effects;
	}
	
	/**
	 * Returns a collection of the plan links of given plan 
	 * @param plan a prolog string representation of a plan
	 * @return a collection of plan links
	 */
	public Vector<PlanLink> getPlanLinks(String plan) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Retrieving all Links from Plan.");
		}
		Vector<PlanLink> planLinks = new Vector<PlanLink>();
		
		Vector<String> links = PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.planLink, plan);
		
		for (String link: links) {
			String from = PrologKB.getInstance().getPrologSingleVariable(PrologKB.planLinkFrom, link);
			String to = PrologKB.getInstance().getPrologSingleVariable(PrologKB.planLinkTo, link);
			String cond = PrologKB.getInstance().getPrologSingleVariable(PrologKB.planLinkCond, link);

			//RDFtriple posTriple = PrologKB.getInstance().prologToTriple(pos, true);
			//RDFtriple negTriple = PrologKB.getInstance().prologToTriple(neg, false);
			
			PlanLink nwLink = new PlanLink(from, to, cond);
			planLinks.add(nwLink);
		}

		return planLinks;
		
	}
	
	/**
	 * Returns a collection of the plan orderings of given plan 
	 * @param plan a prolog string representation of a plan
	 * @return a collection of plan orderings
	 */
	public Vector<PlanOrdering> getPlanOrderings(String plan) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Retrieving all Links from Plan.");
		}
		Vector<String> orderings = PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.planOrdering, plan);
		
		Vector<PlanOrdering> planOrderings = new Vector<PlanOrdering>();
		
		
		for (String ordering: orderings) {
			
			String v1 = PrologKB.getInstance().first(ordering);
			String v2 = PrologKB.getInstance().second(ordering);
			
			String v1_name = PrologKB.getInstance().first(v1);
			String v2_name = PrologKB.getInstance().first(v2);
			
			PlanOrdering nwOrdering = new PlanOrdering(v1_name, v2_name);
			planOrderings.add(nwOrdering);
		}

		return planOrderings;
		
	}
	
	public Vector<PlanStep> getPlanSteps(String plan) {
		Vector<PlanStep> planSteps = new Vector<PlanStep>();
		Vector<String> steps = PrologKB.getInstance().getPrologSingleVariableList(
				PrologKB.planStep, plan);
		
		for (String step: steps) {
			String name = PrologKB.getInstance().first(step);
			String operator = PrologKB.getInstance().second(step);
			String type = PrologKB.getInstance().getSchemaType(operator);
			String clss = PrologKB.getInstance().getSchemaClass(operator);
			
			PlanStep nwStep = new PlanStep(name, operator, type, clss);
			planSteps.add(nwStep);
		}
		
		return planSteps;

	}	
	
	public boolean existsGoalRules() {
		return ask(goal_rules_exist);
	}
	
	/**
	 * Returns a vector containing all goals that are possible to pursue for given character. In BDI terms, these are the "desires"
	 * of the agent. Prolog is responsible for establishing this set of desires; Java is responsible for selecting what to pursue.
	 * No choices are made yet as to a consistent set of goals that the agent is actually pursuing (in BDI terms, the "goals" of the agent). 
	 * 
	 * @param character the URI of the character agent for which to retrieve possible goals
	 * @return a list of possible goals
	 */
	public Vector<String> getPossibleGoals(String character) {
		return getPrologSingleVariableList(
				PrologKB.possible_goal, PrologKB.addQuotes(character));						
	}
	
	/**
	 * Returns a collection of goal rules that are valid 
	 * @param character character to select goal rules for
	 * @return a collection of valid goal rules
	 */
	public Vector<String> selectGoalRules(String character) {
		return getPrologSingleVariableList(PrologKB.select_goal_rule, new Atom(character).toString());
	}
	
	/**
	 * Returns a collection of settings based on executed plot threads (asserted in Prolog)
	 * 
	 * @return a collection of (thread) settings as Prolog strings
	 */
	public Vector<String> selectReactiveActionRules(String character) {
		// TODO: also retrieve fabula elements causing the reactive action. This will be very difficult later on.
		return getPrologSingleVariableList(PrologKB.reactive_action_rule, new Atom(character).toString());
	}
	
	public String selectActionFromRule(String actionSelectionRule) {
		return getPrologSingleVariable(PrologKB.select_action_from_rule, actionSelectionRule);
	}
	
	public String selectGoalFromRule(String goalSelectionRule) {
		return getPrologSingleVariable(PrologKB.select_goal_from_rule, goalSelectionRule);
	}
	
	
	/**
	 * Builds the following structure
	 * <i>prologCommand</i> ( <i>input</i> , GPLVar )
	 * @deprecated - use getOneResult() in future
	 */
	public String getPrologSingleVariable(String prologCommand, String input) {
		Variable X = new Variable("GPLVar");
		// TODO use new Atom and new Term and new Query things
		StringBuilder sb = new StringBuilder();
		sb.append(prologCommand).append('(').append(input).append(", ").append(
				X.toString()).append(").");
		Query q = new Query(sb.toString());//prologCommand + "(" + input + ", " + X.toString() + ").");
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Querying prolog with: " + q.toString());
		}
		String answer = new String();
		//TODO This is ugly
		if (q.hasMoreElements()) {
			Hashtable binding = (Hashtable) q.nextElement();
			Term t = (Term) binding.get(X.toString());
			if (t != null) {
				answer = t.toString();
			}
		}
		q.close();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Prolog returned: " + answer);
		}
		return answer;
	}
	
//------------------------- 
//	Knowledge base module
//-------------------------	
	
	@Deprecated
	public Vector<String> getPrologSingleVariableList(String prologCommand) {
		Variable X = new Variable("GPLVar");
		// TODO use new Atom and new Term and new Query things
		Query q = new Query(prologCommand + "(" + X.toString() + ").");
		
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Querying prolog with: " + q.toString());
		}
		
		//Hashtable[] answers = q.allSolutions();
		Vector<String> returnList = new Vector<String>();
		while (q.hasMoreElements()) {
			Hashtable binding = (Hashtable) q.nextElement();
			Term t = (Term) binding.get(X.toString());
			if (t != null) {
				returnList.add(t.toString());
			}
		}
		q.close();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Prolog returned: " + returnList);
		}
		return returnList;
	}

	/**
	 * @deprecated - use getAllResults() in future
	 */
	public Vector<String> getPrologSingleVariableList(String prologCommand,	String input) {
		Variable X = new Variable("GPLVar");
		// TODO use new Atom and new Term and new Query things
		StringBuilder sb = new StringBuilder();
		sb.append(prologCommand).append('(').append(input).append(", ").append(
				X.toString()).append(").");
		Query q = new Query(sb.toString()); //prologCommand + "(" + input + ", " + X.toString() + ").");
		logger.fine("Querying prolog with: " + q.toString());
		//Hashtable[] answers = q.allSolutions();
		Vector<String> returnList = new Vector<String>();
		while (q.hasMoreElements()) {
			Hashtable binding = (Hashtable) q.nextElement();
			Term t = (Term) binding.get(X.toString());
			if (t != null) {
				returnList.add(t.toString());
			}
		}
		q.close();
		
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Prolog returned: " + returnList);
		}
		return returnList;
	}	
	
//---------------------------
//	Schema management module
//---------------------------

	/**
	 * Returns the URI of the agens of given schema (if it has any) 
	 * @param schema the schema as prolog string
	 * @return the agens of the schema
	 */
	public String getSchemaAgens(String schema) {
		return getQNPF(PrologKB.getOperatorAgens, schema);
	}
	/**
	 * Returns the class of given schema (e.g. action, goal, event, etc)
	 * 
	 * TODO: make in prolog using =..
	 * 
	 * @param schema the schema as Prolog string
	 * @return the class of the schema
	 */
	public String getSchemaClass(String schema) {
		return getQNPF(PrologKB.getSchemaKind, schema);		
	}
	
	/**
	 * Returns the URI of the instrument of given schema (if it has any) 
	 * @param schema the schema as prolog string
	 * @return the instrument of the schema
	 */
	public String getSchemaInstrument(String schema) {
		return getQNPF(PrologKB.getOperatorInstrument, schema);
	}
	
	/**
	 * Returns the URI of the opponent of given schema (if it has any) 
	 * @param schema the schema as prolog string
	 * @return the instrument of the schema
	 */
	public String getSchemaOpponent(String schema) {
		return getQNPF(PrologKB.getOperatorOpponent, schema);
	}		
	
	public int getOperatorDuration(String operator) {
		return Integer.parseInt(getQNPF(PrologKB.getOperatorDuration, operator));
	}
	
	/**
	 * Returns the URI of the patiens of given schema (if it has any) 
	 * @param schema the schema as prolog string
	 * @return the patiens of the schema
	 */
	public String getSchemaPatiens(String schema) {
		return getQNPF(PrologKB.getOperatorPatiens, schema);
	}
	
	
	/**
	 * Retrieves preconditions of given schema
	 * @param schema the schema to retrieve the preconditions of
	 * @return Prolog string representing the preconditions
	 */
	public String getSchemaPreconditions(String schema) {

		return getPrologSingleVariable(
				PrologKB.getSchemaPreconditions, schema);
		
	}
	
	/**
	 * Returns the URI of the target of given schema (if it has any) 
	 * @param schema the schema as prolog string
	 * @return the target of the schema
	 */
	public String getSchemaTarget(String schema) {
		return getQNPF(PrologKB.getOperatorTarget, schema);
	}	
	
	/**
	 * Returns the type of given schema (its corresponding URI in the ontology)
	 * @param schema the schema as Prolog string
	 * @return the type of the schema
	 */
	public String getSchemaType(String schema) {
		return getQNPF(PrologKB.getOperatorType, schema);
	}
	
	public boolean isFramingScopeAll(String schema) {
		boolean sa = call(getFramingScopeAll, schema);
		if (sa) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Given schema has scope ALL");
			}
		}
		return sa;
	}
	
	public boolean isFramingScopePersonal(String schema) {
		boolean sp = call(getFramingScopePersonal, schema);
		if (sp) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Given schema has scope PERSONAL");
			}
		}
		return sp;
	}	
	
	public boolean isFramingScopeHidden(String schema) {
		boolean sh = call(getFramingScopeHidden, schema);
		if (sh) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Given schema has scope HIDDEN");
			}
		}
		return sh;
	}	
	
	public String getUntruePreconditionsOfSchema(String schema) {
		return getPrologSingleVariable(PrologKB.validateSchema, schema);
	}
	
	/**
	 * Checks whether given goal's failure conditions are true
	 * @param schema the goal schema
	 * @return whether failure conditions of goal schema are true
	 */
	public boolean goalFailureConditionsTrue(String schema) {
		return call(validateGoalFailure, schema);
	}
	
	public String goalIntention(String goalSchema) {

		return getPrologSingleVariable(
				PrologKB.goal_intention, goalSchema);
		
	}
	
	/**
	 * Determine which actions given character can pursue
	 * @param character the URI of the character
	 * @return a vector of actions
	 */
	public Vector<String> hasAction(String character) {
		return getPrologSingleVariableList(
				PrologKB.hasAction,PrologKB.addQuotes(character));				
	}	

	/**
	 * Load RDF/OWL knowledge
	 * @param file filename of the knowledge base
	 * @return whether loading succeeded
	 */
	public boolean loadKB(String file) {
		// TODO: translate to stringbuilder (JPL has no support for modules so this does not work)
		Compound c = new Compound(PrologKB.rdf_load, new Term[] {new Atom(file)});
		Query query = new Query(c);
		return query.hasSolution();
	}
	
	/**
	 * Make a narration of given prolog string
	 * @param prologString prolog string representing a narratable structure.
	 * @return narration of this prolog string in "natural language"
	 */
	public String narrate(String prologString) {
		return getPrologSingleVariable(PrologKB.narrate, prologString);
	}
	
	/**
	 * Make a narration of given prolog string, but in an imperative fashion ("Eat the cake" rather than "Grandma eats the cake")
	 * @param prologString prolog string representing a narratable structure.
	 * @return narration of this prolog string in "natural language"
	 */
	public String narrate_imperative(String prologString) {
		return getPrologSingleVariable(PrologKB.narrate_imperative, prologString);
	}
	
	/**
	 * Make a narration of the category of a given prolog string (e.g. "Eat" for an eat action)
	 * @param prologString prolog string representing a narratable structure.
	 * @return narration of this prolog string in "natural language"
	 */
	public String narrate_category(String prologString) {
		return getPrologSingleVariable(PrologKB.narrate_category, prologString);
	}				
	
	/**
	 * Make a narration of the object/character of a given prolog string (e.g. "Wolf" for the red:wolf character)
	 * @param prologString prolog string representing a narratable object.
	 * @return narration of this prolog string in "natural language"
	 */
	public String narrate_object(String prologString) {
		return getPrologSingleVariable(PrologKB.narrate_object, prologString);
	}
	
	/**
	 * Retrieves characters necessary as a result of plot thread executions (asserted in Prolog)
	 * @return a vector containing URIs of characters that are necessary 
	 */	
	public Vector<String> necessaryCharacters(String thread) {
		return getPrologSingleVariableList(PrologKB.necessaryCharacter, thread);
	}
	
	/**
	 * Builds and retrieves a partial-order plan
	 * @param intentions the intentions as prolog list
	 * @return a plan as prolog String, or null if there was no solution.
	 */
	public String plan(String character, boolean isICIntentions, String intentions) {
		String charField; // no pun intended
		
		long start = System.currentTimeMillis();
		
		if (character != null && (! character.equals(""))) {
			Atom a = new Atom(character);
			charField = a.toString();
		} else {
			charField = "_";
		}
		StringBuilder sb = new StringBuilder();		
		sb.append(PrologKB.plan).append('(')
			.append(charField).append(',')
			.append(isICIntentions).append(',')
			.append(intentions).append(", Plan).");
		
		
		if (logger.isLoggable(Level.INFO)) {
			logger.info ("Querying plan with: " + sb.toString ());
		}
		
		Query q = new Query(sb.toString());
		String solution = PrologKB.getInstance().getOneResult(q, new Variable("Plan"));
		//Hashtable solution = PrologKB.getInstance().prologCallOneSolution(sb.toString());

		if (logger.isLoggable(Level.INFO)) {
			if (solution != null) {
				logger.info("Successfully created plan using\n" + sb.toString() +"\nPlanning took: " + (System.currentTimeMillis() - start));
			} else {
				logger.warning("Could not create plan using\n" + sb.toString() +"\nPlanning took: " + (System.currentTimeMillis() - start));
			}
		}
		
		return solution;
		
	}
	
	/**
	 * Adapts an existing partial-order plan
	 * @param intentions the intentions as prolog list
	 * @return a plan as prolog String, or null if there was no solution.
	 */
	public String adaptPlan(String character, boolean isICIntentions, String intentions, String oldPlan) {
		
		// Speedup: only adapt plan if current plan is no longer valid.
		// might yield believability issues because "obvious" plan adaptions will not be chosen
		// (i.e. still sailing to the island to find a treasure chest 
		//  even though someone has placed another treasure chest right in front of your nose.)
		if (! invalidPlan(oldPlan)) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Old plan still OK, reusing it.");
			}
			return oldPlan;
		}
		
		// TODO: if old plan is no longer valid, according to Trabasso et al. what should happen is that 
		// the goal gets a negative outcome (O-), and the goal is re-instantiated (if its preconditions still hold). 
		
		String charField; // no pun intended
		
		if (character != null && (! character.equals(""))) {
			Atom a = new Atom(character);
			charField = a.toString();
		} else {
			charField = "_";
		}
		StringBuilder sb = new StringBuilder();		
		sb.append(PrologKB.adaptPlan).append('(')
			.append(charField).append(',')	
			.append(isICIntentions).append(',')
			.append(intentions).append(',')
			.append(oldPlan).append(", Plan).");
		
		
		if (logger.isLoggable(Level.INFO)) {
			logger.info ("Querying adapt_plan with: " + sb.toString ());
		}
		
		Query q = new Query(sb.toString());
		String solution = PrologKB.getInstance().getOneResult(q, new Variable("Plan"));
		//Hashtable solution = PrologKB.getInstance().prologCallOneSolution(sb.toString());

		if (logger.isLoggable(Level.INFO)) {
			if (solution != null) {
				logger.info("Successfully created plan using\n" + sb.toString());
			} else {
				logger.info("Could not create plan using\n" + sb.toString());
			}			
		}
		
		return solution;
		
	}	
	
//--------------------------------
//	Partial order planning module
//--------------------------------
	
	/**
	 * Retrieve plot threads that are now possible
	 * 
	 * @return vector containing string representations of possible plot threads
	 */
	public Vector<String> possibleThreads() {
		return PrologKB.getInstance().getPrologSingleVariableList(PrologKB.possibleThread);		
	}
	
	public Hashtable[] prologCall(String prologString) {
		Query q = new Query(prologString);
		Hashtable[] answers = q.allSolutions();
		q.close();
		return answers;
	}
	
	public Hashtable[] prologCall(String prologCommand, String input,
			Vector<String> vars) {
		// Use prologCommand^(AgentID, Variable) which is in prolog in BasicCharacterAgent.pl
		StringBuilder queryString = new StringBuilder();
		queryString.append(prologCommand).append('(').append(input);

		for (String v : vars) {
			queryString.append(',').append(v);
		}
		queryString.append(").");

		Query q = new Query(queryString.toString()); //prologCommand + "(" + input + queryString.toString() + ").");
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Querying Prolog with: " + queryString.toString());//prologCommand + "(" + input + queryString.toString() + ").");
		}
		Hashtable[] answers = q.allSolutions();
		q.close();
		return answers;
	}
	
	public Hashtable prologCallOneSolution(String prologString) {
		Query q = new Query(prologString);
		Hashtable answer = q.oneSolution();
		q.close();
		return answer;
	}
	
	/*
	 * @see vs.knowledge.IPrologKnowledgeManager#prologToTriple(java.lang.String, boolean)
	 */
	public List<RDFtriple> prologToList(String prologTripleList, boolean truth) {
		
		List<RDFtriple> tripleList = new Vector<RDFtriple>();
		Term[] triples = null;
		
		try {
			Term t = Util.textToTerm(prologTripleList);
			triples = Util.listToTermArray(t);
		} catch(JPLException e) {
			//do nothing
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not create triples from given Prolog list string: " + prologTripleList);
			}
		} 
		
		for (Term t: triples) {
			tripleList.add(prologToTriple(t.toString(), truth));
		}
		
		return tripleList;
	}
	
	
	/**
	 * Builds an RDFtriple object from prolog string representation of an rdf triple
	 * TODO: replace getSubject...etc by JPL parsing
	 * 
	 * @param prologTriple the string representation of the triple
	 * @param truth whether the triple should be interpreted as true or false
	 * @return an RDFtriple object if successful, or null if unsuccessful
	 */
	public RDFtriple prologToTriple(String prologTriple, boolean truth) {
		RDFtriple t = new RDFtriple();
		
		Term term = Util.textToTerm(prologTriple);
		String subject = getPrologSingleVariable(PrologKB.getSubject,
				prologTriple);
		String predicate = getPrologSingleVariable(PrologKB.getPredicate,
				prologTriple);
		String object = getPrologSingleVariable(PrologKB.getObject,
				prologTriple);
		
		if (subject.equals("") || predicate.equals("") || object.equals("")) {
			return null;
		} else {
			t.setSubject(removeQuotes(subject));
			t.setPredicate(removeQuotes(predicate));
			t.setObject(removeQuotes(object));
			t.setTruth(truth);
		}
		
		return t;
	}
	
	/**
	 * RDF query method
	 * @param triple the RDF triple we want to query Prolog about.
	 * @return a Query object
	 */
	public Query query(String triple) {
		return new Query(PrologKB.query + "(" + triple + ")");
	}
	
	/**
	 * Save knowledge base. 
	 * Not implemented yet.
	 * @param file file name to save KB to
	 * @return whether saving succeeded
	 */
	public boolean saveKB(String file) {
		// TODO Auto-generated method stub
		return false;
	}	
	
//--------------------------------
//	Thread management module
//--------------------------------

	/**
	 * Get second element of tuple
	 * @param tuple a string representing the tuple
	 * @return second element of the tuple
	 */
	public String second(String tuple) {
		return getPrologSingleVariable(PrologKB.second, tuple);
	}
	

	/**
	 * Set the character ID for this agent (e.g. ps:leChuck)
	 */
	public boolean setAgentID(String agentID) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Setting agent ID to: " + agentID);
		}

		StringBuilder qb = new StringBuilder();
		Atom a = new Atom(agentID);
		qb.append(PrologKB.setAgentID).append('(').append(a.toString()).append(')');

		Query q = new Query(qb.toString());
		return q.hasSolution();
	}
	
/*	*//*
	 * Assert that given character is now casted
	 * @param character the character that has been casted
	 * @return whether asserting succeeded
	 *//*
	public boolean castedCharacter(String character) {
		return call(PrologKB.castedCharacter, character);
	}*/
	
	/* See interface */
	public boolean tellRDF(String term) {
		Query q = new Query(PrologKB.rdfAssert + "(" + term + ")");
		//new Query("assert(" + term + ")");
		return q.hasSolution();
	}
	
	public boolean tellRDFtriple(RDFtriple t) {

		if (t.getTruth() == true) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Asserting RDF triple: " + t);
			}
			Query q = new Query(PrologKB.rdfAssert + "(" + tripleToProlog(t) + ")");
			return q.hasSolution();
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Retracting RDF triple: " + t);
			}
			Query q = new Query(PrologKB.rdfRetract + "(" + tripleToProlog(t) + ")");
			return q.hasSolution();
		}
	}
	
	/**
	 * Returns a collection of plot thread goals for given character
	 * 
	 * @param characterURI the URI of the character to retrieve plot thread goals for
	 * @return a collection of goals that the character should adopt
	 */
	public Vector<String> threadGoals(String episode, String characterURI) {
		return getPrologSingleVariableList(PrologKB.threadGoal, episode + "," + PrologKB.addQuotes(characterURI));
	}
	
	/**
	 * Returns a collection of plot thread resolve goals for given character
	 * 
	 * @param characterURI the URI of the character to retrieve plot thread goals for
	 * @return a collection of goals that the character should adopt
	 */
	public Vector<String> threadResolveGoals(String episode, String characterURI) {
		return getPrologSingleVariableList(PrologKB.threadResolveGoal, episode + "," + PrologKB.addQuotes(characterURI));
	}
	
//--------------------------------
//	Narrator module
//--------------------------------	
	
	/**
	 * Returns a collection of settings based on executed plot threads (asserted in Prolog)
	 * @return a collection of (thread) settings as Prolog strings
	 */
	public Vector<String> threadSettings(String thread) {
		return getPrologSingleVariableList(PrologKB.threadSetting, thread);
	}
		
	
/*=====================================================================================================================
 * HELPER METHODS
/*=====================================================================================================================
 */	

	/**
	 * Returns true iff left hand side unifies with right hand side
	 * @param lhs left hand side of the unification
	 * @param rhs right hand side of the unification
	 * @return whether unification succeeds
	 */
	public boolean unifies(String lhs, String rhs) {		
		Query q = new Query(lhs + " = " + rhs);
		if (q.hasSolution()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Checking whether following two terms unify:\n1) " + lhs + "\n2) " + rhs + "\nThey do.");
			}
			return true;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Checking whether following two terms unify:\n1) " + lhs + "\n2) " + rhs + "\nThey do NOT.");
			}
			return false;
		}
		
	}
	
	/* See interface */
	public boolean untellRDF(String term) {
		Query q = new Query(PrologKB.rdfRetract + "(" + term + ")");
		//new Query("retract(" + term + ")");
		return q.hasSolution();
	}
	
}
