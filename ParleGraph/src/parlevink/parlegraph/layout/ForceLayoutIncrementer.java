/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:20 $    
 * @since version 0       
 */

package parlevink.parlegraph.layout;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import java.util.logging.*;
import java.lang.System;

import java.util.*;
import java.io.*;
import java.net.URL;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * code adapted from Salvo's OpenJGraph packages. Longer credits will be added later.
 */
public class ForceLayoutIncrementer implements LayoutIncrementer {

    /** 
     * props van maken!
     */
    private final String FILENAME = "forcelayout.props";
    private double      springLength        = 50; // 50
    private double      stiffness           = 30; // 300
    private double      electricalRepulsion = 600; // 300
    private double      increment           = 0.5; // 0.50

    VGraph theVGraph;
    Set fixed;
    Map VVertexMapper;
    Logger logger;

    public ForceLayoutIncrementer(VGraph vg) {
        try
        {
            /*System.out.println("1");
            Properties props = new Properties();
            System.out.println("2");
		    URL location = getClass().getResource(FILENAME);
		    System.out.println("3");
		    InputStream in = location.openStream();
		    System.out.println("4");
		    props.load(in);
		    System.out.println("5");
		    springLength = new Double(props.getProperty("springLength")).doubleValue();
		    System.out.println("6");
		    stiffness = new Double(props.getProperty("stiffness")).doubleValue();
		    System.out.println("7");
		    electricalRepulsion = new Double(props.getProperty("repulsion")).doubleValue();
		    System.out.println("8");
		    increment = new Double(props.getProperty("increment")).doubleValue();
		    System.out.println("9");*/
        } catch (Exception e) {
            System.out.println("Forcelayout can't load propertyfile");
            // too bad
        }
        theVGraph = vg;
        VVertexMapper = new HashMap();
        /*
                Iterator it = theVGraph.getChildren();
        // create a hashmap that maps ID to VVertex
        while (it.hasNext()) {
        	VComponent vc = (VComponent)it.next();
        	if (vc instanceof VVertex) {
        		VVertexMapper.put(((VVertex) vc).getMComponent().getID(),vc);
        	}
        }
        */
        if (logger == null) {
	    logger = Logger.getLogger(getClass().getName());
	}
    }

    public void setFixed(Set newFixed) {
        fixed = newFixed;
    }

    public void increment() {
        try {
           Iterator it = theVGraph.getChildren();
            while (it.hasNext()) {
                VComponent vc = (VComponent)it.next();
                if (vc instanceof VVertex) {
                    if (fixed.contains(vc)) 
                        continue;
                    relax( (VVertex) vc );
                }
            }
        } catch (ConcurrentModificationException ex) {
            //so. don't bother to solve; somebody is editing the graph so it's OK to skip one increment
        }
    }

