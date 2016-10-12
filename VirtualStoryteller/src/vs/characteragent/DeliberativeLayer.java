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
package vs.characteragent;

import jade.core.AID;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.logging.Logger;

import jpl.Query;

import vs.characteragent.strategy.IGoalAdoptionStrategy;
import vs.characteragent.strategy.AdoptAllPossibleGoalsStrategy;
import vs.characteragent.strategy.RuleBasedGoalAdoptionStrategy;
import vs.communication.FabulaCausality;
import vs.communication.FabulaElement;
import vs.communication.GoalProgress;
import vs.communication.GoalSchema;
import vs.communication.DramaticChoice; //JB
import vs.communication.IncomingSetting;
import vs.communication.StoryAction;
import vs.communication.StoryOutcome;
//JB
import vs.rationalagent.StoryTime;

import vs.debug.LogFactory;
import vs.fabula.FabulaFactory;
import vs.knowledge.PrologKB;
import vs.knowledge.vocab.Fabula;
import vs.poplanner.PoPlanner;
import vs.rationalagent.behaviour.SendInformBehaviour;
import vs.utils.Chooser;

/**
 * Deliberative process of the character agent.
 * Uses goals and planning for its appraisal, coping and action selection.
 * 
 * A part of goal management is handled by Prolog, because it uses a lot of unification and selection that Java cannot really handle well.
 * The decision which goals can be adopted is handled by Prolog (keeps the goals as entities in Java, for display / debug); Java asks Prolog 
 * which goals can be adopted, and lets Prolog know which goals ARE adopted.
 * 
 * @author swartjes
 *
 */
public class DeliberativeLayer extends BehaviourLayer {

	protected Set<AdoptedGoalSchema> goals;
	protected AdoptedGoalSchema activeGoal;
	protected Vector<String> character_motivations;		//JB: List of the character's motivations
	protected Map<Integer, List<String>> desire_management = new HashMap<Integer, List<String>>();	//JB: Keep track of motivations of completed goals and their reset time for the management of their desire values
	protected Map<AdoptedGoalSchema, List<String>> goals_with_relevant_pos_motivations = new HashMap<AdoptedGoalSchema, List<String>>();	//positive motivations shared by both goal and character
	protected Map<AdoptedGoalSchema, List<String>> goals_with_relevant_neg_motivations = new HashMap<AdoptedGoalSchema, List<String>>();	//negative motivations shared by both goal and character
	protected Map<String, List<GoalSchema>> dramatic_choice_with_alternatives = new HashMap<String, List<GoalSchema>>(); 	//map a dramatic choice to its associated alternatives (goals to choose from)
	protected Vector<String> encountered_alternatives; 	//store PrologDesc of encountered alternatives of a dramatic choice to prevent ambiguous goal schemas
	protected Vector<String> logged_dramatic_choices; 	//store encountered dramatic choices so that they won't be logged more than once
	
	protected IGoalAdoptionStrategy goalAdoptionStrategy;		// Select strategy for goal adoption
	
	private Logger logger;
	
	/**
	 * Constructor
	 * 
	 * @param agent the character agent in the story (e.g. ps:leChuck) 
	 */
	public DeliberativeLayer(ICharacterAgent agent) {
		super(agent);
		
		logger = LogFactory.getLogger(this);
		
		goals = new HashSet<AdoptedGoalSchema>();
		
		character_motivations  = PrologKB.getInstance().getMotivations(getAgent().getCharacterURI()); //JB:the motivations of the current character
		encountered_alternatives = new Vector<String>();
		logged_dramatic_choices = new Vector<String>();
		
		// Choose strategy for goal adoption
		if (PrologKB.getInstance().existsGoalRules()) {
			// Adopt goals based on rules if they exist in the domain
			logger.info("Selecting goal adoption strategy: adopt goals based on rules.");
			goalAdoptionStrategy = new RuleBasedGoalAdoptionStrategy();
		} else {
			// No goal rules, just adopt goals based on preconditions
			logger.info("Selecting goal adoption strategy: adopt all possible goals.");
			goalAdoptionStrategy = new AdoptAllPossibleGoalsStrategy();
		}		
	}
	
