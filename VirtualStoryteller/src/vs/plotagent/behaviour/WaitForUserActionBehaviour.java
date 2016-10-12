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

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Vector;
import java.util.logging.Logger;

import vs.IAgent;
import vs.communication.Operator;
import vs.communication.PerformOperator;
import vs.debug.LogFactory;
import vs.plotagent.BasicPlotAgent;
import vs.plotagent.IPlotAgent;
import vs.rationalagent.RationalAgent;

/**
 * Behaviour for waiting for an incoming action of given agent
 * 
 * @author swartjes
 */
public class WaitForUserActionBehaviour extends
		SimpleBehaviour {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1682888567186663058L;
	private Logger logger;
	private String m_selector;
	private BasicPlotAgent m_plotAgent;
	private boolean done = false;
	
	public WaitForUserActionBehaviour(BasicPlotAgent a, String charURI) {		
		super(a);
	
		m_plotAgent = a;
		m_plotAgent.registerWaitForUserActionBehaviour(this);
		logger = LogFactory.getLogger(this);
		m_selector = charURI;
	}
	
	public String getCharURI() {
		
		return m_selector;
	}
	
	public void action() {
		block();
	}
	
	public void stopWaiting() {
		done = true;
		restart();
	}
	
	public boolean done() {
		// Never done, this behaviour needs to be removed for the agent to continue
		return done;
	}

}
