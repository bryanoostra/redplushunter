package natlang.rdg.view;


import natlang.rdg.libraries.*;
import natlang.rdg.model.*;
import parlevink.parlegraph.model.*;

import java.util.*;

/** This class adapts a given RSDepTreeModel, so it can be shown with its root-and
 *	morph-tags. During the process, it destroys the given tree!
 */
public class RSDepTreeViewer extends RSDepTreeModel implements LibraryConstants
{
	/** Creates a viewable tree */
	public RSDepTreeViewer(RSDepTreeModel t)
	{
		super(t.getRoot());
		List components = t.decompose();
		for (int i = 0; i < components.size(); i++)
		{
			MComponentAdapter comp = (MComponentAdapter) components.get(i);
			if (comp.getClass().getName().equals("natlang.rdg.model.RSDepTreeNode"))
			{
				RSDepTreeNode node = (RSDepTreeNode) comp;	
				if (node.isLeaf())
					adaptLeaf(node);
			}
			else
				this.addMEdge((MLabeledEdge) comp);
		}
	}
	
	/** Adapts a leaf node so its morph- and root tag can be viewed, by attaching
	 *	another node beneath it 
	 */
	private void adaptLeaf(RSDepTreeNode leaf)
	{
		MLabeledVertex v = new MLabeledVertex(leaf.getData().get(RSTreeNodeData.ROOT));
		MLabeledEdge edge = new MLabeledEdge();
		String morph;
		if ((morph = leaf.getData().get(RSTreeNodeData.MORPH)) != null)
			edge.setLabel(morph);
		edge.setSource(leaf);
		edge.setTarget(v);
		this.addMEdge(edge);
	}
}
