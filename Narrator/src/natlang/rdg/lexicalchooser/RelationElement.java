package natlang.rdg.lexicalchooser;

/**
 * Entity that is related to another entity by a certain relation (such as father/daughter)
 * 
 * @author Nanda Slabbers
 *
 */
public class RelationElement 
{
	private String conc;
	private String ent1;
	private String ent2;
	
	/**
	 * Creates a relation element
	 * @param c
	 * @param e1
	 * @param e2
	 */
	public RelationElement(String c, String e1, String e2)
	{
		conc = c;
		ent1 = e1;
		ent2 = e2;
	}
	
	/**
	 * Returns the relation that holds between the two entities
	 */
	public String getConc()
	{
		return conc;
	}
	
	/**
	 * Returns the first entity
	 */
	public String getEnt1()
	{
		return ent1;
	}
	
	/**
	 * Returns the second entity
	 */
	public String getEnt2()
	{
		return ent2;
	}
}
