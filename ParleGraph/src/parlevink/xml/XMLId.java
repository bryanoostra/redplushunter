/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLId.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:09  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:04  zwiers
// no message
//

package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import java.util.*;

/**
 * XMLId is in essence a wrapper around a parlevink.util.Id Object
 * that turns these Ids  effectively into an XMLizable objects.
 * It is assumed that Id's do not contain " characters.
 */
public class XMLId extends XMLBasicValue<Id> implements XMLWrapper<Id>
{

   private String idString;

   /**
    * creates a new XMLId with undefined contents. 
    */
   public XMLId() {
   }


   /**
    * creates a new XMLId with specified value.
    */
   public XMLId(String idString) {
      this.idString = idString;
   }

   /**
    * creates a new XMLId with specified value.
    */
   public XMLId(Id id) {
      this.idString = id.toString();
   }

   /**
    * creates a new XMLId with specified value.
    */
   public XMLId(XMLId xmlid) {
      this.idString = xmlid.idString;
   }


   public Id getId() {
      return Id.forName(idString);
   }

   /**
    * returns the String representation of the Id.
    */
   public String toString() {
      return idString;
   }

  /**
   * standard equality test.
   */
   public boolean equals(Object XMLId) {
      return (((XMLId) XMLId).idString.equals(idString));
   }

   /**
    * calculates the hash code 
    * consistent with "equals". i.e "equal" objects get the same hash code.
    */
    public int hashCode() {
      return idString.hashCode();
    }


   /**
    * yields an XML encoded String of this XMLIzable object. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString() {
      return toXMLString(0);
   } 

   /**
    * yields an XML encoding. 
    * The readXML() methods are able to reconstruct this object from 
    * the String delivered by toXMLString().
    */ 
   public String toXMLString(int tab) {
      StringBuffer buf = new StringBuffer();
       for (int i=0; i<tab; i++) buf.append(' ');  
       buf.append("<XMLId id=\"");
       buf.append(idString);
       buf.append("\"/>");
       return buf.toString();      
   } 

   
   /**
    * appends the XML encoding for an Id, in the form of an XMLId element
    */
   public static void appendXML(Id id, StringBuffer buf, int tab) {
      appendXML(id.toString(), buf, tab);
   }

   /**
    * appends the XML encoding for an Id, in the form of an XMLId element
    */
   public static void appendXML(String idString, StringBuffer buf, int tab) {
       for (int i=0; i<tab; i++) buf.append(' ');  
       buf.append("<XMLId id=\"");
       buf.append(idString);
       buf.append("\"/>");
   }

   /**
    * appends the value of this XMLBasicValue to buf.
    * relies on the String as delivered by toXMLString(0)
    */
   public StringBuffer  appendXML(StringBuffer buf) {
      appendXML(idString, buf, 0);
      return buf;
   }

   /**
    * appends the value of this XMLBasicValue to buf.
    * relies on the String as delivered by toXMLString(tab)
    */
   public StringBuffer  appendXML(StringBuffer buf, int tab) {
      appendXML(idString, buf, tab);
      return buf;
   }

   /**
    * returns a String that can be used as XML attribute value.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      return appendAttribute(buf, "id", idString);
   }

   /**
    * decodes the value from an attribute value String
    */
   public void decodeAttribute(String attrName, String valCode, XMLTokenizer tokenizer) {
      idString = valCode;
   }
 
 
   /**
    * returns the Id Object that corresponds to this XMLId object.
    */
   public Id unwrap() {
      return Id.forName(idString);
   } 
 

   /**
    * Returns "XMLId".
    */
   public String getXMLTag() {
      return XMLTAG;
   }


   public static Id createIdFromXML(XMLTokenizer tok) {
      XMLId xid = new XMLId();
      xid.readXML(tok);
      return xid.getId();
   }
     
   public static Id createIdFromXML(String xmlEncoding) {
      XMLId xid = new XMLId();
      xid.readXML(xmlEncoding);
      return xid.getId();
   }

   public static final String XMLTAG = "XMLId";
   public static final String CLASSNAME = "parlevink.xml.XMLId";
   public static final String WRAPPEDCLASSNAME = "parlevink.util.Id";

}
