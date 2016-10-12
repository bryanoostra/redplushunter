/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:19 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VMyTextVertex.java,v $
// Revision 1.1  2006/05/24 09:00:19  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.4  2005/12/07 16:54:50  swartjes
// Enabled multiple annotation values for a certain property
//
// Revision 1.3  2005/12/06 11:44:07  swartjes
// Added annotations as tooltip, showing when you right click on a VMyTextVertex that has annotations.
//
// Revision 1.2  2005/12/05 13:42:42  swartjes
// Added prioritized edges (so you can highlight them) and annotated vertices (so not everything has to be an arrow but can also be a key = value pair in the vertex itself, just like in Turners pictures.
//
// Revision 1.1  2005/11/25 15:28:21  swartjes
// Made a couple of graphical adjustments to clarify knowledge:
// - Vertices are drawn on top of edges in stead of below
// - Edges are light gray
// - Arrows are smaller
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
 * Custom display and added annotations
 */
public class VMyTextVertex extends VMyVertex
{
    protected String text;
    protected String annotations;
    
    /* for drawing the text efficiently */
    protected ArrayList textLines; //stores the lines of text
    protected ArrayList<String> annotationLines; //stores the lines of annotations
    protected FontMetrics fontMet;
    protected Rectangle2D oneLineBounds;        

    public void preInit() {
        super.preInit();
        text = "";
        annotations = "";
        textLines = new ArrayList();
        annotationLines = new ArrayList();
    }

    /**
     */
    public VMyTextVertex(double x, double y, double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }
        
    public VMyTextVertex() {
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
        float textY = (float)y + (float) (0.5*height+0.5*ascent) - (float) (0.5*(textLines.size() -1)*ascent); // + annotationLines.size()
        for (int i = 0; i < textLines.size(); i++) {
            String line = (String)textLines.get(i);
            oneLineBounds = fontMet.getStringBounds(line, g2);
            textX = (float)x + (float)(0.5*(width-oneLineBounds.getWidth()));//remember to put text in middle: minimumwidth is width of text
            g2.drawString(line, textX, textY);
            textY = textY + ascent;
        }/*
        g2.setColor(Color.darkGray);
        for (int i = 0; i < annotationLines.size(); i++) {
            String line = (String)annotationLines.get(i);
            oneLineBounds = fontMet.getStringBounds(line, g2);
            textX = (float)x + (float)(0.5*(width-oneLineBounds.getWidth()));//remember to put text in middle: minimumwidth is width of text
            g2.drawString(line, textX, textY);
            textY = textY + ascent;        	       	
        }*/
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
	            String newAnnotations = "";
	            if (v instanceof MAnnotatedVertex) {
            	    newAnnotations = makeAnnotationString(((MAnnotatedVertex)v).getAnnotations());
        	    } else {
        	    	newAnnotations = "";
        	    }

	            if (newText != text) {
                    prepareText(newText, newAnnotations);
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
	    String newAnnotations = "";
	    if (mcomponent instanceof MAnnotatedVertex) {
	    	newAnnotations = makeAnnotationString(((MAnnotatedVertex)mcomponent).getAnnotations()); 
	    }
  
	    if ((newText != text) || (newAnnotations != annotations)) {
            prepareText(newText, newAnnotations);
        }    
	    
    }
	
	/**
	 * Returns the annotations of this vertex
	 */
	public ArrayList<String> getAnnotations() {
		return annotationLines;
	}

    /**
     * This internal method sets the text and prepares for drawing the text
     */
    protected void prepareText(String newText, String newAnnotations) {
        text = newText;
        annotations = newAnnotations;
        StringTokenizer stringTok;
        //String combi = text.concat("\n").concat(annotations);
	    
	    Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1,1));
        double textWidth = 0;
	    fontMet = g.getFontMetrics();
	    float ascent = fontMet.getAscent();

        
        // Text
        stringTok = new StringTokenizer(text, "\n");
	    //deze g spoort NIETmet de g waar straks op getekend wordt!!!
	    textLines.clear();	
	    while (stringTok.hasMoreElements()) {
	        String nextLine = (String)stringTok.nextElement();
	        textLines.add(nextLine);
	        oneLineBounds = fontMet.getStringBounds(nextLine, g);
	        textWidth = Math.max(textWidth, oneLineBounds.getWidth()+12);
	    }
	        
	    // Annotation
	    stringTok = new StringTokenizer(annotations, "\n");
	    annotationLines.clear();

	    while (stringTok.hasMoreElements()) {
	        String nextLine = (String)stringTok.nextElement();
	        annotationLines.add(nextLine);
	        //oneLineBounds = fontMet.getStringBounds(nextLine, g);
	        //textWidth = Math.max(textWidth, oneLineBounds.getWidth()+12);
	    }
	    
	    double textHeight = 12 + ascent * (textLines.size()); // + annotationLines.size());
	    
	adjustBoundsToTextSize(textWidth, textHeight);
    }
    
    /**
     * This internal method sets the text and prepares for drawing the text
     */
    /*
    protected void prepareAnnotations(String newAnnotations) {
        annotations = newAnnotations;
        StringTokenizer stringTok = new StringTokenizer(annotations, "\n");
        annotationLines.clear();
        Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1,1));
        //deze g spoort NIETmet de g waar straks op getekend wordt!!!
        double textWidth = 0;
        fontMet = g.getFontMetrics();
        float ascent = fontMet.getAscent();

        while (stringTok.hasMoreElements()) {
            String nextLine = (String)stringTok.nextElement();
            annotationLines.add(nextLine);
            oneLineBounds = fontMet.getStringBounds(nextLine, g);
            textWidth = Math.max(textWidth, oneLineBounds.getWidth()+12);
        }
        
        double textHeight = 12 + ascent * annotationLines.size();
	adjustBoundsToTextSize(textWidth, textHeight);
    } */   

    protected String makeAnnotationString(Map<String,Set<Object>> annotationMap) {
    	StringBuilder sb = new StringBuilder();

    	if (annotationMap != null) {
        	for (Map.Entry<String,Set<Object>> e: annotationMap.entrySet()) {
        		Set<Object> cv = e.getValue();
        		
        		StringBuilder nwValString = new StringBuilder();
        		for (Object o: cv) {
        			if (nwValString.length() <1) {
        				nwValString.append(o);
        			} else {        			
        				nwValString.append(", ").append(o);
        			}
        		}
        		sb.append(e.getKey()).append(" = ").append(nwValString).append('\n');
        	}
    	}
        return sb.toString();
    }
       
    protected void adjustBoundsToTextSize(double w, double h) {
        setMinimumSize(w, h);
        setBounds2D(x,y,Math.max(w, width), Math.max(h, height));
    }
    	
    
/**************************
 * Event handling section *
 **************************/  

}