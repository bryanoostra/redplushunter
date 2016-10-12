package vs.characteragent.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vs.Config;
import vs.characteragent.ui.InterfacePanel.GenericInterfaceListener;
import vs.characteragent.ui.MapPanel.MapMouseListener;
import vs.characteragent.ui.MapPanel.MapTUIOListener;
import vs.debug.LogFactory;
import vs.knowledge.PrologKB;
import TUIO.TuioClient;
import TUIO.TuioPoint;

import com.hp.hpl.jena.shared.PrefixMapping;

public class MapFrame extends JFrame implements ActionListener, ListSelectionListener {

	private static final boolean SHOW_DEBUG_ACTION_SELECTION_AREA = false;
	
	private InterfacePanel interfacePanel;
	private MapPanel mapPanel;
	
	private GenericInterfaceListener genericInterfaceListener;
	private MapTUIOListener mapTUIOListener;
	
	private JLabel infoLabel;
	private JList selectionList;
	private JButton executeButton;
	private JButton reconnectButton;
	private JCheckBox skipRound;
	private HumanCharacterAgentGui hgui;

	Vector<Integer> numberOfActionsByType = new Vector<Integer>();
	
	private String currentSelection = "";
	private String humanCharacterName;
	private String humanCharacterCurrentLocation;
	
	private Hashtable<String, String> destination_action;
	
	private PrefixMapping pm;
	private Logger logger;

	private Object sync = new Object();
	protected boolean debug;
	private TuioClient client;
	

	public MapFrame(HumanCharacterAgentGui hgui) {
		this.hgui = hgui;
		
		//initialize the logger
		logger = LogFactory.getLogger(this);
		
		//used for shortening names
		pm = PrefixMapping.Factory.create();
		pm.setNsPrefixes(Config.namespaceMap);

		setTitle("Map of Story World");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				
		JPanel bottomBar = new JPanel(new BorderLayout());
		JSeparator separator = new JSeparator();
		infoLabel = new JLabel(" Select an action:  ");
		selectionList = new JList();
		ListSelectionModel lsm = selectionList.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectionList.setEnabled(false);
		//Use a custom ListCellRenderer to display a shorter String representation of the actions
		selectionList.setCellRenderer(new PossibleActionsListCellRenderer());
		selectionList.addListSelectionListener(this);
		JScrollPane listPanel = new JScrollPane(selectionList);
		executeButton = new JButton("Execute");
		executeButton.setEnabled(false);
		executeButton.addActionListener(this);
		skipRound = new JCheckBox("Skip Round", false);
		skipRound.setEnabled(false);
		skipRound.addActionListener(this);
		reconnectButton = new JButton("Reconnect");
		reconnectButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		
		if (SHOW_DEBUG_ACTION_SELECTION_AREA) {
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
			buttonPanel.add(Box.createVerticalGlue());
			buttonPanel.add(skipRound);
			buttonPanel.add(executeButton);
			
			bottomBar.add(separator, BorderLayout.NORTH);
			bottomBar.add(infoLabel, BorderLayout.WEST);
			bottomBar.add(listPanel, BorderLayout.CENTER);
			
		}
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(reconnectButton);
		bottomBar.add(buttonPanel, BorderLayout.EAST);
		
		mapPanel = new MapPanel(this, hgui);
		interfacePanel = new InterfacePanel(this);
		mapPanel.setLayout(new BorderLayout());
		mapPanel.add(interfacePanel);
			
		setLayout(new BorderLayout());
		add(bottomBar, BorderLayout.SOUTH);
		add(mapPanel, BorderLayout.NORTH);
		
		//initiate some map mouse listening
		MapMouseListener mapMouseListener = mapPanel.new MapMouseListener();
		interfacePanel.addMouseListener(mapMouseListener);
		interfacePanel.addMouseMotionListener(mapMouseListener);
		
		//initiate some map touch listening
		mapTUIOListener = mapPanel.new MapTUIOListener();
		
		//initiate some interface mouse and touch listening!
		genericInterfaceListener = interfacePanel.new GenericInterfaceListener();
		interfacePanel.addMouseListener(genericInterfaceListener);
		interfacePanel.addMouseMotionListener(genericInterfaceListener);
		
		reconnectButton.setEnabled(true);
		connect_to_TUIO_server();
	}
	
