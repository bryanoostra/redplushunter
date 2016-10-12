package vs.plotagent.ui.multitouch.view.actioninterface;

/**
 * A component that generates action selection interface events that should be responded to.
 * 
 * @author swartjes
 *
 */
public interface IInterfaceWidgetComponent {

	public void addEventListener(IInterfaceWidgetEventListener mwel);
	
	public void removeEventListener(IInterfaceWidgetEventListener mwel);
	
	public void fireEvent(InterfaceWidget.Event ev, Object src);
	
}
