/*
 * Based on the Editor in parlevink.parlegraph
 * 
 * @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 08:57:39 $    
 * @since version 0 
 *
 * adapted by: Rieks op den Akker for the DEMO in project.qa      
 */

package project.qa;

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
public class DepTreeEditor {
   private String title;
   private VGraphAdapter vg;
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
    
    public DepTreeEditor(String t){
    	title = t;
    }

    
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
 
// initialize the panel , the viewer and show the MGraph mg   
public void fillPanel(MGraph mg) {
        //create visualisation for graph mg
        vg = new VGraphAdapter(0.0,0.0,300.0,300.0,10.0);
        
        //add viewer to the graph view panel
        vgpan.setVGraph(vg);

        // make the control
        CDefaultGraphController control = new CDefaultGraphController(vg);
        
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
        
        setMGraph(mg);
    }
    
    // show MGraph mg in the editor panel if mg==null nothing happens
    public void setMGraph(MGraph mg){
    	if (mg==null) return;    
        vg.setMComponent(mg); //this operation automatically creates viewer components for all elements of the modelgraph
        
        
        // the following give strange effects!!
        //LayoutIncrementer inc = new ForceLayoutIncrementer(vg);
        //VGraphLayout forcel = new VIncrementalLayout(vg, inc);
        //vg.setLayout(forcel);
        VGraphLayout treelayout = new VTreeLayout(vg,((natlang.deptree.model.DepTreeModel)mg).getRoot());
        vg.setLayout(treelayout);
        mg.notifyMComponentListeners(); //viewer is updated?!?!?!?!?!? 
        vg.doLayout();
    } 
    
    public void setVisible(){
    	f.setVisible(true);	
    }   

   // start this editor: make frame, make panels, viewer and control and toolbar
   // and finally show the MGraph mg
   public void start(MGraph mg) {
      //create logger for this test class
        logger = Logger.getLogger(DepTreeTest.class.getName());      
      f = new JFrame("graph editor "+title+" (test version)");
         getResourceIcon(f, ICON_NAME);
     f.setSize(600,600);
      vgpan = new VGraphPanel();
      scroller = new JScrollPane(vgpan,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
      zoomSlider = new JSlider(javax.swing.SwingConstants.VERTICAL, 0, 370, 300); //0 = 400%; 400 = 1%
      f.getContentPane().add(scroller);
          
      f.setVisible(true);
      f.addWindowListener(new ExitListener());
      fillPanel(mg);
   }    
   
   private class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            vgpan.setZoom((401.0 - (double)zoomSlider.getValue())/100.0);
        }
    }

   private class ExitListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            f.setVisible(false);
        }
    }
}