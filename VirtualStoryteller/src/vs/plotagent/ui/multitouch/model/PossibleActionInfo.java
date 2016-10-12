package vs.plotagent.ui.multitouch.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import vs.knowledge.PrologKB;

/**
 * Stores and retrieves possible actions.
 * 
 * @author Alofs
 *
 */
public class PossibleActionInfo {
	
	private List<PossibleAction> _possibleActionList;
	
	public PossibleActionInfo() {
		_possibleActionList = new ArrayList<PossibleAction>();
		
	}
	
	/**
	 * Updates the information on which actions are possible
	 * 
	 * @param characterURI URI of the character for which to check which actions are possible
	 */
	public void updatePossibleActionInfo(String characterURI) {
		clearPossibleActions();
		
		Hashtable<String,String> action_target = PrologKB.getInstance().getAllPossibleTransitMoveActions2(characterURI);
		
		
		Hashtable<String, Vector<String>> allPossibleActionsByType = PrologKB.getInstance().getAllPossibleActionsGroupedByType(characterURI);
		
		for (String type: allPossibleActionsByType.keySet()) {
			for (String action: allPossibleActionsByType.get(type)) {
				String category = PrologKB.getInstance().narrate_category(action).replaceAll("'", "");
				String description = PrologKB.getInstance().narrate_imperative(action).replaceAll("'", "");
				
				if (action_target.keySet().contains(action)) {
					// this is a transit move action
					String target = action_target.get(action).replaceAll("'", "");
					PossibleMoveAction pta = new PossibleMoveAction(characterURI, action, target, category, description);
					addPossibleAction(pta);
				} else {
					// not a transit move action
					PossibleAction pa = new PossibleAction(characterURI, action, category, description);
					addPossibleAction(pa);
				}
			}
		}
		
		// Add "do nothing" action
		addPossibleAction(DoNothingAction.createDoNothingAction(characterURI));
	}
	
	private void addPossibleAction(PossibleAction a) {
		if (! _possibleActionList.contains(a)) {
			_possibleActionList.add(a);
		}
	}
	
	private void clearPossibleActions() {
		_possibleActionList.clear();
	}
	
	/**
	 * Iterator of possible actions.
	 * 
	 * @return
	 */
	public Iterator<PossibleAction> getPossibleActions() {
		return _possibleActionList.iterator();
	}
	
	public PossibleAction getLastPossibleAction() {
		int index = _possibleActionList.size()-1;
		if(index>=0) {
			return _possibleActionList.get(index);
		} else {
			return null;
		}
	}
	
	/**
	 * Iterator of possible actions for a given action category.
	 * 
	 * @param actionCategory String representation of the action category for which to retrieve possible actions
	 * @return
	 */
	public Iterator<PossibleAction> getPossibleActions(String actionCategory) {
		List<PossibleAction> possActionByCategory = new ArrayList<PossibleAction>();
		for (PossibleAction pa: _possibleActionList) {
			if (pa.getCategory().equals(actionCategory)) {
				possActionByCategory.add(pa);
			}
		}
		return possActionByCategory.iterator();
	}
	
	public PossibleAction getActionByPrologString(String prologDescription) {
		PossibleAction doNothingAction = null;
		for (PossibleAction pa: _possibleActionList) {
			String desc = pa.getPrologDescription();
			if (desc==null) {
				doNothingAction = pa;
			} else if(desc.equals(prologDescription)) {
				return pa;
			}
		}
		return doNothingAction;
	}
	
	/**
	 * Iterator of action categories.
	 * 
	 * @return
	 */
	public Iterator<String> getActionCategories() {
		List<String> categories = new ArrayList<String>();
		for (PossibleAction pa: _possibleActionList) {
			String cat = pa.getCategory(); 
			if (! categories.contains(cat)) {
				categories.add(cat);
			}
		}
		return categories.iterator();
	}
	
	/**
	 * Returns a possible move action that corresponds to a given destination. 
	 * There might be more (e.g. skip to the forest, shuffle to the forest), 
	 * but only one is returned.
	 * 
	 * @param destination the destination of a possible move action.
	 * @return a possible move action
	 */
	public PossibleMoveAction getDestinationMoveAction(String destination) {
		for (PossibleAction pa: _possibleActionList) {
			if (pa instanceof PossibleMoveAction) {
				if (destination.equals(((PossibleMoveAction)pa).getDestination())) {
					return (PossibleMoveAction)pa;
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks whether a destination (represented by a String URI) is a possible destination
	 * given the current set of possible actions (i.e., one of the possible move actions has
	 * the given destination as destination).
	 * 
	 * @param destination URI of a destination
	 * @return
	 */
	public boolean isDestinationPossible(String destination) {
		for (PossibleAction pa: _possibleActionList) {
			if (pa instanceof PossibleMoveAction) {
				if (destination.equals(((PossibleMoveAction)pa).getDestination())) {
					return true;
				}
			}
		}
		
		return false;
	}

}
