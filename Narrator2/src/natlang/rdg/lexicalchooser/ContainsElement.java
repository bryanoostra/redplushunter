package natlang.rdg.lexicalchooser;

/**
 * Ent1 contains Ent2
 * e.g.:
 * 	- treasure contains gold
 *  - crate contains rumbottle
 *  - pond contains water
 * @author René
 *
 */

public class ContainsElement {
	
	private String ent1;
	private String ent2;
	
	public ContainsElement(String ent1, String ent2){
		this.ent1 = ent1;
		this.ent2 = ent2;
		
	}

	/**
	 * @return the ent1
	 */
	public String getEnt1() {
		return ent1;
	}

	/**
	 * @param ent1 the ent1 to set
	 */
	public void setEnt1(String ent1) {
		this.ent1 = ent1;
	}

	/**
	 * @return the ent2
	 */
	public String getEnt2() {
		return ent2;
	}

	/**
	 * @param ent2 the ent2 to set
	 */
	public void setEnt2(String ent2) {
		this.ent2 = ent2;
	}
	
	
	
}