	private void connect_to_TUIO_server() {

		//TODO: how can we nicely start tuioserver from here if it is not already started?
		//maybe something like this?
		//nl.utwente.ewi.hmi.tuioserver.TuioServer server = new TuioServer();
		//XXX: tuioserver should be started as standalone for now...

		if(client!=null && client.isConnected()) {
			client.disconnect();
		}
		client = new TuioClient();
		client.addTuioListener(genericInterfaceListener);
		client.addTuioListener(mapTUIOListener);
		client.connect();
	}

	//sets the characterName
	public void setHumanCharacterName(String characterURI) {
		humanCharacterName = "'" + characterURI + "'";
		setTitle(getTitle()+ "  (playing " + pm.shortForm(characterURI) + ")");
		
		//set the HumanCharacterName in the mapPanel 
		mapPanel.setHumanCharacterName(characterURI);
	}
	
	//sets the domain
	//shows the window with the map interface
	public void setDomainShowMap(String domain, Vector<String> characters) {
		//try to set the domain (load map and character images)
		try {
			mapPanel.setDomain(domain, characters);
		} catch (IOException ioe) {	
			//throws IOException if the map image file is not available
			//this should never happen, all images should be available
			String message = "Problem while loading domain: " + ioe.getMessage();
			logger.severe(message);
			hgui.writeConsole(message);
			//do or do not continue when a problem occurs:
			//return;
		}
		
		//synchronize dimensions
		Dimension mapDimension = mapPanel.getPreferredSize();
		interfacePanel.setMapSize(mapDimension);
		
		//we do not want the top bar visible
		this.setUndecorated(true); 
		
		//now everything is set, we can show the window with the mapInterface
		setVisible(true);
		
		//determine the size
		int listHeight = selectionList.getPreferredScrollableViewportSize().height;
		int panelWidth = mapDimension.width;
		int panelHeight = mapDimension.height;
		//setSize(1280, 50 + PanelHeight + listHeight);
		setSize(panelWidth + 15, 50 + panelHeight + listHeight);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
        
		//setResizable(false);
		
		//try to become the focussed window
		requestFocus();
		
		if(debug==true) {
			Vector<String> linesR1 = new Vector<String>();
			Vector<String> linesR2 = new Vector<String>();
			Vector<String> linesR3 = new Vector<String>();
			
	        linesR1.add("The wolf became quite hungry.");
	        linesR1.add("Little Red Riding Hood skips to the forest.");
	        linesR1.add("Grandma skips to the forest.");
	        linesR1.add("\"Oh, hey, Little Red Riding Hood\" says the wolf. 123456 testerdetest");
	        
	        linesR2.add("Hello, Grandma\" says the wolf.");
	        linesR2.add("\"Hello, the wolf\" says Little Red Riding Hood.");
	        linesR2.add("\"Hello, the wolf\" says Grandma.");
	        linesR2.add("\"Oh, hey, Little Red Riding Hood\" says the wolf. 123 test");
	        
	        linesR3.add("\"Oh, hey, Little Red Riding Hood\" says the wolf.");
	        linesR3.add("Grandma bursts out in tears.");
	        linesR3.add("Little Red Riding Hood bursts out in tears.");
	        
	    	interfacePanel.linesAll.add(linesR1);
	    	interfacePanel.linesAll.add(linesR2);
	    	interfacePanel.linesAll.add(linesR3);
	    	
	    	interfacePanel.frc = ((Graphics2D)getGraphics()).getFontRenderContext();
	    	
	    	interfacePanel.recalculateStory();
	    	
	    	//Hashtable<String, Vector<String>> dummy = new Hashtable<String, Vector<String>>();
	    	Vector<String> dummy1 = new Vector<String>();
	    	Vector<Vector<String>> dummy2 = new Vector<Vector<String>>();
	    	
	    	Vector<String> skipto = new Vector<String>();
	    	skipto.add("SkipTo grandmas house");
	    	skipto.add("SkipTo reds house");
	    	dummy1.add("SkipTo");
	    	dummy2.add(skipto);
	    	
	    	Vector<String> cry = new Vector<String>();
	    	cry.add("Cry like a baby");
	    	dummy1.add("Cry");
	    	dummy2.add(cry);
	    	
	    	interfacePanel.updatePossibleActions(dummy1, dummy2);
        }
        
	}
	
