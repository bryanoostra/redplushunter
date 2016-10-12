/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MousePressListener.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2005/07/10 14:08:18  zwiers
// listeners added
//



package parlevink.util;
import java.util.*;

/**
 * A MousePressedListener is a kind of a "sub interface" of MouseListener:
 * Only mousePressed events are forwarded, and the mouse position (x,y) is 
 * provided as argument,
 * This event is fired by InputStateTrackers, that record the remaining 
 * MouseEvent information.
 */
public interface MousePressListener
{

   public void mousePressed(int x, int y);
      
}