package natlang.rdg;

import natlang.debug.LogFactory;
import natlang.rdg.documentplanner.*;
import natlang.rdg.lexicalchooser.*;
import natlang.rdg.microplanner.*;
import natlang.rdg.model.*;
import natlang.rdg.surfacerealizer.*;

import java.util.Iterator;
import java.util.logging.Logger;


/** 
 * Narrator is the main class and represents the pipeline consisting
 * of a DocumentPlanner, a MicroPlanner and a SurfaceRealizer
 * 
 * @author Nanda Slabbers
 */
public class Narrator 
{
	public static boolean SHOWINIDOC 		= false;
	public static boolean SHOWFINALDOC 		= false;
	public static boolean SHOWINIRDG 		= false;
	public static boolean SHOWFINALRDG 		= false;
	public static boolean SHOWDTS			= false;
	public static boolean REFEXP 			= true;
	public static boolean BRIDGING 			= true;
	public static boolean ADDADJECTIVES 	= true;
	public static boolean TRANSFORMRELS 	= true;	
	public static boolean BGIS 				= true;
	public static boolean ADDDISCMARKERS 	= true;
	public static boolean TRANSFORMSTATES 	= true;
	public static boolean MOOD 				= true;
	public static boolean BRANCH 			= false;
	public static boolean ELLIPT			= false;
	
	private Logger logger;
	
	
	// for testing purposes
	public static String printGoed(RSVertex node, int tabs){
		StringBuilder sb = new StringBuilder();
		String stringtabs = "";
		for(int i = 0; i < tabs; i++)
			stringtabs += "\t";
		
		if (node.getType().equals("rhetrel"))
		{			
			sb.append(stringtabs + "rel: " + ((RhetRelation) node).getLabel()).append('\n');
			
			RSVertex nuc = ((RhetRelation) node).getNucleus();
			RSVertex sat = ((RhetRelation) node).getSatellite();
			
			
			
			if (sat != null)// && sat.getType().equals("rhetrel"))
			{
				
				sb.append(stringtabs + "sat:\n");
				sb.append(printGoed(sat, tabs + 1)).append('\n');
			}
			
			if (nuc != null)// && nuc.getType().equals("rhetrel"))
			{
				sb.append(stringtabs + "nuc:\n");
				sb.append(printGoed(nuc, tabs + 1)).append('\n');
			}			
		}
		else if (node.getType().equals("deptree"))
		{
			sb.append(stringtabs + "node: " + node).append('\n');
		}
		else {
			sb.append(stringtabs + "???: " + node).append('\n');
		}
		return sb.toString();
	}
	
	public Narrator()
	{		
		logger = LogFactory.getLogger(this);
		DocumentPlanner dp = new DocumentPlanner();
		MicroPlanner mc = new MicroPlanner();
		SurfaceRealizer sr = new SurfaceRealizer(dp.getModel());
				 
		// once the fabula is subdivided into paragraphs, this process has to be done recursively 
		// in the exact same way as done in the storytester...
		logger.info("Starting document planner");
		if (dp.transform())
		{
			RSGraph graph = dp.getGraph();		
			logger.fine(graph.size() + " vertices present in document plan graph");
			
			logger.fine(printGoed(graph.getRoot(), 0));
//			Iterator itvertices = graph.getRelations();
//			if(itvertices.hasNext()){
////				graph.printGoed((RSVertex)itvertices.next());
//				RSVertex node = (RSVertex)itvertices.next();
//				printGoed(node, 0);
//			}
//			System.exit(0);
			logger.info("Starting microplanner");
			mc.setGraph(graph);	
			graph = mc.transform();	
			
			logger.fine(printGoed(graph.getRoot(), 0));
//			itvertices = graph.getRelations();
//			if(itvertices.hasNext()){
////				graph.printGoed((RSVertex)itvertices.next());
//				RSVertex node = (RSVertex)itvertices.next();
//				printGoed(node, 0);
//			}
			
			sr.setGraph(graph);
			LexicalChooser lc = mc.getLexicalChooser();
			sr.setLexicalChooser(lc);
			
			logger.info("Starting surface realizer");
			try
			{
	  			if (sr.transform())
	   			{
	        		Iterator<String> it = sr.getResult();
	        		StringBuffer sb = new StringBuffer("");
	    			while (it.hasNext())
	    				sb.append("\n" + (String) it.next());
	    			logger.info("Stukje verhaal:\n" + sb.toString() + "\n");
	    		}
	    		else
	    			logger.warning("Transformation failed");
	    	}
			catch (Exception ex)
			{
				ex.printStackTrace();
				logger.warning("Encountered error: " + ex);
			}
		}		
	}
	
	public static void main(String[] args) 
	{
		new Narrator();
	}
}
