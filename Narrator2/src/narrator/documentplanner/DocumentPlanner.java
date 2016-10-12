package narrator.documentplanner;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.uci.ics.jung.graph.DirectedGraph;

import narrator.Main;
import narrator.lexicon.CharacterInfo;
import narrator.reader.FabulaEdge;
import narrator.reader.FabulaNode;
import narrator.reader.FabulaReader;
import narrator.reader.StringUtil;
import narrator.shared.GraphTransformer;
import narrator.shared.NarratorException;
import narrator.shared.Printer;
import narrator.shared.Settings;
import natlang.rdg.model.RSGraph;


/**
 * Transforms the Fabula into a Dependency Tree containing
 * the basis structure of the story. 
 * 
 * This is done by calling these five subcomponents:
 *  Initial Document Plan Builder
 *  Background Information Supplier
 *  Mood Creator
 *  State Transformer
 *  Branch Remover.
 *  
 * @author Marissa Hoek
 *
 */
public class DocumentPlanner implements GraphTransformer{
	private RSGraph graph;
	private String fabula;
	private CharacterInfo characters;
	private FabulaReader fabulaReader;
	
	public static final String PREFIX_HISTORYFILE = "<!--History file:";
	public static final String SUFFIX_HISTORYFILE = "-->";
	
	public DocumentPlanner(String fabula, CharacterInfo characters){
		this.fabula = fabula;
		graph = null;
		this.characters = characters;
	}

	@Override
	public RSGraph getGraph() {
		return graph;
	}

	@Override
	public RSGraph transform() throws NarratorException {
		//Read the Fabula file
		try {
			fabulaReader = new FabulaReader(fabula);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//Main.error("File not found or other problem with the Fabula file!");
			e.printStackTrace();
			throw new NarratorException("File not found or other problem with the Fabula file!");
		} catch (NullPointerException e) {
			//Main.error("File not found or other problem with the Fabula file!");
			e.printStackTrace();
			throw new NarratorException("There's a problem with the Fabula file!\nCheck if the GraphML file defines all necessary elements.");
		}
			
		if (Settings.FOCALIZATION){
			DirectedGraph<FabulaNode, FabulaEdge> fullGraph = fabulaReader.getGraph();
			Focalizer focalizer = new Focalizer(fullGraph);
			DirectedGraph<FabulaNode, FabulaEdge> focGraph = focalizer.transform();
			fabulaReader.setGraph(focGraph);
		}
		
		if (Settings.FLASHBACK){
			//get History file
			File fabulaFile = new File(fabula);
			File parent = fabulaFile.getAbsoluteFile().getParentFile();
			String path = parent.getAbsolutePath();
			String localFilename = StringUtil.getSettingFile(fabula,PREFIX_HISTORYFILE,SUFFIX_HISTORYFILE).trim();
			String history = path+File.separatorChar+localFilename;

			//Run Flashback module on current graph
			DirectedGraph<FabulaNode, FabulaEdge> fullGraph = fabulaReader.getGraph();
			if (localFilename!=null && !localFilename.equals("")){
				Flashback flashback = new Flashback(fullGraph, history);
				fabulaReader.setGraph(flashback.transform());
			}
		}
		
		DirectedGraph<FabulaNode, FabulaEdge> testGraph = fabulaReader.getGraph();
		if (testGraph.getVertexCount()<2) throw new NarratorException("Fabula needs to have at least two plot elements. Maybe the focalized character is not present in any elements?");
		
		InitialDocPlanBuilder docPlanBuilder = new InitialDocPlanBuilder(fabulaReader);
		Introduction intro = new Introduction(characters);
		BackgroundInformationSupplier bgis = new BackgroundInformationSupplier(characters);
		StateTransformer sFormer = new StateTransformer();
		BranchRemover bRemover = new BranchRemover();
		
		//Create the Initial Document Plan
		try{graph = docPlanBuilder.transform();}
		catch(NullPointerException e){
			e.printStackTrace();
			throw new NarratorException("NullPointerException encountered while creating Document Plan. \n" +
					"Possible cause: Empty Fabula or focalized character doesn't take part in the story.");
		}
		//Print the graph contents
		//System.out.println(Printer.printGoed(graph.getRoot(), 0));
		
		//Add character introductions
		if (Settings.INTRODUCTION){
			intro.setGraph(graph);
			graph = intro.transform();
		}
		
		//Add background information
		if (Settings.BGIS){
			bgis.setGraph(graph);
			graph = bgis.transform();
		}
		
		//Transform emotional states
		if (Settings.TRANSFORMSTATES){
			sFormer.setGraph(graph);
			graph = sFormer.transform();
		}
		
		//Remove long branches
		bRemover.setGraph(graph);
		graph = bRemover.transform();

		
		return graph;
	}

	@Override
	public void setGraph(RSGraph graph) {
		this.graph = graph;
	}
}
