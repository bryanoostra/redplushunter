/* @author Dennis Reidsma
 * @version  0, revision $Revision: 1.1 $,
 */
// Last modification by: $Author: swartjes $
// $Log: RTSI.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:12  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.4  2005/10/20 10:07:25  zwiers
// Java1.5 version of parlevink.xml
//
// Revision 1.3  2003/07/09 12:29:43  dennisr
// *** empty log message ***
//
// Revision 1.2  2003/07/09 09:20:53  dennisr
// sourcedoc
//
// Revision 1.1  2003/07/09 09:11:43  dennisr
// first cvs add
//
package parlevink.util;

import java.io.*;
import java.net.URL;
import java.net.JarURLConnection;
import java.util.jar.*;
import java.util.zip.*;
import java.util.*;
import java.net.*;

/**
 * RunTime Subclass Identification.
 * First version.
 * <p>code under construction?
 * <p>Does NOT find classes in jar files, despite any information stating the opposite. 
 * This will be added in the future.
 * <p>
 * This utility class can iterate classes:
 * <br>* all classes that are somehow accessible,
 * <br>* all classes implementing or inheriting from a given interface or class
 * <p>
 * Finds classes in classpath, root of current package, and all 
 * <i>currently loaded</i> packages including jar files.
 * <p>
 * Does not rely on instantiation of candidate classes, so classes with no empty 
 * constructor can also be found.
 * <p>
 * Remember: this tool finds many classes in many packages, maybe even 
 * more than you expect, but there is NO GARANTUEE that <b>all</b> classes are found.
 * <p>
 * If you want to make sure that classes from a certain package are found, 
 * it must either be in the classpath, or you must instantiate a
 * class from that package (any class will do).
 * <br> Based on the class RTSI by <a href="mailto:daniel@satlive.org">Daniel Le Berre</a>
 * <br> Extension by Dennis Reidsma
 *
 * @author Dennis Reidsma
 */
public class RTSI {

        static String fileSep = System.getProperty("file.separator");
        static String pathSep = System.getProperty("path.separator");
        static String lineSep = System.getProperty("line.separator");
        static String userName = System.getProperty("user.name");
        static String userHome = System.getProperty("user.home");
        static String userDir = System.getProperty("user.dir");
        static String classPath = System.getProperty("java.class.path");
    
    /**
     * singleton instance
     */
    private static RTSI theRTSI;

    /**
     * Returns the singleton instance
     */
    public static RTSI getRTSI() {
        if (theRTSI == null)
            theRTSI = new RTSI();
        return theRTSI;
    }


    /**
     * Returns an Iterator that gives all known classes
     * in the given package that subclass or implement
     * the given interface or class.
     * <p>
     * The Iterator iterates Class objects.
     */
/*    public Iterator allSubClasses(Package pack, Class superClassOrInterface) {
        @@@ string packagename, ipv pack?
	    Iterator allClassIt = allClasses(pack);
	    return new ClassFilter(allClassIt, superClassOrInterface);
    }
*/


    /**
     * Returns a set of possible starting URLs where you might find class files. 
     * These may include jar files, elements in the classpath, and package locations of
     * currently loaded packages.
     * <p>
     * NB: The returned URLs are all valid locations, but do NOT necessarily contain actual
     * class-files!
     */
    public Set<URL> getPossiblePackageRoots () {

        Set<URL> possibleStartLocations = new HashSet<URL>();
        URL url;

        // class root for this package
      	url = RTSI.class.getResource("/"); 
        possibleStartLocations.add(url);

        // class path elements
        StringTokenizer st = new StringTokenizer(classPath,pathSep);
        while (st.hasMoreTokens()) {
            String s= st.nextToken();
            try {
                File f = new File(s);
                url = new URL("file:" + fileSep + f.getCanonicalPath());
                possibleStartLocations.add(url);
            } catch (IOException e) {
            }
        }

        // currently loaded packages
		Package [] pcks = Package.getPackages();
	    for (int i=0;i<pcks.length;i++) {
            String name = pcks[i].getName();
	        if (!name.startsWith(fileSep)) {
	            name = fileSep + name;
	        }	
	        name = name.replace('.',fileSep.charAt(0));
	        try {
	    	    url = new URL("file:" + fileSep + name);
    		    possibleStartLocations.add(url);
            } catch (MalformedURLException ex) {
            }
	    }
        
        return possibleStartLocations;   
    }

