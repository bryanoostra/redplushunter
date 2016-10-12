/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: VGraphPanel.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.22  2004/11/10 15:24:41  herder
// Added Tree Layout  and SpanningTrees, antialiasing in VGraphPanel turned on again
//
// Revision 1.21  2004/03/26 22:01:13  dennisr
// *** empty log message ***
//
// Revision 1.20  2004/03/26 21:57:30  dennisr
// *** empty log message ***
//
// Revision 1.19  2003/07/31 20:29:45  dennisr
// *** empty log message ***
//
// Revision 1.18  2003/07/16 16:48:04  dennisr
// *** empty log message ***
//
// Revision 1.17  2003/06/23 09:46:10  dennisr
// *** empty log message ***
//
// Revision 1.16  2003/03/24 14:38:52  dennisr
// *** empty log message ***
//
// Revision 1.15  2003/03/21 16:18:43  dennisr
// *** empty log message ***
//
// Revision 1.14  2003/01/23 16:16:45  dennisr
// probleem met pijltjes gefixt
//
// Revision 1.13  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.12  2002/11/05 14:44:16  dennisr
// nullpointer fix
//
// Revision 1.11  2002/11/04 09:40:39  dennisr
// *** empty log message ***
//
// Revision 1.10  2002/10/21 07:58:35  dennisr
// *** empty log message ***
//
// Revision 1.9  2002/10/01 12:46:27  dennisr
// *** empty log message ***
//
// Revision 1.8  2002/10/01 12:42:04  dennisr
// *** empty log message ***
//
// Revision 1.7  2002/09/30 20:16:48  dennisr
// isDirty support in view package weer verwijderd, documentatie verbeterd, diverse efficiencyverbeteringen in paint methods
//
// Revision 1.6  2002/09/27 10:25:43  dennisr
// bugfix in clearing the correctly sized area of the image when repainting
//
// Revision 1.5  2002/09/27 08:15:44  dennisr
// introduced isDirty in VComponent, improved VGraphPanel to paint more efficiently, improved paint code for labeled vertex, labeled edge and vtextbox
//
// Revision 1.4  2002/09/23 12:49:44  dennisr
// improved repainting on changes
//
// Revision 1.3  2002/09/23 08:32:52  dennisr
// requestfocus added to get events delivered to panel
//
// Revision 1.2  2002/09/23 07:44:44  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:51  dennisr
// first add
//
// Revision 1.10  2002/05/16 10:00:53  reidsma
// major update (see changes.txt)
//
// Revision 1.8  2002/03/04 12:16:33  reidsma
// Delegates:
// naast de mousedelegates zijn er nu ook keydelegates. Hiervoor zijn een paar functies hernoemd (getMouseEventdelegate ipv geteventdelegate, etc)
// en een paar toegevoegd. De defaultController krijgt nu alle keyevents te horen.
//
// Revision 1.7  2002/02/25 08:55:36  reidsma
// It is not allowed to add MCOmponents to an MGraph when they are already element in another MGraph. Since this version, trying to do this results in an exception.
// VLineEdge/VLineTextEdge/VEdgeAdapter: de sourceCoord en targetCoord zijn verhuisd naar de VEdgeAdapter; sourceCoord en targetCoord zijn een read-only property geworden (altijd geinitialiseerd).
//
package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;

//import java.util.logging.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * VGraphPanel is a JPanel that acts as a "wrapper" for a VGraph.
 * <br>
 * As main functionality it performs the following functions:
 * <ul>
 *  <li>Set focus to the right VComponent (whenever a mouse press occurs, the pressed VComponent will get focus)
 *      <b>This functionality might move to the controller someday. Say tuned.....</b>
 *  <li>Distribute mouse events over the focused components. 
 *  <li>Provide a global zoom function
 *  <li>
 * </ul>
 * Right now it is a single focus version, which greatly improves speed but has some other drawbacks....
 */
