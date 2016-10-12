/* @author Dennis Reidsma, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:28 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $

package parlevink.parlegraph.utils;

import parlevink.parlegraph.model.*;
import parlevink.parlegraph.view.*;
import parlevink.xml.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.image.*;

import org.jibble.epsgraphics.*;

import java.util.logging.*;

/** 
 * For export to XML or EPS formats.
better: one public method with extension as string param; calls approp meth, or throw 'unsupExtension'.
May also have 'getAllSupExtensions' (return list of string)

 */
public class GraphExporter {



    public static void exportEPS(File exportFile, VGraph vg) {
        try {
            PrintWriter epsWriter = null;
            epsWriter = new PrintWriter(new FileWriter(exportFile), true);
            EpsGraphics2D epsg = new EpsGraphics2D();
            vg.paintVComponent(epsg);
        
        	epsWriter.print(epsg.toString());
            epsg.dispose();
            epsWriter.flush();
            epsWriter.close();
        } catch (IOException ex) {
        	JOptionPane.showMessageDialog(null, "Error: could not save eps");
        }
    }        

}
