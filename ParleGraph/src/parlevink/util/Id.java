/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Id.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.9  2005/10/20 10:06:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.8  2004/07/05 08:22:13  zwiers
// XML encoding of Ids via XMLId
//
// Revision 1.7  2004/05/17 14:53:36  zwiers
// IndexedElements added
//
// Revision 1.6  2003/11/27 16:05:49  zwiers
// comment update
//
// Revision 1.5  2003/11/27 15:39:40  zwiers



package parlevink.util;
import java.util.*;
import java.io.*;
//import parlevink.xml.*;


/**
 * Id's are objects that uniquely characterize objects.
 * Id's can only be obtained via the static forName() method. 
 * This method ensures that the Id object for a given String is unique.
 * As a consequence  one may assume that Id's can be compared for equality by means
 * of the "==" test.
 * This results in very efficient equality testing for Id's, even when these are based
 * on (long) Strings like URI's etc.
 * Id's are not XMLizable themselves: because Id's are immutable, it is impossible
 * to implement appropriate readXML methods.
 * However, the xml.XMLId class acts as a wrapper class for Id, and can encode and decode
 * Id's. In order to reconstruct an Id, one must first decode the corresponding XMLId object,
 * and then uwrap the latter. (calling the XMLId.unwrap() method yield an Id object)
 */
 
public final class Id 
implements IndexedElement
{


//   public Id() {
//   }

   /*
    * The private constructor for Id's.
    * It can be called only via the public static getId method.
    */
   private Id(String idString) {
      this.idString = idString;
      hash = idString.hashCode();
   }


   /**
    * method that returns ``this'' Id itself;
    * This method is needed to implement the IndexedElement interface,
    * and allows Id's to be elements of IndexedSets.
    */
   public final Id getId() {
      return this;
   }

   /**
    * The String representation of this Id object.
    * Note that this String uniquely characterizes the Id object.
    */
   public final String toString() {
      return idString;
   }

   /**
    * The XML encoding, in the form of an XMLId
    * (For full XMLStructure capabilities, use xml.XMLId)
    */
   public final String toXMLString() {
      return "<XMLId id=\"" + idString + "\"/>";  
   }


   

   /**
    * equals for Id is automatically based upon "==" testing
    */

   /**
    * optimized hashCode, uses cached hash code of the id String
    */
   public final int hashCode() {
      return hash;
   }

   /**
    * Real cloning an Id is not permitted, as it would violate
    * the assumption that Id's are unique objects.
    * The Id.clone() operation still allows one to call clone()
    * for Id Objects, but simply returns the Id object itself.
    */ 
   public final Object clone() {
      return  this ; 
   }


   /**
    * returns the Id object for idString. Repeated calls for the same String
    * will return the same object.
    */
   public final static synchronized Id forName(String idString) {
      if (idString == null) return null;
      Id id =  ids.get(idString);
      if (id == null)  {
         id = new Id(idString);
         ids.put(idString, id); 
      }
      return id;      
   }


   /**
    * Equivalent to Id.forName. returns the Id object for idString. Repeated calls for the same String
    * will return the same object.
    */
   public final static synchronized Id getId(String idString) {
      return forName(idString);
   }

   /* instance attributes: */
   private String idString;
   private int hash;

   /* class attributes: */
   public static final int ID_HASHMAP_SIZE = 200;
   private static Map<String, Id> ids = new HashMap<String, Id>(ID_HASHMAP_SIZE); /* The collection of all allocated Id's. */  
   public static final Id Text = Id.forName("Text");   



}