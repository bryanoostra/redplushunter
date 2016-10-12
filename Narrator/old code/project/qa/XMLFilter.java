package project.qa;

/*Last change: 5 Oktober 2004 XMLFilter.java
   By: Erik Dikkers
   Description: A filefilter for only XML files, when selecting a question and fragment.
   Only selection based on *.xml extension, not retrieving any information out of the file itself.
*/

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class XMLFilter extends FileFilter {

    	//Accept only XML files
    	public boolean accept(File f) {
    	
    		if (f.isDirectory()) {
			return true;
    		}

	        String extension = getExtension(f);
        
        	if (extension != null) {
            		if (extension.equals("xml")) {
                    		return true;
            		} else {
                		return false;
            		}
        	}
        
        	return false;
    	}

    	//A description for this filter
    	public String getDescription() {
        	return "By Alpino parsed XML file";
    	}
    
    	//Retrieving the extension of a file
    	public static String getExtension(File f) {
        	String ext = null;
        	String s = f.getName();
        	int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
        		ext = s.substring(i+1).toLowerCase();
        	}
        	
        	return ext;
    }
}