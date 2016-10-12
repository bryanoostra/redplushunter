/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MAnnotatedVertex.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.2  2005/12/07 16:54:50  swartjes
// Enabled multiple annotation values for a certain property
//
// Revision 1.1  2005/12/05 13:42:42  swartjes
// Added prioritized edges (so you can highlight them) and annotated vertices (so not everything has to be an arrow but can also be a key = value pair in the vertex itself, just like in Turners pictures.
//
// Revision 1.2  2005/11/25 15:28:22  swartjes
// Made a couple of graphical adjustments to clarify knowledge:
// - Vertices are drawn on top of edges in stead of below
// - Edges are light gray
// - Arrows are smaller
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.2  2003/01/03 16:04:12  dennisr
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
// Revision 1.7  2002/05/16 09:47:34  reidsma
// redocumentation

package parlevink.parlegraph.model;

import parlevink.parlegraph.view.TextInterface;
import parlevink.xml.*;
import java.io.*;
import java.util.*;

/**
 * This class is the implementation of a labeled vertex with annotations.
 * MLabeledVertex can be visualised using a VMyTextVertex.
 */
public class MAnnotatedVertex extends MLabeledVertex {

/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
 
    protected Map<String, Set<Object>> annotations;
     
    /**
     * Creates a new MAnnotatedVertex.
     * afterwards, isdirty == true
     */
     public MAnnotatedVertex() 
     {
         super();
         annotations = new HashMap<String,Set<Object>>();
 	    /* default visualisation */
 	    setViewerClass("parlevink.parlegraph.view.VMyTextVertex");
         flagDirty(true);
     } 

    /**
     * Creates a new MAnnotatedVertex with the specified label.
     * afterwards, isdirty == true
     */
     public MAnnotatedVertex(String newLabel) {
         this();
         setLabel(newLabel.intern());
     } 
    
/*+++++++++++++++++*
 * label section. *
 *+++++++++++++++++*/
    
    /**
     * Returns the annotations of this vertex. 
     */
    public Map<String, Set<Object>> getAnnotations() {
    	return annotations;
    }
    
    /**
     * Puts an annotation in the annotations of this vertex
     * @param label the label of the annotation
     * @param contents the contents of the annotation
     */
    public void putAnnotation(String label, Object contents) {
    	Set<Object> cont = annotations.get(label);
    	if (cont != null) {
    		cont.add(contents);
    	} else {
    		Set<Object> nwSet = new HashSet<Object>();
    		nwSet.add(contents);
    		annotations.put(label, nwSet);
    	}
        //annotations.put(label, contents);
        flagDirty(true);
    }

}
