/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Predicate.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2005/09/14 15:53:51  zwiers
// templates introduced
//
// Revision 1.1  2003/05/09 12:08:02  zwiers
// First version
//



package parlevink.util;
import java.util.*;


/**
 *
 * Predicates are Objects that implement a boolean test on Objects,
 * in the form of their "valid" method. If some Object obj satifies
 * some predicate pred, then pred.valid(obj) should yield "true".
 */
 
public interface Predicate<T>
{
   public boolean valid(T obj);

}