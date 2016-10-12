/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: MVGraphLoader.java,v $
// Revision 1.1  2006/05/24 09:00:28  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:17  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.5  2003/06/24 10:39:12  dennisr
// *** empty log message ***
//
// Revision 1.4  2003/06/24 10:30:38  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/02/24 13:24:00  dennisr
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
// Revision 1.4  2002/06/11 09:10:00  reidsma
// waypoints in multiline edge are now XMLized
// save&load button in default GUIPanel for controller
//
// Revision 1.3  2002/05/16 09:47:53  reidsma
// major update (see changes.txt)
//
// Revision 1.2  2002/02/05 15:29:59  reidsma
// no message
//
// Revision 1.1.1.1  2002/02/05 15:24:37  reidsma
// no message
//

package parlevink.parlegraph.utils;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import parlevink.xml.*;
import java.util.*;
import java.io.*;

import java.util.logging.*;

/** 
 * This class provides functionality to facilitate loading and saving graphs.
 * This class uses the XML defined in the model and view classes for storing graphs. This XML
 * can be encapsulated within an MVGraph tag with the following attributes:
 * <ul>
 * <li> modelclass: the class name of the model graph
 * <li> viewclass: the class name of the graph viewer
 * </ul>
 */
public class MVGraphLoader {

	//logging
	// Define a static category variable so that it references the
	// Category instance named "parlegraph.MComponentAdapter".
	protected static Logger logger = Logger.getLogger("MVGraphLoader");

    /**
     * Gets the XML for a model graph. If encapsulate is true, the XML is encapsulated in an MVGraph tag.
     * if mg = null, null is returned.
     */
	public static String getXML(MGraph mg, boolean encapsulate) {
	    if (mg == null)
	        return null;
	        
	    String result = "";
	    if (encapsulate) {
	        result = result + "<MVGraph modelclass=\"" + mg.getClass().getName() + "\">\n";
	    }
	    result = result + mg.toXMLString();
	    if (encapsulate) {
	        result = result + "\n</MVGraph>";
	    }
	    return result;
	}

    /**
     * Gets the XML for a visualised graph. If encapsulate is true, the XML is encapsulated in an MVGraph tag.
     * The XML for the model graph comes first, followed by the XML for the viewer.
     * if vg = null, null is returned.
     */
	public static String getXML(VGraph vg, boolean encapsulate) {
	    if (vg == null)
	        return null;
	        
	    MGraph mg = (MGraph)vg.getMComponent();
	    String result = "";
	    if (encapsulate) {
	        result = result + "<MVGraph modelclass=\"" + mg.getClass().getName() + 
	                                 "\" viewclass=\"" + vg.getClass().getName() + "\">\n";
	    }
	    result = result + mg.toXMLString();
	    result = result + vg.toXMLString();
	    if (encapsulate) {
	        result = result + "\n</MVGraph>";
	    }
	    return result;
	}

    /**
     * Reads the XML from the XMLTokenizer and returns the MGraph.
     * The XML should be encapsulated in an MVGraph tag. Any viewer information is ignored.
     * If the XML contained no MGraph information, null is returned.
     */
    public static MGraph readMGraph(XMLTokenizer tokenizer) throws IOException {
        boolean b;
        b = tokenizer.setSkipPI(true);
        b = tokenizer.setSkipComment(true);
        b = tokenizer.setCompleteSTags(true);
        b = tokenizer.setSkipDoctype(true);
        MGraph mg;
        String modelClass = "";
        //read MVGraph tag, process relevant attributes
        if (tokenizer.atSTag("MVGraph")) {
            HashMap attributes = tokenizer.attributes;
            modelClass = (String)attributes.get("modelclass");
        }
        tokenizer.takeSTag("MVGraph"); //throws an exception if not at the right tag

        //create the right MGraph
        try {
            mg = (MGraph)Class.forName(modelClass).newInstance();
        } catch (ClassCastException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        } catch (InstantiationException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        } catch (IllegalAccessException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        } catch (ClassNotFoundException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        }                
        
        //read all intervening tags. all MGraph tags go to MGraph; rest are ignored.
        while (!tokenizer.atETag("MVGraph")) {
            if (tokenizer.atSTag()) {
                if (tokenizer.atSTag(modelClass)) {
                    mg.readXML(tokenizer);
                } else {
                    //nested remove of tag-content
                    String tagname = tokenizer.getTagName();
                    int count = 1;
                    while (count != 0) {
                        tokenizer.nextToken();
                        if (tokenizer.atSTag(tagname)) {
                            count++;
                        }
                        if (tokenizer.atETag(tagname)) {
                            count--;
                        }
                    }
                }
            } else { //remove all unknown stuff....
                tokenizer.nextToken();
            }
        }
            
        //remove MVGraph end tag
        tokenizer.takeETag();
        return mg;
    }

