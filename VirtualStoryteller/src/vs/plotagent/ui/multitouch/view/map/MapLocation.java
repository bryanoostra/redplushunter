package vs.plotagent.ui.multitouch.view.map;

import java.util.ArrayList;
import java.util.List;

import org.mt4j.MTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import vs.plotagent.ui.multitouch.MultitouchInterfaceSettings;
import vs.plotagent.ui.multitouch.view.actioninterface.InterfaceWidget;

/**
 * Represents a valid location on the map for characters / items (together: inhabitants) to be at. 
 * @author swartjes
 *
 */
public class MapLocation extends MTEllipse {
	
	public static enum LocationStatus {normal, allowed, disallowed};
	
	private static MTColor normalColor = InterfaceWidget.BLUE;
	private static MTColor normalColor_thalf = new MTColor(0, 0, 255, MTColor.ALPHA_HALF_TRANSPARENCY);
	private static MTColor normalColor_thigh = new MTColor(0, 0, 255, MTColor.ALPHA_HIGH_TRANSPARENCY);
	
	private static MTColor allowedColor = InterfaceWidget.GREEN;
	private static MTColor allowedColor_t = new MTColor(0, 255, 0, MTColor.ALPHA_HALF_TRANSPARENCY);
	
	private static MTColor disallowedColor = InterfaceWidget.RED;
	private static MTColor disallowedColor_t = new MTColor(255, 0, 0, MTColor.ALPHA_HALF_TRANSPARENCY);
	
	//private static float llocationSize = 40f;
	
	private int _radius = 40;
	
	private LocationStatus _status;
	private String _locationURI;
	private List<CharacterView> _inhabitants;
	private MTApplication mtApp;
	private Vector3D _loc;
	
	/**
	 * Constructor 
	 * @param locationURI URI representation of the location.
	 * @param loc the position on the map
	 * @param app the MT application.
	 */
	public MapLocation(int radius, String locationURI, Vector3D loc, MTApplication app) {
		super(app, loc, radius, radius);
		mtApp = app;
		_radius = radius;
		_locationURI = locationURI;
		_loc = loc;
		_inhabitants = new ArrayList<CharacterView>();
				
		setStatus(false, LocationStatus.normal);
		removeAllGestureEventListeners();
	}
	
	/**
	 * Clears all inhabitants from this location.
	 */
	public void clearInhabitants() {
		
		_inhabitants.clear();
	}
	
	/**
	 * Adds an inhabitant (character, item) at this location. Registers the inhabitant to be here
	 * and also updates the coordinates of the character so that it appears to be at this location
	 * on the map.
	 * 
	 * @param cv the character view of the inhabitant.
	 */
	public void addInhabitant(CharacterView cv) {

		_inhabitants.add(cv);
		MTRectangle img = cv.getImage();
		
		float height = img.getHeightXY(TransformSpace.GLOBAL);
		float width = img.getWidthXY(TransformSpace.GLOBAL);
		
		//img.setPositionRelativeToParent(this.getCenterPointLocal());
		Vector3D target = this.getCenterPointGlobal();
		
		cv.setActualPositionGlobal(target);
		cv.setLocation(_locationURI);
		img.setPositionGlobal(target);
		
//		// Translate to correct position.
//		switch (_inhabitants.size()) {
//		case 1:
//			//Leave the first character of each location in the middle of that location
//			break;
//		case 2:
//			// Place in top left of location
//			target.translate(new Vector3D(-width/2, -height/2, 0));
//			break;
//		case 3:
//			// Place in bottom right of location
//			target.translate(new Vector3D(width/2, height/2, 0));
//			break;
//		case 4:
//			// Place in top right of location
//			target.translate(new Vector3D(width/2, -height/2, 0));
//			break;
//		case 5: 
//			// Place in bottom left of location
//			target.translate(new Vector3D(-width/2, height/2, 0));
//			break;
//		default:
//			// Leave the rest in the middle.
//			break;
//		}
		
		//characters can only be moved by humans, not by 'the system':
		//img.setPositionGlobal(target);
		
		//cv.getImage().setPositionRelativeToParent(this.getCenterPointLocal());
	}
	
	/**
	 * Move the inhabitant from its current location to this location. Moving causes
	 * a tween animation rather than simple appearance as with addInahbitant. 
	 * 
	 * @param cv the character view to move.
	 */
	public void moveInhabitant(CharacterView cv) {		

		_inhabitants.add(cv);
		MTRectangle img = cv.getImage();
				
		Vector3D currCharacterLocation = cv.getActualPositionGlobal();
		Vector3D targetCharacterLocation = this.getCenterPointGlobal();

//		// Translate to correct position.
//		float height = img.getHeightXY(TransformSpace.GLOBAL);
//		float width = img.getWidthXY(TransformSpace.GLOBAL);
//		switch (_inhabitants.size()) {
//		case 1:
//			//Leave the first character of each location in the middle of that location
//			break;
//		case 2:
//			// Place in top left of location
//			targetLoc.translate(new Vector3D(-width/2, -height/2, 0));
//			break;
//		case 3:
//			// Place in bottom right of location
//			targetLoc.translate(new Vector3D(width/2, height/2, 0));
//			break;
//		case 4:
//			// Place in top right of location
//			targetLoc.translate(new Vector3D(width/2, -height/2, 0));
//			break;
//		case 5: 
//			// Place in bottom left of location
//			targetLoc.translate(new Vector3D(-width/2, height/2, 0));
//			break;
//		default:
//			// Leave the rest in the middle.
//			break;
//		}
				
		// Tween translate.
		// This also takes care of stopping already running translation animations.
		////characters can only be moved by humans, not by 'the system'
		if(!cv.isHumanControlled() && !targetCharacterLocation.equalsVector(currCharacterLocation)) {
			//tweenTranslate looks nice, but gives problems with locationRope, so do not use for now
			//img.tweenTranslateTo(targetLoc.x, targetLoc.y, targetLoc.z, 1000, 0.1f, 0.7f);
			img.setPositionGlobal(targetCharacterLocation);
		
			cv.updateLocationRope();
		}
		
		cv.setActualPositionGlobal(targetCharacterLocation);
		cv.setLocation(_locationURI);
				
	}
	
	/**
	 * Set status of this location (eg allowed - green, disallowed - red). Use during hovering
	 * of an inhabitant above the location.
	 * @param status
	 */
	public void setStatus(boolean clearDiscoveredStatus, LocationStatus status) {
		_status = status;
		
		switch (_status) {
		case normal:
			if(clearDiscoveredStatus) {
				if(MultitouchInterfaceSettings.CLEAR_DESTINATION_STATUS_ENABLED) {
					//setFillColor(normalColor_thalf);
					setFillColor(normalColor_thigh);
					setStrokeColor(normalColor);
				}
			} else {
				//setFillColor(normalColor_thalf);
				setFillColor(normalColor_thigh);
				setStrokeColor(normalColor);
			}
			break;
		case allowed:
			setFillColor(allowedColor_t);
			setStrokeColor(allowedColor);
			break;
		case disallowed:
			setFillColor(disallowedColor_t);
			setStrokeColor(disallowedColor);
			break;
		}
	}
	
	public LocationStatus getStatus() {
		return _status;	
	}
	
	public Vector3D getLocation() {
		return _loc;
	}
	
	public String getLocationURI() {
		return _locationURI;
	}

}
