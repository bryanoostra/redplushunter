package natlang.deptree;

import natlang.deptree.model.*;
import java.io.*;
import java.net.*;

/** 
 * Uses the alpino site from the RuG to build a dutch dependency structure
 * if for some reason the site is offline, naturally this class only can give error message
 * the site it gets is data from is http://wodan.let.rug.nl/vannoord_bin/
 *
 */

public class DepTreeParserConnection {

private static String alpino_url = "ziu.let.rug.nl/vannoord_bin/alpino?words="; //"http://wodan.let.rug.nl/vannoord_bin/alpino?words=";

/**
 * The  method connects to the alpino site and parse the sentence and builds
 * the tree structure.
 * @param parseString	The Dutch Sentence which is to be translated in a tree dependencystructure
 */
public static DutchDependencyStructure parseFromAlpino(String parseString) {
   	
      DutchDependencyStructure ds = new DutchDependencyStructure(parseString);
      
   
      
   // The url where we want to cut out our data
      String RequestUrl = alpino_url;
   // replace spaces in parseString with %20 and put it in RequestUrl
      int at = parseString.indexOf(' ')+1;
      int start = 0;
      while (at > 0){
         RequestUrl += (parseString.substring(start,at-1))+"%20";
         start=at;
      	 at = parseString.indexOf(' ',at)+1;
      }
      RequestUrl += parseString.substring(start,parseString.length());
   // Fetch URL this gives us the script location
      try {
         URL url = new URL(RequestUrl);
         RequestUrl = "";
         URLConnection uc = url.openConnection();
         BufferedReader br = new BufferedReader(new InputStreamReader (uc.getInputStream()));
         try {
            int pos = -1;
            String ProcessStr="";
            while (pos < 0) {
              ProcessStr = br.readLine();
              pos = ProcessStr.indexOf("<param name=script value=");
            }
            RequestUrl = ProcessStr.substring(pos+26,ProcessStr.indexOf("\">",27+pos));
         } catch (IOException e) {
            System.err.println("DutchDependencyStructure: Unexpected end of file. Could not extract script-file");
         }
         br.close();
      } catch(IOException e) {
      	System.err.println("DutchDependencyStructure: Server doesn't exists or I can't understand him.");
      }
   // Now fetch script file
      if (RequestUrl.compareTo("") != 0) {
         try {
            URL url = new URL(RequestUrl);
            RequestUrl = "";
            URLConnection uc = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader (uc.getInputStream()));
            try {
               int pos = -1;
               String ProcessStr=br.readLine();
               if (ProcessStr== null) {
               	  System.err.println("DutchDependencyStructure: Server doesn't respond");
               	  return ds;
               }
               start = ProcessStr.indexOf('\"');
               int end = ProcessStr.indexOf('\"',start+1);
               parseString = ProcessStr.substring(start+1,end); // Retrives the actual analyzed sentence
               ProcessStr=br.readLine(); 
               if (ProcessStr.indexOf("leaf") < 0) br.readLine(); //This sets the top node, but that's already known
               ProcessStr = br.readLine();
            // interprete script
               do {
                 ds.ProcessLine(ProcessStr,ds.edgeMap(),ds.edgeLabelList());
                 ProcessStr = br.readLine();
                 pos = ProcessStr.indexOf("hide()");
               } while (pos != 0);
               br.close();
               br = null;
               // Ok we have read all the script lines. Now we have to reconstruct the Textlines in the good places in the tree
               ds.ReconstructTextInTree(parseString);
            } catch (IOException e) {
               System.err.println("DutchDependencyStructure: script-file is ill-formated");
            }
            if (br != null) br.close();
         } catch(IOException e) {
      	   System.err.println("DutchDependencyStructure: Server doesn't exists or I can't understand him.");
         }         
      } else System.err.println("DutchDependencyStructure: Unexpected end of file. Could not extract script-file");
   return ds;
   }
}
