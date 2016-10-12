package vs.plotagent.ui.multitouch.view.map;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import processing.core.PImage;
import vs.Config;
import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.model.InterfaceModel;
import vs.plotagent.ui.multitouch.view.SoundManager;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;
import vs.plotagent.ui.multitouch.view.actioninterface.InterfaceCharacterRope;

/**
 * Widget representing the background map, characters and locations. 
 */
public class MapWidget extends MTComponent implements Observer {
	
	private VSTMultitouchApplication mtApp;
	private MTComponent backgroundLayer;
	private MTComponent locationsLayer;
	private MTComponent charactersLayer;
	private MTComponent ropeLayer;
	private Set<CharacterView> characterViews;
	private Map<String,MapLocation> locations;
	
	private CharacterViewManager cvMan;
	private CharacterView currentCharacter;
	private InterfaceCharacterRope interfaceCharacterRope;
	
	private String domainImgDir;

	
	private Vector3D scaleVec = new Vector3D(1,1,1);
	
	private Logger logger;
		
	/**
	 * Constructor.
	 * @param app
	 */
	public MapWidget(VSTMultitouchApplication app) {
		//super(0, 0, 10f, 10f, app);
		super(app);
		
		logger = LogFactory.getLogger(this);
		
		mtApp = app;
				
		backgroundLayer = new MTComponent(app);
		locationsLayer = new MTComponent(app);
		ropeLayer = new MTComponent(app);
		charactersLayer = new MTComponent(app);
		
		if(MultitouchInterfaceSettings.USE_INTERFACE_CHARACTER_ROPE) {
			interfaceCharacterRope = new InterfaceCharacterRope(new Vertex(0,0), new Vertex(100,100), app);
			ropeLayer.addChild(interfaceCharacterRope);
		}
		
		addChild(backgroundLayer);
		addChild(locationsLayer);
		addChild(ropeLayer);
		addChild(charactersLayer);
		
		
		characterViews = new HashSet<CharacterView>();
		locations = new Hashtable<String,MapLocation>();
	}
	
	public CharacterView getCharacter(String charURI) {
		return cvMan.get(charURI, null);
	}
	
	public void loadMap(String domain) {
		
		domainImgDir = Config.IMAGE_DIR + domain + "/";
		
		cvMan = new CharacterViewManager(domainImgDir, mtApp);
		
		String fileMap = domainImgDir + Config.MAP_FILENAME;
		PImage imgMap = mtApp.loadImage(fileMap);
		
		if (imgMap!=null) {
			boolean tiled = false;
			
			//System.out.println("imgMap.width = " + imgMap.width);
			//System.out.println("imgMap.height = " + imgMap.height);
			
			//System.out.println("mtApp.width = " + mtApp.width);
			//System.out.println("mtApp.height = " + mtApp.height);
				
			//mtApp.width = imgMap.width;
			//mtApp.height = imgMap.height;
			
			//System.out.println("mtApp.width = " + mtApp.width);
			//System.out.println("mtApp.height = " + mtApp.height);
			
			MTBackgroundImage mtbiMap = new MTBackgroundImage(mtApp, imgMap, tiled);
			mtbiMap.setPickable(false);
			
			//System.out.println("mtbiMap.width = " + mtbiMap.getWidthXYVectLocal());
			//System.out.println("mtbiMap.height = " + mtbiMap.getHeightXYVectLocal());
			

			//this.setWidthLocal(mtbiMap.getWidthXY(TransformSpace.LOCAL));
			//this.setHeightLocal(height)
			
			float u = (float)mtApp.width/(float)imgMap.width;
			float v = (float)mtApp.height/(float)imgMap.height;
			scaleVec = new Vector3D(u, v, 1);
			
			//scale(u, v, 1f, getCenterOfMass2DLocal());			
			
			backgroundLayer.addChild(mtbiMap);

		} else {
			System.out.println("PImage imgBackground == null");
		}
	}
	
	/**
	 * Iterator to iterate over the locations that are present on the map.
	 * @return
	 */
	public Iterator<MapLocation> locationIterator() {
		return locations.values().iterator();
	}
	
	public MapLocation getMapLocation(String s) {
		return locations.get(s);
	}
	
