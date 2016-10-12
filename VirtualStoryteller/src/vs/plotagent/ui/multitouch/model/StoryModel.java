package vs.plotagent.ui.multitouch.model;

import java.util.Vector;
import java.util.logging.Logger;

import vs.communication.Aborted;
import vs.communication.Finished;
import vs.communication.OperatorResult;
import vs.debug.LogFactory;
import vs.knowledge.PrologKB;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;

/**
 * Model for the story-under-development. Stores operators that were executed
 * and the way they were narrated.
 * 
 * @author swartjes
 *
 */
public class StoryModel extends MyObservable  {
	
	private Logger logger;
			
	private Vector<NarratedOperatorResult> narratedOperatorResults;

	
	public StoryModel() {
		logger = LogFactory.getLogger(this);
		narratedOperatorResults = new Vector<NarratedOperatorResult>();
	}
	
	/**
	 * Called when an operator result occurs
	 * @param or
	 */
	public void addOperatorResult(OperatorResult or) {
		String narration = "";
		if (or.getStatus() instanceof Finished) {
			narration = PrologKB.getInstance().narrate(or.getOperator().getPrologDescription());
			narration = narration.replace("'", "");
			logger.info(narration);
			NarratedOperatorResult nor = new NarratedOperatorResult(or, narration);
			narratedOperatorResults.add(nor);
			notifyObservers(nor);
		} else if (or.getStatus() instanceof Aborted) {
			narration = "ERROR, FAILED: ";
			narration = narration + PrologKB.getInstance().narrate_imperative(or.getOperator().getPrologDescription());
			logger.severe(narration);
			if (MultitouchInterfaceSettings.HIDE_FAILED_OR_ABORTED_ACTIONS_FROM_STORY) {
				return;
			} else {
				NarratedOperatorResult nor = new NarratedOperatorResult(or, narration);
				narratedOperatorResults.add(nor);
				notifyObservers(nor);
			}
		}
	}
}