	public void addOperatorResult(int round, String text, boolean success) {
		interfacePanel.addOperatorResult(round, text, success);
	}
	
	public void updateRoundNumber(int round) {
		infoLabel.setText(" Select an action (round " + round + "):  ");
	}
	
	//prints the location inhabitants in the console
	//finds and stores the current location of the human character
	//forwards the location_inhabitants information to the mapPanel
	public void updateLocationsInhabitants(Hashtable<String, Vector<String>> location_inhabitants) {
		Iterator<Entry<String, Vector<String>>> it = location_inhabitants.entrySet().iterator();
		hgui.writeConsole("===== Which (known) characters are at the (known) locations? =====");
		while(it.hasNext()) {
			Entry<String, Vector<String>> e = (Entry<String, Vector<String>>)it.next();
			String location = (String)e.getKey();
			Vector<String> inhabitants = (Vector<String>)e.getValue();
			String place = pm.shortForm(location.replace("'", ""));
			String description = "@ " + place + " = ";
			Iterator<String> itChar = inhabitants.iterator();
			while (itChar.hasNext()) {
				String name = itChar.next();
				//Store the current location of the human character
				if (name.equals(humanCharacterName)) {
					this.humanCharacterCurrentLocation = location;
				}
				name = name.replace("'", "");
				description = description + pm.shortForm(name) + " ";
			}
			hgui.writeConsole(description);
		}
		hgui.writeConsole("----- No more (known) locations with (known) characters -----");

		//now finally update the inhabitants of each location on the map
		mapPanel.updateLocationsInhabitants(location_inhabitants);
	}
	
	//called to update the currently possible transitMove actions
	public void updatePossibleTransitMoveActions(Hashtable<String,String> location_action) {
		destination_action = location_action;
	}
	
	//called to update the currently possible actions
	//before calling this method ensure that FontRenderContext this.frc is initialized!
	public void updatePossibleActions(Hashtable<String, Vector<String>> allPossibleActionsByType) {
		//update the model of selectionList
		updateSelectionListModel(allPossibleActionsByType);
				
		//debug parameter
		boolean constructReasonableDescription = false;
		
		//Hashtable<String, Vector<String>> allPossibleActionsByTypeShort = new Hashtable<String, Vector<String>>();
		Vector<String> typesShort = new Vector<String>();
		Vector<Vector<String>> actionsByTypeShort = new Vector<Vector<String>>();
		
		Enumeration<String> enumTypes = allPossibleActionsByType.keys();
		while(enumTypes.hasMoreElements()) {
			String type = enumTypes.nextElement();
			Vector<String> actionsTypeX = allPossibleActionsByType.get(type);
			
			Vector<String> actionsTypeXShort = new Vector<String>();
			Iterator<String> itActionsTypeX = actionsTypeX.iterator();
			String fullDescription = "none";
			
	    	while(itActionsTypeX.hasNext()) {
	    		fullDescription = itActionsTypeX.next();
	    		String text = "";
	    		
	    		if(debug || constructReasonableDescription) {
					text = constructReasonableDescriptionFromAction(fullDescription);
				} else {
					PrologKB pkb = PrologKB.getInstance();
					String shortDescription = pkb.narrate_imperative(fullDescription);
					text = shortDescription.replaceAll("'", "");
					//String type = pkb.getSchemaType(fullDescription);
					//System.out.println(type);
				}
							
				//XXX: maybe by getType() and getTarget() and getDuration?
				
				actionsTypeXShort.add(text);
	    	}
	    	
	    	String typeShort;
	    	if(debug || constructReasonableDescription) {
				typeShort = constructReasonableDescriptionFromAction(fullDescription);
			} else {
				PrologKB pkb = PrologKB.getInstance();
				String shortDescription = pkb.narrate_imperative(type);
				typeShort = shortDescription.replaceAll("'", "");
				
				//XXX: tmp debug approach
				typeShort = constructReasonableDescriptionFromAction(fullDescription);
			}	
			//allPossibleActionsByTypeShort.put(typeShort, actionsTypeXShort);
			typesShort.add(typeShort);
			actionsByTypeShort.add(actionsTypeXShort);
		}
		
		interfacePanel.updatePossibleActions(typesShort, actionsByTypeShort);
		
//		writeConsole("===== What are my possible actions? =====");
//		//possibleActionsTable = new Hashtable<String, String>();
//		Iterator<String> it = allPossibleActions.iterator();
//		while (it.hasNext()) {
//			String full = (String)it.next();
//			
//			//shorten the String that represents the action (remove some GENERAL strings)
//			String shortened = full.replaceAll("http://www.owl-ontologies.com/", "");
//			shortened = shortened.replaceAll("'.'", "");
//
//			//remove PARTICULAR String(s) in rather ugly (hardcoded) way:
//			shortened = shortened.replaceAll("Red.owl#", "");
//			//shortened = shortened.replaceAll("Lollipop.owl#", "");
//			//etc...
//			
//			//String shortest = shortened.split("preconditions")[0];
//			//shortest = shortest + "preconditions...";
//			
//			writeConsole(shortened);
//			//possibleActionsTable.put(shortened, full);
//			//possibleActionsTable.put(full, full);
//		}
//		writeConsole("----- No more possible actions -----");
	}
	