	public void loadCharacterImagesAndMapLocations(List<String> characters, String domain) {
		logger.info("Loading character graphics.");
		//String domain = mtApp.getController().getInterfaceModel().getDomain();
		
		cvMan.loadCharacterImages(characters, domain, mtApp.getVSTMultitouchController().getInterfaceModel().getPrefixMapping());
		int maxDiagonal = cvMan.getMaxCharacterImageDiagonal();
		
		//Because of the (large) markers of tangibles, 
		//maxDiagonal/2 is to small for a location.
		//int radius = maxDiagonal/2;
		int radius = maxDiagonal;
		
		//coordinates for all locations should be in properties file
		String fileProperties = Config.DOMAINKNOWLEDGE + domain + Config.LOCATIONS_PROPERTIES;
		Properties domainMapProperties = new Properties();
		try {
			//extract properties of locations from Properties file
			FileInputStream fis = new FileInputStream(fileProperties);
			domainMapProperties.load(fis);
			fis.close();
		} catch (IOException ioe) {
			
		}
		
		//extract coordinates of locations from Properties
		Set<String> knownLocationsInDomain = domainMapProperties.stringPropertyNames();
		Iterator<String> it1 = knownLocationsInDomain.iterator();
		while (it1.hasNext()) {
			String location = it1.next();
			String coordinates = domainMapProperties.getProperty(location);
			String[] xy = coordinates.split(",");
			int x = Integer.parseInt(xy[0]);
			int y = Integer.parseInt(xy[1]);
						
			MapLocation ml = new MapLocation(radius, location, new Vector3D(x*scaleVec.x, y*scaleVec.y, 0), mtApp);
			locations.put(location, ml);
			locationsLayer.addChild(ml);
			logger.info("Adding location: " + location);
		}
	}
	
	/**
	 * Retrieves the character rope between a character and the interface widget.
	 * @return
	 */
	public InterfaceCharacterRope getInterfaceCharacterRope() {
		return interfaceCharacterRope;
	}
	
	public CharacterView getCurrentCharacter() {
		return currentCharacter;
	}
	
	public PImage getCurrentCharacterImage() {
		return cvMan.getCharacterImageCopy(currentCharacter.getURI());
	}
	
/*	private String nameOnly(String fullURI) {
		String nameNoQuotes = fullURI.replace("\'", "");
		String nameShort = mtApp.getController().getInterfaceModel().getPrefixMapping().shortForm(nameNoQuotes);
		return nameShort.split(":")[1];		
	}*/

	/**
	 * Updates the locations of items according to the knowledge base
	 */
	private void updateItemLocations() {
		logger.info("Updating item locations");
		
		Set<CharacterView> processedCharacterViews = new HashSet<CharacterView>();
		
		// First clear all locations
		for (MapLocation location: locations.values()) {
			location.clearInhabitants();
		}
		
		// Then add or move the characters
		Hashtable<String,Vector<String>> locI = mtApp.getVSTMultitouchController().getInterfaceModel().getLocationInhabitants();	
		
		for (String location: locI.keySet()) {
			// Get characters for Location
			String loc = location.replace("\'", ""); //nameOnly(location);
			MapLocation mapLoc = locations.get(loc);
			if (mapLoc != null) {
			
				Vector<String> charsOnLoc = locI.get(location);
				if (charsOnLoc != null) {
					for (String character: charsOnLoc) {
						CharacterView cv = cvMan.get(character, location);
						if(cv!=null) {
							processedCharacterViews.add(cv);
							if (characterViews.contains(cv)) {
								// Just move (animation)
								mapLoc.moveInhabitant(cv);
							} else {
								// Add new (no animation)
								synchronized(characterViews) {
									characterViews.add(cv);
								}
								charactersLayer.addChild(cv.getImage());
								mapLoc.addInhabitant(cv);
							}
						}
					}
				}
			} else {
				logger.severe("No location for string " + loc);
			}
		}
		
		// Replace current character views by the ones encountered now.
		synchronized(characterViews) {
			for (CharacterView cv: characterViews) {
				if (! processedCharacterViews.contains(cv)) {
					// This character was removed from the system.
					charactersLayer.removeChild(cv.getImage());
					// TODO
				}
			}
			characterViews.clear();
			characterViews.addAll(processedCharacterViews);
		}
		
	}
	
