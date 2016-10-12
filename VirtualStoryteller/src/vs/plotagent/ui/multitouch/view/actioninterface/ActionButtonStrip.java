package vs.plotagent.ui.multitouch.view.actioninterface;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.math.Vector3D;

import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.model.PossibleAction;
import vs.plotagent.ui.multitouch.model.PossibleMoveAction;
import vs.plotagent.ui.multitouch.view.MTRoundButton;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;

/**
 * A strip of action buttons that belong together (i.e., only one is selected at one time)
 */
public class ActionButtonStrip extends MTRoundRectangle implements IButtonStrip {
	
	private VSTMultitouchApplication mtApp;
	private InterfaceWidget interfaceWidget;
	
	private List<ActionButton> _buttons;
	
	private MTComponent buttonPanel;
	private MTRoundRectangle hideButton;
	private MTRoundRectangle showButton;
	
	private ActionButtonStrip _other;
	
	private static float PLUS_MINUS_f = 1.5f;
	private static float buttonStickOutX_f = .4f; 
	private static float buttonStickOutY_f = .4f;
	
	public static final float ARC_f = InterfaceWidget.ARC_f;
	
	private boolean isHidden = false;
	private boolean _isShadow = false;
		
	private float _x;
	private float _y;
	
	public ActionButtonStrip(boolean isShadow, InterfaceWidget parent, VSTMultitouchApplication app) {
		super(0, 0, 0, 200, 50, ARC_f*InterfaceWidget.getButtonRadius(app), ARC_f*InterfaceWidget.getButtonRadius(app), app);
		
		interfaceWidget = parent;
		
		mtApp = app;
		
		_isShadow = isShadow;
		
		_buttons = new ArrayList<ActionButton>();
		
		float R = InterfaceWidget.getButtonRadius(mtApp);
				
		// Add "hide" button
		float ARC = ARC_f*InterfaceWidget.getButtonRadius(app);
		hideButton = new MTRoundRectangle(0, 0, 0, PLUS_MINUS_f*R, PLUS_MINUS_f*R, ARC, ARC, mtApp);
		hideButton.setFillColor(InterfaceWidget.CYAN);		
		hideButton.setStrokeColor(InterfaceWidget.BLACK);
		//hideButton.setPickable(false);
		//hideButton.setEnabled(true);
		//hideButton.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		//hideButton.setVisible(false);
		hideButton.removeAllGestureEventListeners();
		hideButton.setGestureAllowance(TapProcessor.class, true);
		hideButton.registerInputProcessor(new TapProcessor(mtApp));
		hideButton.addGestureListener(TapProcessor.class, new ShowHideAction(this, ShowHideAction.ShowHide.hide));
		addChild(hideButton);

		Vector3D centerHide = hideButton.getCenterPointGlobal();
		float strokeWeight = 0.1f*R;//hideButton.getWidthXY(TransformSpace.GLOBAL);
		
		float x1 = centerHide.x-.3f*PLUS_MINUS_f*R;
		float x2 = centerHide.x+.3f*PLUS_MINUS_f*R;
		float y1 = centerHide.y+.5f*buttonStickOutY_f*R;
		//float y2 = centerHide.y+.5f*buttonStickOutY_f*R;
		//MTLine minusLine = new MTLine(app, x1, y1, x2, y2);
		float width = x2-x1;//minusLine.getLength();
		float height = .2f*width;
		MTRectangle minus = new MTRectangle(x1, y1-.5f*height, width, height, app);
		
		minus.setStrokeColor(InterfaceWidget.BLACK);
		minus.setFillColor(InterfaceWidget.BLACK);
		minus.setStrokeWeight(strokeWeight);
		minus.setNoStroke(true);
		minus.setPickable(false);
		hideButton.addChild(minus);
		
		// Add "show" button
		showButton = new MTRoundRectangle(0, 0, 0, PLUS_MINUS_f*R, PLUS_MINUS_f*R, ARC, ARC, mtApp);
		showButton.removeAllGestureEventListeners();
		showButton.setFillColor(InterfaceWidget.CYAN);	
		showButton.setStrokeColor(InterfaceWidget.BLACK);
		//showButton.setPickable(false);
		//showButton.setVisible(false);
		showButton.setGestureAllowance(TapProcessor.class, true);
		showButton.registerInputProcessor(new TapProcessor(mtApp));
		showButton.addGestureListener(TapProcessor.class, new ShowHideAction(this, ShowHideAction.ShowHide.show));
		addChild(showButton);
				
		Vector3D centerShow = showButton.getCenterPointGlobal();
		float a_x1 = centerShow.x-.3f*PLUS_MINUS_f*R;
		float a_x2 = centerShow.x+.3f*PLUS_MINUS_f*R;
		float a_y1 = centerShow.y+.5f*buttonStickOutY_f*R;
		//float a_y2 = centerShow.y+.5f*buttonStickOutY_f*R;
		float b_x1 = centerShow.x;
		//float b_x2 = centerShow.x;
		float b_y1 = centerShow.y-.3f*PLUS_MINUS_f*R+.5f*buttonStickOutY_f*R;
		//float b_y2 = centerShow.y+.6f*R+.5f*buttonStickOutY_f*R;
		//MTLine horizontalLine = new MTLine(app, a_x1, a_y1, a_x2, a_y2);
		//MTLine verticalLine = new MTLine(app, b_x1, b_y1, b_x2, b_y2);
		float long_side = a_x2-a_x1;
		float short_side = .2f*long_side;
		MTRectangle horizontal = new MTRectangle(a_x1, a_y1-.5f*short_side, long_side, short_side, app);
		MTRectangle vertical = new MTRectangle(b_x1-.5f*short_side, b_y1, short_side, long_side, app);
		horizontal.setFillColor(InterfaceWidget.BLACK);
		vertical.setFillColor(InterfaceWidget.BLACK);
		horizontal.setStrokeWeight(strokeWeight);
		vertical.setStrokeWeight(strokeWeight);
		horizontal.setNoStroke(true);
		vertical.setNoStroke(true);
		horizontal.setPickable(false);
		vertical.setPickable(false);
		showButton.addChild(horizontal);
		showButton.addChild(vertical);
		showButton.translate(new Vector3D(buttonStickOutX_f*R, -buttonStickOutY_f*R, 0));
				
		buttonPanel = new MTComponent(app);
		buttonPanel.removeAllGestureEventListeners();
		//buttonPanel.addChild(hideButton);
		
		this.removeAllGestureEventListeners();
		
		//setPickable(false);
	}
	
