/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VGraphAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.3  2005/12/06 15:58:36  swartjes
// Made the graph shape a Rectangle in stead of RoundedRectangle
// Reason: if exported to EPS, it can be nicely introduced in LaTeX as a Figure.
//
// Revision 1.2  2005/11/25 15:28:22  swartjes
// Made a couple of graphical adjustments to clarify knowledge:
// - Vertices are drawn on top of edges in stead of below
// - Edges are light gray
// - Arrows are smaller
//
// Revision 1.1  2005/11/08 16:03:17  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.11  2003/07/16 16:47:47  dennisr
// *** empty log message ***
//
// Revision 1.10  2003/06/24 10:31:06  dennisr
// *** empty log message ***
//
// Revision 1.9  2003/03/28 14:51:00  dennisr
// *** empty log message ***
//
// Revision 1.8  2003/03/03 17:56:07  dennisr
// *** empty log message ***
//
// Revision 1.7  2003/02/24 13:24:00  dennisr
// *** empty log message ***
//
// Revision 1.6  2003/01/03 16:04:12  dennisr
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
// Revision 1.4  2002/11/05 12:22:33  dennisr
// more generic in border and connection points
//
// Revision 1.3  2002/09/30 20:16:48  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.2  2002/09/27 08:15:44  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.1  2002/09/16 14:08:51  dennisr
// first add
//
// Revision 1.16  2002/06/04 12:54:22  reidsma
// minor fixes
//
// Revision 1.15  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.14  2002/02/25 08:55:36  reidsma
// It is not allowed to add MCOmponents to an MGraph when they are already element in another MGraph. Since this version, trying to do this results in an exception.
// VLineEdge/VLineTextEdge/VEdgeAdapter: de sourceCoord en targetCoord zijn verhuisd naar de VEdgeAdapter; sourceCoord en targetCoord zijn een read-only property geworden (altijd geinitialiseerd).
//
// Revision 1.13  2002/02/05 14:41:04  reidsma
// layout
//
// Revision 1.12  2002/02/05 14:35:58  reidsma
// layouting
//
// Revision 1.11  2002/02/04 17:17:32  reidsma
// layout class added
//
// Revision 1.10  2002/02/04 16:19:34  reidsma
// debugging
//
// Revision 1.9  2002/02/01 17:13:04  reidsma
// bug fix...
//
// Revision 1.8  2002/02/01 13:03:50  reidsma
// edit controller; general maintenance
//
// Revision 1.7  2002/01/29 14:58:07  reidsma
// debugging
//
// Revision 1.6  2002/01/28 14:00:45  reidsma
// Enumerations en Vectors vervangen door Iterator en ArrayList
//
// Revision 1.5  2002/01/25 15:23:08  reidsma
// Redocumentation
//
// Revision 1.4  2002/01/22 15:00:47  reidsma
// better connector positions for rectangular vertices
//
// Revision 1.3  2002/01/22 12:29:48  reidsma
// XML; multi-line textlabels; arrowheads; moving endpoints (first test)
//

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.layout.*;
import parlevink.xml.*;

import java.util.logging.*;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;

/**
 * VGraphAdapter is the default implementation for the VGraph interface.
 *
 */
public class VGraphAdapter extends VEllipticVertex implements VGraph {

    public static final double ARCWIDTH = 10;
    protected VGraphLayout layout;

/**********************************
 * Overrides of default shapes etc.
 *********************************/
 
    /**
     * A rounded rectangle, instead of the ellipse from the superclass.
     */
    public RectangularShape createDefaultShape() {
    	return new Rectangle2D.Double(x, y, width, height);
        //return new RoundRectangle2D.Double(x, y, width, height, ARCWIDTH, ARCWIDTH); 
    }

    public VGraphAdapter() {
        super();
    }

    /**
     * Override: larger minimum size (100x100).
     */
    public void preInit() {
        super.preInit();
        width = 100;
        height = 100;
        minimumwidth = 100; // was 100
        minimumheight = 100; // was 100
    }

    /**
     * Custom size. NB: minimumsize is still 100x100.
     */
    public VGraphAdapter(double x, double y, double width, double height) {
        this();
        setBounds2D(x, y, width, height);
    }

    /**
     * VGraph(x, y, width, height, arcwidth) creates a new VGraph in the form of a
     * rounded rectangle, with bounding box defined by the first four parameters.
     * Arcwidth parameter is ignored at the moment
     */
    public VGraphAdapter(double x, double y, double width, double height, double arcwidth) {
        this();
        setBounds2D(x, y, width, height);
    }

