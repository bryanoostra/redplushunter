/**
 * 
 */
package vs.characteragent.strategy;

import java.util.Set;

import vs.IExplainable;
import vs.communication.GoalSchema;
import vs.fabula.FabulaCollector;

/**
 * Strategy for selecting goals to adopt. The strategy determines which goals should be adopted upon execution, 
 * and is responsible for explaining its choices in terms of fabula (i.e., which goals should be added to the fabula,
 * and what caused, motivated or enabled these goals)
 * 
 * @author swartjes
 *
 */
public interface IGoalAdoptionStrategy extends IExplainable {
	
	/**
	 * Returns a set of goal schemas to adopt.
	 */
	public Set<GoalSchema> selectGoalsToAdopt(String character);

}
