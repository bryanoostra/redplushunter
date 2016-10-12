package natlang.deptree.model; 

import java.io.*;
import java.util.*;

/**
 * CGNRelationSet defines a Set of CGNDependencyRelations
 * 
 * The way to make a CGNRelationSet is to use the static method parse(BufferedReader)
 *
 *
 * @see CGNDependencyRelation
 */


public class CGNRelationSet  {

private static int lastId = 1;      // the identifier of the last node added

// if a line in the spec file starts with COMMENT the line is skipped
// as if it was a comment-line.
public static final String COMMENT = ";";

/**
 * node identifiers are Strings and should be obtained by this method
 * @return a new identifier for a node
 */
private static String newId(){
	lastId++;
	return ""+lastId;
}

// the Hash table with keys identifiers and values the relations
List relations;
// the DependencyTree that is constructed from the relations
DependencyTree tree;


protected CGNRelationSet(){
	relations = new ArrayList();	
}

public DependencyTree getTree(){
	if (tree==null){
		if (makeTree())
			return tree;
		else    tree=null;
	}    
	return tree;
}

public DependencyStructure dependencyStructure(){
	if (getTree()== null) 
		return null;
	else
		return new DependencyAnalyser(tree);
}

// make the tree from the list dependency relations
// returns false if something goes wrong constructing the tree 
private boolean makeTree(){
	tree = new DependencyTree();
	CGNDependencyRelation decl;
	DepTreeNode dotter, mother;
	String word, lex;
	String tag;
	String pos;   // morph
	String label; // edge
	String parent;
	for (Iterator iter=iterator();iter.hasNext();){
		decl= (CGNDependencyRelation)iter.next();
		word=decl.word;     // either the lexeme or #nodeId
		tag=decl.tag;
		pos=decl.morph;
		label=decl.edge;
		parent=decl.parent;  // id of parent 
		if (word.startsWith("#")){ // word is a reference to a nodeId
			word=word.substring(1,word.length()); // word == nodeId
				
			dotter = tree.node(word);
			if (dotter==null) return false; // node not found error
			// store the data in the new node
			dotter.setTag(tag);
			dotter.setCat(pos);
			// get or make mother node
			mother = tree.node(parent);
			if (mother==null) {
				tree.add(parent);
				mother=tree.node(parent);
			}
			// add the labeled edge to the tree 
			tree.addTreeNode(parent, label, word);
		}// end if
		else{                // a leaf node should be created
		        lex = word;
		        String lexNodeId = newId(); // 
			tree.add(lexNodeId);
			dotter = tree.node(lexNodeId);
			dotter.setLeaf();
			dotter.setLexicalValue(lex);
			// store the data in the new node
			dotter.setTag(tag);
			dotter.setCat(pos);
			// get or make mother node
			mother = tree.node(parent);
			if (mother==null) {
				tree.add(parent);
				mother=tree.node(parent);
			}
			// add the labeled edge to the tree 
			tree.addTreeNode(parent, label, lexNodeId);
		}// end else
	}// end for
	return true;
}// end method makeTree

private void add(CGNDependencyRelation decl){
	if (decl!=null)
		relations.add(decl);
}

/**
 * makes a CGNRelationSet from a reader that contains the spec of the relations
 * @throws IOException if there is an error in the file
 */
public static CGNRelationSet parse(BufferedReader reader) throws IOException {
	 CGNRelationSet result = new CGNRelationSet();
	 String line;
	 while ( (line=reader.readLine()) != null ){
	 	line = line.trim();
	 	
	 	if (!line.equals("") && !line.startsWith(COMMENT))
	 		result.add(CGNDependencyRelation.parse(line));
	 }
	 return result;
}

/**
 * @return an Iterator over the CGNDependencyRelations stored in this Set
 */
public Iterator iterator(){
	return relations.iterator();
}

/**
 * print this on a file
 * uses a PrintWriter
 * one Declaration per line
 * @throws IOException iff file could not be openend
 */
public void print(String filename)throws IOException{
        PrintWriter pw = new PrintWriter(new FileWriter(filename));
	write(pw);		
}

	

public void write(PrintWriter writer){
	Iterator iter = this.iterator();
	while(iter.hasNext())
		writer.println(iter.next());
	writer.flush();
}

/**
 * required format: an identifier of a CGNDependencyRelation is a String formatted: Dnn where nn are digits
 * @return the CGNDependencyRelation with given index
 * @return null iff no such declaration is known
 */
public CGNDependencyRelation get(int i){
	return (CGNDependencyRelation)relations.get(i);
}

} // end class
		