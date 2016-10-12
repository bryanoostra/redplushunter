/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $
 * @since version 0
 */

// Last modification by: $Author: swartjes $
// $Log: AttributeMap.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.5  2005/10/20 10:05:49  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.4  2004/07/05 08:22:32  zwiers
// XML encoding improved
//
// Revision 1.3  2004/05/17 14:53:17  zwiers
// getXMLTag added
//
// Revision 1.2  2003/11/27 15:38:35  zwiers
// wrapping/unwrapping added
//
// Revision 1.1  2002/10/21 09:56:19  zwiers
// moved, from agents package to util package
//

package parlevink.util;

import java.io.*;
import parlevink.util.*;
import java.util.*;
import parlevink.xml.*;


/**
 * parlevink.util.AttributeMap is like java.util.properties, except that
 * it is not limited to String-typed keys and values, and it is based on
 * HashMaps, rather than Hashtables.
 * Keys take the form of Id's or Strings, although internally Id's are used.
 * For storing/retrieving Object-typed values, the prime methods are 
 * setAttribute(id, value) and getAttribute(id).
 * When values are known to be Strings, the preferred methods are setString(id, string)
 * and getString(id). In all cases, "id" can be an Id, or it can be a String.
 * 
 */
public class AttributeMap implements XMLStructure {
   
 
   /**
    * creates a new, empty set of attributes.
    */
   public AttributeMap() {
      attr = new LinkedHashMap<Id, Object>();  
   }

   /**
    * creates a new, empty set of attributes.
    * The initial size of the internal Hasmap is specified
    * by means of "initialCapacity"
    */
   public AttributeMap(int initialCapacity) {
      attr = new LinkedHashMap<Id, Object>(initialCapacity);  
   }

   /**
    * creates a new, empty set of attributes.
    */
   public AttributeMap(AttributeMap attrmap) {
      attr = attrmap.attr;  
   }

   /**
    * sets the value of the attribute identified by Id  "key".
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object setAttribute(Id key, Object value) {
      return attr.put(key, value);      
   }


   /**
    * sets the value of the attribute identified by the Id for 
    * the String "key".
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object setAttribute(String key, Object value) {
      return attr.put(Id.forName(key), value);
   }

   /**
    * retrieves the (Object-typed) value of the attribute identified by key.
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object getAttribute(Id key) {
      return attr.get(key);
   }

   /**
    * retrieves the (Object-typed) value of the attribute identified by key.
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object getAttribute(String key) {
      return attr.get(Id.forName(key));
   }

   /**
    * sets the String-typed value of the attribute identified by "key".
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object setString(Id key, String value) {
      return  attr.put(key, value);      
   }

   /**
    * sets the String-typed value of the attribute identified by "key".
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public Object setString(String key, String value) {
      return  attr.put(Id.forName(key), value);      
   }

   /**
    * retrieves the String-typed value of the attribute identified by key.
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public String getString(Id key) {
      return (String) (attr.get(key));
   }

   /**
    * retrieves the String-typed value of the property identified by key.
    * Returns the previously bound value for "key", or null,
    * if "key" was not bound. 
    */
   public String getString(String key) {
      return (String) (attr.get(Id.forName(key)));
   }

   /**
    * checks whether an attribute for "key" has been defined.
    */
   public boolean hasAttribute(Id key) {
      return attr.get(key) != null;
   }


   /**
    * checks whether an attribute for "key" has been defined.
    */
   public boolean hasAttribute(String key) {
      return attr.get(Id.forName(key)) != null;
   }

  /**
   * removes the property identified by key.
   */
 // public void removeProperty(String key);

  /**
   * Returns an Iterator for all the property attribute keys.
   */
  public Iterator attributeNames() {
     return attr.keySet().iterator();
  }

  /**
   * Returns an Iterator for all the attribute values.
   */
  public Iterator attributeValues() {
     return attr.values().iterator();
  }


   protected Iterator iterator() {
      return attr.entrySet().iterator();
   }

//----------------------
   /**
    * returns this AttributeMap, i.e. no unwrapping is necessary
    */
   public Object unwrap() {
      return this;
   }


   /**
    * return this Class
    */
   public Class wrappedClass() {
      return wrappedClass;
   }


   private void appendSpace(StringBuffer buf, int nsp) {
      for (int i=0; i<nsp; i++) buf.append(' ');
   }

