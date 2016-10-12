package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
   * Predicate to indicate that the next round starts
* Protege name: NextRound
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class NextRound implements Predicate {

   /**
   * The round number in the simulation in time steps (PA is responsible)
* Protege name: roundNumber
   */
   private int roundNumber;
   public void setRoundNumber(int value) { 
    this.roundNumber=value;
   }
   public int getRoundNumber() {
     return this.roundNumber;
   }

}
