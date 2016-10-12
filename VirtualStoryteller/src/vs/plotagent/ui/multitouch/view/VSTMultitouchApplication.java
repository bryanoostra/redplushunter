package vs.plotagent.ui.multitouch.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.font.IFont;
import org.mt4j.util.MTColor;

import vs.debug.LogFactory;
import vs.plotagent.ui.multitouch.controller.VSTMultitouchController;

/**
 * The VST Multitouch application. Uses MT4J as a framework.
 * 
 * @author swartjes
 *
 */
public class VSTMultitouchApplication extends MTApplication  {
	
	private static final long serialVersionUID = 1L;
	
	private VSTScene scene; 
	
	private VSTMultitouchController controller;
	
	private boolean isStartedUp = false;
	private String domain;
	
	private IFont categoryFont;
	private IFont actionFont;
	
	private static MTColor white = new MTColor(255,255,255);
	private static MTColor transparent = new MTColor(255,255,255, MTColor.ALPHA_FULL_TRANSPARENCY);
	
	private Logger logger;
	
	/**
	 * The multitouch application constructor. 
	 * 
	 * @param VSTdomain the domain for which to run the application
	 * @param controllr the controller of the application
	 */
	public VSTMultitouchApplication(String VSTdomain, VSTMultitouchController controllr) {
		super();
		
		domain = VSTdomain;
		logger = LogFactory.getLogger(this);
		
		// I create them here already due to an unexplainable "JVM Access Violation" when I tried
		// to create them in ActionButton and ActionCategoryButton upon refreshing those buttons.		
		
		controller = controllr;
	}
		
	public VSTMultitouchController getVSTMultitouchController() {
		return controller;
	}
	
	@Override
	public void startUp() {
		scene = new VSTScene(this, "VST Scene");
		this.addScene(scene);
		
		isStartedUp = true;
		loadDomain();
	}
	
	public boolean isStartedUp() {
		return isStartedUp;
	}
	
	/**
	 * Loads the given domain.
	 */
	public void loadDomain() {
		if (scene!= null) {
			if (controller == null) {
				scene.loadDomain("red");
			} else {
				scene.loadDomain();
			}
		} // else: the interface retrieves the domain from the model.
		else {
			System.out.println("Scene = null!");
		}
	}
	
	public String getDomain() {
		return domain;
	}

}



