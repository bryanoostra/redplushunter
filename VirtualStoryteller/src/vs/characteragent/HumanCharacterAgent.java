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

import jade.gui.GuiEvent;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import vs.characteragent.behaviour.ExplainerBehaviour;
import vs.characteragent.ui.HumanCharacterAgentGui;
import vs.characteragent.ui.MapFrame;
import vs.communication.FabulaCausality;
import vs.communication.FabulaElement;
import vs.communication.Finished;
import vs.communication.IncomingSetting;
import vs.communication.NextRound;
import vs.communication.Operator;
import vs.communication.OperatorResult;
import vs.communication.OperatorStatus;
import vs.communication.StoryAction;
import vs.communication.StoryBelief;
import vs.communication.StoryPerception;
import vs.debug.LogFactory;
import vs.fabula.FabulaFactory;
import vs.knowledge.PrologKB;
import vs.knowledge.vocab.Fabula;
import vs.rationalagent.StoryTime;

/**
 * Human Character Agent
 * 
 * @author Thijs Alofs
 * 
 * largely based on BasicCharacterAgent by kruizingaEE
 *
 */

public class HumanCharacterAgent extends BasicCharacterAgent implements ICharacterAgent {

	private static final long serialVersionUID = 1771306973613301263L;

	//Already defined in BasicCharacterAgent
	//public static final int SET_CHARACTER_URI = 2000;
	//public static final int SHOWAGENTID = 2001;
	//public static final int SHOWACTIONS = 2002;
	//public static final int DOACTION = 2003;
	//public static final int SHOWPLANS = 2004;
	//public static final int SETINTENTIONS = 2005;
	//public static final int CREATEPLAN = 2007;
	//public static final int SAVEEPISODICMEMORY = 2008;
	//public static final String PROLOG_FILE = "load_character.pl";
	
	//Already defined in BasicCharacterAgent!
	//protected ActorProcess m_characterProcess;
	//protected IInterpretationModule m_interpretationModule;
	//protected EpisodicMemory m_episodicMemory;
	//protected ExplainerBehaviour m_explainerBehaviour;
	
	//Not needed in HumanCharacterAgent
	//private AID m_plotAgent = null;
	//private String m_characterURI = null;
	
	//Needed because instances in BasicCharacterAgent are not accessible
	private Set<FabulaElement> m_elements;
	private Set<FabulaCausality> m_causalities;
	private Logger logger;
	
	
	private MapFrame mapFrame;
	
	//creates the HumanCharacterAgentGui
	@Override
	protected void createGui() {
		HumanCharacterAgentGui hgui = new HumanCharacterAgentGui(this);
		mapFrame = new MapFrame(hgui);
		myGui = hgui;
	}

//	/* See ICharacterAgent */
//	public String getCharacterURI() {
//		return m_characterURI;
//	}

//	/* See ICharacterAgent */
//	public CharacterProcess getCharacterProcess() {
//		return m_characterProcess;
//	}

//	public EpisodicMemory getEpisodicMemory() {
//		return m_episodicMemory;
//	}

//	public IInterpretationModule getInterpretationModule() {
//		return m_interpretationModule;
//	}
	
//	/* See ICharacterAgent */
//	public AID getPlotAgent() {
//		return m_plotAgent;
//	}	

//	/**
//	 * Return the time (in rounds)
//	 */
//	public int getTime() {
//		return StoryTime.getTime();
//	}


//	public void handleCharacterInfo(CharacterInfo c) {
//		if (getCharacterURI() == null) {
//			setCharacterURI(c.getIndividual());
//		} else {
//			logger.warning("Character info received, but agent already committed to character ID " + getCharacterURI());
//		}
//	}