	/**
	 * Retrieves the goal that the agent is currently pursuing
	 * @return the goal under pursuit
	 */
	public AdoptedGoalSchema getActiveGoal() {
		return activeGoal;
	}
	
	/**
	 * Retrieves all goals that the agent has adopted
	 * @return a set of adopted goals
	 */
	public Set<AdoptedGoalSchema> getGoals() {
		return goals;
	}
	
	/**
	 * Retrieves the planner currently "in focus" (i.e., the planner of the active goal)
	 * @return the planner of the active goal
	 */
	public PoPlanner getPlanner() {
		if (activeGoal != null) {
			return activeGoal.getPlanner();
		} else {
			return null;
		}
	}
	
	/**
	 * Deliberative appraisal:
	 * - Adopt new goals and select active goal (FearNot! places goal selection/adoption under appraisal too, although it might also
	 *   be seen as coping) 
	 */
	@Override
	public void appraise() {
		logger.info("Deliberative appraisal started.");
		// See which new goals to adopt and check whether they are part of a dramatic choice structure
		adoptNewGoals();
		logDramaticChoice();

		// Select which goal should be active
		setActiveGoal(selectActiveGoal());
		
		if (activeGoal == null) {
			logger.info("No more active goals.");
			return;
		}
		
		// Look at failure conditions of goal; if reached, create negative outcome
		if (PrologKB.getInstance().goalFailureConditionsTrue(activeGoal.getGoalSchema().getPrologDescription())) {
			dropGoal(activeGoal, FabulaFactory.Outcome.negative);
			
			return;
		}

	}
	
	/**
	 * Deliberative coping: 
	 * 	
	 *  - adjust plan currently under consideration
	 */
	@Override
	public void cope() {
		logger.info("Deliberative coping started.");
		
		long start = System.currentTimeMillis();
		
		// Return if there's no goals to pursue
		if (activeGoal == null) {
			return;
		}
				
		// Let's see if we can make a (new) plan
		if (getPlanner().plan()) {
		
			// Now, there are three special cases that should be dealt with: 
			//	1) plan is finished
			//	2) no plan found, but there was one before
			//	3) no plan found, but also never was one
			// in the first two cases, the goal should be dropped (and appraisal should be called again?).
			// in the third case, we can either keep it (waiting for an opportunity) or drop it without a sound.
			// 		the first option would require lowering its urgency / moving it down somehow? otherwise the agent gets stuck.
			
			// Is plan finished? This means that the success conditions of the goal have been established.
			// Make positive outcome and select another goal to be active
			if (getPlanner().planFinished()) {
			
				logger.info("Goal state is true; creating positive outcome and selecting a different goal.");
								
				//JB
				resetDesires(activeGoal);
				// Drop it like it's hot
				dropGoal(activeGoal, FabulaFactory.Outcome.positive);
				
				// If we can choose another goal, then cope again
				//cope();
			}
			
		} else {
			// We failed to make a plan.
		
			// Was there a plan before? Interpet as failed outcome.
			if (getPlanner().planUpdateFailed()) {
	
				logger.info("Goal had a plan before; creating negative Outcome");
				
				dropGoal(activeGoal, FabulaFactory.Outcome.negative);
				
				return;
	
			} else {
			
				// Drop goal without a sound
				logger.warning("Goal " + activeGoal.getGoalSchema().getType() + " is unplannable, dropping without outcome (should be negative outcome?).\nMake sure this is not a case of domain underspecification. \nPlanning took: " + (System.currentTimeMillis() - start));
				PrologKB.getInstance().dropGoal(activeGoal.getGoalSchema().getPrologDescription());
				goals.remove(activeGoal);
				activeGoal = null;
			}
		}
		
	}
	
	
	/**
	 * Implementation of selectAction() for deliberative layer: plan-based.
	 * selectAction works on the current plan of the agent, established through the deliberative coping process
	 * 
	 * @return one of the executable actions.
	 */
	@Override
	public StoryAction selectAction() {
		logger.info("Selecting action...");
		if (activeGoal == null) {
			logger.warning("No active goal to select an action for.");
			return null;
		}

		// Do we have a successful plan?
		String currentPlan = activeGoal.getPlanner().getPlan();
		
		if (currentPlan != null) {			
			
			// Is it not done, but there are no actions to choose?
			Vector<String> firstActions = activeGoal.getPlanner().getExecutableOperators();
			
			if (firstActions.isEmpty()) {
				// No executable actions in plan
				logger.info("No executable actions for now; rely on further framing");
			} else {
				// Randomly choose one of the possible actions
				String chosenAction = Chooser.randomChoice(firstActions);

				// Create action
				StoryAction a = FabulaFactory.createAction(chosenAction, getAgent().getCharacterURI());
				
				// Create motivation causality
				FabulaCausality fc = new FabulaCausality();
				fc.setSubjectIndividual(activeGoal.getGoalSchema().getIndividual());
				fc.setCausalProperty(Fabula.motivates);
				fc.setObjectIndividual(a.getIndividual());
				
				// Store in fabulas
				_fabulaCollector.addFabulaElement(a);
				_fabulaCollector.addFabulaCausality(fc);				
				
				return a;
			}
		} 
		return null;
	}	
	

