package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * An incoming perception
* Protege name: IncomingPerception
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class IncomingPerception implements Predicate {

   /**
* Protege name: perception
   */
   private StoryPerception perception;
   public void setPerception(StoryPerception value) { 
    this.perception=value;
   }
   public StoryPerception getPerception() {
     return this.perception;
   }

}
