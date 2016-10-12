package xml;

public class Message {
	public String type;
	public String time;
	
	//for user_id_response
	public ProposedUser proposed_user;
	public User user;
	public World world;
	
	//for action_reponse
	public Action action;
	
	@Override
	public String toString(){
		String result = type+", ";
		if (proposed_user!=null) result+=proposed_user+", ";
		if (user!=null) result+=user+", ";
		if (world!=null) result+=world+", ";
		if (action!=null) result+="\n"+action;
		
		return result;
	}
	
}
