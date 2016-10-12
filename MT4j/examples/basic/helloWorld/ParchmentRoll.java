package basic.helloWorld;

import org.mt4j.MTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

public class ParchmentRoll extends MTImageButton {

	private boolean initialized;
	private boolean isDragged;
	private StoryTextArea story;

	public ParchmentRoll(PImage imgParchmentRoll, MTApplication mtApp, StoryTextArea storyTextArea) {
		super(imgParchmentRoll, mtApp);
		
		story = storyTextArea;
		//this.setAnchor(PositionAnchor.UPPER_LEFT);
		registerInputProcessor(new DragProcessor(mtApp));
		removeAllGestureEventListeners();
		
		//XXX: wat doet dit?
		//this.unregisterAllInputProcessors();
		
		addGestureListener(DragProcessor.class, new DefaultDragAction() 
		{
			public boolean processGestureEvent(MTGestureEvent g) {
				if (g instanceof DragEvent){
					DragEvent dragEvent = (DragEvent)g;
					
					switch (dragEvent.getId()) {
					
					case MTGestureEvent.GESTURE_DETECTED:
						isDragged = true;
						//Put target on top -> draw on top of others
						//sendToFront();
						break;
						
					case MTGestureEvent.GESTURE_UPDATED:
						Vector3D toGlobal = dragEvent.getTo();
						setPositionGlobal(toGlobal);

						Vector3D relative = getPosition(TransformSpace.RELATIVE_TO_PARENT);
						if(relative.y < StoryTextArea.MIN_HEIGHT) {
							relative.setY(StoryTextArea.MIN_HEIGHT);
							setPositionRelativeToParent(relative);
						}
						story.updatePreferredHeight(relative.y);
						//g.setConsumed(true);
						break;
						
					case MTGestureEvent.GESTURE_ENDED:
						isDragged = false;
						break;
						
					default:
						break;
					}		
				}
				return true;
			}
		}
		);
	}

	public void supertranslate(Vector3D vect) {
		super.translate(vect);
	}
	
	public void translate(Vector3D vect) {
		if(!initialized) {
			super.translate(vect);
		} else {
			super.translate(new Vector3D(0, vect.y, 0, 1));
		}
	}

	public void setInitialized(boolean init) {
		this.initialized = init;
	}
	
	public boolean isBeingDragged() {
		return isDragged;
	}
}
