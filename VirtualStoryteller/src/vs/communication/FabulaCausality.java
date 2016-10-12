package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: FabulaCausality
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class FabulaCausality implements Concept {

   /**
   * The subject of the link (a string representing the individual
* Protege name: subjectIndividual
   */
   private String subjectIndividual;
   public void setSubjectIndividual(String value) { 
    this.subjectIndividual=value;
   }
   public String getSubjectIndividual() {
     return this.subjectIndividual;
   }

   /**
   * the object of the link (as Individual)
* Protege name: objectIndividual
   */
   private String objectIndividual;
   public void setObjectIndividual(String value) { 
    this.objectIndividual=value;
   }
   public String getObjectIndividual() {
     return this.objectIndividual;
   }

   /**
   * The property as string (i.e. http://...#psi_causes)
* Protege name: causalProperty
   */
   private String causalProperty;
   public void setCausalProperty(String value) { 
    this.causalProperty=value;
   }
   public String getCausalProperty() {
     return this.causalProperty;
   }

}
