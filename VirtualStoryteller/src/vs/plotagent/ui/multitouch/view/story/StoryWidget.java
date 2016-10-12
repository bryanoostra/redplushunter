package vs.plotagent.ui.multitouch.view.story;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.input.gestureAction.DefaultRotateAction;
import org.mt4j.input.gestureAction.DefaultScaleAction;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateEvent;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Matrix;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;
import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.model.NarratedOperatorResult;
import vs.plotagent.ui.multitouch.view.SoundManager;
import vs.rationalagent.StoryTime;

/**
 * Widget representing a parchment displaying the story so far. The parchment can be rolled
 * in or out to display more or less of the story, and it can be scaled, rotated and moved.
 */
public class StoryWidget extends MTComponent implements Observer {
	
	private MTApplication mtApp;
	private StoryText storyText;
	private ParchmentRoll parchmentRoll;
	private MTRectangle background;
	
	public static MTColor black = new MTColor(0, 0, 0);
		
	//public static float MIN_WIDTH_LOCAL = 320;
	public static float MIN_HEIGHT_LOCAL = 25;
	public static float MAX_HEIGHT_LOCAL = 200;
	
	public static float MIN_SCALE = 2f;
	public static float MAX_SCALE = 7f;
	
	// For speedup
	private int updateCount = 0;
	private float heightCache = 0.0f;
	
	private Logger logger;	
		
