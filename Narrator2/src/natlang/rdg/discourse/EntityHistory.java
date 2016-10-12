package natlang.rdg.discourse;

import java.util.*;

/**
 * A history containing the entities and salience values, used by the REG
 * @author Nanda Slabbers
 *
 */
public class EntityHistory 
{
	private Vector<Entity> ents;
	
	/**
	 * Constructor
	 *
	 */
	public EntityHistory()
	{
		ents = new Vector<Entity>();
	}
	
	/**
	 * Adds entity to the vector
	 * @param e
	 */
	public void addEntity(Entity e)
	{
		String newent = e.getEnt();
		for (int i=0; i<ents.size(); i++)
		{
			Entity ent = (Entity) ents.elementAt(i);
			if (ent.getEnt().equals(newent))
			{
				ent.setFirst(false);
				return;
			}
		}
		ents.addElement(e);
	}
	
	/**
	 * Also adds entity, but if the entity already exists, the noun is added to that
	 * entity
	 * @param e the entity
	 * @param c the concept
	 * @param n the noun 
	 * @param g the gender
	 */
	public void addEntity(String e, String c, String n, String g)
	{		
		for (int i=0; i<ents.size(); i++)
		{
			Entity ent = (Entity) ents.elementAt(i);
			if (ent.getEnt().equals(e))
			{
				ent.setFirst(false);
				ent.addNoun(n);
				return;
			}
		}		
		Entity tmp = new Entity(e, c, g);
		tmp.setFirst(false);
		tmp.addNoun(n);
		ents.addElement(tmp);
	}
	
	/**
	 * Returns all nouns already used to lexicalize the entity
	 * @param entity
	 */
	public Vector<String> getNouns(String entity)
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().equals(entity))
				return tmp.getNouns();
		}
		return null;
	}
	
	/**
	 * Returns the entity
	 * @param ent
	 */
	public Entity getEntity(String ent)
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().equals(ent))
				return tmp;
		}
		return null;
	}
	
	/**
	 * Returns all entities mentioned sofar
	 */
	public Vector<Entity> getEnts()
	{
		return ents;
	}
	
	/**
	 * Updates the salience for an entity
	 * @param entity
	 * @param salience
	 */
	public void updateSalience(String entity, int salience)
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().equals(entity))
			{
				tmp.addSalience(salience);
				return;
			}
		}
	}
	
	/**
	 * Updates the salience and pos for an entity
	 * @param entity
	 * @param salience
	 * @param pos
	 */
	public void updateSaliencePos(String entity, int salience, String pos)
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().equals(entity))
			{
				tmp.addSalience(salience);
				tmp.setPos(pos);
				return;
			}
		}
	}
	
	/**
	 * Updates the pos for an entity
	 * @param entity
	 * @param pos
	 */
	public void updatePos(String entity, String pos)
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().equals(entity))
			{
				tmp.setPos(pos);
				return;
			}
		}
	}
	
	/**
	 * Updates the head for an entity
	 * @param entity
	 */
	public void updateHead(String entity)
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().equals(entity))
			{
				tmp.setHead(false);
				return;
			}
		}
	}
	
	/**
	 * Initializes new sentence: increment nrsents
	 */	
	public void newSentence()
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			tmp.incSents();
		}
	}
	
	/**
	 * Initializes new clause: decrease saliences and reset head
	 *
	 */
	public void newClause()
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			tmp.decreaseSalience();	
			tmp.setHead(true);
		}		
		System.out.println("\nna waardes halveren doordat nieuwe clause wordt begonnen:");
		printSalienceTable();
	}
	
	/**
	 * can be used to print a table containing all entities and their salience values
	 *
	 */
	public void printSalienceTable()
	{
		System.out.println("\n******* Saliences *******");
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			if (tmp.getEnt().length() < 8)
				System.out.println(tmp.getEnt() + "\t\t" + tmp.getSalience());
			else
				System.out.println(tmp.getEnt() + "\t" + tmp.getSalience());
		}
		System.out.println("*************************\n");
	}
	
	public void newParagraph()
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			tmp.setFirst(true);
		}
	}
	
	/**
	 * Checks whether this is the first mention of the entity
	 * @param ent
	 */
	public boolean firstMention(String ent)
	{
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			return tmp.getFirst();				
		}
		return false;
	}
	
	/**
	 * Returns the names of all entities 
	 */
	public Vector<String> getAllEntities()
	{
		Vector<String> result = new Vector<String>();
		
		for (int i=0; i<ents.size(); i++)
		{
			Entity tmp = (Entity) ents.elementAt(i);
			result.add(tmp.getEnt());				
		}
		return result;
	}
}
