package vs.characteragent.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import vs.Config;
import vs.debug.LogFactory;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * A JPanel with the map of the story world drawn on it
 * 
 * @author Thijs Alofs
 **/
public class MapPanel extends JPanel {
	
	private static final long serialVersionUID = 2828942638344038679L;
	
	//the default thickness of lines
	private static final int LOCATION_BORDER_STROKE_SIZE = 4;
	private static final int HALF_OF_THE_MINIMAL_CHARACTER_BORDER_STROKE_SIZE = 2;
	
	//colors
	private static final Color CURRENT = Color.ORANGE;
	private static final Color POSSIBLE = Color.GREEN;
	private static final Color IMPOSSIBLE = Color.RED;
	
	//
	private static final Color TMP_LOCATION_FILL = new Color(0f, 0f, 1f, .25f);
		
	//0 when image is at actual location
	//1 when image is at allowed destination
	//-1 when image is at a not allowed location
	private int possibleDestination = 0;
	
	private String domainDirectory = null;
	
	private boolean draggingCharacter = false;
	
	private String currentProblem = "Waiting for a Plot agent to start the story...";
	
	private BufferedImage biMap;
	private BufferedImage biCurrentMap;
	private Dimension mapDimension = null;
	
	private Rectangle humanRectangle = null;
	private String humanCharacterName = null;
	
	private Hashtable<String, Point> locationsOnMap = new Hashtable<String, Point>();
	private Hashtable<String, Point> charactersOnMap = new Hashtable<String, Point>();
	private Hashtable<String, BufferedImage> characterImages = new Hashtable<String, BufferedImage>();

	//stored just for debug purpose: draw location inhabitants on map
	private Hashtable<String, Vector<String>> location_inhabitants = new Hashtable<String, Vector<String>>();

	private MapFrame mapFrame;
	private HumanCharacterAgentGui hgui;
	
	private PrefixMapping pm;
	private Logger logger;

	private Point centerOfInterface;
	
	//hgui needed/used for writing info to hgui-console
	public MapPanel(MapFrame mapFrame, HumanCharacterAgentGui hgui) {
		this.mapFrame = mapFrame;
		this.hgui = hgui;
		
		setOpaque(true);
		
		//initialize the logger
		logger = LogFactory.getLogger(this);
		
		//used for shortening names
		pm = PrefixMapping.Factory.create();
		pm.setNsPrefixes(Config.namespaceMap);
		
		//panel must have a size (to be able to display debug messages)
		Dimension defaultDimension = new Dimension(100,100);
		this.setPreferredSize(defaultDimension);
		//this.setSize(defaultDimension);
	}
	
