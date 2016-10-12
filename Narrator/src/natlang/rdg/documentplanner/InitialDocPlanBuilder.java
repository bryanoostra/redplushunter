package natlang.rdg.documentplanner;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import natlang.debug.LogFactory;
import natlang.rdg.Narrator;
import natlang.rdg.datafilessetter.DataInstance;
import natlang.rdg.datafilessetter.DataInstanceSetter;
import natlang.rdg.model.Detail;
import natlang.rdg.model.Instrument;
import natlang.rdg.model.PlotElement;
import natlang.rdg.model.RSEdge;
import natlang.rdg.model.RSGraph;
import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;
import natlang.rdg.ontmodels.OntModels;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import parlevink.parlegraph.model.MLabeledEdge;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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

/**
 * <p>InitialDocumentPlanBuilder is the first component of the DocumentPlanner and
 * is responsible for the conversion of the fabula in OWL format into an initial
 * document plan.</p>
 * 
 * <p>A document plan is a binary tree consisting of rethorical relations (e.g.
 * "temporal", "nonvoluntary-cause", etc) as internal nodes. The leave nodes are
 * plot elements which are the fundamental elements of the story. These contain
 * the actions, perceptions, events, etc of the story.</p>
 * 
 * <p>Each internal node can have two children (hence it's a binary tree): a
 * nucleus and a satellite. The nucleus is the most important of the two,
 * and so each parent has at least a nucleus.</p> 
 * 
 * <p>The most important function is createTreeOwl(..) which implements the method 
 * described in Nanda Slabbers' thesis 'Narration for Virtual Storytelling' (2006)
 * (paragraph 6.2.2 Converting the fabula into an initial document plan, page 58-64).</p>
 * 
 * <p>The function transform() is the function that initiates all 
 * necessary functions in the correct order.</p>
 * 
 * 
 * @author Nanda Slabbers
 * @author René Zeeders
 */
public class InitialDocPlanBuilder 
{
	public static final String NS_FABULA = "http://www.owl-ontologies.com/FabulaKnowledge.owl#";
	
	// FabulaElement Sub-Classes:
	public static final String FE_ACTION 			= NS_FABULA + "Action";
	public static final String FE_EVENT 			= NS_FABULA + "Event";
	public static final String FE_GOAL 				= NS_FABULA + "Goal";
	public static final String FE_ATTAIN_GOAL 		= NS_FABULA + "AttainGoal";
	public static final String FE_AVOID_GOAL 		= NS_FABULA + "AvoidGoal";
	public static final String FE_LEAVE_GOAL 		= NS_FABULA + "LeaveGoal";
	public static final String FE_SUSTAIN_GOAL 		= NS_FABULA + "SustainGoal";
	public static final String FE_INTERNAL_ELEMENT 	= NS_FABULA + "InternalElement";
	public static final String FE_OUTCOME 			= NS_FABULA + "Outcome";
	public static final String FE_PERCEPTION 		= NS_FABULA + "Perception";
	public static final String FE_SETTING_ELEMENT 	= NS_FABULA + "SettingElement";
	public static final String FE_FALSEHOOD_GRAPH	= NS_FABULA + "FalsehoodGraph";
	public static final String FE_TRUTH_GRAPH		= NS_FABULA + "TruthGraph";
	
	private RSGraph graph;
	private Vector edges;
//	private OWLReasoningContext context;	
	private PlotElement prevpe;		
	private PlotElement prevprevpe;
	private Vector<RDFNode> toldMessages;
	
	private OntModel model;
	private NamedGraphSet graphset;
	
	private Logger logger;
		
	/**
	 * For testing purposes. Creates a InitialDocumentPlanBuilder and starts
	 * the function transform. 
	 * @param args
	 */
	public static void main(String[] args){
		InitialDocPlanBuilder idpb = new InitialDocPlanBuilder();
		idpb.transform();
	}
	
	/**
	 * Creates the InitialDocPlanBuilder. The Ontology Model is assigned using the
	 * static variable from the class OntModels. The Ontology Model is created after
	 * the start button in the GUI (natlang.start.Narrator) is pushed. If the model is null
	 * (e.g. because the GUI is not used), than the model is read via
	 * the config file config.ini.
	 */
	public InitialDocPlanBuilder()
	{
		logger = LogFactory.getLogger(this);
		
		toldMessages = new Vector<RDFNode>();
		prevpe = null;
		prevprevpe = null;
		
		model = OntModels.ontModelVST;
		graphset = OntModels.graphsetVST;
		
		if(model == null){
			// create the model to which all graphs are added
			model = ModelFactory.createOntologyModel( PelletReasonerFactory.THE_SPEC  );
	
			// Create namespace mappings for OWL, RDF and RDFS
			model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
			model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
			model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
			model.setNsPrefix("fabula", "http://www.owl-ontologies.com/FabulaKnowledge.owl#");
			model.setNsPrefix("swc", "http://www.owl-ontologies.com/StoryWorldCore.owl#");
					
//				System.out.println("Voor fabula laden");
			
			// the graphset to which all graphs are added
			// the resulting graph (union of all graphs) is
			// added to the jena model
			graphset = new NamedGraphSetImpl();
			
			
			DataInstanceSetter dis;
			if(natlang.Narrator.VST_INFO != null)
				dis = natlang.Narrator.VST_INFO;
			else{
				dis= new DataInstanceSetter();
				dis.readConfigFile("config.ini");
			}
			
			Iterator<DataInstance> diit = dis.getDataInstanceIterator();
			while(diit.hasNext()){
				DataInstance di = diit.next();
				logger.fine("Language of data instance: " + di.getLang());
				graphset.read(di.getReader(), di.getLang(), di.getBaseURI());
			}
			
			Iterator it = graphset.listGraphs();
			while(it.hasNext()){
				NamedGraph ng = (NamedGraph)it.next();
				logger.fine("Named graph: " + ng.getGraphName());
			}
			
			Model m = graphset.asJenaModel("http://www.owl-ontologies.com/story");
			model.add(m);
		}
		
		logger.info("Number of graphs: " + graphset.countGraphs());

	}
		
