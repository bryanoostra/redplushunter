/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MEdgeAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:51  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.5  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/03/03 17:56:06  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/01/03 16:04:11  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.2  2002/09/16 15:16:24  dennisr
// documentatie
//
// Revision 1.1  2002/09/16 14:05:42  dennisr
// first add
//
// Full redocumentation
//

package parlevink.parlegraph.model;

import java.util.logging.*;
import parlevink.xml.*;
import java.io.*;
import java.util.*;

/**
 * This class is the default implementation of the MEdge interface.
 * The default visualisation class is VLineEdge.
 */

public class MEdgeAdapter extends MComponentAdapter implements MEdge {

/*++++++++++++++++++++*
 * Variables section. *
 *++++++++++++++++++++*/

    private MVertex source;  //child classes cannot change the source or target directly, because then the administration of resolving and of registering at endpoints will go wrong.
    private MVertex target;
    private boolean directed;

    /* for lazy end-vertex evaluation */
    private boolean sourceIsResolved, targetIsResolved; //true if source or target pointer contains the correct pointer to the vertex
    //NB: if source or target is resolved but still the pointers to the MVertex is null, the edge has no source or target.
    //see documentation of MEdge interface for the resolving mechanism!!!
    
    //those variables only have meaning iff the vertex is NOT resolved:
    private String sourceID, targetID;
    private int sourceUp, targetUp;  //how many supergraphs you have to follow upwards before you start following the path downwards to the end vertex
    private ArrayList sourcePath, targetPath;  //contain ID-list of subgraphs you must follow downwards to find subgraph in which source or target vertex resides

/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
 
   /**
    * Creates a new MEdgeAdapter.
    * afterwards, flagDirty == true
    */
    public MEdgeAdapter() 
    {
        super(); 
        
        /* default settings */
        source = null;
        target = null;
        directed  = true;
        
	    /* default visualisation */
	    setViewerClass("parlevink.parlegraph.view.VLineEdge");
	    
	    /* lazy end vertex evaluation */
        sourceID = ""; 
        targetID = "";
        sourceUp = 0;
        targetUp = 0;
        sourcePath = new ArrayList();
        targetPath = new ArrayList();
        sourceIsResolved = true;
        targetIsResolved = true;
        flagDirty(true);
    } 
  
/*+++++++++++++++++++++++++*
 * Graph functionality.    *
 *+++++++++++++++++++++++++*/
 
    /**
     * If the medge is removed from the graph, it will also be disconnected from its source and target
     */
    public void setMGraph(MGraph mg) {
        super.setMGraph(mg);
        if (mg == null) {
            if (source != null)
                source.disconnectOutMEdge(this);
            if (target != null)
                target.disconnectInMEdge(this);
        } else {
            if (source != null)
                source.connectOutMEdge(this);
            if (target != null)
                target.connectInMEdge(this);
        }
    }

    /**
     * see also: MEdge.setSource(MVertex newSource)
     */
    public void setSource(MVertex newSource) {
        //first check whether vertices must be warned of change in incident edges.
        if (source != newSource) {
            if (source != null) //alwyas disconnect, but only reconnect if in a graph
                source.disconnectOutMEdge(this);
            if (getMGraph() != null)
                if (newSource != null)
                    newSource.connectOutMEdge(this);
        }
        //then set new source
        source = newSource;
        if (currentMGraph != null) {
            if (  (!getTopMGraph().containsMComponentRecursive(newSource))
                &&(newSource != getTopMGraph()))
                currentMGraph.addMVertex(newSource);
        }
        sourceIsResolved = true;
        flagDirty(true);
    }

    /**
     * see also: MEdge.setTarget(MVertex newTarget)
     */
    public void setTarget(MVertex newTarget) {
        //first check whether vertices must be warned of change in incident edges.
        if (target != newTarget) {
            if (target != null) //alwyas disconnect, but only reconnect if in a graph
                target.disconnectInMEdge(this);
            if (getMGraph() != null)
                if (newTarget != null)
                    newTarget.connectInMEdge(this);
        }
        //the set new target
        target = newTarget;
        if (currentMGraph != null) {
            if (  (!getTopMGraph().containsMComponentRecursive(newTarget))
                &&(newTarget != getTopMGraph()))
                currentMGraph.addMVertex(newTarget);
        }
        targetIsResolved = true;
        flagDirty(true);
    }

    /**
     * see also: MEdge.getSource()
     */
    public MVertex getSource() {
        resolveSource();//if resolving fails, sourcepointer is null & resolved is false so this method returns null & a subsequent call to hasSource returns true!
        return source;
    }
    
    /**
     * see also: MEdge.getTarget()
     */
    public MVertex getTarget() {
        resolveTarget();//if resolving fails, targetpointer is null & resolved is false so this method returns null & a subsequent call to hasTarget returns true!
        return target;
    }

