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
 * Docs to be added.
 It's your own responsibility to connect this incrementer to the graph it should worry about.
 both definition of appropriate constructor or method, and calling it.
 May change later
 */
public interface LayoutIncrementer {
    public void increment();
    public void setFixed(Set newFixed);
}