	/**
	 * Returns the Ontology Model used by this InitialDocPlanBuilder.
	 * @return the Ontology Model used by this InitialDocPlanBuilder
	 */
	public OntModel getModel(){
		return model;
	}
	
	/**
	 * This function is the main function that initiates all important processes
	 * in the correct order. Summarized these functions are called:
	 * <ol>
	 * 	<li>createTreeOwl(...) - creates the initial document plan</li>
	 *  <li>removeMessages() - removes certain messages that should not be told by the narrator</li>
	 *  <li>TreeBalancer.balance(..) - balances the tree so nice rethorical relations are created</li>
	 * </ol>
	 * 
	 * This function also glues multiple unrelated trees together, if there
	 * exists more than one in the fabula.
	 * 
	 * @return whether the transformation has ended successfully 
	 */
	public boolean transform()
	{		
		toldMessages = new Vector<RDFNode>();
		edges = new Vector();	
			
//		PlotElement pe = new PlotElement("setting", "be", "linda", "", "", new Vector());				
		RDFNode n = getStartingNode();
//		RSVertex rr = createRhetRel("temporal", createRhetRel("temp-once", pe), createTreeOwl(n, null, null, true));
		Vector<RSVertex> owltrees = new Vector<RSVertex>();
		while (n != null){
			RSVertex rr = createTreeOwl(n, null, null, true);
			owltrees.add(rr);
//			rr = createRhetRel("temporal", rr);
			n = getStartingNode();
		}
		
		Iterator<RSVertex> owltreeIt = owltrees.iterator();
		RSVertex tree = null;
		if(owltreeIt.hasNext())
			tree = owltreeIt.next();
			
		while(owltreeIt.hasNext()){
			
			RSVertex nuc = owltreeIt.next();
			if(nuc != null)
				tree = createRhetRel("temporal", tree, nuc);
			
		}
		
		tree = createRhetRel("temporal", tree);
	
		graph = new RSGraph((RhetRelation) tree);
		
		
		
		for (int i=0; i<edges.size(); i++)
		{			
			MLabeledEdge tmp = (MLabeledEdge) edges.elementAt(i);
			graph.addMEdge(tmp);
		}
		
		logger.info(Narrator.printGoed(graph.getRoot(), 0));
		
		removeMessages();
		
		logger.info(Narrator.printGoed(graph.getRoot(), 0));

		/*balanceTree(graph.getRoot());*/
		TreeBalancer tb = new TreeBalancer(graph);
		tb.balance(graph.getRoot());

		
		/*if(graph.getRoot() instanceof RhetRelation) finalTreeCheck((RhetRelation)graph.getRoot());*/

		logger.info("Transformed graph: \n" + Narrator.printGoed(graph.getRoot(), 0) + "\nGraph printed...");
		
//		System.exit(0);
		
		return true;
	}	
	
		
	/**
	 * Returns the starting node of the fabula, which is the node with the lowest
	 * time argument.
	 * @return the starting node
	 */
	private RDFNode getStartingNode()
	{		
		RDFNode start = null;
		int time = -1;
		
		// get all plot elements and select the one with the smallest time argument
			
		logger.info("\n\n*** PLOT ELEMENTS ***\n\n");
		
		ExtendedIterator stmtIt = model.listIndividuals(ResourceFactory.createProperty(NS_FABULA, "FabulaElement"));
		while(stmtIt.hasNext()){
			

			Resource r = (Resource)stmtIt.next();
			if(!alreadyTold(r)){
				int t = getTime(r);
				
				// the resource that has values for the property/predicate 
				// "causes" with the minimum time is selected to be returned
				if ((time == -1 || t < time) && r.isResource() && ((Resource)r).hasProperty(ResourceFactory.createProperty(NS_FABULA, "causes")))
				{
					time = t;
					start = r;
					logger.fine("even smaller: \n\t" + r + "\n\t" + time);

				}
			}
		}
		
		logger.info("\nmin time:   " + time + "  in case of: " + start + "\n\n*** END PLOT ELEMENTS ***\n\n");
			
		return start;
	}
	
	
	/**
	 * Converts the fabula structure into an initial document plan recursively. See 
	 * Nanda Slabbers' thesis 'Narration for Virtual Storytelling' (2006)
	 * (paragraph 6.2.2 Converting the fabula into an initial document plan, page 58-64)
	 * for a more in depth explanation. 
	 * @param curr the name of the node currently to be added to the document plan
	 * @param prev the name of the last node added to the document plan
	 * @param v the document plan generated so far
	 * @param forward a boolean indicating whether the tree is to be created forwards or backwards
	 * @return the document plan
	 */
	private RSVertex createTreeOwl(RDFNode curr, RDFNode prev, RSVertex v, boolean forward)
	{		
		
		logger.fine(((Resource)curr.as(Resource.class)).getLocalName());
		String localname = ((Resource)curr.as(Resource.class)).getLocalName();
		
		logger.info("CreateTree: " + localname); //getCorrectstring(curr, true));
		toldMessages.add(curr);
		
		Vector nextnodes = new Vector();
		Vector prevnodes = new Vector();

		try
		{
			// find next nodes
			logger.info("*******************************************\ncurr is resource: " + curr.isResource());

			Resource r = null;
			if(curr != null && curr.isResource()) {
				r = (Resource)curr;
				StmtIterator si = r.listProperties(ResourceFactory.createProperty(NS_FABULA, "causes"));
				logger.info(r + " caused rdfnodes...");
				while(si.hasNext()){
					Statement s = si.nextStatement();
					RDFNode causes = s.getObject();
					logger.info("\t* " + causes.toString());
				
					if(!alreadyTold(causes))
						insert(nextnodes, causes);
				}
			}
			
			logger.info("*******************************************");

			ResIterator ri = model.listSubjectsWithProperty(ResourceFactory.createProperty(NS_FABULA, "causes"), curr);
			while(ri.hasNext()){
				Resource rprev = ri.nextResource();
				logger.info("\twas caused by: " + rprev.getLocalName());
				// testing purposes:
				if(rprev.getLocalName().indexOf("Framing") >= 0)
					continue;
					
				if(!alreadyTold(rprev))
					insert(prevnodes, rprev);
			}
			
			logger.info("number of new next nodes in " + localname + ": " + nextnodes.size());
			logger.info("number of new prev nodes in " + localname + ": " + prevnodes.size());
			
			RSVertex result = v;
			
			if (forward) {
				if (prevnodes.size() == 0) {
					// do nothing at all
				}
				else if (prevnodes.size() == 1) {
					if (result != null) {
						// node is added to the document plan based on the time
						// arguments
						if (precedes((RDFNode) prevnodes.elementAt(0), result)) {
							RSVertex tree = createTreeOwl((RDFNode) prevnodes
									.elementAt(0), curr, null, false);
							if (tree != null)
								result = createRhetRel("additive", tree, result);
						}
						else {
							RSVertex tree = createTreeOwl((RDFNode) prevnodes
									.elementAt(0), curr, null, false);
							if (tree != null)
								result = createRhetRel("additive", result, tree);
						}
					}
					else
						result = createTreeOwl(
								(RDFNode) prevnodes.elementAt(0), curr, null,
								false);
				}
				else if (prevnodes.size() > 1) {
					RSVertex tmpresult = null;
					for (int i = prevnodes.size() - 2; i >= 0; i--) {
						if (i == prevnodes.size() - 2) {
							RSVertex tree1 = createTreeOwl((RDFNode) prevnodes
									.elementAt(i), curr, null, false);
							RSVertex tree2 = createTreeOwl((RDFNode) prevnodes
									.elementAt(i + 1), curr, null, false);
							if (tree1 != null && tree2 != null)
								tmpresult = createRhetRel("additive", tree1,
										tree2);
							else if (tree1 == null)
								tmpresult = tree2;
							else if (tree2 == null)
								tmpresult = tree1;
						}
						else {
							RSVertex tree = createTreeOwl((RDFNode) prevnodes
									.elementAt(i), curr, null, false);
							if (tree != null)
								tmpresult = createRhetRel("additive", tree,
										tmpresult);
						}
					}
					if (result != null) {
						if (tmpresult != null)
							result = createRhetRel("additive", result,
									tmpresult);
					}
					else
						result = tmpresult;
				}

				if (nextnodes.size() == 0) {
					if (result != null) {
						logger.warning("1. HIER!");
						String rel = getRelation(prev, curr);
						result = createRhetRel(rel, result,
								createPlotElement(curr));
					}
					else
						result = createPlotElement(curr);
				}
				else if (nextnodes.size() == 1) {
					if (result != null) {
						logger.warning("2. HIER!");
						String rel = getRelation(prev, curr);
						result = createRhetRel(rel, result,
								createPlotElement(curr));
						result = createTreeOwl(
								(RDFNode) nextnodes.elementAt(0), curr, result,
								true);
					}
					else
						result = createTreeOwl(
								(RDFNode) nextnodes.elementAt(0), curr,
								createPlotElement(curr), true);
				}
				else if (nextnodes.size() > 1) {
					RSVertex tmpresult = null;
					for (int i = nextnodes.size() - 2; i >= 0; i--) {
						if (i == nextnodes.size() - 2) {
							RSVertex tree1 = createTreeOwl((RDFNode) nextnodes
									.elementAt(i), curr, null, true);
							RSVertex tree2 = createTreeOwl((RDFNode) nextnodes
									.elementAt(i + 1), curr, null, true);
							if (tree1 != null && tree2 != null)
								tmpresult = createRhetRel(
										"temp-after-sequence", tree1, tree2);
							else if (tree1 == null)
								tmpresult = tree2;
							else if (tree2 == null)
								tmpresult = tree1;
						}
						else {
							RSVertex tree = createTreeOwl((RDFNode) nextnodes
									.elementAt(i), curr, null, true);
							if (tree != null)
								tmpresult = createRhetRel(
										"temp-after-sequence", tree, tmpresult);
						}
					}

					if (result != null) {
						logger.warning("3. HIER!");
						if (prev == null) {
							logger.warning("null");
						}
						System.out.println(prev + " - " + curr);
						String rel = getRelation(prev, curr);
						result = createRhetRel(rel, result,
								createPlotElement(curr));
						String rel2 = getRelation(curr, (RDFNode) nextnodes
								.elementAt(0));
						result = createRhetRel(rel2, result, tmpresult);
					}
					else {
						logger.warning("4. HIER!");
						String rel = getRelation(curr, (RDFNode) nextnodes
								.elementAt(0));
						result = createRhetRel(rel, createPlotElement(curr),
								tmpresult);
					}
				}
			}
			else // if backwards
			{
				RSVertex prevs = null;
				RSVertex nexts = null;

				// the new previous nodes are used to create all incoming
				// branches
				if (prevnodes.size() == 1)
					prevs = createTreeOwl((RDFNode) prevnodes.elementAt(0),
							curr, null, false);
				else if (prevnodes.size() > 1) {
					for (int i = prevnodes.size() - 2; i >= 0; i--) {
						if (i == prevnodes.size() - 2) {
							RSVertex tree1 = createTreeOwl((RDFNode) prevnodes
									.elementAt(i), curr, null, false);
							RSVertex tree2 = createTreeOwl((RDFNode) prevnodes
									.elementAt(i + 1), curr, null, false);
							if (tree1 != null && tree2 != null)
								prevs = createRhetRel("additive", tree1, tree2);
							else if (tree1 == null)
								prevs = tree2;
							else if (tree2 == null)
								prevs = tree1;
						}
						else {
							RSVertex tree = createTreeOwl((RDFNode) prevnodes
									.elementAt(i), curr, null, false);
							if (tree != null)
								prevs = createRhetRel("additive", tree, prevs);
						}
					}
				}

				// the new next nodes are used to create all outgoing branches
				if (nextnodes.size() == 1)
					nexts = createTreeOwl((RDFNode) nextnodes.elementAt(0),
							curr, null, true);
				else if (nextnodes.size() > 1) {
					for (int i = nextnodes.size() - 2; i >= 0; i--) {
						if (i == nextnodes.size() - 2) {
							RSVertex tree1 = createTreeOwl((RDFNode) nextnodes
									.elementAt(i), curr, null, true);
							RSVertex tree2 = createTreeOwl((RDFNode) nextnodes
									.elementAt(i + 1), curr, null, true);
							if (tree1 != null && tree2 != null)
								nexts = createRhetRel("temp-after-sequence",
										tree1, tree2);
							else if (tree1 == null)
								nexts = tree2;
							else if (tree2 == null)
								nexts = tree1;
						}
						else {
							RSVertex tree = createTreeOwl((RDFNode) nextnodes
									.elementAt(i), curr, null, true);
							if (tree != null)
								nexts = createRhetRel("temp-after-sequence",
										tree, nexts);
						}
					}
				}

				// then the sofar generated trees are connected in the correct
				// order
				if (prevs != null) {
					logger.warning("5. HIER!");
					String rel = getRelation((RDFNode) prevnodes
							.elementAt(prevnodes.size() - 1), curr);
					result = createRhetRel(rel, prevs, createPlotElement(curr));
				}
				if (nexts != null) {
					logger.warning("6. HIER!");
					String rel = getRelation(curr, (RDFNode) nextnodes
							.elementAt(0));
					if (result != null)
						result = createRhetRel(rel, result, nexts);
					else
						result = createRhetRel(rel, createPlotElement(curr),
								nexts);
				}
				if (result == null)
					result = createPlotElement(curr);
			}

			
			return result;
		}
		catch (Exception e) {
			logger.warning(e.getMessage() + " - " + e.getClass() + '\n' + curr + '\n' + "error querying knowledge base");
		}

		return null;
	}
		