	/**
	 * Finds goal schemas with established preconditions, and adopts instances of them (if possible).
	 */
	private void adoptNewGoals() {
		logger.info("Adopting new goals...");
		
		// Adopt goals according to strategy
		Set<GoalSchema> goals_to_adopt = goalAdoptionStrategy.selectGoalsToAdopt(getAgent().getCharacterURI());

		// Add fabula created by goal adoption to this collector
		_fabulaCollector.addAllFabulaElements(goalAdoptionStrategy.explainElements());
		_fabulaCollector.addAllFabulaCausalities(goalAdoptionStrategy.explainCausalities());
		
		for (GoalSchema gs: goals_to_adopt) {
			AdoptedGoalSchema ags = new AdoptedGoalSchema(gs, getAgent().getCharacterURI());
			String choice = null;

			if (gs != null) {
				
				choice = ags.getDramaticChoice();
				if (choice != null){
					
					choice = ags.getDramaticChoice().replace('\'', ' ').trim(); // [TODO] add time signature so that later the same choice can appear again 
					
					if(!logged_dramatic_choices.contains(choice)){ 				//dramatic choice not already logged in fabula
					
						if (!dramatic_choice_with_alternatives.containsKey(choice)){ //als choice nog niet is aangemaakt
							List<GoalSchema> l = new Vector<GoalSchema>();
			            	l.add(gs);
			            	encountered_alternatives.add(gs.getPrologDescription());
							dramatic_choice_with_alternatives.put(choice, l);		//maak aan + alternatief
						}
						else{
							if(!encountered_alternatives.contains(gs.getPrologDescription())){
								dramatic_choice_with_alternatives.get(choice).add(gs);	//anders alleen alternatief toevoegen
								encountered_alternatives.add(gs.getPrologDescription());
								logger.info("adding alternative: " + gs.getType());
							}
						}    
					}	
				}
			}	
			adoptGoal(gs);
		}
		
		// Recursive, to adopt goals that are motivated by goals adopted in this cycle
		if (! goals_to_adopt.isEmpty()) {
			adoptNewGoals();
		}
	}
	
	/**
	 * Creates a 'dramatic choice' (DC) fabula-element when goals with such a choice are adopted
	 * The element is placed in between the enabling elements of the adopted goals
	 * and the alterantives (goals) to choose from
	 * 
	 * Een dramatic choice wordt nu één keer per karakter gelogd in de fabula
	 * Door een timestamp toe te voegen bij het bijhouden van tegengekomen keuzes 
	 * zou dezelfde dramatic choice later in het verhaal nog een keer kunnen komen.
	 */
	
