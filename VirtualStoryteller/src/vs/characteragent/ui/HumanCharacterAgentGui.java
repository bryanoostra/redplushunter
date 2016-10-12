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
package vs.characteragent.ui;

import jade.gui.GuiAgent;
import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import vs.Config;
import vs.characteragent.BasicCharacterAgent;
import vs.characteragent.ICharacterAgent;
import vs.debug.LogFactory;
import vs.fabula.io.LanguageFilter;
import vs.fabula.ui.FabulaPanel;
import vs.rationalagent.RationalAgent;
import vs.rationalagent.ui.RationalAgentGui;

import com.hp.hpl.jena.shared.PrefixMapping;

/**
 * Human Character Agent GUI
 * 
 * @author Thijs Alofs
 * 
 * largely based on CharacterAgentGui
 */

public class HumanCharacterAgentGui extends RationalAgentGui {

	private static final long serialVersionUID = 427278786666052587L;

	protected JFrame frame = this;
	private Logger logger;
	
	private PlanGraphPanel graphPane;
	private JMenu menu_CA;
	
	protected JPanel m_mindTreePanel;
	protected JTree m_mindTree;
	
	private FabulaPanel m_fabulaPanel;
	private JPanel m_fabulaTabPanel;	
	
	protected transient Action saveEpisodicMemoryAction;
	protected transient Action showEpisodicMemoryAction;
	protected transient Action planAction;

//	private JFrame mapInterface;
//	private MapCanvas mapCanvas;
//	private JLabel infoLabel;
//	private JList selectionList;
//	private JButton executeButton;
//	private JCheckBox skipRound;

//	private String currentSelection = "";
//	private String humanCharacterName;
//	private String humanCharacterCurrentLocation;
//	
//	private Hashtable<String, String> destination_action;
	
	private PrefixMapping pm;
//	private Object sync = new Object();

	public HumanCharacterAgentGui(RationalAgent a) {
		super(a);
		
		//used for shortening names
		pm = PrefixMapping.Factory.create();
		pm.setNsPrefixes(Config.namespaceMap);
				
		logger = LogFactory.getLogger(this);
		
		registerCommand( new GetAgentIDCommand( myAgent, this ));
		
		//Thijs: best to do this here?
		//initMapFrame();
	}
	
	/**
	 * layout() creates the layout of the gui
	 */
	@Override
	protected void guiLayout() {
		super.guiLayout();
		
		//Frame settings
		setTitle(myAgent.getLocalName() + " (HumanCharacterAgentGui for " + myAgent.getClass() + ")");
		this.setLocationRelativeTo(null);
				
		 // Set icon
	    Image icon = Toolkit.getDefaultToolkit().getImage("img/ca_small.gif");
	    setIconImage(icon);
	    
		m_fabulaTabPanel = new JPanel();
		m_fabulaPanel = null;
		
		m_tabPane.add(m_fabulaTabPanel, "Episodic Memory");		
	}
	
	/**
	 * setup() creates the elements of the gui
	 * and adds event listeners
	 */
	@Override
	protected void guiSetup() {
		// CharacterAgentGui is an extension of RationalAgentGui
		super.guiSetup();
		
		saveEpisodicMemoryAction = new SaveEpisodicMemoryAction("Save episodic memory", null, null, null);
		showEpisodicMemoryAction = new ShowEpisodicMemoryAction("Show episodic memory graph", null, null, null); 
		
		//XXX: we are not making plans, are we?
		planAction = new PlanAction("Make plan for current goal", null, null, null);
	
		// Character Agent menu
			menu_CA = new JMenu("Character Agent");
				
			menu_CA.add(saveEpisodicMemoryAction);
			menu_CA.add(showEpisodicMemoryAction);
			menu_CA.add(planAction);
			
		menuBar.add(menu_CA);
		  
		//Thijs: there is (currently) no mind to display
		//m_mindTreePanel = new MindDisplayer((BasicCharacterAgent) myAgent);				
	    //m_tabPane.add("Mind", m_mindTreePanel);
	}
	
