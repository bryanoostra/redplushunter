package vs.plotagent.ui.multitouch.view.actioninterface;

import java.util.ArrayList;
import java.util.List;

import vs.plotagent.ui.multitouch.view.MTButton;

/**
 * A standard button strip.
 */
public class MTButtonStrip implements IButtonStrip {
	
	private List<MTButton> _buttons;
	
	public MTButtonStrip() {
		_buttons = new ArrayList<MTButton>();
		
	}
	
	public void registerButton(MTButton b) {
		if (! _buttons.contains(b)) {
			_buttons.add(b);
		}
	}
	
	public void buttonSelected(Object b, boolean isSelected) {
		for (MTButton but: _buttons) {
			if (b != but) {
				but.setSelected(! isSelected, false);
			}
		}
	}
	
}