	public StoryWidget(float width, MTApplication app) {
		super(app);
		mtApp = app;
		
		logger = LogFactory.getLogger(this);
						
		// Register sounds for the actions that occur.
		if(MultitouchInterfaceSettings.USE_SOUND_EFFECTS) {
			registerSounds();		
		}
		
		heightCache = StoryWidget.MIN_HEIGHT_LOCAL;
		
		// Create the story text part.
		storyText = new StoryText(mtApp, this);
		storyText.setPickable(false);
		//storyText.setWidthLocal(MIN_WIDTH_LOCAL);
		storyText.setNoFill(true);
		storyText.setNoStroke(true);
		
		// Create the parchment roll
		parchmentRoll = new ParchmentRoll(mtApp, this);		
		//scroll.scale(1.2, 1.2, 0, scalingPoint)
				
		//This texture is currently not used as a texture, only in calculating the width of the storywidget
		PImage txture = mtApp.loadImage(MultitouchInterfaceSettings.FOLDER_NAME.concat("parchment_roll_bg.PNG"));
		
		// Scroll roll should be a little wider than the unrolled part. Calculate the factor for that. 
		float factor = (float)parchmentRoll.getTexture().width / txture.width;		

		// Create the scroll background
		background = new MTRectangle(0, 0, parchmentRoll.getWidthXY(TransformSpace.LOCAL), MIN_HEIGHT_LOCAL, mtApp);
		//use fillcolor instead of texture
		background.setFillColor(StoryText.scrollBGColor);
		//background.setTexture(txture);
		background.setNoStroke(true);
		background.translate(new Vector3D(0, parchmentRoll.getHeightXY(TransformSpace.LOCAL), 0));
				
		// Add text and the roll to the background
		background.addChild(storyText);
		background.addChild(parchmentRoll);
		
		// Move scroll up so it is ABOVE background rather than in the top.
		parchmentRoll.translate(new Vector3D(0, - parchmentRoll.getHeightXY(TransformSpace.LOCAL) + 1, 0));
				
		// Set width of the story widget.
		background.setWidthXYGlobal(width);
				
		addChild(background);
				
		// Resize parchment roll slightly so that it "sticks out" in comparison to the scroll.
		parchmentRoll.setWidthLocal(parchmentRoll.getWidthXY(TransformSpace.LOCAL)*factor);
		parchmentRoll.sendToFront();
		
		//FingerprintHandle fh = new FingerprintHandle(new Vector3D(300, 300, 0), 20f, 30f, mtApp);
		//addChild(fh);
				
		background.removeAllGestureEventListeners();
		
		//check if we want the storyarea to be rotatable
		if(MultitouchInterfaceSettings.ENABLE_STORYAREA_ROTATE) {
			background.addGestureListener(RotateProcessor.class, new DefaultRotateAction()
			{
				public boolean processGestureEvent(MTGestureEvent g) {
					if (g instanceof RotateEvent){
						RotateEvent rotateEvent = (RotateEvent)g;
						float x1 = rotateEvent.getFirstCursor().getCurrentEvtPosX();
						float y1 = rotateEvent.getFirstCursor().getCurrentEvtPosY();
						float x2 = rotateEvent.getSecondCursor().getCurrentEvtPosX();
						float y2 = rotateEvent.getSecondCursor().getCurrentEvtPosY();
						
						if (background.containsPointGlobal(new Vector3D(x1, y1, 0)) && 
							background.containsPointGlobal(new Vector3D(x2, y2, 0)) ) {
								super.processGestureEvent(g);
						}
					}
					return true;
				}
			});
		}
		
		//check if we want the storyarea to be scalable/resizable
		if(MultitouchInterfaceSettings.ENABLE_STORYAREA_SCALE) {
			background.addGestureListener(ScaleProcessor.class, new DefaultScaleAction()
			{
				public boolean processGestureEvent(MTGestureEvent g) {
					if (g instanceof ScaleEvent){
						ScaleEvent scaleEvent = (ScaleEvent)g;
						float x1 = scaleEvent.getFirstCursor().getCurrentEvtPosX();
						float y1 = scaleEvent.getFirstCursor().getCurrentEvtPosY();
						float x2 = scaleEvent.getSecondCursor().getCurrentEvtPosX();
						float y2 = scaleEvent.getSecondCursor().getCurrentEvtPosY();
						
						//check if both cursors are within this StoryWidget 
						if (background.containsPointGlobal(new Vector3D(x1, y1, 0)) 
								&& background.containsPointGlobal(new Vector3D(x2, y2, 0)) ) {
							
							//Get (or calculate) the current scale 
							//In a newer version of MT4j this can be don in one command: Vector3D currentScale = background.getLocalMatrix().getScale();
							//We use for now:
							Matrix m = background.getLocalMatrix();
							double currentScaleX = Math.sqrt(m.m00 * m.m00 + m.m10 * m.m10 + m.m20 * m.m20);
							
							//check if current scale is already at max
							if (currentScaleX * scaleEvent.getScaleFactorX() > MAX_SCALE) {
								//do not change anything
							//check if current scale is already at min
                            } else if (currentScaleX * scaleEvent.getScaleFactorX() < MIN_SCALE) {
								//do not change anything
                            //if not MIN or MAX scale, process GestureEvent:
                            } else {
                            	super.processGestureEvent(g);
                            }
						}
					}
					return true;
				}
			});
		}

		background.addGestureListener(DragProcessor.class, new InertiaDragAction()
		{
			public boolean processGestureEvent(MTGestureEvent g) {
				if (g instanceof DragEvent){
					DragEvent dragEvent = (DragEvent)g;
					Vector3D fromGlobal = dragEvent.getFrom();
					if(background.containsPointGlobal(fromGlobal)) {
						switch (dragEvent.getId()) {
						case MTGestureEvent.GESTURE_DETECTED:
							break;
						case MTGestureEvent.GESTURE_UPDATED:
							translate(dragEvent.getTranslationVect());
							break;
						case MTGestureEvent.GESTURE_ENDED:
							//check if we want the fancy InertiaDrag movement
							if(MultitouchInterfaceSettings.USE_INERTIA_DRAG_PROCESSOR) {
								super.processGestureEvent(g);
							}
							//additional update for storytextarea
							scrollFinishedMoving();
							break;
						default:
							break;
						}
					}
				}
				return true;
			}
		}
		);
		
		// Initialize story text.
		storyText.addOperatorResult(0, " ", true);
		storyText.updatePreferredHeight(heightCache);
		//text.testStory();
	}
	
	public void setName(String name) {
		storyText.setName(name+"-StoryText");
		parchmentRoll.setName(name+"-ParchmentRoll");
	}
	
	public void rotate(int angle) {
		background.rotateZ(background.getCenterPointLocal(), angle);
	}
	
	public float getHeightLocal() {
		return background.getHeightXY(TransformSpace.LOCAL);
	}
		
