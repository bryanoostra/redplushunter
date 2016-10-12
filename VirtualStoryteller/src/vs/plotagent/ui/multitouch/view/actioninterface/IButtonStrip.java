package vs.plotagent.ui.multitouch.view.actioninterface;

import vs.plotagent.ui.multitouch.view.MTButton;
import vs.plotagent.ui.multitouch.view.MTRoundButton;

/**
 * Button strips are mini-controllers for buttons being selected. 
 * When a button is selected, it calls buttonSelected(..) of the
 * strip it is registered to. The button strip then takes care of 
 * the necessary extra actions, such as deselecting the other buttons
 * in the strip.
 * 
 * TODO: would be nicer and more loosly coupled to do this using 
 * a listSelectionListener
 */
public interface IButtonStrip {
	
	public void buttonSelected(Object button, boolean isSelected);
	
}
