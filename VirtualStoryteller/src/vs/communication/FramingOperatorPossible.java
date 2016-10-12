package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Check whether given framing operator is possible
* Protege name: FramingOperatorPossible
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class FramingOperatorPossible implements AgentAction {

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
