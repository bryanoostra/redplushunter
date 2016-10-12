package vs.plotagent.ui.multitouch.view.actioninterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.gestureAction.DefaultDragAction;
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

import vs.debug.LogFactory;
import vs.knowledge.PrologKB;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.model.DoNothingAction;
import vs.plotagent.ui.multitouch.model.InterfaceModel;
import vs.plotagent.ui.multitouch.model.PossibleAction;
import vs.plotagent.ui.multitouch.model.PossibleActionInfo;
import vs.plotagent.ui.multitouch.model.PossibleMoveAction;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;
import vs.plotagent.ui.multitouch.view.map.MapWidget;

/**
 * Represents the action selection interface.
 * 
 * @author swartjes
 *
 */
public class InterfaceWidget extends MTComponent implements Observer, IInterfaceWidgetEventListener {
	
	private VSTMultitouchApplication mtApp;
	private MapWidget mapWidget;
	
	public static MTColor RED = new MTColor(255,0,0);
	public static MTColor GREEN = new MTColor(0,255,0);
	public static MTColor BLUE = new MTColor(0,0,255);
	
	public static MTColor YELLOW = new MTColor(255,255,0);
	public static MTColor CYAN = new MTColor(0,255,255);
	public static MTColor MAGENTA = new MTColor(255,0,255);
	
	public static MTColor ORANGE = new MTColor(255,128,0);
	public static MTColor EASY_BLUE = new MTColor(0,128,255);
	public static MTColor PINK_1 = new MTColor(255,128,255);
		
	public static MTColor GREEN_2 = new MTColor(128,255,0);
	public static MTColor PURPLE = new MTColor(128,0,255);
	public static MTColor BRIGHT_BLUE = new MTColor(128,255,255);
	
	public static MTColor GREEN_3 = new MTColor(0,255,128);
	public static MTColor PINK_2 = new MTColor(255,0,128);
	public static MTColor CREME = new MTColor(255,255,128);
	
	public static MTColor BLACK = new MTColor(0,0,0);
	public static MTColor WHITE = new MTColor(255,255,255);
	public static MTColor GRAY = new MTColor(150,150,150); //was: new MTColor(128,128,128);
	
	public static MTColor TRANSPARENT = new MTColor(255,255,255, MTColor.ALPHA_FULL_TRANSPARENCY);
	public static MTColor HIGHTRANSPARANT = new MTColor(0,0,0, MTColor.ALPHA_HIGH_TRANSPARENCY);
	//ivo dark blue: private static MTColor buttonSelectedColor = new MTColor(0,0,184);
	//ivo dark blue: private static MTColor buttonSelectedColor = new MTColor(50,50,184);
	//ivo grey/blue: private static MTColor buttonColor = new MTColor(100,100,128);
	//ivo dark gray: private static MTColor buttonColor = new MTColor(64,64,64);
	//ivo light gray: private static MTColor roundRectColor = new MTColor(200,200,210);
	//private static MTColor goButtonColor = new MTColor(0,255,0);
	//private static MTColor goButtonDisableColor = new MTColor(128,128,128);
	//private static MTColor goButtonStrokeColor = new MTColor(0,0,0);
	
	//FontSize:
	//All sizes and dimensions in the ActionSelectionInterface are relative to the fontsize.
	//Increasing or decreasing the default interface size should be done by increasing or decreasing the fontsize.
	private static int FONTSIZE = 25;
	private static IFont THE_font = null;
	
	public static float MIN_SCALE = .5f;
	public static float MAX_SCALE = 2f;
	
	public static float ACTION_BUTTON_SEPARATION_f = .075f;
	public static float CATEGORY_BUTTON_SEPARATION_f = .100f;
	public static float ACTION_STRIP_PADDING_f = .125f;
	public static float CATEGORY_STRIP_PADDING_f = .150f;
	public static float EMPTY_SPACE_FOR_FINGERS_f = 1.5f;
	public static float ARC_f = .3f;
	
		
	private ActionButtonStrip actionStrip1;
	private ActionButtonStrip actionStrip2;
	
	private CategoryButtonStrip categoryStrip;
	
	private Map<String, List<ActionButtonStrip>> actionsCategoriesMap;
	
	private boolean widgetActive = false;
	
	private MTRoundRectangle rr;
	private MTRoundRectangle all;
	
	public static enum Event {
		categorySelected	// An action category was selected
		};
	
	private Logger logger;
	
