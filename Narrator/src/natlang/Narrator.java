/**
 * 
 */
package natlang;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import natlang.debug.LogFactory;
import natlang.rdg.datafilessetter.DataInstance;
import natlang.rdg.datafilessetter.DataInstanceSetter;
import natlang.rdg.ontmodels.OntModels;

/**
 * This class is the main entry point of the Narrator. The user is given the 
 * possibility to select all necessary files needed by the Narrator. These files
 * are:
 * <ul>
 * 	<li>VST Files:
 * 		<ul>
 * 			<li>Story World Core Knowledge</li>
 * 			<li>Fabula Knowledge</li>
 * 			<li>Setting files</li>
 * 			<li>Fabula file</li>
 * 		</ul>
 * 	</li>
 * 	<li>Narrator Files:
 * 		<ul>
 * 			<li>Narrator Knowledge</li>
 * 			<li>Common Sense Knowledge</li>
 * 			<li>Lexical Information Files</li>
 * 		</ul>
 * 	</li>
 * </ul>
 * 
 * After selecting all the files the user pushes the 'start' button, and the 
 * narrator will convert the Fabula to a story in Dutch.
 * 
 * When the start button is pushed, the locations of all selected files are
 * stored in a properties file (called: 'narrator.properties') and these are 
 * automatically selected again next time the Narrator is run. 
 * 
 * 
 * @author R. Zeeders
 *
 */
public class Narrator extends JFrame implements ActionListener {
	
	// These files maintain all ontology information
	// they are static so each class can reach them and 
	// use the ontological information
	public static DataInstanceSetter VST_INFO = null, NARRATOR_INFO;
	
	// The keys used in the properties file
	public static final String PROPERTIES_VST_OWL 	= "VST_OWL";	// VST_OWL
	public static final String PROPERTIES_VST_TTL 	= "VST_TTL";	// VST_TTL 
	public static final String PROPERTIES_VST_TRIG 	= "VST_TRIG";	// VST_TRIG
	public static final String PROPERTIES_NAR_OWL 	= "NAR_OWL";	// NAR_OWL
	public static final String PROPERTIES_NAR_TTL 	= "NAR_TTL";	// NAR_TTL
	public static final String PROPERTIES_NAR_TRIG 	= "NAR_TRIG";	// NAR_TRIG
	public static final String PROPERTIES_FILENAME	= "narrator.properties";

	// All the labels on the GUI
	private JLabel lblVST_OWL, lblVST_TTL, lblVST_TRIG, lblNAR_OWL, lblNAR_TTL, lblNAR_TRIG; 

	// All the buttons for adding files to the lists
	private JButton btnVST_OWL, btnVST_TTL, btnVST_TRIG, btnNAR_OWL, btnNAR_TTL, btnNAR_TRIG;
	
	// the underlying list which the graphical representation uses
	private FileListModel lmVST_OWL, lmVST_TTL, lmVST_TRIG, lmNAR_OWL, lmNAR_TTL, lmNAR_TRIG;
	// the graphical representation of a list
	private JList lstVST_OWL, lstVST_TTL, lstVST_TRIG, lstNAR_OWL, lstNAR_TTL, lstNAR_TRIG;
	
	// all the buttons for deleting files from the list
	private JButton delbtnVST_OWL, delbtnVST_TTL, delbtnVST_TRIG, delbtnNAR_OWL, delbtnNAR_TTL, delbtnNAR_TRIG;
	
	// start button, start the narrator process
	private JButton btnStart;
	
	// dividing panels, for clarification; vst and narrator files 
	private JPanel pnlVSTFiles, pnlNarratorFiles;
	
	// stores the directory last visited by the user in the JFileChooser 
	private File currentDirectory = null;
	
	/**
	 * Creates the Narrator GUI. This function calls the private 
	 * function init() to initiate the process of laying out all components.
	 */
	public Narrator(){
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		init();
		load();
		this.setSize(1024, 768);
		this.validate();
		this.setVisible(true);
	}
	
