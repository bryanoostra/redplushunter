package vs.characteragent.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

import hmi.tts.AbstractTTSGenerator;
import hmi.tts.sapi5.SAPI5TTSGenerator;

public class InterfacePanel extends JPanel {
	
	//the approximate size of a human finger
	public static final int FINGER_SIZE = 60;
	
	//values for wrapping text of StoryArea 
	public static final int WRAP_TRESHOLD = 40;
	public static final int WRAP_MIN_FOR_NEW_LINE = 7;
	public static final String WRAP_PADDING = "     ";
	
	//the maximum number of instances of StoryArea
	public static final int MAX_NUMBER_OF_STORYAREAS = 3;
	
	//text to be used for DoNothing 'action'
	//public static final String DO_NOTHING = "Do nothing";
	//public static final String SKIP_A_ROUND = "Skip a round";
	public static final String DO_NOTHING = "Doe niets";
	public static final String SKIP_A_ROUND = "Sla een ronde over";
	
	//whether or not the text should be spoken out
	public static final boolean USE_SPEECH_GENERATION = true;
	
	//half of the default interface width
	public static final double HALF_DEFAULT_INTERFACE_WIDTH = 150;
	
	//the width to be used for left and right margins around TextLayouts
	public static final double TYPE_TEXT_MARGIN = 100;
	public static final double ACTION_TEXT_MARGIN = 35;
	public static final double STORY_TEXT_MARGIN = 35;
	
	//the width between the types that should be left open (currently small vertical yellow line)
	public static final double EMPTY_MARGIN = 20;
	
