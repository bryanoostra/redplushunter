package vs.plotagent.ui.multitouch.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import vs.plotagent.ui.multitouch.controller.VSTMultitouchController;


/**
 * A JFrame encapsulating a VST multitouch application.
 * 
 * @author swartjes
 *
 */
public class VSTMultitouchFrame extends JFrame {

		private final VSTMultitouchApplication instance;
		
		public VSTMultitouchFrame(String domain) {
			this(domain, null);
		}
		
        public VSTMultitouchFrame(String domain, VSTMultitouchController controller) {
        	
        	this.setTitle("VST Multitouch Interface");
        	this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        	this.setUndecorated(true);
        	
            //Should be called early before initializing opengl stuff
        	this.setVisible(true);
               
        	//this.setLayout(new BorderLayout());
       
                //Create our mt4j applet
        	instance = new VSTMultitouchApplication(domain, controller);
        	instance.frame = this; //Important for registering the Windows 7 Touch input
        	instance.init();
             
        	//Add MT4j applet
        	//JPanel pane = new JPanel(new GridLayout(0,1));
        	//pane.add(instance);
        	getContentPane().add(instance);
       
        	/////////MEnu
        	//So that the menu will overlap the heavyweight opengl canvas
        	//JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        	

        	this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        	//this.pack();   // does layout of components.

        }
        
        public VSTMultitouchApplication getVSTMultitouchInterface() {
        	return instance;
        }
        
        public static void main(String[] args) {
        	
        	VSTMultitouchFrame f = new VSTMultitouchFrame("red");
        }

}