	private void logDramaticChoice(){
		
		if(!dramatic_choice_with_alternatives.isEmpty()) {
		
			Iterator<String> it = dramatic_choice_with_alternatives.keySet().iterator();
			String curr_dc = null;
			
			if (it != null){
				while ( it.hasNext() ){
					curr_dc = it.next();
					if (!logged_dramatic_choices.contains(curr_dc)){
						logger.info("met " + curr_dc + " wordt een DC-element aangemaakt");
						 
						 //create DC element
						DramaticChoice dc = FabulaFactory.createDramaticChoice(getAgent().getCharacterURI(), curr_dc);
					/*	for (int i = 0; i < dramatic_choice_with_alternatives.get(curr_dc).size(); i++){
							dc.addContentFabula(dramatic_choice_with_alternatives.get(curr_dc).get(i)); //add all gs associated with the choice as its content
						}*/
						_fabulaCollector.addFabulaElement(dc);
	
						//from IE to DC
						for (String ena_bel: PrologKB.getInstance().getEnablingFabulaElements(dramatic_choice_with_alternatives.get(curr_dc).get(0))) {
							logger.info("en nu de causality van " + ena_bel + " naar " + dc.getIndividual());
							FabulaCausality fc = new FabulaCausality();
							fc.setSubjectIndividual(ena_bel);
							fc.setObjectIndividual(dc.getIndividual());
							fc.setCausalProperty(Fabula.psi_causes);
							
							_fabulaCollector.addFabulaCausality(fc);
						}	
						//from DC to alternatives
						for (int i = 0; i < dramatic_choice_with_alternatives.get(curr_dc).size(); i++){
							FabulaCausality fc2 = new FabulaCausality();
							fc2.setSubjectIndividual(dc.getIndividual());
							fc2.setObjectIndividual(dramatic_choice_with_alternatives.get(curr_dc).get(i).getIndividual());
							fc2.setCausalProperty(Fabula.enables);
							
							_fabulaCollector.addFabulaCausality(fc2);	
						}
					}	
				}
				logged_dramatic_choices.add(curr_dc);
			}
		}	
	}
	

	/**
	 * Adopts given goal if possible (not already adopted, preconditions hold, etc)
	 * 
	 * @param goal the goal to adopt
	 * @return whether the goal was successfully adopted
	 */
	private boolean adoptGoal(GoalSchema goal) {
		
		// Try to adopt. Prolog handle checking if preconditions still hold, and if it was not already adopted.
		if (! PrologKB.getInstance().adoptGoal(goal.getPrologDescription())) {
			logger.severe("Could not adopt goal. Should not happen!\nGoal: " + goal.getPrologDescription());
			return false;
		}
		
		logger.info("Adopting goal " + goal.getIndividual() + " - "+ goal.getType());
		logger.fine("Schema of adopted goal: " + goal.getPrologDescription());
				
		AdoptedGoalSchema ags = new AdoptedGoalSchema(goal, getAgent().getCharacterURI());	
						
		goals.add(ags);
				
		// Assert each adopted goal as a causal anchor (i.e., assert that this goal is certainly part of a coherent fabula). 
		// Assumption: goals are never adopted if they do not causally tie in with the causal network so far.
		// FIXME: assert only if there is no causal anchor yet.
		logger.fine("Asserting " + goal.getIndividual() + " as causal anchor.");
		PrologKB.getInstance().assertCausalAnchor(goal.getIndividual());		
		
		return true;
	}
	
