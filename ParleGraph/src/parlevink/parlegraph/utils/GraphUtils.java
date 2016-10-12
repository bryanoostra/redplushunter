/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: GraphUtils.java,v $
// Revision 1.1  2006/05/24 09:00:28  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:17  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2003/07/16 16:47:23  dennisr
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
// Revision 1.1  2002/09/16 14:08:05  dennisr
// first add
//
// Revision 1.7  2002/06/26 11:48:29  reidsma
// versie 'bevriezen'
//
// Revision 1.6  2002/06/04 12:54:21  reidsma
// minor fixes
//
// Revision 1.5  2002/05/16 09:42:05  reidsma
// major update (see changes.txt)
//
// Revision 1.4  2002/02/25 08:57:11  reidsma
// It is not allowed to add MCOmponents to an MGraph when they are already element in another MGraph. Since this version, trying to do this results in an exception.
// VLineEdge/VLineTextEdge/VEdgeAdapter: de sourceCoord en targetCoord zijn verhuisd naar de VEdgeAdapter; sourceCoord en targetCoord zijn een read-only property geworden (altijd geinitialiseerd).
//
// Revision 1.3  2002/02/19 10:28:51  reidsma
// no message
//
// Revision 1.2  2002/02/11 09:19:33  reidsma
// no message
//
// Revision 1.1.1.1  2002/02/05 15:37:36  reidsma
// no message
//

package parlevink.parlegraph.utils;

import parlevink.parlegraph.model.*;

import java.util.*;
import java.io.*;

import java.util.logging.*;

/**
 * This class contains utility functions for graphs.
 */
public class GraphUtils {

    protected static Logger logger = Logger.getLogger("parlevink.parlegraph.model.GraphUtils");

    /**
     * This method copies vertices & edges from one graph into another, prefixing all ID's 
     
     uses XML, so shared references are broken 
     * <br>
     * NB: it is a responsibility of the caller to ensure that the idPrefix does not result in duplicate id's!
     * This method may throw a DuplicateIDException.
     * The sourceGraph stays unmodified.
     no unify labels is called yet
     */
    public static void copyInto (MGraph sourceGraph, MGraph targetGraph, String idPrefix) {
        if ((sourceGraph != null) && (targetGraph != null)) {
            //create copy
            MGraph copy = null;
            try {
                copy = (MGraph)sourceGraph.getClass().newInstance();
            } catch (ClassCastException e) {
                logger.warning("copyInto error: "+e);
            } catch (InstantiationException e) {
                logger.warning("copyInto error: "+e);
            } catch (IllegalAccessException e) {
                logger.warning("copyInto error: "+e);
            } 
            
            copy.readXML(sourceGraph.toXMLString());
            //take all elements in copy, add them to targetGraph with right prefix to ID
            //first all vertices
            ArrayList vertices = new ArrayList();
            for (Iterator vertexI = copy.getMVertices(); vertexI.hasNext();) {
                MVertex nextMVertex = (MVertex)vertexI.next();
                vertices.add(nextMVertex);
            }
            //next all edges
            ArrayList edges = new ArrayList();
            for (Iterator edgeI = copy.getMEdges(); edgeI.hasNext();) {
                MEdge nextMEdge = (MEdge)edgeI.next();
                edges.add(nextMEdge);
            }
            copy.clearMGraph();
            for (int i = 0; i < vertices.size(); i++) {
                MVertex nextMVertex = (MVertex)vertices.get(i);
                nextMVertex.setID(idPrefix + nextMVertex.getID());
                targetGraph.addMVertex(nextMVertex, nextMVertex.getID());
            }
            for (int i = 0; i < edges.size(); i++) {
                MEdge nextMEdge = (MEdge)edges.get(i);
                nextMEdge.setID(idPrefix + nextMEdge.getID());
                targetGraph.addMEdge(nextMEdge, nextMEdge.getID());
            }
        }        
    }        


}