	//updates the model of selectionList
	private void updateSelectionListModel(Hashtable<String, Vector<String>> allPossibleActionsByType) {
		Vector<String> listOfActions = new Vector<String>();
		numberOfActionsByType.clear();
		Enumeration<String> enumTypes = allPossibleActionsByType.keys();
		while(enumTypes.hasMoreElements()) {
			Vector<String> actionsTypeX = allPossibleActionsByType.get(enumTypes.nextElement());
			numberOfActionsByType.add(new Integer(actionsTypeX.size()));
			listOfActions.addAll(actionsTypeX);
		}
		selectionList.setModel(new DefaultComboBoxModel(listOfActions));
		//default, the selection is 0,0 instead of no selection at all
		setSelectedIndex(0, 0);
	}

	//looks if going to the destination is amongst the possible actions
	//sets the selected value of the selectionList to this action
	//returns 0 when already at destination
	//returns 1 when destination is allowed
	//returns -1 when destination is not allowed
	//returns 2 when it is unknown whether destination is allowed 
	public int tryPossibleDestination(String destination) {
		if (humanCharacterCurrentLocation==null || destination_action==null) {
			return 2;
		}
		if (humanCharacterCurrentLocation.equals(destination)) {
			return 0;
		} else if(destination_action.containsKey(destination)) {
			String action = destination_action.get(destination);
			if (action != null) {
				selectionList.setSelectedValue(action, true);
				return 1;
			}
		}
		clearSelectionList();
		return -1;
	}
	
	public void setSelectedIndex(int indexOfType, int indexOfAction) {
		//if only a type is selected
		if (indexOfAction==-1) {
			selectionList.clearSelection();
			skipRound.setSelected(false);
			interfacePanel.clearSelection();
			return;
		}
		
		//count up to the index in the selectionList
		int count = 0;
		for(int i = 0; i<indexOfType; i++) {
			count = count + numberOfActionsByType.get(i);
		}
		count = count + indexOfAction;
		selectionList.setSelectedIndex(count);
		
		skipRound.setSelected(false);
		
		interfacePanel.updateIndexSelectedAction(indexOfType, indexOfAction);
	}
	
	//clears selectionList
	public void clearSelectionList() {
		selectionList.clearSelection();
		interfacePanel.clearSelection();
	}
	
