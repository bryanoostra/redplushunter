package natlang.structure;
import java.util.*;


/**
 * A DependencyStructure is an ordered tree with labeled edges and vertices
 *
 * Edge label names are Strings that stand for the role/function of the subtree 
 * that is the target of the edge in the whole structure. 
 * (For example: in the CGN specification for
 * Dependency analysis the edge label name SU is used for the subject structure, etc.)
 * Edge labels are supposed to be unique !!
 * A DependencyStructure is called complete iff for all edge labels there is an edge
 * that ends in a DependencyStructure (i.e. is not null).
 * 
 * A leaf is a Dependency Structure without outgoing edges.
 * A DependencyStructure that is part of a larger DependencyStructure has a context
 * the structure above it. 
 * If it has a context it has a role in this context (like the subject role)
 *
 * Hence: if ds is a DependencyStructure than the following equality holds:
 *		ds.getContext().get(ds.getRole()) == ds
 *
 * We care that this equality holds for 'motherless' structures as well.
 * Therefore we define constant TOP to identify the role of such a top most structure.
 * Moreover: for motherless ds we define: ds.getContext() equals ds.
 * 
 * (the paradox says that motherless structures are their own mother) 
 *
 * Thus:  
 * if ds.getRole() == TOP then ds.getContext() equals ds and so the equality holds.
 *
 * Graphically: ds.getRole() is the label of the incoming edge of ds.
 * A top most ds has an incoming edge labeled TOP.
 *  
 * Node/Vertex labels are sets of (feature, value) pairs
 * A particular important feature is 'lexicalValue' the value of which is
 * the list of strings that are the values of the leafs covered by the node (in the order of
 * the nodes).
 * 
 * This interface only provides query methods. 
 * Any implementation should take care of not changing the structure queried.
 * In a more eloquent wording: a DependencyStructure is an immutable object
 */

public interface DependencyStructure {

/**
 * constant TOP is returned as the role value of a structure that has no context/mother
 * TOP is special in that:
 * ds.get(TOP) is ALWAYS defined and returns the top most context of structure ds
 * For we have ds.get(TOP) equals ds
 * Notice: although get(label) is the method to get the target structure of the outgoing edge with label,
 * TOP is not a value of an edge label !
 */
static final String TOP = "top";
static final String TAG = "tag";
/**
 * constant LEX is the name of a FEATURE any structure has
 * the value of this feature is a String in particular: the sequence of words that is
 * covered by the structure; covered in the following sense:
 * If ds = [ (l_0,ds_0),(l_1,ds_1),...,(l_n-1,ds_n-1) ]
 * then LEX(ds) = concat [ LEX(ds_0),LEX(ds_1),...,LEX(ds_n-1) ]
 * where concat makes one list by concatenating the lists in the list 
 * seperating them by one space. Example: concat[["de","man"],["loopt"]] == ["de","man","loopt"]
 */ 
static final String LEX = "lex";

/**
 * constant POS is the name of a FEATURE most structures have
 * it stands for Parts Of Speech and values are 
 * - for words : the word category or lexical tag (like Adj,Adv,N,V,Pron,Det, etc)
 * - for phrases: the syntactic type: NP, PP, ADVP, etc.
 * POS occur for example in verb subcategorization frames to indicate the syntactic 
 * category of complements and modifiers of the verb.
 */
static final String POS="pos";

/**
 * constant HEAD is the name of the LABEL of the edge that contains the main substructure
 * (the linguistic head of the phrase covered by the structure)
 * The head structure determines what other parts the mother of the structure has.
 * The head of a sentence is the verb phrase the head of the verbphrase is the verbal part
 * The head of a noun phrase is the main noun
 * Not every structure has a HEAD labeled edge
 */
static final String HEAD = "hd";

/**
 * @return an iterator over the labels of the outgoing edges of the root of this structure
 * @return null if this is a leaf (has no outgoing edges)
 */ 
Iterator labels();

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
List getLexicalValue();

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
String getFeatureValue(String featName);

/**
 * @param path a list of edge label Strings
 * @return the DependencyStructure at the end of the path following the edge labels in the given list
 * @return null if there is no such path or the structure is missing (latter only occurs in case of incomplete structure)
 */
DependencyStructure get(List path);

/**
 * get(edgeLabel) returns the daugther that is the target of the -unique edge labeled with edgeLabel
 * or: returns the direct sub structure that has the role denoted by edgeLabel
 * For example ds.get(SUBJECT) is the structure that has the SUBJECT role in this structure 
 * Special feature: get(TOP) returns the top most context (equals this if this is the topmost context)
 * @param edgeLabel an edge label String
 * @return the DependencyStructure at the end of edge with the given label
 * @return null if there is no such label or such a structure is missing (latter only occurs in case of incomplete structure)
 */
DependencyStructure get(String edgeLabel);

/**
 * @return true iff this DependencyStructure is complete
 */
boolean isComplete();

/**
 * @return true iff this is a leaf (has no outgoing edges)
 */
boolean isLeaf();

/**
 * @return true iff this.get(TOP) equals this
 */
boolean isTop();

/**
 * @return the immediate context of this DependencyStructure (in fact the mother of this seen as a daughter)
 * @return this structure it self if this has no context (it is the root of all roots)
 */
DependencyStructure getContext();

/**
 * @return a String representing the edge label - i.e. role/function - of the edge coonecting this to it's context
 * @return constant TOP iff this has no mother
 */
String getRole();

/**
 * getTopContext() should be equal to get(TOP)
 * @return the top most context of this structure
 * @return this object itself if this is the top most structure
 */
DependencyStructure getTopContext();

/**
 * @return node id of the root of this structure
 */
String nodeId();
}

