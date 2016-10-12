/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */
 
package parlevink.parlegraph.control;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import parlevink.parlegraph.utils.*;
import parlevink.xml.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Default graph controller.
 * <p> Please refer also to documentation of CGraphController.
 * <p>
 * Default global actions:
 * (??load, save, new, clear, add textbox?, en evt dingen om modes te veranderen als nodig, layout, misschien zelfs setLayoutType, etc. een change properties action? dan zit je al diep in bean achtige zaken. kun je dingen als type vertex enzo zetten. tja.)
 * <ol>
 * <li>Load
 * <li>Save
 * <li>Clear
 * <li>Add textbox
 dfsafsdagsa@@@@@
 * </ol>
 * Default actions on specific visual components:
 * <ol>
 * <li>
 * <li>
 * <li>
 * </ol>
 * <p>
 * Documentation to be extended. Actions possibly too.
 */
public class CDefaultGraphController extends CAbstractGraphController {

    protected String newMVertexClass;
    protected String newMEdgeClass;

/**********************
 * Attributes section *
 **********************/
    
    /**
     * The edge that is being dragged around
     */
    protected VEdge theEdge;    
    protected int dragCoord;
    public static int DRAG_SOURCE = 1;
    public static int DRAG_TARGET = 2;

    protected String lastLabel = "";

    protected File lastFile; //needed to store the last used filename to facilitate fast saving
    
    protected JWindow tooltipWindow;
    
/**************************
 * Initialization section *
 * (constructors, setting *
 * new VGraphs, etc)      *
 **************************/

    /**
     * Default constructor.
     */
    public CDefaultGraphController() {
        super();
        setMVertexClass("parlevink.parlegraph.model.MLabeledVertex");
        setMEdgeClass("parlevink.parlegraph.model.MLabeledEdge");
        theEdge = null;
    }

    /**
     * Initialises the CDefaultGraphController with a VGraph after constructing it.
     */
    public CDefaultGraphController(VGraph newVGraph) {
        this();
        setVGraph(newVGraph);
    }
    
    /**
     * Breaks all connections to the current VGraph and its MComponents,
     * essentially disabling all editing capacities.
     * Only the link to the VGraph itself is preserved, making it possible
     * to re-enable editing by calling register().
     */
    protected void unregister() {
        super.unregister();
        //set theEdge to null
        theEdge = null;
    }
    
    /**
     * Connects to the current VGraph and its MComponents,
     * enabling editing capacities. 
     * All relevant eventDelegate properties are set to this controller;
     * mcomponentListener connections are made to all (sub) MGraphs
     * to keep track of structural changes, 
     */
    protected void register() {
        super.register();
        //set theEdge to null
        theEdge = null;
    }

/**************************
 * Event routing section  *
 **************************/

    protected void moveFocusTo(VComponent vc) {
        ArrayList a = theVGraph.getFocussedComponents();
        for (int i = 0; i < a.size(); i++) {
            ((VComponent)a.get(i)).setFocus(false);
        }
        if (vc != null)
            vc.setFocus(true);
    }

