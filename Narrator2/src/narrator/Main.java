package narrator;

import java.io.File;
import java.util.Iterator;

import narrator.documentplanner.DocumentPlanner;
import narrator.lexicon.CharacterInfo;
import narrator.microplanner.MicroPlanner;
import narrator.reader.CharacterModel;
import narrator.shared.NarratorException;
import narrator.shared.Printer;
import narrator.surfacerealizer.SurfaceRealizer;
import natlang.rdg.model.RSGraph;

public class Main {
	public static final String testFabula = "testFabula.graphml";
	private String fabula;

	public Main(String fabula) {
		this.fabula = fabula;
	}

	public static void main(String[] args){
		String filename = "";
		if (args.length==0){
			filename = testFabula;
		} else{
			filename = args[0];
		}
		try{
			System.out.println(new Main(filename).start());
		} catch (NarratorException e){
			error(e.getMessage());
		}

	}

	public String start() throws NarratorException {
		File f = new File(fabula);
		if(!f.exists() || f.isDirectory()) { throw new NarratorException("Fabula file not found: "+f.getAbsolutePath()); }

		CharacterInfo characters = new CharacterInfo(fabula);

		for (CharacterModel c : characters.getChars()){
			System.out.println(c);
		}

		DocumentPlanner dp = new DocumentPlanner(fabula, characters);
		MicroPlanner mp = new MicroPlanner(fabula, characters);
		SurfaceRealizer sr = new SurfaceRealizer(characters);

		//Create Document Plan
		RSGraph graph = dp.transform();
		System.out.println(Printer.printGoed(graph.getRoot(), 0));	

		//Create Sentence Plan from Document Plan
		mp.setGraph(graph);
		graph = mp.transform();
		System.out.println(Printer.printGoed(graph.getRoot(), 0));

		sr.setLexicalChooser(mp.getLexicalChooser());

		//Create surface text from Sentence Plan
		sr.setGraph(graph);

		String result = "";
		try {
			sr.transform();
			Iterator<String> it = sr.getResult();
			StringBuffer sb = new StringBuffer("");
			while (it.hasNext()){
				String s = it.next();
				if (!s.equals(""))
					sb.append(s+"\n");
			}

			result = sb.toString();
			//System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error("");
		}

		return result;
	}

	public static void error(String string) {
		System.err.println("Fatal error: "+string);
		System.exit(0);
	}
}
