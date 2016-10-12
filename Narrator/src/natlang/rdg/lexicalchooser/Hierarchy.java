package natlang.rdg.lexicalchooser;

import java.util.*;

/**
 * Modelling a subsumption hierarchy containg part-of relations (castle/gate),
 * belongto relations (bedroom/princess) and relation relations (father/daughter)
 * 
 * @author Nanda Slabbers
 */
public class Hierarchy 
{
	private Vector<PartOfElement> partof;
	private Vector<BelongToElement> belongto;
	private Vector<RelationElement> relations;
	private Vector<ContainsElement> contains;
	/**
	 * Creates the hierarchy, initializing the different vectors with relations
	 *
	 */
	public Hierarchy()
	{
		partof = new Vector<PartOfElement>();
		belongto = new Vector<BelongToElement>();		
		relations = new Vector<RelationElement>();
		contains = new Vector<ContainsElement>();
		
//		partof.addElement(new PartOfElement("gate01", "castle01"));
//		partof.addElement(new PartOfElement("hand01", "princess01"));
//		partof.addElement(new PartOfElement("edge01", "forest02"));
//		belongto.addElement(new BelongToElement("bedroom01", "princess01"));
//		belongto.addElement(new BelongToElement("horse01", "knight01"));
//		belongto.addElement(new BelongToElement("behaviour01", "princess01"));
//		belongto.addElement(new BelongToElement("knight01", "country01"));
//		relations.addElement(new RelationElement("father", "king01", "princess01"));
//		relations.addElement(new RelationElement("grandmother", "woman01", "princess01"));
//		
//		belongto.addElement(new BelongToElement("oIceCreamTruck", "otto"));
//		belongto.addElement(new BelongToElement("annesBelt", "anne_bonney"));
//		belongto.addElement(new BelongToElement("annesRapier", "anne_bonney"));
	}
	
	/**
	 * Creates a relationship where ent1 belongs to ent2.
	 * @param ent1
	 * @param ent2
	 */
	public void addBelongToRelation(String ent1, String ent2){
		belongto.addElement(new BelongToElement(ent1, ent2));
	}
	
	/**
	 * Creates a relationship where ent1 is part of ent2.
	 * @param ent1
	 * @param ent2
	 */
	public void addPartOfRelation(String ent1, String ent2){
		partof.addElement(new PartOfElement(ent1, ent2));
//		partof.addElement(new PartOfElement(ent1, ent2));
	}
	
	public void addContainedByRelation(String ent1, String ent2){
		contains.addElement(new ContainsElement(ent1, ent2));
//		partof.addElement(new PartOfElement(ent1, ent2));
	}
	
	/**
	 * Returns a belong to element
	 * @param ent
	 */
	public BelongToElement getBelongToElement(String ent)
	{
		for (int i=0; i<belongto.size(); i++)
		{
			BelongToElement bte = (BelongToElement) belongto.elementAt(i);
			if (bte.getEnt1().equals(ent))
				return bte;
		}
		return null;
	}
	
	/**
	 * Returns a part of element
	 * @param ent
	 */
	public PartOfElement getPartOfElement(String ent)
	{
		for (int i=0; i<partof.size(); i++)
		{
			PartOfElement poe = (PartOfElement) partof.elementAt(i);
			if (poe.getEnt1().equals(ent))
				return poe;
		}
		return null;
	}
	
	/**
	 * Returns a relation element
	 * @param ent
	 */
	public RelationElement getRelationElement(String ent)
	{
		for (int i=0; i<relations.size(); i++)
		{
			RelationElement re = (RelationElement) relations.elementAt(i);
			if (re.getEnt1().equals(ent))
				return re;
		}
		return null;
	}
	
	/**
	 * Returns a contains element
	 * @param ent
	 */
	public ContainsElement getContainsElement(String ent)
	{
		for (int i=0; i<contains.size(); i++)
		{
			ContainsElement ce = contains.elementAt(i);
			if (ce.getEnt1().equals(ent))
				return ce;
		}
		return null;
	}
	
	public Vector<PartOfElement> getPartOfElementVector(){
		return partof;
	}
	
	public Vector<BelongToElement> getBelongToElementVector(){
		return belongto;
	}
}
