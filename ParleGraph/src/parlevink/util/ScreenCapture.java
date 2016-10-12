/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:21 $      
 */

// Last modification by: $Author: swartjes $
// $Log: ScreenCapture.java,v $
// Revision 1.1  2006/05/24 09:00:21  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2005/09/07 15:31:06  zwiers
// initial version
//


package parlevink.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JFileChooser;
import java.util.regex.*;


/**
 * ScreenCapture contains static methods for taking screen shots, and save them as jpg files.
 * The pictures are taken using the Robot.createScreenCapture() method, which implies that
 * a Mouse pointer is not visible. 
 * The basic usage of ScreenCapture is to call one of the screenshot() methods. 
 * This will yake a screenshot, either of the full screen, some selected GUI Component, 
 * or some specified screen area.
 * If a certain GUI Component is specfified, then screenshots will follow the area occupied
 * by that Component, even if it is dragged or resized. 
 * The picure will be saved in a jpg file, where the file name is determined via a Dialog.
 * If no ScreenCapture parameters have been set at all, this dialog will suggest a file name
 * like Screenshot001.jpg, in  a directory like My Documents, with the option to select
 * something different. In default mode, ScreenCapture does not overwrite existing files, and will
 * present the Dialog again if the selected file already exists. It is possible to let ScreenCapture
 * generate filenames. Optionally, it possible to present the user a directory selection Dialog
 * by calling screenshotDirectoryDialog() method, or to set this directory programmatically, 
 * by calling setScreenshotDirectory(), It is also possible to set a so called base name by calling
 * the setScreenshotBaseName() method, to be used in combination with a numeric String for file name
 * generation. For instance, if the base name is set to &quot;shot&quot;, then the generated files 
 * would have names like &quot;shot001&quot;, &quot;shot002&quot;. If the base name would be &quot;shot1&quot;
 * The generated names would be like &quot;shot1&quot;, &quot;shot2&quot; etc.
 * A similar file name generation process is used even when file name generation is not enabled,
 * but only to suggest new names in Dialogs. Finaly, it is always possible to specify the screenshot file
 * directly, either by calling setScreenshotFile() before screenshot() is called, or by specifying the
 * desired file name as argument for the screenshot() method. Usually, the screenshot methods save the file
 * immediately. It is possible to suppress this, and to save by calling a saveImage() method. 
 */
public class ScreenCapture  {

   public static String FILEDIALOGTITLE = "Save Screenshot";
   public static String DIRDIALOGTITLE = "Select screenshot directory";
   public static String DEFAULTSCREENSHOTFILE = "Screenshot";
   public static String DEFAULTSTARTNUMBER = "000"; // files will (by default) be numbered Screenshot001, Screenshot002, etc
   
   /**
    * sets the directory where screenshot images will be stored.
    * The default is the default directory from the (system dependent) FileSystemView.
    * Typically something like "My Documents". 
    */
   public static void setScreenshotDirectory(File dir) {
      currentDirectory = dir;
   }

   /**
    * Specifies the so called base name for screeshot files, used in combination
    * with a numeric string when file names are being generated.
    */
   public static void setScreenshotBaseName(String baseName) {
      if (baseName != null && baseName.length() > 0) {
         screenshotBaseName = baseName;
      }
   }
   /**
    * if set to true, a file name for the last screenshot will be generated each time saveImage()
    * is called without a File argument (or with a null File argument).
    */
   public static void setGenerateFileNames(boolean generateNames) {
       generateFileNames = generateNames;  
   }
   
   /**
    * if overwriteAllowed is set to true, new screenshots can overwrite existing files.
    * The default setting is false. 
    */
   public static void setOverwriteAllowed(boolean b) {
      overwriteAllowed = b;
   }
   
   /*
    * defines the curent screenshotFile, the generatorBaseName, and sets screenshotFileSet to true;
    */
   private static void setScreenshotFile(File f) {
      if (f == null) return;
      currentDirectory = f.getParentFile();
      String s = f.getName();

      int i = s.lastIndexOf('.');
      if (i > 0 &&  i < s.length() - 1) {
         screenshotBaseName = s.substring(0, i);
      } else {
         screenshotBaseName = s;        
      }   
      screenshotFile = new File(currentDirectory, screenshotBaseName+fileExtension);
      screenshotFileSet = true;      
   }


   /**
    * Sets the selected area for taking screen shots.
    * This area will be used when no area is specified for screenshot calls.
    * A null argument specifies "full screen". (The default)
    */
   public static void setSelectedArea(Rectangle area) {
      if (area == null) {
         selectedArea = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
      } else {
         selectedArea = area;
      }
      component = null;
   }

