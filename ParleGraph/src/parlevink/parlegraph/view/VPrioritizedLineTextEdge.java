/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VPrioritizedLineTextEdge.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/12/05 13:42:42  swartjes
// Added prioritized edges (so you can highlight them) and annotated vertices (so not everything has to be an arrow but can also be a key = value pair in the vertex itself, just like in Turners pictures.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2003/07/16 16:48:04  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/04/22 11:17:48  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.3  2002/09/30 20:16:48  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.2  2002/09/27 08:15:44  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.1  2002/09/16 14:08:51  dennisr
// first add
//
// Revision 1.10  2002/05/21 10:29:58  reidsma
// Maintenance
//
// Revision 1.9  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.8  2002/03/04 12:16:33  reidsma
// Delegates:
// naast de mousedelegates zijn er nu ook keydelegates. Hiervoor zijn een paar functies hernoemd (getMouseEventdelegate ipv geteventdelegate, etc)
// en een paar toegevoegd. De defaultController krijgt nu alle keyevents te horen.
//
// Revision 1.7  2002/02/05 14:35:58  reidsma
// layouting
//
// Revision 1.6  2002/02/04 16:19:34  reidsma
// debugging
//
// Revision 1.5  2002/02/01 13:03:50  reidsma
// edit controller; general maintenance
//
// Revision 1.4  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.3  2002/01/22 12:29:48  reidsma
// XML; multi-line textlabels; arrowheads; moving endpoints (first test)
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.io.*;
import parlevink.xml.*;

/**
 * An implementation showing edges as a line with a text label,
 * and prioritized, allowing it to emphasize certain edges as more
 * important than others.
 * 
 */
public class VPrioritizedLineTextEdge extends VLineTextEdge
{
    protected int priority;
    
    public void init() {
        super.init();
    }
    
    /**
     * In addition to standard VLineEdge behaviour, this method updates the VTextBox.
     */
    public void setMComponent(MComponent newObject) {
    	super.setMComponent(newObject);
        try {
            MEdge e = (MEdge)newObject;
            if (!(e instanceof MLabeledPrioritizedEdge)) {
                logger.severe("VPrioritizedLineTextEdge can only visualise MLabeledPrioritizedEdge objects!");
            } else {
            	priority = ((MLabeledPrioritizedEdge)newObject).getPriority();
            	prioritize(priority);
            	textBox.setText(((TextInterface)newObject).getText());
            }
        } catch (ClassCastException e) {
            logger.severe("VLineTextEdge can only visualise MEdge objects!" + e);
        }
    }
    
    private void prioritize(int priority) {
		setColor(Color.lightGray);
    	if (priority == 2) {
    		setColor(Color.darkGray);
    	} else
    	if (priority == 3) {
    		setColor(Color.black);
    	} else 
    	if (priority == 4) {
    		setColor(Color.blue);
    	} else 
    	if (priority > 4) {
    		setColor(Color.red);
    	}
    	textBox.setColor(getColor());
    }

}