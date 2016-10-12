/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */
// Last modification by: $Author: swartjes $
// $Log: Queue.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2003/04/15 12:46:10  zwiers
// update comments, queue improved
//
// Revision 1.2  2003/03/14 17:01:38  zwiers
// comment update
//
// Revision 1.1  2003/03/14 13:00:25  zwiers
// initial version
//

package parlevink.util;
//import java.util.*;

/*
 * A Queue is a efficient implementation of a standard unbounded "queue" data type,
 * implementing the Buffer interface (implementing the traditional put and get methods), 
 * A Queue is not thread safe, and will not block put or get operations.
 * Trying to retrieve an element from an empty Queue will throw a 
 * RuntimeException, rather than waiting for new elements to arrive.
 * The Queue implementation is based upon an array, that is reallocated
 * whenever the put operation is executed and the buffer is full.
 * Each reallocation action doubles the previous array length. 
 * The Queue class is a minimal implementation, not requiring the java Collections
 * framework. An extension (QueueCollection) is available, and offers a richer interface.
 */
public class Queue 
implements  Buffer
{
   /**
    * DEFAULT_CAPACITY is the default initial capacity of Queues.
    * The internal buffer is reallocated as soon as this capacity is exceeded.
    */
   public static final int DEFAULT_CAPACITY = 128;

   /**
    * Queue() allocates a new Queue with default initial capacity. 
    * When this capacity turns out to be insufficient,
    * more buffer space will be allocated automatically.
    */
   public Queue() {
       this(DEFAULT_CAPACITY);     
   }

   /**
    * Queue(size) allocates a new Queue with
    * specified initial capacity. When this capacity turns out to be insufficient,
    * more buffer space will be allocated automatically.
    */
   public Queue(int capacity) {
      bufferSize = capacity + 1;         // One buffer place must be unused at all times.
      buffer = new Object[bufferSize];   // Allocates buffer[0], .., buffer[bufferSize-1].
      in = 0; out = 0;    
   }

   /**
    * get() retrieves, and removes, the next element from the queue.
    * When the Queue is empty, a RuntimeException is thrown.
    */
   public Object get() throws RuntimeException {
      if (in==out) throw new RuntimeException("get operation on empty queue");
      out = (out+1) % bufferSize;  // guarantees 0 <= out < bufferSize
      Object result = buffer[out];
      buffer[out] = null; // allow garbage collection 
      return result;
   } 

   /**
    * put(obj) puts Object obj in the queue.
    * The Queue acts as an unbouded buffer, i.e the put operation cannot fail
    * or block. However, when the capacity of the internal buffer array is
    * exceeded, this operation first doubles the buffer size.
    */
   public void put(Object obj) {
      in = (in+1) % bufferSize;    // guarantees 0 <= in < bufferSize
     if (in==out) {
        int newSize = bufferSize * 2;
        //System.out.println("reallocate, size = " + newSize);
        Object[] newbuffer = new Object[newSize]; // allocates newbuffer[0..newSize-1]
        System.arraycopy(buffer, out+1 ,newbuffer, 0, bufferSize-1-out);
        System.arraycopy(buffer, 0,newbuffer, bufferSize-1-out, in);
        buffer = newbuffer;
        in = bufferSize-1;
        out = newSize-1;
        bufferSize = newSize;
     }
     buffer[in] = obj;
   }
 
   /**
    * Removes all elements from the Queue, leaving it empty.
    */
   public void clear(){
	    while (out != in) {
	       out = (out+1) % bufferSize;
          buffer[out] = null; // allow garbage collection
	    }    
    }

   /**
    * isEmpty() indicates whether the queue is empty. 
    * Note that when concurrent access is allowed,
    * there is no guarantee that a get() will succeed
    * after an isEmpty() call wich returns "false".
    */
   public boolean isEmpty() {
      return (in==out);
   }
 
   /**
    * isFull() is required by the Buffer interface;
    * However, for a Queue, it always returns false;
    */
   public boolean isFull() {
      return false;
   }
 
   /**
    * Returns the number of elements in this Queue.
    */
    public int size() {
       if (out <= in) 
          return (in - out);
       else 
          return (bufferSize + in - out);  
    }

   /* buffer together with in and out form a circular buffer. 
    * If out <= in, then the Buffer contents are:    
    * buffer[out+1], .. ,buffer[in]           
    * Else, the contents are:   
    * buffer[out+1], .. ,buffer[bufferSize-1], buffer[0] .. buffer[in] 
    */
   protected Object[] buffer;                                  
   protected int bufferSize;     
   protected int in, out;    
}