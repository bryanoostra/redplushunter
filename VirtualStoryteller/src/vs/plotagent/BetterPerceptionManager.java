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
package vs.plotagent;

import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import vs.communication.IncomingPerception;
import vs.communication.IncomingSetting;
import vs.communication.InferenceOperator;
import vs.communication.Operator;
import vs.communication.StoryAction;
import vs.communication.StoryEvent;
import vs.communication.StoryPerception;
import vs.communication.StorySettingElement;
import vs.communication.FramingOperator;

import vs.communication.RDFtriple;

import vs.IAgent;
import vs.communication.FabulaCausality;
import vs.communication.FabulaElement;
import vs.communication.Finished;
import vs.communication.OperatorResult;
import vs.debug.LogFactory;
import vs.fabula.FabulaCollector;
import vs.fabula.FabulaFactory;
import vs.knowledge.PrologKB;
import vs.knowledge.vocab.Fabula;
import vs.rationalagent.behaviour.SendInformBehaviour;

/**
 * Better implementation of perception handling.
 * Better separation of concerns between creating setting/perception, and registering result of operator. 
 * In comparison to BasicPerceptionManager, this one doesn't start interpreting the operator result as setting/perceptions 
 * up till the point where characters need to be informed. Also easier to install perception rules since it is clearer where
 * this should be done.
 *  
 * NOTE: Experimental; if bugs occur, one might use BasicPerceptionManager instead.
 * @author swartjes
 *
 */
public class BetterPerceptionManager implements IPerceptionManager {

	private Vector<OperatorEffects> _operatorHistory;
	private Map<AID, Vector<OperatorEffects>> _aware;
	private Map<FramingOperator,Vector<StorySettingElement>> _settingMap;
	
	protected Logger logger;
	private final IPlotAgent m_ownerAgent;
	private final FabulaCollector m_fabulaCollector;
	
	/**
	 * Constructor
	 * @param owner plot agent that owns this perception manager
	 */
	public BetterPerceptionManager(IPlotAgent owner) {
		logger = LogFactory.getLogger(this);
		m_ownerAgent = owner;
		m_fabulaCollector = new FabulaCollector();
		
		_operatorHistory = new Vector<OperatorEffects>();
		_aware = new HashMap<AID, Vector<OperatorEffects>>();
		_settingMap = new HashMap<FramingOperator,Vector<StorySettingElement>>();
	}
	
	@Override
	public void registerOperatorResult(OperatorResult or) {
		
		// Only proceed to create perceptions / settings if we are dealing with a finished operator
		if (! (or.getStatus() instanceof Finished)) {
			logger.info("Operator " + or.getOperator().getIndividual() + " not finished.");
			return;
		}
		
		if (	   or.getOperator() instanceof StoryAction 
				|| or.getOperator() instanceof StoryEvent) {
			// These are the operators with a duration. Actions are already in the fabula but now their
			// end time is known. So add again to ensure this gets logged.
			m_fabulaCollector.addFabulaElement(or.getOperator());
		}
		
		if (or.getOperator() instanceof StoryEvent) {
			// Find SettingElements that enabled the event
			for (String setting: PrologKB.getInstance().getSettingsEnablingEvents((StoryEvent) or.getOperator())) {
				FabulaCausality fc = new FabulaCausality();
				fc.setSubjectIndividual(setting);
				fc.setObjectIndividual(or.getOperator().getIndividual());
				fc.setCausalProperty(Fabula.enables);
				m_fabulaCollector.addFabulaCausality(fc);
			}
		}
		
		Vector<RDFtriple> effects = PrologKB.getInstance().getOperatorEffects(or.getOperator().getPrologDescription());
		OperatorEffects oe = new OperatorEffects(or.getOperator(), effects);
		_operatorHistory.add(oe);
		logger.info("Adding result of finished operator " + or.getOperator().getIndividual() + " to operator history:\n" + effects);
	}
	
	@Override
	public void informCharacters() {

		for (AID character: m_ownerAgent.getCharacterManager().getCastedCharacters()) {
			// Prepare the aware map.
			if (_aware.get(character) == null) {
				_aware.put(character, new Vector<OperatorEffects>());
			}
			
			// Determine perceptions and setting elements (changes _aware)
			Vector<StoryPerception> perceptions = determinePerceptions(character);
			Vector<StorySettingElement> setting_elements = determineSettingElements(character);
			
			// Send them
			sendPerceptions(perceptions, character);
			sendSettingElements(setting_elements, character);
		}
	}	
	
