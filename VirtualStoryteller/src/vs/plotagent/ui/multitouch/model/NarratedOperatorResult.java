package vs.plotagent.ui.multitouch.model;

import vs.communication.OperatorResult;

/**
 * Decorates an OperatorResult with the narration in natural language, if
 * available and appropriate for the given result.
 * 
 * @author swartjes
 *
 */
public class NarratedOperatorResult {

	private OperatorResult _operatorResult;
	private String _narration;
	
	public NarratedOperatorResult(OperatorResult or, String narration) {
		_operatorResult = or;		
		_narration = narration;
	}
	
	public OperatorResult getOperatorResult() {
		return _operatorResult;
	}
	
	public String getNarration() {
		return _narration;
	}
}