	//initialise the mapInterface JFrame
	private void initMapFrame() {
		//mapInterface = new MapFrame(this);
//		mapInterface.setTitle("Map of Story World");
//		mapInterface.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//				
//		JPanel bottomBar = new JPanel(new BorderLayout());
//		JSeparator separator = new JSeparator();
//		infoLabel = new JLabel(" Select an action:  ");
//		//String[] dummy = {"action one", "action two", "test action"};
//		selectionList = new JList();
//		ListSelectionModel lsm = selectionList.getSelectionModel();
//		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		selectionList.setEnabled(false);
//		//Make a custom ListCellRenderer to display a shorter String representation of the actions
//		selectionList.setCellRenderer(new DefaultListCellRenderer() {
//			public Component getListCellRendererComponent(JList lst, Object obj, int arg2, boolean arg3, boolean arg4) {
//				JLabel l = (JLabel)super.getListCellRendererComponent(lst, obj, arg2, arg3, arg4);
//				String full = (String)obj;
//				//shorten the String that represents the action (remove some GENERAL strings)
//				String shortened = full.replaceAll("http://www.owl-ontologies.com/", "");
//				shortened = shortened.replaceAll("'.'", "");
//				//XXX: remove PARTICULAR String(s) in hardcoded way:
//				shortened = shortened.replaceAll("Red.owl#", "");
//				//shortened = shortened.replaceAll("Lollipop.owl#", "");
//				//String shortest = shortened.split("preconditions")[0];
//				//shortest = shortest + "preconditions...";
//				l.setText(shortened);
//				return l;
//			}
//		});
//		selectionList.addListSelectionListener(this);
//		JScrollPane listPanel = new JScrollPane(selectionList);
//		executeButton = new JButton("Execute");
//		executeButton.setEnabled(false);
//		executeButton.addActionListener(this);
//		skipRound = new JCheckBox("Skip Round", false);
//		skipRound.setEnabled(false);
//		skipRound.addActionListener(this);
//		JPanel buttonPanel = new JPanel();
//		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
//		buttonPanel.add(Box.createVerticalGlue());
//		buttonPanel.add(skipRound);
//		buttonPanel.add(executeButton);
//		buttonPanel.add(Box.createVerticalGlue());
//		bottomBar.add(separator, BorderLayout.NORTH);
//		bottomBar.add(infoLabel, BorderLayout.WEST);
//		bottomBar.add(listPanel, BorderLayout.CENTER);
//		bottomBar.add(buttonPanel, BorderLayout.EAST);
//		
//		//uiGlassPane = new InterfaceGlassPane();
//		mapCanvas = new MapCanvas(this);
//			
//		mapInterface.setLayout(new BorderLayout());
//		mapInterface.add(bottomBar, BorderLayout.SOUTH);
//		mapInterface.add(mapCanvas, BorderLayout.NORTH);
	}
	
	private void refreshEpisodicMemoryPanel() {
		if (m_fabulaPanel == null) {
			m_fabulaPanel = new FabulaPanel(((ICharacterAgent) myAgent).getEpisodicMemory(), myAgent.getStoryDomain());
		} else {
			m_fabulaTabPanel.remove(m_fabulaPanel);
		}
		
		m_fabulaPanel.update();
		m_fabulaPanel.setPreferredSize(m_fabulaTabPanel.getSize());
		m_fabulaTabPanel.add(m_fabulaPanel);
		
		repaint();
	}
	
