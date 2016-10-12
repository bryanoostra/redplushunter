/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $    
 * @since version 0       
 */
// Last modification by: $Author: swartjes $
// $Log: IndexedSet.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.6  2005/10/20 10:07:14  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.5  2005/03/02 16:47:30  zwiers
// comment update
//
// Revision 1.4  2004/07/09 11:25:57  zwiers
// tokenizer parameter added for decodeAttribute
//
// Revision 1.3  2004/07/05 08:22:05  zwiers
// XML encoding of Ids via XMLId
//
// Revision 1.2  2004/05/19 13:59:14  zwiers
// removed Console.println..
//
// Revision 1.1  2004/05/17 14:54:13  zwiers
// initial version
//


package parlevink.util;
import java.util.*;
import parlevink.xml.*;

/*
 * A IndexedSet is an implementation of Set that assumes that its elements
 * have a unique Id that can be used as an index for fast retrieval from the Set.
 * It includes a get(Id) method, which is similar to a Map.get() method.
 * (There is no Map.put() method, though, which is replaced here by the Set.add() method)
 * It is assumed that IndexedElements that are added to an IndexedSet are uniquely
 * characterized by their Id: operations like add(), remove(), equals tacitly
 * assume this. For instance, it is impossible to have two different IndexedElements
 * with the same Id in one IndexedSet. (The add operation effectively ignores elements
 * with duplicate Id).
 * IndexedSets also support the filter operation which return an Iterator based upon
 * a Predicate, and a map() method which applies a Transform to all elements.
 *
 * IndexedSets are XMLStructures, and so, have XML encodings defined.
 * The form of encoding depends on the setting of the ``shallowXML'' mode:
 * if ``true'' (the default), then IndexedSets are regarded in essence as sets
 * of links to objects. In this case, only the symbolic links, i.e. the Id's of
 * the IndexedElements are encoded. If shallowXML is false, the encoding will
 * included the XML encodings of all elements. In this case, it is required
 * that the set elements are XMLStructures themselves. Note that when one of these 
 * IndexedElements would contain a link to the IndexedSet, the resulting cycle
 * could lead to non termination of methods like appendXML.
 */
