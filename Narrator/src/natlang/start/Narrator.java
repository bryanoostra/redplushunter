/**
 * 
 */
package natlang.start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import natlang.rdg.datafilessetter.DataInstance;
import natlang.rdg.datafilessetter.DataInstanceSetter;

/**
 * @author m.zeeders
 *
 */
public class Narrator extends JFrame implements ActionListener {
	
	public static DataInstanceSetter VST_INFO = null, NARRATOR_INFO;
	

	private JLabel lblSWC,	lblFK, lblStorySettings, lblStory, lblNK, lblCS, lblLexicalInfo; 

	private JFileChooser fcSWC, fcFK, fcStorySettings, fcStory, fcNK, fcCS, fcLexicalInfo; 
	
	private JButton btnSWC, btnFK, btnStorySettings, btnStory, btnNK, btnCS, btnLexicalInfo;
	
	
	private FileListModel lmSWC, lmFK, lmStorySettings, lmStory, lmNK, lmCS, lmLexicalInfo;
	private JList lstSWC, lstFK, lstStorySettings, lstStory, lstNK, lstCS, lstLexicalInfo;
	
	private JButton delbtnSWC, delbtnFK, delbtnStorySettings, delbtnStory, delbtnNK, delbtnCS, delbtnLexicalInfo;
	
	private JButton btnStart;
	
	private File currentDirectory = null;
	
	public Narrator(){
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		init();
		this.setSize(1024, 768);
		this.validate();
		this.setVisible(true);
	}
	
