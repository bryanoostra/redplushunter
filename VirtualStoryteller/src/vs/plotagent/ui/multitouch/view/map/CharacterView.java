package vs.plotagent.ui.multitouch.view.map;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.mt4j.components.TransformSpace;
import org.mt4j.components.PickResult.PickEntry;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.input.gestureAction.DefaultDragAction;
import org.mt4j.input.gestureAction.TapAndHoldVisualizer;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PImage;
import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.model.InterfaceModel;
import vs.plotagent.ui.multitouch.model.PossibleAction;
import vs.plotagent.ui.multitouch.model.PossibleMoveAction;
import vs.plotagent.ui.multitouch.model.InterfaceModel.DestinationProperty;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;
import vs.plotagent.ui.multitouch.view.actioninterface.InterfaceWidget;
import vs.plotagent.ui.multitouch.view.actioninterface.LocationCharacterRope;
import vs.plotagent.ui.multitouch.view.map.ControlChooser.ControlChoice;

/**
 * Represents a view of a character (i.e. an image of the character at a certain location).
 */
public class CharacterView {
	
	private MTRectangle _imageRectangle;
	private MTRectangle _imageRectangleFront; //the "front" imageRectangle is only used to get the character image in front of the character-location line. _imageRectangleFront is added as a child just like the character-location line  
	private PImage _originalImage;
	private String _URI;
	private String _location;
	
	private CharacterStatus _status;
	private boolean _isTurn;
	
	private ControlChooser _controlChooser;
	
	private MTEllipse _thinker;
	private LocationCharacterRope _rope;
	
	private VSTMultitouchApplication _mtApp;
	
	private MapLocation _dragLocation;
	
	private static float strokeWeightPickable = 8.0f;
	
	private boolean isUser = false;
	private Vector3D actualCharacterPositionGlobal;
	
	public static enum CharacterStatus {
		nonPickable,
		pickable,
		allowedDestination,
		disallowedDestination
	}
	
