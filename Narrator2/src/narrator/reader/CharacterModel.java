package narrator.reader;

import java.util.*;

/**
 * Model of one character, containing the entity, concept, gender, name and properties
 * @author Nanda Slabbers
 * @author Marissa Hoek
 */
public class CharacterModel
{
	private String entity;
	private String concept;
	private String gender;
	private String name;
	private Vector<Property> statprops;
	private Vector<Property> dynprops;
	private String location;
	
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
		statprops = new Vector<Property>();
		dynprops = new Vector<Property>();
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
		statprops = new Vector<Property>();
		dynprops = new Vector<Property>();
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
		statprops = new Vector<Property>();
		dynprops = new Vector<Property>();
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
	 * Sets the entity
	 * @param Entity
	 */
	public void setEntity(String ent){
		this.entity = ent;
	}
	
	/**
	 * Returns the concept
	 */
	public String getConcept()
	{
		return concept;
	}
	
	public void setConcept(String con){
		this.concept = con;
	}
	
	/**
	 * Returns the gender
	 */
	public String getGender()
	{
		return gender;
	}
	
	/**
	 * Sets the gender.
	 * @param gender
	 */
	public void setGender(String gender){
		this.gender = gender;
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
	public Vector<Property> getStatProps()
	{
		return statprops;
	}
	
	/**
	 * Returns the dynamic properties
	 */
	public Vector<Property> getDynProps()
	{
		return dynprops;
	}
	
	public String toString()
	{
		return entity + ": " + name + " ("+gender+"), " + concept;
	}
	
	public void setLocation(String location){
		this.location = location;
	}

	public String getLocation() {
		return location;
	}
}
