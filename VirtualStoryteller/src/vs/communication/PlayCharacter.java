package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Commit oneself to play given part in the story, executed by Character Agent
* Protege name: PlayCharacter
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class PlayCharacter implements AgentAction {

   /**
   * the character info for the request
("do you want to join the story as [characterinfo]"?)
* Protege name: characterInfo
   */
   private CharacterInfo characterInfo;
   public void setCharacterInfo(CharacterInfo value) { 
    this.characterInfo=value;
   }
   public CharacterInfo getCharacterInfo() {
     return this.characterInfo;
   }

}
