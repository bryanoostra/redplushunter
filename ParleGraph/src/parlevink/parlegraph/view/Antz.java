/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */
 
// Last modification by: $Author: swartjes $
// $Log: Antz.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2003/01/24 09:52:57  dennisr
// *** empty log message ***
//
// Revision 1.4  2002/09/24 10:07:14  dennisr
// compile bug fix
//
// Revision 1.3  2002/09/23 13:00:17  dennisr
// *** empty log message ***
//
// Revision 1.2  2002/09/23 07:44:43  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:48  dennisr
// first add
//
// Revision 1.8  2002/03/04 12:16:33  reidsma
// Delegates:
// naast de mousedelegates zijn er nu ook keydelegates. Hiervoor zijn een paar functies hernoemd (getMouseEventdelegate ipv geteventdelegate, etc)
// en een paar toegevoegd. De defaultController krijgt nu alle keyevents te horen.
//
// Revision 1.7  2002/02/26 12:44:57  reidsma
// removed superman-properties
//
// Revision 1.6  2002/02/19 16:13:00  reidsma
// no message
//
// Revision 1.5  2002/02/19 10:28:25  reidsma
// Bugging
//
// Revision 1.4  2002/02/18 16:14:28  reidsma
// nu vliegen ze niet meer uit de bocht
//
// Revision 1.3  2002/02/18 16:08:36  reidsma
// feeding fish
//
// Revision 1.2  2002/02/18 15:03:41  reidsma
// Antz :o))))))
// DONT USE ANTZ
//
// Revision 1.1  2002/02/18 12:26:39  reidsma
// DONT USE!
//

package parlevink.parlegraph.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
/**
 * Warning * * Warning * * Warning * * Warning * * Warning * * Warning * 
 
    INSTABLE CODE
    
    DO NOT USE THESE CLASSES
    
 * Warning * * Warning * * Warning * * Warning * * Warning * * Warning * 
 */
public class Antz {
		
    /* kills all antz... */
    public static void exterminate() {
        fishFeeder.timeToDie = true;
    }
		
	/* fleeps an edge */
    public static void fleep(VEdge ve, double direction) {
        ve.addVComponent(new wanAnt(ve, direction));
    }

	/* antifleeps an edge */
    public static void antifleep(VEdge ve, double direction) {
        ve.addVComponent(new wanAnt(ve, direction));
        ve.addVComponent(new wanAnt(ve, -direction));
    }
    
    /* start feeding fish on panel */
    public static void feedFishEvents(VGraphPanel vgp) {
        fishFeeder ff = new fishFeeder (vgp);
        ff.start();
    }

    /* the visible fleep class */
    private static class wanAnt extends VNonGraphElement {
        /************************
         * attributes section   *	
         ************************/
            VEdge ve;
        	private Rectangle2D fish1;
        	private Rectangle2D fish2;
        	double direction = 1;
        
        /************************
         * Constructors section *	
         ************************/
        	
        	/**
        	 * Creates a new wanAnt
        	 */
        	public wanAnt(VEdge newve, double newdirection) {
        	    //null edges are problem :o)))
        	    //edges with no endpoint too :o))))))
        	    super(newve.getSourceCoord().getX(),newve.getSourceCoord().getY(),5,5);
        	    allowFocus(false);
        		ve = newve;
      		    double x1 = ve.getSourceCoord().getX();
      		    double x2 = ve.getTargetCoord().getX();
      		    double y1 = ve.getSourceCoord().getY();
      		    double y2 = ve.getTargetCoord().getY();
                fish1 = new Rectangle2D.Double(x1, y1, 5,5);
                fish2 = new Rectangle2D.Double(x1 + 0.5 * (x2 - x1), y1 + 0.5 * (y2 - y1), 5,5);
                direction = newdirection;
        		setColor(Color.red);
        		ve.addVComponentMovedListener(this);
        	}
        
          
        /********************
         * Painting section *
         ********************/
         
        	/**
        	 * Paints fleep, moves fish.
        	 */
            public void paintBackground2D(Graphics2D g2){
      		    g2.fill(fish1);
      		    g2.fill(fish2);
      		    move (fish1);
      		    move (fish2);
            }

            /* move a fishfleep */
            public void move(Rectangle2D f) {
      		    double x1 = ve.getSourceCoord().getX();
      		    double x2 = ve.getTargetCoord().getX();
      		    double y1 = ve.getSourceCoord().getY();
      		    double y2 = ve.getTargetCoord().getY();
      		    f.setRect(f.getX() + direction * (x2 - x1), f.getY() + direction * (y2 - y1), 5, 5);
      		    if (f.getX() < Math.min(x1, x2)) {
      		        if (x1 < x2) {
      		            f.setRect(x2, y2, 5, 5);
      		        } else {
      		            f.setRect(x1, y1, 5, 5);
      		        }
      		    } 
      		    if (f.getX() > Math.max(x1, x2)) {
      		        if (x1 < x2) {
      		            f.setRect(x1, y1, 5, 5);
      		        } else {
      		            f.setRect(x2, y2, 5, 5);
      		        }
      		    } 
      		    if (f.getY() > Math.max(y1, y2)) {
      		        if (y1 < y2) {
      		            f.setRect(x1, y1, 5, 5);
      		        } else {
      		            f.setRect(x2, y2, 5, 5);
      		        }
      		    } 
      		    if (f.getY() < Math.min(y1, y2)) {
      		        if (y1 < y2) {
      		            f.setRect(x2, y2, 5, 5);
      		        } else {
      		            f.setRect(x1, y1, 5, 5);
      		        }
      		    }
      		}
        
            public void vcomponentMoved(GraphEvent ge) {
      		    double x1 = ve.getSourceCoord().getX();
      		    double x2 = ve.getTargetCoord().getX();
      		    double y1 = ve.getSourceCoord().getY();
      		    double y2 = ve.getTargetCoord().getY();
//                fish1.setRect(x1, y1, 5,5);
  //              fish2.setRect(x1 + 0.5 * (x2 - x1), y1 + 0.5 * (y2 - y1), 5,5);
            }
    }
    
    private static class fishFeeder extends Thread {
        VGraphPanel vgp;
        public static boolean timeToDie = false;
        
        fishFeeder(VGraphPanel newvgp) {
            vgp = newvgp;
        }
        
        public void run() {
            while (true) {
                if (timeToDie) {
                    timeToDie = false;
                    break;
                }
                vgp.repaint();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("Shut Up, I'm feeding the fish cant you see?!?!?!?");
                }
            }
        }
     }
 


}

