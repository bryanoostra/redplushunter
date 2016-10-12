package rene.tests;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.rete.Triple;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

public class JenaTester2 {
	private OntModel m;
	private NamedGraphSet graphset;
	
	public static final String NS_FABULA = "http://www.owl-ontologies.com/FabulaKnowledge.owl#";
	
	public static final String FE_ACTION 			= NS_FABULA + "Action";
	public static final String FE_EVENT 			= NS_FABULA + "Event";
	public static final String FE_GOAL 				= NS_FABULA + "Goal";
	public static final String FE_INTERNAL_ELEMENT 	= NS_FABULA + "InternalElement";
	public static final String FE_OUTCOME 			= NS_FABULA + "Outcome";
	public static final String FE_PERCEPTION 		= NS_FABULA + "Perception";
	public static final String FE_SETTING_ELEMENT 	= NS_FABULA + "SettingElement";
	
	public void read(){
		graphset = new NamedGraphSetImpl();
		
		graphset.read("StoryWorldCore.owl", "RDF/XML");
		graphset.read("FabulaKnowledge.owl", "RDF/XML");
//		graphset.read("Lollipop.owl", "RDF/XML");
		graphset.read("PiratesSetting.owl", "RDF/XML");
		// then the story setting and the story itself
//		graphset.read("lolli.trig", "TRIG");
		graphset.read("pirates_ivo.trig", "TRIG");
//		graphset.read("park.ttl", "TURTLE");
		graphset.read("bar.ttl", "TURTLE");
		graphset.read("Guybrush.ttl", "TURTLE");
		graphset.read("island.ttl", "TURTLE");
		graphset.read("leChuck.ttl", "TURTLE");
		graphset.read("ship.ttl", "TURTLE");
		
		// Count all graphs in the graphset (2)
		System.out.println("The graphset contains " + graphset.countGraphs() + " graphs.");

//		Iterator it = graphset.listGraphs();
//		while(it.hasNext()){
//			NamedGraph ng = (NamedGraph)it.next();
//			Node n = (Node)ng.getGraphName();
//			System.out.println(n.getURI());
//		}
		
		m = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
		m.add(graphset.asJenaModel("http://example.org/defaultgraph"));
		m.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		m.setNsPrefix("fabula", "http://www.owl-ontologies.com/FabulaKnowledge.owl#");
		m.setNsPrefix("swc", "http://www.owl-ontologies.com/StoryWorldCore.owl#");
//		m.write(System.out, "N-TRIPLE" );
	}
	
	
	public void querySQL(){
		Query q = QueryFactory.create("" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX fabula: <http://www.owl-ontologies.com/FabulaKnowledge.owl#> " +
				"PREFIX lolli: <http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#>" + 
				"SELECT DISTINCT  ?o  " +
				"WHERE {" +
				"	_:linda rdf:type ?o . " +
				"}");
		QueryExecution qe = QueryExecutionFactory.create(q, m);
		ResultSet results = qe.execSelect();
		
		while(results.hasNext()){
			QuerySolution qs = results.nextSolution();
			Resource ro = qs.getResource("o");
			System.out.println("_:" + ro.getLocalName());
//			Resource r = m.getOntResource("http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#oIceCreamTruck").getRDFType(true);
//			
//			System.out.println(":" + r.getLocalName());
		}
	}
	
	public void tellMeAbout(String subject){
		OntResource or = m.getOntResource(subject);
		StmtIterator sit = or.listProperties();
		System.out.println("\n *** PROPERTIES *** ");
		System.out.println(subject + ": ");
		while(sit.hasNext()){
			Statement s = sit.nextStatement();
			System.out.print(" - "  + s.getPredicate().getLocalName());
			if(s.getObject().isResource())
				System.out.println(" " + ((Resource)(s.getObject().as(Resource.class))).getLocalName());
			else 
				System.out.println(" " + ((Literal)(s.getObject().as(Literal.class))).getString());
		}
		
		ExtendedIterator eit = or.listRDFTypes(true);
		System.out.println("\n *** RDFTypes (direct)*** ");
		System.out.println(subject + ": ");
		while(eit.hasNext()){
			Resource r = (Resource)eit.next();
			System.out.println(" - "  + r.getLocalName());
		}
		
				
		ExtendedIterator eit2 = or.listRDFTypes(false);
		System.out.println("\n *** RDFTypes (NOT direct)*** ");
		System.out.println(subject + ": ");
		while(eit2.hasNext()){
			Resource r = (Resource)eit2.next();
			System.out.println(" - "  + r.getLocalName());
		}
	}
	
