package hmi.tts.loquendo;

import java.io.*;
import hmi.tts.*;

/**
 * This message contains a sentence to be converted to speech.
 * 
 * @author Dennis Hofs
 */
public class SentenceMessage extends ServerMessage {
	private String sentence;
	
	/**
	 * Constructs a new sentence message.
	 */
	public SentenceMessage(String sentence) {
		super(ServerMessage.SENTENCE);
		this.sentence = sentence;
	}
	
	/**
	 * Returns the sentence.
	 */
	public String getSentence() {
		return sentence;
	}

	@Override
	public void write(OutputStream out) throws Exception {
		super.write(out);
		BinaryMessageWriter writer = new BinaryMessageWriter(out);
		writer.writeString(sentence);
	}
	
	/**
	 * Reads the contents of a sentence message. The message type should
	 * already have been read.
	 * 
	 * @param type the message type
	 * @param in the input stream
	 * @return the sentence message
	 */
	public static SentenceMessage read(byte type, InputStream in)
	throws Exception {
		BinaryMessageReader reader = new BinaryMessageReader(in);
		String sentence = reader.readString();
		return new SentenceMessage(sentence);
	}
}