    /**
     */
    public void mouseClicked(VComponent ref, GraphMouseEvent e) {
        //if you were currently draggin: don't do anything, system will react on mousereleased....
        if (theEdge != null) 
            snapEdge();

        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) { //left
            if (e.getClickCount() == 2) { //double left
                if (ref instanceof VGraph) { //double left on VGraph
                    VVertex newVv = addMVertex((VGraph)ref, (double)e.getX(), (double)e.getY());
                    moveFocusTo(newVv);
                    return;
                } 
                if (ref instanceof VTextBox) { //only for textboxes & other nongraph elements, because they have no mcomponent to implement textinterface
                    String newText = (String)JOptionPane.showInputDialog(null, "Enter the new Text:", "New Text", JOptionPane.QUESTION_MESSAGE, null, null, ((VTextBox)ref).getText() );
                    if (newText != null) {
                        ((VTextBox)ref).setText(newText);
                    }
                    return;
                }          
                if (ref.getMComponent() instanceof TextInterface) { //double left on edge or vertex implementing textinterface
                    changeLabel(ref);
                    return;
                }                    
                    
            }
        }
        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) { //right click
            if ((ref instanceof VMultiLineEdge)) { //right on multilineedge
                ((VMultiLineEdge)ref).addWaypoint(e.getPoint());
                return;
            }      
        }
        super.mouseClicked(ref, e); //all other cases: super behaviour (bounce)
    }

    /**
     */
    public void mousePressed(VComponent ref, GraphMouseEvent e) {
        //if you were currently draggin: do nothing. 
        if (theEdge != null) 
            snapEdge();
        if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) { //right click
            if ((ref instanceof VMyTextVertex)) { //right on text vertex
            	if (tooltipWindow != null) {
            		tooltipWindow.dispose();
            	}

                FontMetrics fontMet;
                Rectangle2D oneLineBounds;        
            	
            	tooltipWindow = new JWindow();
                tooltipWindow.setLocation((int)(e.getX()), (int)(e.getY()));
            	
        	    Graphics g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1,1));
                double textWidth = 0;
        	    fontMet = g.getFontMetrics();
        	    float ascent = fontMet.getAscent();

            	
                // Add component to the window
            	JTextArea label = new JTextArea();
            	Color bgCol = new Color(255, 255, 220);
            	label.setBackground(bgCol);
            	tooltipWindow.setBackground(bgCol);
            	
            	ArrayList<String> info = ((VMyTextVertex)ref).getAnnotations();
            	StringBuilder sb = new StringBuilder();
            	for (String s: info) {
            		sb.append(s).append('\n');
        	        oneLineBounds = fontMet.getStringBounds(s, g);
        	        textWidth = Math.max(textWidth, oneLineBounds.getWidth()+12);
           		
            	}
            	label.setText(sb.toString());
                tooltipWindow.getContentPane().add(label, BorderLayout.CENTER);
                
        	    //deze g spoort NIETmet de g waar straks op getekend wordt!!!
        	        
        	    double textHeight = 12 + ascent * (info.size());                
                tooltipWindow.setSize((int)Math.ceil(textWidth),(int)Math.ceil(textHeight));
                
                //tooltipWindow.setLocationRelativeTo(theVGraph.get);
                
                // Show the window
                
                tooltipWindow.setVisible(true);
                //return;
            }      
            if (   (ref instanceof VVertex) 
                && (ref != theVGraph)) { //right mouse press on VVertex, not top graph
                theEdge = (VEdge)addMEdge(ref.getVGraph());//die getVGraph moet verdwijnen.
                setEdgeSourceAt((VVertex)ref);
                dragCoord = DRAG_TARGET;
                theEdge.setTargetCoord(new Point2D.Double((double)e.getX(), (double)e.getY()));
                return;
            }
        }                
            
        super.mousePressed(ref, e);
    }
    
    /**
     */
    public void mouseReleased(VComponent ref, GraphMouseEvent e) {
    	if (tooltipWindow != null) {
    		tooltipWindow.setVisible(false);
    		tooltipWindow.dispose();
    	}    	
        if (theEdge != null) {  //if dragging endpoints
            if (dragCoord == DRAG_SOURCE) {  //when mouse release during dragging of the source
                setEdgeSourceAt(e.getX(), e.getY());
//                moveFocusTo(theEdge);
                theEdge = null;
                return;
            }
            if (dragCoord == DRAG_TARGET) {  //when mouse release during dragging of the target
                
                setEdgeTargetAt(e.getX(), e.getY());
//                moveFocusTo(theEdge);
                theEdge = null;
                return;
            }
        }
        super.mouseReleased(ref, e);
    }

    /**
     * if dragging an edge: do update of the right coords
     */
    public void mouseDragged(VComponent ref, GraphMouseEvent e) {
        if (theEdge != null) {  //if dragging endpoints
            if (dragCoord == DRAG_SOURCE) {  //when dragging of the source
                theEdge.setSourceCoord(new Point2D.Double((double)e.getX(), (double)e.getY()));
                return;
            }
            if (dragCoord == DRAG_TARGET) {  //when dragging of the target
                theEdge.setTargetCoord(new Point2D.Double((double)e.getX(), (double)e.getY()));
                return;
            }
        }
        if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) { //left button, not yet dragging
            if (ref instanceof VLineEdge) { //left on edge
                VComponent eventSrc = (VComponent)e.getSource();
                if (eventSrc == ((VLineEdge)ref).getSourceArrowHead()) { //left on source arrowhead of line
                    theEdge = (VLineEdge)ref;
                    dragCoord = DRAG_SOURCE;
                    return;
                } else if (eventSrc == ((VLineEdge)ref).getTargetArrowHead()) {//left on target arrowhead of line
                    theEdge = (VLineEdge)ref;
                    dragCoord = DRAG_TARGET;
                    return;
                } 
            }
        }        
        super.mouseDragged(ref, e);
    }

    /**
     * default bounce event
     */
    public void keyPressed(VComponent ref, GraphKeyEvent e) {
        if (e.getKeyCode() == GraphKeyEvent.VK_DELETE) {
            if (ref instanceof VVertex) {
                //delete corresponding MVertex
                MVertex mv = (MVertex)ref.getMComponent();
                if (mv.getMGraph() != null) {
                    MGraph mg = mv.getMGraph();
                    mg.removeMVertex(mv);
                    theVGraph.getMComponent().notifyMComponentListeners(); //not mg, since some edges may have disappeared which are in other (higher) graphs
                    return;
                }
            } else if (ref instanceof VEdge) {
                if ((e.getSource() == ref) || (e.getSource() instanceof VTextBox)) { //only if delete directly on edge, or on textbox...
                    //delete corresponding MEdge
                    MEdge me = (MEdge)ref.getMComponent();
                    if (me.getMGraph() != null) {
                        MGraph mg = me.getMGraph();
                        mg.removeMEdge(me);
                        mg.notifyMComponentListeners();
                        return;
                    }
                }
            } 
            if (ref instanceof VMultiLineEdge) {
                if ( ((VMultiLineEdge)ref).getWaypoints().contains(e.getSource())) {
                    //delete corresponding waypoint
                    ((VMultiLineEdge)ref).removeWaypoint(((VMultiLineEdge)ref).getWaypoints().indexOf(e.getSource()));
                    return;
                }
            } 
            if (ref instanceof VTextBox) {
                theVGraph.removeVComponent(ref);
                return;
            }
        } 
        super.keyPressed(ref, e);
    }

    /**
     * default bounce event
     */
    public void keyReleased(VComponent ref, GraphKeyEvent e) {
        if (theEdge != null) {
            if ((e.getKeyChar() & GraphKeyEvent.VK_CONTROL) != 0) {
                if (theEdge instanceof VMultiLineEdge) {
                    if (dragCoord == DRAG_SOURCE) {
                        ((VMultiLineEdge)theEdge).addWaypoint(0, theEdge.getSourceCoord());
                    } else {
                        ((VMultiLineEdge)theEdge).addWaypoint(theEdge.getTargetCoord());
                    }
                }
            }
        }
        super.keyReleased(ref, e);
    }

    /**
     * default bounce event
     */
    public void keyTyped(VComponent ref, GraphKeyEvent e) {
        if ((e.getKeyChar() == 'r') || (e.getKeyChar() == 'R')) {
            if (ref instanceof VEdge) {
                MEdge me = (MEdge)ref.getMComponent();
                me.reverseDirection();
                me.notifyMComponentListeners();
                return;
            }
            if (ref instanceof VVertex) {
                ref.setColor(Color.red);
                return;
            }
        }        
        if ((e.getKeyChar() == 'b') || (e.getKeyChar() == 'B')) {
                ref.setColor(Color.blue);
                return;
        }        
        if ((e.getKeyChar() == 'z') || (e.getKeyChar() == 'Z')) {
                ref.setColor(Color.black);
                return;
        }        
        if ((e.getKeyChar() == 'g') || (e.getKeyChar() == 'G')) {
                ref.setColor(Color.green);
                return;
        }        
/*        if ((e.getKeyChar() == 'x') || (e.getKeyChar() == 'X')) {
            if (ref instanceof VVertex) {
                ((VPartialGraphView)theVGraph).explodeNode((MVertex)ref.getMComponent());
                unregister();
                register();
                theVGraph.doLayout();
                return;
            }
        }        */
        super.keyTyped(ref, e);
    }
    

