/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

package parlevink.parlegraph.layout;

import parlevink.parlegraph.view.*;

import java.util.logging.*;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * This type of layout will perform incremental layout operations on a VGraph.
 * <p>The exact type of these operations is defined by the LayoutIncrementer.
 * This LayoutIncrementer might for example perform steps in the DirectedForceLayout.
 * <p>
 * This layoutclass provides access to some general properties to influence the behaviour
 * of the incremental layout: waiting time between steps; whether the layout should be
 * finished before updating the VGraph or whether the layout increments should be continuous, 
 * etc.
 * als er iets fout gaat in de incrementer, doet de layout het daarna nog wel weer?
 
 nog toevoegen: een 'initial layout algorithm' dat bij FULLlayout nog even uitgevoerd wordt
 Eelco: "hiervoor kun je bv project.pals.navigation.graphutil.VSemiRootedTreeLayout gebruiken"
 
 setInitialLayout(VGraphLayout vgl)
 */
public class VIncrementalLayout extends VGraphLayout {

    protected LayoutIncrementer incrementer;
    protected Thread workingThread;

    protected int sleep = 0;
    protected int maxSteps = 500;
    protected boolean showSteps = true;
    
    /**
     * incrementer must not be null
     */
    public VIncrementalLayout(VGraph vg, LayoutIncrementer newIncrementer) {
        super(vg);
        incrementer = newIncrementer;
    }

    /**
     * If already working: don't disturb
     */
    public void doLayout() {
	 if (showSteps) {
	        if ((workingThread == null) || (!workingThread.isAlive())) {
	            workingThread = new WorkingThread(this, incrementer);
	        }
	        if (workingThread.isAlive()) {
	            return;
	        }
	        workingThread.start();
	} else {
		// no need to start a new thread, a for-loop is sufficient
		incrementer.setFixed(fixedVComponents);
        	for (int count=0; count<maxSteps; count++) {   
        	   		incrementer.increment();
           	}
        }
	
    }

    /**
     * Determines the sleeping time between steps. If the sleeping time <= 0
     * then the VGraph will not be repainted until the layout is be finished.
     */
    public void setSleep(int i) {
        sleep = i;
        if ((sleep <=0) && (maxSteps <= 0))
            maxSteps = 100;
    }

    /**
     * Determines the maximum number of steps that the layout of the VGraph may 
     * be updated due to a call to doLayout or fullLayout.
     * <p>
     * If maxSteps <= 0 then the layout will keep on updating the VGraph.
     * <p>maxSteps and sleep may not BOTH be <= 0. In that case maxSteps is 
     * fixed at 100.
     */
    public void setMaxSteps(int i) {
        maxSteps = i;
        if ((sleep <=0) && (maxSteps <= 0))
            maxSteps = 100;
    }
    
    /**
     * By default all steps of the incremental layout are shown;
     * this is aesthetically pleasing, but slows the process down.
     * By setting showSteps to false you can get faster results
     */
     public void setShowSteps(boolean showSteps) {
     	this.showSteps = showSteps;
      }
   

}

class WorkingThread extends Thread {
    LayoutIncrementer incrementer;
    VIncrementalLayout parent;
    int count;
    
    public WorkingThread (VIncrementalLayout p, LayoutIncrementer inc) {
        incrementer = inc;
        parent = p;
    }
        
    public void run() {
        incrementer.setFixed(parent.fixedVComponents);
        count = 0;
        while (true) {
            incrementer.increment();
            if (parent.sleep > 0) {
                //@@@force a repaint here...
                try {
                    sleep(parent.sleep);
                } catch (InterruptedException e) {
                }
            }
            //check max nr of steps
            if (parent.maxSteps > 0) {
                count++;
                if (count > parent.maxSteps) {
                    break;
                }
            }
        }          
    }
}
 