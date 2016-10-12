/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MLabeledEdge.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.2  2003/01/03 16:04:11  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.1  2002/09/16 14:05:42  dennisr
// first add
//
// Revision 1.8  2002/06/04 12:54:22  reidsma
// redocumentation

package parlevink.parlegraph.model;

import java.util.logging.*;
import parlevink.parlegraph.view.TextInterface;
import parlevink.xml.*;
import java.io.*;
import java.util.*;

/**
 * This class is the implementation of a simple labeled edge.
 * A labeled edge cannot have a null label.
 * The default visualisation is parlevink.parlegraph.view.VLineTextEdge.
 * <br>
 * @see TextInterface
 */

public class MLabeledEdge extends MEdgeAdapter implements TextInterface {

/*++++++++++++++++++++*
 * Variables section. *
 *++++++++++++++++++++*/

    /** the label */
    protected String label;

/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
 
   /**
    * Creates a new MLabeledEdge.
    * afterwards, isdirty == true
    */
    public MLabeledEdge() 
    {
        super();
        label = "";
	    /* default visualisation */
	    setViewerClass("parlevink.parlegraph.view.VLineTextEdge");
        flagDirty(true);
    } 

   /**
    * Creates a new MLabeledEdge with the specified label.
    * afterwards, isdirty == true
    */
    public MLabeledEdge(String newLabel) 
    {
        this();
        setLabel(newLabel.intern());
    } 

/*+++++++++++++++++*
 * label section. *
 *+++++++++++++++++*/
    
    /**
     * Returns the label of this edge. This label is never null.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Sets the label for this edge.
     * If newLabel is null, the label stays unchanged.
     * afterwards, isdirty == true
     */
    public void setLabel(String newLabel) {
        if (newLabel != null) {
            label = newLabel;
        }
        flagDirty(true);
    }


/*+++++++++++++++++*
 * viewer section. *
 *+++++++++++++++++*/
    
    /**
     * This method is an alternative access to the label methods, especially for the visualisation.
     * See the TextInterface documentation for an explanation why this method is introduced.
     * See also getLabel
     */
    public String getText() {
        return getLabel();
    }
    
    /**
     * This method is an alternative access to the label methods, especially for the visualisation.
     * See the TextInterface documentation for an explanation why this method is introduced.
     * See also setLabel
     */
    public void setText(String newText) {
        setLabel(newText);
    }


/*+++++++++++++++++++++++++*
 * Graph functionality.    *
 *+++++++++++++++++++++++++*/
 
/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

/*----- toXMLString support -----*/

    /**
     * Adds the following attribute to the attributes of the super class:
     * <ul>
     *      <li> "label", containing the label of this MEdge
     * </ul>
     */
    protected String getAttributes() {
        return "label=\"" + getLabel() + "\" " + super.getAttributes();
    } 


/*----- readXML support -----*/

    /**
     * Reads the following attribute in extension to the attributes of the super class:
     * <ul>
     *      <li> "label", containing the label of this MEdge
     * </ul>
     */
     protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("label")) {
            setLabel((String)attributes.get("label"));
        }
    }
 
}