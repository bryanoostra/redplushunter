/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $
 * @since version 0
 */

// Last modification by: $Author: swartjes $
// $Log: Stopwatch.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.5  2004/08/17 13:34:15  zwiers
// show(String message) method added
//
// Revision 1.4  2004/06/12 21:55:47  zwiers
// comment update
//
// Revision 1.3  2003/08/05 13:50:45  zwiers
// added reset method, modified return types of some other methods
//
// Revision 1.2  2003/04/15 12:46:10  zwiers
// update comments, queue improved
//
// Revision 1.1  2002/12/18 08:46:45  zwiers
// initial version
//
// Revision 1.1  2002/10/21 09:56:19  zwiers
// moved, from agents package to util package
//

package parlevink.util;

import java.util.*;


/**
 * Stopwatch is a class for performing timing.
 * A Stopwatch starts running immediately upon creation.
 * It can at any time be reset to 0 by calling start() or reset().
 * The current stopwatch time is shown on the Console with show();
 * (The latter will not stop or reset the stopwatch; it keeps running)
 * If a series of timeintervals must be measured, it is possible to call
 * showLap() rather than show(). Consecutive showLap() calls show the time
 * between these calls, whereas show() always show the accumulated time.
 * Timing can also be done without Console output, by calling read() instead
 * of show(), or readLap() instead of showLap().
 * Finally, Stopwatch offers a delay(long) method, which has nothing to do with 
 * the stopwatch instances as such. It performs a Thread.sleep(), and catches the
 * annoying exception.
 */
public class Stopwatch  {


   /**
    * create a new numbered Stopwatch.
    * The name is of the form "Stopwatch <n>".
    * It starts running right away.
    */
   public Stopwatch() {
      this.name = "Stopwatch " + (stopwatchcount++);
   }

   /**
    * create a new named Stopwatch.
    * It starts running right away.
    */   
   public Stopwatch(String name) {
      this.name = name;
      reset();
   }

   /**
    * returns the name of this Stopwatch.
    */
   public String getName() {
      return name;
   }

   /**
    * starts/resets the stopwatch, by reading the current system time.
    * The latter value is returned.
    */   
   public long start() {
      return start(false);
   }

   /**
    * starts/resets the stopwatch, by reading the current system time.
    * The latter value is returned.
    * identical to start();
    */   
   public long reset() {
      return start(false);
   }
     
   /**
    * starts/resets the stopwatch, by reading the current system time.
    * The latter is returned.
    * If "show" is true, the current time is also shown on the Console.
    */   
   public long start(boolean show) {
       starttime = System.currentTimeMillis();
       lapstart = starttime;
       if (show) {
           Console.println("start stopwatch " + name + " at " + starttime);
       }
       return starttime;
   }


   /**
    * reads and returns the current time for the stopwatch, relative to the start time,
    * but does not show it on the Console.
    */
   public long read() {
      currenttime = System.currentTimeMillis();
      return (currenttime-starttime);
   }

   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the Console.
    * The time is also returned as a long.
    */
   public long show() {
      currenttime = System.currentTimeMillis();
      Console.println("[" + name + "] time: " + (currenttime-starttime));
      return (currenttime-starttime);
   }

   /**
    * read the current time for the stopwatch, relative to the start time,
    * and show it on the Console.
    * The time is also returned as a long.
    */
   public long show(String message) {
      currenttime = System.currentTimeMillis();
      Console.println("[" + name + "] " + message + " " + (currenttime-starttime));
      return (currenttime-starttime);
   }

   /**
    * reads the "lap" time for the stopwatch, relative to reading the last lap time,
    * or relative to starttime if this is the first readLap call.
    *
    */
   public long readLap() {
      currenttime = System.currentTimeMillis();
      long lap = (currenttime-lapstart);
      lapstart = currenttime;
      return lap;
   }

   /**
    * read the "lap" time for the stopwatch, relative to reading the last lap time,
    * or relative to starttime if this is the first readLap call.
    * and show it on the Console.
    * The lap time is also returned.
    */
   public long showLap() {
      currenttime = System.currentTimeMillis();
      long lap = (currenttime-lapstart);
      Console.println("[" + name + "] lap time: " + lap );
      lapstart = currenttime;
      return lap;
   }

   /**
    * same as Thread.sleep(), but catches and ignores Exceptions.
    */
   public static void delay(long d) {
      try {
          Thread.sleep(d);
      } catch (Exception e) {}
   }

   public static void main(String[] arg) {
      Console.println("Stopwatch test");
      Stopwatch watch1 = new Stopwatch("Watch1");
      watch1.start();
      watch1.delay(2000);
      watch1.showLap();
      watch1.show();
      watch1.delay(3000);
      watch1.show();
      watch1.showLap();
        
   }

   private long starttime = 0;
   private long currenttime = 0;
   private long lapstart = 0;
   private String name;
   private static int stopwatchcount = 1;
} 

