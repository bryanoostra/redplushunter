/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLStructureAdapter.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:09  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.22  2005/10/21 21:32:08  zwiers
// error message improved
//
// Revision 1.21  2005/10/21 16:30:06  zwiers
// no message
//
// Revision 1.20  2005/10/20 09:54:57  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//


package parlevink.xml;

import java.io.*;
import java.util.*;
import parlevink.util.*;
import parlevink.util.Console;

/**
 * XMLStructureAdapter is a minimal implementation of  XMLStructure,
 * and is intended as a base class that must be extended. There are two different approaches:
 *
 * The FIRST APPROACH is to re-implement the methods
 * <ul> 
 * <li> readXML(XMLTokenizer) </li>
 * <li> appendXML(StringBuffer buf, int tab) </li>
 * <li> getXMLTag() </li>
 * </ul>
 * The remaining XMLStructure methods in XMLStructureAdapter are already implemented, in XMLStructureAdapter,
 * and are expressed in terms of these three methods. </br>
 * The SECOND APPROACH uses the fact that even readXML and appendXML already have
 * default implementations in XMLStructureAdapter, that build on top of a few  even simpler methods. 
 * So, rather than re-implementing readXML and appendXML, it is often easier to reimplement
 * those more basic methods. (getXMLTag must still be implemented);
 * With this second approach one should (re-)implement the following five methods:
 * <ul> 
 * <li> public static String getXMLTag() </li>
 * <li> public StringBuffer appendAttributeString(StringBuffer buf) </li>
 * <li> public boolean decodeAttribute(String attrName, String valCode) </li>
 * <li> public StringBuffer appendContent(StringBuffer buf, int tab) or public boolean hasContent() </li>
 * <li> public void decodeContent(XMLTokenizer tok)</li>
 * </ul> 
 * The XML tag is some String that identifies (uniquely) the class that you are implementing, 
 * and that extends XMLStructureAdapter. The deafult choice would be to take the full qualified classname,
 * that is, the class name prefixed with the package name. The alternative is to use a short string, like
 * the class name without the package, and ensure that your class is truly unique. 
 * The XMLStructureAdapter assume that your XML tag can be obtained by calling the getXMLTag method, so
 * you should reimplement this method.
 * The standard way to do this is the following piece of code:<br>
 *   public static final String XMLTAG = "your-tag";<br>
 *   public String getXMLTag() { return XMLTAG; }<br>
 *
 * Unfortunately, you have to copy the code for getXMLTag again for every class, because XMLTag is a static field,
 * and so, the <em>default</em> getXMLTag() always returns "XMLStructureAdapter", since it ignores your
 * XMLTAG, and uses the default XMLTAG instead. 
 * Description of the remaining methods:
 * <ul>
 * <li>The appendAttributeString method must append a string to buf, and return the latter;
 * The appended string must have the form: attr1="value1" attr2="value2" ....</li>
 * <li>The appendContent method is similar, but is called to produce the "contents"
 * in between the start en end tag of this structure. If you don't have any contents,
 * you can skip this method, but you should re-implement the hasContent method instead:
 * public boolean hasContent() { return false: }
 * In this case, appendContent is not used, and the XML code consists only of a so called empty tag,
 * of the form <tag attributes />. </li>
 * <li>The decodeAttribute method is called while the XMLStructure is being reconstructed
 * from XML code. A call of the form decodeAttribute(attr-i, value-i) will be made,
 * for <em>each</em> individual attribute found in the XML encoding. The implementation of decodeAttribute
 * should be able to reconstruct the XMLStructure object from a series of these calls. </li> 
 * <li>Finally, the decodeContent method must be able to handle the XML produced by appendContent.</li>
 * </ul>
 * Note: Although not required at all by the XMLStructure interface, it is often desirable 
 * to register an XMLStructureAdapter class with the global XML class. This guarantees that instances 
 * can be recreated from XML code, even when the precise type is not known on forehand, provided that
 * the XML tag is enough to determine the corresponding Java Class. 
 * This is done conveniently in a static code block, as follows:<br>
 * static { XML.addClass(XMLTAG, fullclassname.class); },<br>
 * where &quot;fullclassname&quot; is the full Class name, including the package prefix.
 */

public class XMLStructureAdapter implements XMLStructure
{

   public static String XMLTag = "XMLStructureAdapter";
   public int tagLine = -1; // -1 signals an invalid position

   /**
    * returns the XML tag that is used to encode this type of XMLStructure.
    * The default returns null.
    */
   public String getXMLTag() {
     return XMLTag; 
   }

   /**
    * returns whether the XML encoding should have an contents part, or should be an empty element tag.
    * This method should be overwritten if necessary in classes that inherit from XMLStructureAdapter.
    * The deafult implementation returns always true.
    */
   public boolean hasContent() {
      return true;  
   }

