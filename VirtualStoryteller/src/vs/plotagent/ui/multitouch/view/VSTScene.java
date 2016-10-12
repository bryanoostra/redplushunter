package vs.plotagent.ui.multitouch.view;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;

import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.PickResult.PickEntry;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.MTFiducialInputEvt;
import org.mt4j.input.inputData.MTFingerInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.input.inputProcessors.globalProcessors.RawFiducialProcessor;
import org.mt4j.input.inputProcessors.globalProcessors.RawFingerProcessor;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.IPreDrawAction;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PImage;
import vs.Config;
import vs.debug.LogFactory;
import vs.knowledge.PrologKB;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.model.InterfaceModel;
import vs.plotagent.ui.multitouch.model.PossibleMoveAction;
import vs.plotagent.ui.multitouch.view.actioninterface.InterfaceCharacterRope;
import vs.plotagent.ui.multitouch.view.actioninterface.InterfaceWidget;
import vs.plotagent.ui.multitouch.view.map.CharacterView;
import vs.plotagent.ui.multitouch.view.map.MapLocation;
import vs.plotagent.ui.multitouch.view.map.MapWidget;
import vs.plotagent.ui.multitouch.view.map.CharacterView.CharacterStatus;
import vs.plotagent.ui.multitouch.view.story.StoryWidget;

/**
 * The main scene in the VST MT application. 
 */
public class VSTScene extends AbstractScene implements Observer, IMTInputEventListener {

	private MTComponent backgroundLayer;
	
	private MapWidget mapWidget;
	
	private StoryWidget storyWidgetA;
	private StoryWidget storyWidgetB;
	
	private InterfaceWidget interfaceWidget;
		
	private MTTextArea infoWidget;
	
	private MTRectangle startStoryButton;
	
	private Logger logger;
	
	private static MTColor black = new MTColor(0,0,0);
	private static MTColor gray = new MTColor(128,128,128);
	private static MTColor white = new MTColor(255,255,255);
	private static MTColor transparent = new MTColor(255,255,255, MTColor.ALPHA_FULL_TRANSPARENCY);
	
	private static MTColor startStoryFill = new MTColor(128,255,128);
	private static MTColor startStoryStroke = black;
	private int msgCount;
	
	private VSTMultitouchApplication mtApp;
	
	@Override
	public boolean processInputEvent(MTInputEvent inEvt) {
		
		int id;
		Vector3D position;
		
		if (inEvt instanceof MTFiducialInputEvt) {
			//this means we are using: FIDUCIAL RECOGNITION (see MultitouchInterfaceSettings.java)
			MTFiducialInputEvt fEvt = (MTFiducialInputEvt)inEvt;
			id = fEvt.getFiducialId();
			position = new Vector3D(fEvt.getPosX(), fEvt.getPosY());
			//System.out.println("inEvt is a MTFiducialInputEvt with id: " + id);
		
		} else if (inEvt instanceof MTFingerInputEvt) {
			//this means we are using: CCV 1.4 OBJECT RECOGNITION (see MultitouchInterfaceSettings.java)
			MTFingerInputEvt fEvt = (MTFingerInputEvt)inEvt;
			id = (int)fEvt.getCursor().getId();
			position = new Vector3D(fEvt.getPosX(), fEvt.getPosY());
			//System.out.println("inEvt is a MTFingerInputEvt with id: " + id);
		
		} else {
			return false;
		}
			
		//check if this fiducial/marker/object ID belongs to a character
		String charURI = MultitouchInterfaceSettings.fiducialMap.get(id);
		if(charURI!=null) {
			CharacterView character = mapWidget.getCharacter(charURI);
			//System.out.println("id " + id + " corresponds with character: " +charURI);
			
			//check if character exists 
			if(character==null) {
				logger.severe("Character " + charURI + " with tangible ID " + id + " is not known in domain " + mtApp.getDomain());
				//System.out.println("Character " + charURI + " with tangible ID " + id + " is not known in domain " + mtApp.getDomain());
				return false;
			}
			
			//update the characterView
			character.getImage().setPositionGlobal(position);						
			character.updateLocationRope();

			//if the user interface (aka ActionSelectionInterface) currently belongs to this character, update&evaluate 
			if (character.isUser()) {			
				character.updateDragLocationAndEvaluate(position, false);
			}
		}
		return true;
	}
	
