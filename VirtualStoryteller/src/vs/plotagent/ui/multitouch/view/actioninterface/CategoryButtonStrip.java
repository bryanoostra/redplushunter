package vs.plotagent.ui.multitouch.view.actioninterface;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;
import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.model.InterfaceModel;
import vs.plotagent.ui.multitouch.view.MTRoundButton;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;
import vs.plotagent.ui.multitouch.view.map.CharacterView;
import vs.plotagent.ui.multitouch.view.map.MapWidget;

/**
 * Represents a strip of action category buttons.
 */
public class CategoryButtonStrip extends MTRectangle implements IButtonStrip, IInterfaceWidgetComponent {
	
	private VSTMultitouchApplication mtApp;
	private MapWidget mapWidget;
	
	private List<CategoryButton> _buttons;
	private ExecuteActionButton _execButton;
	
	private InterfaceWidgetEventListenerDelegate lisDel;
		
	private static MTColor transparent = new MTColor(255,255,255, MTColor.ALPHA_FULL_TRANSPARENCY);
	
	private CategoryButton.VisibilityState visibilityState;
	
	private Logger logger;
	
	public CategoryButtonStrip(ArrayList<CategoryButton> buttons, VSTMultitouchApplication app, MapWidget mapWidgt) { 
		super(0, 0, 200, 50, app);
		
		logger = LogFactory.getLogger(this);
		
		mtApp = app;
		mapWidget = mapWidgt;
		
		lisDel = new InterfaceWidgetEventListenerDelegate();
				
		setButtons(buttons);

		// By default: select first category
		if (buttons.size() > 0) {
			buttons.get(0).setSelected(true);
		}
				
		setVisibilityState(CategoryButton.VisibilityState.both);
		
		setPickable(false);
	}
	
	public CategoryButtonStrip(String message, VSTMultitouchApplication app, MapWidget mapWidgt) { 
		super(0, 0, 200, 50, app);
		
		logger = LogFactory.getLogger(this);
		
		mtApp = app;
		mapWidget = mapWidgt;
		
		lisDel = new InterfaceWidgetEventListenerDelegate();
				
		setMessage(message);

		getExecuteButton().setEnabled(false);
				
		setVisibilityState(CategoryButton.VisibilityState.both);
		
		setPickable(false);
	}
	
	public Iterator<CategoryButton> buttonIterator() {
		return _buttons.iterator();
	}
	
	public String getSelectedCategory() {
		for (CategoryButton b: _buttons) {
			if (b.isSelected()) {
				return b.getCategory();
			}
		}
		return null;
	}
	
	public void setSelectedCategory(String cat) {
		for (CategoryButton b: _buttons) {
			b.setSelected(false);
			if (b.getCategory().equals(cat)) {
				b.setSelected(true);
				//System.out.println("categorybuttonstrip select: " +b.getCategory());
				logger.info("CategoryButtonStrip selected category: " +b.getCategory());
			}
		}
	}
	
	public void buttonSelected(Object b, boolean isSelected) {
		if (isSelected) {			
			for (MTRoundButton other: _buttons) {
				if (other != b) {
					other.setSelected(false, false);
				}
			}
			InterfaceModel im = mtApp.getVSTMultitouchController().getInterfaceModel();
			if(im.isCategoryStripEnabled() && !im.getNonMoveCategoriesShouldBeHidden()) {
				fireEvent(InterfaceWidget.Event.categorySelected, this);
				logger.info("Category button selected.");
			}
		}
	}
	
	public CategoryButton.VisibilityState getVisibilityState() {
		return visibilityState;
	}
	
	public void setVisibilityState(CategoryButton.VisibilityState state) {
		logger.fine("Set visibility state: " + state);
		visibilityState = state;
		
		for (CategoryButton b: _buttons) {
			b.setVisibilityState(state);
		}
	}
	
