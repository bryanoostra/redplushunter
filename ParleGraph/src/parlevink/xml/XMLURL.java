/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $
 */

// Last modification by: $Author: swartjes $
// $Log: XMLURL.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2005/10/20 09:54:57  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.1  2005/06/11 11:36:35  zwiers
// initial version
//
// Revision 1.1  2004/08/27 16:37:48  zwiers
// initial version
//
// Revision 1.1  2004/07/09 14:43:20  zwiers
// revision of X3D: integration with VisualObject loaders
//


package parlevink.xml;

import java.util.*;
import parlevink.util.*;
import parlevink.util.Console;

import java.io.*;
import java.net.*;


/** 
 * XMLURL is an auxiliary class that records url specifications in X3D style.
 * in particular, it can deal with a list of url specifications, rather than
 * just a single url. 
 */

public class XMLURL
{
   
   protected List<String> urlSpecs; // a List of Strings, used if more than one urlSpec is provided
   protected String urlSpec; // a single String, used when only one urlSpec is provided.
   protected URL url;
   
   public XMLURL() {
   }

   public XMLURL(String urls) {
       setURL(urls);  
   }

   
   /**
    * defines the URL specifications, but does not open any stream.
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
      String strippedUrls = urls.trim();
      if (strippedUrls.length() == 0) {
         urlSpecs = null;
         urlSpec = null;
         return;
      }
      char firstChar = strippedUrls.charAt(0);
      if (firstChar == '\"' || firstChar == '\'') {
         urlSpec = null;
         parseURLSpecs(strippedUrls);
         int listLen = urlSpecs.size();
         if (listLen == 0) {
            urlSpecs = null;
         } else if (listLen == 1) {
            urlSpec = (String) urlSpecs.get(0);
            urlSpec = null;
         } // else: keep urlSpecs list
            
         
      } else {
         this.urlSpec = strippedUrls;
         urlSpecs = null;
         //Console.println("single url");
      }
   }


   /*
    * assume that encoding is a list of urls, each embeded by " or ' chars.
    */ 
   private boolean parseURLSpecs(String encoding) {
      urlSpecs = new ArrayList<String>(4);
      int encLen = encoding.length();
      //Console.println("parseURLSpecs: " + encoding + ", length = " + encLen);
      char ch;       // current character, from encoding
      int chPos = 0; // position of current character ch, within encoding
      int startURL; // starting position of URL within encoding
      while (chPos < encLen) {         
         ch = encoding.charAt(chPos);   
         // skip white space
         while ((ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t' || ch == '\f') && ++chPos < encLen) {
            ch = encoding.charAt(chPos);
         }
         if (chPos >= encLen) return true;
         if (ch != '"' && ch != '\'') {
            Console.println("error in URL encoding: \'" + encoding + "\', url fields should start with a \' or \" character");
            Console.println("Found char \'" + ch + " at position " + chPos);
            return false; // error in encoding
         }
         ch = encoding.charAt(++chPos);
         startURL = chPos;
         while (ch != '"' && ch != '\'' && ++chPos < encLen) {
            ch = encoding.charAt(chPos);
         }
         if (ch != '"' && ch != '\'') {
            Console.println("error in URL encoding: \'" + encoding + "\', url fields should end with a \' or \" character");
            Console.println("Found char \'" + ch + " at position " + chPos);
            return false; // error in encoding
         }
         urlSpecs.add(encoding.substring(startURL, chPos));
         chPos++;        
      }          
      return true;
   }



   /**
    * returns the number of urls defined
    */
   public int size() {
      if (urlSpecs != null) {
         return urlSpecs.size();
      } else if (urlSpec !=null) {
         return 1;
      } else {
         return 0;
      }
   }

 
   /**
    * returns an Iterator for all defined urls.
    */
   public Iterator iterator() {
      if (urlSpecs != null) {
         return urlSpecs.iterator();
       } else {
         return new Iterator() {
            private boolean hasnext = (urlSpec != null);
            public boolean hasNext() {
               return hasnext;  
            }
            public Object next() {
               hasnext = false;
               return urlSpec;
            }
            public void remove() {
               throw new UnsupportedOperationException("remove op not supported for XMLURL Iterators");
            }
         };
      }  
   }

   
   /**
    * appends a String of attributes to buf.
    * Attributes: 
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      if (urlSpec != null) { 
         //Console.println("encode single url");
         buf.append(" url=\"");
         buf.append(urlSpec);
         buf.append('\"');
      } else if (urlSpecs != null) {
         //Console.println("encode  url list");
         Iterator iter = urlSpecs.iterator(); 
         StringBuffer bf = new StringBuffer();
         if (! iter.hasNext()) {
             //Console.println("empty url spec list");
             return buf;
         }
         String uspec = (String) iter.next();
         bf.append(" url=\'");
         bf.append('"');
         bf.append(uspec);
         bf.append('"');
         //Console.println("append " + uspec);
         while (iter.hasNext()) { 
            uspec = (String) iter.next();
           // Console.println("append " + uspec); 
            bf.append(' ');          
            bf.append('"');
            bf.append(uspec);
            bf.append('"');
         }
         bf.append('\'');
         buf.append(bf.toString());
      } else {
         //Console.println("empty url");  
      }
      return buf;
   }


 /**/


   /**
    * The XML Stag for XML encoding, and the class name
    */
   public static String XMLTAG = "URL";
   public static String CLASSNAME = "parlevink.xml.XMLURL";
 
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
