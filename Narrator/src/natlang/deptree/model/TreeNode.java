package natlang.deptree.model;

import parlevink.parlegraph.model.*;
import java.util.*;

/**
 * A TreeNode is a node for a tree structure
 * Every node has:
 * - a name;
 * - a possibly empty list of labeled edges connecting it with her daughters
 * - an Object for data stored in the node
 * - a reference to the her mother node
 * - a reference to the label of the incoming edge
 * A TreeNode can be:
 * - a root (in a tree)
 * - a leaf (in a tree)
 */

public class TreeNode extends MVertexAdapter {

Object data; // the data stored in this tree node
private TreeNode mother; // her mother
protected Hashtable dotters; // table of daughters
boolean root; // is this a root
boolean leaf; // is this a leaf
String mlabel; // label of incoming edge
String name;// name of this tree node

/**
 * the uniqueness of names is not guaranteed by this class
 * clients should take care of that
 * the data may be null
 * @param name the name of the tree node
 * @param data the data stored in this tree node
 */
public TreeNode(String name, Object data){
	super();
	this.name = name;
	this.data = data;
}

public String name(){
	return name;
}

public void setName(String n){
	name = n;
}

public void setData(Object o){
	data = o;
}

public Object data(){
	return data;
}

public TreeNode mother(){
	return mother;
}

public boolean setIncomingLabel(String lab){
	if (root) return false;
	mlabel = lab;
	return true;
}

/**
 * you can't set an incoming edge of a root node
 * set the - unique- incoming edge of this tree node
 * only if the names of mother equals name of source of edge
 * and name of this equals name of target of edge
 * @param m the mother TreeNode set as incoming edge of this tree node
 * @return true iff setting the edge was succesfull; false otherwise
 */
public boolean setMother(TreeNode m){
	if (root) return false;
	if (mother!=null) return false;
	this.mother = m;
	return true;
}

/**
 * you can't add an edge to a leaf
 * beware: an existing edge with same label is overwritten
 * add an edge with an empty node as new daugther
 * @param label the edge label attached to the new edge
 * @return true iff edge is added
 */
public boolean addEdge(String label){
	return addEdge(label, new TreeNode("",null));
}

/**
 * you can't add a daughter to a leaf node
 * beware: an existing edge with the same label is overwritten
 * @param tnode may not be null
 * @return true iff edge is added; else return false
 */
public boolean addEdge(String lab, TreeNode tnode){
	if (leaf) return false;
	if (tnode==null) return false;
	if (dotters==null) dotters = new Hashtable();
	dotters.put(lab,tnode);
	tnode.mother=this;
	tnode.mlabel=lab;
	return true;
}
	

/**
 * @return an iterator over the labels of the outgoing edges of this tree node
 * @return null if this is a leaf (has no outgoing edges)
 */ 
public Iterator labels(){
	if (leaf || dotters==null) return null;
	return  dotters.keySet().iterator(); 
}

public boolean hasEdgeLabel(String lab){
	if (leaf || dotters==null) return false;
	return dotters.containsKey(lab);
}


/**
 * @param path a list of edge label Strings
 * @return the TreeNode at the end of the path following the edge labels in the given list
 * @return null if there is no such path or the structure is missing (latter only occurs in case of incomplete structure)
 */
public TreeNode get(List path){
	if (path==null) return this;
	TreeNode dot = this;
	String lab;
	for (Iterator iter=path.iterator(); iter.hasNext();){
	    lab = (String)iter.next();
	    dot = dot.get(lab); 
	    if (dot==null) return null;
	}
	return dot;
}

/**
 * get(edgeLabel) returns the daugther that is the target of the -unique- edge labeled with edgeLabel
 * @param edgeLabel an edge label String
 * @return the TreeNode at the end of edge with the given label
 * @return null if there is no such label or such a structure is missing (latter only occurs in case of incomplete structure)
 */
public TreeNode get(String edgeLabel){
        if (leaf || dotters==null) return null;
	return (TreeNode)dotters.get(edgeLabel);
}



/**
 * @return the number of daughters (outgoing edges)
 */
public int arity(){
	if (dotters==null) return 0;
	return dotters.size();
}

/**
 * @return a List with the daughter TreeNodes of this tree node
 * @return an empty List if there are no daugthers 
 */
public List daughters(){
	List dlist = new ArrayList();
	if (dotters==null) return dlist;
	DepTreeNode dotter;
	for (Iterator iter = dotters.values().iterator();iter.hasNext();){
		dotter = (DepTreeNode)iter.next();
		dlist.add(dotter);
	}
	return dlist;
}

/**
 * @return the label of the incoming edge
 * @return null if this has no incoming edge
 */
public String incomingLabel(){
	if (root) return null;
	else return mlabel;
}

public boolean setLeaf(){
	if (dotters!=null) return false;
	leaf = true;
	return true;
}

public boolean isLeaf(){
	return leaf;
}

public boolean isRoot(){
	return root;
}

public void setRoot(){
	root = true;
}

public String toString(){
	return "TreeNode";
}

} // end TreeNode