	/**
	 * Checks whether plot element n happens before all plot elements used in v
	 * @param n the name of the current plot element
	 * @param v the branch that should appear before or after n
	 * @return whether n precedes all plot elements in v
	 */		
	private boolean precedes(RDFNode n, RSVertex v)
	{
		
		int tn = getTime(n);
		
		int t1 = getRightmostTime(v);
		int t2 = getLeftmostTime(v);
		
		if (tn < t1 && tn < t2)
			return true;
		
		return false;
	}
	
	/**
	 * Gets the time of the rightmost plot element in v
	 * @param v
	 * @return the time
	 */
	private int getRightmostTime(RSVertex v)
	{
		if (v.getType().equals("mess"))
			return ((PlotElement) v).getTime();
		else if (v.getType().equals(("rhetrel")))
			getRightmostTime(((RhetRelation) v).getNucleus());
		return -1;
	}
	
	/**
	 * Gets the time of the leftmost plot element in v
	 * @param v
	 * @return the time
	 */
	private int getLeftmostTime(RSVertex v)
	{
		if (v.getType().equals("mess"))
			return ((PlotElement) v).getTime(); 
		else if (v.getType().equals(("rhetrel")))
			getLeftmostTime(((RhetRelation) v).getNucleus());
		return -1;
	}
	