    /**
     */
    public boolean hasSource() {
        MVertex source = getSource();
        return (source != null) && (sourceIsResolved);
    }
        
    /**
     */
    public boolean hasTarget() {
        MVertex target = getTarget();
        return (target != null) && (targetIsResolved);
    }

    /**
     */
    public MVertex getOtherVertex(MVertex v) {
        if (source == v) {
            return target;
        }
        if (target == v) {
            return source;
        }
        throw new IllegalArgumentException("MEdge.getOtherVertex(MVertex v) : v should be a vertex in the edge!");
    }
    
    /**
     * see also: MEdge.setDirected(boolean dir)
     */
    public void setDirected (boolean dir) {
        directed = dir;
        flagDirty(true);
    }
    
    /**
     * see also: MEdge.isDirected()
     */
    public boolean isDirected () {
        return directed;
    }
    
    /**
     * see also: MEdge.reverseDirection()
     */
    synchronized public void  reverseDirection () {
        MVertex tempV = source;
        int tempU = sourceUp;
        ArrayList tempP = sourcePath;
        String tempI = sourceID;
        boolean tempR = sourceIsResolved; 
        setSource(target);
        sourceUp = targetUp;
        sourcePath = targetPath;
        sourceID = targetID;
        sourceIsResolved = targetIsResolved; 
        setTarget(tempV);
        targetUp = tempU;
        targetPath = tempP;
        targetID = tempI;
        targetIsResolved = tempR; 
        flagDirty(true);
    }
    
    /**
     */
    public boolean incidentWithMVertex(MVertex v) {
        if (v == null) 
            throw new NullPointerException("No null vertex allowed in call to incidentWithMVertex");
        return (getSource() == v) || (getTarget() == v);  //if resolving fails, getSource or getTarget returns null. then expresion evaluates to false
    }


    /**
     * @@@@@
     */
    private void resolveSource() {
        //source
        if (!sourceIsResolved) {
//            logger.warning("resolving edge...source...");
            if (sourceID.equals("")) {
//                logger.warning("resolving edge...source...NO SOURCE");
                setSource(null);
                sourceIsResolved = true;
            } else {
                MGraph commonParent = getMGraph();
                if (sourceUp > 0) {
                    commonParent = getMGraph(sourceUp);
                }
                MGraph sourceMGraph;
                if (sourcePath.size() == 0) {
                    sourceMGraph = commonParent;
                } else {
                    sourceMGraph = commonParent.followPath(sourcePath);
                }
                if (sourceMGraph != null) {
                    if (sourceMGraph.getMVertex(sourceID) != null) {
                        setSource(sourceMGraph.getMVertex(sourceID));
//                        logger.warning("resolving edge...source...SOURCE FOUND");
                        sourceIsResolved = true;
                    }
                }
            }
        }
        flagDirty(true);
    }

    /**
     * @@@@@
     */
    private void resolveTarget() {
        //target
        if (!targetIsResolved) {
            if (targetID.equals("")) {
                setTarget(null);
                targetIsResolved = true;
            } else {
                MGraph commonParent = getMGraph();
                if (targetUp > 0) {
                    commonParent = getMGraph(targetUp);
                }
                MGraph targetMGraph;
                if (targetPath.size() == 0) {
                    targetMGraph = commonParent;
                } else {
                    targetMGraph = commonParent.followPath(targetPath);
                }
                if (targetMGraph != null) {
                    if (targetMGraph.getMVertex(targetID) != null) {
                        setTarget(targetMGraph.getMVertex(targetID));
                        targetIsResolved = true;
                    }
                }
            }
        }
        flagDirty(true);
    }

    public void resolve() {
        resolveTarget();
        resolveSource();
    }

    public boolean isResolved() {
        return targetIsResolved && sourceIsResolved;
    }

/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/
 
/*----- toXMLString support -----*/

