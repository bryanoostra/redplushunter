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
package vs.plotagent.ui.multitouch.controller;

import jade.gui.GuiEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import vs.communication.Operator;
import vs.communication.OperatorResult;
import vs.debug.LogFactory;
import vs.fabula.FabulaFactory;
import vs.plotagent.BasicPlotAgent;
import vs.plotagent.ui.AgentSelectActionEvent;
import vs.plotagent.ui.DomainLoadedEvent;
import vs.plotagent.ui.NextRoundEvent;
import vs.plotagent.ui.OperatorResultEvent;
import vs.plotagent.ui.SelectActionEvent;
import vs.plotagent.ui.TurnEvent;
import vs.plotagent.ui.multitouch.model.DoNothingAction;
import vs.plotagent.ui.multitouch.model.InterfaceModel;
import vs.plotagent.ui.multitouch.model.PossibleAction;
import vs.plotagent.ui.multitouch.model.StoryModel;
import vs.plotagent.ui.multitouch.model.InterfaceModel.InterfaceModelChange;
import vs.plotagent.ui.multitouch.view.VSTMultitouchFrame;
import vs.rationalagent.RationalAgent;
import vs.rationalagent.StoryAgentEventListener;
import vs.rationalagent.ui.StoryAgentEvent;


/**
 * Controller of the VST multitouch application:
 * - Provides read access to the models
 * - Updates the models
 * - Responds to agent events
 * 
 * @author swartjes
 *
 */
public class VSTMultitouchController implements ActionListener, StoryAgentEventListener, Observer {

	private Logger logger;

	protected boolean debug;
	
	private InterfaceModel interfaceModel;
	private StoryModel storyModel;
	
	private RationalAgent myAgent;	
	
	private VSTMultitouchFrame mtFrame;

	public VSTMultitouchController(RationalAgent agent) {
		myAgent = agent;
		
		// Listen to story events
		myAgent.addEventListener(this);
				
		interfaceModel = new InterfaceModel();	
		storyModel = new StoryModel();
		
		interfaceModel.addObserver(this);
		
		//initialize the logger
		logger = LogFactory.getLogger(this);
	}
	
	public StoryModel getStoryModel() {
		return storyModel;
	}
	
	public InterfaceModel getInterfaceModel() {
		return interfaceModel;
	}
		
	public void showMultitouchMap() {
		if (mtFrame == null) {
			logger.info("Creating new multitouch map...");
			mtFrame = new VSTMultitouchFrame(interfaceModel.getDomain(), this);
/*			logger.info("Setting controller...");
			mtFrame.getVSTMultitouchInterface().setController(this);
*/		} else {
			mtFrame.setVisible(true);
		}
	}
	
	/**
	 * Handle story events
	 */
	public void onStoryAgentEvent(StoryAgentEvent sae) {
		// operator result
		if (sae instanceof DomainLoadedEvent) {
			// Domain is loaded
			logger.info("Event: domain loaded.");
			
			interfaceModel.setDomain(((DomainLoadedEvent)sae).getDomainName());
						
			interfaceModel.updateLocationsInhabitants();
			
		} else
		if (sae instanceof OperatorResultEvent) {			
			// Received operator result
			// might have changed locations of characters, and actions that are possible
			logger.info("Event: operator result.");
			
			OperatorResultEvent ore = (OperatorResultEvent) sae;
			OperatorResult or = ore.getOperatorResult();
			
			interfaceModel.updateLocationsInhabitants();
			interfaceModel.updatePossibleActions();
			
			// Also advances the story
			storyModel.addOperatorResult(or);
			
		} else
		if (sae instanceof SelectActionEvent) {
			// Action must be selected: determine actions that are possible for character
			
			// TODO: sometimes, somehow, the startUserTurn model change seems to happen BEFORE setting the new human character name.
			// result: interface widget contains buttons of new character, but is rope-tied to old character. Confusing!
			// Hmm, I never experienced this (thijs)
			
			logger.info("Event: asking user to select action.");
			
			interfaceModel.setHumanCharacterName(((SelectActionEvent)sae).getCharacterURI());
			interfaceModel.updatePossibleActions();
			interfaceModel.setUserTurn(true);
		} else
		if (sae instanceof AgentSelectActionEvent) {
			// actions that are possible for character
			logger.info("Event: asking AGENT to select action.");
			//interfaceModel.setHumanCharacterName(((AgentSelectActionEvent) sae).getCharacterURI());
			interfaceModel.setCurrentlyThinkingAICharacterName(((AgentSelectActionEvent) sae).getCharacterURI());
			interfaceModel.notifyObservers(InterfaceModelChange.startAgentTurn);
		} else 
		if (sae instanceof NextRoundEvent) {
			// Next round
			logger.info("Event: next round.");
			
			if (! interfaceModel.isStoryStarted()) {
				interfaceModel.setStoryStarted(true);
			}
		} else 
		if (sae instanceof TurnEvent) {
			TurnEvent te = (TurnEvent) sae;
			logger.info("Event: turn " + te.getTurnStatus());
			
			if (te.getTurnStatus().equals(TurnEvent.TurnStatus.started)) {
				interfaceModel.turnStarted(te.getCharacterURI());
			} else if (te.getTurnStatus().equals(TurnEvent.TurnStatus.ended)) {
				interfaceModel.turnEnded(te.getCharacterURI());
			}
		}
	}
	
