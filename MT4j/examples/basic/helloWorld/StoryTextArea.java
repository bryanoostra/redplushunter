package basic.helloWorld;

import java.util.Vector;

import org.mt4j.MTApplication;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.MT4jSettings;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.opengl.GLTexture;
import org.mt4j.util.opengl.GLTextureParameters;

import processing.core.PImage;

public class StoryTextArea extends MTTextArea {

	public static final float MIN_HEIGHT = 100;
	
	private MTApplication mtApp;
	private ParchmentRoll parchmentRoll;
	
	private Vector<String> lines = new Vector<String>();
	private int currentRoundNumber = 1;

	private PImage imgParchmentBackground;
	
	public StoryTextArea(MTApplication pApplet, IFont font, PImage imgParchmentBackground) {
		super(pApplet, font);
		mtApp = pApplet;
		
//		this.setAnchor(PositionAnchor.UPPER_LEFT);
		this.imgParchmentBackground = imgParchmentBackground;
		
//		//set background image/texture
//		if (MT4jSettings.getInstance().isOpenGlMode()){
//			GLTextureParameters tp = new GLTextureParameters();
//			tp.minFilter = GLTextureParameters.LINEAR; //"LINEAR" disables mip-mapping in opengl
//		//	tp.target = GLConstants.RECTANGULAR;
//			GLTexture tex = new GLTexture(mtApp, mtApp.width, mtApp.height, tp);
//			tex.putImage(imgParchmentBackground);
//			this.setTexture(tex);
//		}else{
//			this.setTexture(imgParchmentBackground);
//		}
				
		this.setNoFill(false);
		this.setNoStroke(false);
		this.setStrokeColor(new MTColor(255,255,255));
		
		
		//set some dummy data:
		addOperatorResult(1, "etc.", true);
		addOperatorResult(1, "etc..", true);
		addOperatorResult(1, "etc...", true);
		addOperatorResult(1, "A long, long, long, long time ago there was Little Red Rinding Hood the pirate", true);
		
		addOperatorResult(2, "etc.", true);
		addOperatorResult(2, "etc..", true);
		addOperatorResult(2, "etc...", true);
		
		addOperatorResult(3, "etc.", true);
		addOperatorResult(3, "etc..", true);
		addOperatorResult(3, "etc...", true);
		
//		addOperatorResult(4, "etc.", true);
//		addOperatorResult(4, "etc..", true);
//		addOperatorResult(4, "etc...", true);
		
		addOperatorResult(5, "etc.", true);
		addOperatorResult(5, "etc..", true);
		addOperatorResult(5, "etc...", true);
		
		addOperatorResult(6, "Red does this", true);
		addOperatorResult(6, "Wolf does that", true);
		addOperatorResult(6, "Grandma does nothing but baking cakes", true);
		
		addOperatorResult(7, "Red skips to the forest", true);
		addOperatorResult(7, "Wolf hides in the bushes", true);
		addOperatorResult(7, "Grandma bakes a chocolate cake", true);
				
		updatePreferredHeight(MIN_HEIGHT);
		
		this.setWidthXYGlobal(mtApp.getWidth()/10);
		
		//this.setWidthXYGlobal(200);
		//this.setHeightXYGlobal(200);
		
		//this.setTexture(imgParchmentBackground);
		
		
		
	}
	
	public void addOperatorResult(int orRoundNumber, String orText, boolean success) {
		while(currentRoundNumber < orRoundNumber) {
			lines.add("----- Round " + currentRoundNumber + " -----");
			currentRoundNumber++;
		}
		//add the text to the story log
		if(success) { 
			lines.add(orText);
		} else {
			lines.add("failed: " + orText);
		}
	}

	public void addParchmentRoll(PImage imgParchmentRoll) {
		parchmentRoll = new ParchmentRoll(imgParchmentRoll, mtApp, this);
		addChild(parchmentRoll);
		
		float width = this.getWidthXY(TransformSpace.GLOBAL);
		Vector3D position = this.getPosition(TransformSpace.GLOBAL);
		position.setX(position.x + 0.5f*width);
		
		parchmentRoll.setInitialized(false);
		parchmentRoll.setAnchor(PositionAnchor.CENTER);
		parchmentRoll.setWidthXYGlobal(width);
		parchmentRoll.setPositionGlobal(position);
		parchmentRoll.setInitialized(true);
	}
	
	public void translate(Vector3D vect) {
		if(parchmentRoll==null || !parchmentRoll.isBeingDragged()) {
			super.translate(vect);
		} else {	
			//do nothing when parchmentRoll is already being dragged
		}
	}

	public void updatePreferredHeight(float availableHeight) {
		System.out.println("availableHeight                              = " + availableHeight);
		System.out.println("BEGIN this.getHeightXY(TransformSpace.LOCAL) = " + this.getHeightXY(TransformSpace.LOCAL));
		
		float oldWidth = this.getWidthXY(TransformSpace.LOCAL);
		
		if (availableHeight > 1) {
			//remove characters as long as current height > availableHeight 
			while (this.getLineCount() > 1 && this.getHeightXY(TransformSpace.LOCAL) > availableHeight) {
				this.removeLastCharacter();
			}
		}
		
		//add lines as long as current height < availableHeight
		while (this.getHeightXY(TransformSpace.LOCAL) < availableHeight) {
			int numberOfLines = this.getLineCount();
			int index = lines.size()-1-numberOfLines;
			if(index>=0) {
				appendText("\n" + lines.get(index));
			} else {
				appendText("\n");
			}
		}
		
		
		
		//when the width of the storyTextArea changed, update the parchmentRoll
		float newWidth = this.getWidthXY(TransformSpace.LOCAL); 
		if (oldWidth!=newWidth && parchmentRoll!=null) {
			
			//this.setTexture(imgParchmentBackground);
			
			parchmentRoll.setWidthXYRelativeToParent(newWidth);
			
			Vector3D position = this.getPosition(TransformSpace.LOCAL);
			position.setY(availableHeight);
			position.setX(position.x + 0.5f*newWidth);
			
			parchmentRoll.setInitialized(false);
			parchmentRoll.setPositionRelativeToParent(position);
			parchmentRoll.setInitialized(true);			
		}
		System.out.println("END   this.getHeightXY(TransformSpace.LOCAL) = " + this.getHeightXY(TransformSpace.LOCAL));
	}
}
