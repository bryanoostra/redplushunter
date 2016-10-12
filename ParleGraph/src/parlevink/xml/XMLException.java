/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLException.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.8  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:04  zwiers
// no message
//
// Revision 1.1  2005/06/11 11:36:40  zwiers


package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import java.util.*;

/**
 * XMLException is in essence a wrapper around  long, int, Long, or Integer values, 
 * that turns them  effectively into an XMLizable object.
 * The integer value is kept as a "long" value.
 */

public class XMLException extends XMLBasicValue<Exception> implements XMLWrapper<Exception>
{

   public Exception exception;
   private String exceptionClassName;
   private String message;

   /**
    * creates a new XMLException
    */
   public XMLException() {
   }

   /**
    * creates a new XMLException
    */
   public XMLException(Exception e) {
      exception = e;
   }


   /**
    * returns the normal int value as String
    */
   public String toString() {
      if (exception == null) return "NULL Exception";
      else {
         String eclass = exception.getClass().getName();
         String msg = exception.getMessage();
         return eclass + ": " + msg;
      }
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlException) {
      Exception xe = (Exception) ((XMLException) xmlException).exception;
      return (xe.getClass().equals(exception.getClass()) 
              && (xe.getMessage()).equals(exception.getMessage()));
   }

   /**
    * calculates the hash code 
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return exception.getMessage().hashCode();
    }

   /**
    * returns a String that can be used as XML attribute value.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      appendAttribute(buf, "exception", exception.getClass().getName());
      buf.append(' ');
      appendAttribute(buf, "message", exception.getMessage());
      return buf;
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {

          if (attrName.equals("exception")) {

              exceptionClassName = valCode;
            
              if (message != null) {create(exceptionClassName, message);}
          } else if (attrName.equals("message")) {
              message = valCode;
              if (exceptionClassName != null) {create(exceptionClassName, message);}
          } else {
            throw new RuntimeException("Unknown XML attribute for XMLException:" + attrName);
          }
   }
 
   private void create(String exceptionClassName, String msg) {
      try{
           Class exceptionClass = Class.forName(exceptionClassName);
           Class stringClass = Class.forName("java.lang.String");
           exception = (Exception) 
               (exceptionClass.getConstructor(new Class[] {stringClass}))
                .newInstance(new Object[] {message});
      } catch (ClassNotFoundException e) {
         throw new RuntimeException("Unknown XML Exception:" + exceptionClassName);
      } catch (Exception e) {
         throw new RuntimeException("XML Exception:" + e);
      }

   }

   /**
    * returns the value as an Exception Object
    */
   public Exception unwrap() {
      return exception;
   }

 
   /**
    * Returns "XMLException".
    */
   public String getXMLTag() {
      return XMLTAG;
   }
  
   public static final String XMLTAG = "XMLException";
   public static final String CLASSNAME = "parlevink.xml.XMLException";
   public static final String WRAPPEDCLASSNAME = "java.lang.Exception";

}
