/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: BinaryData.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
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
// Revision 1.1  2005/06/11 11:36:43  zwiers
// initial version
//
// Revision 1.4  2004/07/05 08:21:00  zwiers
// createFromXML methods removed
//
// Revision 1.3  2004/05/17 14:57:39  zwiers
// major revision
//
// Revision 1.2  2003/10/25 20:45:11  zwiers
// XMLStructure versions
//
// Revision 1.1  2003/06/20 16:03:05  zwiers
// initial version
//
// Revision 1.1.1.1  2002/09/11 09:36:13  zwiers
// initial import
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import parlevink.util.Console;

/**
 * BinaryData is a class to encode and decode binary data.
 * The binary data must be in the form of byte arrays; the encoding
 * is in the form of Java/unicode characters, with an integer value
 * in the range 0..255. (i.e. 8 bit characters, or  "extended ASCII").
 * The encoding is independent of any character set, and so, encoded data
 * should be regarded as "non printable".
 * BinaryData implements the XMLizable interface, but for this class
 * this should be understood as a mere formal requirement so as to
 * allow BinaryData objects to be used as the "contents" 
 * of parlevink.agents.Messages. That is, there are methods like writeXML()
 * and toXMLString() that convert the binary data to character Strings, and 
 * methods like readXML() that convert these Strings back into binary data.
 * The character String as such do NOT conform to any format defined
 * by XML standards. If XML conformance is required, the encoding String could
 * be wrapped in a parlevink.xml.BinaryCharData object, rather than in 
 * a parlevink.xml.BinaryData object.
 * The advantage of BinaryData over BinaryCharData is a small speed advantage, 
 * since XML escape sequences like &lt; are simply not taken into account. 
 *
 * More in particular, the encoding Strings cannot be used as  so called 
 * "parsed character data" ("CharData") in XML documents, since they 
 * might contain characters like '<', '>', '&'.  In fact the encoding 
 * cannot even be used as so called "unparsed character data" ("CData") 
 * since the encoding might contain  the  character sequence "]]>", 
 * which is used to terminate such data in XML documents. 
 */

public class BinaryData implements XMLStructure {

   private byte[] binaryData;
   private char[] charCode;
   private int binSize, bufSize;

   /**
    * creates an empty BinaryData object, with null buffer array
    */
   public BinaryData() {      
      binSize  = 0;
      bufSize = 0;
   }

   /**
    * creates an empty BinaryData object with buffer array with specified size
    */
   public BinaryData(int bufferSize) {      
      binSize  = 0;
      bufSize = bufferSize;
      binaryData = new byte[bufSize];
   }


   /**
    * creates a new BinaryData wrapper around the binaryData array.
    */
   public BinaryData(byte[] binaryData) {
      this.binaryData = binaryData;
      if (binaryData != null) {
         binSize = binaryData.length;
      } else {
         binSize = 0;
      }
      bufSize = binSize;
   }

   /** 
    * return the current size of the binary data.
    * Note that the actual buffer might be larger than size.
    */
   public int size() {
      return binSize;
   }


   /**
    * returns a byte array containing the binary data.
    * The array has the exact length for the binary data, i.e.
    * the length of the array returned equals the current size
    * of the data, unless the current size = 0, in which case
    * a null result is returned.
    */
   public byte[] getBytes() {
      if (binSize == 0) return null;
      if (bufSize > binSize) {
          // squeeze the data array
          byte[] newbin = new byte[binSize];
          for (int i=0; i<binSize; i++) {
              newbin[i] = binaryData[i];
          }
          binaryData = newbin; 
          bufSize = binSize; 
      }
      return binaryData;
   }
 
 
   /**
    * returns the internal byte buffer array containing the binary data.
    * This array is likely to be larger than the actual size of the data,
    * i.e. the size() method should be used to determine the length
    * of the actual data. 
    * When no data is available, a null result is returned.
    */
   public byte[] getByteBuffer() {
      return binaryData;
   }  
   