	/**
	 * Determines perceptions for given character
	 * @param character character to determine perceptions for
	 * @return a list of perceptions
	 */
	private Vector<StoryPerception> determinePerceptions(AID character) {
		Vector<StoryPerception> perceptions = new Vector<StoryPerception>(); 
		
		for (OperatorEffects oe: _operatorHistory) {
			if ( _aware.get(character).contains(oe)) {
				logger.fine("Character " + character.getLocalName() + " already aware of operator " + oe.getOperator().getIndividual());
			} else {
				if (oe.getOperator() instanceof StoryAction || oe.getOperator() instanceof StoryEvent) {
					
					// Make a perception for each RDF triple that is an effect of the action/event
					for (RDFtriple t: oe.getEffects()) {
						ArrayList l = new ArrayList();
						l.add(t);
						StoryPerception p = FabulaFactory.createPerception(
								m_ownerAgent.getCharacterManager().getStoryWorldRepresentationForAgent(character), 
								l, null);
						
						perceptions.add(p);
						m_fabulaCollector.addFabulaElement(p);									
						FabulaElement p_cause = oe.getOperator();

						if (p_cause != null) {
							FabulaCausality fc = new FabulaCausality();
							fc.setSubjectIndividual(p_cause.getIndividual()); 
							fc.setCausalProperty(Fabula.phi_causes);
							fc.setObjectIndividual(p.getIndividual());
							m_fabulaCollector.addFabulaCausality(fc);
						} else {
							logger.severe("Perception without causality: " + p);
						}

					}
					
					// Make perception of the fabula of the operator itself (e.g. I see you walked)
					ArrayList l = new ArrayList();
					l.add(oe.getOperator());
					StoryPerception p = FabulaFactory.createPerception(
							m_ownerAgent.getCharacterManager().getStoryWorldRepresentationForAgent(character),
							null, l);
					
					perceptions.add(p);
					m_fabulaCollector.addFabulaElement(p);									
					FabulaElement p_cause = oe.getOperator(); // Perception of operator is caused by operator itself

					if (p_cause != null) {
						FabulaCausality fc = new FabulaCausality();
						fc.setSubjectIndividual(p_cause.getIndividual()); 
						fc.setCausalProperty(Fabula.phi_causes);
						fc.setObjectIndividual(p.getIndividual());
						m_fabulaCollector.addFabulaCausality(fc);
					} else {
						logger.severe("Perception without causality: " + p);
					}
					
					// Register agents awareness of the operator's effect
					_aware.get(character).add(oe);					
				} 
			}
		}
		logger.info("Determined perceptions for character " + character.getLocalName() + "..." + perceptions.size() + " perceptions.");
		return perceptions;
	}
	
