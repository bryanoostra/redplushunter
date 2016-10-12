/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */
// Last modification by: $Author: swartjes $
// $Log: Configuration.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:13  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.9  2005/10/20 10:06:04  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.8  2005/01/31 15:41:02  zwiers
// serialVersionUID introduced
//
// Revision 1.7  2003/11/27 15:39:04  zwiers
// System.out replaced by Console
//
// Revision 1.6  2003/09/02 11:41:51  zwiers
// small mod
//
// Revision 1.5  2003/08/05 13:52:38  zwiers
// lots of small revisions
//
// Revision 1.4  2002/12/18 08:46:29  zwiers
// no message
//
// Revision 1.3  2002/11/06 17:17:32  zwiers
// Added loadResource methods
//
// Revision 1.2  2002/09/17 09:56:19  zwiers
// file reading versus resource based reading introduced
//
// Revision 1.1.1.1  2002/09/13 16:06:59  zwiers
// initial import
//

package parlevink.util;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 * A Configuration is an extension of java.util.Properties, that loads properties from a file.
 * It inherits methods like getProperty(String name), getProperty(String name, String default)
 * which can be used to retrieve properties loaded from file.
 * 
 */
public class Configuration extends Properties
{ 

   /**
    * creates an empty Configuration.
    */
   public Configuration() {
      super();
   }
  
   /**
    * creates a new Configuration object and loads its contents from file.
    * The file(path) is local to the current working directory.
    */
   public Configuration(String configFile) {
      super();
      setProperty("configFile", configFile);
      loadFile();
   }
   
   /**
    * loads the Configuration contents from file.
    * The file name is determined from the current value of the "configFile"
    * property. If this property is not set, the file name is DEFAULT_CONFIG_FILE. 
    */
   public boolean loadFile() {
     return loadFile(getProperty("configFile", DEFAULT_CONFIG_FILE));
   }

   /**
    * tries to load the Configuration contents from file, specified as native
    * file the local file system. 
    * The boolean returned denotes whether loading was succesful or not.
    */   
   public boolean loadFile(String configFile) {
     try {
        InputStream configIn = new FileInputStream(configFile);
        load(configIn);
        configIn.close();
        return true;
     }
     catch (FileNotFoundException e) { 
        //Console.println("Could not find configuration file \"" + configFile + "\""); 
        return false;
     }
     catch (IOException e) { 
        Console.println("Could not load configuration: " + e); 
        return false;
     }
     catch (Exception e) {
        Console.println("Could not load configuration: " + e); 
        return false;
     }       
   }

   /**
    * tries to load the Configuration contents from a file specified
    * as a "resource". The package prefix for Object "obj" is prepended
    * to "configFile". The resulting path is the resource that is searched for.
    * In theory the search is determined by the ClassLoader in use; in practice
    * the resource must be in a directory that is on the classpath.
    * For the parlevink/aveiro projects, configFile would refer to some file
    * in the data sub directory associated with "obj".
    * The boolean returned denotes whether loading was succesful or not.
    */   
   public boolean loadAsResource(Object obj, String configFile) {
     try {
        InputStream configIn = obj.getClass().getResourceAsStream(configFile);
        if (configIn == null) {
            return false;
        }
        load(configIn);
        configIn.close();
        return true;
     }
     catch (IOException e) { 
        Console.println("Could not load configuration: " + e); 
        return false;
     }
     catch (Exception e) {
        Console.println("Could not load configuration: " + e); 
        return false;
     }       
   }

   /**
    * short for: loadAsResource(configFilePath, false);
    */   
   public boolean loadAsResource(String configFilePath) {
      return loadAsResource(configFilePath, false);
   }

   /**
    * tries to load the Configuration contents from a file specified
    * as a "resource". The path "configFilePath", whether it starts with a '/' or not,
    * is interpreted relative to the directories that are on the classpath.
    * (In theory the search for "configFilePath" is determined by the ClassLoader in use, 
    * but in practice this means searching within the subdirectories determined by the classpath)
    * For the parlevink projects, "configFilePath" would normally be a path consisting
    * of a package name with the actual configuration file name appended, like
    * "parlevink/agents/agentConfiguration", refering to a file in the corresponding
    * data directory. In the example case , the "agentConfiguration" file would be found
    * in a subdirectory like "C:/java/data/parlevink/agents", assuming that the
    * java base directory would be "C:/java".
    * The boolean returned denotes whether loading was succesful or not.
    * If the file could not be found and "reportNotFound" is true, a message is
    * written to the Console. (This allows you to supress superfluous messages,
    * since a non-existing configuration file might be quite normal.)
    */   
   public boolean loadAsResource(String configFilePath, boolean reportNotFound) {
     String absPath = (configFilePath.charAt(0) == '/') ? configFilePath : "/"+configFilePath;
     //Console.println("load from " + absPath);
     try {
        InputStream configIn =  getClass().getResourceAsStream(absPath);
        if (configIn == null) { 
             if (reportNotFound)
                Console.println ("Configuration file " + absPath + " not found"); 
             return false;
        }
        load(configIn);
        configIn.close();
        return true;
     }
     catch (IOException e) { 
        Console.println("Could not load configuration: " + e); 
        return false;
     }
     catch (Exception e) {
        Console.println("Could not load configuration: " + e); 
        return false;
     }       
   }

