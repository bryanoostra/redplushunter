package narrator.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import narrator.Main;
import narrator.reader.CharacterModel;
import narrator.reader.WorldReader;
import narrator.shared.NarratorException;
import narrator.shared.Settings;
import natlang.rdg.libraries.LibraryConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class NarratorTabGUI {
	  // These filter names are displayed to the user in the file dialog. Note that
	  // the inclusion of the actual extension in parentheses is optional, and
	  // doesn't have any effect on which files are displayed.
	  public static final String[] FILTER_NAMES = {
	      "GraphML Fabula Files (*.graphml)",
	      "XML Files(*.xml)",
	      "All Files (*.*)"};

	  // These filter extensions are used to filter which files are displayed.
	  public static final String[] FILTER_EXTS = { "*.graphml", "*.xml", "*.*"};

	  public Display display = null;

  NarratorTabGUI() {
	    display = new Display();
	    Shell shell = new Shell(display);
	    shell.setText("Narrator 2: Choose settings");
	    shell.setSize(517,217);
	    
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    //System.out.println(rect);
	    shell.setLocation(x, y);
	  
	    shell.setLayout(new FillLayout());
		
		TabFolder tf = new TabFolder(shell, SWT.BORDER);
		
		TabItem ti2 = new TabItem(tf, SWT.BORDER);
		ti2.setText("Narrator");
		ti2.setControl(new GridComposite(tf,this));
		
		TabItem ti4 = new TabItem(tf, SWT.BORDER);
		ti4.setText("Settings");
		ti4.setControl(new SettingsComposite(tf,this));
		
		shell.open();
		while (!shell.isDisposed()) {
		  if (!display.readAndDispatch())
		    display.sleep();
		}
		display.dispose();
  }
  
  public static void main(String[] args) {
	  new NarratorTabGUI();
  }
}

class SettingsComposite extends Composite {
	String language;
	