	public InterfaceWidget(VSTMultitouchApplication app, MapWidget mapWidgt) {
		//super(0, 0, width, height, mtApp);	
		super(app);
		
		mtApp = app;
		mapWidget = mapWidgt;
				
		logger = LogFactory.getLogger(this);
		
		actionsCategoriesMap = new HashMap<String,List<ActionButtonStrip>>();
		
	}
	
	public static IFont getFont(VSTMultitouchApplication app) {
		if(THE_font==null) {
			THE_font = FontManager.getInstance().createFont(app, "arial.ttf", FONTSIZE, InterfaceWidget.BLACK, InterfaceWidget.HIGHTRANSPARANT);
		}
		return THE_font;
	}
	
	public static float getButtonRadius(VSTMultitouchApplication app) {
		MTTextArea label = new MTTextArea(app, InterfaceWidget.getFont(app));
		//label.setText("Dummy Label" );
		float labelHeight = label.getHeightXY(TransformSpace.LOCAL);
		//System.out.println("labelHeight = buttonradius = " + labelHeight);
		return labelHeight;
	}
		
	public Vector3D getCenterPoint() {
		return rr.getCenterPointGlobal();
	}
	
	private void registerActionsToCategory(ActionButtonStrip strip, String category) {
		//actionsCategoriesMap.put(actions, category);
		List<ActionButtonStrip> strips = actionsCategoriesMap.get(category);
		if (strips == null) {
			// First ActionButtonStrip for this category; construct new list.
			strips = new ArrayList<ActionButtonStrip>();
			actionsCategoriesMap.put(category, strips);
		}
		strips.add(strip);
	}
	
