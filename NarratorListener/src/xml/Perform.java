package xml;

public class Perform {
	public Agens agens;
	public Patiens patiens;
	public Target target;
	public Instrument instrument;
	public String time;
	
	@Override
	public String toString(){
		String result = "";
		if (agens!=null) result+=agens+", ";
		if (patiens!=null) result+=patiens+", ";
		if (target!=null) result+=target+", ";
		if (instrument!=null) result+=instrument+", ";
		if (time!=null) result+=time+", ";
		return result;
	}
}
