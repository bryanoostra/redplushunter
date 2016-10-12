package project.qa;

/**
 * DEMO is based on:
 *
 * ProtoFrame.java
   @author: Erik Dikkers
   Description: Build up a frame with the necessary elements and textfields.
   It builds up the frame for the prototype, the main class for the demo.
   
   You can select a Question and a Fragment that contains info that answers the question.
   The demo shows the answer text that is constructed based on an analysis of the Q and the F.
   
 * It uses DepTreeEditor to show the Dep Trees build using the jdom Document.
 * The DepTreeEditor is an adaptation of Dennis Reidsma demo for the parlevink.parlegraph package
 * Therefore an MGRap is constrcuted from the Document.
 * (in class Analyzer, where it does not belong - quick and dirty programming)
*/

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*; 	

import javax.swing.*;
import javax.swing.filechooser.*;
import org.jdom.Document;


public class DEMO extends JFrame implements ActionListener {
	
	private JButton ShowFDepTreeButton, ShowDepTreeButton, SelectQuestionButton, SelectFragmentButton, GenerateAnswerButton, ResetAllButton;
	private JTextArea QuestionArea, FragmentArea, AnswerArea, DebugArea;
	private JLabel QuestionTypeLabel, QuestionTopicLabel, AnswerTypeLabel, WHwordLabel, FragmentTypeLabel, FragmentTopicLabel, TopicPlaceLabel, AnswerLabel, DebugLabel;
	private JTextField QuestionTypeField, QuestionTopicField, AnswerTypeField, WHwordField, FragmentTypeField, FragmentTopicField, TopicPlaceField;
	
	private AnalyzerQ anaQ = new AnalyzerQ();	//analyzer object for the questions
	private AnalyzerF anaF = new AnalyzerF();	//analyzer object for the fragment
	private Formulator form;			//formulator object to show the generated answer
	private final int IntX = 700;			//width of the frame
	private final int IntY = 700;			//height of the frame
	
	private DepTreeEditor myeditor,myfeditor;
	
	private Document treeDoc;                       
	private File questionFile;
	private Document ftreeDoc;                       
	private File fragmentFile;
				