	private MTRoundRectangle makeStrip() {
		logger.info("Making new strip");
		
		float R = getButtonRadius(mtApp);
		
		//float extraSpaceForFinger = ExecuteActionButton.buttonRadius*2;
		float width = categoryStrip.getWidthXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R; // + extraSpaceForFingers*2;
		
		rr = new MTRoundRectangle(0, 0, 0, width, categoryStrip.getHeightXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R, ARC_f*R, ARC_f*R, mtApp); 
		rr.setFillColor(InterfaceWidget.EASY_BLUE);
		rr.setStrokeColor(InterfaceWidget.BLACK);
		rr.setNoStroke(false);
		rr.setPickable(false);
		
		categoryStrip.translate(new Vector3D(CATEGORY_STRIP_PADDING_f*R, CATEGORY_STRIP_PADDING_f*R, 0));

		MTComponent categoryLayer = new MTComponent(mtApp);
		categoryLayer.addChild(rr);
		categoryLayer.addChild(categoryStrip);
		
//		InterfaceModel im = mtApp.getVSTMultitouchController().getInterfaceModel();
//		String selectedCategory = categoryStrip.getSelectedCategory();
//		PossibleAction selectedAction = im.getSelectedAction();
//		if(selectedAction!=null) {
//			selectedCategory = selectedAction.getCategory();
//		}
			
		MTComponent actionsLayer = new MTComponent(mtApp);
		
		// Make action lists for all categories
		for (Iterator<CategoryButton> it = categoryStrip.buttonIterator(); it.hasNext(); ) {
			CategoryButton b = it.next();			
			
			List<ActionButtonStrip> actionsForCategory = actionsCategoriesMap.get(b.getCategory());
			
			// In principle, these are always two action strips. One for one side, and one for the other.
			if (actionsForCategory == null) {
				logger.warning("Should be two action button strips for each category [category " + b.getCategory() + " has null]");
			} else if (actionsForCategory.size() != 2) {
				logger.warning("Should be two action button strips for each category [category " + b.getCategory() + " has " + actionsForCategory.size() + "]");
			} else {	
				for (int stripCount = 0; stripCount < actionsForCategory.size(); stripCount++) {
					ActionButtonStrip s = actionsForCategory.get(stripCount);
					s.translate(new Vector3D(CATEGORY_STRIP_PADDING_f*R, 2*CATEGORY_STRIP_PADDING_f*R + categoryStrip.getHeightXY(TransformSpace.LOCAL), 0));
					// Lelijk! Maakt aanname over aantal en sortering.
					if (stripCount == 0) {
						s.rotateZ(rr.getCenterOfMass2DLocal(), 180);
						if(MultitouchInterfaceSettings.HIDE_HALF_OF_THE_INTERFACE_ON_STARTUP) {
							s.hide(true);
						}
					}
					
//					setVisibleCategory(selectedCategory);
					
					actionsLayer.addChild(s);
				}
			}			
		}
		
		// MTRoundRectangle, because it needs to process drag events. Same size as the visible rectangle so it becomes invisible.
		all = new MTRoundRectangle(0, 0, 0, categoryStrip.getWidthXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R, categoryStrip.getHeightXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R, ARC_f*R, ARC_f*R, mtApp);
		all.addChild(actionsLayer);
		all.addChild(categoryLayer);
		
		all.removeAllGestureEventListeners();

		//add drag processor
		all.addGestureListener(DragProcessor.class, new DefaultDragAction() {
			public boolean processGestureEvent(MTGestureEvent g) {
				if (g instanceof DragEvent){
					DragEvent dragEvent = (DragEvent)g;
					float x1 = dragEvent.getDragCursor().getCurrentEvtPosX();
					float y1 = dragEvent.getDragCursor().getCurrentEvtPosY();
									
					//check if cursor is within this InterfaceWidget 
					//if (rr.containsPointGlobal(new Vector3D(x1, y1, 0))) {
						return super.processGestureEvent(g);
					//} else {
					//	return false;
					//}
				}
				return false;
			}
		});
		
		//check if we want the interface to be rotatable
		if(MultitouchInterfaceSettings.ENABLE_INTERFACE_ROTATE) {
			all.addGestureListener(RotateProcessor.class, new DefaultRotateAction(){
				public boolean processGestureEvent(MTGestureEvent g) {
					if (g instanceof RotateEvent){
						RotateEvent rotateEvent = (RotateEvent)g;
						float x1 = rotateEvent.getFirstCursor().getCurrentEvtPosX();
						float y1 = rotateEvent.getFirstCursor().getCurrentEvtPosY();
						float x2 = rotateEvent.getSecondCursor().getCurrentEvtPosX();
						float y2 = rotateEvent.getSecondCursor().getCurrentEvtPosY();
						
						//check if both cursors are within this InterfaceWidget 
						//if (rr.containsPointGlobal(new Vector3D(x1, y1, 0)) 
						//		&& rr.containsPointGlobal(new Vector3D(x2, y2, 0)) ) {
							return super.processGestureEvent(g);
						//} else {
						//	return false;
						//}
					}
					return false;
				}
			});
		}

		//check if we want the interface to be scalable/resizable
		if(MultitouchInterfaceSettings.ENABLE_INTERFACE_SCALE) {
			all.addGestureListener(ScaleProcessor.class, new DefaultScaleAction() 
			{
				public boolean processGestureEvent(MTGestureEvent g) {
					if (g instanceof ScaleEvent){
						ScaleEvent scaleEvent = (ScaleEvent)g;
						float x1 = scaleEvent.getFirstCursor().getCurrentEvtPosX();
						float y1 = scaleEvent.getFirstCursor().getCurrentEvtPosY();
						float x2 = scaleEvent.getSecondCursor().getCurrentEvtPosX();
						float y2 = scaleEvent.getSecondCursor().getCurrentEvtPosY();
						
						//check if both cursors are within this InterfaceWidget 
						if (rr.containsPointGlobal(new Vector3D(x1, y1, 0)) 
								&& rr.containsPointGlobal(new Vector3D(x2, y2, 0)) ) {
							
							//Get (or calculate) the current scale 
							//In a newer version of MT4j this can be don in one command: Vector3D currentScale = background.getLocalMatrix().getScale();
							//We use for now:
							Matrix m = all.getLocalMatrix();
							double currentScaleX = Math.sqrt(m.m00 * m.m00 + m.m10 * m.m10 + m.m20 * m.m20);
							
							//check if current scale is already at max
							if (currentScaleX * scaleEvent.getScaleFactorX() > MAX_SCALE) {
								//do not change anything
							//check if current scale is already at min
                            } else if (currentScaleX * scaleEvent.getScaleFactorX() < MIN_SCALE) {
								//do not change anything
                            //if not MIN or MAX scale, process GestureEvent:
                            } else {
                            	return super.processGestureEvent(g);
                            }
						} else {
							return false;
						}
					}
					return false;
				}
			});
		}
		
		//check if we want the fancy InertiaDrag movement
		if(MultitouchInterfaceSettings.USE_INERTIA_DRAG_PROCESSOR) {
			all.addGestureListener(DragProcessor.class, new InertiaDragAction());
		}
		
		logger.info("New strip made.");
		
		return all;
	}
	
