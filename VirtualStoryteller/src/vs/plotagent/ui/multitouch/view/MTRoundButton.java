package vs.plotagent.ui.multitouch.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import org.mt4j.components.bounds.BoundsZPlaneRectangle;
import org.mt4j.components.bounds.IBoundingShape;
import org.mt4j.components.interfaces.IclickableButton;
import org.mt4j.components.visibleComponents.shapes.AbstractShape;
import org.mt4j.components.visibleComponents.shapes.MTRoundRectangle;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;

import processing.core.PApplet;
import vs.plotagent.ui.multitouch.view.actioninterface.IButtonStrip;
import vs.plotagent.ui.multitouch.view.actioninterface.InterfaceButtonClickAction;

/**
 * The Class MTButton.
 * A tapprocessor is registered automatically. We can check if the button was
 * clicked by adding an actionlistener to it.
 * @author Christopher Ruff
 */
public class MTRoundButton extends MTRoundRectangle implements IclickableButton {
	
	/** The selected. */
	private boolean selected;
	
	/** The registered action listeners. */
	private ArrayList<ActionListener> registeredActionListeners;
	
	private IButtonStrip _strip;
	
	/**
	 * Instantiates a new mT image button.
	 * 
	 * @param texture the texture
	 * @param pApplet the applet
	 */
	public MTRoundButton(float arcw, float arch, PApplet pApplet) {
		super(0, 0, 0, 200, 50, arcw, arch, pApplet);
		
		setup(pApplet);
	}
	
//	public MTRoundButton(PImage texture, PApplet pApplet) {
//		super(texture, pApplet);
//		
//		setup(pApplet);
//	}
	
	private void setup(PApplet pApplet) {
		this.registeredActionListeners = new ArrayList<ActionListener>();
		
		this.setName("Unnamed image button");
		
		this.selected = false;
		
		//this.setGestureAllowance(DragProcessor.class, false);
		//this.setGestureAllowance(RotateProcessor.class, false);
		//this.setGestureAllowance(ScaleProcessor.class, false);
		
		this.setEnabled(true);
		this.setBoundsBehaviour(AbstractShape.BOUNDS_ONLY_CHECK);
		
		//Make clickable
		this.setGestureAllowance(TapProcessor.class, true);
		this.registerInputProcessor(new TapProcessor(pApplet));
		this.addGestureListener(TapProcessor.class, new InterfaceButtonClickAction(this));
		
		//Draw this component and its children above 
		//everything previously drawn and avoid z-fighting
		this.setDepthBufferDisabled(true);
	}
	
	
	@Override
	protected void setDefaultGestureActions() {
		//Dont register the usual drag,scale,rot processors
	}
	
	
	@Override
	protected IBoundingShape computeDefaultBounds(){
		return new BoundsZPlaneRectangle(this);
	}
	

	/**
	 * Adds the action listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void addActionListener(ActionListener listener){
		if (!registeredActionListeners.contains(listener)){
			registeredActionListeners.add(listener);
		}
	}
	
	/**
	 * Removes the action listener.
	 * 
	 * @param listener the listener
	 */
	public synchronized void removeActionListener(ActionListener listener){
		if (registeredActionListeners.contains(listener)){
			registeredActionListeners.remove(listener);
		}
	}
	
	/**
	 * Gets the action listeners.
	 * 
	 * @return the action listeners
	 */
	public synchronized ActionListener[] getActionListeners(){
		return (ActionListener[])registeredActionListeners.toArray(new ActionListener[this.registeredActionListeners.size()]);
	}
	
	/**
	 * Fire action performed.
	 */
	protected void fireActionPerformed() {
		ActionListener[] listeners = this.getActionListeners();
		synchronized(listeners) {
			for (int i = 0; i < listeners.length; i++) {
				ActionListener listener = (ActionListener)listeners[i];
				listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "action performed on tangible button"));
			}
		}
	}
	
	/**
	 * fires an action event with a ClickEvent Id as its ID.
	 * 
	 * @param ce the ce
	 */
	public void fireActionPerformed(TapEvent ce) {
		ActionListener[] listeners = this.getActionListeners();
		synchronized(listeners) {
			for (int i = 0; i < listeners.length; i++) {
				ActionListener listener = (ActionListener)listeners[i];
				listener.actionPerformed(new ActionEvent(this, ce.getTapID(),  "action performed on tangible button"));
			}
		}
	}

	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set this button as selected
	 * @param selected whether or not to set the button as selected
	 * @param propagate whether to propagate the selection (i.e. generate further events)
	 */
	public void setSelected(boolean selected, boolean propagate) {
		this.selected = selected;
		
		if (_strip != null && propagate) {
			_strip.buttonSelected(this, selected);
		}
		
		
//			this.setStrokeWeight(selected ? this.getStrokeWeight() + 2 : 0);
	}
	
	public void setSelected(boolean selected) {
		setSelected(selected, true);
	}

	public void registerStrip(IButtonStrip strip) {
		_strip = strip;
	}
}
