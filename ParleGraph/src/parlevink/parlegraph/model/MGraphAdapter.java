/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MGraphAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:51  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2003/06/24 10:38:54  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/01/03 16:04:11  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.3  2002/09/30 20:16:45  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.2  2002/09/16 15:16:24  dennisr
// documentatie
//
// Revision 1.1  2002/09/16 14:05:42  dennisr
// first add
//
// Revision 1.18  2002/06/04 12:54:22  reidsma
// Redocumentation
//

package parlevink.parlegraph.model;

import java.util.logging.*;
import parlevink.xml.*;

import java.util.*;
import java.io.*;

/**
 * This class is the default implementation of the MGraph interface.
 */
public class MGraphAdapter extends MVertexAdapter implements MGraph {

/*++++++++++++++++++++*
 * Variables section. *
 *++++++++++++++++++++*/

    //storage of vertices & edges
    protected List edgeList;
    protected List vertexList;

	//ID administration
	protected TreeMap idToMComponentMap;
	protected HashMap mcomponentToIDMap;


/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
 
   /**
    * Creates a new MGraphAdapter.
    * <br>post: isDirty == true
    */
    public MGraphAdapter() 
    {
	    super();
	    /* graph init: create empty graph */
	    edgeList = new ArrayList();
	    vertexList = new ArrayList();
	    idToMComponentMap = new TreeMap();
	    mcomponentToIDMap = new HashMap();
	    /* default visualisation classes */
	    setViewerClass("parlevink.parlegraph.view.VGraphAdapter");
        flagDirty(true);
    } 


/*++++++++++++++++++++++++++++++++*
 * Basic graph functionality      *
 *++++++++++++++++++++++++++++++++*/
 
/*-------Adding components-------*/

    /**
     * see also: MGraph.addMEdge(MEdge e)
     */
    public String addMEdge(MEdge e) {
        if (e==null)
            return null;
        //automatically assign new (free) ID
        String newID = getFreeID();
        //relay to ID-based method. That method will take care of checking the existence of the endpoints, checking if e was already part of another graph, & other things.
        addMEdge(e, newID);
        flagDirty(true);
        return e.getID();
    } 
    
    /**
     * see also: MGraph.addMVertex(MVertex v)
     */
    public String addMVertex(MVertex v) {
        if (v==null)
            return null;
        //automatically assign new (free) ID
        String newID = getFreeID();
        //relay to ID based method
        addMVertex(v, newID);
        flagDirty(true);
        return v.getID();
    } 

    /**
     * see also: MGraph.addMComponent(MComponent mc)
     */
    public String addMComponent(MComponent mc) {
        if (mc != null) {
            if (mc instanceof MEdge) {
                return addMEdge((MEdge)mc);
            }
            if (mc instanceof MVertex) {
                return addMVertex((MVertex)mc);
            }
            throw new RuntimeException("Error in adding new MComponent: unknown type of MComponent");
        }
        return null;
    }

    /**
     * see also: MGraph.addMEdge(MEdge e, String ID)
     */
    public void addMEdge(MEdge e, String ID) {
        //this method could also first link through to addNoID
        if (e != null) {
            if (edgeList.contains(e)) {
                logger.warning("addEdge:edge already in graph");
                setID(e, ID); //still: set new ID
            } else {
                if (e.getMGraph() != null) {
                    throw new IllegalArgumentException("addMEdge: edge already added to another graph " + e.getMGraph().getID());
                }
                edgeList.add(e);
                e.setMGraph(this);
                setID(e, ID);
                
                MVertex source = e.getSource();
                if (source != null) {
                    if ((!containsMComponent(source)) && !getTopMGraph().containsMComponentRecursive(source)) { 
                        //the fist condition is to speed up the package, because non-recursive testing is faster.
                        //if the vertex is already present (which will often be the case) it is mostly within the 
                        //same subgraph.
                        logger.warning("addEdge: adding sourcevertex as well");
                        addMVertex(source);
                    }
                }
                MVertex target = e.getTarget();
                if (target != null) {
                    if ((!containsMComponent(target)) && !getTopMGraph().containsMComponentRecursive(target)) {
                        //the fist condition is to speed up the package, because non-recursive testing is faster.
                        //if the vertex is already present (which will often be the case) it is mostly within the 
                        //same subgraph.
                        logger.warning("addEdge: adding targetvertex as well");
                        addMVertex(target);
                    }
                }
            }
        }
        flagDirty(true);
    }        
    