	/**
	 * Drops given goal from the deliberative process, and creates an outcome depending on given value
	 * @param goal the goal to drop
	 * @param outcome the outcome (positive, negative, etc) of the goal
	 */
	private void dropGoal(AdoptedGoalSchema goal, FabulaFactory.Outcome outcome) {
		logger.info("Dropping goal " + goal.getGoalSchema().getType() + " with outcome " + outcome);

		// Prolog side
		PrologKB.getInstance().dropGoal(goal.getGoalSchema().getPrologDescription());
		
		goals.remove(goal);
		//JB dramatic choice forces exactly one chosen goal.
		if(outcome == FabulaFactory.Outcome.positive && goal.getDramaticChoice()!= null){ //check for mutually exclusive alternatives
			
			Set<AdoptedGoalSchema> clone = new HashSet<AdoptedGoalSchema>(goals); //working copy of the adopted goals
			
			for (AdoptedGoalSchema ags: clone){
				if (ags.getDramaticChoice() == goal.getDramaticChoice()); {
					goals.remove(ags);
					//PrologKB.getInstance().dropGoal(ags.getGoalSchema().getPrologDescription());
					logger.info("Removing goal " + ags.getGoalSchema().getType() + " due to choice: " + ags.getDramaticChoice());
				}
			}
		}
		
		// If it is the active goal too, forget it as the active goal.
		if (goal == activeGoal) {
			activeGoal = null;
		}
		
		// Create outcome
		StoryOutcome so = FabulaFactory.createStoryOutcome(outcome, getAgent().getCharacterURI());
		so.setResolves(goal.getGoalSchema().getIndividual());

		// Create goal progress for PA
		GoalProgress gp = new GoalProgress();
		gp.setCharacter (m_characterAgent.getCharacterURI());
		gp.setGoal (goal.getGoalSchema ().getPrologDescription ());
		
		_fabulaCollector.addFabulaElement(so);

		Set<String> causes;
		switch (outcome) {
			case positive:		
				causes = PrologKB.getInstance().getGoalSuccessCausingFabulaElements(goal.getGoalSchema());
				logger.fine("Finding causes of positive outcome..." + causes.size() + " causes found.");	
				gp.setGoalstatus (GoalProgress.COMPLETED);
				break;
			case negative: 
				causes = PrologKB.getInstance().getGoalFailureCausingFabulaElements(goal.getGoalSchema());
				logger.fine("Finding causes of negative outcome..." + causes.size() + " causes found.");
				gp.setGoalstatus (GoalProgress.FAILED); 
				break;
			default: 
				causes = new HashSet<String>();
				gp.setGoalstatus (GoalProgress.UNKNOWN);
				break;
		}

		for (String cause: causes) {
			FabulaCausality fc = new FabulaCausality();
			fc.setSubjectIndividual(cause);
			fc.setObjectIndividual(so.getIndividual());
			fc.setCausalProperty(Fabula.psi_causes);
			_fabulaCollector.addFabulaCausality(fc);
		}
		
		AID[] receiver = new AID[1];
		receiver[0] = m_characterAgent.getPlotAgent ();
		logger.info("Sending goal progress to plot agent ");
		m_characterAgent.getAgent().addBehaviour(new SendInformBehaviour(m_characterAgent.getAgent(), receiver,
				gp));
	}

