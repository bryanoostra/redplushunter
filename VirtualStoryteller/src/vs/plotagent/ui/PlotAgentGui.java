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
package vs.plotagent.ui;

import jade.core.AID;
import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Set;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.hp.hpl.jena.shared.PrefixMapping;

import jpl.Variable;
import vs.Config;
import vs.fabula.io.LanguageFilter;
import vs.fabula.ui.FabulaPanel;
import vs.knowledge.PrologKB;
import vs.knowledge.vocab.Fabula;
import vs.plotagent.BasicPlotAgent;
import vs.plotagent.ICharacterManager;
import vs.plotagent.IPlotAgent;
import vs.plotagent.inspiration.OperationalizedSuggestion;
import vs.plotagent.inspiration.Suggestion;
import vs.plotagent.ui.multitouch.controller.VSTMultitouchController;
import vs.plotagent.ui.multitouch.view.SoundManager;
import vs.rationalagent.RationalAgent;
import vs.rationalagent.ui.RationalAgentGui;
import vs.rationalagent.ui.StoryAgentEvent;


/**
 * GUI for plot agent
 * 
 * @author swartjes
 * Created on 14-oct-2005
 */
public class PlotAgentGui extends RationalAgentGui {

	/////////////////////////////////////////////
	// ATTRIBUTES
	/////////////////////////////////////////////

	class AgentsRenderer extends DefaultTreeCellRenderer {
	    Icon m_waIcon;
	    Icon m_caIcon;

	    public AgentsRenderer(Icon waIcon, Icon caIcon) {
	        m_waIcon = waIcon;
	        m_caIcon = caIcon;
	    }

	    @Override
		public Component getTreeCellRendererComponent(
	                        JTree tree,
	                        Object value,
	                        boolean sel,
	                        boolean expanded,
	                        boolean leaf,
	                        int row,
	                        boolean hasFocus) {

	        super.getTreeCellRendererComponent(
	                        tree, value, sel,
	                        expanded, leaf, row,
	                        hasFocus);
        	if (value instanceof WATreeNode) {
        		setIcon(m_waIcon);	        		
        	}
        	if (value instanceof CATreeNode) {
        		setIcon(m_caIcon);
        	}
        	
        	this.setText(value.toString());

	        return this;
	    }	
	}
	class CATreeNode extends DefaultMutableTreeNode {
		protected AID characterAgent;
		public CATreeNode(String value, AID ca) {
			super(value);
			characterAgent = ca;
		}
		
		public AID getAID() {
			return characterAgent;
		}
	}
	class GetSuggestionsAction extends ExtendedAction {

		/**
		 * 
		 */

		public GetSuggestionsAction(String text, ImageIcon icon, String desc,
				Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Set<Suggestion> suggestions = (((BasicPlotAgent) myAgent)
					.getInspirationModule().getSuggestions()); 
			for (Suggestion suggestion : suggestions) {
				writeConsole(suggestion.toString(), true);
				Variable nodes = new Variable("Nodes");

				// Check for fabula nodes
				Vector<String> fabulaNodes = ((BasicPlotAgent) myAgent)
						.getKnowledgeManager().getPrologSingleVariableList(
								PrologKB.nodeClass, suggestion.getBody() + ", '" + Fabula.Event + "'");

				for (String n : fabulaNodes) {
					writeConsole("Event node: " + n);
					Vector<String> validatedEvents = ((BasicPlotAgent) myAgent)
							.getKnowledgeManager().getPrologSingleVariableList(
									PrologKB.createValidatedEvent,
									suggestion.getBody() + "," + n);
					for (String vEv : validatedEvents) {
						writeConsole("Leading to event: \n" + vEv);
					}
				}
			}
			
			// operationalize
			
			Set<OperationalizedSuggestion> osSet = ((BasicPlotAgent) myAgent).getInspirationModule().operationalizeSuggestions(suggestions);
			for (OperationalizedSuggestion o: osSet) {
				writeConsole("Operationalized suggestion: \n" + o.toString());
			}
		}
	}
	class PulseAction extends ExtendedAction {

		/**
		 * 
		 */

