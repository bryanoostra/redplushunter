package vs.plotagent.ui.multitouch.view.actioninterface;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.util.math.Vector3D;

import vs.communication.Operator;
import vs.fabula.FabulaFactory;
import vs.plotagent.ui.multitouch.model.PossibleAction;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;

/**
 * Represents the user action to execute a possible story action.
 * @author swartjes
 *
 */
public class ExecuteAction implements IGestureEventListener {
	
	private VSTMultitouchApplication mtApp;
	
	/**
	 * Instantiates a new default button click action.
	 * 
	 * @param poly the poly
	 */
	public ExecuteAction(VSTMultitouchApplication owner){
		mtApp = owner;
		
	}

	/* (non-Javadoc)
	 * @see com.jMT.input.gestureAction.IGestureAction#processGesture(com.jMT.input.inputAnalyzers.GestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {
//		width = polyButton.getWidthLocal();//

		if (g instanceof TapEvent){
			TapEvent clickEvent = (TapEvent)g;
			
			
			if (g.getTargetComponent() instanceof MTComponent){ 
				MTComponent comp = (MTComponent)g.getTargetComponent();
				
				switch (clickEvent.getId()) {
				case MTGestureEvent.GESTURE_DETECTED:

					if ( ((TapEvent)g).getTapID() == TapEvent.BUTTON_DOWN){
						PossibleAction chosenAction = mtApp.getVSTMultitouchController().getInterfaceModel().getSelectedAction();
						if (chosenAction != null) {
							//Operator op = FabulaFactory.createUnknownOperator(chosenAction.getPrologDescription(), chosenAction.getCharacterURI());
							mtApp.getVSTMultitouchController().storyActionChosen(chosenAction.getCharacterURI(), chosenAction);
						}
					}
				break;
				default:
					break;
				}
			}
		}
		return false;
	}


}