	/**
	 * Selects the goal that should currently be active, according to some heuristic.
	 * 
	 * Currently: look whether there is a goal that's more urgent than the current active goal
	 * 	If so: change to more urgent goal
	 * 	If not: continue with current active goal  
	 * When the current active goal is empty and nothing's more urgent, 
	 * 	the choice is based on the character-specific importance of the adopted goals
	 *  
	 * @return the goal that should be active
	 */
	public AdoptedGoalSchema selectActiveGoal() {
		logger.info("Selecting active goal...");								//eerst kijken we naar de urgency van de adopted goals
		Set<AdoptedGoalSchema> goal_options = new HashSet<AdoptedGoalSchema>();
		float curr_urgency = 0;													
		if (activeGoal != null) {											
			curr_urgency = activeGoal.getUrgency();								//de urgency van het huidige active goal
			logger.info("Current_urgency: " + curr_urgency);					
			if ( curr_urgency > 0.5) {											//als de urgency van het huidige active goal groter is dan de standaard 
				goal_options.add(activeGoal);									//waarde (0.5), dan wordt dit doel aan de goal options toegevoegd
			}
		}
		
		for (AdoptedGoalSchema ags: goals) {									//daarna kijken we of bij de andere adopted goals een hogere urgency zit
			// Biggest urgency up till now: this one becomes only option.
			if (ags.getUrgency() > curr_urgency && ags.getUrgency() > 0.5) { 	//when activegoal is null, then curr_urgency = 0: 
				goal_options.clear();											//only goals with urgency bigger then default should be added here
				curr_urgency = ags.getUrgency();
				goal_options.add(ags);
			
			// Equal urgency to a goal earlier found, but at least bigger urgency than the current active goal: add to list of options.
			} else if (ags.getUrgency() == curr_urgency 
						&& activeGoal != null 
						&& activeGoal.getUrgency() < curr_urgency) {
				goal_options.add(ags);
			}
		}
		
		// We should now have selected the set of goals with biggest urgency; choose randomly.
		if (! goal_options.isEmpty()) {
			return Chooser.randomChoice(goal_options);
	    } else {	// none of the goals is more urgent, base decision on importance (only when currently no active goal)
	    	if ( activeGoal == null && !goals.isEmpty()){
	    		AdoptedGoalSchema most_important_goal = determineMostImportantGoal();
	    					
	    		return most_important_goal;
	    	}
	    	else return activeGoal;	//else continue with current active goal
	    }
	}	
	
	/**
	 * JB
	 * Calculate importance of goals for the specific character
	 */
	