   /*
    * encodes the AttributeMap as an XML string
    */
   private void encode(int tab) {
      clearBuf(100);
      appendSpace(buf, tab);
      buf.append("<");
      buf.append(getXMLTag());
      buf.append(">");
      Iterator iter = iterator(); // iterator for Map Entries
      while (iter.hasNext()) {
         Map.Entry elem = (Map.Entry) iter.next();
         String idStr =  elem.getKey().toString(); // Id String value
         buf.append('\n');
         appendSpace(buf, tab+TAB);
         buf.append("<Attribute id=\"");
         buf.append(idStr);
         buf.append('"');
         Object attrValue = elem.getValue();        
         if (  attrValue instanceof java.lang.String) {
             buf.append(" String=\"");
             buf.append(attrValue);
             buf.append("\"/>"); 
         } else if ( attrValue instanceof Integer) {
             buf.append(" Integer=\"");
             buf.append(attrValue);
             buf.append("\"/>");             
         } else if ( attrValue instanceof Long) {
             buf.append(" Long=\"");
             buf.append(attrValue);
             buf.append("\"/>");    
         } else if ( attrValue instanceof Float) {
             buf.append(" Float=\"");
             buf.append(attrValue);
             buf.append("\"/>");  
         } else if ( attrValue instanceof Double) {
             buf.append(" Double=\"");
             buf.append(attrValue);
             buf.append("\"/>");  
         } else if ( attrValue instanceof Boolean) {
             buf.append(" Boolean=\"");
             buf.append(attrValue);
             buf.append("\"/>");  
         } else {  
             buf.append(">\n");           
             XMLStructure xmlVal = XML.wrap(attrValue);                    
             xmlVal.appendXML(buf, tab+2*TAB);
             buf.append('\n');
             appendSpace(buf, tab+TAB);
             buf.append("</Attribute>");
         }
      }
      buf.append('\n');
      appendSpace(buf, tab);
      buf.append("</");
      buf.append(getXMLTag());
      buf.append(">");
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
      attr.clear();
      try {
         tokenizer.takeSTag(getXMLTag());
         while (tokenizer.atSTag()) {
            Map xmlAttributes = tokenizer.getAttributes();
//            String idStr = (String) xmlAttributes.get("id");
//            if (idStr == null) {
//                  throw new XMLScanException("\"id\" attribute missing in Attribute tag");
//            }
//            Id id = Id.forName(idStr);
            Iterator iter = xmlAttributes.entrySet().iterator();
            String idStr = null;
            Object value = null;
            while (iter.hasNext() ) {
                Map.Entry e = (Map.Entry) iter.next();  
                String as = (String) e.getKey();
                String stringVal = (String) e.getValue();
                if (as.equals("id")) {
                    idStr = stringVal;                                    
                } else if (as.equals("String") || as.equals("value")) {
                    value = stringVal;
                } else if (as.equals("Integer") || as.equals("int")) {
                    value = Integer.valueOf(stringVal); 
                } else if (as.equals("Long") || as.equals("long")) {
                    value = Long.valueOf(stringVal); 
                } else if (as.equals("Float") || as.equals("float")) {
                    value = Float.valueOf(stringVal); 
                } else if (as.equals("Double") || as.equals("double")) {
                    value = Double.valueOf(stringVal); 
                } else if (as.equals("Boolean") || as.equals("boolean") || as.equals("bool")) {
                    value = Boolean.valueOf(stringVal); 
                }
            }
            tokenizer.takeSTag();
            if (idStr == null) {
                  throw new XMLScanException("\"id\" attribute missing in Attribute tag");
            }
            Id id = Id.forName(idStr);
            if (value == null) {
                XMLStructure xmlAttr = XML.createXMLStructure(tokenizer);
                if (xmlAttr instanceof XMLWrapper) {
                   value = ((XMLWrapper)xmlAttr).unwrap();
                } else {
                   value = xmlAttr; 
                }
            }
            tokenizer.takeETag("Attribute"); 
            attr.put(id, value);
         }
         tokenizer.takeETag(getXMLTag()); // </AttributeMap>
      }  catch (IOException e) { throw new XMLScanException("IOException in XMLTokenizer: " + e); }
         return this;
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

   
   public boolean equals(Object attributeMap) {
       LinkedHashMap attr2 = ((AttributeMap) attributeMap).attr;
       Set s1 = attr.keySet();
       Set s2 = attr2.keySet();
       return attr.equals(attr2);
   }


   public int hashCode() {
      return attr.hashCode();
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
   * returns the XML tag that is used to encode this type of XMLStructure.
   * The default returns null.
   */
  public String getXMLTag() {
     return XMLTag; 
  }


   private LinkedHashMap<Id, Object> attr;
   
   
   /**
    * the (initial) length of the StringBuffer used for readXML(in).
    */
   public static int BUFLEN = 40;

   private String encoding;
   private StringBuffer buf;
   public String xmlTag;
   private int curtab;
   public static final int TAB = 3;
   
   public static final String ATTRIBUTEMAPTAG = "AttributeMap";
   public static String XMLTag = ATTRIBUTEMAPTAG;
   public static final String CLASSNAME = "parlevink.util.AttributeMap";
   public static final String WRAPPEDCLASSNAME = "parlevink.util.AttributeMap";
   private static Class wrappedClass;
   static {
       try {
         wrappedClass = Class.forName(WRAPPEDCLASSNAME);
       } catch (Exception e) {}
       XML.addClass(ATTRIBUTEMAPTAG, CLASSNAME);     
   }   

   
}
