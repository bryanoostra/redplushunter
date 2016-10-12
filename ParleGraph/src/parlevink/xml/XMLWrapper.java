/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLWrapper.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2005/10/20 09:55:08  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.1  2005/09/18 21:47:21  zwiers
// no message
//

package parlevink.xml;

import java.io.*;

/*
 * XMLWrapper is an extension of XMLStructure; it adds the unwrap method.
 * XMLWrapper is intended for XMLStructures that are really wrappers for 
 * other Java classes, that are not XMLStructures themselves. 
 * The reason for this could be that some class already inherits from some 
 * non-XMLStructure class, or that it is existing code, or a standrad java class
 * like String etc.

 * public Object unwrap();
 * public Class wrappedClass();
  */

public interface XMLWrapper<T> extends XMLStructure
{
   
  
  /**
   * Many XMLStructure Objects are really "wrappers" for other Objects.
   * Considering an XMLStructure as a "wrapper" for some Object,
   * the unwrap method yields this original Object.
   */
  public T unwrap();
  
  /**
   * returns the Class of the wrapped Object.
   * For XMLStructures that are not considered "wrappers", 
   * the result equals getClass();
   */ 
  //public Class wrappedClass();

}
