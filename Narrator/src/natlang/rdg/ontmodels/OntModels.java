package natlang.rdg.ontmodels;

import java.util.Iterator;

import natlang.rdg.datafilessetter.DataInstance;
import natlang.rdg.datafilessetter.DataInstanceSetter;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

public class OntModels {

	public static OntModel ontModelVST, ontModelNarrator, ontModelCombined;
	public static NamedGraphSet graphsetVST, graphsetNarrator;
	
	public static void createOntModels(){
		graphsetVST = new NamedGraphSetImpl();
		graphsetNarrator = new NamedGraphSetImpl();
		
		
		if(natlang.Narrator.VST_INFO != null){
			DataInstanceSetter dis = natlang.Narrator.VST_INFO;
			Iterator<DataInstance> diit = dis.getDataInstanceIterator();
			while(diit.hasNext()){
				DataInstance di = diit.next();
				System.out.println(di.getLang());
				graphsetVST.read(di.getReader(), di.getLang(), di.getBaseURI());
			}
			
			ontModelVST = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
			ontModelVST.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
			ontModelVST.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			ontModelVST.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			ontModelVST.setNsPrefix("fabula", "http://www.owl-ontologies.com/FabulaKnowledge.owl#");
			ontModelVST.setNsPrefix("swc", "http://www.owl-ontologies.com/StoryWorldCore.owl#");
//			m = ModelFactory.createOntologyModel(  );
			ontModelVST.add(graphsetVST.asJenaModel("http://www.owl-ontologies.com/story"));
		}
		
		if(natlang.Narrator.NARRATOR_INFO != null){
			DataInstanceSetter dis = natlang.Narrator.NARRATOR_INFO;
			Iterator<DataInstance> diit = dis.getDataInstanceIterator();
			while(diit.hasNext()){
				DataInstance di = diit.next();
				graphsetNarrator.read(di.getReader(), di.getLang(), di.getBaseURI());
			}
			ontModelNarrator = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
//			m = ModelFactory.createOntologyModel(  );
			ontModelNarrator.add(graphsetNarrator.asJenaModel("http://example.org/defaultgraph"));
		}
		
		if(ontModelVST != null && ontModelNarrator != null){
			ontModelCombined = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
			ontModelCombined.add(graphsetVST.asJenaModel("http://example.org/vst"));
			ontModelCombined.add(graphsetNarrator.asJenaModel("http://example.org/narrator"));
			
		}
		
	}
	
}
