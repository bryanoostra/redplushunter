/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:25 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLElement.java,v $
// Revision 1.1  2006/05/24 09:00:25  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:04  zwiers
// no message
//

package parlevink.xml;


import java.util.*;
import java.io.*;
import parlevink.util.*;

/**
 * An XMLElement encapsulates a complete XML element of the form:
 * <tag attributes /> or <tag attributes> members </tag>
 * It does not handle PCData content,i.e, the content must consist of
 * other XML elements. 
 * The "default" tag name is "XMLElement". Classes that extend XMLElement
 * can easily change this, by assigning to xmlTag in the constructor, and by adding a (static)
 * registration of the class.
 */
public class XMLElement extends XMLStructureAdapter {
  
   private String xmlTag;      // tag name for STag and ETag
   private Map<String, String> attributes; // attribute value pairs
   private List<XMLStructure> elements; // nested XMLStructures
   
   public XMLElement(){
    // classes that extend XMLElement must set xmlTag in their constructor.
      xmlTag = "XMLElement";
   }
 
   public XMLElement(String xmlTag, Map<String, String> attributes, List<XMLStructure> elements){
     this.xmlTag=xmlTag;
     this.attributes=attributes;
     this.elements = elements;
   }
   
   public XMLElement(String xmlTag, Map<String, String> attributes){
     this.xmlTag=xmlTag;
     this.attributes=attributes;
     //elements = new ArrayList();
   }
   
   public XMLElement(String xmlTag){
     this.xmlTag=xmlTag;
     //elements = new ArrayList(); 
   }
    
   public String getTag() {
     return xmlTag;
   }
 
   public void setTag(String xmlTag) {
     this.xmlTag = xmlTag;
   }
 
   public Map getAttributes() {
     return attributes;
   }
 
   public void setAttributes(Map<String, String> attributes) {
     this.attributes = attributes;
   }
   
   public List getElements() {
     return elements;
   }
   
   public void setElements(List<XMLStructure> elements) {
     this.elements = elements;
   }
   
   public void addAttribute(String name, String value) {
     if (attributes == null) attributes = new HashMap<String, String>();
     attributes.put(name, value);
   }
   
   public void addElement(XMLStructure element){
    if (elements == null) elements = new ArrayList<XMLStructure>();
     elements.add(element);
   }
  
   /**
    * appends the value of this XMLElement to buf.
    */
   public StringBuffer appendXML(StringBuffer buf, int tab) {
      for (int i=0; i<tab; i++) buf.append(' ');
      buf.append('<');
      buf.append(xmlTag);
      if (attributes != null) {
         Iterator attrIter = attributes.entrySet().iterator();
         while (attrIter.hasNext()) {
            Map.Entry attr = (Map.Entry)attrIter.next();
            buf.append(' ');
            buf.append(attr.getKey());
            buf.append("=\"");
            buf.append(attr.getValue());
            buf.append('\"');
         }  
      }
      if (elements == null) {
         buf.append("/>");
      } else {
         buf.append('>');
         Iterator elemIter = elements.iterator();
         while(elemIter.hasNext()) {
            buf.append('\n');
            XMLStructure elem = (XMLStructure) elemIter.next();
            elem.appendXML(buf, tab + TAB);  
         }
         buf.append('\n');
         for (int i=0; i<tab; i++) buf.append(' ');
         buf.append("</");
         buf.append(xmlTag);
         buf.append('>');
      }
      return buf;
   }
  
   /**
    * reconstructs this XMLElement using an XMLTokenizer.
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) {
      if (attributes != null) attributes.clear();
      if (elements != null) elements.clear();
      try {
         if (! tokenizer.atSTag(xmlTag)) {
             throw new XMLScanException
                ("Erroneous XML encoding, expected: " + xmlTag 
                  + ", encountered: " + tokenizer.getTagName()
                );
         }
         HashMap attrMap = tokenizer.getAttributes(); // will parse remainder of STag tail, if necessary
         Iterator attrIter = attrMap.entrySet().iterator();
         while (attrIter.hasNext()) {
             Map.Entry attr = (Map.Entry) attrIter.next();
             decodeAttribute((String)attr.getKey(), (String)attr.getValue(), tokenizer); 
         }
         tokenizer.takeSTag();
         while (tokenizer.atSTag()) {
             String tag = tokenizer.getTagName();
             //Console.println("create " + tag);
             XMLStructure xmlElem = XML.createXMLStructure(tokenizer); 
             addElement(xmlElem); 
         }
         tokenizer.takeETag(xmlTag);
         return this;
      }  catch (IOException e) { throw new XMLScanException("IOException in XMLTokenizer: " + e); }
   } 

   /**
    * decodes the value from an attribute value String
    * Can be overwriten by extensions
    */
   public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer) {
      addAttribute(attrName, attrValue);
      return true;
   }

   /**
    * test for equality of xmlTag, attributes, and content elements.
    */
   public boolean equals(Object xmlElement) {
       XMLElement xmle = (XMLElement) xmlElement;
       // test for equality of xmlTags:
       if ( ! (xmlTag.equals(xmle.xmlTag)) ) return false;
       // test for equality of attributes:
       if (attributes != null) {
          if (xmle.attributes == null ) return false; 
          if ( ! (attributes.equals(xmle.attributes))  ) return false;
       } else { // attributes == null
          if (xmle.attributes != null) return false; 
       }
       // test for equality of content elements:
       if (elements != null) {
          if (xmle.elements == null ) return false;
          /*
          Iterator iter = elements.iterator();
          Iterator xmleiter = xmle.elements.iterator();
          while (iter.hasNext()) {
            //Console.println("test element");
              if ( ! (iter.next().equals(xmleiter.next())) ) return false;             
          }
          */
          if ( ! (elements.equals(xmle.elements)) ) return false;
       } else { // elements == null
           if (xmle.elements != null) return false;
       }     
       return true;  
   }

   /**
    * hashCode, consistent with equals,
    * based upon xmlTag, attributes, and elements.
    */
   public int hashCode() {
       int result = xmlTag.hashCode();
       // test for equality of xmlTags:
       if (attributes != null) { 
           result += attributes.hashCode();
       } 
       if (elements != null) {
           result += elements.hashCode();
       }
       return result;
   }


   public static final int TAB = 3;

   //
   // classes that extend XMLElement must add a similar static block,
   // linking their "tag" to the fully qualified class name
//   static {
//       XML.addClass("XMLElement", "parlevink.xml.XMLElement");     
//   }

}