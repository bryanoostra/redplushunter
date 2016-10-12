/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: GraphEvent.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:48  dennisr
// first add
//
// Revision 1.2  2002/01/25 15:23:09  reidsma
// Redocumentation
//
// Revision 1.1.1.1  2001/12/21 14:11:25  reidsma
// added
//
// Revision 1.3  2001/12/21 10:52:44  reidsma
// moved package
//
// Revision 1.2  2001/11/20 10:24:28  zwiers
// dos->unix, added file headers
//

package parlevink.parlegraph.view;

import java.util.EventObject;

/**
 * This class is a basic event class for graph events. At the moment it's just a tagging class.
 * These events contain no information other than the source yet.
 */
public class GraphEvent extends EventObject {
	public GraphEvent(Object newSource) {
		super(newSource);
	}

	public String toString() {
		return "generic GraphEvent: " + super.toString();
	}
}