   /**
    * Sets the Component, to be used for the section area of screenshots.
    * A null argument selects "full screen".
    */
   public static void setComponent(Component c) {
      if (c == null) {
         selectedArea = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
         component = null;
      } else {
         component = c;
      }
   }
   
   
   /**
    * Makes a single screenshot, and saves it.
    */
   public static void screenshot() {
      if (component != null) {
         screenshot(component, true);
      } else {
         screenshot(selectedArea, true);  
      }
   }

   /**
    * makes a single screen shot, using the specified screen area. 
    * The screenshot is saved.
    */
   public static void screenshot(Component c) {      
      screenshot(c, true);
   }


   /**
    * Makes a single screenshot, using the specified screen area. 
    * The screenshot is saved.
    */
   public static void screenshot(Rectangle area) {
      screenshot(area, true);
   }

   /**
    * Makes a single screen shot, using the specified screen area.
    * it can be saved later with a saveImage call.
    */
   public static void screenshot(Component c, boolean savePicture) {
      component = c;
      selectedArea = new Rectangle(c.getLocationOnScreen(), c.getSize());
      try {
         image = new Robot().createScreenCapture(selectedArea );
         if (savePicture) saveImage();
      } catch (Exception e) {
        Console.println("ScreenShot: " + e);
      }
   }


   /**
    * Makes a single screen shot, using the specified screen area. 
    * When save Picture is true, the new screenshot is saved immediately,
    * opening a dialog if necessary to detrmine the file name.
    * it can be saved later with a saveImage call.
    */
   public static void screenshot(Rectangle area, boolean savePicture) {
      selectedArea = area;
      component = null;
      try {
         image = new Robot().createScreenCapture(selectedArea );
         if (savePicture) saveImage();
      } catch (Exception e) {
        Console.println("ScreenShot: " + e);
      }
   
   }


   /**
    * Opens a File dialog, and uses the selected file to define the screenshotFile.
    * This also defines the generator base name, when file names are to be generated.
    */
   public static boolean screenshotFileDialog() {
      checkJFileChooser();           
      File f;
      boolean ok = false;
      do {      
         if (! screenshotFileSet) generateFile();
         fileChooser.setSelectedFile(screenshotFile); 
          screenshotFileSet = false; 
         int result = fileChooser.showDialog(parent, "OK"); // null parent is allowed: default positioning will be used.
         if (result == JFileChooser.APPROVE_OPTION) {
            f = fileChooser.getSelectedFile();                   
            
            
            if (f.isDirectory()) {
               screenshotBaseName = DEFAULTSCREENSHOTFILE;
               currentDirectory = f;
               // not yet  ok
            } else {
               ok = (overwriteAllowed || ! f.exists());
               currentDirectory = fileChooser.getCurrentDirectory();     
            }                              
         } else {
            screenshotFileSet = false; 
            return false;
         }
      } while ( ! ok );
      setScreenshotFile(f);
      return true;
   }

   /*
    * creates the JFileChooser, if it is still null.
    */
   private static void checkJFileChooser() {
      if (fileChooser == null) {
          fileChooser = new JFileChooser();
          //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          //fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);    
          javax.swing.filechooser.FileFilter jpgFilter = new javax.swing.filechooser.FileFilter() {
             public boolean accept(File f) {
                if (f.isDirectory()) return true;      
                String extension = getExtension(f);
                return extension != null &&  extension.equals("jpg");
             }
   
             //The description of this filter
             public String getDescription() {
                 return "JPG Images";
             }
         };   
         fileChooser.setFileFilter(jpgFilter);        
         fileChooser.setDialogTitle(FILEDIALOGTITLE);  
      }       
   }


   /*
    * creates the directory Chooser, if it is still null.
    */
   private static void checkDirectoryChooser() {
      if (dirChooser == null) {
          dirChooser = new JFileChooser();
          dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
          //dirChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);    
          javax.swing.filechooser.FileFilter dirFilter = new javax.swing.filechooser.FileFilter() {
             public boolean accept(File f) {
               return true;
             }   
             //The description of this filter
             public String getDescription() {
                 return "screenshot directory";
             }
         };   
         dirChooser.setFileFilter(dirFilter);        
         dirChooser.setDialogTitle(DIRDIALOGTITLE);  
      }       
   }
   
