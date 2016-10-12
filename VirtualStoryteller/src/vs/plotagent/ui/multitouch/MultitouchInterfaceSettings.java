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
package vs.plotagent.ui.multitouch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Settings for the MT application. Should be put in a settings file eventually.
 */
public class MultitouchInterfaceSettings {
	
	public static String FOLDER_NAME = System.getProperty("user.dir") + File.separator + "img" + File.separator + "interactive_map" + File.separator;

	//The language of almost all text elements is handled in Prolog.
	//This setting is only for the text of the DoNothing action and the waiting message while AI is planning.
	public static enum Language {dutch, english};
	public static final Language LANGUAGE = Language.dutch;
	
	//public static final int MAX_NUMBER_OF_STORYAREAS = 3;
	public static final int MILLIS_BEFORE_RECHECK_DRAG_DESTINATION = 100; //(it is too costly to check for every generated drag-event if the underlying destination is allowed or disallowed)
	
	public static final boolean CHARACTERS_INITIALLY_HUMAN_CONTROLLED = true;
	public static Vector<String> exceptionsOnInitiallyHumanControlled = new Vector<String>();
	static {
		//Characters (from any story domain) that should be computer controlled on startup
		//These characters can however still be made human controlled by using the "Control Chooser"
		exceptionsOnInitiallyHumanControlled.add("http://www.owl-ontologies.com/Red.owl#wolf");
		//test:
		exceptionsOnInitiallyHumanControlled.add("http://www.owl-ontologies.com/Pirate.owl#angrypirate");
		
		// commit:
//		exceptionsOnInitiallyHumanControlled.add("http://www.ontology.com/commit.owl#henk");
//		exceptionsOnInitiallyHumanControlled.add("http://www.ontology.com/commit.owl#ingrid");
//		exceptionsOnInitiallyHumanControlled.add("http://www.ontology.com/commit.owl#juvi1");
//		exceptionsOnInitiallyHumanControlled.add("http://www.ontology.com/commit.owl#juvi2");
	}
	
	//default values behind slashes: //true or //false
	public static final boolean SHOW_DEPRICATED_HUMAN_CHARACTER_AGENT_LAUNCH_BUTTON = false;//false (in VS main screen: AgentLauncher)
	public static final boolean HIDE_FAILED_OR_ABORTED_ACTIONS_FROM_STORY = false;//true 
	public static final boolean HIDE_HALF_OF_THE_INTERFACE_ON_STARTUP = true;//false 
	public static final boolean HIDE_EVENTS_AND_FRAMINGOPERATORS_FROM_MENU = true;//true 
	public static final boolean USE_TEXT_TO_SPEECH = true;//true 
	public static final boolean USE_SOUND_EFFECTS = true;//true
	public static final boolean USE_HUMANORCOMPUTER_CONTROL_CHOOSER = true;//true 
	public static final boolean USE_INTERFACE_CHARACTER_ROPE = true;//true 
	public static final boolean USE_INSTANT_EXECUTE_MOVE_ACTIONS = true;//true 
	public static final boolean	CLEAR_DESTINATION_STATUS_ENABLED = false;//false 
	
	public static final boolean ENABLE_INTERFACE_ROTATE = true;//true 
	public static final boolean ENABLE_STORYAREA_ROTATE = true;//true 
	public static final boolean ENABLE_INTERFACE_SCALE = true;//true 
	public static final boolean ENABLE_STORYAREA_SCALE = true;//true 
	public static final boolean USE_INERTIA_DRAG_PROCESSOR = false;//false 
	
	//change to true when using fiducials:
	public static final boolean USE_FIDUCIALS = false;//false 
	
	//the use of the CCV 1.4 'object recognition' is an alternative for using fiducials.
	//the orange multi-touch table can not recognize normal size fiducials, but object recognition works acceptable for 3 distinct objects.
	public static final boolean USE_CCV_RECTANGLE_RECOGNITION = false; //false 
	//If the above two are both false, the characters should be moved by dragging the image with touch
		
	//Map to specify which Object or Fiducial ID belongs to which character
	public static Map<Integer,String> fiducialMap = new HashMap<Integer,String>();
	static {
		if(USE_FIDUCIALS && !USE_CCV_RECTANGLE_RECOGNITION) {
			//add 8, 9, 10 for debug use with the TUIO Simulator:
			fiducialMap.put(8, "http://www.owl-ontologies.com/Red.owl#red");
			fiducialMap.put(9, "http://www.owl-ontologies.com/Red.owl#grandma");
			fiducialMap.put(10, "http://www.owl-ontologies.com/Red.owl#wolf");
			
			//30hex == 48dec 
			fiducialMap.put(48, "http://www.owl-ontologies.com/Red.owl#red");
			//31hex == 49dec
			fiducialMap.put(49, "http://www.owl-ontologies.com/Red.owl#grandma");
			//32hex == 50dec
			fiducialMap.put(50, "http://www.owl-ontologies.com/Red.owl#wolf");
			
			//test:
			//fiducialMap.put(3, "http://www.owl-ontologies.com/Pirate.owl#angrypirate");
		}
		if(USE_CCV_RECTANGLE_RECOGNITION && ! USE_FIDUCIALS) {
			//in CCV 1.4 objects (that are not fiducials) are numbered with TRUE_ID touch id's 180 and upward:
			fiducialMap.put(180, "http://www.owl-ontologies.com/Red.owl#red");
			fiducialMap.put(181, "http://www.owl-ontologies.com/Red.owl#grandma");
			fiducialMap.put(182, "http://www.owl-ontologies.com/Red.owl#wolf");
			
			//test:
			//fiducialMap.put(183, "http://www.owl-ontologies.com/Pirate.owl#angrypirate");
		}
	}
}


/*
 * dump of Config file for Evoluce One Multi-Touch table: 
 *
 
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<Config activeConfigSet="default">

<Version type="LCD" versionnumber="2.1"/>


<Logging>
	<!-- values: OFF,SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST,ALL -->
	<Console level="INFO"/>
	<File level="FINE"/>
</Logging>


<RMIRegistry host="localhost" port="1098"/>

<APIControl port="1095"/>

<Adapters>
<!--	<Adapter mode="TCP" port="3000" protocol="MIM"/> -->
<!--	<Adapter protocol="TUIO_2DCUR" mode="TCP" port="3000"/> -->
<!--	<Adapter protocol="TUIO_2DOBJ" mode="TCP" port="3000"/> -->
	<Adapter host="127.0.0.1" mode="UDP" port="3333" protocol="TUIO_2DCUR" udpBroadcast="false" udpMaxPacketSize="100"/>
	<Adapter host="127.0.0.1" mode="UDP" port="3333" protocol="TUIO_2DOBJ" udpBroadcast="false" udpMaxPacketSize="100"/> 
</Adapters>


 */
