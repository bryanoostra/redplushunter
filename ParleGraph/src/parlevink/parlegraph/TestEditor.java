/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:29 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: TestEditor.java,v $
// Revision 1.1  2006/05/24 09:00:29  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.2  2005/11/09 16:03:59  swartjes
// Removed error of Test class
//
// Revision 1.1  2005/11/08 16:02:09  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.10  2005/01/05 08:59:52  dennisr
// *** empty log message ***
//
// Revision 1.9  2004/10/16 15:20:08  zwiers
// jdk1.5 patch: show() replaced by setVisible(true)
//
// Revision 1.8  2003/07/31 20:29:44  dennisr
// *** empty log message ***
//
// Revision 1.7  2003/07/29 12:10:41  dennisr
// GUI quirks
//
// Revision 1.6  2003/07/16 16:47:23  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/03/28 14:51:00  dennisr
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
// Revision 1.3  2002/11/05 09:41:20  dennisr
// icons added (checkout data directory!)
//
// Revision 1.2  2002/10/18 10:29:40  dennisr
// *** empty log message ***
//
// Revision 1.1  2002/09/28 15:13:26  dennisr
// first add
//
// Revision 1.2  2002/09/23 07:44:43  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:05:18  dennisr
// first add
//
// Revision 1.24  2002/06/10 08:22:55  reidsma
// Uitbreiding GUI
//
// Revision 1.23  2002/06/04 13:41:11  reidsma
// first GUI for default controller
//
// Revision 1.22  2002/06/04 10:12:45  reidsma
// improved controllers for editing graphs
//
// Revision 1.21  2002/05/16 10:00:54  reidsma
// major update (see changes.txt)
//
// Revision 1.19  2002/03/12 12:16:53  reidsma
// no message
//
// Revision 1.18  2002/03/04 12:53:19  reidsma
// Simple deleting of nodes using DELETE-key implemented in controller
//
// Revision 1.17  2002/02/19 16:13:00  reidsma
// no message
//
// Revision 1.16  2002/02/19 10:28:25  reidsma
// Bugging
//
// Revision 1.15  2002/02/18 16:08:31  reidsma
// feeding fish
//
// Revision 1.14  2002/02/18 15:03:40  reidsma
// Antz :o))))))
// DONT USE ANTZ
//
// Revision 1.13  2002/02/18 12:26:26  reidsma
// no message
//
// Revision 1.12  2002/02/11 09:18:21  reidsma
// no message
//
// Revision 1.11  2002/02/05 14:35:58  reidsma
// layouting
//
// Revision 1.10  2002/02/04 16:36:49  reidsma
// stuff
//
// Revision 1.9  2002/02/04 16:19:35  reidsma
// debugging
//
// Revision 1.8  2002/02/01 13:03:51  reidsma
// edit controller; general maintenance
//
// Revision 1.7  2002/01/29 14:58:08  reidsma
// debugging
//
// Revision 1.6  2002/01/25 15:23:09  reidsma
// Redocumentation
//
// Revision 1.5  2002/01/25 10:41:39  reidsma
// leesbaarder gemaakt
//

package parlevink.parlegraph;

import java.util.logging.*;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import parlevink.parlegraph.utils.*;
import parlevink.parlegraph.control.*;
import parlevink.parlegraph.layout.*;
import parlevink.xml.*;

import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Class for testing of all kinds of tricks and behaviours in parlegraph.
 * You can use this code as an example, but that would not help
 * in creating nice and beautiful applications. Better read the real 
 * documentation or ask Dennis for help :o)
 */
public class TestEditor {
   private VGraphPanel vgpan;
   private JScrollPane scroller;
   private JFrame f;
   private JPanel panel;
    private JSlider zoomSlider;
	//logging
	// Define a static category variable so that it references the
	// Category instance named "parlevink.parlegraph.view.Test".
	static Logger logger;
    public static String ICON_NAME = "juggle.jpg";
    
    private void getResourceIcon(JFrame fr, String name) {
      try {
        ImageIcon i = null;
        i = new ImageIcon(getClass().getResource(name));
        fr.setIconImage(i.getImage());
      } catch (Exception e) {
        System.out.println("Icon not loaded: " + e.getMessage());
        e.printStackTrace();
      }
    }


