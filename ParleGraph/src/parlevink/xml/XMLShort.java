/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLShort.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2005/10/20 09:55:09  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.1  2005/09/18 21:47:21  zwiers
// no message
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import java.util.*;

/**
 * XMLShort is in essence a wrapper around short or Short values, 
 * that turns them  effectively shorto an XMLizable object.
 */
public class XMLShort extends XMLBasicValue<Short> implements XMLStructure
{

   private short val;

   /**
    * creates a new XMLShort with value 0.
    */
   public XMLShort() {
      val = 0;
   }

   /**
    * creates a new XMLShort with specified value.
    */
   public XMLShort(short value) {
      val = value;
   }

   /**
    * creates a new XMLShort with specified value.
    */
   public XMLShort(Short value) {
      val = value;
   }


   /**
    * creates a new XMLShort with specified value.
    */
   public XMLShort(XMLShort xmlValue) {
      val = xmlValue.val;
   }

   /**
    * returns the value as an short.
    */
   public short shortValue() {
      return val;
   }

   /**
    * returns the value as an Short
    */
   public Short ShortValue() {
      return new Short(val);
   }

   /**
    * returns the normal short value as String
    */
   public String toString() {
      return Short.toString(val);
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlShort) {
      return (((XMLShort) xmlShort).val == this.val);
   }

   /**
    * calculates the hash code 
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return (int) val;
    }

   /**
    * returns a String that can be used as XML attribute value.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      return appendAttribute(buf, "val", Short.toString(val));
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      val = Short.parseShort(valCode);
   }
 
   /**
    * returns the value as an Short Object
    */
   public Short unwrap() {
      return new Short((short)val);
   } 
 
   /**
    * Returns "XMLShort".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public static final String XMLTAG = "XMLShort";
   public static final String CLASSNAME = "parlevink.xml.XMLShort";
   public static final String WRAPPEDCLASSNAME = "java.lang.Short";

}
