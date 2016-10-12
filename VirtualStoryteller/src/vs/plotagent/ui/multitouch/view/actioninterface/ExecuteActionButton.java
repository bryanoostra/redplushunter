package vs.plotagent.ui.multitouch.view.actioninterface;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;

/**
 * Button for executing the selected action.
 */
public class ExecuteActionButton extends MTComponent {
	
	private VSTMultitouchApplication mtApp;
	
	private MTEllipse _button;
	private boolean _enabled = false;
	
	public ExecuteActionButton(VSTMultitouchApplication app) {
		super(app);
		mtApp = app;
		
		float buttonRadius = InterfaceWidget.getButtonRadius(app);
		_button = new MTEllipse(mtApp, new Vector3D(0, 0, 0), buttonRadius, buttonRadius);
		
		_button.setFillColor(InterfaceWidget.GRAY);
		_button.setStrokeColor(InterfaceWidget.BLACK);
		
		_button.setGestureAllowance(DragProcessor.class, false);
		_button.setGestureAllowance(RotateProcessor.class, false);
		_button.setGestureAllowance(ScaleProcessor.class, false);
		
		_button.setGestureAllowance(TapProcessor.class, true);
		_button.registerInputProcessor(new TapProcessor(mtApp));
		_button.addGestureListener(TapProcessor.class, new ExecuteAction(mtApp));

		
		addChild(_button);
	}
	
	public void setEnabled(boolean enabled) {
		_enabled = enabled;
		
		if (_enabled) {
			_button.setFillColor(InterfaceWidget.GREEN);			
		} else {
			_button.setFillColor(InterfaceWidget.GRAY);
		}
	}
	
	public boolean isEnabled() {
		return _enabled;
	}

}
