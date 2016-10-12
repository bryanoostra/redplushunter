package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * The result of an operator's execution (e.g. finished, aborted)
* Protege name: OperatorResult
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class OperatorResult implements Predicate {

   /**
* Protege name: operator
   */
   private Operator operator;
   public void setOperator(Operator value) { 
    this.operator=value;
   }
   public Operator getOperator() {
     return this.operator;
   }

   /**
   * The status of the operator
* Protege name: status
   */
   private OperatorStatus status;
   public void setStatus(OperatorStatus value) { 
    this.status=value;
   }
   public OperatorStatus getStatus() {
     return this.status;
   }

}
