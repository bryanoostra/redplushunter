package natlang.rdg.surfacerealizer;

import narrator.lexicon.Entry;
import narrator.lexicon.Lexicon;
import natlang.rdg.libraries.*;
import natlang.rdg.model.*;

/**	Inflects the root of a given RSDepTreeNode, using NounLib of VerbLib
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class Morphology implements LibraryConstants
{
	private Lexicon lexicon;

	public Morphology(Lexicon lexicon){
		this.lexicon = lexicon;
	}
	
	/** Inflects the given node, if necessary, and sets the word-tag. If the 
	 *	node is not a leaf, nothing happens.
	 */
	public String doMorphology(RSDepTreeNode node) throws Exception
	{
		String pos = node.getData().get(RSTreeNodeData.POS);
		String root = node.getData().get(RSTreeNodeData.ROOT);
		//Entry entry = lexicon.getEntryFromWord(root, pos);
		
		//System.out.println("Entrity: "+node.getData().getEntry());
		Entry entry = node.getData().getEntry();
/*		if (entry==null){
			System.out.println(root);
			entry = lexicon.getEntryFromWord(root, pos);
			System.out.println(entry);
		}*/
		
		if (pos == null)
			return null;
			
		if (pos.equals(VERB))
		{
			//VerbLib lib = new VerbLib();
			VerbInflecter lib = new VerbInflecter();
			
			String word = root;
			if (entry!=null)  //if it's not found in the lexicon, just use the root again
				word = lib.getInflectedForm(entry, node.getData().get(RSTreeNodeData.MORPH));
			node.getData().set(RSTreeNodeData.WORD, word);
			return word;
		}
		else if (pos.equals(NOUN))
		{
			//NounLib lib = new NounLib();
			NounInflecter lib = new NounInflecter();
			String word = root; //if it's not found in the lexicon, just use the root again
			if (entry!=null)	
				word = lib.getInflectedForm(entry, node.getData().get(RSTreeNodeData.MORPH));
			
			node.getData().set(RSTreeNodeData.WORD, word);
			return word;
		}
		else if (pos.equals(DET))
		{
			DetLib lib = new DetLib();
			String word = lib.getInflectedForm(node.getData().get(RSTreeNodeData.ROOT), node.getData().get(RSTreeNodeData.MORPH));
			node.getData().set(RSTreeNodeData.WORD, word);
			return word;
		}
		else if (pos.equals(ADJ))
		{
			if (node.getData().get(RSTreeNodeData.MORPH) != null)
			{
				AdjLib lib = new AdjLib();
				//String word = lib.getInflectedForm(node.getData().get(RSTreeNodeData.ROOT), node.getData().get(RSTreeNodeData.MORPH));
				String word = lib.getInflectedForm(node.getData().getEntry(), node.getData().get(RSTreeNodeData.MORPH));
				node.getData().set(RSTreeNodeData.WORD, word);
				return word;
			}
			else
			{
				//String root = node.getData().get(RSTreeNodeData.ROOT);
				node.getData().set(RSTreeNodeData.WORD, root);
				return root;
			}
		}
		else		//else the word does not need to be inflected (at least not now -
		{			//later on we might add functionality for vergrotende trap ed
			//String root = node.getData().get(RSTreeNodeData.ROOT);
			node.getData().set(RSTreeNodeData.WORD, root);
			return root;
		}
	}
}