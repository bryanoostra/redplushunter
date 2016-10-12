package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

import xml.Message;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class SerializationTest {
	private String testXML = 	"<message type=\"user_id_response\">\n"+
								"<proposed_user id=\"Marissa203\"/>\n"+
								"<user id=\"Marissa203\"/>\n"+
								"</message>";
	
	private String testXML2 = 	"<message type=\"action_response\">\n"+
								"<action type=\"say_to\" id=\"say_hello_to\">\n"+
								"<perform>\n"+
								"<agens><character id=\"bystander1\"></character></agens>\n"+
								"<patiens><text>Hoi</text></patiens>\n"+
								"<target><character id=\"bystander2\"></character></target>\n"+
								"</perform>\n"+
								"</action>\n"+
								"</message>";
	private XStream xstream;
	private File writeDir;
	public static final String FILENAME = "world_testcrap.xml";
	
	public SerializationTest(){
		xstream = new XStream(new StaxDriver(new XmlFriendlyNameCoder("_-", "_")));		
		xstream.alias("message", xml.Message.class);
		xstream.useAttributeFor(xml.Message.class,"type");
		xstream.useAttributeFor(xml.Message.class,"time");
		xstream.aliasField("time_stamp", xml.Message.class, "time");
		xstream.useAttributeFor(xml.ProposedUser.class, "id");
		xstream.useAttributeFor(xml.User.class, "id");
		xstream.useAttributeFor(xml.Action.class, "id");
		xstream.useAttributeFor(xml.Action.class, "type");
		xstream.useAttributeFor(xml.CharacterX.class, "id");
		xstream.useAttributeFor(xml.Location.class, "id");
	}
	
	public void test(){
		Message testmsg = new Message();
		testmsg.type="type";
		testmsg.time="1";
		xstream.toXML(testmsg, System.out);
		
		System.out.println(testXML);
		
		Message message = (Message) xstream.fromXML(testXML);
		System.out.println(message);
		
		System.out.println();
		System.out.println(testXML2);
		Message message2 = (Message) xstream.fromXML(testXML2);
		System.out.println(message2);
		
		File temp = new File("worldmessage.xml");
		Message message3 = (Message) xstream.fromXML(temp);
		System.out.println(message3);
		//System.out.println(message3.world);
		
		String worldString = null;
		try {
			worldString = readFile("worldmessage.xml",StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String temp1 = StringUtil.getBetween(worldString, " <world scenario_id=\"0\">" , "</world>");
		File tmpFile = new File(writeDir, FILENAME);
		
		try {
			PrintWriter out;
			if (writeDir!=null)
				out = new PrintWriter(new FileWriter(tmpFile, true));
			else
				out = new PrintWriter(FILENAME);
			out.write(temp1);
			System.out.println("Written to "+tmpFile.getAbsolutePath());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void setWriteDir(File f){
		writeDir = f;
	}
	
	public static void main(String[] args){
		SerializationTest test = new SerializationTest();
		test.test();
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