   /**
    * The default unwrap method simply returns "this".
    * This method should be overwritten by extensions (only) if the XMLStructure
    * is really a "wrapper" for a Java class that is likely to be not an XMLStructure itself,
    * like, for instance, classes like String or Integer.
    *
   public T unwrap() {
      return null;
   }


   /**
    * returns line number of the start tag, if this Object has been decoded by 
    * using an XMLTokenizer.
    * line counting starts at 1.
    */
   public final int getTagLine() {
     return tagLine;
   }


   /**
    * reconstructs this XMLStructure object by reading and parsing XML 
    * encoded text from a Reader.
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    * The default implementation reads nothing.
    */
   public XMLStructure readXML(Reader in) throws IOException {
       return readXML(new XMLTokenizer(in));
   }
   
   /**
    * reconstructs this XMLStructure object by parsing an XML encoded String s
    * This method can throw an (unchecked) ScanException in case of incorrectly
    * formatted XML. 
    * Although this default implementation exploits the readXML(Tokenizer) method
    * to implement readXML(String), it might be more efficient to reimplement
    * the latter, as it is heavily used while decoding Messages.
    */
   public XMLStructure readXML(String s) {
       try {
          return readXML(new StringReader(s));
       } catch (IOException e) {
          // The assumption is that IOExceptions are not possible for StringReaders.
          System.err.println("Unexpected IOException while reading a String: " + e);
          return null;
       }
   } 
   
   /**
    * decodes the value from an attribute value String
    * returns true if succesful, returns false for attribute names
    * that are not recognized. Might throw a RuntimeException when
    * an attribute has been recognized, but is ill formatted.
    * MUST BE OVERWRITTEN BY IMPLEMENTATIONS.
    */
   public boolean decodeAttribute(String attrName, String attrValue) { 
      return decodeAttribute(attrName, attrValue, null);
   }

   /**
    * decodes the value from an attribute value String
    * returns true if succesful, returns false for attribute names
    * that are not recognized. Might throw a RuntimeException when
    * an attribute has been recognized, but is ill formatted.
    * Moreover, an  XMLTokenizer reference is available which can be queried
    * for attributes, like getTokenLine() or getTokenCharPos(), which might be helpful
    * to produce error messages referring to lines/positions within the XML document
    * The default implementation simply calls decodeAttribute(attrName, attrValue)
    * SHOUL BE OVERWRITTEN BY IMPLEMENTATIONS.
    */
   public boolean decodeAttribute(String attrName, String attrValue, XMLTokenizer tokenizer) { 
      
      if (consoleAttributeEnabled && attrName.equals("console")) {
          if (attrValue.equals("")) {
             Console.println("XML at line " + tokenizer.getTokenLine());
          } else {
             Console.println("XML at line " + tokenizer.getTokenLine() + ": " + attrValue);
          }
          return true;
      } else {
         Console.println("unknown attribite: " + attrName);
         return false;
      }
      //return decodeAttribute(attrName, attrValue);
   }


   /**
    * decodes the XML contents, i.e. the XML between the STag and ETag
    * of the encoding.
    * MUST BE OVERWRITTEN BY IMPLEMENTATIONS.
    */
   public void decodeContent(XMLTokenizer tokenizer) throws IOException { 
      // super.decodeContent(tokenizer);
      // decode own content
   }



   /**
    * reconstructs this XMLStructureAdapter using an XMLTokenizer.
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) throws IOException {
      String tag = getXMLTag();
      tagLine = tokenizer.getTokenLine();
      try {
         if (! tokenizer.atSTag(tag)) {
             tag = getClass().getName(); // second chance: try full class name tag
             if (! tokenizer.atSTag(tag)) {
                throw tokenizer.getXMLScanException
                   ("Erroneous XML encoding, expected: " + getXMLTag() 
                     + ", encountered: " + tokenizer.getTagName()
                   );
             }
         }
         HashMap attrMap = tokenizer.getAttributes(); // will parse remainder of STag tail, if necessary
         Iterator attrIter = attrMap.entrySet().iterator();
         while (attrIter.hasNext()) {
             Map.Entry attr = (Map.Entry) attrIter.next();
             decodeAttribute((String)attr.getKey(), (String)attr.getValue(), tokenizer); 
         }
         tokenizer.takeSTag();
         decodeContent(tokenizer);
         if (tokenizer.atETag(tag)) {
            tokenizer.takeETag();
         } else {
            Console.print(tokenizer.getErrorMessage("Expected: </" + tag + ">, skipping tokens..."));
            boolean recovered = tokenizer.recoverAfterETag(tag);
            if (recovered) {
               Console.println(tokenizer.getErrorMessage(" found </" + tag + ">"));
             } else {
               throw tokenizer.getXMLScanException("Unable to find </" + tag + ">");
             }
         }
         //tokenizer.takeETag(tag);
      }  catch (IOException e) { throw tokenizer.getXMLScanException("IOException in XMLTokenizer: " + e); }
      postProcess(tokenizer);
      //return null;
      return this;
   } 


   /**
    * The postProcess method is called after the XML content has been parsed, and the ETag
    * has been checked. This is the point to do some ``post processing'', after all
    * relevant information has been collected.
    * The default implementation does nothing.
    */
   public void postProcess(XMLTokenizer tokenizer) {
      //Console.println("XMLStructureAdapter Default postProcess");
   }

