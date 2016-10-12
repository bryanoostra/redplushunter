package vs.plotagent.ui.multitouch.view.actioninterface;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.math.Vector3D;

import vs.plotagent.ui.multitouch.model.PossibleAction;
import vs.plotagent.ui.multitouch.view.MTRoundButton;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;

/**
 * An "action" button, i.e. a button with a possible action associated to it.
 */
public class ActionButton extends MTRoundButton {
	
	private MTTextArea _label;
	
	private PossibleAction _action;
	
	private IButtonStrip _strip;

	private VSTMultitouchApplication _mtApp;
	
	public static final float ARC = 5;
		
	public ActionButton(PossibleAction action, VSTMultitouchApplication app) {
		super(ARC, ARC, app);

		_mtApp = app;
		_action = action;
		
		_label = new MTTextArea(app, InterfaceWidget.getFont(app));
		_label.setText(action.getDescription());
		_label.setFillColor(InterfaceWidget.TRANSPARENT);
		_label.setStrokeColor(InterfaceWidget.TRANSPARENT);
		_label.setPickable(false);
		
		//doesn't work here?
		setFillColor(InterfaceWidget.WHITE);
		
		//setPickable(false);

		//setWidthLocal(_label.getWidthXY(TransformSpace.LOCAL));
		//setHeightLocal(_label.getHeightXY(TransformSpace.LOCAL));
		float width = _label.getWidthXY(TransformSpace.LOCAL);
		float height = _label.getHeightXY(TransformSpace.LOCAL);
		setSizeLocal(width, height);
		//_label.setPositionRelativeToParent(new Vector3D((float)(.5*_label.getWidthXY(TransformSpace.LOCAL)),(float)(.5*_label.getHeightXY(TransformSpace.LOCAL)),0));
		_label.setPositionRelativeToParent(new Vector3D((float)(.5*_label.getWidthXY(TransformSpace.LOCAL)),(float)(.5*_label.getHeightXY(TransformSpace.LOCAL)),0));
		//_label.setPositionRelativeToOther(getParent(), getCenterPointRelativeToParent());
		
		setNoStroke(true);
		
		addChild(_label);
	}
	
	public PossibleAction getAction() {
		return _action;
	}
	
	public void setSelected(boolean selected, boolean propagate) {
		boolean enabled = _mtApp.getVSTMultitouchController().getInterfaceModel().isCategoryStripEnabled();
		if(enabled) {
			super.setSelected(selected, propagate);
		}
		updateColor(selected);
	}
	

	public void setSelected(boolean selected) {
		boolean enabled = _mtApp.getVSTMultitouchController().getInterfaceModel().isCategoryStripEnabled();
		if(enabled) {
			super.setSelected(selected);
		}
		updateColor(selected);
	}
	
	private void updateColor(boolean selected) {
		if(selected) {
			setFillColor(InterfaceWidget.GREEN);
		} else if (isEnabled() && _mtApp.getVSTMultitouchController().getInterfaceModel().isCategoryStripEnabled()) {
			setFillColor(InterfaceWidget.YELLOW);
		} else {
			setFillColor(InterfaceWidget.GRAY);
		}
	}
}