	/*
	 * Handle an incoming setting by adding it to the KB.
	 */
	@Override
	public void handleIncomingSetting(IncomingSetting is) {
		
		Set<StoryBelief> beliefs = getInterpretationModule().adopt(is.getSetting());
		
		/* Do we go on and interpret these beliefs? And form affective dispositions? It would seem
		 * strange since we pretend this setting element "has always been the case", but if we introduce
		 * a corpse on the deck, it would be strange if characters don't have any disposition towards it.
		 * 
		 * It seems like we need to either:
		 * 	-- put such dispositions in the effect of the improv operator
		 *  -- simulate a "fake" appraisal for a few steps to see what the affective disposition becomes. 
		 *  -- not put affective things in improv operators, only "normal" things, and let the affective
		 *     response occur only upon perceptions that are an effect of settings introduced by the Plot Agent.
		 *     
		 * For now, we don't do anything with affect; just be neutral.
		 */

		/* Incoming settings might enable new improvisations (that required improvisations of which the setting
		 * is now a result); go through the improvisation behaviour again.
		 * 
		 * TODO: this can be awkward; if there are more setting updates, i.e. two effect triples of one improvisation action,
		 * the planner will make a new plan for each of them. This could potentially mean that there is temporarily no plan to make.
		 * Example: 
		 * (1) you can create a crate if there is no crate yet, and the effect is that there is now a crate on the ship.
		 * (2) you can create a bottle in a crate when there is a crate somewhere.
		 * After performing (1) and getting the setting update that there is now a crate (followed by: the crate is on the ship), 
		 * it invalidates (1), making it impossible to get the crate on the ship (although a subsequent setting change from the 
		 * execution of (1) will turn out to resolve that)
		 * 
		 * This problem is removed when we use the continuous planner. For now, make sure that the effects are ordered such that they don't
		 * invalidate preconditions.
		 */
		logger.info("Received new setting; updating plan.\nSetting: " + is.getSetting().getContentTriple());

		// No appraisal, only coping
		/*myGui.writeConsole("Re-adjusting plan after new setting information.");
		getCharacterProcess().getDeliberativeLayer().cope();*/
		
		// Shifted responsibility of skipping appraisal to whether or not a framing operator was accepted
		// by this character. Avoiding appraisal once is not going to do the trick; the agent will just appraise
		// the setting in the next cycle.
		
		//XXX Thijs: following not needed in human character agent because there are no plans?
		//getCharacterProcess().appraise();
		//getCharacterProcess().cope();	
	}

	/*
	 * Next round is responsible to update the map to the new state of the world 
	 */
	@Override
	public void handleNextRound(NextRound n) {
		int round = n.getRoundNumber();
		myGui.writeConsole("");
		myGui.writeConsole("--- Next round starting (time = " + round + ") ---");
		
		// The explainer behaviour tracks state (e.g., "what did I already send to the plot agent?")
		// hence, restart, in stead of adding a new one every time.
		
		//Collect fabula elements and causalities from the character agent, and send them to the Plot Agent
		if (m_explainerBehaviour != null) {
			m_explainerBehaviour.reset();
			addBehaviour(m_explainerBehaviour);
		} else {
			m_explainerBehaviour = new ExplainerBehaviour(this);
			addBehaviour(m_explainerBehaviour);
		}

		HumanCharacterAgentGui hgui = (HumanCharacterAgentGui)myGui;

		//XXX Thijs: moved to SelectAtion(), is this a good idea? 
//		//update who is at which location in the world
//		Hashtable<String,Vector<String>> location_inhabitants = PrologKB.getInstance().getLocationsInhabitants();
//		hgui.updateLocationsInhabitants(location_inhabitants);
//		
//		//also update possible actions
//		Vector<String> allPossibleActions = PrologKB.getInstance().getAllPossibleActions(getCharacterURI());
//		hgui.setPossibleActions(allPossibleActions);
		
		//update the round number in the GUI
		mapFrame.updateRoundNumber(round);
	}

	/*
	 * Handle an incoming result of an operator (finished, aborted, etc)
	 */
	@Override
	public void handleOperatorResult(OperatorResult or) {
		
		ExecutionState.getInstance().registerOperatorResult(or);
		
		Operator o = or.getOperator();
		OperatorStatus os = or.getStatus();
				
		String orText = PrologKB.getInstance().narrate(o.getPrologDescription());
		orText = orText.replaceAll("'", "");
		
		logger.info("Received operator result: " + orText);
		
		int orRoundNumber = StoryTime.getTime();
	
		//2009-12-31: every Operator appears to be unsuccessful, even if it is successful
		//but OperatorStatus DOES contain the result :-)
		if(os instanceof Finished) {
			mapFrame.addOperatorResult(orRoundNumber, orText, true);
		} else {
			//give feedback about unsuccessful operators? all or only those initiated by user?
			mapFrame.addOperatorResult(orRoundNumber, orText, false);
			logger.severe("An operator failed, aborted, or was otherwise unsuccessful: Text=" + orText + " Prolog=" + o.getPrologDescription());
		}
	}
	
