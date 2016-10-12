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
package vs.plotagent.behaviour;

import jade.core.AID;
import jade.core.behaviours.SequentialBehaviour;

import java.util.logging.Logger;

import vs.debug.LogFactory;
import vs.plotagent.BasicPlotAgent;
import vs.plotagent.ui.AgentSelectActionEvent;
import vs.plotagent.ui.SelectActionEvent;

public class RequestInTurnBehaviour extends SequentialBehaviour {

	BasicPlotAgent plotAgent;
	Logger logger;
	
	public RequestInTurnBehaviour(BasicPlotAgent arg0) {
		super();
		logger = LogFactory.getLogger(this);
		plotAgent = arg0;
		
		for (AID receiver: plotAgent.getCharacterManager().getCastedCharacters()) {
			
			// TODO: if (agent is autonomous)
			if (plotAgent.getCharacterManager().isHumanControlled(receiver)) {
				plotAgent.fireEvent(new SelectActionEvent(this, plotAgent.getCharacterManager().getStoryWorldRepresentationForAgent(receiver)));
				addSubBehaviour(new WaitForUserActionBehaviour(plotAgent, plotAgent.getCharacterManager().getStoryWorldRepresentationForAgent(receiver)));
			} else {
				//when the character is AI controlled fire AgentSelectActionEvent instead of normal SelectActionEvent
				plotAgent.fireEvent(new AgentSelectActionEvent(this, plotAgent.getCharacterManager().getStoryWorldRepresentationForAgent(receiver)));
				addSubBehaviour(new InitiateRequestSelectActionBehaviour(plotAgent, receiver));
			}
		}
		
	}

}
