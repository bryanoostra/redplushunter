package vs.plotagent.ui.multitouch.view.map;

import org.mt4j.util.MTColor;

import processing.core.PApplet;
import processing.core.PImage;
import vs.plotagent.ui.multitouch.view.MTButton;

/**
 * Button for choosing human / computer control of a character.
 * @author swartjes
 *
 */
public class ControlChoiceButton extends MTButton {

	private static MTColor buttonSelectedColor = new MTColor(128,128,128);
	private static MTColor buttonColor = new MTColor(255,255,255);

	private ControlChooser _chooser;
	private ControlChooser.ControlChoice _choice;
	
	public ControlChoiceButton(PImage texture, ControlChooser chooser, ControlChooser.ControlChoice choice, PApplet pApplet) {
		super(texture, pApplet);

		_chooser = chooser;
		_choice = choice;
	}
	
	public void setSelected(boolean selected, boolean propagate) {
		super.setSelected(selected, propagate);
		
		if (selected) {
			setFillColor(buttonSelectedColor);
		
		} else {
			setFillColor(buttonColor);
		}
	}
	
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		
		if (selected) {
			setFillColor(buttonSelectedColor);
		
		} else {
			setFillColor(buttonColor);
		}
		
		// TODO: set choice.
		if (selected) {
			_chooser.choiceMade(_choice);
		}
		_chooser.setVisible(false);
	}
}
