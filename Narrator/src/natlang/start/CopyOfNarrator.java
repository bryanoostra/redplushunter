/**
 * 
 */
package natlang.start;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import natlang.rdg.datafilessetter.DataInstance;

/**
 * @author m.zeeders
 *
 */
public class CopyOfNarrator extends JFrame implements ActionListener {

	private JLabel lblSWC,	lblFK, lblStorySettings, lblStory, lblNK, lblCS, lblLexicalInfo; 

	private JFileChooser fcSWC, fcFK, fcStorySettings, fcStory, fcNK, fcCS, fcLexicalInfo; 
	
	private JButton btnSWC, btnFK, btnStorySettings, btnStory, btnNK, btnCS, btnLexicalInfo;
	
	
	private FileListModel lmSWC, lmFK, lmStorySettings, lmStory, lmNK, lmCS, lmLexicalInfo;
	private JList lstSWC, lstFK, lstStorySettings, lstStory, lstNK, lstCS, lstLexicalInfo;
	
	private JButton delbtnSWC, delbtnFK, delbtnStorySettings, delbtnStory, delbtnNK, delbtnCS, delbtnLexicalInfo;
	
	private JButton btnStart;
	
	public CopyOfNarrator(){
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
		setLayout(new GridLayout(8, 4));
		
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
		
//		add(lblSWC); 			add(fcSWC); 			add(btnSWC); 			add(lstSWC); 			add(delbtnSWC);
//		add(lblFK); 			add(fcFK); 				add(btnFK); 			add(lstFK); 			add(delbtnFK);
//		add(lblStorySettings); 	add(fcStorySettings); 	add(btnStorySettings);	add(lstStorySettings);	add(delbtnStorySettings);
//		add(lblStory); 			add(fcStory); 			add(btnStory); 			add(lstStory); 			add(delbtnStory);
//		add(lblNK); 			add(fcNK); 				add(btnNK); 			add(lstNK); 			add(delbtnNK);
//		add(lblCS); 			add(fcCS); 				add(btnCS); 			add(lstCS); 			add(delbtnCS);
//		add(lblLexicalInfo); 	add(fcLexicalInfo); 	add(btnLexicalInfo); 	add(lstLexicalInfo); 	add(delbtnLexicalInfo);

		add(lblSWC); 			add(btnSWC); 			add(lstSWC); 			add(delbtnSWC);
		add(lblFK); 			add(btnFK); 			add(lstFK); 			add(delbtnFK);
		add(lblStorySettings); 	add(btnStorySettings);	add(lstStorySettings);	add(delbtnStorySettings);
		add(lblStory); 			add(btnStory); 			add(lstStory); 			add(delbtnStory);
		add(lblNK); 			add(btnNK); 			add(lstNK); 			add(delbtnNK);
		add(lblCS); 			add(btnCS); 			add(lstCS); 			add(delbtnCS);
		add(lblLexicalInfo); 	add(btnLexicalInfo); 	add(lstLexicalInfo); 	add(delbtnLexicalInfo);
		
		
		JButton btnStart = new JButton("start");
		
		btnStart.addActionListener(this);
		
		add(btnStart);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnStart){ // start the narrator
			
		}
		else if(e.getSource() == btnSWC){
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "OWL Files", "owl");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    	lmSWC.add(chooser.getSelectedFile(), "RDF/XML");
		    
		}
		else if(e.getSource() == btnFK){
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "OWL Files", "owl");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    	lmFK.add(chooser.getSelectedFile(), "RDF/XML");
		}
		else if(e.getSource() == btnStorySettings){
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Turtle Files", "ttl");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    	lmStorySettings.add(chooser.getSelectedFile(), "TURTLE");
		}
		else if(e.getSource() == btnStory){
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "TRIG Files", "trig");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    	lmStory.add(chooser.getSelectedFile(), "TRIG");
		}
		else if(e.getSource() == btnNK){
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "OWL Files", "owl");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    	lmNK.add(chooser.getSelectedFile(), "RDF/XML");
		}
		else if(e.getSource() == btnCS){
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "OWL Files", "owl");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    	lmCS.add(chooser.getSelectedFile(), "RDF/XML");
		}
		else if(e.getSource() == btnLexicalInfo){
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "TRIG Files", "trig");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    	lmLexicalInfo.add(chooser.getSelectedFile(), "TRIG");
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
				list.add(new DataInstance(new FileInputStream(f), lang, f));
				System.out.println(f.getName() + " added");
				notifyListDataListeners();
			}
			catch(FileNotFoundException e){
				
			}
		}
		
		public void remove(int i){
			if(i < list.size() && list.size() > 0){
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
		new CopyOfNarrator();
	}
	
}