	/**
	 * Inserts n into v such that the elements in v are ordered chronologically
	 * @param v vector containing names of plot elements
	 * @param n the name of the plot element to be inserted into v
	 */
	private void insert(Vector v, RDFNode n)
	{		

		int newtime = getTime(n);
		
		int idx = 0;
		while(idx < v.size())
		{					
			int oldtime = getTime((RDFNode)v.elementAt(idx));
			
			if (newtime < oldtime)
				break;
			else 
				idx++;
		}

		v.add(idx, n);
	}
	
	/**
	 * Returns the time this FabulaElement (as RDFNode) occurred.  
	 * @param n the RDFNode of which the time should be returned
	 * @return the time on which n occurred
	 */
	private int getTime(RDFNode n) {
		Statement s = model.getProperty((Resource)n, ResourceFactory.createProperty(NS_FABULA, "time"));
		if (s != null)
			return s.getInt();
		else return -1;
	}
	
	/**
	 * Returns the type of the plot element. 
	 * 
	 * @param n the RDFNode of which the type is requested
	 * @return type the type of n
	 */
	private String getType(RDFNode n) {
		Resource r = model.getOntResource(n.toString()).getRDFType(true);

		String name = r.getLocalName();
		logger.info("@@@ "+n+" is of TYPE: " + name);

		return name;
	}
	
	
	/**
	 * Creates a Plot Element given the RDFNode n.
	 * @param n the RDFNode from which a Plot Element should be constructed
	 * @return a plot element structure that can be understood by the microplanner
	 */
	private PlotElement createPlotElement(RDFNode n)
	{
		String type = "";
		String name = "";
		String agens = "";
		String patiens = "";
		String target = "";
//		Vector<Instrument> instruments = new Vector<Instrument>();
		Vector subargs = new Vector();
		int time = -1;
		boolean success = true;
		boolean addnot = false;

		time = getTime(n);				

		logger.info("createPlotElement: " + n + "\ntime" + n + "..." + time);

		// RZ: name is the type, and type is the class of the instance
		if(isSubclassOf(n, FE_ACTION)){
			type = "action";
			name = getType(n);
			
		}
		else if(isSubclassOf(n, FE_EVENT)){
			type = "event";
			name = getType(n);
		}
		else if(isSubclassOf(n, FE_GOAL)){
			
			System.out.println("It's a goal!");
			if(isSubclassOf(n, FE_ATTAIN_GOAL)){
				type = "attaingoal";
				logger.info("It's a attain goal!");
			}
			else if(isSubclassOf(n, FE_AVOID_GOAL)){
				type = "avoidgoal";
				logger.info("It's a avoid goal!");
			}
			else if(isSubclassOf(n, FE_LEAVE_GOAL)){
				type = "leavegoal";
				logger.info("It's a leave goal!");
			}
			else if(isSubclassOf(n, FE_SUSTAIN_GOAL)){
				type = "sustaingoal";
				logger.info("It's a sustain goal!");
			}
			else
				type = "goal";
			name = getType(n);
			logger.info("Name:" + name);
//			System.exit(0);
		}
		else if(isSubclassOf(n, FE_INTERNAL_ELEMENT)){
			type = "belief";
			name = getType(n);
		}
		else if(isSubclassOf(n, FE_OUTCOME)){
			type = "outcome";
			name = getType(n);
		}
		else if(isSubclassOf(n, FE_PERCEPTION)){
			type = "perception";
			name = "see";
		}
		else if(isSubclassOf(n, FE_SETTING_ELEMENT)){
			logger.warning(" SETTING ELEMENT !!! ");
			type = "setting";
			name = "be";
		}
		// old was:
//		name = getCorrectstring(n);
//		type = getType(n);		
		if (type.equals("belief"))
			name = "think";
		else if (type.indexOf("Goal") > 0)
			name = "want";
		else if (type.equals(""))
			type = getLocalName(n);
				
			
			
			
		// sets the arguments of the plot element
		agens = getArgument("agens", n);
		if (agens.equals(""))
			agens = getArgument("character", n);
		patiens = getArgument("patiens", n);
		target = getArgument("target", n);
		Instrument instr = getInstrument(n);
//		if(instr != null && !instr.equals("")) {
//			instruments.add(instr);
//		}
		
		
		
		if (getArgument("isSuccessful", n).equals("false"))
			success = false;
		if (getArgument("isNonperception", n).equals("true"))
			addnot = true;
		if (getArgument("isNonbelief", n).equals("true"))
			addnot = true;
					
		subargs = getContent(n);
		
		if(subargs.size() > 0 && isSubclassOf((RDFNode)subargs.elementAt(0), FE_FALSEHOOD_GRAPH)){
			addnot = true;
//			subargs.remove(0);
			logger.info("@-&*--" + subargs);
//			if(!isSubclassOf(n, FE_PERCEPTION) && !isSubclassOf(n, FE_INTERNAL_ELEMENT))
//				System.exit(0);
		}
		if(subargs.size() > 0 && isSubclassOf((RDFNode)subargs.elementAt(0), FE_TRUTH_GRAPH)){
			addnot = false;
			if(type.equals("setting")) {
				logger.info("@-&*--" + subargs);
				
//				System.exit(0);
			}
//			subargs.remove(0);
//			if(!isSubclassOf(n, FE_PERCEPTION) && !isSubclassOf(n, FE_INTERNAL_ELEMENT))
				
		}
		
		
		
		// if the vector subargs has any elements create the sub plot element
		// if there is exactly one element, simply call this function recursively
		// if the vector contains more elements call the function createSubplotelement 
		PlotElement subpe = null;
		if (subargs.size() == 1){
//			subpe = createPlotElement((RDFNode) subargs.elementAt(0));
			if(type.equals("setting")){
				subpe = createRZSubPlotElement((RDFNode) subargs.elementAt(0), "setting");
				name = subpe.getName();
				agens = subpe.getAgens();
				patiens = subpe.getPatiens();
				target = subpe.getTarget();
				logger.info(name + " " + agens + " " + patiens + " " + target);
//				System.exit(0);
//				PlotElement oldresult = result;
//				result = new PlotElement(type, name, agens, patiens, target, instruments);
//				result.setTime(time);
//				result.setDescr(oldresult.getDescr());
				subpe = null;
			}
			else 
				subpe = createRZSubPlotElement((RDFNode) subargs.elementAt(0), "state");
		}
		else if (subargs.size() > 1)
			subpe = createSubPlotElement(subargs);
		
		logger.info("\nNew plot element: \ntype: " + type + "\nname: " + name + "\nagens " + agens + "\npatiens: " + patiens + "\ntarget: " + target + "\ninstruments: " + instr + "\nsubmess: " + (subargs.size() > 0) + "\n");
		
		// create the plot element
		PlotElement result = new PlotElement(type, name, agens, patiens, target, instr);
		result.setTime(time);
		
		// if the property isSuccesful is set to false, set the descriptor
		// to 'failed' (the microplanner can now select the correct template)
		if (!success)
			result.setDescr("failed");
			
				
		// if the boolean isNonperception or isNonbelief is set to true, add the
		// modifier not to the details vector of the plot element
		if (addnot)
			result.addDetail(new Detail(name, "not"));
		
		
		
		// set the sub plot element
		if (subpe != null)
			result.setSubElement(subpe);
		
		
		return result;
		
	}
	
