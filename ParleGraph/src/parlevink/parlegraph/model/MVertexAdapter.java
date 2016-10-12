/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MVertexAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:51  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2005/10/20 11:52:07  zwiers
// new java 1.5 version
//
// Revision 1.5  2004/08/03 14:22:36  herder
// outMEdges and inMEdges changed into LinkedHashSets, order of edges is now preserved
//
// Revision 1.4  2003/07/15 21:02:31  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/06/24 10:40:28  dennisr
// *** empty log message ***
//
// Revision 1.2  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.1  2002/09/16 14:05:42  dennisr
// first add
//
// Revision 1.7  2002/01/29 14:58:51  reidsma
// edges possible between nodes in different subgraphs of the top graph;
// MGraph and MComponent functionality extended
//
// Revision 1.6  2002/01/28 14:00:55  reidsma
// Enumerations en Vectors vervangen door Iterator en ArrayList
//
// Revision 1.5  2002/01/25 13:27:21  reidsma
// Redocumentation

package parlevink.parlegraph.model;

import parlevink.xml.*;
import parlevink.util.*;
import java.util.*;
import java.io.*;

/**
 * This class is the default implementation of the interface MVertex. The default 
 * visualisation class is set to be VEllipticVertex.
 */
public class MVertexAdapter extends MComponentAdapter implements MVertex {


/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
 
   /**
    * Creates a new MVertexAdapter.
    */
    public MVertexAdapter() 
    {
        super();
	    /* default visualisation */
	    setViewerClass("parlevink.parlegraph.view.VEllipticVertex");
	    outMEdges= new LinkedHashSet();
	    inMEdges= new LinkedHashSet();
    } 

/*+++++++++++++++++*
 * Graph section: administration of incident edges for fast access..  *
 *+++++++++++++++++*/

    /**
     * Set of incident outgoing edges
     */
    protected Set outMEdges;
    
    /**
     * Set of incident incoming edges
     */
    protected Set inMEdges;
    
    public void connectOutMEdge(MEdge e) {
        outMEdges.add(e);
        flagDirty(true);
    }
    
    public void connectInMEdge(MEdge e) {
        inMEdges.add(e);
        flagDirty(true);
    }
    
    public void disconnectOutMEdge(MEdge e) {
        outMEdges.remove(e);
        flagDirty(true);
    }
    
    public void disconnectInMEdge(MEdge e) {
        inMEdges.remove(e);
        flagDirty(true);
    }
    
    public Iterator getIncidentOutMEdges() {
        final Iterator it = outMEdges.iterator();
        return new Iterator() {
            public boolean hasNext() {
                return it.hasNext();
            }
            
            public Object next() {
                return it.next();
            }
            
            public void remove() {
                throw new RuntimeException("Removing elements not allowed when querying incident edges");
            }
        };
    }
    
    public Iterator getIncidentInMEdges() {
        final Iterator it = inMEdges.iterator();
        return new Iterator() {
            public boolean hasNext() {
                return it.hasNext();
            }
            
            public Object next() {
                return it.next();
            }
            
            public void remove() {
                throw new RuntimeException("Removing elements not allowed when querying incident edges");
            }
        };
    }
    
//    public Iterator getIncidentMEdges() {
//        //IteratorChain is efficienter dan alles samen in een list gooien en dan returnen :)
//        Iterator inIt = inMEdges.iterator();
//        Iterator outIt = outMEdges.iterator();
//        final Iterator[] its = new Iterator[2];
//        its[0] = inIt;
//        its[1] = outIt;
//        return new IteratorChain(its);
//    }


    public Iterator getIncidentMEdges() {
        Iterator inIt = inMEdges.iterator();
        Iterator outIt = outMEdges.iterator();
        final List<Iterator> its = new ArrayList<Iterator>(2);
        its.add(inIt);
        its.add(outIt);
        return new IteratorChain(its);
    }

/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/
 
/*----- no overrides of MComponent XML methods -----*/

}
