package vs.plotagent.ui.multitouch.model;

import java.util.Observable;

/**
 * Overrides Observable so that changes are actually propagated.
 * 
 * @author swartjes
 *
 */
public class MyObservable extends Observable {
	
	public void notifyObservers() {
		// Otherwise it won't propagate changes:
		setChanged();
		super.notifyObservers();
	}
	
	public void notifyObservers(Object b) {
		// Otherwise it won't propagate changes:
		setChanged();
		super.notifyObservers(b);
	}

}
