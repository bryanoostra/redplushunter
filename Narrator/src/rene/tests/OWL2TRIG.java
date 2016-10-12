package rene.tests;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

public class OWL2TRIG {

	public void convertOwl(String fname){
		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read(fname, "RDF/XML");
		graphset.write(System.out, "TRIG", "http://www.owl-ontologies.com/StoryWorldSettings/plop.owl#");
		// Count all graphs in the graphset (2)
//		System.out.println("The graphset contains " + graphset.countGraphs() + " graphs.");
		
//		Model model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
//		Model m = graphset.asJenaModel("http://example.org/defaultgraph");
//		model.add(m);
//		model.write(System.out, "TRIG" );
		
//		Query q = QueryFactory.create("" +
//				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
//				"PREFIX fabula: <http://www.owl-ontologies.com/FabulaKnowledge.owl#> " + 
//				"SELECT ?x ?z " +
//				"WHERE {" +
//				"	?x fabula:hasContext ?z . " +
////				"	?x rdf:type ?fe1 . " +
////				"	?fe1 rdfs:subClassOf ?fe2 . " +
////				"	?fe2 rdfs:subClassOf fabula:FabulaElement . " +
//				"}");
//		QueryExecution qe = QueryExecutionFactory.create(q, model);
//		ResultSet results = qe.execSelect();
//		
//		ResultSetFormatter.out(System.out, results, q);
		
	}
	
	public static void main(String[] args ){
		OWL2TRIG o2t = new OWL2TRIG();
		o2t.convertOwl("plop.owl");
	}
	
}