	/**
	 * Returns all content graphs belonging to the RDFNode n as a Vector of RDFNodes.
	 * @param n the RDFNode of which the content graphs should be returned 
	 * @return a vector of content graphs belonging to RDFNode n
	 */
	private Vector<RDFNode> getContent(RDFNode n){
		Vector<RDFNode> result = new Vector<RDFNode>();
		OntResource or = (OntResource)n.as(OntResource.class);
		StmtIterator sit = or.listProperties(ResourceFactory.createProperty(NS_FABULA, "hasContent"));
		while(sit.hasNext()){
			result.add(sit.nextStatement().getResource());
		}
		return result;
	}
	
	/**
	 * Creates a sub plot element given n, a content graph. 
	 * @param n the content graph from which the sub plot element should be created.
	 * @param type the type of the sub plot element
	 * @return the sub plot element
	 */
	private PlotElement createRZSubPlotElement(RDFNode n, String type){
		logger.info(" createRZSubPlotElement: " + getLocalName(n));
		OntResource or = (OntResource)n.as(OntResource.class);
		
		NamedGraph ng = graphset.getGraph(or.getURI());
		
		ExtendedIterator ei = ng.find(null, null, null);
		while(ei.hasNext()){
			com.hp.hpl.jena.graph.Triple t = (com.hp.hpl.jena.graph.Triple)ei.next();
//			System.out.println(o.getClass());
			
//			String type = "state";
			String name = t.getPredicate().getLocalName();
			String agens = t.getSubject().getLocalName();
			String patiens = t.getObject().getLocalName();
			String target = "" ;
			
			Vector subargs = new Vector();
			
			logger.info("\nNew SUB plot element: \ntype: " + type + "\nname: " + name + "\nagens: " + agens + "\npatiens: " + patiens + "\ntarget: " + target + "\ninstruments: " + null + "\nsubmess: " + (subargs.size() > 0) + "\n");
			PlotElement result = new PlotElement(type, name, agens, patiens, target, null);
			return result;
		}
		return null;
		
	}
	
