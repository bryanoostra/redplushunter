package natlang.rdg.model;

import parlevink.parlegraph.model.*;

import org.jdom.*;

/** RSEdge models the edges in a Rhetorical Dependency Structure. It connects a 
 *	nucleus or a satellite to its RhetRelation. Nuclei and satellites are always
 *	RSVertices.
 *	@author Feikje Hielkema
 *	@version 1.0
 */

public class RSEdge extends MLabeledEdge implements MEdge
{
	/**	Constructs a labeled edge
	 *	@param element
	 */
	public RSEdge(Element element)
	{
		super(element.getName());	//nucleus or satellite
	}

	/**	Constructs a labeled edge
	 *	@param lb
	 */
	public RSEdge(String lb)
	{
		super(lb);
	}

	/** Sets the source of the RSEdge, while ascertaining that an edge cannot be 
	 *	connected to two Deptrees. Overloads MEdgeAdapter.setSource()
	 *	@param source (see parlevink package)
	 *	@throws IllegalArgumentException when both source and target would become 
	 *	RSDepTreeModels
	 */
	public void setSource(MVertex source) throws IllegalArgumentException
	{
		MVertex vertex = getTarget();		
		if ((vertex != null) && (source.getClass().toString().equals("RSDepTreeModel")))
			if (vertex.getClass().toString().equals("RSDepTreeModel"))	
				throw new IllegalArgumentException("Error setting source: illegal to connect two deptrees");
		super.setSource(source);	
	}

	/** Sets the target of the RSEdge, while ascertaining that an edge cannot be 
	 *	connected to two Deptrees. Overloads MEdgeAdapter.setTarget()
	 *	@param target (see parlevink package)
	 *	@throws IllegalArgumentException when both source and target would become 
	 *	RSDepTreeModels
	 */
	public void setTarget(MVertex target) throws IllegalArgumentException
	{
		MVertex vertex = getSource();
		if ((vertex != null) && (target.getClass().toString().equals("RSDepTreeModel")))
			if (vertex.getClass().toString().equals("RSDepTreeModel"))
				throw new IllegalArgumentException("Error setting target: illegal to connect two deptrees");
		super.setTarget(target);	
	}
}