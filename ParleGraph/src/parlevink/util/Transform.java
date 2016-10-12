/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Transform.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2005/10/20 10:07:25  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.1  2003/05/09 12:08:09  zwiers
// First version
//



package parlevink.util;
import java.util.*;

/**
 * Transforms are objects that define a "mapping" from Objects to Object.
 * They are used, for instance, by FilterSet, to define mappings on Sets.
 * The transform could modify the Object "in place", or not.
 * In both cases the modified Object must be returned.
 */
 
public interface Transform<S, T>
{
   public T  transform(S obj);

}