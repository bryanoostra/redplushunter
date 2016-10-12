/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: BoundedBuffer.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:11  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2003/04/29 15:27:32  zwiers
// initial version
//
// Revision 1.2  2000/09/22 11:17:49  zwiers
// Changed get() return value for empty buffers
//
// Revision 1.1  2000/09/21 13:31:08  zwiers
// Added Buffer
//

package parlevink.util;

/** 
 * BoundedBuffer is an implementation of a generic Object fifo buffer.
 * No concurrency control is guaranteed. 
 * Buffer capacity is bounded, and must be specified.
 */
public class BoundedBuffer implements Buffer
{
  
  /**
   * BoundedBuffer(size) allocates a new BoundedBuffer with
   * capacity "capacity".
   */
  public BoundedBuffer(int capacity) {
    bufferSize = capacity + 1;         // reserve one extra place.
    buffer = new Object[bufferSize];   // Allocates buffer[0], .., buffer[bufferSize-1].
    in = 0; out = 0;    
  }
  
  /**
   * put(obj) puts Object val in the buffer.
   * When the buffer is full, this operation
   * is illegal, and val is ignored.
   */
  public void put(Object val) throws RuntimeException {
    in = (in+1) % bufferSize;    // guarantees 0 <= in < bufferSize
    if (in==out)  {
      throw new RuntimeException("buffer overflow");
       //System.err.println("Error: BoundedBuffer.put() on full buffer");
    } else {
    buffer[in] = val;
    }
  }
  
  /**
   * get() retrieves the next Object from the buffer.
    * When the Buffer is empty, a RuntimeException is thrown.
   */
  public Object get() throws RuntimeException {
    if (in==out) throw new RuntimeException("get operation on empty queue");
    //if (in==out) return null;
    out = (out+1) % bufferSize;  // guarantees 0 <= out < bufferSize
    Object result = buffer[out];
    buffer[out] = null; // allow garbage collection 
    return result;
  }

  /**
   * size() yields the number of Objects currently in the Buffer.
   */
  public int size() {
    if (out <= in) return in-out;
    return bufferSize - out + in;
  }

   /**
    * Removes all elements from the buffer, leaving it empty.
    */
   public void clear(){
	    while (out != in) {
	       out = (out+1) % bufferSize;
          buffer[out] = null; // allow garbage collection
	    }    
    }

  
  /**
   * isEmpty() indicates whether the buffer is empty. 
   * Note that when concurrent access is allowed,
   * a false result for an isEmpty() call does not 
   * guarantee that a get() operation will succeed. 
   */
  public boolean isEmpty() {
    
    return (in==out);
  }
    
  /**
   * isFull() indicates whether the buffer is full. 
   * Note that when concurrent access is allowed,
   * a false result for an isFull() call does not 
   * guarantee that a put() operation will succeed. 
   */
  public boolean isFull() {
    return ((in + 1) % bufferSize == out);
  }
  

/* buffer together with in and out form a circular buffer. 
 * If out <= in, then the Buffer contents are:    
 * buffer[out+1], .. ,buffer[in]           
 * Else, the contents are:   
 * buffer[out+1], .. ,buffer[bufferSize-1], buffer[0] .. buffer[in] 
 */
  private Object[] buffer;                                  
  private int bufferSize;     
  private int in, out;                          
   
} 