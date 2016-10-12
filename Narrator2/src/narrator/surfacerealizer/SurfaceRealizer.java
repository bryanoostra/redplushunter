package narrator.surfacerealizer;

import narrator.shared.Settings;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import natlang.rdg.surfacerealizer.*;
import narrator.lexicon.CharacterInfo;
import narrator.lexicon.LexicalChooser;
//import natlang.debug.LogFactory;
//import natlang.rdg.Narrator;
import natlang.rdg.discourse.CueWordHistory;
import natlang.rdg.discourse.RuleHistory;
import natlang.rdg.model.CannedText;
import natlang.rdg.model.OrderedDepTreeModel;
import natlang.rdg.model.RSDepTreeModel;
import natlang.rdg.model.RSGraph;
import natlang.rdg.model.RSVertex;
import natlang.rdg.model.RhetRelation;
import natlang.rdg.surfacerealizer.RDGTransformer;
import natlang.rdg.view.RDGEditor;

/**	The SurfaceRealizer takes an URL or a File, reads the rhetorical dependency graph
 *	and transforms it to a Surface Form, contained in a String. It has several Discourse 
 *	Histories to keep track of some aspects of recent utterances, such as characters
 *	and cue words.
 *
 * @author Marissa Hoek
 * @author Nanda Slabbers
 */
public class SurfaceRealizer implements RDGTransformer
{	

	private List result = new ArrayList();
	
	private ReferringExpressionGenerator reg ;
	private RuleHistory ruleHistory = new RuleHistory();
	private CueWordHistory cueHistory = new CueWordHistory();
	private LexicalChooser lc;
	private CharacterInfo characters;
		
	private RSGraph graph;
	private URL u;
	private File f;
	
	//private Logger logger;
	
	public SurfaceRealizer(CharacterInfo characters){
		reg = new ReferringExpressionGenerator(characters);
		//logger = LogFactory.getLogger(this);
		this.characters = characters;
	}
	
	/** Set a new URL of an xml-file */
	public void setUrl(URL url)
	{
		u = url;
	}
	
	/** Set a new xml-File */
	public void setFile(File file)
	{
		f = file;
	}
	
	/** Set the new graph */
	public void setGraph(RSGraph g)
	{
		graph = g;
	}
	
	public void setLexicalChooser(LexicalChooser lexch)
	{
		lc = lexch;
	}
	
	public LexicalChooser getLexicalChooser()
	{
		return lc;
	}
	
	/** Checks whether there is a RSGraph 
	 */
	public boolean check()
	{
		if (graph == null)
			return false;
		return true;
	}
	
	/** Reads the new RSGraph
	 *	@throws Exception (see RSGraph) */
	private void readGraph() throws Exception
	{
		if (u != null)
			graph = new RSGraph(u);
		else if (f != null)
			graph = new RSGraph(f);
	}
	
	/** Reads the RSGraph and transforms it to Surface Form 
	 *	@throws Exception (see RSGraph) */
	public boolean transform() throws Exception
	{
		if (!check())				//if the RSGraph was not yet initialized, read it
			return false;
		
		result = new ArrayList();	//reset the list of generated trees		
		Iterator it;
				
		if (Settings.TRANSFORMRELS)
		{
			RelationTransformer relTrans = new RelationTransformer(graph, characters,lc);
			relTrans.setDiscourseHistory(cueHistory);
								
			if (!relTrans.check())	//if the RSGraph can't be transformed
			{
				//logger.warning(" RZ: the RSGraph can't be transformed");
				List l = new ArrayList();
				it = graph.getMVertices();
				while (it.hasNext())
				{	//retrieve all relations and trees without parent, and put them and 
					RSVertex v = (RSVertex) it.next();	//their children in a list
					
					if (!v.getParent().hasNext())
					{
						
						if (v.getType().equals("deptree"))
							l.add(v);
						else if (v.getType().equals("rhetrel"))
						{
							l.add(((RhetRelation) v).getNucleus());
							l.add(((RhetRelation) v).getSatellite());
						}
						else if (v.getType().equals("text"))
							l.add(v);
					}
				}
				it = l.iterator();
			}	
			else    //transform the RSGraph, by transforming all relations in it
			{		//and get the result
				//logger.fine(" RZ: relTrans.transform()");
				relTrans.transform();
				it = relTrans.getResult();
			}
		}
		else
		{
			List l = new ArrayList();
			it = graph.getMVertices();
			while (it.hasNext())			// nog even goed controleren of dit de nodes wel in precies de juiste volgorde teruggeeft!
			{
				RSVertex v = (RSVertex) it.next();
				if (v.getType().equals("deptree") || v.getType().equals("text"))
					l.add(v);
				
			}
			it = l.iterator();
		}
		
		while (it.hasNext())	//order every tree in iterator 'it', produce referential
		{						//expressions and generate the surface form
			RSVertex vert = (RSVertex) it.next();
			
			//logger.fine(" RZ: NEXT: " + vert.toXMLString());
			
			if (vert.getType().equals("deptree"))
			{				
				RSDepTreeModel tree = (RSDepTreeModel) vert;				
				Iterator itmp = tree.getMVertices();
				int cnt = 0;
				while (itmp.hasNext())
				{
					cnt++;
					itmp.next();
				}
				
				OrderedDepTreeModel order = new OrderedDepTreeModel(tree, lc.getLexicon());
				order.orderTree(ruleHistory);
				
				System.out.println("Order: "+order);
/*				Iterator it23 = order.getIncidentOutMEdges();
				
				while (it23.hasNext()){
					System.out.println(it23.next());
				}*/
				
				reg.setTree(order);
				reg.setLexicalChooser(lc);
				reg.transform();
				order = (OrderedDepTreeModel) reg.getResult().next();
				
				
				String tmp = order.getSurfaceForm();
				//logger.info("\nTekst:\n" + tmp + "\n");
				result.add(tmp);
				
				if (Settings.SHOWDTS)
				{
					RDGEditor editorRS = new RDGEditor("Dependency Tree");
					editorRS.start(order);
				}							
				order.decompose();	//destroy the tree
			}
			else if (vert.getType().equals("text"))
			{
				CannedText ct = (CannedText) vert;
				String tmp = ct.getText();
				result.add(tmp);
			}
		}
		
		return true;
	}
	
	/** Return the resulting surface form(s) */
	public Iterator getResult()
	{		
		graph.decompose();
		graph = null;
		return result.iterator();
	}
	
	public void newParagraph()
	{
		reg.newParagraph();
	}
}