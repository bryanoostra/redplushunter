/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VGraph.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2003/07/16 16:47:47  dennisr
// *** empty log message ***
//
// Revision 1.2  2003/03/03 17:56:07  dennisr
// *** empty log message ***
//
// Revision 1.1  2002/09/16 14:08:51  dennisr
// first add
//
// Revision 1.4  2002/02/04 17:17:33  reidsma
// layout class added
//
// Revision 1.3  2002/01/29 14:58:07  reidsma
// debugging
//
// Revision 1.2  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.1.1.1  2001/12/21 14:11:25  reidsma
// added
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.layout.*;

/**
 * VGraph is a visualisation of an MGraph.
 * It is a VVertex with a rectangular shape, 
 * that is intended to contain other VVertex and VEdge components.
 * <p>It is the responsibility of a VGraph to create the necessary
 * VComponents to visualize all elements of the MGraph and add them
 * as children to its container.
 * <p>
 * Layout of the VGraph is delegated to a VGraphLayout object.
 * This layout can be executed in two ways: <code>fullLayout</code>
 * will layout the graph from scratch; <code>doLayout</code> might
 * take into account which elements were already positioned by 
 * previous layout operations.
 * <p>On construction, a VGraph already has a default layouter available
 * that uses an extremely stupid layout algorithm. (basically just
 * place anything anywhere).
 * The different layout classes and their helper classes are contained 
 * in the package <code>parlevink.parlegraph.layout</code>.
 */
public interface VGraph extends VVertex
{
    /**
     * Sets which VGraphLayout should take care of layout algorithms.......
     * Not necessary to call this if you use default layouting.
     */
    public void setLayout(VGraphLayout theLayout);
 
    /**
     * Makes the layouter do its job...
     */
    public void doLayout();
    
    /**
     * Makes the layouter do its job...
     */
    public void fullLayout();
    
}