    /**
     * Adds the following attributes to the attributes of the super class:
     * <ul>
     *      <li> "source", containing the ID of the source MVertex
     *      <li> "target", containing the ID of the target MVertex
     *      <li> "directed", a boolean representing whether this is a directed MEdge.
     *      <li> and for multilevel edge support:
     *      <ul>
     *           <li> "multilevel" true iff multilevel
     *           <li> "sourcepath" a list of id.s
     *           <li> "targetpath" a list of id.s
     *           <li> "sourceup" an integer....
     *           <li> "targetup" an integer....
     *      </ul>
     * </ul>
     */
    protected String getAttributes() {
        String result = super.getAttributes();
        if (isDirected()) {
            result = result + " directed=\"true\"";
        }
        boolean multilevel = false;
        if ((getSource() != null) && (getSource().getMGraph() != getMGraph())) {
            multilevel = true;
        }
        if ((getTarget() != null) && (getTarget().getMGraph() != getMGraph())) {
            multilevel = true;
        }
        if (multilevel) {
            ArrayList edgePath = getTopMGraph().findPath(this);
            ArrayList sourcePath = getTopMGraph().findPath(getSource());
            ArrayList targetPath = getTopMGraph().findPath(getTarget());
            Iterator pathI = edgePath.iterator();
            int sourceCount = 0;
            int targetCount = 0;
            while (pathI.hasNext()) { //
                MGraph g = (MGraph)pathI.next();
                if ((sourcePath.size() > 0) && (sourcePath.get(0) == g)) {
                    sourcePath.remove(0);
                    sourceCount++;
                }
                if ((targetPath.size() > 0) && (targetPath.get(0) == g)) {
                    targetPath.remove(0);
                    targetCount++;
                }
            }
            //the paths are the paths to the source & target vertex;
            //edgePath.size() - targetCount is the number of levels you have to go up from this edge
            //before starting the path, to get to the target. (for source the same)

            //set multilevel property
            result = result + " multilevel=\"true\"";
            
            //set sourcepath properties
            result = result + " sourcepath=\"";
            Iterator spI = sourcePath.iterator();
            if (spI.hasNext())
                result = result + ((MGraph)spI.next()).getID();
            while (spI.hasNext())
                result = result + "," + ((MGraph)spI.next()).getID();
            result = result + "\"";
            result = result + " sourceup=\"" + (edgePath.size() - sourceCount) + "\"";

            //set targetpath properties
            result = result + " targetpath=\"";
            Iterator tpI = targetPath.iterator();
            if (tpI.hasNext())
                result = result + ((MGraph)tpI.next()).getID();
            while (tpI.hasNext())
                result = result + "," + ((MGraph)tpI.next()).getID();
            result = result + "\"";
            result = result + " targetup=\"" + (edgePath.size() - targetCount) + "\"";

        } 
        if (getSource() != null) {
            result = result + " source=\"" +
                                        getSource().getID() +
                                    "\" ";
        }
        if (getTarget() != null) {
            result = result + " target=\"" +
                                        getTarget().getID() +
                                    "\" ";
        }
        
        return result;
    } 

/*----- readXML support -----*/
        
    /**
     * Reads the following attributes in extension to the attributes of the super class:
     * <ul>
     *      <li> "source", containing the ID of the source MVertex
     *      <li> "target", containing the ID of the target MVertex
     *      <li> "directed", a boolean representing whether this is a directed MEdge.
     * </ul>

DO NOT FORGET TO CHECK IF SOURCE OR TARGET HAVE BEEN NULL!!! (DAN IS ER GEEN id)
     * A very important condition is that when readXML is called on an MEdge, it should already be 
     * added to a graph, and its end point vertices should already be present in that same graph.
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;

        //directed
        if (attributes.containsKey("directed")) {
            boolean dir = ((String)attributes.get("directed")).equals("true");
            setDirected(dir);
        }
        //find source & target ID (dont forget to initialize on "", to catch the cases where no source or target was present....)
        sourceID = "";
        targetID = "";
        if (attributes.containsKey("source")) {
            sourceID = (String)attributes.get("source");
        }
        if (attributes.containsKey("target")) {
            targetID = (String)attributes.get("target");
        }
        //multilevel
        boolean multilevel = false;
        if (attributes.containsKey("multilevel")) {
            multilevel = ((String)attributes.get("multilevel")).equals("true");
        }
        //source/target path & up
        sourceUp = 0;
        targetUp = 0;
        sourcePath = new ArrayList();
        targetPath = new ArrayList();
        sourceIsResolved = false;
        targetIsResolved = false;
        if (multilevel) {
            if (attributes.containsKey("sourceup")) {
                try {
                    sourceUp = Integer.parseInt((String)attributes.get("sourceup"));
                } catch (NumberFormatException e) {
                    logger.severe("Integer expected as sourceup value. Wrong value: " + attributes.get("sourceup"));
                }
            }
            if (attributes.containsKey("targetup")) {
                try {
                    targetUp = Integer.parseInt((String)attributes.get("targetup"));
                } catch (NumberFormatException e) {
                    logger.severe("Integer expected as targetup value. Wrong value: " + attributes.get("targetup"));
                }
            }
            if (attributes.containsKey("sourcepath")) {
                String sourcep = (String)attributes.get("sourcepath");
                StringTokenizer st = new StringTokenizer(sourcep, ",");
                while (st.hasMoreElements()) {
                    sourcePath.add(st.nextElement());
                }
            }
            if (attributes.containsKey("targetpath")) {
                String targetp = (String)attributes.get("targetpath");
                StringTokenizer st = new StringTokenizer(targetp, ",");
                while (st.hasMoreElements()) {
                    targetPath.add(st.nextElement());
                }
            }
        }
        resolve();
    }

}
