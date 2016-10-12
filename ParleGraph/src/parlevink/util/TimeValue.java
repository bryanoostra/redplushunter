/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: TimeValue.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2004/12/17 16:50:03  herder
// day-of-week (e.g. Sun, Mon, Tue) can now be obtained by argument 'W' in calendarToString
//
// Revision 1.6  2004/11/16 12:33:50  zwiers
// milliseconds format corrected
//
// Revision 1.5  2004/10/20 12:44:34  herder
// DecimalFormat added in calendarToString - for example, calendarToString("h:m:s") now yields 13:01:07 instead of 13:1:7
//
// Revision 1.4  2004/10/11 09:52:35  zwiers
// comment update, dateToString method added
//
// Revision 1.3  2004/10/11 09:44:06  zwiers
// month+1 bug
//
// Revision 1.2  2004/10/11 09:15:52  zwiers
// no message
//
// Revision 1.1  2003/06/06 14:09:07  zwiers
// initial version
//



package parlevink.util;
import java.util.*;
import java.text.*;
import parlevink.util.*;


/**
 * A TimeValue is a wrapper around a time representation in milliseconds,
 * in the form of a "long". It is possible to decompose this value in
 * "fields": year, month, day, hours, minutes, seconds, milliseconds.
 * In the latter case an internal GregorianCalendar object is allocated and used
 * for the conversions. This is supposed to deal with all complicated matters
 * related to dates, like leap years, leap seconds, etc. 
 * 
 */
public class TimeValue
{
   
      /**
    * creates a new TimeValue with the current time in milliseconds
    * after January 1, 1970 00:00:00 GMT.
    */
   public TimeValue() {
       this.millis = System.currentTimeMillis();
   }
   
   
   /**
    * creates a new TimeValue from a specification in milliseconds
    * after January 1, 1970 00:00:00 GMT.
    */
   public TimeValue(long millis) {
       this.millis = millis;
   }


   /**
    * creates a new TimeValue from a specification in the form of a "date",
    * specified by means of a "GregorianCalendar" object.
    */
    public TimeValue(Date date) {
       millis = date.getTime();
   }

   /**
    * creates a new TimeValue from a specification in the form of a "date",
    * specified by means of a "GregorianCalendar" object.
    */
    public TimeValue(GregorianCalendar date) {
       gregor = date;
       millis = gregor.getTimeInMillis();
   }
 
   /**
    * creates a new TimeValue from a specification in the form of a "date",
    * specified by means year, month, day, hour, minutes, seconds,
    * set for the default time zone with the default locale.
    * The specification of these fields is exactly like that for GregorianCalendar constructors.
    * Note in particular that the month field is zero based, i.e.
    * TimeValue.JANUARY == GregorianCalendar.JANUARY == 0.
    * On the other hand, the "date" field, i.e. the Day-Of-Month, must be between 1 and 31.
    * Hours are between 0 and 23, minutes and seconds are between 0 and 59. 
    */
   public TimeValue(int year, int month, int date, int hour, int minute, int second) 
   {
       gregor = new GregorianCalendar(year, month, date, hour, minute, second);
       millis = gregor.getTimeInMillis();
   }
 
   /**
    * creates a new TimeValue from a specification in the form of a "date",
    * specified by means year, month, day, hour, minutes, seconds and milliseconds,
    * set for the default time zone with the default locale.
    * Apart from the milliseconds field, the specification of these fields 
    * is exactly like that for the GregorianCalendar constructors,
    * Note in particular that the month field is zero based, i.e.
    * TimeValue.JANUARY == GregorianCalendar.JANUARY == 0.
    * On the other hand, the "date" field, i.e. the  Day-Of-Month, must be between 1 and 31.
    * Hours are between 0 and 23, minutes and seconds are between 0 and 59. 
    * Milliseconds are between 0 and 999
    */
   public TimeValue(int year, int month, int date, int hour, int minute, int second, int milliseconds) 
   {
       gregor = new GregorianCalendar(year, month, date, hour, minute, second);
       gregor.add(GregorianCalendar.MILLISECOND, milliseconds);
       millis = gregor.getTimeInMillis();
   }
   