	public DEMO () {
		super("Demonstrator answer presentation");
		getContentPane().setLayout(null);
		setBackground( Color.lightGray );
		
		//Buttons
		SelectQuestionButton = new JButton("Select Question");
		SelectQuestionButton.addActionListener (this);
		SelectQuestionButton.setBounds(20, 30, 160, 20);
		getContentPane().add(SelectQuestionButton);
		
		SelectFragmentButton = new JButton("Select Fragment");
		SelectFragmentButton.addActionListener(this);
		SelectFragmentButton.setBounds(20, 55, 160, 20);
		getContentPane().add(SelectFragmentButton);
		
		ShowDepTreeButton = new JButton("Show DepTree Q");
		ShowDepTreeButton.addActionListener(new ShowTreeAction("showTree"));
		ShowDepTreeButton.setBounds(20, 80, 160, 20);
		ShowDepTreeButton.setVisible(false);
		getContentPane().add(ShowDepTreeButton);
		
		ShowFDepTreeButton = new JButton("Show DepTree F");
		ShowFDepTreeButton.addActionListener(new ShowFTreeAction("showFTree"));
		ShowFDepTreeButton.setBounds(20, 105, 160, 20);
		ShowFDepTreeButton.setVisible(false);
		getContentPane().add(ShowFDepTreeButton);
		
		
		GenerateAnswerButton = new JButton("Generate Answer");
		GenerateAnswerButton.addActionListener(this);
		GenerateAnswerButton.setBounds(230, 375, 160, 20);
		getContentPane().add(GenerateAnswerButton);
		
		ResetAllButton = new JButton("Clear ALL Fields");
		ResetAllButton.addActionListener(this);
		ResetAllButton.setBounds(230, 570, 160, 20);
		getContentPane().add(ResetAllButton);
		
		//TextAreas top
		QuestionArea = new JTextArea( "", 5, 20); //, JTextArea.SCROLLBARS_NONE );
		QuestionArea.setEditable(false);
		QuestionArea.setBounds(200, 30, 455, 20);
		getContentPane().add(QuestionArea, BorderLayout.CENTER);
		
		FragmentArea = new JTextArea( "", 5, 20); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		FragmentArea.setEditable(false);
		FragmentArea.setBounds(200, 55, 455, 100);
		getContentPane().add(FragmentArea, BorderLayout.CENTER);
		
		//Lables & TextField middle
		QuestionTypeLabel = new JLabel("Question Type: ");
		QuestionTypeLabel.setBounds(20, 180, 100, 20);
		getContentPane().add(QuestionTypeLabel);

		QuestionTypeField = new JTextField("*****"); //("Wh-word") / ("Yes/No") / ("Choice")
		QuestionTypeField.setBounds(200, 180, 150, 20);
		QuestionTypeField.setEditable(false);
		getContentPane().add(QuestionTypeField);
		
		QuestionTopicLabel = new JLabel("Question Topic: ");
		QuestionTopicLabel.setBounds(20, 200, 100, 20);
		getContentPane().add(QuestionTopicLabel);

		QuestionTopicField = new JTextField("*****"); //("Wh-word") / ("Yes/No") / ("Choice")
		QuestionTopicField.setBounds(200, 200, 150, 20);
		QuestionTopicField.setEditable(false);
		getContentPane().add(QuestionTopicField);
		
		AnswerTypeLabel = new JLabel("Answer Type: ");
		AnswerTypeLabel.setBounds(20, 220, 100, 20);
		getContentPane().add(AnswerTypeLabel);
		
		AnswerTypeField = new JTextField("*****"); //("Boolean") / ("Alternative") / ("Time/Place/Person")
							 //("Reaseon/Manner/(Object/Idea/Action))") / ("Amount/Duration/Frequency/Distance")
		AnswerTypeField.setBounds(200, 220, 150, 20);
		AnswerTypeField.setEditable(false);
		getContentPane().add(AnswerTypeField);
		
		WHwordLabel = new JLabel("WH-word: ");
		WHwordLabel.setBounds(20, 240, 100, 20);
		getContentPane().add(WHwordLabel);
		
		WHwordField = new JTextField("*****"); //("Boolean") / ("Alternative") / ("Time/Place/Person")
							 //("Reaseon/Manner/(Object/Idea/Action))") / ("Amount/Duration/Frequency/Distance")
		WHwordField.setBounds(200, 240, 150, 20);
		WHwordField.setEditable(false);
		getContentPane().add(WHwordField);
		
		FragmentTypeLabel = new JLabel("Fragment Type: ");
		FragmentTypeLabel.setBounds(330, 180, 100, 20);
		getContentPane().add(FragmentTypeLabel);
		
		FragmentTypeField = new JTextField("*****");
		FragmentTypeField.setBounds(415, 180, 150, 20);
		FragmentTypeField.setEditable(false);
		getContentPane().add(FragmentTypeField);
		
		FragmentTopicLabel = new JLabel("Fragment Subjects: ");
		FragmentTopicLabel.setBounds(330, 200, 100, 20);
		getContentPane().add(FragmentTopicLabel);
		
		FragmentTopicField = new JTextField("*****");
		FragmentTopicField.setBounds(415, 200, 150, 20);
		FragmentTopicField.setEditable(false);
		getContentPane().add(FragmentTopicField);
		
		TopicPlaceLabel = new JLabel("Topic placement: ");
		TopicPlaceLabel.setBounds(330, 240, 100, 20);
		getContentPane().add(TopicPlaceLabel);
		
		TopicPlaceField = new JTextField("*****"); //("Boolean") / ("Alternative") / ("Time/Place/Person")
							 //("Reaseon/Manner/(Object/Idea/Action))") / ("Amount/Duration/Frequency/Distance")
		TopicPlaceField.setBounds(415, 240, 150, 20);
		TopicPlaceField.setEditable(false);
		getContentPane().add(TopicPlaceField);
		
		AnswerLabel = new JLabel("Generated answer: ");
		AnswerLabel.setBounds(20, 270, 120, 20);
		getContentPane().add(AnswerLabel);

		//TextArea down
		AnswerArea = new JTextArea( "", 5, 20); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		AnswerArea.setEditable(false);
		AnswerArea.setBounds(20, 290, 545, 80);
		getContentPane().add(AnswerArea, BorderLayout.CENTER);

		DebugLabel = new JLabel("Debugging info: ");
		DebugLabel.setBounds(30, 390, 120, 20);
		getContentPane().add(DebugLabel);
		
		DebugArea = new JTextArea( "", 5, 20); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		DebugArea.setEditable(false);
		DebugArea.setBounds(30, 410, 545, 150);
		getContentPane().add(DebugArea, BorderLayout.CENTER);

              	//Initialize frame at center and not resizable
              	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();    
              	setLocation((screenSize.width - IntX)/2, (screenSize.height - IntY)/2);    
		setSize(IntX, IntY);
		setVisible (true);
		setResizable(false);
		
		System.out.println("*** Program started and ready for input ***");
		
	}
	
	
	public Document getDocument(){
		return treeDoc;
	}
	
	
	public void actionPerformed(ActionEvent e) {
	  	JFileChooser fc = new JFileChooser("c:\\local\\corpus");  //Create a file chooser in the current directory
        	fc.setFileSelectionMode(JFileChooser.FILES_ONLY); 	  //Only selection of files possible
        	
        	XMLFilter XMLFil = new XMLFilter();
        	fc.setFileFilter(XMLFil);   		        	  //Only show *.xml files based on XMLFilter

        	//Handle SelectQuestionButton.
        	if (e.getSource() == SelectQuestionButton) {
        		fc.setCurrentDirectory(new File("../../../data/project/qa/Questions"));
            		int returnVal = fc.showOpenDialog(DEMO.this);

            		if (returnVal == JFileChooser.APPROVE_OPTION) {
                		File f = fc.getSelectedFile();
                		questionFile = f;
                		String qt = "question";
                		anaQ = new AnalyzerQ(f);    
                		
                		//Now able to analyze the QuestionType, QuestionTopic, AnswerType, AnswerPattern
               			QuestionTypeField.setText(anaQ.Qtype);
               			QuestionTopicField.setText(anaQ.Qtopic);
                      		AnswerTypeField.setText(anaQ.Atype);
                      		WHwordField.setText(anaQ.whword.toUpperCase());

                		//Other action to be taken after selecting a question                		                		
                		QuestionArea.setText(null);
                		if (anaQ.isQuestion)
                			QuestionArea.append(anaQ.showSentence(qt));
                	        else
                	        	QuestionArea.append("dit is geen vraag");

				//Forms the debugging area
                		DebugArea.setText(null);
                		DebugArea.append(anaQ.showNodes(qt));
                		ShowDepTreeButton.setVisible(true);
                		
            		} else {
                		QuestionArea.setText("Open Question command cancelled!\n");
            		}
  	          		
        	//Handle SelectFragmentButton.
        	} else if (e.getSource() == SelectFragmentButton) {
        		fc.setCurrentDirectory(new File("../../../data/project/qa/Questions"));
            		int returnVal = fc.showOpenDialog(DEMO.this);
            	
            		if (returnVal == JFileChooser.APPROVE_OPTION) {
	                	File f = fc.getSelectedFile();
	                	fragmentFile=f;
	                	String fm = "fragment";
	                	anaF = new AnalyzerF(f);
	                	
	                	//Now able to analyze the FragmentType, FragmentTopic
	                	FragmentTypeField.setText(anaF.Ftype);
	                	FragmentTopicField.setText(anaF.Ftopic);
                		
                		//Other action to be taken after selecting a fragment
                		FragmentArea.setText(null);
                		FragmentArea.append(anaF.showSentence(fm));
                		
                		//Forms the debugging area
                		DebugArea.setText(null);
                		DebugArea.append(anaF.showNodes(fm));
                		ShowFDepTreeButton.setVisible(true);
            		} else {            			
                		FragmentArea.setText("Open Fragment command cancelled!\n");
                	}
                //Handle GenerateAnswerButton
              	} else if (e.getSource() == GenerateAnswerButton) {
              		if (anaQ.root!=null && anaF.root!=null) {
              			//activating formulator when question and fragment have been selected
              			form = new Formulator(anaQ, anaF);
              			
              			//Other action to be taken after selecting a fragment
              			AnswerArea.setText(form.generatedAnswer);	
              			AnswerArea.append("\n\n");
              			AnswerArea.append(form.algorithmCase);
              			TopicPlaceField.setText(form.Tplacement);
              			
              			//Forms the debugging area
              			DebugArea.setText(null);
                		DebugArea.append(form.showNodesF());
                	} else {
                		
                		AnswerArea.setText("PLEASE, FIRST SELECT BOTH INPUTS ABOVE!");      
                	}
		// Handle ResetAllButton                		
              	} else if (e.getSource() == ResetAllButton) {              			
              			
              			// creating empty objects to clear JDOM objects
              			anaQ = new AnalyzerQ();
              			anaF = new AnalyzerF();
              			
              			// clearing all the fields to the original contents
              			QuestionTypeField.setText("*****");
              			QuestionTopicField.setText("*****");
              			AnswerTypeField.setText("*****");
              			WHwordField.setText("*****");
              			FragmentTypeField.setText("*****");
              			FragmentTopicField.setText("*****");
              			TopicPlaceField.setText("*****");
              			QuestionArea.setText(null);
              			FragmentArea.setText(null);
              			AnswerArea.setText(null);
              			DebugArea.setText(null);
              	}
    	}
    	
