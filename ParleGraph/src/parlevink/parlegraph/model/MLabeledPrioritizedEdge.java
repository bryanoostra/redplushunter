/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MLabeledPrioritizedEdge.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/12/05 13:42:40  swartjes
// Added prioritized edges (so you can highlight them) and annotated vertices (so not everything has to be an arrow but can also be a key = value pair in the vertex itself, just like in Turners pictures.
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
 * Adds a priority to a labeled edge. Priority could for instance be used to 
 * filter visualisation components or determine color or font of the edge.
 * The default visualisation is parlevink.parlegraph.view.VPrioritizedLineTextEdge.
 * <br>
 * @see TextInterface
 */

public class MLabeledPrioritizedEdge extends MLabeledEdge {

/*++++++++++++++++++++*
 * Variables section. *
 *++++++++++++++++++++*/

    /** the priority */
    protected int priority;

/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
 
   /**
    * Creates a new MLabeledEdge.
    * afterwards, isdirty == true
    */
    public MLabeledPrioritizedEdge() 
    {
        super();
        setPriority(1);
	    /* default visualisation */
	    setViewerClass("parlevink.parlegraph.view.VPrioritizedLineTextEdge");
        flagDirty(true);
    } 
    
    public MLabeledPrioritizedEdge(String newLabel) {
    	this();
        setLabel(newLabel.intern());
    	setPriority(priority);    	
    }

    public MLabeledPrioritizedEdge(String newLabel, int priority) {
    	this();
        setLabel(newLabel.intern());
    	setPriority(priority);
    }

/*+++++++++++++++++++*
 * priority section. *
 *+++++++++++++++++++*/
    public int getPriority() {
    	return priority;
    }
    
    public void setPriority(int priority) {
    	this.priority = priority;
    }
    
    

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
        return "priority=\"" + getPriority() + "\" " + super.getAttributes();
    } 


/*----- readXML support -----*/

    /**
     * Reads the following attribute in extension to the attributes of the super class:
     * <ul>
     *      <li> "priority", containing the priority of this MEdge
     * </ul>
     */
     protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        if (attributes.containsKey("priority")) {
            setPriority((Integer)attributes.get("priority"));
        }
    }
 
}