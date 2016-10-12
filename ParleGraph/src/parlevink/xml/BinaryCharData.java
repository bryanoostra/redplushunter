/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: BinaryCharData.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.5  2005/10/20 09:54:59  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:03  zwiers
// no message
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;

/**
 * BinaryCharData is a class to encode and decode binary data in a form
 * that is acceptable as XML style "CharData". The latter consists of
 * normal extended ASCII characters, excluding the special XML characters
 * '<', '>', '", and '''. The latter occur in "excaped forms" like
 * &lt; for '<' etc. (In this respect it differs from the BinaryData class,
 * which also encodes binary data, but does not produce XML CharData)
 * The binary data itself must be in the form of byte arrays; the encoding
 * is in the form of Java/unicode characters, with an integer value
 * in the range 0..255. (i.e. 8 bit characters, or  "extended ASCII").
 * The encoding is independent of any character set, and so, encoded data
 * should be regarded as "non printable".
 * BinaryData implements the XMLizable interface.
 */

public class BinaryCharData implements XMLStructure
{

   private BinaryData binData;
   private CharData charData;

   /**
    * creates an empty BinaryCharData object.
    */
   public BinaryCharData() {      
      binData = new BinaryData();
      charData = new CharData();
   }

   /**
    * creates a new BinaryCharData wrapper around the binaryData array.
    */
   public BinaryCharData(byte[] binaryData) {
      binData = new BinaryData(binaryData);
      charData = new CharData(binData.toXMLString());
   }

   /** 
    * return the current size of the binary data.
    */
   public int size() {
      return binData.size();
   }


   /**
    * returns a byte array containing the binary data.
    * The array has the exact length for the binary data, i.e.
    * the length of the array returned equals the current size
    * of the data, unless the current size = 0, in which case
    * a null result is returned.
    */
   public byte[] getBytes() {
      return binData.getBytes();
   }
 
   
   /**
    * sets the binary data of this object.
    */
   public void setBytes(byte[] binaryData) {
      binData.setBytes(binaryData);
      charData = new CharData(binData.toXMLString());
   }


   /**
    * reads all characters as delivered by "in" until the 
    * end of the stream is reached, and converts the result into
    * binary data.
    */
   public XMLStructure readXML(Reader in) throws IOException {
      charData.readXML(in);
      binData.readXML(charData.toString());
      return this;
   } 
   
   /**
    * reconstructs this BinaryData object from String s,
    * assumed to be encoded via the writeXML or toXMLString methods of this class.
    * Note that s does not have any true XML based structure, but rather
    * can be any 8-bit character String.
    */
   public XMLStructure readXML(String s) {
      charData.readXML(s);
      binData.readXML(charData.toString());  
      return this; 
   } 
   
   /**
    * reconstructs this BinaryCharData object by parsing a stream of XML tokens,
    * that are delivered by a XMLTokenizer. The first token is assumed to be
    * a CharData token, the remaining tokens, if any, are left untouched.
    * From the XML standards it follows that the end of the data is reached
    * when either a '<' character or the end of the input stream is reached. 
    * (The '<' character is not consumed).
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) throws XMLScanException, IOException {
      charData.readXML(tokenizer);
      binData.readXML(charData.toString());
      return this;
   } 

   /**
    * yields the binary data in XML-coded char format, written to "out". 
    * This String is acceptable as XML style "PCData", i.e. characters
    * like '<' and '&' have been encoded.
    * The encoding is the same as the encoding used by the toXMLString() method.
    */ 
   public void writeXML(PrintWriter out) {
      charData.writeXML(out);
   } 

   /**
    * appends an XML encoded String to "buf".
    */
   public StringBuffer appendXML(StringBuffer buf) {     
      return charData.appendXML(buf);
   } 

   /**
    * equivalent to appendXML(buf);
    * The tab setting is (necessarily) ignored.
    */
   public StringBuffer appendXML(StringBuffer buf, int tab) {
      return charData.appendXML(buf);
   }
   /**
    * equivalent to writeXML(out):
    * The tab setting is (necessarily) ignored.
    */
   public void writeXML(PrintWriter out, int tab) {
      charData.writeXML(out);
   } 


   /**
    * yields the binary data in XML-coded char format. 
    * This String is acceptable as XML style "PCData", 
    * ("Parsed Character Data) or XML style "CData".
    * Characters like '<', '>' '"' and '&' have been encoded.
    * The String contains characters with a int value
    * in the range 0 .. 255, excluding the '<', '>', '"',
    * and '"' characters.
    * This implies that the String might contain 
    * non printable characters.
    */ 
   public String toXMLString() {
      return charData.toXMLString();
   } 

   /**
    * equivalent to toXMLString();
    * the tab is (necessarily) ignored
    */
   public String toXMLString(int tab) {
      return charData.toXMLString();
   } 

 
   /**
    * returns the binary data in in XML-coded String format.
    * The result is the same as returned by toXMLString().
    * The result might contain non printable characters.
    */
   public String toString() {
      return charData.toString();
   }

   /**
    * compares for equality of the binaray data.
    * consistent with the hashCode method.
    */
   public boolean equals(Object bdata) {
      BinaryCharData bcd = (BinaryCharData) bdata;
      return bcd.charData.equals(this.charData);
   }

   /**
    * calculates the hash code based upon the binary data;
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return charData.hashCode();
    }



  /**
   * Since BinaryCharData is not encoded using an XML element,
   * the XMLTag is not defined. Therefore, this method returns null.
   */
  public String getXMLTag() {
     return null; 
  }

   /**
    * return the java.lang.String Class
    *
   public Class wrappedClass() {
      return wrappedClass;
   };


   /**
    * returns the String value.
    */
   public Object unwrap() {
      return getBytes();
   }

   /**
    * the (initial) length of the StringBuffer used for readXML(in).
    */
   public static int BUFLEN = 100;


   public static final String CLASSNAME = "parlevink.xml.BinaryCharData";
   public static final String WRAPPEDCLASSNAME = "[B"; // Java's name for byte[]
//   private static Class wrappedClass;
//   static {
//       try {
//         //wrappedClass = Class.forName(WRAPPEDCLASSNAME); // works also
//         wrappedClass = new byte[0].getClass();
//       } catch (Exception e) {}
//   }
   
}
