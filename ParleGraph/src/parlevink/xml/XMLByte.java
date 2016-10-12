/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLByte.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:09  swartjes
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
 * XMLByte is in essence a wrapper around byte or Byte values, 
 * that turns them  effectively byteo an XMLizable object.
 */
public class XMLByte extends XMLBasicValue<Byte> implements XMLStructure
{

   private byte val;

   /**
    * creates a new XMLByte with value 0.
    */
   public XMLByte() {
      val = 0;
   }

   /**
    * creates a new XMLByte with specified value.
    */
   public XMLByte(byte value) {
      val = value;
   }

   /**
    * creates a new XMLByte with specified value.
    */
   public XMLByte(Byte value) {
      val = value;
   }


   /**
    * creates a new XMLByte with specified value.
    */
   public XMLByte(XMLByte xmlValue) {
      val = xmlValue.val;
   }

   /**
    * returns the value as an byte.
    */
   public byte byteValue() {
      return val;
   }

   /**
    * returns the value as an Byte
    */
   public Byte ByteValue() {
      return new Byte(val);
   }

   /**
    * returns the normal byte value as String
    */
   public String toString() {
      return Byte.toString(val);
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlByte) {
      return (((XMLByte) xmlByte).val == this.val);
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
      return appendAttribute(buf, "val", Byte.toString(val));
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      val = Byte.parseByte(valCode);
   }
 
   /**
    * returns the value as an Byte Object
    */
   public Byte unwrap() {
      return new Byte((byte)val);
   } 
 
   /**
    * Returns "XMLByte".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public static final String XMLTAG = "XMLByte";
   public static final String CLASSNAME = "parlevink.xml.XMLByte";
   public static final String WRAPPEDCLASSNAME = "java.lang.Byte";

}
