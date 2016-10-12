package vs.plotagent.ui.multitouch.view.actioninterface;

/**
 * Listens to action selection interface events
 * @author swartjes
 *
 */
public interface IInterfaceWidgetEventListener {
	
	public void onEvent(InterfaceWidget.Event e, Object src);

}
