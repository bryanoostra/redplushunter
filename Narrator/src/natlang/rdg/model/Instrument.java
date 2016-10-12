package natlang.rdg.model;

import natlang.rdg.documentplanner.InitialDocPlanBuilder;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class Instrument {

	// via
	// met
	// door
	
	public static final int TRANSIT_WAY = 0;
	public static final int PASSAGE_WAY = 1;
	public static final int OBJECT 		= 2;
	
	public static final String SWC = "http://www.owl-ontologies.com/StoryWorldCore.owl#";
	public static final String SWC_DOOR = SWC + "Door";
	public static final String SWC_TRANSIT_WAY = SWC + "TransitWay";
	
	
	private String instr;
	private int type;
	
	public Instrument (String instr, int type){
		this.instr = instr;
		this.type = type;
	}
	
	public Instrument (RDFNode n){
		if(InitialDocPlanBuilder.isSubclassOf(n, SWC_DOOR))
			type = PASSAGE_WAY;
		else if(InitialDocPlanBuilder.isSubclassOf(n, SWC_TRANSIT_WAY))
			type = TRANSIT_WAY;
		else
			type = OBJECT;
		
		instr = ((Resource)n.as(Resource.class)).getLocalName();
	}

	/**
	 * @return the instr
	 */
	public String getInstr() {
		return instr;
	}

	/**
	 * @param instr the instr to set
	 */
	public void setInstr(String instr) {
		this.instr = instr;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return
	 * @see java.lang.String#toString()
	 */
	public String toString() {
		return instr.toString();
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return instr.equals(arg0);
	}
	
	public String getPrep(){
		if(type == TRANSIT_WAY)
			return "via";
		else if(type == PASSAGE_WAY)
			return "door";
		else
			return "met";
	}
	
}
