package narrator.documentplanner;

import java.util.Iterator;
import java.util.logging.Logger;

//import natlang.debug.LogFactory;
import natlang.rdg.libraries.LibraryConstants;
import natlang.rdg.model.RSGraph;
import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;
import parlevink.parlegraph.model.MLabeledEdge;

/**
 * This class is responsible for balancing the tree. The algorithm is as follows:
 * 
 * S: the collection of the stop nodes, that is the nodes that are of a certain kind that cannot be broken up
 * for example the voluntary cause: something is causing something else. This relation should not be broken.
 * 
 * R: the collection of all rhethorical nodes (all internal nodes)
 * 
 * P: the collection of all plot elements (all leafs)
 * 
 * root: a node being the root of a certain (sub)tree, root elementOf R
 * 
 * nuc: the nucleus of a certain node
 * 
 * sat: the satellite of a certain node
 * 
 * graph: the complete tree, represented as a connected acyclic graph
 * 
 * algorithm:
 * 
 * 
 *   root = graph.root
 *   
 *   FUNCTION balance(root)
 *     IF root elementOf S THEN
 *	     // do nothing
 *     ELSE IF depth(root.nuc) > depth(root.sat) THEN
 *       root = turnTreeToSat(root, root.par,  (depth(root.nuc) - depth(root.sat))/2))
 *     else
 *       root = turnTreeToNuc(root, root.par, (depth(root.sat) - depth(root.nuc))/2))
 *     
 *     IF root.nuc elementOf R
 *       balance(root.nuc)
 *     IF root.sat elementOf R
 *       balance(root.sat)
 *     
 *       
 *   FUNCTION depth(node)
 *     IF node elementOf P
 *       return 0
 *     else
 *       return max(depth(node.nuc), depth(node.sat))
 *       
 *   FUNCTION turnTreeToSat(root, par, nrOfTimes)
 *     IF nrOfTimes > 0 AND root.nuc !elementOf S
 *       n = root.nuc
 *       ns = root.nuc.sat
 *     
 *       root.parent = n
 *       n.sat = root
 *       root.nuc = ns
 *       ns.par = root
 *       
 *       return turnTreeToSat(n, par, nrOfTimes)
 *     ELSE
 *       root.par = par
 *       par.nuc = root
 *       return root
 *     
 *     
 *      
 * 
 * 
 * 
 * @author zeeders
 *
 */

// a P.S.
// the relation between a rethorical relation and its children is:
//          ,-> PAR -.
//         /          \
//        /            \
//       SAT            `-> NUC
public class TreeBalancer {

	
	private int counter = 1;
	private RSGraph graph;
	//private Logger logger;
	
	public TreeBalancer(RSGraph graph){
		this.graph = graph;
		//logger = LogFactory.getLogger(this);
	}
	
	public void balance(RSVertex root){
		//logger.info("Balancing tree...");
		if(root instanceof RhetRelation){
			/*RhetRelation rRoot =*/ 
			balanceTree((RhetRelation)root);
			finalTreeCheck((RhetRelation)graph.getRoot());
			//logger.info("finished result: \n" + Narrator.printGoed(graph.getRoot(), 0));

//			System.exit(0);
		}
	}
	
	private boolean isStopNode(RhetRelation n){
		if ( n.getLabel().equals("cause-voluntary")
				|| n.getLabel().equals("cause-nonvoluntary") )
			return true;
		else 
			return false;
	}
	
	
	
