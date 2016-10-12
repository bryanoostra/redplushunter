package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Use given suggestion, executed by Character Agent
* Protege name: UseSuggestion
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class UseSuggestion implements AgentAction {

   /**
   * the suggestion (e.g. a goal or action to be performed)
* Protege name: suggestion
   */
   private FabulaElement suggestion;
   public void setSuggestion(FabulaElement value) { 
    this.suggestion=value;
   }
   public FabulaElement getSuggestion() {
     return this.suggestion;
   }

}