	//set domain, load images, set up map
	public void setDomain(String domain, Vector<String> characters) throws IOException {
		domainDirectory = Config.IMAGE_DIR + domain + "/";
		
		//for now, assume there is only one map in each domain
		String fileMap = domainDirectory + Config.MAP_FILENAME;
		try {
			BufferedImage bi = ImageIO.read(new File(fileMap));
			if (bi == null) {
				throw new IOException("error while loading " + fileMap);
			} else {
				//allow the image to be hardware accelerated
				//make sure the image is fully opaque (Alpha blending in software rendering mode is very slow)
				GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
				biMap = gc.createCompatibleImage(bi.getWidth(), bi.getHeight(), Transparency.OPAQUE);
				Graphics2D g2d = biMap.createGraphics();
				g2d.drawImage(bi, 0, 0, null);
				g2d.dispose();
			
				//set the size of the panel to the size of the map
				mapDimension = new Dimension(biMap.getWidth(),biMap.getHeight());
				//this.setSize(mapDimension);
				this.setPreferredSize(mapDimension);
				//this.setMinimumSize(mapDimension);
				//this.setMaximumSize(mapDimension);
				
				//clone the original map
				biCurrentMap = new BufferedImage(mapDimension.width, mapDimension.height, BufferedImage.TYPE_INT_RGB);
				biCurrentMap.setData(biMap.getData());
			}
		} catch (IOException ioe) {
			currentProblem = "imageProblem: could not retrieve " + fileMap;
			throw ioe;
		}
		
		//coordinates for all locations should be in properties file
		String fileProperties = Config.DOMAINKNOWLEDGE + domain + Config.LOCATIONS_PROPERTIES_OLD;
		Properties domainMapProperties = new Properties();
		try {
			//extract properties of locations from Properties file
			FileInputStream fis = new FileInputStream(fileProperties);
			domainMapProperties.load(fis);
			fis.close();
		} catch (IOException ioe) {
			//XXX: does this one ever occur? correctly displayed?
			currentProblem = "coordinateProblem: could not retrieve " + fileProperties;
			throw ioe;
		}
		
		//get full String representation of domain
		String prefix = Config.namespaceMap.get(domain);
		
		//extract coordinates of locations from Properties
		Set<String> knownLocationsInDomain = domainMapProperties.stringPropertyNames();
		Iterator<String> it1 = knownLocationsInDomain.iterator();
		while (it1.hasNext()) {
			String location = it1.next();
			String coordinates = domainMapProperties.getProperty(location);
			String[] xy = coordinates.split(",");
			int x = Integer.parseInt(xy[0]);
			int y = Integer.parseInt(xy[1]);
			//XXX: is it needed to check if point is within the map or just assume it is?
			locationsOnMap.put("'" + prefix + location + "'", new Point(x, y));
		}
		
		//get the images for all the characters (also needed to calculate size of circles in drawCircles() )
		Iterator<String> it2 = characters.iterator();
		while(it2.hasNext()) { 
			String character = it2.next();
			//System.out.println("debug charactername = " +character);
			BufferedImage bi = getBufferedCharacterImage(character);
			if (bi != null) {
				characterImages.put(character, bi);
			} else {
				System.out.println("debug MISSING charactername = " +character);
			}
		}
		
		drawCircles(biCurrentMap.createGraphics());
	}
	
	//used to set the name of the human character when it is known
	public void setHumanCharacterName(String characterName) {
		humanCharacterName = "'"+characterName+"'";
	}
	
	//called to update the inhabitants of each location
	//updates the visual representation of the world
	public void updateLocationsInhabitants(Hashtable<String, Vector<String>> location_inhabitants) {
		//the calculated Points in charactersOnMap are no longer valid 
		charactersOnMap.clear();
		
		//store for debug purposes
		this.location_inhabitants = location_inhabitants;
		
		//(re)check for existing problems
		currentProblem = "";
		if (!existsCoordinateProblem(location_inhabitants.keySet())) {
			if (!existsCharacterImageProblem(location_inhabitants.values())) {
				//no problem: calculate all positions for character images
				calculateCharacterPositionsOnMap(location_inhabitants);
			}
		}

		//reset current map to an empty map
		biCurrentMap.setData(biMap.getData());

		//reset to 0, because character image will be drawn at actual location
		possibleDestination = 0;
		
		drawCircles(biCurrentMap.createGraphics());
		
		if(!charactersOnMap.isEmpty()) {
			//paint all other characters on the BufferedImage of the current map
			//so not on the panel itself and therefore less 'flickering' occurs(!)
			paintOtherCharactersOn(biCurrentMap.createGraphics());
			
			//if there are characters, the human character should be amongst them
			Point p = charactersOnMap.get(humanCharacterName);
			BufferedImage bi = characterImages.get(humanCharacterName);
			//update the humanRectangle
			humanRectangle = new Rectangle(p.x, p.y, bi.getWidth(), bi.getHeight());
		} else {
			humanRectangle = null;
		}
				
		//draw new current map and image of human character on panel 
		repaint();
	}

	public void updateCenterOfInterface(int xCenterInterface, int yCenterInterface) {
    	centerOfInterface = new Point(xCenterInterface, yCenterInterface);
	}
	
