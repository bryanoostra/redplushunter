/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLInteger.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:09  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.10  2005/10/20 09:54:58  zwiers
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
 * XMLInteger is in essence a wrapper around int or Integer values, 
 * that turns them  effectively into an XMLizable object.
 */
public class XMLInteger extends XMLBasicValue<Integer> implements XMLStructure
{

   private int val;

   /**
    * creates a new XMLInteger with value 0.
    */
   public XMLInteger() {
      val = 0;
   }

   /**
    * creates a new XMLInteger with specified value.
    */
   public XMLInteger(int value) {
      val = value;
   }

   /**
    * creates a new XMLInteger with specified value.
    */
   public XMLInteger(Integer value) {
      val = value;
   }


   /**
    * creates a new XMLInteger with specified value.
    */
   public XMLInteger(XMLInteger xmlValue) {
      val = xmlValue.val;
   }

   /**
    * returns the value as an int.
    */
   public int intValue() {
      return val;
   }

   /**
    * returns the value as an Integer
    */
   public Integer IntegerValue() {
      return new Integer(val);
   }

   /**
    * returns the normal int value as String
    */
   public String toString() {
      return Integer.toString(val);
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlInteger) {
      return (((XMLInteger) xmlInteger).val == this.val);
   }

   /**
    * calculates the hash code 
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return val;
    }

   /**
    * returns a String that can be used as XML attribute value.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      return appendAttribute(buf, "val", Integer.toString(val));
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      val = Integer.parseInt(valCode);
   }
 
   /**
    * returns the value as an Integer Object
    */
   public Integer unwrap() {
      return new Integer((int)val);
   } 
 

   /**
    * Returns "XMLInteger".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public static final String XMLTAG = "XMLInteger";
   public static final String CLASSNAME = "parlevink.xml.XMLInteger";
   public static final String WRAPPEDCLASSNAME = "java.lang.Integer";

}