    /**
     * Reads the XML from the XMLTokenizer and returns the MGraph.
     * The XML should be encapsulated in an MVGraph tag. Any viewer information is ignored.
     * If the XML contained no MGraph information, null is returned.
     */
    public static void readIntoMGraph(XMLTokenizer tokenizer, MGraph mg) throws IOException {
        boolean b;
        b = tokenizer.setSkipPI(true);
        b = tokenizer.setSkipComment(true);
        b = tokenizer.setCompleteSTags(true);
        b = tokenizer.setSkipDoctype(true);
        String modelClass = "";
        //read MVGraph tag, process relevant attributes
        if (tokenizer.atSTag("MVGraph")) {
            HashMap attributes = tokenizer.attributes;
            modelClass = (String)attributes.get("modelclass");
        }
        tokenizer.takeSTag("MVGraph"); //throws an exception if not at the right tag

        //check class of mg...
        if (!mg.getClass().getName().equals(modelClass)) {
            throw new IllegalArgumentException("The classes of the MGraph (" + mg.getClass().getName() + ") and the graph stored in file (" + modelClass + ") do not correspond");
        }
        
        //read all intervening tags. all MGraph tags go to MGraph; rest are ignored.
        while (!tokenizer.atETag("MVGraph")) {
            if (tokenizer.atSTag()) {
                if (tokenizer.atSTag(modelClass)) {
                    mg.readXML(tokenizer);
                } else {
                    //nested remove of tag-content
                    String tagname = tokenizer.getTagName();
                    int count = 1;
                    while (count != 0) {
                        tokenizer.nextToken();
                        if (tokenizer.atSTag(tagname)) {
                            count++;
                        }
                        if (tokenizer.atETag(tagname)) {
                            count--;
                        }
                    }
                }
            } else { //remove all unknown stuff....
                tokenizer.nextToken();
            }
        }
            
        //remove MVGraph end tag
        tokenizer.takeETag();
    }

    /**
     * Reads the XML from the XMLTokenizer and returns the VGraph (which in turn contains a reference to the MGraph).
     * The XML should be encapsulated in an MVGraph tag. If the XML contained no viewer information,
     * a new (default) viewer for the MGraph is created and returned. If the XML contained no MGraph information, null
     * is returned.
     */
    public static VGraph readVGraph(XMLTokenizer tokenizer) throws IOException {
        boolean b;
        b = tokenizer.setSkipPI(true);
        b = tokenizer.setSkipComment(true);
        b = tokenizer.setCompleteSTags(true);
        b = tokenizer.setSkipDoctype(true);
        MGraph mg;
        VGraph vg;
        String modelClass = "";
        String viewClass = "";
        //read MVGraph tag, process relevant attributes
        if (tokenizer.atSTag("MVGraph")) {
            HashMap attributes = tokenizer.attributes;
            modelClass = (String)attributes.get("modelclass");
            viewClass = (String)attributes.get("viewclass");
        }
        tokenizer.takeSTag("MVGraph"); //throws an exception if not at the right tag

        //create the right MGraph & VGraph
        try {
            mg = (MGraph)Class.forName(modelClass).newInstance();
            vg = (VGraph)Class.forName(viewClass).newInstance();
            vg.setMComponent(mg);
        } catch (ClassCastException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        } catch (InstantiationException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        } catch (IllegalAccessException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        } catch (ClassNotFoundException e) {
            logger.warning("MVGraphLoad error: "+e);
            return null;
        }                

        
        //read all intervening tags. all MGraph tags go to MGraph; VGraph tags go to VGraph, rest are ignored.
        while (!tokenizer.atETag("MVGraph")) {
            if (tokenizer.atSTag()) {
                if (tokenizer.atSTag(modelClass)) {
                    mg.readXML(tokenizer);
                } else if (tokenizer.atSTag(viewClass)) {
                    vg.readXML(tokenizer);
                } else {
                    //nested remove of tag-content
                    String tagname = tokenizer.getTagName();
                    int count = 1;
                    while (count != 0) {
                        tokenizer.nextToken();
                        if (tokenizer.atSTag(tagname)) {
                            count++;
                        }
                        if (tokenizer.atETag(tagname)) {
                            count--;
                        }
                    }
                }
            } else { //remove all unknown stuff....
                tokenizer.nextToken();
            }
        }
            
        //remove MVGraph end tag
        tokenizer.takeETag();
        
        mg.notifyMComponentListeners();//in case some elements had no visualisation yet
        
        return vg;
    }

