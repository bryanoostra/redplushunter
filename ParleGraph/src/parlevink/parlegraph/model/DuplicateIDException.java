/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:24 $    
 * @since version 0       
 */
 
// Last modification by: $Author: swartjes $
// $Log: DuplicateIDException.java,v $
// Revision 1.1  2006/05/24 09:00:24  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:02:52  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2003/06/23 09:46:09  dennisr
// *** empty log message ***
//
// Revision 1.1  2002/09/16 14:05:41  dennisr
// first add
//
// Revision 1.1  2002/01/15 15:06:42  reidsma
// no message
//
 
package parlevink.parlegraph.model;

import java.lang.RuntimeException;

/**
 * DuplicateIDException is a RuntimeException that is thrown when 
 * a MVertex or MEdge is added to a graph with an ID that is already in use.
 */
public class DuplicateIDException extends java.lang.RuntimeException {
  
  /**
   * default constructor for DuplicateIDException.
   */
  public DuplicateIDException() {
     super();
  }

  /**
   * DuplicateIDException with a String text attribute representing the duplicate ID which has been added.
   */  
  public DuplicateIDException(String s) {
     super("Duplicate ID used: " + s);
  }
}