/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Console.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.22  2005/10/20 10:06:24  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.21  2004/11/18 10:51:26  zwiers
// timestamp option added
//
// Revision 1.20  2004/10/21 12:14:41  zwiers
// exit without frame.dispose
//
// Revision 1.19  2004/10/21 07:35:18  zwiers
// output redirection improved
//
// Revision 1.18  2004/10/20 15:55:37  zwiers
// dispose frame removed
//
// Revision 1.17  2004/10/19 20:49:52  zwiers
// print limit introduced
//
// Revision 1.16  2004/10/16 20:27:54  zwiers
// jdk1.5 patch: show() replaced by setVisible(true)
// Revision 1.14  2004/10/13 08:28:45  zwiers
// static init for TextArea removed
//
// Revision 1.7  2003/08/05 13:51:52  zwiers
// terminateClasss method replaces static terminate method
//
// Revision 1.6  2003/04/15 12:44:33  zwiers
// terminate method changed into terminateClass

package parlevink.util;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


/**
 * Console is an class defining a "System console" window.
 * This console is used by default for showing trace messages.
 * Closing the Console window will cause the system to terminate.
 * The Console class has a large number of <it>static</it> methods,
 * with the intention that the single Console instance can be controlled
 * via these static methods.
 * For instance, to append a text line, use something like:
 * Console.println("hello, console world");
 * A Console window will not be shown until it is actually used.
 * If a console should be visible immediately, one can use, for instance,
 * Console.setVisible(true); or Console.clear();
 * Console windows have three buttons:
 * "Clear" : clears the contents of the text area.
 * "Close" : closes the Console window and terminates the console agent,
 *           but does <it>not</it> terminate the system.
 * "Quit"  : terminates the complete system.
 * Closing the Console window by clicking the "X" button has
 * the same effect as "Quit", that is, the system will terminate and exit.
 *
 * The Console has a number of methods intended for "remote control":
 * close()   : Closes the Console frame; NO termination or exit action implied
 * terminate()  : does not really terminate the Console itself, 
 *                but rather forwards the terminate signal to the Terminator object,
 *                provided that a Terminator object has been registered by calling setTerminator(Terminator t).
 *                During this "system termination phase", the Console remains in operation.
 * exit()       : First executes terminate(), therafter exits the Java Virtual Machine.
 * setBounds(x, y, w, h) : sets the location (x, y) and size (w, h) of the Console Frame
 * setLocation(x, y) : sets the location (x, y) of the Console Frame
 * setSize(w, h) : sets the  size (w, h) of the Console Frame
 * setTitle(String title) : sets the JFrame title of the Console frame.
 * setVisible(boolean isVisible) : makes the Console JFrame visible or invisible
 * setEnabled(boolean mode) : enables or disbales the Console. When disabled, output by
 * means of e.g. println() are ignored.
 * toBack() : moves the Console JFrame to the "back", i.e. behind other windows.
 * toFront() : moves the Console JFrame to the "front", i.e. in front of other windows.
 * setBackground(Color c) : sets the background Color for the Console window.
 * setTextColor(Color c) : sets the text color for the text area.
 *
 * Also there are a number of methods for manipulating the actual text contents of the Console's JFrame:
 * clear() : clears the text area.
 * setText(String newText) : clears the textarea and prints "newText"
 * print(String text) : appends text to the text area.
 * println(String text) : appends text plus a "newline" to the textarea.
 *
 */
public class Console
{

   public static final int DEFAULT_WIDTH  = 400;  // default width of the Console frame.
   public static final int DEFAULT_HEIGHT = 300;  // default height of the Console frame.
   public static final int DEFAULT_XOFF   = 10;  // default x-offset for the Console frame.
   public static final int DEFAULT_YOFF   = 20;  // default y-offset for the Console frame.
   public static final int XLEFTMARGIN   = 5;    // margin for the textarea
   public static final int XRIGHTMARGIN  = 5;    // margin for the textarea
   public static final int YTOPMARGIN    = 25;   // margin for the textarea
   public static final int YBOTTOMMARGIN = 0;    // margin for the Clear button
   public static final int BUTTONWIDTH   = 50;   // width of buttons.
   public static final int BUTTONHEIGHT  = 20;   // height of buttons.
   public static final int CHECKBOXWIDTH   = 65;   // width of "enabled" checkbox.
   public static final String DEFAULT_TITLE = "Console"; // default Console title.
   public static final int DEFAULT_PRINT_LIMIT = 10000; // default max nr of lines that are printed
 
