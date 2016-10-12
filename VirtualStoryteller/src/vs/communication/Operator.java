package vs.communication;

import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Something that can change the world state
* Protege name: Operator
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class Operator extends FabulaElement{ 

   /**
* Protege name: starttime
   */
   private int starttime;
   public void setStarttime(int value) { 
    this.starttime=value;
   }
   public int getStarttime() {
     return this.starttime;
   }

   /**
* Protege name: endtime
   */
   private int endtime;
   public void setEndtime(int value) { 
    this.endtime=value;
   }
   public int getEndtime() {
     return this.endtime;
   }

   /**
   * Instrument of the operator
* Protege name: instrument
   */
   private String instrument;
   public void setInstrument(String value) { 
    this.instrument=value;
   }
   public String getInstrument() {
     return this.instrument;
   }

   /**
   * Patiens of the operator
* Protege name: patiens
   */
   private String patiens;
   public void setPatiens(String value) { 
    this.patiens=value;
   }
   public String getPatiens() {
     return this.patiens;
   }

   /**
   * Target of the operator
* Protege name: target
   */
   private String target;
   public void setTarget(String value) { 
    this.target=value;
   }
   public String getTarget() {
     return this.target;
   }

   /**
   * Agens of the operator
* Protege name: agens
   */
   private String agens;
   public void setAgens(String value) { 
    this.agens=value;
   }
   public String getAgens() {
     return this.agens;
   }

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

   /**
   * whether the Operator was successful (set by Plot Agent when communicating perceptions)
* Protege name: isSuccessful
   */
   private boolean isSuccessful;
   public void setIsSuccessful(boolean value) { 
    this.isSuccessful=value;
   }
   public boolean getIsSuccessful() {
     return this.isSuccessful;
   }

}