    /**
     * see also: MGraph.addMVertex(MVertex v, String ID)
     */
    public void addMVertex(MVertex v, String ID) {
        if (v != null) {
            if (vertexList.contains(v)) {
                logger.warning("addVertex:vertex already in graph");
                setID(v, ID); //still: set new ID
            } else {
                if (v.getMGraph() != null) {
                    throw new IllegalArgumentException("addMVertex: vertex already added to another graph " + v.getMGraph().getID());
                }
                vertexList.add(v);
                v.setMGraph(this);
                setID(v, ID);
            }
        }
        flagDirty(true);
    }

    /**
     * see also: MGraph.addMComponent(MComponent mc, String ID)
     */
    public void addMComponent(MComponent mc, String ID) {
        if (mc != null) {
            if (mc instanceof MEdge) {
                addMEdge((MEdge)mc, ID);
            }
            if (mc instanceof MVertex) {
                addMVertex((MVertex)mc, ID);
            }
            throw new RuntimeException("Error in adding new MComponent: unknown type of MComponent");
        }
    }

    /**
     * see also: MGraph.addMEdgeNoID(MEdge e)
     */
    public void addMEdgeNoID(MEdge e) {
        if (e != null) {
            if (edgeList.contains(e)) {
                logger.warning("addEdge:edge already in graph");
            } else {
                if (e.getMGraph() != null) {
                    throw new IllegalArgumentException("addMEdge: edge already added to another graph " + e.getMGraph().getID());
                }
                edgeList.add(e);
                e.setMGraph(this);
                
                MVertex source = e.getSource();
                if (source != null) {
                    if (!getTopMGraph().containsMComponentRecursive(source)) { 
                        logger.warning("addEdge: adding sourcevertex as well. New ID assigned!");
                        addMVertex(source);
                    }
                }
                MVertex target = e.getTarget();
                if (target != null) {
                    if (!getTopMGraph().containsMComponentRecursive(target)) {
                        logger.warning("addEdge: adding targetvertex as well. New ID assigned!");
                        addMVertex(target);
                    }
                }
            }
        }
        flagDirty(true);
    }        
    
    /**
     * see also: MGraph.addMVertexNoID(MVertex v)
     */
    public void addMVertexNoID(MVertex v) {
        if (v != null) {
            if (vertexList.contains(v)) {
                logger.warning("addVertex:vertex already in graph");
            } else {
                if (v.getMGraph() != null) {
                    throw new IllegalArgumentException("addMVertex: vertex already added to another graph " + v.getMGraph().getID());
                }
                vertexList.add(v);
                v.setMGraph(this);
            }
        }
        flagDirty(true);
    }

    /**
     * see also: MGraph.addMComponentNoID(MComponent mc)
     */
    public void addMComponentNoID(MComponent mc) {
        flagDirty(true);
        if (mc != null) {
            if (mc instanceof MEdge) {
                addMEdgeNoID((MEdge)mc);
            }
            if (mc instanceof MVertex) {
                addMVertexNoID((MVertex)mc);
            }
        }
    }
    
/*-------Removing components-------*/

    /**
     * see also: MGraph.removeMEdge(MEdge e)
     */
    public void removeMEdge(MEdge e) {
        if (e!= null) {
            //remove from edge-list
            if (!edgeList.remove(e)) {
                logger.warning("removeEdge:edge not in graph");
            } else {
                //break backward link to this MGraph
                e.setMGraph(null);
                //remove from both ID maps
                String id = (String)mcomponentToIDMap.get(e);
                mcomponentToIDMap.remove(e);
                idToMComponentMap.remove(id);
                logger.info("removed edge");
            }
        }
        flagDirty(true);
    } 
    
