/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 */
// Last modification by: $Author: swartjes $
// $Log: IteratorChain.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2005/10/20 10:07:25  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2003/07/06 21:33:12  dennisr
// Adapted to Predicate and Transform interfaces
//
// Revision 1.1  2003/07/02 19:50:20  dennisr
// *** empty log message ***
//

package parlevink.util;

import java.util.*;

/**
 * An IteratorChain is used to chain several Iterators, i.e.
 * iterate consecutively through each of the Iterators and present
 * them as one Iterator. The order in qhich the original Iterators are 
 * traversed is determined by the constructor parameter.
 * <p>
 * This implementation is lazy, meaning that the next object from a source 
 * iterator will not be accessed before it is needed due to a call to the 
 * next() method of this IteratorChain.
 * <p>
 * The original Iterators are stored internally in an array, though.
 * <p>
 * By default, the IteratorChain does not allow removal of objects.
 * <br>
 * Example: <br>
 * <code>
 * ..
 * Iterator[] iterators;
 * 
 * Iterator chainedIt = new IteratorChain(iterators);
 * ..
 * </code>
 * <p>
 * Examples of the use of the different Iterator modifyers (IteratorFilter,
 * IteratorTransform, IteratorChain) can be found in the class RTSI.
 *
 * @author Dennis Reidsma, UTwente
 */
public class IteratorChain<T> implements Iterator<T> {
    /**
     * The list of Iterators to chain
     */
    private List<Iterator<T>> iterators;
    private Iterator<Iterator<T>> chainIterator;
    private Iterator<T> currentIterator;
    
    /**
     * The index of the Iterator that is curently being traversed
     */

    

    
    /**
     * @param its The Iterators to be chained
     */
    public IteratorChain(List<Iterator<T>> its) {
        if (its == null) {
            throw new NullPointerException();
        }
        iterators = its;
        
        chainIterator = iterators.iterator();
        currentIterator = null;
    }
    
//    public boolean add(Iterator<T> iter) {
//        iterators.add(iter);
//        return true; 
//    }
    
    /*
     * If currentIterator != null, retyrns immediately.
     * if currentIterator == null, the chain is searched for the first
     * Iterator for which hasNext() == true.
     * If there is no such Iterator anymore, currentIterator will remain null
     */
    private void checkCurrentIterator() {
       if (currentIterator != null && ! currentIterator.hasNext()) currentIterator = null;
       while ( currentIterator == null && chainIterator.hasNext()) {
            currentIterator = chainIterator.next();
            if ( ! currentIterator.hasNext() ) currentIterator = null;          
       }
       // currentIterator == null iff all iterators in the chain are exhausted.
       // if currentIterator != null is returned, currentIterator.hasNext() == true.
    }
    
    /**
     * @throws UnsupportedOperationException
     */
    public void remove(){
        throw new UnsupportedOperationException();
    }
    
    public boolean hasNext() {
        checkCurrentIterator();
        return currentIterator != null && currentIterator.hasNext();
      
//        if (iterators[currentIterator].hasNext())
//            return true;
//        while (currentIterator < iterators.length - 1) {
//            currentIterator++;
//            if (iterators[currentIterator].hasNext())
//                return true;
//        }
//        return false;
    }

    public T next() {
        checkCurrentIterator();
        if (currentIterator == null) {
           return null;
        } else {
           return currentIterator.next();
        }
//        try {
//            return iterators[currentIterator].next(); 
//            //NB: if at end of one iterator, and hasNext was not called, this results in exception!
//            //solution: catch exception and try "hasnext" to force move to next iterator with elements
//        } catch (NoSuchElementException ex) {
//            if (hasNext()) //forces move to next iterator...
//                return iterators[currentIterator].next(); //MOET een element zijn; behalve als er ECHT niks meer was.
//            throw new NoSuchElementException();
//        }
    }

}
