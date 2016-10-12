package vs.plotagent.ui.multitouch.view;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.util.math.Vector3D;


/**
 * Represents the action to "Start the story!"
 * @author swartjes
 *
 */
public class StartStoryAction implements IGestureEventListener {

	/** The poly button. */
	private VSTMultitouchApplication mtApp;
	
	/**
	 * Instantiates a new default button click action.
	 * 
	 * @param poly the poly
	 */
	public StartStoryAction(VSTMultitouchApplication app) {
		mtApp = app;
	
	}

	/* (non-Javadoc)
	 * @see com.jMT.input.gestureAction.IGestureAction#processGesture(com.jMT.input.inputAnalyzers.GestureEvent)
	 */
	public boolean processGestureEvent(MTGestureEvent g) {

		if (g instanceof TapEvent){
			TapEvent clickEvent = (TapEvent)g;
			
			
			if (g.getTargetComponent() instanceof MTComponent){ 
				MTComponent comp = (MTComponent)g.getTargetComponent();
				
				switch (clickEvent.getId()) {
				case MTGestureEvent.GESTURE_DETECTED:

					if ( ((TapEvent)g).getTapID() == TapEvent.BUTTON_DOWN){
						mtApp.getVSTMultitouchController().startStory();
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
