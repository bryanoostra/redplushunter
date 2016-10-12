package natlang.rdg.surfacerealizer;

import java.util.Vector;

import narrator.lexicon.CharacterInfo;
import natlang.rdg.discourse.EntityHistory;
import natlang.rdg.lexicalchooser.Hierarchy;

/**
 * This class takes care of pre-process reasoning about the fabula
 * to create information:
 *  - are certain objects the only objects in the world? In this case, the narrator
 *    should refer to them as 'the boat' instead of 'a boat'
 *  - what are relations between objects and characters?
 *  - ...
 *  @author Marissa Hoek
 */
public class FabulaChecker {
	private EntityHistory entityHist;
	private CharacterInfo charInfo;
	private Hierarchy hierarchy;
	
	public FabulaChecker(EntityHistory entityHist, CharacterInfo charInfo, Hierarchy hierarchy){
		this.entityHist = entityHist;
		this.charInfo = charInfo;
		this.hierarchy = hierarchy;
	}
	
	public void start(){
		
	}
	
	//I don't know what this is supposed to do.
	public boolean exactlyOne(String con1, String con2){
		return false;
		//return true;
	}
	
	/**
	 * Returns whether this concept is the only concept with
	 * its role. If another concept with the same role, i.e.
	 * police man, exists, this returns false.
	 * 
	 * @param The concept that should be checked.
	 * @return Whether this concept is unique.
	 */
	//TODO actually make this work
	public boolean unique(String con){
		return false;
		//return true;
	}
	
	/**
	 * This returns more general concepts that apply to this
	 * concept. For example, for a car this is a vehicle, for
	 * a loitering youth this is a person.
	 * 
	 * Currently not implemented as the data files don't 
	 * support such a hierarchy yet.
	 * 
	 * @param Concept that has to be generalized
	 * @return Empty Vector for now
	 */
	public Vector<String> getMoreGeneralConcepts(String con){
		return new Vector<String>();		//return an empty vector for now
	}
}