		public PulseAction(String text, ImageIcon icon, String desc,
				Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GuiEvent ev = new GuiEvent( null, BasicPlotAgent.NEXT_ROUND);
			myAgent.postGuiEvent(ev);
			//((BasicPlotAgent) myAgent).nextRound();
		}
	}
	class SaveFabulaAction extends ExtendedAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5681926205350781775L;

		public SaveFabulaAction(String text, ImageIcon icon, String desc,
				Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			//File f = new File ("");
			//chooser.setSelectedFile(f);
			chooser.setAcceptAllFileFilterUsed(false);

			chooser.setFileFilter(trixFilter);
			chooser.setFileFilter(trigFilter);

			File returnFile = LanguageFilter.showSaveDialog(chooser, frame);
			String language = null;

			if (returnFile != null) {

				LanguageFilter filter = (LanguageFilter) chooser
						.getFileFilter();
				if (filter == trigFilter || filter == trixFilter) {
					language = filter.getDescription();
					
					//OntModel mod = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
					((IPlotAgent) myAgent).getFabulaBuilder().saveFabula(
							chooser.getSelectedFile(), filter.getLanguage());
				}

			}
		}
	}
	class ShowFabulaAction extends ExtendedAction {

		/**
		 * 
		 */

		public ShowFabulaAction(String text, ImageIcon icon, String desc,
				Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshFabulaPanel();
		}
	}
	
	class ShowMapAction extends ExtendedAction {

		/**
		 * 
		 */
		public ShowMapAction(String text, ImageIcon icon, String desc,
				Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			multitouchMap.showMultitouchMap();
			JMenu menu_reconnect = new JMenu("Reconnect");
			menu_reconnect.add(new ReconnectLoquendoAction("(Re)connect with Loquendo TTS server", null, null, null));
			menuBar.add(menu_reconnect);
			menuBar.revalidate();
		}
	}
	
	class ReconnectLoquendoAction extends ExtendedAction {

		public ReconnectLoquendoAction(String text, ImageIcon icon, String desc, Integer mnemonic) {
			super(text, icon, desc, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Thread connect = new Thread(){
				public void run() {
					SoundManager.getInstance().connect();
				}
			};
			connect.start();
		}
	}
	
	class TakeControlAction extends ExtendedAction {

		/**
		 * 
		 */
		protected String charURI;
		public TakeControlAction(String text, ImageIcon icon, String desc,
				Integer mnemonic, String character) {
			super(text, icon, desc, mnemonic);
			charURI = character;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GuiEvent ev = new GuiEvent( null, BasicPlotAgent.TAKE_CONTROL);
			ev.addParameter(charURI);
			myAgent.postGuiEvent(ev);
			//((BasicPlotAgent) myAgent).nextRound();
		}
	}
	
	class ReleaseControlAction extends ExtendedAction {

		/**
		 * 
		 */
		protected String charURI;
		public ReleaseControlAction(String text, ImageIcon icon, String desc,
				Integer mnemonic, String character) {
			super(text, icon, desc, mnemonic);
			charURI = character;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GuiEvent ev = new GuiEvent( null, BasicPlotAgent.RELEASE_CONTROL);
			ev.addParameter(charURI);
			myAgent.postGuiEvent(ev);
			//((BasicPlotAgent) myAgent).nextRound();
		}
	}	
		
	
	class WATreeNode extends DefaultMutableTreeNode {
		public WATreeNode(String value) {
			super(value);
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5620849255011107225L;

	//CONSTANTS
	private final static int FRAME_WIDTH = 300;
	private final static int FRAME_HEIGHT = 300;
	// VARIABLES
	private JTextArea m_infoArea;
	private JTextArea m_storyLogArea;
	private JScrollPane m_storyLogScrollPane;
	private JToolBar m_toolBar;
	private JComponent m_infoComponent;
	private JFrame m_fabulaFrame;
	
	private JMenu menu_PA;

	private JMenuItem menuItem_PA_Graph;
	private JMenuItem menuItem_PA_SaveFabula;	
	
	private JTree m_agentTree;
	
	private JPanel m_agentTreePanel;

	private JTree m_suggestionsTree;
	private JPanel m_suggestionsTreePanel;
	
	private JCheckBox m_autoNxtRound;

	/////////////////////////////////////////////
	// CONSTRUCTORS AND INSTANTIATION
	/////////////////////////////////////////////

	private JPanel m_fabulaTabPanel;

	/////////////////////////////////////////////
	// SETUP
	/////////////////////////////////////////////

	private FabulaPanel m_fabulaPanel;
	
	private VSTMultitouchController multitouchMap;


	/////////////////////////////////////////////
	// LAYOUT
	/////////////////////////////////////////////

	protected transient Action saveFabulaAction, showFabulaAction, getSuggestionsAction,
			pulseAction;

	/////////////////////////////////////////////
	// METHODS
	/////////////////////////////////////////////

	protected final LanguageFilter trigFilter = new LanguageFilter("TriG",
			".trig", "TRIG", false);
	
	protected final LanguageFilter trixFilter = new LanguageFilter("TriX",
			".trix", "TRIX", false);
	

	protected JFrame frame = this;
	protected JPopupMenu popup;
	
	protected PrefixMapping pm;	
	
	public PlotAgentGui(RationalAgent a) {
		super(a);
		//registerCommand( new ListFabulaCommand( (RationalAgent) myAgent, this ));
		//registerCommand( new TellFabulaCommand( (RationalAgent) myAgent, this ));
		//registerCommand( new AskFabulaCommand( (RationalAgent) myAgent, this ));
		//registerCommand(new StartStoryCommand(myAgent, this));
		registerCommand(new PulseCommand(myAgent, this));
		registerCommand(new GetSuggestionsCommand(myAgent, this));
		
		pm = PrefixMapping.Factory.create();

		pm.setNsPrefixes(Config.namespaceMap);
	}

	/**
	 * action event handler
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// give superclass opportunity to handle event
		super.actionPerformed(e);
		Object source = e.getSource();
		if (source == menuItem_PA_Graph) {
			GuiEvent ev = new GuiEvent(null, BasicPlotAgent.SHOWFABULA);
			myAgent.postGuiEvent(ev);
		}

	}

	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	private void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}
	
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	/////////////////////////////////////////////
	// EVENT HANDLING
	/////////////////////////////////////////////

	/**
	 * layout() creates the layout of the gui
	 */
	@Override
	protected void guiLayout() {
		// same layout
		super.guiLayout();
		
		 // Set icon
	    Image icon = Toolkit.getDefaultToolkit().getImage("img/pa_small.gif");
	    setIconImage(icon);		

		m_infoArea = new JTextArea(20, 60);
		m_storyLogArea = new JTextArea(20, 60);
		m_storyLogScrollPane = 
		    new JScrollPane(m_storyLogArea,
		                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			m_storyLogArea.setEditable(false);
		//m_infoComponent = new JPanel();
		//m_infoComponent.add(m_infoArea);


		popup = new JPopupMenu();
	    popup.setOpaque(true);
	    popup.setLightWeightPopupEnabled(true);

			
		m_agentTree = makeAgentTree();
		
		m_agentTreePanel = new JPanel(new BorderLayout());
		m_agentTreePanel.add(m_agentTree, BorderLayout.CENTER);
		JScrollPane agentScroll = new JScrollPane(m_agentTreePanel);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Suggestions");
		m_suggestionsTree = new JTree(root);
		m_suggestionsTreePanel = new JPanel(new BorderLayout());		
		m_suggestionsTreePanel.add(m_suggestionsTree, BorderLayout.CENTER);
		JScrollPane suggScroll = new JScrollPane(m_suggestionsTreePanel);
		
		m_fabulaTabPanel = new JPanel();
		m_fabulaPanel = null;
		
		m_tabPane.add(agentScroll, "Agents");
		//m_tabPane.add(suggScroll, "Suggestions");
		m_tabPane.add(m_fabulaTabPanel, "Fabula Graph");
		m_tabPane.add(m_storyLogScrollPane, "Story Log");
		UserActionSelectionPanel uasp = new UserActionSelectionPanel(myAgent);
		myAgent.addEventListener(uasp);
		m_tabPane.add(uasp, "User Actions");

		//Frame settings
		setTitle(myAgent.getLocalName() + " (PlotAgentGui for "
				+ myAgent.getClass() + ")");
		//refreshInfo();
		//setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * setup() creates the elements of the gui
	 * and adds event listeners
	 */
	@Override
	protected void guiSetup() {
		// WorldAgentGui is an extension of RationalAgentGui
		super.guiSetup();

		menu_PA = new JMenu("Plot Agent");
		//menuItem_PA_Graph = new JMenuItem("Show fabula graph");
		showFabulaAction = new ShowFabulaAction("Show fabula graph", null, null, null);
		saveFabulaAction = new SaveFabulaAction("Save fabula", null, null, null);
		getSuggestionsAction = new GetSuggestionsAction(
				"Get inspiration suggestions", null, null, null);
		pulseAction = new PulseAction("Next round", null, null, null);

		//menu_PA.add(menuItem_PA_Graph);
		menu_PA.add(showFabulaAction);
		menu_PA.add(saveFabulaAction);
		menu_PA.add(getSuggestionsAction);
		
		menuBar.add(menu_PA);
		
		JMenu menu_interactive = new JMenu("Interactive");
		menu_interactive.add(new ShowMapAction("Show multitiouch map", null, null, null));
		
		menuBar.add(menu_interactive);

		m_toolBar = new JToolBar();

		//m_toolBar.add(getSuggestionsAction);
		m_autoNxtRound = new JCheckBox("Automatic");
		m_autoNxtRound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox) (e.getSource());
				if (source.isSelected() && pulseAction.isEnabled()) {
					pulseAction.actionPerformed(null);
				}
			}
        });
		m_toolBar.add(pulseAction);
		m_toolBar.add(m_autoNxtRound);
		m_controlPanel.add(m_toolBar);
		
		multitouchMap = new VSTMultitouchController(myAgent);

		// Action listeners
		//menuItem_PA_Graph.addActionListener(this);
		
	}

	private JTree makeAgentTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Agents");
		JTree agentTree = new JTree(root);
		
		ImageIcon waIcon = new ImageIcon("img/wa_small.gif");
		ImageIcon caIcon = new ImageIcon("img/ca_small.gif");
		if (waIcon != null && caIcon != null) {
		    AgentsRenderer renderer = new AgentsRenderer(waIcon, caIcon);
		    agentTree.setCellRenderer(renderer);
		} else {
			System.out.println("ERROR: images not found");
		}
		
		agentTree.setExpandsSelectedPaths(true);
		DefaultMutableTreeNode worldNode = new DefaultMutableTreeNode(
				"World Agents");
		DefaultMutableTreeNode charNode = new DefaultMutableTreeNode(
				"Character Agents");
		root.add(worldNode);
		root.add(charNode);

		AID wa = ((IPlotAgent) myAgent).getWorldAgent();

		if (wa != null) {
			WATreeNode wa_n = new WATreeNode(wa
					.getName());
			

			//worldTree.add(wa_n);
			wa_n.add(new DefaultMutableTreeNode("Name: " + wa.getName()));
			for (String address : wa.getAddressesArray()) {
				wa_n.add(new DefaultMutableTreeNode("Address: " + address));
			}
			worldNode.add(wa_n);

		}

		int i = 0;
		ICharacterManager charman = ((IPlotAgent) myAgent)
				.getCharacterManager();

		if (charman != null) {
			for (AID c : (charman.getCharacters())) {

				double rand = Math.random();
				CATreeNode ca_n = new CATreeNode(c
						.getName()
						+ rand, c);
				
				ca_n.add(new DefaultMutableTreeNode("Name: " + c.getName()));
				for (String address : c.getAddressesArray()) {
					ca_n.add(new DefaultMutableTreeNode("Address: " + address));
				}

				if (((IPlotAgent) myAgent).getCharacterManager()
						.getCastedCharacters().contains(c)) {
					ca_n.add(new DefaultMutableTreeNode("Casted as: "
							+ pm.shortForm(((IPlotAgent) myAgent).getCharacterManager()
									.getStoryWorldRepresentationForAgent(c))));
					String controlled;
					if (((IPlotAgent) myAgent).getCharacterManager()
							.isHumanControlled(c)) {
						controlled = "user";
					} else {
						controlled = "computer";
					}
					ca_n.add(new DefaultMutableTreeNode("Controlled by: " + controlled));

				} else {
					ca_n.add(new DefaultMutableTreeNode("Not casted."));
				}

				charNode.add(ca_n);

				i++;
				//charTree.add(ca_n);
			}
		}

		expandAll(agentTree, true);
		
		return agentTree;
	}
	
	private String makeHTML(String header, Object body) {
		StringBuilder sb = new StringBuilder();
		sb.append("<HTML>");
		sb.append("<TABLE cellspacing=1 cellpadding=1 width=500>");
		sb.append("<TR><TD bgcolor=#EEEEEE><B>").append(header).append("</B></TD></TR><TR><TD>"); //.append("<BR>");
		if (body instanceof Set) {
			for (Object o: (Set)body) {
				sb.append(o.toString()).append("<BR>");
			}
		} else {
			sb.append(body.toString());
		}
	
		sb.append("</TD></TR></TABLE></BODY></HTML>");
		return sb.toString();
	}	

	private JTree makeSuggestionsTree(Set<OperationalizedSuggestion> sugs) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Suggestions");
		JTree sugTree = new JTree(root);

		DefaultTreeCellRenderer sugRenderer = new DefaultTreeCellRenderer();
		
		sugTree.setCellRenderer(sugRenderer);
		
		sugTree.setExpandsSelectedPaths(true);

		for (OperationalizedSuggestion os: sugs) {
			DefaultMutableTreeNode s = new DefaultMutableTreeNode(os.getSuggestion().getRuleName());
			
			DefaultMutableTreeNode oe = new DefaultMutableTreeNode(makeHTML("Operationalized element:", os.getOperationalizedElement().getIndividual()));
			DefaultMutableTreeNode oe_type = new DefaultMutableTreeNode(makeHTML("Type:",  os.getOperationalizedElement().getType())); 

			DefaultMutableTreeNode oe_character = new DefaultMutableTreeNode(makeHTML("Character:", os.getOperationalizedElement().getCharacter())); 

			DefaultMutableTreeNode oe_string = new DefaultMutableTreeNode(makeHTML("String value:", os.getOperationalizedElement().toString())); 
			
			oe.add(oe_type);
			oe.add(oe_character);
			oe.add(oe_string);

			DefaultMutableTreeNode body = new DefaultMutableTreeNode(makeHTML("Suggested fabula:", os.getSuggestion().getBody()));
			DefaultMutableTreeNode causers = new DefaultMutableTreeNode(makeHTML("Caused by:", os.getSuggestion().getCausers()));
			s.add(body);
			s.add(oe);
			s.add(causers);
			
			root.add(s);
		}

		//expandAll(sugTree, true);
		sugTree.expandPath(new TreePath(root));
		return sugTree;
	}

	@Override
	public void onStoryAgentEvent(StoryAgentEvent e) {

		if (e instanceof StatusChangedEvent) {
			int status = ((StatusChangedEvent) e).getStatus();
			if (status == BasicPlotAgent.STATUS_WAITING || status == BasicPlotAgent.STATUS_STORY_OVER) {
				pulseAction.setEnabled(false);
			} else {
				pulseAction.setEnabled(true);
				if (m_autoNxtRound.isSelected()) {
					pulseAction.actionPerformed(null);
				}
			}
		} else if (e instanceof AgentsChangedEvent) {
			refreshAgentList();
		} else if (e instanceof NewSuggestionsCreatedEvent) {
			refreshSuggestionsList(((NewSuggestionsCreatedEvent)e).getSuggestions());
		} else {
			// throw on.
			super.onStoryAgentEvent(e);
		}
	}
	
	private void refreshAgentList() {
		m_agentTreePanel.remove(m_agentTree);
		m_agentTree = makeAgentTree();
		
		m_agentTree.addMouseListener(new MouseAdapter () {
        	
			@Override
			public void mousePressed( MouseEvent e) {
        		// UGLY! But causes the treenode to be selected upon right click as well.
        		for (MouseListener ml: m_agentTree.getMouseListeners()) {
        			if (ml != this) {
        				// Simulate a left-click as well.
        				MouseEvent me = new MouseEvent(
        						m_agentTree, e.getID(), 
        						System.currentTimeMillis(), 
        						InputEvent.BUTTON1_MASK, 
        						e.getX(), e.getY(), 
        						1, 
        						false);
        				ml.mousePressed(me);

        			}
        		}
        		
        		//Discover the row that was selected (IE: The branch)
        		int selRow = m_agentTree.getRowForLocation(e.getX(), e.getY());
        		//Get the selection path for the row
        		TreePath selPath = m_agentTree.getPathForLocation(e.getX(), e.getY());
        		if(selRow != -1) {
	        		if(e.getClickCount() == 1) {
		        		if( e.isPopupTrigger()){
		        		//Make the selection
		        			m_agentTree.setSelectionPath(selPath);
		        		}
	        		}
        		}
        	}
        		
           @Override
		public void mouseReleased( MouseEvent e ) {
              if ( e.isPopupTrigger()) {
            	  popup.removeAll();
      		    TreePath path = m_agentTree.getSelectionPath();
      		    if (path != null) {
	    		    Object o = path.getLastPathComponent();
	    		    
	    		    if (o instanceof CATreeNode) {
	    		    	// if human controlled, add computer controlled action and vice versa.
	    		    	AID ca = ((CATreeNode)o).getAID();
	    		    	String charURI = ((IPlotAgent)myAgent).getCharacterManager().getStoryWorldRepresentationForAgent(ca);
	    		    	if (((IPlotAgent)myAgent).getCharacterManager().isHumanControlled(ca)) {
	    		    		popup.add(new ReleaseControlAction("Release control", null, null, null, charURI));
	    		    	} else {
	    		    		popup.add(new TakeControlAction("Take control", null, null, null, charURI));		    		    		
	    		    	}
	    		    }
	          		
	    		    popup.show( (JComponent)e.getSource(), 
	                                 e.getX(), e.getY() );
      		    }
              } 
           }
        }
    );		

		m_agentTreePanel.add(m_agentTree);
		repaint();

	}
	
	private void refreshFabulaPanel() {
		if (m_fabulaPanel == null) {
			m_fabulaPanel = new FabulaPanel(((IPlotAgent) myAgent).getFabulaBuilder(), myAgent.getStoryDomain());
			
		} else {
			m_fabulaTabPanel.remove(m_fabulaPanel);
		}

		
		m_fabulaPanel.update();
		m_fabulaPanel.setPreferredSize(m_fabulaTabPanel.getSize());
		m_fabulaTabPanel.add(m_fabulaPanel);
		
/*		Set<FabulaKnowledgeBase> fabulaSet = ((IPlotAgent) myAgent).getFabulaBuilder().getAllFabulaKnowledgeBase();
		NamedGraphSet theNamedGraphSet = new NamedGraphSetImpl();
		for (FabulaKnowledgeBase fkb: fabulaSet) {
			if (fkb instanceof NamedGraphFabulaKnowledgeBase) {
				theNamedGraphSet = ((NamedGraphFabulaKnowledgeBase)fkb).getNamedGraphSet();
			} 
		}
		m_fabulaPanel = m_fabulaFiewer.getFiewerPanel(theNamedGraphSet);
		
		m_fabulaTabPanel.add(m_fabulaPanel);*/
		repaint();
	}

	private void refreshSuggestionsList(Set<OperationalizedSuggestion> list) {
		m_suggestionsTreePanel.remove(m_suggestionsTree);
		m_suggestionsTree = makeSuggestionsTree(list);

		m_suggestionsTreePanel.add(m_suggestionsTree);
		repaint();

	}
	
	public void narrate(final String text) {
		Runnable updateStoryLog = new Runnable() {			
			public void run() {
				m_storyLogArea.append(text + "\n");
			}
		};
		
		SwingUtilities.invokeLater( updateStoryLog );
	}

}
