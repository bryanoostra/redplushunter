/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $
 */

// Last modification by: $Author: swartjes $
// $Log: XMLInline.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//

package parlevink.xml;

import java.util.*;
import parlevink.util.*;
import parlevink.util.Console;

import java.io.*;
import java.net.*;


/** 
 * XMLInline is an XML element that (temporarily) switches 
 * to a different Reader, specified by means of a (list of) URL(s).
 * The first URL that can be opened for reading will be used.
 * If no URL succeeds, a message is printed.
 * The current tokenizer state is pushed on a stack, so
 * settings made while processing the inlined XML has no
 * effect on the context of the <XMLInline> element.
 * The EndOfDocument, expected at the end of the inlined
 * XML, is silently removed, by setting the popOnEndOfDocument
 * attribute to true. So, in general, the effect is as if the
 * inlined contents is physically inserted within the current 
 * input, like a C style include file.
 */

public class XMLInline extends XMLStructureAdapter {
   
   protected XMLURL urlList;
   
   public XMLInline() {
      super(); 
      urlList = new XMLURL();
   }
   

   /**
    * defines the URL specifications, but does not load the file.
    * The URL(s) are relative to the base url.
    * urlSpec can be a single String, not containing &apos;
    * or &quot; characters, and in this case, it defines a single url.
    * As an alternative it can define several urls, each enclosed
    * by &quot; or &apos; characters and separated by white space. 
    * (according to X3D only &quot; characters must be used for the separate URLs,
    * and the XML attribute as a whole should be enclosed in single &apos;
    * characters; the latter have been stripped away already from the urlSpecs
    * argument.)
    * 
    */
   public void setURL(String urls) {
      urlList.setURL(urls);
   }
   
   /**
    *
    */
   public boolean switchReader(XMLTokenizer tokenizer) {
      if (urlList.size() == 0) return false;
      Iterator iter = urlList.iterator();
      while (iter.hasNext()) {
         String usp = (String) iter.next();
         try {
           tokenizer.pushReader(usp);
           tokenizer.setpopOnEndOfDocument(true); // will be restored on pop
           return true;
         } catch (IOException ie) {
             // rather normal case, when multiple URL specs are being used.
             // just try the next  
         }
      }
      Console.print("No file found for URL(s): ");
      iter = urlList.iterator();
      while (iter.hasNext()) {
         Console.print("  " + (String) iter.next());
      }
      Console.println();
      return false;
   }

   /**
    * executed after all the ETag has been read.
    */
   public void postProcess(XMLTokenizer tokenizer) {
      boolean switched = switchReader(tokenizer);
      if (!switched) {
         Console.println("XMLInline: Could not switch input stream");
      } else {
         //Console.println("switched");  
      }
   }
  
   /**
    * decodes the content of the inlined URL
    */
   public void decodeContent(XMLTokenizer tokenizer) throws IOException {

   }

   /**
    * appends a String of Group attributes to buf.
    * Attributes: 
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      super.appendAttributeString(buf);
      return buf;
   }

   /**
    * decodes a single attribute, as encoded by appendAttributeString()
    */
   public boolean decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      if (attrName.equals("url")) {
          String urlspec = valCode.trim();
          if (urlspec.length() == 0) {
            Console.println("Empty URL at line " + tokenizer.getLine());
          } else {
            setURL(urlspec); 
         }          
          return true;
      } else {
         return super.decodeAttribute(attrName, valCode, tokenizer);
      }  
   } 
 


   /**
    * The XML Stag for XML encoding, and the class name
    */
   public static String XMLTAG = "XMLInline";
   public static String CLASSNAME = "parlevink.xml.XMLInline";
 
   /**
    * returns the XML Stag for XML encoding
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   // used to trigger class loading.
   public static void registerXML() {};

   static {
      XML.addClass(XMLTAG, CLASSNAME); 
   }      
}
