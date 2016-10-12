/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:26 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: XML.java,v $
// Revision 1.1  2006/05/24 09:00:26  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:10  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.18  2005/10/20 17:10:27  zwiers
// no message
//
// Revision 1.2  2005/09/18 21:47:03  zwiers
// no message
//
// Revision 1.1  2005/06/11 11:36:42  zwiers
// initial version
//


package parlevink.xml;

import java.io.*;
import parlevink.util.*;
import parlevink.util.Console;

import java.util.*;
import java.net.*;

/**
 * XML defines static methods related to XML handling
 */

public class XML 
{
@SuppressWarnings("unchecked")

//   public static XMLStructure<Integer> wrap(Integer obj) {
//      return new XMLInteger(obj);
//   }

   /**
    *
    */
   public static XMLStructure wrap(Object obj) {
      if (obj == null) {
         return new XMLNull();   
      } else if (obj instanceof XMLStructure) {
         return (XMLStructure) obj;
      
      } else if (obj instanceof Integer) {
         return new XMLInteger((Integer)obj);
      } else if (obj instanceof Long) {
         return new XMLLong((Long)obj);
      } else if (obj instanceof Float) {
         return new XMLFloat((Float)obj);
      } else if (obj instanceof Double) {
         return new XMLDouble((Double)obj);
      } else if (obj instanceof String) {
         return new XMLString((String)obj);
      } else if (obj instanceof Boolean) {
         return new XMLBoolean((Boolean)obj);
      } else if (obj instanceof Byte) {
         return new XMLByte((Byte)obj);
      } else if (obj instanceof Short) {
         return new XMLShort((Short)obj);
      } else if (obj instanceof Exception) {
         return new XMLException((Exception)obj);
      } else if (obj instanceof Id) {
         return new XMLId((Id)obj);
      } else if (obj instanceof Collection) {
         return new XMLList((Collection)obj);
      } else if (obj instanceof int[]) {
         return new XMLList((int[])obj);
      } else if (obj instanceof double[]) {
         return new XMLList((double[])obj);
      } else if (obj instanceof Object[]) {
         return new XMLList((Object[])obj);
      } else {
         throw new RuntimeException("Cannot XML-wrap Object: " + obj);
      }
   }

   /**
    *
    */
   public static XMLStructure wrap(Float d) {
      return new XMLFloat(d);
   }


   /**
    *
    */
   public static XMLStructure wrap(float d) {
      return new XMLFloat(d);
   }


   /**
    *
    */
   public static XMLStructure wrap(Double d) {
      return new XMLDouble(d);
   }


   /**
    *
    */
   public static XMLStructure wrap(double d) {
      return new XMLDouble(d);
   }


   /**
    *
    */
   public static XMLStructure wrap(Integer i) {
      return new XMLInteger(i);
   }

   /**
    *
    */
   public static XMLStructure wrap(int i) {
      return new XMLInteger(i);
   }


   /**
    *
    */
   public static XMLStructure wrap(String s) {
      return new XMLString(s);
   }


   /**
    *
    */
   public static XMLStructure wrap(Boolean b) {
      return new XMLBoolean(b);
   }

   /**
    *
    */
   public static XMLStructure wrap(boolean b) {
      return new XMLBoolean(b);
   }

   /**
    *
    */
   public static XMLStructure wrap(Exception e) {
      return new XMLException(e);
   }
 
   /**
    *
    */
   public static XMLStructure wrap(Id id) {
      return new XMLId(id);
   } 
 
 
 
   /**
    *
    */
   public static Object unwrap(XMLDouble xd) {
       return xd.DoubleValue();  
   }

   /**
    *
    */
   public static Object unwrap(XMLInteger xi) {
      return xi.IntegerValue();
   }

   /**
    *
    */
   public static Object unwrap(XMLString xs) {
      return xs.toString();
   }

   /**
    *
    */
   public static Object unwrap(XMLStructure xstr) {
      if (xstr instanceof XMLDouble) {
         return unwrap((XMLDouble)xstr);
      } else {
        return xstr;
      }
   }
 
   /**
    *
    */
   public static double doubleValue(XMLStructure xd) {
      return ((XMLDouble) xd).doubleValue();
   }

   /**
    *
    */
   public static int intValue(XMLStructure xi) {
      return ((XMLInteger) xi).intValue();
   }


   /**
    *
    */
   public static String stringValue(XMLStructure xs) {
      return ((XMLString) xs).toString();
   }


   /*
   */
   public static XMLStructure createXMLStructureFromString(String encoding) {
      return createXMLStructure(new XMLTokenizer(encoding));
   }

   /**
    *
    */
   public static XMLStructure createXMLStructure(URL url) {
      if (url != null) {
         return createXMLStructure(new XMLTokenizer(url));
      } else {
         Console.println("createXMLStructure: null URL");
         return null;          
      }
   }

