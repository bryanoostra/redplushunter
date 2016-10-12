/*
 * Created on Mar 26, 2008
 *
 */
package natlang.rdg.util;

import natlang.rdg.model.RSEdge;
import natlang.rdg.model.RSGraph;
import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;

public class GraphHandler
{

	/**
	 * Meant to generate a new rhetorical relation
	 * @param rel
	 * @param vert1
	 * @param vert2
	 * @return
	 */
	public static RhetRelation createRhetRel(String rel, RSVertex vert1, RSVertex vert2)
	{
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge sat = new RSEdge("satellite");
		sat.setTarget(rhetrel);
		sat.setSource(vert1);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert2);
					
		return rhetrel;
	}

	/**
	 * Meant to add a rhetorical relation to a graph
	 * @param graph
	 * @param newrelation
	 * @return
	 */
	public static RSGraph addRhetRel(RSGraph graph, RhetRelation newrelation)
	{
		RSEdge sat = (RSEdge) newrelation.getSatellite();
		RSEdge nuc = (RSEdge) newrelation.getNucleus();
		
		graph.addMEdge(sat);
		graph.addMEdge(nuc);

		return graph;
	}
	
}
