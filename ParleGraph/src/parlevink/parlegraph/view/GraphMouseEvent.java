/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: GraphMouseEvent.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:49  dennisr
// first add
//
// Revision 1.3  2002/03/04 12:16:33  reidsma
// Redocumentation
//

package parlevink.parlegraph.view;

import java.awt.event.MouseEvent;
import java.awt.*;
import javax.swing.*;

/**
 * This class is a basic event class for graph mouse events.
 * This extension is made to allow sources which are a subclass of VComponent, instead of Component.
 */
public class GraphMouseEvent extends MouseEvent {
    
    static JLabel l = new JLabel(""); //create one small global lightweight component, because the constructor for
                                      //a keyevent does not allow null sources

    public GraphMouseEvent (VComponent newsource, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger) {
		super(l, id, when, modifiers, x, y, clickCount, popupTrigger);
		source = newsource;
	}

}
