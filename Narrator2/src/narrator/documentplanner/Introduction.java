package narrator.documentplanner;

import narrator.lexicon.CharacterInfo;
import narrator.reader.CharacterModel;
import natlang.rdg.libraries.LibraryConstants;
import natlang.rdg.model.Instrument;
import natlang.rdg.model.PlotElement;
import natlang.rdg.model.RSEdge;
import natlang.rdg.model.RSGraph;
import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;

public class Introduction {
	private CharacterInfo characters;
	private RSGraph graph;

	public Introduction(CharacterInfo characters){
		this.characters = characters;
	}
	
	public RSGraph transform(){
		System.out.println(graph.getRoot());
		
		for (CharacterModel c : characters.getChars()){
			if (c.getName()!=null && !c.getName().equals("")){
				PlotElement locationPe = createLocationPe(c,c.getLocation());
				System.out.println(locationPe);
				//add to the story
				RSVertex root = graph.getRoot();
				createRhetRel(LibraryConstants.ADDITIVE,locationPe,root);
			}
		}
		
		return graph;
	}
	
	private PlotElement createLocationPe(CharacterModel c, String location) {
		/*kind = "action"
		name = "be_at"
		agens = c
		patiens = ""
		target = location
		instrument = null
		time = 0;*/
		PlotElement result = new PlotElement("action","be_at",c.getEntity(),"",location,new Instrument("",2),0);
		return result;
	}

	public void setGraph(RSGraph graph){
		this.graph = graph;
	}
	
	public RSGraph getGraph(){
		return graph;
	}
	
	/**
	 * Creates RhetRelation
	 * @param rel
	 * @param vert1
	 * @param vert2
	 * @return relation
	 */
	public RhetRelation createRhetRel(String rel, RSVertex vert1, RSVertex vert2)
	{
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge sat = new RSEdge("satellite");
		sat.setTarget(rhetrel);
		sat.setSource(vert1);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert2);
					
		graph.addMEdge(sat);
		graph.addMEdge(nuc);
		
		return rhetrel;
	}
}