	//waits for the user to select an action and press "Execute" button
	//returns the String representation of the selected action
	//returns "" when the user wants to skip a round
	//returns null when interrupted
	public String getUserSelection() {
		//synchronize on a shared object so this thread can be waked up
		synchronized(sync) {
			interfacePanel.setWaitingForUserInput(true);
			selectionList.setEnabled(true);
			skipRound.setEnabled(true);
			skipRound.setSelected(false);
			try {
				//wait until "Execute" button sets currentSelection 
				while (currentSelection == null) {
					sync.wait();				
				}
			} catch (InterruptedException e) {
				//do nothing when this HumanCharacterAgent is interrupted
				//for instance: when the agent is killed
				return null;
			}
		}
		String result = new String(currentSelection);
		currentSelection = null;
		return result;
	}
	
	public void updateCenterOfInterface(int xCenterInterface, int yCenterInterface) {
		mapPanel.updateCenterOfInterface(xCenterInterface, yCenterInterface);
	}
	
	public Point tuioPointToPoint(TuioPoint tuioPoint) {
		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
		
		int x = tuioPoint.getScreenX(screenDimension.width);
		int y = tuioPoint.getScreenY(screenDimension.height);
		
		return  new Point(x, y);
	}	
	
	//called when someone or something changes the selection in the selectionList
	public void valueChanged(ListSelectionEvent e) {
		Object source = e.getSource();
		if (source.equals(selectionList)) {
			if (selectionList.isEnabled()) {
				int selectedIndex = selectionList.getSelectedIndex();
				
				//if there is no selection (anymore)
				if (selectedIndex == -1) {
					interfacePanel.clearSelection();
					if (skipRound.isSelected()) {
						executeButton.setEnabled(true);
					} else {
						executeButton.setEnabled(false);
					}
				} else {
					skipRound.setSelected(false);
					executeButton.setEnabled(true);
				
					int remaining = selectedIndex;
					int i = 0;
					int next = numberOfActionsByType.get(i);
					for(i = 0; remaining >= next; i++) {
						remaining = remaining - next;
						next = numberOfActionsByType.get(i+1);
					}
					
					int indexOfType = i;
					int indexOfAction = remaining;
					interfacePanel.updateIndexSelectedAction(indexOfType, indexOfAction);
				}
			}
		}
	}
	
	@Override
	//called when the execute Button or skipRound CheckBox are 'clicked' 
	public void actionPerformed(ActionEvent e) {
    	//(do not) give superclass opportunity to handle event
    	//super.actionPerformed(e);
    	Object source = e.getSource();
    	
    	if (source.equals(executeButton)) {
    		performSelectedAction();
    	} else if (source.equals(reconnectButton)) {
    		connect_to_TUIO_server();
    	} else if (source.equals(skipRound)) {
    		if (selectionList.isEnabled()) {
    			boolean skip = skipRound.isSelected();
//    			if(skip) {
    				clearSelectionList();
    				executeButton.setEnabled(skip);
    				interfacePanel.selectSkipRound(skip);
//    			} else {
//    				executeButton.setEnabled(false);
//    			}
    		}
    	}
    }
	
	public void performSelectedAction() {
		//synchronize so the thread that waits in getUserSelection() can be waked up
		synchronized(sync) {
			interfacePanel.setWaitingForUserInput(false);
			executeButton.setEnabled(false);
			skipRound.setEnabled(false);
			selectionList.setEnabled(false);
			if (skipRound.isSelected()) {
				currentSelection = "";
			} else {
				currentSelection = (String)selectionList.getSelectedValue();
			}
			sync.notify();
		}
	}
	
	public void selectSkipRound(boolean skip) {
		skipRound.setSelected(skip);
		clearSelectionList();
		if(selectionList.isEnabled()) {
			executeButton.setEnabled(skip);
		}
	}
	
