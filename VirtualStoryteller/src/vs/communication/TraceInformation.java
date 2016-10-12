package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Information for the tracer to print
* Protege name: TraceInformation
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class TraceInformation implements Concept {

   /**
* Protege name: traceDepth
   */
   private int traceDepth;
   public void setTraceDepth(int value) { 
    this.traceDepth=value;
   }
   public int getTraceDepth() {
     return this.traceDepth;
   }

   /**
* Protege name: message
   */
   private String message;
   public void setMessage(String value) { 
    this.message=value;
   }
   public String getMessage() {
     return this.message;
   }

   /**
* Protege name: verbosity
   */
   private int verbosity;
   public void setVerbosity(int value) { 
    this.verbosity=value;
   }
   public int getVerbosity() {
     return this.verbosity;
   }

}
