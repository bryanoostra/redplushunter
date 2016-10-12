/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MComponentAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2003/06/24 10:38:38  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/06/23 09:46:09  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/01/23 16:16:45  dennisr
// probleem met pijltjes gefixt
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
// Revision 1.1  2002/09/16 14:05:41  dennisr
// first add
//
// Revision 1.10  2002/06/04 13:41:11  reidsma
// Everything is redocumented

package parlevink.parlegraph.model;

import parlevink.parlegraph.view.GraphEvent;

import parlevink.xml.*;

import java.util.logging.*;

import javax.swing.event.*;
import java.util.*;
import java.io.*;


/**
 * This class is a default implementation of the MComponent interface. For documentation on the behaviour of this class, see the 
 * interface documentation.
 */
public class MComponentAdapter implements MComponent {

/*++++++++++++++++++++*
 * Variables section. *
 *++++++++++++++++++++*/

    /*--- MComponentListener support ---*/
    
    /**
     * The list of registered Listeners.
     */
	protected EventListenerList listenerList; 
	
	/**
	 * Listeners will only be notified of changes if this flag is set to true to signal that 
	 * this MComponent has actually changed.
	 */
	protected boolean dirty;

	/**
	 * Some internal variable. Don't touch.
	 */
	private GraphEvent graphEvent; 

    /*--- visualisation ---*/
    
    /**
     * The full class name of the default viewer class for this MComponent.
     */
    protected String viewerClass;

    /*--- graph functionality ---*/
    
    /**
     * The reference to the MGraph of which this MComponent is an element. Null if this MComponent is not contained in a parent MGraph.
     */
    protected MGraph currentMGraph;
    
    /**
     * The ID of this MComponent.
     */
    private String ID;

    /*--- log support. --*/
	/**
	* Logging
    */
	protected static Logger logger;

    /*-- marker passing --*/

/** This stuff is new & not documented yet.
    if you want to use it, ask Dennis Reidsma for more information */
    protected int passStatus;

/*++++++++++++++++++++++++*
 * Constructors section.  *
 *++++++++++++++++++++++++*/
 
    /**
     * Creates a new MComponentAdapter.
     * <br>
     * Initial settings:
     * <ul>
     * <li> No MComponentListeners
     * <li> isDirty() == true
     * <li> passStatus() == 0    ('empty')
     * <li> ID is an empty string
     * <li> the new MComponent is not contained in an MGraph.
     * </ul>
     */
     public MComponentAdapter() 
     {
	    listenerList = new EventListenerList();
	    dirty = true;
	    
	    graphEvent = null;

	    passStatus = 0;

	    viewerClass = "parlevink.parlegraph.view.VComponentAdapter";
	
	    currentMGraph = null;
	    ID = "";
	    
	    //initialize logger for this class:
	    if (logger == null) {
	    	logger = Logger.getLogger(getClass().getName());
	    }
     } 
  
/*+++++++++++++++++++++++++++++++++++++++++++++++++++++*
 *+ Listeners section                                 +*
 *+ This section contains the communication           +* 
 *+ between the graph model and the visualisations    +*
 *+ through a Listener structure                      +*
 *+++++++++++++++++++++++++++++++++++++++++++++++++++++*/

	/**
	 * see also: MComponent.addMComponentListener(MComponentListener newListener)
	 */
	public void addMComponentListener (MComponentListener newListener) {
		listenerList.remove(MComponentListener.class, newListener); 
		listenerList.add(MComponentListener.class, newListener); 
	    flagDirty(true); //to ensure that the new listener will get a notify call.
	}

	/**
	 * see also: MComponent.removeMComponentListener(MComponentListener listener)
	 */
	public void removeMComponentListener (MComponentListener listener){
		listenerList.remove(MComponentListener.class, listener);
	}

	/**
	 * see also: MComponent.notifyMComponentListener(MComponentListener newListener)
	 */
	public void notifyMComponentListeners (){
	    if (!isDirty()) {
	        //logger.info("not dirty, no notify");
	        return;
	    }
		/* BEFORE notifying all Listeners, the dirty flag must be set to false.
		This is to ensure that when any changes are made to the MComponent DURING the notification
		process, these changes will also be communicated to the listeners, in a next notify call. */
		flagDirty(false);
		
		// Process the listeners last to first, notifying 
		// those that are interested in this event 
		Object[] listeners = listenerList.getListenerList(); 
		for (int i = listeners.length-2; i>=0; i-=2) { 
			if (graphEvent == null) 
				graphEvent = new GraphEvent(this);
        	((MComponentListener)listeners[i+1]).mcomponentChanged(graphEvent);
		}
	}
	
    /**
     * see also: MComponent.isDirty()
     */
    public boolean isDirty() {
       return dirty; 
       
       //--> dirty behaviour was temporarily suspended due to some unexpected misbehaviour....
       //I'm not certain all problems are fixed :o( but reactivated it anyhow.
       //However, the trouble is in Knowledgegraphs package, so don't worry ;o)
    }
    
