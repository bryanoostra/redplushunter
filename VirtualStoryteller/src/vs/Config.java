/* Copyright (C) 2008 Human Media Interaction - University of Twente
 * 
 * This file is part of The Virtual Storyteller.
 * 
 * The Virtual Storyteller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Virtual Storyteller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with The Virtual Storyteller. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package vs;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Contains configuration settings.
 * TODO: Move this to a text file?
 * 
 * @author swartjes
 * Created on 29-jun-2005
 */
public class Config {
	
	/** The debug level of the log factory **/
	public static Level DEBUGLEVEL = Level.FINE;
	
	/** The relative path of the log files **/
	public static String LOGPATH = "log/";	
	public static String KNOWLEDGEPATH = "knowledge/";
	public static String ONTOLOGYPATH = Config.KNOWLEDGEPATH + "ontology/";
	public static String PROLOGFILESPATH = Config.KNOWLEDGEPATH + "prolog/";
	public static String DOMAINSPATH = Config.KNOWLEDGEPATH + "domain/";
	public static String CHARACTERPROLOGFILESPATH = Config.PROLOGFILESPATH + "CharacterAgent/";
	public static String PLOTPROLOGFILESPATH = Config.PROLOGFILESPATH + "PlotAgent/";
	public static String PROPERTIESFILE = "conf/vst.properties";
	public static String PROPERTIES_STATEFILE = "conf/vst_state.properties";
	
	/*
	 * ATTENTION:
	 * process_launcher.bat contains several hard-coded paths. 
	 * For instance to the "lib" and "bin" folders of the MT4j project.
	 * I assume this is the only way to be able to launch all the agents?
	 * It took me some time to find this as the source of my problem when trying to upgrade from MT4j 0.9 to 0.98
	 * Maybe this comment might help someone or function as a note-to-self. :-) 
	 * -Thijs
	 */
	public static String RUN_PROCESS_BATCHFILE = "process_launcher.bat";
	
	//The port number for all the RMI communication of the JADE agents: 
	public static final String JADE_BOOT = "jade.Boot -port 1098";
	
	//constants used for interactive maps:
	public static final String IMAGE_DIR = "img/interactive_map/";
	public static final String MAP_FILENAME = "map.PNG";
	public static final String DOMAINKNOWLEDGE = "knowledge/domain/";
	public static final String LOCATIONS_PROPERTIES = "/setting/locations.properties";
	public static final String LOCATIONS_PROPERTIES_OLD = "/setting/locations.properties.old";
	
	//public static String INSPIRATIONRULESPATH = Config.DOMAINPATH + "cases/";
	
	/** Main ontology locations */
	public static String SWC_URI = Config.ONTOLOGYPATH + "StoryWorldCore.owl"; // core ontology
	public static String FABULA_URI = Config.ONTOLOGYPATH + "FabulaKnowledge.owl";
	
	/** Namespace Mappings **/	
	public static String SWC_PREFIX = "swc";
	public static String FABULA_PREFIX = "fabula";

	public static Map<String,String> namespaceMap = new HashMap<String,String>();
	static{
		// create namespace mappings for OWL, RDF and RDFS
		Config.namespaceMap.put("owl", "http://www.w3.org/2002/07/owl#");
		Config.namespaceMap.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		Config.namespaceMap.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		Config.namespaceMap.put("xsd", "http://www.w3.org/2001/XMLSchema#");
		
		// create extra namespace mappings for GSW, SSW and STORY
		Config.namespaceMap.put(Config.SWC_PREFIX, "http://www.owl-ontologies.com/StoryWorldCore.owl#");
		Config.namespaceMap.put(Config.FABULA_PREFIX, "http://www.owl-ontologies.com/FabulaKnowledge.owl#");
		Config.namespaceMap.put("cind", "http://www.owl-ontologies.com/StoryWorldSettings/Cinderella#");
		Config.namespaceMap.put("ps", "http://www.owl-ontologies.com/StoryWorldSettings/Pirates#");
		Config.namespaceMap.put("lolli", "http://www.owl-ontologies.com/StoryWorldSettings/Lollipop.owl#");
		Config.namespaceMap.put("love", "http://www.owl-ontologies.com/StoryWorldSettings/MakeLoveNotWar.owl#");
		Config.namespaceMap.put("red", "http://www.owl-ontologies.com/Red.owl#");
		Config.namespaceMap.put("graph", "http://www.owl-ontologies.com/Graphs.owl#");
		// Jeroen:
		// Even more places to put information about the new domain!
		Config.namespaceMap.put("commit", "http://www.ontology.com/commit.owl#");
		// Commit demo year 1
		Config.namespaceMap.put("commit_demo_y1", "http://www.owl-ontologies.com/Red.owl#");
		
		//XXX: at the moment this short version has to be the same as the directory name
		//TODO: change this so we can use for instance "psj2" instead of longer "pirates_jasper2"
		Config.namespaceMap.put("pirates_jasper2", "http://www.owl-ontologies.com/StoryWorldSettings/Pirates_Jasper2.owl#");

	}
}