    /**
     * Sets which VGraphLayout should take care of layout algorithms.
     */
    public void setLayout(VGraphLayout theLayout) {
        layout = theLayout; 
    }
 
    /**
     * Makes the layouter do its job...
     */
    public void doLayout() {
        if (layout == null) {
            layout = new VGraphLayout(this);
        }
        layout.doLayout();
        //put the graph back to left top location, in case the layouter didn't fix that
        if (getTopVGraph() == this) {
            translate2D(VComponent.RECURSIVE, -getX(), -getY());
        }
    }

    /**
     * Makes the layouter do its job...
     */
    public void fullLayout() {
        if (layout == null) {
            layout = new VGraphLayout(this);
        }
        layout.fullLayout();
        //put the graph back to left top location
        if (getTopVGraph() == this) {
            translate2D(VComponent.RECURSIVE, -getX(), -getY());
        }
    }
    
    /** 
     * Overwrites VComponentAdapter.addVComponent, to make vertices go on top
     * of edges within a graph. 
     * @author Ivo Swartjes
     */
    public void addVComponent(VComponent vc){
    	// Subcontainers
    	ArrayList vertexContainer = new ArrayList();
    	ArrayList edgeContainer = new ArrayList();
    	ArrayList otherContainer = new ArrayList();
    	
    	// First split the container into subcontainers
    	if (container == null) 
            container = new ArrayList();
        if (!(container.contains(vc))) {
        	for (Iterator iter = container.iterator(); iter.hasNext();) {
        		Object nxt = iter.next();
        		if (nxt instanceof VEdge) {
        			edgeContainer.add(nxt);
        		} 
        		else if (nxt instanceof VVertex) {
        			vertexContainer.add(nxt);
        		} 
        		else {
        			otherContainer.add(nxt);
        		}
        	}
        }
        
        // Now add the new element
        if (vc instanceof VEdge) {
        	edgeContainer.add(vc);
        } 
        else if (vc instanceof VVertex) {
        	vertexContainer.add(vc);
        }
        else {
        	otherContainer.add(vc);
        }
        
        // Now combine the containers in the right order (the later, the more on top)
        container = new ArrayList();
        container.addAll(edgeContainer);
        container.addAll(vertexContainer);
        container.addAll(otherContainer);
        
        // Clean up
        edgeContainer = null;
        vertexContainer = null;
        otherContainer = null;
    }
    

    /**
     * When a new MGraph is connected to this VGraphAdapter,
     * initialiseNewGraph is called to create visualisations for all elements of that
     * MGraph and to perform a layout on the result.
     */
    public void setMComponent(MComponent newMComponent){
        if (!(newMComponent instanceof MGraph) && (newMComponent != null)) {
            //error: wrong class type. error message, do nothing else.
            logger.severe("A VGraph can visualise only objects of class MGraph");
        } else {
            super.setMComponent(newMComponent);
            //a new graph: clear all contents of this VGraph...
            removeAllVComponents();
            if (mcomponent != null) {
                initialiseNewGraph();
                recalculateShape();
            } else {
            }
        }
    }

    /**
     * This method initialises the visualisation of a new graph by creating visualisations 
     * for all vertices and edges and adding them to the container of this VGraph. 
     * The visualisations are created using the viewer classes specified by the model 
     * components.
     * <p>
     * Finally, a layout is performed as well.
     */
    public void initialiseNewGraph() {

    	//for every vertex
        Iterator vertices = ((MGraph)mcomponent).getMVertices();
        while (vertices.hasNext()) {
            viewMComponent((MVertex)vertices.next());
        }
        
        // To make sure the edges have vertices that are already somewhere
        doLayout();
        
        //for every edge
        Iterator edges = ((MGraph)mcomponent).getMEdges();
        while (edges.hasNext()) {
            viewMComponent((MEdge)edges.next());
        }
        
        doLayout();
    }
    
