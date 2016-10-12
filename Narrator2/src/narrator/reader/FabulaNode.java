package narrator.reader;

import java.util.Random;
import java.util.Vector;

import natlang.rdg.model.Instrument;
import natlang.rdg.model.PlotElement;
import natlang.rdg.model.Detail;

public class FabulaNode {
	private final int id;
	private String type;
	private String agens;
	private String patiens;
	private String target;
	private String instrument;
	private long time; 
	private String name;
	private PlotElement subpe;
	private Vector<Detail> details;
	private boolean isFlashback = false;
	private boolean succesful = true;
	
	public FabulaNode(int id){
		this.id = id;
		details = new Vector<Detail>();
	}
	
	/*		
	kind = k;
	name = n;
	agens = a;
	patiens = p;
	target = t;
	this.instr = instr;*/
	public FabulaNode(String kind, String name, String agens,
			String patiens, String target, String instrument) {
		Random rand = new Random();
		id = rand.nextInt(99999);
		
		this.type = kind;
		this.name = name;
		this.agens = agens;
		this.patiens = patiens;
		this.target = target;
		this.instrument = instrument;
		details = new Vector<Detail>();
	}

	public PlotElement toPlotElement(){
		Instrument instr = null;
		if (!instrument.equals("None"))
			instr = new Instrument(instrument,2); // always make it an object for now.
		
		PlotElement result = new PlotElement(type, name, agens, patiens, target, instr, time);
		
		if (subpe!=null){
			result.setSubElement(subpe);
		}
		
		if (details.size()!=0){
			result.setDetails(details);
		}
		
		if (!succesful)
			result.setDescr("failed");
		
		result.setFlashback(isFlashback);
		
		return result; 
	}

	@Override
	public String toString(){
		return "ID: "+getName()+", Type: "+getType()+", Agens: "+getAgens()+ ", Patiens: "+getPatiens()+", Time: "+getTime();
	}
	
	public int getId() {
		return id;
	}
	
	public String getName(){
		//return (type+String.format("%02d", id)).replaceAll("\\s","");
		return name;
	}

	public void setName(String name){
		this.name=name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getPatiens() {
		return patiens;
	}

	public void setPatiens(String patiens) {
		this.patiens = patiens;
	}

	public String getAgens() {
		return agens;
	}

	public void setAgens(String agens) {
		this.agens = agens;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public PlotElement getSubPlotElement() {
		return subpe;
	}

	public void setSubPlotElement(PlotElement subpe) {
		this.subpe = subpe;
	}
	
	/**
	 * Returns the vector with Details.
	 * In this case the Vector contains Detail objects.
	 */
	public Vector<Detail> getDetails()
	{
		return details;
	}
	
	/**
	 * Adds a detail to the details vector
	 * @param ie
	 */
	public void addDetail(Detail ie)
	{
		details.addElement(ie);		
	}

	public boolean isFlashback() {
		return isFlashback;
	}

	public void setFlashback(boolean isFlashback) {
		this.isFlashback = isFlashback;
	}

	public boolean isSuccesful() {
		return succesful;
	}

	public void setSuccesful(boolean succesful) {
		this.succesful = succesful;
	}
	
}
