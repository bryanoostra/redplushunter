package basic.helloWorld;
import java.io.File;

import org.mt4j.MTApplication;
import org.mt4j.components.MTCanvas;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.components.visibleComponents.shapes.MTRectangle.PositionAnchor;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.components.visibleComponents.widgets.MTImage;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.components.visibleComponents.widgets.buttons.MTImageButton;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

public class HelloWorldScene extends AbstractScene {

	private StoryTextArea storyTextArea;
	private MTComponent backgroundLayer;
	private ParchmentRoll parchmentRoll;
	private PImage imgParchmentBackground;
	
	public HelloWorldScene(MTApplication mtApp, String name) {
		super(mtApp, name);
		
		MTCanvas canvas = this.getCanvas();
		
		MTColor black = new MTColor(0,0,0);
		MTColor gray = new MTColor(128,128,128);
		MTColor white = new MTColor(255,255,255);
		
		this.setClearColor(new MTColor(146, 150, 188, 255));
		
		this.registerGlobalInputProcessor(new CursorTracer(mtApp, this));
		
		IFont fontArial = FontManager.getInstance().createFont(mtApp, "arial.ttf", 
				20, 	//Font size
				black,  //Font fill color
				gray);	//Font outline color
		
		//XXX: hier nog eens iets mee doen?
		fontArial.getFontMaxAscent();
				
		String folderName = System.getProperty("user.dir") + File.separator;
		folderName = folderName.concat("examples" + File.separator + "basic" + File.separator + "helloWorld" + File.separator);
		
		String fileNameMap = folderName.concat("map_pirates.PNG");
		PImage imgMap = mtApp.loadImage(fileNameMap);
		if (imgMap!=null) {
			boolean tiled = true;
			MTBackgroundImage mtbiMap = new MTBackgroundImage(mtApp, imgMap, tiled);
			backgroundLayer = new MTComponent(mtApp);
			backgroundLayer.addChild(mtbiMap);
			canvas.addChild(backgroundLayer);
		} else {
			System.out.println("PImage imgBackground == null");
		}
		
		String fileNameParchmentBackground = folderName.concat("parchment_background.PNG");
		imgParchmentBackground = mtApp.loadImage(fileNameParchmentBackground);
		if (imgParchmentBackground!=null) {
			storyTextArea = new StoryTextArea(mtApp, fontArial, imgParchmentBackground);
			storyTextArea.setPositionGlobal(new Vector3D(mtApp.width/2f, mtApp.height/2f));
			canvas.addChild(storyTextArea);
		} else {
			System.out.println("PImage imgParchmentRoll == null");
		}
		
		String fileNameParchmentRoll = folderName.concat("parchment_roll.PNG");
		PImage imgParchmentRoll = mtApp.loadImage(fileNameParchmentRoll);
		if (imgParchmentRoll!=null) {
			storyTextArea.addParchmentRoll(imgParchmentRoll);
		} else {
			System.out.println("PImage imgParchmentRoll == null");
		}
		
		//storyTextArea.setTexture(imgParchmentBackground);
	}
	
	@Override
	public void init() {
		//storyTextArea.setTexture(imgParchmentBackground);
	}
	@Override
	public void shutDown() {}
}