   /**
    * saves the current Configuration contents. The name of the
    * file is determined by the "configFile" property,
    * or is taken to be to DEFAULT_CONFIG_FILE, in case the 
    * configFile property has not been set.
    */
   public void saveFile() {
      saveFile(getProperty("configFile", DEFAULT_CONFIG_FILE));
   }

   /**
    * saves the current Configuration.
    */
   public void  saveFile(String configFile) {
     try {
        OutputStream configOut = new FileOutputStream(configFile);
        store(configOut, "configuration");
        configOut.close();
     } catch (IOException e) { Console.println("Could not save configuration: " + e); }
   }

   /**
    * saves the current Configuration as a "resource". The implementation
    * is system dependent, but should be compatible with the loadAsResource methods.
    * "configFilePath" is a path that is interpreted as a subdirectory of the
    * "data" directory. The latter is assumed to exist within the curent "java base"
    * directory.  
    */
   public void  saveAsResource(String configFilePath) {
     String fullPath = (configFilePath.charAt(0) == '/') 
                       ? getDataDirPath() + configFilePath
                       : getDataDirPath() + "/" + configFilePath;
     try {
        OutputStream configOut = new FileOutputStream(fullPath);
        store(configOut, "Configuration");
        configOut.close();
     } catch (IOException e) { Console.println("Could not save configuration: " + e); }
   }


   /**
    * returns the class directory path.
    */
   public static String getClassDirPath() {
       //if (conf == null) conf = new Configuration();
       String thisClassPath = "/parlevink/util/Configuration.class";
       String p = (configurationClass.getResource(thisClassPath)).getPath();
       return p.substring(0, p.length()-thisClassPath.length()); 
   }


   /**
    * returns the "java base directory".
    * This is defined as the parent directory of the class directory.
    */
   public static String getJavaBasePath() {
       
       String classDirPath = getClassDirPath();
       return classDirPath.substring(0, classDirPath.lastIndexOf('/')); 
   }

   /**
    * returns the "data directory" path.
    * This is defined as the data sub directory of the java base directory,
    * i.e. the sibbling sub directory of the classes directory with name "data".
    */
   public static String getDataDirPath() {
       String javaBase = getJavaBasePath();
       return javaBase + "/data"; 
   }


   //public File getDataDir() {
   //    return new File(getJavaBase(), "data");
   //}


   /**
    * returns a List containing the individual files that are on the current java classpath.
    */
   public static List getClassPathSegments() {
         return (getPathSegments(System.getProperty("java.class.path")));   
   }

   /**
    * assumes that path consists of a number of segments, separated
    * by the System path separator character (';' for Windows, ':' for Unix)
    * Returns A list containing these segments as individual Strings.
    */
   public static List getPathSegments(String path) {
      List<String> pathSegments = new ArrayList<String>(10);
      char pathSeparator = (System.getProperty("path.separator")).charAt(0);
      int startSeg;
      int endSeg = -1 ;
      do {
         startSeg = endSeg+1;
         endSeg = path.indexOf(pathSeparator, startSeg);
         if (endSeg >= 0) {
            pathSegments.add(path.substring(startSeg, endSeg));
         } else {
            pathSegments.add(path.substring(startSeg));
         }
      } while (endSeg >= 0);
      return pathSegments;  
   }
    
    
   public static void main(String[] arg) {
       Console.println("Configuration test");  

       Console.println("ClassDir: " + Configuration.getClassDirPath());
       Console.println("Java base: " + Configuration.getJavaBasePath());
       Console.println("Data dir: " + Configuration.getDataDirPath());
       Console.println("Class path: " + Configuration.getClassPathSegments());
   }
    
   /**
    * DEFAULT_CONFIG_FILE = "config.txt"
    */ 
   public static final String DEFAULT_CONFIG_FILE = "config.txt";
   
  // private static Configuration conf;
   private static Class configurationClass = new Configuration().getClass();
   
   private static final long serialVersionUID = 0L;
 
}