	private MTComponent testMakeStrip(float x, float y, ArrayList<CategoryButton> categoryButtons, ArrayList<ActionButton> actionButtons, ArrayList<ActionButton> actionButtons2) {
		
		float R = getButtonRadius(mtApp);

		categoryStrip = new CategoryButtonStrip(categoryButtons, mtApp, new MapWidget(mtApp));
		
		actionStrip1 = new ActionButtonStrip(false, this, mtApp);
		actionStrip2 = new ActionButtonStrip(true, this, mtApp);
		actionStrip1.setOpposite(actionStrip2);
		actionStrip2.setOpposite(actionStrip1);
		
		actionStrip1.setButtons(actionButtons);
		actionStrip2.setButtons(actionButtons2); // TODO: lelijk: beter om te clonen denk ik
		
		rr = new MTRoundRectangle(0, 0, 0, categoryStrip.getWidthXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R, categoryStrip.getHeightXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R, ARC_f*R, ARC_f*R, mtApp); 
		rr.setFillColor(InterfaceWidget.EASY_BLUE);
		rr.setStrokeColor(InterfaceWidget.BLACK);
		rr.setNoStroke(false);
		rr.setPickable(false);
		
		categoryStrip.translate(new Vector3D(CATEGORY_STRIP_PADDING_f*R, CATEGORY_STRIP_PADDING_f*R, 0));
		actionStrip1.translate(new Vector3D(CATEGORY_STRIP_PADDING_f*R, 2*CATEGORY_STRIP_PADDING_f*R + categoryStrip.getHeightXY(TransformSpace.LOCAL), 0));
				
		actionStrip2.translate(new Vector3D(CATEGORY_STRIP_PADDING_f*R, 2*CATEGORY_STRIP_PADDING_f*R + categoryStrip.getHeightXY(TransformSpace.LOCAL), 0));
		actionStrip2.rotateZ(rr.getCenterOfMass2DLocal(), 180);
		
		if(MultitouchInterfaceSettings.HIDE_HALF_OF_THE_INTERFACE_ON_STARTUP) {
			actionStrip2.hide(true);
		}
						
		MTComponent categoryLayer = new MTComponent(mtApp);
		categoryLayer.addChild(rr);
		categoryLayer.addChild(categoryStrip);
		
		MTComponent actionsLayer = new MTComponent(mtApp);
		actionsLayer.addChild(actionStrip1);
		actionsLayer.addChild(actionStrip2);
		
		// MTRoundRectangle, because it needs to process drag events. Same size as the visible rectangle so it becomes invisible.
		MTRoundRectangle all = new MTRoundRectangle(0, 0, 0, categoryStrip.getWidthXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R, categoryStrip.getHeightXY(TransformSpace.LOCAL) + 2*CATEGORY_STRIP_PADDING_f*R, ARC_f*R, ARC_f*R, mtApp);
		all.addChild(actionsLayer);
		all.addChild(categoryLayer);
		
		all.translate(new Vector3D(x, y, 0));
		
		if(MultitouchInterfaceSettings.USE_INERTIA_DRAG_PROCESSOR) {
			all.addGestureListener(DragProcessor.class, new InertiaDragAction());
		} else {
			all.addGestureListener(DragProcessor.class, new DefaultDragAction());
		}
				
		return all;
	}
	
	public void hideChange(ActionButtonStrip s) {
		logger.fine("Hide change");
		
		// Make sure that not both sides are hidden.
		if (s.isHidden() && s.getOpposite().isHidden()) {
			
			//logger.fine("Can't hide both sides: un-hiding opposite.");
			//s.getOpposite().hide(false, false);
			
			logger.fine("Can't hide both sides: un-hiding this side.");
			s.hide(false, false);
		}
			
		// Set visibility state of category strip.
		if (! s.isHidden() && s.getOpposite().isHidden()) {
			if (s.isCopy()) {
				// s is the real one
				setVisibilityState(CategoryButton.VisibilityState.one);
			} else {
				setVisibilityState(CategoryButton.VisibilityState.two);
			}
		} else if (s.isHidden() && ! s.getOpposite().isHidden()) {
			if (s.isCopy()) {
				setVisibilityState(CategoryButton.VisibilityState.two);
			} else {
				setVisibilityState(CategoryButton.VisibilityState.one);
			}
		} else if (! s.isHidden() && ! s.getOpposite().isHidden()) {
			setVisibilityState(CategoryButton.VisibilityState.both);
		}
	}
	
