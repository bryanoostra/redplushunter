package natlang.rdg.documentplanner;

import java.util.logging.Logger;

import natlang.debug.LogFactory;
import natlang.rdg.Narrator;
import natlang.rdg.model.RSGraph;
import natlang.rdg.view.RDGEditor;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * DocumentPlanner is the first component in the pipeline of the Narrator agent
 * and consists of the InitialDocumentPlanBuilder, BackgroundInformationSupplier, 
 * StateTransformer and MoodCreator.
 * 
 * For testing purposes there is also a constructor that takes a filename as its argument.
 * This constructor calls the StoryReader instead of the InitialDocumentPlanBuilder
 * 
 * @author Nanda Slabbers
 */
public class DocumentPlanner
{
	private RSGraph graph;
	private InitialDocPlanBuilder idpb;
	private BranchRemover br;
	private BackgroundInformationSupplier bgis;
	private StateTransformer st;
	private MoodCreator mc;
	
	private Logger logger;
	
	/**
	 * Constructor for reading an OWL specification of the fabula
	 *
	 */
	public DocumentPlanner()
	{
		idpb = new InitialDocPlanBuilder();
		br = new BranchRemover();
		bgis = new BackgroundInformationSupplier();
		st = new StateTransformer();
		mc = new MoodCreator();
		logger = LogFactory.getLogger(this);
	}	
	
	/**
	 * Constructor for reading initial document plans
	 */
	public DocumentPlanner(String filename)
	{		
		br = new BranchRemover();
		bgis = new BackgroundInformationSupplier();
		st = new StateTransformer();
		mc = new MoodCreator();
	}
	
	/**
	 * Performs all tasks of the Document Planner 
	 * and shows the initial and final document plans if these variables are set to true
	 * in the Narrator 
	 * @return boolean
	 */
	public boolean transform()
	{		
		logger.info("Starting document planning...");
		
		idpb.setGraph(graph);
		if (idpb.transform())
		{
//			System.exit(0);
			graph = idpb.getGraph();
					
			if (Narrator.SHOWINIDOC)
			{
				RDGEditor editorRS = new RDGEditor("Rhetorical Dependency Graph");
				editorRS.start(graph);
				graph.decompose();
				
				idpb.transform();
				graph = idpb.getGraph();
			}
						
			bgis.reset();
			
			if (Narrator.BGIS)
			{
				bgis.setGraph(graph);
				graph = bgis.transform();
			}
			if (Narrator.TRANSFORMSTATES)
			{
				st.setGraph(graph);
				graph = st.transform();
			}
			if (Narrator.MOOD)
			{
				mc.setGraph(graph);
				graph = mc.transform();
			}
			if (Narrator.BRANCH)
			{
				br.setGraph(graph);
				graph = br.transform();
			}
						
			if (Narrator.SHOWFINALDOC)
			{
				RDGEditor editorRS = new RDGEditor("Rhetorical Dependency Graph");
				editorRS.start(graph);
				graph.decompose();
				
				bgis.reset();
				
				idpb.transform();
				graph = idpb.getGraph();
				
				if (Narrator.BGIS)
				{
					bgis.setGraph(graph);
					graph = bgis.transform();
				}
				if (Narrator.TRANSFORMSTATES)
				{
					st.setGraph(graph);
					graph = st.transform();
				}
				if (Narrator.MOOD)
				{
					mc.setGraph(graph);
					graph = mc.transform();
				}
				if (Narrator.BRANCH)
				{
					br.setGraph(graph);
					graph = br.transform();
				}
			}
			return true;
		}
		else
			return false;
	}
	
/*	*//**
	 * Performs all tasks of the Document Planner 
	 * and shows the initial and final document plans if these variables are set to true
	 * in the Narrator 
	 * @return boolean
	 *//*
	public boolean transform2()
	{			
		str.setGraph(graph);
		if (str.transform())
		{
			graph = str.getGraph();
			
			if (Narrator.SHOWINIDOC)
			{
				RDGEditor editorRS = new RDGEditor("Rhetorical Dependency Graph");
				editorRS.start(graph);
				graph.decompose();
							
				str.reset();
				str.transform();
				graph = str.getGraph();	
			}
			
			if (Narrator.BGIS)
			{				
				bgis.setGraph(graph);			
				graph = bgis.transform();
			}
			if (Narrator.TRANSFORMSTATES)
			{
				st.setGraph(graph);
				graph = st.transform();
			}
			if (Narrator.MOOD)
			{
				mc.setGraph(graph);
				graph = mc.transform();
			}
			if (Narrator.BRANCH)
			{
				br.setGraph(graph);
				graph = br.transform();
			}
			
			if (Narrator.SHOWFINALDOC)  //=-- Why is the entire process more or less repeated??
			{
				RDGEditor editorRS = new RDGEditor("Rhetorical Dependency Graph");
				editorRS.start(graph);
				graph.decompose();
				
				str.reset();				
				str.transform();
				graph = str.getGraph();
				
				if (Narrator.BGIS)
				{					
					bgis.reset();
					
					bgis.setGraph(graph);
					graph = bgis.transform();
					
					bgis.endParagraph();			
				}
				if (Narrator.TRANSFORMSTATES)
				{
					st.setGraph(graph);
					graph = st.transform();
				}
				if (Narrator.MOOD)
				{
					mc.setGraph(graph);
					graph = mc.transform();
				}
				if (Narrator.BRANCH)
				{
					br.setGraph(graph);
					graph = br.transform();
				}
			}			
			return true;
		}
		else
			return false;
	}*/
	
	public RSGraph getGraph()
	{
		return graph;
	}
	
	public void setGraph()
	{
		graph = null;
	}	
	
	public OntModel getModel(){
		return idpb.getModel();
	}
}
