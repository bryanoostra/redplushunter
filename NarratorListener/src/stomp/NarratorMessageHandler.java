package stomp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

import narrator.Main;
import narrator.shared.NarratorException;

import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter.UnknownFieldException;
import pk.aamir.stompj.MessageHandler;
import pk.aamir.stompj.Message;
import test.StringUtil;
import xml.Action;
import xml.Agens;
import xml.Perform;
import xml.Target;

public class NarratorMessageHandler implements MessageHandler {
	public static NarratorMessageHandler instance;
	private File writeDir;
	private ArrayList<String> allMessages;
	private MessageReader reader;
	public String user;
	public static final String WORLD_FILENAME = "world_testcrap.xml";
	public static final String FABULA_FILENAME = "fabula_fromserver.graphml";
	
	private NarratorMessageHandler(){
		allMessages = new ArrayList<String>();
		//allMessages.add("<action></action>");
		//allMessages.add("thing");
		//write a test action
		Action a = new Action();
		Perform p = new Perform();
		a.id="walk_to1";
		a.time_stamp="1";
		a.type="walk_to";
		a.perform = p;
		p.agens = new Agens("policeofficer");
		p.target = new Target("main_street");
		
		allMessages.add(a.toFabula());
		
		reader = new MessageReader();
	}
	
	public static NarratorMessageHandler getInstance(){
		if (instance==null) instance = new NarratorMessageHandler();
		return instance;
	}
	
	public static NarratorMessageHandler refreshInstance(){
		instance = new NarratorMessageHandler();
		return instance;
	}
	
    public synchronized void onMessage(Message msg) {
    	String s = msg.getContentAsString();
    	
    	System.out.println(s);
        //allMessages.add(s);

        xml.Message message = null;

        try{message = reader.read(s);}
        catch(UnknownFieldException e){
        	//This message contains something we don't understand.
        	//Ignore it for now.
        	System.err.println("Unknown message received: "+s);
        	e.printStackTrace();
        }
        if (message!=null){
        	try{
        		if (message.type.equals("user_id_response") && message.proposed_user.id.equals(user)){
        			System.out.println("User:"+user);
        			user = message.user.id;
        		}
            	if (message.type.equals("action_response")){
            		//Set message time stamp as node time stamp
            		message.action.time_stamp = message.time;
            		allMessages.add(message.action.toFabula());
            		//System.out.println(message.time);
            	}
            	if (message.type.equals("init_world_state_response") && message.user.equals(user))
            		writeWorldFile(s);
        	} catch(NullPointerException e){
        		System.err.println("Malformed message received: "+s);
        	}
        		
        }
        //serialize message
        
        //if user_id_response
        //write user as what we get
        
        //if init_world_state_response
        //write world.xml to whatever
    }
    
    private void writeWorldFile(String worldString){
    	String temp1 = StringUtil.getBetween(worldString, "<world scenario_id=\"0\">" , "</world>");
		File tmpFile = new File(writeDir, WORLD_FILENAME);
		if (tmpFile.isFile()) tmpFile.delete();
		
		try {
			PrintWriter out;
			if (writeDir!=null)
				out = new PrintWriter(new FileWriter(tmpFile, true));
			else
				out = new PrintWriter(WORLD_FILENAME);
			out.write("<world scenario_id=\"0\">");
			out.write(temp1);
			out.write("</world>");
			System.out.println("Written to "+tmpFile.getAbsolutePath());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
    }
    
    public void writeFabulaFile(){
    	String LEXICON_FILE = "lexicon_NL.xml";
    	
    	String FABULA_START = 	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
    							"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
    							"xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n"+
    							"<!--World Setting file: "+"world.xml"+"-->\n"+
    							"<!--Lexicon file lang=\"NL\": "+LEXICON_FILE+"-->\n"+
    							"<key id=\"EventType\" for=\"node\" attr.name=\"EventType\" attr.type=\"string\"><default>NoType</default></key>\n"+
      							"<key id=\"Type\" for=\"node\" attr.name=\"Type\" attr.type=\"string\"><default>NoType</default></key>\n"+
      							"<key id=\"Agens\" for=\"node\" attr.name=\"Agens\" attr.type=\"string\"><default></default></key>\n"+
      							"<key id=\"Patiens\" for=\"node\" attr.name=\"Patiens\" attr.type=\"string\"><default></default></key>\n"+
      							"<key id=\"Target\" for=\"node\" attr.name=\"Target\" attr.type=\"string\"><default></default></key>\n"+
      							"<key id=\"Instrument\" for=\"node\" attr.name=\"Instrument\" attr.type=\"string\"><default></default></key>\n"+
      							"<key id=\"Time\" for=\"node\" attr.name=\"Time\" attr.type=\"integer\"><default>-1</default></key>\n"+
      							"<key id=\"RelType\" for=\"edge\" attr.name=\"RelType\" attr.type=\"string\"><default>NoType</default></key>\n"+
      							"<graph id=\"Fabula\" edgedefault=\"directed\">";
    	
    	String FABULA_END = "</graph></graphml>";
    	
    	//Copy lexicon file if it is not in the temporary folder yet
    	File tmpLexFile = new File(writeDir, LEXICON_FILE);
    	if (!tmpLexFile.isFile()){
    		try {
    			File fromFile = new File(LEXICON_FILE);
    			System.out.println(fromFile.getAbsolutePath());
				FileUtils.copyFile(fromFile, tmpLexFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

    	//FileUtils.copyFileToDirectory();
    	
		File tmpFile = new File(writeDir, FABULA_FILENAME);
		if (tmpFile.isFile()) tmpFile.delete();
		
		System.out.println(writeDir);
		
		try {
			PrintWriter out;
			if (writeDir!=null)
				out = new PrintWriter(new FileWriter(tmpFile, true));
			else
				out = new PrintWriter(FABULA_FILENAME);
			out.write(FABULA_START);
			out.write(getAllMessages());
			out.write(FABULA_END);
			System.out.println("Written to "+tmpFile.getAbsolutePath());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
    }
    
    public String getAllMessages(){
    	String result = "";
    	//make this empty now, it is distracting
    	for (String s: allMessages) result+=s+"\n";
    	return result;
    }
    
    public String getNarratorStory(){
    	this.writeFabulaFile();
    	File tmpFile = new File(writeDir, FABULA_FILENAME);
    	String fabula = tmpFile.getAbsolutePath();
    	String result;
		try {
			result = new Main(fabula).start();
		} catch (NarratorException e) {
			//e.printStackTrace();
			result = e.getMessage();
		}
    	return result;
    }
    
    public void setWriteDir(File dir){
    	writeDir = dir;
    }
    
    public void setUser(String user){
    	this.user = user;
    }
    
}