    /**
     * see also: MComponent.flagDirty(boolean isDirty)
     */
    public void flagDirty(boolean isDirty) {
        dirty = isDirty;
    }


/*+++++++++++++++++++++++++*
 * Graph functionality.    *
 *+++++++++++++++++++++++++*/
   
    /**
     * see also: MComponent.setMGraph(MGraph g)
     */
    public void setMGraph(MGraph g) {
        currentMGraph = g;
        flagDirty(true);
    }
    
    /**
     * see also: MComponent.getMGraph()
     */
    public MGraph getMGraph() {
        return currentMGraph;
    }

    /**
     * see also: MComponent.getMGraph(int up)
     */
    public MGraph getMGraph(int up) {
        MGraph parent = getMGraph();
        if ((parent == null) || (up <= 0)) 
            if (this instanceof MGraph) {
                return (MGraph)this;
            } else {
                return null;
            }
        return parent.getMGraph(up - 1);
    }

    /**
     * see also: MComponent.getTopMGraph()
     */
    public MGraph getTopMGraph() {
        MGraph parent = getMGraph();
        if (parent == null) 
            if (this instanceof MGraph) {
                return (MGraph)this;
            } else {
                return null;
            }
        while (parent.getMGraph() != null)
            parent = parent.getMGraph();
        return parent;
    }

    /**
     * see also: MComponent.setID(String newID)
     */
    public void setID(String newID) {
        ID = newID;
        flagDirty(true);
    }
    
    /**
     * see also: MComponent.getID()
     */
    public String getID() {
        return ID;
    }

/*++++++++++++++++++++++++*
 * marker-pass support.   *
 * TO BE EXTENDED         *
 * & NORMALIZED           *
 * see comments in interface *
 * see comments in interface *
 * see comments in interface *
 *++++++++++++++++++++++++*/
    
    public void pass(int pass) {
        passStatus = passStatus | pass;
        flagDirty(true);
    }
    
    public void unpass(int pass) {
        passStatus = passStatus & (~pass);
        flagDirty(true);
    }
    
    public void unpassAll() {
        passStatus = 0;
        flagDirty(true);
    }
    
    public int getPassStatus() {
        return passStatus;
    }
    

    
/*++++++++++++++++++++++++*
 * visualisation section. *
 *++++++++++++++++++++++++*/

	/**
     * see also: MComponent.setViewerClass(String newClass)
     */
	public void setViewerClass(String newClass) {
	    viewerClass = newClass;
        flagDirty(true);
	}   

	/**
     * see also: MComponent.getViewerClass()
     */
	public String getViewerClass() {
	    return viewerClass;
	}

/*+++++++++++++++++*
 * XML section.    *
 *+++++++++++++++++*/

/*----- rerouting -----*/
 
    /**
     * This method is rerouted to readXML(XMLTokenizer tokenizer).
     * <br>
     * isDirty == true, afterwards
     */
    public void readXML(Reader in) throws IOException {
        readXML(new XMLTokenizer(in));
        flagDirty(true);
    }
   
    /**
     * This method is rerouted to readXML(XMLTokenizer tokenizer).
     * <br>
     * isDirty == true, afterwards
     */
    public void readXML(String s) {
        try {
            readXML(new XMLTokenizer(s));
        } catch (IOException ex) {
            logger.severe("IOException caught & ignored in mComponentAdapter.readXML(String s): " +ex);
        }
        flagDirty(true);
    }
    
    /**
     * Uses toXMLString() to write XML to the PrintWriter.
     */
    public void writeXML(PrintWriter out) {
        out.print(toXMLString());
    }

/*----- toXMLString support -----*/

    /**
     * This method is as generic as possible for MComponents and subclasses.
     * The default operation uses the following protected methods:
     * <ul>
     *      <li> getOpenStart()
     *      <li> getAttributes()
     *      <li> getContent()
     *      <li> getEnd()
     * </ul>
     * The methods getOpenStart() and getEnd() provide begin and end tags using the full class name of
     * the MComponent as tags.
     * <br>
     * The method getAttributes() should return a list of (attribute,value) pairs. To add attributes for 
     * a new subclass of MComponent simply override this method, call the super class implementation
     * and add your own attributes at the beginning or end of the attribute string.
     * <br>
     * The method getContent() returns the content that should go between the start and end tags.
     * The default implementation returns an empty string.
     */ 
    public String toXMLString() {
        return getOpenStart() + getAttributes() + ">" + getContent() + getEnd();
    }
 
    /**
     * Default: returns the start tag without closing bracket, using the full class name as tag.
     */
    protected String getOpenStart() {
        return "<" + getClass().getName() + " ";
    } 

