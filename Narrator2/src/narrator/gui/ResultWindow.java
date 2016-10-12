package narrator.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ResultWindow {
	private Display display;
	private String text;
	
	public ResultWindow(Display display){
	    this.display = display;
	}
	
	  public void open(String text) {
	    this.text = text;
	    
	    Shell shell = new Shell(display);
	    shell.setText("Narrator 2: Result");


	    createContents(shell);
    
	    shell.pack();
	    
	    shell.setSize(500,250);
	    Monitor primary = display.getPrimaryMonitor();
	    Rectangle bounds = primary.getBounds();
	    Rectangle rect = shell.getBounds();
	    
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    
	    //System.out.println(rect);
	    shell.setLocation(x, y);
	    
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    shell.dispose();
	    //display.dispose();
	  }
	
	  public void createContents(final Shell shell) {
		    shell.setLayout(new GridLayout(1, false));

		    new Label(shell, SWT.NONE).setText("Generated text:");

		    final Text textBox = new Text(shell,SWT.READ_ONLY | SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		    GridData data = new GridData();
			data.horizontalAlignment = SWT.FILL;
			data.grabExcessHorizontalSpace = true;
			data.verticalAlignment = SWT.FILL;
			data.grabExcessVerticalSpace = true;
		    textBox.setLayoutData(data); 
		    textBox.setText(text); 
	}
	  
	  
	public static void main(String[] args) {
		new ResultWindow(new Display()).open("Dit is een testverhaal.");
	}  
}
