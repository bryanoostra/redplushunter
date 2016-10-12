package vs.plotagent.ui.multitouch.model;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import vs.Config;
import vs.debug.LogFactory;
import vs.knowledge.PrologKB;

import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Model for the action selection interface. Keeps track of the possible actions,
 * the current user, etc. 
 * 
 * @author Alofs
 *
 */
public class InterfaceModel extends MyObservable  {
	
	private Logger logger;
	
	private PrefixMapping pm;
	
	private String domainDirectory = null;
	private String domain;
	
	private String currentCharacter;
	
	private String currentlyThinkingAICharacterName;
	
	private String humanCharacterName;
	private String humanCharacterCurrentLocation;
	
	private Hashtable<String, String> destination_action;
	private Hashtable<String,Vector<String>> locationInhabitants;
	
	private PossibleActionInfo possibleActions;
	private PossibleAction selectedAction;
	
	private boolean storyStarted;
	private boolean userTurn;
	
	
	//index of the currently selected action (-1 means no action selected)
	//always autoselect the first action (there is always an action selected, default index 0)
	private int indexOfSelectedAction = 0;

	private boolean categoryStripEnabled;

	private boolean hideNonMoveCategories;
	
	private PossibleAction lastSelectedNonMoveAction = DoNothingAction.createDoNothingAction("placeholder");
	
	public static enum DestinationProperty {allowed, notAllowed, alreadyAt, unknown};
	
	public static enum InterfaceModelChange {
							humanCharacterChanged,
							actionSelected, 
							possibleActionsUpdated, 
							locationsChanged,
							storyStarted,
							startUserTurn,
							startAgentTurn,
							stopUserTurn,
							startTurn,
							stopTurn, 
							categoryStripEnabled};

	
	public InterfaceModel() {
		
		logger = LogFactory.getLogger(this);
		
		//used for shortening names
		pm = PrefixMapping.Factory.create();
		pm.setNsPrefixes(Config.namespaceMap);
		
		possibleActions = new PossibleActionInfo();
	}
	
	public PrefixMapping getPrefixMapping() {
		return pm;
	}
	
	/**
	 * Sets the URI of the character that is currently human-controlled.
	 */
	public void setHumanCharacterName(String characterName) {
		humanCharacterName = characterName;
		updateLocationsInhabitants();
		
		logger.info("Notifying observers that model has changed. Name of human character: " +characterName);
		notifyObservers(InterfaceModelChange.humanCharacterChanged);
	}
	
	public String getHumanCharacterName() {
		return humanCharacterName;
	}
	
	public void setCurrentlyThinkingAICharacterName(String characterName) {
		logger.info("Currently the interface is waiting on a thinking computer-controlled Character. Name of AI character: " +characterName);
		currentlyThinkingAICharacterName = characterName;
		setHumanCharacterName(null);
	}

	public String getCurrentlyThinkingAICharacterName() {
		return currentlyThinkingAICharacterName;
	}

	public void turnStarted(String characterURI) {
		currentCharacter = characterURI;
		notifyObservers(InterfaceModelChange.startTurn);
		logger.info("Turn started. Name of human character: " +characterURI);
	}
	
	public void turnEnded(String characterURI) {
		currentCharacter = "";
		notifyObservers(InterfaceModelChange.stopTurn);
		logger.info("Turn ended. Name of human character: " +characterURI);
	}
	
	public void updateLocationsInhabitants() {
		locationInhabitants = PrologKB.getInstance().getAllLocationsInhabitants();
		
		Hashtable<String,Vector<String>> locationInhabitantsNQ = new Hashtable<String,Vector<String>>();
		
		Iterator<Entry<String, Vector<String>>> it = locationInhabitants.entrySet().iterator();

		while(it.hasNext()) {
			Entry<String, Vector<String>> e = (Entry<String, Vector<String>>)it.next();
			String location = (String)e.getKey();
			location = location.replace("'", "");
		
			Vector<String> inhabitants = (Vector<String>)e.getValue();
			Vector<String> inhabitantsNQ = new Vector<String>();
			
			//String place = pm.shortForm(location.replace("'", ""));
			//String description = "@ " + place + " = ";
			Iterator<String> itChar = inhabitants.iterator();
						
			while (itChar.hasNext()) {
				String name = itChar.next();
				name = name.replace("'", "");
				inhabitantsNQ.add(name);
				//Store the current location of the human character
				if (name.equals(humanCharacterName)) {
					logger.fine("Setting current location of human character: " + location);
					humanCharacterCurrentLocation = location;
				} else {
					logger.fine("Name is not humancharacter: " + name + " is not " + humanCharacterName);
				}
				
				//name = name.replace("'", "");
				//description = description + pm.shortForm(name) + " ";
			}
			//writeConsole(description);
			
			locationInhabitantsNQ.put(location, inhabitantsNQ);
		}
		//writeConsole("----- No more (known) locations with (known) characters -----");

		//now finally update the inhabitants of each location on the map
		locationInhabitants = locationInhabitantsNQ;
		
		logger.info("Notifying observers that model has changed (locations of inhabitants).");
		notifyObservers(InterfaceModelChange.locationsChanged);
	}
	
	/**
	 * Updates the information on which actions are currently possible for the human-controlled character.
	 */
	public void updatePossibleActions() {
		
		//possibleActions do not have to be updated when no humanCharacterName has been set:
		if(humanCharacterName==null) return;
		
		categoryStripEnabled = true;
		
		possibleActions.updatePossibleActionInfo(humanCharacterName);
		logger.info("Notifying observers that model has changed (possible actions).");
		notifyObservers(InterfaceModelChange.possibleActionsUpdated);
	}
	
