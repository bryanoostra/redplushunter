package parlevink.util;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

/**
 * Displays a window where the default help page is loaded.
 * <br>
 * Using the javahelp pacakges might provide a better alternative.
 *
 * @author Galina Slavova <br>
 *	Niek Schmoller
 *
 * @version <i> 1.2 </i> 29-11-2002 <br>
 * history: <ul>
 * <li><i> 1.2 </i></n> Added method: hyperlinkUpdate</li></ul>
 * <li><i> 1.1 </i></n> Help viewer is resized and set as not editable </li></ul>
 * <li><i> 1.0 </i></n> Initial version. </li></ul>
 */

public class HelpViewer extends JFrame implements HyperlinkListener {
	
	protected static HelpViewer singletonFrame;

	protected JEditorPane editorPane;
	
	private static final long serialVersionUID = 0L;
	
	/**
	 * Constructs a new <code> HelpViewer </code> object, and displays the
	 * help window.
	 */
	protected HelpViewer() {
		super("Help");
		
		setSize(800,610);
		setResizable(false);
        	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        	setLocation((screenSize.width-800)/2,(screenSize.height-600)/2);
		
			editorPane = new JEditorPane();
			editorPane.setEditable(false);
			editorPane.addHyperlinkListener(this);
			getContentPane().add(editorPane);
            addWindowFocusListener(new WindowAdapter() {
                public void windowGainedFocus(WindowEvent e) {
                    repaint();
                }
            });

	      try {
        	ImageIcon i = null;
	        i = new ImageIcon(getClass().getResource("help.jpg"));
	        setIconImage(i.getImage());	
	      } catch (Exception e) {
	        System.out.println("Icon not loaded: " + e.getMessage());
	        e.printStackTrace();
	      }
			setVisible(true);
	}

	public static void showHelp(URL helpPage) {
		try {		
			if (singletonFrame == null) {
				singletonFrame = new HelpViewer();
			}
			singletonFrame.setVisible(true);
			singletonFrame.editorPane.setPage(helpPage);
		} catch (IOException e) {
		    System.out.println("--------------------------");
		    System.out.println("Error creating help viewer");
		    System.out.println("stacktrace:");
		    e.printStackTrace();
			singletonFrame.dispose();
		    System.out.println("--------------------------");
		}
	}
	
	/**
	 * HTML frames are embedded in the document, and this method
	 * should change a portion of the current document.
	 */
        public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			editorPane = (JEditorPane) e.getSource();
			
			if (e instanceof HTMLFrameHyperlinkEvent) {
				HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
				HTMLDocument doc = (HTMLDocument)editorPane.getDocument();
				doc.processHTMLFrameHyperlinkEvent(evt);
			} else {
				try {
					editorPane.setPage(e.getURL());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

    public static Action createHelpAction(URL fileName) {
        return new HelpAction(fileName);
    }
    
}

class HelpAction extends AbstractAction {
    protected URL helpUrl;
    private static final long serialVersionUID = 0L;
    public HelpAction (URL newHelpUrl) {
        super("Help");
        helpUrl = newHelpUrl;
    }

    public void actionPerformed(ActionEvent e) {
        HelpViewer.showHelp(helpUrl);
    }
}