    /**
     * Returns an iterator of all Class objects that can be found...
     */
    public Iterator<Class<?>> getClasses() {
        Set<URL> possibleStartLocations = getPossiblePackageRoots();

        Iterator<String> classFilesIt = new ClassFileIterator(possibleStartLocations);
        Iterator<Class<?>> classesIt = new IteratorTransform<String, Class<?>>(classFilesIt, new ClassFileToClassTransform());
        return new IteratorFilter<Class<?>>(classesIt, new Predicate<Class<?>>() {
                                                 public boolean valid(Class<?> c) {
                                                     return c != null;
                                                 }
                                             });
    }

    /**
     * Returns an Iterator that gives all known classes
     * in the currently loaded packages that subclass or implement
     * the given interface or class.
     * <p>
     * The Iterator iterates Class objects.
     */
    public Iterator<Class<?>> getSubClasses(Class<?> superClassOrInterface) {
	    Iterator<Class<?>> allClassIt = getClasses();
	    Predicate<Class<?>> pred = new ClassPredicate(superClassOrInterface);
	    return new IteratorFilter<Class<?>>(allClassIt, pred);
    }

    /**
     * Filters an Iterator of Class objects on whether the elements 
     * are subclasses/implementations for the class passed into the
     * constructor.
     */
    private class ClassPredicate implements Predicate<Class<?>> {
        Class<?> classToFilter;
        
        public ClassPredicate(Class<?> superClassOrInterface) {
            classToFilter = superClassOrInterface;
        }
        
        public boolean valid(Class<?> c) {
            return classToFilter.isAssignableFrom(c);
        }
            
    }

    /**
     * Transforms class file names (full path) to Class objects.
     * This is done by guessing which part of the path belongs to
     * the package name and which part is superfluous.
     * <p>
     * Returns null whenever fails to find
     */
    private class ClassFileToClassTransform implements Transform<String, Class<?>> {
        
        public Class<?> transform(String fileName) {
            //input is a string containing the full filename for a classfile.
            //output is a corresponding Class object.
            //packagename is determined by trying to recognize the longest 
            //package-name postfix to the path.
           // String fileName = (String)o;
//            System.out.println("Filename: " + fileName);
            String className = fileName.substring(fileName.lastIndexOf(fileSep)+1);
//            System.out.println("classname: " + className);
            String packageName = fileName.substring(0,fileName.lastIndexOf(fileSep)+1);
            packageName = packageName.replace(fileSep.charAt(0), '.');
            while (packageName.endsWith(".")) {
                packageName = packageName.substring(0, packageName.length() - 1);
            }
//            System.out.println("Initial packagename: " + packageName);
            Class<?> result = null;
            while (result == null) {
                String fullName = className;
                if (!packageName.equals("")) {
                    fullName = packageName + "." + fullName;
                }
//                System.out.println("trying fullname: " + fullName);
                try {
                    result = Class.forName(fullName);
                    break; //succesful: then return this class
                } catch (Exception ex) {
//                    System.out.println("----------error creating class object. info:---------");
//                    System.out.println(ex);
                    //ex.printStackTrace();
//                    System.out.println("----------end info-----------------------------------");
                } catch (Error ex) {
//                    System.out.println("----------error creating class object. info:---------");
//                    System.out.println(ex);
                    //ex.printStackTrace();
//                    System.out.println("----------end info-----------------------------------");
                }
                if ((packageName.indexOf(".") < 0) || (packageName.indexOf(".") == packageName.length() - 1)) {
                    packageName = "";
//                    System.out.println("no packageName: " + className);
                    try {
                        result = Class.forName(className);
                        break; //succesful: then return this class
                    } catch (Exception ex) {
//                        System.out.println("----------***error creating class object. info:---------");
//                        System.out.println(ex);
                        //ex.printStackTrace();
//                        System.out.println("----------end info-----------------------------------");
                    } catch (Error ex) {
    //                    System.out.println("----------error creating class object. info:---------");
    //                    System.out.println(ex);
                        //ex.printStackTrace();
    //                    System.out.println("----------end info-----------------------------------");
                    }
                    break; //nothing more to guess, no package... probably an inaccessible class? log deze classes maar eens
                } 
                packageName = packageName.substring(packageName.indexOf(".")+1);
//                System.out.println("trying packageName: " + packageName);
            }
            return result;
        }
            
    }

    /** 
     * Iterates full names of classfiles (as strings!!) from a set of initial locations
     */
    private class ClassFileIterator implements Iterator<String> {
        
        QueueCollection todoQ;

        String[] filesInCurrentDirectory;
        String currentDirectory;
        int indexInCurrentDirectory;
        
        Set<URL> visited;
        
        String next;