   /**
    * sets the binary data of this object.
    */
   public void setBytes(byte[] binaryData) {
      this.binaryData = binaryData;
      if (binaryData != null) {
         binSize = binaryData.length;
      } else {
         binSize = 0;
      }
      bufSize = binSize;
   }

   /*
    * inrenal method that reallocates the binarayData buffer, 
    * doubling its size.
    */
   private void reallocate() {
      int newSize = bufSize * 2;
      byte [] newbin = new byte[newSize];
      for (int i=0; i<binSize; i++) {
         newbin[i] = binaryData[i];
      }
      binaryData = newbin;
      bufSize = newSize;
   }

   /**
    * reads all characters as delivered by "in" until the 
    * end of the stream is reached, and converts the result into
    * binary data.
    */
   public XMLStructure readXML(Reader in) throws IOException {
      if (binaryData == null || bufSize == 0) {
         bufSize = DEFAULT_SIZE;
         binaryData = new byte[bufSize];
      }
      binSize = 0;
      int ci = in.read();
      while (ci != -1) {
          if (binSize >= bufSize) reallocate(); 
          if (ci >=128) {  // encoding of bytes in the range -128 .. -1
             binaryData[binSize++] = (byte) (ci-256); 
          } else {  // encoding of byte in the range 0 .. 127
             binaryData[binSize++] = (byte) ci;
          }  
      	 ci = in.read();
      } 
      return this;
   } 
   
   /**
    * reconstructs this BinaryData object from String s,
    * assumed to be encoded via the writeXML or toXMLString methods of this class.
    * Note that s does not have any true XML based structure, but rather
    * can be any 8-bit character String.
    */
   public XMLStructure readXML(String s) {
      bufSize = s.length();
      binaryData = new byte[bufSize];
      binSize = 0;
      for (int i=0; i<bufSize; i++) {
          int bi = s.charAt(i);
          if (bi >=128) {  // encoding of bytes in the range -128 .. -1
             binaryData[binSize++] = (byte) (bi-256); 
          } else {  // encoding of byte in the range 0 .. 127
             binaryData[binSize++] = (byte) bi;
          }  
      }  
      return this;
   } 
   
   /**
    * readXML() with an XMLTokenizer argument assumes that the buffer size
    * has been set already, for instance by allocating the BinaryData object with 
    * the correct size or by setting the data by means of setBytes with a byte array
    * of the correct size.
    * Note that this buffer size is not the same as the current data size as returned by
    * the size() method. (The latter is always 0 upon allocation.)
    * the readXML method will take exactly "buffer size" characters from the underlying
    * input stream, and convert them to bytes.
    * (If fewer characters than "buffer size" characters are left on the input stream,
    * the available characters are converted, and an XMLScanException is thrown.)
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) throws XMLScanException, IOException {
      String enc = tokenizer.takeString(bufSize);
      return readXML(enc);
      //throw new UnsupportedOperationException("readXML(XMLTokenizer) method not supported");
   } 

   /**
    * yields the binaray data in char format, written to "out". 
    * The String might contain any character with a int value
    * in the range 0 .. 255, so does NOT conform to XML standards.
    * The only guarantee is that the readXML methods of this class can
    * reconstruct the binary data from this String.
    * The encoding is the same as the encoding used by the toXMLString() method.
    * If true conformance to XML encodings is required,
    * the BinaryCharData class should be used instead of this class.
    */ 
   public void writeXML(PrintWriter out) {
      for (int i=0; i<binaryData.length; i++) {
          int bi = binaryData[i];
          if (bi >=0) {  //bytes in the range 0 .. 127
             out.print((char)bi);  
          } else { // bytes in the range -128 .. -1
             out.print((char)(bi+256));
          }  
      }  
   } 

   /**
    * appends an XML encoded String to "buf".
    */
   public StringBuffer appendXML(StringBuffer buf) {
      for (int i=0; i<binaryData.length; i++) {
          int bi = binaryData[i];
          if (bi >=0) {  //bytes in the range 0 .. 127
             buf.append((char)bi);  
          } else { // bytes in the range -128 .. -1
             buf.append((char)(bi+256));
          }  
      }  
      return buf;
   } 