	/*
	 * Handle a perception by adding it to the KB if it is positive and deleting
	 * it if it is negative. It expects a single fact.
	 */
	@Override
	public void handlePerception(StoryPerception p) {
		// Make interpretations for the agent
		getInterpretationModule().interpret(p);
	}
	
	/*
	 * Handles the selection of an action to perform.
	 */
	@Override
	public StoryAction handleSelectAction() {
		// This is the place for appraisal and coping: we have received all perceptions and setting changes.
		// TODO: there are race conditions between appraisal/coping and processing the fabula that the agent has
		//		produced. So appraisal/coping might or might not be using information about newly adopted goals etc.
		//		It is desired to make sure this information is _always_ used. But not sure how.
		
		//XXX Thijs: we are human so we don't need this?
//		myGui.writeConsole("Reactive and deliberative appraisal.");
//		getCharacterProcess().appraise();
//		myGui.writeConsole("Reactive and deliberative coping.");
//		getCharacterProcess().cope();
		
		// Already performing an action?
		if (ExecutionState.getInstance().currentAction() != null) {
			// I am already doing an action.
			// TODO: this is a speedup shortcut; it would be nicer to do selectAction always and just see if the same action was selected.
			//		 this would take into account a changed environment instead of blindly pursuing the action
			logger.info("Not choosing a new action: already performing an action");
			myGui.writeConsole("Select action: already performing an action (namely " + ExecutionState.getInstance().currentAction().getIndividual() + ")");
			return null;
		} 
		
		//HumanCharacterAgentGui hgui = (HumanCharacterAgentGui)myGui;
		String characterURI = getCharacterURI();
		PrologKB pkb = PrologKB.getInstance();
		
		//1 FIRST inform GUI of all possible 'TransitMove' actions
		Hashtable<String,String> location_action = pkb.getAllPossibleTransitMoveActions(characterURI);
		mapFrame.updatePossibleTransitMoveActions(location_action);
		
		//2 THEN update who is at which location in the world
		Hashtable<String,Vector<String>> location_inhabitants = pkb.getAllLocationsInhabitants();
		mapFrame.updateLocationsInhabitants(location_inhabitants);
		
		//3 FINALLY update GUI with all possible actions
		Hashtable<String, Vector<String>> allPossibleActionsByType = pkb.getAllPossibleActionsGroupedByType(characterURI);
		mapFrame.updatePossibleActions(allPossibleActionsByType);
			
		//debug:
		if (location_action.isEmpty()) {
			System.out.println("result of getAllPossibleTransitMoveActions() is empty!?");
		} else {
			System.out.println("result of getAllPossibleTransitMoveActions() contains at least one action!");
		}
		
		StoryAction selectedAction = null;
		
		//System.out.println("all locations: " + pkb.getAllLocations().toString());
		//System.out.println("all characters: " + pkb.getAllCharacters().toString());
		
		//System.out.println("possibleimprovisations " + pkb.getAllPossibleImprovisations().toString());
				
		//System.out.println("possiblegoals" + pkb.getPossibleGoals(characterURI).toString());
		
		//niets: System.out.println(pkb.selectGoalRules(characterURI).toString());
		//reactive: System.out.println(pkb.selectReactiveActionRules(characterURI).toString());

		Set<JustifiableGoalSchema> jgs = pkb.getGoalsPossibleAfterPlan(characterURI);
				
		//ExecutionState.getInstance().
		
		String action = mapFrame.getUserSelection();
			
		if(action!=null && !"".equals(action)) {
			selectedAction = FabulaFactory.createAction(action, characterURI);
		}
		
		// No action selected
		if (selectedAction == null) {
			// I don't want to do an action because I wouldn't know what to do.
			logger.info("Not choosing a new action: I have no idea what to do.");
			myGui.writeConsole("Select action: no idea what to do.");

		} else {

			// Tell execution state that this is the operator we're performing.
			ExecutionState.getInstance().registerPerformingOperator(selectedAction);
			
			// Log to fabula
			// TODO: maybe move to handleOperatorResult? Also save the causality till the action is done? 
			m_elements.add(selectedAction);
			
			// Find enablements of action in the form of a set of Individuals of fabula elements that enable action
			Set<String> enablements = PrologKB.getInstance().getEnablingFabulaElements(selectedAction);
			
			for (String enabler: enablements) {
				FabulaCausality enables = new FabulaCausality();
				enables.setSubjectIndividual(enabler);
				enables.setObjectIndividual(selectedAction.getIndividual());
				enables.setCausalProperty(Fabula.enables);
				m_causalities.add(enables);
			}
		
			myGui.writeConsole("Select action: " + selectedAction.getIndividual() + " (" + selectedAction.getType() + ")");
			logger.info("Select action: " + selectedAction.getIndividual() + " (" + selectedAction.getType() + ")");
		}
		
		return selectedAction;
	}
		