public class IndexedSet<E extends IndexedElement> extends XMLStructureAdapter
implements Set<E>, Collection<E>,Predicate<E>
{   
   /* 
    * The implementation is based upon a HashMap, 
    * which used elem.getId() as index for elem. 
    */
   protected LinkedHashMap<Id, E> elements;
   /* shallowXML   determines whether the
    * XML encoding will encode just the Id's or also the IndexedElements themselves.
    */
   protected boolean shallowXML = true;
   
   /**
    * Constructs a new, empty IndexedSet
    */
   public IndexedSet() {
       elements = new LinkedHashMap<Id, E>();     
   }

   /**
    * Constructs a new IndexedSet with the same elements as the specified collection.
    */
   public IndexedSet(Collection<? extends E> col) {
      elements = new LinkedHashMap<Id, E>(); 
      addAll(col);     
   }

   /**
    * Constructs a new, empty IndexedSet with the specified initial capacity 
    * and the default load factor (0.75).
    */
   public IndexedSet(int initialCapacity) {
       elements = new LinkedHashMap<Id, E>(initialCapacity);     
   }
   
   /**
    * Constructs a new, empty IndexedSet with the specified initial capacity  and load factor.
    */
   public IndexedSet(int initialCapacity, float loadFactor) {
       elements = new LinkedHashMap<Id, E>(initialCapacity, loadFactor);    
   }

   /**
    * retrieves the IndexedElement specified by Id
    */
   public IndexedElement get(Id id) {
      return (IndexedElement) elements.get(id);
   }
      
   /**
    * replace(obj) is an alternate form of add(obj), that differs in the treatment
    * of existing keys. It will always add the new object, and returns the old
    * binding for the Id of that Object, which could be null.
    */
   public E replace(E obj) {
      return(elements.put(((IndexedElement)obj).getId(), obj));
   }
      
      
   /**
    * add(obj) adds IndexedElement obj to the set, provided there was
    * not already some element with the same Id, and  returns true.
    * If there was already an element with the same Id (which might be actually the same 
    * element or not), the new value is not added, and false is returned.
    */
   public boolean add(E obj) {
      Id id = ((IndexedElement)obj).getId();
      if (elements.containsKey(id)) return false;
      elements.put(id, obj);
      return true;
   }

   /**
    * Adds all of the elements in the specified collection, and
    * returns true if at least one element was actually added
    */
   public boolean addAll(Collection<? extends E> col) {
      boolean change = false;
      Iterator<? extends E> iter = col.iterator();
      while(iter.hasNext()) {
         boolean added = add(iter.next());
         change = change || added;
      }
      return change;
   }
   
   /**
    * Removes all of the elements from this set 
    */
   public void clear() {
      elements.clear();
   }
   

   /**
    * Checks whether some element is prsent within the set, where elem can
    * be specified as either an Id or as an IndexedElement.
    * When elem is actually an Id, this method checks whether the set contains
    * some element with that Id. Otherwise, elem is required to be an IndexedElement,
    * and in this case the check is performed for elem.getId().
    * Note that there is no check whether the contained IndexedElement
    * equals the argument IndexedElement; equality of Id's is used here effectively
    * rather than the equals method of the elements.)
    */
   public boolean contains(Object elem) {
      if (elem instanceof Id) {
         return elements.containsKey(elem);
      } else {
         return elements.containsKey(((IndexedElement)elem).getId());
      }
   }  
   
   /**
    * Returns true if this collection contains all of the elements in the specified collection.
    */
   public boolean containsAll(Collection col) {
      Iterator iter = col.iterator();
      while(iter.hasNext()) {
          if ( ! contains(iter.next())) return false;  
      }
      return true;
   }



   
   /**
    * equality test based upon equality of the Ids of the IndexedElements
    */
   public boolean equals(Object indexedSet) {
      IndexedSet ins = (IndexedSet) indexedSet;
      return elements.keySet().equals(ins.elements.keySet());
   }
   
   /** 
    * hashCode based upon the Ids of the IndexedElements
    */
   public int hashCode() {
      return elements.keySet().hashCode();
   }
   
   /**
    * Returns true if this collection contains no elements.
    */
   public boolean isEmpty() {
      return elements.isEmpty();
   }
   
   /**
    * Returns an iterator over the elements in this collection.
    */
   public Iterator<E> iterator() {
      return elements.values().iterator();
   }


   /**
    * removes the IndexdElement specified by Id, if present.
    */
   public boolean remove(Id id) {
      E old = elements.remove(id);
      return old != null;
   }
   
   /**
    * Removes the element with the same Id as that of the specified element from this collection, 
    * if it is present. (Note that when the specified element does not actually
    * occur, but some IndexedSet element happens to have the same Id, then the latter IndexedSet element
    * will be removed!)
    */
   public boolean remove(Object indexedElement) {    
      if (indexedElement == null) return false; // collection unchanged    
      return remove(((IndexedElement)indexedElement).getId());
   }
   
   /**
    * Removes all this collection's elements that are contained in the specified collection 
    */
   public boolean removeAll(Collection<?> col) {
      Iterator<?> iter = col.iterator();
      boolean change = false;
      while(iter.hasNext()) {
          boolean removed = remove(iter.next());
          change = change || removed;
      }
      return change;
   }
   
   /**
    * Retains only the elements in this collection that are contained in the specified collection 
    */
   public boolean retainAll(Collection col) {
      Iterator iter = elements.values().iterator();
      boolean change = false;
      while(iter.hasNext()) {
          if (! col.contains(iter.next())) {
             iter.remove();
             change = true;
          }
      }
      return change;
   }
   
   /**
    * Returns the number of elements in this collection.
    */
   public int size() {
      return elements.size();
   }
   
   /**
    * Returns an array containing all of the elements in this collection.
    */
   public Object[] toArray() {
      return elements.values().toArray();
   }
   
   /**
    * Returns an array containing all of the elements in this collection; 
    * the runtime type of the returned array is that of the specified array.
    */
   public <E> E[] toArray(E[] ar) {
      return elements.values().toArray(ar);
   }
   
   /**
    * Returns a set view of the Ids of the IndexedElements of this Set
    * The key set is backed by the IndexedSet, so changes to the IndexedSet are reflected in the set, 
    * and vice-versa. The set supports element removal, 
    * which removes the corresponding IndexedElement from this IndexedSet, via the Iterator.remove, 
    * Set.remove, removeAll, retainAll, and clear operations. 
    * It does not support the add or addAll operations. 
    */
   public Set keySet() {
      return elements.keySet();  
   }
    
   /**
    * Returns an iterator over the elements in this collection
    * that satisfy the given Predicate.
    */
   public Iterator<E> filter(final Predicate<E> pred) {
       return new Iterator<E>() {
          public  boolean hasNext() { 
             if (objUsed) toNext();
             return (obj != null);
          }
          public E next() {
              if (objUsed) toNext();
              objUsed = true;
              return obj;
          }
          public void remove() {
              setIter.remove();
          } 
          /* advance to next selected VirtualObject, 
           * or null, if there is no such Object anymore.  */
          private void toNext() {
             objUsed = false;
             while (setIter.hasNext()) {              
                obj = setIter.next();
                if (pred.valid(obj)) return; 
             } 
             obj = null;
          }
          private Iterator<? extends E> setIter = iterator();
          private E obj;
          private boolean objUsed = true;
       };
   }

   /**
    * Transforms the set, by applying the given Transform on each Set element.
    * The transformation is "in place", i.e., no Set elements are added or removed,
    * but rather each element is modified by the Transform.
    */
   public void map(final Transform<E, E> t) {
      Iterator<E> setIter = iterator();
      while (setIter.hasNext()) {
         t.transform(setIter.next());  
      }  
   }

   /**
    * This method turns the Set into a Predicate itself, defined to be "valid"
    * for Object obj iff the Object obj belongs to this Set.
    */
   public boolean valid(E obj) {
      return contains(obj);
   }

   /**
    * Returns a shallow copy of this IndexedSet instance: the Ids and values themselves are not cloned
    */
   public Object clone() {
      return new IndexedSet<E>(this);  
   }
    
    
   /**
    * Selects the subset consisting of a shallow copy of all
    * elements of this IndexedSet that satisfy the specified Predicate
    */
   public IndexedSet<E> select(Predicate<E> pred) {
      IndexedSet<E> result = new IndexedSet<E>();
      Iterator<E> iter = filter(pred);
      while (iter.hasNext()) {
         result.add(iter.next());
      }
      return result;
   }

   public String getXMLTag() {
      return XMLTAG;
   }

   /**
    * Appends a String to buf that encodes the attributes for the XML encoding.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      appendAttribute(buf, "shallow", shallowXML);
      return buf;
   }

   /**
    * decodes attributes of IndexedSets.
    */
   public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer) {
      if (attrName.equals("shallow")) {
         shallowXML = (attrValue.equals("true"));
         return true;
      }
      return false;
   }


   /**
    * Appends a String to buf that encodes the contents for the XML encoding.
    */
   public StringBuffer appendContent(StringBuffer buf, int tab) {
      if (shallowXML) {
         Iterator iter = elements.keySet().iterator();  
         while (iter.hasNext()) {
             Id id = (Id) iter.next();
             buf.append('\n');
             //appendEmptyTag(buf, tab, IndexedElement.XMLTAG, "id", id.toString());  
             XMLId.appendXML(id, buf, tab);
             //id.appendXML(buf, tab);  
         }
      } else {
         Iterator iter = elements.values().iterator();  
         while (iter.hasNext()) {
             XMLStructure elem = (XMLStructure) iter.next();
             buf.append('\n');
             elem.appendXML(buf, tab+TAB);  
         }         
      }
      return buf;
   }

   /**
    * decodes the XML contents, i.e. the XML between the <IndexedElement>
    * and the </IndexedElement> tags of the encoding.
    * depending on the shallowXML mode, the contents should consist of
    * a sequence of Id encodings (shallowXML), or else a sequence of IndexedElement
    * encodings. After decoding this IndexedSet contains the decoded elements.
    */
   public void decodeContent(XMLTokenizer tokenizer) throws java.io.IOException  {
      if (elements == null) {
         elements = new LinkedHashMap<Id, E>(); 
      } else {
         elements.clear();
      }
      while (tokenizer.atSTag()) {
         //Console.println("decode indexed element..");
        // E elem = E.class.newInstance();
        
         E elem  =  (E) XML.createXMLStructure(tokenizer);

         if (! add(elem)) {
            Console.println("IndexedSet: duplicate Id: " + (elem.getId()) + " while decoding XML");
         }   
               
      }
   }




   /**
    * sets the shallowXML mode;
    * if set to true, the content of the XML encoding 
    * consists of elements of the form <IndexedElement id="..."/>,
    * one for each set element. If shallowXML is false, the contents
    * consist of a sequence of XML encoding of the set elements.
    * 
    */
   public void setShallowXML(boolean mode) {
      shallowXML = mode;
   }

   public static final String XMLTAG = "IndexedSet";
   
  /**/

}