	public AdoptedGoalSchema determineMostImportantGoal(){
		Set<AdoptedGoalSchema> goal_options = new HashSet<AdoptedGoalSchema>();
	    	    
	    if (character_motivations.isEmpty()) {    			//when a character has no motivations no importance can be derived
	    	logger.info("character has no motivations!");
	        return Chooser.randomChoice(goals);  			//this results in a random choice from adopted goals
	    }
	    
	    //hier wordt een doorsnede gemaakt van alle motivaties in de goalbase en de motivaties van het karakter
	    //zodat alleen de voor het karakter relevante motivaties worden meegenomen in de importance-berekening
	    //wellicht plaatsen in aparte methode filterRelevantMotivations()
	    
		for (AdoptedGoalSchema ags: goals){
			if (! goals_with_relevant_pos_motivations.containsKey(ags)){							//als het goal nog niet is bekeken
				Vector<String> curr_pos_motivations;
				curr_pos_motivations = ags.getPosMotivationList(); 									//alle positieve motivation-types van dit goal
	            if (! curr_pos_motivations.equals("'null'") && ! curr_pos_motivations.isEmpty()){	//lijst is niet null of leeg
	            	Vector<String> l = new Vector<String>();										//zet de motivation-types van dit goal in een lijst
	            	l.addAll(curr_pos_motivations);
	            	
	            	for (int i = 0; i < curr_pos_motivations.size(); i++ ) {
	            		if ( ! character_motivations.contains(curr_pos_motivations.get(i)) ) {		//en verwijder de motivations die het karakter niet heeft
	            				l.remove(curr_pos_motivations.get(i));
	            		}
	            	}
	            	if (! l.isEmpty()) {
	            		goals_with_relevant_pos_motivations.put(ags, l);							//voeg goal + relevant motivations toe aan de map
	            	}	
	            }
			}   
		}

		for (AdoptedGoalSchema ags: goals){
			if (! goals_with_relevant_neg_motivations.containsKey(ags)){							//als het goal nog niet is bekeken
				Vector<String> curr_neg_motivations;
				curr_neg_motivations = ags.getNegMotivationList(); 									//alle negatieve motivations van dit goal
	            if (! curr_neg_motivations.equals("'null'") && ! curr_neg_motivations.isEmpty()){	//lijst is niet null of leeg
	            	Vector<String> l = new Vector<String>();										//zet de motivations van dit goal in een lijst
	            	l.addAll(curr_neg_motivations);													//zet de motivations van dit goal in een lijst
	            	for (int i = 0; i < curr_neg_motivations.size(); i++ ) {
	            		if ( ! character_motivations.contains(curr_neg_motivations.get(i)) ) {		//en verwijder de motivations die het karakter niet heeft
	            			l.remove(i);
	            		}
	            	}
	            	if (! l.isEmpty()) {
	            		goals_with_relevant_neg_motivations.put(ags, l);							//voeg goal + relevant motivations toe aan de map
	            	}
	            }
			}    
        }
		
		logger.info("goals_with_relevant_pos_motivations: " + goals_with_relevant_pos_motivations.toString());	//map met alle adopted goals met de positieve motivations van het karakter
		logger.info("goals_with_relevant_neg_motivations: " + goals_with_relevant_neg_motivations.toString());	//map met alle adopted goals met de negatieve motivations van het karakter
		
	    if (goals_with_relevant_pos_motivations.isEmpty() && goals_with_relevant_neg_motivations.isEmpty()) {	//wanneer leeg kan er wederom geen importance berekend worden
	    	logger.info("no goals have relevant motivations associated!");										//dit resulteert weer in een random choice uit de adopted goals
	        return Chooser.randomChoice(goals);  																		
	    }
	    else {   
	    	if ( ! desire_management.isEmpty() ){	//in order to calculate importance, we need the current desire-values
	    		setDesires(); 						//so we have to update previously reset desires to the current situation 
	    	}
	    	
			float curr_goal_importance = 0;
			float max_goal_importance  = -999;
	    
	    	for (AdoptedGoalSchema ags: goals){
	    		curr_goal_importance = calculateImportance(ags);	//calculate goal importance for each goal
	    		logger.info("curr_goal_importance: " + curr_goal_importance);
	    		if(curr_goal_importance > max_goal_importance) {
	    			max_goal_importance = curr_goal_importance;
	    			goal_options.clear();
	    			goal_options.add(ags);
	    		}
	    		else if (curr_goal_importance == max_goal_importance) {
	    			goal_options.add(ags);							//and add the most important goal(s) to goal_options
	    		}
	    	}	
	    	
	    	logger.info("max_goal_importance: " + max_goal_importance);
			logger.info("goal_options: " + goal_options);
			
			if (! goal_options.isEmpty()) {
				logger.info("choosing (between) most important goal(s)!");
				return Chooser.randomChoice(goal_options);	//if possible, choose an option
			} else {
				logger.info("not able to find an importance value!");
	        	return Chooser.randomChoice(goals);  		//this results in a random choice from adopted goals 							//no goal options so leave as is
			}
	    }	
	}
	
	/**
	 * JB
	 * Calculate importance-value for a given goal by balancing the relevant pos and neg-motivations associated
	 * 
	 * @return the importance of the given goal (float)
	 */
	
	public float calculateImportance(AdoptedGoalSchema goal){
		
    	List<String> pos_motivations = goals_with_relevant_pos_motivations.get(goal);
    	List<String> neg_motivations = goals_with_relevant_neg_motivations.get(goal);
		String curr_motivation;
		float curr_motivation_importance = 0;
		float pos_goal_importance 	 	 = 0;
		float neg_goal_importance 	 	 = 0;
		
		if(pos_motivations == null) {
			pos_goal_importance = 0;
		}
		else {
			for ( int i = 0; i < pos_motivations.size(); i++ ) {	//for each pos_motivation associated with this goal
				curr_motivation = pos_motivations.get(i);			//multiply strength with desire and add the corresponding value to the total pos_importance
				curr_motivation_importance = PrologKB.getInstance().getMotivationStrength(curr_motivation) * PrologKB.getInstance().getInterestDesire(curr_motivation);
				pos_goal_importance = pos_goal_importance + curr_motivation_importance;
			}	
		}
		
		if(neg_motivations == null) {
			neg_goal_importance = 0;
		}
		else {
			for ( int i = 0; i < neg_motivations.size(); i++ ) {	//for each neg_motivation associated with this goal
				curr_motivation = neg_motivations.get(i);			//multiply strength with desire and add the corresponding value to the total neg_importance
				curr_motivation_importance = PrologKB.getInstance().getMotivationStrength(curr_motivation) * PrologKB.getInstance().getInterestDesire(curr_motivation);
				neg_goal_importance = neg_goal_importance + curr_motivation_importance;
			}	
		}
		
		return pos_goal_importance - neg_goal_importance;			//calculate total goal importance by balacing pos and neg
	}
	
