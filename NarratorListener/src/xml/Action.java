package xml;

public class Action {
	public String type;
	public String id;
	public String time_stamp;
	
	public Perform perform;
	public String presentation;
	
	public String toString(){
/*		String result = "Action: "+type+", "+id;
		if (perform!=null) result+="\n"+perform;*/
		String result = toFabula();
		return result;
	}
	
	public String toFabula(){
		String result = "";		
		result += "<node id=\""+id+"\">\n";
		result += "  <data key=\"EventType\">"+"action"+"</data>\n";
		result += "  <data key=\"Type\">"+type+"</data>\n";
		if (perform.agens!=null)	result += "  <data key=\"Agens\">"+perform.agens+"</data>\n";
		if (perform.patiens!=null)		result += "  <data key=\"Patiens\">"+perform.patiens+"</data>\n";
		if (perform.target!=null)		result += "  <data key=\"Target\">"+perform.target+"</data>\n";
		if (perform.instrument!=null)	result += "  <data key=\"Instrument\">"+perform.instrument+"</data>\n";
		if (time_stamp!=null)			result += "  <data key=\"Time\">"+time_stamp+"</data>\n";
		result += "</node>";
		return result;
	}
}