	public VSTScene(VSTMultitouchApplication app, String name) {
		super(app, name);
		mtApp = app;
		
		msgCount++;
		
		logger = LogFactory.getLogger(this);
		
		// TODO: too large
		//SoundManager.getInstance().registerActionSound("ambient", new File(SoundManager.soundPath + "forest.wav"));
		
		
		MTCanvas canvas = this.getCanvas();
		
		this.setClearColor(new MTColor(146, 150, 188, 255));
		
		//CursorTracer is responsible for showing the (blue) circles at each cursor/touch point
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
	
		//start global fiducial tracking
		if(MultitouchInterfaceSettings.USE_FIDUCIALS) {
			RawFiducialProcessor fiducialProcessor = new RawFiducialProcessor();
			fiducialProcessor.addProcessorListener(this);
			registerGlobalInputProcessor(fiducialProcessor);
		}
		if(MultitouchInterfaceSettings.USE_CCV_RECTANGLE_RECOGNITION) {
			//the use of the CCV 1.4 'object recognition' is an alternative for using fiducials.
			//in CCV 1.4 objects that are recognized are however send out as finger touches (with id 180 and upwards)  
			//so, the fiducialProcessor approach doesn't work with CCV1.4 so use the following:
			RawFingerProcessor fingerProcessor = new RawFingerProcessor();
			fingerProcessor.addProcessorListener(this);
			registerGlobalInputProcessor(fingerProcessor);
		}
		
		mapWidget = new MapWidget(app);
		
		//storyWidget midden rechts
		storyWidgetA = new StoryWidget(app.getWidth()/4, app);
		storyWidgetA.setName("storyWidgetA");
		storyWidgetA.translate(new Vector3D(app.width - .80f*storyWidgetA.getWidthGlobal(), .52f*app.height, 0));
		
		//storyWidget linksboven
		storyWidgetB = new StoryWidget(app.getWidth()/4	, app);
		storyWidgetB.setName("storyWidgetB");
		storyWidgetB.rotate(180);
		storyWidgetB.translate(new Vector3D(.45f*storyWidgetB.getWidthGlobal(), .25f*storyWidgetB.getWidthGlobal(), 0));
		
		interfaceWidget = new InterfaceWidget(app, mapWidget);
		
		infoWidget = new MTTextArea(0, 0, 800, 20, 
				FontManager.getInstance().createFont(app, "arial.ttf", 14, black, gray), app);	//Font outline color);, app);
		infoWidget.setText("Info widget");
		
		// Add to canvas
		canvas.addChild(mapWidget);
		canvas.addChild(storyWidgetA);
		canvas.addChild(storyWidgetB);
//		/interfaceWidget.testStrip(200, 200);
		canvas.addChild(interfaceWidget);
		//interfaceWidget.testStrip2(400, 400);
		
		//canvas.addChild(infoWidget);

		
		// Add "start story" button
		if ((app.getVSTMultitouchController() != null && ! app.getVSTMultitouchController().getInterfaceModel().isStoryStarted())
				||  app.getVSTMultitouchController() == null) {

			// TODO: make language-dependent so that language can be set with a property. (DU -> EN)
			String fileBtn = Config.IMAGE_DIR + "/" + "VST-startstorybutton-red-DU.png";
			PImage img = mtApp.loadImage(fileBtn);
			startStoryButton =  new MTRectangle(img, mtApp);
			startStoryButton.setNoStroke(true);
			
			startStoryButton.setGestureAllowance(DragProcessor.class, false);
			startStoryButton.setGestureAllowance(RotateProcessor.class, false);
			startStoryButton.setGestureAllowance(ScaleProcessor.class, false);
			//Make clickable
			startStoryButton.setGestureAllowance(TapProcessor.class, true);
			startStoryButton.registerInputProcessor(new TapProcessor(mtApp));
			startStoryButton.addGestureListener(TapProcessor.class, new StartStoryAction(mtApp));
					
			startStoryButton.translate(new Vector3D(app.getWidth()/2 - startStoryButton.getWidthXY(TransformSpace.LOCAL)/2,
													app.getHeight()/2 - startStoryButton.getHeightXY(TransformSpace.LOCAL)/2,
													0));
			
			canvas.addChild(startStoryButton);

		}

		// mtApp.width/2f, mtApp.height/2f, mtApp.getWidth()/10, mtApp.getHeight()/10
		
		//storyTextArea.setTexture(imgParchmentBackground);
		
		// Listen to model changes (if scene is created within context of an agent)
		if (app.getVSTMultitouchController() != null) {
			logger.info("Adding model listeners.");
			app.getVSTMultitouchController().getInterfaceModel().addObserver(mapWidget);
			app.getVSTMultitouchController().getInterfaceModel().addObserver(interfaceWidget);
			app.getVSTMultitouchController().getInterfaceModel().addObserver(this);
			app.getVSTMultitouchController().getStoryModel().addObserver(storyWidgetA);
			app.getVSTMultitouchController().getStoryModel().addObserver(storyWidgetB);
			app.getVSTMultitouchController().getStoryModel().addObserver(SoundManager.getInstance());
			
		} else {
			logger.warning("No MapController: should only happen if run stand-alone.");
		}
		
		// Make sure character rope (if present) is updated
		if(MultitouchInterfaceSettings.USE_INTERFACE_CHARACTER_ROPE) {
			this.registerPreDrawAction(new UpdateInterfaceRopeAction());
		}
		
		// TODO: this.registerPreDrawAction (... draw character rope)
	}
	
