/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLBoolean.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:04  zwiers
// no message
//


package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import java.util.*;

/**
 * XMLBoolean is in essence a wrapper around  boolean values, 
 * that turns them  effectively into an XMLStructure object.
 */

public class XMLBoolean extends XMLBasicValue<Boolean> implements XMLWrapper<Boolean>
{

   private boolean val;

   /**
    * creates a new XMLBoolean with value false.
    */
   public XMLBoolean() {
      val = false;
   }

   /**
    * creates a new XMLBoolean with specified value.
    */
   public XMLBoolean(boolean value) {
      val = value;
   }
   /**
    * creates a new XMLBoolean with specified value.
    */
   public XMLBoolean(Boolean value) {
      val = value.booleanValue();
   }


   /**
    * creates a new XMLBoolean with specified value.
    */
   public XMLBoolean(XMLBoolean xmlvalue) {
      val = xmlvalue.val;
   }

   /**
    * returns the value as a boolean.
    */
   public boolean booleanValue() {
      return  val;
   }

   /**
    * returns the value as a Boolean
    */
   public Boolean BooleanValue() {
      return new Boolean(val);
   }

   /**
    * returns the normal int value as String
    */
   public String toString() {
      return Boolean.toString(val);
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlBoolean) {
      return (((XMLBoolean) xmlBoolean).val == this.val);
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
      return appendAttribute(buf, "val", Boolean.toString(val));
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      val = (valCode.trim().toLowerCase().equals("true"));
   }
 
   /**
    * returns the value as an Integer Object
    */
   public Boolean unwrap() {
      return new Boolean(val);
   } 
 

   /**
    * Returns "XMLBoolean".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public static final String XMLTAG = "XMLBoolean";
   public static final String CLASSNAME = "parlevink.xml.XMLBoolean";
   public static final String WRAPPEDCLASSNAME = "java.lang.Boolean";

}
