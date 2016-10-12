package stomp;

import java.io.File;
import java.util.Random;

import javax.servlet.ServletContext;

import pk.aamir.stompj.Connection;
import pk.aamir.stompj.ErrorHandler;
import pk.aamir.stompj.ErrorMessage;
import pk.aamir.stompj.StompJException;

public class AGENTConnector {
	final static String TOPIC = "/topic/game.1.world";
	final static String SYSTEMTOPIC = "/topic/system";

	static boolean connected;
	static AGENTConnector instance;

	String user;
	String idrequest;
	String server, userid, password;
	Connection con;
	int port;
	ServletContext application;

	public static AGENTConnector getInstance(String server, int port, String userid, String password,ServletContext application){
		if (instance!=null) return instance;
		AGENTConnector temp = new AGENTConnector(server, port, userid, password,application);
		if (connected){
			instance = temp;
		}
		return instance;
	}

	private AGENTConnector(String server, int port, String userid, String password,ServletContext application){
		this.application = application;

		Random rand = new Random();
		user = "Marissa"+rand.nextInt(999);
		idrequest = "<message type=\"user_id_request\">"+
				"<proposed_user id=\""+user+"\"/>"+
				"</message>";

		con = new Connection(server, port, userid, password);
		con.setErrorHandler(new ErrorHandler()
		{
			@Override
			public void onError(ErrorMessage errorMsg) {
				System.out.println(errorMsg.getMessage());
				System.out.println(errorMsg.getContentAsString());
			}
		});

		try{con.connect();connected=true;}
		catch(StompJException e){
			connected = false;
		}

		if (connected) connect();
	}

	public void connect(){
		con.subscribe(SYSTEMTOPIC, false);
		NarratorMessageHandler handler = NarratorMessageHandler.refreshInstance();
		con.addMessageHandler(SYSTEMTOPIC,handler);		
		con.subscribe(TOPIC, false);
		con.addMessageHandler(TOPIC,NarratorMessageHandler.getInstance());
		handler.setUser(user);
		con.send(idrequest, SYSTEMTOPIC);

		con.send("<message type=\"init_world_state_request\">" +
				"<user id=\""+handler.user+"\" /></message>",TOPIC);

		handler.setWriteDir((File)application.getAttribute("javax.servlet.context.tempdir"));

		;
	}
}
