package project.qa;

/*Last change: 28 Oktober 2004 ProtoFrame.java
   By: Erik Dikkers
   Description: Build up a frame with the necessary elements and textfields.
   It builds up the frame for the prototype, the main class for the demo.
*/

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*; 	

import javax.swing.*;
import javax.swing.filechooser.*;
import org.jdom.Document;


public class ProtoFrame extends Frame implements ActionListener {
	
	private Button ShowDepTreeButton, SelectQuestionButton, SelectFragmentButton, GenerateAnswerButton, ResetAllButton;
	private TextArea QuestionArea, FragmentArea, AnswerArea, DebugArea;
	private Label QuestionTypeLabel, QuestionTopicLabel, AnswerTypeLabel, WHwordLabel, FragmentTypeLabel, FragmentTopicLabel, TopicPlaceLabel, AnswerLabel, DebugLabel;
	private TextField QuestionTypeField, QuestionTopicField, AnswerTypeField, WHwordField, FragmentTypeField, FragmentTopicField, TopicPlaceField;
	
	private AnalyzerQ anaQ = new AnalyzerQ();	//analyzer object for the questions
	private AnalyzerF anaF = new AnalyzerF();	//analyzer object for the fragment
	private Formulator form;			//formulator object to show the generated answer
	private final int IntX = 600;			//width of the frame
	private final int IntY = 600;			//height of the frame
	
	boolean editorStarted = false;
	private Document treeDoc;                       
	private File questionFile;
				
