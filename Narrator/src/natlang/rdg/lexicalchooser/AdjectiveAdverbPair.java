package natlang.rdg.lexicalchooser;

/**
 * Pair of an adjective and adverb
 * @author Nanda Slabbers
 */
public class AdjectiveAdverbPair 
{	
	private String adjective;
	private String adverb;
	
	/**
	 * Creates a pair of an adjective and an adverb
	 * @param adj
	 * @param adv
	 */
	public AdjectiveAdverbPair(String adj, String adv)
	{
		adjective = adj;
		adverb = adv;
	}
	
	/**
	 * Returns the adjective
	 */
	public String getAdjective()
	{
		return adjective;
	}
	
	/**
	 * Returns the adverb
	 */
	public String getAdverb()
	{
		return adverb;
	}
}