	/**
	 * Whether the strip is a copy of another strip, or whether this strip should be seen as the 
	 * original. Allows us to distinguish action strips in two categories, for two different sides
	 * of the interface.  
	 * 
	 * @return
	 */
	public boolean isCopy() {
		return _isShadow;
	}
	
	public void setOpposite(ActionButtonStrip other) {
		_other = other;
	}
	
	public ActionButtonStrip getOpposite() {
		return _other;
	}
	
	public void selectAction(PossibleAction pa) {
		for (ActionButton b: _buttons) {
			if (pa!=null && b.getAction()!=null) {
				if(b.getAction().getPrologDescription()!=null && b.getAction().getPrologDescription().equals(pa.getPrologDescription())) {
					//found the button matching the action to be selected
					b.setSelected(true, false);
				} else if (b.getAction().getPrologDescription()==null && pa.getPrologDescription()==null) {
					//found the button of the doNothing action
					b.setSelected(true, false);
				} else {
					//this button does not match the action to be selected, so do not select
					if(b.getAction() instanceof PossibleMoveAction) {
						//disable this button when the action to be selected is a (transit)Move action
						b.setEnabled(false);
					}
					b.setSelected(false, false);
				}
				
			} else {
				//when the action to be selected is null or the button does seem to have null as its action, do nothing
				//(this happens after clearSelection() was called when standing on a 'red' location)
				b.setSelected(false, false);
			}
		}
	}
	