	public void tellMeAboutAllSubjects(){
		ResIterator rit = m.listSubjects();
		while(rit.hasNext()){
			Resource r = rit.nextResource();
			
			if(r != null && r.getNameSpace() != null && 
					(r.getNameSpace().equals("http://www.owl-ontologies.com/StoryWorldSettings/Pirates#")
					||r.getNameSpace().equals("file:pirate.trig#"))){
				OntResource or = (OntResource)r.as(OntResource.class);
				
//				if(or.hasRDFType(FE_OUTCOME))
					tellMeAbout(r.getURI());//System.err.println("ACTION ALERT: " + r);
			
//			else if (r.getNameSpace() == null) {
//				System.out.println(" NULL NAMESPACE | r.uri : " + (r.getURI()));
//				System.out.println("\t" + r.toString());
//			}
//			else
//				System.out.println(" r.uri : " + (r.getURI()));
			
				
			}
		}
	}
	
	public void whichCaused(String o){
		System.out.println(o);
		System.out.println("exists: " + (m.getResource(o) != null));
		ResIterator ri = m.listSubjectsWithProperty(ResourceFactory.createProperty(NS_FABULA, "causes"), m.getResource(o));
		while(ri.hasNext()){
			Resource r = ri.nextResource();
			System.out.println(r.getLocalName());
		}
		
	}
	
	public void whatCaused(String o){
		System.out.println(o);
		Resource r = m.getResource(o);
		System.out.println("exists: " + (m.getResource(o) != null));
		StmtIterator sit = r.listProperties(ResourceFactory.createProperty(NS_FABULA, "causes"));
		
		while(sit.hasNext()){
			Statement s = sit.nextStatement();
			System.out.println(((Resource)s.getObject().as(Resource.class)).getLocalName());
		}
		
		Query q = QueryFactory.create("" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
				"PREFIX fabula: <http://www.owl-ontologies.com/FabulaKnowledge.owl#> " +
				"PREFIX pirate: <http://www.owl-ontologies.com/StoryWorldSettings/Pirates#>" + 
				"SELECT DISTINCT  ?o  " +
				"WHERE {" +
				"	_:"+r.getLocalName()+" fabula:causes ?o . " +
				"}");
		QueryExecution qe = QueryExecutionFactory.create(q, m);
		ResultSet results = qe.execSelect();
		
		while(results.hasNext()){
			QuerySolution qs = results.nextSolution();
			Resource ro = qs.getResource("o");
			System.out.println("_:" + ro.getLocalName());
//			Resource r = m.getOntResource("http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#oIceCreamTruck").getRDFType(true);
//			
//			System.out.println(":" + r.getLocalName());
		}
	}
	
	public void causalLinkOf(String o, int sp){
		if(sp == 0) System.out.println(o);
		Resource r = m.getResource(o);

		StmtIterator sit = r.listProperties(ResourceFactory.createProperty(NS_FABULA, "causes"));
		
		while(sit.hasNext()){
			Statement s = sit.nextStatement();
			Resource next = (Resource)s.getObject().as(Resource.class);
			for(int i = 0; i < sp; i++){
				System.out.print("    ");
			}
			System.out.println(" - " + next.getLocalName());
			causalLinkOf(next.getURI(), sp+1);
		}
	}
	
