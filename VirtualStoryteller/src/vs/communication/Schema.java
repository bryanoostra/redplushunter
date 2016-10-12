package vs.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * A schema-based fabula element, defined by its head consisting of a type and optional arguments. The idea of a Schema class is that it completely defines the fabula element due to its parameters.
- "class" slot of schema defined by object class
* Protege name: Schema
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class Schema extends FabulaElement{ 

   /**
   * Prolog description of the operator
* Protege name: prologDescription
   */
   private String prologDescription;
   public void setPrologDescription(String value) { 
    this.prologDescription=value;
   }
   public String getPrologDescription() {
     return this.prologDescription;
   }

}
