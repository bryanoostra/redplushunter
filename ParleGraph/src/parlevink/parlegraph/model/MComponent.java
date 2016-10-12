/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MComponent.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2003/06/23 09:46:09  dennisr
// *** empty log message ***
//
// Revision 1.1  2002/09/16 14:05:41  dennisr
// first add
//
// Revision 1.7  2002/05/16 09:53:29  reidsma
// Everything is redocumented

package parlevink.parlegraph.model;

import parlevink.parlegraph.view.GraphEvent;
import parlevink.xml.*;

/**
 * This interface is the base interface for the graph model classes.
 * These classes are used to model abstract graphs and to manipulate them.
 * Such graphs could be NFA's, generic labeled directed graphs, or any other
 * type of graph model for which the appropriate subclasses exist.
 * <BR>
 * All graph elements are MComponents: edges, vertices and graphs in all their different implementations.
 * This top-level interface defines the following behaviour of components in a graph:
 * <ul>
 * <li>The communication between an abstract graph and its visualisations
 * <li>The methods to export or import an abstract graph to or from an XML representation 
 * <li>Methods to maintain ID's of components.
 * </ul>
 * <br>
 * The communication between an abstract graph model and its visualisations 
 * is defined in the following way:
 * <ul>
 * <li> 
 *      MComponents are visualized by VComponents (see VComponent in the parevink.parlegraph.view package).
 * <li> 
 *      It is possible to define which subclass of VComponent should be used 
 *      by default to visualise an MComponent of a certain class.
 * <li>
 *      Whenever a MComponent should be visualised a VComponent will be created, possibly using
 *      the default visualisation class. This visualisation object can be registered as a MComponentListener 
 *      for this MComponent.
 * <li> 
 *      The MComponent interface defines a method 'notifyMComponentListeners' that notifies all MComponentListeners 
 *      that a change has occurred in this MComponent (provided the MComponent is flagged to be 'dirty', see the last point).
 *      This means among other things that when 'notifyMComponentListeners'
 *      is called, all visualisations of this MComponent are notified of the changes on this MComponent, 
 *      so the visualisation can be changed to reflect the new situation.
 * <li>
 *      If the above notification method would be called for every small change in the graph data model, the 
 *      performance of the model in extensive algorithms would be heavily impaired. Therefore the 
 *      'notifyMComponentListeners' is NOT automatically called by the graph model itself when changes are made to the model.
 *      Instead, calling this method is a responsibility of the 
 *      application using this package, or a responsibility of the visualisation components. 
 * <li>
 *      Another improvement in performance is made through the 'dirty flag' support.
 *      A call to 'notifyMComponentListeners' will only result in actual notification calls to the listeners if
 *      the MComponent is flagged to be 'dirty', i.e. some change of the MComponent has occurred since the 
 *      previous call to 'notifyMComponentListeners'.
 *      <br>
 *      If you build your own MComponent's, you must set the dirty flag in every method where you changes the MComponent, 
 *      except when you call a superclass method that would set the dirty flag. To avoid unnecesary calls to flagDirty(), 
 *      every method that changes the dirty flag (directly or indirectly) should be documented as accordingly.
 * </ul>
 * <br> 
 * An MComponent can also have an ID. This ID may be unique within a context, but this is not enforced
 * by the default implementations. The ID's are most heavily used in the MGraph interface, where all 
 * MComponents in the graph can be accessed through their ID.
 * <br>
 * To ensure that the MGraph can keep track of all IDs, changing ID's of components that are part 
 * of an MGraph should be done through the methods of the MGraph.
 * <br>
 * For XML support, the MComponent interface extends the XMLIzable interface, defining methods for reading & writing objects in XML format to 
 * and from strings and/or streams.
 *
 * @author Dennis Reidsma, UTwente
 */
public interface MComponent extends XMLizable {

/*+++++++++++++++++++++++++++++++++++++++++++++++++++++*
 *+ Listeners section                                 +*
 *+ This section contains the communication           +* 
 *+ between the graph model and the visualisations    +*
 *+ through a Listener structure                      +*
 *+ Objects other than visualisations may also        +*
 *+ register as Listeners.                            +*
 *+++++++++++++++++++++++++++++++++++++++++++++++++++++*/

	/**
	 * Registers a MComponentListener. MComponentListeners are notified of changes in the 
	 * MComponent whenever notifyMComponentListeners() is called on this MComponent.
     * Sets the dirty flag to true.
	 */
	public void addMComponentListener (MComponentListener newListener);

