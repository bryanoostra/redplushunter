/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MouseMoveListener.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2005/07/10 14:08:18  zwiers
// listeners added
//



package parlevink.util;
import java.util.*;

/**
 * A MouseMoveListener is a kind of a "sub interface" of MouseMotionListener:
 * Only mouseMoved events are forwarded, and the actual drag delta, since the last 
 * mouse release or move event, is provided via the arguments.
 * This event is fired by InputStateTrackers, that record the remaining 
 * MouseEvent information.
 */ 
public interface MouseMoveListener
{

   public void mouseMoved(int deltaX, int deltaY);
      
}