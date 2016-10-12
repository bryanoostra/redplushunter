/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */
 
// Last modification by: $Author: swartjes $
// $Log: CGraphController.java,v $
// Revision 1.1  2006/05/24 09:00:28  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:22  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2003/07/29 12:10:41  dennisr
// GUI quirks
//
// Revision 1.1  2003/07/16 16:48:55  dennisr
// *** empty log message ***
//
//

package parlevink.parlegraph.control;

import parlevink.parlegraph.view.*;
import parlevink.parlegraph.utils.*;
import javax.swing.ActionMap;

/**
 * Generic interface for controllers for visualised graphs. A controller
 * is supposed to handle interactive editing of the graph such as adding
 * or removing elements.
 * <p>
 * Furthermore a CGraphController defines an ActionMap containing global 
 * actions such as 'load' or 'save', which can be put in a toolbar or 
 * menubar by the application.
 * <p>NB: never connect a VGraph to more than one controller. You will never 
 * know where your events end up being processed.
 */
public interface CGraphController {

    /**
     * Initialises the CGraphController with a new VGraph. If the controller was 
     * connected to another VGraph all connections to that old VGraph and its MComponents 
     * are broken.
     */
    public void setVGraph(VGraph newVGraph);

    /**
     * Returns the VGraph currently controlled by this CGraphController
     */
    public VGraph getVGraph();

    /**
     * Returns an ActionMapMap of 'global' action, i.e. actions that are not 
     * specifically targeted at a specific graph element. 
     * It is the responsibility of the application to create for example
     * a menu- or toolbar for this ActionMapMap.
     */
    public ActionMapMap getGlobalActionMap();
}
