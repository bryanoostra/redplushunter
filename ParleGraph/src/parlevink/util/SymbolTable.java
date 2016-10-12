/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $
 */

// Last modification by: $Author: swartjes $
// $Log: SymbolTable.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.2  2005/10/20 10:07:25  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.1  2004/08/27 16:15:27  zwiers
// initial version
//



package parlevink.util;


import java.util.*;
import parlevink.xml.*;
import parlevink.util.*;


/** 
 * A symbol table is basically a stack of HashMaps, where each
 * HashMap, maps Ids to Object. Strings are allowed instead of Ids,
 * but they are converted internally to Ids.
 * Normal put and get operations operate on the top of the stack.
 */

public class SymbolTable //extends XMLStructureAdapter

{
 
   private HashMap<Id, Object> currentTable = new HashMap<Id, Object>(20);
   private ArrayList<HashMap<Id, Object>> tableStack = null;
   
   
   /**
    * default constructor
    */
   public SymbolTable() {
      
   }
  
  
   /**
    * add (i.e. replaces) and Object with specified object Id, 
    * within the current top table
    */
   public final void put(String objectIdString, Object obj) {
      currentTable.put(Id.forName(objectIdString), obj);
   }  

   /**
    * add (i.e. replaces) and Object with specified object Id, 
    * within the current top table
    */
   public final void put(Id objectId, Object obj) {
      currentTable.put(objectId, obj);
   }  

   /**
    * retrieves an Object from the current top table
    */
   public final Object get(String objectIdString) {
      return currentTable.get(Id.forName(objectIdString));
   }

   /**
    * retrieves an Object from the current top table
    */
   public final Object get(Id objectId) {
      return currentTable.get(objectId);
   }
   

   /**
    * pushes the current top table, and allocates a new top table.
    * If clone is true, the old top table is cloned, else a new, empty
    * table is allocated.
    */
   public final void push(boolean clone) {
      if (tableStack == null) tableStack = new ArrayList<HashMap<Id, Object>>(10);
      tableStack.add(currentTable);
      if (clone) {
         currentTable = new HashMap<Id, Object>(currentTable);  
      } else {
         currentTable = new HashMap<Id, Object>();
      }
   }

 
   /**
    * pushes the current top table, and allocates a new empty top table.
    */
   public final void push() {
      push(false);
   }
   
   /**
    * pops the current top table from the stack
    */
   public final void pop() {
      if (tableStack == null || tableStack.size() == 0) {
         throw new IllegalStateException("pop of empty SymbolTable stack");
      }
      int len = tableStack.size();
	   currentTable =  tableStack.get(len - 1);
	   tableStack.remove(len - 1);
   }
   
   /**
    * returns a String representation, listing the contents of all tables
    */
   public String toString() {
        if (tableStack == null) return "[null SymbolTable stack]";   
        StringBuffer buf = new StringBuffer("SymbolTableStack[");   
        Iterator iter = tableStack.iterator();
        while (iter.hasNext()) {
           buf.append("\n{");
           HashMap tb = (HashMap)iter.next();
           buf.append(tb.toString());
           buf.append("}");
        }
        buf.append("\n]"); 
        return buf.toString();
   }
 
   
   
}
