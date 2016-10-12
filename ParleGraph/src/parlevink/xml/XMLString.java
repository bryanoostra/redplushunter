/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLString.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.13  2005/10/20 09:54:57  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import java.util.*;

/*
 * XMLString is in essence a wrapper around a String, that turns it effectively
 * into an XMLizable object.
 * The String inside can be retrieved with the standard toString() method.
 */

public class XMLString extends XMLBasicValue<String> implements XMLStructure
{

  protected CharData chardata;

  /**
   * creates a new XMLString with undefined String value.
   */
   public XMLString() {
       chardata = new CharData();
   }

  /**
   * creates a new XMLString, representing String "str"
   */
   public XMLString(String str) {
      chardata = new CharData(str);
   }
    
   public String toString() {
      return chardata.toString();
   }

  /**
   * equality, based on String equality for the value fields;
   */
   public boolean equals(Object xmlstring) {
      return ((XMLString) xmlstring).chardata.equals(this.chardata);
   }

   /**
    * calculates the hash code 
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return chardata.hashCode();
    }
  
   /**
    * appends a String that can be used as XML attribute value.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      buf.append("val=\"");
      chardata.appendXML(buf);
      buf.append('"');
      return buf;
   }

   /**
    * decodes the value from an attribute value String, assuming that "valCode"
    * contains "PCData", i.e. data with possible occurrences of XML
    * escape sequences like &lt;, which must be translated into characters like <
    * It should NOT be used for raw character data, since & characters will
    * be interpreted incorrectly.
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      chardata = new CharData();
      //Console.println("XMLString, decode: " + valCode);
      chardata.readXML(valCode);
   }
 

   /**
    * reconstructs this XMLString using a XMLTokenizer.
    * The reason for overriding the readXML method from XMLBasicValue is the
    * handling of PCData. The tokenizer already handles escape sequences
    * like &lt;. Therefore, we must avoid calling decodeAttribute, as it would try
    * to expand, as soon as it sees & characters.
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) {
      try {
         if (! tokenizer.atSTag(getXMLTag())) {
             throw tokenizer.getXMLScanException
                ("Erroneous XML encoding, expected: " + getXMLTag() 
                  + ", encountered: " + tokenizer.getTagName()
                );
         }
         HashMap attrMap = tokenizer.getAttributes(); // will parse remainder of STag tail, if necessary
         chardata = new CharData((String) attrMap.get("val")); 
         tokenizer.takeSTag();
         tokenizer.takeETag();
         return this;
      }  catch (IOException e) { throw tokenizer.getXMLScanException("IOException in XMLTokenizer: " + e); }
   } 


   /**
    * returns the value as a String Object
    */
   public String unwrap() {
      return chardata.toString();
   }
 
   /**
    * The default is equivalent to getClass()
    * This method should be overwritten by extensions if the XMLStructure
    * is really a "wrapper" for a (non-XMLStructure) Java Object.
    *
   public Class wrappedClass() {
      return wrappedClass;
   }

   /**
    * Returns "XMLString".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public static final String XMLTAG = "XMLString";
   public static final String CLASSNAME = "parlevink.xml.XMLString";
   public static final String WRAPPEDCLASSNAME = "java.lang.String";
//   private static Class wrappedClass;
//   static {
//       try {
//         wrappedClass = Class.forName(WRAPPEDCLASSNAME);
//       } catch (Exception e) {}
//       //XML.addClass(XMLTAG, CLASSNAME);     
//   }

}
