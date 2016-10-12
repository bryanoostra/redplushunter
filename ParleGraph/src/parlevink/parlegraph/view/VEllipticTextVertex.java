/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VEllipticTextVertex.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.2  2005/11/11 14:57:57  swartjes
// Removed a bug with ForceLayout and made vertices non-resizable (annoying when trying to drag)
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2003/12/21 12:13:14  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/07/29 12:10:50  dennisr
// GUI quirks
//
// Revision 1.5  2003/07/16 16:47:46  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.2  2002/09/27 08:15:44  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.7  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.6  2002/02/01 13:03:51  reidsma
// edit controller; general maintenance
//
// Revision 1.5  2002/01/28 14:00:45  reidsma
// Enumerations en Vectors vervangen door Iterator en ArrayList
//
// Revision 1.4  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.3  2002/01/22 12:29:49  reidsma
// XML; multi-line textlabels; arrowheads; moving endpoints (first test)

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;

/**
 * VEllipticTextVertex visualises a vertex using a circle and a text.
 * This viewer can only display MComponents implementing the TextInterface class.
 * The displayed text is determined by calling getText on the MComponent.
 */
public class VEllipticTextVertex extends VEllipticVertex
{
    protected String text;
    
    /* for drawing the text efficiently */
    protected ArrayList textLines; //stores the lines of text
    protected FontMetrics fontMet;
    protected Rectangle2D oneLineBounds;        

    public void preInit() {
        super.preInit();
        text = "";
        textLines = new ArrayList();
    }

    /**
     */
    public VEllipticTextVertex(double x, double y, double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }
        
    public VEllipticTextVertex() {
        super();
    }
        
    /** Ivo Swartjes: added because resize is never needed
     * 
     */
    protected int getDefaultResizeMode() {
    	return super.getDefaultResizeMode() & ~RM_ALLOW_RESIZE;
    }
    
    /**
     * Paints the background of the VEllipticTextVertex, in the form of an elliptic border with a text in it.
     */
    public void paintBackground2D(Graphics2D g2) {
        super.paintBackground2D(g2);
        paintText2D(g2);
    }

    /**
     * Paints the text of the VEllipticTextVertex, in the form of one or more lines of text.
     */
    public void paintText2D(Graphics2D g2) {
        fontMet = g2.getFontMetrics();
        float ascent = fontMet.getAscent();
        float textX = 0;
        float textY = (float)y + (float) (0.5*height+0.5*ascent) - (float) (0.5*(textLines.size()-1)*ascent);
        for (int i = 0; i < textLines.size(); i++) {
            String line = (String)textLines.get(i);
            oneLineBounds = fontMet.getStringBounds(line, g2);
            textX = (float)x + (float)(0.5*(width-oneLineBounds.getWidth()));//remember to put text in middle: minimumwidth is width of text
            g2.drawString(line, textX, textY);
            textY = textY + ascent;
        }
    }


    /**
     * In addition to standard VVertex behaviour, this method sets the text to be displayed.
     * Adjusting the bounding box to the text is also done here.
     */
    public void setMComponent(MComponent newObject) {
        if (newObject == null) {
            return;
        }
        try {
            MVertex v = (MVertex)newObject;
            if (!(v instanceof TextInterface)) {
                logger.severe("VEllipticTextVertex can only visualise objects implementing the TextInterface!" );
            } else {
                super.setMComponent(newObject);
	            String newText = ((TextInterface)mcomponent).getText();
        	    if (newText != text) {
                    prepareText(newText);
                }
            }
        } catch (ClassCastException e) {
            logger.severe("VEllipticTextVertex can only visualise MVertex objects!");
        }
    }
    
	/**
     * Whenever the vertex changes, the displaytext is updated. 
     * Adjusting the bounding box to the text is also done here.
	 */
	public void mcomponentChanged(GraphEvent ge) {
	    super.mcomponentChanged(ge);
	    String newText = ((TextInterface)mcomponent).getText();
	    if (newText != text) {
            prepareText(newText);
        }
    }

    /**
     * This internal method sets the text and prepares for drawing the text
     */
    protected void prepareText(String newText) {
        text = newText;
        StringTokenizer stringTok = new StringTokenizer(text, "\n");
        textLines.clear();
        Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1,1));
        //deze g spoort NIETmet de g waar straks op getekend wordt!!!
        double textWidth = 0;
        fontMet = g.getFontMetrics();
        float ascent = fontMet.getAscent();

        while (stringTok.hasMoreElements()) {
            String nextLine = (String)stringTok.nextElement();
            textLines.add(nextLine);
            oneLineBounds = fontMet.getStringBounds(nextLine, g);
            textWidth = Math.max(textWidth, oneLineBounds.getWidth()+12);
        }
        
        double textHeight = 12 + ascent * textLines.size();
	adjustBoundsToTextSize(textWidth, textHeight);
    }

    protected void adjustBoundsToTextSize(double w, double h) {
        setMinimumSize(w, h);
        setBounds2D(x,y,Math.max(w, width), Math.max(h, height));
    }
    	
    
/**************************
 * Event handling section *
 **************************/  

}