package vs.plotagent.ui.multitouch.view.actioninterface;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.math.Vector3D;

import vs.plotagent.ui.multitouch.model.InterfaceModel;
import vs.plotagent.ui.multitouch.view.MTRoundButton;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;

/**
 * A button representing an action category.
 */
public class CategoryButton extends MTRoundButton {
	
	private MTTextArea _label1;
	private MTTextArea _label2;
	private MTTextArea _labelSingle1;
	private MTTextArea _labelSingle2;
	
	private String _text;
		
	private boolean isDoubleSided;
	
	private VSTMultitouchApplication _mtApp;
			
	public static final float ARC = 5;
	
	public static enum VisibilityState {one, two, both} 
	
	public CategoryButton(String label, VSTMultitouchApplication app) {
		super(ARC, ARC, app);
		
		_mtApp = app;
		_text = label;
		
		setFillColor(InterfaceWidget.GRAY);
		
		setComposite(true);
			
		_label1 = new MTTextArea(app, InterfaceWidget.getFont(app));
		_label1.setText(label);
		_label1.setNoFill(true);
		_label1.setNoStroke(true);
		_label1.unregisterAllInputProcessors();
		_label1.setPositionRelativeToParent(new Vector3D((float)(.5*_label1.getWidthXY(TransformSpace.LOCAL)),(float)(.5*_label1.getHeightXY(TransformSpace.LOCAL)),0));
		
		_label2 = new MTTextArea(app, InterfaceWidget.getFont(app));
		_label2.setText(label);
		_label2.setNoFill(true);
		_label2.setNoStroke(true);
		_label2.unregisterAllInputProcessors();
		
		_label2.rotateZ(_label2.getCenterPointLocal(), 180);
		//_label2.translate(new Vector3D(0, _label1.getHeightXY(TransformSpace.LOCAL)*2, 0));
		_label2.setPositionRelativeToParent(new Vector3D((float)(.5*_label2.getWidthXY(TransformSpace.LOCAL)),(float)(_label1.getHeightXY(TransformSpace.LOCAL) + .5*_label2.getHeightXY(TransformSpace.LOCAL)),0));
		
		
		_labelSingle1 = new MTTextArea(app, InterfaceWidget.getFont(app));
		_labelSingle1.setText(label);
		_labelSingle1.setNoFill(true);
		_labelSingle1.setNoStroke(true);
		//_labelSingle1.translate(new Vector3D(0, _label1.getHeightXY(TransformSpace.LOCAL)/2, 0));
		_labelSingle1.setPositionRelativeToParent(new Vector3D((float)(.5*_labelSingle1.getWidthXY(TransformSpace.LOCAL)),(float)(1*_labelSingle1.getHeightXY(TransformSpace.LOCAL)),0));
		_labelSingle1.unregisterAllInputProcessors();
		
		_labelSingle2 = new MTTextArea(app, InterfaceWidget.getFont(app));
		_labelSingle2.setText(label);
		_labelSingle2.setNoFill(true);
		_labelSingle2.setNoStroke(true);
		_labelSingle2.rotateZ(_labelSingle2.getCenterPointLocal(), 180f);
		//_labelSingle2.translate(new Vector3D(0, _label1.getHeightXY(TransformSpace.LOCAL)*2, 0));
		_labelSingle2.setPositionRelativeToParent(new Vector3D((float)(.5*_labelSingle2.getWidthXY(TransformSpace.LOCAL)),(float)(1*_labelSingle2.getHeightXY(TransformSpace.LOCAL)),0));
		_labelSingle2.unregisterAllInputProcessors();
		
		addChild(_label1);
		addChild(_label2);
		addChild(_labelSingle1);
		addChild(_labelSingle2);
		
		//setWidthLocal(_label1.getWidthXY(TransformSpace.LOCAL));
		//setHeightLocal(_label1.getHeightXY(TransformSpace.LOCAL) + _label2.getHeightXY(TransformSpace.LOCAL));
		float width = _label1.getWidthXY(TransformSpace.LOCAL);
		float height = _label1.getHeightXY(TransformSpace.LOCAL) + _label2.getHeightXY(TransformSpace.LOCAL);
		setSizeLocal(width, height);
		
		setNoStroke(true);	
	}
	
	public String getCategory() {
		return _text;
	}
	
	public void setSelected(boolean selected, boolean propagate) {
		super.setSelected(selected, propagate);
		InterfaceModel im = _mtApp.getVSTMultitouchController().getInterfaceModel();
		if(im.isCategoryStripEnabled()) {
			if (selected) {
				setFillColor(InterfaceWidget.GREEN);
			} else {
				setFillColor(InterfaceWidget.YELLOW);
			}
		}else {
			setFillColor(InterfaceWidget.GRAY);
		}
	}
	
	public void setSelected(boolean selected) {
		super.setSelected(selected);
		InterfaceModel im = _mtApp.getVSTMultitouchController().getInterfaceModel();
		if(im.isCategoryStripEnabled()) {
			if (selected) {
				setFillColor(InterfaceWidget.GREEN);
			} else {
				setFillColor(InterfaceWidget.YELLOW);
			}
		} else {
			setFillColor(InterfaceWidget.GRAY);
		}
	}
	
	public void setVisibilityState(VisibilityState state) {
		if (state.equals(VisibilityState.one)) {
			_label1.setVisible(false);
			_label2.setVisible(false);
			_labelSingle1.setVisible(true);
			_labelSingle2.setVisible(false);
		} else if (state.equals(VisibilityState.two)) {
			_label1.setVisible(false);
			_label2.setVisible(false);
			_labelSingle1.setVisible(false);
			_labelSingle2.setVisible(true);
		} else if (state.equals(VisibilityState.both)) {
			_label1.setVisible(true);
			_label2.setVisible(true);
			_labelSingle1.setVisible(false);
			_labelSingle2.setVisible(false);
		}
	}

	public void setDoubleSided(boolean doubleSided) {
		
		isDoubleSided = doubleSided;
				
		if (isDoubleSided) {
			_label1.setVisible(true);
			_label2.setVisible(true);
			_labelSingle1.setVisible(false);
		
		} else {
			_labelSingle1.setVisible(true);
			_label1.setVisible(false);
			_label2.setVisible(false);
		}
	}
	
	public boolean isDoubleSided() {
		return isDoubleSided;
	}
}
