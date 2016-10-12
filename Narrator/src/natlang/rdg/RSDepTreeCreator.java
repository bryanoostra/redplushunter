package natlang.rdg;

import natlang.rdg.lexicalchooser.*;
import natlang.rdg.libraries.*;
import natlang.rdg.model.*;

import java.util.Vector;


/**
 * Class for generating entire RSDepTreeNodes
 * @author Nanda Slabbers
 */
public class RSDepTreeCreator 
{
	private LexicalChooser lc;
	
	/**
	 * Constructor, initializes the Lexical Chooser 
	 */
	public RSDepTreeCreator(LexicalChooser l)
	{
		lc = l;
	}
		
	/**
	 * Creates cat-node with the correct label 
	 */
	public RSDepTreeNode createCatNode(String lb, String cat)
	{
		RSTreeNodeData d = new RSTreeNodeData(lb);
		d.set("cat", cat);
		
		return new RSDepTreeNode(d);
	}
	
	/**
	 * Creates nom-node 
	 */
	public RSDepTreeNode createNameNode(String rel, String name)
	{
		RSTreeNodeData d = new RSTreeNodeData(rel);
		d.set("pos", "nom");
		d.set("root", name);
		
		return new RSDepTreeNode(d);
	}
	
	/**
	 * Creates noun-node 
	 */
	public RSDepTreeNode createNounNode(String entity, String noun)
	{
		RSTreeNodeData data = new RSTreeNodeData("hd");
		data.set("pos", "noun");
		data.set("root", noun);
		data.set("morph", "sing");
		data.set("index", entity);
			
		return new RSDepTreeNode(data);
	}
	
	/**
	 * Creates a node for the verb particle 
	 */
	public RSDepTreeNode createSvpNode(String svp)
	{
		RSTreeNodeData data = new RSTreeNodeData("svp");
		data.set("pos", "part");
		data.set("root", svp);
				
		return new RSDepTreeNode(data);
	}
	
	/**
	 * Creates a node for the anticipatory subject 'er' 
	 */
	public RSDepTreeNode createErNode()
	{
		RSTreeNodeData data = new RSTreeNodeData("sup");
		data.set("pos", "adv");
		data.set("root", "er");
		
		return new RSDepTreeNode(data);
	}
	
	/**
	 * Creates a node for the word 'dat' in a complex sentence 
	 */
	public RSDepTreeNode createCmpNode()
	{
		RSTreeNodeData data = new RSTreeNodeData("cmp");
		data.set("pos", "adv");
		data.set("root", "dat");
		
		return new RSDepTreeNode(data);
	}
	
	/**
	 * Creates a prep node 
	 */
	public RSDepTreeNode createPrepNode(String prep)
	{
		RSTreeNodeData data = new RSTreeNodeData("hd");
		data.set("pos", "prep");
		data.set("root", prep);
		
		return new RSDepTreeNode(data);
	}
	
	/**
	 * Creates a mod node 
	 */
	public RSDepTreeNode createModNode(String mod)
	{
		Entry ent = lc.getEntry(mod, true);
		
		if (ent != null)
		{
			RSTreeNodeData data = new RSTreeNodeData("mod");
			data.set("pos", ent.getPos());
			data.set("root", ent.getRoot());
			
			return new RSDepTreeNode(data);
		}
		return createNullNode("mod");
	}
	
	/**
	 * Creates a verb node with the correct label and morphological information 
	 */
	public RSDepTreeNode createVerbNode(String verb, String lb, String morph)
	{		
		Entry ent = lc.getEntry(verb, true);
			
		if (ent != null)
		{
			RSTreeNodeData data = new RSTreeNodeData(lb);
			data.set("pos", ent.getPos());
			data.set("root", ent.getRoot());
			data.set("morph", morph);
			
			return new RSDepTreeNode(data);			
		}
		
		return createNullNode(lb);
	}
		
	/**
	 * Creates a node for a determiner 
	 */
	public RSDepTreeNode createDetNode(String det)
	{
		RSTreeNodeData data = new RSTreeNodeData("det");
		data.set("pos", "det");
		data.set("root", det);
		data.set("morph", "sing");
		
		return new RSDepTreeNode(data);
	}
			
	/**
	 * Creates a node for the coordinator 'en'
	 */
	public RSDepTreeNode createCrdNode()
	{
		RSTreeNodeData data = new RSTreeNodeData("crd");
		data.set("pos", "vg");
		data.set("root", "en");
		
		return new RSDepTreeNode(data);
	}
	
	/**
	 * The same as the coordinator 'en', but in case the enumeration contains more
	 * than two items a comma is used 
	 */
	public RSDepTreeNode createCommaNode()
	{
		RSTreeNodeData data = new RSTreeNodeData("crd");
		data.set("pos", "cm");
		data.set("root", ",");
		
		return new RSDepTreeNode(data);
	}
	
	/**
	 * Creates an entity node 
	 */
	public RSDepTreeNode createEntNode(String entity, String label, Vector<String> adjs)
	{	
		RSTreeNodeData data = new RSTreeNodeData(label);
		data.set("pos", "ent");
		data.set("root", entity);
		data.set("index", entity);
		
		RSDepTreeNode entnode = new RSDepTreeNode(data);
		
		for (int i=0; i<adjs.size(); i++)
		{
			String s = (String) adjs.elementAt(i);				
			entnode.addAdj(s);
		}		
		return entnode; 
	}
	
	/**
	 * Creates a node for an adjective with the correct morph tag. Furthermore if the 
	 * String is a verb, the correct tense of the verb is selected first. An example
	 * is the word 'tegenstribbelen', this is first changed into 'tegenstribbelend' and
	 * finally this can be inflected into 'tegenstribbelende' in the Morphology class.
	 */
	public RSDepTreeNode createAdjNode(String adj, String pos, String rel, boolean morph)
	{	
		Entry adjEnt = lc.getEntry(adj, true);
				
		if (adjEnt != null)
		{			
			RSTreeNodeData data = new RSTreeNodeData(rel);			
			data.set("pos", pos);
			
			String tmp = "";
			if (adjEnt.getPos().equals("verb"))
			{
				try
				{			
					VerbLib vl = new VerbLib();			
					tmp = vl.getInflectedForm(adjEnt.getRoot(), "prog");
				}
				catch(Exception e)
				{
					System.out.println("error");
				}
			}
			if (!tmp.equals(""))
				data.set("root", tmp);
			else
				data.set("root", adjEnt.getRoot());
			
			if (morph)
				data.set("morph", "sing");			
			
			return new RSDepTreeNode(data);
		}
		
		return createNullNode(rel);
	}
	
	/**
	 * Creates a node for a pronoun
	 */
	public RSDepTreeNode createPronNode(String rel, String pron)
	{		
		RSTreeNodeData pronData = new RSTreeNodeData(rel);
		pronData.set("pos", "pron");
		pronData.set("root", pron);
		
		return new RSDepTreeNode(pronData);
	}
	
	/**
	 * Creates a node in which all strings are initialized to ""
	 * Can be used if a word is not stored in the lexicon in which case the word
	 * is simply removed and the program is executed normally (instead of a nullpointerexception) 
	 */
	public RSDepTreeNode createNullNode(String rel)
	{
		RSTreeNodeData d = new RSTreeNodeData(rel);	
		d.set("pos", "");
		d.set("cat", "");
		d.set("root", "");
		d.set("morph", "");
		d.set("index", "");
		return new RSDepTreeNode(d);
	}
}