   private static int width    = DEFAULT_WIDTH;
   private static int height   = DEFAULT_HEIGHT;
   private static int xoff     = DEFAULT_XOFF;
   private static int yoff     = DEFAULT_YOFF;
   private static String title = DEFAULT_TITLE;

   //private static Terminator terminator;  
   
   private static ArrayList<Terminator> terminatorList;
   private static boolean classTerminated = false; 
   private static TextArea text;                 
   private static Button clearButton, closeButton, quitButton;
   private static Checkbox enabledBox;
   private static Frame frame;
   private static boolean enabled = true; // determines whether text will be added
                                          // and whether delays will be executed.
   private static boolean frameEnabled = true; // show a JFrame when true, use System.out if false.                                          
   private static ConsoleListener consoleListener;
   private static PrintStream out = null;
   private static int printLimit = DEFAULT_PRINT_LIMIT;
   private static int printCounter = 0;
   
   private static String timeFormat = "h:m:s";
   private static boolean timestampsEnabled = false;
   
   /**
    * specifies the format to be used for timestamps.
    * The default is "h:m:s", which print hours, minutes,
    * and seconds separated by colon characters. 
    * See parlevink.util.TimeValue for other formats
    */
   public static void setTimeFormat(String format) {
      timeFormat = format;
   }
  

   /**
    * enables or disables printing of timestamps
    *
    */
   public static void setTimestampsEnabled(boolean enabled) {
      timestampsEnabled = enabled;
   }   
   

   /**
    * sets the max number of lines that will be printed on the Console;
    * if limit is negative, the printLimit is set to Integer.MAX_VALUE (2^31-1),
    * so effectively no limit.
    */
   public static void setPrintLimit(int limit) {
      if (limit >= 0) {
         printLimit = limit;
      } else {
         printLimit = Integer.MAX_VALUE;  
      }
   }

   /**
    * returns the number of lines that can still be printed, 
    * without reaching the print limit
    */
   public static int getPrintLimit() {
      return printLimit-printCounter;
   }

   private static boolean printLimitReached() {
       if (printCounter > printLimit) return true;
       if (printCounter < printLimit) return false; 
       printCounter++;                
       if (consoleListener != null) consoleListener.println("Console printlimit reached  (" + printLimit + ")");      
       if (!enabled) return true;
       if (checkFrame()) {
          text.append("Console printlimit reached  (" + printLimit + ")\n");
       } else {
          System.out.println("Console printlimit reached  (" + printLimit + ")");
       }
       return true;

   }

   /**
    * static method for closing the Console window. (No terminate or exit though)
    * No effect if frame == null;
    * It does <it>not</it> terminate a possible running console <it>agent.</it>
    * if reset== true, the title, size and location are reset to default values.
    */
   public static void close(boolean reset) {
    if (frame != null) {
          frame.dispose();
          frame = null;
       }
    if (reset) reset();
   }


   /**
    * static method for closing the Console window.
    * No effect if frame == null;
    * It does <it>not</it> terminate a possible running console <it>agent.</it>
    * title, size, and location are reset to default values.
    */
   public static void close() {
      close(true);
   }

   /**
    * resets title, size, and location to default values.
    */
   public static void reset() {
      width    = DEFAULT_WIDTH;
      height   = DEFAULT_HEIGHT;
      xoff     = DEFAULT_XOFF;
      yoff     = DEFAULT_YOFF;
      title    = DEFAULT_TITLE;
   } 
   