   /**
    * The default implementation relies on appendXML()
    */
   public void writeXML(PrintWriter out) {
      StringBuffer buf = new StringBuffer();
      appendXML(buf);
      out.print(buf);
   } 

   /**
    * writes an XML encoded String to "out".
    * The int "tab" can be used as a hint for indentation, and
    * denotes the indentation to be applied to the XML code
    * as a whole. 
    * This String should equal the result of toXMLString(tab).
    * The default implementation relies on appendXML(.. tab).
    */
   public void writeXML(PrintWriter out, int tab) {
      StringBuffer buf = new StringBuffer();
      appendXML(buf, tab);
      out.print(buf);
   }

   /**
    * yields an XML encoded String of this XMLStructure object. 
    * The default implementation relies on appendXML()
    */ 
   public String toXMLString() {
      StringBuffer buf = new StringBuffer();
      appendXML(buf);
      return buf.toString();
   } 

   /**
    * yields an XML encoded String of this XMLStructure object. 
    * The readXML() methods should be able to reconstruct this object from 
    * the String delivered by toXMLString().
    * The default implementation relies on appendXML( .., tab)
    */ 
   public String toXMLString(int tab) {
      StringBuffer buf = new StringBuffer();
      appendXML(buf, tab);
      return buf.toString();
   }

   /**
    * the default toString() method returns the result of toXMLSTring()
    */
   public String toString() {
      return toXMLString();
   }



   /**
    * Appends a String to buf that encodes the contents for the XML encoding.
    * MUST BE OVERWRITTEN BY IMPLEMENTATIONS. (The default implementation appends nothing).
    * The encoding should start on a new line, using indentation equal to tab.
    * There should be no newline after the encoding.
    */
   public StringBuffer appendContent(StringBuffer buf, int tab) {
      // super.addContent(buf, tab);
      return buf;
   }

   /**
    * A special case of appendAttributeString that also takes a tab parameter.
    * This method need not be re-implemted, unless it is desitable to layout the attributes
    * spanning several lines. The X3D standard is an example where XML attributes are abused
    * to store complete arrays of data; in such cases, inserting newlines between data elements
    * is highly desirable. Of course, the new lines then should start with a proper indentation,
    * as denoted by the tab parameter.
    * The default implementation simply ignores this tab, and calls appendAttributeString(buf),
    * i.e. without the tab parameter, which is fine for all cases where the attributes
    * are on the same line as the XML tag.
    * 
    */
   public StringBuffer appendAttributeString(StringBuffer buf, int tab) {
      return appendAttributeString(buf);
   }

   /**
    * Appends a String to buf that encodes the attributes for the XML encoding.
    * When non empty, the attribute string should start with a space character.
    * Hint: call the appendAttribute(StringBuffer buf, String attrName, String attrValue)
    * for every relevant attribute; this takes care of the leading space as well as spaces 
    * in between the attributes)
    * MUST BE OVERWRITTEN BY IMPLEMENTATIONS. (The default implementation appends nothing).
    * The encoding should preferably not add newline characters.
    */
   public StringBuffer appendAttributeString(StringBuffer buf) {
      // super.appendAttributeString(buf);
      // appendAttribute(buf, attr-1, value-1);
      // .....
      // appendAttribute(buf, attr-n, value-n);
      return buf;
   }


   /**
    * like writeXML(PrintWriter ,int), except that the XML encoding is appended
    * to a StringBuffer.
    * The latter must be returned.
    * The default implementation appends nothing, and must be overwrritten
    * by extension classes.
    */   
   public StringBuffer appendXML(StringBuffer buf, int tab) {
      if (tab > 0) appendSpaces(buf, tab);
      buf.append('<');
      String tag = getXMLTag();
      buf.append(tag);
      //buf.append(' ');
      appendAttributeString(buf, tab);
      if (hasContent()) {
         buf.append('>');
         appendContent(buf, tab+TAB);
         buf.append('\n');
         if (tab > 0) appendSpaces(buf, tab);
         buf.append("</");
         buf.append(tag);
         buf.append('>');
      } else {
         buf.append("/>");
      }
      return buf;
   }


   /**
    * like writeXML(PrintWriter), except that the XML encoding is appended
    * to a StringBuffer.
    * the latter must be returned.
    * The default implementation relies on appendXML(..., 0)
    */
   public StringBuffer appendXML(StringBuffer buf) {
      return appendXML(buf, 0);
   }
   
   /**
    * returns a String consisting of exactly "tab" spaces.
    */
   public final static String spaces(int tab) {             
      while (lotsOfSpaces.length() < tab ) {
         lotsOfSpaces.append("         ");
      }
      return lotsOfSpaces.substring(0, tab); 
   }
   