    /**
     * Refreshes the view of this visualisation to reflect the changes in the MComponent.
     * <p>
     * In this case, the change may be a structural change in the graph model.
     * A check is made for all elements in the mcomponent whether a visualisation
     * exists for this element and a check is made for all visualisation whether their corresponding
     * component is still element of the mcomponent. When needed visualisations are added or
     * removed. 
     * After that, all mcomponents in the model are called to do a notify to their corresponding vcomponent.
     * Visualisations that never had a corresponding MComponent, like extra text labels, are left unchanged.
     * <p>
     * The implementation of this method may change drastically in times to come.
     * Its effect however is supposed to stay the same.
     */
    public void mcomponentChanged(GraphEvent ge) {
       /*
        OK, dus de conclusie is dat dit te inefficient is. Oftewel: 
            er wordt kwistig vaak iterators doorlopen
            het gebruik van getViewerForMComponent is te duur
            als je nou bv alle MCs in een set gooit, dan de VC's 
            checkt en meteen hun mcs UIT de set gooit en dan de rest....
            maar een element uit de set gooien, vereist dat niet ook te veel
            doorlopen van lisjten? of is de hashset heel efficient?
       */
       
        //@@@dit is echt tering inefficient.
        logger.info("VGraphAdapter: mc changed... source: "  + ge.getSource().getClass().getName());
        if (mcomponent == null)
            return;
        //creating and adding new viewers where needed...
        Iterator it;
        MComponent mc;
        //first do a notify for all elements of the MGraph
        //@@fucking inefficient! this is not funny. too many times the same iterator.
        it = ((MGraph)mcomponent).getMComponents();
        while (it.hasNext()) {
            mc = (MComponent)it.next();
            mc.notifyMComponentListeners(); //wil je dit? en waarom? als het element is verander, moet dan zo nodig zo de notify gedaan? en niet op de elementen zelf? ik zou hoogstens in de MGraph een 'notifyrecursive' implementeren, ALS EXTRA CONVENIENCE METHOD!!!!
            //en er hier vanuit gaan dat de model elements verder zelf die update wel doen! (en dan dus checken of de controllers en andere apps zich daar aan houden :)
        }
        //start creating new viewers where needed for all elements of the mgraph
        it = ((MGraph)mcomponent).getMComponents();
        while (it.hasNext()) {
            mc = (MComponent)it.next();
            if (getViewerForMComponent(mc) == null) {
                //no viewer for this component exists yet, so create and add new viewer
                //find out what is the default viewer class
                String viewClass = mc.getViewerClass();
                // create a viewer
                VComponent vc = null;
                try {
                    vc = (VComponent)Class.forName(viewClass).newInstance();
                } catch (Exception ex) {
                    logger.severe("error creating visualisation object:" + ex + ex.getMessage());
                    ex.printStackTrace();
                }
                //initialise VGraph property of viewer
                vc.setVGraph(this);
                //connect viewer to MComponent
                vc.setMComponent(mc);
                //add viewer to container of this VGraphAdapter
                addVComponent(vc);
            }
        }
        //removing viewers where needed... (recursively!!!)
        removeUnconnectedViewers();
        //do another notify for all elements, in case something was added or removed
        it = ((MGraph)mcomponent).getMComponents();
        while (it.hasNext()) {
            mc = (MComponent)it.next();
            mc.notifyMComponentListeners(); //ALWEER?!?!?!?!?!? als er iets geadd is, is er al een setMC (== meteen mcCHanged) gedaan!
        }
    }

    /** 
     * recursively removes all viewers for which the mcomponent has been removed from the MGraph 
     */
    protected void removeUnconnectedViewers() {
//        logger.info("removing vcomponent recursively...");
        if (container != null) {
            iter = container.listIterator();
            while (iter.hasNext()) {//for every child, remove if mcomponent no longer in graph
                VComponent next = (VComponent)iter.next();
                if (!(next instanceof VNonGraphElement)) { //leave VNonGraphElement unchanged, since they belong to the graph visualisation
                    if (!((MGraph)mcomponent).containsMComponent(next.getMComponent())) {
                        next.setMComponent(null);
                        next.setParent(null);
                        iter.remove(); //remove component (don't use removeVComponent!! (concurrentModExc on container...)
                        logger.warning("removing sub-vcomponent..." + next.getClass().getName());
                    } else if (next instanceof VGraphAdapter) {
                        ((VGraphAdapter)next).removeUnconnectedViewers();
                    }
                }
            }
        }
    }

 
/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

/*----- toXMLString support -----*/

    /**
     * Returns the XML representation for all children as content.
     */
    protected String getContent() {
        String result = "";
         //get alle elements...
         if (container != null) {
            iter = container.listIterator();
            while (iter.hasNext()) {//for every child, get XML
                VComponent next = (VComponent)iter.next();
                result = result + next.toXMLString() + "\n";
            }
            iter = null;
        }
        return result;
    } 

/*----- readXML support -----*/

