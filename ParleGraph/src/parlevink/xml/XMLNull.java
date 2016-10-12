/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLNull.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import java.util.*;

/**
 * XMLNull reresents a null value, in the form of an XMLStructure
 */

public class XMLNull extends XMLBasicValue implements XMLStructure
{


   /**
    * creates a new XMLNull Object
    */
   public XMLNull() {
   }


   /**
    * returns "<Null>"
    */
   public String toString() {
      return "<Null>";
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object obj) {
      return obj instanceof XMLNull;
   }

   /**
    * calculates the hash code 
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return 0;
    }

   /**
    * returns a String that can be used as XML attribute value.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      return buf;
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      throw new RuntimeException("XMLNull with attribute encountered");
   }
 
   /**
    * returns the value as an Integer Object
    */
   public Object unwrap() {
      return null;
   } 
 
   /**
    * The default is equivalent to getClass()
    * This method should be overwritten by extensions if the XMLStructure
    * is really a "wrapper" for a (non-XMLStructure) Java Object.
    *
   public Class wrappedClass() {
      return wrappedClass;
   }

   /**
    * Returns "XMLNull".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public static final String XMLTAG = "XMLNull";
   public static final String CLASSNAME = "parlevink.xml.XMLNull";
   public static final String WRAPPEDCLASSNAME = "java.lang.Object";
//   private static Class wrappedClass;
//   static {
//       try {
//         wrappedClass = Class.forName(WRAPPEDCLASSNAME);
//       } catch (Exception e) {}
//      // XML.addClass(XMLTAG, CLASSNAME);     
//   }

}
