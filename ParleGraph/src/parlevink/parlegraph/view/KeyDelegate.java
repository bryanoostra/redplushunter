/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: KeyDelegate.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:49  dennisr
// first add
//
// Revision 1.1  2002/03/04 12:16:46  reidsma
// Delegates:
// naast de mousedelegates zijn er nu ook keydelegates. Hiervoor zijn een paar functies hernoemd (getMouseEventdelegate ipv geteventdelegate, etc)
// en een paar toegevoegd. De defaultController krijgt nu alle keyevents te horen.
//
// Revision 1.1  2002/02/01 13:04:09  reidsma
// edit controller; general maintenance
//

package parlevink.parlegraph.view;

/**
 * Interface for eventDelegates for GraphKeyEvents. See VComponent.
 * All event methods in this interface have a parameter (VComponent ref)
 * indicating which VComponent delegated the event.
 */
public interface KeyDelegate {

/*******************************
 * Delegate Graph Key Events *
 *******************************/

    public void keyPressed(VComponent ref, GraphKeyEvent e);
    public void keyReleased(VComponent ref, GraphKeyEvent e);
    public void keyTyped(VComponent ref, GraphKeyEvent e);
}