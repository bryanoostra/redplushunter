package vs.plotagent.ui;

import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.hp.hpl.jena.shared.PrefixMapping;

import vs.Config;
import vs.debug.LogFactory;
import vs.fabula.FabulaFactory;
import vs.knowledge.PrologKB;
import vs.plotagent.BasicPlotAgent;
import vs.rationalagent.RationalAgent;
import vs.rationalagent.StoryAgentEventListener;
import vs.rationalagent.ui.StoryAgentEvent;

public class UserActionSelectionPanel extends JPanel implements ActionListener, ListSelectionListener, StoryAgentEventListener {
	
	protected Logger logger;
	
	private PrefixMapping pm;
	
	private JLabel infoLabel;
	private JLabel characterLabel;
	private JList selectionList;
	private JButton executeButton;
	private JCheckBox skipRound;	
	
	private String currCharacterURI = "";
	private RationalAgent myAgent;
	
	
	public UserActionSelectionPanel(RationalAgent owner) {
		
		super(new BorderLayout());
		
		myAgent = owner;
		
		//initialize the logger
		logger = LogFactory.getLogger(this);
		
		//used for shortening names
		pm = PrefixMapping.Factory.create();
		pm.setNsPrefixes(Config.namespaceMap);		
		
		setup();
	}
	
	/**
	 * Handle story events
	 */
	public void onStoryAgentEvent(StoryAgentEvent sae) {
		// operator result
		if (sae instanceof DomainLoadedEvent) {
			// Domain is loaded
			updateLocationsInhabitants();
		} else
		if (sae instanceof OperatorResultEvent) {
			// Received operator result
			// might have changed locations of characters, and actions that are possible
			updateLocationsInhabitants();
			updatePossibleActions(currCharacterURI);
		} else
		if (sae instanceof SelectActionEvent) {
			// Action must be selected: determine actions that are possible for character			
			currCharacterURI = ((SelectActionEvent)sae).getCharacterURI();
			updatePossibleActions(currCharacterURI);
			selectionList.setEnabled(true);
			skipRound.setEnabled(true);
			skipRound.setSelected(false);
		} else
		if (sae instanceof AgentSelectActionEvent) {
			//The AI controlled agent is selecting an action
			selectionList.setEnabled(false);
			skipRound.setEnabled(false);
			skipRound.setSelected(false);
		} 	
	}
	
	/**
	 * Handle user actions
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
    	if (source.equals(executeButton)) {
    		performSelectedAction();
    	} else if (source.equals(skipRound)) {
    		if (selectionList.isEnabled()) {
    			boolean skip = skipRound.isSelected();

				selectionList.clearSelection();
				executeButton.setEnabled(skip);
    		}
    	}

		
		// selecting an action
		//selectionList.setEnabled(false);
		
		// taking control of character
		
		// returning control of character
		
		// Start story / next round
		
    }	
	
	public void performSelectedAction() {
		//synchronize so the thread that waits in getUserSelection() can be waked up
		executeButton.setEnabled(false);
		skipRound.setEnabled(false);
		selectionList.setEnabled(false);

		GuiEvent ev = new GuiEvent( null, BasicPlotAgent.ACTION_CHOSEN);
		ev.addParameter(currCharacterURI);

		if (skipRound.isSelected()) {
			ev.addParameter(null);
		} else {
			String currentSelection = (String)selectionList.getSelectedValue();
			ev.addParameter(FabulaFactory.createUnknownOperator(currentSelection, currCharacterURI));
		}
		
		myAgent.postGuiEvent(ev);
	}
	
	private void setup() {
		infoLabel = new JLabel(" Select an action for character: ");
		characterLabel = new JLabel("[none]");
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
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(skipRound);
		buttonPanel.add(executeButton);
		buttonPanel.add(Box.createVerticalGlue());		
		
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.add(infoLabel, BorderLayout.NORTH);
		infoPanel.add(characterLabel, BorderLayout.CENTER);
		
		add(infoPanel, BorderLayout.NORTH);
		add(listPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);		

	}	
	
	private void updateLocationsInhabitants() {
		Hashtable<String,Vector<String>> location_inhabitants = PrologKB.getInstance().getAllLocationsInhabitants();
		
		Iterator<Entry<String, Vector<String>>> it = location_inhabitants.entrySet().iterator();
		//hgui.writeConsole("===== Which (known) characters are at the (known) locations? =====");
		while(it.hasNext()) {
			Entry<String, Vector<String>> e = (Entry<String, Vector<String>>)it.next();
			String location = (String)e.getKey();
			Vector<String> inhabitants = (Vector<String>)e.getValue();
			String place = pm.shortForm(location.replace("'", ""));
			String description = "@ " + place + " = ";
			Iterator<String> itChar = inhabitants.iterator();
			while (itChar.hasNext()) {
				String name = itChar.next();

				name = name.replace("'", "");
				description = description + pm.shortForm(name) + " ";
			}
			//hgui.writeConsole(description);
		}
		//hgui.writeConsole("----- No more (known) locations with (known) characters -----");		
		
		// TODO: show visually where everyone is.
	}
	 
	private void updatePossibleActions(String characterURI) {
		logger.info("Updating possible actions for character: " + characterURI);
		Vector<String> allPossibleActions = PrologKB.getInstance().getAllPossibleActions(characterURI);
		characterLabel.setText(characterURI);
		selectionList.setModel(new DefaultComboBoxModel(allPossibleActions));
						
	}
	
	//called when someone or something changes the selection in the selectionList
	public void valueChanged(ListSelectionEvent e) {
		Object source = e.getSource();
		if (source.equals(selectionList)) {
			if (selectionList.isEnabled()) {
				int selectedIndex = selectionList.getSelectedIndex();
				
				//if there is no selection (anymore)
				if (selectedIndex == -1) {
					
					if (skipRound.isSelected()) {
						executeButton.setEnabled(true);
					} else {
						executeButton.setEnabled(false);
					}
				} else {
					skipRound.setSelected(false);
					executeButton.setEnabled(true);
				
				}
			}
		}
	}	
	
	private class PossibleActionsListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList lst, Object obj, int arg2, boolean arg3, boolean arg4) {
			JLabel l = (JLabel)super.getListCellRendererComponent(lst, obj, arg2, arg3, arg4);
			String fullDescription = (String)obj;
			
			String shortDescription = PrologKB.getInstance().narrate_imperative(fullDescription);
			l.setText(shortDescription.replaceAll("'", ""));

			return l;
		}
	}	

}
