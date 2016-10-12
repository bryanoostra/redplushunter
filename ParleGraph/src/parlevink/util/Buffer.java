/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Buffer.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2003/04/15 12:46:10  zwiers
// update comments, queue improved
//
// Revision 1.1  2003/03/14 13:00:35  zwiers
// initial version
//


package parlevink.util;


/** 
 * Buffer is the interface for a generic Object fifo buffer.
 * In the general case, no concurrency control is guaranteed, and the  
 * buffer capacity is unspecified. Implementations must decide
 * whether their put/get operations will throw exceptions or will block instead,
 * in the case of a full or empty queue.
 */
public interface Buffer {
  
  /**
   * put(obj) puts Object obj in the buffer.
   * When the buffer is full, this operation
   * is illegal, and the behaviour is not prescribed.
   * For instance, a RuntimeException might be thrown,
   * or the operation might block.
   */
  public void put(Object val);
  
  /**
   * get() retrieves the next Object from the buffer.
   * When the buffer is empty this operation might return null,
   * or an Exception might be thrown, or the operation might block
   */
  public Object get();


   /**
    * Removes all elements from the Buffer, leaving it empty.
    */
   public void clear();

  /**
   * size() yields the number of Objects currently in the Buffer.
   */
  public int size();
  
  /**
   * isEmpty() indicates whether the buffer is empty. 
   * Note that when concurrent access is allowed,
   * a false result for an isEmpty() call does not 
   * necessarily guarantee that a get() operation will succeed. 
   */ 
  public boolean isEmpty();
    
  /**
   * isFull() indicates whether the buffer is full. 
   * Note that when concurrent access is allowed,
   * a false result for an isFull() call does not 
   * necessarily guarantee that a put() operation will succeed. 
   */
  public boolean isFull();  

} 