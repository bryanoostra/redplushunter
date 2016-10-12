package stomp;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

import xml.Message;

public class MessageReader {
	XStream xstream;
	
	/**Initializes the reader by setting up
	 * the XML serializer.
	 */
	public MessageReader(){
		this.xstream = new XStream(new StaxDriver(new XmlFriendlyNameCoder("_-", "_")));
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
	
	/** Reads an XML message and transforms it to the serialized form
	 */
	public Message read(String xml){
		return (Message) xstream.fromXML(xml);
	}
}
