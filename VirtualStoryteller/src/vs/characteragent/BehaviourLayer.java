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

import java.util.Set;

import vs.IExplainable;
import vs.communication.FabulaCausality;
import vs.communication.FabulaElement;
import vs.communication.StoryAction;

/**
 * A behaviour layer is any character agent layer that produces in-character behaviour. 
 * This abstract class defines the structure of any behaviour layer:
 * - It should be able to appraise the current state to update its "behaviour context"
 * - It should be able to cope, make decisions based on the current "behaviour context"
 * - It should be able to select a new action on request
 * 
 *  Implementing IExplainable, to log its decisions in the fabula.
 *  
 *  NOTE: this class is by no means final, maybe it is smarter to tie appraisal/coping/selecting actions to behaviour layers
 *  in other ways than the appraise(), cope() and selectAction() methods defined in this class.
 * 
 * @author swartjes
 *
 */
public abstract class BehaviourLayer implements IExplainable {
	
	protected ICharacterAgent m_characterAgent;
	protected EpisodicMemoryCollector _fabulaCollector;
	
	public BehaviourLayer(ICharacterAgent owner) {
		m_characterAgent = owner;
		_fabulaCollector = new EpisodicMemoryCollector(m_characterAgent.getEpisodicMemory());
	}
	
	/**
	 * Retrieves the character agent to which this behaviour layer belongs.
	 */
	public ICharacterAgent getAgent() {
		return m_characterAgent;
	}
	
	/**
	 * Updates the behaviour context (e.g. emotions, thoughts) based on the current agent state 
	 */
	public abstract void appraise();

	/**
	 * Prepares decisions concerning the selection of new behaviour (e.g. performs planning) based on the appraisal. 
	 */
	public abstract void cope();
	
	/**
	 * Selects an action
	 */
	public abstract StoryAction selectAction();
	
	
	/* See IExplainable */
	public Set<FabulaElement> explainElements() {
		return _fabulaCollector.explainElements();
	}
	
	/* See IExplainable */
	public Set<FabulaCausality> explainCausalities() {
		return _fabulaCollector.explainCausalities();
	}

}
