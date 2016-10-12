/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLDouble.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.10  2005/10/20 09:54:58  zwiers
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
 * XMLDouble is in essence a wrapper around  double or Double values, 
 * that turns them  effectively into an XMLizable object.
 */

public class XMLDouble extends XMLBasicValue<Double> implements XMLStructure
{

   private double val;

   /**
    * creates a new XMLDouble with value 0.
    */
   public XMLDouble() {
      val = 0;
   }


   /**
    * creates a new XMLDouble with specified value.
    */
   public XMLDouble(double value) {
      val = value;
   }

   /**
    * creates a new XMLDouble with specified value.
    */
   public XMLDouble(Double value) {
      val = value;
   }

   /**
    * creates a new XMLDouble with specified value.
    */
   public XMLDouble(XMLDouble xmlvalue) {
      val = xmlvalue.val;
   }


   /**
    * returns the value as a double.
    */
   public double doubleValue() {
      return val;
   }


   /**
    * returns the value as an Double
    */
   public Double DoubleValue() {
      return new Double(val);
   }


   /**
    * returns the normal int value as String
    */
   public String toString() {
      return Double.toString(val);
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlDouble) {
      return (((XMLDouble) xmlDouble).val == this.val);
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
      return appendAttribute(buf, "val", Double.toString(val));
   }


   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      val = Double.parseDouble(valCode);
   }
 
   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode) {
      val = Double.parseDouble(valCode);
   }


   /**
    * returns the value as an Double Object
    */
   public Double unwrap() {
      return new Double(val);
   }


   /**
    * Returns "XMLDouble".
    */
   public String getXMLTag() {
      return XMLTAG;
   }
  
   public static final String XMLTAG = "XMLDouble";
   public static final String CLASSNAME = "parlevink.xml.XMLDouble";
   public static final String WRAPPEDCLASSNAME = "java.lang.Double";
  

}
