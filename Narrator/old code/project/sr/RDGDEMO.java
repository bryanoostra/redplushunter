package project.sr;

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
 *
 *  adapted by Feikje Hielkema, to show rhetdepgraphs
*/

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*; 	

import javax.swing.*;
import javax.swing.filechooser.*;
import org.jdom.Document;

import natlang.rdg.view.*;
import natlang.structure.*;
import project.qa.*;


public class RDGDEMO extends JFrame implements ActionListener 
{
	private JButton SelectRDGButton, ShowRDGButton;
	private JTextArea RDGArea, DebugArea;
	private JLabel DebugLabel;
	
	private AnalyzerRDG anaRDG = new AnalyzerRDG();	//analyzer object for the questions
	private final int IntX = 700;			//width of the frame
	private final int IntY = 400;			//height of the frame
	
	private RDGEditor myeditor,myfeditor;
	
	private Document treeDoc;                       
	private File RDGFile;
				
	public RDGDEMO () 
	{
		super("Demonstrator Rhetorical Dependency Graph presentation");
		getContentPane().setLayout(null);
		setBackground( Color.lightGray );
		
		//Buttons
		SelectRDGButton = new JButton("Select Rhetorical Dependeny Graph");
		SelectRDGButton.addActionListener (this);
		SelectRDGButton.setBounds(20, 30, 160, 20);
		getContentPane().add(SelectRDGButton);
		
		ShowRDGButton = new JButton("Show Rhetorical Dependency Graph");
		ShowRDGButton.addActionListener(new ShowTreeAction("showTree"));
		ShowRDGButton.setBounds(20, 80, 160, 20);
		ShowRDGButton.setVisible(false);
		getContentPane().add(ShowRDGButton);
		
		//TextAreas top
		RDGArea = new JTextArea( "", 5, 20); //, JTextArea.SCROLLBARS_NONE );
		RDGArea.setEditable(false);
		RDGArea.setBounds(200, 30, 455, 20);
		getContentPane().add(RDGArea, BorderLayout.CENTER);
		
		DebugLabel = new JLabel("Debugging info: ");
		DebugLabel.setBounds(30, 200, 120, 20);
		getContentPane().add(DebugLabel);
		
		DebugArea = new JTextArea( "", 5, 20); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		DebugArea.setEditable(false);
		DebugArea.setBounds(30, 220, 545, 150);
		getContentPane().add(DebugArea, BorderLayout.CENTER);

       	//Initialize frame at center and not resizable
       	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();    
        setLocation((screenSize.width - IntX)/2, (screenSize.height - IntY)/2);    
		setSize(IntX, IntY);
		setVisible (true);
		setResizable(false);
		
		System.out.println("*** Program started and ready for input ***");
		
	}
	
	
	public Document getDocument()
	{
		return treeDoc;
	}
	
	
	public void actionPerformed(ActionEvent e) 
	{
	  	JFileChooser fc = new JFileChooser("c:\\local\\corpus");  //Create a file chooser in the current directory
       	fc.setFileSelectionMode(JFileChooser.FILES_ONLY); 	  //Only selection of files possible
        	
       	XMLFilter XMLFil = new XMLFilter();
       	fc.setFileFilter(XMLFil);   		        	  //Only show *.xml files based on XMLFilter

       	//Handle SelectQuestionButton.
       	if (e.getSource() == SelectRDGButton) 
       	{
       		fc.setCurrentDirectory(new File("H:/scriptie/inputvoorbeelden/"));
        	int returnVal = fc.showOpenDialog(RDGDEMO.this);
        	if (returnVal == JFileChooser.APPROVE_OPTION) 
        	{
            	File f = fc.getSelectedFile();
            	RDGFile = f;
            	String rdg = "rhetdepgraph";
            	anaRDG = new AnalyzerRDG(f);    
              		
            	//Other action to be taken after selecting a question                		                		
          		RDGArea.setText(null);
                		
				//Forms the debugging area
           		DebugArea.setText(null);
           		DebugArea.append(anaRDG.showNodes(rdg));
           		ShowRDGButton.setVisible(true);
       		} 
       		else 
           		RDGArea.setText("Open RDG command cancelled!\n");
		}
    }
    	
    public static void main (String[] args) 
    {
		RDGDEMO pf = new RDGDEMO();
		pf.addWindowListener (new CloseWindowAndExit());
	}
    	
    	
    	
	class ShowTreeAction extends AbstractAction 
    {
    	public ShowTreeAction(String name) 
    	{
       		super(name);
    	}
    	
    	public void actionPerformed(ActionEvent e) 
    	{
       		natlang.rdg.model.RSDepTreeModel mg=null;
       		try
       		{
    	        //treeDoc = RSGraphViewer.makeDocument(RDGFile);
       			//mg = (natlang.narrator.model.RSDepTreeModel)RSGraphViewer.makeMGraph(treeDoc);
       			if (myeditor==null) 
       			{             				
       				myeditor = new RDGEditor("Rhetorical Dependency Graph");            				
       				myeditor.start(mg); 		
       			}
       			else
      			{		
    	        	myeditor.setVisible();            			      
            		myeditor.setMGraph(mg);            				
            	}
            		
            }
            catch (Exception exc)
            {
            	System.out.println("Exception thrown by RDGViewer.readMGraph:"+exc.getMessage());
            }
       	}
    }// end class action
}// end class RDGDEMO