	public ProtoFrame () {
		super("Demonstrator answer presentation");
		setLayout(null);
		setBackground( Color.lightGray );
		
		//Buttons
		SelectQuestionButton = new Button("Select Question");
		SelectQuestionButton.addActionListener (this);
		SelectQuestionButton.setBounds(30, 30, 100, 20);
		add(SelectQuestionButton);
		
		SelectFragmentButton = new Button("Select Fragment");
		SelectFragmentButton.addActionListener(this);
		SelectFragmentButton.setBounds(30, 55, 100, 20);
		add(SelectFragmentButton);
		
		ShowDepTreeButton = new Button("Show DepTree Q");
		ShowDepTreeButton.addActionListener(new ShowTreeAction("showTree"));
		ShowDepTreeButton.setBounds(30, 80, 100, 20);
		ShowDepTreeButton.setVisible(false);
		add(ShowDepTreeButton);
		
		GenerateAnswerButton = new Button("Generate Answer");
		GenerateAnswerButton.addActionListener(this);
		GenerateAnswerButton.setBounds(230, 375, 140, 20);
		add(GenerateAnswerButton);
		
		ResetAllButton = new Button("Clear ALL Fields");
		ResetAllButton.addActionListener(this);
		ResetAllButton.setBounds(230, 570, 140, 20);
		add(ResetAllButton);
		
		//TextAreas top
		QuestionArea = new TextArea( "", 5, 20, TextArea.SCROLLBARS_NONE );
		QuestionArea.setEditable(false);
		QuestionArea.setBounds(140, 30, 435, 20);
		add(QuestionArea, BorderLayout.CENTER);
		
		FragmentArea = new TextArea( "", 5, 20, TextArea.SCROLLBARS_VERTICAL_ONLY );
		FragmentArea.setEditable(false);
		FragmentArea.setBounds(140, 55, 435, 100);
		add(FragmentArea, BorderLayout.CENTER);
		
		//Lables & TextField middle
		QuestionTypeLabel = new Label("Question Type: ");
		QuestionTypeLabel.setBounds(30, 180, 100, 20);
		add(QuestionTypeLabel);

		QuestionTypeField = new TextField("*****"); //("Wh-word") / ("Yes/No") / ("Choice")
		QuestionTypeField.setBounds(140, 180, 150, 20);
		QuestionTypeField.setEditable(false);
		add(QuestionTypeField);
		
		QuestionTopicLabel = new Label("Question Topic: ");
		QuestionTopicLabel.setBounds(30, 200, 100, 20);
		add(QuestionTopicLabel);

		QuestionTopicField = new TextField("*****"); //("Wh-word") / ("Yes/No") / ("Choice")
		QuestionTopicField.setBounds(140, 200, 150, 20);
		QuestionTopicField.setEditable(false);
		add(QuestionTopicField);
		
		AnswerTypeLabel = new Label("Answer Type: ");
		AnswerTypeLabel.setBounds(30, 220, 100, 20);
		add(AnswerTypeLabel);
		
		AnswerTypeField = new TextField("*****"); //("Boolean") / ("Alternative") / ("Time/Place/Person")
							 //("Reaseon/Manner/(Object/Idea/Action))") / ("Amount/Duration/Frequency/Distance")
		AnswerTypeField.setBounds(140, 220, 150, 20);
		AnswerTypeField.setEditable(false);
		add(AnswerTypeField);
		
		WHwordLabel = new Label("WH-word: ");
		WHwordLabel.setBounds(30, 240, 100, 20);
		add(WHwordLabel);
		
		WHwordField = new TextField("*****"); //("Boolean") / ("Alternative") / ("Time/Place/Person")
							 //("Reaseon/Manner/(Object/Idea/Action))") / ("Amount/Duration/Frequency/Distance")
		WHwordField.setBounds(140, 240, 150, 20);
		WHwordField.setEditable(false);
		add(WHwordField);
		
		FragmentTypeLabel = new Label("Fragment Type: ");
		FragmentTypeLabel.setBounds(300, 180, 100, 20);
		add(FragmentTypeLabel);
		
		FragmentTypeField = new TextField("*****");
		FragmentTypeField.setBounds(415, 180, 150, 20);
		FragmentTypeField.setEditable(false);
		add(FragmentTypeField);
		
		FragmentTopicLabel = new Label("Fragment Subjects: ");
		FragmentTopicLabel.setBounds(300, 200, 100, 20);
		add(FragmentTopicLabel);
		
		FragmentTopicField = new TextField("*****");
		FragmentTopicField.setBounds(415, 200, 150, 20);
		FragmentTopicField.setEditable(false);
		add(FragmentTopicField);
		
		TopicPlaceLabel = new Label("Topic placement: ");
		TopicPlaceLabel.setBounds(300, 240, 100, 20);
		add(TopicPlaceLabel);
		
		TopicPlaceField = new TextField("*****"); //("Boolean") / ("Alternative") / ("Time/Place/Person")
							 //("Reaseon/Manner/(Object/Idea/Action))") / ("Amount/Duration/Frequency/Distance")
		TopicPlaceField.setBounds(415, 240, 150, 20);
		TopicPlaceField.setEditable(false);
		add(TopicPlaceField);
		
		AnswerLabel = new Label("Generated answer: ");
		AnswerLabel.setBounds(30, 270, 120, 20);
		add(AnswerLabel);

		//TextArea down
		AnswerArea = new TextArea( "", 5, 20, TextArea.SCROLLBARS_VERTICAL_ONLY );
		AnswerArea.setEditable(false);
		AnswerArea.setBounds(30, 290, 545, 80);
		add(AnswerArea, BorderLayout.CENTER);

		DebugLabel = new Label("Debugging info: ");
		DebugLabel.setBounds(30, 390, 120, 20);
		add(DebugLabel);
		
		DebugArea = new TextArea( "", 5, 20, TextArea.SCROLLBARS_VERTICAL_ONLY );
		DebugArea.setEditable(false);
		DebugArea.setBounds(30, 410, 545, 150);
		add(DebugArea, BorderLayout.CENTER);

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
            		int returnVal = fc.showOpenDialog(ProtoFrame.this);

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
            		int returnVal = fc.showOpenDialog(ProtoFrame.this);
            	
            		if (returnVal == JFileChooser.APPROVE_OPTION) {
	                	File f = fc.getSelectedFile();
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
    	
    	
    	
    class ShowTreeAction extends AbstractAction {
      	DocumentOwner owner;
    	public ShowTreeAction(String name) {
        	super(name);
        	this.owner = owner;
    	}
    	public void actionPerformed(ActionEvent e) {
        	if (JOptionPane.showConfirmDialog(null, "Are you sure you want me to show the graph?", "Show tree", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            	parlevink.parlegraph.model.MGraph mg = null;
            	try{
            	        treeDoc = Analyzer.makeDocument(questionFile);
            		mg = Analyzer.makeMGraph(treeDoc);
            		NodeLister.listNodes(treeDoc,0);
            		if (!editorStarted) { DepTreeTest.do_it(mg);
            		  editorStarted=true;
            		}else{
            			DepTreeTest.showMGraph(mg);
            		}
            		
            	}catch (Exception exc){
            		System.out.println("Exception thrown by Analyzer.readMGraph:"+exc.getMessage());
            	}
            	
        }
    }
}

	public static void main (String[] args ) {
	
		ProtoFrame pf = new ProtoFrame();
		pf.addWindowListener (new CloseWindowAndExit() );
	}
}