	/**
	 * Creates and lays out all graphical components. It also
	 * creates the FileListModels which are binded to the graphical lists.
	 */
	private void init(){
		
		this.setTitle("Narrator");
		// StoryWorldCore.owl
		// FabulaKnowledge.owl
		// story settings (turtle)
		// story (trig)
		// narratorknowledge (owl)
		// commonsense  (owl)
		// story narrator information (lexical info) (trig)
	
		lblVST_OWL 	= new JLabel("OWL files: ");
		lblVST_TTL	= new JLabel("TTL files: "); 
		lblVST_TRIG	= new JLabel("TriG files: ");
		lblNAR_OWL	= new JLabel("OWL files: "); 
		lblNAR_TTL	= new JLabel("TTL files: ");
		lblNAR_TRIG	= new JLabel("TriG files: "); 
		
//		fcSWC 				= new JFileChooser(); 
//		fcFK				= new JFileChooser(); 
//		fcStorySettings		= new JFileChooser(); 
//		fcStory				= new JFileChooser(); 
//		fcNK				= new JFileChooser(); 
//		fcCS				= new JFileChooser(); 
//		fcLexicalInfo		= new JFileChooser(); 
		
		btnVST_OWL 		= new JButton("+");
		btnVST_TTL		= new JButton("+"); 
		btnVST_TRIG		= new JButton("+"); 
		btnNAR_OWL		= new JButton("+"); 
		btnNAR_TTL		= new JButton("+"); 
		btnNAR_TRIG		= new JButton("+"); 
		
		btnVST_OWL.addActionListener(this); 				
		btnVST_TTL.addActionListener(this);
		btnVST_TRIG.addActionListener(this);	
		btnNAR_OWL.addActionListener(this);		
		btnNAR_TTL.addActionListener(this);
		btnNAR_TRIG.addActionListener(this);
		
		lmVST_OWL 		= new FileListModel(); 
		lmVST_TTL		= new FileListModel(); 
		lmVST_TRIG		= new FileListModel(); 
		lmNAR_OWL		= new FileListModel(); 
		lmNAR_TTL		= new FileListModel(); 
		lmNAR_TRIG		= new FileListModel(); 
		
		lstVST_OWL 		= new JList(lmVST_OWL); 
		lstVST_TTL		= new JList(lmVST_TTL); 
		lstVST_TRIG		= new JList(lmVST_TRIG); 
		lstNAR_OWL		= new JList(lmNAR_OWL); 
		lstNAR_TTL		= new JList(lmNAR_TTL); 
		lstNAR_TRIG		= new JList(lmNAR_TRIG); 
	
		JScrollPane spSWC 				= new JScrollPane(lstVST_OWL); 
		JScrollPane spStorySettings		= new JScrollPane(lstVST_TTL); 
		JScrollPane spStory				= new JScrollPane(lstVST_TRIG); 
		JScrollPane spNK				= new JScrollPane(lstNAR_OWL); 
		JScrollPane spCS				= new JScrollPane(lstNAR_TTL); 
		JScrollPane spLexicalInfo		= new JScrollPane(lstNAR_TRIG); 
		
		lmVST_OWL.setList(lstVST_OWL);
		lmVST_TTL.setList(lstVST_TTL);
		lmVST_TRIG.setList(lstVST_TRIG);
		lmNAR_OWL.setList(lstNAR_OWL);
		lmNAR_TTL.setList(lstNAR_TTL);
		lmNAR_TRIG.setList(lstNAR_TRIG);
		
		
		delbtnVST_OWL 				= new JButton("X"); 
		delbtnVST_TTL		= new JButton("X"); 
		delbtnVST_TRIG				= new JButton("X"); 
		delbtnNAR_OWL				= new JButton("X"); 
		delbtnNAR_TTL				= new JButton("X"); 
		delbtnNAR_TRIG		= new JButton("X"); 
		
		delbtnVST_OWL.addActionListener(this); 		
		delbtnVST_TTL.addActionListener(this);
		delbtnVST_TRIG.addActionListener(this);	
		delbtnNAR_OWL.addActionListener(this);		
		delbtnNAR_TTL.addActionListener(this);
		delbtnNAR_TRIG.addActionListener(this);
		
		btnStart = new JButton("start");
		
		btnStart.addActionListener(this);
		
		GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		pnlVSTFiles = new JPanel();
		pnlVSTFiles.setBorder(BorderFactory.createTitledBorder("VST files"));
		GroupLayout layoutVST = new GroupLayout(pnlVSTFiles);
		pnlVSTFiles.setLayout(layoutVST);
		layoutVST.setAutoCreateGaps(true);
		layoutVST.setAutoCreateContainerGaps(true);
		
		pnlNarratorFiles = new JPanel();
		pnlNarratorFiles.setBorder(BorderFactory.createTitledBorder("Narrator files"));
		GroupLayout layoutNarrator = new GroupLayout(pnlNarratorFiles);
		pnlNarratorFiles.setLayout(layoutNarrator);
		layoutNarrator.setAutoCreateGaps(true);
		layoutNarrator.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(pnlVSTFiles)
				.addComponent(pnlNarratorFiles)
				.addComponent(btnStart)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addComponent(pnlVSTFiles)
				.addComponent(pnlNarratorFiles)
				.addComponent(btnStart)
		);
		