	public void loadDomain(String domain) {
		logger.info("Loading domain (test)");
		mapWidget.loadMap(domain);
		//mapWidget.loadMapLocations(domain);
		//mapWidget.loadCharacterImages(PrologKB.getInstance().getAllEntitiesAtAnyLocation());
	}
	
	// Jeroen:
	// Domain is loaded here, so start tracing here. :)
	public void loadDomain() {
		logger.info("Loading domain");
		String domain = mtApp.getVSTMultitouchController().getInterfaceModel().getDomain();
		mapWidget.loadMap(domain);
				
		Vector<String> characters = PrologKB.getInstance().getAllEntitiesAtAnyLocation();
		Vector<String> charactersNQ = new Vector<String>();
		
		for (String c: characters) {
			charactersNQ.add(c.replace("'", ""));
			logger.fine("Putting " + c);
		}
		
		mapWidget.loadCharacterImagesAndMapLocations(charactersNQ, domain);
	}
	
	public void update(Observable obs, Object o) {

		if (o instanceof InterfaceModel.InterfaceModelChange) {
			final InterfaceModel.InterfaceModelChange change = (InterfaceModel.InterfaceModelChange) o;
		
			mtApp.invokeLater(new Runnable() {
				public void run() {
					infoWidget.setText("Interface model change: " + change + "(" + msgCount + ")");
					msgCount++;					
				}
			});
			
			if (startStoryButton != null && change.equals(InterfaceModel.InterfaceModelChange.storyStarted)) {
				
				mtApp.invokeLater(new Runnable() {
					public void run() {
						startStoryButton.destroy();						
					}
				});
			}
		}
	}	
	
	@Override
	public void init() {
		//storyTextArea.setTexture(imgParchmentBackground);
	}
	@Override
	public void shutDown() {}
	
	class UpdateInterfaceRopeAction implements IPreDrawAction {
		public void processAction() {
			if (interfaceWidget.isWidgetActive()) {
				InterfaceCharacterRope rope = mapWidget.getInterfaceCharacterRope();
				CharacterView cv = mapWidget.getCurrentCharacter();
				if(rope!=null && cv!=null) {
					MTRectangle image = cv.getImage();
					rope.setStatus(cv.getStatus());
					if(image!=null) {
						Vector3D imageCenterGlobal = image.getCenterPointGlobal();
						rope.update(new Vertex(interfaceWidget.getCenterPoint()), new Vertex(imageCenterGlobal));
						if(mapWidget.getMapLocation(cv.getLocation()).containsPointGlobal(imageCenterGlobal)) {
							//actually this is a quick and dirty workaround, but I don't care for pretty anymore...
							if(CharacterStatus.nonPickable == cv.getStatus()) {
								rope.setStrokeColor(InterfaceWidget.TRANSPARENT);
							} else {
								rope.setStrokeColor(InterfaceWidget.YELLOW);
							}
							image.setStrokeColor(InterfaceWidget.YELLOW);
							//the current character should be in front of all other characters, so it can be seen and dragged:
							image.sendToFront();
						}
					}
				}
				mapWidget.getInterfaceCharacterRope().setVisible(true);
			} else {
				mapWidget.getInterfaceCharacterRope().setVisible(false);
			}
		}
		
		public boolean isLoop() {
			// Keep doing this before every frame (false = one-shot action)
			return true;
		}
	}
}
