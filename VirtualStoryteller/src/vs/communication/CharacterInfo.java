package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * CharacterInfo(x)
x is the individual of the character

Information about the character:
- its name in the story world
* Protege name: CharacterInfo
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class CharacterInfo implements Concept {

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

}