    /**
     * In addition to the superclass functionality, this method reads and processes the boolean attribute
     * "add". If add is true, the XML that is currently being processed should not replace the current
     * graph content but should rather be addded to the existing graph. If add is false, the current graph 
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
            //remove all..
            if (container != null) {
                iter = container.listIterator();
                ArrayList al = new ArrayList();
                while (iter.hasNext()) {//for every child, remove if mcomponent no longer in graph@@@@???wat betekent dit commentaar? [DR]
                    al.add((VComponent)iter.next());
                }
                iter = null;
                for (int i = 0; i < al.size(); i++) {                    
                    VComponent next = (VComponent)al.get(i);
                    if (!(next instanceof VNonGraphElement)) //textboxes and such do not have an mcomponent ;o)
                        next.setMComponent(null); //any VComponent that is removed, should no longer listen to its MComponent...
                    removeVComponent(next); //remove component
                }
            }
        }
    }        

    /**
     * This method reads all elements till the closing tag. Every element is interpreted as a VComponent; a new
     * VComponent of the right class is created, it is added to this VGraphAdapter and readXML(tokenizer) is 
     * called on that element to process the rest of the XML for that element.
     * <br>
     * To ensure proper operation of the readXML methods, the following requirements are made for the content XML:
     * <ul>
     *      <li> For every element that is introduced, the corresponding MComponent should already be present in the 
     *           MGraph of this VGraph.
     * </ul>
     */
    protected void readContent(XMLTokenizer tokenizer) throws IOException {
        //read all subcomponents....
        while (!tokenizer.atETag(getClass().getName())) {
            //read classname of next element (if atSTag)
            if (tokenizer.atSTag()) {
                try {
                    Class vcClass = Class.forName(tokenizer.getTagName());
                    VComponent newVC = null;
                    VComponent testVC = (VComponent)vcClass.newInstance();
                    if (   (testVC instanceof VGraph)  //if the next component is a VGraph,
                        && (tokenizer.attributes.containsKey("add"))
                        && (((String)tokenizer.attributes.get("add")).equals("true")) //and add='true'
                        ) { // find existing VComponent, instead of creating new.
                        if (tokenizer.attributes.containsKey("mcomponent")) {
                            String mcID = (String)tokenizer.attributes.get("mcomponent");
                            if (!(mcID.equals(""))) {
                                MComponent newMC = ((MGraph)getMComponent()).getMComponent(mcID);
                                newVC = getViewerForMComponent(newMC); //if one exists, this one should process XML.
                            }
                        }
                    } 
                    if (newVC == null) {//VC does not exist yet: create & add, & connect to MC
                        newVC = (VComponent)vcClass.newInstance();
                        newVC.setVGraph(this);
                        if (tokenizer.attributes.containsKey("mcomponent")) {
                            String mcID = (String)tokenizer.attributes.get("mcomponent");
                            if (!(mcID.equals(""))) {
                                //set mcomponentconnection...
                                MComponent newMC = ((MGraph)getMComponent()).getMComponent(mcID);
                                newVC.setMComponent(newMC);
                            }
                        }
                        addVComponent(newVC);
                    }
                    newVC.readXML(tokenizer);
                } catch (Exception e) {
                    logger.severe("error reading XML in VGraphAdapter: " + e);
                    tokenizer.nextToken(); //skip offending input
                }                
            } else {
                tokenizer.nextToken(); //skip unknown stuff
            }
        }
    }

    /**
     * Helper method: given an MComponent, this method creates the corresponding VComponent,
     * registers it with its MComponent and adds it to this VGraphAdapter.
     */
    protected void viewMComponent(MComponent mc) {
        //find out what is the viewer class
        String viewClass = mc.getViewerClass();
        // create a viewer
        VComponent vc = null;
        try {
            vc = (VComponent)Class.forName(viewClass).newInstance();
        } catch (ClassCastException ex) {
            logger.severe("error creating visualisation object:" + ex.getClass().getName() + ex.getMessage() + viewClass);
        } catch (ClassNotFoundException ex) {
            logger.severe("error creating visualisation object:" + ex.getClass().getName() + ex.getMessage() + viewClass);
        } catch (InstantiationException ex) {
            logger.severe("error creating visualisation object:" + ex.getClass().getName() + ex.getMessage() + viewClass);
        } catch (IllegalAccessException ex) {
            logger.severe("error creating visualisation object:" + ex.getClass().getName() + ex.getMessage() + viewClass);
        }                
        //initialise VGraph property of viewer
        ((VComponent)vc).setVGraph(this);
        //connect viewer to MComponent
        vc.setMComponent(mc);
        //add viewer to container of this VGraphAdapter
        addVComponent(vc);
    }
    

}