/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: CharData.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.12  2005/10/20 09:54:59  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:03  zwiers
// no message
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;

/**
 * CharData objects are containers for Strings that can be translated (back and forth)
 * into XML style CharData. The latter refers to the character data that occurs between
 * XML tags: <tag ...> CharData </tag>, or as Attribute Value strings of the form : "text"
 * or 'text'.  Such character data and string texts cannot include the '<' and '&,
 * characters, and, in the case of text strings, it cannot contain the 
 * string quote or apostrophe character. Moreover, XML CharData cannot include the pattern "]]>".
 * CharData <i>objects</i> take care of all these restrictions by means of a marshalling 
 * process carried out when the methods writeXML() or toXMLSTring() are called, 
 * and an similar unmarshalling process for the methods readXML()or toString(). 
 * Marshalling here means that selected characters are replaced by XML entity references, 
 * such as "&lt;", representing a "<" character. Unmarshalling carries out the 
 * reverse translation. Marshalling performs the following translations:
 * '<' to "&lt;", '>' to "&gt;", '&' to "&amp;", '"' to "&quot;", '\'' to "&apos;".
 * (Note that the sequence of the form "]]>" is therefore translated to "]]&gt;").
 * CharData does implement the XMLStructure <i>methods<i>, but does not fulfill 
 * the XMLStructure requirements as far as XML encoding is concerned. 
 * (For instance, writeXML does not produce legal XML, but rather a character string
 * that can be incorporated into XML)
 * Rather, CharData is an auxiliary class that can be used within 
 * other XMLStructures, to encode normal character data
 * as proper XML style  parsed character data.
 */
public class CharData implements XMLWrapper<String>
{

   /* The "internal" String variable is the normal String representation of the CharData object, 
    * where all characters can be used, including '<', '>', '&' , '"', and '\''.
    * "external" is the external XML representation that can occur as XML-CharData
    * between tags, or as attribute value Strings.
    * "internal" is always defined, but "external" will be calculated (and cached) only
    * when neccesary. 
    */
   public String internal;
   public String external;
   private StringBuffer buf;

  /**
   * creates a new CharData object, with undefined value.
   */
   public CharData() {
      internal = "";
      external = null;
   }

  /**
   * creates a new CharData object for String "str".
   * This String can contain <it>all</it> characters, 
   * including characters like '<' that are not allowed in XML CharData.
   * Such characters will be replaced by encodings, like "&lt;", when the
   * CharData object is converted to XML, by means of the toXMLString
   * or the writeXML methods.
   */
   public CharData(String str) {
      internal = str;
      external = null;
   }

  /*
   * creates a new CharData object for String "str"
   * "isExternal" denotes whether "str" is the considered to be
   * the internal or the external representation.
   * <it>Any</it> String can be used as <it>internal</it> value; 
   * however, <it>external</it> values must conform to XML CharData requirements. 
   * That is, if isExternal == true,  '<', '>', '&' characters are not allowed, and
   * '\'' and '"' are not to be used if the CharData object will be used
   * to represent attribute values. 
   * Catch: The validity of external representations is <it>not checked.</it>
   * Rather it is assumed that these do not contains charcaters like &lt;
   *
   private CharData(String str, boolean isExternal) {
      if (isExternal) {
         internal = null;
         external = str;
         toInternal();
      } else {
         internal = str;
         external = null;
      }
   }


   /**
    * reconstructs this CharData object by reading and parsing XML encoded 
    * CharData from in. Data is read until the end of data is reached,
    * or until a '<' character is read. (a sequence "&lt;" is translated to '<',
    * but does not terminate the CharData string).
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML, as well as IOExceptions.
    */
   public XMLStructure readXML(Reader in) throws IOException {
      clearBuf(BUFLEN);   
      int ci = in.read();
      while (ci != -1 && ci != '<') {
      	 buf.append((char)ci);
      	 ci = in.read();
      }   
      external = buf.toString();
      toInternal();
      return this;
   } 
   
   /**
    * reconstructs this CharData object by parsing an XML encoded String s
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(String s) {
      external = s;
      toInternal();
      return this;
   } 
   
   /**
    * reconstructs this CharData object by parsing a stream of XML tokens,
    * that are delivered by a XMLTokenizer. The first token is assumed to be
    * a CharData token, the remaining tokens, if any, are left untouched.
    * From the XML standards it follows that the end of the data is reached
    * when either a '<' character or the end of the input stream is reached. 
    * (The '<' character is not consumed).
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) {
      try {
         if ( ! tokenizer.atCharData() ) throw new XMLScanException("XMLTokenizer not at CharData");
         internal = tokenizer.takeCharData();
         return this;
      } catch (IOException e) { throw new XMLScanException("IOException in XMLTokenizer: " + e); }
   } 

   /**
    * Like readXML(tokenizer), but strips the first and last character of the CharData.
    * (These characters are presumably whitespace, like \n chars.)
    * Special purpose method, used in parlevink.agents.Message.
    */
   public XMLStructure readAndTrimXML(XMLTokenizer tokenizer) {
      try {
         if ( ! tokenizer.atCharData() ) throw new XMLScanException("XMLTokenizer not at CharData");        
         String chdata = tokenizer.takeCharData();
         internal = chdata.substring(1, chdata.length()-1);
         return this;
      } catch (IOException e) { throw new XMLScanException("IOException in XMLTokenizer: " + e); }
   } 

   /**
    * trims the (internal) character data representation, 
    * i.e. trim() removes leading and trailing whitespace. 
    */
   public void trim() {
      internal.trim();
   }