	public float getWidthGlobal() {
		return background.getWidthXY(TransformSpace.GLOBAL);
	}
	
	
	public void registerSounds() {
		//SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#BecomeHungry", new File(SoundManager.soundPath + "hungry.wav"));
		SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#Eat", new File(SoundManager.soundPath + "eat.wav"));
		SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#Die", new File(SoundManager.soundPath + "die.wav"));
		SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#Cry", new File(SoundManager.soundPath + "cry.wav"));
		SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#Laugh", new File(SoundManager.soundPath + "laugh.wav"));
		SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#Fart", new File(SoundManager.soundPath + "fart.wav"));
		SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#Fart2", new File(SoundManager.soundPath + "fart.wav"));
		SoundManager.getInstance().registerActionSound("http://www.owl-ontologies.com/Red.owl#Fart3", new File(SoundManager.soundPath + "fart.wav"));
	}
	
	public void scrollMoved(Vector3D translation) {
		
		// Translate the translation to local space
		Vector3D translLocalBackground = translation.getCopy();
		translLocalBackground.transformDirectionVector(background.getGlobalInverseMatrix());
		//Calculate the new height
		float newHeight = background.getHeightXY(TransformSpace.LOCAL) - translLocalBackground.y;
		
		//check if the new height is within the restrictions
		if (newHeight < MIN_HEIGHT_LOCAL) {
			//do nothing
		} else if (newHeight > MAX_HEIGHT_LOCAL) {
			// do nothing
		} else {
			heightCache = newHeight;
			background.setHeightLocal(heightCache);
			Vector3D move = new Vector3D(0,translLocalBackground.y, 0, 1);
			background.translate(move, TransformSpace.LOCAL);
		
			// Problem: updating the TextArea requires removing all lines and adding
			// the lines needed. Internally, MT4j does this letter-by-letter, each letter being
			// a shape. So updating the TextArea is very costly and causes the rolling/unrolling
			// of the scroll to be laggy.
			// Solution: DURING scroll moving, only update it every so often (e.g. once every 10 times). 
			//           AFTER scroll moving always do it (this happens in scrollFinishedMoving()).
			// The decrease in update frequency is only really visible when moving fast (cause the 
			// changes in size are bigger), but in that case the speed helps in hiding the lack of update. 
			// When scroll is moved slowly, there are more tiny updates in size so the effect is much less visible.
			
			updateCount++;
			if (updateCount == 15) {		
				storyText.updatePreferredHeight(heightCache);// - scroll.getHeightXY(TransformSpace.LOCAL));
				updateCount = 0;
			}
			
			//Correctly reset the bottom left corner of the text
			textChanged();
		}
	}
	
	/** 
	 * Handles the moment where the scroll has finished moving (user stopped dragging).
	 */
	public void scrollFinishedMoving() {
		storyText.updatePreferredHeight(heightCache); // - scroll.getHeightXY(TransformSpace.LOCAL));
		textChanged();
	}
	
	/**
	 * Call when the story text has changed (in size).
	 */
	public void textChanged() {
		background.setAnchor(PositionAnchor.LOWER_LEFT);
		storyText.setAnchor(PositionAnchor.LOWER_LEFT);
		storyText.setPositionRelativeToParent(new Vector3D(5, background.getHeightXY(TransformSpace.LOCAL) - 5 , 0 ));
	}
	
	/**
	 * Handles the fact that the story model has changed.
	 * 
	 * NOTE: because the Plot Agent does not run on the same thread as the MT application, 
	 *       each call to the application should be encapsulated in a Runnable. 
	 *       This way the MT application is updated at the correct time (prevents crashes)
	 */
	public void update(Observable obs, Object o) {
		
		NarratedOperatorResult nor = (NarratedOperatorResult) o;	
	
		// Sound should be handled in SoundManager!
		// There can be many StoryWidgets, but every sentence should only be pronounced once
//		if (nor.getOperatorResult().getStatus() instanceof Finished) {
//			logger.fine("Playing action sound for action type " + nor.getOperatorResult().getOperator().getType());
//			SoundManager.getInstance().playActionSound(nor.getOperatorResult().getOperator().getType());
//			SoundManager.getInstance().speakLine(nor.getNarration());
//		}

		// Text
		final String nwLine = nor.getNarration();
		if (! nwLine.equals("")) {
			mtApp.invokeLater(new Runnable() {
				public void run() {
					storyText.addOperatorResult(StoryTime.getTime(), nwLine, true);
					storyText.updatePreferredHeight(heightCache);
				}
			});
		}
	}
		


}
