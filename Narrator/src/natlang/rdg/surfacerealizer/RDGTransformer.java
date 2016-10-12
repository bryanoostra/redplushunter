package natlang.rdg.surfacerealizer;

import java.util.*;

/** RDGTransformer is an interface for classes that transform the Rhetorical Dependency Graph. These classes are 
  * RelationTransformer, Conjunctor, ReferentialExpressionGenerator, SurfaceRealizer and Elliptor
  * @author Feikje Hielkema
  * @version 1.0, March 3d 2005
  */
  
public interface RDGTransformer
{
	/** Transforms the object that the transformer is holding */
	public boolean transform() throws Exception;
	
	/** Checks whether the transformation can take place */
	public boolean check();
	
	/** Returns the results of the transformation */
	public Iterator getResult();
}