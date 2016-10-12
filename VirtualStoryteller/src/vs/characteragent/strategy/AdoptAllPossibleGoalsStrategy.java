/**
 * 
 */
package vs.characteragent.strategy;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import vs.communication.FabulaCausality;
import vs.communication.FabulaElement;
import vs.communication.GoalSchema;
import vs.debug.LogFactory;
import vs.fabula.FabulaCollector;
import vs.fabula.FabulaFactory;
import vs.knowledge.PrologKB;
import vs.knowledge.vocab.Fabula;

/**
 * Strategy to just adopt all possible goals (i.e., goals that have their preconditions matched)
 * 
 * @author swartjes
 */
public class AdoptAllPossibleGoalsStrategy implements
		IGoalAdoptionStrategy {
	
	private FabulaCollector _fabulaCollector;
	private Logger logger;
	
	public AdoptAllPossibleGoalsStrategy() {
		logger = LogFactory.getLogger(this);
		_fabulaCollector = new FabulaCollector();
	}
	
	/* (non-Javadoc)
	 * @see vs.characteragent.strategy.IGoalAdoptionStrategy#selectGoalsToAdopt()
	 */
	@Override
	public Set<GoalSchema> selectGoalsToAdopt(String character) {
		
		Set<GoalSchema> goals_to_adopt = new HashSet<GoalSchema>();
		
		for (String g: PrologKB.getInstance().getPossibleGoals(character)) {
			
			// Create goal
			GoalSchema gs = FabulaFactory.createGoalSchema(g, character);
			goals_to_adopt.add(gs);
			
			// Log in fabula
			// TODO: add to fabula only when we know it is plannable. Otherwise pretend it was never adopted.
			// 		 this sounds ugly; more elegant is to make this a choice of goal adoption in the first place; 
			//			* only adopt goals that are plannable
			//			* once adopted, it is really adopted and should be in the fabula.
			
			// Jeroen:
			// See above; indeed, only when goals are plannable should they be adopted (and thus, narrated!).

			_fabulaCollector.addFabulaElement(gs);
			findCausalities(gs);
			
		}

		return goals_to_adopt;
	}
	
	private void findCausalities(GoalSchema gs) {
		// Add beliefs enabling the goal
		for (String ena_bel: PrologKB.getInstance().getEnablingFabulaElements(gs)) {
		
			logger.info("Enabling fabula elements of schema " + gs.getIndividual() + ":\n" + ena_bel);
			FabulaCausality fc = new FabulaCausality();
			fc.setSubjectIndividual(ena_bel);
			fc.setObjectIndividual(gs.getIndividual());
			fc.setCausalProperty(Fabula.enables);
			
			_fabulaCollector.addFabulaCausality(fc);
		}
		
		// Add supergoals motivating the goal
		for (String moti_bel: PrologKB.getInstance().getMotivatingFabulaElements(gs)) {
			logger.info("Motivating fabula elements of schema " + gs.getIndividual() + ":\n" + moti_bel);
			FabulaCausality fc = new FabulaCausality();
			fc.setSubjectIndividual(moti_bel);
			fc.setObjectIndividual(gs.getIndividual());
			fc.setCausalProperty(Fabula.motivates);
			
			_fabulaCollector.addFabulaCausality(fc);
		}
		
		// TODO: add causal link from emotions (psi-causes)
		// fabulaCausalities.add(...);

	}
	
	public Set<FabulaElement> explainElements() {
		Set<FabulaElement> elements = new HashSet<FabulaElement>();
		elements.addAll(_fabulaCollector.explainElements());
		_fabulaCollector.resetFabulaElements();
		return elements;
	}
	
	public Set<FabulaCausality> explainCausalities() {
		Set<FabulaCausality> causalities = new HashSet<FabulaCausality>();
		causalities.addAll(_fabulaCollector.explainCausalities());
		_fabulaCollector.resetFabulaCausalities();
		return causalities;
	}

}