    /**
     * see also: MGraph.removeMVertex (MVertex v)
     */
    public void removeMVertex(MVertex v) {
        if (v!= null) {
            //check for this vertex in edges.... remove all those edges
            //@@@failure: the edges into subgraphs are not yet removed... & their 
            //end points are also not set to null :o( Wie weet kan decompose hier uitkomst bieden.
            Iterator edgei = v.getIncidentMEdges();
            logger.warning("Any edges to remove due to remove-vertex:" + edgei.hasNext());
            MEdge e = null;
            ArrayList removeEdges = new ArrayList();
            while (edgei.hasNext()) { //this is because otherwise a concurrentmodexception is possible...
                removeEdges.add(edgei.next());
            }
            for (int i =0; i < removeEdges.size(); i++) {
                e = (MEdge)removeEdges.get(i);
                e.getMGraph().removeMEdge(e);
                //e.setMGraph(null);wordt hierboven al gedaan?
            }
            
            //after removing any edges which cannot be maintained anymore:
            //remove from vertex list
            vertexList.remove(v);
            //break backwards link to this MGraph
            v.setMGraph(null);
            //remove from both ID maps
            String id = (String)mcomponentToIDMap.get(v);
            mcomponentToIDMap.remove(v);
            if (id!=null) {
                idToMComponentMap.remove(id);
            }
            logger.info("removed vertex");
        }
        flagDirty(true);
    } 
    
    /**
     * see also: MGraph.removeMEdge(String ID)
     */
    public void removeMEdge(String ID) {
        MEdge e = (MEdge)idToMComponentMap.get(ID);
        if (e != null) {
            removeMEdge(e);
        }
    }    

    /**
     * see also: MGraph.removeMVertex(String ID)
     */
    public void removeMVertex(String ID) {
        MVertex v = (MVertex)idToMComponentMap.get(ID);
        if (v != null) {
            removeMVertex(v);
        }
    }    
    
    /**
     * see also: MGraph.clearMGraph ()
     */
    public void clearMGraph() {
        Iterator it = vertexList.iterator();
        ArrayList removeVertices = new ArrayList();
        while (it.hasNext()) {
            removeVertices.add(it.next());
        }
        for (int i = 0; i < removeVertices.size(); i++) {
            MVertex nextV = (MVertex)removeVertices.get(i);
            if (nextV instanceof MGraph)
                ((MGraph)nextV).clearMGraph();
            removeMVertex(nextV);
        } //incidentally, this trick should also remove all edges...
        flagDirty(true);
    }

    public ArrayList decompose() {
        ArrayList result = new ArrayList();
        result.addAll(vertexList);
        result.addAll(edgeList);
        vertexList.clear();
        edgeList.clear();
        for (int i = 0; i < result.size(); i++)
            ((MComponent)result.get(i)).setMGraph(null);
        flagDirty(true);
        return result;
    }
    
/*-------Accessing components-------*/

    public int size() {
        return vertexList.size();
    }

    public boolean ancestorOf(MComponent mc) {
        MGraph parent = mc.getMGraph();
        while (true) {
            if (this == parent)
                return true;
            if (this == null)
                return false;
            parent = parent.getMGraph();
        }
    }

    public Iterator getMEdges() {
        final Iterator it = edgeList.iterator();
        return new Iterator() { //to make sure that it's unmodifiable
            public boolean hasNext() {
                return it.hasNext();
            }
            public Object next() {
                return it.next();
            }
            public void remove() {
                throw new RuntimeException("Modification not allowed ");
            }
        };
    }