   /**
    * The first "terminateClass" call executes the Terminator, if it is set.
    * (Further "terminateClass" calls are ignored.)
    * The Java Virtual Machine is not exited, and also the Console
    * itself is not "stopped". This implies that processes that are handled by the
    * Terminator can still use the Console for output.
    * (See also the exit() methods for exiting the Java Virtual Machine) 
    */
   public static  boolean terminateClass() {
      //System.out.println("Console terminating..");
      Console.println("Console.terminateClass...");
      if (classTerminated) {
         System.out.println("second terminate ignored"); 
         return true;
      }
      classTerminated = true;
      boolean result = true;
   	  //println("Terminate signal received..");
      if (terminatorList != null) {
      	 println("exec terminators");
      	 Iterator iter = terminatorList.iterator(); 
      	 while (iter.hasNext()) {
      	    Terminator terminator = (Terminator) iter.next();
      	    println("exec terminator");
             boolean properlyTerminated = terminator.terminate();  
             if (! properlyTerminated) {
                 result = false;
                 println("Console: improper termination"); 
             }
          }
         println("Console.terminateClass finished");       
      } else {
      	 //println("null terminator");
      }
      return result;
   }

   /**
    * First calls "terminateClass(),
    * and thereafter exits the Java Virtual Machine.
    * "exitCode" is the value returned to the OS.
    */
   public static  void exit(int exitCode) {
      println("Console.exit");
      boolean properlyTerminated = terminateClass();
      if (!properlyTerminated) {
         Console.println("Some agents not terminated");
         delay(1000);
      }
      delay(1);
      // frame.dispose() removed: problems in combination with thread interrupt
      // in AgentAdapter, while calling terminate()
      System.exit(exitCode);
   }


   /**
    * First calls "terminateClass(),
    * and thereafter exits the Java Virtual Machine.
    * The value 0 is returned to the OS as "exit code".
    */
   public static void exit() {
      exit(0);
   }


   public static Frame getFrame() {
      return frame;
   }

