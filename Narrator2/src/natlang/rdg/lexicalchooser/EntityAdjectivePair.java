package natlang.rdg.lexicalchooser;

/**
 * Pair of entity and adjective
 * @author Nanda Slabbers
 */
public class EntityAdjectivePair 
{
	private String entity;
	private String adjective;
	
	/**
	 * Creates a pair of an entity and an adjective
	 * @param e
	 * @param a
	 */
	public EntityAdjectivePair(String e, String a)
	{
		entity = e;
		adjective = a;
	}
	
	/**
	 * Returns the entity
	 */
	public String getEntity()
	{
		return entity;
	}
	
	/**
	 * Returns the adjective
	 */
	public String getAdjective()
	{
		return adjective;
	}
}