	public void hide(boolean hide) {
		hide(hide, true);
	}
	
	public void hide(boolean hide, boolean propagate) {
		isHidden = hide;
		
		if (isHidden) {
			buttonPanel.setVisible(false);
			showButton.setVisible(true);
			hideButton.setVisible(false);
			this.setFillColor(InterfaceWidget.TRANSPARENT);
			this.setNoStroke(true);
		} else {
			buttonPanel.setVisible(true);
			hideButton.setVisible(true);
			showButton.setVisible(false);
			this.setFillColor(InterfaceWidget.EASY_BLUE);
			this.setNoStroke(false);
			
			for (MTRoundButton b: _buttons) {
				b.sendToFront();
			}
		}
		
		if (propagate) {
			interfaceWidget.hideChange(this);
		}
	}
	
	public void setVisibilityOfHideButton() {
		if (!isHidden && _other.isHidden()) {
			hideButton.setVisible(false);
		}
	}
	
	public boolean isHidden() {
		return isHidden;
	}
	
	public void setButtons(ArrayList<ActionButton> buttons) {
		_buttons = buttons;		
		
		float R = InterfaceWidget.getButtonRadius(mtApp);
		
		//setAnchor(PositionAnchor.UPPER_LEFT);
		
		float sigWidth = 0.0f;
		float sigHeight = InterfaceWidget.ACTION_STRIP_PADDING_f*R;
		
		for (MTRoundButton butt: _buttons) {
			butt.registerStrip(this);
			
			if (butt.getWidthXY(TransformSpace.LOCAL) > sigWidth) {
				sigWidth = butt.getWidthXY(TransformSpace.LOCAL);
			}
						
			butt.translate(new Vector3D(InterfaceWidget.ACTION_STRIP_PADDING_f*R, sigHeight, 0));
			sigHeight += butt.getHeightXY(TransformSpace.LOCAL);
			buttonPanel.addChild(butt);
			
			sigHeight += InterfaceWidget.ACTION_BUTTON_SEPARATION_f*R;
		}
		sigHeight -= InterfaceWidget.ACTION_BUTTON_SEPARATION_f*R;
		
		hideButton.translate(new Vector3D(buttonStickOutX_f*R, sigHeight - buttonStickOutY_f*R, 0));
		//buttonPanel.addChild(hideButton);
		
		for (MTRoundButton butt: _buttons) {
			//butt.setWidthLocal(sigWidth);
			butt.setSizeLocal(sigWidth, butt.getHeightXY(TransformSpace.LOCAL));
			
			//apparently needed here:
			butt.setFillColor(InterfaceWidget.YELLOW);
			
			//butt.sendToFront();
		}
		
		addChild(buttonPanel);
		
		sigWidth += 2*InterfaceWidget.ACTION_STRIP_PADDING_f*R;
		sigHeight += InterfaceWidget.ACTION_STRIP_PADDING_f*R;
		
		//this.setWidthLocal(sigWidth);
		//this.setHeightLocal(sigHeight);
		setSizeLocal(sigWidth, sigHeight);
		
		this.setFillColor(InterfaceWidget.EASY_BLUE);
		this.setStrokeColor(InterfaceWidget.BLACK);
				
		this.setPositionGlobal(new Vector3D(_x - 10 + (float)(.5*sigWidth), _y + (float)(.5*sigHeight), 0));		
	}
	
	public void buttonSelected(Object b, boolean isSelected) {
		if (isSelected) {
			
			for (MTRoundButton other: _buttons) {
				if (other != b) {
					other.setSelected(false, false);
				}
			}
			mtApp.getVSTMultitouchController().getInterfaceModel().setSelectedAction(((ActionButton)b).getAction());
			LogFactory.getLogger(this).info("Action button selected.");
		}
	}
}
