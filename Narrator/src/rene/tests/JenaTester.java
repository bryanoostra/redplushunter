package rene.tests;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.VCARD;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;




public class JenaTester {

	public void createModel(){
		// some definitions
		String personURI    = "http://somewhere/JohnSmith";
		String givenName    = "John";
		String familyName   = "Smith";
		String fullName     = givenName + " " + familyName;

		// create an empty Model
		Model model = ModelFactory.createDefaultModel();

		// create the resource
		//   and add the properties cascading style
		Resource johnSmith
		  = model.createResource(personURI)
		         .addProperty(VCARD.FN, fullName)
		         .addProperty(VCARD.N,
		                      model.createResource()
		                           .addProperty(VCARD.Given, givenName)
		                           .addProperty(VCARD.Family, familyName));
	
		// list the statements in the Model
		StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object

		    System.out.print(subject.toString());
		    System.out.print(" " + predicate.toString() + " ");
		    if (object instanceof Resource) {
		       System.out.print(object.toString());
		    } else {
		        // object is a literal
		        System.out.print(" \"" + object.toString() + "\"");
		    }

		    System.out.println(" .");
		} 
		
		System.out.println("\n\n");
		model.write(System.out, "N-TRIPLE");
	}
	
	public void readModel(String filename){
		Model m = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
		
//		*********************************************
		// JENA Create namescapce mappings for OWL, RDF and RDFS
		m.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		m.setNsPrefix("fabula", "http://www.owl-ontologies.com/FabulaKnowledge.owl#");
		m.setNsPrefix("swc", "http://www.owl-ontologies.com/StoryWorldCore.owl#");
		//*********************************************
		try{
			m.read(new FileInputStream(filename), "");
			m.write(System.out, "N-TRIPLE");
			
			Query q = QueryFactory.create("" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"PREFIX fabula: <http://www.owl-ontologies.com/FabulaKnowledge.owl#> " + 
					"SELECT ?x " +
					"WHERE {" +
					"	?x rdf:type fabula:FabulaElement . " +
//					"	?x rdf:type ?fe1 . " +
//					"	?fe1 rdfs:subClassOf ?fe2 . " +
//					"	?fe2 rdfs:subClassOf fabula:FabulaElement . " +
					"}");
			QueryExecution qe = QueryExecutionFactory.create(q, m);
			ResultSet results = qe.execSelect();
			
			ResultSetFormatter.out(System.out, results, q);

			qe.close();
			
			
		}
		catch(FileNotFoundException e){
			System.err.println(" FILE NOT FOUND " + filename);
		}
	}
	//phi_causes
	public void readTRIG(String fname){
		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read(fname, "TRIG");
		graphset.read("FabulaKnowledge.owl", "RDF/XML");
//		graphset.read("park.ttl", "N3");
		// Count all graphs in the graphset (2)
		System.out.println("The graphset contains " + graphset.countGraphs() + " graphs.");

		Iterator it = graphset.listGraphs();
		while(it.hasNext()){
			NamedGraph ng = (NamedGraph)it.next();
			Node n = (Node)ng.getGraphName();
			System.out.println(n.getURI());
		}
		
		OntModel m = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
		m.add(graphset.asJenaModel("http://example.org/defaultgraph"));
		m.write(System.out, "N-TRIPLE" );
//		Model m = graphset.asJenaModel("http://example.org/defaultgraph");
		Query q = QueryFactory.create("" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX fabula: <http://www.owl-ontologies.com/FabulaKnowledge.owl#> " +
				"PREFIX lolli: <http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#>" + 
				"SELECT DISTINCT  ?o ?s " +
				"WHERE {" +
				"	_:oIceCreamTruck rdfs:subClassOf fabula:Put . " +
				"	?s rdfs:subClassOf fabula:Put . " +
				"}");
		QueryExecution qe = QueryExecutionFactory.create(q, m);
		ResultSet results = qe.execSelect();
		
//		System.out.println("** " + curr);
//		ResultSetFormatter.out(System.out, results, q);
		
		while(results.hasNext()){
			QuerySolution qs = results.nextSolution();
			Resource ro = qs.getResource("s");
			System.out.println(ro.toString());
			Resource r = m.getOntResource("http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#oIceCreamTruck").getRDFType(true);
			
//			System.out.println(ro.getLocalName());
			System.out.println(":" + r.getLocalName());
//			if(ro.getLocalName().equals("iperception_plot_14")){
//				Resource rs = qs.getResource("s");
//				System.out.println("\t*" + rs);
//			}
			
		}
		
		
//		System.out.println(results.hasNext());
//		while(results.hasNext()){
//			System.out.println(((OntResource)results.nextSolution().getResource("o").as(OntResource.class)).getRDFType(true));
//		}
//		System.out.println(m.getOntResource(results.nextSolution().getResource("o")).getRDFType(true));
		
		
		
		
		
		
//		q = QueryFactory.create(
//				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
//				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
//				"PREFIX fabula: <http://www.owl-ontologies.com/FabulaKnowledge.owl#> " + 
//				"SELECT ?graph ?s ?o WHERE { GRAPH ?graph { ?s fabula:time ?o } }");
//		qe = QueryExecutionFactory.create(q, new NamedGraphDataset(graphset));
//		results = qe.execSelect();
//		while (results.hasNext()) {
//		    QuerySolution result = results.nextSolution();
//		    RDFNode graph = result.get("graph");
//		    RDFNode s = result.get("s");
//		    
//		    RDFNode o = result.get("o");
//		    int t = -1; 
//		    if(o.isLiteral()){
//		    	t = ((Literal)o.as(Literal.class)).getInt();
//		    }
//		    
////		    String c = result.getResource("o").getgetValue().getClass().toString();
//		    
//		    System.out.println(
//		    		graph + 
//		    		" { " + 
//		    		s + 
//		    		" -- time -- " + 
//		    		t +
//		    		" }"
//		    );
//}

		
		
	}
	
	public void readOWL(String fname){
		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read(fname, "RDF/XML");
		// Count all graphs in the graphset (2)
		System.out.println("The graphset contains " + graphset.countGraphs() + " graphs.");
		
		Model model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
		Model m = graphset.asJenaModel("http://example.org/defaultgraph");
		model.add(m);
		model.write(System.out, "N-TRIPLE" );
		
		Query q = QueryFactory.create("" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX fabula: <http://www.owl-ontologies.com/FabulaKnowledge.owl#> " + 
				"SELECT ?x ?z " +
				"WHERE {" +
				"	?x fabula:hasContext ?z . " +
//				"	?x rdf:type ?fe1 . " +
//				"	?fe1 rdfs:subClassOf ?fe2 . " +
//				"	?fe2 rdfs:subClassOf fabula:FabulaElement . " +
				"}");
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet results = qe.execSelect();
		
		ResultSetFormatter.out(System.out, results, q);
		
	}
	
	public void test(){
		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read("lolli.trig", "TRIG");
		graphset.read("FabulaKnowledge.owl", "RDF/XML");
		graphset.read("park.ttl", "N3");
		// Count all graphs in the graphset (2)
		System.out.println("The graphset contains " + graphset.countGraphs() + " graphs.");

		Iterator it = graphset.listGraphs();
		while(it.hasNext()){
			NamedGraph ng = (NamedGraph)it.next();
			Node n = (Node)ng.getGraphName();
			System.out.println(n.getURI());
		}
		
		OntModel m = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
		m.add(graphset.asJenaModel("http://example.org/defaultgraph"));
//		m.write(System.out, "N-TRIPLE" );
		//_:iAction_linda_4 ?rel _:iperception_plot_14 . 
		Resource s = m.getResource("file:lolli.trig#iAction_linda_4");
		Resource o = m.getResource("file:lolli.trig#iperception_plot_14");
		StmtIterator stmtIt = m.listStatements(s, null, o);
		System.out.println("Relation between " + s + " & " + o );
		while (stmtIt.hasNext()){
			System.out.println("\t* " + stmtIt.nextStatement().getPredicate().toString());
		}
	}
	
	public static void main(String[] args){
		JenaTester jt = new JenaTester();
//		jt.readModel("plop.owl");
		jt.readTRIG("lolli.trig");
//		jt.readOWL("plop.owl");
//		jt.test();
	}
}