		layoutVST.setHorizontalGroup(
				layoutVST.createSequentialGroup()
		   		.addGroup(layoutVST.createParallelGroup(GroupLayout.Alignment.TRAILING)
		   				.addComponent(lblVST_OWL)
		   				.addComponent(lblVST_TTL)
		   				.addComponent(lblVST_TRIG)
		   		)
		   		.addGroup(layoutVST.createParallelGroup(GroupLayout.Alignment.LEADING)
		   				.addComponent(btnVST_OWL)
		   				.addComponent(btnVST_TTL)
		   				.addComponent(btnVST_TRIG)
		   		)
		   		.addGroup(layoutVST.createParallelGroup(GroupLayout.Alignment.LEADING)
		   				.addComponent(spSWC, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spStorySettings, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spStory, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   		)
		   		.addGroup(layoutVST.createParallelGroup(GroupLayout.Alignment.LEADING)
		   				.addComponent(delbtnVST_OWL)
		   				.addComponent(delbtnVST_TTL)
		   				.addComponent(delbtnVST_TRIG)
		   		)
		   		
		);
		layoutVST.setVerticalGroup(
			layoutVST.createSequentialGroup()
		   		.addGroup(layoutVST.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblVST_OWL)
		           .addComponent(btnVST_OWL)
		           .addComponent(spSWC, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnVST_OWL))
		        .addGroup(layoutVST.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblVST_TTL)
		           .addComponent(btnVST_TTL)
		           .addComponent(spStorySettings, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnVST_TTL))
		        .addGroup(layoutVST.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblVST_TRIG)
		           .addComponent(btnVST_TRIG)
		           .addComponent(spStory, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnVST_TRIG))
		);

		layoutNarrator.setHorizontalGroup(
				layoutNarrator.createSequentialGroup()
				   		.addGroup(layoutNarrator.createParallelGroup(GroupLayout.Alignment.TRAILING)
				   				.addComponent(lblNAR_OWL)
				   				.addComponent(lblNAR_TTL)
				   				.addComponent(lblNAR_TRIG)
				   		)
				   		.addGroup(layoutNarrator.createParallelGroup(GroupLayout.Alignment.LEADING)
				   				.addComponent(btnNAR_OWL)
				   				.addComponent(btnNAR_TTL)
				   				.addComponent(btnNAR_TRIG)
				   		)
				   		.addGroup(layoutNarrator.createParallelGroup(GroupLayout.Alignment.LEADING)
				   				.addComponent(spNK, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				   				.addComponent(spCS, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				   				.addComponent(spLexicalInfo, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				   		)
				   		.addGroup(layoutNarrator.createParallelGroup(GroupLayout.Alignment.LEADING)
				   				.addComponent(delbtnNAR_OWL)
				   				.addComponent(delbtnNAR_TTL)
				   				.addComponent(delbtnNAR_TRIG)
				   		)
				   		
				);
		layoutNarrator.setVerticalGroup(
				layoutNarrator.createSequentialGroup()
				        .addGroup(layoutNarrator.createParallelGroup(GroupLayout.Alignment.BASELINE)
				           .addComponent(lblNAR_OWL)
				           .addComponent(btnNAR_OWL)
				           .addComponent(spNK, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				           .addComponent(delbtnNAR_OWL))
				        .addGroup(layoutNarrator.createParallelGroup(GroupLayout.Alignment.BASELINE)
				           .addComponent(lblNAR_TTL)
				           .addComponent(btnNAR_TTL)
				           .addComponent(spCS, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				           .addComponent(delbtnNAR_TTL))
				        .addGroup(layoutNarrator.createParallelGroup(GroupLayout.Alignment.BASELINE)
				           .addComponent(lblNAR_TRIG)
				           .addComponent(btnNAR_TRIG)
				           .addComponent(spLexicalInfo, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				           .addComponent(delbtnNAR_TRIG))
				);
		
	}

	/**
	 * Opens a dialog in which a file can be chosen to be added to the given list.
	 * A filter can be specified, so the user can only select files depicted by the filter.
	 * Also the language of the file should be given, which is important when the files
	 * are read by the Jena Ontology Model. 
	 * @param flm the FileListModel to which the selected file should be added
	 * @param sfiltername the name for the filter (e.g. "OWL files")
	 * @param sfilter the filter (e.g. "owl")
	 * @param slang the language of the to be selected file (e.g. "RDF/XML")
	 */
	private void openFile(FileListModel flm, String sfiltername, String sfilter, String slang){
		
		JFileChooser chooser = new JFileChooser(currentDirectory);
		chooser.setMultiSelectionEnabled(true);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	    		sfiltername, sfilter);
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION){
	    	File[] files = (chooser.getSelectedFiles());
	    	for(int i=0; i<files.length; i++){
	    		flm.add(files[i], slang);
	    	}
	    	currentDirectory = files[files.length - 1].getParentFile();
	    	
	    }
	}
	
	/**
	 * Stores all fields in a properties file (narrator.properties), so that when the Narrator is opened
	 * again, these values can be automatically loaded into these fields via
	 * the function load().
	 */
	private void store(){
		Properties p = new Properties();
		
		//VST OWL files
		Iterator<DataInstance> i = lmVST_OWL.getList().iterator();
		while(i.hasNext()){
			p.setProperty(i.next().getFileName(), PROPERTIES_VST_OWL);
		}
		
		//VST TTL files
		i = lmVST_TTL.getList().iterator();
		while(i.hasNext()){
			p.setProperty(i.next().getFileName(), PROPERTIES_VST_TTL);
		}
		
		//VST TriG files
		i = lmVST_TRIG.getList().iterator();
		while(i.hasNext()){
			p.setProperty(i.next().getFileName(), PROPERTIES_VST_TRIG);
		}
		
		//NARRATOR OWL files
		i = lmNAR_OWL.getList().iterator();
		while(i.hasNext()){
			p.setProperty(i.next().getFileName(), PROPERTIES_NAR_OWL);
		}
		
		//NARRATOR TTL files
		i = lmNAR_TTL.getList().iterator();
		while(i.hasNext()){
			p.setProperty(i.next().getFileName(), PROPERTIES_NAR_TTL);
		}
		
		//NARRATOR TriG files
		i = lmNAR_TRIG.getList().iterator();
		while(i.hasNext()){
			p.setProperty(i.next().getFileName(), PROPERTIES_NAR_TRIG);
		}
		
		try{
			p.store(new FileOutputStream(PROPERTIES_FILENAME), "properties file for the Narrator");
		}
		catch(IOException e){
			
		}
	}
	
	/**
	 * Loads the contents of the properties file (if it exists) and
	 * fills the lists with the files previously loaded into the 
	 * narrator.
	 */
	private void load(){
		Properties p = new Properties();
		try{
			p.load(new FileInputStream(PROPERTIES_FILENAME));
			
			Iterator<Entry<Object, Object>> it = p.entrySet().iterator();
			
			while(it.hasNext()){
				Entry<Object, Object> e = it.next();
				String key = (String)e.getKey();
				String val = (String)e.getValue();
				
				if(val.equals(PROPERTIES_VST_OWL)){
					lmVST_OWL.add(new File(key), "RDF/XML");
				}
				if(val.equals(PROPERTIES_VST_TTL)){
					lmVST_TTL.add(new File(key), "TURTLE");
				}
				if(val.equals(PROPERTIES_VST_TRIG)){
					lmVST_TRIG.add(new File(key), "TRIG");
				}
				if(val.equals(PROPERTIES_NAR_OWL)){
					lmNAR_OWL.add(new File(key), "RDF/XML");
				}
				if(val.equals(PROPERTIES_NAR_TTL)){
					lmNAR_TTL.add(new File(key), "TURTLE");
				}
				if(val.equals(PROPERTIES_NAR_TRIG)){
					lmNAR_TRIG.add(new File(key), "TRIG");
				}
			}
			
		}
		catch(FileNotFoundException e){
			
		}
		catch(IOException e){
			
		}
	}
	
	/**
	 * For each element on the GUI the actions to be performed are specified. 
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnStart){ // start the narrator
			store();
			VST_INFO = new DataInstanceSetter();
			VST_INFO.addDataInstanceList(lmVST_OWL.getList());
			VST_INFO.addDataInstanceList(lmVST_TTL.getList());
			VST_INFO.addDataInstanceList(lmVST_TRIG.getList());
			
			NARRATOR_INFO = new DataInstanceSetter();
			NARRATOR_INFO.addDataInstanceList(lmNAR_OWL.getList());
			NARRATOR_INFO.addDataInstanceList(lmNAR_TTL.getList());
			NARRATOR_INFO.addDataInstanceList(lmNAR_TRIG.getList());
			System.err.println("Before CreateOntModels");
			OntModels.createOntModels();
			System.err.println("Before Narrator doing it's thing");
			new natlang.rdg.Narrator();
			
		}
		else if(e.getSource() == btnVST_OWL){
			openFile(lmVST_OWL, "OWL Files", "owl", "RDF/XML");
		}
		else if(e.getSource() == btnVST_TTL){
			openFile(lmVST_TTL, "Turtle Files", "ttl", "TURTLE");
		}
		else if(e.getSource() == btnVST_TRIG){
			openFile(lmVST_TRIG, "TriG Files", "trig", "TRIG");
		}
		else if(e.getSource() == btnNAR_OWL){
			openFile(lmNAR_OWL, "OWL Files", "owl", "RDF/XML");
		}
		else if(e.getSource() == btnNAR_TTL){
			openFile(lmNAR_TTL, "Turtle Files", "ttl", "TURTLE");
		}
		else if(e.getSource() == btnNAR_TRIG){
			openFile(lmNAR_TRIG, "TriG Files", "trig", "TRIG");
		}
		else if(e.getSource() == delbtnVST_OWL){
			int[] index = lstVST_OWL.getSelectedIndices();
			for(int i=index.length-1; i >=0 ; i-- ){
				lmVST_OWL.remove(index[i]);
			}
		}
		else if(e.getSource() == delbtnVST_TTL){
			int[] index = lstVST_TTL.getSelectedIndices();
			for(int i=index.length-1; i >=0 ; i-- ){
				lmVST_TTL.remove(index[i]);
			}
		}
		else if(e.getSource() == delbtnVST_TRIG){
			int[] index = lstVST_TRIG.getSelectedIndices();
			for(int i=index.length-1; i >=0 ; i-- ){
				lmVST_TRIG.remove(index[i]);
			}
		}
		else if(e.getSource() == delbtnNAR_OWL){
			int[] index = lstNAR_OWL.getSelectedIndices();
			for(int i=index.length-1; i >=0 ; i-- ){
				lmNAR_OWL.remove(index[i]);
			}
		}
		else if(e.getSource() == delbtnNAR_TTL){
			int[] index = lstNAR_TTL.getSelectedIndices();
			for(int i=index.length-1; i >=0 ; i-- ){
				lmNAR_TTL.remove(index[i]);
			}
		}
		else if(e.getSource() == delbtnNAR_TRIG){
			int[] index = lstNAR_TRIG.getSelectedIndices();
			for(int i=index.length-1; i >=0 ; i-- ){
				lmNAR_TRIG.remove(index[i]);
			}
		}
	}
	
	/**
	 * This internal class is an extension of the ListModel class
	 * which represents the data side for a GUI list. It is specially
	 * made for the Ontology files. For each opened file, a DataInstance
	 * is created. These lists ultimately are passed to a DataInstanceSetter
	 * which reads the ontology files.
	 * @author R. Zeeders
	 *
	 */
	class FileListModel implements ListModel {

		Vector<DataInstance> list;
		
		JList jlist;
		
		Vector<ListDataListener> datalisteners;
		
		/**
		 * Creates a FileListModel. The list and a vector of datalisteners are 
		 * constructed.
		 */
		FileListModel(){
			list = new Vector<DataInstance>();
			datalisteners = new Vector<ListDataListener>();
		}
		
		/**
		 * Returns the list with all DataInstances.
		 * @return
		 */
		Vector<DataInstance> getList(){
			return list;
		}
		
		
		public void addListDataListener(ListDataListener arg0) {
			datalisteners.add(arg0);
		}

		
		
		public Object getElementAt(int arg0) {
			// TODO Auto-generated method stub
			if(arg0 < list.size())
				return list.elementAt(arg0);
					
			return null;
		}

		
		public int getSize() {
			return list.size();
		}

		/**
		 * Adds a file to this list. This file and it's language are used
		 * to create a DataInstance, which is then added to the list.
		 * @param f the file to be added to the list 
		 * @param lang the language of the file (e.g. "RDF/XML" in case of owl files)
		 */
		public void add(File f, String lang){
			try{
				System.out.println("file:"+f.getName());
				list.add(new DataInstance(new FileInputStream(f), lang, f));
				notifyListDataListeners();
				System.out.println("DataInstance added and datalisteners notified");
			}
			catch(FileNotFoundException e){
				System.err.println("FileNotFound");
				e.printStackTrace();
			}
		}
		
		/**
		 * Removes the specified element from the list.
		 * @param i the index of the element to be removed
		 */
		public void remove(int i){
			if(i >= 0 && i < list.size() && list.size() > 0){
				list.remove(i);
				notifyListDataListeners();
			}
			
		}
		
		
		public void removeListDataListener(ListDataListener arg0) {
			// TODO Auto-generated method stub
			datalisteners.remove(arg0);
		}
		
		/**
		 * Sets the GUI list belonging to this ListModel
		 * @param l
		 */
		public void setList(JList l){
			this.jlist = l;
		}
		
		/**
		 * Notifies all DataListeners that a change has occurred.
		 */
		private void notifyListDataListeners(){
			Iterator<ListDataListener> it = datalisteners.iterator();
			
			while(it.hasNext()){
				it.next().intervalAdded(new ListDataEvent(jlist, ListDataEvent.CONTENTS_CHANGED, 0, list.size()));
			}
		}
	}

	/**
	 * Starts the Narrator.
	 * @param args
	 */
	public static void main(String[] args){
		Logger logger = LogFactory.getLogger("natlang.rdg");
		new Narrator();
	}
	
}

