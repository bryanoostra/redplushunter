package natlang.rdg.lexicalchooser;

import java.util.*;

/**
 * Model of one character, containing the entity, concept, gender, name and properties
 * @author Nanda Slabbers
 */
public class CharacterModel
{
	private String entity;
	private String concept;
	private String gender;
	private String name;
	private Vector statprops;
	private Vector dynprops;
	
	/**
	 * Creates an empty model for one character
	 *
	 */
	public CharacterModel()
	{
		entity = "";
		concept = "";
		gender = "";
		name = "";
		statprops = new Vector();
		dynprops = new Vector();
	}
	
	/**
	 * Creates a model for one character
	 *
	 */
	public CharacterModel(String ent, String con, String gen)
	{
		entity = ent;
		concept = con;
		gender = gen;
		name = "";
		statprops = new Vector();
		dynprops = new Vector();
	}
	
	/**
	 * Creates a model for one character including the name
	 *
	 */
	public CharacterModel(String ent, String con, String gen, String nm)
	{
		entity = ent;
		concept = con;
		gender = gen;
		name = nm;
		statprops = new Vector();
		dynprops = new Vector();
	}
	
	/**
	 * Sets the name
	 * @param nm
	 */
	public void setName(String nm)
	{
		name = nm;
	}
	
	/**
	 * Adds a static property
	 * @param p
	 */
	public void addStatprop(Property p)
	{
		statprops.addElement(p);
	}
	
	/**
	 * Adds a dynamic property
	 * @param p
	 */
	public void addDynprop(Property p)
	{
		dynprops.addElement(p);
	}
	
	/**
	 * Returns the entity
	 */
	public String getEntity()
	{
		return entity;
	}
	
	/**
	 * Returns the concept
	 */
	public String getConcept()
	{
		return concept;
	}
	
	/**
	 * Returns the gender
	 */
	public String getGender()
	{
		return gender;
	}
	
	/**
	 * Returns the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the static properties
	 */
	public Vector getStatProps()
	{
		return statprops;
	}
	
	/**
	 * Returns the dynamic properties
	 */
	public Vector getDynProps()
	{
		return dynprops;
	}
	
	/*public String toString()
	{
		return entity + ": " + name + "  " + concept;
	}*/
}