   /**
    * appends a String consisting of exactly "tab" spaces.
    */
   public final static StringBuffer appendSpaces(StringBuffer buf, int tab) {    
      if (tab <= 0) return buf;         
      while (lotsOfSpaces.length() < tab ) {
         lotsOfSpaces.append("         ");
      }
      return buf.append(lotsOfSpaces.substring(0, tab)); 
   }

   /**
    * appends a String consisting of the amount of spaces for a "Tab".
    * This amount is equal to XMLStructureAdapter.TAB;
    */
   public final static StringBuffer appendTab(StringBuffer buf) {             
      return buf.append(TAB_STRING); 
   }
   
   /**
    * appends an XML comment string to buf,
    * The string appended is of the form:
    * &lt;!-- comment --&gt;
    */
   public final static StringBuffer appendComment(StringBuffer buf, String comment) {
      buf.append("<!-- ");
      buf.append(comment);
      buf.append(" -->");
      return buf;
   }


   /**
    * This method appends an XML open-STag with specified tag name to buf.
    * The string appended is of the form &lt;tagName;
    */      
   public final static StringBuffer appendOpenSTag(StringBuffer buf, String tagName) {      
      buf.append('<');
      buf.append(tagName);    
      return buf;
   }

   /**
    * Appends &gt; (Closing an STag)
    */      
   public final static StringBuffer appendCloseSTag(StringBuffer buf) {      
      buf.append('>'); 
      return buf;
   }
 
   /**
    * Appends /&gt; 
    */      
   public final static StringBuffer appendCloseEmptyTag(StringBuffer buf) {      
      buf.append("/>"); 
      return buf;
   }
      
   /**
    * This method appends an XML STag with specified tag name to buf.
    * The string appended is of the form &lt;tagName&gt;
    * No attributes are included
    */      
   public final static StringBuffer appendSTag(StringBuffer buf, String tagName) {      
      buf.append('<');
      buf.append(tagName);    
      buf.append('>');
      return buf;
   }


   /**
    * This method appends an XML STag with specified tag name to buf.
    * The string appended is of the form &lt;tagName&gt;
    * No attributes are included The tag is on a new line, preceded by tab spaces.
    */      
   public final static StringBuffer appendSTag(StringBuffer buf, String tagName, int tab) {    
      buf.append('\n');
      if (tab > 0) appendSpaces(buf, tab);
      buf.append('<');
      buf.append(tagName);    
      buf.append('>');
      return buf;
   }

   /**
    * This method appends an XML STag with specified tag name and attributes to buf.
    * It is assumed that keys and values are String typed,
    * and that values do not contain " characters.
    * The string appended is of the form &lt;tagName attr_0="value_0" ... attr_n="value_n">
    */      
   public final static StringBuffer appendSTag(StringBuffer buf, String tagName, HashMap attributes) {      
      buf.append('<');
      buf.append(tagName);    
      if (attributes!=null) appendAttributes(buf, attributes);
      buf.append('>');
      return buf;
   }


   /**
    * This method appends an "empty element" XML STag with specified tag name to buf.
    * The string appended is of the form &lt;tagName/&gt;
    * No attributes are included
    */      
   public final static StringBuffer appendEmptyTag(StringBuffer buf, String tagName) {      
      buf.append('<');
      buf.append(tagName);    
      buf.append("/>");
      return buf;
   }


   /**
    * This method appends an "empty element" XML STag with specified tag name to buf.
    * The string appended is of the form &lt;tagName attr=value/&gt;
    * A single attribute-value pair is included
    */      
   public final static StringBuffer appendEmptyTag(StringBuffer buf, int tab, String tagName, String attrName, String attrValue) {      
      if (tab > 0) appendSpaces(buf, tab);
      buf.append('<');
      buf.append(tagName);   
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      buf.append(attrValue==null ? "NULL" : attrValue);
      buf.append('"');
      buf.append("/>");
      return buf;
   }

   /**
    * This method appends an "empty element" XML STag with specified tag name to buf.
    * The string appended is of the form &lt;tagName attr_0="value_0" ... attr_n="value_n"/&gt;
    */      
   public final static StringBuffer appendEmptyTag(StringBuffer buf, String tagName, HashMap attributes) {      
      buf.append('<');
      buf.append(tagName);  
      if (attributes!=null) appendAttributes(buf, attributes);  
      buf.append("/>");
      return buf;
   }


   /**
    * This method appends an "empty element" XML STag with specified tag name to buf.
    * The string appended is of the form &lt;tagName attr_0="value_0" ... attr_n="value_n"/&gt;
    */      
   public final static StringBuffer appendEmptyTag(StringBuffer buf, int tab, String tagName, HashMap attributes) {      
      appendSpaces(buf, tab);
      buf.append('<');
      buf.append(tagName);  
      if (attributes!=null) appendAttributes(buf, attributes);  
      buf.append("/>");
      return buf;
   }

