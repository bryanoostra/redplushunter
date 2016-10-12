package vs.plotagent.ui.multitouch.model;

import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;


/**
 * Represents a "do nothing" possible action (i.e., skip a turn)
 * @author swartjes
 *
 */
public class DoNothingAction extends PossibleAction {

	private String _characterURI;
	private String _prologDescription;
	private String _category;
	private String _description;
	
	private static String prologString = null;
	
	public DoNothingAction(String charURI, String category, String description) {
		super(charURI, prologString, category, description);	
	}
	
	public static PossibleAction createDoNothingAction(String characterURI) {
		String category = "DoNothing category";
		String action = "DoNothing action";
		if(MultitouchInterfaceSettings.LANGUAGE == MultitouchInterfaceSettings.Language.dutch) {
			category = "Doe niets";
			action = "Sla een ronde over";
		} else if(MultitouchInterfaceSettings.LANGUAGE == MultitouchInterfaceSettings.Language.english) {
			category = "Do nothing";
			action = "Skip turn this round";
		}
		return new DoNothingAction(characterURI, category, action);
	}
}
