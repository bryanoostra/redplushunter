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
package vs.plotagent.ui;

import vs.communication.NextRound;
import vs.communication.OperatorResult;
import vs.rationalagent.ui.StoryAgentEvent;

public class TurnEvent extends StoryAgentEvent {
	
	public static enum TurnStatus {started, ended};
	
	private String m_charURI;
	private TurnStatus m_status;
	
	public TurnEvent(Object src, String characterURI, TurnStatus status) {
		super(src);
		m_charURI = characterURI;
		m_status = status;
	}
	
	public String getCharacterURI() {
		return m_charURI;
	}
	
	public TurnStatus getTurnStatus() {
		return m_status;
	}

}
