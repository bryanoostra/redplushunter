/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLFloat.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2005/10/20 09:54:58  zwiers
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
 * XMLFloat is in essence a wrapper around  float, double, Float or Double values, 
 * that turns them  effectively into an XMLizable object.
 */

public class XMLFloat extends XMLBasicValue<Float> implements XMLStructure
{
   private float val;

   /**
    * creates a new XMLFloat with value 0.
    */
   public XMLFloat() {
      val = 0;
   }

   /**
    * creates a new XMLFloat with specified value.
    */
   public XMLFloat(float value) {
      val = value;
   }

   /**
    * creates a new XMLFloat with specified value.
    */
   public XMLFloat(Float value) {
      val = value.floatValue();
   }

   /**
    * creates a new XMLFloat with specified value.
    */
   public XMLFloat(XMLFloat xmlvalue) {
      val = xmlvalue.val;
   }

   /**
    * returns the value as a float.
    */
   public float floatValue() {
      return  val;
   }

   
   /**
    * returns the value as a Float
    */
   public Float FloatValue() {
      return new Float(val);
   }

   /**
    * returns the normal int value as String
    */
   public String toString() {
      return Float.toString(val);
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object XMLFloat) {
      return (((XMLFloat) XMLFloat).val == this.val);
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
      return appendAttribute(buf, "val", Float.toString(val));
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      val = Float.parseFloat(valCode);
   }
 
   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode) {
      val = Float.parseFloat(valCode);
   }

   /**
    * returns the value as an Double Object
    */
   public Float unwrap() {
      return new Float(val);
   }

   /**
    * Returns "XMLFloat".
    */
   public String getXMLTag() {
      return XMLTAG;
   }
  
   public static final String XMLTAG = "XMLFloat";
   public static final String CLASSNAME = "parlevink.xml.XMLFloat";
   public static final String WRAPPEDCLASSNAME = "java.lang.Float";

}
