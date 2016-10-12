package natlang.rdg.model;

import parlevink.parlegraph.model.*;

/** This class models the edges in an ordered Dependency Tree. Basically, they are
 *	MLabeledEdges with an extra label, an integer giving their position in the 
 *	surface form
 *	@author Feikje Hielkema
 *	@version 1.0
 */
 
 public class MOrderedEdge extends MLabeledEdge
 {
 	private int position;		//position of this node in surface form of parent node
 	
 	/** Creates an MOrderedEdge by copying the label, source and target of an old
 	 *	Edge. The position tag is left unspecified for the moment
 	 */
 	public MOrderedEdge(MLabeledEdge oldEdge)
 	{
 		super(oldEdge.getLabel());
 		setSource(oldEdge.getSource());
 		setTarget(oldEdge.getTarget());
 	}
 	
 	/** Sets the position */
 	public void setPosition(int i)
 	{
 		position = i;
 	}
 	
 	/** Returns the position */
 	public int getPosition()
 	{
 		return position;
 	}
 	
 	/*public String toString()
    {
   	 return "morderededge:\t" + getSource() + "\t" + getTarget(); 
    }*/
 }