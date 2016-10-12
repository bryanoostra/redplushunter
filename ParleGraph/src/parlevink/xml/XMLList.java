/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLList.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:09  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.13  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import java.util.*;

/**
 * Defines an XMLizable list of other XMLizable objects.
 * 
 */
public class XMLList implements XMLWrapper<ArrayList<?>>
{

   /**
    * creates a new XMLList
    */
   public XMLList() {   
      list = new ArrayList<XMLStructure>();
   }

 
   /**
    * creates a new XMLList
    */
   public XMLList(Collection<?> col) {   
      this();
      for (Object obj:col) {
         list.add(XML.wrap(obj));
      }
   }

   /**
    * creates a new XMLList
    */
   public XMLList(Object[] objList) {   
      this();
      for (int i=0; i<objList.length; ++i) {
         list.add(XML.wrap(objList[i]));
      }
   }

   /**
    * creates a new XMLList
    */
   public XMLList(int[] intList) {   
      this();
      for (int i=0; i<intList.length; ++i) {
         list.add(XML.wrap(intList[i]));
      }
   } 
 
   /**
    * creates a new XMLList
    */
   public XMLList(double[] doubleList) {   
      this();
      for (int i=0; i<doubleList.length; ++i) {
         list.add(XML.wrap(doubleList[i]));
      }
   }

   /**
    * creates a new XMLList
    */
   public XMLList(String[] stringList) {   
      this();
      for (int i=0; i<stringList.length; ++i) {
         list.add(XML.wrap(stringList[i]));
      }
   }
  

   /**
    * 
    */
   public int size() {
      return list.size();
   }

   /**
    *
    */
   public Object get(int i) {
      Object li = list.get(i);
      if (li instanceof XMLWrapper) {
         return ((XMLWrapper)li).unwrap(); 
      } else {
         return li;  
      }
   }


   /**
    *
    */
   public void set(int i, Object obj) {
      list.set(i, XML.wrap(obj));  
   }

   /**
    *
    */
   public XMLList(Object e0, Object e1) {
      this();
      list.add(XML.wrap(e0));  
      list.add(XML.wrap(e1));
   }
   
   /**
    *
    */
   public XMLList(Object e0, Object e1, Object e2) {
      this();
      list.add(XML.wrap(e0));  
      list.add(XML.wrap(e1));
      list.add(XML.wrap(e2));
   }

   /**
    *
    */
   public XMLList(Object e0, Object e1, Object e2, Object e3) {
      this();
      list.add(XML.wrap(e0));  
      list.add(XML.wrap(e1));
      list.add(XML.wrap(e2));
      list.add(XML.wrap(e3));
   }
 
   /**
    * adds an XMLStructure Object to the list
    */
   public boolean add(Object xmlElem) {
//      if ( ! ( xmlElem instanceof XMLStructure)) {
//          throw new IllegalArgumentException("XMLList.add() requires XMLStructure argument");  
//      }
       return list.add(XML.wrap(xmlElem));  
   }

   /**
    * adds an XMLStructure Object to the list
    */
   public void add(int index, Object xmlElem) {
//      if ( ! ( xmlElem instanceof XMLStructure)) {
//          throw new IllegalArgumentException("XMLList.add() requires XMLStructure argument");  
//      }
      list.add(index, XML.wrap(xmlElem));  
   }


   public boolean addAll(Collection col) {
       Iterator iter = col.iterator();
       boolean result = true;
       while (iter.hasNext()) {
          result = result && add(iter.next());
       }  
       return result;
   }

//   /**
//    * adds an XMLStructure Object to the list
//    */
//   public Object set(int index, Object xmlElem) {
//      if ( ! ( xmlElem instanceof XMLStructure)) {
//          throw new IllegalArgumentException("XMLList.add() requires XMLStructure argument");  
//      }
//      return super.set(index, XML.wrap(xmlElem));  
//   }
//
//
   /**
    * adds a wrapped integer to the list
    */
   public boolean add(int i) {
       return list.add(new XMLInteger(i));  
   }

   /**
    * adds a wrapped double to the list
    */
   public boolean add(double d) {
       return list.add(new XMLDouble(d));  
   }

   /**
    * adds a wrapped String to the list
    */
   public boolean add(String s) {
       return list.add(new XMLString(s));  
   }

   public boolean equals(Object xmlList) {
      if (xmlList instanceof XMLList) {
         return ((XMLList)xmlList).list.equals(list);
      } else {
         return false;
      }  
   }