    /**
     * Reads the XML from the XMLTokenizer and returns the VGraph (which in turn contains a reference to the MGraph).
     * The XML should be encapsulated in an MVGraph tag. If the XML contained no viewer information,
     * a new (default) viewer for the MGraph is created and returned. If the XML contained no MGraph information, null
     * is returned.
     */
    public static void readIntoVGraph(XMLTokenizer tokenizer, VGraph vg) throws IOException {
        boolean b;
        b = tokenizer.setSkipPI(true);
        b = tokenizer.setSkipComment(true);
        b = tokenizer.setCompleteSTags(true);
        b = tokenizer.setSkipDoctype(true);
        MGraph mg = null;
        String modelClass = "";
        String viewClass = "";
        //read MVGraph tag, process relevant attributes
        if (tokenizer.atSTag("MVGraph")) {
            HashMap attributes = tokenizer.attributes;
            modelClass = (String)attributes.get("modelclass");
            viewClass = (String)attributes.get("viewclass");
        }
        tokenizer.takeSTag("MVGraph"); //throws an exception if not at the right tag

        //check class of mg...
        if (!vg.getClass().getName().equals(viewClass)) {
            throw new IllegalArgumentException("The classes of the VGraph and the graph stored in file do not correspond");
        }
        
        //create the right MGraph 
        try {
            mg = (MGraph)Class.forName(modelClass).newInstance();
            vg.setMComponent(mg);
        } catch (ClassCastException e) {
            logger.warning("MVGraphLoad error: "+e);
        } catch (InstantiationException e) {
            logger.warning("MVGraphLoad error: "+e);
        } catch (IllegalAccessException e) {
            logger.warning("MVGraphLoad error: "+e);
        } catch (ClassNotFoundException e) {
            logger.warning("MVGraphLoad error: "+e);
        }                

        
        //read all intervening tags. all MGraph tags go to MGraph; VGraph tags go to VGraph, rest are ignored.
        while (!tokenizer.atETag("MVGraph")) {
            if (tokenizer.atSTag()) {
                if (tokenizer.atSTag(modelClass)) {
                    mg.readXML(tokenizer);
                } else if (tokenizer.atSTag(viewClass)) {
                    vg.readXML(tokenizer);
                } else {
                    //nested remove of tag-content
                    String tagname = tokenizer.getTagName();
                    int count = 1;
                    while (count != 0) {
                        tokenizer.nextToken();
                        if (tokenizer.atSTag(tagname)) {
                            count++;
                        }
                        if (tokenizer.atETag(tagname)) {
                            count--;
                        }
                    }
                }
            } else { //remove all unknown stuff....
                tokenizer.nextToken();
            }
        }
            
        //remove MVGraph end tag
        tokenizer.takeETag();
        
        mg.notifyMComponentListeners();//in case some elements had no visualisation yet
        
    }
}