	private class PossibleActionsListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList lst, Object obj, int arg2, boolean arg3, boolean arg4) {
			JLabel l = (JLabel)super.getListCellRendererComponent(lst, obj, arg2, arg3, arg4);
			String fullDescription = (String)obj;
			
			boolean constructReasonableDescription = false;
			if(debug || constructReasonableDescription) {
				l.setText(constructReasonableDescriptionFromAction(fullDescription));
			} else {
				String shortDescription = PrologKB.getInstance().narrate_imperative(fullDescription);
				l.setText(shortDescription.replaceAll("'", ""));
			}
			return l;
		}
	}
	
	private String constructReasonableDescriptionFromAction(String fullDescription) {

		return PrologKB.getInstance().narrate_category(fullDescription).replaceAll("'", "");
		
		/*
		
		//shorten the String that represents the action (remove some GENERAL strings)
//		String shortened = full.replaceAll("http://www.owl-ontologies.com/", "");
//		shortened = shortened.replaceAll("'.'", "");
//		//remove PARTICULAR String(s) in hardcoded way:
//		shortened = shortened.replaceAll("Red.owl#", "");
//		//shortened = shortened.replaceAll("Lollipop.owl#", "");
//		//String shortest = shortened.split("preconditions")[0];
//		//shortest = shortest + "preconditions...";

		//try to assemble a short and understandable String for each action 
		//this should be done more generic!
		//maybe by getType() and getTarget() and ...?
		//for now we use the following UGLY approach :-)
		String[] trysplit = fullDescription.split("preconditions");
		if(trysplit.length>1) {
    		String shortened = trysplit[0];
    		shortened = shortened.replaceAll("http://www.owl-ontologies.com/", "");
    		shortened = shortened.replaceAll("http:", "");
    		shortened = shortened.replaceAll("//www.owl-ontologies.com/", "");
    		
    		//XXX: remove PARTICULAR String(s) in hardcoded way:
    		//shortened = shortened.replaceAll("StoryWorldSettings/", "");
			shortened = shortened.replaceAll("Red.owl#", "");
			//shortened = shortened.replaceAll("FabulaKnowledge.owl#", "");
			//shortened = shortened.replaceAll("Pirates_Jasper2.owl#", "");
			
			String agensName = shortened.split("agens")[1].split(",")[0];
			shortened = shortened.replaceAll(agensName, "");
			
			shortened = shortened.replaceAll("'", "");
			shortened = shortened.replace('.', ',');
			shortened = shortened.replace('(', ',');
			shortened = shortened.replace(')', ',');
			shortened = shortened.replace('[', ',');
			shortened = shortened.replace(']', ',');
			shortened = shortened.replaceAll(",", "");
			
			shortened = shortened.replaceAll("type", "");
			shortened = shortened.replaceAll("agens", "");
			shortened = shortened.replaceAll("patiens", "");
			shortened = shortened.replaceAll("arguments", "");
			shortened = shortened.replaceAll("target", "");
				    			
			shortened = shortened.replaceFirst(" ", ":");
			shortened = shortened.replaceAll(" ", "");
			shortened = shortened.replaceFirst(":", " ");
			shortened = shortened.replaceAll("duration", " (duration = ");
			shortened = shortened.replaceAll("  ", " ");
			shortened = shortened + ")";
			
			return shortened;
		} else {
			String shortened = fullDescription.replaceAll("http://www.owl-ontologies.com/", "");
    		
    		//XXX: remove PARTICULAR String(s) in hardcoded way:
			shortened = shortened.replaceAll("Red.owl#", "");
			shortened = shortened.replaceAll("FabulaKnowledge.owl#", "");
			shortened = shortened.replaceAll("Pirates_Jasper2.owl#", "");
			shortened = shortened.replaceAll("StoryWorldSettings/", "");
			shortened = shortened.replaceAll("'", "");
			
			return shortened;
		}*/
	}
	
	//only for quick startup and testing, not for running anything
	//will throw nullpointer exception eventually!
	public static void main(String[] args) {
		JFrame jf = new JFrame("Debug MapFrame.java");
		JButton start = new JButton("start new MapFrame");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MapFrame mf = new MapFrame(null);
				mf.debug = true;
				mf.setHumanCharacterName("testhenk");
				mf.setDomainShowMap("red", new Vector<String>());
				mf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			}});
		jf.add(start);
		jf.setVisible(true);
		jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jf.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
}
