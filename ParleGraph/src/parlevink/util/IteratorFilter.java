/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 */
// Last modification by: $Author: swartjes $
// $Log: IteratorFilter.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2005/10/20 10:07:25  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.6  2003/07/07 20:32:17  dennisr
// *** empty log message ***
//
// Revision 1.5  2003/07/06 21:33:13  dennisr
// Adapted to Predicate and Transform interfaces
//
// Revision 1.4  2003/07/02 19:50:21  dennisr
// *** empty log message ***
//
package parlevink.util;

import java.util.*;

/**
 *
 * An IteratorFilter is used to filter Objects in an Iterator 
 * and present them as a new Iterator.
 * <br>
 * The filter is initialised with a Predicate; only Objects for which 
 * the predicate p.valid(Object o) returns true
 * will be passed on, other object will be left out.
 * This implementation is lazy, meaning that the next object from the original 
 * iterator will not be accessed before it is needed due to a call to the 
 * next() method of this IteratorFilter.
 * The original Iterator will therefore be traversed only once and the elements 
 * will not be stored in an intermediate array or something like that.
 * <br>
 * By default, the IteratorFilter does not allow removal of objects.
 * <br>
 * Example: <br>
 * <code>
 * ..
 * Iterator it = someSetOfStrings.iterator();
 * Predicate p = new Predicate() {
 *     public boolean valid(Object o) {
 *         return ((String)o).startsWith("a");
 *     }
 * };            
 * Iterator filteredIt = new IteratorFilter(it, p);
 * ..
 * </code>
 * <br>
 * You can also implement special subclasses of Predicate
 * to achieve more complex boolean tests.
 * <p>
 * Examples of the use of the different Iterator modifyers (IteratorFilter,
 * IteratorTransform, IteratorChain) can be found in the class RTSI.
 *
 * @author Dennis Reidsma, UTwente
 */
public class IteratorFilter<T> implements Iterator<T> {
    /**
     * The Iterator to filter
     */
    private Iterator<T> iter;
    
    /** 
     * A look-ahead for the Iterator, to allow examination of
     * the properties of the next object.
     */
    private T next;

    private boolean traversalStarted = false;

    private Predicate<T> thePred;
  
    /**
     * @param it The Iterator to filter
     * @param pred The Predicate
     */
    public IteratorFilter(Iterator<T> it, Predicate<T> pred) {
        if ((it == null) || (pred == null)) {
            throw new NullPointerException();
        }
        iter = it;
        thePred = pred;
    }
    
    private void startTraversal() {
        traversalStarted = true;
        moveToNextObject();
    }
    
    /**
     * Used to skip over all objects that should NOT
     * be passed on.
     */
    private void moveToNextObject() {
        next = null; //otherwise it gets stuck on the last object
        while (iter.hasNext()) {
            next = iter.next();
            if (!thePred.valid(next)) {
                next = null;
            } else {
                break;
            }
        }
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove(){
        throw new UnsupportedOperationException();
    }
    
    public boolean hasNext() {
        if (!traversalStarted)
            startTraversal();
        return next != null;
    }

    public T next() {
        if (!traversalStarted)
            startTraversal();
        if (next == null) {
            throw new NoSuchElementException ();
        }
        T result = next;
        moveToNextObject();
        return result;
    }

}
