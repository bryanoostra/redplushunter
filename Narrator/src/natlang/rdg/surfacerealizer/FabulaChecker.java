package natlang.rdg.surfacerealizer;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;

import natlang.debug.LogFactory;
import natlang.rdg.discourse.EntityHistory;
import natlang.rdg.documentplanner.InitialDocPlanBuilder;
import natlang.rdg.lexicalchooser.CharacterInfo;
import natlang.rdg.lexicalchooser.Hierarchy;
import natlang.rdg.ontmodels.OntModels;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/**
 * This class takes care of pre-process reasoning about the fabula
 * to create information:
 *  - are certain objects the only objects in the world? In this case, the narrator
 *    should refer to them as 'the boat' instead of 'a boat'
 *  - what are relations between objects and characters?
 *  - ...
 * @author Rene Zeeders
 *
 */

public class FabulaChecker {

	public static final String SWC 			= "http://www.owl-ontologies.com/StoryWorldCore.owl#";
	public static final String SWC_PHYSICAL	= SWC + "Physical"; 
	public static final String SWC_ROLE		= SWC + "Role";
	public static final String SWC_OBJECT	= SWC + "Object";
	public static final String SWC_ENTITY	= SWC + "Entity";
	public static final String SWC_HAS_PART	= SWC + "hasPart";
	public static final String SWC_CONTAINS	= SWC + "contains";
	public static final String RDF			= "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDF_TYPE		= RDF + "type";
	//public static final String FABULA		= "http://www.owl-ontologies.com/StoryWorldSettings/Pirates#"; 
		//"http://www.owl-ontologies.com/FabulaKnowledge.owl#";
	public static final String SWC_OWNS	= SWC + "owns";
	
	public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String RDFS_SUBCLASSOF = RDFS + "subClassOf";
	
	public static final String COMMON_SENSE = "http://www.owl-ontologies.com/CommonSense.owl#";
	public static final String COMMON_SENSE_RELATIONSHIP = COMMON_SENSE + "RelationShip";
	public static final String COMMON_SENSE_PARENT = COMMON_SENSE + "parent";
	public static final String COMMON_SENSE_CHILD = COMMON_SENSE + "child";
	
//	public static final String OWL			= "http://www.w3.org/2002/07/owl#";
//	public static final String OWL_TYPE		= OWL + "a";
	
	private OntModel model;
	private EntityHistory entityHist;
	private CharacterInfo charInfo;
	private Hierarchy hierarchy;
	
	
	private Vector<Instance> uniqueInstances;
	private HashMap<String, Resource> library; // for the relation between the local name of an instance and the resource
	private TreeMap<String, Vector<String>> roles;
	
	private FileWriter fw;
	
	private Logger logger;
	