	  public SettingsComposite(Composite c, final NarratorTabGUI gui) {
		    super(c, 0);
		    GridLayout gl = new GridLayout();
		    gl.numColumns = 5;
		    this.setLayout(gl);
		    
		    GridData data3 = new GridData(GridData.FILL_HORIZONTAL);
		    data3.horizontalSpan = 3;
		    
		    GridData data4 = new GridData(GridData.FILL_HORIZONTAL);
		    data4.horizontalSpan = 4;
		    
		    GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		    data1.horizontalSpan = 1;
		    GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		    data2.horizontalSpan = 2;
		    
		    Label labelLang = new Label(this, SWT.NULL);
		    labelLang.setText("Choose language:");
		    
		    final Combo combo = new Combo(this, SWT.NULL);
		    
		    String[] languages = new String[]{"Dutch"};
		    
		    Arrays.sort(languages);
		    
		    for(int i=0; i<languages.length; i++)
		      combo.add(languages[i]);
		    
		    if (languages.length>0) combo.select(0);
		    
		    combo.addSelectionListener(new SelectionListener() {
		      public void widgetSelected(SelectionEvent e) {
		        //System.out.println("Selected index: " + combo.getSelectionIndex() + ", selected item: " + combo.getItem(combo.getSelectionIndex()) + ", text content in the text field: " + combo.getText());
		        String result = combo.getItem(combo.getSelectionIndex());
		    	  if (result!=null){
		    		if (result.equals("Dutch")) language = "NL";
		    		else language = result;
		        }
		      }

		      public void widgetDefaultSelected(SelectionEvent e) {
		      }
		    });
		    		    
		    //create an empty label to fill the space so that the next combo is on the next row.  
		    Label emptyLabel3 = new Label(this, SWT.NONE);
		    emptyLabel3.setText("");
		    emptyLabel3.setLayoutData(data3);
		    
		    final Button checkIntroduction = new Button(this, SWT.CHECK);
		    checkIntroduction.setSelection(Settings.INTRODUCTION);
		    checkIntroduction.setText("Introduction");
		    checkIntroduction.pack();
		    
		    checkIntroduction.addSelectionListener(new SelectionListener() {
			      public void widgetSelected(SelectionEvent e) {
			    	  Settings.INTRODUCTION = checkIntroduction.getSelection();
			      }

			      public void widgetDefaultSelected(SelectionEvent e) {
			      }
			});
		    
		    //create an empty label to fill the space so that the next combo is on the next row.  
		    Label emptyLabel4 = new Label(this, SWT.NONE);
		    emptyLabel4.setText("");
		    emptyLabel4.setLayoutData(data1);
		    
		    final Button checkBGIS = new Button(this, SWT.CHECK);
		    checkBGIS.setSelection(Settings.BGIS);
		    checkBGIS.setText("Add names");
		    checkBGIS.pack();
		    
		    checkBGIS.addSelectionListener(new SelectionListener() {
			      public void widgetSelected(SelectionEvent e) {
			    	  Settings.BGIS = checkBGIS.getSelection();
			      }
			      public void widgetDefaultSelected(SelectionEvent e) {
			      }
			});
		    
		    //create an empty label to fill the space so that the next combo is on the next row.  
		    Label emptyLabel5 = new Label(this, SWT.NONE);
		    emptyLabel5.setText("");
		    emptyLabel5.setLayoutData(data2);
		    
		    final Button checkFlashback = new Button(this, SWT.CHECK);
		    checkFlashback.setSelection(Settings.FLASHBACK);
		    checkFlashback.setText("Add flashbacks");
		    checkFlashback.pack();
		    checkFlashback.addSelectionListener(new SelectionListener() {
			      public void widgetSelected(SelectionEvent e) {
			    	  Settings.FLASHBACK = checkFlashback.getSelection();
			      }
			      public void widgetDefaultSelected(SelectionEvent e) {
			      }
			});
		    
		    //create an empty label to fill the space so that the next combo is on the next row.  
		    Label emptyLabel6 = new Label(this, SWT.NONE);
		    emptyLabel6.setText("");
		    emptyLabel6.setLayoutData(data1);
		    
		    final Button checkDTS = new Button(this, SWT.CHECK);
		    checkDTS.setSelection(Settings.SHOWDTS);
		    checkDTS.setText("Show dependency trees");
		    checkDTS.pack();
		    checkDTS.addSelectionListener(new SelectionListener() {
			      public void widgetSelected(SelectionEvent e) {
			    	  Settings.SHOWDTS = checkDTS.getSelection();
			      }
			      public void widgetDefaultSelected(SelectionEvent e) {
			      }
			});
		    
		    //create an empty label to fill the space so that the next combo is on the next row.  
		    Label emptyLabel7 = new Label(this, SWT.NONE);
		    emptyLabel7.setText("");
		    emptyLabel7.setLayoutData(data2);
		    
		    final Button checkAdjectives = new Button(this, SWT.CHECK);
		    checkAdjectives.setSelection(Settings.ADDADJECTIVES);
		    checkAdjectives.setText("Add adjectives");
		    checkAdjectives.pack();
		    checkAdjectives.addSelectionListener(new SelectionListener() {
			      public void widgetSelected(SelectionEvent e) {
			    	  Settings.ADDADJECTIVES = checkAdjectives.getSelection();
			      }

			      public void widgetDefaultSelected(SelectionEvent e) {
			      }
			});
		    
		    //create an empty label to fill the space so that the next combo is on the next row.  
		    Label emptyLabel8 = new Label(this, SWT.NONE);
		    emptyLabel8.setText("");
		    emptyLabel8.setLayoutData(data1);
		    
		    final Button checkREG = new Button(this, SWT.CHECK);
		    checkREG.setSelection(Settings.ADDADJECTIVES);
		    checkREG.setText("Referring expressions");
		    checkREG.pack();
		    checkREG.addSelectionListener(new SelectionListener() {
			      public void widgetSelected(SelectionEvent e) {
			    	  Settings.REFEXP = checkREG.getSelection();
			      }

			      public void widgetDefaultSelected(SelectionEvent e) {
			      }
			});
		    
		    
/*		    //create an empty label to fill the space so that the next combo is on the next row.  
		    Label emptyLabel2 = new Label(this, SWT.NONE);
		    emptyLabel2.setText("");
		    emptyLabel2.setLayoutData(data);*/
	  }
}

class GridComposite extends Composite {
	  //define these early so we can use them.
	  public Button start = null;
	  public String file = null;
	  public String language = null;
	  final Combo combo3;
	  
