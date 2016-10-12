/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:17 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VLineTextEdge.java,v $
// Revision 1.1  2006/05/24 09:00:17  swartjes
// Parlevink and Parlegraph code.
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
 * An implementation showing edges as a line with a text label.
 * To show the label, a VLineTextEdge contains a VTextBox.
 * To make it easier to recognize which textbox belongs to this edge, 
 * the focus decoration of the textbox is also painted when the 
 * focusdecoration of the edge is painted.
 *
 */
public class VLineTextEdge extends VMultiLineEdge
{
    protected VTextBox textBox;
    
    double labelOffsetX = 0;
    double labelOffsetY = 0;

    public void init() {
        super.init();
        textBox = new VTextBox("") { //matig... veranderen@@@
    		public void paintFocusDecoration(Graphics2D g) {
			    super.paintFocusDecoration(g);
			    VComponent p = getParent();
			    if ((p != null) && hasFocus() && !p.hasFocus()) 
    				p.paintFocusDecoration(g);
		    }
    	};
        textBox.setMouseEventDelegate(this, GraphMouseEvent.MOUSE_CLICKED);
        for (int eventNr = GraphKeyEvent.KEY_FIRST; eventNr <= GraphKeyEvent.KEY_LAST; eventNr++)
            textBox.setKeyEventDelegate(this, eventNr);
        textBox.setMoveRestrictions(ResizableVComponent.MR_RESIZE_PARENT);
        addVComponent(textBox);
        //set textlabel at 'average center'
        textBox.setLocation2D(centerx - (textBox.getWidth()  / 2) , centery - (textBox.getHeight()  / 2)); 
        textBox.addVComponentMovedListener(this);
    }
    
    /**
     * In addition to standard VLineEdge behaviour, this method updates the VTextBox.
     */
    public void setMComponent(MComponent newObject) {
        if (newObject == null) {
            super.setMComponent(newObject);
            textBox.setText("");
            return;
        }
        try {
            MEdge e = (MEdge)newObject;
            if (!(e instanceof TextInterface)) {
                logger.severe("VLineTextEdge can only visualise objects implementing the TextInterface!");
            } else {
                super.setMComponent(newObject);
                textBox.setText(((TextInterface)newObject).getText());
            }
        } catch (ClassCastException e) {
            logger.severe("VLineTextEdge can only visualise MEdge objects!" + e);
        }
    }
    
	/**
     * Whenever the vertex changes, the VTextBox is updated. 
	 */
	public void mcomponentChanged(GraphEvent ge) {
	    super.mcomponentChanged(ge);
	    if (mcomponent != null) {
	        double oldW = textBox.getWidth();
	        double oldH = textBox.getHeight();
            textBox.setText(((TextInterface)mcomponent).getText());
	        double newW = textBox.getWidth();
	        double newH = textBox.getHeight();
	        labelOffsetX = labelOffsetX + (oldW - newW) / 2;
	        labelOffsetY = labelOffsetY + (oldH - newH) / 2;
        }
	    //maintain relative location of textbox wrt 'average center'
        textBox.setLocation2D(centerx + labelOffsetX, centery + labelOffsetY);
    }

    public void vcomponentMoved(GraphEvent ge) {
        //get number of line part where label is closest to; get relative location between endpoints of this line
        super.vcomponentMoved(ge);
        if (ge.getSource() == textBox) { //probleem: in verplaatsen van supergraaf gaat het label te hard mee :o) dat moet in translate2D opgevangen worden
            labelOffsetX = textBox.getX() - centerx;
            labelOffsetY = textBox.getY() - centery;
        } else {
            textBox.setLocation2D( centerx + labelOffsetX , centery + labelOffsetY );
        }
    }


    /**
     * Translates the VComponent. The label should be reset afterwards.....
     */
    public void translate2D(int mode, double tx, double ty) {
        //store old labelofffset
        double oldLabelOffsetX = labelOffsetX;
        double oldLabelOffsetY = labelOffsetY;
        super.translate2D(mode, tx, ty);
        //restore old labeloffset (is now wrong due to recursive translation)
        labelOffsetX = oldLabelOffsetX;
        labelOffsetY = oldLabelOffsetY;
        textBox.setLocation2D( centerx + labelOffsetX , centery + labelOffsetY );
        recalculateShape();
    }

/**paint*/
public void paintFocusDecoration(Graphics2D g) {
	super.paintFocusDecoration(g);
	if (!textBox.hasFocus())
		textBox.paintFocusDecoration(g);	
	
}
    
/**************************
 * Event handling section *
 **************************/  
 
   
/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        double lx = 0;
        double ly = 0;
        
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("labelx")) {
            try {
                lx = Double.parseDouble((String)attributes.get("labelx"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("labelx"));
            }
        }
        if (attributes.containsKey("labely")) {
            try {
                ly = Double.parseDouble((String)attributes.get("labely"));
            } catch (NumberFormatException e) {
                logger.severe("coordinates in XML should be double values. Wrong value: " + attributes.get("labely"));
            }
        }
        
        textBox.setLocation2D(lx,ly);
    }        

    /**
     * label x & y
     */
    protected String getAttributes() {
        String result = super.getAttributes() + 
                        " labelx=\"" +
                        textBox.getX() +
                        "\" labely=\"" +
                        textBox.getY() +
                        "\"";
        return result;
    } 

}