    	public static void main (String[] args ) {
		DEMO pf = new DEMO();
		pf.addWindowListener (new CloseWindowAndExit() );
	}
    	
    	
    	
    class ShowTreeAction extends AbstractAction {
    	public ShowTreeAction(String name) {
        	super(name);
    	}
    	public void actionPerformed(ActionEvent e) {
            	natlang.deptree.model.DepTreeModel mg=null;
            	try{
            	        treeDoc = DepTreeAnalyzer.makeDocument(questionFile);
            		mg = (natlang.deptree.model.DepTreeModel)DepTreeAnalyzer.makeMGraph(treeDoc);
            		if (myeditor==null) { 
            			myeditor = new DepTreeEditor("Question"); 
            			myeditor.start(mg); 					
            		}else{
            		        myeditor.setVisible();
            			myeditor.setMGraph(mg);
            		}
            		
            	}catch (Exception exc){
            		System.out.println("Exception thrown by DepTreeAnalyzer.readMGraph:"+exc.getMessage());
            	}
       }
    }// end class action
    
    class ShowFTreeAction extends AbstractAction {
    	public ShowFTreeAction(String name) {
        	super(name);
    	}
    	public void actionPerformed(ActionEvent e) {
        	if (JOptionPane.showConfirmDialog(null, "Are you sure you want me to show the graph?", "Show tree", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            	natlang.deptree.model.DepTreeModel  mg = null;
            	try{
            	        ftreeDoc = DepTreeAnalyzer.makeDocument(fragmentFile);
            		mg = (natlang.deptree.model.DepTreeModel)DepTreeAnalyzer.makeMGraph(ftreeDoc);
            		if (myfeditor==null) { 
            			myfeditor = new DepTreeEditor("Fragment"); 
            			myfeditor.start(mg); 					
            		}else{
            			myfeditor.setMGraph(mg);
            		}
            		
            	}catch (Exception exc){
            		System.out.println("Exception thrown by DepTreeAnalyzer.readMGraph:"+exc.getMessage());
            	}
            	
        }
    }// end class action
}	
}// end class DEMO