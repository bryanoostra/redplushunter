package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Perform an operator, executed by World Agent
* Protege name: PerformOperator
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class PerformOperator implements AgentAction {

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

}