	public PlanGraphPanel getGraphPane() {
		if (graphPane == null && ((ICharacterAgent)myAgent).getCharacterProcess().getDeliberativeLayer() != null) {
			graphPane = new PlanGraphPanel(((ICharacterAgent)myAgent).getCharacterProcess().getDeliberativeLayer().getPlanner());
       		m_tabPane.add("Plan Graph", graphPane);
		} 
		return graphPane;
	}
	
//	//sets the characterName
//	public void setHumanCharacterName(String characterURI) {
//		this.humanCharacterName = "'" + characterURI + "'";
//		mapInterface.setTitle(mapInterface.getTitle()+ "  (playing " + pm.shortForm(characterURI) + ")");
//		
//		//set the HumanCharacterName in the mapCanvas 
//		mapCanvas.setHumanCharacterName(characterURI);
//	}
//	
//	//sets the domain
//	//shows the window with the map interface
//	public void setDomainShowMap(String domain) {
//		//try to set the domain (load map and character images)
//		try {
//			mapCanvas.setDomain(domain);
//		} catch (IOException ioe) {	
//			//throws IOException if the map image file is not available
//			//this should never happen, all images should be available
//			String message = "Problem while loading domain: " + ioe.getMessage();
//			logger.severe(message);
//			this.writeConsole(message);
//			//do or do not continue when a problem occurs:
//			//return;
//		}
//		
//		//now everything is set, we can show the window with the mapInterface
//		mapInterface.setVisible(true);
//		int listHeight = selectionList.getPreferredScrollableViewportSize().height;
//		Dimension canvasDimension = mapCanvas.getPreferredSize();
//		int canvasWidth = canvasDimension.width;
//		int canvasHeight = canvasDimension.height;
//		//mapInterface.setSize(1280, 50 + canvasHeight + listHeight);
//		mapInterface.setSize(canvasWidth + 15, 50 + canvasHeight + listHeight);
//		mapInterface.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		//mapInterface.setResizable(false);
//		
//		//mapInterface.setGlassPane(uiGlassPane);
//		//uiGlassPane.setVisible(true);
//		
//		mapInterface.requestFocus();
//	}
//	
//	public void updateRoundNumber(int round) {
//		infoLabel.setText(" Select an action (round " + round + "):  ");
//	}
//	
//	//prints the location inhabitants in the console
//	//finds and stores the current location of the human character
//	//forwards the location_inhabitants information to the mapCanvas
//	public void updateLocationsInhabitants(Hashtable<String, Vector<String>> location_inhabitants) {
//		Iterator<Entry<String, Vector<String>>> it = location_inhabitants.entrySet().iterator();
//		writeConsole("===== Which (known) characters are at the (known) locations? =====");
//		while(it.hasNext()) {
//			Entry<String, Vector<String>> e = (Entry<String, Vector<String>>)it.next();
//			String location = (String)e.getKey();
//			Vector<String> inhabitants = (Vector<String>)e.getValue();
//			String place = pm.shortForm(location.replace("'", ""));
//			String description = "@" + place + " = ";
//			Iterator<String> itChar = inhabitants.iterator();
//			while (itChar.hasNext()) {
//				String name = itChar.next();
//				//Store the current location of the human character
//				if (name.equals(humanCharacterName)) {
//					this.humanCharacterCurrentLocation = location;
//				}
//				name = name.replace("'", "");
//				description = description + pm.shortForm(name) + " ";
//			}
//			writeConsole(description);
//		}
//		writeConsole("----- No more (known) locations with (known) characters -----");
//
//		//now finally update the inhabitants of each location on the map
//		mapCanvas.updateLocationsInhabitants(location_inhabitants);
//	}
//	
//	//called to update the currently possible transitMove actions
//	public void updatePossibleTransitMoveActions(Hashtable<String,String> location_action) {
//		destination_action = location_action;
//	}
//	
//	//called to update the currently possible actions
//	public void updatePossibleActions(Vector<String> allPossibleActions) {
//		selectionList.setModel(new DefaultComboBoxModel(allPossibleActions));
//		
////		writeConsole("===== What are my possible actions? =====");
////		//possibleActionsTable = new Hashtable<String, String>();
////		Iterator<String> it = allPossibleActions.iterator();
////		while (it.hasNext()) {
////			String full = (String)it.next();
////			
////			//shorten the String that represents the action (remove some GENERAL strings)
////			String shortened = full.replaceAll("http://www.owl-ontologies.com/", "");
////			shortened = shortened.replaceAll("'.'", "");
////
////			//remove PARTICULAR String(s) in rather ugly (hardcoded) way:
////			shortened = shortened.replaceAll("Red.owl#", "");
////			//shortened = shortened.replaceAll("Lollipop.owl#", "");
////			//etc...
////			
////			//String shortest = shortened.split("preconditions")[0];
////			//shortest = shortest + "preconditions...";
////			
////			writeConsole(shortened);
////			//possibleActionsTable.put(shortened, full);
////			//possibleActionsTable.put(full, full);
////		}
////		writeConsole("----- No more possible actions -----");
//	}
//	
//	//looks if going to the destination is amongst the possible actions
//	//sets the selected value of the selectionList to this action
//	//returns 0 when already at destination
//	//returns 1 when destination is allowed
//	//returns -1 when destination is not allowed
//	public int tryPossibleDestination(String destination) {
//		if (humanCharacterCurrentLocation.equals(destination)) {
//			return 0;
//		} else if(destination_action.containsKey(destination)) {
//			String action = destination_action.get(destination);
//			if (action !=null) {
//				selectionList.setSelectedValue(action, true);
//				return 1;
//			}
//		}
//		clearSelectionList();
//		return -1;
//	}
//	
//	//clears selectionList
//	public void clearSelectionList() {
//		selectionList.clearSelection();
//	}
//	
//	//waits for the user to select an action and press "Execute" button
//	//returns the String representation of the selected action
//	//returns "" when the user wants to skip a round
//	//returns null when interrupted
//	public String getUserSelection() {
//		//synchronize on a shared object so this thread can be waked up
//		synchronized(sync) {
//			selectionList.setEnabled(true);
//			skipRound.setEnabled(true);
//			skipRound.setSelected(false);
//			try {
//				//wait until "Execute" button sets currentSelection 
//				while (currentSelection == null) {
//					sync.wait();				
//				}
//			}catch (InterruptedException e) {
//				//do nothing when this HumanCharacterAgent is interrupted
//				//for instance: when the agent is killed
//				return null;
//			}
//		}
//		String result = new String(currentSelection);
//		currentSelection = null;
//		return result;
//	}

//	//called when someone or something changes the selection in the selectionList
//	public void valueChanged(ListSelectionEvent e) {
//		Object source = e.getSource();
//		if (source.equals(selectionList)) {
//			if (selectionList.isEnabled()) {
//				if (selectionList.getSelectedValue() == null) {
//					if (skipRound.isSelected()) {
//						executeButton.setEnabled(true);
//					} else {
//						executeButton.setEnabled(false);
//					}
//				} else {
//					skipRound.setSelected(false);
//					executeButton.setEnabled(true);
//				}
//			}
//		}
//	}
	
//	@Override
//	//called when the execute Button or skipRound CheckBox are 'clicked' 
//	public void actionPerformed(ActionEvent e) {
//    	// give superclass opportunity to handle event
//    	super.actionPerformed( e );
//    	Object source = e.getSource();
//    	
//    	if (source.equals(executeButton)) {
//    		//synchronize so the thread that waits in getUserSelection() can be waked up
//			synchronized(sync) {
//				writeConsole("selectionButton actionPerformed()");
//				executeButton.setEnabled(false);
//				skipRound.setEnabled(false);
//				selectionList.setEnabled(false);
//				if (skipRound.isSelected()) {
//					currentSelection = "";
//				} else {
//					currentSelection = (String)selectionList.getSelectedValue();
//				}
//				sync.notify();
//			}
//			
//    	} else if (source.equals(skipRound)) {
//    		if (selectionList.isEnabled()) {
//    			if(skipRound.isSelected()) {
//    				selectionList.clearSelection();
//    				executeButton.setEnabled(true);
//    			} else {
//    				executeButton.setEnabled(false);
//    			}
//    		}
//    	}
//    }