   /**
    * trims the first and last character of the (internal) string.
    * (Not necessarily whitespace; although the intended usage is to 
    * remove the \n characters that are added in typical XML encodings)
    */
   public void trim1() {
      internal = internal.substring(1, internal.length()-1);
   }


   /**
    * writes an XML encoded String to "out".
    * This String equals the result of toXMLString().
    */
   public void writeXML(PrintWriter out) {
      if (external == null) toExternal();
      out.print(external);
   } 

   /**
    * writes an XML encoded String to "out".
    * This String equals the result of toXMLString().
    * The tab setting is (necessarily) ignored.
    */
   public void writeXML(PrintWriter out, int tab) {
      writeXML(out);
   } 


   /**
    * appends an XML encoded String to "buf".
    * This String equals the result of toXMLString().

    */
   public StringBuffer appendXML(StringBuffer buf) {
      if (external == null) toExternal();
      buf.append(external);
      return buf;
   } 


   /**
    * appends an XML encoded String to "buf".
    * This String equals the result of toXMLString().
    * The tab setting is (necessarily) ignored.
    */
   public StringBuffer appendXML(StringBuffer buf, int tab) {
      return appendXML(buf);
   }

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString() {
      if (external == null) toExternal();
      return external;
   } 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    * The tab setting is (necessarily) ignored.
    */ 
   public String toXMLString(int tab) {
      return toXMLString();
   } 


  /**
   * returns the String typed unmarshalled value.
   * That is, the value where characters like '<' occur as such,
   * rather than being encoded like "&lt;".
   */
  public String toString() {
     if (internal == null) toInternal();
     return internal;
  }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object chardata) {
      return ((CharData) chardata).internal.equals(this.internal);
   }
  
   public int hashCode() {
       return internal.hashCode();
   }
  
   private final void clearBuf(int len) {
      if (buf == null) buf = new StringBuffer(len);
      else buf.delete(0, buf.length());
   }

   /**
    * converts the internal (ordinary) String representation
    * to the external XML representation. 
    * The latter uses XML entity references: "&lt"; for '<', "&gt;" for '>', and "&amp;" for '&'
    * "&quot;" for '"', and "&apos;" for '\''.
    * The resulting external String can be safely used as CharData or attribute value
    * in an XML text.
    */
   public void toExternal() {
      int len = internal.length();
      clearBuf(len + 10);
      for (int i=0; i<len; i++) {
          char ch = internal.charAt(i);
               if (ch == '<') buf.append("&lt;");
          else if (ch == '>') buf.append("&gt;");
          else if (ch == '&') buf.append("&amp;");
          else if (ch == '"') buf.append("&quot;");
          else if (ch == '\'') buf.append("&apos;");
          else buf.append(ch);   
      }
      external = buf.toString();
   }

   /**
    * converts the external XML representation
    * to the internal String representation. 
    * The former uses "&lt"; for '<', "&gt;" for '>', and "&amp;" for '&';
    * "&quot;" for '"', and "&apos;" for '\''.
    * these XML entity references are translated back to ordinary characters.
    */
   public void toInternal() {
      if (external == null) {
         internal = null;
         return;
      }
      int len = external.length();
      clearBuf(len);
      int i = 0; 
      char ch;
      while (i < len) {
         // parse char external char i 
         if ( (ch=external.charAt(i++)) != '&') {
             buf.append(ch);
         } else {
            //Console.println("external=" + external + "\ncharAt(i)=" + external.charAt(i));
            switch ( external.charAt(i++) ) {
               case 'l' : {  
                  if ( external.charAt(i++) != 't' || external.charAt(i++) != ';') throw new XMLScanException("error in \"&lt;\" reference");
                  buf.append('<');
                  break; 
               }           
               case 'g' : {               
                  if (external.charAt(i++) != 't' || external.charAt(i++) != ';') throw new XMLScanException("error in \"&gt;\" reference");
                  buf.append('>');
                  break; 
               }
               case 'q' : {               
                  if (external.charAt(i++) != 'u' ||  external.charAt(i++) != 'o' 
                   || external.charAt(i++) != 't' ||  external.charAt(i++) != ';') throw new XMLScanException("error in \"&quot;\" reference");
                  buf.append('"');
                  break; 
               }
               case 'a' : {  // could be &amp; or &apos;  
                  if (external.charAt(i) == 'm') {
                      i++;
                      if (external.charAt(i++) != 'p' || external.charAt(i++) != ';') 
                           throw new XMLScanException("error in \"&amp;\" reference");
                      else buf.append('&');
                  } else if (external.charAt(i) == 'p') {
                      i++;
                      if (external.charAt(i++) != 'o' || external.charAt(i++) != 's' || external.charAt(i++) != ';') 
                           throw new XMLScanException("error in \"&apos;\" reference");
                      else buf.append('\'');
                  } else {
                      throw new XMLScanException("error in \"$amp;\" or \"&apos;\" reference");
                  }
                  break; 
               }    
               default: { throw new XMLScanException("unexpected character after \'&\' : " + (external.charAt(i-1)) );            
               }
            }
         }
      }
      internal = buf.toString();
   }

  
  /**
   * Since CharData is not encoded using an XML element,
   * the XMLTag is not defined. Therefore, this method returns null.
   */
  public String getXMLTag() {
     return null; 
  }


   /**
    * returns the String value.
    */
   public String unwrap() {
      return toString();
   }

   /**
    * the (initial) length of the StringBuffer used for readXML(in).
    */
   public static int BUFLEN = 100;

   public static final String CLASSNAME = "parlevink.xml.CharData";
   public static final String WRAPPEDCLASSNAME = "java.lang.String";

}