    /**
     * see also: MGraph.getMEdges(MVertex v)
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getMEdges(MVertex v) {
        ArrayList result = new ArrayList();
        Iterator edgeIt = getMEdges();
        while (edgeIt.hasNext()) {
            MEdge nextE = (MEdge)edgeIt.next();
            if (nextE.incidentWithMVertex(v)) 
                result.add(nextE);
        }
        return result.iterator();
    }
    
    /**
     * see also: MGraph.getOutMEdges(MVertex v)
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getOutMEdges(MVertex v) {
        ArrayList result = new ArrayList();
        Iterator edgeIt = getMEdges();
        while (edgeIt.hasNext()) {
            MEdge nextE = (MEdge)edgeIt.next();
            if (nextE.getSource() == v) 
                result.add(nextE);
        }
        return result.iterator();
    }

    /**
     * see also: MGraph.getInMEdges(MVertex v)
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getInMEdges(MVertex v) {
        ArrayList result = new ArrayList();
        Iterator edgeIt = getMEdges();
        while (edgeIt.hasNext()) {
            MEdge nextE = (MEdge)edgeIt.next();
            if (nextE.getTarget() == v) 
                result.add(nextE);
        }
        return result.iterator();
    }
    
    /**
     * see also: MGraph.getMVertices ()
     */
    public Iterator getMVertices() {
        final Iterator it = vertexList.iterator();
        return new Iterator() { //to make sure that it's unmodifiable
            public boolean hasNext() {
                return it.hasNext();
            }
            public Object next() {
                return it.next();
            }
            public void remove() {
                throw new RuntimeException("Modification not allowed ");
            }
        };
    }
    
    /**
     * see also: MGraph.getMComponents ().
     * first vertices, then edges
     */
    public Iterator getMComponents() {
        return new Iterator() { //to make sure that it's unmodifiable
            Iterator it = vertexList.iterator();
            boolean iteratingEdges = false;
        
            public boolean hasNext() {
                if (!(it.hasNext() || iteratingEdges)) {
                    it = edgeList.iterator();
                    iteratingEdges = true;
                }
                return it.hasNext();
            }
            public Object next() {
                return it.next();
            }
            public void remove() {
                throw new RuntimeException("Modification not allowed ");
            }
        };
    }

    /**
     * see also: MGraph.containsMEdge (MEdge e)
     */
    public boolean containsMEdge(MEdge e) {
        return edgeList.contains(e);
    }
    
    /**
     * see also: MGraph.containsMVertex (MVertex v)
     */
    public boolean containsMVertex(MVertex v) {
        return vertexList.contains(v);
    }

    /**
     * see also: MGraph.containsMComponent(MComponent mc)
     */
    public boolean containsMComponent(MComponent mc) {
        return vertexList.contains(mc) || edgeList.contains(mc);
    }

/*++++++++++++*
 * ID section *
 *++++++++++++*/

    /**
     * see also: MGraph.getID(MVertex v)
     */
    public String getID(MVertex v) {
        return (String)mcomponentToIDMap.get(v);
    }

    /**
     * see also: MGraph.getID(MEdge e)
     */
    public String getID(MEdge e) {
        return (String)mcomponentToIDMap.get(e);
    }
    
    /**
     * see also: MGraph.getMVertex(String ID)
     */
    public MVertex getMVertex(String ID) {
        try {
            Object o = idToMComponentMap.get(ID);
            if (o == null) {
                throw new IllegalArgumentException("No component with such ID: " + ID);
            }
            return (MVertex)o;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Component with ID: " + ID + " is not an vertex");
        }
    }

    /**
     * see also: MGraph.getMEdge(String ID)
     */
    public MEdge getMEdge(String ID) {
        try {
            Object o = idToMComponentMap.get(ID);
            if (o == null) {
                throw new IllegalArgumentException("No component with such ID: " + ID);
            }
            return (MEdge)o;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Component with ID: " + ID + " is not an edge");
        }
    }
    
    /**
     * see also: MGraph.getMComponent(String ID)
     */
    public MComponent getMComponent(String ID) {
        if (!idToMComponentMap.containsKey(ID)) {
            throw new IllegalArgumentException("No component with such ID: " + ID);
        }
        return (MComponent)idToMComponentMap.get(ID);
    }