   /**
    * This method appends a single XML style attribute to buf.
    * The string has the form : " attrName=\"attrValue\"".
    * It is assumed that attrName is a legal XML attribute name,
    * and that attrValue is a legal XML value, not containing " characters.
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, String attrValue) {      
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      buf.append(attrValue==null ? "NULL" : attrValue);
      buf.append('"');
      return buf;
   }


   /**
    * This method appends a single XML style attribute to buf.
    * The string has the form : " attrName=\"attrValue1 attrValue2\"".
    * It is assumed that attrName is a legal XML attribute name,
    * and that attrValue is a legal XML value, not containing " characters.
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       String attrValue1, String attrValue2) {      
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      buf.append(attrValue1==null ? "NULL" : attrValue1);
      buf.append(' ');
      buf.append(attrValue2==null ? "NULL" : attrValue2);
      buf.append('"');
      return buf;
   }

   /**
    * This method appends a single XML style attribute to buf.
    * The string has the form : " attrName=\"attrValue1 attrValue2 attrValue3\"".
    * It is assumed that attrName is a legal XML attribute name,
    * and that attrValue is a legal XML value, not containing " characters.
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       String attrValue1, String attrValue2, String attrValue3) {      
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      buf.append(attrValue1==null ? "NULL" : attrValue1);
      buf.append(' ');
      buf.append(attrValue2==null ? "NULL" : attrValue2);
      buf.append(' ');
      buf.append(attrValue3==null ? "NULL" : attrValue3);
      buf.append('"');
      return buf;
   }

   /**
    * This method appends a single XML style attribute to buf.
    * The string has the form : " attrName=\"attrValue1 attrValue2 attrValue3 attrValue4\"".
    * It is assumed that attrName is a legal XML attribute name,
    * and that attrValue is a legal XML value, not containing " characters.
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       String attrValue1, String attrValue2, String attrValue3, String attrValue4) {      
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      buf.append(attrValue1==null ? "NULL" : attrValue1);
      buf.append(' ');
      buf.append(attrValue2==null ? "NULL" : attrValue2);
      buf.append(' ');
      buf.append(attrValue3==null ? "NULL" : attrValue3);
      buf.append(' ');
      buf.append(attrValue4==null ? "NULL" : attrValue4);
      buf.append('"');
      return buf;
   }

   /**
    * appends an attribute of the form name="value".
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, int intValue) {
       return appendAttribute(buf, attrName, 
         Integer.toString(intValue));  
   }

   /**
    * appends an attribute of the form name="value1 value2"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       int intValue1, int intValue2) {
       return appendAttribute(buf, attrName, 
         Integer.toString(intValue1), Integer.toString(intValue2));  
   }

   /**
    * appends an attribute of the form name="value1 value2 value3"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       int intValue1, int intValue2, int intValue3) {
       return appendAttribute(buf, attrName, 
         Integer.toString(intValue1), Integer.toString(intValue2), Integer.toString(intValue3));  
   }

   /**
    * appends an attribute of the form name="value1 value2 value3 value4"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       int intValue1, int intValue2, int intValue3, int intValue4) {
       return appendAttribute(buf, attrName, 
         Integer.toString(intValue1), Integer.toString(intValue2), Integer.toString(intValue3), Integer.toString(intValue4));  
   }



   /**
    * appends an attribute of the form name="value".
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, long longValue) {
       return appendAttribute(buf, attrName, 
         Long.toString(longValue));  
   }

   /**
    * appends an attribute of the form name="value1 value2".
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       long longValue1, long longValue2) {
       return appendAttribute(buf, attrName, 
         Long.toString(longValue1), Long.toString(longValue2));  
   }

   /**
    * appends an attribute of the form name="value1 value2 value3".
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       long longValue1, long longValue2, long longValue3) {
       return appendAttribute(buf, attrName, 
         Long.toString(longValue1), Long.toString(longValue2), Long.toString(longValue3));  
   }

   /**
    * appends an attribute of the form name="value1 value2 value3 value4".
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                       long longValue1, long longValue2, long longValue3, long longValue4) {
       return appendAttribute(buf, attrName, 
         Long.toString(longValue1), Long.toString(longValue2), Long.toString(longValue3), Long.toString(longValue4));  
   }


   /**
    * appends an attribute of the form name="value"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                        float floatValue) {
       return appendAttribute(buf, attrName, Float.toString(floatValue));  
   }

   /**
    * appends an attribute of the form name="value1 value2"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                        float floatValue1, float floatValue2) {
       return appendAttribute(buf, attrName, 
         Float.toString(floatValue1), Float.toString(floatValue2));  
   }

   /**
    * appends an attribute of the form name="value1 value2 value3"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                        float floatValue1, float floatValue2, float floatValue3) {
       return appendAttribute(buf, attrName, 
         Float.toString(floatValue1), Float.toString(floatValue2), Float.toString(floatValue3));  
   }   
   
   /**
    * appends an attribute of the form name="value1 value2 value3 value4"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                        float floatValue1, float floatValue2, float floatValue3, float floatValue4) {
       return appendAttribute(buf, attrName, 
         Float.toString(floatValue1), Float.toString(floatValue2), Float.toString(floatValue3), Float.toString(floatValue4));  
   }
   
   /**
    * appends an attribute of the form name="value"
    */           
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, double doubleValue) {
       return appendAttribute(buf, attrName, Double.toString(doubleValue));  
   }

   /**
    * appends an attribute of the form name="value1 value2"
    */           
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                        double doubleValue1, double doubleValue2) {
       return appendAttribute(buf, attrName, 
         Double.toString(doubleValue1), Double.toString(doubleValue2));  
   }
   
   /**
    * appends an attribute of the form name="value1 value2 value3"
    */           
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                        double doubleValue1, double doubleValue2, double doubleValue3) {
       return appendAttribute(buf, attrName, 
         Double.toString(doubleValue1), Double.toString(doubleValue2), Double.toString(doubleValue3));  
   }
   
   /**
    * appends an attribute of the form name="value1 value2 value3 value4"
    */           
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                        double doubleValue1, double doubleValue2, double doubleValue3, double doubleValue4) {
       return appendAttribute(buf, attrName, 
         Double.toString(doubleValue1), Double.toString(doubleValue2), Double.toString(doubleValue3), Double.toString(doubleValue4));  
   }   


   /**
    * appends an attribute of the form name="value"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, boolean boolValue) {
       return appendAttribute(buf, attrName, Boolean.toString(boolValue));  
   }

   /**
    * appends an attribute of the form name="value1 value2"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                         boolean boolValue1, boolean boolValue2) {
       return appendAttribute(buf, attrName, 
         Boolean.toString(boolValue1), Boolean.toString(boolValue2));  
   }

   /**
    * appends an attribute of the form name="value1 value2 value3"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                         boolean boolValue1, boolean boolValue2, boolean boolValue3) {
       return appendAttribute(buf, attrName, 
         Boolean.toString(boolValue1), Boolean.toString(boolValue2), Boolean.toString(boolValue3));  
   }
   
   /**
    * appends an attribute of the form name="value1 value2 value3 value4"
    */      
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, 
                         boolean boolValue1, boolean boolValue2, boolean boolValue3, boolean boolValue4) {
       return appendAttribute(buf, attrName, 
         Boolean.toString(boolValue1), Boolean.toString(boolValue2), Boolean.toString(boolValue3), Boolean.toString(boolValue4));  
   }   




   /**
    * appends an attribute of the form name="valu0 value1 ....."
    *
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, int[] ints) {
      return appendAttribute(buf, attrName, ints, ' ');
   }

   /**
    * appends an attribute of the form name="valu0 value1 ....."
    */
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, int[] ints, char separator) {
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      if (ints != null && ints.length > 0) {
         buf.append(ints[0]); 
         for (int i=1; i<ints.length; i++) {
            buf.append(separator);
            buf.append(ints[i]);       
         }
      }
      buf.append('"');    
      return buf;  
   }

   /**
    * appends an attribute of the form name="value0 value1 ....."
    *
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, float[] floats) {
      return appendAttribute(buf, attrName, floats, ' ');
   }

   /**
    * appends an attribute of the form name="value0 value1 ....."
    */
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, float[] floats, char separator) {
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      if (floats != null && floats.length > 0) {
         buf.append(floats[0]); 
         for (int i=1; i<floats.length; i++) {
            buf.append(separator);
            buf.append(floats[i]);       
         }
      }
      buf.append('"');    
      return buf;  
   }

   /**
    * appends an attribute of the form name="value0 value1 .....", with maxNrOfElements float at one line.
    */
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, float[] floats, char separator, int tab, int nrElementsPerLine) {
      int tabCounter = nrElementsPerLine;
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      if (floats != null && floats.length > 0) {
         buf.append(floats[0]); tabCounter--;
         for (int i=1; i<floats.length; i++) {
            if (tabCounter == 0) {
               buf.append('\n'); 
               appendSpaces(buf, tab);
               tabCounter = nrElementsPerLine;   
            }
            buf.append(separator);
            buf.append(floats[i]);
            tabCounter--;       
         }
      }
      buf.append('"');    
      return buf;  
   }

   /**
    * appends an attribute of the form name="valu0 value1 ....."
    */
   public final static StringBuffer appendAttribute(StringBuffer buf, String attrName, double[] doubles, char separator) {
      buf.append(' ');
      buf.append(attrName);
      buf.append("=\"");
      if (doubles != null && doubles.length > 0) {
         buf.append(doubles[0]); 
         for (int i=1; i<doubles.length; i++) {
            buf.append(separator);
            buf.append(doubles[i]);       
         }
      }
      buf.append('"');    
      return buf;  
   }


    /**
    * This method appends XML style tag attributes to buf.
    * It is assumed that the keys and values of the attributes hashMap
    * are String typed, and that values do not contain " characters.
    */      
   public final static StringBuffer appendAttributes(StringBuffer buf,HashMap attributes) {      
      Iterator iter = attributes.keySet().iterator();
      while (iter.hasNext()) {
         String key = (String)iter.next();
         String value = (String)attributes.get(key);
         buf.append(' ');
         buf.append(key);
         buf.append("=\"");
         buf.append(value==null ? "NULL" : value);
         buf.append('"');
      }
      return buf;
   }


   /**
    * appends an XML ETag to buf
    */
   public final static StringBuffer appendETag(StringBuffer buf, String tagName) {
      buf.append("</");
      buf.append(tagName);
      buf.append('>');
      return buf;
   }

   /**
    * appends an XML ETag to buf, on a new line, preceded by tab spaces.
    */
   public final static StringBuffer appendETag(StringBuffer buf, String tagName, int tab) {
      buf.append('\n');
      appendSpaces(buf, tab);
      buf.append("</");
      buf.append(tagName);
      buf.append('>');
      return buf;
   }

   /*
    * append a platform dependent NEWLINE character String to buf
    */
   public final static StringBuffer appendSystemNewLine(StringBuffer buf) {
	   buf.append(SYSTEMNEWLINE);
	   return buf;
   }

   /*
    * append a platform indepenent '\n' character to buf
    */
   public final static StringBuffer appendNewLine(StringBuffer buf) {
	   buf.append('\n');
	   return buf;
   }

   /*
    * append a platform indepenent '\n' character to buf, followed by tab spaces
    */
   public final static StringBuffer appendNewLine(StringBuffer buf, int tab) {
	   buf.append('\n');
	   while (lotsOfSpaces.length() < tab ) {
         lotsOfSpaces.append("         ");
      }
      return buf.append(lotsOfSpaces.substring(0, tab)); 
   }

   /**
    * decodes an int 
    */
   public final static int decodeInt(String encoding) {
      return  Integer.parseInt(encoding);  
   }
   
   /**
    * decodes a long 
    */
   public final static long decodeLong(String encoding) {
      return  Long.parseLong(encoding);  
   }   

   /**
    * decodes a float 
    */
   public final static float decodeFloat(String encoding) {
      return  Float.parseFloat(encoding);  
   }

   /**
    * decodes a double
    */
   public final static double decodeDouble(String encoding) {
      return  Double.parseDouble(encoding);  
   }

   /**
    * decodes a boolean value (true or false)
    */
   public final static boolean decodeBoolean(String encoding) {
      return encoding.trim().toLowerCase().equals("true");
   }


   /**
    * counts the number of tokens, separated by specified delimiter characters, 
    * within a given encoding.
    */
   public final static int countTokens(String encoding, String delimiters) {
      StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
      return tokenizer.countTokens();
   }

   /**
    * counts the number of tokens, separated by ATTRIBUTE_TOKEN_DELIMITERS characters, 
    * within a given encoding.
    */
   public final static int countTokens(String encoding) {
      StringTokenizer tokenizer = new StringTokenizer(encoding, ATTRIBUTE_TOKEN_DELIMITERS);
      return tokenizer.countTokens();
   }  



   /**
    * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
    */
   public final static int[] decodeIntArray(String encoding) {
      return decodeIntArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    */
   public final static int[] decodeIntArray(String encoding, String delimiters) {
      return decodeIntArray(encoding, null, delimiters);
   }

   /**
    * equivalent to decodeIntArray(encoding, ints, ATTRIBUTE_TOKEN_DELIMITERS);
    */
   public final static int[] decodeIntArray(String encoding, int[] ints) {
      return decodeIntArray(encoding, ints, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a int array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    * The ints array should be sufficiently large so that all tokens can be allocated,
    * or else it should be null. In the latter case, a new int array
    * is allocated with length equal to the number of tokens.
    */
   public final static int[] decodeIntArray(String encoding, int[] ints, String delimiters) {       
      int decode = 0;
      StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
      if (ints == null) ints = new int[tokenizer.countTokens()]; 
      while (tokenizer.hasMoreTokens() ) {
         if (decode >= ints.length) {
              throw new RuntimeException("More ints that expected: " + decode);
         }
         ints[decode++] = Integer.parseInt(tokenizer.nextToken());
      }
      return ints;      
   }


   /**
    * decodes a float array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    */
   public final static float[] decodeFloatArray(String encoding) {
      return decodeFloatArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a float array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    */
   public final static float[] decodeFloatArray(String encoding, String delimiters) {
      return decodeFloatArray(encoding, null, delimiters);
   }

   /**
    * equivalent to decodeFloatArray(encoding, floats, ATTRIBUTE_TOKEN_DELIMITERS);
    * (i.e. attribute tokens have to be separated by white space)
    */
   public final static float[] decodeFloatArray(String encoding, float[] floats) {
      return decodeFloatArray(encoding, floats, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a float array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    * The floats array should be sufficiently large so that all tokens can be allocated,
    * or else it should be null. In the latter case, a new float array
    * is allocated with length equal to the number of tokens.
    */
   public final static float[] decodeFloatArray(String encoding, float[] floats, String delimiters) {       
      int decode = 0;
      StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
      if (floats == null) floats = new float[tokenizer.countTokens()]; 
      while (tokenizer.hasMoreTokens() ) {
         if (decode >= floats.length) {
              throw new RuntimeException("More floats that expected: " + decode);
         }
         floats[decode++] = Float.parseFloat(tokenizer.nextToken());
      }
      return floats;      
   }


   /**
    * decodes a double array, encoded as String of the form value0 value1 ..... value-n
    */
   public final static double[] decodeDoubleArray(String encoding) {
      return decodeDoubleArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a double array, encoded as String of the form value0 value1 ..... value-n
    */
   public final static double[] decodeDoubleArray(String encoding, String delimiters) {
      return decodeDoubleArray(encoding, null, delimiters);
   }

   /**
    * decodes a double array, encoded as String of the form value0 value1 ..... value-n
    * The doubles array should be sufficiently large so that all tokens can be allocated,
    * or else it should be null. In the latter case, a new double array
    * is allocated with length equal to the number of tokens.
    */
   public final static double[] decodeDoubleArray(String encoding, double[] doubles) {
      return decodeDoubleArray(encoding, doubles, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a double array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    * The doubles array should be sufficiently large so that all tokens can be allocated,
    * or else it should be null. In the latter case, a new double array
    * is allocated with length equal to the number of tokens.
    */
   public final static double[] decodeDoubleArray(String encoding, double[] doubles, String delimiters) {
      int decode = 0;
      StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
      if (doubles == null) doubles = new double[tokenizer.countTokens()];  
      while (tokenizer.hasMoreTokens() ) {
         if (decode >= doubles.length) {
              throw new RuntimeException("More doubles that expected: " + decode);
         }
         doubles[decode++] = Double.parseDouble(tokenizer.nextToken());
      }
      return doubles;      
   }



   /**
    * decodes a string array, encoded as String of the form value0 value1 ..... value-n
    */
   public final static String[] decodeStringArray(String encoding) {
      return decodeStringArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a String array, encoded as String of the form value0 value1 ..... value-n
    */
   public final static String[] decodeStringArray(String encoding, String delimiters) {
      return decodeStringArray(encoding, null, delimiters);
   }

   /**
    * decodes a String array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    * The Strings array should be sufficiently large so that all tokens can be allocated,
    * or else it should be null. In the latter case, a new String array
    * is allocated with length equal to the number of tokens.
    */
   public final static String[] decodeStringArray(String encoding, String[] strings) {
      return decodeStringArray(encoding, strings, ATTRIBUTE_TOKEN_DELIMITERS);
   }


   /**
    * decodes a sequence of Strings, separated by specified delimiters
    */
   public final static String[] decodeStringArray(String encoding, String[] strings, String delimiters) {
      int decode = 0;
      StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
      if (strings == null) strings = new String[tokenizer.countTokens()];  
      while (tokenizer.hasMoreTokens() ) {
         if (decode >= strings.length) {
              throw new RuntimeException("More Strings that expected: " + decode);
         }
         strings[decode++] = tokenizer.nextToken();
      }
      return strings;      
   }



   /**
    * decodes a Id array, encoded as String of the form value0 value1 ..... value-n.
    */
   public final static Id[] decodeIdArray(String encoding) {
      return decodeIdArray(encoding, null, ATTRIBUTE_TOKEN_DELIMITERS);
   }

   /**
    * decodes a Id array, encoded as String of the form value0 value1 ..... value-n,
    * where the values are separated by characters specified by means of the delimiter String.
    * The ids array should be sufficiently large so that all tokens can be allocated,
    * or else it should be null. In the latter case, a new Id array
    * is allocated with length equal to the number of tokens.
    */
   public final static Id[] decodeIdArray(String encoding, Id[] ids, String delimiters) {
      int decode = 0;
      StringTokenizer tokenizer = new StringTokenizer(encoding, delimiters);
      if (ids == null) ids = new Id[tokenizer.countTokens()];  
      while (tokenizer.hasMoreTokens() ) {
         if (decode >= ids.length) {
              throw new RuntimeException("More Ids that expected: " + decode);
         }
         ids[decode++] = Id.forName(tokenizer.nextToken());
      }
      return ids;      
   }


   public static void setConsoleAttributeEnabled(boolean b) {
      consoleAttributeEnabled = b;
   }

   public static boolean consoleAttributeEnabled = true;
   private static StringBuffer lotsOfSpaces = new StringBuffer("               ");
   public static final int TAB = 3;
   public static final String TAB_STRING = "   ";
   public static final String NEWLINE = "\n";
   public static final String SYSTEMNEWLINE = System.getProperty("line.separator");
   public static final String ATTRIBUTE_TOKEN_DELIMITERS = " \t\n\r\f";
   public static final String COMMA_SEPARATOR = ", \t\n\r\f";
   public static final int DECODEDARRAYSIZE = 4;

//   public static final String NEWLINE = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

}
