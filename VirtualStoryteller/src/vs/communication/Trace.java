package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Process a trace message (obsolete)
* Protege name: Trace
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class Trace implements AgentAction {

   /**
* Protege name: traceInformation
   */
   private TraceInformation traceInformation;
   public void setTraceInformation(TraceInformation value) { 
    this.traceInformation=value;
   }
   public TraceInformation getTraceInformation() {
     return this.traceInformation;
   }

}
