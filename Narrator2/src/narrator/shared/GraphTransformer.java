package narrator.shared;

import natlang.rdg.model.RSGraph;

public interface GraphTransformer {
	public RSGraph getGraph();
	
	public void setGraph(RSGraph graph);
	
	public RSGraph transform() throws NarratorException;
}