	class SaveEpisodicMemoryAction extends ExtendedAction {

		private static final long serialVersionUID = 546008773071285944L;

		public SaveEpisodicMemoryAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			LanguageFilter trigFilter = new LanguageFilter("TriG", ".trig", "TRIG", false);	
			LanguageFilter trixFilter = new LanguageFilter("TriX", ".trix", "TRIX", false);
			//File f = new File ("");
			//chooser.setSelectedFile(f);
			chooser.setAcceptAllFileFilterUsed(false);

			chooser.setFileFilter(trixFilter);
			chooser.setFileFilter(trigFilter);

			File returnFile = LanguageFilter.showSaveDialog(chooser, frame);
			String language = null;

			if (returnFile != null) {
	
				LanguageFilter filter = (LanguageFilter) chooser.getFileFilter();
				if (filter == trigFilter || filter == trixFilter) {
					language = filter.getDescription();
			
					GuiEvent ev = new GuiEvent(null, BasicCharacterAgent.SAVEEPISODICMEMORY);
					ev.addParameter(returnFile);
					ev.addParameter(filter);
					((GuiAgent) myAgent).postGuiEvent(ev);
				}
			}
		}
	}
	
	class ShowEpisodicMemoryAction extends ExtendedAction {

		private static final long serialVersionUID = -7623891899230883399L;

		public ShowEpisodicMemoryAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshEpisodicMemoryPanel();
		}
	}
	class WATreeNode extends DefaultMutableTreeNode {
		
		private static final long serialVersionUID = 4900371788183258350L;

		public WATreeNode(String value) {
			super(value);
		}
	}
	
	class PlanAction extends ExtendedAction {

		private static final long serialVersionUID = 4557230247889641570L;

		public PlanAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GuiEvent ev = new GuiEvent(null, BasicCharacterAgent.CREATEPLAN);
			((GuiAgent) myAgent).postGuiEvent(ev);
		}
	}	
}
