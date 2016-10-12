/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:17 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VComponentMovedListener.java,v $
// Revision 1.1  2006/05/24 09:00:17  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.2  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.1.1.1  2001/12/21 14:11:25  reidsma
// added
//
// Revision 1.3  2001/12/21 10:52:43  reidsma
// moved package
//
// Revision 1.2  2001/11/20 10:24:27  zwiers
// dos->unix, added file headers
//

package parlevink.parlegraph.view;

import java.util.EventListener;

/** 
 * The interface VComponentMovedListener can be used to keep visualisations 
 * informed of changes in the location of another visualisation.
 * This interface can for example be used to make an edge move with its vertices.
 */
public interface VComponentMovedListener  extends EventListener {

	/**
     * This method will be called to inform the VComponentMovedListener that 
     * the visualsiation for which it registered as a listener has moved.
	 */
	public void vcomponentMoved(GraphEvent ge);

}