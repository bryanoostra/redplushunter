package vs.plotagent.ui.multitouch.view.actioninterface;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.StyleInfo;
import org.mt4j.components.visibleComponents.shapes.MTLine;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

/**
 * Represents the rope between a character image and the action selection interface.
 */
public class LocationCharacterRope extends MTLine {
	
	Vertex _start;
	Vertex _end;
	
	public LocationCharacterRope(Vertex start, Vertex end, MTApplication app) {
		super(app, start, end);
		this.setPickable(false);
				
		_start = start;
		_end = end;
		
		StyleInfo si = new StyleInfo();
		si.setLineStipple((short)0xDDDD);
		this.setStyleInfo(si);
		
		this.setStrokeWeight(7f);
		this.setStrokeColor(InterfaceWidget.BLUE);
	}
	
	public void updateBoth(Vector3D start, Vector3D end) {
		_start = new Vertex(start);
		_end = new Vertex(end);
		this.setVertices(new Vertex[]{_start, _end}); 
	}
	
	public void updateStart(Vector3D start) {
		_start = new Vertex(start);
		this.setVertices(new Vertex[]{_start, _end}); 
	}
	
	public void updateEnd(Vector3D end) {
		_end = new Vertex(end);
		this.setVertices(new Vertex[]{_start, _end}); 
	}
}
