/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:27 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XMLMethodCall.java,v $
// Revision 1.1  2006/05/24 09:00:27  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.7  2005/10/20 09:54:58  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.2  2005/09/18 21:47:05  zwiers
// no message
//


package parlevink.xml;


import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import parlevink.util.*;

/**
 * An XMLMethodCall encapsulates a Java method call for an Agent,
 * including the method's name, its formal parameter typing and the corresponding
 * actual parameters.
 */
public class XMLMethodCall extends XMLStructureAdapter {
  
   /**
    * empty argument constructor;
    */
   public XMLMethodCall() {
   }
  
   /**
    * create a new XMLMethodCall object, for specified method name, 
    * and an (implicit) null array of parameters
    */ 
   public XMLMethodCall(String methodName) {
      this(methodName, null);
   }

   /**
    * create a new XMLMethodCall object, for specified method name, 
    * and specified arrays of formal parameter type names and actual parameter values.
    */ 
   public XMLMethodCall(String methodName, Object[] arg) {
      this.methodName = methodName;
      this.arg = arg;
   }
  
   /**
    * invokes the Method on the specified Object,
    * using the actual parameter values of this XMLMethodCall Object.
    */
   public Object invokeOn(Object obj) {
      if (arg == null) {
         arg = new Object[] {};
      }
      try {
        Class[] parameterType = new Class[arg.length];
        for (int i=0; i<arg.length; ++i) {
            parameterType[i] = arg[i].getClass();
        }
        //Method method = obj.getClass().getDeclaredMethod(methodName, parameterType);
        Method method = obj.getClass().getMethod(methodName, parameterType);
        return method.invoke(obj, arg);  
      } catch (IllegalAccessException e) {
         throw new RuntimeException("Illegal Access: " + e);         
      } catch (InvocationTargetException e) {
         throw new RuntimeException("InvocationTargetException: " + e); 
      } catch (NoSuchMethodException e) {
         throw new RuntimeException("Method not found: " + e); 
      }
   }

   /**
    * converts to an XML encoded String format
    */
   public String toString() {
      if (methodName == null) {
          return "<Uninitialized XMLMethodCall>";           
      } else {
          return toXMLString();  
      }
   }

   /**
    * appends the XML encoding of this XMLMethodCall to buf.
    */
   public StringBuffer appendXML(StringBuffer buf, int tab) {
      appendSpaces(buf, tab);
      appendOpenSTag(buf, XMLTAG);
      appendAttribute(buf, "method", methodName);
      int argc = (arg == null) ? 0 : arg.length;
      appendAttribute(buf, "argc", Integer.toString(argc));
      appendCloseSTag(buf);
      for (int i=0; i<argc; ++i) {
         appendNewLine(buf);
         (XML.wrap(arg[i])).appendXML(buf, tab+TAB);
      }
      appendNewLine(buf);
      appendSpaces(buf, tab);
      appendETag(buf, XMLTAG);
      return buf;
   }   

   /**
    * reconstructs this XMLMethodCall object using an XMLTokenizer.
    * This method can throw an (unchecked) XMLScanException in case of incorrectly
    * formatted XML. 
    */
   public XMLStructure readXML(XMLTokenizer tokenizer) {
      try {
         if (! tokenizer.atSTag(XMLTAG)) {
             throw tokenizer.getXMLScanException
                ("Erroneous XML encoding, expected: " + XMLTAG 
                  + ", encountered: " + tokenizer.getTagName()
                );
         }
         HashMap attrMap = tokenizer.getAttributes(); 
         int argc = Integer.parseInt((String)attrMap.get("argc"));
         methodName = (String) attrMap.get("method");        
         tokenizer.takeSTag();
         arg = new Object[argc];
         for (int i=0; (tokenizer.atSTag()); ++i) {
            String tag = tokenizer.getTagName();
            XMLStructure xmlElem = XML.createXMLStructure(tokenizer); 
            if (xmlElem instanceof XMLWrapper) {
               arg[i] = ((XMLWrapper)xmlElem).unwrap();   
            } else {
               arg[i] = xmlElem;
            }         
         }        
         tokenizer.takeETag(XMLTAG);
         return this;
      }  catch (IOException e) { 
         throw tokenizer.getXMLScanException("IOException in XMLTokenizer: " + e);
      }
   } 


   /**
    * Returns "XMLMethodCall".
    */
   public String getXMLTag() {
      return XMLTAG;
   }

   public String methodName;
   public Object[] arg;


   public static final String XMLTAG = "XMLMethodCall";
   public static final String CLASSNAME = "parlevink.xml.XMLMethodCall";
//   static {
//       XML.addClass(XMLTAG, CLASSNAME);     
//   }

}