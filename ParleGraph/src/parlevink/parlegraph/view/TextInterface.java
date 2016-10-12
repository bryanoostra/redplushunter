/* @author Dennis Reidsma, Twente University
 * @author Job Zwiers, Twente University
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:17 $    
 * @since version 0       
 */

// Last modification by: $Author: swartjes $
// $Log: TextInterface.java,v $
// Revision 1.1  2006/05/24 09:00:17  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:15  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2002/09/16 14:08:50  dennisr
// first add
//
// Revision 1.4  2002/05/16 10:00:54  reidsma
// Redocumentation

package parlevink.parlegraph.view;

/**
 * This interface is implemented by MComponents; it provides VComponents with a text they can display.
 * <br>
 * This interface is used by the viewers VTextVertex and VTextEdge, visualizing labeled edges and vertices.
 * MComponents that use those viewers need to implement this interface to 
 * provide the viewers with the text to display.
 * The two methods in this interface give the viewers access to the text 
 * they must display, but also make it possible to change the object by 
 * giving it a new text.
 * <br>
 * How this new text is interpreted by the MComponent is a matter of implementation.
 * A MLabeledVertex will use the new text as a label; the class parlevink.knowledgegraphs.MKGEdge
 * for example will parse the new text into a label and a weight.
 * <br> See also the documentation of for example the classes VTextVertex or VTextEdge,
 * or the label modification code for knowledge graph edges (MKGEdge) in the knowledge graph controller
 * (MKGController).
 */
public interface TextInterface {
    
    /**
     * This method provides the model component implementing this 
     * interface with a new text such as the view component is supposed to display.
     * How this text is interpreted (as label or as complex structured data)
     * is up to the model component; it is not certain that after a call of 
     * setText(s), getText() will equal s
     * <br>
     * NB: if this object is a model component, this method may flag the component dirty!
     */
    public String getText();
    
    /**
     * This method provides view components with a text they can display 
     * showing information of the model component implementing this interface.
     * This may be for example a label, or a string version of other information.
     */
    public void setText(String newText);
   
}
