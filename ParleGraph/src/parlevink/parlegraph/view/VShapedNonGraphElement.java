/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VShapedNonGraphElement.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.9  2003/07/16 16:48:19  dennisr
// *** empty log message ***
//
// Revision 1.8  2003/04/22 11:17:48  dennisr
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
// Revision 1.5  2002/11/04 09:40:40  dennisr
// *** empty log message ***
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
import parlevink.xml.*;
import java.util.logging.*;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;

/**
 * A VShapedNonGraphElement is a simple VNonGraphElement in which it is possible to set a Shape
 * used to draw the element.
 * <br>
 * When the shape is changed, the bounding box is automatically recalculated, but the (x,y) 
 * location of the element stays unchanged.
 * <br> 'contains' is defined by a contains method on the Shape
 * Resizing and transforming the component resizes and transforms the shape as well.
 * <br>
 * Used f.i. in the VMultiLineEdge, where it displays a waypoint, or in the subclass ArrowHead 
 * used to display the arrow head on an edge.
 */
public class VShapedNonGraphElement extends VNonGraphElement {
    
    private Shape shape;
    protected boolean paintShape;
    protected boolean fillShape;
    
    public VShapedNonGraphElement() {
        super();
    }

    public void preInit() {
        super.preInit();
        fillShape = true;
        paintShape = true;
        width = 10;
        height = 10;
    }

    /**
     */
    public VShapedNonGraphElement(double x, double y, double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }

    /**
     * Creates a default shape
     */
    public void init() {
        super.init();
        shape = createDefaultShape();
    }

    
    /**
     * Depends on preInit being called already; will be called from init()
     */
    public Shape createDefaultShape() {
        Shape result =  new Ellipse2D.Double(x, y, width, height);
        return result;
    }
        
    /** 
     * recalculates the bounding box needed to hold the shape;
     * then changes the shape.
     * no null allowed!! (throws exception)
     */
    public void setShape(Shape newShape) {
        if (shape == null)
            throw new NullPointerException("no null shape allowed in VShapedNonGraphElement");
        Rectangle2D rv = newShape.getBounds2D();
        setBounds2D((double)rv.getX(), (double)rv.getY(), (double)rv.getWidth(), (double)rv.getHeight());
        shape = newShape;
    }
    
    /**
     * returns the shape. The result should NOT be manipulated directly!
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * If paintShape is set to true, the shape is painted.
     */
    public void setPaintShape(boolean newPaintShape) {
        paintShape = newPaintShape;
    }

    /**
     * If paintShape is set to true, the shape is painted
     */
    public boolean getPaintShape() {
        return paintShape;
    }

    /**
     * If fillShape is set to true, the shape is filled.
     */
    public void setFillShape(boolean newFillShape) {
        fillShape = newFillShape;
    }

    public boolean getFillShape() {
        return fillShape;
    }

    
    /**
     * true iff shape contains p....
     */
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }
    /**
     * should replace the shape
     */
    public void recalculateShape() {
        super.recalculateShape();
        Rectangle2D r = shape.getBounds2D();
        double sx = r.getX();
        double sy = r.getY();
        double sw = r.getWidth();
        double sh = r.getHeight();
        AffineTransform at;
        if ((sx != x) || (sy!=y)) {
            at = AffineTransform.getTranslateInstance(x - sx, y - sy);
            shape = at.createTransformedShape(shape);
        }
        if ((sw != width) || (sh!=height)) {
            at = AffineTransform.getScaleInstance(width/sw, height/sh);
            shape = at.createTransformedShape(shape);
        }
    }

	/**
     * paint the Shape
	 */
    public void paintBackground2D(Graphics2D g2){
        super.paintBackground2D(g2);
        if (paintShape) {
            g2.draw(shape);
		}
        if (fillShape) {
            g2.fill(shape);
		}
    }

    /**
     * Adds the following attributes to the attributes of the super class:
     * <ul>
     *      <li> "paintshape"
     *      <li> "fillshape"
     * </ul>
     */
    protected String getAttributes() {
        String result = super.getAttributes() + " paintshape=\"" + paintShape + "\""
                                              + " fillshape=\"" + fillShape + "\"";
        return result;
    } 


    /**
     * Reads the following attributes in extension to the attributes of the super class:
     * <ul>
     * </ul>
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("paintshape")) {
            boolean pb = ((String)attributes.get("paintshape")).equals("true");
            setPaintShape(pb);
        }
        if (attributes.containsKey("fillshape")) {
            boolean pb = ((String)attributes.get("fillshape")).equals("true");
            setFillShape(pb);
        }
    }        
}