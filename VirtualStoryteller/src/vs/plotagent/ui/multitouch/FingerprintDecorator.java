package vs.plotagent.ui.multitouch;

import java.awt.event.ActionEvent;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.PickResult;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.AbstractVisibleComponent;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.util.math.Vector3D;

public class FingerprintDecorator extends MTComponent {
	
	private MTApplication _app;
	private MTPolygon _decorated;
	
	
	public FingerprintDecorator(MTPolygon decorated, MTApplication app) {
		super(app);
		_app = app;
		_decorated = decorated;
		
		Vector3D center = _decorated.getCenterPointGlobal();
		
		FingerprintHandle h1 = new FingerprintHandle(new Vector3D(
				center.x - _decorated.getWidthXY(TransformSpace.GLOBAL)/2 - FingerprintHandle.radiusX,
				center.y,
				0),
				_app);
		
		FingerprintHandle h2 = new FingerprintHandle(new Vector3D(
				center.x + _decorated.getWidthXY(TransformSpace.GLOBAL)/2 + FingerprintHandle.radiusX,
				center.y,
				0),
				_app);		
		
		_decorated.addChild(h1);
		_decorated.addChild(h2);
		addChild(_decorated);
		
		
		
/*		registerInputProcessor(new ScaleProcessor(app));
		addGestureListener(ScaleProcessor.class, new DefaultScaleAction());
		registerInputProcessor(new RotateProcessor(app));
		addGestureListener(ScaleProcessor.class, new DefaultRotateAction());
*/		
	}
	

}