	/**	
	 * TODO: Kijken naar het feit dat de recursieve aanroep van het balanceren van 
	 * de kinderen zowel voor als na het balanceren van de huidige node gebeurd.
	 * 
	 * 	FUNCTION balance(root)
	 *     IF root elementOf S THEN
	 *	     // do nothing
	 *     ELSE IF depth(root.nuc) > depth(root.sat) THEN
	 *       root = turnTreeToSat(root, root.par,  (depth(root.nuc) - depth(root.sat))/2))
	 *     else
	 *       root = turnTreeToNuc(root, root.par, (depth(root.sat) - depth(root.nuc))/2))
	 *     
	 *     IF root.nuc elementOf R
	 *       balance(root.nuc)
	 *     IF root.sat elementOf R
	 *       balance(root.sat)
	 */
	private void balanceTree(RhetRelation root){
		
		// Safety check
		if (counter > 100) {
			//logger.severe("Something wrong in the algorithm's recursion; stopping.");
			return;
		}
		
		if(root.getSatellite() instanceof RhetRelation){
			//logger.info(" in round " + counter + " balancing satellite");
			balanceTree((RhetRelation)root.getSatellite());
			
		}
		if(root.getNucleus() instanceof RhetRelation){
			//logger.info(" in round " + counter + " balancing nucleus");
			balanceTree((RhetRelation)root.getNucleus());
		}
		
		//logger.info( "run " + counter++ );
		//logger.info("rootnode: " + root.getLabel());
		
		int totalsize = depthSize(root);
		int nucsize = depthSize(root.getNucleus());
		int satsize = depthSize(root.getSatellite());
		
		//logger.info("total size: " + totalsize);
		//logger.info("size nuc: " + nucsize);
		//logger.info("size sat: " + satsize);
		
		if(isStopNode(root) || nucsize == satsize){
			//logger.info( "either the current node is a stop node or nucsize == satsize");
		}
		else{
			RhetRelation par = null;
//			boolean isNucleus = false;
			if(root.getParent().hasNext()){
				par = (RhetRelation)root.getParent().next();
//				if(par.getNucleus() == root)
//					isNucleus = true;
			}
			
			if(nucsize > satsize)
				root = turnTreeToSat(root, par, (nucsize - satsize)/2);
			else
				root = turnTreeToNuc(root, par, (satsize - nucsize)/2);
			
		//	logger.info("after turning: \nsize nuc: " + depthSize(root.getNucleus()) + "\nsize sat: " + depthSize(root.getSatellite()));
			
//			if(par != null){
//				if(isNucleus)
//					getNucleusEdge(par).setSource(root);
//				else
//					getSatelliteEdge(par).setTarget(root);
//			}
		}
			
		//logger.info("Tree turned, result: ");
		//logger.info(Narrator.printGoed(graph.getRoot(), 0));
		
		//logger.info("root: " + root);
		
		
		if(root.getSatellite() instanceof RhetRelation){
			//logger.info(" in round " + counter + " balancing satellite");
			balanceTree((RhetRelation)root.getSatellite());
			
		}
		if(root.getNucleus() instanceof RhetRelation){
		//	logger.info(" in round " + counter + " balancing nucleus");
			balanceTree((RhetRelation)root.getNucleus());
		}
		
			
//		return root;
	}
	
	/**
	 * 
	 * check all relationships and alter the following:
	 *  - translate 'temporal' to 'temp-after-sequence'
	 *  - if a parent has one child (a nuc), replace the parent
	 *    with the child
	 *    
	 */
	private void finalTreeCheck(RhetRelation root){
				
		if(root.getLabel().equals("temporal"))
			root.setLabel("additive");
		
		if(root.getSatellite() == null || root.getNucleus() == null){
			RSVertex nuc = root.getNucleus();
			RSVertex sat = root.getSatellite();
			RhetRelation par = (root.getParent().hasNext() ? (RhetRelation)root.getParent().next() : (RhetRelation)null);
			MLabeledEdge paredge = null;
			MLabeledEdge nucedge = getNucleusEdge(root);
			MLabeledEdge satedge = getSatelliteEdge(root);
			
			if(par != null) {
				paredge = getParentEdge(root, par);
				
				
				if(paredge.getLabel().equals(LibraryConstants.SATELLITE))
					paredge.setSource(nuc != null ? nuc : sat);
				else
					paredge.setTarget(nuc != null ? nuc : sat);
				
			}
			graph.removeMEdge(nuc != null ? nucedge : satedge);
			graph.removeMVertex(root);
			if(nuc != null && nuc instanceof RhetRelation) 
				finalTreeCheck((RhetRelation) nuc);
			if(sat != null && sat instanceof RhetRelation) 
				finalTreeCheck((RhetRelation) sat);
		}
		else {
			if(root.getNucleus() instanceof RhetRelation) finalTreeCheck((RhetRelation)root.getNucleus());
			if(root.getSatellite() instanceof RhetRelation) finalTreeCheck((RhetRelation)root.getSatellite());
		}
	}
	