   /**
    * helper method in combination with createXMLStructure: if this mthod returns
    * true, the latter method should at least be able to find the proper XMLStructure class 
    * that corresponds to the current STag.
    * If, on the other hand, isRecognizedXMLStructure() returns false, createXMLStructure will always
    * return a null result.
    */
   public static Class<?> XMLStructureClass(String xmlTag) {
      try {
         Class<?> xmlClass = (Class) classMap.get(xmlTag);
         if (xmlClass != null) return xmlClass;
         //Console.println("null xmlClass" );
         String className = getClassName(xmlTag);
         if (className != null) {
            xmlClass = Class.forName(className);
            classMap.put(xmlTag, xmlClass);
            return xmlClass;
         } else {
               //Console.println("XML: Could not determine class for " + xmlTag );
           return null; 
              //throw new RuntimeException("XMLStructure class not resolved for: " + tokenizer.getTagName());  
         }
      }  catch (ClassNotFoundException cnf) {
            throw new RuntimeException("ClassNotFoundException while recreating XMLStructure: \n" + cnf);
      }
   }


   /**
    * reads the first STag, determines the class from the tag name,
    * allocates a new XMLStructure object from that class, and 
    * reads the remainder of the XML code of the new 
    * XMLStructure Object, which finally is returned.
    */
   public static XMLStructure createXMLStructure(XMLTokenizer tokenizer) {
      XMLStructure result = newXMLStructureInstance(tokenizer);
      try {
         result.readXML(tokenizer);  
         return result;
      }  catch (IOException e) { 
            throw new RuntimeException("IOException while recreating XMLStructure: \n" + e); 
      }
   }