	public void typeOf(String n){
		System.out.println("getRDFType: " + m.getOntResource(n).getRDFType(true).getLocalName());
		ExtendedIterator ei = m.getOntResource(n).listRDFTypes(true);
		System.out.println("ontresource.listRDFTypes(true)");
		while(ei.hasNext()){
			Resource or = (Resource)ei.next();
			System.out.println(" - " + or.getLocalName());
		}
		StmtIterator si = m.listStatements(m.getOntResource(n), ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), (RDFNode)null);
//		ExtendedIterator ei = m.getOntResource(n).getPlistRDFTypes(true);
		System.out.println("model.listStatements(n, 'type', ?o)");
		while(si.hasNext()){
			Statement s = si.nextStatement();
			System.out.println(" - " + ((Resource)s.getObject()).getLocalName());
		}
		
		System.out.println("getRDFType: " + m.getOntResource(n).getRDFType(true).getLocalName());
		
		
	}
	
	public void typesOf(String n, int i){
		
		ExtendedIterator ei = m.getOntResource(n).listRDFTypes(true);
		
		
		while(ei.hasNext()){
			Resource or = (Resource)ei.next();
			for(int x=0; x<i;x++)
				System.out.print("  ");
			System.out.println(" -> " + or.getURI());
			typesOf(or.getURI(), i+1);
			if(or.hasProperty(ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), ResourceFactory.createResource("http://www.w3.org/2002/07/owl#Class"))){
				System.out.println("Superclasses: ");
				superClassesOf((OntResource)or.as(OntResource.class), i+1);
			}
		}
		
	}
	
	public void superClassesOf(OntResource r, int i){
		ExtendedIterator ei;
		try{
			
			ei = ((OntClass)r.as(OntClass.class)).listSuperClasses(true);
		}catch(NullPointerException e){
			System.err.println(" null pointer: " + r);
			System.err.println(" ontresource: " + r );
			return;
		}
		
		while(ei.hasNext()){
			OntResource or = (OntResource)ei.next();
			for(int x=0; x<i;x++)
				System.out.print("  ");
			System.out.println(" -> " + or.getURI());
			if(or.hasProperty(ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), ResourceFactory.createResource("http://www.w3.org/2002/07/owl#Class"))){
				
				superClassesOf(or, i+1);
			}
		}
		
	}
	
	private void talkAboutContents(String name){
		System.out.println("contents of: " + name);
//		OntResource or = m.getOntResource(name);
//		System.out.println("ontresource.listRDFTypes(true)");
//		Model m = or.getModel();
//		
//		NodeIterator nit = m.listObjects();
//		
//		while(nit.hasNext()){
//			RDFNode n = nit.nextNode();
//			System.out.println(" - " + n.toString());
////			System.out.println(" - " + ((Resource)n.as(Resource.class)).getLocalName());
//		}
		NamedGraph ng = graphset.getGraph(name);
		ExtendedIterator ei = ng.find(null, null, null);
		while(ei.hasNext()){
			com.hp.hpl.jena.graph.Triple t = (com.hp.hpl.jena.graph.Triple)ei.next();
//			System.out.println(o.getClass());
			System.out.println(t.getObject().getLocalName());
		}
		
		
	}
	
	public static void main(String[] args ){
		JenaTester2 jt2 = new JenaTester2();
		jt2.read();
//		jt2.causalLinkOf("file:pirate.trig#iGoal_leChuck_3", 0);
		jt2.tellMeAboutAllSubjects();
//		jt2.talkAboutContents("file:pirates_ivo.trig#iperception_plot_8_contents");
//		jt2.whichCaused("file:lolli.trig#iBelief_linda_5");
//		System.out.println("super-types of iAction_linda_1: ");
//		jt2.typesOf("file:lolli.trig#iAction_linda_4", 0);
//		jt2.typeOf("http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#ToddleOffTo");
//		jt2.tellMeAbout("http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#Buy");
//		jt2.tellMeAbout("file:lolli.trig#iEpisodicGoal_linda_5");
	}
}
