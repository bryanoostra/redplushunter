/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:18 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: ResizableVComponent.java,v $
// Revision 1.1  2006/05/24 09:00:18  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:16  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.4  2003/07/16 16:47:23  dennisr
// *** empty log message ***
//
// Revision 1.3  2003/01/03 16:04:12  dennisr
// - Switch to java 1.4 (for logging facilities)
// - Improvements in visualizations of edges
// - VGraphPanel no longer switches to the foreground
// - VVertex will stay within bounds of parent VGraph
// - Some more javadoc comments
// - Decreased amount of code
//
// Revision 1.2  2002/09/23 07:44:43  dennisr
// Documentatie,
// zoom op VGraphPanel
// Scrollbars werken weer
// meer excepties bij verkeerde parameters
//
// Revision 1.1  2002/09/16 14:08:49  dennisr
// first add

package parlevink.parlegraph.view;

import parlevink.parlegraph.model.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/**
 * This subinterface of VComponent supports visual and mouse operations
 * to resize and move VComponents.
 * <br>
 * Any subclass of ResizableVComponent can activate the resizing behaviour 
 * by setting the property 'resizeMode' to the right values.
 * <br>
 * In some resizeModes, a VComponent will be visualised with resize handles, as long
 * as the paintbackGround of the subclass does a call to the superclass paint methods.
 * <br>A component in resizeMode will react to mouse operations with resizing behaviour, as long
 * as the mouse operations of the subclass do call the event handlers of this interface.
 * <br>
 * The actual resizing is done using the methods resize2D(mode, neww, newh) and 
 * resize2D(mode, neww, newh, fixedpoint). 
 * <br>
 * The following resizing modes are possible (or'ed together to get the complete resizemode):
 * <ul>
 * <li>RM_ALLOW_RESIZE    //This one must be on if there is to be any resizing at all. If resizing is not allowed, none of the other settings have any effect and the resizing mouse events will be ignored.
 * <li>RM_MIDDLE_HANDLES  //Show the middle resizing handles
 * <li>RM_CORNER_HANDLES  //Show the corner resizing handles
 * <li>RM_LOCK_RATIO      //If on, the ratio width/height can not be changed through mouse resizing events
 * <li>RM_FOCUS_ENTAILS_RESIZING //If on, a VComponent that gets focus will automatically also go into resize mode.
 * <li>RM_X_LOCK          //If on, the width of a VComponent cannot be changed through interactive resizing.
 * <li>RM_Y_LOCK          //If on, the height of a VComponent cannot be changed through interactive resizing.
 * <li>RM_RESIZING        //this is the actual resize mode: if on (and resizing is allowed), the resize decoration is drawn and the VComponent will react to resize mouse operations
 * </ul>   
 * Any combination of resize modes is allowed.
 * The modes do only pertain to the interactive resizing through mouse operations
 * (dragging etcetera).
 * <b>NB: The method resize2D is not affected by these constants.</b>
 * <br>
 * The moving behaviour: this interface also defines the moving behaviour of components, in reaction to mouse events.
 * Any subclass of ResizableVComponent can activate the moving behaviour 
 * by setting the property 'moveMode' to the correct value. VComponents in moveMode 
 * have no special visualisation.
 * When the component is in moveMode, it can be dragged to a new position using the mouse.
 * The subclass is responsible for setting the component in moveMode in reaction to
 * things like focus changes.
 * <br>
 * The following resizing modes are possible (or'ed together to get the complete resizemode):
 * <ul>
 * <li>MM_ALLOW_MOVE    //This one must be on if there is to be any moving at all. If moving is not allowed, none of the other settings have any effect and the moving mouse events will be ignored.
 * <li>MM_FOCUS_ENTAILS_MOVING //If on, a VComponent that gets focus will automatically also go into move mode.
 * <li>MM_MOVING //this is the actual move mode: if on (and moveing is allowed), the VComponent will react to move mouse operations
 * <li>MM_X_LOCK          //If on, the x location of a VComponent cannot be changed through interactive moving.
 * <li>MM_Y_LOCK          //If on, the y location of a VComponent cannot be changed through interactive moving.
 * </ul>   
 * Any combination of move modes is allowed.
 * The modes do only pertain to the interactive moving through mouse operations
 * (dragging etcetera) and not to the VComponents methods affecting the location of a VComponent.
 * <br>
 * *** move restriction modes!!!
 * <br>
 */
public interface ResizableVComponent extends VComponent {

    public static final int RM_NO_RESIZE = 0;
    public static final int RM_ALLOW_RESIZE = 1;    //IF NOT THIS ONE, THEN ALL OTHERS HAVE NO EFFECT
    public static final int RM_MIDDLE_HANDLES = 2;
    public static final int RM_CORNER_HANDLES = 4;
    public static final int RM_LOCK_RATIO = 8;
    public static final int RM_FOCUS_ENTAILS_RESIZING = 16;
    public static final int RM_X_LOCK = 32;
    public static final int RM_Y_LOCK = 64;
    public static final int RM_RESIZING = 128; 
    //detail: een paar combinaties zijn  zinloos, zoals xlock/lockratio; xlock/ylock/allow; allow/nocorner/nomiddle
    //werk dat uit... in docs

    /**
     * sets resizing mode (see RM constants)
     */
    public void setResizeMode(int resize);
    
    /**
     * Returns resizing mode (see RM constants)
     */
    public int getResizeMode();

    public static final int NOT_IN_USE_1       = 1;    
    public static final int MR_CONSTRAIN_CHILD = 2;
    public static final int MR_RESIZE_PARENT   = 3;
    
    /**
     * sets move restrictions (see MR constants)
     */
    public void setMoveRestrictions(int mr);
    
    /**
     * Returns move restrictions (see MR constants)
     */
    public int getMoveRestrictions();

    public static final int MM_ALLOW_MOVE = 1;    //IF NOT THIS ONE, THEN ALL OTHERS HAVE NO EFFECT
    public static final int MM_FOCUS_ENTAILS_MOVING = 2;
    public static final int MM_MOVING = 4; //ONLY WHEN THIS ONE AND THE ALLOW, THEN DRAGGIN == MOVEING
    public static final int MM_X_LOCK = 8;
    public static final int MM_Y_LOCK = 16;
    
    /**
     * sets moving mode (see MM constants)
     */
    public void setMoveMode(int move);
    
    /**
     * Returns moving mode (see MM constants)
     */
    public int getMoveMode();

}