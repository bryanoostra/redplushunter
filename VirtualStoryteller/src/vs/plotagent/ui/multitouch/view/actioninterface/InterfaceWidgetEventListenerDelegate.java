package vs.plotagent.ui.multitouch.view.actioninterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Delegate for interface widget event listeners. 
 * @author swartjes
 *
 */
public class InterfaceWidgetEventListenerDelegate implements IInterfaceWidgetComponent {
	
	private List<IInterfaceWidgetEventListener> listeners;
	
	public InterfaceWidgetEventListenerDelegate() {
		listeners = new ArrayList<IInterfaceWidgetEventListener>();
	}

	public void addEventListener(IInterfaceWidgetEventListener listener) {
		if (! listeners.contains(listener)) { 
			listeners.add(listener);
		}
	}
	
	public void removeEventListener(IInterfaceWidgetEventListener listener) {
		listeners.remove(listener);
	}	
	
	public void fireEvent(InterfaceWidget.Event event, Object src) {
		for (IInterfaceWidgetEventListener l: listeners) {
			l.onEvent(event, src);
		}
	}
	
}
