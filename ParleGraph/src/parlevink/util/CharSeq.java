/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: CharSeq.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2003/10/27 09:13:23  zwiers
// initial version
//


package parlevink.util;
import java.util.*;
import java.io.*;
import parlevink.xml.*;


/**
 *
 * A CharSeq is a wrapper for a CharSequence plus a pointer in that sequence.
 * It defines methods for reading and advancing the pointer.
 */
 
public final class CharSeq {

   public CharSeq() {     
   }

   public CharSeq(CharSequence seq) {
      this.seq = seq;
      seqLen = seq.length();
      chPos = -1;
      isReader = false;
      next();
   }
 
   public CharSeq(Reader in) {
      this.in = in;  
      chPos = -1;     
      isReader = true;
      next();
   }
   
   public final void next()   {
      if (ch < 0) return;
      if (isReader) {
         try {
            ch = in.read();  // will set ch to -1 when end-of-stream has been reached.
            chPos++; 
         } catch (IOException ioe) {
            throw new RuntimeException("IOEXception: " + ioe);  
         }
      } else {
         if (++chPos < seqLen) {
            ch = seq.charAt(chPos);
         } else {
            ch = -1;  
         }
      }
   }


   public final void skip(int count) {
      for (int i=0; i<count; i++) next();
   }

   public final boolean eos() {
      return (ch < 0);
   }

    /*
     * skips "blank space" characters. i.e skips ' ', '\n', and '\r' characters.
     */
   public final void skipSpaces() {
      while(ch >= 0 && (ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r'))  {        
         next();
      } 
   }

   public final boolean matchChar(char c) {
      if (ch != c) return false;
      next();
      return true;
   }


   /*
    * matches a string from the current position, and advances chPos, if succesful
    */
   public final boolean match(CharSequence pat) {
      for (int i=0; i< pat.length(); i++) {
         if (ch < 0 || ch != pat.charAt(i)) return false;
         next();
      }  
      return true;
   }

   /*
    *
    */
   public final String readString() {
      int delim = ch;
      StringBuffer buf = new StringBuffer();
      next();
      while (ch >= 0 && ch != delim) {
          buf.append((char)ch);
          next();  
      }
      if (ch >= 0) next();
      return buf.toString();  
   } 


   public String toString() {
       if (isReader) {
           return "CharSeq[<Reader> at position " + chPos + "]";
       } else {
           return ("CharSeq[\"" + seq + "\" at position " + chPos + "]"); 
       }  
   }

   public CharSequence seq;
   public int seqLen;
   
   public Reader in;

   public int ch;
   public int chPos;
   public boolean isReader;
   public static final int EOS = -1;

}