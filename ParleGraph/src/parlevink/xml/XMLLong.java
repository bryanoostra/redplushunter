/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLLong.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
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
 * XMLLong is in essence a wrapper around  long or Long values, 
 * that turns them  effectively into an XMLizable object.
 */
public class XMLLong extends XMLBasicValue<Long> implements XMLStructure
{

   private long val;

   /**
    * creates a new XMLLong with value 0.
    */
   public XMLLong() {
      val = 0;
   }

   /**
    * creates a new XMLLong with specified value.
    */
   public XMLLong(long value) {
      val = value;
   }
   /**
    * creates a new XMLLong with specified value.
    */
   public XMLLong(Long value) {
      val = value;
   }


   /**
    * creates a new XMLLong with specified value.
    */
   public XMLLong(XMLLong xmlValue) {
      val = xmlValue.val;
   }


   /**
    * returns the value as a long.
    */
   public long longValue() {
      return val;
   }

   /**
    * returns the value as a Long
    */
   public Long LongValue() {
      return new Long(val);
   }


   /**
    * returns the normal int value as String
    */
   public String toString() {
      return Long.toString(val);
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlLong) {
      return (((XMLLong) xmlLong).val == this.val);
   }

   /**
    * calculates the hash code 
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return (int) (val % Integer.MAX_VALUE);
    }

   /**
    * returns a String that can be used as XML attribute value.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      return appendAttribute(buf, "val", Long.toString(val));
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      val = Long.parseLong(valCode);
   }
 
   /**
    * returns the value as an Long Object
    */
   public Long unwrap() {
      return new Long(val);
   } 
 

   /**
    * Returns "XMLLong".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public static final String XMLTAG = "XMLLong";
   public static final String CLASSNAME = "parlevink.xml.XMLLong";
   public static final String WRAPPEDCLASSNAME = "java.lang.Long";

}