   /*
    * creates or recreates the output window for the console.
    */   
   private static void createFrame() {
      if (frame != null) frame.dispose();
      //System.out.println("Console agentName: " + agentName);
      frame = new Frame();// {
//          public boolean isFocusTraversable() { return true; }  
//      }
      frame.setFocusable(true);;   
      frame.setVisible(true); // does not show as yet.toFront
      frame.setBackground(Color.lightGray);
      frame.setResizable(true);
      frame.setBounds(xoff, yoff, width, height);
      frame.setTitle(title);
      frame.setLayout(null);
      frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) { windowClose(); } 
        } );

      text = new TextArea();      
      frame.add(text);

      enabledBox = new Checkbox("Enabled", true);
      enabledBox.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
             enabled = enabledBox.getState();
          } 
        } );
      frame.add(enabledBox);      
      
      clearButton = new Button("Clear");
      clearButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             clear();
          } 
        } );
      frame.add(clearButton);

      closeButton = new Button("Close");
      closeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             close(false);
          } 
        } );
      frame.add(closeButton);
      
      quitButton = new Button("Quit");
      quitButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             exit();
          } 
        } );
      frame.add(quitButton);

      frame.addComponentListener(new ComponentAdapter(){
          public void componentResized(ComponentEvent e) {
              windowResize();
          }
        } );

      windowResize(); // calculate size and position of textarea and buttons.
      frame.setVisible(true);
   }


   /*
    * Checks whether the Console is enabled or disabled.
    * If enabled, and the Console Frame is also enable3d, 
    * it checks whether a Console window
    * has already been created, and if not, it will
    * open the Console window.
    * If finally a Console Frame is available, true is returned, else false is returned.
    */
   private static synchronized boolean checkFrame() {
     if (! frameEnabled || !enabled) return false;
     if (frame == null)
          createFrame();
     return true;
   }

   /*
    * callback method for closing the Console window.
    * disposes the window frame, and calls exit().
    */
   private static void windowClose() {

      //terminate();
      exit();
   }


   /*
    * closes the window Frame, but does not terminate or exit.
    */
    private static void closeFrame() {
       if (frame != null)  {
          frame.dispose();
       }
       frame = null;
    }      
   
   /*
    * callback method for (re)sizing the Console window.
    */
   private static void windowResize() {
      if (frame == null) return;
      width = frame.getWidth();
      height = frame.getHeight();
      int ew = width - (XLEFTMARGIN + XRIGHTMARGIN); // effective width of textarea.
      int eh = height- (YTOPMARGIN  + YBOTTOMMARGIN + 2*BUTTONHEIGHT); // effective height of textarea.
      int bY = (int) (height - YBOTTOMMARGIN - 1.5*BUTTONHEIGHT); // Button y position.
      int gap = (int) ((width - CHECKBOXWIDTH - 3*BUTTONWIDTH) / 5); // gap between buttons.
      int enabledX = (int) gap;
      int clearX = (int) enabledX + CHECKBOXWIDTH + gap;
      int closeX = (int) clearX + BUTTONWIDTH + gap;
      int quitX  = (int) closeX + BUTTONWIDTH + gap;
      text.setBounds(XLEFTMARGIN, YTOPMARGIN, ew, eh);
      enabledBox.setBounds ( enabledX, bY, CHECKBOXWIDTH, BUTTONHEIGHT);
      clearButton.setBounds( clearX, bY, BUTTONWIDTH, BUTTONHEIGHT);
      closeButton.setBounds( closeX, bY, BUTTONWIDTH, BUTTONHEIGHT);
      quitButton.setBounds ( quitX,  bY, BUTTONWIDTH, BUTTONHEIGHT);                                              
   }


   /**
    * sets the size and location of the console window.
    */
   public static synchronized void setBounds(int x, int y, int w, int h) {
      xoff = x;
      yoff = y;
      width = w;
      height = h;
      if (frame != null) {
         frame.setBounds(xoff, yoff, width, height);
         windowResize();
      }
   }

   /**
    * sets the location of the console window.
    */
   public static synchronized void setLocation(int x, int y) {
      xoff = x;
      yoff = y;
      if (frame != null) {
         frame.setBounds(xoff, yoff, width, height);
         windowResize();
      }
   }

   /**
    * sets the size of the console window.
    */
   public static synchronized void setSize(int w, int h) {
      width = w;
      height = h;
      if (frame != null) {
         frame.setBounds(xoff, yoff, width, height);
         windowResize();
      }
   }

   /**
    * sets the title of the console window.
    */
   public static synchronized void setTitle(String s) {
      title = s;
      if (frame != null) frame.setTitle(title);
   }






   /**
    * determines whether the Console window is visible or not.
    * When visible is true, a new console window is opened if necessary.
    * When visible is false, the console window is made invisible, if 
    * such a console window is present; no new console will be opened.
    */
   public static void setVisible(boolean visible) {
      if (frameEnabled && visible) checkFrame();
      if (frame != null)  frame.setVisible(visible);
   }

   /**
    * clears the Console window.
    * Also resets the printCounter to 0
    * Ignored when the Console is disabled.
    */
   public static void clear() {
      if (!enabled) return;
      printCounter = 0;
      if (checkFrame())
         text.setText("");
   }



   /**
    * replaces the current text in the Console window by a new text.
    * Ignored when the Console is disabled.
    */
   public static void setText(String s) {
     if (printLimitReached()) return;
     if (!enabled ) return;
     if (checkFrame()) {
        if (s == null) clear();
        else text.setText(s);
     }
     if (out != null) {
         out.println("=setText===============");
         out.println(s);
         out.println("=======================");
     } 
     printCounter++; // count as one print action
   }

   /**
    * appends s to the text in the Console window.
    * A newline character is <it> not</it> automatically added.
    * This method is identical to append(); 
    * it has been added so that "System.out.print()" calls
    * can be replaced easily by "Console.print()" calls.
    * Ignored when the Console is disabled.
    */
   public static void print(String s) {
      if (s == null || printLimitReached()) return;
      if (consoleListener != null) consoleListener.print(s);
      if (!enabled) return;
      if (checkFrame()) {
          text.append(s);
      } 
      if (out != null) {
         out.print(s);
      } 
      printCounter++;
   }

   /**
    * appends a newline to the text in the Console window.
    * Ignored when the Console is disabled.
    */
   public static void println() {

      if (printLimitReached()) return;
      if (consoleListener != null) consoleListener.println();      
      if (!enabled) return;
      if (checkFrame()) {
          text.append("\n");
      } 
      if (out != null) {
         out.println();
      } 
      printCounter++;
   }


   /**
    * appends s to the text in the Console window.
    * A newline character is automatically added.
    * This method is identical to appendln(); 
    * it has been added so that "System.out.println()" calls
    * can be replaced easily by "Console.println()" calls.
    * Ignored when the Console is disabled.
    */
   public static void println(String s) {
      if (printLimitReached()) return;
      if (s==null) {
         println();
      } else {
         String spr = (timestampsEnabled ? TimeValue.getCurrentTime(timeFormat) + " : " + s : s);
         
         if (consoleListener != null) consoleListener.println(spr);
         if (!enabled) return;
         if (checkFrame()) {
            text.append(spr); 
            text.append("\n");
         } 
         if (out != null) {
            out.println(spr);
         } 
     }
     printCounter++;
   }

   /**
    *
    */
   public static void println(boolean test, String strue, String sfalse) {
      if (printLimitReached()) return;
      println((test ? strue : sfalse));

   }

   /**
    *
    */
   public static void println(boolean test, String s) {
      if (printLimitReached()) return;
      if (test) println(s);
      
   }





 
   /**
    * moves the Console window "to the back".
    * Will <it>not</it> open a new console window.
    */
   public static void toBack() {
     if (!enabled || !frameEnabled) return;
     if (frame != null) frame.toBack();
   }

   /**
    * moves the Console window "to the front".
    * If necessary, a new Console window is opened.
    */  
   public static void toFront() {
     if (!enabled || !frameEnabled) return;
     checkFrame();
     if (frame != null)  {
        frame.toFront();
     }
   }

   /**
    * "Utility" method for executing a delay of "d" milliseconds. 
    * Effectively the same as executing Thread.sleep(), but catches and ignores Exceptions.
    */
   public static void delay(long d) {
      try {
          Thread.sleep(d);
      } catch (Exception e) {}
   }



   /**
    * sets the background color of the console frame.
    */
   public static void setBackground(Color c) {
       if (!enabled || !frameEnabled) return;
       if (frame == null) return;
       frame.setBackground(c);
       enabledBox.setBackground(c);
       clearButton.setBackground(c);
       //clearButton.setForeground(c);
       closeButton.setBackground(c);
       quitButton.setBackground(c);
       //text.setBackground(c);
   }

   /**
    * sets the text color of the console frame.
    */
   public static void setTextColor(Color c) {
       if (!enabled || !frameEnabled) return;
       text.setForeground(c);
   }


   /**
    * sets the Terminator.
    * The Terminator, i.e. an implementation of the Terminator interface,
    * is called when the Console executes its "terminate" method. 
    * (terminate is called either directly, or implicitly when exit() is called, 
    * or the quit button is pressed)
    * The general idea is that the Terminator should stop all running processes
    * and takes care of "clean termination". However, it should not stop
    * the Java Virtual Machine for instance by calling System.exit.
    */
    public static void addTerminator(Terminator t) {
      if (terminatorList == null) terminatorList = new ArrayList<Terminator>(4);
      try {
    	  terminatorList.add(t);
    	} catch (Exception e) {}
    }


   /**
    * determines whether the Console is enabled or disabled.
    * When disabled, the text area will not change, for instance,
    * no text will be appended, printed etc.
    * The Console window, if visible, will <it>not</it> disappear.
    */
   public static void setEnabled(boolean mode) {
      enabled = mode;
   }

   /**
    * determines whether the Console Window is enabled or disabled.
    * When disabled, Console output is printed on System.out, else
    * it appears in the Console window.
    */
   public static void setFrameEnabled(boolean mode) {
      frameEnabled = mode;
      if (!frameEnabled) closeFrame();
   }

   /**
    * Sets the PrintStream that is used when the Console Frame is not enabled.
    * The default is System.out.
    */
   public static void setPrintStream(PrintStream s) {
      out = s;
   }

   public static final int SYSTEMOUT = 1;
   public static final int SYSTEMERR = 2;
   public static final int WINDOW = 4;
   public static final int NONE = 0;
   

   /**
    *
    */
   public static void toChannels(int channel) {
      if ( (channel & SYSTEMOUT) != 0) {
         setPrintStream(System.out);
      } else if ((channel & SYSTEMERR) != 0) {
         setPrintStream(System.err);  
      } else {
         setPrintStream(null);  
      }
      if ( (channel & WINDOW) != 0) {
         setFrameEnabled(true);
      } else {
         setFrameEnabled(false);
      }   
   }  
    
   /**
    * disables (and if necessary disposes) the Console window,
    * thereafter, Console output is redirected to System.out
    */
   public static void toSystemOut() {
      toChannels(SYSTEMOUT);
   }

   /**
    * disables (and if necessary disposes) the Console window,
    * thereafter, Console output is redirected to System.err
    */
   public static void toSystemErr() {
       toChannels(SYSTEMERR); 
   }

   /**
    * disables the printstream output, and enables the Console
    */
   public static void toConsoleWindow() {
       toChannels(WINDOW);
   }   

   /**
    *
    */
   public static void setConsoleListener(ConsoleListener cl) {
       consoleListener = cl;  
   }