   /* initializes the gregor Calendar member */
   private void convert() {
      if (gregor == null) {
         gregor = new GregorianCalendar(0, 0, 0); // do not consult System.currentTimeMillis()
         gregor.setTimeInMillis(millis);
      }
   }
   
   /**
    * return the time value in milliseconds.
    * This is not just the "millisecond" fraction, but rather
    * the complete time, like the value returned by System.currentTimeMillis()
    * Note that this differs from "getMilliSeconds()".
    * (The latter delivers the millisecond fraction 
    * in a year-month-day-hour-minute-second-milliseconds decomposition.
    */
   public final long getTimeMillis() {
      return millis;  
   }


   /**
    * return the "year" part of the time value, as an integer like 2003.
    */
   public final int getYear() {
       if (gregor == null) convert(); 
       return gregor.get(GregorianCalendar.YEAR);  
   }

   /**
    * return the "month" part of the time value, as an integer between
    * 0 and 11. There are constants like TimeValue.January (=0) to denote
    * these values.
    */ 
   public final int getMonth() {
       if (gregor == null) convert(); 
       return gregor.get(GregorianCalendar.MONTH);  
   }

   /**
    * return the "day of the month" part of the time value, as an integer between
    * 1 and 31. 
    */ 
   public final int getDayOfMonth() {
       if (gregor == null) convert(); 
       return gregor.get(GregorianCalendar.DAY_OF_MONTH);  
   } 

   /**
    * return the "hours" part of the time value, between 0 and 23.
    * (The "HOUR_OF_DAY" field of a Calendar)
    */ 
   public final int getHours() {
       if (gregor == null) convert(); 
       return gregor.get(GregorianCalendar.HOUR_OF_DAY);  
   } 

   /**
    * return the "minutes" part of the time value, between 0 and 59.
    */    
   public final int getMinutes() {
       if (gregor == null) convert(); 
       return gregor.get(GregorianCalendar.MINUTE);  
   } 

   /**
    * return the "seconds" part of the time value, between 0 and 59.
    * (Occasionally, the seconds part could be 60 or even 61, 
    * when so called "leap seconds" are added. See the java docs for
    * Date and Calendar)
    * The "seconds" part does not include a decimal fraction for milliseconds.
    */    
   public final int getSeconds() {
       if (gregor == null) convert(); 
       return gregor.get(GregorianCalendar.SECOND);  
   } 

   /**
    * return the "millseconds" fraction of the time value.
    * Note that this differs from "getTimeMillis()".
    * (The latter delivers the complete time, not just the 
    * millisecond fraction).
    */    
   public final int getMilliSeconds() {
       if (gregor == null) convert(); 
       return gregor.get(GregorianCalendar.MILLISECOND);  
   } 


   /**
    * returns the date and time in the form of a String, formatted
    * like 2/11/2003 10:08:59.123 i.e. date followed by time, including milliseconds
    * after the decimal dot.
    */    
   public String toString() {
       if (gregor == null) convert();
       return calendarToString(gregor);
   }


   /**
    * returns the date and time in the form of a String, formatted
    * like 2/11/2003 10:08:59.123 i.e. date followed by time, including milliseconds
    * after the decimal dot. See calendarToString for the format.
    */    
   public String toString(String format) {
       if (gregor == null) convert();
       return calendarToString(gregor, format);
   }

   /**
    * Converts the Calendar to a String formatted like 2/11/2003 10:08:59.123,
    * i.e. day-month-year fields separated by '/' characters,
    * followed by two-digit hours, minutes, seconds, separated by ':' characters,
    * followed by milliseconds as a three-digit decimal fraction.
    */    
   public static final String calendarToString(Calendar cal) {
      return calendarToString(cal, DEFAULT_FORMAT);
   }