	private int depthSize(RSVertex v){
		int nuc = 0, sat = 0;
		
		if(v instanceof RhetRelation){
			RhetRelation root = (RhetRelation)v;
			if(root.getNucleus() instanceof RhetRelation)
				nuc = depthSize((RhetRelation)root.getNucleus());
			if(root.getSatellite() != null && root.getSatellite() instanceof RhetRelation)
				sat = depthSize((RhetRelation)root.getSatellite());
			
			return (Math.max(nuc, sat)+1);
		}
		else
			return 0;
		
	}
	
	/**
	 *  FUNCTION turnTreeToSat(root, par, nrOfTimes)
	 *     IF nrOfTimes > 0 AND root.nuc !elementOf S
	 *       n = root.nuc
	 *       ns = root.nuc.sat
	 *     
	 *       root.parent = n
	 *       n.sat = root
	 *       root.nuc = ns
	 *       ns.par = root
	 *       
	 *       return turnTreeToSat(n, par, nrOfTimes)
	 *     ELSE
	 *       root.par = par
	 *       par.nuc = root
	 *       return root
	 */
	/**
	 * Startvert is the node that the tree is rotated about. Curr is the 
	 * current node that has to be adjusted for this purpose.
	 * 
	 * The algorithm is as follows:
	 * recursively follow the following algorithm:
	 * 		1. IF curr.parent exists THEN curr.sattelite = curr.parent 
	 * 		   ELSE <satellite stays satellite>
	 * 		2. IF curr != startvert THEN curr.nucleus = curr.nucleus.sattelite 
	 * 
	 *  if curr == 2 && startvert == 3
	 *   1                     2                  3
	 *  / \                   / \                / \
	 * a   2     after       1   c  total       2   4 
	 *    / \    ------->           ------->   / \
	 *   b   3   function           result    1   c
	 *      / \                              / \
	 *     c   4                            a  b
	 *     
	 * (PS. node 4 is not the last node... 4 has children too)
	 * 
	 * If curr == startvert the nuc is not changed.
	 * 
	 * if curr == 3 && startvert == 3
	 *   1                     3                  3
	 *  / \                   / \                / \
	 * a   2     after       2   4  total       2   4 
	 *    / \    ------->           ------->   / \
	 *   b   3   function           result    1   c
	 *      / \                              / \
	 *     c   4                            a  b
	 * 
	 * @param root
	 * @param startvert
	 */
	private RhetRelation turnTreeToSat(RhetRelation root, RhetRelation par, int nrOfTimes){
		//logger.info( "Turn tree to Satellite ");
		if(nrOfTimes > 0 
				&& root.getNucleus() instanceof RhetRelation 
				&& !isStopNode((RhetRelation)root.getNucleus())){
			RhetRelation nuc = (RhetRelation)root.getNucleus();
			RSVertex nucsat = nuc.getSatellite();
			
	/*		if(par!= null) logger.info
				(" par: " + par.getLabel());
			logger.info
				("ROOT:\n" + root);*/
			
			
			MLabeledEdge nucedge = getNucleusEdge(root);
			
			MLabeledEdge nucsatedge = getSatelliteEdge(nuc);
			MLabeledEdge paredge = null;
			if(par != null) paredge = getParentEdge(root, par);
			
			nucedge.setTarget(nucsat);
			nucsatedge.setSource(root);
			
			if(par != null && paredge.getLabel().equals(LibraryConstants.NUCLEUS))
				paredge.setTarget(nuc);
			else if(par != null) paredge.setSource(nuc);
			
			return turnTreeToSat(nuc, par, nrOfTimes - 1);
//			RhetRelation result = turnTreeToSat(nuc, par, nrOfTimes - 1);
//			if(result == null) return root;
//			else return result;
		}
		else {
			//logger.warning( "stopping because: ");
			//if(nrOfTimes == 0) logger.fine("  nrOfTimes == 0");
			//else if(!(root.getNucleus() instanceof RhetRelation)) logger.info("  nucleus not instanceof RhetRelation");
		//	else if(isStopNode((RhetRelation)root.getNucleus())) logger.info("  nucleus is stopnode");
//			if(par == null){
//				// do nothing
//			}
//			else if(par.getNucleus() == null){
//				getNucleusEdge(par).setSource(root);
//			}
//			else {
//				getSatelliteEdge(par).setTarget(root);
//			}
			return root;	
		}
	}
	