	/**
	 * Retrieves an argument of a plot element
	 * @param kind the type of argument (e.g. agens, patiens..)
	 * @param plotel the current plot element
	 * @return the value of the argument
	 */
	private String getArgument(String kind, RDFNode plotel)
	{
//		System.err.println(" GET ARGUMENT: " + kind + " <--> " + plotel);
		try
		{
			Statement s = model.getProperty((Resource)plotel, ResourceFactory.createProperty(NS_FABULA, kind));
			
			if(s != null) {
				logger.info(" Statement with object " + s.getObject() + "\n is a resource: " + s.getObject().isResource());
				return getLocalName(s.getObject());
				
			}
			else return "";			
			
		}
		catch (Exception e) 
		{
			logger.warning("error creating plotelement: \n" + e.getMessage());

			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Retrieves an instrument of a plot element
	 * @param kind the type of argument (e.g. agens, patiens..)
	 * @param plotel the current plot element
	 * @return the value of the argument
	 */
	private Instrument getInstrument(RDFNode plotel)
	{
		Statement s = model.getProperty((Resource)plotel, ResourceFactory.createProperty(NS_FABULA, "instrument"));
		if(s != null) 
			return new Instrument(s.getObject());
		else return null;
	}
	
	// Ervan uitgaan dat altijd de volgende dingen gelden (niet helemaal zeker?):
	// - Vector args heeft precies twee elementen
	// - er bestaat precies één relatie tussen die elementen
	// - dit kan altijd verwoord worden door een setting node met de relatie als name en de argumenten als agens en target
	// (Voorbeeld: Plop dacht 'dat er een appel in huis lag')
	/**
	 * Creates a sub plot element based on the elements in the Vector. The function
	 * first finds the relation that holds between the arguments and then creates
	 * a settings plot element for this relation and its arguments.
	 * 
	 */
	private PlotElement createSubPlotElement(Vector args)
	{
		logger.info("createSubPlotElement: " + args);
		System.exit(0); // if a subplotelement is created, the program will end
		// of course this is solely for testing purposes
		// TODO: fix this
//		
//		String type = "setting";
//		String name = "";
//		String patiens = "";
//		String agens = "";
//		String target = "";
//		Vector instruments = new Vector();
//		
//		try
//		{	
//			ReasoningStepIterator rsi = context.ask("(holds ?rel " + args.elementAt(0) + " " + args.elementAt(1) + ")");
//			ReasoningStep rs = null;				
//			if ((rs = rsi.next()) != null)
//			{
//				Literal lit = SubstUtils.deReferenceLiteral((Literal) rs.getGoal());
//				name = getCorrectstring((Symbol) lit.getArgs().get(0), false);
//				agens = getCorrectstring((Symbol) lit.getArgs().get(1), true);
//				target = getCorrectstring((Symbol) lit.getArgs().get(2), true);
//			}
//			
//			// in case the elements are added to the vector in the incorrect order
//			rsi = context.ask("(holds ?rel " + args.elementAt(1) + " " + args.elementAt(0) + ")");
//			rs = null;				
//			if ((rs = rsi.next()) != null)
//			{
//				Literal lit = SubstUtils.deReferenceLiteral((Literal) rs.getGoal());
//				name = getCorrectstring((Symbol) lit.getArgs().get(0), false);
//				agens = getCorrectstring((Symbol) lit.getArgs().get(1), true);
//				target = getCorrectstring((Symbol) lit.getArgs().get(2), true);
//			}
//		}
//		catch (Exception e) 
//		{
//			System.out.println("error creating subplotelement");
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}	
//		
//		return new PlotElement(type, name, agens, patiens, target, instruments);
		return null;
	}
			
	/**
	 * This function returns the localname of the given resource. This
	 * name is used in plot elements. 
	 * 
	 * Example:
	 * 
	 * the local name of &lt;http://www.owl-ontologies.com/StoryWorldCore.owl#contains&gt;
	 * is 'contains'
	 *   
	 * @param n the RDFNode of which the local name should be returned
	 * @return the local name of the RDFNode n
	 */
	private String getLocalName(RDFNode n){
		return ((Resource)n.as(Resource.class)).getLocalName();
	}
	
	/**
	 * Removes plot elements from the tree that are not interesting to tell.
	 * First the function transformDepthFirst(...) is called, which returns
	 * a vector with all the Plot Elements to be removed. Then each plot
	 * element contained by this vector is removed.  
	 */
	private void removeMessages()
	{
		Vector<PlotElement> removemess = new Vector<PlotElement>();
		if(graph.getRoot() != null)
			removemess = transformDepthFirst(graph.getRoot(), new Vector<PlotElement>());
		
		for (int i=0; i<removemess.size(); i++)		
			removeMessage(removemess.elementAt(i));
	}
	
	/**
	 * Checks the document plan recursively in order to find out which plot elements should be removed.
	 * Plot Elements are added to the Vector to be returned in the following cases (those marked are removed):
	 * <ul>
	 *  <li><b>outcomes</b></li>
	 *  <li>a <b>perception</b> followed by a <b>belief</b></li>
	 *  <li>a setting followed by a <b>belief</b></li>
	 * </ul>
	 * 
	 * @param v the document plan
	 * @param rem the nodes to be removed
	 * @return a vector containing the names of the nodes to be removed
	 */	
	private Vector<PlotElement> transformDepthFirst(RSVertex v, Vector<PlotElement> rem)
	{
		if (v.getType().equals("mess"))
		{
			boolean print = false;
			
			PlotElement pe = (PlotElement) v;
			String type = pe.getKind();
			
			String prevtype = "";
			if (prevpe != null)
				prevtype = prevpe.getKind();
			
			String prevprevtype = "";
			if (prevprevpe != null)
				prevprevtype = prevprevpe.getKind();
			
			if (print) logger.info("RZ: TDF: " + type + " - " + prevtype + " - " + prevprevtype);
			
			if (type.equals("outcome"))
				rem.add(pe);
			
			else if (type.equals("belief") && prevtype.equals("perception")) // && prevprevtype.equals("action"))
			{
				rem.add(prevpe);
				rem.add(pe);
			}
			
			
			
			else if (
//					type.equals("setting") && prevtype.equals("belief") )
////					||
					type.equals("belief") && prevtype.equals("setting"))
			{
				rem.add(prevpe);
				rem.add(pe);
			}
			
			prevprevpe = prevpe;
			prevpe = pe;
		}
		else if (v.getType().equals("text"))
		{
		}
		else if (v.getType().equals("rhetrel"))
		{
			RSVertex sat = ((RhetRelation) v).getSatellite();
			RSVertex nuc = ((RhetRelation) v).getNucleus();
			
			if (sat != null)
				transformDepthFirst(sat, rem);
			if (nuc != null)
				transformDepthFirst(nuc, rem);			
		}
		
		return rem;
	}
	
	/**
	 * This function removes the given PlotElement from the tree.
	 * There are 6 posibilities with three solutions:
	 * 1.	sat
	 * 			*nuc*
	 * 			*sat*
	 * 
	 * 2.	nuc
	 * 			*nuc*
	 * 			*sat*
	 * 
	 * solution for each case: if one of two starred vertices is removed,
	 * the content of the other vertice becomes the content of its parent.
	 * 
	 * 3.	sat
	 * 			*sat*
	 * 	
	 * 4.	sat
	 * 			*nuc*
	 * 
	 * solution for each case: if starred vertice is removed, delete parent also.
	 * 
	 * 5.	nuc
	 * 			*sat*
	 * 	
	 * 6.	nuc
	 * 			*nuc*
	 * 
	 * solution for each case: rename the other child of the parent of the 
	 * parent of the starred vertice (the one to be removed) to 'nuc', and then
	 * remove parent along with the starred vertice
	 * 
	 * @author R. Zeeders
	 * @param pe the node to be removed 
	 */	
	private void removeMessage(PlotElement pe)
	{
		// pe is element to be removed
		// pe_edge is the edge leading to pe (either nuc or sat)
		// par is parent of pe
		// othernode is the other child of par
		// parpar is the parent of par
		// parothernode is the the parent's sibbling
		
		boolean print = false;
		
		if (print) logger.info("getting par...");
		RhetRelation par =(RhetRelation) pe.getParent().next();
		
		// relation between par is either nucleus or satellite
		// if nucleus, then par is source of edge
		// if satellite, then par is target of edge
		
		MLabeledEdge pe_edge = getParentEdge(pe, par);
		
		String label = pe_edge.getLabel();
		RSVertex othernode = label.equals("nucleus") ? par.getSatellite() : par.getNucleus();
		
		MLabeledEdge othernode_edge = null;
		if(othernode != null)
			othernode_edge = getParentEdge(othernode, par);
		
		
		if (print) logger.info("getting parpar...");
		RhetRelation parpar = (RhetRelation) par.getParent().next();
		MLabeledEdge par_edge = getParentEdge(par, parpar);
		String parlabel = par_edge.getLabel();

		RSVertex parothernode = parlabel.equals("nucleus") ? parpar.getSatellite() : parpar.getNucleus();
		MLabeledEdge parothernode_edge = null;
		if(parothernode != null)
			parothernode_edge = getParentEdge(parothernode, parpar);
		
		String parothernodelabel = null;
		if (parothernode != null && parothernode.getType().equals("rhetrel"))
			parothernodelabel = ((RhetRelation) parothernode).getLabel();
		else if (parothernode != null)
			parothernodelabel = ((PlotElement) parothernode).getName();
		
		// solution 1 & 2
		// par.content = othernode.content
		// delete pe
		if(othernode != null){
			if (print) {
				StringBuilder sb = new StringBuilder();
				sb.append("Solution 1 & 2").append('\n');
				sb.append("	" + ((RhetRelation) parpar).getLabel()).append('\n');
				sb.append("		\\---"+par_edge.getLabel()+"---> " + ((RhetRelation) par).getLabel()).append('\n');
				sb.append("					\\---"+pe_edge.getLabel()+"---> " + pe).append('\n');
				if(othernode != null )
					sb.append("					\\---"+othernode_edge.getLabel()+"--->" + othernode).append('\n');
				if(parothernode != null )
					sb.append("		\\---"+parothernode_edge.getLabel()+"---> " + parothernode).append('\n');
				
				logger.info(sb.toString());
			}			
			

			if (print) logger.info(" --> " + othernode_edge.getLabel());
			if(par_edge.getLabel().equals("nucleus")){
				par_edge.setTarget(othernode);

			}
			else {
				par_edge.setSource(othernode);

			}
			
			graph.removeMVertex(pe);
			graph.removeMVertex(par);
			graph.removeMEdge(pe_edge);
			graph.removeMEdge(othernode_edge);

			
		}
		else{
			// solution 3 & 4
			if(par_edge.getLabel().equals("satellite")){
				if (print) {
					StringBuilder sb = new StringBuilder();
					sb.append("Solution 3 & 4").append('\n');
					sb.append("	" + ((RhetRelation) parpar).getLabel()).append('\n');
					sb.append("		\\---"+par_edge.getLabel()+"---> " + ((RhetRelation) par).getLabel()).append('\n');
					sb.append("					\\---"+pe_edge.getLabel()+"---> " + pe).append('\n');
					sb.append("		\\---"+(parlabel.equals("nucleus") ? "satellite" : "nucleus")+"---> " + parothernodelabel).append('\n');
					logger.info(sb.toString());
				}
//				parpar.disconnectOutMEdge(par_edge);
//				
				graph.removeMVertex(par);
				graph.removeMVertex(pe);
				graph.removeMEdge(pe_edge);
				graph.removeMEdge(par_edge);
			}
			else{	// solution 5 & 6
				if (print) {
					StringBuilder sb = new StringBuilder();
					sb.append("Solution 5 & 6").append('\n');
					sb.append("	" + ((RhetRelation) parpar).getLabel()).append('\n');
					sb.append("		\\---"+par_edge.getLabel()+"---> " + ((RhetRelation) par).getLabel()).append('\n');
					sb.append("					\\---"+pe_edge.getLabel()+"---> " + pe).append('\n');
					sb.append("		\\---"+(parlabel.equals("nucleus") ? "satellite" : "nucleus")+"---> " + parothernodelabel).append('\n');
					logger.info(sb.toString());
					
					logger.info(parothernodelabel);
				}
				Iterator it = parothernode.getIncidentMEdges();
				while(it.hasNext()){
					if (print) logger.info(" - " + ((MLabeledEdge)it.next()).getLabel());
				}
				if (print) logger.info("<end of list>");
//				MLabeledEdge parothernode_edge = (MLabeledEdge)parothernode.getIncidentInMEdges().next();
//				
////				parothernode_edge.setLabel("nucleus");
//				parothernode.connectInMEdge(par_edge);
//				par.disconnectInMEdge(par_edge);
//				graph.removeMVertex(par);
//				graph.removeMVertex(pe);
//				graph.removeMEdge(pe_edge);
			}
		}
		
	}
	
	/**
	 * Gives the edge between the given element and its parent.
	 * Relation between par and el is either nucleus or satellite. 
	 * If nucleus, then par is source of edge.
	 * if satellite, then par is target of edge.
	 * 
	 * @param el the child of par
	 * @param par the parent of el
	 * @return the edge between par and el
	 */
	private MLabeledEdge getParentEdge(RSVertex el, RSVertex par){
		MLabeledEdge edge = null;
		Iterator edges_it = el.getIncidentMEdges();
		while(edges_it.hasNext()){
			MLabeledEdge e = (MLabeledEdge) edges_it.next();
			if(e.getLabel().equals("satellite") && e.getTarget() == par)
				edge = e;
			else if (e.getLabel().equals("nucleus") && e.getSource() == par)
				edge = e;
		}
		return edge;
	}
	
	
	
		
	/**
	 * Retrieves the relation between two plot elements from the fabula structure.
	 * Momentarily these fabula relations are translated to these rethorical relations:
	 * <ul>
	 *  <li>psi_causes, motivates --> cause-voluntary</li>
	 *  <li>phi_causes, enables --> cause-nonvoluntary</li>
	 * </ul>
	 * 
	 * If the relation is not one of the above, the rethorical relation 'temporal'
	 * is returned.
	 * 
	 * @param m1 name of the first plot element
	 * @param m2 name of the second plot element
	 * @return the relation between m1 and m2
	 */
	private String getRelation(RDFNode n1, RDFNode n2)
	{
		
		logger.info("get relation: " + getLocalName(n1) + " - " + getLocalName(n2));
		try
		{
			StmtIterator stmtIt = model.listStatements((Resource)n1, null, n2);
			
			while(stmtIt.hasNext()){
				Statement s = stmtIt.nextStatement();
				String rel = getLocalName(s.getPredicate());
				
				System.out.println(rel);
				
				if (rel.equals("psi_causes") || rel.equals("motivates")){
					logger.info("relatie: " + rel);
					return "cause-voluntary";
				}
				else if (rel.equals("phi_causes") || rel.equals("enables")){
					logger.info("relatie: " + rel);
					return "cause-nonvoluntary";
				}
			}
		}
		catch (Exception e)
		{
			logger.severe("error querying knowledge base");
		}		
		
		// if no relation is specified: return temporal
		logger.warning("no relation specified");
		return "temporal";
	}
		
	/**
	 * Checks whether the plot element has already been added to the document plan.
	 * @param n the name of the plot element
	 * @return true if m has been added to the document plan, false otherwise
	 */	
	private boolean alreadyTold(RDFNode n)
	{
		return toldMessages.contains(n);
	}

	/**
	 * Creates RhetRelation node with two child nodes
	 * @param rel kind of relation
	 * @param vert1 satellite
	 * @param vert2 nucleus
	 * @return the RhetRelation node of type rel and with children vert1 and vert2
	 */
	private RhetRelation createRhetRel(String rel, RSVertex vert1, RSVertex vert2)
	{
		logger.info(" CREATERHETREL: " + rel + '\n' + " ver1: " + vert1 + '\n' + " ver2: " + vert2 + '\n');
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge sat = new RSEdge("satellite");
		sat.setTarget(rhetrel);
		sat.setSource(vert1);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert2);
					
		edges.addElement(sat);
		edges.addElement(nuc);
		
		return rhetrel;
	}
	
