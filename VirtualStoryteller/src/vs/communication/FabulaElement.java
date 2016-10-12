package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Any element that occurs in fabula (e.g. goals, actions, events)
* Protege name: FabulaElement
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class FabulaElement implements Concept {

   /**
* Protege name: type
   */
   private String type;
   public void setType(String value) { 
    this.type=value;
   }
   public String getType() {
     return this.type;
   }

   /**
   * the Individual that the character represents in the world
* Protege name: individual
   */
   private String individual;
   public void setIndividual(String value) { 
    this.individual=value;
   }
   public String getIndividual() {
     return this.individual;
   }

   /**
   * refers to the entity in the story world that 'has' or 'intends' the fabula element.
* Protege name: character
   */
   private String character;
   public void setCharacter(String value) { 
    this.character=value;
   }
   public String getCharacter() {
     return this.character;
   }

   /**
   * time at which the fabula element was created
* Protege name: time
   */
   private int time;
   public void setTime(int value) { 
    this.time=value;
   }
   public int getTime() {
     return this.time;
   }

}
