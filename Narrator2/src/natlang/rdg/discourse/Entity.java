package natlang.rdg.discourse;

import java.util.*;

/**
 * An entity object contains all information necessary for the Referring Expression Generator:
 * the unique name, concept, gender, which nouns have been used to lexicalize the entity,
 * whether it is the first reference to the entity in the current paragraph, its salience value, the number of 
 * pronouns used in a row, the number of sentences ago that the entity was mentioned, the 
 * part of speech, and whether it has already been the head of a phrase in the current sentence
 * @author Nanda
 *
 */
public class Entity 
{
	private String ent;
	private String concept;
	private String gender;
	private Vector<String> nouns;
	private boolean first;
	private int salience;
	private int nrprons;
	private int nrsents;
	private String pos;
	private boolean head;
	
	/**
	 * Creates an empty entity element
	 */
	public Entity()
	{
		ent = "";
		concept = "";
		gender = "";
		nouns = new Vector<String>();
		first = true;
		salience = 0;
		nrprons = 0;
		nrsents = 0;
		pos = "";
		head = true;
	}
	
	/**
	 * Creates an entity
	 */	
	public Entity(String e, String c, String g)
	{
		ent = e;
		concept = c;
		gender = g;
		nouns = new Vector<String>();
		first = true;
		salience = 0;
		pos = "";
		head = true;
	}
	
	/**
	 * Sets the entity to e
	 */	
	public void setEnt(String e)
	{
		ent = e;
	}
	
	/**
	 * Sets the boolean first to f
	 */	
	public void setFirst(boolean f)
	{
		first = f;
	}
	
	/**
	 * Sets the part of speech
	 */	
	public void setPos(String p)
	{
		pos = p;
	}
	
	/**
	 * Sets the variable head
	 */	
	public void setHead(boolean h)
	{
		head = h;
	}
	
	/**
	 * Returns the salience value of the entity
	 * @return the salience value
	 */	
	public int getSalience()
	{
		return salience;
	}
	
	/**
	 * Returns the number of pronouns used in a row
	 * @return the number of pronouns used in a row
	 */	
	public int getNrprons()
	{
		return nrprons;
	}
	
	/**
	 * Returns the number of sentences between the current sentence and the last time the entity was referred to by a noun phrase 
	 * @return the number of sentences
	 */	
	public int getNrsents()
	{
		return nrsents;
	}
	
	/**
	 * Returns the concept which the entity is an instance of
	 * @return the concept
	 */	
	public String getConcept()
	{
		return concept;
	}
	
	/**
	 * Returns the gender of the entity
	 * @return the gender
	 */	
	public String getGender()
	{
		return gender;
	}
	
	/**
	 * Returns whether this reference is the first reference in the current paragraph
	 */	
	public boolean getFirst()
	{
		return first;
	}
	
	/**
	 * Returns the part of speech of the last reference to this entity
	 * @return the part of speech
	 */	
	public String getPos()
	{
		return pos;
	}
	
	/**
	 * Returns whether the entity has already been head in the current sentence
	 */	
	public boolean getHead()
	{
		return head;
	}
	
	/**
	 * Updates the salience value
	 */	
	public void addSalience(int cnt)
	{
		salience += cnt;
	}
	
	/**
	 * Increments variable nrprons
	 *
	 */
	public void incProns()
	{
		nrprons++;
	}
	
	/**
	 * Increments variable nrsents
	 */	
	public void incSents()
	{
		nrsents++;
	}
	
	/**
	 * Set variable nrprons to 0
	 */	
	public void resetProns()
	{
		nrprons = 0;
	}
	
	/**
	 * Set variable nrsents to 0
	 */	
	public void resetSents()
	{
		nrsents = 0;
	}
		
	/**
	 * Adds a noun to the list with used nouns
	 * @param n the noun
	 */
	public void addNoun(String n)
	{				
		nouns.addElement(n);
	}
		
	/**
	 * Returns the unique entity's name 
	 * @return the entity
	 */
	public String getEnt()
	{
		return ent;
	}
	
	/**
	 * Returns the Vector with used nouns
	 * @return the nouns
	 */	
	public Vector<String> getNouns()
	{
		return nouns;
	}	
	
	/**
	 * Returns whether the noun has not been used before to refer to the entity
	 * @param n the noun
	 */
	public boolean isNew(String n)
	{
		for (int i=0; i<nouns.size(); i++)
			if (((String) nouns.elementAt(i)).equals(n))
				return false;
		
		return true;
	}
		
	/**
	 * Divdes the salience value by 2
	 */	
	public void decreaseSalience()
	{
		salience = (int) salience / 2;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#equals(java.lang.Object)
	 */
	public boolean equals(Object e) {
		return ent.equals(((Entity)e).getEnt());
	}	
	
	
}