public interface ConsoleListener
{   
   /**
    * method for closing the TextBox
    *
   public void close();

   /**
    * sets the size and location of the console window.
    *
   public void setBounds(int x, int y, int w, int h);

   /**
    * sets the location of the console window.
    *
   public void setLocation(int x, int y);

   /**
    * sets the size of the console window.
    *
   public void setSize(int w, int h);

   /**
    * sets the title of the console window.
    *
   public void setTitle(String s);

   /**
    * determines whether the Console window is visible or not.
    * When visible is true, a new console window is opened if necessary.
    * When visible is false, the console window is made invisible, if 
    * such a console window is present; no new console will be opened.
    *
   public void setVisible(boolean visible);

   /**
    * clears the Console window.
    * Ignored when the Console is disabled.
    *
   public void clear();

   /**
    * appends s to the text in the Console window.
    * A newline character is <it> not</it> automatically added.
    * This method is identical to append(); 
    * it has been added so that "System.out.print()" calls
    * can be replaced easily by "Console.print()" calls.
    * Ignored when the Console is disabled.
    */
   public void print(String s);

   /**
    * appends s to the text in the Console window.
    * A newline character is automatically added.
    * This method is identical to appendln(); 
    * it has been added so that "System.out.println()" calls
    * can be replaced easily by "Console.println()" calls.
    * Ignored when the Console is disabled.
    */
   public void println(String s);