	//used by InterfacePanel to find out the size of the map
	public Dimension getMapDimension() {
		if(mapDimension==null) return null;
		return new Dimension(mapDimension.width, mapDimension.height);
	}
	
	//checks if coordinates for all currently occupied locations are available
	//not able to resolve problems
	private boolean existsCoordinateProblem(Set<String> locations) {
		Iterator<String> it = locations.iterator();
		while(it.hasNext()) {
			String location = it.next();
			Point p = locationsOnMap.get(location);
			
			//System.out.println("prefix = " + prefix);
			//System.out.println("location = " + location);
			
			String place = pm.shortForm(location.replace("'", ""));
			//System.out.println("place = " + place);
		
			
			//check if coordinate point exists
			if (p==null) {
				//there is a coordinateProblem, write line to console / logger
				currentProblem = "coordinateProblem: no coordinates available for " + location;
				String message = "[MapPanel] " + currentProblem; 
				logger.severe(message);
				hgui.writeConsole(message);
				return true;
			}
		}
		return false;
	}
	
	//checks if images for all currently visible characters are available
	//tries to resolve the problem
	private boolean existsCharacterImageProblem(Collection<Vector<String>> collection) {
		Iterator<Vector<String>> itCollection = collection.iterator();
		while(itCollection.hasNext()) {
			Vector<String> vector = itCollection.next();
			Iterator<String> itVector = vector.iterator();
			while(itVector.hasNext()) {
				String character = itVector.next();
				//try to retrieve the BufferedImage for this character
				BufferedImage retrievedBI = characterImages.get(character);
				if(retrievedBI == null) {
					//first try to resolve the problem
					BufferedImage createdBI = getBufferedCharacterImage(character);
					if(createdBI != null) {
						//just add BufferedImage to the Vector with characterImages
						characterImages.put(character, createdBI);
					} else {
						//if no image could be created, there is a problem
						System.out.println("debug existsCharacterImageProblem(): " + character);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//returns BufferedImage for given character
	//returns null when failed
	private BufferedImage getBufferedCharacterImage(String fullName) {
		String nameNoQuotes = fullName.replace("\'", "");
		String nameShort = pm.shortForm(nameNoQuotes);
		String nameOnly = nameShort.split(":")[1];
		String file = domainDirectory + nameOnly +".PNG";
		try {	
			BufferedImage bi = ImageIO.read(new File(file));
			if (bi == null) throw new IOException(file);
			return bi;
		} catch (IOException ioe) {
			//write debug/logger/console output
			currentProblem = "imageProblem: could not retrieve " + file;
			String message = "[MapPanel] " + currentProblem; 
			logger.severe(message);
			hgui.writeConsole(message);
			return null;
		}
	}
	
	//calculates the position of each character image on the image of the map
	private void calculateCharacterPositionsOnMap(Hashtable<String, Vector<String>> location_inhabitants) {
		//iterate over all locations
		Iterator<String> itLocations = location_inhabitants.keySet().iterator();
		while(itLocations.hasNext()) {
			String location = (String)itLocations.next();
			Vector<String> inhabitants = location_inhabitants.get(location);
			if (inhabitants!=null && inhabitants.size()>0) {
				Point p = locationsOnMap.get(location);
				int x = (int)p.getX();
				int y = (int)p.getY();
				if (inhabitants.size()==1) {
					String name = inhabitants.get(0);
					BufferedImage bi = characterImages.get(name);
					charactersOnMap.put(name, new Point(x - bi.getWidth()/2, y - bi.getHeight()/2));
				} else if (inhabitants.size()==2) {
					String name1 = inhabitants.get(0);
					String name2 = inhabitants.get(1);
					BufferedImage bi2 = characterImages.get(name2);
					charactersOnMap.put(name1, new Point(x, y));
					charactersOnMap.put(name2, new Point(x - bi2.getWidth(), y - bi2.getHeight()));
				} else if (inhabitants.size()==3) {
					String name1 = inhabitants.get(0);
					String name2 = inhabitants.get(1);
					String name3 = inhabitants.get(2);
					BufferedImage bi2 = characterImages.get(name2);
					BufferedImage bi3 = characterImages.get(name3);
					charactersOnMap.put(name1, new Point(x, y));
					charactersOnMap.put(name2, new Point(x - bi2.getWidth(), y - bi2.getHeight()));
					charactersOnMap.put(name3, new Point(x, y - bi3.getHeight()));
				} else if (inhabitants.size()>=4) {
					String name1 = inhabitants.get(0);
					String name2 = inhabitants.get(1);
					String name3 = inhabitants.get(2);
					String name4 = inhabitants.get(3);
					BufferedImage bi2 = characterImages.get(name2);
					BufferedImage bi3 = characterImages.get(name3);
					BufferedImage bi4 = characterImages.get(name4);
					charactersOnMap.put(name1, new Point(x, y));
					charactersOnMap.put(name2, new Point(x - bi2.getWidth(), y - bi2.getHeight()));
					charactersOnMap.put(name3, new Point(x, y - bi3.getHeight()));
					charactersOnMap.put(name4, new Point(x - bi4.getWidth(), y));
					if (inhabitants.size()>4) {
						//iterate over remaining inhabitants of this location
						Iterator<String> itInhabitants = inhabitants.iterator();
						//skip 1, 2, 3 and 4
						itInhabitants.next();
						itInhabitants.next();
						itInhabitants.next();
						itInhabitants.next();
						while(itInhabitants.hasNext()) {
							String name = (String)itInhabitants.next();
							BufferedImage bi = characterImages.get(name);
							charactersOnMap.put(name, new Point(x - bi.getWidth()/2, y - bi.getHeight()/2));
							//Don't know where to draw next, for now all in center
						}
					}
				}
			}
		}
	}
		
	//overrides default paint method
	//loads the BufferedImage of the current map, which also has all other characters
	//calls paintHumanCharacter to draw its image on the panel
	protected void paintComponent(Graphics g){
		//System.out.println("MapPanel paintComponent()");
		
		//convert Graphics to Graphics2D
		Graphics2D g2d = (Graphics2D)g;
			
		//check if there is a map
		if (biCurrentMap != null) {
			//draw the map on the panel
			g2d.drawImage(biCurrentMap, null, 0, 0);
//			//draw the circles to mark locations
//			drawCircles(g2d);
		}
		
		if (humanCharacterName != null) {
//			//paint all characters but the human character
//			paintOtherCharactersOn(g2d);
			//draw the human character on the panel
			paintDraggedHumanCharacterOn(g2d);
			paintFixedHumanCharacterOn(g2d);
		}

		//debug: write location_inhabitants information on map
		//this.writeLocationInhabitants(g2d);
			
		if (!"".equals(currentProblem)) {
			//there is a problem, write a message to the display
			TextLayout tl = new TextLayout(currentProblem, g2d.getFont(), g2d.getFontRenderContext());
			Rectangle2D b = tl.getBounds();
			g2d.setColor(Color.YELLOW);
			g2d.fillRect(0, 0, (int)b.getWidth()+20, (int)b.getHeight()+20);
			g2d.setColor(Color.BLACK);
			tl.draw(g2d, 10, 10 + (int)b.getHeight());
		}
	}
	
	//draws circles at locations on the map
	//calculates the size of the circle based on the largest character image
	private void drawCircles(Graphics2D g2d) {

		double maxDiagonal = 10*LOCATION_BORDER_STROKE_SIZE;
		//double maxHeightDimension = 1*MAP_STROKE_SIZE;
		//double maxWidthDimension = 1*MAP_STROKE_SIZE;

		//iterate over all character images and find the largest diagonal
		//the max diagonal determines the size of the circle
		Iterator<BufferedImage> itChar = characterImages.values().iterator();
		if(!itChar.hasNext()) {
			System.out.println("debug drawCircles(): no character images!");
		}
		while(itChar.hasNext()) {
			BufferedImage bi = itChar.next();
			double h = bi.getHeight();
			double w = bi.getWidth();
			double diagonal = Math.sqrt(Math.pow(h, 2) + Math.pow(w, 2));
			maxDiagonal = Math.max(maxDiagonal, diagonal);
			//maxHeightDimension = Math.max(maxHeightDimension, h);
			//maxWidthDimension = Math.max(maxWidthDimension, w);
		}
		int offset = (int)(maxDiagonal/2); 
		//int offset = (int)Math.min(maxHeightDimension, maxWidthDimension);
		int size = 2*offset;
		
		//set the stroke size
		g2d.setStroke(new BasicStroke(LOCATION_BORDER_STROKE_SIZE));
		
		//iterate over all locations and for each draw a circle
		Iterator<String> itLoc = locationsOnMap.keySet().iterator();
		while(itLoc.hasNext()) {
			String prologString = itLoc.next();
			Point p = locationsOnMap.get(prologString);
			int possibleDestination = mapFrame.tryPossibleDestination(prologString);
			Color location;
			if(possibleDestination == 1) {
				location = POSSIBLE;
			} else if (possibleDestination == -1) {
				location = IMPOSSIBLE;
			} else if (possibleDestination == 0) {
				location = CURRENT;
			} else {
				location = Color.BLUE;
			}
			
			//Using transparent colors can have strange color-effects when underlying image is not desaturated first
			//TODO: desaturate underlaying image and use nice (Orange, Green, Red) transparent glows again. :-)
			//Color transparentLocation = new Color(location.getRed(), location.getGreen(), location.getBlue(), 65);
			//g2d.setColor(transparentLocation);
			//for now just use a blue glow for all locations
			g2d.setColor(TMP_LOCATION_FILL);
			g2d.fillOval(p.x-offset, p.y-offset, size, size);
			
			g2d.setColor(location);
			g2d.drawOval(p.x-offset, p.y-offset, size, size);
		}
	}
		
	//paints all characters, except the character that the user controls
	private void paintOtherCharactersOn(Graphics2D g2d) {
		//iterate over all characters on the map
		Iterator<String> itCharacters = charactersOnMap.keySet().iterator();
		while (itCharacters.hasNext()) {
			String name = (String)itCharacters.next();
			//don't draw the image of the human character
			if(!name.equals(humanCharacterName)) {
				Point p = charactersOnMap.get(name);
				BufferedImage bi = characterImages.get(name);
				//paint the character on the BufferedImage of the current map
				//so not on the panel itself and therefore less 'flickering' occurs(!)
				g2d.drawImage(bi, null, p.x, p.y);
			}
		}
	}
	
	//paints the humanCharacter on the Graphics environment
	private void paintFixedHumanCharacterOn(Graphics2D g2d) {
		if(!charactersOnMap.isEmpty()) {
			//if there are characters, the human character should be amongst them
			BufferedImage bi = characterImages.get(humanCharacterName);
			//get the current location of the human character
			Point p = charactersOnMap.get(humanCharacterName);
			//draw the human character on the panel
			g2d.drawImage(bi, null, p.x, p.y);
		}
	}
	
	//paints the humanCharacter on the Graphics environment
	private void paintDraggedHumanCharacterOn(Graphics2D g2d) {
		if(!charactersOnMap.isEmpty()) {

			//set color for humanCharacterBorder and 'lifeline'
			if(possibleDestination == 1) {
				//allowed: draw green rectangle on the panel
				g2d.setColor(POSSIBLE);
			} else if (possibleDestination == -1) {
				//not allowed: draw red rectangle on the panel
				g2d.setColor(IMPOSSIBLE);
			} else if (possibleDestination == 0) {
				//dragging above current location
				g2d.setColor(CURRENT);
			} else {
				return;
			}
				
			int largeHalfSize = 2*HALF_OF_THE_MINIMAL_CHARACTER_BORDER_STROKE_SIZE;
			int smallHalfSize = HALF_OF_THE_MINIMAL_CHARACTER_BORDER_STROKE_SIZE;
			if(draggingCharacter) {
				g2d.setStroke(new BasicStroke(2*largeHalfSize));
				g2d.drawRect(humanRectangle.x-2*largeHalfSize, humanRectangle.y-2*largeHalfSize, humanRectangle.width+4*largeHalfSize, humanRectangle.height+4*largeHalfSize);
			}else {
				g2d.setStroke(new BasicStroke(2*smallHalfSize));
				g2d.drawRect(humanRectangle.x-smallHalfSize, humanRectangle.y-smallHalfSize, humanRectangle.width+2*smallHalfSize, humanRectangle.height+2*smallHalfSize);
			}
			
	    	//draw 'lifeline' to the character from center of interface
			if(centerOfInterface!=null) {
				g2d.setStroke(new BasicStroke(2*largeHalfSize));
				g2d.drawLine(centerOfInterface.x, centerOfInterface.y, (int)humanRectangle.getCenterX(), (int)humanRectangle.getCenterY());
			}
			
			//if there are characters, the human character should be amongst them
			BufferedImage bi = characterImages.get(humanCharacterName);
			BufferedImageOp op = new ColorConvertOp(
				       ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
		     bi = op.filter(bi, null);
			
			//draw the human character on the panel
			g2d.drawImage(bi, null, humanRectangle.x, humanRectangle.y);
		}
	}
	
	//just for debug purpose, commented out in paint method
	private void writeLocationInhabitants(Graphics2D g2d) {
		//iterate over all locations
		Iterator<String> itLocations = location_inhabitants.keySet().iterator();
		while(itLocations.hasNext()) {
			String location = (String)itLocations.next();
			Vector<String> inhabitants = location_inhabitants.get(location);
			String place = pm.shortForm(location.replace("'", ""));
			String description = "@" + place + " = ";
			Iterator<String> itChar = inhabitants.iterator();
			while (itChar.hasNext()) {
				String name = itChar.next().replace("'", "");
				description = description + pm.shortForm(name) + " ";
			}
			Point p = locationsOnMap.get(location);
			TextLayout tl = new TextLayout(description, g2d.getFont(), g2d.getFontRenderContext());
			Rectangle2D b = tl.getBounds();
			float x = p.x - (float)b.getCenterX();
			float y = p.y - (float)b.getCenterY();
			g2d.setColor(Color.YELLOW);
			g2d.fillRect((int)x-3, (int)(y-3-b.getHeight()), (int)b.getWidth()+10, (int)b.getHeight()+10);
			g2d.setColor(Color.BLACK);
			tl.draw(g2d, x, y);
		}
	}

	//Class that handles all mouse listening within the MapPanel
	public class MapMouseListener implements MouseListener, MouseMotionListener {
		private int dX;
		private int dY;
		
		//checks if we are starting a drag
		public void mousePressed(MouseEvent e) {
			Point point = e.getPoint();
			//nothing can be done when there are no characters on the map
			if(!charactersOnMap.isEmpty()) {
				//check if we clicked on the image of the human character
				if(humanRectangle.contains(point)) {
					draggingCharacter = true;
					Point pointCharacter = humanRectangle.getLocation();
					dX = point.x - pointCharacter.x;
					dY = point.y - pointCharacter.y;
					System.out.println("started dragging humanCharacter: " + humanCharacterName);
					repaint();
				}
			}
		}

		//handles dragging of character
		public void mouseDragged(MouseEvent e) {
			//nothing has to be done when we are not dragging a character
			if (draggingCharacter) { 
				update_drag(e.getPoint(), dX, dY);
			}
		}
		
		//handles dropping a character
		public void mouseReleased(MouseEvent e) {
			//nothing has to be done when we are not dragging a character 
			if(draggingCharacter) {
				//mouse button was released, so dragging stops
				draggingCharacter = false;
				//draw everything at the current location
				repaint();
			}
		}
		
		public void mouseClicked(MouseEvent e) {
			//when a mouse button is pressed and released (at the same point)			
		}
		
		public void mouseEntered(MouseEvent e) {
			//when mouse cursor enters the area of the component
		}
		
		public void mouseExited(MouseEvent e) {
			//when mouse cursor exits the area of the component
		}
		
		public void mouseMoved(MouseEvent e) {
			//whenever the mouse cursor moves
		}
	}
	
	//Class that handles all TUIO listening within the MapPanel
	public class MapTUIOListener implements TuioListener {
		private int draggingCursorID = -1;
		private int dX;
		private int dY;
					
		@Override
		//corresponds more or less with mousePressed()
		public void addTuioCursor(TuioCursor tuioCursor) {
			Point point = mapFrame.tuioPointToPoint(tuioCursor.getPosition());
			//nothing can be done when there are no characters on the map
			if(!charactersOnMap.isEmpty()) {
				//check if we clicked on the image of the human character
				if(humanRectangle.contains(point)) {
					draggingCharacter = true;
					draggingCursorID = tuioCursor.getCursorID();
					Point pointCharacter = humanRectangle.getLocation();
					dX = point.x - pointCharacter.x;
					dY = point.y - pointCharacter.y;
					System.out.println("started dragging humanCharacter: " + humanCharacterName);
					repaint();
				}
			}
		}
	
		@Override
		//corresponds more or less with mouseDragged()
		public void updateTuioCursor(TuioCursor tuioCursor) {
			Point point = mapFrame.tuioPointToPoint(tuioCursor.getPosition());
			int id = tuioCursor.getCursorID();
			//nothing has to be done when this cursor is not dragging a character
			if (draggingCharacter && id==draggingCursorID) {
				update_drag(point, dX, dY);
			}
		}

		@Override
		//corresponds more or less with mouseReleased();
		public void removeTuioCursor(TuioCursor tuioCursor) {
			int id = tuioCursor.getCursorID();
			//nothing has to be done when we are not dragging a character 
			if(draggingCharacter && id==draggingCursorID) {
				//mouse button was released, so dragging stops
				draggingCharacter = false;
				draggingCursorID = -1;
				System.out.println("stopped dragging humanCharacter");
				//draw everything at the current location
				repaint();
			}
		}
		
		@Override
		//This callback method is invoked by the TuioClient to mark the end of a received TUIO message bundle.
		public void refresh(TuioTime time) {
			//System.out.println("refresh was called! " + time.getTotalMilliseconds());	
		}
			
		@Override
		public void addTuioObject(TuioObject tuioObject) {
					
		}
		
		@Override
		public void updateTuioObject(TuioObject tuioObject) {
				
		}
		
		@Override
		public void removeTuioObject(TuioObject tuioObject) {
			
		}
	}
	
	private void update_drag(Point point, int dX, int dY) {
		//are we still dragging inside the MapPanel?
		if (contains(point)) {
			//update the location of the character
			//don't update: charactersOnMap.remove(humanCharacterName);
			Point pCharacter = new Point(point.x-dX, point.y-dY);
			//don't update: charactersOnMap.put(humanCharacterName, pCharacter);
			humanRectangle.setLocation(pCharacter);
			
			//check if this drag is allowed or not
			//iterate over all locations
			Iterator<String> itLoc = locationsOnMap.keySet().iterator();
			while(itLoc.hasNext()) {
				String location = itLoc.next();
				Point pLocation = locationsOnMap.get(location);
				
				//check if we are dragging above this particular location
				if (humanRectangle.contains(pLocation)) {
					//we found above which location the character is dragged
					
					//check if moving to this location is a possible action
					//also selects corresponding action in selectionList if allowed
					//or clears selection in selectionList if not a possible destination
					possibleDestination = mapFrame.tryPossibleDestination(location);
					repaint();
					return;
				}
			}
		}
		//we are not dragging above a location at all, so never possible
		possibleDestination = -1;
		mapFrame.clearSelectionList();
		repaint();
	}
}
