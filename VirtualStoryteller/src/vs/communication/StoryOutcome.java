package vs.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * The outcome of a goal
* Protege name: StoryOutcome
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class StoryOutcome extends FabulaElement{ 

   /**
   * Which Goal (referenced by its Individual string) this outcome resolves
* Protege name: resolves
   */
   private String resolves;
   public void setResolves(String value) { 
    this.resolves=value;
   }
   public String getResolves() {
     return this.resolves;
   }

}
