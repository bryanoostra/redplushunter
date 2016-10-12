package natlang.rdg.model;

import parlevink.parlegraph.model.*;

import java.util.*;

/** RSVertex models a structure in a Rhetorical Dependency Structure. This can be
 *	a RhetRelation or a RSDepTreeModel. At the moment only has a function to 
 *	determine whether the object is a RSDepTreeModel or a RhetRelation
 *	@author Feikje Hielkema
 *	@version 1.0
 */

public interface RSVertex extends MVertex
{
	/**	Checks whether the object is a RSDepTreeModel, or not (in which case it is a RhetRelation)
	 *	@return boolean
	 */
	public String getType();
	
	/** Returns the RSVertices of which this RSVertex is the nucleus or satellite,
	 *	if there are any
	 */
	public Iterator<RSVertex> getParent();
}			