	/**
	 * The ActorAgentGui sends GuiEvents, which are handled by onGuiEvent
	 */
	@Override
	protected void onGuiEvent(GuiEvent ev) {
		// let superclass handle event first
		super.onGuiEvent(ev);
	}
	
	public boolean setCharacterURI(String characterURI) {
		boolean sup = super.setCharacterURI(characterURI);
					
		//set the characterName and thereby storyDomain. 
		//Make the mapInterface window visible.
		mapFrame.setHumanCharacterName(characterURI);

		//System.out.println("setCharacterURI() domain=" + getStoryDomain().getName());
		
		//thijstest: wat is dit thijs? 
		//PrologKB.getInstance().assertCausalAnchor(characterURI);
		
		return sup;
	}
	
//	/* See ICharacterAgent */
//	public void setPlotAgent(AID agent) {
//		myGui.writeConsole("Setting PlotAgentAID from " + m_plotAgent + " to " + agent);
//		m_plotAgent = agent;
//	}
	
	/*
	 * Same as in BasicCharacterAgent
	 * Needed because instances in BasicCharacterAgent are not accessible
	 */
	@Override
	public Set<FabulaCausality> explainCausalities() {
		//Explaining the agent means explaining the interpretation module, and the agent processes
		m_causalities.addAll(getInterpretationModule().explainCausalities());
		m_causalities.addAll(getCharacterProcess().explainCausalities());
		return m_causalities;
	}
	
	/*
	 * Same as in BasicCharacterAgent
	 * Needed because instances in BasicCharacterAgent are not accessible
	 */
	@Override
	public Set<FabulaElement> explainElements() {
		//Explaining the agent means explaining the interpretation module, and the agent processes
		m_elements.addAll(getInterpretationModule().explainElements());
		m_elements.addAll(getCharacterProcess().explainElements());
		return m_elements;
	}	

	/*
	 * override vs.characteragent.BasicCharacterAgent.setup
	 * (which overrides jade.core.Agent.setup)
	 */
	@Override
	public void setup() {
		super.setup();
		
		logger = LogFactory.getLogger(this);
		
		//after super.setup, the story domain should be set
		String domainName = getStoryDomain().getName();
		//System.out.println("setup " + domainName);
		logger.info("We now know the domain, show map of: " + domainName);
		
		PrologKB pkb = PrologKB.getInstance();
		//System.out.println("HCA.setup() all locations: " + pkb.getAllLocations().toString());
		//System.out.println("HCA.setup() all characters: " + pkb.getAllEntitiesAtAnyLocation().toString());
		
		//retrieve everybody (everything?) that is at any locations (for now only characters)
		Vector<String> characters = PrologKB.getInstance().getAllEntitiesAtAnyLocation();
		
		mapFrame.setDomainShowMap(domainName, characters);
		
		m_elements = new HashSet<FabulaElement>();
		m_causalities = new HashSet<FabulaCausality>();
				
		logger.info("Succesfully set up a HUMAN character agent.");
	}
	
//	// Show a list in the gui
//	private void showList(Vector<String> list, String typeName) {
//		myGui.writeConsole("This agent has " + list.size() + " " + typeName, false);
//		if (list.isEmpty()) {
//			myGui.writeConsole(".");
//		} else {
//			myGui.writeConsole(":");
//			for (int i = 0; i < list.size(); i++) {
//				myGui.writeConsole(list.elementAt(i));
//			}
//		}
//		;
//	}
}