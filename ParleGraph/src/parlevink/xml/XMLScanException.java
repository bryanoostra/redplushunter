/* @author Alexander Broersen
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLScanException.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.4  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//
 
package parlevink.xml;

import java.lang.RuntimeException;
import java.io.*;
import java.net.*;

/*
 * XMLScanException is a RuntimeException that is thrown when 
 * a XML input that is not lexically well-formed is scanned, or
 * if an unexpected End-Of-Data is reached.
 */
public class XMLScanException extends java.lang.RuntimeException {
  
  /**
   * default constructor for XMLScanExceptions.
   */
   public XMLScanException() {
      super();
   }

   /**
    * XMLScanExceptions with a String text attribute.
    */  
   public XMLScanException(String s) {
      super(s);
   }
  
   /**
    * produces the error message
    */
   public String toString() {
      return "XMLScanException: " + getMessage();  
   }

   private static final long serialVersionUID = 0L;
  
}