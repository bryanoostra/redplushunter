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

import java.util.Vector;
import java.util.logging.Logger;

import vs.communication.FabulaCausality;
import vs.communication.StoryAction;
import vs.debug.LogFactory;
import vs.fabula.FabulaFactory;
import vs.knowledge.PrologKB;
import vs.knowledge.vocab.Fabula;
import vs.utils.Chooser;

/**
 * Reactive process of the character agent.
 * Defines a behaviour layer for fast, immediate responses to the environment.
 * 
 * @author swartjes
 *
 */
public class ReactiveLayer extends BehaviourLayer {
	
	public Logger logger;
	
	public ReactiveLayer(ICharacterAgent owner) {
		super(owner);
		logger = LogFactory.getLogger(this);
	}

	@Override
	public void appraise() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cope() {
		// TODO Auto-generated method stub

	}

	@Override
	public StoryAction selectAction() {

		Vector<String> reactiveActions = PrologKB.getInstance().selectReactiveActionRules(getAgent().getCharacterURI());
		if (reactiveActions == null || reactiveActions.isEmpty()) {
			logger.info("Select action: no reactive actions.");
			return null;
		}
		
		String chosenActionRule = Chooser.randomChoice(reactiveActions);
		
		String chosenAction = PrologKB.getInstance().selectActionFromRule(chosenActionRule);
		
		StoryAction selectedAction = FabulaFactory.createAction(
					chosenAction, getAgent().getCharacterURI());
		
		_fabulaCollector.addFabulaElement(selectedAction);
		
		// Add causalities based on action rule ( IE -m-> A)
		for (String s: PrologKB.getInstance().getActionRuleCauses(chosenActionRule)) {
			FabulaCausality fc = new FabulaCausality();
			fc.setSubjectIndividual(s);
			fc.setObjectIndividual(selectedAction.getIndividual());
			fc.setCausalProperty(Fabula.motivates);
			_fabulaCollector.addFabulaCausality(fc);
		}

		// Add motivations based on action rule ( G -m-> A)
		for (String s: PrologKB.getInstance().getMotivatingFabulaElements(chosenActionRule)) {
			FabulaCausality fc = new FabulaCausality();
			fc.setSubjectIndividual(s);
			fc.setObjectIndividual(selectedAction.getIndividual());
			fc.setCausalProperty(Fabula.motivates);
			_fabulaCollector.addFabulaCausality(fc);
		}
		
		logger.info("Select action: choosing one of the reactive actions: " + selectedAction.getType());
		
		return selectedAction;
	}
	

}