    /**
     * see also: MGraph.setID(MVertex v, String ID)
     */
    public void setID(MVertex v, String ID) {
        if (!containsMVertex(v)) {
            throw new IllegalArgumentException("MGraphAdapter.setID: vertex not present in graph");
        }
        if (idToMComponentMap.containsKey(ID)) {
            throw(new DuplicateIDException(ID));
        }
        idToMComponentMap.put(ID, v);
        mcomponentToIDMap.put(v, ID);
        v.setID(ID);
        flagDirty(true);
    }

    /**
     * see also: MGraph.setID(MEdge e, String ID)
     */
    public void setID(MEdge e, String ID) {
        if (!containsMEdge(e)) {
            throw new IllegalArgumentException("MGraphAdapter.setID: edge not present in graph");
        }
        if (idToMComponentMap.containsKey(ID)) {
            throw(new DuplicateIDException(ID));
        }
        idToMComponentMap.put(ID, e);
        mcomponentToIDMap.put(e, ID);
        e.setID(ID);
        flagDirty(true);
    }

    /**
     * see also: MGraph.setID(MComponent mc, String ID)
     */
    public void setID(MComponent mc, String ID) {
        if (mc != null) {
            if (mc instanceof MEdge) {
                setID((MEdge)mc, ID);
            }
            if (mc instanceof MVertex) {
                setID((MVertex)mc, ID);
            }
        }
    }
    
    /**
     * see also: MGraph.containsID(String ID)
     */
    public boolean containsID(String ID) {
        return idToMComponentMap.containsKey(ID);
    }
    
    /**
     * see also: MGraph.getFreeID()
     */
    public String getFreeID() {
        int idNr = edgeList.size() + vertexList.size();
        String id = Integer.toString(idNr);
        while (containsID(id)) {
            idNr++;
            id = Integer.toString(idNr);
        }
        logger.finer("id assigned: " + id);
        return id;
    }
   
    /**
     * see also: MGraph.getAllIDs()
     */
    public Iterator getAllIDs() {
        return idToMComponentMap.keySet().iterator();
    }

/*+++++++++++++++++++++++++++++++++*
 * Recursive section               *
 * This section contains methods   *
 * search the subgraphs of this    *
 * MGraph recursively.             *
 *+++++++++++++++++++++++++++++++++*/

    /**
     * see also: MGraph.containsMComponentRecursive(MComponent mc)
     */
    public boolean containsMComponentRecursive(MComponent mc) {
        if (containsMComponent(mc))
            return true;
        Iterator vertices = vertexList.iterator();
        while (vertices.hasNext()) {
            MVertex v = (MVertex)vertices.next();
            if (v instanceof MGraph)
                if (((MGraph)v).containsMComponentRecursive(mc))
                    return true;
        }
        return false;
    }

    /**
     * see also: MGraph.findPath(MComponent mc)
     */
    public ArrayList findPath(MComponent mc) {
        if (mc == null) 
            return null;
        ArrayList result = new ArrayList();
        if (containsMComponent(mc)) {
            result.add(this);
            return result;
        }
        Iterator vertices = vertexList.iterator();
        while (vertices.hasNext()) {
            MVertex v = (MVertex)vertices.next();
            if (v instanceof MGraph) {
                ArrayList recursResult = ((MGraph)v).findPath(mc);
                if (recursResult.size() > 0) { //if this subgraph contained the component
                    result.add(this);
                    result.addAll(recursResult);
                    return result;
                }
            }
        }
        return result; //return emptyarraylist if mc was not contained in this MGraph (recursive...)
                       //no exception because now yu can do 'contains' & 'getpath' in one round. More efficiency :o)
    }
    
    /**
     * see also: MGraph.followPath(ArrayList path)
     */
    public MGraph followPath(ArrayList path) {
        if (path == null) 
            return null;
        if (path.size() == 0) //end of path reached
            return this;
        String id = (String)path.get(0);
        if (!containsID(id))
            return null;    //wrong path
        MComponent mc = getMComponent(id);
        if (!(mc instanceof MGraph)) //only follow paths through subgraphs....
            return null;            //wrong path
        path.remove(0);
        return ((MGraph)mc).followPath(path); //recurse
    }

