package vs.plotagent.ui.multitouch.view.actioninterface;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.util.math.Vector3D;

/**
 * Action for showing/hiding an action button strip.
 * 
 * @author swartjes
 *
 */
public class ShowHideAction implements IGestureEventListener {

	public static enum ShowHide {show, hide};
	
	/** The poly button. */
	private ActionButtonStrip _strip;
	private ShowHide _action;

	
	/**
	 * Instantiates a new default button click action.
	 * 
	 * @param poly the poly
	 */
	public ShowHideAction(ActionButtonStrip strip, ShowHide whatToDo ){
		_strip = strip;
		_action = whatToDo;
		
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
						if (_action.equals(ShowHide.hide)) {
							_strip.hide(true);
						} else {
							_strip.hide(false);
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
