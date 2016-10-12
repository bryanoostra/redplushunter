package vs.plotagent.ui.multitouch.view.map;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

import vs.Config;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.view.MTButton;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;
import vs.plotagent.ui.multitouch.view.actioninterface.MTButtonStrip;

/**
 * Widget for choosing whether a character is human or computer controlled.
 * 
 * @author swartjes
 *
 */
public class ControlChooser extends MTRoundRectangle {
	
	public enum ControlChoice {human, computer, none}
	
	private VSTMultitouchApplication mtApp;
	private ControlChoice choice;
	private CharacterView characterView;
	
	private static int IMG_SIZE = 60;

	
	public ControlChooser(ControlChoice currentControl, CharacterView view, VSTMultitouchApplication app) {
		super(0, 0, 0, IMG_SIZE*2 + 15, IMG_SIZE + 10, 10f, 10f, app);
			
		mtApp = app;
		choice = currentControl;
		characterView = view;
		
		PImage humanImg = mtApp.loadImage(Config.IMAGE_DIR + "user.png");
		humanImg.resize(IMG_SIZE, IMG_SIZE);
		PImage computerImg = mtApp.loadImage(Config.IMAGE_DIR  + "computer.png");
		computerImg.resize(IMG_SIZE, IMG_SIZE);
			
		if (humanImg == null || computerImg == null) {
			System.out.println("Warning: images for ControlChooser not found!");
		} else {
					
			MTButton humanB = new ControlChoiceButton(humanImg, this, ControlChoice.human, app);
			MTButton compB = new ControlChoiceButton(computerImg, this, ControlChoice.computer, app);
			
			MTButtonStrip bStrip = new MTButtonStrip();
			bStrip.registerButton(humanB);
			bStrip.registerButton(compB);
			humanB.registerStrip(bStrip);
			compB.registerStrip(bStrip);
			
			if (choice.equals(ControlChoice.human) ) {
				humanB.setSelected(true, false);
			} else if (choice.equals(ControlChoice.computer)) {
				compB.setSelected(true, false);
			}
			
/*			humanB.addGestureListener(TapProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent g) {
					if (g instanceof TapEvent){
						TapEvent clickEvent = (TapEvent)g;
						
						if (clickEvent.getId() == MTGestureEvent.GESTURE_ENDED) {
							
						}
					}
					return true;
				}
			});
*/			
			//humanB.setSizeXYGlobal(40, 40);
			//compB.setSizeXYGlobal(40, 40);
			humanB.translate(new Vector3D(5, 5, 0));
			compB.translate(new Vector3D(IMG_SIZE + 10, 5, 0));
			
			addChild(humanB);
			addChild(compB);
		}
	}
	
	public void setPosition(Vector3D pos) {
		this.setPositionGlobal(pos);
	}
	
	public ControlChoice getControlChoice() {
		return choice;
	}
	
	public void setControlChoice(ControlChoice c) {
		choice = c;
	}
	
	/**
	 * Enforces making a certain choice.
	 * @param c the choice made by the user.
	 */
	public void choiceMade(ControlChoice c) {
		setControlChoice(c);
		
		switch (c) {
		case human:			
			mtApp.getVSTMultitouchController().humanControl(characterView.getURI());
			//if we ARE using tangibles, the image of human controlled characters should be grayed
			if(MultitouchInterfaceSettings.USE_FIDUCIALS || MultitouchInterfaceSettings.USE_CCV_RECTANGLE_RECOGNITION) {
				characterView.setGrayed(true);
			}
			break;
		case computer:
			mtApp.getVSTMultitouchController().computerControl(characterView.getURI());
			characterView.setGrayed(false);
			break;
		}
		
	}
	
	
}