   /**
    * returns the value as an unwrapped List Object,
    * where the List elements are unwrapped themselves, if necessary
    */
   public ArrayList<Object> unwrap() {
      ArrayList<Object> result = new ArrayList<Object>(list.size());
      Iterator<XMLStructure> iter = list.iterator();
      while (iter.hasNext()) {
         Object nxt = iter.next();
         if (nxt instanceof XMLWrapper) {
            result.add( ((XMLWrapper)nxt).unwrap() );
         } else {
            result.add(nxt);  
         }
          //result.add( ((XMLStructure)iter.next()).unwrap());
      }
      return result;
   }


   /**/
   private void appendSpace(StringBuffer buf, int nsp) {
      for (int i=0; i<nsp; i++) buf.append(' ');
   }


  /**
   * returns the String "XMLList"
   */
  public String getXMLTag() {
   if (list.size() > 0) {
      String ElementTag = list.get(0).getXMLTag();  
   }
     return "XMLList"; 
  }

   /*
    * encodes the List as an XML string
    */
   private void encode(int tab) {
      //if (encoding != null && curtab == tab) return;
      curtab = tab;
      clearBuf(100);
      appendSpace(buf, tab);
      buf.append("<XMLList>");
      Iterator<XMLStructure> iter = list.iterator();
      while (iter.hasNext()) {
          XMLStructure elem = iter.next();
          buf.append('\n');
          elem.appendXML(buf, tab+TAB);   
      }
      buf.append('\n');
      appendSpace(buf, tab);
      buf.append("</XMLList>");
      encoding = buf.toString();
   }

   /**
    * reconstructs this XMLLIst object by parsing an XML encoded String s
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(String s) {
      return readXML(new XMLTokenizer(s));
   } 



   /**
    * reconstructs this XMLList object by reading and parsing XML encoded data
    * Data is read until the end of data is reached,
    * or until a '<' character is read. 
    * This method can throw IOExceptions.
    */
   public XMLStructure readXML(Reader in) throws IOException {
      /*
      clearBuf(BUFLEN);   
      int ci = in.read(); //  '<' character of "<XMLInteger tag
      buf.append((char)ci); 
      ci = in.read();
      while (ci != -1 && ci != '<') {
      	 buf.append((char)ci);
      	 ci = in.read();
      }   
      encoding = buf.toString();
      //Console.println("readXML, encoding = \"" + encoding + "\"");
      decode();
      */
      return readXML(new XMLTokenizer(in));
   } 


   /**
    * reconstructs this XMLList using a XMLTokenizer.
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) {
      //Console.println("XMLList.readXML(Tokenizer)");
      list.clear();
      try {
         tokenizer.takeSTag("XMLList");
         while (tokenizer.atSTag()) {
             String tag = tokenizer.getTagName();
             //Console.println("create " + tag);
             //XMLStructure<E> xmlElem = (XMLStructure<E>) XML.createXMLStructure(tokenizer);
             XMLStructure xmlElem =  XML.createXMLStructure(tokenizer);  
             
             list.add(xmlElem); 
         }
         tokenizer.takeETag("XMLList");
         return this;
      }  catch (IOException e) { throw tokenizer.getXMLScanException("IOException in XMLTokenizer: " + e); }
   } 

   
   /**
    * writes the value of this XMLList to out.
    */
   public void writeXML(PrintWriter out) {
      encode(0);
      out.write(encoding);
   }

   /**
    * writes the value of this XMLList to out.
    */
   public void writeXML(PrintWriter out, int tab) {
      encode(tab);
      out.write(encoding);
   }

   /**
    * appends the value of this XMLList to buf.
    */
   public StringBuffer  appendXML(StringBuffer buf) {
      encode(0);
      buf.append(encoding);
      return buf;
   }

   /**
    * appends the value of this XMLList to buf.
    */
   public StringBuffer  appendXML(StringBuffer buf, int tab) {
      encode(tab);
      buf.append(encoding);
      return buf;
   }


   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString() {
      encode(0);
      return encoding;
   } 

   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString(int tab) {
      encode(tab);
      return encoding;
   } 

   public String toString() {
      return toXMLString();
   }


   /*
    * allocates a new StringBuffer , if necessary, and else deletes
    * all data in the buffer
    */
   private final void clearBuf(int len) {
      if (buf == null) buf = new StringBuffer(len);
      else buf.delete(0, buf.length());
   }


   /**
    * the (initial) length of the StringBuffer used for readXML(in).
    */
   public static int BUFLEN = 40;

   private String encoding;
   private StringBuffer buf;
   private ArrayList<XMLStructure> list;
   private int curtab;
   public static final int TAB = 3;
   
   public static final String XMLLISTTAG = "XMLList";
   public static final String XMLLISTCLASSNAME = "parlevink.xml.XMLList";
   public static final String WRAPPEDCLASSNAME = "java.util.ArrayList";

  
}