/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:22 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Resources.java,v $
// Revision 1.1  2006/05/24 09:00:22  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.10  2004/10/14 15:45:55  zwiers
// temp
//
// Revision 1.9  2004/10/01 16:17:00  zwiers
// comment update
//
// Revision 1.8  2004/09/22 13:31:39  zwiers
// main method removed
//
// Revision 1.7  2004/09/16 16:19:42  zwiers
// Reader changed into BufferedReader
//
// Revision 1.6  2004/09/08 16:03:13  zwiers
// new style
//
// Revision 1.5  2004/08/31 13:20:32  zwiers
// new style of loading
//
// Revision 1.4  2004/07/09 11:26:55  zwiers
// minor update
//
// Revision 1.3  2004/06/23 08:15:20  zwiers
// getReader method added
//
// Revision 1.2  2004/06/18 16:04:03  zwiers
// comment update
//
// Revision 1.1  2003/09/02 11:41:07  zwiers
// initial version
//

package parlevink.util;


import java.io.*;
import java.net.*;
import parlevink.util.*;

/**
 * Resources are objects that serve as an ``anchor'' for loading files
 * called ``resources''. Such files ar assumed to be at a fixed place 
 * within the directory structure associated with a project. 
 * They are, however, not assumed to be at a fixed place within the file
 * system, so as to avoid undesirable system dependencies.
 * The methods in Resources all depend, ultimately, on
 * the Java getResource() and getResourceAsStream() methods.
 * These methods search for resource files within directories that have their
 * path name included within the Java classpath, but extended by a path
 * segment that is determined by the package name of some Class. 
 * (The ``dots'' in the package name are replaced by slashes or backlashes) 
 * Example: if the ``/C:/java/data'' directory is included on the Java class path,
 * and the package name that is used would be ``project.projectX'', then
 * resources would be serached for within the ``/C:/java/data/project/projectX''
 * directory. Note that when the data directory would be relocated to an other place
 * within the file system, it suffices to modify the Java class path.
 * There are two styles for using the Resources class:
 * <ol>
 * <li> Use the the static Resource methods getFileReader() and getFileURL().
 *       This requires that you call Resources.setBase(this) within the initialization
 *       of your ``main application'', that determines the ``root'' directory of the project.
 *       This will set the ``root'' for all resource loading.
 * <li> Create a new Resource instance by calling the constructor Resources(this)
 *      within one of your project classes. Then use the (non-static) methods
 *      getReader() and getURL() on that Resources instance. The advantage of this style
 *      is that you can have several Resources Objects, each determining one particular
 *      ``root'' for a set of related resources.
 * </ol>
 */
public class Resources {
  
   /*
    * create a new Resources object, that determines the Class that will
    * serve as the "root" for getURL() and getInputStream().
    * This doesn't work unless you extend the Resources class;
    * The extension should be defined within the ``root'' directory
    * of your project, and thus defines the same ``root'' within the 
    * parlevink data directories.
    * This constructor should be regarded obsolete, since it
    * is easier to use the Resources(Object) constructor.
    */
   public Resources() {  
      // This will result in setting the proper root Class,
      // in particular for Classes that extend Resources:
      resourceRoot = adjustPath(this.getClass().getPackage().getName());
   }
   
   /**
    * create a new Resources object, that determines the Class that will
    * serve as the "root" for resources. The package name of the rootObject
    * determines the name of the data directory; this data directory
    * is searched for within the directories that are on the java classpath.
    * (We normally assume that the ...java/data directory is 
    * included on that class path, so if rootObject belongs, say, to
    * package ``project.projectX'', then resources should be placed within
    * the ``.../java/data/project/projectX'' directory, and its sub directories.
    */
   public Resources(Object rootObject) {  
      resourceRoot = adjustPath(rootObject.getClass().getPackage().getName());
   }
  

   /**
    * create a new Resources object by means of specifying the Class that will
    * serve as the "root" for resources. The package name of the rootClass parameter
    * determines the name of the data directory; this data directory
    * is searched for within the directories that are on the java classpath.
    * (We normally assume that the ...java/data directory is 
    * included on that class path, so if rootObject belongs, say, to
    * package ``project.projectX'', then resources should be placed within
    * the ``.../java/data/project/projectX'' directory, and its sub directories.
    */
   public Resources(Class rootClass) {  
      resourceRoot = adjustPath(rootClass.getPackage().getName());
   }

   /**
    * create a new Resources object, that determines the directory that will
    * serve as the "root" for resources. This directory
    * is searched for within the directories that are on the java classpath.
    */
   public Resources(String resourceDir) {  
      resourceRoot = adjustPath(resourceDir);
   }

