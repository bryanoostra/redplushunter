package vs.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: GoalSchema
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class GoalSchema extends Schema{ 

   /**
* Protege name: opponent
   */
   private String opponent;
   public void setOpponent(String value) { 
    this.opponent=value;
   }
   public String getOpponent() {
     return this.opponent;
   }

   /**
   * Patiens of the operator
* Protege name: patiens
   */
   private String patiens;
   public void setPatiens(String value) { 
    this.patiens=value;
   }
   public String getPatiens() {
     return this.patiens;
   }

   /**
   * Instrument of the operator
* Protege name: instrument
   */
   private String instrument;
   public void setInstrument(String value) { 
    this.instrument=value;
   }
   public String getInstrument() {
     return this.instrument;
   }

   /**
   * Target of the operator
* Protege name: target
   */
   private String target;
   public void setTarget(String value) { 
    this.target=value;
   }
   public String getTarget() {
     return this.target;
   }

   /**
   * Agens of the operator
* Protege name: agens
   */
   private String agens;
   public void setAgens(String value) { 
    this.agens=value;
   }
   public String getAgens() {
     return this.agens;
   }

}