	private void setVisibilityState(CategoryButton.VisibilityState state) {
		
		logger.fine("Setting visibility state to: " + state);
		
		categoryStrip.setVisibilityState(state);
		
		for (List<ActionButtonStrip> absL: actionsCategoriesMap.values()) {
			for (ActionButtonStrip abs: absL) {
				switch (state) {
				case one:
					if (abs.isCopy()) {
						abs.hide(false, false);
					} else {
						abs.hide(true, false);
						if (abs.getOpposite().isHidden()) {
							abs.getOpposite().hide(false, false);
						}
					}
					break;
				case two:
					if (abs.isCopy()) {
						abs.hide(true, false);
						if (abs.getOpposite().isHidden()) {
							abs.getOpposite().hide(false, false);
						}
					} else {
						abs.hide(false, false);
					}
					break;
				case both:
					abs.hide(false, false);
					break;
				}
			}
			
		}
		
		//check/set/correct visibility of hide button
		for (List<ActionButtonStrip> absL: actionsCategoriesMap.values()) {
			for (ActionButtonStrip abs: absL) {
				abs.setVisibilityOfHideButton();
			}
		}
	}
	
	public void setVisibleCategory(String category) {
		//System.out.println("InterfaceWidget setVisibleCategory: "  +category);
		
		logger.fine("Setting visible category: " + category);
		
		// Do this only when the category EXISTS. This allows us to call setVisibleCategory without 
		// knowing whether the category (still) exists (for state maintenance purposes).		
		if (actionsCategoriesMap.get(category) == null) {
			return;
		}
		
		// Hide actions of ALL categories first
		for (List<ActionButtonStrip> strips: actionsCategoriesMap.values()) {
			for (ActionButtonStrip strip: strips) {
				strip.setVisible(false);
			}
		}
		
		// Now, make actions of currently selected category visible 
		for (ActionButtonStrip actionBstrip: actionsCategoriesMap.get(category)) {
			actionBstrip.setVisible(true);		
		}
	}

	private String A_RANDOM_TRANSIT_MOVE_CATEGORY = "Bewegen";