   /**
    * appends a newline to the text in the Console window.
    * Ignored when the Console is disabled.
    */
   public void println();
 
   /**
    * moves the Console window "to the back".
    * Will <it>not</it> open a new console window.
    *
   public void toBack();

   /**
    * moves the Console window "to the front".
    * If necessary, a new Console window is opened.
    *  
   public void toFront();


   /**
    * determines whether the Console is enabled or disabled.
    * When disabled, the text area will not change, for instance,
    * no text will be appended, printed etc.
    * The Console window, if visible, will <it>not</it> disappear.
    *
   public  void setEnabled(boolean mode);

   /**
    * sets the background color of the console frame.
    *
   public void setBackground(Color c);

   /**
    * sets the text color of the console frame.
    *
   public void setTextColor(Color c);
 
 /**/
}

   /*
    * for testing purposes only
    */
   public static void main(String[] arg) {
      //toSystemErr();
     
      setVisible(true);
      //Console.println("show frame...");
      //Console.println("NUMPAD 8 = " + KeyEvent.VK_NUMPAD8);
      //Console.println("up arrow = " + KeyEvent.VK_UP);
      //InputStateTracker h = new InputStateTracker(Console.getFrame(), InputStateTracker.MOUSELISTENER | InputStateTracker.KEYLISTENER);
      
      
//      Package utilpack = Package.getPackage("parlevink.util");
//      String spec = utilpack.getSpecificationTitle();
//      if (spec == null) spec = "no spec ";
//      String cv = "0.9";
//      boolean compatible = false;
//      String specversion = utilpack.getSpecificationVersion();
//      if (specversion != null)  compatible = utilpack.isCompatibleWith(cv);       
//      if (specversion == null) specversion = "X";
//    
      println("Console test");
//      println();
//      println("specification: " + spec + ", version: " + specversion);
//      println ("Compatible with version " + cv + ": " + compatible); 
      setTimestampsEnabled(true);
      setPrintLimit(30);
      for (int i=0; i<40; i++) {
         println("line : " + i);
      }      
   }
  
    public static void captureError() {
        System.setErr(new PrintStream(new ConsoleOutputStream()));
    }
    
    public static void captureOut() {
        System.setOut(new PrintStream(new ConsoleOutputStream()));
    }
    
    static class ConsoleOutputStream extends OutputStream {
        public void write(int b) throws IOException {
            text.append("" + (char)b);
        }
    }
}

