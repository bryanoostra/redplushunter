/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $    
 * @since version 0       
 */
// Last modification by: $Author: swartjes $
// $Log: QueueCollection.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2003/05/09 12:07:37  zwiers
// modified to allow null elements
//
// Revision 1.1  2003/04/29 15:27:32  zwiers
// initial version
//
// Revision 1.2  2003/03/14 17:01:38  zwiers
// comment update
//
// Revision 1.1  2003/03/14 13:00:25  zwiers
// initial version
//



package parlevink.util;
import java.util.*;

/*
 * A QueueCollection is a efficient implementation of a standard unbounded 
 * "queue" data type, in the style of a java Collection. 
 * It is an extension of Queue, so it also implements the Buffer interface, 
 * including the traditional put and get operations. (Note that the Buffer.get
 * operation shaes the name, but not its semantics with the get method in, 
 * for instance, the Vector and List classes. We do have the elementAt method,
 * however, which plays the same role in Queue, as well as Vector and List)
 * Like all java Collections, a QueueCollection is not thread safe. 
 * Since concurrent access is not allowed, the put and get methods are 
 * non-blocking, i.e. trying to retrieve an element from an empty QueueCollection 
 * will throw a RuntimeException.
 */
public class QueueCollection extends Queue
implements Collection, Buffer
{
   /**
    * QueueCollection() allocates a new QueueCollection with default initial capacity. 
    * When this capacity turns out to be insufficient,
    * more buffer space will be allocated automatically.
    */
   public QueueCollection() {
       super(DEFAULT_CAPACITY);     
   }

   /**
    * QueueCollection(size) allocates a new QueueCollection with
    * specified initial capacity. When this capacity turns out to be insufficient,
    * more buffer space will be allocated automatically.
    */
   public QueueCollection(int capacity) {
      super(capacity);
   }

   /**
    * Allocates a new QueueCollection containing all elements from
    * Collection col.
    */
   public QueueCollection(Collection col) {
       super(col.size()); 
       Iterator iter = col.iterator();
       while (iter.hasNext()) {
         add(iter.next());
       }    
   }

   /**
    * Returns the component at the specified index,
    * without actually removing it.
    * @exception ArrayIndexOutOfBoundsException if the specified index is negative
    * or not less than the current size of the QueueCollection.
    */
   public Object elementAt(int index) throws ArrayIndexOutOfBoundsException {
      if (((out <= in) && index >= (in-out)) ||
          ((out >  in) && index >= (bufferSize + in - out)) )      
          throw new ArrayIndexOutOfBoundsException("Index out of bounds for QueueCollection.elementAt operation");
      return buffer[(out + index + 1) % bufferSize];
   } 

   /**
    * Returns the component at the specified index,
    * and removes it from the QueueCollection.
    * @exception ArrayIndexOutOfBoundsException if the specified index is negative
    * or not less than the current size of the QueueCollection.
    */
   public Object remove(int index) throws ArrayIndexOutOfBoundsException {
      if (((out <= in) && index >= (in-out)) ||
          ((out >  in) && index >= (bufferSize + in - out)) )      
          throw new ArrayIndexOutOfBoundsException("Index out of bounds for QueueCollection.elementAt operation");
      int p = (out + index + 1) % bufferSize;
      Object result = buffer[p];
      bufferRemove(p);
      return result;
   } 

   /**
    * add(obj) adds Object obj to the queue, and (always) returns true.
    * Essentially the same operation as put(obj), that is included so as
    * to implement the Collections interface.
    */
   public boolean add(Object obj) {
      put(obj);
      return true;
   }
  
   /** 
    * Adds all elements from the specified Collection.
    * (always returns true.)
    */  
   public boolean addAll(Collection col) {
      Iterator iter = col.iterator();
      while(iter.hasNext()) {
          add(iter.next());  
      }
      return true;   
   }
 
   /**
    * Returns true if this QueueCollection contains the specified element.
    */
   public boolean contains(Object obj){
       int p = out;
	    while (p != in) {
	      p = (p+1) % bufferSize;
	      if (obj==null ? buffer[p]==null : obj.equals(buffer[p])) return true;
	    }      
	   return false;
   }  

   /**
    * Returns true if this QueueCollection contains all of the elements in the specified collection.
    */
   public boolean containsAll(Collection col){
      Iterator iter = col.iterator();
      while(iter.hasNext()) {
          if ( ! contains(iter.next())) return false;  
      }
      return true;   
   }  

   /**
    * Compares the specified object with this QueueCollection for equality.
    */
   public boolean equals(Object obj){
      if (! (obj instanceof QueueCollection) ) return false;
      QueueCollection queue = (QueueCollection) obj;
      if (size() != queue.size()) return false;
	   int p = out;
	   int pq = queue.out;
	   while (p != in) {
	      p = (p+1) % bufferSize;
	      pq = (pq+1) % queue.bufferSize;
	      if (! (buffer[p].equals(queue.buffer[pq]) )   ) return false;
	   }
	   return true;
   }  

   /**
    * Returns the hash code value for this collection.
    */
   public int hashCode(){
      long hash = 0;
      long maxint = (long) Integer.MAX_VALUE;
	   int p = out;
	   while (p != in) {
	      p = (p+1) % bufferSize;
	      if (buffer[p] != null) {
	         hash = (hash + buffer[p].hashCode()) % maxint;	      
	      }
	   }
      return  (int) hash;              
   }
 
   /**
    * Returns an iterator over the elements contained in this collection.
    */  
   public Iterator iterator() {
       return new Iterator() {
          public  boolean hasNext() { 
              return (p != in);
          }
          public Object next() {
              p = (p+1) % bufferSize;
              Object nxt = buffer[p];
              return  nxt;
          }
          public void remove() {
             bufferRemove(p);
          } 
          int p = out;
       };
   }

   /*
    * removes element p from the buffer, and shifts all elements
    * from (out +1)%bufferSize to (p-1) %bufferSize
    * assumes that out < p <= in OR p <= in <= out
    */
   private void bufferRemove(int p) {
       buffer[p] = null; // allow garbage collection
       int lastindex = bufferSize-1;
       int q = (p-1<0) ? lastindex : p-1;
       //System.out.println("out, q, p = " + out + ", " + q + ", " + p);
       while (q != out) {
          buffer[p] = buffer[q];
          p = (p-1<0 ? lastindex : p-1); 
          q = (q-1<0 ? lastindex : q-1);  
          //System.out.println("q, p = " + q + ", " + p);       
       }
       out = p;
   }

   /**
    * Removes the first occurrence of obj, where "first"
    * means the first one that would be obtained by repeated get() operations.
    * Returns true if the QueueCollection actually contained the specified element.
    */ 
   public boolean remove(Object obj) {
       int p = out;
	    while (p != in) {
	       p = (p+1) % bufferSize;
	       if (obj==null ? buffer[p]==null : obj.equals(buffer[p])) {
	          bufferRemove(p);
	          return true;
	       }
	    }      
       return false; // not found/QueueCollection not changed
   }

   /**
    * Removes from this collection all of its elements that are contained 
    * in the specified collection.
    * returns true is the QueueCollection is actually changed.  
    */
   public boolean removeAll(Collection col) {
      //dump("removeAll");
      int p = in;
      int lastindex = bufferSize-1;
      boolean found = false;
      while ( p!= out && ! found) {
         if (col.contains(buffer[p])) {
            found = true;
         } else {
            p = (p-1<0 ? lastindex : p-1);          
         }
      }   
      if (! found ) return false;
      buffer[p] = null;  // allow garbage collection 
      int q = p;  // follow pointer; copy from p to q location
      p = (p-1<0 ? lastindex : p-1); 
      while ( p!= out ) {
         if (col.contains(buffer[p])) {
             buffer[p] = null;  // allow garbage collection 
             p = (p-1<0 ? lastindex : p-1); 
         } else {
            buffer[q] = buffer[p];
             p = (p-1<0 ? lastindex : p-1);
             q = (q-1<0 ? lastindex : q-1);            
         }  
      }      
      out = q;
      //dump("return");
      return true;
   }

   /**
    * Removes from this collection all of its elements that are not contained 
    * in the specified collection.  
    * returns true is the QueueCollection is actually changed.  
    */
    public boolean retainAll(Collection col) {
      int p = in;
      int lastindex = bufferSize-1;
      boolean found = false;
      while ( p!= out && ! found) {
         if (! col.contains(buffer[p])) {
            found = true;
         } else {
            p = (p-1<0 ? lastindex : p-1);          
         }
      }   
      if (! found ) return false;
      buffer[p] = null;  // allow garbage collection 
      int q = p;  // follow pointer; copy from p to q location
      p = (p-1<0 ? lastindex : p-1); 
      while ( p!= out ) {
         if (! col.contains(buffer[p])) {
             buffer[p] = null;  // allow garbage collection 
             p = (p-1<0 ? lastindex : p-1); 
         } else {
            buffer[q] = buffer[p];
             p = (p-1<0 ? lastindex : p-1);
             q = (q-1<0 ? lastindex : q-1);            
         }  
      }      
      out = q;
      return true;
   }

   /**
    * Returns an array containing all of the elements in this QueueCollection,
    * in the order consistent with repeated get() operations.
    * The length of the array equals the current QueueCollection size.
    */
   public Object[] toArray() {
       Object[] result = new Object[size()];
       int p = out;
       int i = 0;
	    while (p != in) {
	      p = (p+1) % bufferSize;
	      result[i++] =  buffer[p];
	    }       
       return result;  
   }

   /**
    * Returns an array containing all of the elements in this collection. 
    * The runtime type of the returned array is that of the specified array. 
    * If the collection fits in the specified array, it is returned therein. 
    * Otherwise, a new array is allocated with the runtime type of the 
    * specified array and the current QueueCollection size.
    */
   public Object[] toArray(Object[] a) {
      int alen = a.length;
      if (alen < size()) {
         Class componentType = a.getClass().getComponentType();
         a = (Object[]) java.lang.reflect.Array.newInstance(componentType, size());
      }
       int p = out;
       int i = 0;
	    while (p != in) {
	      p = (p+1) % bufferSize;
	      a[i++] =  buffer[p];
	    }       
	   if (alen > size()) a[size()] = null;
	   return a;            
 
   }

   /**
    * constructs a String of the form "[e0, e1, ....]",
    * showing the current contents of the QueueCollection
    */
   public String toString() {
       int p = (out+1) % bufferSize;
       StringBuffer buf = new StringBuffer(size());
       buf.append('[');
       if (in == out) {
           buf.append(']');
           return buf.toString();
       } else {
           if (buffer[p] != null) {
              buf.append(buffer[p].toString());
           } else {
             buf.append("Null");
           }
       }       
	    while (p != in) {
	       p = (p+1) % bufferSize;
	       buf.append(','); buf.append(' ');
	       if (buffer[p] != null) {
              buf.append(buffer[p].toString());
           } else {
             buf.append("Null");
           }
	    }    
	    buf.append(']');
	    return buf.toString();   
   }

   /*
    * shows the value of in, out and bufferSize indices as well as the
    * current   QueueCollection contents ( for debugging purposes.)
    */   public String internalString() {
      String result = "QueueCollection object, in = " + in + ", out = " + out + " bufferSize = " + bufferSize
                      + "\nbuffer = " + toString();
      return result;      
   }

   /*
    * dumps the value of internalString() to System.out
    * for debugging purposes.
    */
   public void dump(String msg) {
       System.out.println(msg + " " + internalString());
   }
}