	public void refreshButtons() {				
		
		logger.info("Refreshing buttons");
		
		// Store current state
		PossibleAction selectedAction = DoNothingAction.createDoNothingAction("placeholder");
		String currCategory = null;
		CategoryButton.VisibilityState currVisibility = null;
		float currX = 300;
		float currY = 400;
		Matrix matrix = null;
		
		// Save state and destroy if already exists. Saves memory.
		if (all !=null && categoryStrip != null) {
			selectedAction = mtApp.getVSTMultitouchController().getInterfaceModel().getSelectedAction();
			//System.out.println("selectedAction: " +selectedAction.getDescription());
			currCategory = categoryStrip.getSelectedCategory(); 
			currVisibility = categoryStrip.getVisibilityState();

			Vector3D currPos = all.getCenterPointGlobal();
			//currPos = categoryStrip.getPosition(TransformSpace.GLOBAL);
			currX = currPos.x - .5f*all.getWidthXY(TransformSpace.GLOBAL);
			currY = currPos.y - .5f*all.getHeightXY(TransformSpace.GLOBAL);
			
			all.translate(new Vector3D(.5f*all.getWidthXY(TransformSpace.LOCAL), 0, 0), TransformSpace.LOCAL);
			matrix = all.getGlobalMatrix();
			
			logger.fine("Destroying old strip.");
			categoryStrip.destroy();
		}
		
		//check if we are waiting for a thinking agent-controlled character
		String human = mtApp.getVSTMultitouchController().getInterfaceModel().getHumanCharacterName();
		boolean user = mtApp.getVSTMultitouchController().getInterfaceModel().isUserTurn();
		boolean waitingForThinkingAgent = !user || human==null;
						
		// Clear actions.
		actionsCategoriesMap.clear();
		
		ArrayList<CategoryButton> categoryButtons = new ArrayList<CategoryButton>();
		if(!waitingForThinkingAgent) {
			PossibleActionInfo poi = mtApp.getVSTMultitouchController().getInterfaceModel().getPossibleActionInfo();
			// Iterate over all categories
			for (Iterator<String> it = poi.getActionCategories(); it.hasNext(); ) {
				String cat = it.next();
				logger.info("Constructing action category " + cat );
				
				boolean instanceof_PossibleMoveAction = false;
				boolean instanceof_EventORFramingOperator = false;
				
				// Make action buttons for category
				ArrayList<ActionButton> buttons1 = new ArrayList<ActionButton>();
				ArrayList<ActionButton> buttons2 = new ArrayList<ActionButton>();
				
				// Iterate over all possible actions for this category
				for (Iterator<PossibleAction> it2 = poi.getPossibleActions(cat); it2.hasNext(); ) {
	
					PossibleAction pa = it2.next();
					
					//fucking ugly hardcoded filter for now, sorry...
					//TODO: fix this properly!
					if(cat.toLowerCase().contains("event") || 
						cat.toLowerCase().contains("framing") ||
						cat.toLowerCase().contains("gebeurtenis")){
							//the category of this action is an event or framing operator
							instanceof_EventORFramingOperator = true;
					}
					
					logger.info("Possible action for category " + cat + ": " + pa.getDescription());
					ActionButton abutt = new ActionButton(pa, mtApp);
					ActionButton abutt2 = new ActionButton(pa, mtApp);
					
					if(pa instanceof PossibleMoveAction) {
						instanceof_PossibleMoveAction = true;
						A_RANDOM_TRANSIT_MOVE_CATEGORY = pa.getCategory();
						//buttons should never be clickable if they represent a PossibleMoveAction (aka TransitMoveAction)
						abutt.setEnabled(false);
						abutt2.setEnabled(false);
					}
					
					buttons1.add(abutt);
					buttons2.add(abutt2);
				}
				
				logger.info("Done finding possible actions for category " + cat);
				
				ActionButtonStrip buttonStrip1 = new ActionButtonStrip(false, this,mtApp);
				ActionButtonStrip buttonStrip2 = new ActionButtonStrip(true, this, mtApp);
				buttonStrip1.setOpposite(buttonStrip2);
				buttonStrip2.setOpposite(buttonStrip1);
				buttonStrip1.setButtons(buttons1);
				buttonStrip2.setButtons(buttons2);
							
				registerActionsToCategory(buttonStrip1, cat);
				registerActionsToCategory(buttonStrip2, cat);
				
				CategoryButton categoryButt = new CategoryButton(cat, mtApp);
				
				//if non-move actions should be hidden, don't show category buttons for other categories 
				if(mtApp.getVSTMultitouchController().getInterfaceModel().getNonMoveCategoriesShouldBeHidden()) {
					if(!instanceof_PossibleMoveAction) {
						categoryButt.setVisible(false);
						categoryButt.setEnabled(false);
					}
				} else {
					//if move actions should be hidden, don't show category buttons for any TransitMove category
					if(instanceof_PossibleMoveAction) {  //was: if(MultitouchInterfaceSettings.HIDE_MOVE_CATEGORY_FROM_MENU && instanceof_PossibleMoveAction)
						categoryButt.setVisible(false);
						categoryButt.setEnabled(false);
					}
				}
				//System.out.println("InterfaceWidget getNonMoveCategoriesShouldBeHidden() = "+mtApp.getVSTMultitouchController().getInterfaceModel().getNonMoveCategoriesShouldBeHidden());
				
				//if Events and FramingOperators sould be hidden, don't show category button for these types
				if(MultitouchInterfaceSettings.HIDE_EVENTS_AND_FRAMINGOPERATORS_FROM_MENU && instanceof_EventORFramingOperator){
					categoryButt.setVisible(false);
					categoryButt.setEnabled(false);
				}
				
				categoryButtons.add(categoryButt);
			}
		}
		
		// Clear the widget
		removeAllChildren();
		
		// Display a message if the user is waiting for a thinking AI agent-controlled character
		if(waitingForThinkingAgent) {
			InterfaceModel im = mtApp.getVSTMultitouchController().getInterfaceModel();
			String characterURI = im.getCurrentlyThinkingAICharacterName();
			String characterName = PrologKB.getInstance().narrate_object("\'" + characterURI + "\'").replaceAll("'", "");
			String explanation = "";
			if(MultitouchInterfaceSettings.LANGUAGE == MultitouchInterfaceSettings.Language.dutch) {
				explanation = "probeert een plan te bedenken...";
			} else if(MultitouchInterfaceSettings.LANGUAGE == MultitouchInterfaceSettings.Language.english) {
				explanation = "tries to make a plan...";
			}
			String message = characterName + " " + explanation; 
			categoryStrip = new CategoryButtonStrip(message, mtApp, mapWidget);
			im.setCategoryStripEnabled(false);
			all = makeStrip();
			all.setVisible(false);
			new java.util.Timer().schedule( 
				new java.util.TimerTask() {
					public void run() {
						if(all!=null) {
							all.setVisible(true);
						}
					}
				}, 2000 //delay before message will be displayed
			);
		} else {
			categoryStrip = new CategoryButtonStrip(categoryButtons, mtApp, mapWidget);
			categoryStrip.addEventListener(this);
			all = makeStrip();
		}
		
		if (currCategory != null) {
			categoryStrip.setSelectedCategory(currCategory);
			setVisibleCategory(currCategory);
		}
		
		if(selectedAction != null) {
			//in the current setup this gives problems with donothing action when possible actions are updated, so don't use it for now
			mtApp.getVSTMultitouchController().getInterfaceModel().setSelectedActionByPrologString(selectedAction.getPrologDescription());
		}
		
		if (currVisibility != null) {
			setVisibilityState(currVisibility);
		}		

		if(matrix !=null) {
			//restore scaling and rotation with matrix
			all.transform(matrix);
			all.translate(new Vector3D(-.5f*all.getWidthXY(TransformSpace.LOCAL), 0, 0), TransformSpace.LOCAL);
		} else {
			all.translate(new Vector3D(currX, currY, 0), TransformSpace.GLOBAL);
		}
		
		logger.fine("Adding new interface component to interfacewidget...");
		addChild(all);
		logger.fine("done.");
	}
	
