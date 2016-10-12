/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: IndexedElement.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2004/05/17 14:54:13  zwiers
// initial version
//


package parlevink.util;
import java.util.*;

/**
 *
 * IndexedElements are Object that have an associated Id,
 * that can be retrieved via the getId() method.
 */
 
public interface IndexedElement
{
   public Id getId();

   public static final String XMLTAG = "IndexedElement";

}