	public CharacterView(String URI, PImage image, String locationURI, VSTMultitouchApplication app) {
		
		_URI = URI;
		_originalImage = image;
		_location = locationURI;
		_mtApp = app;
		
		_status = CharacterStatus.nonPickable;
		
		_imageRectangle = new MTRectangle(_originalImage, app);
		_imageRectangle.setStrokeWeight(0f);
		_imageRectangle.setStrokeColor(InterfaceWidget.YELLOW);
		_imageRectangle.setNoStroke(true);
		
		//the "front" imageRectangle is only used to get the character image in front of the character-location line. 
		//A parent can never be visible in front of it's children
		//_imageRectangleFront is added as a child just like the character-location line
		//When the image is also added as a child, it can overlap the character-location line. Ugly fix, but solved. 
		_imageRectangleFront = new MTRectangle(_originalImage, app);
		_imageRectangleFront.removeAllGestureEventListeners();
		_imageRectangleFront.setStrokeWeight(0f);
		_imageRectangleFront.setStrokeColor(InterfaceWidget.TRANSPARENT);
		_imageRectangleFront.setNoStroke(true);
		_imageRectangleFront.setEnabled(false);
		_imageRectangleFront.setPickable(false);
		
			
		_thinker = new MTEllipse(app, new Vector3D(0, 0, 0), .1f*image.height, .1f*image.width);
		MTEllipse bulb2 = new MTEllipse(app, new Vector3D(0, 0, 0), .3f*image.height, .3f*image.width);
		MTEllipse bulb3 = new MTEllipse(app, new Vector3D(0, 0, 0), image.height, image.width);

		boolean thinking_clouds_go_down = true;
		if(thinking_clouds_go_down) {
			_thinker.translate(new Vector3D(0.3f*image.width, 1.2f*image.height, 0));
			bulb2.translate(new Vector3D(0.2f*image.width, 0.4f*image.height, 0));
			bulb3.translate(new Vector3D(1.4f*image.width, 1.2f*image.height, 0));
		} else {
			_thinker.translate(new Vector3D(0.3f*image.width, -0.2f*image.height, 0));
			bulb2.translate(new Vector3D(0.2f*image.width, -0.4f*image.height, 0));
			bulb3.translate(new Vector3D(1.4f*image.width, -1.2f*image.height, 0));
		}
		_thinker.setStrokeColor(InterfaceWidget.BLACK);
		_thinker.setPickable(false);
		_thinker.setVisible(false);
		bulb2.setStrokeColor(InterfaceWidget.BLACK);
		bulb3.setStrokeColor(InterfaceWidget.BLACK);
		bulb2.setPickable(false);
		bulb3.setPickable(false);
		_thinker.addChild(bulb2);
		_thinker.addChild(bulb3);
		
		//create the location-character rope
		_rope = new LocationCharacterRope(new Vertex(), new Vertex(), _mtApp);
		
		//create the control chooser
		if (MultitouchInterfaceSettings.CHARACTERS_INITIALLY_HUMAN_CONTROLLED && !MultitouchInterfaceSettings.exceptionsOnInitiallyHumanControlled.contains(_URI)) {
			_mtApp.getVSTMultitouchController().humanControl(_URI);
			_controlChooser = new ControlChooser(ControlChooser.ControlChoice.human, this, app);
		} else {
			_mtApp.getVSTMultitouchController().computerControl(_URI);
			_controlChooser = new ControlChooser(ControlChooser.ControlChoice.computer, this, app);
		}
		_controlChooser.setPickable(false);
		_controlChooser.setVisible(false);
		
		_imageRectangle.addChild(_thinker);
		_imageRectangle.addChild(_rope);
		_imageRectangle.addChild(_controlChooser);
		_imageRectangle.addChild(_imageRectangleFront);
		
		_imageRectangle.removeAllGestureEventListeners();
		
		//use "tap and hold" human/computer control chooser or not? 
		if(MultitouchInterfaceSettings.USE_HUMANORCOMPUTER_CONTROL_CHOOSER) {
			//Add tap&hold gesture to clear all tails
			TapAndHoldProcessor tapAndHold = new TapAndHoldProcessor(app);
			tapAndHold.setMaxFingerUpDist(10);
			tapAndHold.setHoldTime(500);
			_imageRectangle.registerInputProcessor(tapAndHold);
			
			_imageRectangle.addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
				public boolean processGestureEvent(MTGestureEvent ge) {
					TapAndHoldEvent t = (TapAndHoldEvent)ge;
					if (t.getId() == TapAndHoldEvent.GESTURE_ENDED && t.isHoldComplete()){
						// Time of tap-and-hold has elapsed: show button.
	/*					if (isUser) {
							// Show "give up control" button
							_controlChooser.setControlChoice(ControlChooser.ControlChoice.human);
						} else {
							// Show "take control" button
							_controlChooser.setControlChoice(ControlChooser.ControlChoice.computer);
						}*/
						
						
						Vector3D currPos = _imageRectangle.getPosition(TransformSpace.GLOBAL);
						float move = .5f*(_imageRectangle.getHeightXY(TransformSpace.LOCAL)+_controlChooser.getHeightXY(TransformSpace.LOCAL));
						_controlChooser.setPosition(currPos);
						_controlChooser.translate(new Vector3D(0, -move, 0));
						_controlChooser.setVisible(true);
					}
					return false;
				}
			});
			_imageRectangle.addGestureListener(TapAndHoldProcessor.class, new TapAndHoldVisualizer(app, app.getCurrentScene().getCanvas()));
		}
		
		//character images can only be dragged if tangible objects are NOT used
		if(!MultitouchInterfaceSettings.USE_FIDUCIALS && !MultitouchInterfaceSettings.USE_CCV_RECTANGLE_RECOGNITION) {			
			_imageRectangle.addGestureListener(DragProcessor.class, new DefaultDragAction()  
			{
				public boolean processGestureEvent(MTGestureEvent g) {
					if (g instanceof DragEvent){
						DragEvent dragEvent = (DragEvent)g;
	//					MTRectangle self = (MTRectangle) dragEvent.getTargetComponent();
						
						long id = dragEvent.getDragCursor().getId();
						//System.out.println("cursor of dragEvent has id: " + id);

						switch (dragEvent.getId()) {
						
						case MTGestureEvent.GESTURE_DETECTED:
							//Put target on top -> draw on top of others
							//No, don't do this, cause it interferes with the fact that the current character should always be on top
							//The character to which the actions in the action selection interface belong, should always be visible on top!
							//_image.sendToFront();
							LogFactory.getLogger(this).fine("Character drag gesture detected");
							break;
							
						case MTGestureEvent.GESTURE_UPDATED:
	
							_imageRectangle.translate(dragEvent.getTranslationVect());
													
							updateLocationRope();
							
							if (isUser) {
								Vector3D position = new Vector3D(dragEvent.getDragCursor().getCurrentEvtPosX(), dragEvent.getDragCursor().getCurrentEvtPosY());
								updateDragLocationAndEvaluate(position, false);
							}
							break;
							
						case MTGestureEvent.GESTURE_ENDED:
							if (isUser) {
								LogFactory.getLogger(this).fine("Character drag gesture ended.");
								Vector3D position = new Vector3D(dragEvent.getDragCursor().getCurrentEvtPosX(), dragEvent.getDragCursor().getCurrentEvtPosY());
								updateDragLocationAndEvaluate(position, true);
							}
							break;
							
						default:
							break;
						}		
					}
					return true;
				}
			});
		} 
		else {
			//...so we ARE using tangibles. 
			//If this character is human controlled, it's image should be grayed:
			if(this.isHumanControlled()) {
				this.setGrayed(true);
			}
		}
	}
	
	//if we ARE using tangibles, the image of human controlled characters should be grayed
	public void setGrayed(boolean grayed) {
		if(grayed) {
			PImage pi = _imageRectangle.getTexture();
			try {
				pi = (PImage)pi.clone();
			} catch(Exception e) {
				//whatever
			}
			pi.filter(PImage.GRAY);
			
			_imageRectangle.setTexture(pi);
			_imageRectangleFront.setTexture(pi);
		} else {
			_imageRectangle.setTexture(this._originalImage);
			_imageRectangleFront.setTexture(this._originalImage);
		}
	}
	
	private class TimerTaskDelayedEvaluation extends TimerTask {
		boolean _draggingCharacterEnded;
		List<PickEntry> _underneathComponents;
		public TimerTaskDelayedEvaluation(List<PickEntry> underneathComponents, boolean draggingCharacterEnded) {
			super();
			_draggingCharacterEnded = draggingCharacterEnded;
			_underneathComponents = underneathComponents;
		}
		public void run() {
			//System.out.println("CharacterView TimerTaskDelayedEvaluation");
			//check if we are still the current user
			if(isUser()) {
				evaluate(_underneathComponents, _draggingCharacterEnded);
				//this.cancel();
			}
		}
	}
		
	private long lastEvaluation = 0;
	private Timer timer = new Timer();
	private TimerTask timerTask;
	/* 
	 * Check if this character is currently dragged over/at an allowed or disallowed location/destination.
	 * @ensure Only called when this human-controlled character currently has to select an action.
	 * //XXX: also have to use "invokeLater" somewhere?
	 */
	public void updateDragLocationAndEvaluate(Vector3D position, boolean draggingCharacterEnded) {
		//double check to make sure if this character currently has to select an action and is human-controlled
		if(!this.isUser()) {return;}
		
		// PROBLEM: Realtime evaluation of allowed or disallowed locations during dragging is very costly.
		// It is too costly to check for every generated drag-event if the underlying destination is allowed or disallowed
		// Solution: Only check once every X milliseconds if the character is at an allowed location.
		
		//When dragging the character just ended, skip the delay to ensure evaluation/recheck 
		if(!draggingCharacterEnded) {
			//lets see if it's time to evaluate/re-check
			boolean tooSoonToEvaluate = System.currentTimeMillis() < (lastEvaluation + MultitouchInterfaceSettings.MILLIS_BEFORE_RECHECK_DRAG_DESTINATION);
			if(tooSoonToEvaluate) {
				//set a new timer (for the case this was the last generated event for some time)
				if(timerTask!=null)timerTask.cancel();
				List<PickEntry> underneathComponents = _mtApp.getCurrentScene().getCanvas().pick(position.x, position.y).getPickList();
				timerTask = new TimerTaskDelayedEvaluation(underneathComponents, draggingCharacterEnded);
				timer.schedule(timerTask, 5*MultitouchInterfaceSettings.MILLIS_BEFORE_RECHECK_DRAG_DESTINATION); 
				return;
			} else {
				//System.out.println("CharacterView: not to soon, enough time passed, time to evaluate, so continue...");
			}
		}
		
		//System.out.println("\nlastEvaluationOfAllowedDestination: " +lastEvaluationOfAllowedDestination);
		//System.out.println("now its timeToEvaluate: " + System.currentTimeMillis());
		lastEvaluation = System.currentTimeMillis();
		
		// Check which components are under the coords of the drag cursor
		List<PickEntry> underneathComponents = _mtApp.getCurrentScene().getCanvas().pick(position.x, position.y).getPickList();
		
		evaluate(underneathComponents, draggingCharacterEnded);
	}
	
	private void evaluate(List<PickEntry> underneathComponents, boolean draggingCharacterEnded) {
		lastEvaluation = System.currentTimeMillis();
		
		//double check to make sure if this character currently has to select an action and is human-controlled
		if(!this.isUser()) {
			//System.out.println("CharacterView: do not evaluate when character is not the current user! " + this.getURI());
			return;
		}
		
		boolean dragOverLocation = false;

		for (PickEntry p: underneathComponents) {
			if (p.hitObj instanceof MapLocation) {
				
				// Check status.
				dragOverLocation = true;
				
				MapLocation ml = (MapLocation) p.hitObj;																		
				changeDragLocation(ml);
				
				//select target action if allowed.
				if (_dragLocation != null) {
					InterfaceModel im = _mtApp.getVSTMultitouchController().getInterfaceModel();
					PossibleMoveAction pma = im.getPossibleActionInfo().getDestinationMoveAction(_dragLocation.getLocationURI());
					if(pma==null) {
						//at a blue or red location
						if(CharacterStatus.disallowedDestination.equals(_status)) {
							//at a red location
							im.hideNonMoveCategories(true);	
							im.setCategoryStripEnabled(false);
							//System.out.println("CharacterView on a red location ");
							LogFactory.getLogger(this).info("CharacterView on a red location");
							//nothing should be selected, so:
							im.clearSelectedAction();
						} else {
							//at blue location
							im.hideNonMoveCategories(false);	
							im.setCategoryStripEnabled(true);
							PossibleAction pa = im.getLastSelectedNonMoveAction();
							if(pa==null){
								//System.out.println("CharacterView on a blue location, select: " +pa);
								LogFactory.getLogger(this).info("CharacterView on a blue location, select: " +pa);
								im.setSelectedAction(pa);
							} else {
								//System.out.println("CharacterView on a blue location, select: " +pa.getDescription());
								LogFactory.getLogger(this).info("CharacterView on a blue location, select: " +pa.getDescription());
								im.setSelectedActionByPrologString(pa.getPrologDescription());
							}
						}
					} else {
						//at a green location (corresponding move action is allowed!)
						im.hideNonMoveCategories(true);	
						im.setCategoryStripEnabled(true);
						//System.out.println("CharacterView on a green location, select: " +pma.getDescription());
						LogFactory.getLogger(this).info("CharacterView on a green location, select: " +pma.getDescription());
						im.setSelectedAction(pma);
						//if this is the end of the drag-character-gesture, execute the corresponding moveAction (when instant execute is enabled)
						//instant execute the move action when a tangible is placed on an allowed location
						if((draggingCharacterEnded || MultitouchInterfaceSettings.USE_FIDUCIALS) 
								&& MultitouchInterfaceSettings.USE_INSTANT_EXECUTE_MOVE_ACTIONS) {
							_mtApp.getVSTMultitouchController().storyActionChosen(pma.getCharacterURI(), pma);
						}
					}
				}
			} 
		}
		if (! dragOverLocation) {
			// Dragging over no location (=orange)
			changeDragLocation(null);
			InterfaceModel im = _mtApp.getVSTMultitouchController().getInterfaceModel();
			im.hideNonMoveCategories(false);	
			im.setCategoryStripEnabled(true);
			PossibleAction pa = im.getLastSelectedNonMoveAction();
			if(pa==null){
				//System.out.println("CharacterView on an orange loacation, select: " +pa);
				LogFactory.getLogger(this).info("CharacterView on an orange loacation, select: " +pa);
				im.setSelectedAction(pa);
			} else {
				//System.out.println("CharacterView on an orange loacation, select: " +pa.getDescription());
				LogFactory.getLogger(this).info("CharacterView on an orange loacation, select: " +pa.getPrologDescription());
				im.setSelectedActionByPrologString(pa.getPrologDescription());
			}
		}
		//System.out.println("now we are done evaluating: " + System.currentTimeMillis());
		lastEvaluation = System.currentTimeMillis();
	}
	
	public void changeDragLocation(MapLocation newLoc) {
		
		// Cases:
		// - moving within current (do nothing)
		// - moving away from current to nothing (reset)
		// - moving from nothing to new (set new)
		// - moving from current to new (set new)		
		
		if (newLoc == null) {
			if (_dragLocation != null) {
				//clear the color of the discovered status
				_dragLocation.setStatus(true, MapLocation.LocationStatus.normal);
			}
			_dragLocation = null;
			
			setStatus(CharacterStatus.pickable);

			return;
		}
				
		if (_dragLocation != newLoc) {
			//set old location to normal
			if(_dragLocation!=null) {
				//clear the color of the discovered status
				_dragLocation.setStatus(true, MapLocation.LocationStatus.normal);
			}
			
			//update to new location
			_dragLocation = newLoc;
			
			DestinationProperty dp = _mtApp.getVSTMultitouchController().getInterfaceModel().tryPossibleDestination(_dragLocation.getLocationURI());			
			
			//DestinationProperty dp = DestinationProperty.allowed;
			
			switch (dp) {
			case alreadyAt:
				setStatus(CharacterStatus.pickable);
				_dragLocation.setStatus(false, MapLocation.LocationStatus.normal);
				break;
			case allowed:
				setStatus(CharacterStatus.allowedDestination);
				_dragLocation.setStatus(false, MapLocation.LocationStatus.allowed);
				break;
			case notAllowed:
				setStatus(CharacterStatus.disallowedDestination);
				_dragLocation.setStatus(false, MapLocation.LocationStatus.disallowed);
				break;		
			case unknown:
				setStatus(CharacterStatus.nonPickable);
				_dragLocation.setStatus(false, MapLocation.LocationStatus.normal);
				break;
			default:
				setStatus(CharacterStatus.pickable);
				_dragLocation.setStatus(false, MapLocation.LocationStatus.normal);
				break;
			}	
			
			return;
		} 
		
		return;
	}
	
	public MTRectangle getImage() {
		return _imageRectangle;
	}

	//never use this!