	public PossibleActionInfo getPossibleActionInfo() {
		return possibleActions;
	}
	
	public PossibleAction getFirstPossibleActionOfType(String actionCategory) {
		//System.out.println("InterfaceModel getFirstPossibleActionOfType: " +actionCategory);
		Iterator<PossibleAction> it = possibleActions.getPossibleActions(actionCategory);
		if (it.hasNext()) {
			return it.next();
		} else {
			//this can never happen right?
			return null;
		}
	}
	
	public Hashtable<String,Vector<String>> getLocationInhabitants() {
		// TODO: iterator
		return locationInhabitants;
	}
	
	public void setDomain(String domainName) {
		domain = domainName;
		logger.info("Domain set to: " +domainName);
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String getCurrentCharacter() {
		return currentCharacter;
	}
	
	public void setUserTurn(boolean isUserTurn) {
		
		userTurn = isUserTurn;
		
		if (userTurn) {
			notifyObservers(InterfaceModelChange.startUserTurn);
			logger.fine("Start User Turn");
		} else {
			notifyObservers(InterfaceModelChange.stopUserTurn);
			logger.fine("Stop User Turn");
		}
	}
	
	// Whether it's currently the user's turn.
	public boolean isUserTurn() {
		return userTurn;
	}
	
	public boolean isStoryStarted() {
		return storyStarted;
	}
	
	public void setStoryStarted(boolean isStoryStarted) {
		storyStarted = isStoryStarted;
		notifyObservers(InterfaceModelChange.storyStarted);
		logger.fine("The Story Started");
	}

	
	//looks if going to the destination is amongst the possible actions
	//sets the selected value of the selectionList to this action
	//returns 0 when already at destination
	//returns 1 when destination is allowed
	//returns -1 when destination is not allowed
	//returns 2 when it is unknown whether destination is allowed 
	public DestinationProperty tryPossibleDestination(String destination) {
		logger.fine("Comparing current location with possible destination.\nCurrent: " + humanCharacterCurrentLocation + "\nPossible: " + destination);
		if (humanCharacterCurrentLocation==null) {
			return DestinationProperty.unknown;
		}
		
		if (humanCharacterCurrentLocation.equals(destination)) {
			return DestinationProperty.alreadyAt;
		}
		
		if (possibleActions.isDestinationPossible(destination)) {
			return DestinationProperty.allowed;
		}

		return DestinationProperty.notAllowed;
	}
	
	public PossibleAction getLastSelectedNonMoveAction() {
//		if(lastSelectedNonMoveAction!=null) {
//			System.out.println("InterfaceModel getLastSelectedNonMoveAction(): " +lastSelectedNonMoveAction.getDescription());
//		} else {
//			System.out.println("InterfaceModel getLastSelectedNonMoveAction(): " +lastSelectedNonMoveAction);
//		}
		return lastSelectedNonMoveAction;
	}
	
	public PossibleAction getSelectedAction() {
		return selectedAction;
	}
	
	public void setSelectedAction(PossibleAction action) {
//		if(action==null) {
//			System.out.println("InterfaceModel setSelectedAction = " + action);
//		} else {
//			System.out.println("InterfaceModel setSelectedAction = " + action.getDescription());
//		}
		
		if(action!=null && !(action instanceof PossibleMoveAction)) {
			//System.out.println("1 InterfaceModel.setSelectedAction(nonMoveAction) = " + action.getDescription() + "\n" + action);
			lastSelectedNonMoveAction = action;
			//System.out.println("InterfaceModel lastSelectedNonMoveAction = " + lastSelectedNonMoveAction.getDescription());
		}
		
//		System.out.println("2 old+new selected actions:");
//		if(selectedAction!=null)System.out.println(selectedAction.getDescription());
//		if(action!=null)System.out.println(action.getDescription());
		
		if(selectedAction==null || !selectedAction.equals(action)) {
			selectedAction = action;
			logger.info("Notifying observers that model has changed (selected action).");
			notifyObservers(InterfaceModelChange.actionSelected);
		} else {
			//when selectedAction was null, update it to the new selection
			//selectedAction = action;
		}
	}
	
	public void setSelectedActionByPrologString(String prologDescription) {
		logger.fine("Trying to set the selected action by a Prolog string");
		PossibleAction pa = possibleActions.getActionByPrologString(prologDescription);
		//System.out.println("InterfaceModel selectbyprologstring " +prologDescription + " " + pa);
		setSelectedAction(pa);
	}
	
	//called when on a red location
	public void clearSelectedAction() {
		//set the selected action to null
		setSelectedAction(null);
		logger.info("Set the selected action to null. (Probably character at a red/forbidden destination");
	}
	
	public void selectDoNothingAction() {
		//setSelectedAction(possibleActions.getLastPossibleAction());
		//or:
		setSelectedAction(DoNothingAction.createDoNothingAction(currentCharacter));
		logger.info("Set the selected action to DoNothing.");
	}

	public void hideNonMoveCategories(boolean hide) {
		if(hide != hideNonMoveCategories) {
			hideNonMoveCategories = hide;
			notifyObservers(InterfaceModelChange.categoryStripEnabled);
			logger.info("Non-Move categories should be hidden = " + hide);
		}
	}
	
	public boolean getNonMoveCategoriesShouldBeHidden() {
		return hideNonMoveCategories;
	}
	
	public void setCategoryStripEnabled(boolean enabled) {
		categoryStripEnabled = enabled;
		//why not notifying observers? must have had a good reason...
		//notifyObservers(InterfaceModelChange.categoryStripEnabled);
	}
	
	public boolean isCategoryStripEnabled() {
		return categoryStripEnabled;
	}
}
