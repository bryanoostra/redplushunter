/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: Info.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.3  2004/10/14 15:45:55  zwiers
// temp
//
// Revision 1.2  2004/06/10 07:56:04  zwiers
// no message
//
// Revision 1.1  2003/09/02 11:42:32  zwiers
// initial version
//

package parlevink.util;

/**
 * The info class is intended to be used as "Main class" when
 * the package is jarred.
 * Running java -jar <packageJarFile> will print some package information.
 * Note that this information is only available from the Manifest.mf file,
 * that is included in the jar file. In particular, no info is available
 * when running directly from the class files.
 */
public class Info {
   
   
   public Info() {
   }
   
   // Change this to reflect the current WORKING version:
   public static final String CURRENTVERSION = "1.1";
   // The fully qualified package name:
   public static final String packageName = "parlevink.util";
   //The vendor string:
   public static final String VENDOR = "University of Twente, TKI";


   public static String specificationTitle;
   public static String specificationVersion;
   public static String specificationVendor; 
   private static Package pack;
   
   static {

      // the following queries return null, unless run from a jar file with
      // a suitable Manifest.mf file:
      if (packageName == null) Console.println("NULL package name");
      pack = Package.getPackage(packageName);
      specificationTitle = pack.getSpecificationTitle();
      if (specificationTitle == null) specificationTitle = packageName;
      specificationVersion = pack.getSpecificationVersion();
      if (specificationVersion == null) specificationVersion = CURRENTVERSION;
      specificationVendor = pack.getSpecificationVendor();
      if (specificationVendor == null) specificationVendor = "UT";   
   }
 
   
   /*
    * Tries to obtain information from Manifest.mf, and prints it.
    */
   public static void main(String[] arg) {
      //System.out.println("Package info for " + packageName);
      if(specificationTitle != null) {
          System.out.println(specificationTitle);
      }
      if(specificationVersion != null) {
          System.out.println("Version: " + specificationVersion);
      } 
      System.out.println("Working version: " + CURRENTVERSION);
      if (specificationVendor != null) {
         System.out.println(specificationVendor);
      }
      Resources res = new Resources();
      String dataDir = Resources.getDataDir();
      if (dataDir == null) dataDir = "NULL";
      System.out.println("data dir: " + dataDir);
      

   }
   
   /**
    * returns the specification version, extracted from the Manifest.mf file,
    * if packaged in a jar file; otherwise returns the value of CURRENTVERSION
    */
   public static String getVersion() {
        return specificationVersion;
   }
      
   /**
    * returns the Package object for this package
    */
   public static Package getPackage() {
       return pack;  
   }

   /**
    * returns the specification title for this package
    */ 
   public static String getDescription() {
      return specificationTitle;
   }

   /**
    * returns the vendor string for this package
    */       
   public static String getVendor() {
      return specificationVendor;
   }
  
}
