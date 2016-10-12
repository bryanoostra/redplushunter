/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 08:57:38 $    
 * @since version 0       
 */


package project.qa;

import java.util.logging.*;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import parlevink.parlegraph.utils.*;
import parlevink.parlegraph.control.*;
import parlevink.xml.*;

import java.io.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


/**
 * Test class for testing a simpel graph-viewer
 */
public class DepTreeTest {
		

    private  JFrame f;
    private  JButton newGraphB;
    private Box b;
    public static String ICON_NAME = "engarde.jpg";
    private static DepTreeEditor myeditor;
    
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

private MGraph mg;

   public  void start(MGraph mg) {
   	this.mg = mg;
      
      f = new JFrame("GraphEditor startcenter");
         getResourceIcon(f, ICON_NAME);
      f.setSize(600,600);
      b = new Box(BoxLayout.X_AXIS);
      newGraphB = new JButton("start an editor");
      b.add(newGraphB);
      
      f.getContentPane().add(b);
      f.pack();
      f.setVisible(true);
      f.addWindowListener(new ExitListener());
      newGraphB.addActionListener(new StartGraphListener());
}

   public static void showMGraph(MGraph mg){
   	 if (myeditor!=null)
   	     myeditor.fillPanel(mg);
   }	
  
   public static void do_it(MGraph mg) {
        LogManager.getLogManager().reset();
        new DepTreeTest().start(mg);
    }
     
   
    public static void main(String[] arg) {
        LogManager.getLogManager().reset();
        new DepTreeTest().start(null);
    }
   
   private  class StartGraphListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            myeditor = new DepTreeEditor("");
            myeditor.start(mg);
        }
    }

   private  class ExitListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }
}