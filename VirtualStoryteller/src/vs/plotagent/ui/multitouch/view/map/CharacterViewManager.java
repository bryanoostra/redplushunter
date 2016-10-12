package vs.plotagent.ui.multitouch.view.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import processing.core.PImage;
import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.view.VSTMultitouchApplication;

import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Manager for character views. Loads character view images and caches
 * character views once created.
 * @author swartjes
 *
 */
public class CharacterViewManager {

	private VSTMultitouchApplication mtApp;
	
	private Map<String,CharacterView> cvMap;
	private String domainDirectory;
	private Logger logger;
	private PrefixMapping prefixMapping;
	
	// To cache images of characters and objects
	private Map<String,PImage> imgDB = new HashMap<String,PImage>();
	private Map<String,PImage> imgDB2 = new HashMap<String,PImage>();
	
	public CharacterViewManager(String domainDir, VSTMultitouchApplication app) {
		mtApp = app;
		
		logger = LogFactory.getLogger(this);
		
		domainDirectory = domainDir; 
		
		cvMap = new HashMap<String,CharacterView>();
	}
	
	
	public void loadCharacterImages(List<String> characters, String domain, PrefixMapping pm) {

		prefixMapping = pm;
		
		for (String character: characters) {

			//System.out.println("debug charactername = " +character);
			PImage bi = loadCharacterImage(character);
			PImage bi2 = loadCharacterImage(character);
			if (bi != null) {
				logger.info("Adding image for character " + character);
				imgDB.put(character, bi);
				imgDB2.put(character, bi2);
			} else {
				logger.warning("debug MISSING charactername = " +character);
			}
		}
	}
	
	//calculates the size of the circle based on the largest character image
	public int getMaxCharacterImageDiagonal() {

		double maxDiagonal = 10;//*LOCATION_BORDER_STROKE_SIZE;
		//double maxHeightDimension = 1*MAP_STROKE_SIZE;
		//double maxWidthDimension = 1*MAP_STROKE_SIZE;

		//iterate over all character images and find the largest diagonal
		//the max diagonal determines the size of the circle
		Iterator<PImage> itChar = imgDB.values().iterator();
		if(!itChar.hasNext()) {
			System.out.println("debug drawCircles(): no character images!");
		}
		while(itChar.hasNext()) {
			PImage pi = itChar.next();
			double h = pi.height;
			double w = pi.width;
			double diagonal = Math.sqrt(Math.pow(h, 2) + Math.pow(w, 2));
			maxDiagonal = Math.max(maxDiagonal, diagonal);
			//maxHeightDimension = Math.max(maxHeightDimension, h);
			//maxWidthDimension = Math.max(maxWidthDimension, w);
		}
		
		//round it off and make it even
		int offset = (int)(maxDiagonal/2); 
		int size = 2*offset;
		
		return size;
	}		
	
	private PImage loadCharacterImage(String fullName) {
		String file = domainDirectory + nameOnly(fullName) +".PNG";
		
		return mtApp.loadImage(file);
	}
	
	public PImage getCharacterImageCopy(String fullName) {
		return imgDB2.get(fullName);
	}
	
	private String nameOnly(String fullURI) {
		String nameNoQuotes = fullURI.replace("\'", "");
		String nameShort = prefixMapping.shortForm(nameNoQuotes);
		return nameShort.split(":")[1];		
	}
	
	public CharacterView get(String charURI, String locURI) {
		CharacterView cv = cvMap.get(charURI);
		
		if (cv == null) {
			// construct new & put.
			PImage img = imgDB.get(charURI);
			if (img == null) {
				logger.severe("No image for item " + charURI);
				return null;
			} 
			
			logger.info("Creating new character view for character " + charURI);
			
			cv = new CharacterView(charURI, img, locURI, mtApp);
			cvMap.put(charURI, cv);
						
		} else {
		
			logger.info("Using existing character view for character " + charURI);
		}
		
		return cv;
	}
}