        public ClassFileIterator(Set possibleStartLocations) {
            todoQ = new QueueCollection(possibleStartLocations);
            visited = new HashSet<URL>();
            indexInCurrentDirectory = 0;
            filesInCurrentDirectory = new String[0];
            currentDirectory = "";
            moveToNextObject();
        }

        public boolean hasNext() {
            return next != null;
        }
        
        public void remove(){
            throw new UnsupportedOperationException();
        }
        
        public String next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            String result = next;
            moveToNextObject();
            return result;
        }
    
        /**
         * pre: 
         * <ol><li>filesInCurrentDirectory is an array of files that are in the directory
         *         that is currently being examined
         *     <li>indexInCurrentDirectory points to the first file that has not yet been processed
         *     <li>next is the previously returned Class name
         *     <li>todoQ is a queue of URLs that have not yet been examined
         * </ol>
         * effect:
         * <ol>
         *     <li>
         * </ol>
         * post: 
         * <ol><li>next != null || !hasNext()
         * </ol>
         */
        private void moveToNextObject() {
            next = null;
            while (next == null) {
                //if no more files in this list: get next todo item from queue. 
                //If no more items: break
                if (indexInCurrentDirectory >= filesInCurrentDirectory.length) {
                    if (todoQ.isEmpty()) {
                        break; //nothing left to process. next will stay null
                    }
                    URL nextURL = (URL)todoQ.get(); //there SHALL be a next item
                    if (visited.contains(nextURL)) { // already visited: continue, and in next 
                                                     // iteration try next queue item
                        continue; 
                    }
                    visited.add(nextURL);
                	File directory = new File(nextURL.getFile());
                	if (!directory.exists()) { //only *exisiting* directories. Skip, try next queue item
                	    System.out.println("URL may be JAR file: " + nextURL);//@@@!!!
                	    continue;
                	}
                	if (!directory.isDirectory()) { //only exisiting *directories*. Skip, try next queue item
                	    continue;
                	}
                    // At this point directory is an actual existing directory.
                    // Get its files, put them in the list, initialise index.
                    try {
                        currentDirectory = directory.getCanonicalPath();
                    } catch (IOException ex) {
                        //can't get it, so continue
                        continue;
                    }
                    filesInCurrentDirectory = directory.list();
                    indexInCurrentDirectory = 0;
                    if (filesInCurrentDirectory.length <= 0) { //too bad, empty directory. Try next one.
                        continue;
                    }
                }
                //At this point, indexInCurrentDirectory points to a file or directory in
                //filesInCurrentDirectory. Get it, increase index; process filename
                String nextFileName = currentDirectory + fileSep + filesInCurrentDirectory[indexInCurrentDirectory++];
                //System.out.println("Examining file: " + nextFileName);
                //try to create a file out of it
                File f = new File(nextFileName);
                //if it is a directory: add to queue, continue next loop iteration
                if (f.exists() && f.isDirectory()) {
                    try {
                        todoQ.add(f.toURL());
                    } catch (MalformedURLException ex) {
                        //can't get it, so continue
                        continue;
                    }
                    continue;
                }
                //if it doesn't exist: be surprised and skip it (why was it returned at all?)
                if (!f.exists()) {
                    continue;
                }
                //At this point, it is a file and it exists. test whether it ends with class. 
                //If not, skip.
                if (!nextFileName.endsWith(".class")) {
                    continue;
                }
                //if so: get classname out of it (what shall be the packagename?) 
                //and put that in next and break. (Yes! We found the next class!)
    		    // removes the .class extension
    		    String classname = nextFileName.substring(0,nextFileName.length()-6);
    		    // WE STILL DON"T KNOW THE PACKAGENAME, ONLY THE FULL PATH!
    		    //lets first try returning that, and see what we get. You can use 
    		    //iteratorfilters and -transforms to find classes for these files.
                next = classname;
            }
        }
    }       

    /**
     * Command line implementation.
     */
    public static void main(String []args) throws Exception {
        String className = "java.util.Iterator";
        if (args.length > 0) {
            className = args[0];
        } else {
            System.out.println("======================================================");
            System.out.println("Command line usage: RTSI <full classname>");
            System.out.println("API usage: see javadoc documentation.");
            System.out.println("");
            System.out.println("Below follows the result for \"RTSI java.util.Iterator\"");
            System.out.println("======================================================");
        }            
        Iterator vcIt = getRTSI().getSubClasses(Class.forName(className));
        while (vcIt.hasNext()) {
            System.out.println("next component: " + vcIt.next());
        }
    }

    
}