	private RhetRelation turnTreeToNuc(RhetRelation root, RhetRelation par, int nrOfTimes){
		//logger.info( "Turn tree to Nucleus ");
		if(nrOfTimes > 0 
				&& root.getSatellite() instanceof RhetRelation 
				&& !isStopNode((RhetRelation)root.getSatellite())){
			RhetRelation sat = (RhetRelation)root.getSatellite();
			RSVertex satnuc = sat.getNucleus();
			
			MLabeledEdge satedge = getSatelliteEdge(root);
			MLabeledEdge satnucedge = getNucleusEdge(sat);
			
			MLabeledEdge paredge = null;
			if(par != null) paredge = getParentEdge(root, par);
			
			satedge.setSource(satnuc);
			satnucedge.setTarget(root);
			
			if(par != null && paredge.getLabel().equals(LibraryConstants.NUCLEUS))
				paredge.setTarget(sat);
			else if(par != null) paredge.setSource(sat);
			
			return turnTreeToNuc(sat, par, nrOfTimes - 1);
//			RhetRelation result = turnTreeToNuc(sat, par, nrOfTimes - 1);
//			if(result == null) return root;
//			else return result;
		}
		else {
//			if(par == null){
//				// do nothing
//			}
//			else if(par.getNucleus() == null){
//				getNucleusEdge(par).setSource(root);
//			}
//			else {
//				getSatelliteEdge(par).setTarget(root);
//			}
			return root;	
		}
	}
	
	
	private MLabeledEdge getNucleusEdge(RSVertex el){
		Iterator edges_it = el.getIncidentMEdges();
		while(edges_it.hasNext()){
			MLabeledEdge e = (MLabeledEdge) edges_it.next();
			if(e.getLabel().equals(LibraryConstants.NUCLEUS) && e.getSource() == el)
				return e;
		}
		return null;
	}
	
	private MLabeledEdge getSatelliteEdge(RSVertex el){
		Iterator edges_it = el.getIncidentMEdges();
		while(edges_it.hasNext()){
			MLabeledEdge e = (MLabeledEdge) edges_it.next();
			if(e.getLabel().equals(LibraryConstants.SATELLITE) && e.getTarget() == el)
				return e;
		}
		return null;
	}
	
	/**
	 * Gives the edge between the give element and it's parent.
	 * Relation between par and el is either nucleus or satellite. 
	 * If nucleus, then par is source of edge
	 * if satellite, then par is target of edge
	 * @param el
	 * @param par
	 * @return
	 */
	private MLabeledEdge getParentEdge(RSVertex el, RSVertex par){
		MLabeledEdge edge = null;
		Iterator edges_it = el.getIncidentMEdges();
		while(edges_it.hasNext()){
			MLabeledEdge e = (MLabeledEdge) edges_it.next();
			if(e.getLabel().equals(LibraryConstants.SATELLITE) && e.getTarget() == par)
				edge = e;
			else if (e.getLabel().equals(LibraryConstants.NUCLEUS) && e.getSource() == par)
				edge = e;
		}
		return edge;
	}
	
}
