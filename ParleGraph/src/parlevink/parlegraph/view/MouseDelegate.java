/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MouseDelegate.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:49  dennisr
// first add
//
// Revision 1.1  2002/02/01 13:04:09  reidsma
// edit controller; general maintenance
//

package parlevink.parlegraph.view;

/**
 * Interface for eventDelegates for GraphMouseEvents. See VComponent.
 * All event methods in this interface have a parameter (VComponent ref)
 * indicating which VComponent delegated the event.
 */
public interface MouseDelegate {

/*******************************
 * Delegate Graph Mouse Events *
 *******************************/

    public void mouseClicked(VComponent ref, GraphMouseEvent e);
    public void mouseEntered(VComponent ref, GraphMouseEvent e);
    public void mouseExited(VComponent ref, GraphMouseEvent e);
    public void mousePressed(VComponent ref, GraphMouseEvent e);
    public void mouseReleased(VComponent ref, GraphMouseEvent e);
    public void mouseDragged(VComponent ref, GraphMouseEvent e);
    public void mouseMoved(VComponent ref, GraphMouseEvent e); 
}