	public void selectAction(PossibleAction pa) {
		
		//Don't do anything if there is no categoryStrip
		//if(categoryStrip==null) {return;}
		
		// Update category panel
		if (pa == null) {
			//so the chararcter is at a red location!
			//System.out.println("InterfaceWidget selectAction(null) red?->bewegen");
			logger.info("InterfaceWidget selectAction(null)");
			ExecuteActionButton executeActionButton = categoryStrip.getExecuteButton();
			executeActionButton.setEnabled(false);
			categoryStrip.setSelectedCategory(A_RANDOM_TRANSIT_MOVE_CATEGORY);
			setVisibleCategory(A_RANDOM_TRANSIT_MOVE_CATEGORY);
		} else {
			//select (and show) corresponding category
			String category = pa.getCategory();
			logger.info("InterfaceWidget, selectAction: " +pa.getDescription() + " of category: " + category);
			categoryStrip.setSelectedCategory(category);
			//XXX: workaround because TransitMove categories are sometimes not in categoryStrip (based on MultitouchInterfaceSettings.HIDE_MOVE_ACTIONS_FROM_MENU) 
			setVisibleCategory(category);
			categoryStrip.getExecuteButton().setEnabled(true);
		}
		
		// Update action panels
		for (List<ActionButtonStrip> buttons: actionsCategoriesMap.values()) {
			for (ActionButtonStrip buttonStrip: buttons) {
				buttonStrip.selectAction(pa);
			}
		}	
	}
		
	public boolean isWidgetActive() {
		return widgetActive;
	}
	
	public void onEvent(Event e, Object src) {
		if (e.equals(Event.categorySelected)) {
			// Category was selected
						
			String category = categoryStrip.getSelectedCategory();
			InterfaceModel im = mtApp.getVSTMultitouchController().getInterfaceModel();
			PossibleAction selectedAction = im.getSelectedAction();
			
			//System.out.println("InterfaceWidget: categorySelected Event: " +category);
			//if(selectedAction!=null) System.out.println("InterfaceWidget: selected action: " + selectedAction.getDescription());
			
			if(selectedAction == null || !selectedAction.getCategory().equals(category)) {
				PossibleAction firstAction = im.getFirstPossibleActionOfType(category);
				im.setSelectedAction(firstAction);
			}
			setVisibleCategory(category);
		}
	}
	
