package natlang.rdg.lexicalchooser;

/**
 * Entity that is part of another entity
 * @author Nanda Slabbers
 */
public class PartOfElement 
{
	private String ent1;
	private String ent2;
	
	/**
	 * Creates a part of element
	 * @param e1
	 * @param e2
	 */
	public PartOfElement(String e1, String e2)
	{
		ent1 = e1;
		ent2 = e2;
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
	
	public String toString(){
		return ("[" + ent1 + " part of " + ent2 + "]");
	}
}
