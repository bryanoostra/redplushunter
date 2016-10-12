/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $    
 * @since version 0       
 */
// Last modification by: $Author: swartjes $
// $Log: ArrayStack.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2005/09/17 20:14:22  zwiers
// toString methods added/modified
//

package parlevink.util;
import java.util.*;

/*
 * A Stack is a simple implementation of (unbounded) stacks,
 * based upon arraylists.
 */
public class ArrayStack<E> extends ArrayList<E>
{
   /**
    * DEFAULT_CAPACITY is the default initial capacity of Stacks.
    */
   public static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final long serialVersionUID = 0L;

   /**
    * allocates a new Stack with default initial capacity
    */
   public ArrayStack() {
       super(DEFAULT_INITIAL_CAPACITY);     
   }

   /**
    * allocates a new Stack with specified initial capacity
    */
   public ArrayStack(int initialCapacity) {
      super(initialCapacity);
   }

   /**
    * push a new element on top of the stack
    */
   public final void push(E elem) {
      add(elem);        
   }

   /**
    * returns the top element from the stack, without removing it.
    */
   public final E top() {
      int len = size();
      if (len == 0)
         throw new EmptyStackException();
      return get(len - 1);
   }

   /**
    * removes the top element from tghe stack
    * and returns the element that was removed.
    */
   public final E pop() {
      E obj;
      int len = size();
	   obj = get(len - 1);
	   remove(len - 1);
      return obj;
   }

   /**
    * alternative name for the top() operation;
    * in line with  java.util.Stack
    */
   public final E peek() {
      return top();
   }

   /**
    * returns a String listing all Stack elements, using
    * the toString() methods of the Stack elements.
    * The elements are enclosed within the string ``Stack[ ..... ]'',
    * Each stack element is printed on a mew line.
    */
   public String toString() {
      return toString("Stack", "\n");
   }

   /**
    * returns a String listing all Stack elements, using
    * the toString() methods of the Stack elements.
    * The elements are enclosed within the string ``label[ ..... ]''
    * Each stack element is printed on a new line.
    */
   public String toString(String label) {
      return toString(label, "\n");
   }

   /**
    * returns a String listing all Stack elements, using
    * the toString() methods of the Stack elements.
    * The elements are enclosed within the string ``label[ ..... ]''
    * Stack elements are separated by means of the separator String.
    * (The default separator is &quot;\n&quot;)
    */
   public String toString(String label, String separator) {
      StringBuffer buf = new StringBuffer();
      if (label != null) buf.append(label);
      buf.append('[');
      Iterator<E> iter = iterator();
      while (iter.hasNext()) {
         buf.append(separator);
         E elem = iter.next();
         buf.append(elem.toString());
      }
      buf.append(']');
      return buf.toString();
   } 

}