	  public boolean tenseSelected = false;
	  public static final String NOFOCALIZATION = " No focalization";
	  String[] characters = new String[]{NOFOCALIZATION};
	  Vector<CharacterModel> characterList ;
	  
  public GridComposite(Composite c, final NarratorTabGUI gui) {
    super(c, 0);
    GridLayout gl = new GridLayout();
    gl.numColumns = 5;
    this.setLayout(gl);
    
    new Label(this, SWT.NONE).setText("Fabula File:");

    final Text fileName = new Text(this,SWT.READ_ONLY | SWT.BORDER);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.horizontalSpan = 3;
    fileName.setLayoutData(data);
    
    Button open = new Button(this, SWT.PUSH);
    open.setText("Open...");
    open.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        // User has selected to open a single file
        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
        dlg.setFilterNames(NarratorTabGUI.FILTER_NAMES);
        dlg.setFilterExtensions(NarratorTabGUI.FILTER_EXTS);
        String fn = dlg.open();
        if (fn != null) {
          fileName.setText(fn);
          file = fn;
          
          //Read the characters for the focalization list
          WorldReader wr = new WorldReader(file);
          try{wr.getXml();}
          catch(Exception e){
      		MessageBox mb = new MessageBox(getShell());
            mb.setText("Narrator Error");
            mb.setMessage("Something wrong with this file!\nMessage:\n"+e.getMessage());
            mb.open();
          }
          Vector<CharacterModel> tempCharacterList = wr.getCharacters();
          
          characterList = new Vector<CharacterModel>();
          for (CharacterModel c : tempCharacterList){
        	  if (!c.getName().equals("")){
        		  characterList.add(c);
        	  }
          }
          
          ArrayList<String> characterAL = new ArrayList<String>();
          characterAL.add(NOFOCALIZATION);
          for (CharacterModel c : characterList){
        	  characterAL.add(c.getName());
        	  //System.out.println(c.getName());
          }
          characters = new String[characterAL.size()];
          characterAL.toArray(characters);
          
          //Add the characters to the focalization combo
          combo3.removeAll();
          for(int i=0; i<characters.length; i++)
              combo3.add(characters[i]);
          combo3.select(0);
          
          //Enable the start button if we have a proper file
          start.setEnabled(true);
        }
      }
    });
   
    Label labelTense = new Label(this, SWT.NULL);
    labelTense.setText("Choose tense:");
    
    final Combo combo2 = new Combo(this, SWT.NULL);
    String[] tenses = new String[]{"Present","Past"};
    for(int i=0; i<tenses.length; i++)
      combo2.add(tenses[i]);
    if (Settings.PASTTENSE) combo2.select(1);
    else combo2.select(0);
    
    
    combo2.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        //System.out.println("Selected index: " + combo.getSelectionIndex() + ", selected item: " + combo.getItem(combo.getSelectionIndex()) + ", text content in the text field: " + combo.getText());
        String result = combo2.getItem(combo2.getSelectionIndex());
    	  if (result!=null){
    		if (result.equals("Past")) {
    			Settings.setPastTense();
    			tenseSelected = true;
    		}
    		if (result.equals("Present")){
    			Settings.setPresentTense();
    			tenseSelected = true;
    		}
        }
      }

      public void widgetDefaultSelected(SelectionEvent e) {
        //System.out.println("Default selected index: " + combo.getSelectionIndex() + ", selected item: " + (combo.getSelectionIndex() == -1 ? "<null>" : combo.getItem(combo.getSelectionIndex())) + ", text content in the text field: " + combo.getText());
        String text = combo2.getText();
        if(combo2.indexOf(text) < 0) { // Not in the list yet. 
          combo2.add(text);
          // Re-sort
          String[] items = combo2.getItems();
          Arrays.sort(items);
          combo2.setItems(items);
        }
      }
    });
    
    //create an empty label to fill the space so that the next combo is on the next row.
    GridData data2 = new GridData();
    data2.horizontalSpan = 3;
    Label emptyLabel2 = new Label(this, SWT.NONE);
    emptyLabel2.setText("");
    emptyLabel2.setLayoutData(data2);
    
    Label labelPersp= new Label(this, SWT.NULL);
    labelPersp.setText("Focalization perspective:");
    
    final Combo combo = new Combo(this, SWT.NULL);
    
    String[] perspectives = new String[]{"First","Second","Third"};
    
    for(int i=0; i<perspectives.length; i++)
      combo.add(perspectives[i]);
    
    combo.select(0);
    String perspective = Settings.PERSPECTIVE;
    if (perspective.equals(LibraryConstants.FIRST)) combo.select(0);
    if (perspective.equals(LibraryConstants.SECOND)) combo.select(1);
    if (perspective.equals(LibraryConstants.THIRD)) combo.select(2);
    
    combo.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        //System.out.println("Selected index: " + combo.getSelectionIndex() + ", selected item: " + combo.getItem(combo.getSelectionIndex()) + ", text content in the text field: " + combo.getText());
        String result = combo.getItem(combo.getSelectionIndex());
    	  if (result!=null){
    		if (result.equals("First")) Settings.PERSPECTIVE = LibraryConstants.FIRST;
    		else if (result.equals("Second")) Settings.PERSPECTIVE = LibraryConstants.SECOND;
    		else if (result.equals("Third")) Settings.PERSPECTIVE = LibraryConstants.THIRD;
        }
      }

      public void widgetDefaultSelected(SelectionEvent e) {
        //System.out.println("Default selected index: " + combo.getSelectionIndex() + ", selected item: " + (combo.getSelectionIndex() == -1 ? "<null>" : combo.getItem(combo.getSelectionIndex())) + ", text content in the text field: " + combo.getText());
        String text = combo.getText();
        if(combo.indexOf(text) < 0) { // Not in the list yet. 
          combo.add(text);
          // Re-sort
/*          String[] items = combo.getItems();
          Arrays.sort(items);
          combo.setItems(items);*/
        }
      }
    });
    
    Label emptyLabel3 = new Label(this, SWT.NONE);
    emptyLabel3.setText("");
    emptyLabel3.setLayoutData(data2);
 
    Label labelFocalization = new Label(this, SWT.NULL);
    labelFocalization.setText("Focalizing character:");
    
    combo3 = new Combo(this, SWT.NULL);
    
    //Arrays.sort(characters);
       
    for(int i=0; i<characters.length; i++)
      combo3.add(characters[i]);
    combo3.select(0);
    combo3.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        //System.out.println("Selected index: " + combo.getSelectionIndex() + ", selected item: " + combo.getItem(combo.getSelectionIndex()) + ", text content in the text field: " + combo.getText());
        String result = combo3.getItem(combo3.getSelectionIndex());
    	  if (result!=null){
    		  if (result.equals(NOFOCALIZATION))
    			  Settings.FOCALIZATION = false;
    		  else {
    			  Settings.FOCALIZATION = true;
    			  int index = combo3.getSelectionIndex();
    			  if (index >=1) Settings.FOCALIZEDCHARACTER = characterList.get(index - 1).getEntity();
    		  }
        }
      }

      public void widgetDefaultSelected(SelectionEvent e) {
        //System.out.println("Default selected index: " + combo.getSelectionIndex() + ", selected item: " + (combo.getSelectionIndex() == -1 ? "<null>" : combo.getItem(combo.getSelectionIndex())) + ", text content in the text field: " + combo.getText());
        String text = combo3.getText();
        if(combo3.indexOf(text) < 0) { // Not in the list yet. 
          combo3.add(text);
        }
      }
    });
    
    //create an empty label to fill the space so that the start button is on the next row.
    Label emptyLabel1 = new Label(this, SWT.NONE);
    emptyLabel1.setText("");
    emptyLabel1.setLayoutData(data2);
    
    start = new Button(this, SWT.PUSH);
    start.setText("Start Narrator");
    start.setEnabled(false);
    start.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
        	//start Narrator
        	try{
        		String result = new Main(file).start();
        		//gui.display.dispose();
        		new ResultWindow(gui.display).open(result);
        	} catch (NarratorException e){
        		//Some problem with file, give an error
        		//box and ask again.
        		MessageBox mb = new MessageBox(getShell());
                mb.setText("Narrator Error");
                mb.setMessage(e.getMessage());
                mb.open();
        	}
        	
        }
      });
  }
}
      