	/**
	 * Handles updates in the model.
	 * 
	 * NOTE: because the Plot Agent does not run on the same thread as the MT application, 
	 *       each call to the application should be encapsulated in a Runnable. 
	 *       This way the MT application is updated at the correct time (prevents crashes)
	 */
	public void update(Observable obs, Object o) {
		
		if (o instanceof InterfaceModel.InterfaceModelChange) {
			InterfaceModel.InterfaceModelChange change = (InterfaceModel.InterfaceModelChange) o;
			
			logger.info("Interface model changed: " + change);
			
			switch (change) {
			case locationsChanged:
				// Interface panel does not need to change
				break;
			case possibleActionsUpdated:
				if (mtApp.getVSTMultitouchController().getInterfaceModel().isUserTurn()) {
					mtApp.invokeLater(new Runnable() {
						public void run() {
							refreshButtons();
						}
					});					
				}
				break;
			case humanCharacterChanged:
				if (mtApp.getVSTMultitouchController().getInterfaceModel().isUserTurn()) {
					mtApp.invokeLater(new Runnable() {
						public void run() {
							refreshButtons();
						}
					});
				}
				break;
			case actionSelected:
				// We could handle this event here and update the interface. Maybe not necessary.
				mtApp.invokeLater(new Runnable() {
					public void run() {
						selectAction(mtApp.getVSTMultitouchController().getInterfaceModel().getSelectedAction());
					}
				});
				break;
			case startUserTurn:
				mtApp.invokeLater(new Runnable() {
					public void run() {
						refreshButtons();
						widgetActive = true;
					}
				});
				break;
			case startAgentTurn:
				mtApp.invokeLater(new Runnable() {
					public void run() {
						refreshButtons();
						widgetActive = true;
					}
				});
				break;
			case stopUserTurn:
				mtApp.invokeLater(new Runnable() {
					public void run() {
						widgetActive = false;
						removeAllChildren();
					}
				});
				break;
			case categoryStripEnabled:
				mtApp.invokeLater(new Runnable() {
					public void run() {
						refreshButtons();
					}
				});
				break;
			default:
				logger.warning("Unhandled interface model change: " + change);
				break;
			}
		} else {
			logger.warning("Unknown model change: " + o);
		}
	}
	
	
	
	
	
	
	
	public void testStrip(float x, float y) {
		MTComponent all = testMakeStrip(x, y, testCategoryButtons(), testActionButtons(), testActionButtons());
		addChild(all);
	}
	
	public void testStrip2(float x, float y) {
		MTComponent strip1 = testMakeStrip(x, y, testCategoryButtons(), testActionButtons(), testActionButtons()); 
		addChild(strip1);
		
		// Stress test
		long initTime = System.currentTimeMillis();

		for (int i = 0; i < 200; i++) {
			System.out.println("Test " + i + ": " + (System.currentTimeMillis() - initTime) + "ms");
			MTComponent strip2 = testMakeStrip(x, y, testCategoryButtons(), testActionButtons(), testActionButtons()); 
			//removeChild(strip1);
			strip1.destroy();
			addChild(strip2);
			
			strip1 = testMakeStrip(x, y, testCategoryButtons(), testActionButtons(), testActionButtons());
			//removeChild(strip2);
			strip2.destroy();
			addChild(strip1);
		}
		
		MTComponent strip2 = testMakeStrip(x, y, testCategoryButtons(), testActionButtons(), testActionButtons()); 
		removeChild(strip1);
		strip1.destroy();
		addChild(strip2);
	}
	
	public ArrayList<CategoryButton> testCategoryButtons() {
		ArrayList<CategoryButton> buttons = new ArrayList<CategoryButton>();
		
		for (int i=0; i < 5; i++) {
			CategoryButton butt = new CategoryButton("Test button", mtApp);
			buttons.add(butt);
		}
		return buttons;
	}
	
	public ArrayList<CategoryButton> testCategoryButtons2() {
		ArrayList<CategoryButton> buttons = new ArrayList<CategoryButton>();
		
		for (int i=0; i < 3; i++) {
			CategoryButton butt = new CategoryButton("Test button 2", mtApp);
			buttons.add(butt);
		}
		return buttons;
	}
	
	public ArrayList<ActionButton> testActionButtons() {
		
		ArrayList<ActionButton> buttons = new ArrayList<ActionButton>();
		
		for (int i=0; i < 5; i++) {
			String rndString = "";
			for (int j = 0; j < Math.round(Math.random()*20) + 1; j++) {
				rndString += ("*");
			} 
			ActionButton butt = new ActionButton(new PossibleAction(null, null, "category", "Test action button " + rndString), mtApp);
			buttons.add(butt);
		
		}
		return buttons;
	}
	
	public ArrayList<ActionButton> testActionButtons2() {
		
		ArrayList<ActionButton> buttons = new ArrayList<ActionButton>();
		
		for (int i=0; i < 3; i++) {
			String rndString = "";
			for (int j = 0; j < Math.round(Math.random()*20) + 1; j++) {
				rndString += ("*");
			} 
			ActionButton butt = new ActionButton(new PossibleAction(null, null, "category", "Test action button 2" + rndString), mtApp);
			buttons.add(butt);
		
		}
		return buttons;
	}

}
