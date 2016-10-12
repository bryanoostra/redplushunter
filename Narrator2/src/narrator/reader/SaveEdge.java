package narrator.reader;

public class SaveEdge {
	public FabulaEdge edge;
	public FabulaNode node1;
	public FabulaNode node2;
	
	/**
	 * Saves an edge including the to and from nodes.
	 * @param edge
	 * @param node1
	 * @param node2
	 */
	public SaveEdge(FabulaEdge edge, FabulaNode node1, FabulaNode node2){
		this.edge = edge;
		this.node1 = node1;
		this.node2 = node2;
	}
}
