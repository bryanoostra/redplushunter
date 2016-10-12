package hmi.tts.loquendo;

import java.io.*;
import hmi.tts.*;

/**
 * A voice message contains the name of the voice to be used by Loquendo.
 * 
 * @author Dennis Hofs
 */
public class SetVoiceMessage extends ServerMessage {
	private String voice;
	
	/**
	 * Constructs a new set voice message.
	 */
	public SetVoiceMessage(String voice) {
		super(ServerMessage.SET_VOICE);
		this.voice = voice;
	}
	
	/**
	 * Returns the voice name.
	 */
	public String getVoice() {
		return voice;
	}

	@Override
	public void write(OutputStream out) throws Exception {
		super.write(out);
		BinaryMessageWriter writer = new BinaryMessageWriter(out);
		writer.writeString(voice);
	}
	
	/**
	 * Reads the contents of a set voice message. The message type should
	 * already have been read.
	 * 
	 * @param type the message type
	 * @param in the input stream
	 * @return the set voice message
	 */
	public static SetVoiceMessage read(byte type, InputStream in)
	throws Exception {
		BinaryMessageReader reader = new BinaryMessageReader(in);
		String voice = reader.readString();
		return new SetVoiceMessage(voice);
	}
}
