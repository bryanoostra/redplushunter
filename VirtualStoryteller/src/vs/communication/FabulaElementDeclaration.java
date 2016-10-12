package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * FabulaElementDeclaration(X):
X is a fabula element that just 'occurred' in the agent
* Protege name: FabulaElementDeclaration
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class FabulaElementDeclaration implements Predicate {

   /**
* Protege name: fabulaElement
   */
   private FabulaElement fabulaElement;
   public void setFabulaElement(FabulaElement value) { 
    this.fabulaElement=value;
   }
   public FabulaElement getFabulaElement() {
     return this.fabulaElement;
   }

}