    /**
     * "Relax" the force on the VisualVertex. "Relax" here means to
     * get closer to the equilibrium position.
     * <p>
     * This method will:
     * <ul>
     * <li>Find the spring force between all of its adjacent VisualVertices</li>
     * <li>Get the electrical repulsion between all VisualVertices, including
     * those which are not adjacent.</li>
     * </ul>
     */
    private void relax( VVertex vVertex ) {
            double  xForce = 0;
            double  yForce = 0;
    
            double  distance;
            double  spring;
            double  repulsion;
            double  xSpring = 0;
            double  ySpring = 0;
            double  xRepulsion = 0;
            double  yRepulsion = 0;
    
            double  adjacentDistance = 0;
    
            double  adjX;
            double  adjY;
            double  thisX = vVertex.getCenter2D().getX();
            double  thisY = vVertex.getCenter2D().getY();
    
            List       adjacentVertices;
            VVertex    adjacentVisualVertex;
            int        i, size;
            

            // Get the spring force between all of its adjacent vertices.
            Iterator adjacentVerticesIt = ((MVertex)vVertex.getMComponent()).getIncidentMEdges();

            while (adjacentVerticesIt.hasNext()) {
                MEdge med = (MEdge) adjacentVerticesIt.next();
                MVertex mv = med.getSource();
                if (mv == vVertex.getMComponent())
                    mv = med.getTarget();
                if (mv == null)
                    continue;
                   // adjacentVisualVertex = (VVertex) VVertexMapper.get(mv.getID());
                adjacentVisualVertex = (VVertex)theVGraph.getViewerForMComponent( mv ); //@@@ouch....
                if( adjacentVisualVertex == vVertex )
                    continue;
    
                if( adjacentVisualVertex == null )
                    continue; //don't try to fix this problem
                    
                adjX = adjacentVisualVertex.getCenter2D().getX();
                adjY = adjacentVisualVertex.getCenter2D().getY();
    
                distance = Point2D.distance( adjX, adjY, thisX, thisY );
                if( distance == 0 )
                    distance = .0001;
    
                //spring = this.stiffness * ( distance - this.springLength ) *
                //    (( thisX - adjX ) / ( distance ));
                spring = this.stiffness * Math.log( distance / this.springLength ) *
                    (( thisX - adjX ) / ( distance ));
    
                xSpring += spring;
    
                //spring = this.stiffness * ( distance - this.springLength ) *
                //    (( thisY - adjY ) / ( distance ));
                spring = this.stiffness * Math.log( distance / this.springLength ) *
                    (( thisY - adjY ) / ( distance ));
    
                ySpring += spring;
    
            }
    

            // Get the electrical repulsion between all vertices,
            // including those that are not adjacent.
            Iterator it2 = theVGraph.getChildren();
            while (it2.hasNext()) {
                VComponent vc = (VComponent)it2.next();
                if (vc instanceof VVertex) {
                    VVertex aVisualVertex = (VVertex)vc;
                    if( aVisualVertex == vVertex )
                        continue;

                    adjX = aVisualVertex.getCenter2D().getX();
                    adjY = aVisualVertex.getCenter2D().getY();

                    distance = Point2D.distance( adjX, adjY, thisX, thisY );
                    if( distance == 0 )
                        distance = .0001;

                    repulsion = ( this.electricalRepulsion / (distance) ) *
                        (( thisX - adjX ) / ( distance));

                    xRepulsion += repulsion;

                    repulsion = ( this.electricalRepulsion / (distance*distance) ) *
                        (( thisY - adjY ) / ( distance ));

                    yRepulsion += repulsion;
                }
            }
            
            // Repulsion of the borders
            
            adjX = theVGraph.getX();
            distance  = Math.abs(thisX - adjX);
            if (distance == 0)
                distance = .0001;
            xRepulsion += (this.electricalRepulsion / (distance)) *
                (( thisX-adjX) / (distance));
            adjX = theVGraph.getX() + theVGraph.getWidth();
            distance = Math.abs(adjX - thisX);
            if (distance == 0)
                distance = .0001;
            xRepulsion += (this.electricalRepulsion / (distance)) *
                (( thisX-adjX) / (distance));            
            adjY = theVGraph.getY();
            distance = Math.abs(thisY - adjY);
            if (distance == 0);
                distance = .0001;
            yRepulsion += (this.electricalRepulsion / (distance)) *
                (( thisY - adjY) / (distance));
            adjY = theVGraph.getY()+theVGraph.getHeight();
            distance = Math.abs(adjY - thisY);
            if (distance == 0)
                distance = .0001;
             yRepulsion += (this.electricalRepulsion / (distance)) *
                ((thisY - adjY) / (distance));
                
                
            // Combine the two to produce the total force exerted on the vertex.
            xForce = xSpring - xRepulsion;
            yForce = ySpring - yRepulsion;

            // Move the vertex in the direction of "the force" --- thinking of star wars :-)
            // by a small proportion
            double xadj = 0 - ( xForce * this.increment );
            double yadj = 0 - ( yForce * this.increment );

            double newX = vVertex.getX() + xadj;
            double newY = vVertex.getY() + yadj;
            if (newX < theVGraph.getX())
                xadj = 0;
            if (newY < theVGraph.getY())
                yadj = 0;
            if (newX + vVertex.getWidth() > theVGraph.getWidth() + theVGraph.getX())
                xadj = 0;
            if (newY + vVertex.getHeight() > theVGraph.getHeight() + theVGraph.getY())
                yadj = 0;

            vVertex.translate2D(VComponent.RECURSIVE, xadj, yadj );
    }
}