	/**
	 * JB
	 * Reset the desire-values of the motivations of a successfully completed goal
	 * Every motivation's desire is stored with the storytime on which it was reset
	 * For example (5 = [alcohol_1]) means that the desire for alcohol_1 is reset to zero (after completion of the associated goal) at time = 5
	 * Currently only positive motivations are reset (since they are temporarily satisfied when succesfully completing a chosen goal)
	 */
	
	public void resetDesires(AdoptedGoalSchema goal){
		int reset_time = StoryTime.getTime();
		int curr_key;
		logger.info(goal + " completed in round " + reset_time);
		List<String> to_be_reset = goals_with_relevant_pos_motivations.get(goal);
		//logger.info(to_be_reset.toString());
		if (to_be_reset != null && !to_be_reset.isEmpty()) {
			for (String motivation: to_be_reset){
				logger.info("resetting desire for " + motivation);
				PrologKB.getInstance().setDesire(motivation, 0);	//reset the desire of the associated interests to zero
				Iterator<Integer> it = desire_management.keySet().iterator();
				
				if (it != null){
					while ( it.hasNext() ){
						 curr_key = it.next();
						 if(desire_management.get(curr_key).contains(motivation)){	//if the motivation was reset earlier: remove it from the mapping
							 desire_management.get(curr_key).remove(motivation); 
						 }
						 if( desire_management.get(curr_key).isEmpty()){			//if this results in a mapping to an empty list: remove the whole mapping
							 desire_management.remove(curr_key);
						 }
					 }
				}	
			}
			
			desire_management.put(reset_time, to_be_reset);			//add a new mapping of the associated motivations with the new reset-time
			logger.info("storing info: " + desire_management.toString());
			goals_with_relevant_pos_motivations.remove(goal);		//!!!check of dit nodig is!!!
			goals_with_relevant_neg_motivations.remove(goal);		//!!!check of dit nodig is!!!
		}	
	}
	
	/**
	 * JB
	 * Set the desire-values of a character's motivations
	 * This method sets the desire to (current_time - reset_time)*0.01f
	 */ 
	
	public void setDesires(){
		Iterator<Integer> it = desire_management.keySet().iterator();
		int reset_time = 0;
		float diff = 0;
		int curr_time = StoryTime.getTime();
		
	    while ( it.hasNext() ){
	    	reset_time = it.next();
	    	diff = (curr_time - reset_time)*0.02f;
	    	List<String> motivations = desire_management.get(reset_time);
	    	for(String motivation: motivations) {
	    		logger.info("setting desire for " + motivation + " to " + diff);
	    		PrologKB.getInstance().setDesire(motivation, diff);
	    	}
	    }
	}
	
	
	/**
	 * Replaces the currently active goal by given goal
	 * @param ags the goal that should become active
	 */
	private void setActiveGoal(AdoptedGoalSchema ags) {
		activeGoal = ags;
		if (activeGoal != null) {
			logger.info("Setting active goal to: " + activeGoal.getGoalSchema().getType());
		} else {
			logger.info("Setting active goal to null.");
		}
	}
}