   /**
    * equivalent to appendXML(buf);
    * The tab setting is (necessarily) ignored.
    */
   public StringBuffer appendXML(StringBuffer buf, int tab) {
      return appendXML(buf);
   }
   
   /**
    * equivalent to writeXML(out):
    * The tab setting is (necessarily) ignored.
    */
   public void writeXML(PrintWriter out, int tab) {
      writeXML(out);
   } 

   /**
    * yields the binaray data in String format. 
    * The String might contain any character with a int value
    * in the range 0 .. 255, so does NOT conform to XML standards.
    * The only guarantee is that the readXML methods of this class can
    * reconstruct the binary data from this String.
    * If true conformance to XML encodings is required,
    * the XMLData class should be used.
    */ 
   public String toXMLString() {
      StringBuffer charCode = new StringBuffer(binaryData.length);
      for (int i=0; i<binaryData.length; i++) {
          int bi = binaryData[i];
          if (bi >=0) {
             charCode.append((char)bi); //bytes in the range 0 .. 127
          } else {
             charCode.append((char)(bi+256));  // bytes in the range -128 .. -1
          }  
      }  
      return charCode.toString();
   } 
 
   /**
    * equivalent to toXMLString(): the tab is
    * (necessarily) ignored.
    */
   public String toXMLString(int tab) { 
       return toXMLString();  
   }
 
   /**
    * returns the binary data in String format.
    * The result might contain non printable characters.
    */
   public String toString() {
      return toXMLString();
   }

   /**
    * compares for equality of the binaray data.
    * consistent with the hashCode method.
    */
   public boolean equals(Object bdata) {
      BinaryData bd = (BinaryData) bdata;
      for (int i=0; i<binaryData.length; i++) {
          if (binaryData[i] != bd.binaryData[i]) {
              return false;  
          }
      }
      return true;
   }

   /**
    * calculates the hash code based upon the binary data;
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      int hash = 0;
      for (int i=0; i<binaryData.length; i++) {
          hash = (hash+binaryData[i]) % Integer.MAX_VALUE;
      }
      return hash;
   }

   /**
    * converts an array of bytes to an array of characters
    * bytes in the range 0 <= b <= 127 are mapped to characters with the same int value,
    * bytes in the range -128 <= b <0 are mapped to charcters in the range 128 .. 255.
    * byteToChar is the inverse of charToByte.
    */
   public static void byteToChar(byte[] in, char[] out) {
      Console.println("byteToChar, inlen = " + in.length + ", outlen = " + out.length);
      for (int i=0; i<in.length; i++) {
          int bi = in[i];
          if (bi >=0) {
             out[i] = (char)bi; 
          } else {
            out[i] = (char)(bi+256);
          }  
      }  
     
   }

   /**
    * converts an array of characters to an array of bytes.
    * Characters in the range 0 <= c <= 127 are mapped to the same byte value;
    * characters in the range 128 <= c < 256 are mapped to bytes in the range -128..-1.
    * charToByte is the inverse of byteToChar.
    * Assumption: the characters have int values in the range 0 .. 255.
    */
   public static void charToByte(char[] in, byte[] out) {
      for (int i=0; i<in.length; i++) {
          int bi = in[i];
          if (bi >=128) {
             out[i] = (byte) (bi-256); 
          } else {
            out[i] = (byte) bi;
          }  
      }  
     
   }


  
  
  /**
   * Since BinaryData is not encoded using an XML element,
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


   public static final String CLASSNAME = "parlevink.xml.BinaryData";
   public static final String WRAPPEDCLASSNAME = "[B"; // Java's name for byte[]
//   private static Class wrappedClass;
//   static {
//       try {
//         //wrappedClass = Class.forName(WRAPPEDCLASSNAME); // works also
//         wrappedClass = new byte[0].getClass();
//       } catch (Exception e) {}
//   }
//

   /**
    * default size of the byte buffer array
    */
   public static final int DEFAULT_SIZE = 128;
   
   
}
