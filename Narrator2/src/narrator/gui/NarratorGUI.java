package narrator.gui;

import java.util.Arrays;
import narrator.Main;
import narrator.shared.NarratorException;
import narrator.shared.Settings;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

@Deprecated
public class NarratorGUI {
  // These filter names are displayed to the user in the file dialog. Note that
  // the inclusion of the actual extension in parentheses is optional, and
  // doesn't have any effect on which files are displayed.
  private static final String[] FILTER_NAMES = {
      "GraphML Fabula Files (*.graphml)",
      "XML Files(*.xml)",
      "All Files (*.*)"};

  // These filter extensions are used to filter which files are displayed.
  private static final String[] FILTER_EXTS = { "*.graphml", "*.xml", "*.*"};

  //define these early so we can use them.
  Button start = null;
  String file = null;
  String language = null;
  Display display = null;
  boolean tenseSelected = false;
  
  /**
   * Runs the application
   */
  public void run() {
    display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Narrator 2: Choose settings");
    shell.setSize(521,132);
    
    Monitor primary = display.getPrimaryMonitor();
    Rectangle bounds = primary.getBounds();
    Rectangle rect = shell.getBounds();
    
    int x = bounds.x + (bounds.width - rect.width) / 2;
    int y = bounds.y + (bounds.height - rect.height) / 2;
    
    //System.out.println(rect);
    shell.setLocation(x, y);

    createContents(shell);
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }

  /**
   * Creates the contents for the window
   * 
   * @param shell the parent shell
   */
  public void createContents(final Shell shell) {
    shell.setLayout(new GridLayout(5, true));

    new Label(shell, SWT.NONE).setText("Fabula File:");

    final Text fileName = new Text(shell,SWT.READ_ONLY | SWT.BORDER);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.horizontalSpan = 3;
    fileName.setLayoutData(data);
 
    Button open = new Button(shell, SWT.PUSH);
    open.setText("Open...");
    open.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        // User has selected to open a single file
        FileDialog dlg = new FileDialog(shell, SWT.OPEN);
        dlg.setFilterNames(FILTER_NAMES);
        dlg.setFilterExtensions(FILTER_EXTS);
        String fn = dlg.open();
        if (fn != null) {
          fileName.setText(fn);
          file = fn;
          if (language!=null && tenseSelected) start.setEnabled(true);
        }
      }
    });
     
    Label labelLang = new Label(shell, SWT.NULL);
    labelLang.setText("Choose language:");
    
    final Combo combo = new Combo(shell, SWT.NULL);
    
    String[] languages = new String[]{"Dutch"};
    
    Arrays.sort(languages);
    
    for(int i=0; i<languages.length; i++)
      combo.add(languages[i]);
    
    combo.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        //System.out.println("Selected index: " + combo.getSelectionIndex() + ", selected item: " + combo.getItem(combo.getSelectionIndex()) + ", text content in the text field: " + combo.getText());
        String result = combo.getItem(combo.getSelectionIndex());
    	  if (result!=null){
    		if (result.equals("Dutch")) language = "NL";
    		else language = result;
	        if (file!=null && tenseSelected) start.setEnabled(true);
        }
      }

      public void widgetDefaultSelected(SelectionEvent e) {
        //System.out.println("Default selected index: " + combo.getSelectionIndex() + ", selected item: " + (combo.getSelectionIndex() == -1 ? "<null>" : combo.getItem(combo.getSelectionIndex())) + ", text content in the text field: " + combo.getText());
        String text = combo.getText();
        if(combo.indexOf(text) < 0) { // Not in the list yet. 
          combo.add(text);
          // Re-sort
          String[] items = combo.getItems();
          Arrays.sort(items);
          combo.setItems(items);
        }
      }
    });
    
    //create an empty label to fill the space so that the next combo is on the next row.
    
    Label emptyLabel2 = new Label(shell, SWT.NONE);
    emptyLabel2.setText("");
    emptyLabel2.setLayoutData(data);
    
    Label labelTense = new Label(shell, SWT.NULL);
    labelTense.setText("Choose tense:");
    
    final Combo combo2 = new Combo(shell, SWT.NULL);
    
    String[] tenses = new String[]{"Present","Past"};
    
    Arrays.sort(tenses);
       
    for(int i=0; i<tenses.length; i++)
      combo2.add(tenses[i]);
    
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
	        if (file!=null && language!=null && tenseSelected) start.setEnabled(true);
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
    
    Label emptyLabel3 = new Label(shell, SWT.NONE);
    emptyLabel3.setText("");
    emptyLabel3.setLayoutData(data);
 
    Label labelFocalization = new Label(shell, SWT.NULL);
    labelFocalization.setText("Focalizing character:");
    
    final Combo combo3 = new Combo(shell, SWT.NULL);
    
    String[] characters = new String[]{" No Focalization","Adrie","Barry"};
    
    Arrays.sort(characters);
       
    for(int i=0; i<characters.length; i++)
      combo3.add(characters[i]);
    
    combo3.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent e) {
        //System.out.println("Selected index: " + combo.getSelectionIndex() + ", selected item: " + combo.getItem(combo.getSelectionIndex()) + ", text content in the text field: " + combo.getText());
        String result = combo3.getItem(combo3.getSelectionIndex());
    	  if (result!=null){
/*    		if (result.equals("Past")) {
    			Settings.setPastTense();
    			tenseSelected = true;
    		}
    		if (result.equals("Present")){
    			Settings.setPresentTense();
    			tenseSelected = true;
    		}
	        if (file!=null && language!=null && tenseSelected) start.setEnabled(true);*/
    		;
        }
      }

      public void widgetDefaultSelected(SelectionEvent e) {
        //System.out.println("Default selected index: " + combo.getSelectionIndex() + ", selected item: " + (combo.getSelectionIndex() == -1 ? "<null>" : combo.getItem(combo.getSelectionIndex())) + ", text content in the text field: " + combo.getText());
        String text = combo3.getText();
        if(combo3.indexOf(text) < 0) { // Not in the list yet. 
          combo3.add(text);
          // Re-sort
          String[] items = combo3.getItems();
          Arrays.sort(items);
          combo3.setItems(items);
        }
      }
    });
    
    //create an empty label to fill the space so that the start button is on the next row.
    
    Label emptyLabel1 = new Label(shell, SWT.NONE);
    emptyLabel1.setText("");
    emptyLabel1.setLayoutData(data);
    
    start = new Button(shell, SWT.PUSH);
    start.setText("Start Narrator");
    start.setEnabled(false);
    start.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
        	//start Narrator
        	try{
        		String result = new Main(file).start();
        		display.dispose();
        		new ResultWindow(display).open(result);
        	} catch (NarratorException e){
        		//Some problem with file, give an error
        		//box and ask again.
        		MessageBox mb = new MessageBox(shell);
                mb.setText("Narrator Error");
                mb.setMessage(e.getMessage());
                mb.open();
        	}
        }
      });
  }

  /**
   * The application entry point
   * 
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    new NarratorGUI().run();
  }
}