   /**
    * returns the URL for a file name. 
    * Assume that this method is called for a class C that extends
    * "Resources". Call package prefix for this class "package".
    * If "filename" is a relative path, i.e. if it does not start with a
    * "/", then the package prefix is prepended, and the resulting file
    * is searched for on the classpath. 
    * For the Parlevink project,
    * we assume that the data directory is included in the classpath.
    * If "filename" is an absolute file name", than the package prefix is 
    * ignored, and the file is searched for on the class path.
    */
   public URL getURL(String fileName) {
      String resource; 
      if (fileName.length() > 0) {
         char firstChar = fileName.charAt(0);
         if (firstChar == '/' || firstChar == '\\') {
            resource = fileName;
         } else {
            resource = resourceRoot + fileName;
         }   
      } else {
         resource = fileName;
      }     
      URL url = loader.getResource(resource);
      if (url == null) {
      	Console.println("Cannot find resource file: " + resource);
      }
      return url; 	
   }





   /**
    * Opens a file, and returns an InputStream for the file.
    * Assume that this method is called for a class with name C that extends
    * "Resources". Call the package prefix for this C class "package".
    * If "filename" is a relative path, i.e. if it does not start with a
    * "/", then the package prefix is prepended, and the resulting file
    * is searched for on the classpath. 
    * For the Parlevink project,
    * we assume that the data directory is included in the classpath.
    * If "filename" is an absolute file name", than the package prefix is 
    * ignored, and the file is searched for on the class path.
    */
   public InputStream getInputStream(String fileName) {
      String resource; 
      char firstChar = fileName.charAt(0);
      if (firstChar == '/' || firstChar == '\\') {
         resource = fileName;
      } else {
         resource = resourceRoot + fileName;
      }        
      InputStream stream = loader.getResourceAsStream(resource);
      if (stream == null) {
        Console.println("Cannot find resource file: " + resource);
      }
      return stream; 	
   }


   /**
    * Opens a file, and returns a (buffered) Reader for the file.
    * Assume that this method is called for a class with name C that extends
    * "Resources". Call the package prefix for this C class "package".
    * If "filename" is a relative path, i.e. if it does not start with a
    * "/", then the package prefix is prepended, and the resulting file
    * is searched for on the classpath. 
    * For the Parlevink project,
    * we assume that the data directory is included in the classpath.
    * If "filename" is an absolute file name", than the package prefix is 
    * ignored, and the file is searched for on the class path.
    */
   public BufferedReader getReader(String fileName) {
      InputStream stream = getInputStream(fileName);
      if (stream == null) {
         return null;
      } else {
         return new BufferedReader(new InputStreamReader(stream)); 
      }	
   }

   /**
    * returns the root Class, i.e the Class that is used as "root"
    * for resolving file names.
    */
   public String getResourceRoot() {
      return resourceRoot;
   }

   /**
    * (re)sets the root Class, i.e the Class that is used as "root"
    * for resolving file names. Initially, this rootClass is the 
    * Class of "this" Object.
    */   
   public void setResourceRoot(String root) {
      this.resourceRoot = adjustPath(root);

   }

   /*
    * replaces \\ by /, . by /, and appends a / at the end, if necessary
    * idea: rootPath could be specified in DOS format, or as package name.
    * It should be converted to a normal path.
    */
   private static final String adjustPath(String rootPath) {
      String result = rootPath.replace('\\', '/');
      result = result.replace('.', '/');
      int len = result.length();
      if (len > 0 && result.charAt(result.length()-1) != '/') {
         result = result + '/';
      }
      return result;
   }


   /**
    * returns a Reader for a file (not a file URL). 
    * If fileName starts with a / character, it is 
    * searched for in the data directory (in fact also in the class directory)
    * If fileName does not start with a /, the class prefix, defined by the clas name of
    * the Class that extends this Resources Clas is used.
    * This class prefix is prepended for the fileName, and the result is searched for
    * in the date (and class) directory. 
    */
   public static InputStream getFileStream(String fileName) {
      if (globalResourceRoot == null) {
         Console.println("Resource base directory not defined");
         return null;
      }
      String resource; 
      char firstChar = fileName.charAt(0);
      if (firstChar == '/' || firstChar == '\\') {
         resource = fileName;
      } else {
         resource = globalResourceRoot + fileName;
      }        
      InputStream stream = loader.getResourceAsStream(resource);
      if (stream == null) {
        Console.println("Cannot find resource file: " + resource);
      }
      return stream; 	
   }



   /**
    * returns a Reader for a file (not a file URL). 
    * If fileName starts with a / character, it is 
    * searched for in the data directory (in fact also in the class directory)
    * If fileName does not start with a /, the class prefix, defined by the clas name of
    * the Class that extends this Resources Clas is used.
    * This class prefix is prepended for the fileName, and the result is searched for
    * in the date (and class) directory. 
    */
   public static BufferedReader getFileReader(String fileName) {
      InputStream stream = getFileStream(fileName);
      if (stream == null) {
         return null;
      } else {
         return new BufferedReader(new InputStreamReader(stream)); 
      }	
   }