	public void startStory() {
		GuiEvent msg = new GuiEvent(null, BasicPlotAgent.START_STORY);
		myAgent.postGuiEvent(msg);
	}
	
	public void humanControl(String charURI) {
		GuiEvent msg = new GuiEvent(null, BasicPlotAgent.TAKE_CONTROL);
		msg.addParameter(charURI);
		myAgent.postGuiEvent(msg);
	}
	
	public void computerControl(String charURI) {
		GuiEvent msg = new GuiEvent(null, BasicPlotAgent.RELEASE_CONTROL);
		msg.addParameter(charURI);
		myAgent.postGuiEvent(msg);
	}

	
	@Override
	//called when the execute Button or skipRound CheckBox are 'clicked' 
	public void actionPerformed(ActionEvent e) {
    	//(do not) give superclass opportunity to handle event
    	//super.actionPerformed(e);
    	Object source = e.getSource();

    }

	
	public void writeConsole(String message) {
		GuiEvent msg = new GuiEvent(null, RationalAgent.CONSOLE_MSG);
		msg.addParameter(message);
		myAgent.postGuiEvent(msg);
	}	
	
	public void storyActionChosen(String characterURI, PossibleAction storyAction) {
		Operator o = null;
		
		//an action has been chosen, non-move categories should no longer stay hidden (in case they are)
		getInterfaceModel().hideNonMoveCategories(false);	

		if (! (storyAction instanceof DoNothingAction)) {
			
			o = FabulaFactory.createUnknownOperator(storyAction.getPrologDescription(), storyAction.getCharacterURI());
		}

		//action chosen, reset selected action or not?
		//getInterfaceModel().selectDoNothingAction();

		// TODO: the plot agent should tell the controller (by means of an event) 
		// that the user turn ended. It depends on the type of operator; choosing 
		// framing operators might not mean the end of your turn.
		// (maybe what happens now is that the user turn is given up and immediately
		//  given back by the Plot Agent?)
		getInterfaceModel().setUserTurn(false);
		
		// Tell Plot Agent what action was chosen.
		GuiEvent msg = new GuiEvent(null, BasicPlotAgent.ACTION_CHOSEN);
		msg.addParameter(characterURI);
		msg.addParameter(o);
		myAgent.postGuiEvent(msg);
	}
	
	/**
	 * Handles model updates.
	 * 
	 */
	public void update(Observable obs, Object o) {
		
		if (o instanceof InterfaceModel.InterfaceModelChange) {
			InterfaceModel.InterfaceModelChange change = (InterfaceModel.InterfaceModelChange) o;
			
			logger.info("Interface model changed: " + change);
			
			switch (change) {
			case storyStarted:
				
				//boolean tmp = MultitouchInterfaceSettings.ALL_CHARACTERS_HUMAN_ON_STARTUP;
				
				//Making characters humancontrolled on startup is now handled in the CharacterView constructor
//				if (MultitouchInterfaceSettings.ALL_CHARACTERS_HUMAN_ON_STARTUP) {
//					logger.info("Making all characters human-controlled...");
//					Set<String> inhabitants = new HashSet<String>();			
//					for (Vector<String> inhabURIs: getInterfaceModel().getLocationInhabitants().values()) {
//						inhabitants.addAll(inhabURIs);
//					}
//					// Set all characters to human controlled.
//					// ONLY if the GUI is shown! Otherwise the VST cannot run without the GUI
//					if (mtFrame != null && mtFrame.isVisible()) {
//						for (String inhURI: inhabitants) {
//							// Assumption: if URI is not a character (but e.g. an object), this
//							//  	action will have no effect.
//							logger.fine("Making human controlled: " + inhURI);
//							humanControl(inhURI);
//						}
//					}
//				}
			}
		}
	}

}
