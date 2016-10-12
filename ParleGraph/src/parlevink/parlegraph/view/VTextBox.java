/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VTextBox.java,v $
// Revision 1.1  2006/05/24 09:00:20  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.9  2003/07/16 16:48:19  dennisr
// *** empty log message ***
//
// Revision 1.8  2003/03/28 14:51:00  dennisr
// *** empty log message ***
//
// Revision 1.7  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/01/03 16:04:13  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.5  2002/11/08 15:05:03  dennisr
// varia
//
// Revision 1.4  2002/09/30 20:16:49  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.3  2002/09/27 08:15:44  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.2  2002/09/23 07:44:44  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:52  dennisr
// first add
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.util.logging.*;

import java.util.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import parlevink.xml.*;

/**
 * VTextBox is a VNonGraphElement displaying a text box.
 * It can for example be used to display labels in another VComponent.
 * This subclass of VNonGraphElement uses the eventdelegate:
 * any event is rerouted to the delegate if one is present.
 */
public class VTextBox extends VShapedNonGraphElement
{
    protected String text;
    
    /* for drawing the text efficiently */
    protected ArrayList textLines;   //stores the different lines of the tekst...
    protected ArrayList textLineX;   //stores for every line the offset from this component's 'x' where that line should be displayed
    protected float ascent;          //stores the height of one line of text
    protected FontMetrics fontMet;
    protected Rectangle2D oneLineBounds;        
    
    public VTextBox() {
        super();
    }

    public VTextBox(String newText) {
        super();
        setText(newText);
    }

    /**
     */
    public VTextBox(double x, double y, double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }

    /**
     *  different defaults :) @@@ should still be transposed to a "getDefaultThisorthat" method.
     */
    public void preInit() {
        super.preInit();
        fillShape = false;
        paintShape = false;
        text = "";
        textLines = new ArrayList();
    }
    
    public Shape createDefaultShape() {
        return new RoundRectangle2D.Double(x, y, width, height, 3, 3); 
    }
                    
    /**
     *Changes the text of the VTextBox
     */
    public void setText(String newText) {
        text = newText.replaceAll("\\\\n", "\n");
        StringTokenizer stringTok = new StringTokenizer(text, "\n");
        textLines.clear();

        //bedenk maar eens: dit hele verhaal is ALLEENMAAR om de bounds te bepalen.
        //@@@nog steeds duur... kan dit helemaal anders?
        Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1,1));
        fontMet = g.getFontMetrics();
        ascent = fontMet.getAscent();

        double textWidth = 0;

        while (stringTok.hasMoreElements()) {
            String nextLine = (String)stringTok.nextElement();
            textLines.add(nextLine);
            oneLineBounds = fontMet.getStringBounds(nextLine, g);
            textWidth = Math.max(textWidth, oneLineBounds.getWidth()+12);
        }
        double textHeight = 12 + ascent * textLines.size();

        //resize bounds to new size...
        setMinimumSize(textWidth, textHeight);
//        if ((textWidth > width) || (textHeight > height)) {
            resize2D(VComponent.COMPONENT_ONLY,textWidth, textHeight);
//        }
    }


    /**
     * returns the text currently displayed by the textbox 
     */
    public String getText() {
        return text.replaceAll("\n", "\\\\n");
    }

    /**
     * If paintBorder is set to true, this VTextBox is painted with a border.
     @deprecated    not in use anymore. Use PaintShape instead 
     */
    public void setPaintBorder(boolean newPaintBorder) {
        //paintBorder = newPaintBorder;
    }

    /**
     * If paintBorder is set to true, this VTextBox is painted with a border.
     @deprecated    not in use anymore. Use PaintShape instead 
     */
    public boolean getPaintBorder() {
        return false;
        //return paintBorder;
    }

    /**
     * Paints the background of the VTextBox, in the form of one or more lines of text.
     */
    public void paintBackground2D(Graphics2D g2) {
        super.paintBackground2D(g2);
        paintText2D(g2);
    }

    /**
     * Paints the text of the VTextBox, in the form of one or more lines of text.
     */
    public void paintText2D(Graphics2D g2) {
        float textX = 0;
        float textY = (float)y + (float)(0.5*(height-minimumheight));
        fontMet = g2.getFontMetrics();
        float ascent = fontMet.getAscent();
        for (int i = 0; i < textLines.size(); i++) {
            String line = (String)textLines.get(i);
            oneLineBounds = fontMet.getStringBounds(line, g2);
            textX = (float)x + (float)(0.5*(width-oneLineBounds.getWidth()));//remember to put text in middle: minimumwidth is width of text
            textY = textY + ascent;
            g2.drawString(line, textX, textY);
        }
    }
    
/**************************
 * Event handling section *
 **************************/  
 

/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

/*----- toXMLString support -----*/

    /**
     * Adds the following attributes to the attributes of the super class:
     * <ul>
     *      <li> "text", containing the text contents of this VTextBox
     * </ul>
     */
    protected String getAttributes() {
        String result = super.getAttributes() + " text=\"" + getText() + "\"";
        return result;
    } 

/*----- readXML support -----*/

    /**
     * Reads the following attributes in extension to the attributes of the super class:
     * <ul>
     *      <li> "text", containing the text contents of this VTextBox
     *      <li> "paintborder", a boolean representing whether this a border should be drawn around the textbox.
     * </ul>
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("text")) {
            setText((String)attributes.get("text"));
        }
    }        
    
}