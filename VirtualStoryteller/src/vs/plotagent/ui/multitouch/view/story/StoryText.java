package vs.plotagent.ui.multitouch.view.story;

import java.util.ArrayList;

import org.apache.commons.lang.WordUtils;
import org.mt4j.MTApplication;
import org.mt4j.components.visibleComponents.font.FontManager;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.util.MTColor;

/**
 * Text Area representing the story. 
 */
public class StoryText extends MTTextArea {

	public static final float MIN_HEIGHT = 100;
	
	private MTApplication mtApp;
	private StoryWidget parent;
	
	private static int MAX_CHARS = 55;
	private static String INDENT = "     ";
	
	private ArrayList<String> lines = new ArrayList<String>();
	private int numLinesVisible = 0;
	private boolean linesDirty = false;
	private int currentRoundNumber = 1;
	
	private float currentHeight;
	
	private float fontHeight;
	
	StringBuilder txtBuilder;
	
	private static MTColor black = new MTColor(0,0,0);
	private static MTColor gray = new MTColor(128,128,128);
	private static MTColor white = new MTColor(255,255,255);
	
	public static MTColor scrollColor = new MTColor(128, 115, 100);
	public static MTColor scrollBGColor = new MTColor(232, 216, 164);
	
	public StoryText(MTApplication pApplet, StoryWidget parentWidget) {
	
		super(pApplet, FontManager.getInstance().createFont(pApplet, "maian.ttf", 
				4, 	//Font size
				black,  //Font fill color
				scrollColor));	//Font outline color);
		
		parent = parentWidget;
		
		mtApp = pApplet;	
		
		fontHeight = getFont().getFontAbsoluteHeight();// + this.getInnerPadding();
		
		txtBuilder = new StringBuilder();
						
		setInnerPadding(0);
		
		currentHeight = StoryWidget.MIN_HEIGHT_LOCAL;
		
		//setAnchor(PositionAnchor.UPPER_LEFT);
	}
	
	/**
	 * Add an operator result, so it can be displayed as text.
	 * 
	 * @param orRoundNumber the round number of the result
	 * @param orText the text that narrates the operator
	 * @param success whether the operator was successful.
	 */
	public void addOperatorResult(int orRoundNumber, String orText, boolean success) {
		linesDirty = true;
		
		while(currentRoundNumber < orRoundNumber) {
			//because there currently is only 1 action in each round, numbering rounds in de scrolls is kinda useless:
			//lines.add("----- Round " + currentRoundNumber + " -----");
			currentRoundNumber++;
		}
		
		if(success) { 
			String[] wrap = wrap(orText);
			for (String w: wrap) {		
				lines.add(w);
			}
		} 
		
		parent.textChanged();
	}
	
	/**
	 * Wraps a given unwrapped line.
	 * @param unwrapped String of text that needs to be wrapped.
	 * @return array of strings, each representing a wrapped and possibly indented portion of the original string.
	 */
	public String[] wrap(String unwrapped) {
		
		String wrapped = WordUtils.wrap(unwrapped, MAX_CHARS, "\n", false);
		String[] splitted = wrapped.split("\n");
		
		// Add indent spaces
		for (int i = 1; i < splitted.length; i++) {
			String s = INDENT + splitted[i];
			splitted[i] = s;
		}
		return splitted;
	}

	/**
	 * Updates preferred height of the text area. As a consequence, more or less lines may 
	 * be displayed on the text area.
	 * 
	 * @param preferredHeight
	 */
	public void updatePreferredHeight(float preferredHeight) {
		
		currentHeight = preferredHeight;
		
		int linesPossible = linesPossible(preferredHeight);
		//System.out.println(""+this.getName()+ " Lines possible in " + preferredHeight + ": " + linesPossible);
		
		// Rebuild checks for speedup:

		// 	1. only rebuild if more or less lines are possible than already visible, or when the lines have changed.
		if (linesPossible == numLinesVisible && ! linesDirty) return;		
		
		//  2. only rebuild if we do not see all lines already. 		
		if (numLinesVisible == lines.size() && linesPossible >= lines.size()) return;		
		
		// Add possible lines to textarea.		
		int beginIndex = Math.max(0, lines.size() - linesPossible);
		
		//System.out.println("Adding lines " + beginIndex + " to " + lines.size());
		
		// Clear buffer.
		txtBuilder.setLength(0);
		
		// Add lines followed by a newline (if there is more than one line).
		if (lines.size() > 1) {
			for (int i = beginIndex; i < lines.size() - 1; i++) {
				txtBuilder.append(lines.get(i));
				txtBuilder.append('\n');
			}
		}
		
		// Add last line (if there is one).
		if (lines.size() > 0) {
			txtBuilder.append(lines.get(lines.size() - 1));
		}
		
		setText(txtBuilder.toString());
		
		numLinesVisible = Math.min(linesPossible, lines.size());
		
		linesDirty = false;
		
	}
	
	/**
	 * @param height height available for the story text
	 * @return how many lines are possible given the available height.
	 */
	private int linesPossible(float height) {

		//System.out.println("Fontheight: " + fontHeight + "\nPadding: " + padding + "\nLines possible for height " + height + ": " + linesPossible);
		
		return Math.round(height / fontHeight);
	}

//	public void translate(Vector3D vect) {
//		if(parent==null || !parent.parchementRollIsBeingDragged()) {
//			super.translate(vect);
//		} else {	
//			//do nothing when parchmentRoll is already being dragged
//		}
//	}
	
	public void testStory() {
		addOperatorResult(1, "This is a test of the story widget.", true);
		addOperatorResult(1, "It should, among many things, be able to deal with extremely long lines, which is not an easy thing to do.", true);
		addOperatorResult(2, "And short ones.", true);
		addOperatorResult(2, "But they already work well.", true);
		addOperatorResult(3, "Multiple rounds is another issue.", true);
		addOperatorResult(4, "Multiple rounds is another issue.", true);
		addOperatorResult(5, "Multiple rounds is another issue.", true);
		addOperatorResult(6, "That worked well three times. This is a good sign that things are working as they should. Let's add lots of lines to see how scrolling 'floats'.", true);
		addOperatorResult(7, "And now on to the real story.", true);
		addOperatorResult(8, "Which is a great story.", true);
		addOperatorResult(9, "It's about Little Red Riding Hood.", true);
		addOperatorResult(10, "The End.", true);
		//addOperatorResult(2, "It should, among many things, be able to deal with extremely long lines, which is not an easy thing to do.", true);
	}
	
}