	public FabulaChecker(OntModel model, EntityHistory entityHist, CharacterInfo charInfo , Hierarchy hierarchy){
		logger = LogFactory.getLogger(this);
		this.model = OntModels.ontModelCombined; //model;
		this.entityHist = entityHist;
		this.charInfo = charInfo;
		this.hierarchy = hierarchy;
		try{
			fw = new FileWriter("testfileFC.txt");
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		
	}
	
	private void createLibrary(){
		library = new HashMap<String, Resource>();
		
		logger.info("Adding these to the library: ");
		// all resources are added to the library
		
		StmtIterator it = model.listStatements(null, model.createProperty(RDF_TYPE), (RDFNode)model.createResource(SWC_ENTITY));
		
		while(it.hasNext()){
			Statement s = it.nextStatement();
			Resource r = s.getSubject();
			
			if(InitialDocPlanBuilder.isSubclassOf(r, SWC_ROLE)
					|| InitialDocPlanBuilder.isSubclassOf(r, SWC_OBJECT)){
				
				library.put(r.getLocalName(), r);
			}
		}
	}
	
	private void extractRoles(){
		roles = new TreeMap<String, Vector<String>>();
//		StmtIterator it = model.listStatements(null, model.createProperty(RDF_TYPE), (RDFNode)null);
		ExtendedIterator ei = model.listIndividuals();
		
		logger.info("relation types...");
		
		while(ei.hasNext()){
			RDFNode obj = (RDFNode)ei.next();
			
			
			
			if(obj instanceof Resource && InitialDocPlanBuilder.isSubclassOf(obj, SWC_ROLE)){
				Resource r = (Resource)obj;
				Resource type = getType(r, SWC_ROLE);
				
//				charInfo.addCharacter(r.getLocalName(), type.getLocalName(), "male", "Le Chuck");
				
				logger.info(r.getLocalName() + " --> " + type.getLocalName());
//				Vector<Resource> vecR = new Vector<Resource>();
//				vecR.add(type);
				Vector<String> vecR = addSuperClass(type, new Vector<String>());
				roles.put(r.getLocalName(), vecR);
				// print superclasses:
				logger.info(" superclasses of " + r.getLocalName());
				Iterator<String> it = vecR.iterator();
				while(it.hasNext()){
					logger.info(" - " + it.next());
				}
				
			}
		}
		
		logger.info("done...");
//		System.exit(0);
	}
	
	public Vector<String> getMoreGeneralConcepts(String ent){
		return roles.get(ent);
	}
	
	/**
	 * Adds superclasses recursively, this way the first superclass in 
	 * the vector is the closest to the instance, etc.
	 * @param r to be added to the vector, along with the superclasses of it
	 * @param vecR the vector with all subclasses of r (or empty)
	 * @return the vector with all sub-, superclasses of r and r itself
	 */
	private Vector<String> addSuperClass(Resource r, Vector<String> vecR){
		if(!r.toString().equals(SWC_ROLE)){
			
			vecR.add(r.getLocalName());
			
			OntClass oc = model.createClass(r.getURI());
			ExtendedIterator ei = oc.listSuperClasses(true);
			
			while(ei.hasNext()){
				OntClass sclass = (OntClass)ei.next();
				if(sclass.hasSuperClass(model.createResource(SWC_ROLE)))
					return addSuperClass(sclass, vecR);
			}
		}	
		
		return vecR;
	}
	
	/**
	 * Checks the fabula which things are part of which things. Momentarily
	 * this is performed via the SWC:partOf relation. Maybe this should be done
	 * more explicit via the SWC:hasObject, or maybe somewhere in between?
	 *
	 */
	private void extractRelationPartOf(){
		StmtIterator it = model.listStatements(null, model.createProperty(SWC_HAS_PART), (RDFNode)null);
//		ExtendedIterator ei = model.listIndividuals();
		logger.info("part-of...");
		while(it.hasNext()){
			Statement s = it.nextStatement();
//			if(s.getSubject().getLocalName().equals("oShip_1")
//					|| s.getObject().toString().endsWith("oShip_1"))
				logger.info(
					s.getSubject().getLocalName() + " \n\t " +
					s.getPredicate().getLocalName() + " \n\t\t " +
					s.getObject());
			if(s.getObject() instanceof Resource){
				hierarchy.addPartOfRelation(((Resource)s.getObject()).getLocalName(), s.getSubject().getLocalName());
				writeToFile("partof: " + ((Resource)s.getObject()).getLocalName() + " -- " + s.getSubject().getLocalName());
			}
		}
		
		logger.info("done...");
//		System.exit(0);
	}
	
	private void extractRelationProperty(){
		//
		StmtIterator it = model.listStatements(null, model.createProperty(SWC_OWNS), (RDFNode)null);
//		ExtendedIterator ei = model.listIndividuals();
		logger.info("properties...");
		while(it.hasNext()){
			Statement s = it.nextStatement();
//			if(s.getSubject().getLocalName().equals("oShip_1")
//					|| s.getObject().toString().endsWith("oShip_1"))
				logger.info(
					s.getSubject().getLocalName() + " \n\t " +
					s.getPredicate().getLocalName() + " \n\t\t " +
					s.getObject());
			if(s.getObject() instanceof Resource)
				hierarchy.addBelongToRelation(((Resource)s.getObject()).getLocalName(), s.getSubject().getLocalName());
		}
		
		logger.info("done...");
//		
	}
	
	private void extractRelationContainedBy(){
		StmtIterator it = model.listStatements(null, model.createProperty(SWC_CONTAINS), (RDFNode)null);
//		ExtendedIterator ei = model.listIndividuals();
		logger.info("containments...");
		while(it.hasNext()){
			Statement s = it.nextStatement();
//			if(s.getSubject().getLocalName().equals("oShip_1")
//					|| s.getObject().toString().endsWith("oShip_1"))
			logger.info(
					s.getSubject().getLocalName() + " \n\t " +
					s.getPredicate().getLocalName() + " \n\t\t " +
					s.getObject());
			if(s.getObject() instanceof Resource)
				hierarchy.addContainedByRelation(((Resource)s.getObject()).getLocalName(), s.getSubject().getLocalName());
		}
		
		logger.info("done...");
//		System.exit(0);
	}	
	
	/**
	 * Extracts the unique things in this world. It first adds all instances 
	 * available in the fabula to a vector, after which it checks for each value in 
	 * that vector if other instances of the same class are available. If so
	 * it deletes all instances of that class from the vector. This
	 * way, at the end, a vector with all unique values remains.
	 * @return
	 */
	private void extractUniques(){
		
		uniqueInstances = new Vector<Instance>();
		
		StmtIterator it = model.listStatements(null, model.createProperty(RDF_TYPE), (RDFNode)model.createResource(COMMON_SENSE_RELATIONSHIP));
		logger.info("unique extractions...");
		while(it.hasNext()){
			Statement s = it.nextStatement();
//			if(s.getSubject().getLocalName().equals("oShip_1")
//					|| s.getObject().toString().endsWith("oShip_1"))
			logger.info(
				s.getSubject().getLocalName() + " \n\t " +
				s.getPredicate().getLocalName() + " \n\t\t " +
				s.getObject());

			//hierarchy.addContainedByRelation(((Resource)s.getObject()).getLocalName(), s.getSubject().getLocalName());
			StmtIterator parent_it = model.listStatements(s.getSubject(), model.createProperty(COMMON_SENSE_PARENT), (RDFNode)null);
			StmtIterator child_it = model.listStatements(s.getSubject(), model.createProperty(COMMON_SENSE_CHILD), (RDFNode)null);
			Resource parent = (Resource)parent_it.nextStatement().getObject();
			Resource child = (Resource)child_it.nextStatement().getObject();
			uniqueInstances.add(new Instance(parent, child));
		}
		Iterator<Instance> instit = uniqueInstances.iterator();
		while(instit.hasNext()){
			Instance i = instit.next();
			logger.info("1: " + i.getType());
			logger.info("2: " + i.getInstance());
		}
		
		
	}
	
	private void writeToFile(String line){
		try{
			fw.write(line + "\n");
			fw.flush();
		}
		catch(Exception e){
			e.printStackTrace();

			System.exit(0);
		}
	}
	
	public boolean exactlyOne(String ent1, String con2){
		logger.info("exactlyOne? " + ent1 + ", " + con2);
		writeToFile("exactlyOne? " + ent1 + ", " + con2);
//		if(con2.equals("Hold")) System.exit(0);
		Resource rEnt1 = (Resource)library.get(ent1);
		
		Resource type = getType(rEnt1);
		
		logger.info("ent1 URI: " + rEnt1);
		logger.info("type URI: " + type);
		
		StmtIterator it = model.listStatements(null, model.createProperty(RDF_TYPE), (RDFNode)model.createResource(COMMON_SENSE_RELATIONSHIP));
		while(it.hasNext()){
			Statement s = it.nextStatement();
			StmtIterator parent_it = model.listStatements(s.getSubject(), model.createProperty(COMMON_SENSE_PARENT), (RDFNode)null);
			StmtIterator child_it = model.listStatements(s.getSubject(), model.createProperty(COMMON_SENSE_CHILD), (RDFNode)null);
			Resource parent = (Resource)parent_it.nextStatement().getObject();
			Resource child = (Resource)child_it.nextStatement().getObject();
			logger.info("parent: " + parent);
			logger.info("child: " + child);
			if(parent.equals(type)){
				logger.info("1. there is a rule for " + ent1);
				logger.info("1. the following child corresponds with it " + child.getLocalName());
				if(child.getLocalName().equals(con2)){
					logger.info("1. YES!");
					return true;
				}
			}
		}
		
		
		
		
//		System.exit(0);
//		Iterator<Instance> it = uniqueInstances.iterator();
//		while(it.hasNext()){
//			Instance i = it.next();
//			if(i.getType().getLocalName().equals(con2)
//					&& i.getInstance().getLocalName().equals(ent1))
//				return true;
//		}
		return false;
	}
	
	/**
	 * Checks of the given entity is unique in this world. The function
	 * checks if the entity is available in the vector created by
	 * extractUniques(), and returns whether it does.
	 * @param ent the entity which is either unique or it isn't
	 * @return true if the given entity is unique
	 */
	public boolean unique(String ent){
//		writeToFile("unique? " + ent);
//		System.out.println("VRAAGJE OVER UNIQUE");
//		System.out.println(ent);
////		uniqueInstances.elementAt(1000);
//		Iterator<Instance> it = uniqueInstances.iterator();
//		while(it.hasNext()){
//			Instance i = it.next();
//			if(i.getType().getLocalName().equals(ent))
//				return true;
//		}
//		System.out.println("IS NIET UNIQUE");
		return false;
	}
	
	/**
	 * Returns the type of the given Resource.
	 * @param s
	 * @return type
	 */
	private Resource getType(Resource r) {
//		return ((OntResource)r).getRDFType(true);
		return model.getOntResource(r.toString()).getRDFType(true);
	}
	
	/**
	 * Returns the type (subclass of the given superclass)of the given Resource.
	 * This means that any type of Resource r that is not a subclass of the 
	 * given superclass is not returned. 
	 * @param s
	 * @return type
	 */
	private Resource getType(Resource r, String superclass) {
//		return ((OntResource)r).getRDFType(true);
		ExtendedIterator ei = model.getOntResource(r.toString()).listRDFTypes(true);
		while(ei.hasNext()){
			OntClass or = (OntClass)((Resource)ei.next()).as(OntClass.class);
			if(or.hasSuperClass(model.createResource(superclass)))
					return or;
		}
		return null;
	}
	
	private boolean isOfType(RDFNode n, String type){
		Resource r = model.getOntResource(n.toString()).getRDFType(true);
		if(InitialDocPlanBuilder.isSubclassOf(r, SWC_PHYSICAL))
			return true;
		else return false;
	}
	
	public void start(){
		createLibrary();
		extractRoles();
		extractRelationPartOf();
		extractRelationProperty();
		extractRelationContainedBy();
		extractUniques();
	}
	
	class Instance {
		
		Resource type, instance;
		
		Instance(Resource type, Resource instance){
			this.type = type;
			this.instance = instance;
		}

		public Resource getType(){
			return type;
		}
		
		public Resource getInstance(){
			return instance;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {
			return (
//					type.equals(((Instance)o).getType())
//					&&
					instance.equals(((Instance)o).getInstance())
					);
		}
		
		
	}
	
}

