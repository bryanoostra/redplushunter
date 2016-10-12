/**
 * 
 */
package natlang.rdg.datafilessetter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @author Rene Zeeders
 *
 */
public class DataInstanceSetter {

	private Vector<DataInstance> datainstances;
	
	
	public DataInstanceSetter(){
		datainstances = new Vector<DataInstance>();
	}
	
	public void addDataInstance(InputStream in, String lang, String baseURI){
		datainstances.add(new DataInstance(in, lang, baseURI));
	}
	
	public void addDataInstance(InputStream in, String lang){
		datainstances.add(new DataInstance(in, lang));
	}
	
	public void addDataInstanceList(Vector<DataInstance> list){
		datainstances.addAll(list);
	}
	
	public Iterator<DataInstance> getDataInstanceIterator(){
		Iterator<DataInstance> it = datainstances.iterator();
		return it;
	}
	
	
	
	/**
	 * Reads the given config file and converts all named
	 * files to inputstreams. The format of the config file should be:
	 * <em>filename|lang[|baseURI]</em>
	 * in which the filename is the name of the file to be read, lang is
	 * the language ("RDF/XML", "TRIG", "TURTLE") and baseURI is the base URI
	 * when a file does not contain named graphs. 
	 * @param filename the name of the config file to be read
	 */
	public void readConfigFile(String filename){
		try{
			BufferedReader configIn = new BufferedReader(new FileReader(filename));
			String line = configIn.readLine();
			
			// first line contains directory
			String dir = line;
			
			line = configIn.readLine();
			while(line != null){
				System.out.println("line: " + line);
				String[] fields = line.split("[|]");
				System.out.println(fields.length);
				for(int i = 0; i < fields.length; i++)
					System.out.print(fields[i] + " ");
				System.out.println("");
				if(fields.length == 2){
					datainstances.add(new DataInstance(new FileInputStream(dir+fields[0]), fields[1], "file:" + fields[0]));
				}
				else if(fields.length == 3){
					datainstances.add(new DataInstance(new FileInputStream(dir+fields[0]), fields[1], fields[2]));
				}
				else {
					System.err.println("Could not read config file '" + filename + "'!");
					System.err.println("format should be:");
					System.err.println("\tfilename|lang|baseURI");
					System.err.println("or");
					System.err.println("\tfilename|lang|baseURI");
					System.exit(0);
				}
					
				line = configIn.readLine();
			}
		}
		catch(FileNotFoundException e){
			System.err.println("File '" + filename + "' not found!");
			System.exit(0);
		}
		catch(IOException e){
			System.err.println("IO Exception while reading file '" + filename + "'!");
			System.exit(0);
		}
		
		
	}
}