	//the default thickness of lines
	public static final int INTERFACE_STROKE_SIZE = 4;
	public static final BasicStroke BASIC_STROKE = new BasicStroke(INTERFACE_STROKE_SIZE);
	public static final BasicStroke BASIC_STROKE_ROUNDED = new BasicStroke(INTERFACE_STROKE_SIZE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final BasicStroke FAT_STROKE_ROUNDED = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static final BasicStroke OUTER_INTERFACE_STROKE = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	//Fonts
	public static final Font ACTIONS_FONT = new Font("Arial", Font.PLAIN , 100);
	public static final Font STORY_FONT = new Font("Times New Roman", Font.PLAIN , 100);
	
	//Colors
	public static final Color SKIN_COLOR = Color.PINK;
	public static final Color TRANSPARENT_SKIN_COLOR = new Color(SKIN_COLOR.getRed(), SKIN_COLOR.getGreen(), SKIN_COLOR.getBlue(), 200);
	public static final Color ACTION_COLOR = Color.YELLOW;
	public static final Color STORY_COLOR = Color.WHITE;
	public static final Color EXECUTE_COLOR = Color.GREEN;
	public static final Color TEXT_COLOR = Color.BLACK;
	public static final Color BORDER_COLOR = Color.BLACK;
	public static final Color INTERFACE_BACKGROUND_COLOR = new Color(0,0,0,0); //transparent!
	//alternative: private static final Color INTERFACE_BACKGROUND_COLOR = Color.WHITE;
	
	//show actions(text) on both sides of interface? default=both
	private boolean[] showActions = {true, true};
	
	//handles aka fingerprints
	private Ellipse2D p1 = null; //first handle/fingerprint of the interfacePanel
	private Ellipse2D p2 = null; //second handle/fingerprint of the interfacePanel
	
	//used to show location of last registrated touchpoint
	private Ellipse2D debugLastPoint = null;
	
	//dynamic colors (changed in updateColors())
	private Color backgroundActionPossible;
	private Color backgroundActionSelected;
	private Color backgroundTypePossible;
	private Color backgroundTypeSelected;
	private Color backgroundExecuteButton;
	private Color textExecuteButton; //currently no text/icon on executeButton, but might change 
	private Color textTypePossible;
	private Color textNormal;
			
	//some action height values
	private double ascentActions;
	private double descentActions;
	private double lineActionsHeight;
		
	//some story height values
	private double ascentStory;
	private double descentStory;
	private double lineStoryHeight;
	
	//some width values
	private double maxActionWidth;
	private double maxStoryWidth;
	private double maxWidth;
	private double sumTypesWidth;
	
	//some general height values
	private double lineSpacing;
	private double halfStripHeight;
	
	//index of the currently selected type (there is always a type selected, default=0)
	private int indexOfSelectedType = 0;
	
	//index of the currently selected action (-1 means no action selected)
	//always autoselect the first action (there is always an action selected, default index 0)
	private int indexOfSelectedAction = 0;
	
	//index of the 'DoNothing' type, needed because these actions are not ordinairy actions
	private int indexOfTypeDoNothing;
	
	//store AffineTransforms corresponding to both sides of interface, needed for event handling
	private AffineTransform atA = new AffineTransform();
	private AffineTransform atB = new AffineTransform();
	
	//store rectangles of actions corresponding to both sides of interface, needed for event handling
	private Vector<RectangularShape> rectsActionsA = new Vector<RectangularShape>();
	private Vector<RectangularShape> rectsActionsB = new Vector<RectangularShape>();
	
	//store rectangles of all types, needed for event handling
	private Vector<RectangularShape> rectsTypes = new Vector<RectangularShape>();
	
	//store rectangles of all buttons, needed for event handling
	private Vector<RectangularShape> rectsButtons = new Vector<RectangularShape>();
	
	//TextLayouts of types and actions
	private Vector<TextLayout> txtTypes = new Vector<TextLayout>();
	private Vector<Vector<TextLayout>> txtActionsByType = new Vector<Vector<TextLayout>>();

	//possibleActionsByType only stored for possible debug purposes
	private Hashtable<String, Vector<String>> possibleActionsByType = new Hashtable<String, Vector<String>>();

	//linesAll with all lines, grouped by round, is public for debug purposes
	public Vector<Vector<String>> linesAll = new Vector<Vector<String>>();
	
	//TextLayouts of all lines of the story so far
	private Vector<TextLayout> txtStory = new Vector<TextLayout>();
	
	//all existing StoryAreas
	private Vector<StoryArea> storyAreas = new Vector<StoryArea>();
		
	//FontRenderContext and FontMetrics, frc is public for debug purposes
	public FontRenderContext frc;
	private FontMetrics fm;
	
	//TextLayout of message if there are no possible actions to choose
 	private TextLayout tlMessage;
	
 	//is the system waiting for input from the user? --> colors updated, interface enabled
	private boolean waitingForUserInput;
	
	//the round number from which all operator results 
	//with a higher round numbers should be emphasized BOLD
	private int operatorResultBOLDfromRoundNumber;
		
	//images of a fingerprint and a parchment roll
	public static Image imgFingerprint;
	public static Image imgRoll;

	//rectangles left and right of the currently selected type, used to draw a nice border
	private Rectangle2D.Double rectLeft;
	private Rectangle2D.Double rectRight;
	
	//text-to-speech generator
	private AbstractTTSGenerator ttsG = new SAPI5TTSGenerator();

	//reference back to owner/parent
	private MapFrame mapFrame;
	
	private static final long serialVersionUID = -5527593667900416121L;

	/**
	 * Constructor for InterfacePanel
	 * Basic init and loads some images (parchmentroll, fingerprint)
	 * @param mapFrame reference back to the owner/parent
	 */
	public InterfacePanel(MapFrame mapFrame) {
		
		//super(null); 
		this.mapFrame = mapFrame;
        setOpaque(false);
                
        try {
        	String fileRoll = "img\\interactive_map\\parchment_roll.PNG";
        	imgRoll = ImageIO.read(new File(fileRoll));
        	
        	String fileFingerPrint = "img\\interactive_map\\fingerprint.gif";
        	imgFingerprint = ImageIO.read(new File(fileFingerPrint));
        	
        	if (imgFingerprint!=null && (imgFingerprint.getWidth(null) > imgFingerprint.getHeight(null))) {
        		imgFingerprint = imgFingerprint.getScaledInstance((int)(/*.9**/ FINGER_SIZE), -1, 0);
        	} else {
        		imgFingerprint = imgFingerprint.getScaledInstance(-1, (int)(/*.9**/ FINGER_SIZE), 0); 
        	}
        } catch(IOException ioe) {
        	imgFingerprint = null;
        }
	}
	
	/**
	 * first/main method for painting interface
	 */
	protected void paintComponent(Graphics g) {
		//is cloning Graphics needed here?
		//yes, java API: "If you override this method in a subclass you should not make permanent changes to the passed in Graphics"
		Graphics2D g2d = (Graphics2D)g.create();
		
		//set some rendering hints (but only the useful ones :-))
    	g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    	//g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    	//g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, null);
    	//g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, new Integer(140));
    	//g2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, new Integer(180));
    	//g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    	//g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    	//g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
    	//check if this is the first time and we need to initialize
    	if(frc==null || fm==null) {
    		//create FontRenderContext frc
    		g2d.setFont(ACTIONS_FONT);
    		fm = g2d.getFontMetrics();
    		frc = g2d.getFontRenderContext();
    		//the first time the interfacePanel is painted, there are no possible actions
    		//Hashtable<String, Vector<String>> noPossibleActions = new Hashtable<String, Vector<String>>();
    		updatePossibleActions(new Vector<String>(), new Vector<Vector<String>>());
    		//make sure the story displays a message
    		recalculateStory();
    	} else {    	
    		//(re)create FontRenderContext and FontMetrics (needed, or just to be sure?)
    		g2d.setFont(ACTIONS_FONT);
    		//frc = g2d.getFontRenderContext();
    		//fm = g2d.getFontMetrics();
    	}
    	
    	//paint all instances of StoryArea before main interface is painted
    	paintStoryAreas(g2d);
    	
    	//paint the main interface above everything so far
    	paintInterface(g2d);
    	    	  	
    	//debug: printing last touched point
    	if(true && debugLastPoint!=null) {
//    		if(imgFingerprint != null) {
//				int xFingerprint = (int)debugLastCursor.getX() + (FINGER_SIZE - imgFingerprint.getWidth(null))/2;
//				int yFingerprint = (int)debugLastCursor.getY() + (FINGER_SIZE - imgFingerprint.getHeight(null))/2;
//				g2d.drawImage(imgFingerprint, xFingerprint, yFingerprint, null);
//			}
    		g2d.setColor(SKIN_COLOR);
			g2d.draw(debugLastPoint);
    	}
	}
    
	/**
	 * paints all StoryAreas on the provided Graphics2D environment
	 * @param g2d the Grapics environment to draw on
	 */
	private void paintStoryAreas(Graphics2D g2d) {
		//paint in reversed order because oldest StoryArea should be on top of others 
		for (int index = storyAreas.size()-1; index >= 0; index--) {
			StoryArea storyArea = storyAreas.get(index);
			//remove storyArea when too small, not dragged, and there is at least 1 other StoryArea
			if (storyArea.isTooSmallAndNotDragged() && storyAreas.size()>1) {
				//XXX: this can generate concurrent modification exception
				//because event handlers iterate over it, TODO: FIXME
				storyAreas.remove(storyArea);
			} else {
				//draw this StoryArea 
				storyArea.paintYourself(g2d, txtStory, maxStoryWidth);
			}
		}
	}
	
	/**
	 * responsible for the fact that main interface gets painted
	 * @param g2d
	 */
    private void paintInterface(Graphics2D g2d) {
    	if(p1!=null && p2 !=null) {
	    	
	    	//paint p1 and p2 handles
			g2d.setColor(TRANSPARENT_SKIN_COLOR);
			g2d.fill(p1);
			g2d.fill(p2);
	    	
	    	//draw the fingerprints
	    	if(imgFingerprint!=null) {
	    		int x1Fingerprint = (int)p1.getX() + (FINGER_SIZE - imgFingerprint.getWidth(null))/2;
				int y1Fingerprint = (int)p1.getY() + (FINGER_SIZE - imgFingerprint.getHeight(null))/2;
				g2d.drawImage(imgFingerprint, x1Fingerprint, y1Fingerprint, null);
	    		int x2Fingerprint = (int)p2.getX()+ (FINGER_SIZE - imgFingerprint.getWidth(null))/2;
				int y2Fingerprint = (int)p2.getY()+ (FINGER_SIZE - imgFingerprint.getHeight(null))/2;
				g2d.drawImage(imgFingerprint, x2Fingerprint, y2Fingerprint, null);
	    	}
	    		        	
	    	//create a new AffineTransform
			atA = new AffineTransform();
			
			//calculate slope and set rotation
	    	double slope = Math.atan2(p2.getCenterY() - p1.getCenterY(), p2.getCenterX() - p1.getCenterX());
	    	atA.rotate(slope, p1.getCenterX(), p1.getCenterY());
	    	
	    	//calculate diagonal
	    	double diagonal = Point.distance(p1.getCenterX(), p1.getCenterY(), p2.getCenterX(), p2.getCenterY()) - FINGER_SIZE;
	    	
	    	//width should (at least) be equal to the width of all the types of actions and the ovals together
			maxWidth = sumTypesWidth + EMPTY_MARGIN + 2*halfStripHeight + EMPTY_MARGIN + halfStripHeight;
			
			//constraint: width should at least be equal to sumActionsHeight
			double sumActionsHeight = 0;
			Vector<TextLayout> txtActionsOfSelectedType = txtActionsByType.get(indexOfSelectedType);
			if (txtActionsOfSelectedType!=null) {
				sumActionsHeight = lineActionsHeight * txtActionsOfSelectedType.size();
				maxWidth = Math.max(maxWidth, sumActionsHeight);
			}
	   		
	    	//calculate scale and set scaling
	    	double scale = diagonal/maxWidth;
	    	atA.scale(scale, scale);
	    	
	    	//perform actual rotation and scaling on Graphics2D 
	    	g2d.setTransform(atA);	        	
	    					
	    	//calculate the needed coordinates of p1 in the scaled Graphics context
	    	double scaledX = (1/scale) * (p1.getCenterX() + .5*FINGER_SIZE);
	    	double scaledY = (1/scale) * (p1.getCenterY());
	    	
	    	double currx = scaledX;
	    	double curry = scaledY - halfStripHeight - EMPTY_MARGIN;
	    	
	    	//calculate totalHeight and curry
	    	double totalHeight = 2*halfStripHeight + 2*EMPTY_MARGIN;
	    	if (showActions[0]) {
	    		//totalHeight = totalHeight + emptyMargin + sumActionsHeight;
	    		totalHeight = totalHeight + sumActionsHeight;
	    	}
	    	if (showActions[1]) {
	    		//curry = curry - emptyMargin - sumActionsHeight;
	    		//totalHeight = totalHeight + emptyMargin + sumActionsHeight;
	    		curry = curry - sumActionsHeight;
	    		totalHeight = totalHeight + sumActionsHeight;
	    	}
	    	
	    	//create rectangle of complete interface and paint (optional) background, defaultColor=transparent
	    	Rectangle2D rectInterface = new Rectangle2D.Double(currx, curry, maxWidth, totalHeight);
	    	g2d.setColor(INTERFACE_BACKGROUND_COLOR);
	    	g2d.fill(rectInterface);
	    	
	    	//paint in the center the types and buttons of the 'commandButtonStrip'
	    	paintCommandButtonStrip(g2d, scaledX, scaledY);
	    	
	    	//reset currx and curry
	    	currx = scaledX;
	    	curry = scaledY + halfStripHeight + EMPTY_MARGIN;
	    	
	    	//paint actions and story upside-up and store rectangles in shapesA
	    	rectsActionsA = paintActions(g2d, currx, curry, showActions[0]);
	    	
	    	//rotate coordinate system 180 degrees
	    	atB = new AffineTransform(atA);
	    	atB.rotate(Math.PI, scaledX, scaledY);
	    	g2d.setTransform(atB);
	    	
	    	//reset currx and curry
	    	currx = scaledX - maxWidth;
	    	curry = scaledY + halfStripHeight + EMPTY_MARGIN;
	    	
	    	//paint actions and story upside-down and store rectangles in shapesB
	    	rectsActionsB = paintActions(g2d, currx, curry, showActions[1]);
	    	
	    	//reset transform
	    	g2d.setTransform(atA);
	    	
	    	//set color and stroke
	    	g2d.setColor(BORDER_COLOR);
	    	g2d.setStroke(BASIC_STROKE);
	    	
	    	//draw rectangles left and right of currently selected type, looks nice :)
	    	if(rectLeft!=null)g2d.draw(rectLeft);
			if(rectRight!=null)g2d.draw(rectRight);
			
			//finally draw border around complete interface
	    	g2d.setStroke(OUTER_INTERFACE_STROKE);
	    	g2d.draw(rectInterface);
	    	
	    	//reset g2d back to empty transform
	    	g2d.setTransform(new AffineTransform());
    	}    	
	}

    /**
     * responsible for painting types and buttons in the 'commandButtonStrip'
     * @param g2d
     * @param scaledX
     * @param scaledY
     */
	private void paintCommandButtonStrip(Graphics2D g2d, double scaledX, double scaledY) {
//     	//possible characters: 
//     	//8 0 O I S H X - + / # % * = ~ : | () {} [] <>
//     	//bad: <> [] {} () : * 8
//     	//available: I S H X O 0 = ~ % # / |
//     	String showMore = "+";
//     	String showLess = "-";
//		String skipRound = "O"; //options: O, 0, /, //, ~, S  
//     	String endStory = "##"; //options: ##, X
//     	String closeInterface = "X";
//     	String readAloud = "%"; //options: %, H 
//     	String spare = "~";
//     	String execute = "="; //options: =, X
//     	//all buttons:
//     	String[] butText = {execute, skipRound, showMore, showLess, endStory, readAloud, spare, demo};
//     	Color[] butColor = {Color.GREEN, Color.CYAN, Color.ORANGE, Color.ORANGE, Color.RED, Color.MAGENTA, Color.LIGHT_GRAY, Color.PINK};
//     	Color[] butTextColor = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
    	
		//update all colors (based on boolean waitingForUserInput)
		updateColors();
		
		//reset the stored rectangles of buttons
     	rectsButtons.clear();
     	
     	//XXX: first check if there are actually some types? hmm, not needed (for now)
	    //if (!txtTypes.isEmpty()) {

	    	//reset the stored rectangles of types
	    	rectsTypes.clear();
	    			    
		    //define currx and curry
	    	double currx = scaledX;
		    double curry = scaledY;
		    
		    //draw the available types of actions
		    for(int typeIndex = 0; typeIndex<txtTypes.size(); typeIndex++) {
	    		TextLayout tlType = txtTypes.get(typeIndex);
	    		//calculate the width of this type
	    		double typeWidth = tlType.getBounds().getWidth();
	    			    		
	    		//create the enclosing rectangle of this type
	    		Rectangle2D rectType = new Rectangle2D.Double(currx, curry-halfStripHeight, TYPE_TEXT_MARGIN + typeWidth + TYPE_TEXT_MARGIN, 2*halfStripHeight);
	    		//alternative=ellipses: Ellipse2D ellipseType = new Ellipse2D.Double(currx, curry-halfStripHeight, TYPE_TEXT_MARGIN + typeWidth + TYPE_TEXT_MARGIN, 2*halfStripHeight);
	    		
	    		//save the enclosing rectangle of this type for mouselistening etc.
	    		rectsTypes.add(rectType);
	    		
	    		//if this type is NOT the currently selected type
	    		if(typeIndex!=indexOfSelectedType) {
	    			//set the background color
	    			g2d.setColor(backgroundTypePossible);
	    			//draw (dark_gray) rectangle
	    			g2d.fill(rectType);
	    			
	    			//draw border around unselected possible types
	    			g2d.setStroke(BASIC_STROKE);
	    			g2d.setColor(BORDER_COLOR);
	    			g2d.draw(rectType);
	    			//g2d.draw(ellipseType);
	    				    			
	    			//do (not) make sure it is a bit larger than the text it will contain :-)
	    			///g2d.draw(rectType);
	    			
	    			//set the color for the text
	    			g2d.setColor(textTypePossible);
	    		} 
	    		//if this type IS the currently selected type:
	    		else { 
	    			//create and store the rectangles left and right of it
	    			rectLeft = new Rectangle2D.Double(scaledX, scaledY-halfStripHeight-EMPTY_MARGIN, currx - scaledX, 2*halfStripHeight + 2*EMPTY_MARGIN);
	    			rectRight = new Rectangle2D.Double(currx+rectType.getWidth(), rectLeft.y, maxWidth - ((currx - scaledX)+rectType.getWidth()), rectLeft.height);
	    			
	    			//calculate rectangle of the selected type itself
	    			Rectangle2D.Double rectSelectedType = new Rectangle2D.Double(currx, curry-halfStripHeight-EMPTY_MARGIN, TYPE_TEXT_MARGIN + typeWidth + TYPE_TEXT_MARGIN, 2*halfStripHeight + 2*EMPTY_MARGIN);
	    			if (showActions[0] && showActions[1]) {
	    				rectSelectedType.y = rectSelectedType.y - lineActionsHeight;
	    				rectSelectedType.height = rectSelectedType.height + 2*lineActionsHeight;
	    			} else if (showActions[0] && !showActions[1]) {
	    				rectSelectedType.height = rectSelectedType.height + lineActionsHeight;
	    			} else if (!showActions[0] && showActions[1]) {
	    				rectSelectedType.y = rectSelectedType.y - lineActionsHeight;
	    				rectSelectedType.height = rectSelectedType.height + lineActionsHeight;
	    			} else if (!showActions[0] && !showActions[1]) {
	    				//nothing changes
	    			}
	    			
	    			//set the background color
	    			g2d.setColor(backgroundTypeSelected);
	    			
	    			//draw selected background
	    			g2d.fill(rectSelectedType);

	    			//set the color for the text
	    			g2d.setColor(textNormal);
	    		}
	    		
	    		//draw (rounded/squared) arrows if actions are showed on both sides of interface
	    		if (showActions[0] && showActions[1]) {
		    		//g2d.setStroke(FAT_ROUNDED_STROKE);
		    		g2d.setStroke(BASIC_STROKE);
		    		g2d.drawArc((int)(currx+.5*TYPE_TEXT_MARGIN), (int)(curry-lineActionsHeight/2), (int)(.6*TYPE_TEXT_MARGIN), (int)lineActionsHeight, 90, 180);
		    		//g2d.drawArc((int)(currx+.3*TYPE_TEXT_MARGIN), (int)(curry-lineActionsHeight/2), (int)(1.0*TYPE_TEXT_MARGIN), (int)lineActionsHeight, 90, 180);
		    		Polygon p1 = new Polygon();
		    		p1.addPoint((int)(currx+.9*TYPE_TEXT_MARGIN), (int)(curry-lineActionsHeight/2));
		    		p1.addPoint((int)(currx+.7*TYPE_TEXT_MARGIN), (int)(curry-lineActionsHeight/2-.2*TYPE_TEXT_MARGIN));
		    		p1.addPoint((int)(currx+.7*TYPE_TEXT_MARGIN), (int)(curry-lineActionsHeight/2+.2*TYPE_TEXT_MARGIN));
		    		g2d.fillPolygon(p1);
		    		
		    		g2d.drawArc((int)(currx+typeWidth+0.9*TYPE_TEXT_MARGIN), (int)(curry-lineActionsHeight/2), (int)(.6*TYPE_TEXT_MARGIN), (int)lineActionsHeight, 270, 180);
		    		//g2d.drawArc((int)(currx+typeWidth+0.7*TYPE_TEXT_MARGIN), (int)(curry-lineActionsHeight/2), (int)(1.0*TYPE_TEXT_MARGIN), (int)lineActionsHeight, 270, 180);
		    		Polygon p2 = new Polygon();
		    		p2.addPoint((int)(currx+typeWidth+1.1*TYPE_TEXT_MARGIN), (int)(curry+lineActionsHeight/2));
		    		p2.addPoint((int)(currx+typeWidth+1.3*TYPE_TEXT_MARGIN), (int)(curry+lineActionsHeight/2-.2*TYPE_TEXT_MARGIN));
		    		p2.addPoint((int)(currx+typeWidth+1.3*TYPE_TEXT_MARGIN), (int)(curry+lineActionsHeight/2+.2*TYPE_TEXT_MARGIN));
		    		g2d.fillPolygon(p2);
	    		}
	    		
	    		//draw textlayout UPSIDE-UP
	    		if (showActions[0]) {
		    		//draw textlayout of this type (with some manual adjustments in placing!)
	    			tlType.draw(g2d, (float)(currx + TYPE_TEXT_MARGIN - 10), (float)(curry - lineActionsHeight + ascentActions ));
	    			//g2d.drawString(txtType, (int)(currx + TEXT_MARGIN - 10), (int)(curry - halfStripHeight + ascentActions - 5));
	    			
	    			//ROUNDED TEXT!?
//	    			if (g2d instanceof Graphics2D) {
//    	            	AffineTransform backupAT = g2d.getTransform();
//	    	            Point pt = new Point((int)(currx + TEXT_MARGIN + typeWidth / 2), (int)(curry+typeWidth/4));
////	    	            //Point pt = new Point((int)(currx + TEXT_MARGIN - 10), (int)(curry - halfStripHeight + ascentActions - 10));
////	    				Dimension cd = getSize();
////    		            Point pt = new Point(cd.width / 2, cd.height / 2);
////    		            int radius = (int)(pt.x * 0.84);
//	    	            int radius = (int)(typeWidth/2);
////    		            g2d.drawArc(pt.x - radius, pt.y - radius, radius*2-1, radius*2-1, 0, 360);
//    		            g2d.setFont(ACTIONS_FONT);
//	    	            //double angle = Math.atan2(p2.getCenterY() - p1.getCenterY(), p2.getCenterX() - p1.getCenterX());
////    		            drawCircleText((Graphics2D)g2d, txtType, pt, radius, angle, 1.0);
//    		            drawCircleText((Graphics2D)g2d, txtType, pt, radius, -Math.PI/4, 1.0);
////	    	            drawCircleText((Graphics2D)g2d, txtType, pt, radius, 0, 1.0);
//	    	            g2d.setTransform(backupAT);
//    		        }
//    		            else {
//    		            System.out.println("Cannot draw curved text without a Graphics2D");
//    		        }
	    			
	    		}
	    		//draw textlayout UPSIDE-DOWN
	    		if (showActions[1]) {
	    			//backup Transform
	    			AffineTransform atBackup = g2d.getTransform();
	    			
	    			//rotate Graphics2D
	    	    	g2d.rotate(Math.PI, currx, curry);
		    		
	    	    	//draw textlayout of this type (with some manual adjustments in placing!)
	    	    	tlType.draw(g2d, (float)(currx - TYPE_TEXT_MARGIN - typeWidth - 5), (float)(curry - lineActionsHeight + ascentActions ));
	    	    	//g2d.drawString(txtType, (float)(currx - TEXT_MARGIN - typeWidth - 5), (float)(curry - halfStripHeight + ascentActions - 5));
	    	    	
	    			//ROUNDED TEXT!?
//	    			if (g2d instanceof Graphics2D) {
////    	            	AffineTransform backupAT = g2d.getTransform();
//	    	            Point pt = new Point((int)(currx - TEXT_MARGIN - typeWidth / 2), (int)(curry+typeWidth/4));
////	    	            //Point pt = new Point((int)(currx + TEXT_MARGIN - 10), (int)(curry - halfStripHeight + ascentActions - 10));
////	    				Dimension cd = getSize();
////    		            Point pt = new Point(cd.width / 2, cd.height / 2);
////    		            int radius = (int)(pt.x * 0.84);
//	    	            int radius = (int)(typeWidth/2);
////    		            g2d.drawArc(pt.x - radius, pt.y - radius, radius*2-1, radius*2-1, 0, 360);
//    		            g2d.setFont(ACTIONS_FONT);
//	    	            //double angle = Math.atan2(p2.getCenterY() - p1.getCenterY(), p2.getCenterX() - p1.getCenterX());
////    		            drawCircleText((Graphics2D)g2d, txtType, pt, radius, angle, 1.0);
//    		            drawCircleText((Graphics2D)g2d, txtType, pt, radius, -Math.PI/4, 1.0);
////	    	            drawCircleText((Graphics2D)g2d, txtType, pt, radius, 0, 1.0);
////	    	            g2d.setTransform(backupAT);
//    		        }
//    		            else {
//    		            System.out.println("Cannot draw curved text without a Graphics2D");
//    		        }
	    	    		    	    	
	    			//restore Transform
	    			g2d.setTransform(atBackup);
	    		}
	    		//update the current x
	    		currx = currx + TYPE_TEXT_MARGIN + typeWidth + TYPE_TEXT_MARGIN + EMPTY_MARGIN;
	    		
	    	}//end for-loop (done iterating all types)
		    
/////// DRAW BUTTONS of command button strip /////////////////////////////////////////////////////////////////
	    	
	    	//width execute button = height execute button = height command button strip 
    		Ellipse2D greenOval = new Ellipse2D.Double(currx, curry-halfStripHeight, 2*halfStripHeight, 2*halfStripHeight);
     		
    		//add to Vector for future mouselistening
     		rectsButtons.add(greenOval);
     		
     		//paint background color of execute button
     		g2d.setColor(backgroundExecuteButton);
     		g2d.fill(greenOval);
     			
     		//paint border of execute button
     		g2d.setStroke(BASIC_STROKE);
     		g2d.setColor(BORDER_COLOR);
     		g2d.draw(greenOval);
     		
     		//draw the = symbol on the execute button, turned off for now...
//     		g2d.setColor(textExecuteButton);
//     		g2d.fillRect((int)(currx+.5*halfStripHeight), (int)(curry-.3*halfStripHeight), (int)(1.0*halfStripHeight), (int)(.6*halfStripHeight));
//     		g2d.setColor(backgroundExecuteButton);
//     		g2d.fillRect((int)(currx+.2*halfStripHeight), (int)(curry-.15*halfStripHeight), (int)(1.6*halfStripHeight), (int)(.3*halfStripHeight));
     		    		
     		//update the current x
     		currx = currx + 2*halfStripHeight + EMPTY_MARGIN - INTERFACE_STROKE_SIZE;
     		double curryA = curry + EMPTY_MARGIN - INTERFACE_STROKE_SIZE;
     		double curryB = curry - EMPTY_MARGIN + INTERFACE_STROKE_SIZE;
     			
    		//define size of small yellow ovals
    		Ellipse2D yellowOvalA = new Ellipse2D.Double(currx, curryA,                 halfStripHeight, halfStripHeight);
    		Ellipse2D yellowOvalB = new Ellipse2D.Double(currx, curryB-halfStripHeight, halfStripHeight, halfStripHeight);
    		//alternative: use square buttons?
    		//Rectangle2D yellowOvalA = new Rectangle2D.Double(currx, curryA,                 halfStripHeight, halfStripHeight);
    		//Rectangle2D yellowOvalB = new Rectangle2D.Double(currx, curryB-halfStripHeight, halfStripHeight, halfStripHeight);
    		    		
    		//add to Vector for future mouselistening
    		rectsButtons.add(yellowOvalA);
			rectsButtons.add(yellowOvalB);
			
			//paint background color of small yellow ovals
    		g2d.setColor(ACTION_COLOR);
			g2d.fill(yellowOvalA);
			g2d.fill(yellowOvalB);
			
			//paint border of small yellow ovals
			g2d.setStroke(BASIC_STROKE);
			g2d.setColor(BORDER_COLOR);
			g2d.draw(yellowOvalA);
			g2d.draw(yellowOvalB);

			//set stroke for drawing plus and minus symbols 
			g2d.setStroke(FAT_STROKE_ROUNDED);
			
			//draw two minus signs
			g2d.drawLine((int)(currx + .2*halfStripHeight), (int)(curryA + .5*halfStripHeight), (int)(currx + .8*halfStripHeight), (int)(curryA + .5*halfStripHeight));
			g2d.drawLine((int)(currx + .2*halfStripHeight), (int)(curryB - .5*halfStripHeight), (int)(currx + .8*halfStripHeight), (int)(curryB - .5*halfStripHeight));
			
			//calculate xVertical for plus symbols
			int xVertical = (int)(currx + .5*halfStripHeight);
			
			//when types and actions not shown...
			if (!showActions[0]) {
				//...draw vertical to make the minus a plus sign
				g2d.drawLine(xVertical, (int)(curryA + .8*halfStripHeight), xVertical, (int)(curryA + .2*halfStripHeight));
			}
			//when types and actions not shown...
			if (!showActions[1]) {
				//...draw vertical to make the minus a plus sign
				g2d.drawLine(xVertical, (int)(curryB - .8*halfStripHeight), xVertical, (int)(curryB - .2*halfStripHeight));
			}
	    //}
	}

//    /**
//    * Draw a piece of text on a circular curve, one
//    * character at a time. This is harder than it looks.
//    */
//    static void drawCircleText(Graphics2D g, String st, Point center, double r, double a1, double af) {
//    	
//    	AffineTransform atBackup = g.getTransform();
//        double curangle = a1;
//        Point2D c = new Point2D.Double(center.x, center.y);
//        char ch[] = st.toCharArray();
//        
//        FontMetrics fm = g.getFontMetrics();
//        
//        //fm.getStringBounds(str, context);
//        //fm.stringWidth(str);
//        
//        AffineTransform xform1, cxform;
//        xform1 = AffineTransform.getTranslateInstance(c.getX(),c.getY());
//            for(int i = 0; i < ch.length; i++) {
//            double cwid = (double)(getWidth(ch[i],fm));
//                if (!(ch[i] == ' ' || Character.isSpaceChar(ch[i]))) {
//                cwid = (double)(fm.charWidth(ch[i]));
//                cxform = new AffineTransform(xform1);
//                cxform.rotate(curangle, 0.0, 0.0);
//                String chstr = new String(ch, i, 1);
//                g.setTransform(atBackup);
//                g.transform(cxform);
//                g.drawString(chstr, (float)(-cwid/2), (float)(-r));
//            }
//            
//            // compute advance of angle assuming cwid<
//                if (i < (ch.length - 1)) {
//                double adv = cwid/2.0 + fm.getLeading() + getWidth(ch[i + 1],fm)/2.0;
//                curangle += Math.sin(adv / r);
//            }
//        }
//    }
//	
//	    static int getWidth(char c, FontMetrics fm) {
//            if (c == ' ' || Character.isSpaceChar(c)) {
//            	return fm.charWidth('n');
//            } else {
//            	return fm.charWidth(c);
//            }
//	    }
	
	/**
	 * responsible for painting list of actions on one side of interface at the time
	 * @result a Vector with rectangles which serve as hit-targets in event handling
	 */
	private Vector<RectangularShape> paintActions(Graphics2D g2d, double currx, double curry, boolean showActions) {
		
		//create Vector that will contain rectangularShapes of Actions of currently selected type
		Vector<RectangularShape> rectsActions = new Vector<RectangularShape>();
    	
    	//when actions should be displayed on this side of the interface
		if (showActions) {
			
			//first check if there are actually actions to choose, not needed (for now)
		    ///if (!txtTypes.isEmpty() && !txtActionsByType.isEmpty()) {
		    	
		    	//calculate (and draw) the area needed to display the actions of the selected type
		    	Vector<TextLayout> txtActionsOfSelectedType = txtActionsByType.get(indexOfSelectedType);
		    	int numberOfActionsOfSelectedType = txtActionsOfSelectedType.size();
		    	Rectangle2D rectActions = new Rectangle2D.Double(currx, curry, maxWidth, lineActionsHeight*numberOfActionsOfSelectedType);
		    	g2d.setColor(backgroundActionPossible);
		    	g2d.fill(rectActions);
		    	
		    	//draw the actions of the selected type
		    	for(int i = 0; i<numberOfActionsOfSelectedType; i++) {
		    		TextLayout tlAction = txtActionsOfSelectedType.get(i);
		    		//save the bounds of the this action
		    		Rectangle2D.Double rectAction = new Rectangle2D.Double(currx, curry, maxWidth, lineActionsHeight);
		    		rectsActions.add(rectAction);
		    		
		    		//change rectangle if larger than interface
		    		double realWidth = ACTION_TEXT_MARGIN + tlAction.getBounds().getWidth() + ACTION_TEXT_MARGIN;
		    		if(realWidth>maxWidth) {
		    			rectAction = new Rectangle2D.Double(currx, curry, realWidth, lineActionsHeight);
		    			g2d.setColor(backgroundActionPossible);
		    			g2d.fill(rectAction);
		    		}
		    		
		    		//make the currently selected action stand out by lighting it up with a green glow :) 
		    		if(i == indexOfSelectedAction) {
						g2d.setColor(backgroundActionSelected); //green glow!? :)
						g2d.fill(rectAction);		
						//black border around selected action
						//g2d.setColor(borderColor);
						//g2d.draw(rect);
					}
		    		
			        curry = curry + ascentActions;
			        g2d.setColor(textNormal);
			        //draw textlayout of this action (with some manual adjustments in placing!)
					tlAction.draw(g2d, (float)(currx + ACTION_TEXT_MARGIN), (float)curry-5);
					curry = curry + descentActions;
		    	}
			
		    ///} else {
		    	//draw TextLayout with 'no-actions'-message
		    	//optional: todo display message
			///}
			
		}
		return rectsActions;
	}
	
	/**
	 * updates the colors of interface elements (largely based on boolean waitingForUserInput)
	 */
	private void updateColors() {
		if(waitingForUserInput) { 
			backgroundActionPossible = ACTION_COLOR;
			backgroundActionSelected = new Color(0f, 1f, 0f, .5f); //this is the green 'glow' for selected types and actions
			backgroundTypePossible = Color.DARK_GRAY;
			backgroundTypeSelected = ACTION_COLOR;
			textNormal = TEXT_COLOR;
			textTypePossible = ACTION_COLOR;
			//when no action is selected, the execute button should be gray
			if(indexOfSelectedAction == -1) {
	     		backgroundExecuteButton = Color.LIGHT_GRAY;
	     		textExecuteButton = Color.GRAY;
	     	} else {
	     		backgroundExecuteButton = EXECUTE_COLOR;
	     		textExecuteButton = TEXT_COLOR;
	     	}
		} else {
			backgroundActionPossible = Color.LIGHT_GRAY;
			backgroundActionSelected = Color.GRAY;
			backgroundTypePossible = Color.DARK_GRAY;
			backgroundTypeSelected = Color.GRAY;
			backgroundExecuteButton = Color.LIGHT_GRAY;
     		textNormal = Color.DARK_GRAY;
			textTypePossible = Color.GRAY;
			textExecuteButton = Color.GRAY;
		}
	}

	/**
	 * recalculates the visual part of the story
	 * 
	 * This method was originally used to create 
	 * a Vector of TextLayouts for the last n rounds.
	 * Currently it always starts at round 1 (first=1)
	 * 
	 * Recalculation is currently needed for generating
	 * BOLD font for new operator results...
	 *  
	 * (public for debug purposes)
	 */
	public void recalculateStory() {
		//reset vector for TextLayouts of story
    	txtStory.clear();
    	
    	//reset the maxStoryWidth
    	maxStoryWidth = 0.0;
    	
    	int firstRound = 1;   	    	
    	int lastRound = linesAll.size();
    	for (int round = firstRound; round<=lastRound; round++) {
    		Vector<String> linesRoundI = linesAll.get(round-1);
			Iterator<String> it = linesRoundI.iterator();
			while(it.hasNext()) {
				//retrieve the full sentence
				String textNotYetDisplayed = it.next();

				//word wrap this sentence
				boolean doneWrapping = false;
				while (!doneWrapping) {
				
					//create String for next line to be printed
					String line;
					
					//Remainder of sentence too long for one line?
					if(textNotYetDisplayed.length() > WRAP_TRESHOLD) {
						
						//find index of last space before treshold
						String hardCrop = textNotYetDisplayed.substring(0, WRAP_TRESHOLD);
						int index = hardCrop.lastIndexOf(" ");
						
						//if there _is_ a space AND if 'behind' this index, there is more text left than the minimum amount of text for a new line 
						if(index > WRAP_PADDING.length() && WRAP_MIN_FOR_NEW_LINE < textNotYetDisplayed.substring(index).length()) {
							//introduce a line wrap
							line = textNotYetDisplayed.substring(0,index);
							textNotYetDisplayed = WRAP_PADDING + textNotYetDisplayed.substring(index);
						} else {
							//to litlle text for a new line, so print on this line
							line = textNotYetDisplayed;
							doneWrapping = true;
						}
					} else {
						//smaller, so just display everything on this line
						line = textNotYetDisplayed;
						doneWrapping = true;
					}
					
					TextLayout tl;
					if(round>operatorResultBOLDfromRoundNumber) {
						tl = new TextLayout(line, STORY_FONT.deriveFont(Font.BOLD), frc);
					} else {
						tl = new TextLayout(line, STORY_FONT.deriveFont(Font.ITALIC), frc);
					}
		        	txtStory.add(tl);
		        	double lineWidth = tl.getBounds().getWidth();
		        	maxStoryWidth = Math.max(maxStoryWidth, lineWidth);
				}
			}
			
//			//currently we don't want to display round numbers, so commented out for now:
//    		String strRound = "---------- Round " + round + " ----------";
//    		TextLayout tlRound = new TextLayout(strRound, STORY_FONT, frc);
//        	txtStory.add(tlRound);
//        	double roundWidth = tlRound.getBounds().getWidth();
//        	maxStoryWidth = Math.max(maxStoryWidth, roundWidth);
		}
    	    	
    	//ensure that there is a message if the story has not started yet
		if(txtStory.isEmpty()) {
			String message = "Waiting for a Plot agent to start the story...";
    		TextLayout tlMessage = new TextLayout(message, STORY_FONT, frc);
    		maxStoryWidth = Math.max(maxStoryWidth, tlMessage.getBounds().getWidth());
        	txtStory.add(tlMessage);
		}
	
		//we ensured there is always a firstElement
		TextLayout tlStory = txtStory.firstElement();
		ascentStory = tlStory.getAscent();
		descentStory = tlStory.getDescent();
		lineStoryHeight = ascentStory + descentStory;		
	}
	
	/**
	 * Called when something happens in the world
	 * @param orRoundNumber the corresponding round number
	 * @param orText the text description
	 * @param success if the operator was successful
	 */
	public void addOperatorResult(int orRoundNumber, String orText, boolean success) {
		
		// Adding text to the text lines.
		if (! "".equals(orText)) {
			//ensure that for all rounds up until orRoundNumber a Vector<String> exists in linesAll
			while (linesAll.size()<orRoundNumber) {
				Vector<String> newVector = new Vector<String>();
				linesAll.add(newVector);
			}
			
			//in the while loop above we ensured that an item at index round-1 exists, get it. 
			Vector<String> linesRound = linesAll.get(orRoundNumber-1);
			//add the text to the story log
			if(success) { 
				//add the text to the story
				linesRound.add(orText);
				
				//recalculate the (visual part) of the story and repaint
				recalculateStory();
				repaint();
				
				//let the TTS speak the text
				//ttsG.speak(orText);
				
			} else {
				//do not show (and don't speak)
				//linesRound.add("failed: " + orText);
			}
		}
	}
	
	/**
	 * are we waiting for input from the user?
	 * @param waiting
	 */
	public void setWaitingForUserInput(boolean waiting) {
		waitingForUserInput = waiting;
		if(waitingForUserInput) {
			operatorResultBOLDfromRoundNumber = linesAll.size();
		}
		repaint();
	}
	
	/**
	 * Called to communicate the size of the map
	 * Sets the size of the InterfacePanel
	 * calculates the position of the main interface
	 * calculates the position of the first StoryArea
	 * @param mapDimension the dimension of the map
	 */
	public void setMapSize(Dimension mapDimension) {
		setPreferredSize(mapDimension);
		
		double mapWidth = mapDimension.getWidth();
		double mapHeight = mapDimension.getHeight();
		
		//determine position of first StoryArea
		//int y = (int)(mapHeight - FINGER_SIZE);
		int y = 250;
		int xStoryMin = FINGER_SIZE;
		int xStoryMax = (int) (mapWidth / 4);

		StoryArea firstStoryArea = new StoryArea(new Point(xStoryMin, y), new Point(xStoryMax, y));
        storyAreas.add(firstStoryArea);

		double centerX = mapWidth/2;
		double centerY = mapHeight/2;
		
		double xP1InterfaceMin = 666-.5*FINGER_SIZE;
		double yP1InterfaceMax = 512-.5*FINGER_SIZE;
		
		double xP2InterfaceMin = 1337-.5*FINGER_SIZE;
		double yP2InterfaceMax = 404-.5*FINGER_SIZE;
		
		//create and set handles/fingerprints
		p1 = new Ellipse2D.Double(xP1InterfaceMin, yP1InterfaceMax, FINGER_SIZE, FINGER_SIZE);
		p2 = new Ellipse2D.Double(xP2InterfaceMin, yP2InterfaceMax, FINGER_SIZE, FINGER_SIZE);
		
		//p1 and p2 changed so recalculate center of interface
		updateCenterOfInterface();
		
		
	}
	
	/**
	 * called to update the currently possible actions
	 * @param typesShort
	 * @param actionsByTypeShort
	 */
	public void updatePossibleActions(Vector<String> typesShort, Vector<Vector<String>> actionsByTypeShort) {
		//reset selection parameters
		indexOfSelectedType = 0;
		indexOfSelectedAction = 0;
		
		//reset message
		tlMessage = null;
		
		//create vector for TextLayouts of types and possible actions
		txtTypes.clear();
    	txtActionsByType.clear();
    	
    	//calculate maxActionsWidth
    	maxActionWidth = 0.0;
    	int maxActionsOfOneSingleType = 0;
    	sumTypesWidth = 0.0;
    	for(int i = 0; i<typesShort.size(); i++) {
    		String type = typesShort.get(i);
    		TextLayout tlType = new TextLayout(type, ACTIONS_FONT, frc);
    		Vector<String> actionsTypeX = actionsByTypeShort.get(i);
    		maxActionsOfOneSingleType = Math.max(maxActionsOfOneSingleType, actionsTypeX.size());
    		Vector<TextLayout> textlayoutsTypeX = new Vector<TextLayout>();
			Iterator<String> itActionsTypeX = actionsTypeX.iterator();
	    	while(itActionsTypeX.hasNext()) {
	    		String action = itActionsTypeX.next();
	    		TextLayout tlAction = new TextLayout(action, ACTIONS_FONT, frc);
	    		textlayoutsTypeX.add(tlAction);
	    		double actionWidth = tlAction.getBounds().getWidth();
	    		maxActionWidth = Math.max(maxActionWidth, actionWidth);
	    	}
	    	if(!textlayoutsTypeX.isEmpty()) {
	    		txtActionsByType.add(textlayoutsTypeX);
	    		txtTypes.add(tlType);
	    		sumTypesWidth += TYPE_TEXT_MARGIN + tlType.getBounds().getWidth() + TYPE_TEXT_MARGIN + EMPTY_MARGIN;
	    	}
    	}
    	
    	//ensure that there is a message if there are no possible actions to choose
    	if(txtActionsByType.isEmpty()) {
    		//construct TextLayout with message
	    	String message = "There are no possible actions to select...";
    		//String message = "There are no actions to select...";
	    	tlMessage = new TextLayout(message, ACTIONS_FONT.deriveFont(Font.ITALIC), frc);
	    	maxActionWidth = Math.max(maxActionWidth, tlMessage.getBounds().getWidth());
    	}
	    
    	//also add a DoNothing option! 
		TextLayout tlTypeDoNothing = new TextLayout(DO_NOTHING, ACTIONS_FONT, frc);
    	TextLayout tlActionDoNothing = new TextLayout(SKIP_A_ROUND, ACTIONS_FONT, frc);
    	Vector<TextLayout> vectorDoNothing = new Vector<TextLayout>();
    	vectorDoNothing.add(tlActionDoNothing);
    	txtActionsByType.add(vectorDoNothing);
		txtTypes.add(tlTypeDoNothing);
		indexOfTypeDoNothing = txtTypes.indexOf(tlTypeDoNothing);
		sumTypesWidth += TYPE_TEXT_MARGIN + tlTypeDoNothing.getBounds().getWidth() + TYPE_TEXT_MARGIN;
		
    	//calculate the height of an action
		ascentActions = tlActionDoNothing.getAscent();
		descentActions = tlActionDoNothing.getDescent();
		//XXX: TODO: implement lineSpacing everywhere
		lineSpacing = 0;
		lineActionsHeight = ascentActions + descentActions + lineSpacing;
		
		//define half the height of the command button strip (for now: equal to the height of an action)
 		halfStripHeight = lineActionsHeight;
     	halfStripHeight = 1.25*lineActionsHeight;
		
     	//repaint to show changes
		repaint();
	}
	
	/**
	 * Sets the indexOfType and indexOfAction of selected action
	 * @param indexOfType
	 * @param indexOfAction
	 */
	public void updateIndexSelectedAction(int indexOfType, int indexOfAction) {
		indexOfSelectedType = indexOfType;
		indexOfSelectedAction = indexOfAction;
		repaint();
	}
	
	/**
	 * Clears the current selected action
	 */
	public void clearSelection() {
		indexOfSelectedAction = -1;
		repaint();
	}
	
	/**
	 * Select the SkipRound/DoNothing 'action'
	 * @param skip
	 */
	public void selectSkipRound(boolean skip) {
		if(skip) {
			indexOfSelectedType = indexOfTypeDoNothing;
			indexOfSelectedAction = 0;
		} else {
			indexOfSelectedType = indexOfTypeDoNothing;
			indexOfSelectedAction = -1;
		}
		repaint();
	}

	/**
	 * calculates and updates the center of the interface
	 */
	private void updateCenterOfInterface() {
		if(p1!=null && p2 !=null) {
			//calculate start coordinates for 'lifeline' to the character
			int xCenterInterface = (int)((p1.getCenterX() + p2.getCenterX())/2);
	    	int yCenterInterface = (int)((p1.getCenterY() + p2.getCenterY())/2);
			mapFrame.updateCenterOfInterface(xCenterInterface, yCenterInterface);
		}
	}
	
	/**
	 * Class that handles all TUIO (and Mouse) listening within the InterfacePanel
	 * @author alofs
	 */
	public class GenericInterfaceListener implements TuioListener, MouseListener, MouseMotionListener {
		
		//some constant value to represent the mouse-cursor
		private static final int MOUSE_ID = 1;
		
		//some constant value to represent that something is not being dragged
		private static final int NO_ID = -1;
		
		private int id_draggingP1 = NO_ID; //the id of the cursor dragging the first handle
		private int id_draggingP2 = NO_ID; //the id of the cursor dragging the second handle
		
		private double p1dX; //used to store dx of touchpoint relative to x of first handle
		private double p1dY; //used to store dy of touchpoint relative to y of first handle
		private double p2dX; //used to store dx of touchpoint relative to x of second handle
		private double p2dY; //used to store dy of touchpoint relative to y of second handle
		
		private Point possibleNewStoryAreaAnchorPoint = null;
		private int possibleNewStoryAreaAnchorPointID = NO_ID;
		
		/**
		 * 
		 */
		public void mousePressed(MouseEvent e) {
			Point point = e.getPoint();
			//with right mouse click, multi-touch is emulated
			if(e.getButton() == MouseEvent.BUTTON3) {
				//remove the possibly existing old emulated point
				remove(111);
				//add a new emulated multi-touch at this point point
				add(111, point);
			} else {
				add(MOUSE_ID, point);
				//remove the possibly existing old emulated point
				remove(111);
			}
		}
		
		/**
		 * 
		 */
		public void mouseDragged(MouseEvent e) {
			Point point = e.getPoint();
			update(MOUSE_ID, point);
		}
		
		/**
		 * 
		 */
		public void mouseReleased(MouseEvent e) {
			Point point = e.getPoint();
			remove(MOUSE_ID);
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
		
		/**
		 * 
		 */
		public void addTuioCursor(TuioCursor tuioCursor) {
			int id = tuioCursor.getCursorID();
			Point point = mapFrame.tuioPointToPoint(tuioCursor.getPosition());
			setDebugLastPoint(point);
			add(id, point);
		}
		
		/**
		 * 
		 */
		public void updateTuioCursor(TuioCursor tuioCursor) {
			int id = tuioCursor.getCursorID();
			Point point = mapFrame.tuioPointToPoint(tuioCursor.getPosition());
			setDebugLastPoint(point);
			update(id, point);
		}
		
		/**
		 * 
		 */
		public void removeTuioCursor(TuioCursor tuioCursor) {
			int id = tuioCursor.getCursorID();
			remove(id);
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
	
		/**
		 * This callback method is invoked by the TuioClient to mark the end of a received TUIO message bundle.
		 * @Override
		 */
		public void refresh(TuioTime time) {
			//System.out.println("refresh was called! " + time.getTotalMilliseconds());	
		}
		
		/**
		 * corresponds more or less with mousePressed()
		 * @param id
		 * @param point
		 */
		private void add(int id, Point point) {
			System.out.println("ADD id of cursor: " +id);
			//does the event happened inside the MapPanel?
			if (contains(point)) {
				//clicked on p1 to start a drag?
				if (p1!=null && id_draggingP1==NO_ID && p1.contains(point)) {
					id_draggingP1 = id;
					System.out.println("p1 drag started");
					p1dX = point.getX() - p1.getX();
					p1dY = point.getY() - p1.getY();
				}
				
				//clicked on p2 to start a drag?
				else if (p2!=null && id_draggingP2==NO_ID && p2.contains(point)) {
					id_draggingP2 = id;
					System.out.println("p2 drag started");
					p2dX = point.getX() - p2.getX();
					p2dY = point.getY() - p2.getY();
				}
				 
				else {
					//clicked on an interface element? 
					boolean hittedInterface = checkForInterfaceHit(point); 
					if(!hittedInterface) {
						
						//clicked on any anchor of any StoryArea which starts a drag?
						 boolean startedDrag = false;
						 //find out if any StoryArea is dragged at the moment
						 boolean anyStoryAreaIsDragged = false;
						 //check in normal order because oldest StoryArea is drawn on top of others 
						 for (int index = 0; !startedDrag && index <= storyAreas.size()-1; index++) {
						 	 StoryArea sa = storyAreas.get(index);
						 	startedDrag = sa.checkForStartDrag(id, point);
						 	if(sa.isCurrentlyDragged()) {
						 		anyStoryAreaIsDragged = true;
						 	}
						 }
						 //if no drag started, the user clicked in empty space on map
						 if(!startedDrag) {
							 //if the maximum number of storyareas has not yet been reached
							 if(storyAreas.size() < MAX_NUMBER_OF_STORYAREAS) {
								 //if there was already another point for a new StoryArea detected
								 if(possibleNewStoryAreaAnchorPoint!=null && possibleNewStoryAreaAnchorPointID!=NO_ID) {
									 //only if nothing else is being dragged at the moment!
									 if (!anyStoryAreaIsDragged && id_draggingP1==NO_ID && id_draggingP2==NO_ID) {
										 //create new StoryArea! :-)
										 StoryArea newStoryArea = new StoryArea(possibleNewStoryAreaAnchorPoint, point);
										 storyAreas.add(newStoryArea);
										 //after creating new storyArea, directly start dragging it!
										 newStoryArea.checkForStartDrag(id, point);
										 newStoryArea.checkForStartDrag(possibleNewStoryAreaAnchorPointID, possibleNewStoryAreaAnchorPoint);
										 //now erase possibleNewStoryAreaAnchorPoint(ID)
										 possibleNewStoryAreaAnchorPoint = null;
										 possibleNewStoryAreaAnchorPointID = NO_ID;
										 repaint();
										 System.out.println("created new StoryArea!");
									 }
								 } else {
									 //store this point as point for a possible new StoryArea 
									 possibleNewStoryAreaAnchorPoint = point;
									 possibleNewStoryAreaAnchorPointID = id;
									 System.out.println("dragging possible new StoryArea anchor point started");
								 }
							 }
						 }
					}
				}
			}
		}
	
		/**
		 * corresponds more or less with mouseDragged()
		 * @param id
		 * @param point
		 */
		private void update(int id, Point point) {
			//System.out.println("UPDATE id of cursor: " +id);
			//are we still dragging inside the MapPanel?
			if (contains(point)) {
				if (id==id_draggingP1) {
					double x = point.getX() - p1dX;
					double y = point.getY() - p1dY;
					
					System.out.println("p1 x = " + x);
					System.out.println("p1 x = " + x);
					
					p1.setFrame(x, y, p1.getWidth(), p1.getHeight());
					updateCenterOfInterface();
					repaint();
				} else if (id==id_draggingP2) {
					double x = point.getX() - p2dX;
					double y = point.getY() - p2dY;
					
					System.out.println("p2 x = " + x);
					System.out.println("p2 x = " + x);
					
					p2.setFrame(x, y, p2.getWidth(), p2.getHeight());
					updateCenterOfInterface();
					repaint();
				} else if (id==possibleNewStoryAreaAnchorPointID) {
					possibleNewStoryAreaAnchorPoint = point;
				} else {
					//inform all storyAreas that this particular drag has changed
					for (int index = 0; index <= storyAreas.size()-1; index++) {
					 	 StoryArea sa = storyAreas.get(index);
						 sa.updatePosition(id, point);
						 //XXX remove StoryArea when points close together or touch eachother?
						 //yes, this check is currently performed before drawing 
					 }
					repaint();
				}
			}
		}

		/**
		 * corresponds more or less with mouseReleased()
		 * @param id
		 */
		private void remove(int id) {
			System.out.println("REMOVE id of cursor: " +id);
			if(id==id_draggingP1) {
				id_draggingP1 = NO_ID;
				System.out.println("p1 drag stopped");
			} else if(id==id_draggingP2) {
				id_draggingP2 = NO_ID;
				System.out.println("p2 drag stopped");
			} else if (id==possibleNewStoryAreaAnchorPointID) {
				possibleNewStoryAreaAnchorPoint = null;
				possibleNewStoryAreaAnchorPointID = NO_ID;
				System.out.println("dragging possible new StoryArea anchor point stopped");
			} else {
				//inform all storyAreas that this particular drag has stopped
				for (int index = 0; index <= storyAreas.size()-1; index++) {
				 	 StoryArea sa = storyAreas.get(index);
					sa.removeID(id); 
				}
				System.out.println("yeah that's right, take your greasy fingers off the screen!");
				repaint();
			}
		}
	}
	
	/**
	 * 
	 * @param point
	 * @return
	 */
	private boolean checkForInterfaceHit(Point point) {
		//clicked on an interface element? try to find this element...
		boolean foundSource = false;
		
		//transform clicked point to same coordinate space as interface
		Point pt = new Point();
		try {
			atA.inverseTransform(point, pt);
		} catch (NoninvertibleTransformException e1) {
			//should never happen
			e1.printStackTrace();
		}
		
		//first check the buttonStrip
		for(int i = 0; i<rectsButtons.size(); i++) {
			RectangularShape r = rectsButtons.get(i);
			if (r.contains(pt)) {
				foundSource = true;
				//we found the source, now perform corresponding command
				
				//when clicked on "execute selected action"
				if(i==0) {
					//check if an action is selected
					if(waitingForUserInput && indexOfSelectedAction!=-1) {
						//perform the currently selected action
						mapFrame.performSelectedAction();
					} else {
						//do nothing, because we are not clickable
					}
					
				//when clicked on yellowOvalA
				} else if (i==1) {
					boolean toggle = !showActions[0];
					showActions[0] = toggle;
					if(toggle == false) {
						showActions[1] = true;
					}
			
				//when clicked on yellowOvalB
				} else if (i==2) {
					boolean toggle = !showActions[1];
					showActions[1] = toggle;
					if(toggle == false) {
						showActions[0] = true;
					}
				}
				break;
			}
		}
		
		//check Types
		for(int indexType = 0; !foundSource && indexType<rectsTypes.size(); indexType++) {
			RectangularShape shape = rectsTypes.get(indexType);
			if (shape.contains(pt)) {
				foundSource = true;
				if (waitingForUserInput) {
					//update interfacePanel
					updateIndexSelectedAction(indexType, 0);
					//update mapFrame
					if(indexType==indexOfTypeDoNothing) {
						//NOT needed anymore: setting skipRound to false after clicking on the type "DoNothing" 
						//Because now, the first action is always auto-selected when a type is clicked
						//mapFrame.selectSkipRound(false);
						
						//but skipRound should then also be 'auto'-selected
						//FIXME: using a GUI element as data/model is not very MVC, sorry... :(
						mapFrame.selectSkipRound(true);
						
					} else {
						mapFrame.setSelectedIndex(indexType, 0);
					}
				}
			}
		}
		
		//check rectsActionsA
		if(!foundSource) {
			foundSource = checkActionRects(pt, 0, rectsActionsA);
		}
		
		//rotate 180 degrees (and check rectsActionsB)
		if(!foundSource) {
			try {
				atB.inverseTransform(point, pt);
			} catch (NoninvertibleTransformException e1) {
				//should never happen
				e1.printStackTrace();
			}
			//check rectsActionsB
			foundSource = checkActionRects(pt, 1, rectsActionsB);
		}
		
		//always repaint if something in the interface was clicked
		if(foundSource)repaint();
		return foundSource;
	}
	
	/**
	 * 
	 * @param pt
	 * @param i
	 * @param rectsActionsX
	 * @return
	 */
	private boolean checkActionRects(Point pt, int i, Vector<RectangularShape> rectsActionsX) {
		//have we found the source of the click already?
		boolean foundSource = false;
		
		//check if one of the actions was clicked
		for(int indexAction = 0; !foundSource && indexAction<rectsActionsX.size(); indexAction++) {
			RectangularShape s = rectsActionsX.get(indexAction);
			if (s.contains(pt)) {
				foundSource = true;
				if (waitingForUserInput) {
					if(indexOfSelectedType==indexOfTypeDoNothing) {
						mapFrame.selectSkipRound(true);
						indexOfSelectedAction = indexAction;
					} else {
						mapFrame.clearSelectionList();
						//clear selection if we press twice? no!
//						if (index == indexSelectedAction) {
//							mapFrame.clearSelectionList();
//							indexSelectedAction = -1;
//						} else {
							mapFrame.setSelectedIndex(indexOfSelectedType, indexAction);
							//(mapFrame will call updateIndexSelectedAction() and this will do all the rest)
//						}
					}
				}
			}
		}
		return foundSource;
	}
	
	/**
	 * 
	 * @param point
	 */
	private void setDebugLastPoint(Point point) {
		if (point!=null) {
			debugLastPoint = new Ellipse2D.Double(point.getX()-.1*FINGER_SIZE, point.getY()-.1*FINGER_SIZE, 0.2*FINGER_SIZE, 0.2*FINGER_SIZE);
		} else {
			debugLastPoint = null;
		}
		repaint();
	}
}