	private void test() {
		int radius = 100;
		MapLocation ml = new MapLocation(radius, "test1", new Vector3D(100, 100, 0), mtApp);
		locations.put("test1", ml);
		locationsLayer.addChild(ml);
		
		MapLocation ml2 = new MapLocation(radius, "test2", new Vector3D(200, 300, 0), mtApp);
		ml2.setStatus(false, MapLocation.LocationStatus.allowed);
		locations.put("test2", ml);
		locationsLayer.addChild(ml2);
		
		MapLocation ml3 = new MapLocation(radius, "test3", new Vector3D(500, 400, 0), mtApp);
		ml3.setStatus(false, MapLocation.LocationStatus.disallowed);
		locations.put("test3", ml);
		locationsLayer.addChild(ml3);
	}
	
	/**
	 * Handles model updates.
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
			case storyStarted:
				SoundManager.getInstance().playActionSound("ambient");
				mtApp.invokeLater(new Runnable() {
					public void run() {
						updateItemLocations();						
					}
				});
			case locationsChanged:
				mtApp.invokeLater(new Runnable() {
					public void run() {
						updateItemLocations();						
					}
				});

				break;
			case possibleActionsUpdated:
				break;
			case humanCharacterChanged:
				logger.info("Changing statuses of the " + characterViews.size() + " character views");

				Vector<SetUser> runnableVector = new Vector<SetUser>();
				synchronized(characterViews) {
					for (CharacterView cv: characterViews) {
						logger.fine("Comparison:\n" + cv.getURI() + "\n" + mtApp.getVSTMultitouchController().getInterfaceModel().getHumanCharacterName());
						if (cv.getURI().equals(mtApp.getVSTMultitouchController().getInterfaceModel().getHumanCharacterName())) {
							runnableVector.add(new SetUser(cv, true));
							cv.getImage().sendToFront();
						} else {
							runnableVector.add(new SetUser(cv, false));
						}
					}
				}
				
				for (SetUser u: runnableVector) {
					mtApp.invokeLater(u);
				}
				
				break;
			case actionSelected:
				break;
			case startUserTurn:
				synchronized(characterViews) {
					for (final CharacterView cv: characterViews) {
						if (cv.isUser()) {
							mtApp.invokeLater(new Runnable() {
								public void run() {
									cv.setStatus(CharacterView.CharacterStatus.pickable);
									//cv.changeDragLocation(cv._dragLocation);
									//XXX: ???
								}
							});
							
						}
					}
				}
				break;
			case stopUserTurn:
				synchronized(characterViews) {
					for (final CharacterView cv: characterViews) {
						mtApp.invokeLater(new Runnable() {
							public void run() {
								cv.setStatus(CharacterView.CharacterStatus.nonPickable);
							}
						});						
					
					}
				}
				
				synchronized(locations) {
					for (final MapLocation loc: locations.values()) {
						mtApp.invokeLater(new Runnable() {
							public void run() {
								loc.setStatus(false, MapLocation.LocationStatus.normal);
							}
						});						
					
					}
				}
				
				break;
			case startTurn:
				synchronized(characterViews) {
					for (final CharacterView cv: characterViews) {
						if (cv.getURI().equals(mtApp.getVSTMultitouchController().getInterfaceModel().getCurrentCharacter())) {
							mtApp.invokeLater(new Runnable() {
								public void run() {
									cv.setTurn(true);
								}
							});
							
						}
					}
				}
				
				break;
			case stopTurn:
				synchronized(characterViews) {
					for (final CharacterView cv: characterViews) {
						mtApp.invokeLater(new Runnable() {
							public void run() {
								cv.setTurn(false);
							}
						});
							
					}
				}
				
				break;
			default:
				logger.warning("Unhandled interface model change: " + change);
				break;
			}
		} else {
			logger.warning("Unknown model change: " + o);
		}
	}
	
	class SetUser implements Runnable {
		CharacterView _view;
		boolean _isUser;
		
		public SetUser(CharacterView cv, boolean isUser) {
			_view = cv;
			_isUser = isUser;
		}
		
		public void run() {
			logger.info("Setting user status of " + _view.getURI() + " to " + _isUser);
			_view.setUser(_isUser);
			if (_isUser) {
				currentCharacter = _view;
			}
		}
	}
}
