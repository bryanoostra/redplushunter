package natlang.rdg.model;

import java.io.*;
import org.jdom.*;

/** RSTreeNodeData holds the data of a treenode in a RS Dependency tree, e.g. 
 *	its category or its pos-tag, its relation to its parent node, its root, 
 *	perhaps its surface form (word), and perhaps its index. It also contains a 
 *	boolean stating whether another such node exists in the dependency graph
 *	@author Feikje Hielkema
 *	@version 1.0
 */

public class RSTreeNodeData
{
	private String cat;		//syntactic category
	private String pos;		//pos-tag
	private String rel;		//relation to parent node
	private String root;		//root form of selected word
	private String morph;		//morphological information
	private String word;		//word (e.g. inflected root)
	private String index;		//identity
	private boolean unique = true;	//if the node appears elsewhere in the RDG, unique is false

	//constants
	public static final String CAT = "cat";	//syntactic category
	public static final String POS = "pos";	//part-of-speech
	public static final String REL = "rel";	//dependency label
	public static final String ROOT = "root";	//root form 
	public static final String MORPH = "morph";	//morphological info
	public static final String WORD = "word";	//inflected word
	public static final String INDEX = "index";	//identity

	/**	Constructs a RSTreeNodeData from xml, initialising all present data
	 *	@param element
	 *	@throws IOException
	 */
	public RSTreeNodeData(Element element) throws IOException	//does it??
	{
		cat = element.getAttributeValue(CAT);	
		pos = element.getAttributeValue(POS);
		rel = element.getAttributeValue(REL);
		root = element.getAttributeValue(ROOT);
		morph = element.getAttributeValue(MORPH);
		word = element.getAttributeValue(WORD);
		index = element.getAttributeValue(INDEX);
		
		if (index != null)
			unique = false;
	}
	
	/**	Constructs a RSTreeNodeData, giving it a relation (all nodes need a relation)
	 *	@param relation
	 */
	public RSTreeNodeData(String relation)
	{
		rel = relation;
	}
	
	/**	Gets the value of the given attribute
	 *	@param attribute stating the desired attribute
	 *	@return String stating the value
	 */
	public String get(String attribute)
	{
		if (attribute.equals(CAT))
			return cat;
		else if (attribute.equals(POS))
			return pos;
		else if (attribute.equals(REL))
			return rel;
		else if (attribute.equals(MORPH))
			return morph;
		else if (attribute.equals(WORD))
			return word;
		else if (attribute .equals(INDEX))
			return index;
		else if (attribute.equals(ROOT))
			return root;
		else
			return null;

	}
	
	/**	Gets the syntactic category of the pos-tag of the node 
	 *	@return String 
	 */
	public String getNodeLabel()
	{
		if (pos == null)
			return cat;
		return pos;
	}
	
	/**	Return boolean stating whether the node appears elsewhere in the RSGraph
	 *	@return boolean 
	 */
	public boolean isUnique()
	{
		return unique;
	}
	
	/**	Sets the specified attribute to the specified value
	 *	@param attribute
	 *	@param value
	 *	@return boolean, whether operation has succeeded
	 */
	public boolean set(String attribute, String value)
	{
		if (attribute.equals(CAT))
			cat = value;
		else if (attribute.equals(POS))
			pos = value;
		else if (attribute.equals(REL))
			rel = value;
		else if (attribute.equals(MORPH))
			morph = value;
		else if (attribute.equals(WORD))
			word = value;
		else if (attribute.equals(INDEX))
			index = value;
		else if (attribute.equals(ROOT))
			root = value;
		else
			return false;
			
		return true;
	}
	
	/**	Checks whether the node is a leaf
	 *	@return boolean 
	 */
	public boolean isLeaf()
	{
		if (pos == null)
			return false;
		return true;
	}
	
	/** Compares this data to another data. If the pos or cat tags are identical,
	 *	and the rel and root tags are identical, return true
	 */
	public boolean equals(RSTreeNodeData newData)
	{
		if (isLeaf() && newData.isLeaf())
		{
			if ((rel.equals(newData.get(REL))) && (pos.equals(newData.get(POS))) && //root.equals(newData.get(ROOT)))
					((index == null && root.equals(newData.get(ROOT))) ||
					 (index != null && index.equals(newData.get(INDEX)))))
				return true;
		}
		else if ((!isLeaf()) && (!newData.isLeaf()))
		{
			if (cat.equals(newData.get(CAT)) && rel.equals(newData.get(REL)))
				return true;
		}
		
		return false;
	}
	
	/*public String toString()
	{
		if (pos != null)
			return (rel + "\t" + pos + "\t" + root);
		else
			return (rel + "\t" + cat);
	}*/
}