   /**
    * presents a file dialog to the user, allowing him to select a directory, to be used
    * for storing screenshot images. 
    */
   public static boolean screenshotDirectoryDialog() {
      checkDirectoryChooser();  
      int result = dirChooser.showDialog(parent, "OK"); // null parent is allowed: default positioning will be used.
      if (result != JFileChooser.APPROVE_OPTION) return false;
      currentDirectory = dirChooser.getSelectedFile();
      return true;        
   }

   /**
    * Saves the last screenshot, with specified file.
    * Usually, this method need not to be called by the user, since
    * the screenshot() methods will call it.
    */
   public static void saveImage(File f) {
      if (f == null) {
         saveImage();
      }
      try {
        //Console.println("Save screenshot file: " + screenshotFile);
         OutputStream  out  = new FileOutputStream(screenshotFile);
         JPEGCodec.createJPEGEncoder( out ).encode( image );
         out.close(); 
      } catch (Exception e) {
          Console.println("ScreenCapture: " + e);  
      } finally {
         screenshotFileSet = false;  
      }
   }

   /**
    * Save the last screenshot. When the screenshot file name has not been set, 
    * a Dialog is opened, or a file name is generated, depending on the settings.
    * Usually, this method need not to be called by the user, since
    * the screenshot() methods will call it.
    */
   public static void saveImage() {
      if (generateFileNames) {
             generateFile();
      } else if ( ! screenshotFileSet) {
             boolean ok = screenshotFileDialog();
             if (! ok) {
               //Console.println("Cancel...");
               return; // return without saving
             }
      }
      if (screenshotFile == null) return;
      saveImage(screenshotFile);
   }


   /*
    * Assumes that ns is a String consisting of digits only, representing some number n
    * returns a String that represents n+1, with at least the same length as ns, including leading zeros
    * if necessary. (So, 123 becomes 124, 003 become 004, 999 becomes 1000 etc)
    */
   private static String incrementNumberString(String ns) {
      int len = ns.length();
      if (len == 0) return "1";
      int n;
      try {
        n = Integer.parseInt(ns);
      } catch (Exception e) {
          return ns;  
      }
      n++;
      String nsinc = Integer.toString(n);
      int nsincLen = nsinc.length();
      StringBuffer b = new StringBuffer(len);
      for (int i=0; i<(len-nsincLen); i++) b.append('0');
      b.append(nsinc);
      return b.toString();
   }
   
   /*
    * generates a new screenshotFile, by increasing the number in the current screenshotBaseName.
    * If overwriteAllowed is false, this process is repeated until a non-existing file is found.
    */
   private static void generateFile() {
      //Console.println("generate file screenshotBaseName = " + screenshotBaseName);
      Matcher m = numberPattern.matcher(screenshotBaseName);
      String base;
      String num;
      if (m.matches()) {
         base = screenshotBaseName.substring(m.start(1), m.end(1));
         num = screenshotBaseName.substring(m.start(2), m.end(2));
      } else {
         base = screenshotBaseName;
         num = DEFAULTSTARTNUMBER;
      }
      screenshotBaseName = base + num;
      screenshotFile = new File(currentDirectory, screenshotBaseName+fileExtension);
      do {
         num = incrementNumberString(num);
         screenshotBaseName = base + num;
         screenshotFile = new File(currentDirectory, screenshotBaseName+fileExtension);
      } while (! overwriteAllowed && screenshotFile.exists());     
      screenshotFileSet = true;
   }

   /**
    * Get the extension of a file.
    */
   private static String getExtension(File f) {
      String ext = null;
      String s = f.getName();
      int i = s.lastIndexOf('.');
   
      if (i > 0 &&  i < s.length() - 1) {
          ext = s.substring(i+1).toLowerCase();
      }
      return ext;
   }


   private static Component parent = null;
   private static File screenshotFile;
   private static File currentDirectory;
   private static String screenshotBaseName;
  
   private static final String fileExtension = ".jpg";
   private static boolean overwriteAllowed = false;
     
   private static boolean screenshotFileSet = false;
   private static boolean generateFileNames = false;   
   private static JFileChooser fileChooser = null;
   private static JFileChooser dirChooser = null;
   private static Rectangle selectedArea;
   private static Component component;
   private static BufferedImage image;   
   private static Pattern numberPattern = Pattern.compile("(.*?)(\\d\\d*)$");
   
   
   static {
        currentDirectory = FileSystemView.getFileSystemView().getDefaultDirectory();
        screenshotBaseName = DEFAULTSCREENSHOTFILE;
        setComponent(null); // specifies "full screen"
   }

}