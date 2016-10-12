/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Terminator.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2003/10/25 20:50:01  zwiers
// boolean terminate
//
// Revision 1.2  2002/11/06 17:18:17  zwiers
// updated comments
//
// Revision 1.1  2002/10/21 09:56:18  zwiers
// moved, from agents package to util package
//
// Revision 1.1.1.1  2002/09/11 09:36:14  zwiers
// initial import

package parlevink.util;

/**
 * A Terminator is an interface that requires a "terminate" method.
 * The intention is that a Terminator performs actions immediately before
 * a JVM is terminated, and allows for a "clean termination" procedure.
 */
public interface Terminator
{
 
   /**
    * signal that the system is about to terminate; This object should
    * perform the necessary actions for "clean termination", without
    * exiting the system as a whole.
    * The boolean value returned can be used to signal whether termination
    * was succesful or not.
    */
   public boolean terminate();
      
}