	/**
	 * Determines setting elements for given character
	 * 
	 * @param character character to determine setting elements for
	 * @return a list of setting elements
	 */
	private Vector<StorySettingElement> determineSettingElements(AID character) {
		Vector<StorySettingElement> setting_elements = new Vector<StorySettingElement>();
		for (OperatorEffects oe: _operatorHistory) {
			if (_aware.get(character).contains(oe)) {
				logger.fine("Character " + character.getLocalName() + " already aware of operator " + oe.getOperator().getIndividual());
			} else {
				if (oe.getOperator() instanceof FramingOperator) {
					// TEST: framing operator needs to be scoped "all", OR "personal" but requested by self
					if (PrologKB.getInstance().isFramingScopeAll(oe.getOperator().getPrologDescription()) ||  
							(PrologKB.getInstance().isFramingScopePersonal(oe.getOperator().getPrologDescription()) &&  
									oe.getOperator().getCharacter().equals(m_ownerAgent.getCharacterManager().getStoryWorldRepresentationForAgent(character)))) {
						
						// Either reuse already created setting element, or make new one
						Vector<StorySettingElement> alreadyCreatedElements = _settingMap.get(oe.getOperator());
						if (alreadyCreatedElements != null) {
							setting_elements.addAll(alreadyCreatedElements);
						} else {
							
							Vector<StorySettingElement> setting_elements_of_operator = new Vector<StorySettingElement>();
							
							// Make a setting element for each RDF triple
							for (RDFtriple eff: oe.getEffects()) {
								
								List contTriple = new ArrayList();
								contTriple.add(eff);
								StorySettingElement newElement = FabulaFactory.createSettingElement("plotagent", contTriple, null );
								m_fabulaCollector.addFabulaElement(newElement);
								setting_elements_of_operator.add(newElement);
								
								logger.info("Created setting element for effect: " + eff + "\nof framing operator: " + oe.getOperator().getIndividual());
							}

							// Remember for reuse
							_settingMap.put((FramingOperator)oe.getOperator(), setting_elements_of_operator);
							setting_elements.addAll(setting_elements_of_operator);
						}
					} else {
						logger.info("Framing operator " + oe.getOperator().getIndividual() + " not scoped 'all' or personal to " +  m_ownerAgent.getCharacterManager().getStoryWorldRepresentationForAgent(character) + "; not sending setting elements.");
					}

					_aware.get(character).add(oe);
				} else if (oe.getOperator () instanceof InferenceOperator) {
					for (RDFtriple eff: oe.getEffects()) {
						
						List contTriple = new ArrayList();
						contTriple.add(eff);
						StorySettingElement sse = FabulaFactory.createSettingElement("plotagent", contTriple, null ); 
						setting_elements.add(sse);
						m_fabulaCollector.addFabulaElement(sse);
						
						logger.info("Created setting element for effect: " + eff + "\nof framing operator: " + oe.getOperator().getIndividual());
					}
					_aware.get(character).add(oe);
				}

			} 

		}
		logger.info("Determined setting elements for character " + character.getLocalName() +"..." + setting_elements.size() + " setting elements.");
		return setting_elements;
	}
	
	/**
	 * Sends given set of perceptions to given character
	 * 
	 * @param perceptions perceptions to send
	 * @param character AID of character to send them to
	 */
	public void sendPerceptions(Vector<StoryPerception> perceptions, AID character) {		
		for (StoryPerception p: perceptions) {
			IncomingPerception ip = new IncomingPerception();
			ip.setPerception(p);
			
			AID[] receiver = new AID[1];
			receiver[0] = character;
			for (AID c: receiver) {
				logger.info("Sending perceptions to character: " + c);
			}
			m_ownerAgent.getAgent().addBehaviour(new SendInformBehaviour(m_ownerAgent.getAgent(),
					receiver, ip));

		}
	}
	
	/**
	 * Sends given set of setting elements to given character
	 * 
	 * @param setting_elements setting elements to send
	 * @param character AID of character to send them to
	 */
	public void sendSettingElements(Vector<StorySettingElement> setting_elements, AID character) {		
		for (StorySettingElement sse: setting_elements) {
			IncomingSetting is = new IncomingSetting();
			is.setSetting(sse);
			
			AID[] receiver = new AID[1];
			receiver[0] = character;
			for (AID c: receiver) {
				logger.info("Sending setting element: " + sse.getIndividual() + "\nto character: " + c);
			}
			m_ownerAgent.getAgent().addBehaviour(new SendInformBehaviour(m_ownerAgent.getAgent(),
					receiver, is));

		}
	}
	

	@Override
	public IAgent getAgent() {
		return m_ownerAgent;
	}

	@Override
	public Set<FabulaCausality> explainCausalities() {
		HashSet<FabulaCausality> newCausalities = new HashSet<FabulaCausality>();
		for (FabulaCausality fc: m_fabulaCollector.explainCausalities()) {
			newCausalities.add(fc);
		}
		m_fabulaCollector.resetFabulaCausalities();
		return newCausalities;	
	}

	@Override
	public Set<FabulaElement> explainElements() {
		HashSet<FabulaElement> newElements = new HashSet<FabulaElement>();
		for (FabulaElement fe: m_fabulaCollector.explainElements()) {
			newElements.add(fe);
		}
		m_fabulaCollector.resetFabulaElements();
		return newElements;		
	}

	/**
	 * Inner class to map operators to their effects
	 * @author swartjes
	 *
	 */
	class OperatorEffects {
		private Operator _op;
		private Vector<RDFtriple> _effs;
		public OperatorEffects(Operator o, Vector<RDFtriple> eff) {
			_op = o;
			_effs = eff;			
		}
		
		public Operator getOperator() {
			return _op;
		}
		
		public Vector<RDFtriple> getEffects() {
			return _effs;
		}
	}
}
