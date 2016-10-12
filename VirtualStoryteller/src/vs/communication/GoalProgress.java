package vs.communication;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: GoalProgress
* @author ontology bean generator
* @version 2009/03/16, 15:28:17
*/
public class GoalProgress implements Predicate {

//////////////////////////// User code
public static final int COMPLETED = 0;
public static final int FAILED = 1;
public static final int WAITFOREXPECTATION = 2;
public static final int NEARLYCOMPLETED = 3;
public static final int UNKNOWN = 4;
   /**
   * The status of this goal
* Protege name: goalstatus
   */
   private int goalstatus;
   public void setGoalstatus(int value) { 
    this.goalstatus=value;
   }
   public int getGoalstatus() {
     return this.goalstatus;
   }

   /**
   * The goal the character was performing
* Protege name: goal
   */
   private String goal;
   public void setGoal(String value) { 
    this.goal=value;
   }
   public String getGoal() {
     return this.goal;
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

}
