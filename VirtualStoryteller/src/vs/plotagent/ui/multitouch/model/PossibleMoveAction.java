package vs.plotagent.ui.multitouch.model;

/**
 * A possible action that represents a move of a character from one location to another.
 * 
 * @author Alofs
 *
 */
public class PossibleMoveAction extends PossibleAction {
	
	private String _destination;
	
	public PossibleMoveAction(String charURI, String prologString, String destination, String category, String description) {
		super(charURI, prologString, category, description);
		_destination = destination;
	}
	
	public String getDestination() {
		return _destination;
	}

}
