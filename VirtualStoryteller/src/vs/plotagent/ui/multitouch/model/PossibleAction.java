package vs.plotagent.ui.multitouch.model;

/**
 * Represents an action that can currently be executed (and thus be selected by the user).
 * 
 * @author Alofs
 *
 */
public class PossibleAction {

	private String _characterURI;
	private String _prologDescription;
	private String _category;
	private String _description;
	
	public PossibleAction(String charURI, String prologString, String category, String description) {
		_characterURI = charURI;
		_prologDescription = prologString;
		_category = category;
		_description = description;
		
	}

	public String getCharacterURI() {
		return _characterURI;
	}

	public String getPrologDescription() {
		return _prologDescription;
	}

	/**
	 * Retrieves the action category of the given action (e.g., "Loop", "Gebeurtenis")
	 * @return
	 */
	public String getCategory() {
		return _category;
	}

	/**
	 * Retrieves the description of the action as it occurs in the interface
	 * (e.g. "Loop naar het bos.")
	 */
	public String getDescription() {
		return _description;
	}
	
	
}