    /**
     * see also: MGraph.getMEdgesRecursive(MVertex v)
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getMEdgesRecursive(MVertex v) {
        ArrayList result = new ArrayList();

        Iterator edgeIt = getMEdges();
        while (edgeIt.hasNext()) {
            MEdge nextE = (MEdge)edgeIt.next();
            if (nextE.incidentWithMVertex(v)) 
                result.add(nextE);
        }

        Iterator vertices = vertexList.iterator();
        while (vertices.hasNext()) {
            MVertex subv = (MVertex)vertices.next();
            if (subv instanceof MGraph) {
                Iterator it = ((MGraph)subv).getMEdgesRecursive(v);
                while (it.hasNext())
                    result.add(it.next());  //not efficient.....
            }
        }
        
        return result.iterator();
    }        

    /**
     * see also: MGraph.getOutMEdgesRecursive(MVertex v)
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getOutMEdgesRecursive(MVertex v) {
        ArrayList result = new ArrayList();

        Iterator edgeIt = getMEdges();
        while (edgeIt.hasNext()) {
            MEdge nextE = (MEdge)edgeIt.next();
            if (nextE.getSource() == v) 
                result.add(nextE);
        }

        Iterator vertices = vertexList.iterator();
        while (vertices.hasNext()) {
            MVertex subv = (MVertex)vertices.next();
            if (subv instanceof MGraph) {
                Iterator it = ((MGraph)subv).getOutMEdgesRecursive(v);
                while (it.hasNext())
                    result.add(it.next());
            }
        }
        
        return result.iterator();
    }

    /**
     * see also: MGraph.getInMEdgesRecursive(MVertex v)
     * @deprecated Use MVertex.getOutMEdges and MVertex.getInMEdges instead
     */
    public Iterator getInMEdgesRecursive(MVertex v) {
        ArrayList result = new ArrayList();

        Iterator edgeIt = getMEdges();
        while (edgeIt.hasNext()) {
            MEdge nextE = (MEdge)edgeIt.next();
            if (nextE.getTarget() == v) 
                result.add(nextE);
        }

        Iterator vertices = vertexList.iterator();
        while (vertices.hasNext()) {
            MVertex subv = (MVertex)vertices.next();
            if (subv instanceof MGraph) {
                Iterator it = ((MGraph)subv).getInMEdgesRecursive(v);
                while (it.hasNext())
                    result.add(it.next());
            }
        }
        
        return result.iterator();
    }
   
/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

/*----- toXMLString support -----*/
    
    /**
     * The content of the MGraphAdapter XML element is made of the XML for the vertices followed by the XML 
     * of the vertices. 
     */
    protected String getContent() {
        return getVertices() + getEdges();
    }

    /**
     * Returns a string of the toXML() for all edges.
     */
    protected String getEdges() {
        String result = "";
        Iterator it = edgeList.iterator();
        while (it.hasNext()) {
            result = result + ((MEdge)it.next()).toXMLString() + "\n";
        }
        return result;
    } 

    /**
     * Returns a string of the toXML() for all vertices.
     */
    protected String getVertices() {
        String result = "";
        Iterator it = vertexList.iterator();
        while (it.hasNext()) {
            result = result + ((MVertex)it.next()).toXMLString() + "\n";
        }
        return result;
    } 

/*----- readXML support -----*/