    /**
     * Default: returns an empty string.
     */
    protected String getContent() {
        return "";
    } 

    /**
     * Default: returns the (attribute,value) pairs for the attributes "ID" and "viewerclass", and the passStatus if it is not 0.
     */
    protected String getAttributes() {
        String result = 
               "ID=\"" +
                 getID() + 
               "\" viewerClass=\"" +
                 getViewerClass() +
               "\"";
        if (passStatus != 0)
            result = result + " passstatus=\"" + passStatus + "\"";
        return result;
    } 

    /**
     * Default: returns the complete end tag using the full class name as tag.
     */
    protected String getEnd() {
        return "</" + getClass().getName() + ">";
    } 

/*----- readXML support -----*/

    /**
     * This method is as generic as possible for MComponents and subclasses.
     * By default, the XMLTokenizer is set to skipping doctype, processing instructions and comments
     * and to parsing complete STags.
     * The default operation uses the following protected methods:
     * <ul>
     *      <li> readStart()
     *      <li> readAttributes()
     *      <li> readContent()
     *      <li> readEnd()
     * </ul>
     * The method readStart() only checks whether the start tag is the correct class name of this MComponent. It does NOT remove
     * the STag from the tokenizer.
     * <br>
     * The method readAttributes() reads the attributes that this component should be able to recognize, using 
     * the HashMap attributes from XMLTokenizer. The start tag and attributes are NOT removed from the tokenizer, 
     * to make it possible for subclasses to read their own attributes as well.
     * <br>
     * The method readContent() should read all content between the start and end tags. The default implementation
     * skips all content until the corresponding end tag, taking care to skip nested pairs of begin and end tags
     * with the same tag name as this MComponent.
     * <br>
     * The method readEnd() reads and removes the end tag from the tokenizer, checking whether the end tag 
     * is the correct class name of this MComponent.
     * <br>
     * isDirty == true, afterwards
     */ 
    public void readXML(XMLTokenizer tokenizer) throws IOException {
        boolean b;
        b = tokenizer.setSkipPI(true); //deze vier regels misschien in een initproc apart, zodat subclasses dit ook weer kunnen aanpassen....
        b = tokenizer.setSkipComment(true);
        b = tokenizer.setCompleteSTags(true);
        b = tokenizer.setSkipDoctype(true);
        readStart(tokenizer);
        readAttributes(tokenizer);
        tokenizer.takeSTag();
        readContent(tokenizer);
        readEnd(tokenizer);
        flagDirty(true);
    }

    /**
     * Default: test whether the start tag is the full class name of this MComponent. Does not change the
     * state of the tokenizer.
     */
    protected void readStart(XMLTokenizer tokenizer) throws IOException {
        //test for right tagname;
        if (!tokenizer.atSTag(getClass().getName())) {
            throw new IllegalStateException("At wrong tag when trying to read " + getClass().getName() + tokenizer.getTagName());
        }
    }

    /**
     * Default: Read and process the attributes "ID", "passStatus" and "viewerClass", if they are present. 
     * Does not change the state of the tokenizer.
     * If this component is contained within a MGraph, the ID is also set for in the MGraph ID administration..
     */
    protected void readAttributes(XMLTokenizer tokenizer) throws IOException {
        //process any known attributes from hashtable
        HashMap attributes = tokenizer.attributes;

        if (attributes.containsKey("ID")) {
            if (getMGraph() != null) {
                getMGraph().setID(this, (String)attributes.get("ID"));
            } else {
                setID((String)attributes.get("ID"));
            }
        }
        
        if (attributes.containsKey("viewerClass")) {
            setViewerClass((String)attributes.get("viewerClass"));
        }
        
        passStatus = 0;
        if (attributes.containsKey("passstatus")) {
            try {
                passStatus = (Integer.parseInt((String)attributes.get("passstatus")));
            } catch (NumberFormatException e) {
                logger.severe("passstatus in XML should be int values. Wrong value: " + attributes.get("passstatus") + ". ---passstatus atttribute ignored.");
            }
        }
    }

    /**
     * Default: skip all content until end tag, processing nested tags correctly.
     * afterwards, the tokenizer should be atETag(classname)
     */
    protected void readContent(XMLTokenizer tokenizer) throws IOException {
        //skip content until (nested correctly) close tag
        int nestCount = 1;
        while (true) {//zolang altijd
            if (tokenizer.atSTag(getClass().getName())) {
                nestCount++;
            } else if (tokenizer.atETag(getClass().getName())) {
                nestCount--;
            }
            if (nestCount == 0) {
                break;
            }
            tokenizer.nextToken();
        }
    }

    /**
     * Default: test whether the end tag is the full class name of this MComponent, then removes the end tag.
     */
    protected void readEnd(XMLTokenizer tokenizer) throws IOException {
        tokenizer.takeETag(getClass().getName());
    }
}
