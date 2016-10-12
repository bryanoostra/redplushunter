package vs.plotagent.ui.multitouch.view.actioninterface;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.util.math.Vertex;

import vs.plotagent.ui.multitouch.view.map.CharacterView.CharacterStatus;

/**
 * Represents the rope between a character image and the action selection interface.
 */
public class InterfaceCharacterRope extends MTLine {
	
	public InterfaceCharacterRope(Vertex start, Vertex end, MTApplication app) {
		super(app, start, end);
		this.setStrokeWeight(10.0f);
		this.setStrokeColor(InterfaceWidget.TRANSPARENT);
		this.setPickable(false);
	}
	
	public void update(Vertex start, Vertex end) {
		this.setVertices(new Vertex[]{start, end}); 
	}
	
	public void setStatus(CharacterStatus cs) {
		switch(cs) {
		case nonPickable:
			//Never happens? hmm, now it does...
			//this.setStrokeColor(InterfaceWidget.BLACK);
			this.setStrokeColor(InterfaceWidget.TRANSPARENT);
			break;
		case pickable:
			this.setStrokeColor(InterfaceWidget.ORANGE);
			break;
		case allowedDestination:
			this.setStrokeColor(InterfaceWidget.GREEN);
			break;
		case disallowedDestination:
			this.setStrokeColor(InterfaceWidget.RED);
			break;			
		}
	}
}