	private void init(){
		
		this.setTitle("Narrator");
		// StoryWorldCore.owl
		// FabulaKnowledge.owl
		// story settings (turtle)
		// story (trig)
		// narratorknowledge (owl)
		// commonsense  (owl)
		// story narrator information (lexical info) (trig)
		GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

		
		lblSWC 				= new JLabel("StoryWorldCore.owl: ");
		lblFK				= new JLabel("FabulaKnowledge.owl: "); 
		lblStorySettings	= new JLabel("Storry Setting files: "); 
		lblStory			= new JLabel("Story file: ");
		lblNK				= new JLabel("NarratorKnowledge.owl: "); 
		lblCS				= new JLabel("CommonSense.owl: ");
		lblLexicalInfo		= new JLabel("Lexical Info files: "); 
		
//		fcSWC 				= new JFileChooser(); 
//		fcFK				= new JFileChooser(); 
//		fcStorySettings		= new JFileChooser(); 
//		fcStory				= new JFileChooser(); 
//		fcNK				= new JFileChooser(); 
//		fcCS				= new JFileChooser(); 
//		fcLexicalInfo		= new JFileChooser(); 
		
		btnSWC 				= new JButton("+");
		btnFK				= new JButton("+"); 
		btnStorySettings	= new JButton("+"); 
		btnStory			= new JButton("+"); 
		btnNK				= new JButton("+"); 
		btnCS				= new JButton("+"); 
		btnLexicalInfo		= new JButton("+"); 
		
		btnSWC.addActionListener(this); 				
		btnFK.addActionListener(this);			
		btnStorySettings.addActionListener(this);
		btnStory.addActionListener(this);	
		btnNK.addActionListener(this);		
		btnCS.addActionListener(this);
		btnLexicalInfo.addActionListener(this);
		
		lmSWC 				= new FileListModel(); 
		lmFK				= new FileListModel(); 
		lmStorySettings		= new FileListModel(); 
		lmStory				= new FileListModel(); 
		lmNK				= new FileListModel(); 
		lmCS				= new FileListModel(); 
		lmLexicalInfo		= new FileListModel(); 
		
		lstSWC 				= new JList(lmSWC); 
		lstFK				= new JList(lmFK); 
		lstStorySettings	= new JList(lmStorySettings); 
		lstStory			= new JList(lmStory); 
		lstNK				= new JList(lmNK); 
		lstCS				= new JList(lmCS); 
		lstLexicalInfo		= new JList(lmLexicalInfo); 
	
		JScrollPane spSWC 				= new JScrollPane(lstSWC); 
		JScrollPane spFK				= new JScrollPane(lstFK); 
		JScrollPane spStorySettings		= new JScrollPane(lstStorySettings); 
		JScrollPane spStory				= new JScrollPane(lstStory); 
		JScrollPane spNK				= new JScrollPane(lstNK); 
		JScrollPane spCS				= new JScrollPane(lstCS); 
		JScrollPane spLexicalInfo		= new JScrollPane(lstLexicalInfo); 
		
		lmSWC.setList(lstSWC);
		lmFK.setList(lstFK);
		lmStorySettings.setList(lstStorySettings);
		lmStory.setList(lstStory);
		lmNK.setList(lstNK);
		lmCS.setList(lstCS);
		lmLexicalInfo.setList(lstLexicalInfo);
		
		
		delbtnSWC 				= new JButton("X"); 
		delbtnFK				= new JButton("X"); 
		delbtnStorySettings		= new JButton("X"); 
		delbtnStory				= new JButton("X"); 
		delbtnNK				= new JButton("X"); 
		delbtnCS				= new JButton("X"); 
		delbtnLexicalInfo		= new JButton("X"); 
		
		delbtnSWC.addActionListener(this); 				
		delbtnFK.addActionListener(this);			
		delbtnStorySettings.addActionListener(this);
		delbtnStory.addActionListener(this);	
		delbtnNK.addActionListener(this);		
		delbtnCS.addActionListener(this);
		delbtnLexicalInfo.addActionListener(this);
		
		btnStart = new JButton("start");
		
		btnStart.addActionListener(this);
		
//		add(btnStart);
		
//		add(lblSWC); 			add(fcSWC); 			add(btnSWC); 			add(lstSWC); 			add(delbtnSWC);
//		add(lblFK); 			add(fcFK); 				add(btnFK); 			add(lstFK); 			add(delbtnFK);
//		add(lblStorySettings); 	add(fcStorySettings); 	add(btnStorySettings);	add(lstStorySettings);	add(delbtnStorySettings);
//		add(lblStory); 			add(fcStory); 			add(btnStory); 			add(lstStory); 			add(delbtnStory);
//		add(lblNK); 			add(fcNK); 				add(btnNK); 			add(lstNK); 			add(delbtnNK);
//		add(lblCS); 			add(fcCS); 				add(btnCS); 			add(lstCS); 			add(delbtnCS);
//		add(lblLexicalInfo); 	add(fcLexicalInfo); 	add(btnLexicalInfo); 	add(lstLexicalInfo); 	add(delbtnLexicalInfo);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
		   layout.createSequentialGroup()
		   		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   				.addComponent(lblSWC)
		   				.addComponent(lblFK)
		   				.addComponent(lblStorySettings)
		   				.addComponent(lblStory)
		   				.addComponent(lblNK)
		   				.addComponent(lblCS)
		   				.addComponent(lblLexicalInfo)
		   		)
		   		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   				.addComponent(btnSWC)
		   				.addComponent(btnFK)
		   				.addComponent(btnStorySettings)
		   				.addComponent(btnStory)
		   				.addComponent(btnNK)
		   				.addComponent(btnCS)
		   				.addComponent(btnLexicalInfo)
		   		)
		   		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   				.addComponent(spSWC, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spFK, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spStorySettings, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spStory, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spNK, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spCS, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   				.addComponent(spLexicalInfo, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		   		)
		   		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   				.addComponent(delbtnSWC)
		   				.addComponent(delbtnFK)
		   				.addComponent(delbtnStorySettings)
		   				.addComponent(delbtnStory)
		   				.addComponent(delbtnNK)
		   				.addComponent(delbtnCS)
		   				.addComponent(delbtnLexicalInfo)
		   				.addComponent(btnStart)
		   		)
		   		
		);
		layout.setVerticalGroup(
		   layout.createSequentialGroup()
		   		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblSWC)
		           .addComponent(btnSWC)
		           .addComponent(spSWC, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnSWC))
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblFK)
		           .addComponent(btnFK)
		           .addComponent(spFK, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnFK))
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblStorySettings)
		           .addComponent(btnStorySettings)
		           .addComponent(spStorySettings, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnStorySettings))
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblStory)
		           .addComponent(btnStory)
		           .addComponent(spStory, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnStory))
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblNK)
		           .addComponent(btnNK)
		           .addComponent(spNK, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnNK))
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblCS)
		           .addComponent(btnCS)
		           .addComponent(spCS, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnCS))
		        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(lblLexicalInfo)
		           .addComponent(btnLexicalInfo)
		           .addComponent(spLexicalInfo, 50, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		           .addComponent(delbtnLexicalInfo))
		        .addComponent(btnStart)
		);


		
