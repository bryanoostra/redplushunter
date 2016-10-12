package vs.plotagent.ui.multitouch.view.story;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;

/**
 * Represents the roll of the parchment on which the story is being displayed. The parchment roll
 * needs special handling of dragging.
 */
public class ParchmentRoll extends MTRectangle {

	//private boolean initialized;
	//private boolean allowedToMove = true;
	
	private StoryWidget storyWidget;
	private static String fileNameParchmentRoll = MultitouchInterfaceSettings.FOLDER_NAME.concat("parchment_roll.PNG");

	public ParchmentRoll(MTApplication mtApp, StoryWidget storyWid) {
		super(0, 0, 100, 20, mtApp);
		
		storyWidget = storyWid;
		
		PImage txture = mtApp.loadImage(fileNameParchmentRoll);
		//float scale = 400/txture.width;
		//txture.resize((int)(txture.width*scale), (int)(txture.height*scale));
		setTexture(txture);
		setNoStroke(true);
		
		removeAllGestureEventListeners();
		
		addGestureListener(DragProcessor.class, new DefaultDragAction() {
			public boolean processGestureEvent(MTGestureEvent g) {
				if (g instanceof DragEvent){
					DragEvent dragEvent = (DragEvent)g;
					int id = dragEvent.getId();
					Vector3D fromGlobal = dragEvent.getFrom();
					if(id==MTGestureEvent.GESTURE_UPDATED && containsPointGlobal(fromGlobal)) {
						//update the storyWidget with movement of this DragEvent
						storyWidget.scrollMoved(dragEvent.getTranslationVect());
					}
					if (id==MTGestureEvent.GESTURE_ENDED) {
						//update the storyWidget that the scroll stopped moving
						storyWidget.scrollFinishedMoving();
					}
				}
				return true;
			}
		});
	}
}
