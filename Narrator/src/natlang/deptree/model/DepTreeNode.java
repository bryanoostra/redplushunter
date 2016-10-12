package natlang.deptree.model;
import java.util.*;

/**
 * A DepTreeNode is a TreeNode for DependencyTrees 
 * what is special is that instead of Objects node data is of type TreeNodeData
 *
 * Every node has:
 * - a name;
 * - a possibly empty list of labeled edges connecting it with her daughters
 * - a TreeNodeData object for data stored in the node
 * - a reference to her mother node
 * - a reference to the label of the incoming edge
 * A TreeNode can be:
 * - a root (in a tree)
 * - a leaf (in a tree)
 */

public class DepTreeNode extends TreeNode{

// order = Inter.parseInt(nodeid)
// it can be used for defining an order on edges of a node
private int order;

/**
 * the uniqueness of names is not guaranteed by this class
 * clients should take care of that
 * if data==null then this.data = new TreeNodeData()
 * @param ident the string repr of the number which is an id for this tree node
 * @param data the data stored in this tree node
 */
public DepTreeNode(String ident, TreeNodeData data){
	super(ident,data);
	try{
		order = Integer.parseInt(ident);
		if (data==null)
			this.data = new TreeNodeData();
	}catch(NumberFormatException exc){
		System.out.println("WARNING: DepTreeNode id: "+ident+" is not a number.");
	}
}

/**
 * @param ident should be a number string
 */
public DepTreeNode(String ident){
	this(ident,new TreeNodeData());
}

public void setData(TreeNodeData data){
	this.data=data;
	if (data==null)
		this.data=new TreeNodeData();
}

public TreeNodeData getData(){
	return (TreeNodeData)super.data();
}

public void setCat(String cat){
	getData().setCat(cat);
}

public void setTag(String tag){
	getData().setTag(tag);
}

public void setYield(List y){ 
	getData().setYield(y);
}

/**
 * apply this method before getYield() when the tree has been changed
 * this method (re)computes the yield of this tree node data
 * and recursively of all children of this tree node
 * @return the new computed yield list of this tree node
 */
public List setYield(){
	if (leaf){
		if (getYield()==null)
			setYield(new ArrayList());
		return getYield();
	}else{
		List result = new ArrayList();
		// compute result = concat of setYields() of all children
		List dlist;
		List dotters = this.daughters();
		for (int i=0; i < dotters.size(); i++){
			dlist=((DepTreeNode)dotters.get(i)).setYield();
			result.addAll(dlist);
		}
		setYield(result);
		return result;
	}	
}


/**
 * this method will only be applied to leaf nodes
 * set the yield of the data of this node at [lex]
 */
public void setLexicalValue(String lex){
	List lst = new ArrayList();
	lst.add(lex);
	setYield(lst);
}

public String getCat(){ return getData().getCat();}
public String getTag(){ return getData().getTag();}

/**
 * this method does not (re)compute the yield
 * to (re)compute the yield use method setYield()
 */
public List getYield(){ 
	return getData().getYield();
}

public boolean isComplete(){
	return false;
}

/**
 * order() computes minumum ident number of it's daughter nodes
 * order() is identical to ident number for leaf nodes
 * @return the order number in the tree.
 */
public int order(){
	int nd = arity();
	if (nd==0)
		return this.order;
	// compute minimum of orders of daughters of this node
	else{
	        int min = 0;
	        int ord;
		Iterator iter = dotters.values().iterator();
		if (iter.hasNext())
			min = ((DepTreeNode)iter.next()).order();
		while (iter.hasNext()){
			ord = ((DepTreeNode)iter.next()).order();
			if (ord < min ) min = ord;
		}
		this.order = min; 
		return min;
	}
}

/**
 * this method overwrites the super class method
 * the method cares for the order of daughters
 * @return a List Iterator with the daughter TreeNodes of this tree node
 * @return -if there are no daughters- an empty list
 */
public List daughters(){
	List dlist = new ArrayList();
	if (dotters==null||arity()==0) 
		return dlist;
	DepTreeNode dotter;
	for (Iterator iter = dotters.values().iterator();iter.hasNext();){
		dotter = (DepTreeNode)iter.next();
		dlist.add(dotter);
	}
	sortList(dlist);
	return dlist;
}

// assume this works as promised in the Collections API
public static void sortList(List dlist){
	Collections.sort(dlist,comp);
}

private static Comparator comp = new TreeComparator();

public boolean equals(Object o){
	DepTreeNode n = (DepTreeNode)o;
	return n.name().equals(this.name());
}

public String toXMLString(){
 StringBuffer sb = new StringBuffer();
 List childs = daughters();
 DepTreeNode node;
 sb.append("<node>\n");
 for (int i=0;i<childs.size();i++){
 		node = (DepTreeNode)(childs.get(i));
 		sb.append(node.toXMLString());	
 } 
 sb.append("</node>\n");
 return new String(sb);	
	
}

// inner class
static class TreeComparator implements Comparator{

/**
 *	Compares its two arguments for order. 
 *	Returns a negative integer, zero, or a positive integer 
 *	as the first argument is less than, equal to, or greater than the second.
*/
public int compare(Object o1,Object o2){
	DepTreeNode n1 = (DepTreeNode)o1;
	DepTreeNode n2 = (DepTreeNode)o2;
	return n1.order() - n2.order();
}


	
} // end inner class

// for testing only
public static void main(String[] args){
	List dlist = new ArrayList();
	dlist.add(new DepTreeNode("12"));
	dlist.add(new DepTreeNode("1"));
	dlist.add(new DepTreeNode("8"));
	dlist.add(new DepTreeNode("22"));
	dlist.add(new DepTreeNode("21"));
	dlist.add(new DepTreeNode("112"));
	dlist.add(new DepTreeNode("2"));
	sortList(dlist);
	for (int i=0; i < dlist.size(); i++)
		System.out.println( ((DepTreeNode)dlist.get(i)).order()+"");
}

} // end class DepTreeNode