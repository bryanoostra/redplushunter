package natlang.structure;
import java.util.*;
import java.io.*;

public class DepRelParser{

private static CGNRelationSet relset;



/**
 * read and parse a CGN DECLARATIONS specification
 * after executing this method succesfully 
 * the CGN_DeclsSet contains the declarations read from the reader
 * It can be obtained by methode declset()
 * @throws IOException iff file could not be opened or contains errors
 */
public static void parseRels(BufferedReader reader) throws IOException {
	relset = CGNRelationSet.parse(reader);	
}

/**
 * read and parse a CGN DECLARATIONS specification
 * after executing this method succesfully 
 * the CGN_DeclsSet contains the declarations read form the reader
 * It can be obtained by methode declset()
 * @throws IOException iff file could not be opened or contains errors
 */
public static void parseRels(String filename) throws IOException {
	BufferedReader reader =
  		new BufferedReader(new FileReader(filename));
	parseRels(reader);
	reader.close();	
}

/**
 * 
 * After executing method parseDecls succesfully 
 * the CGN_DeclSet contains the declarations read from a specifcation file
 * It can be obtained by methode declset()
 */
public static CGNRelationSet relset(){
	return relset;
}

/**
 * print the DeclSet on a file
 * uses a PrintWriter
 * one Declaration per line
 * @throws IOException iff file could not be openend
 */
public static void printRels(String filename)throws IOException{
	
        PrintWriter pw = new PrintWriter(new FileWriter(filename));
	if (relset!=null)
		relset.write(pw);
	else
	    System.out.println("DepRelParser.print():relset == null");
		
}

// for testing only
public static void main(String[] args){
	String filename;
	
	/************tags***************************************/
	
	filename = "cgntest1.txt"; //tagger.cgn.Console.readString("dependency relations filename :");
	try{
		DepRelParser.parseRels(filename);
		printRels("rel_test.out");
		if (relset()==null){
			System.out.println("relation set is null");
		}
		else{
			DependencyViewer viewer = new DependencyViewer(relset.dependencyStructure());
			viewer.start();
		}
	}
	catch(IOException exc){
		System.out.println("ERROR :"+exc.getMessage());
	}
} // end main

} // end of class

 