public class VGraphPanel extends JPanel implements  MouseListener, 
				                                    MouseMotionListener, 
				                                    KeyListener,
				                                    VComponentMovedListener {
    /**
     * The graph viewed on this panel
     */
    protected VGraph vg;
    
    int imageWidth = 0;
    int imageHeight = 0;
    
    /**
     * The transform used for zooming 
     */
    AffineTransform at;
    
    /**
     * An arraylist of the VComponents in the VGraph that currrently have focus. Used when multifocus is coming in....
     */
    protected ArrayList focussedComponents;
    
    protected VComponent focusComponent;
    
    /**
     * The zoom factor of the panel.
     */
    protected double zoom;
    
    /** 
     * Initializes the panel; registering for mouse and key events; zoomfactor 1
     */
    public VGraphPanel() {
        super();
        setToolTipText("Graph panel");
        setBackground(Color.white);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        vg = null;
        zoom = 1;
        at = AffineTransform.getScaleInstance(zoom, zoom);
        focussedComponents = new ArrayList();
        focusComponent = null;
        requestFocus();   //the erquestfocus is needed to get the key events delivered to this panel!!!!!
    }

    /**
     * Initializes the panel and sets the VGraph to be displayed
     */
    public VGraphPanel(VGraph newVg) {
        this();
        setVGraph(newVg);
    }

    /**
     * Sets the VGraph to be displayed.
     * Also registers the VGraphPanel as a VComponentMovedListener at the VGraph,
     * To keep track of the size of the displayed graph.
     * The top level graph cannot be moved around through mouse events.
     */
    public void setVGraph(VGraph newVg) {
        if (vg != null)
            vg.removeVComponentMovedListener(this);
        vg = newVg;
        focussedComponents = new ArrayList();
        if (vg != null) {
            focussedComponents = vg.getFocussedComponents();
            vg.addVComponentMovedListener(this);
            //vg.setMoveMode((vg.getMoveMode() | vg.MM_ALLOW_MOVE) - vg.MM_ALLOW_MOVE);
            imageWidth = (int)(zoom*vg.getWidth()+3);
            imageHeight = (int)(zoom*vg.getHeight()+3);
            //set size of panel (vg size or zoom may have changed)
  		    setPreferredSize(new Dimension((int)(zoom * Math.abs(vg.getX())) + imageWidth, (int)(zoom * Math.abs(vg.getY())) + imageHeight)); //sja, hier had-ie dus groter moeten zijn. De getWidth etc gaat mis als de subcomponenten buiten het ding staan....
        }
        //warn scrollbars (panel size may have changed)
        revalidate();

        repaint();
        requestFocus();
    }
   
    public VGraph getVGraph() {
        return vg;
    }

    //@@@goed plaatsen & doccen
    public String getToolTipText(MouseEvent event){
        if (vg == null)
            return "No VGraph";
        Point2D p = new Point2D.Double((int)(event.getX()/zoom), (int)(event.getY()/zoom));
        ArrayList toolTipCandidates = vg.findAllVComponentsAt(p);
        for (int i = 0; i < toolTipCandidates.size(); i++) {
            if (((VComponent)toolTipCandidates.get(i)).getToolTipText() != null)
                return ((VComponent)toolTipCandidates.get(i)).getToolTipText();
        }
        return null;
    }

    /** 
     * Paints the panel. 
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (vg != null) {
            Graphics2D panelGraphics = (Graphics2D)g;
            panelGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                       RenderingHints.VALUE_ANTIALIAS_ON);
            panelGraphics.transform(at);
            vg.paintVComponent(panelGraphics);
       	}
    }
    
    public void setZoom(double newZoom) {
        if (newZoom != 0) {
            zoom = newZoom;
            if (vg == null)
                return;
            at = AffineTransform.getScaleInstance(zoom, zoom);
            imageWidth = (int)(zoom*vg.getWidth()+3);
            imageHeight = (int)(zoom*vg.getHeight()+3);
            //set size of panel (vg size or zoom may have changed)
      		setPreferredSize(new Dimension((int)(zoom * Math.abs(vg.getX())) + imageWidth, (int)(zoom * Math.abs(vg.getY())) + imageHeight)); //sja, hier had-ie dus groter moeten zijn. De getWidth etc gaat mis als de subcomponenten buiten het ding staan....
            //warn scrollbars (panel size may have changed)
            revalidate();

            repaint();
            requestFocus();
	    }
    }


	/**
	 * When the content graph of this panel changes, the size of the panelshould change as well....
	 */
	public void vcomponentMoved(GraphEvent ge) {
            int newWidth = (int)(zoom*vg.getWidth()+3);
            int newHeight = (int)(zoom*vg.getHeight()+3);
            imageWidth = newWidth;
            imageHeight = newHeight;
            //set size of panel (vg size or zoom may have changed)
      		setPreferredSize(new Dimension((int)(zoom * Math.abs(vg.getX())) + imageWidth, (int)(zoom * Math.abs(vg.getY())) + imageHeight)); //sja, hier had-ie dus groter moeten zijn. De getWidth etc gaat mis als de subcomponenten buiten het ding staan....
            //warn scrollbars (panel size may have changed)
            revalidate();

            repaint();
            requestFocus();
	}

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void mouseClicked(MouseEvent e) {
        if (focusComponent == null)
            return;
        checkFocus();
        if ((vg != null) && (focusComponent != null)) {
            //focussedComponents = vg.getFocussedComponents();        
        	//for (int i = 0; i < focussedComponents.size(); i++)
            	//(focusComponent).mouseClicked(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        	focusComponent.mouseClicked(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        	repaint();
        }
//        requestFocus();//om de repaint af te dwingen. Stom....
    }

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void mouseEntered(MouseEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        //if ((vg != null) && (focusComponent != null))
//            focussedComponents = vg.getFocussedComponents();        
  //      for (int i = 0; i < focussedComponents.size(); i++)
    //        (focusComponent).mouseEntered(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        if (focusComponent != null) 
        	focusComponent.mouseEntered(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        repaint();
//        requestFocus();
    }


    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void mouseExited(MouseEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        /*if ((vg != null) && (focusComponent != null))
            focussedComponents = vg.getFocussedComponents();        
        for (int i = 0; i < focussedComponents.size(); i++)
            (focusComponent).mouseExited(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        */
        if (focusComponent != null) 
        	focusComponent.mouseExited(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        repaint();
//      requestFocus();
    }

    /**
     * Event handling: first set the fous to the most recently mouse-pressed VComponent,
     * then pass on to VComponents that have focus
     */
    public void mousePressed(MouseEvent e) {
        if (vg == null) 
            return;
        if (vg != null) {
        	Point2D p = new Point2D.Double((int)(e.getX()/zoom), (int)(e.getY()/zoom));
        	ArrayList focusCandidates = vg.findAllVComponentsAt(p);
        	VComponent newFocusComponent = null;
        	for (int i = 0; i < focusCandidates.size(); i++) {
        	    if (((VComponent)focusCandidates.get(i)).focusAllowed()) {
        	        newFocusComponent = (VComponent)focusCandidates.get(i);
        	        break;
        	    }
        	}
			if (focusComponent != newFocusComponent) {
			    moveFocusTo(newFocusComponent);
			}
			/*if (!focussedComponents.contains(newFocusComponent)) {
				for (int i = 0; i < focussedComponents.size(); i++) {
				    if (focussedComponents.get(i) != null) {
					    (focusComponent).setFocus(false);
					}
				}
				focussedComponents.clear();
				
				if (newFocusComponent != null) {
    				focussedComponents.add(newFocusComponent);
   					newFocusComponent.setFocus(true);
				}
			}
            focussedComponents = vg.getFocussedComponents();        
        	for (int i = 0; i < focussedComponents.size(); i++)
            	(focusComponent).mousePressed(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));*/
            if (focusComponent != null) 
        	    focusComponent.mousePressed(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        	repaint();
        }
      requestFocus();
    }

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void mouseReleased(MouseEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        /*if (vg != null)
            focussedComponents = vg.getFocussedComponents();        
        for (int i = 0; i < focussedComponents.size(); i++)
            (focusComponent).mouseReleased(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        */
        if (focusComponent != null) 
        	focusComponent.mouseReleased(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        repaint();
        //requestFocus();
    }

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void mouseDragged(MouseEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        /*if (vg != null)
            focussedComponents = vg.getFocussedComponents();        
        for (int i = 0; i < focussedComponents.size(); i++)
            (focusComponent).mouseDragged(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        */
        if (focusComponent != null) 
        	focusComponent.mouseDragged(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        repaint();
        //requestFocus();
    }

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void mouseMoved(MouseEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        /*if (vg != null)
            focussedComponents = vg.getFocussedComponents();        
        for (int i = 0; i < focussedComponents.size(); i++)
            (focusComponent).mouseMoved(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        */
        if (focusComponent != null) 
        	focusComponent.mouseMoved(new GraphMouseEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), (int)(e.getX()/zoom), (int)(e.getY()/zoom), e.getClickCount(), e.isPopupTrigger()));
        repaint();
        //requestFocus();
    }

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void keyPressed(KeyEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        /*if (vg != null)
            focussedComponents = vg.getFocussedComponents();        
        for (int i = 0; i < focussedComponents.size(); i++)
            (focusComponent).keyPressed(new GraphKeyEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
        */
        if (focusComponent != null) 
        	focusComponent.keyPressed(new GraphKeyEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
        repaint();
       // requestFocus();
    }

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void keyReleased(KeyEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        /*if (vg != null)
            focussedComponents = vg.getFocussedComponents();        
        for (int i = 0; i < focussedComponents.size(); i++)
            (focusComponent).keyReleased(new GraphKeyEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
        */
        if (focusComponent != null) 
        	focusComponent.keyReleased(new GraphKeyEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
        repaint();
        //requestFocus();
    }

    /**
     * Event handling: pass on to VComponents that have focus
     */
    public void keyTyped(KeyEvent e) {
        if (focusComponent == null)
            return;
        if (vg == null) 
            return;
        checkFocus();
        /*if (vg != null)
            focussedComponents = vg.getFocussedComponents();        
        for (int i = 0; i < focussedComponents.size(); i++)
            (focusComponent).keyTyped(new GraphKeyEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
        */
        if (focusComponent != null) 
        	focusComponent.keyTyped(new GraphKeyEvent(focusComponent, e.getID(), e.getWhen(), e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
        repaint();
        //requestFocus();
    } 
    
    protected void moveFocusTo(VComponent vc) {
        ArrayList a = vg.getFocussedComponents();
        for (int i = 0; i < a.size(); i++) {
            ((VComponent)a.get(i)).setFocus(false);
        }
        if (vc != null) 
            vc.setFocus(true);
        focusComponent = vc;
    }

    protected void checkFocus() {
        ArrayList a = vg.getFocussedComponents();
        if (a.size() > 0) {
            if (a.get(0) != focusComponent) {
                moveFocusTo((VComponent)a.get(0));
            }
        } else {
            focusComponent = null;
        }
    }        


}