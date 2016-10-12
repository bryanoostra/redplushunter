package natlang.structure;
import java.util.*;


/**
 * A DependencyAnalyser is a minimal implementation of interface DependencyStructure
 * It only contains methods to query the underlying DependencyTree
 * This class wraps a DependencyTree model; that is an administrative class
 * for a DepTreeNode 
 * Subtrees of the tree model are obtained by setting the current variable in the model
 * using the fact that operations in DependencyTree work on the current tree node. 
 * 
 * @author Rieks op den Akker
 * @version december 2002  
 */

public class DependencyAnalyser implements DependencyStructure {

private DependencyTree model;

private static final String TAG = "tag";
/**
 * @param model the DependencyTree wrapped in this class
 */
public DependencyAnalyser(DependencyTree model){
	this.model = model;
}

/**
 * @return an iterator over the labels of the outgoing edges of the root of this structure
 * @return null if this is a leaf (has no outgoing edges)
 */ 
public Iterator labels(){
	return model.current().labels();
}

/**
 * 			TOP
 *                       |
 *                      DS_0
 *                 -------------
 *                 |            |
 *              SU-|            |-HEAD
 *                 |            |
 *                DS_1         DS_2
 *              de man        loopt 
 *
 * DS_0.getLexicalValue() equals ["de man","loopt"]
 *
 * @return a list of the lexical values of all nodes covered by this structure
 */
public List getLexicalValue(){
	return model.getLexicalValue();
}

/**
 * Get the feature value of a given feature 
 *  
 * For the special feature LEX it holds that: 
 *		DS_0.getFeatureValue(LEX) equals "de man loopt"
 * In general for LEX: the concatenation of the String elements (with a space in between)
 * in the List returned by getLexicalValue() equals the value of the LEX feature
 * 
 * @param featName name of the feature whose value is queried
 * @return the features value of the given feature
 * @return null if featName is not the name of a feature of this structure
 */
public String getFeatureValue(String featName){
	return model.getFeatureValue(featName);
}

/**
 * @param path a list of edge label Strings
 * @return the DependencyStructure at the end of the path following the edge labels in the given list
 * @return null if there is no such path or the structure is missing (latter only occurs in case of incomplete structure)
 */
public DependencyStructure get(List path){
	model.down(path);
	return this;
}

/**
 * get(edgeLabel) returns the daughter that is the target of the -unique edge labeled with edgeLabel
 * i.e.: returns the direct sub structure that has the role denoted by edgeLabel
 * For example ds.get(SUBJECT) is the structure that has the SUBJECT role in this structure 
 * Special feature: get(TOP) returns the top most context (equals this if this is the topmost context)
 * @param edgeLabel an edge label String
 * @return the DependencyStructure at the end of edge with the given label
 * @return null if there is no such label or such a structure is missing (latter only occurs in case of incomplete structure)
 */
public DependencyStructure get(String edgeLabel){
	if (edgeLabel.equals(TOP))
		model.reset();
	else 
		model.down(edgeLabel);
	return this;	
}

/**
 * @return true iff this DependencyStructure is complete
 */
public boolean isComplete(){
	return model.isComplete();
}

/**
 * @return true iff this is a leaf (has no outgoing edges)
 */
public boolean isLeaf(){
	return model.isLeaf();
}

/**
 * @return true iff this.get(TOP) equals this
 */
public boolean isTop(){
	return model.isTop();
}

/**
 * @return the immediate context of this DependencyStructure (in fact the mother of this seen as a daughter)
 * @return this structure it self if this has no context (it is the root of all roots)
 */
public DependencyStructure getContext(){
	model.up();
	return this;
}


/**
 * @return a String representing the edge label - i.e. role/function - of the edge connecting this to it's context
 * @return constant TOP iff this has no mother
 */
public String getRole(){
	return model.getRole();
}


/**
 * getTopContext() should be equal to get(TOP)
 * @return the top most context of this structure
 * @return this object itself if this is the top most structure
 */
public DependencyStructure getTopContext(){
	model.reset();
	return this;
}

public String toString(){
	return "DependencyAnalyser";
}

public String nodeId(){
	return model.nodeId();
}

} // end DependencyAnalyser
