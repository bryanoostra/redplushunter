/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:17 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: GraphKeyEvent.java,v $
// Revision 1.1  2006/05/24 09:00:17  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:49  dennisr
// first add
//
// Revision 1.1  2002/03/04 12:16:46  reidsma
// Redocumentation

package parlevink.parlegraph.view;

import java.awt.event.KeyEvent;
import java.awt.*;
import javax.swing.*;

/**
 * This class is a basic event class for graph key events.
 * This extension to KeyEvent is made to allow sources which are a subclass of VComponent instead of Component.
 */
public class GraphKeyEvent extends KeyEvent {
    
    static JLabel l = new JLabel(""); //create one small global lightweight component, because the constructor for
                                      //a keyevent does not allow null sources
    
    public GraphKeyEvent (VComponent newsource, int id, long when, int modifiers, int keyCode, char keyChar) {
		super(l, id, when, modifiers, keyCode, keyChar);  
		source = newsource;
	}

}