    /**
     * This method reads all elements till the closing tag. Every element is interpreted as an MComponent; a new
     * MComponent of the right class is created, it is added to this MGraphAdapter and readXML(tokenizer) is 
     * called on that element to process the rest of the XML for that element.
     * <br>
     @@@veranderd! de edges mogen blijven "hangen"
     * To ensure proper operation of the readXML methods, the following requirements are made for the content XML:
     * <ul>
     *      <li> For every edge that is introduced, the corresponding vertices should already be present in the graph.
     *      <li> As long as the readXML method is not yet finished, or as long as more readXML calls with add="true"
     *           can occur, the IDs of graph elements should not be changed.
     * </ul>
     */
    protected void readContent(XMLTokenizer tokenizer) throws IOException {
        while (!tokenizer.atETag(getClass().getName())) {//as long as closing tag is not reached
            //read classname of next element (if atSTag)
            if (tokenizer.atSTag()) {
                try {
                    Class mcClass = Class.forName(tokenizer.getTagName());
                    MComponent testMC = (MComponent)mcClass.newInstance();
                    //first try special case: subgraphs need extra care (due to 'add' attribute)
                    if (testMC instanceof MGraph) //if the next component is an MGraph,
                        if (processMGraph(tokenizer))
                            continue;
                    //default processing:
                    MComponent newMC = (MComponent)mcClass.newInstance();
                    addMComponentNoID(newMC);
                    newMC.readXML(tokenizer); //this also sets the id of this component, in this MGraph.....
                } catch (ClassCastException e) {
                    logger.warning("error: "+e);
                    tokenizer.nextToken(); //skip offending input
                } catch (InstantiationException e) {
                    logger.warning("error: "+e);
                    tokenizer.nextToken(); //skip offending input
                } catch (IllegalAccessException e) {
                    logger.warning("error: "+e);
                    tokenizer.nextToken(); //skip offending input
                } catch (ClassNotFoundException e) {
                    logger.warning("error: "+e);
                    tokenizer.nextToken(); //skip offending input
                }                
            } else {
                tokenizer.nextToken(); //skip unknown stuff
            }
        }
        //end with resolving any edges for which the end nodes were not yet connected to the edges.
        resolveEdges();
    }
    
    /**
     * If the XML for an Edge is read, it is posible that the corresponding end vertices were not yet 
     * created. In that case connecting the edge to the vertices is postponed. This method resolves 
     * all of such edges for which the end vertices have been created by now.
     * This is a recursive call.
     * <br> post:isdirty==true
     */
    public void resolveEdges() {
        for (int i = 0; i < edgeList.size(); i++) {
            MEdge e = (MEdge)edgeList.get(i);
            if (!e.isResolved()) {
                e.resolve();
            }
        }
        Iterator vertices = vertexList.iterator();
        while (vertices.hasNext()) {
            MVertex v = (MVertex)vertices.next();
            if (v instanceof MGraph) {
                ((MGraph)v).resolveEdges();
            }
        }
        flagDirty(true);
    }
    
    /**
     * This method tries to process the XML for an MGraph.
     * If add='true', and an MGraph with the given ID already existed, that MGraph 
     * gets to process the XML. Otherwise, the method returns false to signal that the XML 
     * should be processed using the default methods. 
     */
    protected boolean processMGraph(XMLTokenizer tokenizer) throws IOException {
        boolean result = false;
        if (   (tokenizer.attributes.containsKey("add"))
            && (((String)tokenizer.attributes.get("add")).equals("true")) //if add='true'
            ) { // find existing MComponent.
            if (tokenizer.attributes.containsKey("ID")) {
                String mcID = (String)tokenizer.attributes.get("ID");
                if (!(mcID.equals(""))) {
                    MComponent newMC = getMComponent(mcID);
                    if (newMC != null) {
                        result = true;
                        newMC.readXML(tokenizer);
                    }
                }
            }
        } 
        return result;
    }

    /**
     * In addition to the superclass functionality, this method reads and processes the boolean attribute
     * "add". If add is true, the XML that is currently being processed should not replace the current
     * graph content but should rather be added to the existing graph. If add is false, the current graph 
     * contents are cleared in this method.
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        super.readAttributes(tokenizer);
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;
        boolean add = false;
        if (attributes.containsKey("add")) {
            add = (new Boolean(true)).equals(new Boolean((String)attributes.get("add")));
        }
        //if not add: empty vertices and edges
        if (!add) {
            clearMGraph();
        }
    }
    
} 
