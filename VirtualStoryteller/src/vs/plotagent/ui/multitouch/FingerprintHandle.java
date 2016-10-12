package vs.plotagent.ui.multitouch;

import org.mt4j.MTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.input.gestureAction.InertiaDragAction;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import processing.core.PImage;

public class FingerprintHandle extends MTComponent {

	
	private static MTColor skinColor = new MTColor(239, 208, 207);
	
	private static String fileNameFingerprint = Settings.FOLDER_NAME.concat("fingerprint.gif");
	public static float radiusX = 20;
	public static float radiusY = 30;
	
	public FingerprintHandle(Vector3D centerpoint, MTApplication app) {
		super(app);
		
		setComposite(true);
		
		//addGestureListener(DragProcessor.class, new InertiaDragAction());
			
		PImage img = app.loadImage(fileNameFingerprint);
			
		MTEllipse e1 = new MTEllipse(app, centerpoint, radiusX, radiusY);
		e1.setFillColor(skinColor);
		e1.setNoStroke(true);
		
		MTEllipse e2 = new MTEllipse(app, centerpoint, radiusX, radiusY);
		e2.setTexture(img);
		e2.setTextureEnabled(true);
		e2.setNoStroke(true);
		
		addChild(e1);
		addChild(e2);
		
		//setFillColor(skinColor);
	}
}
