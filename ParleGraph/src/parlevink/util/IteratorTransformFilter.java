/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 */
// Last modification by: $Author: swartjes $
// $Log: IteratorTransformFilter.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2005/10/20 10:07:25  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2003/07/07 23:02:57  dennisr
// *** empty log message ***
//
// Revision 1.1  2003/07/06 21:33:13  dennisr
// Adapted to Predicate and Transform interfaces
//
// Revision 1.4  2003/07/02 19:50:21  dennisr
// *** empty log message ***
//
package parlevink.util;

import java.util.*;

/**
 *
 * An IteratorTransform is used to transform Objects in an Iterator 
 * and present them as a new Iterator.
 * The difference with a normal IteratorTransform is that this implementation will 
 * not throw exceptions when the transformation fails but rather will act as a filter
 * for those objects, passing on only those objects in the original iterator on
 * which the transformation can be successfully applied.
 * <p>
 * I.e., the function hasNext will return true if the original iterator has no
 * next object that can be transformed.
 * <p>
 * NB: This is a VERY expensive iterator when there are many objects that cannot 
 * be transformed, due to all the exceptions that will be thrown.
 * 
 * @author Dennis Reidsma, UTwente
 */
public class IteratorTransformFilter<S, T> implements Iterator<T> {
    /**
     * The Iterator to filter
     */
    private Iterator<S> iter;
    
    /** 
     * A look-ahead for the Iterator, to allow examination of
     * the properties of the next object.
     */
    private T next;

    private boolean traversalStarted = false;

    private Transform<S, T> theTransform;
  
    /**
     * @param it The Iterator to filter
     * @param trans The Transformation
     */
    public IteratorTransformFilter(Iterator<S> it, Transform<S, T> trans) {
        if ((it == null) || (trans == null)) {
            throw new NullPointerException();
        }
        iter = it;
        theTransform = trans;
    }
    
    private void startTraversal() {
        traversalStarted = true;
        moveToNextObject();
    }
    
    /**
     * Used to skip over all objects that cannot be transformed
     */
    private void moveToNextObject() {
        next = null; //otherwise it gets stuck on the last object
        while (iter.hasNext()) {
            try {
                next = theTransform.transform(iter.next());
                break;
            } catch (Exception ex) {
                next = null;
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