   private void fillPanel() {
    
        //create graph
        MGraphAdapter mg = new MGraphAdapter();
        
        //create nodes
        MLabeledVertex v1 = new MLabeledVertex("v1");
        MLabeledVertex v2 = new MLabeledVertex("v2");
        MLabeledVertex v3 = new MLabeledVertex("v3");

        
        //create edges, set endpoints & add to graph mg. Endpoints are automatically added to MGraph
        MLabeledEdge e8 = new MLabeledEdge("e8");
        e8.setSource(v1);
        e8.setTarget(v2);
        mg.addMEdge(e8);
        e8.setDirected(true);
        
        MLabeledEdge e = new MLabeledEdge("e");
        e.setSource(v2);
        e.setTarget(v3);
        mg.addMEdge(e);
        
        //create anothergraph
        MGraphAdapter mg2 = new MGraphAdapter();
        MLabeledVertex v4 = new MLabeledVertex("4");
        MLabeledVertex v5 = new MLabeledVertex("5");
        MLabeledVertex v6 = new MLabeledVertex("6");
        MLabeledEdge e2 = new MLabeledEdge("e2");
        e2.setSource(v4);
        e2.setTarget(v5);
        mg2.addMEdge(e2);
        mg2.addMVertex(v6);
        
        //Add graph 2 to graph 1, as vertex
        //MLabeledEdge e3 = new MLabeledEdge("e3");
        //e3.setSource(mg2);
        //e3.setTarget(v1);
        //mg.addMEdge(e3);
        mg.addMVertex(mg2);


        MLabeledEdge e9 = new MLabeledEdge("e9");
        e9.setSource(v4);
        e9.setTarget(v1);
        e9.setDirected(true);
        mg.addMEdge(e9);

      
        //create visualisation for graph 1
        VGraphAdapter vg = new VGraphAdapter(0.0,0.0,300.0,300.0,10.0);
        vg.setMComponent(mg); //this operation automatically creates viewer components for all elements of the modelgraph
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        
        //add viewer to the graph view panel
        vgpan.setVGraph(vg);

        //modify modelgraph 
        //mg.removeMEdge(e3);
        //notify viewer of changes
        mg.notifyMComponentListeners(); //viewer is updated?!?!?!?!?!? 

        //add a non-graph-element to the viewer (an element not connected to the model)
        //VTextBox vt = new VTextBox("Partial class hierarchy");
        //vt.setPaintBorder(true);
        //vg.addVComponent(vt);
        vg.doLayout();
        
        
        //test the XML functionality...
        String mvXml = MVGraphLoader.getXML(vg, true);
//        logger.warning(mvXml);
        VGraphAdapter vg3 = null;
        try {
            vg3 = (VGraphAdapter)MVGraphLoader.readVGraph(new XMLTokenizer(mvXml));
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }

        //again:change some in model graph
        MGraph mg3 = (MGraph)vg3.getMComponent();
//        logger.warning("1");
        //mg3.removeMVertex((MVertex)mg3.getMVertices().next());
//        logger.warning("2");
        mg3.notifyMComponentListeners();
//        logger.warning(mg3.toXMLString());
//        logger.warning(vg3.toXMLString());
//        logger.warning("3");
        VTextBox tb= new VTextBox("....");
        tb.allowFocus(false);
        vg3.addVComponent(tb);
        tb.setLocation2D (50,50);
        vgpan.setVGraph(vg3);
   /*     mg.setID("SOME_ID");
        //String mgXML = mg.toXMLString();
        String mgXML = MVGraphLoader.getXML(mg, true);
        //String vgXML = vg.toXMLString();
        logger.warning(mgXML);
        //logger.warning(vgXML);
        MGraphAdapter mg3 = new MGraphAdapter();
        VGraphAdapter vg3 = new VGraphAdapter();
        try {
            mg3 = (MGraphAdapter)MVGraphLoader.readMGraph(new XMLTokenizer(mgXML));
        } catch (IOException ex) {
            logger.severe(ex.getmessage());
        }
        vg3.setMComponent(mg3);
        //mg3.readXML(mgXML);
        mg3.notifyMComponentListeners();
//        vg3.readXML(vgXML);
//        mgXML="<parlevink.parlegraph.model.MGraphAdapter add=\"true\"><parlevink.parlegraph.model.MEdgeAdapter ID=\"11\" source=\"4\" target=\"1\" directed=\"true\"/></parlevink.parlegraph.model.MGraphAdapter>";
  //      vgXML="<parlevink.parlegraph.view.VGraphAdapter add=\"true\"><parlevink.parlegraph.view.VLineEdge mcomponent=\"11\" x=\"50\" y=\"50\" cx=\"55\" cy=\"55\" width=\"20\" height=\"20\"/></parlevink.parlegraph.view.VGraphAdapter>";
        vgpan.setVGraph(vg3);
*/
        LayoutIncrementer inc = new ForceLayoutIncrementer(vg3);
        VGraphLayout forcel = new VIncrementalLayout(vg3, inc);
        VGraphLayout treel = new VTreeLayout(vg3);
        vg3.setLayout(treel);
        CDefaultGraphController control = new CDefaultGraphController(vg3);
        
        ActionMapMap amm = control.getGlobalActionMap();
        Iterator keyIt = amm.keySet().iterator();
            JToolBar toolb = new JToolBar("all controls in one toolbar... :o(");
        while (keyIt.hasNext()) {
            String key = (String)keyIt.next();
            ActionMap a = (ActionMap)amm.get(key);
            Object[] keys = a.allKeys();
            for (int i = 0; i < keys.length; i++) {
                toolb.add((Action)a.get(keys[i]));
            }
            toolb.addSeparator();
        }
            f.getContentPane().add(toolb, BorderLayout.NORTH);
        f.getContentPane().add(zoomSlider, BorderLayout.WEST);
        zoomSlider.addChangeListener(new SliderListener());
    }

   public void start() {
      //create loggger for this test class
        logger = Logger.getLogger(TestEditor.class.getName());      
      f = new JFrame("graph editor test version");
         getResourceIcon(f, ICON_NAME);
     f.setSize(600,600);
      vgpan = new VGraphPanel();
      scroller = new JScrollPane(vgpan,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
      zoomSlider = new JSlider(javax.swing.SwingConstants.VERTICAL, 0, 370, 300); //0 = 400%; 400 = 1%
      f.getContentPane().add(scroller);
      fillPanel();      
      f.setVisible(true);
      f.addWindowListener(new ExitListener());
   }    
   
   private class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            vgpan.setZoom((401.0 - (double)zoomSlider.getValue())/100.0);
        }
    }

   private class ExitListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            f.dispose();
        }
    }
}