   /**
    * returns the URL for a file. 
    * If fileName starts with a / character, it is 
    * searched for in the data directory (in fact also in the class directory)
    * If fileName does not start with a /, the class prefix, defined by the class name of
    * the Class that extends this Resources Class is used.
    * This class prefix is prepended for the fileName, and the result is searched for
    * in the date (and class) directory. 
    */
   public static URL getFileURL(String fileName) {
      if (globalResourceRoot == null) {
         Console.println("Resource base directory not defined");
         return null;
      }
      String resource; 
      char firstChar = fileName.charAt(0);
      if (firstChar == '/' || firstChar == '\\') {
         resource = fileName;
      } else {
         resource = globalResourceRoot + fileName;
      }        
      URL url = loader.getResource(resource);
      if (url == null) {
      	Console.println("Cannot find resource file: " + resource);
      }
      return url; 	
   }

   

   /**
    * defines the resource base, by means of the package to which rootObject belongs
    * Resource files will be searched for in the data directories that have 
    * the relative name derived from the package name for the Class of the obj Object,
    * and that can be found in one of the directories that are listed on the 
    * java classpath. Within the parlevink tree this means that resources will
    * be serached for in a director like ``/java/data/project/projextX'', assuming that
    * obj is an Object with some Class type defined within package ``project.projectX''
    * Hint: call Resources.setBase(this) during initialization of your 
    * ``main application'' class, that you define in the ``root'' directory of your project.  
    */
   public static void setBase(Object rootObject) {
      globalResourceRoot = adjustPath(rootObject.getClass().getPackage().getName());
   }
         

   /**
    * Like setBase(Object), except that here you specify the root Class, rather than an instance
    * of that Class, as the parameter that defines the package name.
    */
   public static void setBase(Class rootClass) {
      globalResourceRoot = adjustPath(rootClass.getPackage().getName());
   }   
   
   /**
    * Like setBase(Object), except that here you specify the resource directory itself,
    * as a directory name, relative to the java/data directory
    */
   public static void setBase(String globalResourceDir) {  
      globalResourceRoot = adjustPath(globalResourceDir);
   }


   public String getDataFileName(String fileName) {
      if (dataDir == null || fileName == null || fileName.length() == 0) return null;
      String resource; 
      char firstChar = fileName.charAt(0);
      if (firstChar == '/' || firstChar == '\\') {
         resource = dataDir + fileName;
      } else {
         resource = dataDir + resourceRoot + fileName;
      }   
      return resource;

   }       
   

   /**
    * returns the full path name of the java data directory, or null
    * if no such directory could be found.
    * For instance, when running from a jar file, no data directory can be used,
    * unless the default data directory was set explicity.
    * Note that for reading data files, it is usually better to use methods
    * like getURL() or getReader(), since they will function even for jar files,
    * provided the data directory was included into the jar file.
    * For writing to data files, this is not the case, however.
    */
   public static String getDataDir() {     
      return dataDir;      
   }
   
   /**
    * sets the data directory that will be used if the normal initialization
    * cannot determine the standard java data directory, for instance,
    * when running the application from an executable jar file.
    * The specified data directory should be a full path name, like
    * for instance: "/C:/java/data".
    */
   public static void setDefaultDataDirectory(String dataDir) {
      defaultDataDir = dataDir;
   }

   
   public static String globalResourceRoot; // global (``static'') root directory for resources.

   //public Class rootClass;
   public String resourceRoot = ""; // root directory of resources, within the java/data directory
   static ClassLoader loader = new Resources().getClass().getClassLoader();
   
   static URL dataURL; 
   static String dataDir = null;
   static String defaultDataDir = null;
   
   static {
      dataURL = loader.getResource("parlevink/util/Resources.class");
      if (dataURL != null) {
         String classPath = dataURL.getPath();
         String classSuffix = "/classes/.*";
         String dataSuffix = "/data/";
         if (classPath.matches(".*" + classSuffix)) {
            dataDir = classPath.replaceFirst(classSuffix, dataSuffix);
         } else { //probably running from a jar file.
            dataDir = null;
         }
      } else {
         Console.println("Could not locate the data directory");  
      }
   }
   

//   public static void main(String[] arg) {
//       Resources res = new Resources();
//      // Console.println("data file: " + res.getDataFileName("help.jpg"));
//       
//       Console.println("Data dir: " + getDataDir());
//       Console.println("data file name for help: " + res.getDataFileName("help.jpg"));
//      
//   }

  
}