//	public void setImage(MTRectangle image) {
//		this._imageRectangle = image;
//	}
	
	public String getURI() {
		return _URI;
	}
	
	public void setURI(String uri) {
		_URI = uri;
	}
	
	//this checks if the ActionSelectionInterface currently belongs to this character? 
	public boolean isUser() {
		return isUser;
	}
	
	public boolean isHumanControlled() {
		if(_controlChooser.getControlChoice()==ControlChoice.human) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setUser(boolean isUser) {
		this.isUser = isUser;

		// Trigger update of visuals
		setStatus(_status);
	}
	
	public String getLocation() {
		return _location;
	}
	
	public MapLocation getDragLocation() {
		return _dragLocation;
	}
	
	public void setLocation(String location) {
		this._location = location;
	}
	
	public void setStatus(CharacterStatus cs) {
		_status = cs;	
		switch(_status) {
		case nonPickable:
			if (isUser) {
				//Never happens? hmm, now it does...
				//_imageRectangle.setStrokeColor(InterfaceWidget.BLACK);
				_imageRectangle.setStrokeColor(InterfaceWidget.TRANSPARENT);
				//_image.setPickable(false);
				_imageRectangle.setStrokeWeight(0);
			} else {
				_imageRectangle.setNoStroke(true);
				//_image.setPickable(false);				
			}
			break;
		case pickable:
			if (isUser) {
				//_image.setPickable(true);
				_imageRectangle.setNoStroke(false);
				_imageRectangle.setStrokeWeight(strokeWeightPickable);
				_imageRectangle.setStrokeColor(InterfaceWidget.ORANGE);
			}

			break;
		case allowedDestination:
			if (isUser) {
				//_image.setPickable(true);
				_imageRectangle.setNoStroke(false);
				_imageRectangle.setStrokeWeight(strokeWeightPickable);
				_imageRectangle.setStrokeColor(InterfaceWidget.GREEN);
			} else {
				_imageRectangle.setNoStroke(true);
				//_image.setPickable(false);	
			}

			break;
		case disallowedDestination:
			if (isUser) {
				_imageRectangle.setPickable(true);
				_imageRectangle.setNoStroke(false);
				_imageRectangle.setStrokeWeight(strokeWeightPickable);
				_imageRectangle.setStrokeColor(InterfaceWidget.RED);
			} else {
				_imageRectangle.setNoStroke(true);
				_imageRectangle.setPickable(false);	
			}
			
			break;			
		}
	}
	
	public void setTurn(boolean isTurn) {
		_isTurn = isTurn;
		if (_isTurn) {
			_thinker.setVisible(true);
		} else {
			_thinker.setVisible(false);
		}
	}
	
	public CharacterStatus getStatus() {
		return _status;
	}

	public void setActualPositionGlobal(Vector3D targetLoc) {
		actualCharacterPositionGlobal = targetLoc;
		updateLocationRope();
	}
	
	public Vector3D getActualPositionGlobal() {
		return actualCharacterPositionGlobal;
		
	}
	
	public void updateLocationRope() {
		Vector3D imagePositionGlobal = _imageRectangle.getCenterPointGlobal();
		Vector3D imagePositionLocal = _imageRectangle.getCenterPointLocal();
		
		float dx = imagePositionGlobal.x - actualCharacterPositionGlobal.x;
		float dy = imagePositionGlobal.y - actualCharacterPositionGlobal.y;
		
		_rope.updateBoth(new Vector3D(imagePositionLocal.x-dx, imagePositionLocal.y-dy, 0), imagePositionLocal);
		
		//No, don't do this, cause it interferes with the fact that the current character should always be on top
		//The character to which the actions in the action selection interface belong, should always be visible on top!
		//_image.sendToFront();
	}
	

}