//		add(lblSWC); 			add(btnSWC); 			add(lstSWC); 			add(delbtnSWC);
//		add(lblFK); 			add(btnFK); 			add(lstFK); 			add(delbtnFK);
//		add(lblStorySettings); 	add(btnStorySettings);	add(lstStorySettings);	add(delbtnStorySettings);
//		add(lblStory); 			add(btnStory); 			add(lstStory); 			add(delbtnStory);
//		add(lblNK); 			add(btnNK); 			add(lstNK); 			add(delbtnNK);
//		add(lblCS); 			add(btnCS); 			add(lstCS); 			add(delbtnCS);
//		add(lblLexicalInfo); 	add(btnLexicalInfo); 	add(lstLexicalInfo); 	add(delbtnLexicalInfo);
		
		
		
	}

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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnStart){ // start the narrator
			VST_INFO = new DataInstanceSetter();
			VST_INFO.addDataInstanceList(lmSWC.getList());
			VST_INFO.addDataInstanceList(lmFK.getList());
			VST_INFO.addDataInstanceList(lmStorySettings.getList());
			VST_INFO.addDataInstanceList(lmStory.getList());
			
			NARRATOR_INFO = new DataInstanceSetter();
			NARRATOR_INFO.addDataInstanceList(lmNK.getList());
			NARRATOR_INFO.addDataInstanceList(lmCS.getList());
			NARRATOR_INFO.addDataInstanceList(lmLexicalInfo.getList());
			
			new natlang.rdg.Narrator();
		}
		else if(e.getSource() == btnSWC){
			openFile(lmSWC, "OWL Files", "owl", "RDF/XML");
		}
		else if(e.getSource() == btnFK){
			openFile(lmFK, "OWL Files", "owl", "RDF/XML");
		}
		else if(e.getSource() == btnStorySettings){
			openFile(lmStorySettings, "Turtle Files", "ttl", "TURTLE");
		}
		else if(e.getSource() == btnStory){
			openFile(lmStory, "TRIG Files", "trig", "TRIG");
		}
		else if(e.getSource() == btnNK){
			openFile(lmNK, "OWL Files", "owl", "RDF/XML");
		}
		else if(e.getSource() == btnCS){
			openFile(lmCS, "OWL Files", "owl", "RDF/XML");
		}
		else if(e.getSource() == btnLexicalInfo){
			openFile(lmLexicalInfo, "TURTLE Files", "ttl", "TURTLE");
		}
		else if(e.getSource() == delbtnSWC){
			lmSWC.remove(lstSWC.getSelectedIndex());
		}
		else if(e.getSource() == delbtnFK){
			lmFK.remove(lstFK.getSelectedIndex());
		}
		else if(e.getSource() == delbtnStorySettings){
			lmStorySettings.remove(lstStorySettings.getSelectedIndex());
		}
		else if(e.getSource() == delbtnStory){
			lmStory.remove(lstStory.getSelectedIndex());
		}
		else if(e.getSource() == delbtnNK){
			lmNK.remove(lstNK.getSelectedIndex());
		}
		else if(e.getSource() == delbtnCS){
			lmCS.remove(lstCS.getSelectedIndex());
		}
		else if(e.getSource() == delbtnLexicalInfo){
			lmLexicalInfo.remove(lstLexicalInfo.getSelectedIndex());
		}
	}
	
	class FileListModel implements ListModel {

		Vector<DataInstance> list;
		
		JList jlist;
		
		Vector<ListDataListener> datalisteners;
		
		FileListModel(){
			list = new Vector<DataInstance>();
			datalisteners = new Vector<ListDataListener>();
		}
		
		Vector<DataInstance> getList(){
			return list;
		}
		
		@Override
		public void addListDataListener(ListDataListener arg0) {
			datalisteners.add(arg0);
		}

		@Override
		public Object getElementAt(int arg0) {
			// TODO Auto-generated method stub
			if(arg0 < list.size())
				return list.elementAt(arg0);
					
			return null;
		}

		@Override
		public int getSize() {
			return list.size();
		}

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
		
		public void remove(int i){
			if(i >= 0 && i < list.size() && list.size() > 0){
				list.remove(i);
				notifyListDataListeners();
			}
			
		}
		
		@Override
		public void removeListDataListener(ListDataListener arg0) {
			// TODO Auto-generated method stub
			datalisteners.remove(arg0);
		}
		
		public void setList(JList l){
			this.jlist = l;
		}
		
		private void notifyListDataListeners(){
			Iterator<ListDataListener> it = datalisteners.iterator();
			
			while(it.hasNext()){
				it.next().intervalAdded(new ListDataEvent(jlist, ListDataEvent.CONTENTS_CHANGED, 0, list.size()));
			}
		}
	}

	
	public static void main(String[] args){
		new Narrator();
	}
	
}
