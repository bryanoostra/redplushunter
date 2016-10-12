/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Pred.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2005/10/20 10:07:25  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2004/05/17 14:53:49  zwiers
// comment update
//


package parlevink.util;

/**
 * A utility class for Predicates.
 * Pred contains static Predicates TRUE and FALSE,
 * and static methods for combining existing predicates,
 * like "AND", "OR", "NOT", "IMPLIES".
 */
 
public class Pred {
   
   /** 
    * The Predicate that is always "false",
    * i.e. FALSE.valid(obj) return always false.
    */
   public static Predicate FALSE = new Predicate() {
      public boolean valid(Object obj) {
         return false;
      }
   };

   /** 
    * The Predicate that is always "true",
    * i.e. TRUE.valid(obj) return always true.
    */
   public static  Predicate TRUE = new Predicate() {
      public boolean valid(Object obj) {
         return true;
      }
   };

   /** 
    * creates a new Predicate that is the logical "AND" of the two
    * given Predicates.
    */
   public static <T> Predicate<T> AND(final Predicate<T> pred1, final Predicate<T> pred2) {
      return new Predicate<T>() {
         public boolean valid(T obj) {
            return pred1.valid(obj) && pred2.valid(obj);
         }
      };
   }
   
   /** 
    * creates a new Predicate that is the logical "OR" of the two
    * given Predicates.
    */
   public static <T> Predicate<T> OR(final Predicate<T> pred1, final Predicate<T> pred2) {
      return new Predicate<T>() {
         public boolean valid(T obj) {
            return pred1.valid(obj) || pred2.valid(obj);
         }
      };
   }   

   /** 
    * creates a new Predicate that is the logical "XOR" of the two
    * given Predicates.
    */
   public static <T> Predicate<T> XOR(final Predicate<T> pred1, final Predicate<T> pred2) {
      return new Predicate<T>() {
         public boolean valid(T obj) {
            return pred1.valid(obj) != pred2.valid(obj);
         }
      };
   }   

   /** 
    * creates a new Predicate that is true if the logical "implication" is true.
    */
   public static <T> Predicate<T> IMPLIES(final Predicate<T> pred1, final Predicate<T> pred2) {
      return new Predicate<T>() {
         public boolean valid(T obj) {
            return (!pred1.valid(obj)) || pred2.valid(obj);
         }
      };
   }   

   /** 
    * creates a new Predicate that is true if the logical "equivalence" is true.
    */
   public static <T> Predicate<T> EQUIV(final Predicate<T> pred1, final Predicate<T> pred2) {
      return new Predicate<T>() {
         public boolean valid(T obj) {
            return ( (pred1.valid(obj) == pred2.valid(obj)) );
         }
      };
   }   

   /** 
    * creates a new Predicate that is the logical negation of its argument predicate
    */
   public static <T> Predicate<T> NOT(final Predicate<T> pred) {
      return new Predicate<T>() {
         public boolean valid(T obj) {
            return ! pred.valid(obj) ;
         }
      };
   }    
     
}