	/**
	 * Removes a MComponentListener, so it will no longer be notified of changes in this MComponent. 
	 */
	public void removeMComponentListener (MComponentListener listener);

	/**
	 * This method notifies all registered MComponentListeners that the contents of this 
	 * MComponent have been changed. Any Listeners that seem to have disappeared are
	 * removed from the list of Listeners. 
	 * <br>
	 * NB: This method has only any effect if isDirty() returns true! 
	 * (see overview documentation for the MComponent interface).
	 * After a call to this method, isDirty == false; except when a new change has been 
	 * made to this MComponent during the notification process.
	 */
	public void notifyMComponentListeners ();

    /**
     * Returns true if this MComponent has been flagged to be dirty since the last notification of the MComponentListeners.
     */
    public boolean isDirty();
    
    /**
     * Sets the isDirty flas.
     * If the MComponent is changed, this method should be called with parameter 'true', to ensure that
     * the MComponentListeners are notified of the changes in the next call to 'notifyMComponentListeners'.
     */
    public void flagDirty(boolean isDirty);
    
/*+++++++++++++++++++++++++*
 * Graph functionality.    *
 *+++++++++++++++++++++++++*/
   
    /**
     * Sets the parent MGraph of which this MComponent is an element.
     * <b>This method should be called only by the MGraph to which this MComponent is added!</b>
     * Aftwerward, isDirty == true
     */
    public void setMGraph(MGraph g);
    
    /**
     * Returns the MGraph of which this MComponent is a part (or null when this MComponent is not part of any MGraph).
     */
    public MGraph getMGraph();
    
    /**
     * Returns the MGraph of which this MComponent is a part, 'up' levels above the
     * MGraph which directly contains this MComponent (or null when this MComponent is not part of any MGraph).
     * If there are not enough 'up' levels above this mcomponent, the method returns the topMGraph.
     * getMGraph(1) amounts to getMGraph().
     */
    public MGraph getMGraph(int up);
    
    /**
     * Returns the top-level MGraph of which this MComponent is a part (or null when this MComponent
     * is not part of any MGraph). Uses recursive calls to getMGraph().
     */
    public MGraph getTopMGraph();
    
    /**
     * Sets the ID of this MComponent.
     * Every MComponent has an ID. This identifier is not necessarily unique. If this ID should be unique 
     * within a context, the context classes should take care of this.
     * <br>
     * In the context of an MGraph uniqueness of the elements of the graph is guaranteed by the MGraphAdapter implementation.
     * Aftwerward, isDirty == true
     * <br>
     * NB: This method is usually called by the parent MGraph. If you want the ID of this MComponent
     * changed, it is best to call the appropriate methods in MGraph.
     */
    public void setID(String newID);

    /**
     * Returns the ID of this MComponent.
     * Every MComponent has an ID. This identifier is not necessarily unique. If this ID should be unique 
     * within a context, the context classes should take care of this.
     * <br>
     * In the context of an MGraph uniqueness of the elements of the graph is guaranteed by the MGraphAdapter implementation.
     */
    public String getID();
    
/*++++++++++++++++++++++++*
 * visualisation section. *
 *++++++++++++++++++++++++*/

	/**
     * This method defines which visualisation class should be used by default
     * to visualise this MComponent.
     * @param newClass should be the full class name of the appropriate subclass of VComponent.
     * Aftwerward, isDirty == true
     */
	public void setViewerClass(String newClass);

	/**
     * Returns the default viewer class for this MComponent, as set through 'setViewerClass'.
     */
	public String getViewerClass();

/*++++++++++++++++++++++++*
 * marker-pass support.   *
 * TO BE EXTENDED         *
 * & NORMALIZED           *
 * This stuff is new & not documented yet.
 * if you want to use it, ask Dennis Reidsma for more information
 *++++++++++++++++++++++++*/
    
/*
Passing is administrated using a bitmap. Every passer can use its own bit for passing.
No central maintenance of used bits is done yet.


*/

/** This stuff is new & not documented yet.
    if you want to use it, ask Dennis Reidsma for more information */
    public void pass(int pass);
    
/** This stuff is new & not documented yet.
    if you want to use it, ask Dennis Reidsma for more information */
    public void unpass(int pass);
    
/** This stuff is new & not documented yet.
    if you want to use it, ask Dennis Reidsma for more information */
    public void unpassAll();  //maybe this one to MGraph einterface?
    
/** This stuff is new & not documented yet.
    if you want to use it, ask Dennis Reidsma for more information */
    public int getPassStatus();
   
}
