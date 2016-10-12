package stomp;

import javax.servlet.ServletContext;

public class StompConnector {
	AGENTConnector connection;
	String server, userid, password;
	int port;
	ServletContext application;
	
	public StompConnector(ServletContext application){
		this.application = application;
	}
	
	public String connect(){
		connection = AGENTConnector.getInstance(server, port, userid, password,application);
		if (connection == null) return "Connection failed!";
		else return "Connection succesful!";
	}
	
	public String test(){
		//server = "hmics05.ewi.utwente.nl";
		server = "130.89.13.30";
		//server = "localhost";
		port = 61613;
		userid = "admin";
		password = "Parle$vink";
		//password = "admin";
		String result = "";
		try{result = connect();}
		catch(Exception e){result = e.getLocalizedMessage();}
		return result;
	}
}
