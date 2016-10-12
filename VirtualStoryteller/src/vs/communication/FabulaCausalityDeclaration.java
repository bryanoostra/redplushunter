package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * FabulaCausalityDeclaration(x):
x is a fabula causality declaration
* Protege name: FabulaCausalityDeclaration
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class FabulaCausalityDeclaration implements Predicate {

   /**
* Protege name: fabulaCausality
   */
   private FabulaCausality fabulaCausality;
   public void setFabulaCausality(FabulaCausality value) { 
    this.fabulaCausality=value;
   }
   public FabulaCausality getFabulaCausality() {
     return this.fabulaCausality;
   }

}