	/**
	 * Creates RhetRelation node with only one child node
	 * @param rel kind of relation
	 * @param vert1 the nucleus
	 * @return the RhetRelation node of type rel and with child vert1
	 */
	private RhetRelation createRhetRel(String rel, RSVertex vert1)
	{
		logger.info(" CREATERHETREL: " + rel + "\n ver1: " + vert1);
		
		RhetRelation rhetrel = new RhetRelation(rel);
		
		RSEdge nuc = new RSEdge("nucleus");
		nuc.setSource(rhetrel);
		nuc.setTarget(vert1);
									
		edges.addElement(nuc);
		
		return rhetrel;
	}
	
	/**
	 * Returns the document plan
	 * @return document plan
	 */
	public RSGraph getGraph()
	{
		return graph;
	}
	
	/**
	 * Sets the graph (document plan)
	 * @param g 
	 */
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	/**
	 * Resets the document plan 
	 *
	 */
	public void setGraph()
	{
		graph = null;
	}
	
		
	/**
	 * Returns true if RDFNode 'n' is a subclass of 'superclass'. 
	 * This means that 'n' is itself a class, and not an instance.
	 * @param n the class for which is checked whether it's a subclass of the given superclass
	 * @param superclass the URI of the superclass for which is checked whether it's a superclas of n
	 * @return true if 'n' is a subclass of 'superclass', false otherwise. 
	 */
	public static boolean isSubclassOf(RDFNode n, String superclass){
		OntResource nr = (OntResource)n.as(OntResource.class);
		
		return nr.hasRDFType(superclass);
	}
	
	
	
}