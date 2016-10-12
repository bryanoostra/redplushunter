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
package vs.poplanner;

import java.util.Vector;
import java.util.logging.Logger;

import vs.debug.LogFactory;
import vs.knowledge.PrologKB;

/**
 * Interface for Partial Order Planner
 * 
 * @author kruizingaEE
 * Created on 3-may-2007
 
*/
public class PoPlanner {

	private final Logger logger;

	private String m_character;
	private String m_Goals;
	private String m_currentPlan;	// Stores the current plan for this PoPlanner. Is set by PoPlanner.plan()
	private String m_oldPlan;
	private boolean m_goalIsIC;	// Whether goal is an IC goal
	private Vector<String> m_PlanFirstSteps;

	/**
	 * Constructor: create planner for given character
	 * @param character URI of character of the planner
	 * @param goalIsIC whether the goal that the planner plans for is an IC goal (i.e., a character goal) 
	 */
	public PoPlanner(String character, boolean goalIsIC) {
		logger = LogFactory.getLogger(this);
		m_character = character;
		m_goalIsIC = goalIsIC;
	}
	
	/**
	 * Constructor: create planner that is not tied to any character 
	 */
	public PoPlanner() {
		logger = LogFactory.getLogger(this);
		m_character = null;
		m_goalIsIC = false;	// Cannot be IC, per definition.
	}

	/**
	 * Retrieves executable events
	 * @return the executable events
	 */
	public Vector<String> getExecutableEvents() {
		logger.fine("Retrieving all event Steps from Plan.");
		return PrologKB.getInstance().executableEvents(m_currentPlan);
	}
	
	/**
	 * Retrieves executable framing operators (i.e., framing operators that do not depend on the execution of 
	 * earlier steps in the plan)
	 * @return the executable framing operators
	 */
	public Vector<String> getExecutableFramingOperators() {
		logger.fine("Retrieving all framing operator Steps from Plan.");
		return PrologKB.getInstance().executableFramingOperators(m_currentPlan);
	}
	
	/**
	 * Retrieves executable inference operators (i.e., inference operators that do not depend on the execution of 
	 * earlier steps in the plan)
	 * @return the executable inference operators
	 */
	public Vector<String> getExecutableInferenceOperators() {
		logger.fine("Retrieving all inference operator Steps from Plan.");
		return PrologKB.getInstance().executableInferenceOperators(m_currentPlan);
	}
	
	/**
	 * Retrieves executable internal element operators (i.e., operators that operate on the character's mind) 
	 * @return the executable internal element operators
	 */
	public Vector<String> getExecutableInternalElementOperators() {
		logger.fine("Retrieving all inference operator Steps from Plan.");
		return PrologKB.getInstance().executableInternalElementOperators(m_currentPlan);
	}	
	
	/**
	 * Retrieves the operators from the plan that are ready for execution (i.e., do not depend on the execution of 
	 * earlier steps in the plan)
	 */
	public Vector<String> getExecutableOperators() {
		logger.fine("Retrieving executable operators from plan.");
		m_PlanFirstSteps = PrologKB.getInstance().executableOperators(m_currentPlan);
		
		if (m_PlanFirstSteps == null) {
			logger.warning("First steps is null.");
		}
		return m_PlanFirstSteps;
	}

	/**
	 * Retrieves all causal links from the plan
	 */
	public Vector<PlanLink> getLinks() {
		logger.fine("Retrieving all Links from Plan.");
		return PrologKB.getInstance().getPlanLinks(m_currentPlan);
	}

	/**
	 * Retrieves all orderings from the plan
	 */
	public Vector<PlanOrdering> getOrderings() {
		logger.fine("Retrieving all Orderings from Plan.");
		return PrologKB.getInstance().getPlanOrderings(m_currentPlan);
	}
	
	/**
	 * Return the plan in SWI-Prolog representation. 
	 */
	public String getPlan() {
		return m_currentPlan;
	}	
	
	/**
	 * Retrieves all steps from the plan
	 */
	public Vector<PlanStep> getSteps() {
		logger.fine("Retrieving all Steps from Plan.");
		return PrologKB.getInstance().getPlanSteps(m_currentPlan);
	}		
	
	/**
	 * Creates and stores a plan for given goals. Leaves old plan intact if planning fails.
	 */
	public boolean plan() {
		String newPlan;
		
		logger.info("Start planning now");
		long start = System.currentTimeMillis();
		
		if (m_currentPlan != null ) {
			newPlan = PrologKB.getInstance().adaptPlan(m_character, m_goalIsIC, m_Goals, m_currentPlan);
		} else {
			newPlan = PrologKB.getInstance().plan(m_character, m_goalIsIC, m_Goals);
		}
		if (newPlan != null) {
			logger.info("Successfully made a plan in: " + (System.currentTimeMillis() - start));
			m_oldPlan = m_currentPlan;
			m_currentPlan = newPlan;
			return true;
		} else {
			logger.warning("Could not make a plan for goals \n" + m_Goals +"\nPlanning took: " + (System.currentTimeMillis() - start));

			// Store old plan if it isn't null, so that m_oldPlan always contains the latest successful plan
			if (m_currentPlan != null) {
				m_oldPlan = m_currentPlan;
			}
			m_currentPlan = null;
			return false;
		}
	}

	/**
	 * Checks whether the current plan is finished.
	 */
	public boolean planFinished() {
		return PrologKB.getInstance().finishedPlan(m_currentPlan);
	}
	
	/**
	 * Checks whether the old plan failed
	 */
	public boolean planUpdateFailed() {
		return (m_currentPlan == null) && (m_oldPlan != null);
	}

	/**
	 * Sets the goals for the planner (a Prolog list of conditions)
	 */
	public void setGoals(String goals) {
		logger.fine("Setting planner goal to: " + goals);
		m_Goals = goals;
	}
	
	/**
	 * Explicitly set the plan of this planner (use with care)
	 * @param plan the plan for this planner
	 */
	public void setPlan(String plan) {
		if (m_currentPlan != null) {
			logger.warning("Overriding plan! Make sure this is intended.");
		}
		m_currentPlan = plan;
	}
	
}
