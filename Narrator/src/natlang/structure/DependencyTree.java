package natlang.structure;
import java.util.*;

/**
 * A DependencyTree is an administrator class that holds an ordered labeled tree structure
 * This class cares that every DepTreeNode in this tree has a unique identifier
 * The root DepTreeNode always has identifier "0"
 *
 * Labeled edges should be added to nodes in left-to-right-order otherwise
 * the yield method will result in a mixed text.
 *
 * Features:
 * - root  refers to the root of the tree
 * - current refers to the actual tree node of this tree
 *	all operations are with/on the current node
 * - nodes is a Hashtable : a mapping from identifier -> nodes in the tree
 */

public class DependencyTree {

private DepTreeNode root;    // the root of this dtree
private DepTreeNode current; // the node on which operations - like add - take place
private int lastId = 0;      // the identifier of the last node added

/*
 * nodes contains all DepTreeNodes that have an identifier
 * not all these nodes need be attached to some edge in the tree already
 * however all nodes that are attached also have an id and are in the nodes table
 */
private Hashtable nodes;     // table with nodes of this dtree 
                             // (map:: nodeId:String -> node:DepTreeNode)

// constants used in query methods
private static final String TAG = "tag";
private static final String TOP = "top";
private static final String POS = "pos";
private static final String LEX = "lex";

/**
 * makes a root DepTreeNode with ident "0"
 * and data = null
 * sets current to root
 * makes an empty table nodes
 */
public DependencyTree(){
	root = new DepTreeNode(""+0,null);
	root.setRoot();
	current = root;
	nodes = new Hashtable();
	add(""+0,root);
}

/**
 * node identifiers are Strings and should be obtained by this method
 * @return a new identifier for a node
 */
private String newId(){
	lastId++;
	return ""+lastId;
}

/**
 * @return DepTreeNode with given nodeId
 */
public DepTreeNode node(String nodeId){
	return (DepTreeNode)nodes.get(nodeId);
}

/**
 * add a new empty DepTreeNode with given id to the table
 * overwrites already administrated node with same id.
 */
public void add(String nodeId){
	add(nodeId, new DepTreeNode(nodeId));
}

private void add(String nodeId, DepTreeNode node){
	nodes.put(nodeId,node);
}


/**
 * @return an iterator over the node identifiers (Strings)
 */
public Iterator identifiers(){
	return nodes.keySet().iterator();
}

/**
 * use this method to attach a new node (and a labeled edge) to an 
 * arbitrary (i.e. not necessarily the current) node in the tree.
 * add a new DepTreeNode with data to the source node with nodeId
 * the method overwrites an existing outgoing edge with same label.  
 * @param nodeId the existing mother node of the new added node
 * @param label the label of the new edge from mother to new node
 * @param data the TreeNodeData stored in the node
 * @return true iff a new node is added succesfull
 * @return false iff node could not be added; if nodeId is not id of existing node
 */
public boolean addTreeNode(String nodeId, String label, TreeNodeData data){
	DepTreeNode mot = node(nodeId);
	if  (mot==null) return false;
	String nnid = newId();
	DepTreeNode dn = new DepTreeNode(nnid,data);
	nodes.put(nnid,dn);
	mot.addEdge(label,dn);
	return true;
}

/**
 * add a new node with a new nodeId to the nodes table
 * @param data the TreeNodeData stored in the node
 */	
public boolean addTable(TreeNodeData data){
	String nid = newId();
	DepTreeNode dn = new DepTreeNode(nid,data);
	nodes.put(nid,dn);
	return true;
}

/**
 * requires that both motherId and dotterId are present in nodes table
 * add to the tree a labeled edge from mother to dotter node
 * @param motherId the id of the mother node to be retrieved in the table
 * @return true iff added
 */
public boolean addTreeNode(String motherId, String label, String dotterId){
	DepTreeNode mot = node(motherId);
	if (mot==null) return false;
	DepTreeNode dot = node(dotterId);
	if (dot==null) return false;
	mot.addEdge(label,dot);
	return true;	
}

/**
 * @return id of current node
 */
public String nodeId(){
	return current.name();
}


/** 
 * set the current tree node to that with ident as identifier
 * if ident is not an identifier current is not reset
 * @param ident the identifier that becomes the new current tree node
 * @return true iff the current node is reset
 */
public boolean setCurrent(String ident){
	DepTreeNode tn = (DepTreeNode)nodes.get(ident);
	if (tn==null) return false;
	current = tn;
	return true;
}

/**
 * set current to mother of current 
 * @return false iff current == root
 */
public boolean up(){
	if (current==root) return false;
	current=(DepTreeNode)current.mother();
	return true;
}

/**
 * go form current node down the edges labeled with the given path
 * set current to the node at the end
 * do not set current if the given path does not exist
 * @param path a list of edge labels starting from current node
 * @return true iff current is set according to the path given
 */
public boolean down(List path){
	String lab;
	DepTreeNode store_curr = current; // for restoring if there is no path
	for (int i=0; i < path.size(); i++){
		lab = (String)path.get(i);
		if (!down(lab)) {
			current = store_curr;
			return false;
		}	
	}
	return true;
}

/**
 * set current to daughter at edge with lab
 * @return true iff current is set to the indicated daughter
 */
public boolean down(String lab){
	DepTreeNode tn = (DepTreeNode)current.get(lab);
	if (tn==null) return false;
	current = tn;
	return true;	
}

/**
 * set current to root
 */
public void reset(){
	current=root;
}


/**
 * @return the root of this dtree
 */
public DepTreeNode getRoot(){
	return root;
}

public DepTreeNode current(){ 
	return current;
}

/**
 * @return true iff current is a leaf (has no outgoing edges)
 */
public boolean isLeaf(){
	return current.isLeaf();
}

/**
 * @return true iff this DependencyTree is complete
 */
public boolean isComplete(){
	return current.isComplete();
}

/**
 * @return true iff current equals the root 
 */
public boolean isTop(){
	return current==root;
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
	return current.setYield();
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
	if (featName.equals(POS)) return current.getCat();
	if (featName.equals(TAG)) return current.getTag();
	if (featName.equals(LEX) ){
		List list =current.setYield(); 
		return concat(list);
	} 
	return null;
}

// lst is a list of Strings ; 
// concatenates the strings with " " in between
private static String concat(List lst){
	if (lst==null || lst.size()==0) 
		return "";
	String result = "";
	int i=0;
	while ( i < lst.size()-1 ){
		result+=(String)lst.get(i)+" ";
		i++;
	}
	return result+=(String)lst.get(i);
}
	

/**
 * @return a String representing the edge label - i.e. role/function - of the edge connecting this to it's context
 * @return constant TOP iff this has no mother
 */
public String getRole(){
	if (current.mother()==null) return TOP;
	return current.incomingLabel();
}

} // end class DependencyTree