/***********************************
 * Internal editing functionality  *
 ***********************************/

    protected void changeLabel(VComponent ref) {
        String newText = (String)JOptionPane.showInputDialog(null, "Enter the new Text:", "New Text", JOptionPane.QUESTION_MESSAGE, null, null, ((TextInterface)ref.getMComponent()).getText() );
        if (newText != null) {
            ((TextInterface)ref.getMComponent()).setText(newText);
            ref.getMComponent().notifyMComponentListeners();
            if (ref instanceof VEdge)
                lastLabel = newText;
        }
    }        

    /**
     * Adds an MVertex to the (sub) MGraph , adds a corresponding 
     * VVertex to the corresponding VGraph and sets its bounding box properties.
     * Returns the new viewer.
     */
    protected VVertex addMVertex(VGraph parent, double x, double y) {
        VVertex vv = null;
        MGraph mg = (MGraph)parent.getMComponent();
        try {
            MVertex mv = (MVertex)Class.forName(newMVertexClass).newInstance();
            mg.addMVertex(mv);
            if (mv instanceof TextInterface) 
                ((TextInterface)mv).setText(mv.getID());
            vv = (VVertex)Class.forName(mv.getViewerClass()).newInstance();
            //initialise VGraph property of viewer
            vv.setVGraph(parent);
            //connect viewer to MComponent 
            vv.setMComponent(mv);            
            //add viewer to container of this VGraphAdapter
            parent.addVComponent(vv);
            parent.getMComponent().notifyMComponentListeners();
            //resize viewer
            vv.setLocation2D(x,y);
        } catch (ClassCastException e) {
            System.out.println("error: "+e);
        } catch (InstantiationException e) {
            System.out.println("error: "+e);
        } catch (IllegalAccessException e) {
            System.out.println("error: "+e);
        } catch (ClassNotFoundException e) {
            System.out.println("error: "+e);
        }                
        return vv;
    } 

    /**
     * Adds an MEdge  and adds a corresponding 
     * VEdge to the corresponding VGraph.
     vg not null
     */
    protected VEdge addMEdge(VGraph vg) {
        VEdge ve = null;
        MGraph mg = (MGraph)vg.getMComponent();
        try {
            MEdge me = (MEdge)Class.forName(newMEdgeClass).newInstance();
            if (me instanceof TextInterface) {
                ((TextInterface)me).setText(lastLabel);
            }
            mg.addMEdge(me);
            me.setDirected(true);
            ve = (VEdge)Class.forName(me.getViewerClass()).newInstance();
            //initialise VGraph property of viewer
            ve.setVGraph(vg);
            //connect viewer to MComponent 
            ve.setMComponent(me);            
            //add viewer to container of this VGraphAdapter
            vg.addVComponent(ve);
            vg.getMComponent().notifyMComponentListeners();
        } catch (ClassCastException e) {
            System.out.println("error: "+e + " : " + e.getMessage());
        } catch (InstantiationException e) {
            System.out.println("error: "+e + " : " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println("error: "+e + " : " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("error: "+e + " : " + e.getMessage());
        }                
        return ve;
    } 

    /**
     * Finds the VVertex at (x,y) and connects the current edited VEdge to that VVertex
     * through modifying the model. If the only VVertex at (x,y) is the topVGraph, then snap the edge...
     */
    protected void setEdgeSourceAt(double x, double y) {
        //See whether a vertex is at(p)
        Point2D p = new Point2D.Double(x, y);
        ArrayList al = theVGraph.findAllVComponentsAt(p);
        VVertex newSource = null;
        for (int i = al.size()-1; i >= 0; i--) {
            if (al.get(i) instanceof VVertex) {
                newSource = (VVertex)al.get(i);
            }
        }
        setEdgeSourceAt(newSource);
    } 

    /**
     * Finds the VVertex at (x,y) and connects the current edited VEdge to that VVertex
     * through modifying the model. If the only VVertex at (x,y) is the topVGraph, then snap the edge...
     */
    protected void setEdgeSourceAt(VVertex newSource) {
        if (newSource != null) {
            if (newSource == theVGraph) {
                snapEdge();
            } else {
                //set new endpoint
                ((MEdge)theEdge.getMComponent()).setSource((MVertex)newSource.getMComponent());
                ((MEdge)theEdge.getMComponent()).notifyMComponentListeners();
                checkLoop();  //idf it is a loop: make sure that there is a waypoint to show the loop...
                theEdge.vcomponentMoved(new GraphEvent(this)); //reset endpoint
            }
        }
    } 

    /**
     * Finds the VVertex at (x,y) and connects the current edited VEdge to that VVertex
     * through modifying the model. If the only VVertex at (x,y) is the topVGraph, then snap the edge...
     */
    protected void setEdgeTargetAt(double x, double y) {
        //See whether a vertex is at(p)
        Point2D p = new Point2D.Double(x, y);
        ArrayList al = theVGraph.findAllVComponentsAt(p);
        VVertex newTarget = null;
        for (int i = al.size()-1; i >= 0; i--) {
            if (al.get(i) instanceof VVertex) {
                newTarget = (VVertex)al.get(i);
            }
        }
        setEdgeTargetAt(newTarget);
    } 

    /**
     * Finds the VVertex at (x,y) and connects the current edited VEdge to that VVertex
     * through modifying the model. If the only VVertex at (x,y) is the topVGraph, then snap the edge...
     */
    protected void setEdgeTargetAt(VVertex  newTarget) {
        if ((newTarget == null) || (newTarget == theVGraph)) {
            snapEdge();
        } else {
            //set new endpoint
                ((MEdge)theEdge.getMComponent()).setTarget((MVertex)newTarget.getMComponent());
            ((MEdge)theEdge.getMComponent()).notifyMComponentListeners();
            checkLoop();  //idf it is a loop: make sure that there is a waypoint to show the loop...
            theEdge.vcomponentMoved(new GraphEvent(this)); //reset endpoint
        }
    } 

    /**
     * if theEdge is a loop, and it has no waypoints, a waypoint is added 
     */
    public void checkLoop() {
        MEdge me = (MEdge)theEdge.getMComponent();
        if ((me.hasTarget()) && (theEdge instanceof VMultiLineEdge) && (me.getTarget() == me.getSource())) {
            if (((VMultiLineEdge)theEdge).getWaypoints().size() == 0) {
                VComponent vc = theVGraph.getViewerForMComponent(me.getTarget());
                ((VMultiLineEdge)theEdge).addWaypoint(new Point2D.Double(vc.getX() + vc.getWidth()/2 + 12, vc.getY() - 8));
                ((VMultiLineEdge)theEdge).addWaypoint(new Point2D.Double(vc.getX() + vc.getWidth()/2, vc.getY() - 15));
                ((VMultiLineEdge)theEdge).addWaypoint(new Point2D.Double(vc.getX() + vc.getWidth()/2 - 12, vc.getY() - 8));
            }
        }
    }
   
    /**
     * resets the endpoints of the edge to their current VVertices. If the edge has no target, 
     * it is the edge that is currently being  added so it is removed (the 'adding an edge' process fails)
     */
    protected void snapEdge() {
        MEdge me = (MEdge)theEdge.getMComponent();
        if (!me.hasTarget()) {
            //delete corresponding MEdge
            MGraph mg = me.getMGraph();
            mg.removeMEdge(me);
            mg.notifyMComponentListeners();
        } else {
            theEdge.vcomponentMoved(new GraphEvent(this)); //reset endpoint
        }
        //finsih up with setting theEdge to null: it is no longer being dragged around
        theEdge = null;
    }
        
 
/**************************
 * External manipulation  *
 * of controller          *
 * (editing modes etc)    *
 **************************/
    
    public void setMVertexClass(String newmvc) {
        newMVertexClass = newmvc;
    }
    
    public void setMEdgeClass(String newmec) {
        newMEdgeClass = newmec;
    }

/**************************
 * Actions for controller *
 **************************/

    /** 
     */
    public ActionMapMap getGlobalActionMap() {
        ActionMapMap result = new ActionMapMap();
        ActionMap fileM = new ActionMap();
        fileM.put("clear", new ClearAction("clear", this));
        fileM.put("load", new LoadAction("load", this));
        fileM.put("save", new SaveAction("save", this));
        fileM.put("dumpEPS", new DumpEPSAction("dumpEPS", this));
        result.put("file", fileM);
        ActionMap layoutM = new ActionMap();
        layoutM.put("doLayout", new LayoutAction("doLayout", this));
        layoutM.put("fullLayout", new FullLayoutAction("fullLayout", this));
        result.put("layout", layoutM);
        ActionMap editM = new ActionMap();
        editM.put("addTextBox", new AddTextBoxAction("addTextBox", this));
        result.put("edit", editM);
        return result;
    }
        
}

/**************
Below you will find actions that are 'global', i.e. act on the graph as a whole.
These are returned in the ActionMap for inclusion in menus or toolbars.
***************/
       
class LayoutAction extends AbstractAction {
    CDefaultGraphController control;
    public LayoutAction(String name, CDefaultGraphController ctr) {
        super(name);
        control = ctr;
    }
    public void actionPerformed(ActionEvent e) {
        control.getVGraph().doLayout();
    }        
}

class FullLayoutAction extends AbstractAction {
    CDefaultGraphController control;
    public FullLayoutAction(String name, CDefaultGraphController ctr) {
        super(name);
        control = ctr;
    }
    public void actionPerformed(ActionEvent e) {
        control.getVGraph().fullLayout();
    }        
}

class AddTextBoxAction extends AbstractAction {
    CDefaultGraphController control;
    public AddTextBoxAction(String name, CDefaultGraphController ctr) {
        super(name);
        control = ctr;
    }
    public void actionPerformed(ActionEvent e) {
        VTextBox newVT = new VTextBox("new text box");
        //newVT.setPaintBorder(true);
        newVT.setPaintShape(true);
        newVT.setResizeMode(  ResizableVComponent.RM_ALLOW_RESIZE 
                            | ResizableVComponent.RM_FOCUS_ENTAILS_RESIZING 
                            | ResizableVComponent.RM_CORNER_HANDLES);
        control.getVGraph().addVComponent(newVT);
        newVT.setLocation2D(control.getVGraph().getCenter2D());
        control.connectEventDelegate(newVT); ///////VERY important
    }        
}

class LoadAction extends AbstractAction {
    CDefaultGraphController control;
    public LoadAction(String name, CDefaultGraphController ctr) {
        super(name);
        control = ctr;
    }
    public void actionPerformed(ActionEvent e) {
        //get filename
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(control.lastFile);
        Reader graphReader = null;
        try {
            int returnVal = chooser.showOpenDialog(null); 
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                control.lastFile = chooser.getSelectedFile();
                graphReader = new FileReader(chooser.getSelectedFile());
                MVGraphLoader.readIntoVGraph(new XMLTokenizer(graphReader), control.getVGraph());
                graphReader.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        control.setVGraph(control.getVGraph());
    }        
}

class SaveAction extends AbstractAction {
    CDefaultGraphController control;
    public SaveAction(String name, CDefaultGraphController ctr) {
        super(name);
        control = ctr;
    }
    public void actionPerformed(ActionEvent e) {
        //get filename
        JFileChooser chooser = new JFileChooser();
          chooser.setSelectedFile(control.lastFile);
        PrintWriter graphWriter = null;
        try {
            int returnVal = chooser.showSaveDialog(null); 
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                control.lastFile = chooser.getSelectedFile();
                graphWriter = new PrintWriter(new FileWriter(chooser.getSelectedFile()), true);
                graphWriter.print(MVGraphLoader.getXML(control.getVGraph(), true));
                graphWriter.flush();
                graphWriter.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
class ClearAction extends AbstractAction {
    CDefaultGraphController control;
    public ClearAction(String name, CDefaultGraphController ctr) {
        super(name);
        control = ctr;
    }
    public void actionPerformed(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(null, "Are you sure that you want to clear the Graph? You cannot undo this operation.", "Clear all contents", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            MGraph mg = (MGraph)control.getVGraph().getMComponent();
            mg.clearMGraph();
            mg.notifyMComponentListeners();
        }
    }
}


class DumpEPSAction extends AbstractAction {
    CDefaultGraphController control;
    public DumpEPSAction(String name, CDefaultGraphController ctr) {
        super(name);
        control = ctr;
    }
    public void actionPerformed(ActionEvent e) {
        //get filename
        JFileChooser chooser = new JFileChooser();
        File f = new File ("untitled.eps");
        if (control.lastFile != null) {
            f = new File(control.lastFile.getName() + ".eps");
        }
        chooser.setSelectedFile(f);
        int returnVal = chooser.showSaveDialog(null); 
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            GraphExporter.exportEPS(chooser.getSelectedFile(), control.getVGraph());
        }
    }
}
