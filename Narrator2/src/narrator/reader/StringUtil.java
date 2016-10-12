package narrator.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;
//import java.util.Scanner;
import java.util.Scanner;

import narrator.Main;

public class StringUtil {
	public static String getBetween(String string, String startString, String endString){
		int start = string.indexOf(startString);
		int end=-1;
		if (start>=0) 
			end = start +startString.length()+ string.substring(start+startString.length()).indexOf(endString);
		//System.out.println(start+startString.length()+", "+end);
		if (end-(start+startString.length())>=0)
			return string.substring(start+startString.length(),end);
		else return "";
	}
	
	public static List<String> getAllBetween(String string, String startString, String endString){
		List<String> result = new ArrayList<String>();
		int start = string.indexOf(startString);
		int end=-1;
		if (start>=0)
			end = start + string.substring(start).indexOf(endString);
		while (start!=-1&&end!=-1){
			end = start+startString.length() + string.substring(start+startString.length()).indexOf(endString);
			//System.out.println(start+startString.length()+", "+end);
			if (end-(start+startString.length())>=0) result.add(string.substring(start+startString.length(),end));
			string = string.substring(end);
			start = string.indexOf(startString);
		}
		return result;
	}
	
    public static String convertStreamToString(InputStream is) throws IOException {
	/*
	 * To convert the InputStream to String we use the
	 * Reader.read(char[] buffer) method. We iterate until the
	 * Reader return -1 which means there's no more data to
	 * read. We use the StringWriter class to produce the string.
	 */
		if (is != null) {
		    Writer writer = new StringWriter();
		
		    char[] buffer = new char[1024];
		    try {
		        Reader reader = new BufferedReader(
		                new InputStreamReader(is, "UTF-8"));
		        int n;
		        while ((n = reader.read(buffer)) != -1) {
		            writer.write(buffer, 0, n);
		        }
		    } finally {
		        is.close();
		    }
		    return writer.toString();
		} else {        
		    return "";
		}
    }
    
/*    private String readFile(String pathname) throws IOException {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");
        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }*/
    
	//This code is actually not even THAT horrible.
	public static String getSettingFile(String fabula, String prefix, String suffix){
        File file = new File(fabula);
        Scanner scanner = null;
        String result = "";
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			Main.error("File not found or other problem with the Fabula file!");
		}
        try {
            while(scanner.hasNextLine()) {        
                String s = scanner.nextLine();
                String getBetween = StringUtil.getBetween(s, prefix, suffix);
                if (!getBetween.equals("")){
                	//System.out.println(getBetween);
                	result = getBetween;
                	break;
                }
            }
        } finally {
            scanner.close();
        }
        return result;
	}
    
}