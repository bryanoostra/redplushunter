package natlang.rdg.surfacerealizer;

import natlang.rdg.libraries.*;
import natlang.rdg.model.*;

/**	Inflects the root of a given RSDepTreeNode, using NounLib of VerbLib
 *	@author Feikje Hielkema
 *	@version 1.0
 */
public class Morphology implements LibraryConstants
{
	/** Inflects the given node, if necessary, and sets the word-tag. If the 
	 *	node is not a leaf, nothing happens.
	 */
	public String doMorphology(RSDepTreeNode node) throws Exception
	{
		String pos = node.getData().get(RSTreeNodeData.POS);
		if (pos == null)
			return null;
			
		if (pos.equals(VERB))
		{
			VerbLib lib = new VerbLib();
			String word = lib.getInflectedForm(node.getData().get(RSTreeNodeData.ROOT), node.getData().get(RSTreeNodeData.MORPH));
			node.getData().set(RSTreeNodeData.WORD, word);
			return word;
		}
		else if (pos.equals(NOUN))
		{
			NounLib lib = new NounLib();
			String word = lib.getInflectedForm(node.getData().get(RSTreeNodeData.ROOT), node.getData().get(RSTreeNodeData.MORPH));
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
				String word = lib.getInflectedForm(node.getData().get(RSTreeNodeData.ROOT), node.getData().get(RSTreeNodeData.MORPH));
				node.getData().set(RSTreeNodeData.WORD, word);
				return word;
			}
			else
			{
				String root = node.getData().get(RSTreeNodeData.ROOT);
				node.getData().set(RSTreeNodeData.WORD, root);
				return root;
			}
		}
		else		//else the word does not need to be inflected (at least not now -
		{			//later on we might add functionality for vergrotende trap ed
			String root = node.getData().get(RSTreeNodeData.ROOT);
			node.getData().set(RSTreeNodeData.WORD, root);
			return root;
		}
	}
}