   /**
    * reads the first STag, determines the class from the tag name,
    * allocates a new XMLStructure object from that class, and 
    * reads the remainder of the XML code of the new 
    * XMLStructure Object, which finally is returned.
    */
   public static <T extends XMLStructure> T createXMLStructure(Class<T> tClass,  XMLTokenizer tokenizer) {
      try {
        T result = tClass.newInstance();
        result.readXML(tokenizer);
        return result;
      }  catch (InstantiationException ie) {
            throw new RuntimeException("InstantiationException while recreating XMLStructure: \n" + ie);
      }  catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException while recreating XMLStructure: \n" + iae);
      }  catch (IOException e) { 
            throw new RuntimeException("IOException while recreating XMLStructure: \n" + e); 
      }
   }

   /**
    * reads the first STag, determines the class from the tag name,
    * allocates a new XMLStructure object from that class. It does not read the remainder 
    * of the XML code. 
    */
   public static XMLStructure newXMLStructureInstance(XMLTokenizer tokenizer) {   
      try {
         //Console.println("createXMLStruct for " + tokenizer.getTagName());
         if (tokenizer.atSTag()) {
            String xmlTag = tokenizer.getTagName();
            Class xmlClass = (Class) classMap.get(xmlTag);
            if (xmlClass == null) {
               //Console.println("null xmlClass" );
               String className = getClassName(xmlTag);
               if (className != null) {
                  xmlClass = Class.forName(className);
                  classMap.put(xmlTag, xmlClass);
               } else {
                     Console.println("XML: Could not determine class for " + xmlTag );
                     return null;
               }
            }
            XMLStructure xmlObject = (XMLStructure) xmlClass.newInstance();  
            return xmlObject;
         } else if (tokenizer.atCharData()) {
            CharData chdata = new CharData();
            return chdata;           
         } else {
            throw new RuntimeException ("Cannot create XML structure for " 
                                   + tokenizer.getTokenString() + " at line " + tokenizer.getTokenLine() + ", position " + tokenizer.getTokenCharPos());
         }            
      }  catch (RuntimeException re) { // including XMLSCanException
            Console.println(re.toString());
            if (debug) {
               //re.printStackTrace();
               throw new RuntimeException("XML: " + re);
            } else {
               //Console.println("XML RuntimeException: " + re.toString());  
               throw tokenizer.getXMLScanException("XML: " + re);
            }   
      }  catch (IOException e) { 
            throw new RuntimeException("IOException while recreating XMLStructure: \n" + e); 
      }  catch (InstantiationException ie) {
            throw new RuntimeException("InstantiationException while recreating XMLStructure: \n" + ie);
      }  catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException while recreating XMLStructure: \n" + iae);
      }  catch (ClassNotFoundException cnf) {
            throw new RuntimeException("ClassNotFoundException while recreating XMLStructure: \n" + cnf);
      }
   }



   /**
    * Determines the class from the xmlTag,
    * allocates a new XMLStructure object from that class. It does not read the remainder 
    * of the XML code. 
    */
   public static XMLStructure newXMLStructureInstance(String xmlTag) {   
      try {
            Class xmlClass = XMLStructureClass(xmlTag);
            if (xmlClass == null) return null;
            XMLStructure xmlObject = (XMLStructure) xmlClass.newInstance();  
            return xmlObject;
      }  catch (InstantiationException ie) {
            throw new RuntimeException("InstantiationException while recreating XMLStructure: \n" + ie);
      }  catch (IllegalAccessException iae) {
            throw new RuntimeException("IllegalAccessException while recreating XMLStructure: \n" + iae);
      }
   }





 
   /**
    * determines the class from the current tokenizer STag name
    * and returns the Class Object for that tag, or null if it couldn't be found.
    */
   public static Class getClass(XMLTokenizer tokenizer) {
      try {
         if (!tokenizer.atSTag()) {
            throw new RuntimeException("XML.getTagClass: not at STag");  
         }
         return getClass(tokenizer.getTagName());
      } catch (IOException e) {
         throw new RuntimeException("IOException while recreating XMLStructure: \n" + e); 
      }
   }

   /**
    * determines the class from the xml tag name,
    * and returns the Class Object for that tag, or null if it couldn't be found.
    */
   public static Class getClass(String xmlTag) {
      try {
         Class xmlClass = (Class) classMap.get(xmlTag);
         if (xmlClass == null) {
             try {
                 xmlClass = Class.forName(xmlTag);
                 classMap.put(xmlTag, xmlClass);
             } catch (ClassNotFoundException cnfe) {
                 // try to resolve?
                 String className = getClassName(xmlTag);
                 if (className != null) {
                     xmlClass = Class.forName(className);
                     classMap.put(xmlTag, xmlClass);
                 } else {
                     throw new RuntimeException("XMLStructure class not resolved for: " + xmlTag);  
                 }
             } 
         }
         return xmlClass;
      }  catch (ClassNotFoundException cnf) {
            throw new RuntimeException("ClassNotFoundException while recreating XMLStructure: \n" + cnf);
      }
   }


   public static void addClass(String xmlName, Class c) {
       classMap.put(xmlName, c);  
   }

   /**
    * maps xmlName to the Class for className, for the pupose of creating
    * new XMLizable objects by means of the "createXMLizable" method.
    * returns the old value from the map.
    */
   public static Object addClass(String xmlName, String xmlStructureClassName) {
      try {
         return classMap.put(xmlName, Class.forName(xmlStructureClassName));
      } catch (ClassNotFoundException cnfe) {
         throw new RuntimeException("XMLObject, ClassNotFoundException: " + cnfe);  
      }
      
   }

   /**
    * maps xmlName to className, for the pupose of creating
    * new XMLizable objects by means of the "createXMLizable" method.
    * returns the old value from the map.
    * The specified className must be a fully qualified name, i.e. including
    * the package prefix. 
    * Unlike addXMLStructureClass, no attempt is made to actually load that class,
    * or to verify that it exists.
    */
   public static String addClassName(String xmlName, String xmlStructureClassName) {
      return (String) classNameMap.put(xmlName, xmlStructureClassName);
   }

   private static void addInstance(String xmlName) {
      
   }
      

   /**
    * returns the currently stored full class name for a specified XML tag
    */
   public static String getClassName(String xmlName) {
       return (String) classNameMap.get(xmlName);  
   }

   /*
    * The maps that contains the mapping from XML tags to Class Objects
    * as used by the "createXMLizable" method.
    * classMap maps XML tags to classes, i.e maps Strings to Class objects
    * classNameMap maps XML tags to class names  i.e.maps Strings to Strings
    */
   private static HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();
   private static HashMap<String, String> classNameMap = new HashMap<String, String>();
   

  
   /**/
   
   private static boolean debug = false;
   
   /**
    * sets the debug mode;
    * when set to true, RuntimeExceptions are not catched
    */
   public static void setDebug(boolean mode) {
      debug = mode;
   }
   
  /**/
   static {
      // predefined XML tags for XMLStructures
       XML.addClass("XMLInteger",    parlevink.xml.XMLInteger.class);  
       XML.addClass("XMLString",     parlevink.xml.XMLString.class);
       XML.addClass("XMLDouble",     parlevink.xml.XMLDouble.class);
       XML.addClass("XMLFloat",      parlevink.xml.XMLFloat.class);
       XML.addClass("XMLBoolean",    parlevink.xml.XMLBoolean.class);
       XML.addClass("XMLByte",       parlevink.xml.XMLByte.class);
       XML.addClass("XMLShort",      parlevink.xml.XMLShort.class);
       XML.addClass("XMLException",  parlevink.xml.XMLException.class);
       XML.addClass("XMLList",       parlevink.xml.XMLList.class);  
       XML.addClass("XMLElement",    parlevink.xml.XMLElement.class);
       XML.addClass("XMLMethodCall", parlevink.xml.XMLMethodCall.class);
       XML.addClass("XMLNull",       parlevink.xml.XMLNull.class); 
       XML.addClass("XMLId",         parlevink.xml.XMLId.class); 
   }  
  
}
