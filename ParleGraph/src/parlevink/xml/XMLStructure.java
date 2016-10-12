/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLStructure.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.10  2005/10/20 09:54:57  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//



package parlevink.xml;

import java.io.*;

/**
 * XMLStructure is an extension of XMLizable, that makes some extra assumptions, 
 * and also requires a few more methods.
 * The most important assumption is that XMLStructures have an XMLEncoding
 * that consists of proper XML (unlike XMLizable, which does not require this).
 * In particular, it is required that "CharData" contents do not contain
 * forbidden characters, like '<'.
 * Moreover, the XML encoding should start with an opening STag of the
 * form "<ClassSpec", where "ClassSpec is an identifier that identifies the
 * Class of the implementation. This Class specification must be registered
 * with the XMLObject class, by calling one of its addXMLizableClass() method,
 * preferably in a static init block of the XMLStructure class.
 * These assumptions guarantee that an XMLStructure Object can be created by means
 * of the readXML method.
 * The methods required by this interface, including methods required by XMLizable are:
 *
 * public void readXML(Reader) throws IOException
 * public void readXML(String)
 * public void readXML(XMLTokenizer) throws IOException
 * public void writeXML(PrintWriter)
 * public void writeXML(PrintWriter, int tab)
 * public StringBuffer appendXML(StringBuffer buf);
 * public StringBuffer appendXML(StringBuffer buf, int tab);
 * public String toXMLString()
 * public String toXMLString(int tab)
 * public Object unwrap();
 * public Class wrappedClass();
 * public String getXMLTag();
 *
 * The readXML(XMLTokenizer) implementation is optional; the method must be
 * available, but is allowed to throw an UnsupportedOperationException.
 * The other methods must be properly implemented, and must be consistent.
 * That is, XML formatted Strings, as written by writeXML, or produced by toXMLSTring()
 * must be accepted by the readXML() methods and must lead to reconstruction of
 * the original XMLizable object.
 */

public interface XMLStructure
{
   /**
    * reconstructs this XMLizable object by reading and parsing XML encoded text from a Reader.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(Reader in) throws IOException; 
   
   /**
    * reconstructs this XMLizable object by parsing an XML encoded String s.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(String s); 
   
   /**
    * reconstructs this XMLizable object by parsing a stream of XML tokens,
    * that are delivered by a XMLTokenizer.
    * This method need not be supported, in which case the method should throw
    * a java.lang.UnsupportedOperationException.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) throws IOException; 

 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods should be able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString(); 
   

   /**
    * yields an XML encoded String of this XMLizable object. 
    * The readXML() methods should be able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString(int tab); 
   
   /**
    * like writeXML(PrintWriter), except that the XML encoding is appended
    * to a StringBuffer.
    * the latter must be returned.
    */
   public StringBuffer appendXML(StringBuffer buf);

   /**
    * like writeXML(PrintWriter ,int), except that the XML encoding is appended
    * to a StringBuffer.
    * The latter must be returned.
    */   
   public StringBuffer appendXML(StringBuffer buf, int tab);
   
   /**
    * writes an XML encoded String to "out".
    * The int "tab" can be used as a hint for indentation, and
    * denotes the indentation to be applied to the XML code
    * as a whole. 
    * This String should equal the result of toXMLString(tab).
    */
   public void writeXML(PrintWriter out, int tab); 
  
   /**
    * writes an XML encoded String to "out".
    * This String should equal the result of toXMLString().
    */
   public void writeXML(PrintWriter out); 
  
   /**
    * returns the XML tag that is used to encode this type of XMLStructure.
    */
   public String getXMLTag();
  
  /**
   * Many XMLStructure Objects are really "wrappers" for other Objects.
   * Considering an XMLStructure as a "wrapper" for some Object,
   * the unwrap method yields this original Object.
   * For XMLStructures that are not considered "wrappers", the
   * unwrap method must simply return the XMLStructure Object itself.
   */
  //public T unwrap();
  
  /**
   * returns the Class of the wrapped Object.
   * For XMLStructures that are not considered "wrappers", 
   * the result equals getClass();
   */ 
  //public Class wrappedClass();

}
