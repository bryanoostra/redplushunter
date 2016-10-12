package vs.characteragent.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public class StoryArea {

	private Ellipse2D left; //left handle/fingertip of this StoryArea
	private Ellipse2D right; //right handle/fingertip of this StoryArea
	
	//some constant value to represent that something is not being dragged
	private static final int NO_ID = -1;
	
	private int id_dragging_left = NO_ID; //the id of the cursor dragging the left handle
	private int id_dragging_right = NO_ID; //the id of the cursor dragging the right handle
	private int id_dragging_roll = NO_ID; //the id of the cursor dragging the parchment roll
	private int id_dragging_text = NO_ID; //the id of the cursor dragging the parchment roll
	
	private double left_dX; //used to store dx of touchpoint relative to x of left handle
	private double left_dY; //used to store dy of touchpoint relative to y of left handle
	private double right_dX; //used to store dx of touchpoint relative to x of right handle
	private double right_dY; //used to store dy of touchpoint relative to y of right handle
	
	private double text_left_dX; //used to store dx of touchpoint relative to x of left handle
	private double text_left_dY; //used to store dy of touchpoint relative to y of left handle
	private double text_right_dX; //used to store dx of touchpoint relative to x of right handle
	private double text_right_dY; //used to store dy of touchpoint relative to y of right handle
	
	private double roll_dY; //used to store dy of touchpoint relative to y of parchment roll
	
	private double baseY; //used to store the y value of the bottom_left corner of this StoryArea
	
	private double widthHeightRatio = 2; //used to store width/height ratio of this StoryArea, default=2
	
	private Rectangle2D.Double rectRoll = new Rectangle2D.Double();; //Rectangle of parchment roll, stored for event-handling
	private AffineTransform atRoll = new AffineTransform(); //AffineTransform of parchment roll, stored for event-handling
	
	private Rectangle2D.Double rectText = new Rectangle2D.Double();; //Rectangle of text area, stored for event-handling
	private AffineTransform atText = new AffineTransform(); //AffineTransform of text area, stored for event-handling
	
	/**
	 * constructor to create new StoryArea with provided bottomLeft and bottomRight points
	 * @param pBottomLeft
	 * @param pBottomRight
	 */
	public StoryArea(Point pBottomLeft, Point pBottomRight) {
		int fs = InterfacePanel.FINGER_SIZE;
		left = new Ellipse2D.Double(pBottomLeft.getX()-.5*fs, pBottomLeft.getY()-.5*fs, fs, fs);
		right = new Ellipse2D.Double(pBottomRight.getX()-.5*fs, pBottomRight.getY()-.5*fs, fs, fs);
	}
	
	/**
	 * paint this StoryArea on the provided Graphics2D environment 
	 * @param g2d
	 * @param txtStory a vector with the TextLayouts of the story, should never be empty!
	 * @param maxStoryWidth
	 */
	public void paintYourself(Graphics2D g2d, Vector<TextLayout> txtStory, double maxStoryWidth) {
		g2d.setColor(InterfacePanel.TRANSPARENT_SKIN_COLOR);
		g2d.fill(left);
		g2d.fill(right);
		
		Image imgFinger = InterfacePanel.imgFingerprint;
		if(imgFinger!=null) {
    		//draw first fingerprint
			double x1Fingerprint = left.getX() + (left.getWidth() - imgFinger.getWidth(null))/2;
			double y1Fingerprint = left.getY() + (left.getHeight() - imgFinger.getHeight(null))/2;
			g2d.drawImage(imgFinger, (int)x1Fingerprint, (int)y1Fingerprint, null);
			
    		//draw second fingerprint			
			double x2Fingerprint = right.getX() + (right.getWidth() - imgFinger.getWidth(null))/2;
			double y2Fingerprint = right.getY() + (right.getHeight() - imgFinger.getHeight(null))/2;
			g2d.drawImage(imgFinger, (int)x2Fingerprint, (int)y2Fingerprint, null);
    	}

    	//calculate diagonal
    	double diagonal = getDiagonal();

    	//return when there is no space between points/handles/fingerprints 
    	if(diagonal<1) return;
    	
		//create new AffineTransforms
    	atText = new AffineTransform();
    	atRoll = new AffineTransform();
		
		//calculate slope and set rotation
    	double slope = Math.atan2(right.getCenterY() - left.getCenterY(), right.getCenterX() - left.getCenterX());
    	atRoll.rotate(slope, left.getCenterX(), left.getCenterY());
    	atText.rotate(slope, left.getCenterX(), left.getCenterY());
    	
    	//increase width to provide left and right margins
    	maxStoryWidth = maxStoryWidth + 2*InterfacePanel.STORY_TEXT_MARGIN;
    	
    	//calculate scale and set scaling
    	double scaleText = diagonal/maxStoryWidth;
    	atText.scale(scaleText, scaleText);
    	    	
    	//perform actual rotation and scaling on Graphics2D 
    	g2d.setTransform(atText);
    					
    	//calculate the needed coordinates of p1 in the scaled Graphics context
    	double scaledX_text = (1/scaleText) * (left.getCenterX() + .5*InterfacePanel.FINGER_SIZE);
    	double scaledY_text = (1/scaleText) * (left.getCenterY() + .5*InterfacePanel.FINGER_SIZE);
    	double scaledHeightOfStoryArea_text = maxStoryWidth / widthHeightRatio;
   	
    	//update the rectangle surrounding the text (for event handling)
    	rectText.x = scaledX_text;
    	rectText.y = scaledY_text-scaledHeightOfStoryArea_text;
    	rectText.width = maxStoryWidth;
    	rectText.height = scaledHeightOfStoryArea_text;
    	
    	//draw white area for text
		g2d.setColor(InterfacePanel.STORY_COLOR);
		g2d.fill(rectText);
		
		//set color for text
		g2d.setColor(InterfacePanel.TEXT_COLOR);
		
		//there is always a firstElement, use it to calculate ascent and descent
		TextLayout tlStory = txtStory.firstElement();
		double ascentStory = tlStory.getAscent();
		double descentStory = tlStory.getDescent();
		
		//draw actual TextLayouts of story
		double currx = scaledX_text;
		double curry = scaledY_text;
		//iterate backwards over TextLayouts of story
		for (int index = txtStory.size()-1; index>=0; index--) {
			tlStory = txtStory.get(index);
			curry = curry - descentStory;
			
			//continue until there is no space left
			if ((curry-ascentStory) < (scaledY_text-scaledHeightOfStoryArea_text)) break;
			
			//actual drawing of this story line
			tlStory.draw(g2d, (float)(currx + InterfacePanel.STORY_TEXT_MARGIN), (float)curry-5); //-5 is a manual adjustment
			curry = curry - ascentStory;
		}
		
		//the image of the parchment roll is only loaded once in InterfacePanel
		Image imgRoll = InterfacePanel.imgRoll;
		
		//the width and height of the image
    	int imgWidth = imgRoll.getWidth(null);
    	int imgHeight = imgRoll.getHeight(null);
    	
    	//calculate scale and set scaling
    	double scaleRoll = diagonal/imgWidth;
    	atRoll.scale(scaleRoll, scaleRoll);

    	//perform actual rotation and scaling on Graphics2D 
    	g2d.setTransform(atRoll);
    	
    	//calculate the needed coordinates of p1 in the scaled Graphics context
    	double scaledX_roll = (1/scaleRoll) * (left.getCenterX() + .5*InterfacePanel.FINGER_SIZE);
    	double scaledY_roll = (1/scaleRoll) * (left.getCenterY() + .5*InterfacePanel.FINGER_SIZE);
    	double scaledHeightOfStoryArea_roll = imgWidth / widthHeightRatio;
		
    	//update the rectangle surrounding the parchment roll image (for event handling)
    	rectRoll.x = scaledX_roll;
    	rectRoll.y = scaledY_roll-scaledHeightOfStoryArea_roll;
    	rectRoll.width = imgWidth;
    	rectRoll.height = imgHeight;
    	
    	//try to draw the parchment roll image
    	if(imgRoll!=null) {
    		//FIXME: the following line makes the interface slow, can the same be achieved faster?
			g2d.drawImage(imgRoll, (int)scaledX_roll, (int)(scaledY_roll-scaledHeightOfStoryArea_roll), null);
    		
    		//XXX: for now just make it ugly, but nice and fast
    		g2d.setColor(new Color(150, 100, 50)); //brown
    		//g2d.fillRect((int)scaledX_roll, (int)(scaledY_roll-scaledHeightOfStoryArea_roll), (int)imgWidth, (int)imgHeight);

        	//NOTE: scaling like this takes too much time 
    		//Image imgRoll = InterfacePanel.imgRoll.getScaledInstance((int)(diagonal), -1, 0);
     	
    		//TODO: find out why image is slow, 
    		//		probably source file is rather large, 
    		//		but needed when scaling up
    		//		is there a way to draw high quality images quicker?
    		//		try using scalable vector image
		}
		
		//draw a border around this StoryArea
    	g2d.setColor(InterfacePanel.BORDER_COLOR);
		g2d.setStroke(InterfacePanel.BASIC_STROKE_ROUNDED);
		g2d.setTransform(atText);
		g2d.draw(rectText);
				
    	//reset g2d back to empty transform!
    	g2d.setTransform(new AffineTransform());
	}
	
	/**
	 * Get the distance between left and right handle/point/fingerprint
	 * @return the available width for this StoryArea
	 */
	private double getDiagonal() {
		return Point.distance(left.getCenterX(), left.getCenterY(), right.getCenterX(), right.getCenterY()) - InterfacePanel.FINGER_SIZE;
	}
	
	/**
	 * Determine if this StoryArea should be removed
	 * @return boolean true if StoryArea is too small and is not being dragged
	 */
	public boolean isTooSmallAndNotDragged() {
		//check if diagonal is too small and points/fingertips are not dragged
		return (getDiagonal()<1 && id_dragging_left==NO_ID && id_dragging_right==NO_ID);
	}
	
	/**
	 * Check if this cursor/touchpoint (that just came into being) initiates a new drag
	 * If true, starts new drag by storing needed information
	 * @param id the id of the 'cursor'
	 * @param point the (touch)point of the 'cursor'
	 * @return if the provided cursor initiated a new drag
	 */
	public boolean checkForStartDrag(int id, Point point) {
		if (id_dragging_left==NO_ID && left.contains(point)) {
			id_dragging_left = id;
			left_dX = point.getX() - left.getX();
			left_dY = point.getY() - left.getY();
			return true;
		} else if (id_dragging_right==NO_ID && right.contains(point)) {
			id_dragging_right = id;
			right_dX = point.getX() - right.getX();
			right_dY = point.getY() - right.getY();
			return true;
		} else {
			
			//transform clicked point to same coordinate space as parchmentRoll
			Point pt = new Point();
			try {
				atRoll.inverseTransform(point, pt);
			} catch (NoninvertibleTransformException e1) {
				//should never happen
				e1.printStackTrace();
			}
			if(id_dragging_roll==NO_ID && rectRoll.contains(pt)) {
				id_dragging_roll = id;
				roll_dY = pt.getY() - rectRoll.getY();
				baseY = rectRoll.getY() + (rectRoll.getWidth()/widthHeightRatio);
				return true;
			} else {
			
				//transform clicked point to same coordinate space as text
				pt = new Point();
				try {
					atText.inverseTransform(point, pt);
				} catch (NoninvertibleTransformException e1) {
					//should never happen
					e1.printStackTrace();
				}
				if(id_dragging_text==NO_ID && rectText.contains(pt)) {
					id_dragging_text = id;
					text_left_dX = point.getX() - left.getX();
					text_left_dY = point.getY() - left.getY();
					text_right_dX = point.getX() - right.getX();
					text_right_dY = point.getY() - right.getY();
					
					//delete this?
					//text_dY = pt.getY() - rectText.getY();
					//baseY = rectText.getY() + (rectText.getWidth()/widthHeightRatio);
					return true;
				}
			}
			
			return false;
		}
	}
	
	/**
	 * The 'cursor' or 'touchpoint' with the provided id has moved to the provided point
	 * Check if this cursor was dragging something and update position
	 * @param id the id of the 'cursor'
	 * @param point the (touch)point of the 'cursor'
	 */
	public void updatePosition(int id, Point point) {
		if(id_dragging_left==id) {
			double x = point.getX() - left_dX;
			double y = point.getY() - left_dY;
			left.setFrame(x, y, left.getWidth(), left.getHeight());
		} else if(id_dragging_right==id) {
			double x = point.getX() - right_dX;
			double y = point.getY() - right_dY;
			right.setFrame(x, y, right.getWidth(), right.getHeight());
		} else if (id_dragging_text==id) {
			//transform clicked point to same coordinate space as text area
			Point pt = new Point();
			try {
				atText.inverseTransform(point, pt);
			} catch (NoninvertibleTransformException e1) {
				//should never happen
				e1.printStackTrace();
			}
			
			//check if conversion did go allright
			if(pt.x==point.x) {
				//something went wrong in inverseTransform(point, pt)
				//just ignore this cursor update!
			} else {
				double left_x = point.getX() - text_left_dX;
				double left_y = point.getY() - text_left_dY;
				left.setFrame(left_x, left_y, left.getWidth(), left.getHeight());
				
				double right_x = point.getX() - text_right_dX;
				double right_y = point.getY() - text_right_dY;
				right.setFrame(right_x, right_y, right.getWidth(), right.getHeight());
				
				//roll_dY = roll_dY
			}
			
			
			
			
		} else if (id_dragging_roll==id) {
			//transform clicked point to same coordinate space as parchmentRoll
			Point pt = new Point();
			try {
				atRoll.inverseTransform(point, pt);
			} catch (NoninvertibleTransformException e1) {
				//should never happen
				e1.printStackTrace();
			}
			
			//System.out.println(" ");
			//System.out.println("point.x "+point.x);
			//System.out.println("rectRoll.getMinX() "+rectRoll.getMinX());
			//System.out.println("pt.x "+pt.x);
			//System.out.println("rectRoll.getMaxX() "+ rectRoll.getMaxX());
			
			//check if conversion did go allright
			if(pt.x==point.x) {
				//something went wrong in inverseTransform(point, pt)
				//just ignore this cursor update!
			}
			
			//the roll can only move in one dimension, so it should be constantly checked 
			//if the point is still inbetween the x dimensions of the roll
			else if(rectRoll.getMinX() < pt.x && pt.x < rectRoll.getMaxX()) {
				System.out.println("still inbetween x dimensions of roll!");

				widthHeightRatio = rectRoll.getWidth() / (baseY - pt.getY() + roll_dY);
			
				//heightOfStoryArea should always remain larger than height of Roll image
				//therefore widthHeightRation is checked against rectRoll dimensions
				if (widthHeightRatio > rectRoll.getWidth() / rectRoll.getHeight()) {
					widthHeightRatio = rectRoll.getWidth() / rectRoll.getHeight();
					//need to update dy
					roll_dY = pt.getY() - rectRoll.getY();
					//if roll_dY is larger than roll height, we are no longer dragging
					//System.out.println("roll_dY "+roll_dY);
					//System.out.println("rectRoll.getHeight() "+rectRoll.getHeight());
					//System.out.println("rectRoll.getMaxY() "+ rectRoll.getMaxY());
					//System.out.println("baseY "+baseY);
					//System.out.println(" ");
					if (roll_dY > rectRoll.getHeight()) { //XXX && rectRoll.getMaxY()>=baseY) {
						//the roll is no longer being dragged
						id_dragging_roll = NO_ID;
					}
				}
			} 
			else {
				System.out.println("outside x dimensions of roll!");
				//the roll is no longer being dragged
				id_dragging_roll = NO_ID;
			}
		} else {
			//do nothing
		}
	}
	
	/**
	 * Called when a cursor no longer is touching the screen/touchtable
	 * Removes id from the item this cursor was dragging
	 * Sets id to NO_ID (==-1), which corresponds with "not being dragged right now" 
	 * @param id the id of the 'cursor'
	 */
	public void removeID(int id) {
		if(id_dragging_left==id) {
			id_dragging_left = NO_ID;
		}
		if(id_dragging_right==id) {
			id_dragging_right = NO_ID;
		}
		if(id_dragging_roll==id) {
			id_dragging_roll = NO_ID;
		}
		if(id_dragging_text==id) {
			id_dragging_text = NO_ID;
		}
	}

	public boolean isCurrentlyDragged() {
		if(id_dragging_left==NO_ID && id_dragging_right==NO_ID && id_dragging_roll==NO_ID && id_dragging_text==NO_ID) {
			return false;
		} else {
			return true;
		}
	}
}