   /**
    * converts a Calendar value to a String, using a format:
    * In principle, characters within format are included in the result String, 
    * except for the following special characters:
    * Y is replaced by a year field, M by a month field, D by a day-of-month field,
    * W by a day-of-week field (i.e. Sun, Mon, Tue etc)
    * h by the hour (24 -hour based), m by the minutes, s by the seconds.
    * a second m character after the seconds is allowed and denotes a three digit millisecond field.
    */
   public static final String calendarToString(Calendar cal, String format) {
      StringBuffer b = new StringBuffer();
      boolean seconds = false;
      int tempTime;
      for (int i=0; i<format.length(); i++) {
          char c = format.charAt(i);
          if (c=='Y') {
            b.append(cal.get(Calendar.YEAR));
          } else if (c=='M') {
            b.append(twodecFormat.format(cal.get((Calendar.MONTH))+1));            
          } else if (c=='D') {
            b.append(twodecFormat.format(cal.get(Calendar.DAY_OF_MONTH)));               
          } else if (c=='h') {
            b.append(twodecFormat.format(cal.get(Calendar.HOUR_OF_DAY)));   
          } else if (c=='m' && ! seconds) {
            b.append(twodecFormat.format(cal.get(Calendar.MINUTE)));   
          } else if (c=='s') {
            b.append(twodecFormat.format(cal.get(Calendar.SECOND)));
            seconds = true;
          } else if (c=='m' && seconds) {
            b.append(milliFormat.format(cal.get(Calendar.MILLISECOND)));     
          } else if (c=='W') {
            int day = cal.get(Calendar.DAY_OF_WEEK);
            switch(day)
            {
                case Calendar.SUNDAY: b.append("Sun"); break;
                case Calendar.MONDAY: b.append("Mon"); break;
                case Calendar.TUESDAY: b.append("Tue"); break;
                case Calendar.WEDNESDAY: b.append("Wed"); break;
                case Calendar.THURSDAY: b.append("Thu"); break;
                case Calendar.FRIDAY: b.append("Fri"); break;
                case Calendar.SATURDAY: b.append("Sat"); break;
                default: b.append(""); break;
            } 
          }else {
             b.append(c); 
          }
         
      }
      
      return b.toString();
   }


   /**
    * converts a Java.util.Date to a String, according to a format String.
    * See claendarToString.
    */       
   public static final String dateToString(Date date, String format) {
       TimeValue t = new TimeValue(date);
       return t.toString( format); 
   }

   /**
    * returns the day/month/year  hours:minutes:seconds.milliseconds representation of 
    * the current time.
    */
   public static String getCurrentTime() {
      return getCurrentTime(DEFAULT_FORMAT); 
   }

   /**
    * returns the day/month/year  hours:minutes:seconds.milliseconds representation of 
    * the current time.
    */
   public static String getCurrentTime(String format) {
       currentTimeValue.setTimeInMillis(System.currentTimeMillis());
       return calendarToString(currentTimeValue, format);
   }

   public static void main(String[] arg) {
      Console.println("Current date (default): " + getCurrentTime());  
      Console.println("Current date: " + getCurrentTime("D/M/Y"));
      Console.println("time: " + getCurrentTime("h:m:s"));
      Console.println("time + millis " + getCurrentTime("h:m:s.m"));
      Console.println("seconds only " + getCurrentTime("s"));
      
      TimeValue t = new TimeValue(0);
      Console.println("Time: " + t.toString("Y/M/D-h:m:s.m"));
      Console.println("Jan = " + JANUARY);
   }



       
       
   private static GregorianCalendar currentTimeValue = new GregorianCalendar();
   
   public static final int JANUARY   = Calendar.JANUARY;
   public static final int FEBRUARY  = Calendar.FEBRUARY;
   public static final int MARCH     = Calendar.MARCH;
   public static final int APRIL     = Calendar.APRIL;
   public static final int MAY       = Calendar.MAY;
   public static final int JUNE      = Calendar.JUNE;
   public static final int JULY      = Calendar.JULY;
   public static final int AUGUST    = Calendar.AUGUST;
   public static final int SEPTEMBER = Calendar.SEPTEMBER;
   public static final int OCTOBER   = Calendar.OCTOBER;
   public static final int NOVEMBER  = Calendar.NOVEMBER;
   public static final int DECEMBER  = Calendar.DECEMBER;
   
   // representation of this time value in terms of year-month-day-hours-minutes-seconds
   // need not be set.
   private GregorianCalendar gregor; 
   // representation of this time value in milliseconds.
   private long millis;
   
   private static DecimalFormat milliFormat = new DecimalFormat("000"); 
   private static DecimalFormat twodecFormat = new DecimalFormat("00"); 
   
   public static final String DEFAULT_FORMAT = "D/M/Y  h:m:s.m";
   
   

      
      
   
}