	private void setMessage(String message) {
		
		float R = InterfaceWidget.getButtonRadius(mtApp);
		
		setAnchor(PositionAnchor.UPPER_LEFT);
		
		float sigWidth = 0.0f;
		float sigHeight = 76.0f; //default value so it always is non-zero
		
		sigWidth += InterfaceWidget.EMPTY_SPACE_FOR_FINGERS_f*R;
		
		_buttons = new ArrayList<CategoryButton>();
		CategoryButton button = new CategoryButton(message, mtApp);
		_buttons.add(button);
		button.registerStrip(this);
		button.translate(new Vector3D(sigWidth, 0, 0));
		sigHeight = button.getHeightXY(TransformSpace.LOCAL);
		if(button.isVisible() && button.isEnabled()) {
			sigWidth += button.getWidthXY(TransformSpace.LOCAL);
			sigWidth += InterfaceWidget.CATEGORY_BUTTON_SEPARATION_f*R;
		}
		addChild(button);			
		button.setEnabled(false);
		button.setFillColor(InterfaceWidget.GRAY);
		
		//create executeButton in background, but do not add to interface
		_execButton = new ExecuteActionButton(mtApp);
		if (mtApp.getVSTMultitouchController().getInterfaceModel().getSelectedAction() != null) {
			_execButton.setEnabled(true);
		}
		
		sigWidth += InterfaceWidget.EMPTY_SPACE_FOR_FINGERS_f*R;
		
		this.setWidthLocal(sigWidth);
		this.setHeightLocal(sigHeight);
		
		this.setFillColor(transparent);
		setNoStroke(true);
	}
	
	private void setButtons(ArrayList<CategoryButton> buttons) {
		_buttons = buttons;
		
		float R = InterfaceWidget.getButtonRadius(mtApp);
		
		// Rebuild everything
		setAnchor(PositionAnchor.UPPER_LEFT);
		
		float sigWidth = 0.0f;
		float sigHeight = 76.0f; //default value so it always is non-zero
		
		float buttonRadius = InterfaceWidget.getButtonRadius(mtApp);
		sigWidth += InterfaceWidget.EMPTY_SPACE_FOR_FINGERS_f*R;
		
		//remember place to put execute button		
		float executeButtonHorizontalOffset = sigWidth;
		
		sigWidth += buttonRadius*2 + InterfaceWidget.CATEGORY_STRIP_PADDING_f*R;
		
		for (CategoryButton butt: _buttons) {
			butt.registerStrip(this);
			butt.translate(new Vector3D(sigWidth, 0, 0));
			sigHeight = butt.getHeightXY(TransformSpace.LOCAL);
			if(butt.isVisible() && butt.isEnabled()) {
				sigWidth += butt.getWidthXY(TransformSpace.LOCAL);
				sigWidth += InterfaceWidget.CATEGORY_BUTTON_SEPARATION_f*R;
			}
			addChild(butt);			
		}
		sigWidth -= InterfaceWidget.CATEGORY_BUTTON_SEPARATION_f*R;
		
		//create character image for CategoryButtonStrip of the Action Selection Interface 
		//CharacterView cv = mapWidget.getCurrentCharacter();
		//MTRectangle imageRectangle = cv.getImage();
		//PImage pi = imageRectangle.getTexture();
		//pi = pi.get();
		//Image scaledImage = pi.getImage().getScaledInstance(-1, (int)sigHeight, Image.SCALE_DEFAULT);
		//MTRectangle myImageRectangle = new MTRectangle(new PImage(scaledImage), mtApp);
		
		//create character image for CategoryButtonStrip of the Action Selection Interface
		PImage pi = mapWidget.getCurrentCharacterImage();
		pi = pi.get(); //clone
		pi.resize(0, (int)sigHeight);
		MTRectangle myImageRectangle = new MTRectangle(pi, mtApp);
				
		myImageRectangle.setNoStroke(true);
		myImageRectangle.setPickable(false);
		
		//add character image to CategoryButtonStrip of the Action Selection Interface
		sigWidth += InterfaceWidget.CATEGORY_STRIP_PADDING_f*R;
		addChild(myImageRectangle);
		myImageRectangle.translate(new Vector3D(sigWidth, 0, 0));
		float scaledImageWidth = pi.width;
		sigWidth += scaledImageWidth;
			
		_execButton = new ExecuteActionButton(mtApp);
		if (mtApp.getVSTMultitouchController().getInterfaceModel().getSelectedAction() != null) {
			_execButton.setEnabled(true);
		}
		addChild(_execButton);
		_execButton.translate(new Vector3D(executeButtonHorizontalOffset + buttonRadius, sigHeight/2, 0));
		
		sigWidth += InterfaceWidget.EMPTY_SPACE_FOR_FINGERS_f*R;
		
		this.setWidthLocal(sigWidth);
		this.setHeightLocal(sigHeight);
		
		this.setFillColor(transparent);
		setNoStroke(true);
	}
	
	public ExecuteActionButton getExecuteButton() {
		return _execButton;
	}
	
	public void addEventListener(IInterfaceWidgetEventListener listener) {
		lisDel.addEventListener(listener);
	}
	
	public void removeEventListener(IInterfaceWidgetEventListener listener) {
		lisDel.removeEventListener(listener);
	}	
	
	public void fireEvent(InterfaceWidget.Event event, Object src) {
		lisDel.fireEvent(event, src);
	}
}
