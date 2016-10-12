package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * An incoming setting
* Protege name: IncomingSetting
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class IncomingSetting implements Predicate {

   /**
* Protege name: setting
   */
   private StorySettingElement setting;
   public void setSetting(StorySettingElement value) { 
    this.setting=value;
   }
   public StorySettingElement getSetting() {
     return this.setting;
   }

}
