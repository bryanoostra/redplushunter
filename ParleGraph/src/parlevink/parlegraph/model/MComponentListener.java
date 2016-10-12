/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MComponentListener.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:05:41  dennisr
// first add
//
// Revision 1.4  2002/05/16 09:53:29  reidsma
// Redocumentation
//

package parlevink.parlegraph.model;

import java.util.EventListener;
import parlevink.parlegraph.view.GraphEvent;

/** 
 * The interface MComponentListener can be used to keep visualisations of MComponents
 * informed of changes in the MComponent.
 * It is not guaranteed however that a MComponentListener will always be informed immediately about all changes
 * in the MComponent.
 */
public interface MComponentListener extends EventListener {

	/**
     * This method is called to inform the MComponentListener that a change occurred in a
     * MComponent for which it registered as a listener.
	 */
	public void mcomponentChanged(GraphEvent ge);

}
